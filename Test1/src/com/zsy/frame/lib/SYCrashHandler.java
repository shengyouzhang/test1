package com.zsy.frame.lib;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

import com.zsy.frame.lib.ui.SYActivityManager;
import com.zsy.frame.lib.utils.FileUtils;
import com.zsy.frame.lib.utils.SystemTool;

/**
 * UncaughtExceptionHandler：线程未捕获异常控制器是用来处理未捕获异常的。 如果程序出现了未捕获异常默认情况下则会出现强行关闭对话框
 * 实现该接口并注册为程序中的默认未捕获异常处理 这样当未捕获异常发生时，就可以做些异常处理操作 例如：收集异常信息，发送错误报告 等。
 * UncaughtException处理类,当程序发生Uncaught异常的时候,由该类来接管程序,并记录发送错误报告. <br>
 * <b>警告</b> : 如果需错误报告到服务器，需要设置 protected boolean openUpload = true;
 * 且需要手动重写uploadLog()方法<br>
 * <b>创建时间</b> 2014-7-2
 */
public class SYCrashHandler implements UncaughtExceptionHandler {
	private Context mContext;
	public boolean openUpload = true;
	private static final String DEFAULT_LOG_DIR = "log";
	// log文件的后缀名
	private static final String FILE_NAME_SUFFIX = ".log";
	private static SYCrashHandler sInstance = null;
	// 系统默认的异常处理（默认情况下，系统会终止当前的异常程序）
	private UncaughtExceptionHandler mDefaultCrashHandler;

	private SYCrashHandler(Context cxt) {
		// 获取系统默认的异常处理器
		mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
		// 将当前实例设为系统默认的异常处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
		// 获取Context，方便内部使用
		this.mContext = cxt.getApplicationContext();
	}

	public synchronized static SYCrashHandler create(Context cxt) {
		if (sInstance == null) {
			sInstance = new SYCrashHandler(cxt);
		}
		return sInstance;
	}

	/**
	 * 这个是最关键的函数，当程序中有未被捕获的异常，系统将会自动调用#uncaughtException方法
	 * thread为出现未捕获异常的线程，ex为未捕获的异常，有了这个ex，我们就可以得到异常信息。
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		try {
			// 导出异常信息到SD卡中
			saveToSDCard(ex);
		}
		catch (Exception e) {
		}
		finally {
			// 如果系统提供了默认的异常处理器，则交给系统去结束我们的程序，否则就由我们自己结束自己
			// ViewInject.longToast(mContext, "您的手机可能不兼容该功能");
			// if (mDefaultCrashHandler != null) {
			// mDefaultCrashHandler.uncaughtException(thread, ex);
			// } else {
			// ex.printStackTrace();
			// KJActivityManager.create().finishAllActivity();
			// }
			ex.printStackTrace();
			SYActivityManager.create().AppExit(null);
		}
	}

	private void saveToSDCard(Throwable ex) throws Exception {
		// File file = FileUtils.getSaveFile(logPath, SystemTool.getDataTime("yyyy-MM-dd-HH-mm-ss") + FILE_NAME_SUFFIX);
		File file = FileUtils.getSaveFile(mContext.getPackageName() + File.separator + DEFAULT_LOG_DIR, SystemTool.getDataTime("yyyy-MM-dd-HH-mm-ss") + FILE_NAME_SUFFIX);
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		// 导出发生异常的时间
		pw.println(SystemTool.getDataTime("yyyy-MM-dd-HH-mm-ss"));
		// 导出手机信息
		dumpPhoneInfo(pw);

		pw.println();
		// 导出异常的调用栈信息
		ex.printStackTrace(pw);
		pw.close();
	}

	private void dumpPhoneInfo(PrintWriter pw) throws NameNotFoundException {
		// 应用的版本名称和版本号
		PackageManager pm = mContext.getPackageManager();
		PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
		pw.print("App Version: ");
		pw.print(pi.versionName);
		pw.print('_');
		pw.println(pi.versionCode);
		pw.println();

		// android版本号
		pw.print("OS Version: ");
		pw.print(Build.VERSION.RELEASE);
		pw.print("_");
		pw.println(Build.VERSION.SDK_INT);
		pw.println();

		// 手机制造商
		pw.print("Vendor: ");
		pw.println(Build.MANUFACTURER);
		pw.println();

		// 手机型号
		pw.print("Model: ");
		pw.println(Build.MODEL);
		pw.println();

		// cpu架构
		pw.print("CPU ABI: ");
		pw.println(Build.CPU_ABI);
		pw.println();
	}
}