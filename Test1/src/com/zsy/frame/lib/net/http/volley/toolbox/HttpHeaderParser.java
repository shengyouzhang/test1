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

import java.util.Map;

import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;
import org.apache.http.protocol.HTTP;

import com.zsy.frame.lib.net.http.volley.Cache;
import com.zsy.frame.lib.net.http.volley.NetworkResponse;

/**
 * Utility methods for parsing HTTP headers.
 * 
 * Http header 的解析工具类，在 Volley 中主要作用是用于解析 Header 从而判断返回结果是否需要缓存，如果需要返回 Header 中相关信息。
有三个方法
public static long parseDateAsEpoch(String dateStr)
解析时间，将 RFC1123 的时间格式，解析成 epoch 时间
public static String parseCharset(Map<String, String> headers)
解析编码集，在 Content-Type 首部中获取编码集，如果没有找到，默认返回 ISO-8859-1
public static Cache.Entry parseCacheHeaders(NetworkResponse response)
比较重要的方法，通过网络响应中的缓存控制 Header 和 Body 内容，构建缓存实体。如果 Header 的 Cache-Control 字段含有no-cache或no-store表示不缓存，返回 null。
(1). 根据 Date 首部，获取响应生成时间
(2). 根据 ETag 首部，获取响应实体标签
(3). 根据 Cache－Control 和 Expires 首部，计算出缓存的过期时间，和缓存的新鲜度时间
两点需要说明下：
1.没有处理Last-Modify首部，而是处理存储了Date首部，并在后续的新鲜度验证时，使用Date来构建If-Modified-Since。 这与 Http 1.1 的语义有些违背。
2.计算过期时间，Cache－Control 首部优先于 Expires 首部。
 */
public class HttpHeaderParser {
	public static String DEFAULT_PARSE_CHARSET = HTTP.DEFAULT_CONTENT_CHARSET;

	/**
	 * Extracts a {@link Cache.Entry} from a {@link NetworkResponse}.
	 *
	 * @param response The network response to parse headers from
	 * @return a cache entry for the given response, or null if the response is not cacheable.
	 */
	public static Cache.Entry parseCacheHeaders(NetworkResponse response) {
		long now = System.currentTimeMillis();

		Map<String, String> headers = response.headers;

		long serverDate = 0;
		long serverExpires = 0;
		long softExpire = 0;
		long maxAge = 0;
		boolean hasCacheControl = false;

		String serverEtag = null;
		String headerValue;

		headerValue = headers.get("Date");
		if (headerValue != null) {
			serverDate = parseDateAsEpoch(headerValue);
		}

		headerValue = headers.get("Cache-Control");
		if (headerValue != null) {
			hasCacheControl = true;
			String[] tokens = headerValue.split(",");
			for (int i = 0; i < tokens.length; i++) {
				String token = tokens[i].trim();
				if (token.equals("no-cache") || token.equals("no-store")) {
					return null;
				}
				else if (token.startsWith("max-age=")) {
					try {
						maxAge = Long.parseLong(token.substring(8));
					}
					catch (Exception e) {
					}
				}
				else if (token.equals("must-revalidate") || token.equals("proxy-revalidate")) {
					maxAge = 0;
				}
			}
		}

		headerValue = headers.get("Expires");
		if (headerValue != null) {
			serverExpires = parseDateAsEpoch(headerValue);
		}

		serverEtag = headers.get("ETag");

		// Cache-Control takes precedence over an Expires header, even if both exist and Expires
		// is more restrictive.
		if (hasCacheControl) {
			softExpire = now + maxAge * 1000;
		}
		else if (serverDate > 0 && serverExpires >= serverDate) {
			// Default semantic for Expire header in HTTP specification is softExpire.
			softExpire = now + (serverExpires - serverDate);
		}

		Cache.Entry entry = new Cache.Entry();
		entry.data = response.data;
		entry.etag = serverEtag;
		entry.softTtl = softExpire;
		entry.ttl = entry.softTtl;
		entry.serverDate = serverDate;
		entry.responseHeaders = headers;

		return entry;
	}

	/**
	 * Parse date in RFC1123 format, and return its value as epoch
	 */
	public static long parseDateAsEpoch(String dateStr) {
		try {
			// Parse date in RFC1123 format if this header contains one
			return DateUtils.parseDate(dateStr).getTime();
		}
		catch (DateParseException e) {
			// Date in invalid format, fallback to 0
			return 0;
		}
	}

	/**
	 * Returns the charset specified in the Content-Type of this header,
	 * or the HTTP default (ISO-8859-1) if none can be found.
	 */
	public static String parseCharset(Map<String, String> headers) {
		String contentType = headers.get(HTTP.CONTENT_TYPE);
		if (contentType != null) {
			String[] params = contentType.split(";");
			for (int i = 1; i < params.length; i++) {
				String[] pair = params[i].trim().split("=");
				if (pair.length == 2) {
					if (pair[0].equals("charset")) { return pair[1]; }
				}
			}
		}

		return DEFAULT_PARSE_CHARSET;
	}
}
