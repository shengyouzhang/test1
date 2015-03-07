package com.zsy.frame.lib.net.http.volley.app.samy;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zsy.frame.lib.SYLoger;
import com.zsy.frame.lib.net.http.volley.NetworkResponse;
import com.zsy.frame.lib.net.http.volley.ParseError;
import com.zsy.frame.lib.net.http.volley.Request.Method;
import com.zsy.frame.lib.net.http.volley.Response;
import com.zsy.frame.lib.net.http.volley.Response.ErrorListener;
import com.zsy.frame.lib.net.http.volley.Response.Listener;
import com.zsy.frame.lib.net.http.volley.VolleyError;
import com.zsy.frame.lib.net.http.volley.app.FormBaseRequest;
import com.zsy.frame.lib.net.http.volley.toolbox.HttpHeaderParser;

public class ObjectResultRequest<T> extends FormBaseRequest<T> {
	private Gson mGson = new GsonBuilder().serializeNulls().create();
	private final  Type typeOfT;

	public ObjectResultRequest(String url, Listener<T> listener, ErrorListener errorListener, Type typeOfT) {
		this(Method.POST, url, listener, errorListener, typeOfT);
	}
	
	public ObjectResultRequest(int method, String url, Listener<T> listener, ErrorListener errorListener, Type typeOfT) {
		super(method, url, listener, errorListener);
		this.typeOfT = typeOfT;
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			SYLoger.print("返回的json:" + json);
			T ps = mGson.fromJson(json, typeOfT);
			return Response.success(ps, HttpHeaderParser.parseCacheHeaders(response));
		}
		catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		}
		catch (Exception e) {// 拦截服务器处理异常，判断flag
			try {
				String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
				SYLoger.print("onerror的json:" + json);
				return Response.error(new ServerJsonUnParseError(json));
			}
			catch (Exception er) {
			}
			return Response.error(new ParseError(e));
		}
	}
    
    @Override
    public void deliverError(VolleyError error) {
    	error.url = getUrl();
    	super.deliverError(error);
    }
}
