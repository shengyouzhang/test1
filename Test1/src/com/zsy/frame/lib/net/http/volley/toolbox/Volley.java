/*
 * Copyright (C) 2012 The Android Open Source Project
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

package com.zsy.frame.lib.net.http.volley.toolbox;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import android.os.Environment;

import com.zsy.frame.lib.net.http.volley.Network;
import com.zsy.frame.lib.net.http.volley.RequestQueue;

/**
 * @description：这个和 Volley 框架同名的类，其实是个工具类，作用是构建一个可用于添加网络请求的RequestQueue对象。
 * @author samy
 * @date 2015-3-15 上午11:25:25
 */
public class Volley {

	/** Default on-disk cache directory. */
	private static final String DEFAULT_CACHE_DIR = "volley";
	private static File cacheDir;

	public static File getCacheDir() {
		return cacheDir;
	}

	/**
	 * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
	 * @param context A {@link Context} to use for creating the cache dir.
	 * @param stack An {@link HttpStack} to use for the network, or null for default.
	 * @return A started {@link RequestQueue} instance.
	 */
	public static RequestQueue newRequestQueue(Context context, HttpStack stack) {
		boolean isOk = false;
		cacheDir = null;
		File cacheRoot = null;
		// 先SD卡
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			String cString = Environment.getExternalStorageDirectory().getPath() + File.separator + context.getPackageName();
			cacheRoot = new File(cString);
			if (!cacheRoot.exists()) {
				isOk = cacheRoot.mkdirs();
				try {
					File nomedia = new File(cacheRoot, ".nomedia");
					nomedia.createNewFile();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			else {
				isOk = true;
			}
		}
		// 后手机系统自带卡
		if (!isOk) {
			cacheRoot = context.getCacheDir();
		}

		cacheDir = new File(cacheRoot, DEFAULT_CACHE_DIR);

		// 2.1
		// 处理 Http 请求，返回请求结果。目前 Volley 中有基于 HttpURLConnection 的HurlStack和 基于 Apache HttpClient 的HttpClientStack。
		// 如果 HttpStatck 参数为 null，则如果系统在 Gingerbread 及之后(即 API Level >= 9)，采用基于 HttpURLConnection 的 HurlStack，如果小于 9，采用基于 HttpClient 的 HttpClientStack。
		// 2.2 HttpURLConnection 和 AndroidHttpClient(HttpClient 的封装)如何选择及原因：
		// 在 Froyo(2.2) 之前，HttpURLConnection 有个重大 Bug，调用 close() 函数会影响连接池，导致连接复用失效，所以在 Froyo 之前使用 HttpURLConnection 需要关闭 keepAlive。
		// 另外在 Gingerbread(2.3) HttpURLConnection 默认开启了 gzip 压缩，提高了 HTTPS 的性能，Ice Cream Sandwich(4.0) HttpURLConnection 支持了请求结果缓存。
		// 再加上 HttpURLConnection 本身 API 相对简单，所以对 Android 来说，在 2.3 之后建议使用 HttpURLConnection，之前建议使用 AndroidHttpClient
		// 2.3关于 User Agent
		// 通过代码我们发现如果是使用 AndroidHttpClient，Volley 还会将请求头中的 User-Agent 字段设置为 App 的 ${packageName}/${versionCode}，如果异常则使用 "volley/0"，不过这个获取 User-Agent 的操作应该放到 if else 内部更合适。而对于 HttpURLConnection 却没有任何操作，为什么呢？
		// 如果用 Fiddler 或 Charles 对数据抓包我们会发现，我们会发现 HttpURLConnection 默认是有 User-Agent 的，类似：
		// Dalvik/1.6.0 (Linux; U; Android 4.1.1; Google Nexus 4 - 4.1.1 - API 16 - 768x1280_1 Build/JRO03S)
		// 经常用 WebView 的同学会也许会发现似曾相识，是的，WebView 默认的 User-Agent 也是这个。实际在请求发出之前，会检测 User-Agent 是否为空，如果不为空，则加上系统默认 User-Agent。在 Android 2.1 之后，我们可以通过
		// String userAgent = System.getProperty("http.agent");
		// 得到系统默认的 User-Agent，Volley 如果希望自定义 User-Agent，可在自定义 Request 中重写 getHeaders() 函数
		// @Override
		// public Map<String, String> getHeaders() throws AuthFailureError {
		// // self-defined user agent
		// Map<String, String> headerMap = new HashMap<String, String>();
		// headerMap.put("User-Agent", "android-open-project-analysis/1.0");
		// return headerMap;
		// }

		if (stack == null) {
			if (Build.VERSION.SDK_INT >= 9) {
				stack = new HurlStack();
			}
			else {
				// Prior to Gingerbread, HttpUrlConnection was unreliable.
				// See: http://android-developers.blogspot.com/2011/09/androids-http-clients.html
				String userAgent = "volley/0";
				try {
					String packageName = context.getPackageName();
					PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
					userAgent = packageName + "/" + info.versionCode;
				}
				catch (NameNotFoundException e) {
				}
				stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
			}
		}
		// 得到了 HttpStack,然后通过它构造一个代表网络（Network）的具体实现BasicNetwork。
		// 接着构造一个代表缓存（Cache）的基于 Disk 的具体实现DiskBasedCache。
		// 最后将网络（Network）对象和缓存（Cache）对象传入构建一个 RequestQueue，启动这个 RequestQueue，并返回。
		Network network = new BasicNetwork(stack);
		RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir), network);
		queue.start();

		return queue;
	}

	/**
	 * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
	 *
	 * @param context A {@link Context} to use for creating the cache dir.
	 * @return A started {@link RequestQueue} instance.
	 */
	public static RequestQueue newRequestQueue(Context context) {
		return newRequestQueue(context, null);
	}
}
