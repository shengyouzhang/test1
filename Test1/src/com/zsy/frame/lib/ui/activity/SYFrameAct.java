package com.zsy.frame.lib.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;

import com.zsy.frame.lib.ui.ISYBroadcastReg;
import com.zsy.frame.lib.ui.SYActivityManager;
import com.zsy.frame.lib.ui.annotation.AnnotateUtil;

/**
 * @description：框架Activity基类
 * @author samy
 * @date 2015-1-22 上午12:12:24
 * @version 1.0
 */
public abstract class SYFrameAct extends FragmentActivity implements OnClickListener, ISkipAct, ISYAct, ISYBroadcastReg {

	/**
	 * @description：这种方法在后台线程中运行,所以你不应该改变ui
	 * @author samy
	 * @date 2015-1-22 上午12:16:34
	 */
	protected void initDataFromThread() {
	}

	protected void initData() {
	}

	protected void initWidget(Bundle savedInstanceState) {
	}

	@Override
	public void initialize(Bundle savedInstanceState) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				initDataFromThread();
			}
		}).start();
		initData();
		initWidget(savedInstanceState);
	}

	@Override
	public void widgetClick(View v) {
	}

	@Override
	public void onClick(View v) {
		widgetClick(v);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SYActivityManager.create().addActivity(this);
		if (checkLoginOrOther()) {
			finish();
//			return;
		}// 注意这里的方法Return必须放在生命周期中

		setRootView(); // 必须放在annotate之前调用
//		现在发现在这里注册广播，有点不好；现线放在这个生命周期里面
		registerBroadcast();
		// 读取配置文件看是否开启注解初始化viewui;
		AnnotateUtil.initBindView(this); // 必须放在initialization之前调用
		initialize(savedInstanceState);
	}

	@Override
	public void registerBroadcast() {
	}

	@Override
	public void unRegisterBroadcast() {
	}

	/**
	 * @description：是否需要优先登录或者其他操作
	 * @author samy
	 * @date 2015-1-22 上午12:17:26
	 */
	protected boolean checkLoginOrOther() {
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegisterBroadcast();
		SYActivityManager.create().finishActivity(this);
	}
}
