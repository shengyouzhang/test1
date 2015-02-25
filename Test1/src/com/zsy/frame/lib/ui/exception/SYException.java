package com.zsy.frame.lib.ui.exception;

/**
 * @description：UI框架异常类
 * @author samy
 * @date 2015-1-22 下午10:15:09
 */
public class SYException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public SYException() {
		super();
	}

	public SYException(String msg) {
		super(msg);
	}

	public SYException(Throwable ex) {
		super(ex);
	}

	public SYException(String msg, Throwable ex) {
		super(msg, ex);
	}
}
