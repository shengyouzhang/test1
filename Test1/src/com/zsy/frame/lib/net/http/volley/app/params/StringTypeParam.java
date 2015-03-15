package com.zsy.frame.lib.net.http.volley.app.params;

import android.text.TextUtils;

/**
 * @description：普通字符串请求参数
 * @author samy
 * @date 2015-3-15 下午4:57:12
 */
public class StringTypeParam extends CommonTypeParam<String> {

	public StringTypeParam(String key, String value, String defaultValue) {
		super(key, value, defaultValue);
	}

	@Override
	protected String getValue(String v, String defV) {
		if (TextUtils.isEmpty(v)) {
			v = defV;
			if (TextUtils.isEmpty(v)) return null;
		}
		return v;
	}

}
