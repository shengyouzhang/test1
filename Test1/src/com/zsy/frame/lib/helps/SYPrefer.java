package com.zsy.frame.lib.helps;

import android.content.SharedPreferences;

import com.zsy.frame.lib.SYApp;

/**
 * @description：PreferHelper
 * @author samy
 * @date 2014年9月17日 下午6:30:24
 */
public class SYPrefer {
	public static final String NAME = "SYFrame_preference";
	private static SharedPreferences sp;
	private static SharedPreferences.Editor editor;
	private static SYPrefer mInstance;

	private SYPrefer() {
		sp = SYApp.getInstance().getSharedPreferences(NAME, 0);
		editor = sp.edit();
	}

	// 增加了双重判断
	public static SYPrefer getInstance() {
		if (null == mInstance) {
			synchronized (SYPrefer.class) {
				if (null == mInstance) {
					mInstance = new SYPrefer();
				}
			}
		}
		return mInstance;
	}

	/**
	 * 储存值
	 * @param key
	 * @param value
	 */
	public void setString(String key, String value) {
		editor.putString(key, value);
		editor.commit();
	}

	public void setInt(String key, int value) {
		editor.putInt(key, value);
		editor.commit();
	}

	public void setLong(String key, long value) {
		editor.putLong(key, value);
		editor.commit();
	}

	public void setBoolean(String key, boolean value) {
		editor.putBoolean(key, value);
		editor.commit();
	}

	/**
	 * 获取值
	 * @param key
	 * @return
	 */
	public String getString(String key) {
		return sp.getString(key, "");
	}

	public int getInt(String key) {
		return sp.getInt(key, -1);
	}

	public long getLong(String key) {
		return sp.getLong(key, 1);
	}

	public boolean getBoolean(String key, boolean defaultValue) {
		return sp.getBoolean(key, defaultValue);
	}

	/**
	 * @description：移除特定的
	 * @date 2014年11月5日 下午4:30:08
	 */
	public void remove(String name) {
		editor.remove(name);
		editor.commit();
	}
}
