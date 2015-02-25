package com.zsy.frame.lib.ui.activity;

import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import com.zsy.frame.lib.ui.SYActivityManager;
import com.zsy.frame.lib.ui.ViewInject;
import com.zsy.frame.lib.ui.fragment.SYBaseFra;

/**
 * @description：Activity和Fragment交互
 * @author samy
 * @date 2015-1-22 下午10:51:43
 */
public abstract class SYFragmentAct extends SYBaseAct {
	private boolean openBackListener = false;

	public SYFragmentAct() {
		openBackListener = getBackListener();
		setBackListener(false);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (openBackListener && keyCode == KeyEvent.KEYCODE_BACK && getFragmentManager().getBackStackEntryCount() == 0 && SYActivityManager.create().getCount() < 2) {
			ViewInject.create().getExitDialog(this);
		}
		return super.onKeyDown(keyCode, event);
	}

	/** 改变界面的fragment */
	protected void changeFragment(int resView, SYBaseFra targetFragment) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(resView, targetFragment, targetFragment.getClass().getName());
		transaction.commit();
	}

	/**
	 * 你应该在这里调用changeFragment(R.id.content, addStack, targetFragment);
	 * 
	 * @param targetFragment
	 *            要改变的Activity
	 */
	public abstract void changeFragment(SYBaseFra targetFragment);
}
