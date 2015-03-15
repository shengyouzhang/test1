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

package com.zsy.frame.lib.net.http.volley.toolbox;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpResponse;

import com.zsy.frame.lib.net.http.volley.AuthFailureError;
import com.zsy.frame.lib.net.http.volley.Request;

/**
 * An HTTP stack abstraction.
 * 用于处理 Http 请求，返回请求结果的接口。目前 Volley 中的实现有基于 HttpURLConnection 的 HurlStack 和 基于 Apache HttpClient 的 HttpClientStack。
 * 执行 Request 代表的请求，第二个参数表示发起请求之前，添加额外的请求 Headers。
唯一方法，执行请求
 */
public interface HttpStack {
	/**
	 * Performs an HTTP request with the given parameters.
	 *
	 * <p>A GET request is sent if request.getPostBody() == null. A POST request is sent otherwise,
	 * and the Content-Type header is set to request.getPostBodyContentType().</p>
	 *
	 * @param request the request to perform
	 * @param additionalHeaders additional headers to be sent together with
	 *         {@link Request#getHeaders()}
	 * @return the HTTP response
	 */
	public HttpResponse performRequest(Request<?> request, Map<String, String> additionalHeaders) throws IOException, AuthFailureError;

}
