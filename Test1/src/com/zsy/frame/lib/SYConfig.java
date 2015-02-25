package com.zsy.frame.lib;

/**
 * @description：Lib全局控制；
 * @author samy
 * @date 2014年10月15日 上午12:03:12
 */
public final class SYConfig {
	public static final double VERSION = 2.0;

	/**
	 * 控制全局是否degug;
	 */
	// public static final Boolean IS_DEGUG = BuildConfig.DEBUG;
	public static Boolean IS_DEBUG_ENABLE = false;
	public static String DEBUG_TAG = "huikaLib";

	/** 声音改变的广播 */
	public static final String RECEIVER_MUSIC_CHANGE = SYConfig.class.getName() + "com.huika.lib.music_change";
	/** 错误处理广播 */
	public static final String RECEIVER_ERROR = SYConfig.class.getName() + "com.huika.lib.error";
	/** 无网络警告广播 */
	public static final String RECEIVER_NOT_NET_WARN = SYConfig.class.getName() + "com.huika.lib.notnet";
}
