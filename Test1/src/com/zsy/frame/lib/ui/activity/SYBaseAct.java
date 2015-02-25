package com.zsy.frame.lib.ui.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.zsy.frame.lib.SYLoger;
import com.zsy.frame.lib.ui.SYActivityManager;
import com.zsy.frame.lib.ui.ViewInject;

/**
 * @description：控制屏幕翻转、Actionbar和Activity生命周期打印调试
 * @author samy
 * @date 2015-1-22 下午9:52:29
 */
public abstract class SYBaseAct extends SYFrameAct {
	public Activity aty;

	/**
	 * 当前Activity状态
	 */
	public static enum ActivityState {
		RESUME, PAUSE, STOP, DESTROY
	}

	/** Activity状态 */
	public ActivityState activityState = ActivityState.DESTROY;

	/**
	 * Activity显示方向
	 */
	public static enum ScreenOrientation {
		HORIZONTAL, VERTICAL, AUTO
	}

	/** 是否允许全屏 */
	private boolean mAllowFullScreen = false;
	/** 是否隐藏ActionBar */
	private boolean mHiddenActionBar = false;
	/** 是否启用框架的退出界面 */
	private boolean mOpenBackListener = false;
	/** 屏幕方向 */
	private ScreenOrientation orientation = ScreenOrientation.VERTICAL;

	/**
	 * 是否全屏显示本Activity，全屏后将隐藏状态栏，默认不全屏（若修改必须在构造方法中调用）
	 * 
	 * @param allowFullScreen
	 *            是否允许全屏
	 */
	public void setAllowFullScreen(boolean allowFullScreen) {
		this.mAllowFullScreen = allowFullScreen;
	}

	/**
	 * 是否隐藏ActionBar，默认隐藏（若修改必须在构造方法中调用）
	 * 
	 * @param hiddenActionBar
	 *            是否隐藏ActionBar
	 */
	public void setHiddenActionBar(boolean hiddenActionBar) {
		this.mHiddenActionBar = hiddenActionBar;
	}

	/**
	 * 修改屏幕显示方向，默认竖屏锁定（若修改必须在构造方法中调用）
	 * 
	 * @param orientation
	 */
	public void setScreenOrientation(ScreenOrientation orientation) {
		this.orientation = orientation;
	}

	/**
	 * 是否启用返回键监听，若启用，则在显示最后一个Activity时将弹出退出对话框。默认启用（若修改必须在构造方法中调用）
	 * 
	 * @param openBackListener
	 */
	public void setBackListener(boolean openBackListener) {
		this.mOpenBackListener = openBackListener;
	}

	/**
	 * 返回是否启用返回键监听
	 */
	protected boolean getBackListener() {
		return this.mOpenBackListener;
	}

	@Override
	public void skipActivity(Activity aty, Class<?> cls) {
		showActivity(aty, cls);
		aty.finish();
	}

	@Override
	public void skipActivity(Activity aty, Intent it) {
		showActivity(aty, it);
		aty.finish();
	}

	@Override
	public void skipActivity(Activity aty, Class<?> cls, Bundle extras) {
		showActivity(aty, cls, extras);
		aty.finish();
	}

	@Override
	public void showActivity(Activity aty, Class<?> cls) {
		Intent intent = new Intent();
		intent.setClass(aty, cls);
		aty.startActivity(intent);
	}

	@Override
	public void showActivity(Activity aty, Intent it) {
		aty.startActivity(it);
	}

	@Override
	public void showActivity(Activity aty, Class<?> cls, Bundle extras) {
		Intent intent = new Intent();
		intent.putExtras(extras);
		intent.setClass(aty, cls);
		aty.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		aty = this;
		SYLoger.state(this.getClass().getName(), "---------onCreate");
		switch (orientation) {
			case HORIZONTAL:
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				break;
			case VERTICAL:
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				break;
			case AUTO:
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
				break;
		}

		if (mHiddenActionBar) {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
		else {
			ActionBar a = getActionBar();
			a.show();
		}
		if (mAllowFullScreen) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStart() {
		super.onStart();
		SYLoger.state(this.getClass().getName(), "---------onStart");
	}

	@Override
	protected void onResume() {
		super.onResume();
		activityState = ActivityState.RESUME;
		SYLoger.state(this.getClass().getName(), "---------onResume");
	}

	@Override
	protected void onPause() {
		super.onPause();
		activityState = ActivityState.PAUSE;
		SYLoger.state(this.getClass().getName(), "---------onPause");
	}

	@Override
	protected void onStop() {
		super.onStop();
		activityState = ActivityState.STOP;
		SYLoger.state(this.getClass().getName(), "---------onStop");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		SYLoger.state(this.getClass().getName(), "---------onRestart");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		activityState = ActivityState.DESTROY;
		SYLoger.state(this.getClass().getName(), "---------onDestroy");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (mOpenBackListener && keyCode == KeyEvent.KEYCODE_BACK && SYActivityManager.create().getCount() < 1) {
			ViewInject.create().getExitDialog(this);
		}
		return super.onKeyDown(keyCode, event);
	}
}
