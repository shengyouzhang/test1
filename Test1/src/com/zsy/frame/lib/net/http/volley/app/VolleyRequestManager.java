package com.zsy.frame.lib.net.http.volley.app;

import java.io.File;

import org.apache.http.protocol.HTTP;

import android.app.ActivityManager;
import android.content.Context;

import com.zsy.frame.lib.net.http.volley.Request;
import com.zsy.frame.lib.net.http.volley.RequestQueue;
import com.zsy.frame.lib.net.http.volley.VolleyLog;
import com.zsy.frame.lib.net.http.volley.toolbox.HttpHeaderParser;
import com.zsy.frame.lib.net.http.volley.toolbox.HttpStack;
import com.zsy.frame.lib.net.http.volley.toolbox.Volley;
import com.zsy.frame.lib.net.http.volley.toolbox.ImageLoader;

public class VolleyRequestManager {
	private static RequestQueue mRequestQueue;
	private static ImageLoader mImageLoader;
	private static File cacheDir;

	private VolleyRequestManager() {
		// no instances
	}

	/**
	 * 我们平时大多采用Volly.newRequestQueue(context)的默认实现，构建RequestQueue。
	通过源码可以看出，我们可以抛开 Volley 工具类构建自定义的RequestQueue，采用自定义的HttpStatck，采用自定义的Network实现，采用自定义的Cache实现等来构建RequestQueue。
	优秀框架的高可拓展性的魅力来源于此啊
	 * 
	 * 初始化volley
	 * @param context
	 * @param isDebug 是否打开日志，volley日志tag为Volley
	 */
	public static void init(Context context, boolean isDebug) {
		mRequestQueue = Volley.newRequestQueue(context);
		VolleyLog.DEBUG = isDebug;
		innerInit(context);
	}

	public static void init(Context context, HttpStack stack, boolean isDebug) {
		mRequestQueue = Volley.newRequestQueue(context, stack);
		VolleyLog.DEBUG = isDebug;
		innerInit(context);
	}

	private static void innerInit(Context context) {
		cacheDir = Volley.getCacheDir();
		setDefaultParseCharset(HTTP.UTF_8);
		// 获取系统给应用配置的内存分配大小；
		int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
		// Use 1/8th of the available memory for this memory cache.volley简单的图片处理显示，也要为其分配图片缓存大小空间
		int cacheSize = 1024 * 1024 * memClass / 8;
		mImageLoader = new ImageLoader(mRequestQueue, new BitmapLruCache(cacheSize));
	}

	public static void setDefaultParseCharset(String charset) {
		HttpHeaderParser.DEFAULT_PARSE_CHARSET = charset;
	}

	/**
	 * 添加请求到队列
	 * @param request
	 * @param tag
	 */
	public static void addRequest(Request<?> request, Object tag) {
		if (tag != null) {
			request.setTag(tag);
		}
		mRequestQueue.add(request);
	}

	/**
	 * 取消相应tag的所有请求
	 * @param tag
	 */
	public static void cancelAll(Object tag) {
		mRequestQueue.cancelAll(tag);
	}
	

	public static RequestQueue getRequestQueue() {
		if (mRequestQueue != null) {
			return mRequestQueue;
		}
		else {
			throw new IllegalStateException("RequestQueue not initialized");
		}
	}

	/**
	 * 获取缓存目录
	 * @return
	 */
	public static File getCacheDir() {
		return cacheDir;
	}


	/**
	 * Returns instance of ImageLoader initialized with {@see FakeImageCache}
	 * which effectively means that no memory caching is used. This is useful
	 * for images that you know that will be show only once.
	 * 图片加载显示用
	 * @return
	 */
	public static ImageLoader getImageLoader() {
		if (mImageLoader != null) {
			return mImageLoader;
		}
		else {
			throw new IllegalStateException("ImageLoader not initialized");
		}
	}
}
