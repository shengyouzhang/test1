package com.zsy.frame.lib.ui.fragment;

import com.zsy.frame.lib.ui.annotation.AnnotateUtil;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

/**
 * @description：框架Fragment基类
 * @author samy
 * @date 2015-1-22 下午10:54:03
 */
public abstract class SYFrameFra extends Fragment implements OnClickListener {

	protected abstract View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle);

	protected void initWidget(View parentView) {
	}

	protected void initData() {
	}

	protected void initThreadData() {
	}

	protected void widgetClick(View v) {
	}

	@Override
	public void onClick(View v) {
		widgetClick(v);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflaterView(inflater, container, savedInstanceState);
		AnnotateUtil.initBindView(this, view);
		new Thread(new Runnable() {
			@Override
			public void run() {
				initThreadData();
			}
		}).start();
		initData();
		initWidget(view);
		return view;
	}
}
