package com.zsy.frame.lib.ui;

import java.util.Stack;

import android.app.Activity;
import android.content.Context;

import com.zsy.frame.lib.ui.activity.ISYAct;

/**
 * @description：用于Activity管理和应用程序退出,模拟堆栈处理
 * @author samy
 * @date 2015-1-22 下午9:47:05
 */
final public class SYActivityManager {
	private static Stack<ISYAct> activityStack;

	private SYActivityManager() {
	}

	private static class ManagerHolder {
		private static final SYActivityManager instance = new SYActivityManager();
	}

	public static SYActivityManager create() {
		return ManagerHolder.instance;
	}

	/**
	 * 获取当前Activity栈中元素个数
	 */
	public int getCount() {
		return activityStack.size();
	}

	/**
	 * 添加Activity到栈
	 */
	public void addActivity(ISYAct activity) {
		if (activityStack == null) {
			activityStack = new Stack<ISYAct>();
		}
		activityStack.add(activity);
	}

	/**
	 * 获取当前Activity（栈顶Activity）
	 */
	public Activity topActivity() {
		if (activityStack == null) { throw new NullPointerException("Activity stack is Null,your Activity must extend BaseActivity"); }
		if (activityStack.isEmpty()) { return null; }
		ISYAct activity = activityStack.lastElement();
		return (Activity) activity;
	}

	/**
	 * 获取当前Activity（栈顶Activity） 没有找到则返回null
	 */
	public Activity findActivity(Class<?> cls) {
		ISYAct activity = null;
		for (ISYAct aty : activityStack) {
			if (aty.getClass().equals(cls)) {
				activity = aty;
				break;
			}
		}
		return (Activity) activity;
	}

	/**
	 * 结束当前Activity（栈顶Activity）
	 */
	public void finishActivity() {
		ISYAct activity = activityStack.lastElement();
		finishActivity((Activity) activity);
	}

	/**
	 * 结束指定的Activity(重载)
	 */
	public void finishActivity(Activity activity) {
		if (activity != null) {
			activityStack.remove(activity);
			activity.finish();
			activity = null;
		}
	}

	/**
	 * 结束指定的Activity(重载)
	 */
	public void finishActivity(Class<?> cls) {
		for (ISYAct activity : activityStack) {
			if (activity.getClass().equals(cls)) {
				finishActivity((Activity) activity);
			}
		}
	}

	/**
	 * 关闭除了指定activity以外的全部activity 如果cls不存在于栈中，则栈全部清空
	 * 
	 * @param cls
	 */
	public void finishOthersActivity(Class<?> cls) {
		for (ISYAct activity : activityStack) {
			if (!(activity.getClass().equals(cls))) {
				finishActivity((Activity) activity);
			}
		}
	}

	/**
	 * 结束所有Activity
	 */
	public void finishAllActivity() {
		for (int i = 0, size = activityStack.size(); i < size; i++) {
			if (null != activityStack.get(i)) {
				((Activity) activityStack.get(i)).finish();
			}
		}
		activityStack.clear();
	}

	/**
	 * 应用程序退出
	 */
	public void AppExit(Context context) {
		try {
			finishAllActivity();
			Runtime.getRuntime().exit(0);
		}
		catch (Exception e) {
			Runtime.getRuntime().exit(-1);
		}
	}
}