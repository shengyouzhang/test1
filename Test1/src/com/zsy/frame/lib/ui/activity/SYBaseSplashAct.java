package com.zsy.frame.lib.ui.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

/**
 * @description：应用启动的欢迎界面模板
 * @author samy
 * @date 2015-1-22 下午10:56:24
 */
public abstract class SYBaseSplashAct extends SYBaseAct {
	/**
	 * 用于显示启动界面的背景图片
	 */
	protected ImageView mImageView;

	protected abstract void setRootBackground(ImageView view);

	/** 欢迎页动画的时间 */
	protected long animationDuration = 1000;

	/**
	 * 默认设置为全屏、竖屏锁定显示
	 */
	public SYBaseSplashAct() {
		setAllowFullScreen(true);
		setHiddenActionBar(true);
		setScreenOrientation(ScreenOrientation.VERTICAL);
	}

	@Override
	public void setRootView() {
		mImageView = new ImageView(this);
		mImageView.setScaleType(ScaleType.FIT_XY);
		setContentView(mImageView);
		setRootBackground(mImageView);
	}

	@Override
	protected void initWidget(Bundle savedInstance) {
		super.initWidget(savedInstance);
		AnimationSet animationSet = new AnimationSet(true);
		AlphaAnimation alphaAnimation = new AlphaAnimation(0.2F, 0.9F);
		alphaAnimation.setDuration(animationDuration);
		animationSet.addAnimation(alphaAnimation);
		// 监听动画过程
		animationSet.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				checkVersion();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				redirectTo();
			}
		});
		mImageView.startAnimation(animationSet);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return true;
	}

	/**
	 * 跳转到...
	 */
	protected void redirectTo() {
		if (firstsInstall()) {
		}
	}

	/**
	 * 判断首次使用
	 */
	protected boolean firstsInstall() {
		return true;
	}

	/**
	 * 检查更新
	 */
	protected void checkVersion() {
	}
}
