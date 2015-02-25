package com.zsy.frame.lib.net.http.volley.app;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.alibaba.fastjson.JSON;
import com.zsy.frame.lib.net.http.volley.NetworkResponse;
import com.zsy.frame.lib.net.http.volley.ParseError;
import com.zsy.frame.lib.net.http.volley.Response;
import com.zsy.frame.lib.net.http.volley.Response.ErrorListener;
import com.zsy.frame.lib.net.http.volley.Response.Listener;
import com.zsy.frame.lib.net.http.volley.toolbox.HttpHeaderParser;

/**
 * @author fanxing
 *         不支持嵌套泛型 如List<T>
 * @param <T>
 */
public class GsonFormRequest<T> extends FormBaseRequest<T> {
	public GsonFormRequest(int method, String url, Listener<T> listener, ErrorListener errorListener) {
		super(method, url, listener, errorListener);
		Type type = getClass().getGenericSuperclass();
		Type trueType = ((ParameterizedType) type).getActualTypeArguments()[0];
		this.entityClass = (Class) trueType;
	}

	public GsonFormRequest(String url, Listener<T> listener, ErrorListener errorListener) {
		super(url, listener, errorListener);
	}

	// private Gson mGson = new Gson();
	// private JSON mGson = new JSON();
	protected Class entityClass;

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			// T res = (T) mGson.fromJson(json, entityClass);
			T res = (T) JSON.parseObject(json, entityClass);

			return Response.success(res, HttpHeaderParser.parseCacheHeaders(response));
		}
		catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		}
	}

	@Override
	protected void deliverResponse(T response) {
		super.deliverResponse(response);
	}
	// /**
	// * 设置自定义gson转换器
	// * @param mGson
	// */
	// public void setGson(Gson mGson) {
	// this.mGson = mGson;
	// }
}
