package com.zsy.frame.lib.net.http.volley.app.samy;

import com.zsy.frame.lib.net.http.volley.VolleyError;
/**
 * 服务器端正常返回，但是服务端处理结果flag=0,
 * result = 原生请求结果string
 */
public class ServerJsonUnParseError extends VolleyError {
	public String result;
	
	public ServerJsonUnParseError(String result) {
		this.result = result;
	}
}
