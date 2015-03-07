package com.zsy.frame.lib.net.http.volley.app.samy;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONEntity extends StringEntity{
	public static final String CT_APPLICATION_JSON = "application/json";
	
	public JSONEntity(List<BasicNameValuePair> paramsList) throws UnsupportedEncodingException {
		this(convertParamToJSON(paramsList).toString(), "UTF-8");
	}
	
	public JSONEntity(List<BasicNameValuePair> paramsList, String charset) throws UnsupportedEncodingException {
		super(convertParamToJSON(paramsList).toString(), charset);
		setContentType(CT_APPLICATION_JSON);
	}
	
	public JSONEntity(String json) throws UnsupportedEncodingException {
		this(json, "UTF-8");
	}

	public JSONEntity(String json, String charset) throws UnsupportedEncodingException {
		super(json, charset);
		setContentType(CT_APPLICATION_JSON);
	}
	
	public static JSONObject convertParamToJSON(List<BasicNameValuePair> paramsList) {
		JSONObject jsonObject = new JSONObject();
		for (BasicNameValuePair entry : paramsList) {
			try {
				jsonObject.put(entry.getName(), entry.getValue());
			}
			catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return jsonObject;
	}
}
