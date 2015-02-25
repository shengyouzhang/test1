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

public class Volley {

	/** Default on-disk cache directory. */
	private static final String DEFAULT_CACHE_DIR = "volley";
	private static File cacheDir;

	public static File getCacheDir() {
		return cacheDir;
	}

	/**
	 * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
	 *
	 * @param context A {@link Context} to use for creating the cache dir.
	 * @param stack An {@link HttpStack} to use for the network, or null for default.
	 * @return A started {@link RequestQueue} instance.
	 */
	public static RequestQueue newRequestQueue(Context context, HttpStack stack) {
		boolean isOk = false;
		cacheDir = null;
		File cacheRoot = null;
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
		if (!isOk) {
			cacheRoot = context.getCacheDir();
		}

		cacheDir = new File(cacheRoot, DEFAULT_CACHE_DIR);

		String userAgent = "volley/0";
		try {
			String packageName = context.getPackageName();
			PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
			userAgent = packageName + "/" + info.versionCode;
		}
		catch (NameNotFoundException e) {
		}

		if (stack == null) {
			if (Build.VERSION.SDK_INT >= 9) {
				stack = new HurlStack();
			}
			else {
				// Prior to Gingerbread, HttpUrlConnection was unreliable.
				// See: http://android-developers.blogspot.com/2011/09/androids-http-clients.html
				stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
			}
		}

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
