package com.ningwuyue.sdk.nwyvideoplayersdk.util.common;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.ningwuyue.sdk.nwyvideoplayersdk.init.NWYVideoPlayer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static android.Manifest.permission.WRITE_SETTINGS;

public class ScreenUtils {

	private static int screenWidth = 0;

	private static int screenHeight = 0;
	private static int screenTotalHeight = 0;
	private static int statusBarHeight = 0;

	private static final int TITLE_HEIGHT = 0;

	/** 
     */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/** 
     */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static int getScreenWidth() {
		Context context= NWYVideoPlayer.getInstance().getApplication();
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		return screenWidth;
	}

	public static int getScreenHeight() {
		Context context= NWYVideoPlayer.getInstance().getApplication();
		int top = 0;
		if (context instanceof Activity) {
			top = ((Activity) context).getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
			if (top == 0) {
				top = (int) (TITLE_HEIGHT * getScreenDensity(context));
			}
		}
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		screenHeight = dm.heightPixels - top;
		return screenHeight;
	}

	public static int getScreenTotalHeight(Context context) {
		if (screenTotalHeight != 0) {
			return screenTotalHeight;
		}
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		screenTotalHeight = displayMetrics.heightPixels;
		return screenTotalHeight;
	}
	public static float getScreenDensity(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metric = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metric);
		return metric.density;
	}

	public static float getScreenDensityDpi(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metric = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metric);
		return metric.densityDpi;
	}
	public static int getStatusBarHeight(Context context) {
		if (statusBarHeight != 0) {
			return statusBarHeight;
		}
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return statusBarHeight;
	}
	public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }


	//获取屏幕原始尺寸高度，包括虚拟功能键高度
	public static int getDpi(Context context){
		int dpi = 0;
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		@SuppressWarnings("rawtypes")
        Class c;
		try {
			c = Class.forName("android.view.Display");
			@SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics",DisplayMetrics.class);
			method.invoke(display, displayMetrics);
			dpi=displayMetrics.heightPixels;
		}catch(Exception e){
			e.printStackTrace();
		}
		return dpi;
	}

	/**
	 * 获取 虚拟按键的高度
	 * @param context
	 * @return
	 */
	public static  int getBottomStatusHeight(Context context){
		int totalHeight = getDpi(context);

		int contentHeight = getScreenHeight();

		return totalHeight  - contentHeight;
	}

	/**
	 * 标题栏高度
	 * @return
	 */
	public static int getTitleHeight(Activity activity){
		return  activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
	}

	/**
	 * 获得状态栏的高度
	 *
	 * @param context
	 * @return
	 */
	public static int getStatusHeight(Context context)
	{

		int statusHeight = -1;
		try
		{
			Class<?> clazz = Class.forName("com.android.internal.R$dimen");
			Object object = clazz.newInstance();
			int height = Integer.parseInt(clazz.getField("status_bar_height")
					.get(object).toString());
			statusHeight = context.getResources().getDimensionPixelSize(height);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return statusHeight;
	}



	private ScreenUtils() {
		throw new UnsupportedOperationException("u can't instantiate me...");
	}

	

	/**
	 * Return the density of screen.
	 *
	 * @return the density of screen
	 */
	public static float getScreenDensity() {
		return NWYVideoPlayer.getInstance().getApplication().getResources().getDisplayMetrics().density;
	}

	/**
	 * Return the screen density expressed as dots-per-inch.
	 *
	 * @return the screen density expressed as dots-per-inch
	 */
	public static int getScreenDensityDpi() {
		return NWYVideoPlayer.getInstance().getApplication().getResources().getDisplayMetrics().densityDpi;
	}

	/**
	 * Set full screen.
	 *
	 * @param activity The activity.
	 */
	public static void setFullScreen(@NonNull final Activity activity) {
		activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
				| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
	}

	/**
	 * Set the screen to landscape.
	 *
	 * @param activity The activity.
	 */
	public static void setLandscape(@NonNull final Activity activity) {
		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}

	/**
	 * Set the screen to portrait.
	 *
	 * @param activity The activity.
	 */
	public static void setPortrait(@NonNull final Activity activity) {
		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	/**
	 * Return whether screen is landscape.
	 *
	 * @return {@code true}: yes<br>{@code false}: no
	 */
	public static boolean isLandscape() {
		return NWYVideoPlayer.getInstance().getApplication().getResources().getConfiguration().orientation
				== Configuration.ORIENTATION_LANDSCAPE;
	}

	/**
	 * Return whether screen is portrait.
	 *
	 * @return {@code true}: yes<br>{@code false}: no
	 */
	public static boolean isPortrait() {
		return NWYVideoPlayer.getInstance().getApplication().getResources().getConfiguration().orientation
				== Configuration.ORIENTATION_PORTRAIT;
	}

	/**
	 * Return the rotation of screen.
	 *
	 * @param activity The activity.
	 * @return the rotation of screen
	 */
	public static int getScreenRotation(@NonNull final Activity activity) {
		switch (activity.getWindowManager().getDefaultDisplay().getRotation()) {
			case Surface.ROTATION_0:
				return 0;
			case Surface.ROTATION_90:
				return 90;
			case Surface.ROTATION_180:
				return 180;
			case Surface.ROTATION_270:
				return 270;
			default:
				return 0;
		}
	}

	/**
	 * Return the bitmap of screen.
	 *
	 * @param activity The activity.
	 * @return the bitmap of screen
	 */
	public static Bitmap screenShot(@NonNull final Activity activity) {
		return screenShot(activity, false);
	}

	/**
	 * Return the bitmap of screen.
	 *
	 * @param activity          The activity.
	 * @param isDeleteStatusBar True to delete status bar, false otherwise.
	 * @return the bitmap of screen
	 */
	public static Bitmap screenShot(@NonNull final Activity activity, boolean isDeleteStatusBar) {
		View decorView = activity.getWindow().getDecorView();
		decorView.setDrawingCacheEnabled(true);
		decorView.buildDrawingCache();
		Bitmap bmp = decorView.getDrawingCache();
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		Bitmap ret;
		if (isDeleteStatusBar) {
			Resources resources = activity.getResources();
			int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
			int statusBarHeight = resources.getDimensionPixelSize(resourceId);
			ret = Bitmap.createBitmap(
					bmp,
					0,
					statusBarHeight,
					dm.widthPixels,
					dm.heightPixels - statusBarHeight
			);
		} else {
			ret = Bitmap.createBitmap(bmp, 0, 0, dm.widthPixels, dm.heightPixels);
		}
		decorView.destroyDrawingCache();
		return ret;
	}

	/**
	 * Return whether screen is locked.
	 *
	 * @return {@code true}: yes<br>{@code false}: no
	 */
	public static boolean isScreenLock() {
		KeyguardManager km =
				(KeyguardManager) NWYVideoPlayer.getInstance().getApplication().getSystemService(Context.KEYGUARD_SERVICE);
		return km != null && km.inKeyguardRestrictedInputMode();
	}

	/**
	 * Set the duration of sleep.
	 * <p>Must hold {@code <uses-permission android:name="android.permission.WRITE_SETTINGS" />}</p>
	 *
	 * @param duration The duration.
	 */
	@RequiresPermission(WRITE_SETTINGS)
	public static void setSleepDuration(final int duration) {
		Settings.System.putInt(
				NWYVideoPlayer.getInstance().getApplication().getContentResolver(),
				Settings.System.SCREEN_OFF_TIMEOUT,
				duration
		);
	}

	/**
	 * Return the duration of sleep.
	 *
	 * @return the duration of sleep.
	 */
	public static int getSleepDuration() {
		try {
			return Settings.System.getInt(
					NWYVideoPlayer.getInstance().getApplication().getContentResolver(),
					Settings.System.SCREEN_OFF_TIMEOUT
			);
		} catch (Settings.SettingNotFoundException e) {
			e.printStackTrace();
			return -123;
		}
	}

	/**
	 * Return whether device is tablet.
	 *
	 * @return {@code true}: yes<br>{@code false}: no
	 */
	public static boolean isTablet() {
		return (NWYVideoPlayer.getInstance().getApplication().getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK)
				>= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}
}