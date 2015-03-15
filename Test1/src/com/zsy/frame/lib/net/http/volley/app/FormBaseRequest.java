package com.zsy.frame.lib.net.http.volley.app;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;

import com.zsy.frame.lib.net.http.volley.AuthFailureError;
import com.zsy.frame.lib.net.http.volley.NetworkResponse;
import com.zsy.frame.lib.net.http.volley.Request;
import com.zsy.frame.lib.net.http.volley.Response;
import com.zsy.frame.lib.net.http.volley.Response.ErrorListener;
import com.zsy.frame.lib.net.http.volley.Response.Listener;
import com.zsy.frame.lib.net.http.volley.VolleyError;
import com.zsy.frame.lib.net.http.volley.VolleyLog;

/**
 * @description：form表单请求
 * @author samy
 * @date 2015-3-15 下午3:54:47
 */
public abstract class FormBaseRequest<T> extends Request<T> {
	public final static String CONTENT_LENGTH = "Content-Length";

	private final Listener<T> mListener;
	private AjaxParams requestParams;
	private Map<String, String> mHeaders;

	/**
	 * 
	 * 默认post请求
	 * @param url 请求地址
	 * @param listener 请求回调
	 * @param errorListener 异常回调
	 */
	public FormBaseRequest(String url, Listener<T> listener, ErrorListener errorListener) {
		this(Method.POST, url, listener, errorListener);
	}

	/**
	 * 
	 * @param method 请求方式  参考 {@link com.android.volley.Request.Method}
	 * @param url 请求地址
	 * @param listener 请求回调
	 * @param errorListener 异常回调
	 */
	public FormBaseRequest(int method, String url, Listener<T> listener, ErrorListener errorListener) {
		super(method, url, errorListener);
		mListener = listener;
		requestParams = new AjaxParams();
	}

	/**
	 * 设置请求参数
	 * @param requestParams
	 */
	public void setRequestParams(AjaxParams requestParams) {
		this.requestParams = requestParams;
	}

	/**
	 * 设置请求头部信息(一般文件上传得修改头参数)
	 */
	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		long contentLength = requestParams.getContentLength();
		if (requestParams.isFileUpload()) {
			addHeader(CONTENT_LENGTH, "" + contentLength);
		}
		return mHeaders != null ? mHeaders : super.getHeaders();
	}

	public void addHeader(String key, String value) {
		if (mHeaders == null) {
			mHeaders = new HashMap<String, String>();
		}
		mHeaders.put(key, value);
	}
	

	/**
	 * 获取数据包实体，也可以重写{@link #getBody()}, 但此方法必须返回空，参见改动说明1
	 */
	@Override
	public HttpEntity getHttpEntity() {
		if (requestParams.getEntity() == null) {
			VolleyLog.d("数据包为空");
			cancel();
			deliverError(new HttpEntityError());
		}
		return requestParams.getEntity();
	}
	
	/**
	 * 获取数据包内容类型
	 */
	@Override
	public String getBodyContentType() {
		return requestParams.getEntity().getContentType().getValue();
	}

	/**
	 * 解析请求回来的数据
	 */
	@Override
	protected abstract Response<T> parseNetworkResponse(NetworkResponse response);

	/**
	 * 请求回调转发
	 */
	@Override
	protected void deliverResponse(T response) {
		if (mListener != null) {
			mListener.onResponse(response);
		}
	}

	/**
	 * 异常转发
	 */
	@Override
	public void deliverError(VolleyError error) {
		error.url = getUrl();
		super.deliverError(error);
	}
}
