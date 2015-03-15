package com.zsy.frame.lib.net.http.volley.app.params;

/**
 * @description：常规请求参数键值对封装，提供默认值，具体情况需要重写getValue方法
 * @author samy
 * @date 2015-3-15 下午4:00:28
 */
public class CommonTypeParam<T> {

	public String key;
	/**如果为空，将不加入到请求参数中*/
	public String value;

	/**
	 * 
	 * @param key 参数名
	 * @param value 参数值
	 * @param defaultValue 参数默认值
	 */
	public CommonTypeParam(String key, T value, T defaultValue) {
		super();
		this.key = key;
		this.value = getValue(value, defaultValue);
	}

	/**
	 * 得到真正的值，默认为参数值
	 * @param v 参数值
	 * @param defV 参数默认值
	 * @return
	 */
	protected String getValue(T v, T defV) {
		if (v == null) { return null; }
		return v.toString();
	}
}
