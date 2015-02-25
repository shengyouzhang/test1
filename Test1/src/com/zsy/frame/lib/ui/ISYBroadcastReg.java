package com.zsy.frame.lib.ui;

/**
 * @description：统一下注册和解绑广播
 * @author samy
 * @date 2015-2-7 下午8:48:05
 */
public interface ISYBroadcastReg {
	/**
	 * 注册广播
	 */
	void registerBroadcast();

	/**
	 * 解除注册广播
	 */
	void unRegisterBroadcast();
}
