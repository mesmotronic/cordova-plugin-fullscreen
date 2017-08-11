package com.mesmotronic.plugins;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.graphics.Point;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.graphics.Color;
import android.os.Handler;

public class FullScreenPlugin extends CordovaPlugin
{
	public static final String ACTION_IS_SUPPORTED = "isSupported";
	public static final String ACTION_IS_IMMERSIVE_MODE_SUPPORTED = "isImmersiveModeSupported";
	public static final String ACTION_IMMERSIVE_WIDTH = "immersiveWidth";
	public static final String ACTION_IMMERSIVE_HEIGHT = "immersiveHeight";
	public static final String ACTION_LEAN_MODE = "leanMode";
	public static final String ACTION_SHOW_SYSTEM_UI = "showSystemUI";
	public static final String ACTION_SHOW_UNDER_STATUS_BAR = "showUnderStatusBar";
	public static final String ACTION_SHOW_UNDER_SYSTEM_UI = "showUnderSystemUI";
	public static final String ACTION_IMMERSIVE_MODE = "immersiveMode";
	public static final String ACTION_SET_SYSTEM_UI_VISIBILITY = "setSystemUiVisibility";
	
	private CallbackContext context;
	private Activity activity;
	private Window window;
	private View decorView;
	private int mLastSystemUIVisibility = 0;
	private final Handler mLeanBackHandler = new Handler();
	private final Runnable mEnterLeanback = new Runnable() {
	    @Override
	    public void run() {
	        leanMode();
	    }
	};
	
	/**
     * Sets the context of the Command. This can then be used to do things like
     * get file paths associated with the Activity.
     *
     * @param cordova The context of the main Activity.
     * @param webView The CordovaWebView Cordova is running in.
     */
    @Override
    public void initialize(final CordovaInterface cordova, final CordovaWebView webView) {
        super.initialize(cordova, webView);

        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Clear flag FLAG_FORCE_NOT_FULLSCREEN which is set initially
                // by the Cordova.
                Window window = cordova.getActivity().getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

                // Read 'StatusBarBackgroundColor' from config.xml, default is #000000.
                setStatusBarBackgroundColor(preferences.getString("StatusBarBackgroundColor", "#000000"));
            }
        });
    }

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException
	{
		context = callbackContext;
		activity = cordova.getActivity();
		window = activity.getWindow();
		decorView = window.getDecorView();
		
		if (ACTION_IS_SUPPORTED.equals(action))
			return isSupported();
		else if (ACTION_IS_IMMERSIVE_MODE_SUPPORTED.equals(action))
			return isImmersiveModeSupported();
		else if (ACTION_IMMERSIVE_WIDTH.equals(action))
			return immersiveWidth();
		else if (ACTION_IMMERSIVE_HEIGHT.equals(action))
			return immersiveHeight();
		else if (ACTION_LEAN_MODE.equals(action))
			return leanMode();
		else if (ACTION_SHOW_SYSTEM_UI.equals(action))
			return showSystemUI();
		else if (ACTION_SHOW_UNDER_STATUS_BAR.equals(action))
			return showUnderStatusBar();
		else if (ACTION_SHOW_UNDER_SYSTEM_UI.equals(action))
			return showUnderSystemUI();
		else if (ACTION_IMMERSIVE_MODE.equals(action))
			return immersiveMode();
		else if (ACTION_SET_SYSTEM_UI_VISIBILITY.equals(action))
			return setSystemUiVisibility(args.getInt(0));
		
		return false;
	}
	
	protected void resetWindow()
	{
		decorView.setOnFocusChangeListener(null); 
		decorView.setOnSystemUiVisibilityChangeListener(null);
		
		window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
	}
	
	/**
	 * Are any of the features of this plugin supported?
	 */
	protected boolean isSupported()
	{
		boolean supported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
		
        PluginResult res = new PluginResult(PluginResult.Status.OK, supported);
        context.sendPluginResult(res);
		return supported;
	}
	
	/**
	 * Is immersive mode supported?
	 */
	protected boolean isImmersiveModeSupported()
	{
		boolean supported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
		
        PluginResult res = new PluginResult(PluginResult.Status.OK, supported);
        context.sendPluginResult(res);
		return supported;
	}
	
	/**
	 * The width of the screen in immersive mode
	 */
	protected boolean immersiveWidth()
	{
		activity.runOnUiThread(new Runnable()
		{
			@Override
			public void run() 
			{
				try
				{
					Point outSize = new Point();
					
					decorView.getDisplay().getRealSize(outSize);
					
			        PluginResult res = new PluginResult(PluginResult.Status.OK, outSize.x);
			        context.sendPluginResult(res);
				}
				catch (Exception e)
				{
					context.error(e.getMessage());
				}
			}
		});
		
		return true;
	}
	
	/**
	 * The height of the screen in immersive mode
	 */	
	protected boolean immersiveHeight()
	{
		activity.runOnUiThread(new Runnable()
		{
			@Override
			public void run() 
			{
				try
				{
					Point outSize = new Point();
					
					decorView.getDisplay().getRealSize(outSize);
					
			        PluginResult res = new PluginResult(PluginResult.Status.OK, outSize.y);
			        context.sendPluginResult(res);
				}
				catch (Exception e)
				{
					context.error(e.getMessage());
				}
			}
		});
        
		return true;
	}
	
	/**
	 * Hide system UI until user interacts
	 */
	protected boolean leanMode()
	{
		if (!isSupported())
		{
			context.error("Not supported");
			return false;
		}
		
		activity.runOnUiThread(new Runnable()
		{
			@Override
			public void run() 
			{
				try
				{
					resetWindow();
					
					int uiOptions = 
						View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_FULLSCREEN
			            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
			            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
			            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
					
					mLastSystemUIVisibility = uiOptions;
					decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
					{
						@Override
						public void onSystemUiVisibilityChange(int visibility) 
						{
							if((mLastSystemUIVisibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) != 0
                 					&& (visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0) {
        						resetHideTimer();
    						}
    						mLastSystemUIVisibility = visibility;
						}
					});

					decorView.setSystemUiVisibility(uiOptions);
					
					context.success();
				}
				catch (Exception e)
				{
					context.error(e.getMessage());
				}
			}
		});
		
		return true;
	}

	private void resetHideTimer() {
	    // First cancel any queued events - i.e. resetting the countdown clock
	    mLeanBackHandler.removeCallbacks(mEnterLeanback);
	    // And fire the event in 3s time
	    mLeanBackHandler.postDelayed(mEnterLeanback, 3000);
	}
	
	/**
	 * Show system UI
	 */
	protected boolean showSystemUI()
	{
		if (!isSupported())
		{
			context.error("Not supported");
			return false;
		}
		
		activity.runOnUiThread(new Runnable()
		{
			@Override
			public void run() 
			{
				try
				{
					resetWindow();
			        
					// Remove translucent theme from bars
					
					window.clearFlags
					(
						WindowManager.LayoutParams.FLAG_FULLSCREEN 
						| WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION 
						| WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
					);
					
			        // Update system UI
					
					decorView.setOnSystemUiVisibilityChangeListener(null);
					decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
					
					PluginResult res = new PluginResult(PluginResult.Status.OK, true);
			        context.sendPluginResult(res);
					
					context.success();
				}
				catch (Exception e)
				{
					context.error(e.getMessage());
				}
			}
		});			
		
		return true;
	}
	
	/**
	 * Extend your app underneath the status bar (Android 4.4+ only)
	 */
	protected boolean showUnderStatusBar()
	{
		if (!isImmersiveModeSupported())
		{
			context.error("Not supported");
			return false;
		}
		
		activity.runOnUiThread(new Runnable()
		{
			@Override
			public void run() 
			{
				try
				{
					resetWindow();
					
					// Make the status bar translucent
					
			        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			        
			        // Extend view underneath status bar
					
					int uiOptions = 
						View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
					
					decorView.setSystemUiVisibility(uiOptions);
					
					context.success();
				}
				catch (Exception e)
				{
					context.error(e.getMessage());
				}
			}
		});
		
		return true;
	}
	
	/**
	 * Extend your app underneath the system UI (Android 4.4+ only)
	 */
	protected boolean showUnderSystemUI()
	{
		if (!isImmersiveModeSupported())
		{
			context.error("Not supported");
			return false;
		}
		
		activity.runOnUiThread(new Runnable()
		{
			@Override
			public void run() 
			{
				try
				{
					resetWindow();
					
					// Make the status and nav bars translucent
					
					window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
					window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
					
					// Extend view underneath status and nav bars
					
					int uiOptions = 
							View.SYSTEM_UI_FLAG_LAYOUT_STABLE
							| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
					
					decorView.setSystemUiVisibility(uiOptions);
					
					context.success();
				}
				catch (Exception e)
				{
					context.error(e.getMessage());
				}
			}
		});
		
		return true;
	}
	
	/**
	 * Hide system UI and switch to immersive mode (Android 4.4+ only)
	 */
	protected boolean immersiveMode()
	{
		if (!isImmersiveModeSupported())
		{
			context.error("Not supported");
			return false;
		}
		
		activity.runOnUiThread(new Runnable()
		{
			@Override
			public void run() 
			{
				try
				{
					resetWindow();
					
					final int uiOptions = 
						View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
						| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_FULLSCREEN
						| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
					
					window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
					decorView.setSystemUiVisibility(uiOptions);
					
					decorView.setOnFocusChangeListener(new View.OnFocusChangeListener() 
					{
						@Override
						public void onFocusChange(View v, boolean hasFocus) 
						{
							if (hasFocus)
							{
								decorView.setSystemUiVisibility(uiOptions);
							}
						}
					});
					
					decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
					{
						@Override
						public void onSystemUiVisibilityChange(int visibility) 
						{
							decorView.setSystemUiVisibility(uiOptions);
						}
					});
					
					context.success();
				}
				catch (Exception e)
				{
					context.error(e.getMessage());
				}
			}
		});
			
		return true;
	}
	
	protected boolean setSystemUiVisibility(final int visibility)
	{
		if (!isSupported())
		{
			context.error("Not supported");
			return false;
		}
		
		activity.runOnUiThread(new Runnable()
		{
			@Override
			public void run() 
			{
				try
				{
					resetWindow();
					decorView.setSystemUiVisibility(visibility);
					context.success();
				}
				catch (Exception e)
				{
					context.error(e.getMessage());
				}
			}
		});
		
		return true;
	}
	
	private void setStatusBarBackgroundColor(final String colorPref) {
        if (Build.VERSION.SDK_INT >= 21) {
            if (colorPref != null && !colorPref.isEmpty()) {
                final Window window = cordova.getActivity().getWindow();
                // Method and constants not available on all SDKs but we want to be able to compile this code with any SDK
                window.clearFlags(0x04000000); // SDK 19: WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(0x80000000); // SDK 21: WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                try {
                    // Using reflection makes sure any 5.0+ device will work without having to compile with SDK level 21
                    window.getClass().getDeclaredMethod("setStatusBarColor", int.class).invoke(window, Color.parseColor(colorPref));
                } catch (IllegalArgumentException ignore) {
                } catch (Exception ignore) {
                }
            }
        }
    }
	
}
