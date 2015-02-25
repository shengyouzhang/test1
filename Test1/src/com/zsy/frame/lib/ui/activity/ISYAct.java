package com.zsy.frame.lib.ui.activity;

import android.os.Bundle;
import android.view.View;

/**
 * @description：实现此接口可使用SYActivityManager堆栈
 * @author samy
 * @date 2015-1-22 下午10:50:11
 */
public interface ISYAct {
	/** 初始化方法 */
	void initialize(Bundle savedInstance);

	/** 设置root界面 */
	void setRootView();

	/** 点击事件回调方法 */
	void widgetClick(View v);
}
