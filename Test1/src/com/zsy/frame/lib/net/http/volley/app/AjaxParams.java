package com.zsy.frame.lib.net.http.volley.app;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import android.text.TextUtils;

import com.zsy.frame.lib.net.http.volley.app.helps.ParamEncryptor;
import com.zsy.frame.lib.net.http.volley.app.params.CommonTypeParam;
import com.zsy.frame.lib.net.http.volley.app.params.FileParamValidatorParam;
import com.zsy.frame.lib.net.http.volley.app.params.FileTypeParam;
import com.zsy.frame.lib.net.http.volley.app.params.StringTypeParam;

public class AjaxParams {
	// 默认编码格式
	private static String DEFAULT_ENCODING = "UTF-8";
	// 一般的请求参数
	protected Map<String, String> urlParams;
	// 上传文件的请求参数
	protected Map<String, FileTypeParam> fileParams;
	// 要加密的请求参数key
	private Set<String> encryptkeys;
	private ParamEncryptor paramEncryptor;// 加密接口
	// 文件上传验证参数
	protected FileParamValidatorParam fileValidatorParam;
	// 最终包装实体
	private HttpEntity entity = null;

	public AjaxParams() {
		urlParams = new HashMap<String, String>();

		fileParams = new HashMap<String, FileTypeParam>();

		encryptkeys = new HashSet<String>();
	}

	/**初始化加密*/
	public void initEncrypt(ParamEncryptor paramEncryptor, String... keys) {
		this.paramEncryptor = paramEncryptor;
		if (keys != null) {
			for (String k : keys)
				encryptkeys.add(k);
		}
		setEntityEmpty();
	}

	/**添加加密字段*/
	public void addEncrypt(String enKey) {
		encryptkeys.add(enKey);
		setEntityEmpty();
	}

	/**添加加密字段*/
	public void addEncrypts(Collection<String> enKeys) {
		if (enKeys == null || enKeys.isEmpty()) return;

		for (String k : enKeys)
			encryptkeys.add(k);
		setEntityEmpty();
	}

	/**添加常规类请求参数*/
	public AjaxParams put(CommonTypeParam param) {
		if (param.value == null) { return this; }

		String key = param.key;
		String value = param.value;
		if (paramEncryptor != null && encryptkeys.contains(key)) {
			value = paramEncryptor.encrypt(value);
		}
		urlParams.put(key, value);
		setEntityEmpty();
		return this;
	}

	/**添加一个putCommonTypeParam,默认值为null*/
	public <T> AjaxParams putCommonTypeParam(String key, T value) {
		return putCommonTypeParam(key, value, null);
	}

	/**添加一个putCommonTypeParam,手动指定默认值*/
	public <T> AjaxParams putCommonTypeParam(String key, T value, T defaultValue) {
		return put(new CommonTypeParam<T>(key, value, defaultValue));
	}

	/**添加一个StringTypeParam,默认值为null*/
	public AjaxParams putStringTypeParam(String key, String value) {
		return putStringTypeParam(key, value, null);
	}

	/**添加一个StringTypeParam,手动指定默认值*/
	public AjaxParams putStringTypeParam(String key, String value, String defaultValue) {
		return put(new StringTypeParam(key, value, defaultValue));
	}

	/**添加文件上传参数*/
	public void put(FileTypeParam fileTypeParam) {
		if (fileTypeParam.isValidate()) {
			fileParams.put(fileTypeParam.key, fileTypeParam);
			setEntityEmpty();
		}
	}

	/**添加文件上传验证参数*/
	public void setFileParamValidatorParam(FileParamValidatorParam fileValidatorParam) {
		this.fileValidatorParam = fileValidatorParam;
		setEntityEmpty();
	}

	public void remove(String key) {
		urlParams.remove(key);
		fileParams.remove(key);
		setEntityEmpty();
	}

	/**
	 * 方法概述：是否包含图片上传
	 * 
	 * @description 方法详细描述：
	 * @author ldm
	 * @param @return
	 * @return boolean
	 * @throws
	 * @Title: AjaxParams.java
	 * @Package com.huika.huixin.model.net.http
	 * @date 2014-5-14 下午1:54:40
	 */
	public boolean isFileUpload() {
		return !fileParams.isEmpty();
	}

	/**
	 * 重新获取entity
	 * @return 上传数据包长度
	 */
	public long getContentLength() {
		return getEntity().getContentLength();
	}

	/**设置entity为空,以便请求刷新数据*/
	public void setEntityEmpty() {
		entity = null;
	}

	/**
	 * Returns an HttpEntity containing all request parameters
	 */
	public HttpEntity getEntity() {
		if (entity != null) // 避免重复创建
			return entity;

		if (isFileUpload()) {
			MultipartEntity multipartEntity = getFileUploadEntity();

			entity = multipartEntity;
		}
		else {
			try {
				entity = getCommonEntity(getParamsList());
			}
			catch (UnsupportedEncodingException e) {
				throw new IllegalStateException("获取参数实体异常：" + e.toString());
			}
		}
		return entity;
	}

	/**
	 * 获取文件上传请求的封装实体
	 */
	protected MultipartEntity getFileUploadEntity() {
		MultipartEntity multipartEntity = new MultipartEntity();

		if (fileValidatorParam != null) {// 文件验证参数追加
			String validateStr = fileValidatorParam.getValidateValue(fileParams);
			if (!TextUtils.isEmpty(validateStr)) {
				urlParams.put(fileValidatorParam.key, validateStr);
			}
		}

		// 文件上传时，填充参数到MultipartEntity中
		if (!urlParams.isEmpty()) {
			for (Map.Entry<String, String> entry : urlParams.entrySet()) {
				multipartEntity.addPart(entry.getKey(), entry.getValue().toString());
			}
		}

		// Add file params
		int currentIndex = 0;
		int lastIndex = fileParams.entrySet().size() - 1;
		for (HashMap.Entry<String, FileTypeParam> entry : fileParams.entrySet()) {
			FileTypeParam fileParam = entry.getValue();
			if (fileParam.inputStream != null) {
				boolean isLast = currentIndex == lastIndex;
				if (fileParam.contentType != null) {
					multipartEntity.addPart(entry.getKey(), fileParam.fileName, fileParam.inputStream, fileParam.contentType, isLast);
				}
				else {
					multipartEntity.addPart(entry.getKey(), fileParam.fileName, fileParam.inputStream, isLast);
				}
			}
			currentIndex++;
		}
		return multipartEntity;
	}

	/**
	 * 获取普通请求的封装实体
	 */
	protected HttpEntity getCommonEntity(List<BasicNameValuePair> paramsList) throws UnsupportedEncodingException {
		return new UrlEncodedFormEntity(getParamsList(), DEFAULT_ENCODING);
	}

	/**
	 * 不包含文件上传时，参数的获取
	 * @return
	 */
	protected List<BasicNameValuePair> getParamsList() {
		List<BasicNameValuePair> lparams = new LinkedList<BasicNameValuePair>();

		for (HashMap.Entry<String, String> entry : urlParams.entrySet()) {
			lparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		return lparams;
	}

	public String getParamString() {
		return URLEncodedUtils.format(getParamsList(), DEFAULT_ENCODING);
	}

	/**
	 * 返回当前的普通请求参数
	 * @return
	 */
	public Map<String, String> getUrlParams() {
		return urlParams;
	}
}
