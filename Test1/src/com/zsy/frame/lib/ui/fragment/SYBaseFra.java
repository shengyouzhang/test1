package com.zsy.frame.lib.ui.fragment;

import android.os.Bundle;

import com.zsy.frame.lib.SYLoger;

/**
 * @description：Fragment生命周期打印调试
 * @author samy
 * @date 2015-1-22 下午11:01:28
 */
public abstract class SYBaseFra extends SYFrameFra {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SYLoger.state(this.getClass().getName(), "---------onCreateView ");
	}

	@Override
	public void onResume() {
		SYLoger.state(this.getClass().getName(), "---------onResume ");
		super.onResume();
	}

	@Override
	public void onPause() {
		SYLoger.state(this.getClass().getName(), "---------onPause ");
		super.onPause();
	}

	@Override
	public void onStop() {
		SYLoger.state(this.getClass().getName(), "---------onStop ");
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		SYLoger.state(this.getClass().getName(), "---------onDestroy ");
		super.onDestroyView();
	}
}
