package com.zsy.frame.lib.net.http.volley.app.samy;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.zsy.frame.lib.SYLoger;
import com.zsy.frame.lib.net.http.volley.NetworkResponse;
import com.zsy.frame.lib.net.http.volley.ParseError;
import com.zsy.frame.lib.net.http.volley.Response;
import com.zsy.frame.lib.net.http.volley.Response.ErrorListener;
import com.zsy.frame.lib.net.http.volley.Response.Listener;
import com.zsy.frame.lib.net.http.volley.VolleyError;
import com.zsy.frame.lib.net.http.volley.app.FormBaseRequest;
import com.zsy.frame.lib.net.http.volley.toolbox.HttpHeaderParser;
/**
 * 用HKCacheParser替换HttpHeaderParser实现缓存,貌似需要验证可用性
 * if(error!=null && (error instanceof NoConnectionError || error instanceof NetworkError)){
			try {
				Cache cache = VolleyRequestManager.getRequestQueue().getCache();
				Entry entry = cache.get(error.url);
				if(entry != null){
					String cacheStr = new String(entry.data, "utf-8");
					System.out.println(cacheStr);
				}
			} catch (UnsupportedEncodingException e) {
			}
		}
	<br>
 * 嵌套泛型，必须通过外部获取 如泛型T=RequestResult&ltAccountInfo&gt, 
 * 则typeOfT = new TypeToken&ltRequestResult&ltAccountInfo&gt&gt() {}.getType()
 * @param <T>
 */
public class FormResultRequest<T> extends FormBaseRequest<RequestResult<T>>{
	private static final Type DEFAULT_TYPE = new TypeToken<RequestResult<String>>() {}.getType();
	
	private Gson mGson = new GsonBuilder().serializeNulls().create();;
	private final Type typeOfT;

	public FormResultRequest(String url, Listener<RequestResult<T>> listener, ErrorListener errorListener, Type typeOfT) {
		this(Method.POST, url, listener, errorListener, typeOfT);
	}
	
	public FormResultRequest(int method, String url, Listener<RequestResult<T>> listener, ErrorListener errorListener, Type typeOfT) {
		super(method, url, listener, errorListener);
		this.typeOfT = typeOfT;
	}

	/**
	 * 复杂数据解析可外部传递gson
	 * @param mGson
	 */
	public void setGson(Gson mGson) {
		this.mGson = mGson;
	}
	
	@Override
	protected Response<RequestResult<T>> parseNetworkResponse(NetworkResponse response) {
		try {
			String date = null;
			if(response.headers!=null)
				date = response.headers.get("Date");
			
			String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			SYLoger.print( "返回的json:"+json);
			
			JSONObject jsonObject = new JSONObject(json);
			//拦截服务器处理异常
			if(jsonObject.getInt("flag") == 0){
				RequestResult<String> res = mGson.fromJson(json, DEFAULT_TYPE);
				res.dateStr = date;
				return Response.error(new ServerFlagError(res));
			}
			RequestResult<T> res = mGson.fromJson(json, typeOfT);
			res.dateStr = date;
			return Response.success(res,
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (Exception e) {//拦截服务器处理异常，判断flag
			try{
				String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
				SYLoger.print("error的json:"+json);
				return Response.error(new ServerJsonUnParseError(json));
			}catch(Exception er){
			}
			return Response.error(new ParseError(e));
		}
	}
	/**
	 * 追加请求地址(又进才有出)
	 */
    @Override
    protected void deliverResponse(RequestResult<T> response) {
    	response.url = getUrl();
    	super.deliverResponse(response);
    }
    
    @Override
    public void deliverError(VolleyError error) {
    	error.url = getUrl();
    	super.deliverError(error);
    }
    
}
