package com.zsy.frame.lib.net.http.volley.app.params;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FileTypeParam {
	public String key;
	public InputStream inputStream;
	public String fileName;
	public String contentType;

	public FileTypeParam(String key, InputStream inputStream, String fileName, String contentType) {
		this.key = key;
		this.inputStream = inputStream;
		this.fileName = fileName;
		this.contentType = contentType;
	}

	public FileTypeParam(String key, String filePath, String contentType) throws FileNotFoundException {
		this.key = key;
		File file = new File(filePath);
		inputStream = new FileInputStream(file);
		fileName = file.getName();
		this.contentType = contentType;
	}

	/**
	 * 判断该文件上传参数是否有效，无效则不会添加到请求参数中
	*/
	public boolean isValidate() {
		if (inputStream == null) return false;

		return true;
	}
}