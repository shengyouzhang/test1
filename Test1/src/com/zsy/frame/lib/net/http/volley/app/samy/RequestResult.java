package com.zsy.frame.lib.net.http.volley.app.samy;

import java.io.Serializable;

public class RequestResult<T> implements Serializable {
	public String url;//请求地址
	/**提示信息*/
	public String msg;
	/**请求状态, 0失败，1成功*/
	public int flag;
	//http请求头里返回的时间,可能为空
	public String dateStr;
	
	/**数据总数*/
	private int totalSize;
	private T rs;
	public T getRs() {
		return rs;
	}
	public void setRs(T rs) {
		this.rs = rs;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getTotalSize() {
		return totalSize;
	}
	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}
	
}
