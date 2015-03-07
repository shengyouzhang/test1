package com.zsy.frame.lib.net.http.volley.app.samy;

import com.zsy.frame.lib.net.http.volley.VolleyError;
/**
 * 服务器端正常返回，但是服务端处理异常，如flag=0的异常 
 */
public class ServerFlagError extends VolleyError {
	public RequestResult<String> result;
	
	public ServerFlagError(RequestResult<String> result) {
		this.result = result;
	}
}
