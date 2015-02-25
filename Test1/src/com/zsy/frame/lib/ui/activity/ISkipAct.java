package com.zsy.frame.lib.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * @description：跳转接口配置
 * @author samy
 * @date 2015-1-22 下午9:46:07
 */
public interface ISkipAct {
	public void skipActivity(Activity aty, Class<?> cls);

	public void skipActivity(Activity aty, Intent it);

	public void skipActivity(Activity aty, Class<?> cls, Bundle extras);

	public void showActivity(Activity aty, Class<?> cls);

	public void showActivity(Activity aty, Intent it);

	public void showActivity(Activity aty, Class<?> cls, Bundle extras);
}
