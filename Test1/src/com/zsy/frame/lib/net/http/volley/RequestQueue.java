/*
 * Copyright (C) 2011 The Android Open Source Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zsy.frame.lib.net.http.volley;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import android.os.Handler;
import android.os.Looper;

/**
 * A request dispatch queue with a thread pool of dispatchers.
 *
 * Calling {@link #add(Request)} will enqueue the given Request for dispatch,
 * resolving from either cache or network on a worker thread, and then delivering
 * a parsed response on the main thread.
 * 
 * 表示请求队列，里面包含一个CacheDispatcher(用于处理走缓存请求的调度线程)、NetworkDispatcher数组(用于处理走网络请求的调度线程)，
 * 一个ResponseDelivery(返回结果分发接口)，通过 start() 函数启动时会启动CacheDispatcher和NetworkDispatchers。
 */
public class RequestQueue {

	/** Used for generating monotonically-increasing sequence numbers for requests. */
	private AtomicInteger mSequenceGenerator = new AtomicInteger();

	/**
	 * Staging area for requests that already have a duplicate request in flight.
	 *
	 * <ul>
	 *     <li>containsKey(cacheKey) indicates that there is a request in flight for the given cache
	 *          key.</li>
	 *     <li>get(cacheKey) returns waiting requests for the given cache key. The in flight request
	 *          is <em>not</em> contained in that list. Is null if no requests are staged.</li>
	 * </ul>
	 * 维护了一个等待请求的集合，如果一个请求正在被处理并且可以被缓存，后续的相同 url 的请求，将进入此等待队列。
	 */
	private final Map<String, Queue<Request<?>>> mWaitingRequests = new HashMap<String, Queue<Request<?>>>();

	/**
	 * The set of all requests currently being processed by this RequestQueue. A Request
	 * will be in this set if it is waiting in any queue or currently being processed by
	 * any dispatcher.
	 * 
	 * 维护了一个正在进行中，尚未完成的请求集合。
	 */
	private final Set<Request<?>> mCurrentRequests = new HashSet<Request<?>>();

	/**
	 * (1). 主要成员变量
	 RequestQueue 中维护了两个基于优先级的 Request 队列，缓存请求队列和网络请求队列。
	放在缓存请求队列中的 Request，将通过缓存获取数据；放在网络请求队列中的 Request，将通过网络获取数据。
	 */
	/** The cache triage queue. */
	private final PriorityBlockingQueue<Request<?>> mCacheQueue = new PriorityBlockingQueue<Request<?>>();

	/** The queue of requests that are actually going out to the network. */
	private final PriorityBlockingQueue<Request<?>> mNetworkQueue = new PriorityBlockingQueue<Request<?>>();

	/** Number of network request dispatcher threads to start. */
	private static final int DEFAULT_NETWORK_THREAD_POOL_SIZE = 4;

	/** Cache interface for retrieving and storing responses. */
	private final Cache mCache;

	/** Network interface for performing requests. */
	private final Network mNetwork;

	/** Response delivery mechanism. */
	private final ResponseDelivery mDelivery;

	/** The network dispatchers. */
	private NetworkDispatcher[] mDispatchers;

	/** The cache dispatcher. */
	private CacheDispatcher mCacheDispatcher;

	/**
	 * Creates the worker pool. Processing will not begin until {@link #start()} is called.
	 *
	 * @param cache A Cache to use for persisting responses to disk
	 * @param network A Network interface for performing HTTP requests
	 * @param threadPoolSize Number of network dispatcher threads to create
	 * @param delivery A ResponseDelivery interface for posting responses and errors
	 */
	public RequestQueue(Cache cache, Network network, int threadPoolSize, ResponseDelivery delivery) {
		mCache = cache;
		mNetwork = network;
		mDispatchers = new NetworkDispatcher[threadPoolSize];
		mDelivery = delivery;
	}

	/**
	 * Creates the worker pool. Processing will not begin until {@link #start()} is called.
	 *
	 * @param cache A Cache to use for persisting responses to disk
	 * @param network A Network interface for performing HTTP requests
	 * @param threadPoolSize Number of network dispatcher threads to create
	 */
	public RequestQueue(Cache cache, Network network, int threadPoolSize) {
		this(cache, network, threadPoolSize, new ExecutorDelivery(new Handler(Looper.getMainLooper())));
	}

	/**
	 * Creates the worker pool. Processing will not begin until {@link #start()} is called.
	 *
	 * @param cache A Cache to use for persisting responses to disk
	 * @param network A Network interface for performing HTTP requests
	 */
	public RequestQueue(Cache cache, Network network) {
		this(cache, network, DEFAULT_NETWORK_THREAD_POOL_SIZE);
	}

	/**
	 * Starts the dispatchers in this queue.
	 * 
	 * Volley 的总体设计图
	 * 主要是通过两种Diapatch Thread不断从RequestQueue中取出请求，根据是否已缓存调用Cache或Network这两类数据获取接口之一，
	 * 从内存缓存或是服务器取得请求的数据，然后交由ResponseDelivery去做结果分发及回调处理
	 * 
	 * 
	 * (2). 启动队列
	创建出 RequestQueue 以后，调用 start 方法，启动队列。
	start 方法中，开启一个缓存调度线程CacheDispatcher和 n 个网络调度线程NetworkDispatcher，这里 n 默认为4，存在优化的余地，比如可以根据 CPU 核数以及网络类型计算更合适的并发数。
	缓存调度线程不断的从缓存请求队列中取出 Request 去处理，网络调度线程不断的从网络请求队列中取出 Request 去处理。
	 */
	public void start() {
		stop(); // Make sure any currently running dispatchers are stopped.
		// Create the cache dispatcher and start it.
		mCacheDispatcher = new CacheDispatcher(mCacheQueue, mNetworkQueue, mCache, mDelivery);
		mCacheDispatcher.start();

		// Create network dispatchers (and corresponding threads) up to the pool size.
		for (int i = 0; i < mDispatchers.length; i++) {
			NetworkDispatcher networkDispatcher = new NetworkDispatcher(mNetworkQueue, mNetwork, mCache, mDelivery);
			mDispatchers[i] = networkDispatcher;
			networkDispatcher.start();
		}
	}

	/**
	 * Stops the cache and network dispatchers.
	 */
	public void stop() {
		if (mCacheDispatcher != null) {
			mCacheDispatcher.quit();
		}
		for (int i = 0; i < mDispatchers.length; i++) {
			if (mDispatchers[i] != null) {
				mDispatchers[i].quit();
			}
		}
	}

	/**
	 * Gets a sequence number.
	 */
	public int getSequenceNumber() {
		return mSequenceGenerator.incrementAndGet();
	}

	/**
	 * Gets the {@link Cache} instance being used.
	 */
	public Cache getCache() {
		return mCache;
	}

	/**
	 * A simple predicate or filter interface for Requests, for use by
	 * {@link RequestQueue#cancelAll(RequestFilter)}.
	 */
	public interface RequestFilter {
		public boolean apply(Request<?> request);
	}

	/**
	 * Cancels all requests in this queue for which the given filter applies.
	 * @param filter The filtering function to use
	 * 
	 * (5). 请求取消
	public void cancelAll(RequestFilter filter)
	public void cancelAll(final Object tag)
	取消当前请求集合中所有符合条件的请求。
	filter 参数表示可以按照自定义的过滤器过滤需要取消的请求。
	tag 表示按照Request.setTag设置好的 tag 取消请求，比如同属于某个 Activity 的。
	 */
	public void cancelAll(RequestFilter filter) {
		synchronized (mCurrentRequests) {
			for (Request<?> request : mCurrentRequests) {
				if (filter.apply(request)) {
					request.cancel();
				}
			}
		}
	}

	/**
	 * Cancels all requests in this queue with the given tag. Tag must be non-null
	 * and equality is by identity.
	 */
	public void cancelAll(final Object tag) {
		if (tag == null) { throw new IllegalArgumentException("Cannot cancelAll with a null tag"); }
		cancelAll(new RequestFilter() {
			@Override
			public boolean apply(Request<?> request) {
				return request.getTag() == tag;
			}
		});
	}

	/**
	 * Adds a Request to the dispatch queue.
	 * @param request The request to service
	 * @return The passed-in request
	(3). 加入请求
	public <T> Request<T> add(Request<T> request);
	 */
	public <T> Request<T> add(Request<T> request) {
		// Tag the request as belonging to this queue and add it to the set of current requests.
		request.setRequestQueue(this);
		synchronized (mCurrentRequests) {
			mCurrentRequests.add(request);
		}

		// Process requests in the order they are added.
		request.setSequence(getSequenceNumber());
		request.addMarker("add-to-queue");

		// If the request is uncacheable, skip the cache queue and go straight to the network.
		if (!request.shouldCache()) {
			mNetworkQueue.add(request);
			return request;
		}

		// Insert request into stage if there's already a request with the same cache key in flight.
		synchronized (mWaitingRequests) {
			String cacheKey = request.getCacheKey();
			if (mWaitingRequests.containsKey(cacheKey)) {
				// There is already a request in flight. Queue up.
				Queue<Request<?>> stagedRequests = mWaitingRequests.get(cacheKey);
				if (stagedRequests == null) {
					stagedRequests = new LinkedList<Request<?>>();
				}
				stagedRequests.add(request);
				mWaitingRequests.put(cacheKey, stagedRequests);
				if (VolleyLog.DEBUG) {
					VolleyLog.v("Request for cacheKey=%s is in flight, putting on hold.", cacheKey);
				}
			}
			else {
				// Insert 'null' queue for this cacheKey, indicating there is now a request in
				// flight.
				mWaitingRequests.put(cacheKey, null);
				mCacheQueue.add(request);
			}
			return request;
		}
	}

	/**
	 * Called from {@link Request#finish(String)}, indicating that processing of the given request
	 * has finished.
	 *
	 * <p>Releases waiting requests for <code>request.getCacheKey()</code> if
	 *      <code>request.shouldCache()</code>.</p>
	 *      (4). 请求完成
	void finish(Request<?> request)
	Request 请求结束

	(1). 首先从正在进行中请求集合mCurrentRequests中移除该请求。
	(2). 然后查找请求等待集合mWaitingRequests中是否存在等待的请求，如果存在，则将等待队列移除，
	并将等待队列所有的请求添加到缓存请求队列中，让缓存请求处理线程CacheDispatcher自动处理。
	 */
	void finish(Request<?> request) {
		// Remove from the set of requests currently being processed.
		synchronized (mCurrentRequests) {
			mCurrentRequests.remove(request);
		}

		if (request.shouldCache()) {
			synchronized (mWaitingRequests) {
				String cacheKey = request.getCacheKey();
				Queue<Request<?>> waitingRequests = mWaitingRequests.remove(cacheKey);
				if (waitingRequests != null) {
					if (VolleyLog.DEBUG) {
						VolleyLog.v("Releasing %d waiting requests for cacheKey=%s.", waitingRequests.size(), cacheKey);
					}
					// Process all queued up requests. They won't be considered as in flight, but
					// that's not a problem as the cache has been primed by 'request'.
					mCacheQueue.addAll(waitingRequests);
				}
			}
		}
	}
}
