package com.zsy.frame.lib.image.imageloader.utils;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.zsy.frame.lib.image.imageloader.cache.disc.impl.UnlimitedDiskCache;
import com.zsy.frame.lib.image.imageloader.cache.disc.naming.Md5FileNameGenerator;
import com.zsy.frame.lib.image.imageloader.core.DisplayImageOptions;
import com.zsy.frame.lib.image.imageloader.core.ImageLoader;
import com.zsy.frame.lib.image.imageloader.core.ImageLoaderConfiguration;
import com.zsy.frame.lib.image.imageloader.core.assist.ImageScaleType;
import com.zsy.frame.lib.image.imageloader.core.assist.QueueProcessingType;
import com.zsy.frame.lib.image.imageloader.core.display.FadeInBitmapDisplayer;
import com.zsy.frame.lib.image.imageloader.core.display.SimpleBitmapDisplayer;
import com.zsy.frame.lib.image.imageloader.core.listener.ImageLoadingListener;
import com.zsy.frame.lib.image.imageloader.core.listener.SimpleImageLoadingListener;

/**
 * @description：
 * Android Universal Image Loader 是一个强大的、可高度定制的图片缓存，本文简称为UIL。
 * 简单的说 UIL 就做了一件事——获取图片并显示在相应的控件上。
 * 如果图片显示简单的话可以不用UIL显示，直接用Velloy自带的简单显示图片；
 * UniversalImageLoad加载显示类
 *  注意事项，必须在Application初始化配置一下；
 *  ImageLoader.getInstance().init(ImgCacheUtil.getInstance().getImgLoaderConfig());
 * @author samy
 * @date 2014年11月11日 下午2:07:16
 */
public class ImgCacheUtils {
	private static ImgCacheUtils imgCacheUtil = null;
	private static ImageLoaderConfiguration imageConfig;
	private DisplayImageOptions imageOptions;
	private Context mContext;

	public static ImgCacheUtils getInstance(Context context) {
		if (imgCacheUtil == null) {
			imgCacheUtil = new ImgCacheUtils(context);
		}
		return imgCacheUtil;
	}

	private ImgCacheUtils(Context context) {
		this.mContext = context;
		File cacheDir = StorageUtils.getOwnCacheDirectory(mContext, mContext.getPackageName() + File.separator + "cache/images");
		imageOptions = new DisplayImageOptions.Builder()// 开始构建, 显示的图片的各种格式
				.resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
				.cacheInMemory(true)// 开启内存缓存
				.cacheOnDisk(true) // 开启硬盘缓存
				// .displayer(new RoundedBitmapDisplayer(20))// 是否设置为圆角，弧度为多少；避免使用RoundedBitmapDisplayer.他会创建新的ARGB_8888格式的Bitmap对象；
				// .displayer(new FadeInBitmapDisplayer(100))// 是否图片加载好后渐入的动画时间
				.displayer(new SimpleBitmapDisplayer())// 正常显示一张图片　
				.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型;使用.bitmapConfig(Bitmap.config.RGB_565)代替ARGB_8888;
				.considerExifParams(true)// 是否考虑JPEG图像EXIF参数（旋转，翻转）
				.imageScaleType(ImageScaleType.EXACTLY)// 缩放级别
				// .imageScaleType(ImageScaleType.IN_SAMPLE_INT)//这两种配置缩放都推荐
				.build();// 构建完成
		imageConfig = new ImageLoaderConfiguration.Builder(context)// 开始构建 ,图片加载配置
				.threadPriority(Thread.NORM_PRIORITY - 2)// 设置线程优先级
				.threadPoolSize(3)// 线程池内加载的数量 ;减少配置之中线程池的大小，(.threadPoolSize).推荐1-5；
				.denyCacheImageMultipleSizesInMemory()// 设置加载的图片有多样的
				.tasksProcessingOrder(QueueProcessingType.LIFO)// 图片加载任务顺序
				// .memoryCache(new WeakMemoryCache())//使用.memoryCache(new WeakMemoryCache())，不要使用.cacheInMemory();
				.memoryCacheExtraOptions(480, 800) // 即保存的每个缓存文件的最大长宽
				.memoryCacheSizePercentage(60)// 图片内存占应用的60%；
				// .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())//使用HASHCODE对UIL进行加密命名
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())// 将保存的时候的URI名称用MD5 加密
				.diskCacheSize(50 * 1024 * 1024) // 缓存设置大小为50 Mb
				.diskCache(new UnlimitedDiskCache(cacheDir))// 自定义缓存路径
				.diskCacheFileCount(100) // 缓存的文件数量
				.denyCacheImageMultipleSizesInMemory()// 自动缩放
				// .imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
				// .memoryCacheExtraOptions(480, 800)//设置缓存图片时候的宽高最大值，默认为屏幕宽高;保存的每个缓存文件的最大长宽
				.defaultDisplayImageOptions(imageOptions)// 如果需要打开缓存机制，需要自己builde一个option,可以是DisplayImageOptions.createSimple()
				// .writeDebugLogs() // Remove for release app
				.build();// 构建完成
		// ImageLoader.getInstance().init(config);// 全局初始化此配置;mageLoaderConfiguration必须配置并且全局化的初始化这个配置ImageLoader.getInstance().init(config); 否则也会出现错误提示
	}

	public ImageLoaderConfiguration getImgLoaderConfig() {
		return imageConfig;
	}
	
	public DisplayImageOptions getImageOptionsConfig() {
		return imageOptions;
	}
	
	
	/**
	 * @description：下载图片，解析为 Bitmap 传递给回调接口。
	 * @author samy
	 * @date 2015-3-16 下午10:45:35
	 */
	public void displayImage(String imageUri){
		ImageLoader.getInstance().loadImage(imageUri, new SimpleImageLoadingListener() {
		    @Override
		    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
		    	
		    }
		});
	}
	
	

	/**
	 * String imageUri = "http://site.com/image.png"; // from Web
	 * String imageUri = "file:///mnt/sdcard/image.png"; // from SD card
	 * String imageUri = "content://media/external/audio/albumart/13"; // from content provider
	 * String imageUri = "assets://image.png"; // from assets
	 * String imageUri = "drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
	 * 加载自定义配置的图片
	 * 
	 * @param url
	 * @param imageView
	 * @param imageOptions
	 */
	public void displayImage(String imageUri, ImageView imageView) {
		ImageLoader.getInstance().displayImage(imageUri, imageView, imageOptions);
	}

	/**
	 * @param url
	 * @param imageView
	 * @param listener
	 *            监听图片下载情况
	 */
	public void displayImage(String imageUri, ImageView imageView, ImageLoadingListener listener) {
		ImageLoader.getInstance().displayImage(imageUri, imageView, imageOptions, listener);
	}
	
	

	/**
	 * @description：是否显示动画加载
	 *                       displayImage(url, imageView, new AnimateFirstDisplayListener());
	 * @author samy
	 * @date 2014年11月11日 下午3:18:06
	 */
	class AnimateFirstDisplayListener extends SimpleImageLoadingListener {
		final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}

	// 清空数组
	private void clean(AnimateFirstDisplayListener animateFirstDisplayListener) {
		if (null != animateFirstDisplayListener) {
			animateFirstDisplayListener.displayedImages.clear();
		}
	}
}