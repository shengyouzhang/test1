package com.zsy.frame.lib;

import java.security.Security;
import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.widget.ImageView;

import com.zsy.frame.lib.image.imageloader.core.ImageLoader;
import com.zsy.frame.lib.image.imageloader.core.listener.PauseOnScrollListener;
import com.zsy.frame.lib.image.imageloader.utils.ImgCacheUtils;
import com.zsy.frame.lib.net.http.volley.app.VolleyRequestManager;
import com.zsy.frame.lib.net.http.volley.toolbox.ImageLoader.ImageListener;
import com.zsy.frame.lib.ui.SYActivityManager;
import com.zsy.frame.lib.utils.SystemTool;

/**
 * @description：SYAppliction公共类
 * @author samy
 * @date 2015-1-29 下午12:36:44
 */
public abstract class SYApp extends Application {
	private static SYApp instance;
	public static int SCREEN_WIDTH;
	public static int SCREEN_HEIGHT;
	/** 利用全局内存进行变量传递 */
	private Map<String, Object> syAppMap = new HashMap<String, Object>();

	/** 图片缓存滑动监听 */
	public static PauseOnScrollListener mPauseOnScrollListener;

	public static SYApp getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		if (this.getPackageName().equals(SystemTool.getCurrProcName(this))) {
			SYCrashHandler.create(this);
			instance = this;
			SCREEN_WIDTH = getResources().getDisplayMetrics().widthPixels;
			SCREEN_HEIGHT = getResources().getDisplayMetrics().heightPixels;
			initSYLib();
			initBaseLib();
		}
	}

	protected void initSYLib() {
		initHttp();
		initImage();
	}

	protected abstract void initBaseLib();

	/**
	 * @description：这里图片处理显示，先配置volley(简单显示图片)和imageloader(复杂显示图片)
	 * @author samy
	 * @date 2015-1-29 下午2:55:04
	 */
	private void initImage() {
		initImageLoader();
		mPauseOnScrollListener = new PauseOnScrollListener(ImageLoader.getInstance(), true, true);
	}

	protected void initImageLoader() {
		ImageLoader.getInstance().init(ImgCacheUtils.getInstance(this).getImgLoaderConfig());
	}

	/**
	 * 使用volley框架加载图片
	 * 
	 * @param url
	 * @param imageListener
	 */
	public static void loadImg(ImageView view, String url, ImageListener imageListener) {
		VolleyRequestManager.getImageLoader().get(url, imageListener, SCREEN_WIDTH, SCREEN_HEIGHT);
	}

	// 初始话网络请求
	private void initHttp() {
		/**
		 * dns解析相关
		 * "networkaddress.cache.ttl"表示查询成功的缓存，
		 * "networkaddress.cache.negative.ttl"表示查询失败的缓存。
		 * 第二个参数表示缓存有效时间，单位是秒。
		 */
		Security.setProperty("networkaddress.cache.ttl", String.valueOf(10 * 60));
		Security.setProperty("networkaddress.cache.negative.ttl", String.valueOf(0));

		VolleyRequestManager.init(this, SYConfig.IS_DEBUG_ENABLE);
		// String userAgent = "volley/0";
		// HttpStack stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
		// VolleyRequestManager.init(this, stack, SYConfig.IS_DEBUG_ENABLE);
	}
	

	/**
	 * @description：清空全局变量数据
	 * @author samy
	 * @date 2015-1-29 下午3:36:33
	 */
	private void cleanMap() {
		if (!syAppMap.isEmpty()) {
			syAppMap.clear();
		}
	}
	
	@Override
	public void onTerminate() {
		SYActivityManager.create().AppExit(null);
		super.onTerminate();
	}
	
}
