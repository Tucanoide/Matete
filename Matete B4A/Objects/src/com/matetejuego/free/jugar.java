package com.matetejuego.free;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class jugar extends Activity implements B4AActivity{
	public static jugar mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = true;
	public static final boolean includeTitle = false;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isFirst) {
			processBA = new BA(this.getApplicationContext(), null, null, "com.matetejuego.free", "com.matetejuego.free.jugar");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (jugar).");
				p.finish();
			}
		}
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		mostCurrent = this;
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(processBA, wl, true))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "com.matetejuego.free", "com.matetejuego.free.jugar");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.matetejuego.free.jugar", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (jugar) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (jugar) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEvent(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return jugar.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null) //workaround for emulator bug (Issue 2423)
            return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (jugar) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
			if (mostCurrent == null || mostCurrent != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (jugar) Resume **");
		    processBA.raiseEvent(mostCurrent._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}

public anywheresoftware.b4a.keywords.Common __c = null;
public static com.matetejuego.free.publicos._tcolorespaleta[] _gt_colorespaleta = null;
public static int _gi_historia_p_arriba = 0;
public static int _gi_historia_p_abajo = 0;
public static int _gi_historia_textsize = 0;
public static boolean _gb_palabrarejugada = false;
public static anywheresoftware.b4a.http.HttpClientWrapper _vvvvv4 = null;
public static anywheresoftware.b4a.http.HttpClientWrapper _vvvvv5 = null;
public static String _vvvvv6 = "";
public static String _vvvvv7 = "";
public static boolean _vvvvv0 = false;
public static anywheresoftware.b4a.objects.AnalyticsTracker _vvvvvv1 = null;
public static anywheresoftware.b4a.inappbilling3.BillingManager3 _vvvvvv2 = null;
public static String _vvvvvvvvvvvvvvvvv6 = "";
public static com.matetejuego.free.publicos._tproductoscomprados[] _vvvvvv3 = null;
public static com.matetejuego.free.publicos._tproductosps[] _vvvvvv4 = null;
public static int _vvvvvv5 = 0;
public static String[] _vvvvvv6 = null;
public static int _gi_animacionencurso = 0;
public static int _gi_neuronascompradas = 0;
public static int _gi_productopremium = 0;
public static boolean _gb_espremium = false;
public static boolean _gb_premiumremoto = false;
public static String _gs_idappfireworks = "";
public static boolean _vvvvvv7 = false;
public static boolean _vvvvvv0 = false;
public static anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _vvvvvvv1 = null;
public static anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _vvvvvvv2 = null;
public static anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _vvvvvvv3 = null;
public static anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _vvvvvvv4 = null;
public static anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _vvvvvvv5 = null;
public static anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _vvvvvvv6 = null;
public static int _gi_propagandacuenta = 0;
public static int _vvvvvvv7 = 0;
public static String _vvvvvvv0 = "";
public static int _vvvvvvvv1 = 0;
public static String _vvvvvvvv2 = "";
public static int _gi_propagandarate = 0;
public static com.matetejuego.free.publicos._tconfigurapantalla _vvvvvvvv3 = null;
public static int _vvvvvvvv4 = 0;
public static com.matetejuego.free.publicos._tquerysremotos _vvvvvvvv5 = null;
public static com.matetejuego.free.publicos._tpistas[] _vvvvvvvv6 = null;
public static com.matetejuego.free.publicos._tletraspalabra[] _vvvvvvvv7 = null;
public static com.matetejuego.free.publicos._tletraselegir[] _vvvvvvvv0 = null;
public static String _vvvvvvvvv1 = "";
public static String _vvvvvvvvv2 = "";
public static boolean _vvvvvvvvv3 = false;
public static int _vvvvvvvvv4 = 0;
public static String _g_nombrebaselocaljuego = "";
public static String _g_nombrebaselocalusuario = "";
public static String _g_spswbasej = "";
public static String _g_spswbaseu = "";
public static String _g_dirgrabable = "";
public static anyhwheresoftware.b4a.objects.sqlcipher.SQLCipher _g_sqlbaselocaljuego = null;
public static anyhwheresoftware.b4a.objects.sqlcipher.SQLCipher _g_sqlbaselocalusuario = null;
public static com.matetejuego.free.publicos._tpalabra _vvvvvvvvvvvvvvvvvv4 = null;
public static com.matetejuego.free.publicos._tnivel _vvvvvvvvvvvvvvvvvv7 = null;
public static int _g_devicevaluesheight = 0;
public static int _g_devicevalueswidth = 0;
public static float _g_devicevaluesscreensize = 0f;
public static float _g_devicevaluesscale = 0f;
public static boolean _vvvvvvvvv5 = false;
public static boolean _vvvvvvvvv6 = false;
public static boolean _vvvvvvvvv7 = false;
public static boolean _vvvvvvvvv0 = false;
public static boolean _vvvvvvvvvv1 = false;
public static com.matetejuego.free.jugar._tcolorpaneles[][] _vvvvvvvvvv2 = null;
public static com.matetejuego.free.jugar._tcolorpaneles[][] _vvvvvvvvvv3 = null;
public static int _gi_combinacioncolores = 0;
public static anywheresoftware.b4a.keywords.constants.TypefaceWrapper _vvvvvvvvvv4 = null;
public static anywheresoftware.b4a.keywords.constants.TypefaceWrapper _vvvvvvvvvv5 = null;
public anywheresoftware.b4a.objects.ScrollViewWrapper _vvvvvvvvvvvvvvvvvvvvv7 = null;
public com.matetejuego.free.publicos._tcolores _gt_color = null;
public anywheresoftware.b4a.objects.PanelWrapper[] _vvvvvvvvvvvvvvvvvvvvv0 = null;
public anywheresoftware.b4a.objects.LabelWrapper _vvvvvvvvvvvvvvvvvvvvvv1 = null;
public com.matetejuego.free.slidemenu _vvvvvvvvvvvvvvvvvvvvvv2 = null;
public static int _sonido_adivino = 0;
public static int _sonido_error = 0;
public static int _sonido_nuevapalabra = 0;
public anywheresoftware.b4a.audio.SoundPoolWrapper _sound_adivino = null;
public static int _isound_adivino = 0;
public anywheresoftware.b4a.audio.SoundPoolWrapper _sound_error = null;
public static int _isound_error = 0;
public anywheresoftware.b4a.audio.SoundPoolWrapper _sound_nuevapalabra = null;
public static int _isound_nuevapalabra = 0;
public com.datasteam.b4a.socialapi.BaseProviderActivity _vvvvvvvvvvvvvvvvvvvvvv3 = null;
public anywheresoftware.b4a.objects.LabelWrapper _vvvvvvvvvvvvvvvvvvvvvv4 = null;
public static int _vvvvvvvvvvvvvvvvvvvvvv5 = 0;
public anywheresoftware.b4a.objects.LabelWrapper[] _vvvvvvvvvvvvvvvvvvvv7 = null;
public anywheresoftware.b4a.objects.LabelWrapper[] _vvvvvvvvvvvvvvvvvvv1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _vvvvvvvvvvvvvvvvvvv4 = null;
public com.leadbolt.b4a.LeadBoltB4A _vvvvvvvvvvvvvvvvvv1 = null;
public com.leadbolt.b4a.LeadBoltB4A _vvvvvvvvvvvvvvvvvvvvvv6 = null;
public com.leadbolt.b4a.LeadBoltB4A _vvvvvvvvvvvvvvvvvvvvvv7 = null;
public io.displayb4a.iodisplayWrapper _vvvvvvvvvvvvvvvvvvvvvv0 = null;
public static int _g_imonedas = 0;
public static int _g_ipuntos = 0;
public static int _g_ijugadas = 0;
public static int _g_ipalabras = 0;
public anywheresoftware.b4a.objects.LabelWrapper _vvvvvvvvvvvvvvvvvvvvvvv1 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imgavance = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imgmenu = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imgneuronas = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblavance = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblneuronas = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblv2nivel = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel1 = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel2 = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel3 = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel4 = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel5 = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel6 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imgpedirletra = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imgsaltarpalabra = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imgcompartir = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblpedirletra = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblsaltarpalabra = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblv2def = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel51 = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel41 = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel61 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbl41 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbl51 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbl61 = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel21 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbl21 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblcompartir = null;
public flm.b4a.animationplus.AnimationPlusWrapper _vvvvvvvvvvvvvvvv0 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imganimacion = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _img51 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _img51facebook = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _img51twitter = null;
public anywheresoftware.b4a.objects.LabelWrapper _vvvvvvvvvvvvvvvvvvvvvvv2 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbl51facebook = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbl51twitter = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imgbajarletras = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblbajarletras = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbl51mensajematete = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblcalctextsize = null;
public anywheresoftware.b4a.objects.ScrollViewWrapper _scrviewayuda = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imgshadow = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel11 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbl11 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imgloading = null;
public anywheresoftware.b4a.objects.PanelWrapper _pnlloading = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblloading = null;
public anywheresoftware.b4a.objects.PanelWrapper _pnlinvisible = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblpedirletracosto = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblsaltarpalabracosto = null;
public anywheresoftware.b4a.objects.PanelWrapper _pnlhistoria = null;
public anywheresoftware.b4a.objects.PanelWrapper _pnlhistfiltro = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblfiltro = null;
public com.matetejuego.free.main _vvvvvvvvvvvvvvv6 = null;
public com.matetejuego.free.publicos _vvvvvvvvvvvvvvv7 = null;
public com.matetejuego.free.facebookactivity _vvvvvvvvvvvvvvvv1 = null;
public com.matetejuego.free.twitter _vvvvvvvvvvvvvvvv2 = null;
public com.matetejuego.free.svcbaseremota _vvvvvvvvvvvvvvvv3 = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static class _tcolorpaneles{
public boolean IsInitialized;
public int iColorR;
public int iColorG;
public int iColorB;
public void Initialize() {
IsInitialized = true;
iColorR = 0;
iColorG = 0;
iColorB = 0;
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 266;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 268;BA.debugLine="Activity.LoadLayout(\"Loading\")";
mostCurrent._activity.LoadLayout("Loading",mostCurrent.activityBA);
 //BA.debugLineNum = 269;BA.debugLine="Publicos.Set_PantallaCargaSet(Activity, imgLoadin";
mostCurrent._vvvvvvvvvvvvvvv7._set_pantallacargaset(mostCurrent.activityBA,mostCurrent._activity,mostCurrent._imgloading,mostCurrent._lblloading,anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (151),(int) (89),(int) (152)),_vvvvv0);
 //BA.debugLineNum = 270;BA.debugLine="DoEvents";
anywheresoftware.b4a.keywords.Common.DoEvents();
 //BA.debugLineNum = 271;BA.debugLine="Activity.LoadLayout(\"JugarV2\")";
mostCurrent._activity.LoadLayout("JugarV2",mostCurrent.activityBA);
 //BA.debugLineNum = 273;BA.debugLine="If FirstTime Then";
if (_firsttime) { 
 //BA.debugLineNum = 276;BA.debugLine="tracker.Initialize";
_vvvvvv1.Initialize(processBA);
 //BA.debugLineNum = 279;BA.debugLine="gi_PropagandaCuenta = Rnd(0,1)";
_gi_propagandacuenta = anywheresoftware.b4a.keywords.Common.Rnd((int) (0),(int) (1));
 //BA.debugLineNum = 282;BA.debugLine="httpRemoto.Initialize(\"httpRemoto\")";
_vvvvv4.Initialize("httpRemoto");
 //BA.debugLineNum = 283;BA.debugLine="httpRemotoSelect.Initialize(\"httpRemotoSelect\")";
_vvvvv5.Initialize("httpRemotoSelect");
 //BA.debugLineNum = 286;BA.debugLine="g_DirGrabable = Publicos.Get_DirectorioBase";
_g_dirgrabable = mostCurrent._vvvvvvvvvvvvvvv7._get_directoriobase(mostCurrent.activityBA);
 //BA.debugLineNum = 287;BA.debugLine="v2_CopiaImagenes ' Copia las imágenes desde asse";
_v2_copiaimagenes();
 //BA.debugLineNum = 288;BA.debugLine="V2_CargaImagenesEstaticasEnVariables";
_v2_cargaimagenesestaticasenvariables();
 //BA.debugLineNum = 292;BA.debugLine="Create_Variables ' genera variables de tipo esas";
_create_variables();
 //BA.debugLineNum = 294;BA.debugLine="Publicos.AbreBasesParam(g_sqlBaseLocalJuego, g_N";
mostCurrent._vvvvvvvvvvvvvvv7._vv1(mostCurrent.activityBA,_g_sqlbaselocaljuego,_g_nombrebaselocaljuego,_g_spswbasej,_g_sqlbaselocalusuario,_g_nombrebaselocalusuario,_g_spswbaseu);
 //BA.debugLineNum = 297;BA.debugLine="CheckPremium";
_vvvvvvvvvvvvvvvvv5();
 //BA.debugLineNum = 301;BA.debugLine="manager.Initialize(\"manager\", key)";
_vvvvvv2.Initialize(processBA,"manager",_vvvvvvvvvvvvvvvvv6);
 //BA.debugLineNum = 302;BA.debugLine="manager.DebugLogging = True";
_vvvvvv2.setDebugLogging(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 304;BA.debugLine="gi_PropagandaCuenta = Propaganda_SetAleatoria";
_gi_propagandacuenta = _propaganda_setaleatoria();
 }else {
 //BA.debugLineNum = 306;BA.debugLine="manager.GetOwnedProducts";
_vvvvvv2.GetOwnedProducts(processBA);
 };
 //BA.debugLineNum = 311;BA.debugLine="Propaganda_Crear";
_propaganda_crear();
 //BA.debugLineNum = 312;BA.debugLine="Set_ColoresVariable (0)";
_set_coloresvariable((int) (0));
 //BA.debugLineNum = 313;BA.debugLine="V2_MenuGenera";
_v2_menugenera();
 //BA.debugLineNum = 317;BA.debugLine="bCanceloFacebookoTwitter= False";
_vvvvvvvvv0 = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 318;BA.debugLine="bPublicoEnFacebook = False";
_vvvvvvvvv6 = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 319;BA.debugLine="bPublicoEnTwitter = False";
_vvvvvvvvv7 = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 322;BA.debugLine="lblLoading.RemoveView";
mostCurrent._lblloading.RemoveView();
 //BA.debugLineNum = 323;BA.debugLine="imgLoading.visible = False";
mostCurrent._imgloading.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 325;BA.debugLine="End Sub";
return "";
}
public static boolean  _activity_keypress(int _keycode) throws Exception{
 //BA.debugLineNum = 1015;BA.debugLine="Sub activity_KeyPress(KeyCode As Int) As Boolean";
 //BA.debugLineNum = 1017;BA.debugLine="If KeyCode = KeyCodes.KEYCODE_BACK Then";
if (_keycode==anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_BACK) { 
 //BA.debugLineNum = 1020;BA.debugLine="If iPantallaActiva = xConfiguraPantalla.Jugar And";
if (_vvvvvvvv4==_vvvvvvvv3.Jugar && _vvvvvvvvv3) { 
 //BA.debugLineNum = 1021;BA.debugLine="V2_PantallaConfigura(xConfiguraPantalla.Historia";
_v2_pantallaconfigura(_vvvvvvvv3.Historia,(int) (0));
 }else {
 //BA.debugLineNum = 1023;BA.debugLine="If iPantallaActiva = xConfiguraPantalla.jugar Or";
if (_vvvvvvvv4==_vvvvvvvv3.Jugar || _vvvvvvvv4==_vvvvvvvv3.Premium || _vvvvvvvv4==_vvvvvvvv3.FinDeJuego) { 
 //BA.debugLineNum = 1024;BA.debugLine="StartActivity(\"Main\")";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)("Main"));
 //BA.debugLineNum = 1025;BA.debugLine="Activity.finish";
mostCurrent._activity.Finish();
 }else {
 //BA.debugLineNum = 1029;BA.debugLine="If iPantallaActiva = xConfiguraPantalla.Adivino";
if (_vvvvvvvv4==_vvvvvvvv3.Adivino) { 
 //BA.debugLineNum = 1030;BA.debugLine="lbl61_Click";
_lbl61_click();
 }else {
 //BA.debugLineNum = 1032;BA.debugLine="V2_PantallaConfigura(xConfiguraPantalla.Jugar,";
_v2_pantallaconfigura(_vvvvvvvv3.Jugar,(int) (0));
 };
 };
 };
 //BA.debugLineNum = 1036;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
 }else {
 //BA.debugLineNum = 1038;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 1041;BA.debugLine="End Sub";
return false;
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 406;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 408;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
boolean _bnorefrescar = false;
 //BA.debugLineNum = 327;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 328;BA.debugLine="Dim bNoRefrescar As Boolean = bCanceloFacebookoTw";
_bnorefrescar = _vvvvvvvvv0 || _vvvvvvvvv6 || _vvvvvvvvv7 || _vvvvvvvvvv1;
 //BA.debugLineNum = 330;BA.debugLine="bAnulaInsterstitialInicial = True";
_vvvvvv7 = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 333;BA.debugLine="If bNoRefrescar Then";
if (_bnorefrescar) { 
 //BA.debugLineNum = 335;BA.debugLine="V2_PantallaConfigura(xConfiguraPantalla.Jugar,0";
_v2_pantallaconfigura(_vvvvvvvv3.Jugar,(int) (0));
 //BA.debugLineNum = 336;BA.debugLine="If bComproNeuronas Then";
if (_vvvvvvvvvv1) { 
 //BA.debugLineNum = 337;BA.debugLine="V2_PantallaConfigura (xConfiguraPantalla.Compro";
_v2_pantallaconfigura(_vvvvvvvv3.ComproNeuronas,_gi_neuronascompradas);
 //BA.debugLineNum = 338;BA.debugLine="bComproNeuronas=False";
_vvvvvvvvvv1 = anywheresoftware.b4a.keywords.Common.False;
 }else {
 //BA.debugLineNum = 342;BA.debugLine="Neuronas_Mostrar";
_neuronas_mostrar();
 };
 }else {
 //BA.debugLineNum = 345;BA.debugLine="Log(\"reume else norefrescar\")";
anywheresoftware.b4a.keywords.Common.Log("reume else norefrescar");
 //BA.debugLineNum = 346;BA.debugLine="V2_SetPantalla";
_v2_setpantalla();
 };
 //BA.debugLineNum = 351;BA.debugLine="SonidosInicia";
_vvvvvvvvvvvvvvvvv7();
 //BA.debugLineNum = 353;BA.debugLine="If bFinDeJuego Or Publicos.get_EsUltimaPalabra(g_";
if (_vvvvvvvvv3 || mostCurrent._vvvvvvvvvvvvvvv7._get_esultimapalabra(mostCurrent.activityBA,(anywheresoftware.b4a.sql.SQL)(_g_sqlbaselocalusuario),_g_sqlbaselocaljuego)==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 354;BA.debugLine="imgLoading.Visible = True";
mostCurrent._imgloading.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 356;BA.debugLine="V2_PantallaConfigura(xConfiguraPantalla.FinDeJue";
_v2_pantallaconfigura(_vvvvvvvv3.FinDeJuego,(int) (5));
 }else {
 //BA.debugLineNum = 359;BA.debugLine="If bNoRefrescar Then";
if (_bnorefrescar) { 
 //BA.debugLineNum = 360;BA.debugLine="bCanceloFacebookoTwitter=False";
_vvvvvvvvv0 = anywheresoftware.b4a.keywords.Common.False;
 }else {
 //BA.debugLineNum = 362;BA.debugLine="InicioMatete";
_vvvvvvvvvvvvvvvvv0();
 };
 };
 //BA.debugLineNum = 365;BA.debugLine="If gb_EsPremium = False Then";
if (_gb_espremium==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 366;BA.debugLine="adBC.loadAd()";
mostCurrent._vvvvvvvvvvvvvvvvvv1.loadAd();
 };
 //BA.debugLineNum = 369;BA.debugLine="If bPublicoEnFacebook Then";
if (_vvvvvvvvv6) { 
 //BA.debugLineNum = 370;BA.debugLine="bPublicoEnFacebook = False";
_vvvvvvvvv6 = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 371;BA.debugLine="ToastMessageShow(\"Publicación exitosa en tu muro";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Publicación exitosa en tu muro!",anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 375;BA.debugLine="If bPublicoEnTwitter Then";
if (_vvvvvvvvv7) { 
 //BA.debugLineNum = 376;BA.debugLine="bPublicoEnTwitter = False";
_vvvvvvvvv7 = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 377;BA.debugLine="ToastMessageShow(\"Publicación exitosa en Twitter";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Publicación exitosa en Twitter!",anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 382;BA.debugLine="If bIniciarPremium Then";
if (_vvvvv0) { 
 //BA.debugLineNum = 383;BA.debugLine="V2_PantallaConfigura(xConfiguraPantalla.Premium,";
_v2_pantallaconfigura(_vvvvvvvv3.Premium,(int) (0));
 };
 //BA.debugLineNum = 390;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvvvvv2(boolean _badivino) throws Exception{
 //BA.debugLineNum = 835;BA.debugLine="Sub AdivinoGrabaNivel (bAdivino As Boolean)";
 //BA.debugLineNum = 837;BA.debugLine="Try";
try { //BA.debugLineNum = 839;BA.debugLine="AdivinoGrabaNivel_Avance (bAdivino)";
_adivinograbanivel_avance(_badivino);
 //BA.debugLineNum = 842;BA.debugLine="AdivinoGrabaNivel_Usuario(bAdivino)";
_adivinograbanivel_usuario(_badivino);
 } 
       catch (Exception e5) {
			processBA.setLastException(e5); //BA.debugLineNum = 845;BA.debugLine="ManejaErrorJugar(\"AdivinoGrabaNivel\",\"Error Grab";
_vvvvvvvvvvvvvvvvvv3("AdivinoGrabaNivel","Error GrabaNivel");
 };
 //BA.debugLineNum = 849;BA.debugLine="End Sub";
return "";
}
public static boolean  _adivinograbanivel_avance(boolean _badivino) throws Exception{
String _ssql = "";
boolean _bret = false;
String _sfecha = "";
 //BA.debugLineNum = 784;BA.debugLine="Sub AdivinoGrabaNivel_Avance(bAdivino As Boolean)";
 //BA.debugLineNum = 785;BA.debugLine="Dim sSql As String, bRet As Boolean, sFecha As Str";
_ssql = "";
_bret = false;
_sfecha = "";
 //BA.debugLineNum = 787;BA.debugLine="Try";
try { //BA.debugLineNum = 789;BA.debugLine="If xtPalabra.bRejugada = False Or xtPalabra.bSal";
if (_vvvvvvvvvvvvvvvvvv4.bRejugada==anywheresoftware.b4a.keywords.Common.False || _vvvvvvvvvvvvvvvvvv4.bSalteada) { 
 //BA.debugLineNum = 790;BA.debugLine="If bAdivino Then";
if (_badivino) { 
 //BA.debugLineNum = 791;BA.debugLine="sSql = \"Update Avance Set adivinada=1, Saltead";
_ssql = "Update Avance Set adivinada=1, Salteada =0";
 }else {
 //BA.debugLineNum = 793;BA.debugLine="sSql = \"Update Avance Set salteada =1, adivina";
_ssql = "Update Avance Set salteada =1, adivinada = 0 ";
 };
 //BA.debugLineNum = 795;BA.debugLine="sFecha = DateTime.Date(DateTime.now)";
_sfecha = anywheresoftware.b4a.keywords.Common.DateTime.Date(anywheresoftware.b4a.keywords.Common.DateTime.getNow());
 //BA.debugLineNum = 796;BA.debugLine="sSql = sSql & \", Fin ='\" & sFecha & \"' where id";
_ssql = _ssql+", Fin ='"+_sfecha+"' where idPalabra = "+BA.NumberToString(_vvvvvvvvvvvvvvvvvv4.idPalabra);
 //BA.debugLineNum = 797;BA.debugLine="g_sqlBaseLocalUsuario.ExecNonQuery (sSql)";
_g_sqlbaselocalusuario.ExecNonQuery(_ssql);
 //BA.debugLineNum = 799;BA.debugLine="BaseRemota_GrabarAvance (xtPalabra.idPalabra)";
_baseremota_grabaravance(_vvvvvvvvvvvvvvvvvv4.idPalabra);
 };
 //BA.debugLineNum = 802;BA.debugLine="bRet = True";
_bret = anywheresoftware.b4a.keywords.Common.True;
 } 
       catch (Exception e16) {
			processBA.setLastException(e16); //BA.debugLineNum = 805;BA.debugLine="bRet = False";
_bret = anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 808;BA.debugLine="Return bRet";
if (true) return _bret;
 //BA.debugLineNum = 809;BA.debugLine="End Sub";
return false;
}
public static boolean  _adivinograbanivel_usuario(boolean _badivino) throws Exception{
String _ssql = "";
boolean _bret = false;
int _imonxniv = 0;
 //BA.debugLineNum = 811;BA.debugLine="Sub AdivinoGrabaNivel_Usuario (bAdivino As Boolean";
 //BA.debugLineNum = 812;BA.debugLine="Dim sSql As String, bRet As Boolean, iMonxNiv As I";
_ssql = "";
_bret = false;
_imonxniv = 0;
 //BA.debugLineNum = 814;BA.debugLine="Try";
try { //BA.debugLineNum = 815;BA.debugLine="If xtPalabra.bRejugada = False Or xtPalabra.bSal";
if (_vvvvvvvvvvvvvvvvvv4.bRejugada==anywheresoftware.b4a.keywords.Common.False || _vvvvvvvvvvvvvvvvvv4.bSalteada) { 
 //BA.debugLineNum = 818;BA.debugLine="If bAdivino Then";
if (_badivino) { 
 //BA.debugLineNum = 820;BA.debugLine="iMonxNiv = Get_MonedasPorNivel";
_imonxniv = _get_monedaspornivel();
 //BA.debugLineNum = 821;BA.debugLine="sSql =\"Update Usuario Set Puntos = ifnull(Punt";
_ssql = "Update Usuario Set Puntos = ifnull(Puntos,1)+1,  Monedas = ifnull(monedas,0) + "+BA.NumberToString(_imonxniv);
 }else {
 //BA.debugLineNum = 823;BA.debugLine="sSql =\"Update Usuario Set Monedas = monedas -";
_ssql = "Update Usuario Set Monedas = monedas - "+BA.NumberToString(_get_costosaltarpalabra());
 };
 //BA.debugLineNum = 826;BA.debugLine="g_sqlBaseLocalUsuario.ExecNonQuery(sSql)";
_g_sqlbaselocalusuario.ExecNonQuery(_ssql);
 };
 //BA.debugLineNum = 828;BA.debugLine="bRet = True";
_bret = anywheresoftware.b4a.keywords.Common.True;
 } 
       catch (Exception e14) {
			processBA.setLastException(e14); //BA.debugLineNum = 830;BA.debugLine="bRet = False";
_bret = anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 832;BA.debugLine="Return bRet";
if (true) return _bret;
 //BA.debugLineNum = 833;BA.debugLine="End Sub";
return false;
}
public static String  _vvvvvvvvvvvvvvvvvv5() throws Exception{
int _itop = 0;
 //BA.debugLineNum = 745;BA.debugLine="Sub AdivinoPalabra";
 //BA.debugLineNum = 746;BA.debugLine="Dim iTop As Int";
_itop = 0;
 //BA.debugLineNum = 749;BA.debugLine="Sonido(SONIDO_ADIVINO)";
_vvvvvvvvvvvvvvvvvv6(_sonido_adivino);
 //BA.debugLineNum = 751;BA.debugLine="AdivinoGrabaNivel(True)";
_vvvvvvvvvvvvvvvvvv2(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 753;BA.debugLine="V2_PantallaConfigura(xConfiguraPantalla.Adivino,0";
_v2_pantallaconfigura(_vvvvvvvv3.Adivino,(int) (0));
 //BA.debugLineNum = 760;BA.debugLine="End Sub";
return "";
}
public static String  _anima_desplazaimagen(anywheresoftware.b4a.objects.ImageViewWrapper _imganimar,int _ifromdx,int _ifromdy,int _itodx,int _itody,int _imilisegundos,int _iinterpolator) throws Exception{
 //BA.debugLineNum = 3231;BA.debugLine="Sub Anima_DesplazaImagen (imgAnimar As ImageView,";
 //BA.debugLineNum = 3232;BA.debugLine="AnimPlus.SetInterpolator(iInterPolator)";
mostCurrent._vvvvvvvvvvvvvvvv0.SetInterpolator(_iinterpolator);
 //BA.debugLineNum = 3233;BA.debugLine="AnimPlus.InitializeTranslate(\"AnimPlus\", iFromDx,";
mostCurrent._vvvvvvvvvvvvvvvv0.InitializeTranslate(mostCurrent.activityBA,"AnimPlus",(float) (_ifromdx),(float) (_ifromdy),(float) (_itodx),(float) (_itody));
 //BA.debugLineNum = 3234;BA.debugLine="AnimPlus.PersistAfter = False";
mostCurrent._vvvvvvvvvvvvvvvv0.setPersistAfter(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3236;BA.debugLine="Activity.invalidate";
mostCurrent._activity.Invalidate();
 //BA.debugLineNum = 3237;BA.debugLine="AnimPlus.Duration = iMilisegundos";
mostCurrent._vvvvvvvvvvvvvvvv0.setDuration((long) (_imilisegundos));
 //BA.debugLineNum = 3238;BA.debugLine="If AnimPlus.IsInitialized Then";
if (mostCurrent._vvvvvvvvvvvvvvvv0.IsInitialized()) { 
 //BA.debugLineNum = 3239;BA.debugLine="AnimPlus.Stop(imgAnimar)";
mostCurrent._vvvvvvvvvvvvvvvv0.Stop((android.view.View)(_imganimar.getObject()));
 //BA.debugLineNum = 3240;BA.debugLine="Activity.Invalidate";
mostCurrent._activity.Invalidate();
 //BA.debugLineNum = 3241;BA.debugLine="AnimPlus.Start(imgAnimar)";
mostCurrent._vvvvvvvvvvvvvvvv0.Start((android.view.View)(_imganimar.getObject()));
 };
 //BA.debugLineNum = 3244;BA.debugLine="End Sub";
return "";
}
public static String  _anima_desplazascreenshot() throws Exception{
anywheresoftware.b4a.objects.ImageViewWrapper _imgscreenshot = null;
 //BA.debugLineNum = 3246;BA.debugLine="Sub Anima_DesplazaScreenshot";
 //BA.debugLineNum = 3247;BA.debugLine="Dim imgScreenShot As ImageView";
_imgscreenshot = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 3253;BA.debugLine="imgScreenShot.Initialize(\"\")";
_imgscreenshot.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 3254;BA.debugLine="imgScreenShot.Gravity = Gravity.FILL";
_imgscreenshot.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.FILL);
 //BA.debugLineNum = 3255;BA.debugLine="Activity.AddView(imgScreenShot, 0,0,Activity.Widt";
mostCurrent._activity.AddView((android.view.View)(_imgscreenshot.getObject()),(int) (0),(int) (0),mostCurrent._activity.getWidth(),mostCurrent._activity.getHeight());
 //BA.debugLineNum = 3256;BA.debugLine="imgScreenShot.SetBackgroundImage(LoadBitmap(g_Dir";
_imgscreenshot.SetBackgroundImage((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(_g_dirgrabable,"SSAnimar.png").getObject()));
 //BA.debugLineNum = 3259;BA.debugLine="Activity.invalidate";
mostCurrent._activity.Invalidate();
 //BA.debugLineNum = 3260;BA.debugLine="DoEvents";
anywheresoftware.b4a.keywords.Common.DoEvents();
 //BA.debugLineNum = 3261;BA.debugLine="imgScreenShot.bringtofront";
_imgscreenshot.BringToFront();
 //BA.debugLineNum = 3263;BA.debugLine="Anima_DesplazaImagen (imgScreenShot, 0,0, Activit";
_anima_desplazaimagen(_imgscreenshot,(int) (0),(int) (0),mostCurrent._activity.getWidth(),(int) (0),(int) (500),mostCurrent._vvvvvvvvvvvvvvvv0.INTERPOLATOR_LINEAR);
 //BA.debugLineNum = 3266;BA.debugLine="imgScreenShot.removeview";
_imgscreenshot.RemoveView();
 //BA.debugLineNum = 3267;BA.debugLine="End Sub";
return "";
}
public static String  _animacion_sumarneuronas(int _ineuronas) throws Exception{
 //BA.debugLineNum = 3743;BA.debugLine="Sub Animacion_SumarNeuronas(iNeuronas As Int)";
 //BA.debugLineNum = 3746;BA.debugLine="Dim imgNeuronas As ImageView";
mostCurrent._imgneuronas = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 3747;BA.debugLine="imgNeuronas.Initialize(\"\")";
mostCurrent._imgneuronas.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 3748;BA.debugLine="pnlInvisible.RemoveAllViews";
mostCurrent._pnlinvisible.RemoveAllViews();
 //BA.debugLineNum = 3749;BA.debugLine="pnlInvisible.Color = Colors.Transparent";
mostCurrent._pnlinvisible.setColor(anywheresoftware.b4a.keywords.Common.Colors.Transparent);
 //BA.debugLineNum = 3750;BA.debugLine="pnlInvisible.AddView(imgNeuronas, 0,0,Activity.Wi";
mostCurrent._pnlinvisible.AddView((android.view.View)(mostCurrent._imgneuronas.getObject()),(int) (0),(int) (0),(int) (mostCurrent._activity.getWidth()/(double)3),(int) (mostCurrent._activity.getWidth()/(double)3));
 //BA.debugLineNum = 3751;BA.debugLine="Publicos.CentrarControlEnPanel(pnlInvisible, imgN";
mostCurrent._vvvvvvvvvvvvvvv7._vv0(mostCurrent.activityBA,mostCurrent._pnlinvisible,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._imgneuronas.getObject())),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3752;BA.debugLine="imgNeuronas.SetBackgroundImage(bitmNeurona)";
mostCurrent._imgneuronas.SetBackgroundImage((android.graphics.Bitmap)(_vvvvvvv1.getObject()));
 //BA.debugLineNum = 3754;BA.debugLine="Dim lblNeuronas As Label";
mostCurrent._lblneuronas = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 3755;BA.debugLine="lblNeuronas.Initialize(\"\")";
mostCurrent._lblneuronas.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 3756;BA.debugLine="pnlInvisible.AddView(lblNeuronas, 0,imgNeuronas.T";
mostCurrent._pnlinvisible.AddView((android.view.View)(mostCurrent._lblneuronas.getObject()),(int) (0),(int) (mostCurrent._imgneuronas.getTop()+mostCurrent._imgneuronas.getHeight()*1.1),(int) (mostCurrent._activity.getWidth()/(double)2),(int) (mostCurrent._imgneuronas.getHeight()/(double)3));
 //BA.debugLineNum = 3757;BA.debugLine="lblNeuronas.Text = \"SUMAS \" & iNeuronas & \" NEURO";
mostCurrent._lblneuronas.setText((Object)("SUMAS "+BA.NumberToString(_ineuronas)+" NEURONAS"));
 //BA.debugLineNum = 3758;BA.debugLine="Publicos.SetLabelTextSize(lblNeuronas, lblNeurona";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv4(mostCurrent.activityBA,mostCurrent._lblneuronas,mostCurrent._lblneuronas.getText(),(float) (40),(float) (6),(int) (100));
 //BA.debugLineNum = 3759;BA.debugLine="lblNeuronas.color = Colors.White";
mostCurrent._lblneuronas.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 3760;BA.debugLine="lblNeuronas.TextColor = lblv2Def.textcolor";
mostCurrent._lblneuronas.setTextColor(mostCurrent._lblv2def.getTextColor());
 //BA.debugLineNum = 3761;BA.debugLine="Publicos.CentrarControlEnPanel(pnlInvisible, lblN";
mostCurrent._vvvvvvvvvvvvvvv7._vv0(mostCurrent.activityBA,mostCurrent._pnlinvisible,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._lblneuronas.getObject())),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3763;BA.debugLine="pnlInvisible.Visible=True";
mostCurrent._pnlinvisible.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3764;BA.debugLine="gi_AnimacionEnCurso = 1";
_gi_animacionencurso = (int) (1);
 //BA.debugLineNum = 3765;BA.debugLine="V2_AnimaRotateImageview(imgNeuronas, 0, 720, 3000";
_v2_animarotateimageview(mostCurrent._imgneuronas,(int) (0),(int) (720),(int) (3000));
 //BA.debugLineNum = 3769;BA.debugLine="End Sub";
return "";
}
public static String  _animplus_animationend() throws Exception{
 //BA.debugLineNum = 3771;BA.debugLine="Sub AnimPlus_AnimationEnd";
 //BA.debugLineNum = 3773;BA.debugLine="Select (True)";
switch (BA.switchObjectToInt((anywheresoftware.b4a.keywords.Common.True),_gi_animacionencurso==_vvvvvvvv3.ComproNeuronas,_gi_animacionencurso==_vvvvvvvv3.Adivino)) {
case 0: {
 //BA.debugLineNum = 3776;BA.debugLine="Panel21.Visible = False";
mostCurrent._panel21.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3777;BA.debugLine="pnlInvisible.Visible = False";
mostCurrent._pnlinvisible.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3778;BA.debugLine="gi_AnimacionEnCurso=-1";
_gi_animacionencurso = (int) (-1);
 //BA.debugLineNum = 3780;BA.debugLine="V2_AnimaRotateImageview	(imgNeuronas, 0, 2880, 2";
_v2_animarotateimageview(mostCurrent._imgneuronas,(int) (0),(int) (2880),(int) (2000));
 break; }
case 1: {
 //BA.debugLineNum = 3784;BA.debugLine="Panel21.Visible = False";
mostCurrent._panel21.setVisible(anywheresoftware.b4a.keywords.Common.False);
 break; }
}
;
 //BA.debugLineNum = 3787;BA.debugLine="End Sub";
return "";
}
public static String  _aviso_muestra() throws Exception{
int _icolorletra = 0;
anywheresoftware.b4a.objects.PanelWrapper _pnlrecuadro = null;
anywheresoftware.b4a.objects.PanelWrapper _pnlproductos2 = null;
anywheresoftware.b4a.objects.LabelWrapper _lbltitulop = null;
anywheresoftware.b4a.objects.LabelWrapper _lblcancelarp = null;
anywheresoftware.b4a.objects.LabelWrapper _lblmensaje = null;
int _ileft = 0;
int _itop = 0;
int _iwidth = 0;
String _iheight = "";
 //BA.debugLineNum = 3965;BA.debugLine="Sub Aviso_Muestra";
 //BA.debugLineNum = 3966;BA.debugLine="bAnulaInsterstitialInicial=True";
_vvvvvv7 = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 3968;BA.debugLine="Dim iColorLetra As Int";
_icolorletra = 0;
 //BA.debugLineNum = 3969;BA.debugLine="iColorLetra = Get_aTextColor(gi_CombinacionColores";
_icolorletra = _get_atextcolor(_gi_combinacioncolores,(int) (1));
 //BA.debugLineNum = 3971;BA.debugLine="iPantallaActiva = xConfiguraPantalla.MuestraAviso";
_vvvvvvvv4 = _vvvvvvvv3.MuestraAviso;
 //BA.debugLineNum = 3973;BA.debugLine="pnlInvisible.color = Colors.transparent";
mostCurrent._pnlinvisible.setColor(anywheresoftware.b4a.keywords.Common.Colors.Transparent);
 //BA.debugLineNum = 3975;BA.debugLine="pnlInvisible.BringToFront";
mostCurrent._pnlinvisible.BringToFront();
 //BA.debugLineNum = 3978;BA.debugLine="Dim pnlRecuadro As Panel";
_pnlrecuadro = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 3979;BA.debugLine="pnlRecuadro.Initialize(\"\")";
_pnlrecuadro.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 3980;BA.debugLine="pnlRecuadro.Color = Colors.LightGray";
_pnlrecuadro.setColor(anywheresoftware.b4a.keywords.Common.Colors.LightGray);
 //BA.debugLineNum = 3982;BA.debugLine="pnlInvisible.AddView(pnlRecuadro, 0,0,0,0)";
mostCurrent._pnlinvisible.AddView((android.view.View)(_pnlrecuadro.getObject()),(int) (0),(int) (0),(int) (0),(int) (0));
 //BA.debugLineNum = 3983;BA.debugLine="pnlRecuadro.Height = pnlInvisible.Height*0.8 +4d";
_pnlrecuadro.setHeight((int) (mostCurrent._pnlinvisible.getHeight()*0.8+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (4))));
 //BA.debugLineNum = 3984;BA.debugLine="pnlRecuadro.Width = pnlInvisible.Width *0.8 + 4d";
_pnlrecuadro.setWidth((int) (mostCurrent._pnlinvisible.getWidth()*0.8+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (4))));
 //BA.debugLineNum = 3985;BA.debugLine="Publicos.CentrarControlEnPanel(pnlInvisible, pnl";
mostCurrent._vvvvvvvvvvvvvvv7._vv0(mostCurrent.activityBA,mostCurrent._pnlinvisible,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(_pnlrecuadro.getObject())),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3988;BA.debugLine="Dim pnlProductos2 As Panel";
_pnlproductos2 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 3989;BA.debugLine="pnlProductos2.Initialize(\"\")";
_pnlproductos2.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 3991;BA.debugLine="pnlRecuadro.AddView(pnlProductos2, 0,0,0,0)";
_pnlrecuadro.AddView((android.view.View)(_pnlproductos2.getObject()),(int) (0),(int) (0),(int) (0),(int) (0));
 //BA.debugLineNum = 3992;BA.debugLine="pnlProductos2.Height = pnlRecuadro.Height-4dip";
_pnlproductos2.setHeight((int) (_pnlrecuadro.getHeight()-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (4))));
 //BA.debugLineNum = 3993;BA.debugLine="pnlProductos2.Width = pnlRecuadro.Width-4dip";
_pnlproductos2.setWidth((int) (_pnlrecuadro.getWidth()-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (4))));
 //BA.debugLineNum = 3994;BA.debugLine="Publicos.CentrarControlEnPanel(pnlRecuadro, pnlP";
mostCurrent._vvvvvvvvvvvvvvv7._vv0(mostCurrent.activityBA,_pnlrecuadro,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(_pnlproductos2.getObject())),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3998;BA.debugLine="Dim lblTituloP As Label";
_lbltitulop = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 3999;BA.debugLine="lblTituloP.Initialize(\"\")";
_lbltitulop.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 4000;BA.debugLine="pnlProductos2.AddView(lblTituloP , 0,0,pnlProduct";
_pnlproductos2.AddView((android.view.View)(_lbltitulop.getObject()),(int) (0),(int) (0),_pnlproductos2.getWidth(),(int) (_pnlproductos2.getHeight()*0.15));
 //BA.debugLineNum = 4001;BA.debugLine="lblTituloP.Typeface = tFontOpenSansSemiBold";
_lbltitulop.setTypeface((android.graphics.Typeface)(_vvvvvvvvvv4.getObject()));
 //BA.debugLineNum = 4002;BA.debugLine="lblTituloP.Text = \"MATETE DICE\"";
_lbltitulop.setText((Object)("MATETE DICE"));
 //BA.debugLineNum = 4003;BA.debugLine="Publicos.SetLabelTextSize(lblTituloP , lblTituloP";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv4(mostCurrent.activityBA,_lbltitulop,_lbltitulop.getText(),(float) (40),(float) (6),(int) (70));
 //BA.debugLineNum = 4004;BA.debugLine="lblTituloP.Color = gt_Color.ColorMedio";
_lbltitulop.setColor(mostCurrent._gt_color.ColorMedio);
 //BA.debugLineNum = 4005;BA.debugLine="lblTituloP.TextColor = Colors.white";
_lbltitulop.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 4006;BA.debugLine="lblTituloP.Gravity = Gravity.CENTER";
_lbltitulop.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 4009;BA.debugLine="Dim lblCancelarP As Label";
_lblcancelarp = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 4011;BA.debugLine="lbl61.Tag = xConfiguraPantalla.Producto";
mostCurrent._lbl61.setTag((Object)(_vvvvvvvv3.Producto));
 //BA.debugLineNum = 4012;BA.debugLine="lblCancelarP.initialize(\"lbl61\")";
_lblcancelarp.Initialize(mostCurrent.activityBA,"lbl61");
 //BA.debugLineNum = 4013;BA.debugLine="lblCancelarP.Color = Colors.white";
_lblcancelarp.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 4014;BA.debugLine="lblCancelarP.TextColor = gt_Color.ColorMedio";
_lblcancelarp.setTextColor(mostCurrent._gt_color.ColorMedio);
 //BA.debugLineNum = 4015;BA.debugLine="pnlProductos2.AddView(lblCancelarP, pnlProductos2";
_pnlproductos2.AddView((android.view.View)(_lblcancelarp.getObject()),(int) (_pnlproductos2.getWidth()*0.05),(int) (0),(int) (_pnlproductos2.getWidth()*0.9),mostCurrent._lbl61.getHeight());
 //BA.debugLineNum = 4016;BA.debugLine="lblCancelarP.Top = pnlProductos2.Height-lblCancel";
_lblcancelarp.setTop((int) (_pnlproductos2.getHeight()-_lblcancelarp.getHeight()-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (3))));
 //BA.debugLineNum = 4017;BA.debugLine="lblCancelarP.Typeface = tFontOpenSansLight";
_lblcancelarp.setTypeface((android.graphics.Typeface)(_vvvvvvvvvv5.getObject()));
 //BA.debugLineNum = 4018;BA.debugLine="lblCancelarP.Text = \"CONTINUAR\"";
_lblcancelarp.setText((Object)("CONTINUAR"));
 //BA.debugLineNum = 4019;BA.debugLine="Publicos.SetLabelTextSize(lblCancelarP, lblCancel";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv4(mostCurrent.activityBA,_lblcancelarp,_lblcancelarp.getText(),(float) (40),(float) (6),(int) (100));
 //BA.debugLineNum = 4020;BA.debugLine="lblCancelarP.Gravity = Gravity.CENTER";
_lblcancelarp.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 4022;BA.debugLine="pnlProductos2.Color = Colors.white";
_pnlproductos2.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 4024;BA.debugLine="Dim lblMensaje As Label";
_lblmensaje = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 4025;BA.debugLine="lblMensaje.Initialize(\"\")";
_lblmensaje.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 4026;BA.debugLine="lblMensaje.Typeface = tFontOpenSansLight";
_lblmensaje.setTypeface((android.graphics.Typeface)(_vvvvvvvvvv5.getObject()));
 //BA.debugLineNum = 4027;BA.debugLine="lblMensaje.TextColor = iColorLetra";
_lblmensaje.setTextColor(_icolorletra);
 //BA.debugLineNum = 4028;BA.debugLine="lblMensaje.Gravity = Gravity.CENTER";
_lblmensaje.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 4029;BA.debugLine="pnlProductos2.addview(lblMensaje , 0, pnlProducto";
_pnlproductos2.AddView((android.view.View)(_lblmensaje.getObject()),(int) (0),(int) (_pnlproductos2.getHeight()*0.15),(int) (_pnlproductos2.getWidth()*0.85),(int) (_pnlproductos2.getHeight()*0.7));
 //BA.debugLineNum = 4030;BA.debugLine="Publicos.CentrarControlEnPanel(pnlProductos2,lblM";
mostCurrent._vvvvvvvvvvvvvvv7._vv0(mostCurrent.activityBA,_pnlproductos2,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(_lblmensaje.getObject())),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 4031;BA.debugLine="lblMensaje.Text = xtNivel.MensajeInicio";
_lblmensaje.setText((Object)(_vvvvvvvvvvvvvvvvvv7.MensajeInicio));
 //BA.debugLineNum = 4032;BA.debugLine="Publicos.SetLabelTextSize(lblMensaje, lblMensaje.";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv4(mostCurrent.activityBA,_lblmensaje,_lblmensaje.getText(),(float) (30),(float) (6),(int) (100));
 //BA.debugLineNum = 4034;BA.debugLine="pnlInvisible.Visible = True ' que incongruencia";
mostCurrent._pnlinvisible.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 4035;BA.debugLine="pnlInvisible.SetColorAnimated(1000, Colors.White,";
mostCurrent._pnlinvisible.SetColorAnimated((int) (1000),anywheresoftware.b4a.keywords.Common.Colors.White,anywheresoftware.b4a.keywords.Common.Colors.Transparent);
 //BA.debugLineNum = 4038;BA.debugLine="pnlProductos2.Visible=True";
_pnlproductos2.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 4039;BA.debugLine="Dim ileft As Int = pnlRecuadro.Left, iTop As Int =";
_ileft = _pnlrecuadro.getLeft();
_itop = _pnlrecuadro.getTop();
_iwidth = _pnlrecuadro.getWidth();
_iheight = BA.NumberToString(_pnlrecuadro.getHeight());
 //BA.debugLineNum = 4040;BA.debugLine="pnlRecuadro.SetLayoutAnimated(500, Activity.Width/";
_pnlrecuadro.SetLayoutAnimated((int) (500),(int) (mostCurrent._activity.getWidth()/(double)2),(int) (0),(int) (0),(int) (0));
 //BA.debugLineNum = 4041;BA.debugLine="pnlRecuadro.SetLayoutAnimated(1000, ileft, iTop, i";
_pnlrecuadro.SetLayoutAnimated((int) (1000),_ileft,_itop,_iwidth,(int)(Double.parseDouble(_iheight)));
 //BA.debugLineNum = 4043;BA.debugLine="pnlHistoria.Visible = False";
mostCurrent._pnlhistoria.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 4045;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvvvvv0() throws Exception{
int _j = 0;
 //BA.debugLineNum = 910;BA.debugLine="Sub BajarLetras";
 //BA.debugLineNum = 912;BA.debugLine="For j=0 To lblArrayPalabra.Length-1";
{
final int step1 = 1;
final int limit1 = (int) (mostCurrent._vvvvvvvvvvvvvvvvvvv1.length-1);
for (_j = (int) (0) ; (step1 > 0 && _j <= limit1) || (step1 < 0 && _j >= limit1); _j = ((int)(0 + _j + step1)) ) {
 //BA.debugLineNum = 913;BA.debugLine="lblPalabraClick (lblArrayPalabra(j))";
_vvvvvvvvvvvvvvvvvvv2(mostCurrent._vvvvvvvvvvvvvvvvvvv1[_j]);
 }
};
 //BA.debugLineNum = 918;BA.debugLine="End Sub";
return "";
}
public static String  _baseremota_grabaravance(int _idpalabra) throws Exception{
anywheresoftware.b4a.sql.SQL.CursorWrapper _cursor1 = null;
String _ssql = "";
String _sinsert = "";
String _squerylocal = "";
 //BA.debugLineNum = 4227;BA.debugLine="Sub BaseRemota_GrabarAvance(idPalabra As Int)";
 //BA.debugLineNum = 4228;BA.debugLine="Dim Cursor1 As Cursor, Ssql As String, sInsert As";
_cursor1 = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
_ssql = "";
_sinsert = "";
_squerylocal = "";
 //BA.debugLineNum = 4229;BA.debugLine="Try";
try { //BA.debugLineNum = 4231;BA.debugLine="Ssql =\"Select inicio, fin, adivinada, salteada f";
_ssql = "Select inicio, fin, adivinada, salteada from avance where idpalabra = "+BA.NumberToString(_idpalabra);
 //BA.debugLineNum = 4232;BA.debugLine="Cursor1 = g_sqlBaseLocalUsuario.ExecQuery(Ssql)";
_cursor1.setObject((android.database.Cursor)(_g_sqlbaselocalusuario.ExecQuery(_ssql)));
 //BA.debugLineNum = 4235;BA.debugLine="If Cursor1.RowCount >0 Then";
if (_cursor1.getRowCount()>0) { 
 //BA.debugLineNum = 4236;BA.debugLine="Cursor1.Position = 0";
_cursor1.setPosition((int) (0));
 //BA.debugLineNum = 4238;BA.debugLine="sInsert = \"call Avance_IU ('\" & Publicos.GetUsu";
_sinsert = "call Avance_IU ('"+mostCurrent._vvvvvvvvvvvvvvv7._vvv4(mostCurrent.activityBA)+"','"+mostCurrent._vvvvvvvvvvvvvvv7._vvv3(mostCurrent.activityBA)+"',"+BA.NumberToString(_idpalabra)+",'"+_cursor1.GetString2((int) (0))+"','"+_cursor1.GetString2((int) (1))+"',"+BA.NumberToString(_cursor1.GetInt2((int) (2)))+","+BA.NumberToString(_cursor1.GetInt2((int) (3)))+",'')";
 //BA.debugLineNum = 4243;BA.debugLine="sQueryLocal = \"Update Avance set registroenviad";
_squerylocal = "Update Avance set registroenviado = 1 where idPalabra = "+BA.NumberToString(_idpalabra);
 //BA.debugLineNum = 4244;BA.debugLine="Remoto_EjecutarNonQuery (sInsert, sQueryLocal,";
_remoto_ejecutarnonquery(_sinsert,_squerylocal,_vvvvvvvv5.Avance);
 };
 //BA.debugLineNum = 4246;BA.debugLine="Cursor1.Close";
_cursor1.Close();
 } 
       catch (Exception e13) {
			processBA.setLastException(e13); //BA.debugLineNum = 4249;BA.debugLine="ManejaErrorJugar(\"MuestraPuntos\",\"MuestraPuntos\"";
_vvvvvvvvvvvvvvvvvv3("MuestraPuntos","MuestraPuntos");
 };
 //BA.debugLineNum = 4256;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvvvvvv3(boolean _bbloquear,String _stipobloqueo,boolean _btransparente) throws Exception{
 //BA.debugLineNum = 762;BA.debugLine="Sub bloquearpantalla (bBloquear As Boolean, sTipoB";
 //BA.debugLineNum = 763;BA.debugLine="lblBloqueo.BringToFront";
mostCurrent._vvvvvvvvvvvvvvvvvvv4.BringToFront();
 //BA.debugLineNum = 764;BA.debugLine="lblBloqueo.Visible = bBloquear";
mostCurrent._vvvvvvvvvvvvvvvvvvv4.setVisible(_bbloquear);
 //BA.debugLineNum = 765;BA.debugLine="lblBloqueo.Tag = sTipoBloqueo";
mostCurrent._vvvvvvvvvvvvvvvvvvv4.setTag((Object)(_stipobloqueo));
 //BA.debugLineNum = 766;BA.debugLine="If bTransparente Then";
if (_btransparente) { 
 //BA.debugLineNum = 767;BA.debugLine="lblBloqueo.Color = Colors.Transparent";
mostCurrent._vvvvvvvvvvvvvvvvvvv4.setColor(anywheresoftware.b4a.keywords.Common.Colors.Transparent);
 };
 //BA.debugLineNum = 769;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvvvvvv5() throws Exception{
anywheresoftware.b4a.objects.LabelWrapper _l = null;
 //BA.debugLineNum = 1007;BA.debugLine="Sub BorraLabels";
 //BA.debugLineNum = 1009;BA.debugLine="For Each L As Label In lblArrayPalabra";
final anywheresoftware.b4a.objects.LabelWrapper[] group1 = mostCurrent._vvvvvvvvvvvvvvvvvvv1;
final int groupLen1 = group1.length;
for (int index1 = 0;index1 < groupLen1 ;index1++){
_l = group1[index1];
 //BA.debugLineNum = 1010;BA.debugLine="L.RemoveView";
_l.RemoveView();
 }
;
 //BA.debugLineNum = 1013;BA.debugLine="End Sub";
return "";
}
public static String  _cargaimagenes_volatiles() throws Exception{
 //BA.debugLineNum = 4911;BA.debugLine="Sub CargaImagenes_Volatiles";
 //BA.debugLineNum = 4913;BA.debugLine="imgMenu.SetBackgroundImage(gt_Color.bitmMenu)";
mostCurrent._imgmenu.SetBackgroundImage((android.graphics.Bitmap)(mostCurrent._gt_color.bitmMenu.getObject()));
 //BA.debugLineNum = 4914;BA.debugLine="imgAvance.SetBackgroundImage(gt_Color.bitmFlag)";
mostCurrent._imgavance.SetBackgroundImage((android.graphics.Bitmap)(mostCurrent._gt_color.bitmFlag.getObject()));
 //BA.debugLineNum = 4916;BA.debugLine="imgPedirLetra.SetBackgroundImage(gt_Color.bitmPed";
mostCurrent._imgpedirletra.SetBackgroundImage((android.graphics.Bitmap)(mostCurrent._gt_color.bitmPedir.getObject()));
 //BA.debugLineNum = 4917;BA.debugLine="imgSaltarPalabra.SetBackgroundImage(gt_Color.bitm";
mostCurrent._imgsaltarpalabra.SetBackgroundImage((android.graphics.Bitmap)(mostCurrent._gt_color.bitmSaltarPalabra.getObject()));
 //BA.debugLineNum = 4921;BA.debugLine="lblPedirLetraCosto.setbackgroundimage(gt_Color.bi";
mostCurrent._lblpedirletracosto.SetBackgroundImage((android.graphics.Bitmap)(mostCurrent._gt_color.bitmCostos.getObject()));
 //BA.debugLineNum = 4922;BA.debugLine="lblSaltarPalabraCosto.SetBackgroundImage(gt_Color";
mostCurrent._lblsaltarpalabracosto.SetBackgroundImage((android.graphics.Bitmap)(mostCurrent._gt_color.bitmCostos.getObject()));
 //BA.debugLineNum = 4925;BA.debugLine="imgCompartir.SetBackgroundImage(gt_Color.bitmComp";
mostCurrent._imgcompartir.SetBackgroundImage((android.graphics.Bitmap)(mostCurrent._gt_color.bitmCompartir.getObject()));
 //BA.debugLineNum = 4926;BA.debugLine="imgBajarLetras.SetBackgroundImage(gt_Color.bitmBa";
mostCurrent._imgbajarletras.SetBackgroundImage((android.graphics.Bitmap)(mostCurrent._gt_color.bitmBajarLetras.getObject()));
 //BA.debugLineNum = 4928;BA.debugLine="End Sub";
return "";
}
public static boolean  _vvvvvvvvvvvvvvvvvvv6(int _idnivel,int _idpalabra) throws Exception{
String _ssql = "";
boolean _bret = false;
anywheresoftware.b4a.sql.SQL.CursorWrapper _cursor1 = null;
 //BA.debugLineNum = 595;BA.debugLine="Sub CargarNivel (idNivel As Int, idPalabra As Int)";
 //BA.debugLineNum = 597;BA.debugLine="Dim sSql As String, bRet As Boolean";
_ssql = "";
_bret = false;
 //BA.debugLineNum = 598;BA.debugLine="Dim Cursor1 As Cursor";
_cursor1 = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
 //BA.debugLineNum = 599;BA.debugLine="Try";
try { //BA.debugLineNum = 601;BA.debugLine="sSql =\"select idNivel, Nombre, ifnull(imagenfond";
_ssql = "select idNivel, Nombre, ifnull(imagenfondo,''), ifnull(costosaltar,15), ifnull(costoletra,1), ifnull(costoayuda,1), ifnull(mensajeinicio,''), ifnull(mensajefinal,''),";
 //BA.debugLineNum = 602;BA.debugLine="sSql= sSql & \"ifnull(imageninicio,''), ifnull(im";
_ssql = _ssql+"ifnull(imageninicio,''), ifnull(imagenfinal,'') from Niveles where idnivel = "+BA.NumberToString(_idnivel);
 //BA.debugLineNum = 603;BA.debugLine="Cursor1 = g_sqlBaseLocalJuego.ExecQuery(sSql)";
_cursor1.setObject((android.database.Cursor)(_g_sqlbaselocaljuego.ExecQuery(_ssql)));
 //BA.debugLineNum = 605;BA.debugLine="If Cursor1.RowCount >0 Then";
if (_cursor1.getRowCount()>0) { 
 //BA.debugLineNum = 606;BA.debugLine="bRet = True";
_bret = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 607;BA.debugLine="Cursor1.Position = 0";
_cursor1.setPosition((int) (0));
 //BA.debugLineNum = 609;BA.debugLine="xtNivel.idNivel = Cursor1.getint2(0)";
_vvvvvvvvvvvvvvvvvv7.idNivel = _cursor1.GetInt2((int) (0));
 //BA.debugLineNum = 610;BA.debugLine="xtNivel.Nombre = Cursor1.GetString2(1)";
_vvvvvvvvvvvvvvvvvv7.Nombre = _cursor1.GetString2((int) (1));
 //BA.debugLineNum = 611;BA.debugLine="xtNivel.ImagenFondo= Cursor1.GetString2(2)";
_vvvvvvvvvvvvvvvvvv7.ImagenFondo = _cursor1.GetString2((int) (2));
 //BA.debugLineNum = 612;BA.debugLine="xtNivel.CostoSaltar = Cursor1.getint2(3)";
_vvvvvvvvvvvvvvvvvv7.CostoSaltar = _cursor1.GetInt2((int) (3));
 //BA.debugLineNum = 613;BA.debugLine="xtNivel.CostoLetra = Cursor1.getint2(4)";
_vvvvvvvvvvvvvvvvvv7.CostoLetra = _cursor1.GetInt2((int) (4));
 //BA.debugLineNum = 614;BA.debugLine="xtNivel.CostoAyuda = Cursor1.getint2(5)";
_vvvvvvvvvvvvvvvvvv7.CostoAyuda = _cursor1.GetInt2((int) (5));
 //BA.debugLineNum = 615;BA.debugLine="xtNivel.MensajeInicio= Cursor1.GetString2(6)";
_vvvvvvvvvvvvvvvvvv7.MensajeInicio = _cursor1.GetString2((int) (6));
 //BA.debugLineNum = 616;BA.debugLine="xtNivel.MensajeFinal= Cursor1.GetString2(7)";
_vvvvvvvvvvvvvvvvvv7.MensajeFinal = _cursor1.GetString2((int) (7));
 //BA.debugLineNum = 617;BA.debugLine="xtNivel.IdPrimeraPalabra = Get_NivelPrimeraPala";
_vvvvvvvvvvvvvvvvvv7.IdPrimeraPalabra = _get_nivelprimerapalabra(_vvvvvvvvvvvvvvvvvv7.idNivel);
 //BA.debugLineNum = 618;BA.debugLine="xtNivel.IdUltimaPalabra = Get_NivelUltimaPalabr";
_vvvvvvvvvvvvvvvvvv7.IdUltimaPalabra = _get_nivelultimapalabra(_vvvvvvvvvvvvvvvvvv7.idNivel);
 };
 //BA.debugLineNum = 621;BA.debugLine="Cursor1.close";
_cursor1.Close();
 } 
       catch (Exception e23) {
			processBA.setLastException(e23); //BA.debugLineNum = 623;BA.debugLine="ManejaErrorJugar(\"CargarNivel\",LastException.Mes";
_vvvvvvvvvvvvvvvvvv3("CargarNivel",anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage());
 };
 //BA.debugLineNum = 626;BA.debugLine="If Publicos.Get_EsPremium(g_sqlBaseLocalUsuario)";
if (mostCurrent._vvvvvvvvvvvvvvv7._get_espremium(mostCurrent.activityBA,_g_sqlbaselocalusuario)) { 
 //BA.debugLineNum = 627;BA.debugLine="lblV2Nivel.Text = \"MATETE DIVERGENTE PREMIUM\" &";
mostCurrent._lblv2nivel.setText((Object)("MATETE DIVERGENTE PREMIUM"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"NIVEL "+_vvvvvvvvvvvvvvvvvv7.Nombre.toUpperCase()));
 }else {
 //BA.debugLineNum = 629;BA.debugLine="lblV2Nivel.Text = \"MATETE DIVERGENTE\" & Chr(10)";
mostCurrent._lblv2nivel.setText((Object)("MATETE DIVERGENTE"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"NIVEL "+_vvvvvvvvvvvvvvvvvv7.Nombre.toUpperCase()));
 };
 //BA.debugLineNum = 634;BA.debugLine="If xtNivel.MensajeInicio.Trim <>\"\" Then";
if ((_vvvvvvvvvvvvvvvvvv7.MensajeInicio.trim()).equals("") == false) { 
 //BA.debugLineNum = 635;BA.debugLine="If xtNivel.IdPrimeraPalabra = idPalabra And xtNi";
if (_vvvvvvvvvvvvvvvvvv7.IdPrimeraPalabra==_idpalabra && _vvvvvvvvvvvvvvvvvv7.bMensajeInicioMostrado==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 636;BA.debugLine="V2_PantallaConfigura (xConfiguraPantalla.Muestr";
_v2_pantallaconfigura(_vvvvvvvv3.MuestraAviso,(int) (0));
 //BA.debugLineNum = 638;BA.debugLine="xtNivel.bMensajeInicioMostrado = True";
_vvvvvvvvvvvvvvvvvv7.bMensajeInicioMostrado = anywheresoftware.b4a.keywords.Common.True;
 };
 };
 //BA.debugLineNum = 641;BA.debugLine="Return bRet";
if (true) return _bret;
 //BA.debugLineNum = 642;BA.debugLine="End Sub";
return false;
}
public static boolean  _vvvvvvvvvvvvvvvvvvv7(int _pidpalabra,boolean _brejuego,boolean _bsalteada) throws Exception{
int _iidpalabra = 0;
String _ssql = "";
anywheresoftware.b4a.sql.SQL.CursorWrapper _cursor1 = null;
String _sfin = "";
String _sfecha = "";
int _iinsertaravance = 0;
boolean _bret = false;
int _iproximapalabra = 0;
 //BA.debugLineNum = 493;BA.debugLine="Sub CargarPalabra(pIDPalabra As Int, bRejuego As B";
 //BA.debugLineNum = 496;BA.debugLine="Dim iIDPalabra As Int, sSql As String";
_iidpalabra = 0;
_ssql = "";
 //BA.debugLineNum = 497;BA.debugLine="Dim Cursor1 As Cursor, sFin As String, sFecha As";
_cursor1 = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
_sfin = "";
_sfecha = "";
 //BA.debugLineNum = 498;BA.debugLine="Dim iInsertarAvance As Int = 1";
_iinsertaravance = (int) (1);
 //BA.debugLineNum = 499;BA.debugLine="Dim bRet As Boolean = False, iProximaPalabra As I";
_bret = anywheresoftware.b4a.keywords.Common.False;
_iproximapalabra = 0;
 //BA.debugLineNum = 500;BA.debugLine="Try";
try { //BA.debugLineNum = 502;BA.debugLine="Sonido(SONIDO_NUEVAPALABRA)";
_vvvvvvvvvvvvvvvvvv6(_sonido_nuevapalabra);
 //BA.debugLineNum = 504;BA.debugLine="If bRejuego Then";
if (_brejuego) { 
 //BA.debugLineNum = 505;BA.debugLine="iIDPalabra = pIDPalabra";
_iidpalabra = _pidpalabra;
 //BA.debugLineNum = 506;BA.debugLine="iInsertarAvance = 0";
_iinsertaravance = (int) (0);
 }else {
 //BA.debugLineNum = 509;BA.debugLine="sSql =\"select idPalabra, ifnull(fin,'') fin fro";
_ssql = "select idPalabra, ifnull(fin,'') fin from avance where idPalabra = (select max(idPalabra) from avance)";
 //BA.debugLineNum = 510;BA.debugLine="Cursor1 = g_sqlBaseLocalUsuario.ExecQuery(sSql)";
_cursor1.setObject((android.database.Cursor)(_g_sqlbaselocalusuario.ExecQuery(_ssql)));
 //BA.debugLineNum = 512;BA.debugLine="If Cursor1.RowCount >0 Then";
if (_cursor1.getRowCount()>0) { 
 //BA.debugLineNum = 513;BA.debugLine="bRet = True";
_bret = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 515;BA.debugLine="Cursor1.Position = 0";
_cursor1.setPosition((int) (0));
 //BA.debugLineNum = 516;BA.debugLine="sFin = \"\"& Cursor1.getstring2(1)'fin";
_sfin = ""+_cursor1.GetString2((int) (1));
 //BA.debugLineNum = 517;BA.debugLine="iIDPalabra= Cursor1.GetInt2(0)'idpalabra";
_iidpalabra = _cursor1.GetInt2((int) (0));
 //BA.debugLineNum = 519;BA.debugLine="If sFin = \"\"  Then ' si es una palabra que tod";
if ((_sfin).equals("")) { 
 //BA.debugLineNum = 520;BA.debugLine="iIDPalabra = iIDPalabra-1 '";
_iidpalabra = (int) (_iidpalabra-1);
 //BA.debugLineNum = 521;BA.debugLine="iInsertarAvance = 0";
_iinsertaravance = (int) (0);
 };
 }else {
 //BA.debugLineNum = 525;BA.debugLine="iIDPalabra = 0";
_iidpalabra = (int) (0);
 };
 //BA.debugLineNum = 527;BA.debugLine="Cursor1.Close";
_cursor1.Close();
 };
 //BA.debugLineNum = 530;BA.debugLine="If iInsertarAvance = 1 Then";
if (_iinsertaravance==1) { 
 //BA.debugLineNum = 531;BA.debugLine="sFecha = DateTime.Date(DateTime.now)";
_sfecha = anywheresoftware.b4a.keywords.Common.DateTime.Date(anywheresoftware.b4a.keywords.Common.DateTime.getNow());
 //BA.debugLineNum = 537;BA.debugLine="sSql = \"select min(idpalabra) from palabras whe";
_ssql = "select min(idpalabra) from palabras where idpalabra > "+BA.NumberToString(_iidpalabra);
 //BA.debugLineNum = 538;BA.debugLine="Cursor1 = g_sqlBaseLocalJuego.ExecQuery(sSql)";
_cursor1.setObject((android.database.Cursor)(_g_sqlbaselocaljuego.ExecQuery(_ssql)));
 //BA.debugLineNum = 539;BA.debugLine="If Cursor1.rowcount>0 Then";
if (_cursor1.getRowCount()>0) { 
 //BA.debugLineNum = 540;BA.debugLine="Cursor1.Position = 0";
_cursor1.setPosition((int) (0));
 //BA.debugLineNum = 541;BA.debugLine="iProximaPalabra= Cursor1.GetInt2(0)";
_iproximapalabra = _cursor1.GetInt2((int) (0));
 };
 //BA.debugLineNum = 543;BA.debugLine="Cursor1.close";
_cursor1.Close();
 //BA.debugLineNum = 545;BA.debugLine="sSql = \"Insert Into Avance (idPalabra, Inicio,";
_ssql = "Insert Into Avance (idPalabra, Inicio, adivinada, salteada) Values (";
 //BA.debugLineNum = 546;BA.debugLine="sSql = sSql & iProximaPalabra & \",'\" & sFecha &";
_ssql = _ssql+BA.NumberToString(_iproximapalabra)+",'"+_sfecha+"',0,0)";
 //BA.debugLineNum = 548;BA.debugLine="g_sqlBaseLocalUsuario.ExecNonQuery(sSql)";
_g_sqlbaselocalusuario.ExecNonQuery(_ssql);
 };
 //BA.debugLineNum = 551;BA.debugLine="If bRejuego = False Then";
if (_brejuego==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 553;BA.debugLine="sSql = \"select idPalabra, Palabra, Descripcion,";
_ssql = "select idPalabra, Palabra, Descripcion, Dificultad, IdNivel, TipoPalabra, Diccionario, PalabraDiccionario from palabras where idpalabra =";
 //BA.debugLineNum = 555;BA.debugLine="sSql = sSql & \"(Select Min(idpalabra) from pala";
_ssql = _ssql+"(Select Min(idpalabra) from palabras where idpalabra>"+BA.NumberToString(_iidpalabra)+")";
 }else {
 //BA.debugLineNum = 557;BA.debugLine="sSql = \"select idPalabra, Palabra, Descripcion,";
_ssql = "select idPalabra, Palabra, Descripcion, Dificultad, IdNivel, TipoPalabra, Diccionario, PalabraDiccionario from palabras where idpalabra ="+BA.NumberToString(_pidpalabra);
 };
 //BA.debugLineNum = 559;BA.debugLine="Cursor1 = g_sqlBaseLocalJuego.ExecQuery(sSql)";
_cursor1.setObject((android.database.Cursor)(_g_sqlbaselocaljuego.ExecQuery(_ssql)));
 //BA.debugLineNum = 561;BA.debugLine="If Cursor1.RowCount>0 Then";
if (_cursor1.getRowCount()>0) { 
 //BA.debugLineNum = 563;BA.debugLine="Cursor1.Position =0";
_cursor1.setPosition((int) (0));
 //BA.debugLineNum = 564;BA.debugLine="igIDPalabra= Cursor1.GetInt2(0)";
_vvvvvvvv1 = _cursor1.GetInt2((int) (0));
 //BA.debugLineNum = 565;BA.debugLine="sgPalabraDescripcion = Cursor1.GetString2(2)";
_vvvvvvvv2 = _cursor1.GetString2((int) (2));
 //BA.debugLineNum = 567;BA.debugLine="xtPalabra.idPalabra = Cursor1.GetInt2(0)";
_vvvvvvvvvvvvvvvvvv4.idPalabra = _cursor1.GetInt2((int) (0));
 //BA.debugLineNum = 568;BA.debugLine="xtPalabra.Palabra= Cursor1.Getstring2(1): xtPal";
_vvvvvvvvvvvvvvvvvv4.Palabra = _cursor1.GetString2((int) (1));
 //BA.debugLineNum = 568;BA.debugLine="xtPalabra.Palabra= Cursor1.Getstring2(1): xtPal";
_vvvvvvvvvvvvvvvvvv4.Palabra = _vvvvvvvvvvvvvvvvvv4.Palabra.toUpperCase();
 //BA.debugLineNum = 569;BA.debugLine="sgLetras = xtPalabra.Palabra";
_vvvvvvv0 = _vvvvvvvvvvvvvvvvvv4.Palabra;
 //BA.debugLineNum = 571;BA.debugLine="xtPalabra.Descripcion = Cursor1.Getstring2(2)";
_vvvvvvvvvvvvvvvvvv4.Descripcion = _cursor1.GetString2((int) (2));
 //BA.debugLineNum = 572;BA.debugLine="xtPalabra.Dificultad = Cursor1.Getstring2(3)";
_vvvvvvvvvvvvvvvvvv4.Dificultad = _cursor1.GetString2((int) (3));
 //BA.debugLineNum = 573;BA.debugLine="xtPalabra.idNivel = Cursor1.GetInt2(4)";
_vvvvvvvvvvvvvvvvvv4.idNivel = _cursor1.GetInt2((int) (4));
 //BA.debugLineNum = 574;BA.debugLine="xtPalabra.TipoPalabra= Cursor1.Getstring2(5)";
_vvvvvvvvvvvvvvvvvv4.TipoPalabra = _cursor1.GetString2((int) (5));
 //BA.debugLineNum = 575;BA.debugLine="xtPalabra.Diccionario = Cursor1.GetString2(6)";
_vvvvvvvvvvvvvvvvvv4.Diccionario = _cursor1.GetString2((int) (6));
 //BA.debugLineNum = 576;BA.debugLine="xtPalabra.PalabraDiccionario = Cursor1.GetStrin";
_vvvvvvvvvvvvvvvvvv4.PalabraDiccionario = _cursor1.GetString2((int) (7));
 //BA.debugLineNum = 577;BA.debugLine="xtPalabra.bRejugada = bRejuego";
_vvvvvvvvvvvvvvvvvv4.bRejugada = _brejuego;
 //BA.debugLineNum = 578;BA.debugLine="xtPalabra.bSalteada = bSalteada";
_vvvvvvvvvvvvvvvvvv4.bSalteada = _bsalteada;
 }else {
 //BA.debugLineNum = 581;BA.debugLine="ManejaErrorJugar(\"Cursor Vacio\", \"Cursor\")";
_vvvvvvvvvvvvvvvvvv3("Cursor Vacio","Cursor");
 };
 //BA.debugLineNum = 583;BA.debugLine="Cursor1.close";
_cursor1.Close();
 //BA.debugLineNum = 586;BA.debugLine="Palabra_CargaLetras (xtPalabra.Palabra, igIDPala";
_palabra_cargaletras(_vvvvvvvvvvvvvvvvvv4.Palabra,_vvvvvvvv1);
 } 
       catch (Exception e69) {
			processBA.setLastException(e69); //BA.debugLineNum = 589;BA.debugLine="ManejaErrorJugar(\"CargarPalabra\",\"Cursor Vacio\")";
_vvvvvvvvvvvvvvvvvv3("CargarPalabra","Cursor Vacio");
 };
 //BA.debugLineNum = 592;BA.debugLine="Return bRet";
if (true) return _bret;
 //BA.debugLineNum = 593;BA.debugLine="End Sub";
return false;
}
public static String  _vvvvvvvvvvvvvvvvv5() throws Exception{
String _ssql = "";
 //BA.debugLineNum = 4654;BA.debugLine="Sub CheckPremium";
 //BA.debugLineNum = 4664;BA.debugLine="gb_EsPremium = (Publicos.Get_EsPremium(g_sqlBaseLo";
_gb_espremium = (mostCurrent._vvvvvvvvvvvvvvv7._get_espremium(mostCurrent.activityBA,_g_sqlbaselocalusuario));
 //BA.debugLineNum = 4666;BA.debugLine="If 	gb_EsPremium  Then";
if (_gb_espremium) { 
 //BA.debugLineNum = 4667;BA.debugLine="If Publicos.Get_EsPremiumGrabadoRemoto(g_sqlBaseL";
if (mostCurrent._vvvvvvvvvvvvvvv7._get_espremiumgrabadoremoto(mostCurrent.activityBA,_g_sqlbaselocalusuario)==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 4668;BA.debugLine="Remoto_UpdatePremium";
_remoto_updatepremium();
 };
 }else {
 //BA.debugLineNum = 4672;BA.debugLine="Dim sSql As String = \"CALL Usuario_rem_s ('\" & Pu";
_ssql = "CALL Usuario_rem_s ('"+mostCurrent._vvvvvvvvvvvvvvv7._vvv4(mostCurrent.activityBA)+"', '')";
 //BA.debugLineNum = 4673;BA.debugLine="RemotoSelect (sSql, xQuerysRemotos.Premium)";
_vvvvvvvvvvvvvvvvvvv0(_ssql,_vvvvvvvv5.Premium);
 };
 //BA.debugLineNum = 4677;BA.debugLine="If Publicos.Get_SoyYo = True Then";
if (mostCurrent._vvvvvvvvvvvvvvv7._get_soyyo(mostCurrent.activityBA)==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 4678;BA.debugLine="gb_EsPremium = False";
_gb_espremium = anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 4682;BA.debugLine="End Sub";
return "";
}
public static boolean  _vvvvvvvvvvvvvvvvvvvv1() throws Exception{
String _spalabra = "";
anywheresoftware.b4a.objects.LabelWrapper _v = null;
 //BA.debugLineNum = 709;BA.debugLine="Sub ComparaPalabra As Boolean";
 //BA.debugLineNum = 710;BA.debugLine="Dim sPalabra As String = \"\"";
_spalabra = "";
 //BA.debugLineNum = 712;BA.debugLine="For Each v As Label In lblArrayPalabra";
final anywheresoftware.b4a.objects.LabelWrapper[] group2 = mostCurrent._vvvvvvvvvvvvvvvvvvv1;
final int groupLen2 = group2.length;
for (int index2 = 0;index2 < groupLen2 ;index2++){
_v = group2[index2];
 //BA.debugLineNum = 713;BA.debugLine="If v.Text =\"\" Then";
if ((_v.getText()).equals("")) { 
 //BA.debugLineNum = 714;BA.debugLine="Exit";
if (true) break;
 };
 //BA.debugLineNum = 716;BA.debugLine="sPalabra = sPalabra & v.Text";
_spalabra = _spalabra+_v.getText();
 }
;
 //BA.debugLineNum = 718;BA.debugLine="Return sPalabra = xtPalabra.palabra";
if (true) return (_spalabra).equals(_vvvvvvvvvvvvvvvvvv4.Palabra);
 //BA.debugLineNum = 719;BA.debugLine="End Sub";
return false;
}
public static int  _vvvvvvvvvvvvvvvvvvvv2() throws Exception{
int _icant = 0;
anywheresoftware.b4a.objects.LabelWrapper _v = null;
 //BA.debugLineNum = 723;BA.debugLine="Sub ComparaPalabraCuentaLetras As Int";
 //BA.debugLineNum = 724;BA.debugLine="Dim iCant As Int = 0";
_icant = (int) (0);
 //BA.debugLineNum = 725;BA.debugLine="For Each v As Label In lblArrayPalabra";
final anywheresoftware.b4a.objects.LabelWrapper[] group2 = mostCurrent._vvvvvvvvvvvvvvvvvvv1;
final int groupLen2 = group2.length;
for (int index2 = 0;index2 < groupLen2 ;index2++){
_v = group2[index2];
 //BA.debugLineNum = 726;BA.debugLine="If v.Text =\"\" Then";
if ((_v.getText()).equals("")) { 
 //BA.debugLineNum = 727;BA.debugLine="Exit";
if (true) break;
 };
 //BA.debugLineNum = 729;BA.debugLine="iCant = iCant +1";
_icant = (int) (_icant+1);
 }
;
 //BA.debugLineNum = 731;BA.debugLine="Return iCant";
if (true) return _icant;
 //BA.debugLineNum = 732;BA.debugLine="End Sub";
return 0;
}
public static String  _vvvvvvvvvvvvvvvvvvvv3() throws Exception{
 //BA.debugLineNum = 734;BA.debugLine="Sub ComparaPalabraHacer";
 //BA.debugLineNum = 736;BA.debugLine="If ComparaPalabra Then";
if (_vvvvvvvvvvvvvvvvvvvv1()) { 
 //BA.debugLineNum = 737;BA.debugLine="AdivinoPalabra";
_vvvvvvvvvvvvvvvvvv5();
 }else {
 //BA.debugLineNum = 739;BA.debugLine="If ComparaPalabraCuentaLetras = xtPalabra.Palabr";
if (_vvvvvvvvvvvvvvvvvvvv2()==_vvvvvvvvvvvvvvvvvv4.Palabra.length()) { 
 //BA.debugLineNum = 740;BA.debugLine="V2_NoAdivinoPalabraResalta";
_v2_noadivinopalabraresalta();
 };
 };
 //BA.debugLineNum = 743;BA.debugLine="End Sub";
return "";
}
public static boolean  _vvvvvvvvvvvvvvvvvvvv4(int _ipos,int _iposletras) throws Exception{
String _ssql = "";
boolean _bret = false;
 //BA.debugLineNum = 1323;BA.debugLine="Sub ComprarLetraGrabar(iPos As Int, iPosLetras As";
 //BA.debugLineNum = 1324;BA.debugLine="Dim sSql As String, bRet As Boolean=False";
_ssql = "";
_bret = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 1326;BA.debugLine="Try";
try { //BA.debugLineNum = 1328;BA.debugLine="g_sqlBaseLocalUsuario.BeginTransaction";
_g_sqlbaselocalusuario.BeginTransaction();
 //BA.debugLineNum = 1329;BA.debugLine="sSql = \"Insert Into LetrasCompradas (idpalabra, p";
_ssql = "Insert Into LetrasCompradas (idpalabra, posicion) Values ("+BA.NumberToString(_vvvvvvvv1)+","+BA.NumberToString(_ipos)+")";
 //BA.debugLineNum = 1330;BA.debugLine="g_sqlBaseLocalUsuario.ExecNonQuery(sSql)";
_g_sqlbaselocalusuario.ExecNonQuery(_ssql);
 //BA.debugLineNum = 1332;BA.debugLine="sSql =\"Update Usuario Set Monedas = monedas -\"& G";
_ssql = "Update Usuario Set Monedas = monedas -"+_get_costocomprarletras()+"";
 //BA.debugLineNum = 1333;BA.debugLine="g_sqlBaseLocalUsuario.ExecNonQuery(sSql)";
_g_sqlbaselocalusuario.ExecNonQuery(_ssql);
 //BA.debugLineNum = 1335;BA.debugLine="sSql =\"Update LetrasPorPalabra set letra ='?', Po";
_ssql = "Update LetrasPorPalabra set letra ='?', PosicionPalabra = "+BA.NumberToString(_ipos)+" where idpalabra="+BA.NumberToString(_vvvvvvvv1)+" and Posicion="+BA.NumberToString(_iposletras);
 //BA.debugLineNum = 1336;BA.debugLine="g_sqlBaseLocalUsuario.ExecNonQuery(sSql)";
_g_sqlbaselocalusuario.ExecNonQuery(_ssql);
 //BA.debugLineNum = 1339;BA.debugLine="g_sqlBaseLocalUsuario.TransactionSuccessful";
_g_sqlbaselocalusuario.TransactionSuccessful();
 //BA.debugLineNum = 1340;BA.debugLine="g_sqlBaseLocalUsuario.EndTransaction";
_g_sqlbaselocalusuario.EndTransaction();
 //BA.debugLineNum = 1341;BA.debugLine="bRet = True";
_bret = anywheresoftware.b4a.keywords.Common.True;
 } 
       catch (Exception e14) {
			processBA.setLastException(e14); //BA.debugLineNum = 1344;BA.debugLine="ManejaErrorJugar(\"ComprarLetrasGrabar\",\"Error Com";
_vvvvvvvvvvvvvvvvvv3("ComprarLetrasGrabar","Error ComprarLetrasGrabar");
 //BA.debugLineNum = 1345;BA.debugLine="g_sqlBaseLocalUsuario.EndTransaction";
_g_sqlbaselocalusuario.EndTransaction();
 };
 //BA.debugLineNum = 1347;BA.debugLine="Return bRet";
if (true) return _bret;
 //BA.debugLineNum = 1348;BA.debugLine="End Sub";
return false;
}
public static String  _vvvvvvvvvvvvvvvvvvvv5() throws Exception{
int _iposletras = 0;
int _iletraenpalabra = 0;
int _icomprar = 0;
int _iposactualletracomprarenpalabra = 0;
String _sletraacomprar = "";
 //BA.debugLineNum = 1611;BA.debugLine="Sub ComprarLetraHacer";
 //BA.debugLineNum = 1613;BA.debugLine="Dim  iPosLetras As Int,  iLetraEnPalabra As Int";
_iposletras = 0;
_iletraenpalabra = 0;
 //BA.debugLineNum = 1614;BA.debugLine="Dim iComprar As Int";
_icomprar = 0;
 //BA.debugLineNum = 1615;BA.debugLine="Dim iPosActualLetraComprarEnPalabra As Int, sLetr";
_iposactualletracomprarenpalabra = 0;
_sletraacomprar = "";
 //BA.debugLineNum = 1620;BA.debugLine="iComprar =Get_ComprarLetraAzar";
_icomprar = _get_comprarletraazar();
 //BA.debugLineNum = 1622;BA.debugLine="If lblArrayPalabra(iComprar).Text <>\"\" Then";
if ((mostCurrent._vvvvvvvvvvvvvvvvvvv1[_icomprar].getText()).equals("") == false) { 
 //BA.debugLineNum = 1623;BA.debugLine="lblPalabraClick (lblArrayPalabra(iComprar))";
_vvvvvvvvvvvvvvvvvvv2(mostCurrent._vvvvvvvvvvvvvvvvvvv1[_icomprar]);
 };
 //BA.debugLineNum = 1627;BA.debugLine="sLetraAComprar = aLetrasPalabra(iComprar).letra";
_sletraacomprar = BA.ObjectToString(_vvvvvvvv7[_icomprar].letra);
 //BA.debugLineNum = 1628;BA.debugLine="iPosLetras = Get_LetrasPosicion(sLetraAComprar)";
_iposletras = _get_letrasposicion(_sletraacomprar);
 //BA.debugLineNum = 1630;BA.debugLine="If iPosLetras = -1 Then";
if (_iposletras==-1) { 
 //BA.debugLineNum = 1631;BA.debugLine="iPosActualLetraComprarEnPalabra = get_LetraEnPal";
_iposactualletracomprarenpalabra = _get_letraenpalabra(_sletraacomprar);
 //BA.debugLineNum = 1632;BA.debugLine="lblPalabraClick (lblArrayPalabra(iPosActualLetra";
_vvvvvvvvvvvvvvvvvvv2(mostCurrent._vvvvvvvvvvvvvvvvvvv1[_iposactualletracomprarenpalabra]);
 //BA.debugLineNum = 1634;BA.debugLine="iPosLetras = Get_LetrasPosicion(sLetraAComprar)";
_iposletras = _get_letrasposicion(_sletraacomprar);
 };
 //BA.debugLineNum = 1638;BA.debugLine="If ComprarLetraGrabar (iComprar, iPosLetras) Then";
if (_vvvvvvvvvvvvvvvvvvvv4(_icomprar,_iposletras)) { 
 //BA.debugLineNum = 1639;BA.debugLine="Neuronas_Mostrar";
_neuronas_mostrar();
 //BA.debugLineNum = 1640;BA.debugLine="EligeUnaLetra (lblArrayLetras(iPosLetras), True,";
_vvvvvvvvvvvvvvvvvvvv6(mostCurrent._vvvvvvvvvvvvvvvvvvvv7[_iposletras],anywheresoftware.b4a.keywords.Common.True,_icomprar,_iposletras);
 };
 //BA.debugLineNum = 1645;BA.debugLine="Activity.Invalidate";
mostCurrent._activity.Invalidate();
 //BA.debugLineNum = 1646;BA.debugLine="DoEvents";
anywheresoftware.b4a.keywords.Common.DoEvents();
 //BA.debugLineNum = 1649;BA.debugLine="V2_AnimaRotateImageview(imgPedirLetra, 0, 720, 50";
_v2_animarotateimageview(mostCurrent._imgpedirletra,(int) (0),(int) (720),(int) (500));
 //BA.debugLineNum = 1650;BA.debugLine="V2_AnimaRotateImageview(imgNeuronas, 0, 720, 800)";
_v2_animarotateimageview(mostCurrent._imgneuronas,(int) (0),(int) (720),(int) (800));
 //BA.debugLineNum = 1654;BA.debugLine="End Sub";
return "";
}
public static boolean  _vvvvvvvvvvvvvvvvvvvv0() throws Exception{
boolean _bret = false;
 //BA.debugLineNum = 1257;BA.debugLine="Sub ComprarLetraPuede As Boolean";
 //BA.debugLineNum = 1258;BA.debugLine="Dim bret As Boolean";
_bret = false;
 //BA.debugLineNum = 1259;BA.debugLine="bret = Get_CostoComprarLetras <= g_iMonedas";
_bret = (double)(Double.parseDouble(_get_costocomprarletras()))<=_g_imonedas;
 //BA.debugLineNum = 1261;BA.debugLine="Return bret";
if (true) return _bret;
 //BA.debugLineNum = 1262;BA.debugLine="End Sub";
return false;
}
public static boolean  _vvvvvvvvvvvvvvvvvvvvv1() throws Exception{
boolean _bret = false;
String _stit = "";
int _icostomonedas = 0;
String _sdescmonedas = "";
 //BA.debugLineNum = 1264;BA.debugLine="Sub ComprarLetraQuiere () As Boolean";
 //BA.debugLineNum = 1265;BA.debugLine="Dim bRet As Boolean = False, sTit As String, iCos";
_bret = anywheresoftware.b4a.keywords.Common.False;
_stit = "";
_icostomonedas = 0;
_sdescmonedas = "";
 //BA.debugLineNum = 1266;BA.debugLine="iCostoMonedas = Get_CostoComprarLetras";
_icostomonedas = (int)(Double.parseDouble(_get_costocomprarletras()));
 //BA.debugLineNum = 1267;BA.debugLine="If iCostoMonedas = 1 Then";
if (_icostomonedas==1) { 
 //BA.debugLineNum = 1268;BA.debugLine="sDescMonedas =\"moneda\"";
_sdescmonedas = "moneda";
 }else {
 //BA.debugLineNum = 1270;BA.debugLine="sDescMonedas = \"monedas\"";
_sdescmonedas = "monedas";
 };
 //BA.debugLineNum = 1273;BA.debugLine="sTit = \"¿Confirma la compra de una letra?\"";
_stit = "¿Confirma la compra de una letra?";
 //BA.debugLineNum = 1274;BA.debugLine="bRet = Msgbox2(sTit,\"Costo: \"&iCostoMonedas &\" \"&";
_bret = anywheresoftware.b4a.keywords.Common.Msgbox2(_stit,"Costo: "+BA.NumberToString(_icostomonedas)+" "+_sdescmonedas,"Comprar","Cancelar","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null),mostCurrent.activityBA)==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE;
 //BA.debugLineNum = 1276;BA.debugLine="Return bRet";
if (true) return _bret;
 //BA.debugLineNum = 1277;BA.debugLine="End Sub";
return false;
}
public static String  _costos_muestra() throws Exception{
 //BA.debugLineNum = 3735;BA.debugLine="Sub Costos_Muestra";
 //BA.debugLineNum = 3736;BA.debugLine="lblSaltarPalabraCosto.text =xtNivel.costosaltar";
mostCurrent._lblsaltarpalabracosto.setText((Object)(_vvvvvvvvvvvvvvvvvv7.CostoSaltar));
 //BA.debugLineNum = 3737;BA.debugLine="lblPedirLetraCosto.text = xtNivel.CostoLetra";
mostCurrent._lblpedirletracosto.setText((Object)(_vvvvvvvvvvvvvvvvvv7.CostoLetra));
 //BA.debugLineNum = 3739;BA.debugLine="Publicos.SetLabelTextSize(lblSaltarPalabraCosto,";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv4(mostCurrent.activityBA,mostCurrent._lblsaltarpalabracosto,mostCurrent._lblsaltarpalabracosto.getText(),(float) (30),(float) (6),(int) (95));
 //BA.debugLineNum = 3740;BA.debugLine="lblPedirLetraCosto.TextSize = lblSaltarPalabraCos";
mostCurrent._lblpedirletracosto.setTextSize(mostCurrent._lblsaltarpalabracosto.getTextSize());
 //BA.debugLineNum = 3741;BA.debugLine="End Sub";
return "";
}
public static String  _create_variables() throws Exception{
 //BA.debugLineNum = 4646;BA.debugLine="Sub Create_Variables";
 //BA.debugLineNum = 4647;BA.debugLine="xConfiguraPantalla= CreateTypeConfiguraPantalla(0";
_vvvvvvvv3 = _vvvvvvvvvvvvvvvvvvvvv2((int) (0),(int) (1),(int) (2),(int) (3),(int) (4),(int) (5),(int) (6),(int) (7),(int) (8),(int) (9),(int) (10),(int) (11),(int) (12));
 //BA.debugLineNum = 4648;BA.debugLine="xQuerysRemotos = CreateTypeQuerysRemotos(0,1)";
_vvvvvvvv5 = _vvvvvvvvvvvvvvvvvvvvv3((int) (0),(int) (1));
 //BA.debugLineNum = 4649;BA.debugLine="tFontOpenSansSemiBold= Typeface.LoadFromAssets(\"O";
_vvvvvvvvvv4.setObject((android.graphics.Typeface)(anywheresoftware.b4a.keywords.Common.Typeface.LoadFromAssets("OpenSans-Semibold.ttf")));
 //BA.debugLineNum = 4650;BA.debugLine="tFontOpenSansLight= Typeface.LoadFromAssets(\"Open";
_vvvvvvvvvv5.setObject((android.graphics.Typeface)(anywheresoftware.b4a.keywords.Common.Typeface.LoadFromAssets("OpenSans-Light.ttf")));
 //BA.debugLineNum = 4651;BA.debugLine="Inicializa_ColoresPaleta";
_inicializa_colorespaleta();
 //BA.debugLineNum = 4652;BA.debugLine="End Sub";
return "";
}
public static com.matetejuego.free.publicos._tconfigurapantalla  _vvvvvvvvvvvvvvvvvvvvv2(int _pcompartir,int _pcomprarletra,int _psaltarpalabra,int _pjugar,int _padivino,int _payuda,int _pcreditos,int _pfindejuego,int _pproducto,int _pcomproneuronas,int _pmuestraaviso,int _ppremium,int _phistoria) throws Exception{
com.matetejuego.free.publicos._tconfigurapantalla _m = null;
 //BA.debugLineNum = 2673;BA.debugLine="Sub CreateTypeConfiguraPantalla(pCompartir As Int,";
 //BA.debugLineNum = 2677;BA.debugLine="Dim m As tConfiguraPantalla";
_m = new com.matetejuego.free.publicos._tconfigurapantalla();
 //BA.debugLineNum = 2678;BA.debugLine="m.Initialize";
_m.Initialize();
 //BA.debugLineNum = 2679;BA.debugLine="m.Compartir = pCompartir";
_m.Compartir = _pcompartir;
 //BA.debugLineNum = 2680;BA.debugLine="m.ComprarLetra = pComprarLetra";
_m.ComprarLetra = _pcomprarletra;
 //BA.debugLineNum = 2681;BA.debugLine="m.SaltarPalabra= pSaltarPalabra";
_m.SaltarPalabra = _psaltarpalabra;
 //BA.debugLineNum = 2682;BA.debugLine="m.Jugar = pJugar";
_m.Jugar = _pjugar;
 //BA.debugLineNum = 2683;BA.debugLine="m.Adivino = pAdivino";
_m.Adivino = _padivino;
 //BA.debugLineNum = 2684;BA.debugLine="m.Ayuda = pAyuda";
_m.Ayuda = _payuda;
 //BA.debugLineNum = 2685;BA.debugLine="m.Creditos = pCreditos";
_m.Creditos = _pcreditos;
 //BA.debugLineNum = 2686;BA.debugLine="m.FinDeJuego = pFinDeJuego";
_m.FinDeJuego = _pfindejuego;
 //BA.debugLineNum = 2687;BA.debugLine="m.Producto = pProducto";
_m.Producto = _pproducto;
 //BA.debugLineNum = 2688;BA.debugLine="m.ComproNeuronas = pComproNeuronas";
_m.ComproNeuronas = _pcomproneuronas;
 //BA.debugLineNum = 2689;BA.debugLine="m.MuestraAviso = pMuestraAviso";
_m.MuestraAviso = _pmuestraaviso;
 //BA.debugLineNum = 2690;BA.debugLine="m.Premium = pPremium";
_m.Premium = _ppremium;
 //BA.debugLineNum = 2691;BA.debugLine="m.Historia = pHistoria";
_m.Historia = _phistoria;
 //BA.debugLineNum = 2692;BA.debugLine="Return m";
if (true) return _m;
 //BA.debugLineNum = 2693;BA.debugLine="End Sub";
return null;
}
public static com.matetejuego.free.publicos._tquerysremotos  _vvvvvvvvvvvvvvvvvvvvv3(int _pavance,int _ppremium) throws Exception{
com.matetejuego.free.publicos._tquerysremotos _m = null;
 //BA.debugLineNum = 2666;BA.debugLine="Sub CreateTypeQuerysRemotos(pAvance As Int, pPremi";
 //BA.debugLineNum = 2667;BA.debugLine="Dim m As tQuerysRemotos";
_m = new com.matetejuego.free.publicos._tquerysremotos();
 //BA.debugLineNum = 2668;BA.debugLine="m.Avance = pAvance";
_m.Avance = _pavance;
 //BA.debugLineNum = 2669;BA.debugLine="m.Premium = pPremium";
_m.Premium = _ppremium;
 //BA.debugLineNum = 2670;BA.debugLine="Return m";
if (true) return _m;
 //BA.debugLineNum = 2671;BA.debugLine="End Sub";
return null;
}
public static String  _vvvvvvvvvvvvvvvvvvvvv4(anywheresoftware.b4a.objects.LabelWrapper _llabel) throws Exception{
anywheresoftware.b4a.objects.LabelWrapper _v = null;
 //BA.debugLineNum = 851;BA.debugLine="Sub DescartaUnaLetra (lLabel As Label) ' recibe co";
 //BA.debugLineNum = 855;BA.debugLine="If lLabel.Text <>\"\" And lLabel.Tag <>\"C\" Then ' s";
if ((_llabel.getText()).equals("") == false && (_llabel.getTag()).equals((Object)("C")) == false) { 
 //BA.debugLineNum = 856;BA.debugLine="For Each v As Label In lblArrayLetras";
final anywheresoftware.b4a.objects.LabelWrapper[] group2 = mostCurrent._vvvvvvvvvvvvvvvvvvvv7;
final int groupLen2 = group2.length;
for (int index2 = 0;index2 < groupLen2 ;index2++){
_v = group2[index2];
 //BA.debugLineNum = 857;BA.debugLine="If v.Tag = lLabel.tag Then ' si es el label que";
if ((_v.getTag()).equals(_llabel.getTag())) { 
 //BA.debugLineNum = 858;BA.debugLine="v.Text = lLabel.Text";
_v.setText((Object)(_llabel.getText()));
 //BA.debugLineNum = 861;BA.debugLine="lLabel.text = \"\"";
_llabel.setText((Object)(""));
 //BA.debugLineNum = 863;BA.debugLine="lLabel.Text = \"\"";
_llabel.setText((Object)(""));
 //BA.debugLineNum = 865;BA.debugLine="v.TextColor = Get_ColorLetraDisponible";
_v.setTextColor(_get_colorletradisponible());
 //BA.debugLineNum = 866;BA.debugLine="lLabel.SetBackgroundImage(gt_Color.bitmPalabra";
_llabel.SetBackgroundImage((android.graphics.Bitmap)(mostCurrent._gt_color.bitmPalabraOff.getObject()));
 //BA.debugLineNum = 867;BA.debugLine="Exit";
if (true) break;
 };
 }
;
 };
 //BA.debugLineNum = 874;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvvvvvvv6(anywheresoftware.b4a.objects.LabelWrapper _llabel,boolean _bcomprada,int _iposicionpalabra,int _iposletras) throws Exception{
String _sletra = "";
String _saux = "";
int _i = 0;
 //BA.debugLineNum = 644;BA.debugLine="Sub EligeUnaLetra (lLabel As Label, bComprada As B";
 //BA.debugLineNum = 647;BA.debugLine="Dim sLetra As String, sAux As String";
_sletra = "";
_saux = "";
 //BA.debugLineNum = 650;BA.debugLine="Try";
try { //BA.debugLineNum = 653;BA.debugLine="If iPosicionPalabra = -1 Then ' si va a la primer";
if (_iposicionpalabra==-1) { 
 //BA.debugLineNum = 655;BA.debugLine="For i = 0 To lblArrayPalabra.Length-1";
{
final int step4 = 1;
final int limit4 = (int) (mostCurrent._vvvvvvvvvvvvvvvvvvv1.length-1);
for (_i = (int) (0) ; (step4 > 0 && _i <= limit4) || (step4 < 0 && _i >= limit4); _i = ((int)(0 + _i + step4)) ) {
 //BA.debugLineNum = 656;BA.debugLine="If lblArrayPalabra(i).Text = \"\" Then";
if ((mostCurrent._vvvvvvvvvvvvvvvvvvv1[_i].getText()).equals("")) { 
 //BA.debugLineNum = 657;BA.debugLine="iPosicionPalabra = i";
_iposicionpalabra = _i;
 //BA.debugLineNum = 658;BA.debugLine="Exit";
if (true) break;
 };
 }
};
 };
 //BA.debugLineNum = 664;BA.debugLine="lblArrayPalabra(iPosicionPalabra).Text = lLabel.T";
mostCurrent._vvvvvvvvvvvvvvvvvvv1[_iposicionpalabra].setText((Object)(_llabel.getText()));
 //BA.debugLineNum = 665;BA.debugLine="sLetra = lLabel.Text";
_sletra = _llabel.getText();
 //BA.debugLineNum = 666;BA.debugLine="If bComprada Then";
if (_bcomprada) { 
 //BA.debugLineNum = 668;BA.debugLine="aLetrasPalabra(iPosicionPalabra).comprada = True";
_vvvvvvvv7[_iposicionpalabra].comprada = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 669;BA.debugLine="lblArrayPalabra(iPosicionPalabra).Tag=\"?\"";
mostCurrent._vvvvvvvvvvvvvvvvvvv1[_iposicionpalabra].setTag((Object)("?"));
 //BA.debugLineNum = 671;BA.debugLine="For i = 0 To sgLetras.Length-1";
{
final int step16 = 1;
final int limit16 = (int) (_vvvvvvv0.length()-1);
for (_i = (int) (0) ; (step16 > 0 && _i <= limit16) || (step16 < 0 && _i >= limit16); _i = ((int)(0 + _i + step16)) ) {
 //BA.debugLineNum = 672;BA.debugLine="If i<> iPosLetras Then";
if (_i!=_iposletras) { 
 //BA.debugLineNum = 673;BA.debugLine="sAux = sAux & sgLetras.SubString2(i,i+1)";
_saux = _saux+_vvvvvvv0.substring(_i,(int) (_i+1));
 }else {
 //BA.debugLineNum = 675;BA.debugLine="sAux = sAux & \"?\"";
_saux = _saux+"?";
 };
 }
};
 //BA.debugLineNum = 678;BA.debugLine="sgLetras = sAux";
_vvvvvvv0 = _saux;
 //BA.debugLineNum = 679;BA.debugLine="lblArrayPalabra(iPosicionPalabra).SetBackgroundI";
mostCurrent._vvvvvvvvvvvvvvvvvvv1[_iposicionpalabra].SetBackgroundImage((android.graphics.Bitmap)(mostCurrent._gt_color.bitmPalabraComprada.getObject()));
 }else {
 //BA.debugLineNum = 681;BA.debugLine="lblArrayPalabra(iPosicionPalabra).TextColor = gt";
mostCurrent._vvvvvvvvvvvvvvvvvvv1[_iposicionpalabra].setTextColor(mostCurrent._gt_color.ColorTexto);
 //BA.debugLineNum = 682;BA.debugLine="lblArrayPalabra(iPosicionPalabra).Tag = lLabel.T";
mostCurrent._vvvvvvvvvvvvvvvvvvv1[_iposicionpalabra].setTag(_llabel.getTag());
 //BA.debugLineNum = 683;BA.debugLine="lblArrayPalabra(iPosicionPalabra).SetBackgroundI";
mostCurrent._vvvvvvvvvvvvvvvvvvv1[_iposicionpalabra].SetBackgroundImage((android.graphics.Bitmap)(mostCurrent._gt_color.bitmPalabraOn.getObject()));
 };
 //BA.debugLineNum = 689;BA.debugLine="lLabel.textcolor = Get_ColorLetraVacia";
_llabel.setTextColor(_get_colorletravacia());
 //BA.debugLineNum = 692;BA.debugLine="lblArrayPalabra(iPosicionPalabra).text = sLetra";
mostCurrent._vvvvvvvvvvvvvvvvvvv1[_iposicionpalabra].setText((Object)(_sletra));
 //BA.debugLineNum = 696;BA.debugLine="If ComparaPalabraCuentaLetras = xtPalabra.Palabra";
if (_vvvvvvvvvvvvvvvvvvvv2()==_vvvvvvvvvvvvvvvvvv4.Palabra.length()) { 
 //BA.debugLineNum = 697;BA.debugLine="ComparaPalabraHacer";
_vvvvvvvvvvvvvvvvvvvv3();
 };
 } 
       catch (Exception e36) {
			processBA.setLastException(e36); //BA.debugLineNum = 702;BA.debugLine="ManejaErrorJugar(\"EligeUnaLetra\",\"Error Letra: \"&";
_vvvvvvvvvvvvvvvvvv3("EligeUnaLetra","Error Letra: "+_sletra);
 };
 //BA.debugLineNum = 707;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvvvvvvvv5() throws Exception{
 //BA.debugLineNum = 1350;BA.debugLine="Sub FinDeJuego";
 //BA.debugLineNum = 1351;BA.debugLine="End Sub";
return "";
}
public static int  _get_ancholetra() throws Exception{
int _iret = 0;
 //BA.debugLineNum = 1670;BA.debugLine="Sub Get_AnchoLetra As Int";
 //BA.debugLineNum = 1671;BA.debugLine="Dim iRet As Int";
_iret = 0;
 //BA.debugLineNum = 1675;BA.debugLine="iRet = Panel5.Width /9";
_iret = (int) (mostCurrent._panel5.getWidth()/(double)9);
 //BA.debugLineNum = 1678;BA.debugLine="Return iRet";
if (true) return _iret;
 //BA.debugLineNum = 1679;BA.debugLine="End Sub";
return 0;
}
public static int  _get_ancholetrapalabra(int _ilargo) throws Exception{
int _iret = 0;
 //BA.debugLineNum = 1680;BA.debugLine="Sub Get_AnchoLetraPalabra(iLargo As Int) As Int";
 //BA.debugLineNum = 1681;BA.debugLine="Dim iRet As Int";
_iret = 0;
 //BA.debugLineNum = 1685;BA.debugLine="If iLargo <10 Then";
if (_ilargo<10) { 
 //BA.debugLineNum = 1686;BA.debugLine="iRet = Activity.Width /11";
_iret = (int) (mostCurrent._activity.getWidth()/(double)11);
 }else {
 //BA.debugLineNum = 1688;BA.debugLine="iRet = Activity.Width /12.2";
_iret = (int) (mostCurrent._activity.getWidth()/(double)12.2);
 };
 //BA.debugLineNum = 1691;BA.debugLine="Return iRet";
if (true) return _iret;
 //BA.debugLineNum = 1692;BA.debugLine="End Sub";
return 0;
}
public static int  _get_atextcolor(int _a,int _b) throws Exception{
int _iret = 0;
 //BA.debugLineNum = 4905;BA.debugLine="Sub Get_aTextColor (a As Int, b As Int) As Int";
 //BA.debugLineNum = 4906;BA.debugLine="Dim iRet As Int";
_iret = 0;
 //BA.debugLineNum = 4907;BA.debugLine="iRet = gt_Color.ColorTexto";
_iret = mostCurrent._gt_color.ColorTexto;
 //BA.debugLineNum = 4908;BA.debugLine="Return iRet";
if (true) return _iret;
 //BA.debugLineNum = 4909;BA.debugLine="End Sub";
return 0;
}
public static int  _get_colorletradisponible() throws Exception{
int _iret = 0;
 //BA.debugLineNum = 2472;BA.debugLine="Sub Get_ColorLetraDisponible As Int";
 //BA.debugLineNum = 2473;BA.debugLine="Dim iRet As Int";
_iret = 0;
 //BA.debugLineNum = 2474;BA.debugLine="iRet = gt_Color.colordefault";
_iret = mostCurrent._gt_color.ColorDefault;
 //BA.debugLineNum = 2476;BA.debugLine="Return iRet";
if (true) return _iret;
 //BA.debugLineNum = 2477;BA.debugLine="End Sub";
return 0;
}
public static int  _get_colorletravacia() throws Exception{
int _iret = 0;
 //BA.debugLineNum = 2479;BA.debugLine="Sub Get_ColorLetraVacia As Int";
 //BA.debugLineNum = 2480;BA.debugLine="Dim iRet As Int";
_iret = 0;
 //BA.debugLineNum = 2481;BA.debugLine="iRet = Colors.Transparent";
_iret = anywheresoftware.b4a.keywords.Common.Colors.Transparent;
 //BA.debugLineNum = 2482;BA.debugLine="Return iRet";
if (true) return _iret;
 //BA.debugLineNum = 2483;BA.debugLine="End Sub";
return 0;
}
public static int  _get_comprarletraazar() throws Exception{
int _iaux = 0;
int _iletracomprada = 0;
int _ipos = 0;
int[] _ailetrasnocompradas = null;
int _j = 0;
 //BA.debugLineNum = 1596;BA.debugLine="Sub Get_ComprarLetraAzar As Int";
 //BA.debugLineNum = 1598;BA.debugLine="Dim iAux As Int =0, iLetraComprada As Int, iPos As";
_iaux = (int) (0);
_iletracomprada = 0;
_ipos = 0;
_ailetrasnocompradas = new int[_vvvvvvvv7.length];
;
 //BA.debugLineNum = 1600;BA.debugLine="For j = 0 To aLetrasPalabra.Length-1";
{
final int step2 = 1;
final int limit2 = (int) (_vvvvvvvv7.length-1);
for (_j = (int) (0) ; (step2 > 0 && _j <= limit2) || (step2 < 0 && _j >= limit2); _j = ((int)(0 + _j + step2)) ) {
 //BA.debugLineNum = 1601;BA.debugLine="If aLetrasPalabra(j).comprada = False Then";
if (_vvvvvvvv7[_j].comprada==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 1602;BA.debugLine="iAux = iAux +1";
_iaux = (int) (_iaux+1);
 //BA.debugLineNum = 1603;BA.debugLine="ailetrasnocompradas(iAux-1) = j";
_ailetrasnocompradas[(int) (_iaux-1)] = _j;
 };
 }
};
 //BA.debugLineNum = 1606;BA.debugLine="iPos = Rnd(0,iAux)";
_ipos = anywheresoftware.b4a.keywords.Common.Rnd((int) (0),_iaux);
 //BA.debugLineNum = 1607;BA.debugLine="iLetraComprada = ailetrasnocompradas(iPos)";
_iletracomprada = _ailetrasnocompradas[_ipos];
 //BA.debugLineNum = 1608;BA.debugLine="Return iLetraComprada";
if (true) return _iletracomprada;
 //BA.debugLineNum = 1609;BA.debugLine="End Sub";
return 0;
}
public static int  _get_costoayuda() throws Exception{
 //BA.debugLineNum = 1110;BA.debugLine="Sub Get_CostoAyuda As Int";
 //BA.debugLineNum = 1111;BA.debugLine="Return xtNivel.CostoAyuda";
if (true) return _vvvvvvvvvvvvvvvvvv7.CostoAyuda;
 //BA.debugLineNum = 1112;BA.debugLine="End Sub";
return 0;
}
public static String  _get_costocomprarletras() throws Exception{
int _iret = 0;
 //BA.debugLineNum = 1096;BA.debugLine="Sub Get_CostoComprarLetras";
 //BA.debugLineNum = 1098;BA.debugLine="Dim iRet As Int";
_iret = 0;
 //BA.debugLineNum = 1099;BA.debugLine="iRet = xtNivel.CostoLetra";
_iret = _vvvvvvvvvvvvvvvvvv7.CostoLetra;
 //BA.debugLineNum = 1100;BA.debugLine="Return iRet";
if (true) return BA.NumberToString(_iret);
 //BA.debugLineNum = 1101;BA.debugLine="End Sub";
return "";
}
public static int  _get_costosaltarpalabra() throws Exception{
int _iret = 0;
 //BA.debugLineNum = 1103;BA.debugLine="Sub Get_CostoSaltarPalabra As Int";
 //BA.debugLineNum = 1105;BA.debugLine="Dim iret As Int";
_iret = 0;
 //BA.debugLineNum = 1106;BA.debugLine="iret = xtNivel.CostoSaltar";
_iret = _vvvvvvvvvvvvvvvvvv7.CostoSaltar;
 //BA.debugLineNum = 1107;BA.debugLine="Return iret";
if (true) return _iret;
 //BA.debugLineNum = 1108;BA.debugLine="End Sub";
return 0;
}
public static anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper  _get_imagencolor(String _simagen) throws Exception{
anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _bitret = null;
 //BA.debugLineNum = 4763;BA.debugLine="Sub Get_ImagenColor(sImagen As String) As Bitmap";
 //BA.debugLineNum = 4765;BA.debugLine="Dim bitRet As Bitmap";
_bitret = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
 //BA.debugLineNum = 4766;BA.debugLine="bitRet = LoadBitmap(File.DirAssets, sImagen)";
_bitret = anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),_simagen);
 //BA.debugLineNum = 4767;BA.debugLine="Return bitRet";
if (true) return _bitret;
 //BA.debugLineNum = 4768;BA.debugLine="End Sub";
return null;
}
public static int  _get_inicioletras(int _iancho,int _ipcantletras) throws Exception{
int _iret = 0;
 //BA.debugLineNum = 1700;BA.debugLine="Sub Get_InicioLetras (iAncho As Int, ipCantLetras";
 //BA.debugLineNum = 1701;BA.debugLine="Dim iRet As Int";
_iret = 0;
 //BA.debugLineNum = 1702;BA.debugLine="iRet = Activity.Width / 2 - (Get_SeparaLetra+iAnc";
_iret = (int) (mostCurrent._activity.getWidth()/(double)2-(_get_separaletra()+_iancho)*(_ipcantletras/(double)2));
 //BA.debugLineNum = 1703;BA.debugLine="Return iRet";
if (true) return _iret;
 //BA.debugLineNum = 1704;BA.debugLine="End Sub";
return 0;
}
public static int  _get_letraenpalabra(String _sletra) throws Exception{
int _iret = 0;
int _j = 0;
 //BA.debugLineNum = 1656;BA.debugLine="Sub get_LetraEnPalabra (sLetra As String) As Int";
 //BA.debugLineNum = 1657;BA.debugLine="Dim iRet As Int = -1";
_iret = (int) (-1);
 //BA.debugLineNum = 1659;BA.debugLine="For j =0 To aLetrasPalabra.Length-1";
{
final int step2 = 1;
final int limit2 = (int) (_vvvvvvvv7.length-1);
for (_j = (int) (0) ; (step2 > 0 && _j <= limit2) || (step2 < 0 && _j >= limit2); _j = ((int)(0 + _j + step2)) ) {
 //BA.debugLineNum = 1660;BA.debugLine="If aLetrasPalabra(j).comprada = False Then";
if (_vvvvvvvv7[_j].comprada==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 1661;BA.debugLine="If lblArrayPalabra(j).Text = sLetra Then";
if ((mostCurrent._vvvvvvvvvvvvvvvvvvv1[_j].getText()).equals(_sletra)) { 
 //BA.debugLineNum = 1662;BA.debugLine="iRet = j";
_iret = _j;
 //BA.debugLineNum = 1663;BA.debugLine="Exit";
if (true) break;
 };
 };
 }
};
 //BA.debugLineNum = 1667;BA.debugLine="Return iRet";
if (true) return _iret;
 //BA.debugLineNum = 1668;BA.debugLine="End Sub";
return 0;
}
public static String  _get_letraselegirfb() throws Exception{
String _sret = "";
int _j = 0;
 //BA.debugLineNum = 1783;BA.debugLine="Sub Get_LetrasElegirFb As String";
 //BA.debugLineNum = 1784;BA.debugLine="Dim sRet As String = \"\"";
_sret = "";
 //BA.debugLineNum = 1785;BA.debugLine="For j = 0 To sgLetras.Length-1";
{
final int step2 = 1;
final int limit2 = (int) (_vvvvvvv0.length()-1);
for (_j = (int) (0) ; (step2 > 0 && _j <= limit2) || (step2 < 0 && _j >= limit2); _j = ((int)(0 + _j + step2)) ) {
 //BA.debugLineNum = 1786;BA.debugLine="If sgLetras.SubString2(j,j+1) = \"?\" Then";
if ((_vvvvvvv0.substring(_j,(int) (_j+1))).equals("?")) { 
 //BA.debugLineNum = 1787;BA.debugLine="sRet = sRet & \" \"";
_sret = _sret+" ";
 }else {
 //BA.debugLineNum = 1789;BA.debugLine="sRet = sRet & sgLetras.SubString2(j,j+1)";
_sret = _sret+_vvvvvvv0.substring(_j,(int) (_j+1));
 };
 }
};
 //BA.debugLineNum = 1792;BA.debugLine="Return sRet";
if (true) return _sret;
 //BA.debugLineNum = 1793;BA.debugLine="End Sub";
return "";
}
public static String  _get_letraspalabrafb() throws Exception{
String _sret = "";
int _j = 0;
 //BA.debugLineNum = 1770;BA.debugLine="Sub Get_LetrasPalabraFb As String";
 //BA.debugLineNum = 1772;BA.debugLine="Dim sRet As String";
_sret = "";
 //BA.debugLineNum = 1773;BA.debugLine="For j = 0 To aLetrasPalabra.Length -1";
{
final int step2 = 1;
final int limit2 = (int) (_vvvvvvvv7.length-1);
for (_j = (int) (0) ; (step2 > 0 && _j <= limit2) || (step2 < 0 && _j >= limit2); _j = ((int)(0 + _j + step2)) ) {
 //BA.debugLineNum = 1774;BA.debugLine="If aLetrasPalabra(j).comprada Then";
if (_vvvvvvvv7[_j].comprada) { 
 //BA.debugLineNum = 1775;BA.debugLine="sRet = sRet & aLetrasPalabra(j).letra";
_sret = _sret+BA.ObjectToString(_vvvvvvvv7[_j].letra);
 }else {
 //BA.debugLineNum = 1777;BA.debugLine="sRet = sRet & \"_\"";
_sret = _sret+"_";
 };
 }
};
 //BA.debugLineNum = 1780;BA.debugLine="Return sRet";
if (true) return _sret;
 //BA.debugLineNum = 1781;BA.debugLine="End Sub";
return "";
}
public static int  _get_letrasposicion(String _sletra) throws Exception{
int _iret = 0;
int _j = 0;
 //BA.debugLineNum = 1308;BA.debugLine="Sub Get_LetrasPosicion(sLetra As String) As Int";
 //BA.debugLineNum = 1310;BA.debugLine="Dim iRet As Int = -1";
_iret = (int) (-1);
 //BA.debugLineNum = 1311;BA.debugLine="For j= 0 To aLetrasElegir.Length-1";
{
final int step2 = 1;
final int limit2 = (int) (_vvvvvvvv0.length-1);
for (_j = (int) (0) ; (step2 > 0 && _j <= limit2) || (step2 < 0 && _j >= limit2); _j = ((int)(0 + _j + step2)) ) {
 //BA.debugLineNum = 1312;BA.debugLine="If aLetrasElegir(j).LetraMovil = aLetrasElegir(j)";
if (_vvvvvvvv0[_j].LetraMovil==_vvvvvvvv0[_j].LetraSiempre) { 
 //BA.debugLineNum = 1314;BA.debugLine="If lblArrayLetras(j).Text= sLetra Then";
if ((mostCurrent._vvvvvvvvvvvvvvvvvvvv7[_j].getText()).equals(_sletra)) { 
 //BA.debugLineNum = 1315;BA.debugLine="iRet = j";
_iret = _j;
 //BA.debugLineNum = 1316;BA.debugLine="Exit";
if (true) break;
 };
 };
 }
};
 //BA.debugLineNum = 1320;BA.debugLine="Return iRet";
if (true) return _iret;
 //BA.debugLineNum = 1321;BA.debugLine="End Sub";
return 0;
}
public static String  _get_mensajeadivino(int _idpalabra) throws Exception{
String _sret = "";
int _ipos = 0;
String[] _amensajes = null;
 //BA.debugLineNum = 1367;BA.debugLine="Sub Get_MensajeAdivino (idPalabra As Int) As Strin";
 //BA.debugLineNum = 1369;BA.debugLine="Dim  sRet As String, iPos As Int";
_sret = "";
_ipos = 0;
 //BA.debugLineNum = 1370;BA.debugLine="Dim aMensajes () As String";
_amensajes = new String[(int) (0)];
java.util.Arrays.fill(_amensajes,"");
 //BA.debugLineNum = 1371;BA.debugLine="aMensajes = Array As String  (\"Correcto. \" &Chr(10";
_amensajes = new String[]{"Correcto. "+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Tu divergencia sigue aumentando","Acertaste. "+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Tus engranajes se van aflojando","Bien. "+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Ya estás con un pié fuera de la caja","Superluper. "+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Vas quitando herrumbre a tus neuronas","La pucha."+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Eres una barbaridad","Tremendo."+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Aunque ésta era fácil, pero la próxima te saca una cana","Revolución."+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Tus neuronas están como locas","Eres suertudo"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"o tu pensamiento lateral está muy desarrollado","Tu mate te está funcionando muy bien","Tienes el cerebro frito con aceite de lubricar motores","Patapufete."+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Si sigues adivinando se nos van a acabar las palabras","Sambomba."+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Se ha generado un nuevo circuito en tu cerebro","Caramba que vas muy bien. Apostaría que ya puedas descubrir la traducción combinada de TheySmokeDos","Sorprendente."+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Tienes una gran capacidad de abandonar la lógica. Tu pareja tiene razón","Te luciste."+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Estás cerca de graduarte en pensamiento divergente","Caracoles."+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Se nota que has practicado en el viaje","Cáspitas."+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Tu cociente intelectual debería llamarse multiplicador intelectual","Impactante."+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Debes tener un procesador de cuatro núcleos","Chanfle."+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Ya te mereces tu propia estatua del pensador","Excelente."+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Te ganaste un premio Eric de aplausos y campanadas"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Clap ton Clap ton","Yuuujuuu."+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Eres el primero en acertar esta palabra a esta hora en este lugar."+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Y con esas medias!","En breve tu cabeza quedará chica para la expansión que está sufriendo tu cerebro","Boing boing."+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Tus neuronas están dando saltos increíbles","Cáspitas."+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Eso fue muy rápido. Espero que estés jugando limpio. Sino ve a asearte","Soberbio."+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"El pitecantropus estaría impresionado con tu evolución","Lativa te queda chica."+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Tu performance es superlativa","Fantástico."+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Ya se debe estar gestando tu propio club de fans","Tu capacidad de resolución se está volviendo infinita","Fenómeno."+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Evidentemente tienes un ecosistema cerebral hiper desarrollado","Impresionante lo que has logrado este último tiempo","Recórcholis."+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"En cualquier momento empiezas a inventar tus propios matetes","Piedra libre."+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"El orden ayuda a encontrar y el caos a descubrir."+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Si, si, te ha ayudado el caos","Te mereces un anterior a lo que me pertenece."+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Claro Claro: Un premio","Patapufete."+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Tu tienes habilidad para salir de la caja pero por favor cierra la tapa para que no se escapen otros","Caramba."+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Tus habilidades matetísticas dejan paticonfuso a cualquiera","Tus neuronas están decididas a abandonar el blanco y negro","Matete está provocando una migración masiva de neuronas. La lógica convencional ya no tiene espacio en tu cerebro","Zás!"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Matete está reconfigurando tus neuronas","Upalalá"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"La parte colorida de tu cerebro parece un arcoiris","Santos Matetes Batman, este jugador podría ayudarnos a derrotar al Acertijo","Carambolas"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Resuelves muy bien los Matetes, podrías dedicarte a...."+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Lo siento, es una habilidad completamente inútil","Con tanto Matete ¿te parece que empiezas a sentir el movimiento de tus neuronas?"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Es extraño. Posiblemente tengas piojos","El ejercicio mental te rejuvenece. Cada matete resuelto te hace ver 39 segundos más joven.","Deberían nominarte para el premio intergaláctico de Matetes. Si existiera, claro.","Santos tragos barman!"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"A algunas personas Matete les hace crecer tanto las neuronas que le empujan el pelo hacia afuera y las confunden con el tío cosa.","Resolviste hábilmente este Matete, pero tengo que advertirte que el próximo produce insomnio.","Esferas de Caram!"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Logras sorprenderme con tus habilidades."+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+" Bah, no es cierto, pero te quería levantar el ánimo.","A la little ball."+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Hubiera jurado que esta la salteabas, me monja-encendiste.","Si pudiste resolver este Matete, el problemita que te está dando vueltas en la cabeza es pan comido.","Otro Matete resuelto es otro pequeño puente entre tus hemisferios cerebrales.","A la pipetuá! Deberían inventar un superheroe en tu nombre, algo así como Supermantete.","A esta altura del juego es posible que te descubras analizando tooodas las palabras buscando Matetes. El diagnóstico es Matetitis.","Acertaste y te ganaste un consejo: No pidas peras al olmo. Son mucho más ricas al borgoña.","Uii Uii."+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Estás haciendo rechinar la estantería de tu pensamiento lógico.","Cataplún! "+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Lógica lógica derrumbada. Bienvenida lógica ilógica."};
 //BA.debugLineNum = 1428;BA.debugLine="Try";
try { //BA.debugLineNum = 1430;BA.debugLine="iPos= idPalabra Mod aMensajes.Length";
_ipos = (int) (_idpalabra%_amensajes.length);
 //BA.debugLineNum = 1431;BA.debugLine="sRet = aMensajes(iPos)";
_sret = _amensajes[_ipos];
 } 
       catch (Exception e8) {
			processBA.setLastException(e8); //BA.debugLineNum = 1433;BA.debugLine="sRet = \"Correcto. \"&Chr(10)& \"Tu divergencia sigu";
_sret = "Correcto. "+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Tu divergencia sigue aumentando";
 };
 //BA.debugLineNum = 1435;BA.debugLine="Return sRet";
if (true) return _sret;
 //BA.debugLineNum = 1437;BA.debugLine="End Sub";
return "";
}
public static int  _get_monedaspornivel() throws Exception{
int _iret = 0;
 //BA.debugLineNum = 1439;BA.debugLine="Sub Get_MonedasPorNivel As Int";
 //BA.debugLineNum = 1441;BA.debugLine="Dim iRet As Int =10";
_iret = (int) (10);
 //BA.debugLineNum = 1443;BA.debugLine="Try";
try { //BA.debugLineNum = 1453;BA.debugLine="iRet = xtPalabra.palabra.Length";
_iret = _vvvvvvvvvvvvvvvvvv4.Palabra.length();
 } 
       catch (Exception e5) {
			processBA.setLastException(e5); //BA.debugLineNum = 1456;BA.debugLine="ManejaErrorJugar (\"Get_MonedasxNivel\", \"Error Set";
_vvvvvvvvvvvvvvvvvv3("Get_MonedasxNivel","Error Seteos");
 };
 //BA.debugLineNum = 1459;BA.debugLine="Return iRet";
if (true) return _iret;
 //BA.debugLineNum = 1461;BA.debugLine="End Sub";
return 0;
}
public static int  _get_nivelprimerapalabra(int _idnivel) throws Exception{
String _ssql = "";
anywheresoftware.b4a.sql.SQL.CursorWrapper _crcursor1 = null;
int _iret = 0;
 //BA.debugLineNum = 1706;BA.debugLine="Sub Get_NivelPrimeraPalabra(idNivel As Int) As Int";
 //BA.debugLineNum = 1708;BA.debugLine="Dim sSql As String, crCursor1 As Cursor, iRet As I";
_ssql = "";
_crcursor1 = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
_iret = (int) (-1);
 //BA.debugLineNum = 1709;BA.debugLine="Try";
try { //BA.debugLineNum = 1710;BA.debugLine="sSql =\"Select ifnull(min(idPalabra),-1) idPalabra";
_ssql = "Select ifnull(min(idPalabra),-1) idPalabra from Palabras where idNivel = "+BA.NumberToString(_idnivel);
 //BA.debugLineNum = 1711;BA.debugLine="crCursor1 = g_sqlBaseLocalJuego.ExecQuery(sSql)";
_crcursor1.setObject((android.database.Cursor)(_g_sqlbaselocaljuego.ExecQuery(_ssql)));
 //BA.debugLineNum = 1712;BA.debugLine="If crCursor1.RowCount >0 Then";
if (_crcursor1.getRowCount()>0) { 
 //BA.debugLineNum = 1713;BA.debugLine="crCursor1.Position = 0";
_crcursor1.setPosition((int) (0));
 //BA.debugLineNum = 1714;BA.debugLine="iRet = 	crCursor1.GetInt2(0)";
_iret = _crcursor1.GetInt2((int) (0));
 };
 //BA.debugLineNum = 1716;BA.debugLine="crCursor1.close";
_crcursor1.Close();
 } 
       catch (Exception e11) {
			processBA.setLastException(e11); //BA.debugLineNum = 1718;BA.debugLine="ManejaErrorJugar(\"Get_NivelPrimeraLetra\", LastExc";
_vvvvvvvvvvvvvvvvvv3("Get_NivelPrimeraLetra",anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage());
 };
 //BA.debugLineNum = 1720;BA.debugLine="Return iRet";
if (true) return _iret;
 //BA.debugLineNum = 1721;BA.debugLine="End Sub";
return 0;
}
public static int  _get_nivelultimapalabra(int _idnivel) throws Exception{
String _ssql = "";
anywheresoftware.b4a.sql.SQL.CursorWrapper _crcursor1 = null;
int _iret = 0;
 //BA.debugLineNum = 1723;BA.debugLine="Sub Get_NivelUltimaPalabra(idNivel As Int) As Int";
 //BA.debugLineNum = 1725;BA.debugLine="Dim sSql As String, crCursor1 As Cursor, iRet As I";
_ssql = "";
_crcursor1 = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
_iret = (int) (-1);
 //BA.debugLineNum = 1726;BA.debugLine="Try";
try { //BA.debugLineNum = 1727;BA.debugLine="sSql =\"Select ifnull(max(idPalabra),-1) idPalabra";
_ssql = "Select ifnull(max(idPalabra),-1) idPalabra from Palabras where idNivel = "+BA.NumberToString(_idnivel);
 //BA.debugLineNum = 1728;BA.debugLine="crCursor1 = g_sqlBaseLocalJuego.ExecQuery(sSql)";
_crcursor1.setObject((android.database.Cursor)(_g_sqlbaselocaljuego.ExecQuery(_ssql)));
 //BA.debugLineNum = 1729;BA.debugLine="If crCursor1.RowCount >0 Then";
if (_crcursor1.getRowCount()>0) { 
 //BA.debugLineNum = 1730;BA.debugLine="crCursor1.Position = 0";
_crcursor1.setPosition((int) (0));
 //BA.debugLineNum = 1731;BA.debugLine="iRet = 	crCursor1.GetInt2(0)";
_iret = _crcursor1.GetInt2((int) (0));
 };
 //BA.debugLineNum = 1733;BA.debugLine="crCursor1.close";
_crcursor1.Close();
 } 
       catch (Exception e11) {
			processBA.setLastException(e11); //BA.debugLineNum = 1735;BA.debugLine="ManejaErrorJugar(\"Get_NivelPrimeraLetra\", LastExc";
_vvvvvvvvvvvvvvvvvv3("Get_NivelPrimeraLetra",anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage());
 };
 //BA.debugLineNum = 1737;BA.debugLine="Return iRet";
if (true) return _iret;
 //BA.debugLineNum = 1738;BA.debugLine="End Sub";
return 0;
}
public static int  _get_posicionenpalabra(anywheresoftware.b4a.objects.LabelWrapper _lblpalabra) throws Exception{
int _iret = 0;
int _j = 0;
 //BA.debugLineNum = 1565;BA.debugLine="Sub Get_PosicionEnPalabra(lblPalabra As Label) As";
 //BA.debugLineNum = 1566;BA.debugLine="Dim iRet As Int=-1";
_iret = (int) (-1);
 //BA.debugLineNum = 1568;BA.debugLine="For j = 0 To lblArrayLetras.Length-1";
{
final int step2 = 1;
final int limit2 = (int) (mostCurrent._vvvvvvvvvvvvvvvvvvvv7.length-1);
for (_j = (int) (0) ; (step2 > 0 && _j <= limit2) || (step2 < 0 && _j >= limit2); _j = ((int)(0 + _j + step2)) ) {
 //BA.debugLineNum = 1569;BA.debugLine="If lblArrayLetras(j).Tag = lblPalabra.tag Then";
if ((mostCurrent._vvvvvvvvvvvvvvvvvvvv7[_j].getTag()).equals(_lblpalabra.getTag())) { 
 //BA.debugLineNum = 1570;BA.debugLine="If lblArrayLetras(j).Text<>\"\" Then";
if ((mostCurrent._vvvvvvvvvvvvvvvvvvvv7[_j].getText()).equals("") == false) { 
 //BA.debugLineNum = 1571;BA.debugLine="iRet = j";
_iret = _j;
 };
 //BA.debugLineNum = 1573;BA.debugLine="Exit";
if (true) break;
 };
 }
};
 //BA.debugLineNum = 1576;BA.debugLine="Return iRet";
if (true) return _iret;
 //BA.debugLineNum = 1577;BA.debugLine="End Sub";
return 0;
}
public static boolean  _get_quedanpalabrasalmacen() throws Exception{
boolean _bret = false;
 //BA.debugLineNum = 4219;BA.debugLine="Sub Get_QuedanPalabrasAlmacen As Boolean";
 //BA.debugLineNum = 4220;BA.debugLine="Dim bRet As Boolean";
_bret = false;
 //BA.debugLineNum = 4221;BA.debugLine="If Publicos.get_CantidadPalabrasAlmacen(g_sqlBase";
if (mostCurrent._vvvvvvvvvvvvvvv7._get_cantidadpalabrasalmacen(mostCurrent.activityBA,_g_sqlbaselocaljuego,anywheresoftware.b4a.keywords.Common.True)>mostCurrent._vvvvvvvvvvvvvvv7._get_cantidadpalabras(mostCurrent.activityBA,_g_sqlbaselocaljuego)) { 
 //BA.debugLineNum = 4222;BA.debugLine="bRet = True";
_bret = anywheresoftware.b4a.keywords.Common.True;
 };
 //BA.debugLineNum = 4224;BA.debugLine="Return bRet";
if (true) return _bret;
 //BA.debugLineNum = 4225;BA.debugLine="End Sub";
return false;
}
public static int  _get_separaletra() throws Exception{
int _iret = 0;
 //BA.debugLineNum = 1694;BA.debugLine="Sub Get_SeparaLetra As Int";
 //BA.debugLineNum = 1695;BA.debugLine="Dim iRet As Int";
_iret = 0;
 //BA.debugLineNum = 1696;BA.debugLine="iRet = 2dip";
_iret = anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (2));
 //BA.debugLineNum = 1697;BA.debugLine="Return iRet";
if (true) return _iret;
 //BA.debugLineNum = 1698;BA.debugLine="End Sub";
return 0;
}
public static int  _get_textsizeaux(String _smensaje,int _iw,int _ih) throws Exception{
 //BA.debugLineNum = 3188;BA.debugLine="Sub Get_TextSizeAux (sMensaje As String, iW As Int";
 //BA.debugLineNum = 3189;BA.debugLine="lblCalcTextSize.width = iW";
mostCurrent._lblcalctextsize.setWidth(_iw);
 //BA.debugLineNum = 3190;BA.debugLine="lblCalcTextSize.Height = iH";
mostCurrent._lblcalctextsize.setHeight(_ih);
 //BA.debugLineNum = 3191;BA.debugLine="Publicos.SetLabelTextSize(lblCalcTextSize, sMensa";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv4(mostCurrent.activityBA,mostCurrent._lblcalctextsize,_smensaje,(float) (40),(float) (5),(int) (100));
 //BA.debugLineNum = 3193;BA.debugLine="Return lblCalcTextSize.textsize";
if (true) return (int) (mostCurrent._lblcalctextsize.getTextSize());
 //BA.debugLineNum = 3194;BA.debugLine="End Sub";
return 0;
}
public static int  _vvvvvvvvvvvvvvvvvvvvv6() throws Exception{
String _ssql = "";
int _iret = 0;
anywheresoftware.b4a.sql.SQL.CursorWrapper _xcursor = null;
 //BA.debugLineNum = 1043;BA.debugLine="Sub GetCantidadPistasPendientes() As Int";
 //BA.debugLineNum = 1044;BA.debugLine="Dim sSql As String, iRet As Int=0, xCursor As Curs";
_ssql = "";
_iret = (int) (0);
_xcursor = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
 //BA.debugLineNum = 1046;BA.debugLine="Try";
try { //BA.debugLineNum = 1047;BA.debugLine="sSql =\"Select count(1) cta from pistas where pala";
_ssql = "Select count(1) cta from pistas where palabraid = "+BA.NumberToString(_vvvvvvvv1)+" and comprada=0 and gratis =0";
 //BA.debugLineNum = 1048;BA.debugLine="xCursor = g_sqlBaseLocalJuego.ExecQuery(sSql)";
_xcursor.setObject((android.database.Cursor)(_g_sqlbaselocaljuego.ExecQuery(_ssql)));
 //BA.debugLineNum = 1050;BA.debugLine="If xCursor.RowCount >0 Then";
if (_xcursor.getRowCount()>0) { 
 //BA.debugLineNum = 1051;BA.debugLine="xCursor.Position=0";
_xcursor.setPosition((int) (0));
 //BA.debugLineNum = 1052;BA.debugLine="iRet = xCursor.GetInt2(0)";
_iret = _xcursor.GetInt2((int) (0));
 };
 //BA.debugLineNum = 1054;BA.debugLine="xCursor.Close";
_xcursor.Close();
 } 
       catch (Exception e11) {
			processBA.setLastException(e11); //BA.debugLineNum = 1056;BA.debugLine="ManejaErrorJugar (\"GetCantidadPistasPendientes\",\"";
_vvvvvvvvvvvvvvvvvv3("GetCantidadPistasPendientes","Error en Pistas");
 };
 //BA.debugLineNum = 1058;BA.debugLine="Return iRet";
if (true) return _iret;
 //BA.debugLineNum = 1059;BA.debugLine="End Sub";
return 0;
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 133;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 139;BA.debugLine="Dim scvMain As ScrollView";
mostCurrent._vvvvvvvvvvvvvvvvvvvvv7 = new anywheresoftware.b4a.objects.ScrollViewWrapper();
 //BA.debugLineNum = 140;BA.debugLine="Dim gt_Color As tColores";
mostCurrent._gt_color = new com.matetejuego.free.publicos._tcolores();
 //BA.debugLineNum = 145;BA.debugLine="Dim avpnlItemP () As Panel";
mostCurrent._vvvvvvvvvvvvvvvvvvvvv0 = new anywheresoftware.b4a.objects.PanelWrapper[(int) (0)];
{
int d0 = mostCurrent._vvvvvvvvvvvvvvvvvvvvv0.length;
for (int i0 = 0;i0 < d0;i0++) {
mostCurrent._vvvvvvvvvvvvvvvvvvvvv0[i0] = new anywheresoftware.b4a.objects.PanelWrapper();
}
}
;
 //BA.debugLineNum = 149;BA.debugLine="Dim lblAyuda As Label";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv1 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 150;BA.debugLine="Dim sm As SlideMenu";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv2 = new com.matetejuego.free.slidemenu();
 //BA.debugLineNum = 152;BA.debugLine="Dim SONIDO_ADIVINO As Int= 1, SONIDO_ERROR As Int";
_sonido_adivino = (int) (1);
_sonido_error = (int) (2);
_sonido_nuevapalabra = (int) (3);
 //BA.debugLineNum = 153;BA.debugLine="Dim sound_Adivino As SoundPool, iSound_Adivino As";
mostCurrent._sound_adivino = new anywheresoftware.b4a.audio.SoundPoolWrapper();
_isound_adivino = 0;
 //BA.debugLineNum = 154;BA.debugLine="Dim sound_Error As SoundPool, iSound_Error As Int";
mostCurrent._sound_error = new anywheresoftware.b4a.audio.SoundPoolWrapper();
_isound_error = 0;
 //BA.debugLineNum = 155;BA.debugLine="Dim sound_NuevaPalabra As SoundPool, iSound_Nueva";
mostCurrent._sound_nuevapalabra = new anywheresoftware.b4a.audio.SoundPoolWrapper();
_isound_nuevapalabra = 0;
 //BA.debugLineNum = 158;BA.debugLine="Dim ThisActivity As SocialApiActivity";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv3 = new com.datasteam.b4a.socialapi.BaseProviderActivity();
 //BA.debugLineNum = 159;BA.debugLine="Private lblFacebook As Label";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv4 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 164;BA.debugLine="Dim iClickVacio As Int";
_vvvvvvvvvvvvvvvvvvvvvv5 = 0;
 //BA.debugLineNum = 167;BA.debugLine="Dim lblArrayLetras() As Label  'array donde guard";
mostCurrent._vvvvvvvvvvvvvvvvvvvv7 = new anywheresoftware.b4a.objects.LabelWrapper[(int) (0)];
{
int d0 = mostCurrent._vvvvvvvvvvvvvvvvvvvv7.length;
for (int i0 = 0;i0 < d0;i0++) {
mostCurrent._vvvvvvvvvvvvvvvvvvvv7[i0] = new anywheresoftware.b4a.objects.LabelWrapper();
}
}
;
 //BA.debugLineNum = 168;BA.debugLine="Dim lblArrayPalabra() As Label ' array donde guar";
mostCurrent._vvvvvvvvvvvvvvvvvvv1 = new anywheresoftware.b4a.objects.LabelWrapper[(int) (0)];
{
int d0 = mostCurrent._vvvvvvvvvvvvvvvvvvv1.length;
for (int i0 = 0;i0 < d0;i0++) {
mostCurrent._vvvvvvvvvvvvvvvvvvv1[i0] = new anywheresoftware.b4a.objects.LabelWrapper();
}
}
;
 //BA.debugLineNum = 171;BA.debugLine="Private lblBloqueo As Label";
mostCurrent._vvvvvvvvvvvvvvvvvvv4 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 175;BA.debugLine="Dim adBC As LeadBoltB4A ' 664913187";
mostCurrent._vvvvvvvvvvvvvvvvvv1 = new com.leadbolt.b4a.LeadBoltB4A();
 //BA.debugLineNum = 176;BA.debugLine="Dim adBGris As LeadBoltB4A ' 463221194";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv6 = new com.leadbolt.b4a.LeadBoltB4A();
 //BA.debugLineNum = 177;BA.debugLine="Dim adInt As LeadBoltB4A '703320557 'Dim adWall A";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv7 = new com.leadbolt.b4a.LeadBoltB4A();
 //BA.debugLineNum = 192;BA.debugLine="Dim adDispIO As ioDisplay";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv0 = new io.displayb4a.iodisplayWrapper();
 //BA.debugLineNum = 195;BA.debugLine="Dim g_iMonedas As Int, g_iPuntos As Int, g_iJugad";
_g_imonedas = 0;
_g_ipuntos = 0;
_g_ijugadas = 0;
 //BA.debugLineNum = 196;BA.debugLine="Dim g_iPalabras As Int";
_g_ipalabras = 0;
 //BA.debugLineNum = 198;BA.debugLine="Private lblAviso As Label";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvvv1 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 199;BA.debugLine="Private imgAvance As ImageView";
mostCurrent._imgavance = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 200;BA.debugLine="Private imgMenu As ImageView";
mostCurrent._imgmenu = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 201;BA.debugLine="Private imgNeuronas As ImageView";
mostCurrent._imgneuronas = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 202;BA.debugLine="Private lblAvance As Label";
mostCurrent._lblavance = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 203;BA.debugLine="Private lblNeuronas As Label";
mostCurrent._lblneuronas = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 205;BA.debugLine="Private lblV2Nivel As Label";
mostCurrent._lblv2nivel = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 206;BA.debugLine="Private Panel1 As Panel";
mostCurrent._panel1 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 207;BA.debugLine="Public Panel2 As Panel";
mostCurrent._panel2 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 208;BA.debugLine="Private Panel3 As Panel";
mostCurrent._panel3 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 209;BA.debugLine="Private Panel4 As Panel";
mostCurrent._panel4 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 210;BA.debugLine="Private Panel5 As Panel";
mostCurrent._panel5 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 211;BA.debugLine="Private Panel6 As Panel";
mostCurrent._panel6 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 214;BA.debugLine="Private imgPedirLetra As ImageView";
mostCurrent._imgpedirletra = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 215;BA.debugLine="Private imgSaltarPalabra As ImageView";
mostCurrent._imgsaltarpalabra = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 216;BA.debugLine="Private imgCompartir As ImageView";
mostCurrent._imgcompartir = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 217;BA.debugLine="Private lblPedirLetra As Label";
mostCurrent._lblpedirletra = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 218;BA.debugLine="Private lblSaltarPalabra As Label";
mostCurrent._lblsaltarpalabra = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 220;BA.debugLine="Private lblv2Def As Label";
mostCurrent._lblv2def = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 221;BA.debugLine="Private Panel51 As Panel";
mostCurrent._panel51 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 222;BA.debugLine="Private Panel41 As Panel";
mostCurrent._panel41 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 223;BA.debugLine="Private Panel61 As Panel";
mostCurrent._panel61 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 224;BA.debugLine="Private lbl41 As Label";
mostCurrent._lbl41 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 225;BA.debugLine="Private lbl51 As Label";
mostCurrent._lbl51 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 226;BA.debugLine="Private lbl61 As Label";
mostCurrent._lbl61 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 227;BA.debugLine="Private Panel21 As Panel";
mostCurrent._panel21 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 228;BA.debugLine="Private lbl21 As Label";
mostCurrent._lbl21 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 229;BA.debugLine="Private lblCompartir As Label";
mostCurrent._lblcompartir = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 232;BA.debugLine="Dim AnimPlus As AnimationPlus";
mostCurrent._vvvvvvvvvvvvvvvv0 = new flm.b4a.animationplus.AnimationPlusWrapper();
 //BA.debugLineNum = 234;BA.debugLine="Private imgAnimacion As ImageView";
mostCurrent._imganimacion = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 235;BA.debugLine="Private img51 As ImageView";
mostCurrent._img51 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 236;BA.debugLine="Private img51Facebook As ImageView";
mostCurrent._img51facebook = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 237;BA.debugLine="Private img51Twitter As ImageView";
mostCurrent._img51twitter = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 239;BA.debugLine="Private lblMenuTitulo As Label";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvvv2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 240;BA.debugLine="Private lbl51Facebook As Label";
mostCurrent._lbl51facebook = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 241;BA.debugLine="Private lbl51Twitter As Label";
mostCurrent._lbl51twitter = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 242;BA.debugLine="Private imgBajarLetras As ImageView";
mostCurrent._imgbajarletras = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 243;BA.debugLine="Private lblBajarLetras As Label";
mostCurrent._lblbajarletras = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 244;BA.debugLine="Private lbl51MensajeMatete As Label";
mostCurrent._lbl51mensajematete = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 247;BA.debugLine="Dim lblCalcTextSize As Label";
mostCurrent._lblcalctextsize = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 249;BA.debugLine="Dim scrViewAyuda As ScrollView";
mostCurrent._scrviewayuda = new anywheresoftware.b4a.objects.ScrollViewWrapper();
 //BA.debugLineNum = 251;BA.debugLine="Private imgShadow As ImageView";
mostCurrent._imgshadow = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 252;BA.debugLine="Private Panel11 As Panel";
mostCurrent._panel11 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 253;BA.debugLine="Private lbl11 As Label";
mostCurrent._lbl11 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 255;BA.debugLine="Private imgLoading As ImageView";
mostCurrent._imgloading = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 256;BA.debugLine="Private pnlLoading As Panel";
mostCurrent._pnlloading = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 257;BA.debugLine="Private lblLoading As Label";
mostCurrent._lblloading = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 258;BA.debugLine="Private pnlInvisible As Panel";
mostCurrent._pnlinvisible = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 259;BA.debugLine="Private lblPedirLetraCosto As Label";
mostCurrent._lblpedirletracosto = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 260;BA.debugLine="Private lblSaltarPalabraCosto As Label";
mostCurrent._lblsaltarpalabracosto = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 261;BA.debugLine="Private pnlHistoria As Panel";
mostCurrent._pnlhistoria = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 262;BA.debugLine="Private pnlHistFiltro As Panel";
mostCurrent._pnlhistfiltro = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 263;BA.debugLine="Private lblFiltro As Label";
mostCurrent._lblfiltro = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 264;BA.debugLine="End Sub";
return "";
}
public static String  _historia_llenascroll(anywheresoftware.b4a.objects.PanelWrapper _pnlscroll,boolean _bsaltadas,String _sdireccion) throws Exception{
anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _bitmap1 = null;
int _icantpalabras = 0;
anywheresoftware.b4a.objects.PanelWrapper _panel0 = null;
int _paneltop = 0;
int _panelheight = 0;
int _label2top = 0;
int _progressbartop = 0;
int _progressbarwidth = 0;
int _panelwidth = 0;
int _labelheight = 0;
int _labelwidth = 0;
int _progress = 0;
int _progressbar1top = 0;
int _progressbarheight = 0;
String _sand = "";
int _ipanelscrollheight = 0;
int _icantregistrosmuestra = 0;
String _ssql = "";
anywheresoftware.b4a.sql.SQL.CursorWrapper _cursor1 = null;
anywheresoftware.b4a.sql.SQL.CursorWrapper _crdef = null;
anywheresoftware.b4a.objects.LabelWrapper _labelsize = null;
int _i = 0;
int _ilimitesuperior = 0;
int _ilimiteinferior = 0;
com.matetejuego.free.publicos._thistoria[] _xhistoria = null;
int _icursor = 0;
anywheresoftware.b4a.objects.PanelWrapper _xpanelitem = null;
anywheresoftware.b4a.objects.ImageViewWrapper _imageview1 = null;
anywheresoftware.b4a.objects.PanelWrapper _panelitem = null;
com.matetejuego.free.publicos._thistoria _xh = null;
 //BA.debugLineNum = 4332;BA.debugLine="Sub Historia_llenaScroll(pnlScroll As Panel, bSalt";
 //BA.debugLineNum = 4333;BA.debugLine="Dim Bitmap1 As Bitmap, iCantPalabras As Int, Pane";
_bitmap1 = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
_icantpalabras = 0;
_panel0 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 4334;BA.debugLine="Dim PanelTop, PanelHeight, Label2Top, ProgressBar";
_paneltop = 0;
_panelheight = 0;
_label2top = 0;
_progressbartop = 0;
_progressbarwidth = 0;
_panelwidth = 0;
 //BA.debugLineNum = 4335;BA.debugLine="Dim labelHeight As Int, labelWidth As Int";
_labelheight = 0;
_labelwidth = 0;
 //BA.debugLineNum = 4336;BA.debugLine="Dim Progress As Int, ProgressBar1Top As Int, Prog";
_progress = 0;
_progressbar1top = 0;
_progressbarheight = 0;
 //BA.debugLineNum = 4337;BA.debugLine="Dim sAnd As String, iPanelScrollHeight As Int = A";
_sand = "";
_ipanelscrollheight = (int) (mostCurrent._activity.getHeight()-(mostCurrent._lbl11.getTop()+mostCurrent._lbl11.getHeight())-(mostCurrent._activity.getHeight()-mostCurrent._panel61.getTop()));
 //BA.debugLineNum = 4338;BA.debugLine="Dim iCantRegistrosMuestra As Int = 20 'cantidad d";
_icantregistrosmuestra = (int) (20);
 //BA.debugLineNum = 4341;BA.debugLine="If scvMain.IsInitialized Then";
if (mostCurrent._vvvvvvvvvvvvvvvvvvvvv7.IsInitialized()) { 
 //BA.debugLineNum = 4342;BA.debugLine="scvMain.RemoveView";
mostCurrent._vvvvvvvvvvvvvvvvvvvvv7.RemoveView();
 };
 //BA.debugLineNum = 4344;BA.debugLine="scvMain.Initialize(500)";
mostCurrent._vvvvvvvvvvvvvvvvvvvvv7.Initialize(mostCurrent.activityBA,(int) (500));
 //BA.debugLineNum = 4347;BA.debugLine="scvMain.Panel.RemoveAllViews";
mostCurrent._vvvvvvvvvvvvvvvvvvvvv7.getPanel().RemoveAllViews();
 //BA.debugLineNum = 4350;BA.debugLine="pnlScroll.AddView(scvMain,0,pnlHistFiltro.Height,";
_pnlscroll.AddView((android.view.View)(mostCurrent._vvvvvvvvvvvvvvvvvvvvv7.getObject()),(int) (0),mostCurrent._pnlhistfiltro.getHeight(),_pnlscroll.getWidth(),_ipanelscrollheight);
 //BA.debugLineNum = 4351;BA.debugLine="scvMain.Panel.Width = pnlScroll.Width";
mostCurrent._vvvvvvvvvvvvvvvvvvvvv7.getPanel().setWidth(_pnlscroll.getWidth());
 //BA.debugLineNum = 4352;BA.debugLine="scvMain.Panel.Height = iPanelScrollHeight";
mostCurrent._vvvvvvvvvvvvvvvvvvvvv7.getPanel().setHeight(_ipanelscrollheight);
 //BA.debugLineNum = 4353;BA.debugLine="scvMain.Color = Colors.white";
mostCurrent._vvvvvvvvvvvvvvvvvvvvv7.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 4356;BA.debugLine="Panel0=scvMain.Panel";
_panel0 = mostCurrent._vvvvvvvvvvvvvvvvvvvvv7.getPanel();
 //BA.debugLineNum = 4357;BA.debugLine="Panel0.Color=Colors.white";
_panel0.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 4358;BA.debugLine="pnlHistoria.Color = Colors.white";
mostCurrent._pnlhistoria.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 4360;BA.debugLine="PanelTop=0";
_paneltop = (int) (0);
 //BA.debugLineNum = 4361;BA.debugLine="PanelWidth = scvMain.Width";
_panelwidth = mostCurrent._vvvvvvvvvvvvvvvvvvvvv7.getWidth();
 //BA.debugLineNum = 4362;BA.debugLine="PanelHeight = iPanelScrollHeight*0.2";
_panelheight = (int) (_ipanelscrollheight*0.2);
 //BA.debugLineNum = 4363;BA.debugLine="labelWidth = PanelWidth * 0.75";
_labelwidth = (int) (_panelwidth*0.75);
 //BA.debugLineNum = 4364;BA.debugLine="labelHeight = PanelHeight * 0.7";
_labelheight = (int) (_panelheight*0.7);
 //BA.debugLineNum = 4365;BA.debugLine="ProgressBar1Top= PanelHeight*0.8";
_progressbar1top = (int) (_panelheight*0.8);
 //BA.debugLineNum = 4366;BA.debugLine="ProgressBarWidth=labelWidth";
_progressbarwidth = _labelwidth;
 //BA.debugLineNum = 4367;BA.debugLine="ProgressBarHeight = PanelHeight* 0.1";
_progressbarheight = (int) (_panelheight*0.1);
 //BA.debugLineNum = 4370;BA.debugLine="lblFiltro.TextSize = lbl11.TextSize -2";
mostCurrent._lblfiltro.setTextSize((float) (mostCurrent._lbl11.getTextSize()-2));
 //BA.debugLineNum = 4374;BA.debugLine="Dim sSql As String, Cursor1 As Cursor, crDef As C";
_ssql = "";
_cursor1 = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
_crdef = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
 //BA.debugLineNum = 4375;BA.debugLine="If bSaltadas Then";
if (_bsaltadas) { 
 //BA.debugLineNum = 4376;BA.debugLine="Select sDireccion";
switch (BA.switchObjectToInt(_sdireccion,"ABAJO","ARRIBA")) {
case 0: {
 //BA.debugLineNum = 4378;BA.debugLine="sSql =\"Select idPalabra, adivinada, salteada fr";
_ssql = "Select idPalabra, adivinada, salteada from avance where fin is not null and salteada = 1 and idPalabra>"+BA.NumberToString(_gi_historia_p_abajo)+" order by idPalabra limit "+BA.NumberToString((_icantregistrosmuestra+1));
 break; }
case 1: {
 //BA.debugLineNum = 4380;BA.debugLine="sSql =\"Select idPalabra, adivinada, salteada fr";
_ssql = "Select idPalabra, adivinada, salteada from avance where fin is not null and salteada = 1 and idPalabra<"+BA.NumberToString(_gi_historia_p_arriba)+" order by idPalabra DESC limit "+BA.NumberToString((_icantregistrosmuestra+1));
 break; }
default: {
 //BA.debugLineNum = 4382;BA.debugLine="sSql =\"Select idPalabra, adivinada, salteada fr";
_ssql = "Select idPalabra, adivinada, salteada from avance where fin is not null and salteada = 1 and idPalabra>="+BA.NumberToString(_gi_historia_p_arriba)+" order by idPalabra limit "+BA.NumberToString((_icantregistrosmuestra+1));
 break; }
}
;
 }else {
 //BA.debugLineNum = 4385;BA.debugLine="Select sDireccion";
switch (BA.switchObjectToInt(_sdireccion,"ABAJO","ARRIBA")) {
case 0: {
 //BA.debugLineNum = 4387;BA.debugLine="sSql =\"Select idPalabra, adivinada, salteada fr";
_ssql = "Select idPalabra, adivinada, salteada from avance where fin is not null and idPalabra>"+BA.NumberToString(_gi_historia_p_abajo)+" order by idPalabra limit "+BA.NumberToString((_icantregistrosmuestra+1));
 break; }
case 1: {
 //BA.debugLineNum = 4389;BA.debugLine="sSql =\"Select idPalabra, adivinada, salteada fr";
_ssql = "Select idPalabra, adivinada, salteada from avance where fin is not null and idPalabra<"+BA.NumberToString(_gi_historia_p_arriba)+" order by idPalabra DESC limit "+BA.NumberToString((_icantregistrosmuestra+1));
 break; }
default: {
 //BA.debugLineNum = 4391;BA.debugLine="sSql =\"Select idPalabra, adivinada, salteada fr";
_ssql = "Select idPalabra, adivinada, salteada from avance where fin is not null and idPalabra>="+BA.NumberToString(_gi_historia_p_arriba)+" order by idPalabra limit "+BA.NumberToString((_icantregistrosmuestra+1));
 break; }
}
;
 };
 //BA.debugLineNum = 4396;BA.debugLine="Cursor1 = g_sqlBaseLocalUsuario.ExecQuery(sSql)";
_cursor1.setObject((android.database.Cursor)(_g_sqlbaselocalusuario.ExecQuery(_ssql)));
 //BA.debugLineNum = 4398;BA.debugLine="Dim labelSize As Label , i As Int, iLimiteSuperio";
_labelsize = new anywheresoftware.b4a.objects.LabelWrapper();
_i = 0;
_ilimitesuperior = 0;
_ilimiteinferior = 0;
 //BA.debugLineNum = 4399;BA.debugLine="labelSize.Initialize(\"\")";
_labelsize.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 4400;BA.debugLine="pnlScroll.AddView(labelSize,0,0, labelWidth, labe";
_pnlscroll.AddView((android.view.View)(_labelsize.getObject()),(int) (0),(int) (0),_labelwidth,_labelheight);
 //BA.debugLineNum = 4403;BA.debugLine="If Cursor1.RowCount >0 Then";
if (_cursor1.getRowCount()>0) { 
 //BA.debugLineNum = 4410;BA.debugLine="If Cursor1.RowCount -1 < iCantRegistrosMuestra T";
if (_cursor1.getRowCount()-1<_icantregistrosmuestra) { 
 //BA.debugLineNum = 4411;BA.debugLine="iLimiteSuperior = Cursor1.RowCount -1";
_ilimitesuperior = (int) (_cursor1.getRowCount()-1);
 }else {
 //BA.debugLineNum = 4413;BA.debugLine="iLimiteSuperior = iCantRegistrosMuestra -1";
_ilimitesuperior = (int) (_icantregistrosmuestra-1);
 };
 //BA.debugLineNum = 4415;BA.debugLine="If sDireccion = \"ABAJO\" Or sDireccion = \"IGUAL\"";
if ((_sdireccion).equals("ABAJO") || (_sdireccion).equals("IGUAL")) { 
 //BA.debugLineNum = 4416;BA.debugLine="iLimiteInferior = 0";
_ilimiteinferior = (int) (0);
 //BA.debugLineNum = 4417;BA.debugLine="Dim xHistoria(iLimiteSuperior+1) As tHistoria '";
_xhistoria = new com.matetejuego.free.publicos._thistoria[(int) (_ilimitesuperior+1)];
{
int d0 = _xhistoria.length;
for (int i0 = 0;i0 < d0;i0++) {
_xhistoria[i0] = new com.matetejuego.free.publicos._thistoria();
}
}
;
 }else {
 //BA.debugLineNum = 4420;BA.debugLine="iLimiteInferior = iLimiteSuperior";
_ilimiteinferior = _ilimitesuperior;
 //BA.debugLineNum = 4421;BA.debugLine="iLimiteSuperior = 0";
_ilimitesuperior = (int) (0);
 //BA.debugLineNum = 4422;BA.debugLine="Dim xHistoria(iLimiteInferior+1) As tHistoria '";
_xhistoria = new com.matetejuego.free.publicos._thistoria[(int) (_ilimiteinferior+1)];
{
int d0 = _xhistoria.length;
for (int i0 = 0;i0 < d0;i0++) {
_xhistoria[i0] = new com.matetejuego.free.publicos._thistoria();
}
}
;
 };
 //BA.debugLineNum = 4425;BA.debugLine="Dim icursor As Int=0";
_icursor = (int) (0);
 //BA.debugLineNum = 4426;BA.debugLine="i=iLimiteInferior";
_i = _ilimiteinferior;
 //BA.debugLineNum = 4428;BA.debugLine="Do While icursor <= iLimiteInferior+iLimiteSuper";
while (_icursor<=_ilimiteinferior+_ilimitesuperior) {
 //BA.debugLineNum = 4429;BA.debugLine="Cursor1.Position = icursor";
_cursor1.setPosition(_icursor);
 //BA.debugLineNum = 4430;BA.debugLine="icursor = icursor+1";
_icursor = (int) (_icursor+1);
 //BA.debugLineNum = 4434;BA.debugLine="crDef = g_sqlBaseLocalJuego.ExecQuery(\"Select d";
_crdef.setObject((android.database.Cursor)(_g_sqlbaselocaljuego.ExecQuery("Select descripcion from palabras where idpalabra= "+BA.NumberToString(_cursor1.GetInt2((int) (0))))));
 //BA.debugLineNum = 4435;BA.debugLine="If crDef.RowCount > 0 Then";
if (_crdef.getRowCount()>0) { 
 //BA.debugLineNum = 4436;BA.debugLine="crDef.Position=0";
_crdef.setPosition((int) (0));
 //BA.debugLineNum = 4437;BA.debugLine="xHistoria(i).idPalabra = Cursor1.GetInt2(0)";
_xhistoria[_i].idPalabra = _cursor1.GetInt2((int) (0));
 //BA.debugLineNum = 4438;BA.debugLine="xHistoria(i).Adivinada = Cursor1.GetInt2(1)";
_xhistoria[_i].Adivinada = _cursor1.GetInt2((int) (1));
 //BA.debugLineNum = 4439;BA.debugLine="xHistoria(i).Salteada = Cursor1.GetInt2(2)";
_xhistoria[_i].Salteada = _cursor1.GetInt2((int) (2));
 //BA.debugLineNum = 4440;BA.debugLine="xHistoria(i).Definicion = crDef.GetString2(0)";
_xhistoria[_i].Definicion = _crdef.GetString2((int) (0));
 //BA.debugLineNum = 4441;BA.debugLine="labelSize.Text = xHistoria(i).Definicion.Trim";
_labelsize.setText((Object)(_xhistoria[_i].Definicion.trim()));
 //BA.debugLineNum = 4442;BA.debugLine="Publicos.SetLabelTextSize(labelSize, labelSize";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv4(mostCurrent.activityBA,_labelsize,_labelsize.getText(),(float) (18),(float) (4),(int) (100));
 //BA.debugLineNum = 4443;BA.debugLine="If labelSize.TextSize < gi_Historia_TextSize T";
if (_labelsize.getTextSize()<_gi_historia_textsize) { 
 //BA.debugLineNum = 4444;BA.debugLine="gi_Historia_TextSize = labelSize.TextSize";
_gi_historia_textsize = (int) (_labelsize.getTextSize());
 };
 };
 //BA.debugLineNum = 4447;BA.debugLine="crDef.Close";
_crdef.Close();
 //BA.debugLineNum = 4448;BA.debugLine="If iLimiteSuperior>iLimiteInferior Then 'ordena";
if (_ilimitesuperior>_ilimiteinferior) { 
 //BA.debugLineNum = 4449;BA.debugLine="i=i+1";
_i = (int) (_i+1);
 }else {
 //BA.debugLineNum = 4451;BA.debugLine="i=i-1";
_i = (int) (_i-1);
 };
 }
;
 //BA.debugLineNum = 4456;BA.debugLine="labelSize.RemoveView";
_labelsize.RemoveView();
 //BA.debugLineNum = 4458;BA.debugLine="If iLimiteSuperior>iLimiteInferior Then";
if (_ilimitesuperior>_ilimiteinferior) { 
 //BA.debugLineNum = 4459;BA.debugLine="gi_Historia_P_Abajo = xHistoria(iLimiteSuperior";
_gi_historia_p_abajo = _xhistoria[_ilimitesuperior].idPalabra;
 //BA.debugLineNum = 4460;BA.debugLine="gi_Historia_P_Arriba = xHistoria(iLimiteInferio";
_gi_historia_p_arriba = _xhistoria[_ilimiteinferior].idPalabra;
 }else {
 //BA.debugLineNum = 4462;BA.debugLine="gi_Historia_P_Abajo = xHistoria(iLimiteInferior";
_gi_historia_p_abajo = _xhistoria[_ilimiteinferior].idPalabra;
 //BA.debugLineNum = 4463;BA.debugLine="gi_Historia_P_Arriba = xHistoria(iLimiteSuperio";
_gi_historia_p_arriba = _xhistoria[_ilimitesuperior].idPalabra;
 };
 //BA.debugLineNum = 4467;BA.debugLine="Panel0.RemoveAllViews";
_panel0.RemoveAllViews();
 //BA.debugLineNum = 4469;BA.debugLine="If bSaltadas Then";
if (_bsaltadas) { 
 //BA.debugLineNum = 4470;BA.debugLine="lblFiltro.Text = \"SALTADOS\" ' & i& \"]\"";
mostCurrent._lblfiltro.setText((Object)("SALTADOS"));
 //BA.debugLineNum = 4471;BA.debugLine="sAnd = \" AND salteada = 1\"";
_sand = " AND salteada = 1";
 }else {
 //BA.debugLineNum = 4473;BA.debugLine="lblFiltro.Text = \"TODOS\" ' & i & \"]\"";
mostCurrent._lblfiltro.setText((Object)("TODOS"));
 };
 //BA.debugLineNum = 4476;BA.debugLine="If Publicos.Get_HayPalabrasAnteriores(g_sqlBaseL";
if (mostCurrent._vvvvvvvvvvvvvvv7._get_haypalabrasanteriores(mostCurrent.activityBA,_g_sqlbaselocalusuario,_xhistoria[(int) (0)].idPalabra,_sand)) { 
 //BA.debugLineNum = 4477;BA.debugLine="Dim xPanelItem As Panel, ImageView1 As ImageVie";
_xpanelitem = new anywheresoftware.b4a.objects.PanelWrapper();
_imageview1 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 4478;BA.debugLine="Historia_llenaScroll_Item (xPanelItem, gi_Histo";
_historia_llenascroll_item(_xpanelitem,_gi_historia_textsize,_xhistoria[(int) (0)],_labelwidth,_labelheight,_imageview1,"ANTERIORES",_panel0,_paneltop,_panelwidth,_panelheight,_pnlscroll,"HistoriaClickArriba",_ipanelscrollheight);
 //BA.debugLineNum = 4480;BA.debugLine="PanelTop=PanelTop+PanelHeight+1dip";
_paneltop = (int) (_paneltop+_panelheight+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (1)));
 };
 //BA.debugLineNum = 4483;BA.debugLine="For i = 0 To xHistoria.Length-1";
{
final int step111 = 1;
final int limit111 = (int) (_xhistoria.length-1);
for (_i = (int) (0) ; (step111 > 0 && _i <= limit111) || (step111 < 0 && _i >= limit111); _i = ((int)(0 + _i + step111)) ) {
 //BA.debugLineNum = 4484;BA.debugLine="If xHistoria(i).Definicion <> \"\" Then";
if ((_xhistoria[_i].Definicion).equals("") == false) { 
 //BA.debugLineNum = 4486;BA.debugLine="Dim PanelItem As Panel, ImageView1 As ImageVie";
_panelitem = new anywheresoftware.b4a.objects.PanelWrapper();
_imageview1 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 4488;BA.debugLine="Historia_llenaScroll_Item (PanelItem, gi_Histo";
_historia_llenascroll_item(_panelitem,_gi_historia_textsize,_xhistoria[_i],_labelwidth,_labelheight,_imageview1,"",_panel0,_paneltop,_panelwidth,_panelheight,_pnlscroll,"HistoriaClick",_ipanelscrollheight);
 //BA.debugLineNum = 4491;BA.debugLine="PanelTop=PanelTop+PanelHeight+1dip";
_paneltop = (int) (_paneltop+_panelheight+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (1)));
 };
 }
};
 //BA.debugLineNum = 4497;BA.debugLine="If xHistoria.Length >= iCantRegistrosMuestra The";
if (_xhistoria.length>=_icantregistrosmuestra) { 
 //BA.debugLineNum = 4499;BA.debugLine="Dim PanelItem As Panel, ImageView1 As ImageView";
_panelitem = new anywheresoftware.b4a.objects.PanelWrapper();
_imageview1 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 4500;BA.debugLine="Historia_llenaScroll_Item (PanelItem, gi_Histor";
_historia_llenascroll_item(_panelitem,_gi_historia_textsize,_xhistoria[(int) (0)],_labelwidth,_labelheight,_imageview1,"SIGUIENTES",_panel0,_paneltop,_panelwidth,_panelheight,_pnlscroll,"HistoriaClickAbajo",_ipanelscrollheight);
 };
 }else {
 //BA.debugLineNum = 4505;BA.debugLine="Dim xh As tHistoria, PanelItem As Panel, ImageV";
_xh = new com.matetejuego.free.publicos._thistoria();
_panelitem = new anywheresoftware.b4a.objects.PanelWrapper();
_imageview1 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 4506;BA.debugLine="Historia_llenaScroll_Item (PanelItem, gi_Histor";
_historia_llenascroll_item(_panelitem,_gi_historia_textsize,_xh,_labelwidth,_labelheight,_imageview1,"NO HAY MATETES",_panel0,_paneltop,_panelwidth,_panelheight,_pnlscroll,"SinHistoriaClick",_ipanelscrollheight);
 };
 //BA.debugLineNum = 4510;BA.debugLine="Cursor1.close";
_cursor1.Close();
 //BA.debugLineNum = 4511;BA.debugLine="If Panel0.Height < PanelTop Then";
if (_panel0.getHeight()<_paneltop) { 
 //BA.debugLineNum = 4512;BA.debugLine="Panel0.Height=PanelTop+Panel0.Height*0.2 + Panel";
_panel0.setHeight((int) (_paneltop+_panel0.getHeight()*0.2+mostCurrent._panel61.getHeight()));
 };
 //BA.debugLineNum = 4514;BA.debugLine="Panel0.Height = Panel0.Height";
_panel0.setHeight(_panel0.getHeight());
 //BA.debugLineNum = 4515;BA.debugLine="pnlScroll.Height = Panel0.Height";
_pnlscroll.setHeight(_panel0.getHeight());
 //BA.debugLineNum = 4517;BA.debugLine="End Sub";
return "";
}
public static String  _historia_llenascroll_item(anywheresoftware.b4a.objects.PanelWrapper _panelitem,int _itextsizemenor,com.matetejuego.free.publicos._thistoria _xhistoria,int _labelwidth,int _labelheight,anywheresoftware.b4a.objects.ImageViewWrapper _imageview1,String _sdescpisa,anywheresoftware.b4a.objects.PanelWrapper _panel0,int _paneltop,int _panelwidth,int _panelheight,anywheresoftware.b4a.objects.PanelWrapper _pnlscroll,String _sevento,int _ipanelscrollheight) throws Exception{
anywheresoftware.b4a.objects.PanelWrapper _separa = null;
anywheresoftware.b4a.objects.LabelWrapper _label1 = null;
 //BA.debugLineNum = 4518;BA.debugLine="Sub Historia_llenaScroll_Item(PanelItem As Panel,";
 //BA.debugLineNum = 4522;BA.debugLine="PanelItem.Initialize(sEvento)";
_panelitem.Initialize(mostCurrent.activityBA,_sevento);
 //BA.debugLineNum = 4523;BA.debugLine="Panel0.AddView(PanelItem,0,paneltop,panelwidth,p";
_panel0.AddView((android.view.View)(_panelitem.getObject()),(int) (0),_paneltop,_panelwidth,_panelheight);
 //BA.debugLineNum = 4524;BA.debugLine="PanelItem.Color=Colors.white";
_panelitem.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 4526;BA.debugLine="If sDescPisa <> \"\" Then ' si es el anteriores o";
if ((_sdescpisa).equals("") == false) { 
 //BA.debugLineNum = 4527;BA.debugLine="PanelItem.Height= iPanelScrollHeight/5";
_panelitem.setHeight((int) (_ipanelscrollheight/(double)5));
 }else {
 //BA.debugLineNum = 4529;BA.debugLine="PanelItem.Height= iPanelScrollHeight/5";
_panelitem.setHeight((int) (_ipanelscrollheight/(double)5));
 };
 //BA.debugLineNum = 4533;BA.debugLine="If xHistoria.Salteada = 1 Then";
if (_xhistoria.Salteada==1) { 
 //BA.debugLineNum = 4534;BA.debugLine="PanelItem.Tag = xHistoria.idpalabra*-1";
_panelitem.setTag((Object)(_xhistoria.idPalabra*-1));
 }else {
 //BA.debugLineNum = 4536;BA.debugLine="PanelItem.Tag = xHistoria.idpalabra";
_panelitem.setTag((Object)(_xhistoria.idPalabra));
 };
 //BA.debugLineNum = 4540;BA.debugLine="Dim separa As Panel";
_separa = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 4541;BA.debugLine="separa.Initialize(\"\")";
_separa.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 4542;BA.debugLine="PanelItem.AddView(separa,0,0, PanelItem.Width, 1";
_panelitem.AddView((android.view.View)(_separa.getObject()),(int) (0),(int) (0),_panelitem.getWidth(),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (1)));
 //BA.debugLineNum = 4543;BA.debugLine="separa.Color = Colors.DarkGray";
_separa.setColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 4546;BA.debugLine="Dim Label1 As Label";
_label1 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 4547;BA.debugLine="Label1.Initialize(\"\")";
_label1.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 4548;BA.debugLine="PanelItem.AddView(Label1,PanelItem.width*0.05,Pa";
_panelitem.AddView((android.view.View)(_label1.getObject()),(int) (_panelitem.getWidth()*0.05),(int) (_panelitem.getHeight()*0.1),_labelwidth,_panelitem.getHeight());
 //BA.debugLineNum = 4549;BA.debugLine="Publicos.CentrarControlEnPanel(PanelItem, Label1";
mostCurrent._vvvvvvvvvvvvvvv7._vv0(mostCurrent.activityBA,_panelitem,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(_label1.getObject())),anywheresoftware.b4a.keywords.Common.False,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 4551;BA.debugLine="Label1.Color=Colors.transparent";
_label1.setColor(anywheresoftware.b4a.keywords.Common.Colors.Transparent);
 //BA.debugLineNum = 4552;BA.debugLine="Label1.textsize = iTextSizeMenor";
_label1.setTextSize((float) (_itextsizemenor));
 //BA.debugLineNum = 4553;BA.debugLine="Label1.Gravity = Gravity.CENTER";
_label1.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 4555;BA.debugLine="ImageView1.Initialize(\"\")";
_imageview1.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 4556;BA.debugLine="PanelItem.AddView(ImageView1, PanelItem.Width*0.";
_panelitem.AddView((android.view.View)(_imageview1.getObject()),(int) (_panelitem.getWidth()*0.85),(int) (_panelitem.getHeight()*0.1),(int) (_panelitem.getWidth()*0.1),(int) (_panelitem.getWidth()*0.1));
 //BA.debugLineNum = 4557;BA.debugLine="Publicos.CentrarControlEnPanel(PanelItem, ImageV";
mostCurrent._vvvvvvvvvvvvvvv7._vv0(mostCurrent.activityBA,_panelitem,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(_imageview1.getObject())),anywheresoftware.b4a.keywords.Common.False,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 4559;BA.debugLine="ImageView1.Gravity = Gravity.FILL";
_imageview1.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.FILL);
 //BA.debugLineNum = 4561;BA.debugLine="If sDescPisa = \"NO HAY MATETES\" Then";
if ((_sdescpisa).equals("NO HAY MATETES")) { 
 //BA.debugLineNum = 4562;BA.debugLine="Label1.Text = sDescPisa";
_label1.setText((Object)(_sdescpisa));
 //BA.debugLineNum = 4563;BA.debugLine="Publicos.CentrarControlEnPanel(PanelItem,Label1";
mostCurrent._vvvvvvvvvvvvvvv7._vv0(mostCurrent.activityBA,_panelitem,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(_label1.getObject())),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 4564;BA.debugLine="Publicos.SetLabelTextSize (Label1, Label1.Text,";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv4(mostCurrent.activityBA,_label1,_label1.getText(),mostCurrent._lbl61.getTextSize(),(float) (6),(int) (100));
 }else {
 //BA.debugLineNum = 4567;BA.debugLine="If sDescPisa <>\"\" Then ' si manda una descripci";
if ((_sdescpisa).equals("") == false) { 
 //BA.debugLineNum = 4568;BA.debugLine="Publicos.CentrarControlEnPanel(PanelItem,Label";
mostCurrent._vvvvvvvvvvvvvvv7._vv0(mostCurrent.activityBA,_panelitem,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(_label1.getObject())),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 4571;BA.debugLine="Publicos.SetLabelTextSize (Label1, Label1.Text";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv4(mostCurrent.activityBA,_label1,_label1.getText(),mostCurrent._lbl61.getTextSize(),(float) (6),(int) (100));
 //BA.debugLineNum = 4572;BA.debugLine="If sDescPisa = \"ANTERIORES\" Then";
if ((_sdescpisa).equals("ANTERIORES")) { 
 //BA.debugLineNum = 4573;BA.debugLine="ImageView1.Bitmap = gt_Color.bitmSubir";
_imageview1.setBitmap((android.graphics.Bitmap)(mostCurrent._gt_color.bitmSubir.getObject()));
 }else {
 //BA.debugLineNum = 4575;BA.debugLine="ImageView1.Bitmap = gt_Color.bitmBajar";
_imageview1.setBitmap((android.graphics.Bitmap)(mostCurrent._gt_color.bitmBajar.getObject()));
 };
 //BA.debugLineNum = 4577;BA.debugLine="Publicos.CentrarControlEnPanel(PanelItem, Imag";
mostCurrent._vvvvvvvvvvvvvvv7._vv0(mostCurrent.activityBA,_panelitem,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(_imageview1.getObject())),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 4579;BA.debugLine="Label1.Tag=xHistoria.idPalabra";
_label1.setTag((Object)(_xhistoria.idPalabra));
 //BA.debugLineNum = 4580;BA.debugLine="If Publicos.GetUsuario =\"JUAN.DAROCHA@GMAIL.CO";
if ((mostCurrent._vvvvvvvvvvvvvvv7._vvv4(mostCurrent.activityBA)).equals("JUAN.DAROCHA@GMAIL.COM")) { 
 //BA.debugLineNum = 4581;BA.debugLine="Label1.Text= xHistoria.idPalabra & \" \" & xHis";
_label1.setText((Object)(BA.NumberToString(_xhistoria.idPalabra)+" "+_xhistoria.Definicion));
 }else {
 //BA.debugLineNum = 4583;BA.debugLine="Label1.Text= xHistoria.Definicion";
_label1.setText((Object)(_xhistoria.Definicion));
 };
 //BA.debugLineNum = 4585;BA.debugLine="If xHistoria.Adivinada = 1 Then";
if (_xhistoria.Adivinada==1) { 
 //BA.debugLineNum = 4586;BA.debugLine="ImageView1.Bitmap=bitmVerdeConfirmar";
_imageview1.setBitmap((android.graphics.Bitmap)(_vvvvvvv4.getObject()));
 }else {
 //BA.debugLineNum = 4588;BA.debugLine="ImageView1.Bitmap=bitmRojoCancelar";
_imageview1.setBitmap((android.graphics.Bitmap)(_vvvvvvv3.getObject()));
 };
 };
 };
 //BA.debugLineNum = 4594;BA.debugLine="Label1.TextColor = Get_aTextColor(gi_Combinacion";
_label1.setTextColor(_get_atextcolor(_gi_combinacioncolores,(int) (2)));
 //BA.debugLineNum = 4602;BA.debugLine="End Sub";
return "";
}
public static String  _historia_muestra(String _sdireccion) throws Exception{
int[] _icolor = null;
int _icolorletra = 0;
 //BA.debugLineNum = 4296;BA.debugLine="Sub Historia_Muestra(sDireccion As String)";
 //BA.debugLineNum = 4298;BA.debugLine="iPantallaActiva = xConfiguraPantalla.Historia";
_vvvvvvvv4 = _vvvvvvvv3.Historia;
 //BA.debugLineNum = 4300;BA.debugLine="Dim iColor (3) As Int, iColorLetra As Int";
_icolor = new int[(int) (3)];
;
_icolorletra = 0;
 //BA.debugLineNum = 4301;BA.debugLine="iColor(0) = Colors.RGB(247, 219, 245)";
_icolor[(int) (0)] = anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (247),(int) (219),(int) (245));
 //BA.debugLineNum = 4302;BA.debugLine="iColor(1) = Colors.RGB(242, 198, 241)";
_icolor[(int) (1)] = anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (242),(int) (198),(int) (241));
 //BA.debugLineNum = 4303;BA.debugLine="iColor(2) = Colors.RGB(238, 183, 237)";
_icolor[(int) (2)] = anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (238),(int) (183),(int) (237));
 //BA.debugLineNum = 4304;BA.debugLine="iColorLetra = Get_aTextColor(gi_CombinacionColore";
_icolorletra = _get_atextcolor(_gi_combinacioncolores,(int) (1));
 //BA.debugLineNum = 4306;BA.debugLine="pnlHistoria.Visible = True";
mostCurrent._pnlhistoria.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 4307;BA.debugLine="pnlHistoria.BringToFront";
mostCurrent._pnlhistoria.BringToFront();
 //BA.debugLineNum = 4308;BA.debugLine="imgLoading.Visible = False";
mostCurrent._imgloading.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 4310;BA.debugLine="Historia_llenaScroll(pnlHistoria, lblFiltro.tag =";
_historia_llenascroll(mostCurrent._pnlhistoria,(mostCurrent._lblfiltro.getTag()).equals((Object)("SALTADOS")),_sdireccion);
 //BA.debugLineNum = 4312;BA.debugLine="lbl11.Text = \"MATETES JUGADOS\"";
mostCurrent._lbl11.setText((Object)("MATETES JUGADOS"));
 //BA.debugLineNum = 4313;BA.debugLine="lbl11.TextColor= iColorLetra";
mostCurrent._lbl11.setTextColor(_icolorletra);
 //BA.debugLineNum = 4314;BA.debugLine="lbl61.Text = \"VOLVER\"";
mostCurrent._lbl61.setText((Object)("VOLVER"));
 //BA.debugLineNum = 4316;BA.debugLine="Panel61.Color =  gt_Color.Colordefault";
mostCurrent._panel61.setColor(mostCurrent._gt_color.ColorDefault);
 //BA.debugLineNum = 4317;BA.debugLine="Panel61.Visible = True";
mostCurrent._panel61.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 4318;BA.debugLine="Panel61.BringToFront";
mostCurrent._panel61.BringToFront();
 //BA.debugLineNum = 4319;BA.debugLine="Panel11.Visible = True";
mostCurrent._panel11.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 4320;BA.debugLine="lbl61.TextColor = gt_Color.ColorTexto";
mostCurrent._lbl61.setTextColor(mostCurrent._gt_color.ColorTexto);
 //BA.debugLineNum = 4322;BA.debugLine="imgShadow.Top = Panel11.Top + Panel11.Height";
mostCurrent._imgshadow.setTop((int) (mostCurrent._panel11.getTop()+mostCurrent._panel11.getHeight()));
 //BA.debugLineNum = 4324;BA.debugLine="lblFiltro.TextColor = Get_aTextColor(gi_Combinaci";
mostCurrent._lblfiltro.setTextColor(_get_atextcolor(_gi_combinacioncolores,(int) (1)));
 //BA.debugLineNum = 4326;BA.debugLine="Activity.Color = Colors.White 'Get_aColoresColor(";
mostCurrent._activity.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 4327;BA.debugLine="pnlInvisible.Visible = False";
mostCurrent._pnlinvisible.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 4329;BA.debugLine="End Sub";
return "";
}
public static String  _historiaclick_click() throws Exception{
anywheresoftware.b4a.objects.PanelWrapper _send = null;
int _idpalabra = 0;
boolean _isaltada = false;
 //BA.debugLineNum = 4617;BA.debugLine="Sub HistoriaClick_Click";
 //BA.debugLineNum = 4618;BA.debugLine="Dim Send As Panel";
_send = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 4619;BA.debugLine="Send = Sender'";
_send.setObject((android.view.ViewGroup)(anywheresoftware.b4a.keywords.Common.Sender(mostCurrent.activityBA)));
 //BA.debugLineNum = 4620;BA.debugLine="Dim idPalabra As Int = Send.tag";
_idpalabra = (int)(BA.ObjectToNumber(_send.getTag()));
 //BA.debugLineNum = 4621;BA.debugLine="Dim iSaltada As Boolean = False";
_isaltada = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 4622;BA.debugLine="If idPalabra <0 Then";
if (_idpalabra<0) { 
 //BA.debugLineNum = 4623;BA.debugLine="iSaltada = True";
_isaltada = anywheresoftware.b4a.keywords.Common.True;
 };
 //BA.debugLineNum = 4626;BA.debugLine="V2_PantallaConfigura(xConfiguraPantalla.Jugar,0)";
_v2_pantallaconfigura(_vvvvvvvv3.Jugar,(int) (0));
 //BA.debugLineNum = 4627;BA.debugLine="v2_MuestraNuevaPalabra (Abs(idPalabra), iSaltada,";
_v2_muestranuevapalabra((int) (anywheresoftware.b4a.keywords.Common.Abs(_idpalabra)),_isaltada,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 4630;BA.debugLine="End Sub";
return "";
}
public static String  _historiaclickabajo_click() throws Exception{
 //BA.debugLineNum = 4752;BA.debugLine="Sub HistoriaClickAbajo_Click";
 //BA.debugLineNum = 4753;BA.debugLine="Historia_Muestra (\"ABAJO\")";
_historia_muestra("ABAJO");
 //BA.debugLineNum = 4755;BA.debugLine="End Sub";
return "";
}
public static String  _historiaclickarriba_click() throws Exception{
 //BA.debugLineNum = 4758;BA.debugLine="Sub HistoriaClickArriba_Click";
 //BA.debugLineNum = 4759;BA.debugLine="Historia_Muestra(\"ARRIBA\")";
_historia_muestra("ARRIBA");
 //BA.debugLineNum = 4761;BA.debugLine="End Sub";
return "";
}
public static String  _httpremoto_responseerror(anywheresoftware.b4a.http.HttpClientWrapper.HttpResponeWrapper _response,String _reason,int _statuscode,int _tarea) throws Exception{
 //BA.debugLineNum = 4288;BA.debugLine="Sub httpRemoto_ResponseError (Response As HttpResp";
 //BA.debugLineNum = 4289;BA.debugLine="Log(\"Error: \" & Reason & \", StatusCode: \" & Status";
anywheresoftware.b4a.keywords.Common.Log("Error: "+_reason+", StatusCode: "+BA.NumberToString(_statuscode));
 //BA.debugLineNum = 4290;BA.debugLine="If Response <> Null Then";
if (_response!= null) { 
 //BA.debugLineNum = 4291;BA.debugLine="Log(Response.GetString(\"UTF8\"))";
anywheresoftware.b4a.keywords.Common.Log(_response.GetString("UTF8"));
 //BA.debugLineNum = 4292;BA.debugLine="Response.Release";
_response.Release();
 };
 //BA.debugLineNum = 4294;BA.debugLine="End Sub";
return "";
}
public static String  _httpremoto_responsesuccess(anywheresoftware.b4a.http.HttpClientWrapper.HttpResponeWrapper _response,int _tarea) throws Exception{
String _resultstring = "";
 //BA.debugLineNum = 4272;BA.debugLine="Sub httpRemoto_ResponseSuccess (Response As HttpRe";
 //BA.debugLineNum = 4273;BA.debugLine="Dim resultString As String";
_resultstring = "";
 //BA.debugLineNum = 4274;BA.debugLine="resultString = Response.GetString(\"UTF8\")";
_resultstring = _response.GetString("UTF8");
 //BA.debugLineNum = 4277;BA.debugLine="If aQuerysLocales(tarea) <>\"\" Then";
if ((_vvvvvv6[_tarea]).equals("") == false) { 
 //BA.debugLineNum = 4278;BA.debugLine="Try";
try { //BA.debugLineNum = 4279;BA.debugLine="g_sqlBaseLocalUsuario.ExecNonQuery(aQuerysLocale";
_g_sqlbaselocalusuario.ExecNonQuery(_vvvvvv6[_tarea]);
 //BA.debugLineNum = 4280;BA.debugLine="aQuerysLocales(tarea) = \"\"";
_vvvvvv6[_tarea] = "";
 } 
       catch (Exception e8) {
			processBA.setLastException(e8); //BA.debugLineNum = 4282;BA.debugLine="Log(\"Error query local ok remoto\")";
anywheresoftware.b4a.keywords.Common.Log("Error query local ok remoto");
 };
 };
 //BA.debugLineNum = 4286;BA.debugLine="End Sub";
return "";
}
public static String  _httpremotoselect_responseerror(anywheresoftware.b4a.http.HttpClientWrapper.HttpResponeWrapper _response,String _reason,int _statuscode,int _tarea) throws Exception{
 //BA.debugLineNum = 4690;BA.debugLine="Sub httpRemotoSelect_ResponseError (Response As Ht";
 //BA.debugLineNum = 4691;BA.debugLine="Log(\"Error: \" & Reason & \", StatusCode: \" & Status";
anywheresoftware.b4a.keywords.Common.Log("Error: "+_reason+", StatusCode: "+BA.NumberToString(_statuscode));
 //BA.debugLineNum = 4692;BA.debugLine="If Response <> Null Then";
if (_response!= null) { 
 //BA.debugLineNum = 4693;BA.debugLine="Log(Response.GetString(\"UTF8\"))";
anywheresoftware.b4a.keywords.Common.Log(_response.GetString("UTF8"));
 //BA.debugLineNum = 4694;BA.debugLine="Response.Release";
_response.Release();
 };
 //BA.debugLineNum = 4696;BA.debugLine="ProgressDialogHide";
anywheresoftware.b4a.keywords.Common.ProgressDialogHide();
 //BA.debugLineNum = 4697;BA.debugLine="End Sub";
return "";
}
public static String  _httpremotoselect_responsesuccess(anywheresoftware.b4a.http.HttpClientWrapper.HttpResponeWrapper _response,int _tarea) throws Exception{
String _res = "";
anywheresoftware.b4a.objects.collections.JSONParser _parser = null;
anywheresoftware.b4a.objects.collections.List _registros = null;
int _ipremium = 0;
anywheresoftware.b4a.objects.collections.Map _m = null;
String _sfecha = "";
 //BA.debugLineNum = 4702;BA.debugLine="Sub httpRemotoSelect_ResponseSuccess (Response As";
 //BA.debugLineNum = 4703;BA.debugLine="Dim res As String";
_res = "";
 //BA.debugLineNum = 4704;BA.debugLine="res = Response.GetString(\"UTF8\")";
_res = _response.GetString("UTF8");
 //BA.debugLineNum = 4706;BA.debugLine="Dim parser As JSONParser";
_parser = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 4707;BA.debugLine="parser.Initialize(res)";
_parser.Initialize(_res);
 //BA.debugLineNum = 4709;BA.debugLine="Select tarea";
switch (BA.switchObjectToInt(_tarea,_vvvvvvvv5.Premium)) {
case 0: {
 //BA.debugLineNum = 4714;BA.debugLine="Try";
try { //BA.debugLineNum = 4715;BA.debugLine="Dim Registros As List, iPremium As Int";
_registros = new anywheresoftware.b4a.objects.collections.List();
_ipremium = 0;
 //BA.debugLineNum = 4716;BA.debugLine="Registros.Initialize";
_registros.Initialize();
 //BA.debugLineNum = 4717;BA.debugLine="Registros = parser.NextArray";
_registros = _parser.NextArray();
 //BA.debugLineNum = 4719;BA.debugLine="If Registros.Size >0 Then";
if (_registros.getSize()>0) { 
 //BA.debugLineNum = 4720;BA.debugLine="Dim m As Map";
_m = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 4721;BA.debugLine="m = Registros.Get(0)";
_m.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_registros.Get((int) (0))));
 //BA.debugLineNum = 4722;BA.debugLine="iPremium= m.Get(\"Premium\")      ' ES";
_ipremium = (int)(BA.ObjectToNumber(_m.Get((Object)("Premium"))));
 //BA.debugLineNum = 4723;BA.debugLine="gb_PremiumRemoto = iPremium >0";
_gb_premiumremoto = _ipremium>0;
 //BA.debugLineNum = 4724;BA.debugLine="gb_EsPremium = gb_PremiumRemoto And Publicos.Ge";
_gb_espremium = _gb_premiumremoto && mostCurrent._vvvvvvvvvvvvvvv7._get_soyyo(mostCurrent.activityBA)==anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 4725;BA.debugLine="If gb_PremiumRemoto And Publicos.get_soyyo = Fa";
if (_gb_premiumremoto && mostCurrent._vvvvvvvvvvvvvvv7._get_soyyo(mostCurrent.activityBA)==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 4728;BA.debugLine="Dim sFecha As String";
_sfecha = "";
 //BA.debugLineNum = 4729;BA.debugLine="DateTime.DateFormat =\"yyyy-MM-dd hh:mm:ss\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("yyyy-MM-dd hh:mm:ss");
 //BA.debugLineNum = 4730;BA.debugLine="sFecha = DateTime.Date(DateTime.now)";
_sfecha = anywheresoftware.b4a.keywords.Common.DateTime.Date(anywheresoftware.b4a.keywords.Common.DateTime.getNow());
 //BA.debugLineNum = 4731;BA.debugLine="g_sqlBaseLocalUsuario.ExecNonQuery(\"Insert Int";
_g_sqlbaselocalusuario.ExecNonQuery("Insert Into Seteosusuario (TipoSeteo, texto, fechadesde) values ('premiumremoto','desderemoto', '"+_sfecha+"')");
 //BA.debugLineNum = 4732;BA.debugLine="Propaganda_ApagaYa";
_propaganda_apagaya();
 };
 };
 } 
       catch (Exception e26) {
			processBA.setLastException(e26); //BA.debugLineNum = 4737;BA.debugLine="Publicos.ManejaError(\"httpRemotoSelect_Response\"";
mostCurrent._vvvvvvvvvvvvvvv7._vvv7(mostCurrent.activityBA,"httpRemotoSelect_Response","Matete Divergente R");
 };
 break; }
}
;
 //BA.debugLineNum = 4740;BA.debugLine="Response.Release";
_response.Release();
 //BA.debugLineNum = 4741;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvvvvvvvvvv3(boolean _c,String _trueres,String _falseres) throws Exception{
 //BA.debugLineNum = 3011;BA.debugLine="Sub IIF(C As Boolean, TrueRes As String, FalseRes";
 //BA.debugLineNum = 3012;BA.debugLine="If C Then Return TrueRes Else Return FalseRes";
if (_c) { 
if (true) return _trueres;}
else {
if (true) return _falseres;};
 //BA.debugLineNum = 3013;BA.debugLine="End Sub";
return "";
}
public static String  _img51_click() throws Exception{
int _icual = 0;
 //BA.debugLineNum = 2597;BA.debugLine="Sub Img51_Click";
 //BA.debugLineNum = 2598;BA.debugLine="Dim iCual As Int";
_icual = 0;
 //BA.debugLineNum = 2599;BA.debugLine="iCual = img51.tag";
_icual = (int)(BA.ObjectToNumber(mostCurrent._img51.getTag()));
 //BA.debugLineNum = 2602;BA.debugLine="Select iCual";
switch (BA.switchObjectToInt(_icual,_vvvvvvvv3.ComprarLetra,_vvvvvvvv3.SaltarPalabra)) {
case 0: {
 //BA.debugLineNum = 2604;BA.debugLine="ComprarLetraHacer";
_vvvvvvvvvvvvvvvvvvvv5();
 break; }
case 1: {
 //BA.debugLineNum = 2606;BA.debugLine="SaltarPalabraHacer";
_vvvvvvvvvvvvvvvvvvvvvvv4();
 break; }
}
;
 //BA.debugLineNum = 2609;BA.debugLine="If Not (ComparaPalabra ) And Not (bFinDeJuego) Th";
if (anywheresoftware.b4a.keywords.Common.Not(_vvvvvvvvvvvvvvvvvvvv1()) && anywheresoftware.b4a.keywords.Common.Not(_vvvvvvvvv3)) { 
 //BA.debugLineNum = 2610;BA.debugLine="V2_PantallaConfigura(xConfiguraPantalla.jugar, 0";
_v2_pantallaconfigura(_vvvvvvvv3.Jugar,(int) (0));
 };
 //BA.debugLineNum = 2612;BA.debugLine="End Sub";
return "";
}
public static String  _img51facebook_click() throws Exception{
 //BA.debugLineNum = 2633;BA.debugLine="Sub img51Facebook_Click";
 //BA.debugLineNum = 2635;BA.debugLine="If Publicos.Facebook_GetUso (g_sqlBaseLocalUsuari";
if (mostCurrent._vvvvvvvvvvvvvvv7._facebook_getuso(mostCurrent.activityBA,_g_sqlbaselocalusuario)==0) { 
 //BA.debugLineNum = 2636;BA.debugLine="StartActivity(\"FacebookActivity\")";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)("FacebookActivity"));
 //BA.debugLineNum = 2637;BA.debugLine="FacebookActivity.sDefinicion = xtPalabra.Descrip";
mostCurrent._vvvvvvvvvvvvvvvv1._vvvvvvvvvv7 = _vvvvvvvvvvvvvvvvvv4.Descripcion;
 //BA.debugLineNum = 2638;BA.debugLine="FacebookActivity.sLetrasElegir = Get_LetrasElegi";
mostCurrent._vvvvvvvvvvvvvvvv1._vvvvvvvvvv6 = _get_letraselegirfb();
 //BA.debugLineNum = 2639;BA.debugLine="FacebookActivity.sPalabra = xtPalabra.Palabra";
mostCurrent._vvvvvvvvvvvvvvvv1._vvvvvvvvvv0 = _vvvvvvvvvvvvvvvvvv4.Palabra;
 //BA.debugLineNum = 2640;BA.debugLine="FacebookActivity.iCostoLetra = xtNivel.CostoLetr";
mostCurrent._vvvvvvvvvvvvvvvv1._vvvvvvvvvvv1 = _vvvvvvvvvvvvvvvvvv7.CostoLetra;
 //BA.debugLineNum = 2641;BA.debugLine="FacebookActivity.sLetrasPalabra = Get_LetrasPala";
mostCurrent._vvvvvvvvvvvvvvvv1._vvvvvvvvvvv2 = _get_letraspalabrafb();
 //BA.debugLineNum = 2642;BA.debugLine="FacebookActivity.g_sqlBaseLocalUsuario = g_sqlBa";
mostCurrent._vvvvvvvvvvvvvvvv1._g_sqlbaselocalusuario = _g_sqlbaselocalusuario;
 }else {
 //BA.debugLineNum = 2644;BA.debugLine="ToastMessageShow(\"Ya publicaste esta palabra en";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Ya publicaste esta palabra en Facebook!",anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 2647;BA.debugLine="End Sub";
return "";
}
public static String  _img51twitter_click() throws Exception{
int _bret = 0;
 //BA.debugLineNum = 1795;BA.debugLine="Sub img51Twitter_Click";
 //BA.debugLineNum = 1796;BA.debugLine="Dim bRet As Int";
_bret = 0;
 //BA.debugLineNum = 1797;BA.debugLine="If Publicos.twitter_getuso(g_sqlBaseLocalUsuario)";
if (mostCurrent._vvvvvvvvvvvvvvv7._twitter_getuso(mostCurrent.activityBA,_g_sqlbaselocalusuario)==0) { 
 //BA.debugLineNum = 1798;BA.debugLine="StartActivity(\"Twitter\")";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)("Twitter"));
 //BA.debugLineNum = 1799;BA.debugLine="Twitter.sDefinicion = xtPalabra.Descripcion";
mostCurrent._vvvvvvvvvvvvvvvv2._vvvvvvvvvv7 = _vvvvvvvvvvvvvvvvvv4.Descripcion;
 //BA.debugLineNum = 1800;BA.debugLine="Twitter.sLetrasElegir = Get_LetrasElegirFb";
mostCurrent._vvvvvvvvvvvvvvvv2._vvvvvvvvvv6 = _get_letraselegirfb();
 //BA.debugLineNum = 1801;BA.debugLine="Twitter.sPalabra = xtPalabra.Palabra";
mostCurrent._vvvvvvvvvvvvvvvv2._vvvvvvvvvv0 = _vvvvvvvvvvvvvvvvvv4.Palabra;
 //BA.debugLineNum = 1802;BA.debugLine="Twitter.iCostoLetra = xtNivel.CostoLetra";
mostCurrent._vvvvvvvvvvvvvvvv2._vvvvvvvvvvv1 = _vvvvvvvvvvvvvvvvvv7.CostoLetra;
 //BA.debugLineNum = 1803;BA.debugLine="Twitter.sLetrasPalabra = Get_LetrasPalabraFb";
mostCurrent._vvvvvvvvvvvvvvvv2._vvvvvvvvvvv2 = _get_letraspalabrafb();
 //BA.debugLineNum = 1804;BA.debugLine="Twitter.g_sqlBaseLocalUsuario = g_sqlBaseLocalUs";
mostCurrent._vvvvvvvvvvvvvvvv2._g_sqlbaselocalusuario = _g_sqlbaselocalusuario;
 }else {
 //BA.debugLineNum = 1806;BA.debugLine="ToastMessageShow(\"Ya publicaste esta palabra en";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Ya publicaste esta palabra en Twitter!",anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 1809;BA.debugLine="End Sub";
return "";
}
public static String  _imgbajarletras_click() throws Exception{
 //BA.debugLineNum = 920;BA.debugLine="Sub imgBajarLetras_Click";
 //BA.debugLineNum = 921;BA.debugLine="BajarLetras";
_vvvvvvvvvvvvvvvvvv0();
 //BA.debugLineNum = 922;BA.debugLine="End Sub";
return "";
}
public static String  _imgcompartir_click() throws Exception{
 //BA.debugLineNum = 2614;BA.debugLine="Sub imgCompartir_Click";
 //BA.debugLineNum = 2616;BA.debugLine="Publicos.Screenshot(Activity, g_DirGrabable, \"Pru";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv3(mostCurrent.activityBA,mostCurrent._activity,_g_dirgrabable,"PruebaSS.png");
 //BA.debugLineNum = 2618;BA.debugLine="V2_PantallaConfigura (xConfiguraPantalla.Comparti";
_v2_pantallaconfigura(_vvvvvvvv3.Compartir,(int) (0));
 //BA.debugLineNum = 2621;BA.debugLine="End Sub";
return "";
}
public static String  _imgmenu_click() throws Exception{
 //BA.debugLineNum = 2649;BA.debugLine="Sub imgMenu_Click";
 //BA.debugLineNum = 2650;BA.debugLine="If Panel61.Visible = False Then";
if (mostCurrent._panel61.getVisible()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 2653;BA.debugLine="If sm.isVisible Then";
if (mostCurrent._vvvvvvvvvvvvvvvvvvvvvv2._vvvvvvvvvvvvv6()) { 
 //BA.debugLineNum = 2654;BA.debugLine="sm.hide";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv2._vvvvvvvvvvvvv5();
 }else {
 //BA.debugLineNum = 2656;BA.debugLine="sm.Show";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv2._vvvvvvvvvvvvvv1();
 };
 };
 //BA.debugLineNum = 2659;BA.debugLine="End Sub";
return "";
}
public static String  _imgpedirletra_click() throws Exception{
String _smsg = "";
int _iret = 0;
 //BA.debugLineNum = 1228;BA.debugLine="Sub imgPedirLetra_click";
 //BA.debugLineNum = 1230;BA.debugLine="If ComprarLetraPuede Then";
if (_vvvvvvvvvvvvvvvvvvvv0()) { 
 //BA.debugLineNum = 1240;BA.debugLine="V2_PantallaConfigura(xConfiguraPantalla.ComprarLe";
_v2_pantallaconfigura(_vvvvvvvv3.ComprarLetra,(int) (0));
 }else {
 //BA.debugLineNum = 1243;BA.debugLine="Dim sMsg As String, iRet As Int";
_smsg = "";
_iret = 0;
 //BA.debugLineNum = 1244;BA.debugLine="sMsg = \"No tienes \" & Publicos.sNombreMonedas & \"";
_smsg = "No tienes "+mostCurrent._vvvvvvvvvvvvvvv7._v5+" suficientes"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Te faltan "+BA.NumberToString(((double)(Double.parseDouble(_get_costocomprarletras()))-_g_imonedas))+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Puedes adquirir neuronas en el Shop";
 //BA.debugLineNum = 1246;BA.debugLine="iRet = Msgbox2(sMsg, \"MATETE\", \"Ir al Shop\", \"Sal";
_iret = anywheresoftware.b4a.keywords.Common.Msgbox2(_smsg,"MATETE","Ir al Shop","Salir","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null),mostCurrent.activityBA);
 //BA.debugLineNum = 1247;BA.debugLine="If iRet = DialogResponse.POSITIVE Then";
if (_iret==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
 //BA.debugLineNum = 1248;BA.debugLine="V2_PantallaConfigura(xConfiguraPantalla.producto";
_v2_pantallaconfigura(_vvvvvvvv3.Producto,(int) (0));
 };
 };
 //BA.debugLineNum = 1255;BA.debugLine="End Sub";
return "";
}
public static String  _imgsaltarpalabra_click() throws Exception{
String _bret = "";
String _stit = "";
String _smsg = "";
int _iret = 0;
 //BA.debugLineNum = 1062;BA.debugLine="Sub imgSaltarPalabra_click";
 //BA.debugLineNum = 1063;BA.debugLine="Dim bRet As String, sTit As String =\"¿Confirma sa";
_bret = "";
_stit = "¿Confirma saltar el matete?";
 //BA.debugLineNum = 1066;BA.debugLine="If SaltarPalabraPuede(g_iMonedas) Then";
if (_vvvvvvvvvvvvvvvvvvvvvvv5(_g_imonedas)) { 
 //BA.debugLineNum = 1070;BA.debugLine="V2_PantallaConfigura(xConfiguraPantalla.SaltarPa";
_v2_pantallaconfigura(_vvvvvvvv3.SaltarPalabra,(int) (0));
 }else {
 //BA.debugLineNum = 1076;BA.debugLine="Dim sMsg As String, iRet As Int";
_smsg = "";
_iret = 0;
 //BA.debugLineNum = 1077;BA.debugLine="sMsg = \"No tienes \" & Publicos.sNombreMonedas &";
_smsg = "No tienes "+mostCurrent._vvvvvvvvvvvvvvv7._v5+" suficientes"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Te faltan "+BA.NumberToString((_get_costosaltarpalabra()-_g_imonedas))+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Puedes adquirir neuronas en el Shop";
 //BA.debugLineNum = 1080;BA.debugLine="iRet = Msgbox2(sMsg, \"MATETE\", \"Ir al Shop\", \"Sa";
_iret = anywheresoftware.b4a.keywords.Common.Msgbox2(_smsg,"MATETE","Ir al Shop","Salir","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null),mostCurrent.activityBA);
 //BA.debugLineNum = 1081;BA.debugLine="If iRet = DialogResponse.POSITIVE Then";
if (_iret==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
 //BA.debugLineNum = 1082;BA.debugLine="V2_PantallaConfigura(xConfiguraPantalla.product";
_v2_pantallaconfigura(_vvvvvvvv3.Producto,(int) (0));
 };
 };
 //BA.debugLineNum = 1085;BA.debugLine="End Sub";
return "";
}
public static String  _inicializa_colorespaleta() throws Exception{
 //BA.debugLineNum = 4802;BA.debugLine="Sub Inicializa_ColoresPaleta";
 //BA.debugLineNum = 4803;BA.debugLine="gt_ColoresPaleta(0).ColorNombre= \"Violeta\"";
_gt_colorespaleta[(int) (0)].ColorNombre = "Violeta";
 //BA.debugLineNum = 4804;BA.debugLine="gt_ColoresPaleta(0).ColorClaro = Colors.RGB(190,";
_gt_colorespaleta[(int) (0)].ColorClaro = anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (190),(int) (124),(int) (190));
 //BA.debugLineNum = 4805;BA.debugLine="gt_ColoresPaleta(0).ColorIntermedio =Colors.RGB(1";
_gt_colorespaleta[(int) (0)].ColorIntermedio = anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (167),(int) (99),(int) (167));
 //BA.debugLineNum = 4806;BA.debugLine="gt_ColoresPaleta(0).ColorOscuro= Colors.RGB(151,";
_gt_colorespaleta[(int) (0)].ColorOscuro = anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (151),(int) (89),(int) (152));
 //BA.debugLineNum = 4807;BA.debugLine="gt_ColoresPaleta(0).ColorTexto = Colors.RGB(151,";
_gt_colorespaleta[(int) (0)].ColorTexto = anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (151),(int) (89),(int) (152));
 //BA.debugLineNum = 4808;BA.debugLine="gt_ColoresPaleta(0).colorDefault = Colors.white";
_gt_colorespaleta[(int) (0)].ColorDefault = anywheresoftware.b4a.keywords.Common.Colors.White;
 //BA.debugLineNum = 4812;BA.debugLine="gt_ColoresPaleta(1).ColorNombre= \"Rojo\"";
_gt_colorespaleta[(int) (1)].ColorNombre = "Rojo";
 //BA.debugLineNum = 4813;BA.debugLine="gt_ColoresPaleta(1).ColorClaro = Colors.RGB(242,";
_gt_colorespaleta[(int) (1)].ColorClaro = anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (242),(int) (72),(int) (77));
 //BA.debugLineNum = 4814;BA.debugLine="gt_ColoresPaleta(1).ColorIntermedio  = Colors.RGB";
_gt_colorespaleta[(int) (1)].ColorIntermedio = anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (241),(int) (46),(int) (51));
 //BA.debugLineNum = 4815;BA.debugLine="gt_ColoresPaleta(1).ColorOscuro= Colors.RGB(223,";
_gt_colorespaleta[(int) (1)].ColorOscuro = anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (223),(int) (17),(int) (28));
 //BA.debugLineNum = 4816;BA.debugLine="gt_ColoresPaleta(1).ColorTexto = Colors.RGB(223,";
_gt_colorespaleta[(int) (1)].ColorTexto = anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (223),(int) (17),(int) (28));
 //BA.debugLineNum = 4817;BA.debugLine="gt_ColoresPaleta(1).colorDefault = Colors.white";
_gt_colorespaleta[(int) (1)].ColorDefault = anywheresoftware.b4a.keywords.Common.Colors.White;
 //BA.debugLineNum = 4819;BA.debugLine="gt_ColoresPaleta(2).ColorNombre= \"Azul\"";
_gt_colorespaleta[(int) (2)].ColorNombre = "Azul";
 //BA.debugLineNum = 4820;BA.debugLine="gt_ColoresPaleta(2).ColorClaro = Colors.RGB(119,";
_gt_colorespaleta[(int) (2)].ColorClaro = anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (119),(int) (126),(int) (219));
 //BA.debugLineNum = 4821;BA.debugLine="gt_ColoresPaleta(2).ColorIntermedio  = Colors.RGB";
_gt_colorespaleta[(int) (2)].ColorIntermedio = anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (87),(int) (97),(int) (210));
 //BA.debugLineNum = 4822;BA.debugLine="gt_ColoresPaleta(2).ColorOscuro= Colors.RGB(63, 7";
_gt_colorespaleta[(int) (2)].ColorOscuro = anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (63),(int) (72),(int) (204));
 //BA.debugLineNum = 4823;BA.debugLine="gt_ColoresPaleta(2).ColorTexto = Colors.RGB(63, 7";
_gt_colorespaleta[(int) (2)].ColorTexto = anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (63),(int) (72),(int) (204));
 //BA.debugLineNum = 4824;BA.debugLine="gt_ColoresPaleta(2).colorDefault = Colors.white";
_gt_colorespaleta[(int) (2)].ColorDefault = anywheresoftware.b4a.keywords.Common.Colors.White;
 //BA.debugLineNum = 4826;BA.debugLine="gt_ColoresPaleta(3).ColorNombre = \"Verde\"";
_gt_colorespaleta[(int) (3)].ColorNombre = "Verde";
 //BA.debugLineNum = 4827;BA.debugLine="gt_ColoresPaleta(3).ColorClaro = Colors.RGB(80, 2";
_gt_colorespaleta[(int) (3)].ColorClaro = anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (80),(int) (222),(int) (122));
 //BA.debugLineNum = 4828;BA.debugLine="gt_ColoresPaleta(3).ColorIntermedio  = Colors.RGB";
_gt_colorespaleta[(int) (3)].ColorIntermedio = anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (34),(int) (177),(int) (76));
 //BA.debugLineNum = 4829;BA.debugLine="gt_ColoresPaleta(3).ColorOscuro= Colors.RGB(29, 1";
_gt_colorespaleta[(int) (3)].ColorOscuro = anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (29),(int) (150),(int) (65));
 //BA.debugLineNum = 4830;BA.debugLine="gt_ColoresPaleta(3).ColorTexto = Colors.RGB(29, 1";
_gt_colorespaleta[(int) (3)].ColorTexto = anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (29),(int) (150),(int) (65));
 //BA.debugLineNum = 4831;BA.debugLine="gt_ColoresPaleta(3).colorDefault = Colors.white";
_gt_colorespaleta[(int) (3)].ColorDefault = anywheresoftware.b4a.keywords.Common.Colors.White;
 //BA.debugLineNum = 4833;BA.debugLine="gt_ColoresPaleta(4).ColorNombre = \"Gris\"";
_gt_colorespaleta[(int) (4)].ColorNombre = "Gris";
 //BA.debugLineNum = 4834;BA.debugLine="gt_ColoresPaleta(4).ColorClaro = Colors.RGB(181,";
_gt_colorespaleta[(int) (4)].ColorClaro = anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (181),(int) (181),(int) (181));
 //BA.debugLineNum = 4835;BA.debugLine="gt_ColoresPaleta(4).ColorIntermedio  = Colors.RGB";
_gt_colorespaleta[(int) (4)].ColorIntermedio = anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (129),(int) (129),(int) (129));
 //BA.debugLineNum = 4836;BA.debugLine="gt_ColoresPaleta(4).ColorOscuro= Colors.RGB(105,";
_gt_colorespaleta[(int) (4)].ColorOscuro = anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (105),(int) (105),(int) (105));
 //BA.debugLineNum = 4837;BA.debugLine="gt_ColoresPaleta(4).ColorTexto = Colors.RGB(105,";
_gt_colorespaleta[(int) (4)].ColorTexto = anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (105),(int) (105),(int) (105));
 //BA.debugLineNum = 4838;BA.debugLine="gt_ColoresPaleta(4).colorDefault = Colors.white";
_gt_colorespaleta[(int) (4)].ColorDefault = anywheresoftware.b4a.keywords.Common.Colors.White;
 //BA.debugLineNum = 4840;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvvvv0() throws Exception{
 //BA.debugLineNum = 410;BA.debugLine="Sub InicioMatete ' inicio del Activity";
 //BA.debugLineNum = 412;BA.debugLine="v2_MuestraNuevaPalabra(0,False,False) 'muestra en";
_v2_muestranuevapalabra((int) (0),anywheresoftware.b4a.keywords.Common.False,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 414;BA.debugLine="End Sub";
return "";
}
public static String  _jugar_onadshown(String _placementid) throws Exception{
 //BA.debugLineNum = 4931;BA.debugLine="Sub Jugar_onAdShown(placementId As String)";
 //BA.debugLineNum = 4932;BA.debugLine="Log(\"ioDisplay.Shown\")";
anywheresoftware.b4a.keywords.Common.Log("ioDisplay.Shown");
 //BA.debugLineNum = 4934;BA.debugLine="End Sub";
return "";
}
public static String  _jugar_onnoads(String _placementid) throws Exception{
 //BA.debugLineNum = 4936;BA.debugLine="Sub Jugar_onNoAds(placementId As String)";
 //BA.debugLineNum = 4937;BA.debugLine="Log(\"ioDisplay.NoAds\")";
anywheresoftware.b4a.keywords.Common.Log("ioDisplay.NoAds");
 //BA.debugLineNum = 4939;BA.debugLine="End Sub";
return "";
}
public static String  _lbl51mensajematete_click() throws Exception{
 //BA.debugLineNum = 3933;BA.debugLine="Sub lbl51MensajeMatete_Click";
 //BA.debugLineNum = 3935;BA.debugLine="Select (True)";
switch (BA.switchObjectToInt((anywheresoftware.b4a.keywords.Common.True),mostCurrent._lbl51mensajematete.getText().contains(_vvvvvvvvvvvvvvvvvv4.Diccionario),(mostCurrent._lbl51mensajematete.getText()).equals(_vvvvvvvvvvvvvvvvvv4.Descripcion))) {
case 0: {
 //BA.debugLineNum = 3937;BA.debugLine="lbl51MensajeMatete.Text = xtPalabra.Descripcion";
mostCurrent._lbl51mensajematete.setText((Object)(_vvvvvvvvvvvvvvvvvv4.Descripcion));
 //BA.debugLineNum = 3938;BA.debugLine="Publicos.SetLabelTextSize(lbl51MensajeMatete, lbl";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv4(mostCurrent.activityBA,mostCurrent._lbl51mensajematete,mostCurrent._lbl51mensajematete.getText(),(float) (40),(float) (6),(int) (100));
 break; }
case 1: {
 //BA.debugLineNum = 3940;BA.debugLine="If xtPalabra.Palabra.ToUpperCase <> xtPalabra.Pal";
if ((_vvvvvvvvvvvvvvvvvv4.Palabra.toUpperCase()).equals(_vvvvvvvvvvvvvvvvvv4.PalabraDiccionario.toUpperCase()) == false) { 
 //BA.debugLineNum = 3941;BA.debugLine="lbl51MensajeMatete.Text = \"(\" & xtPalabra.Palabr";
mostCurrent._lbl51mensajematete.setText((Object)("("+_vvvvvvvvvvvvvvvvvv4.PalabraDiccionario+")"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))));
 }else {
 //BA.debugLineNum = 3943;BA.debugLine="lbl51MensajeMatete.Text = \"\"";
mostCurrent._lbl51mensajematete.setText((Object)(""));
 };
 //BA.debugLineNum = 3945;BA.debugLine="lbl51MensajeMatete.Text = lbl51MensajeMatete.Text";
mostCurrent._lbl51mensajematete.setText((Object)(mostCurrent._lbl51mensajematete.getText()+_vvvvvvvvvvvvvvvvvv4.Diccionario));
 //BA.debugLineNum = 3946;BA.debugLine="Publicos.SetLabelTextSize(lbl51MensajeMatete, lbl";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv4(mostCurrent.activityBA,mostCurrent._lbl51mensajematete,mostCurrent._lbl51mensajematete.getText(),(float) (40),(float) (6),(int) (100));
 break; }
}
;
 //BA.debugLineNum = 3949;BA.debugLine="If lblv2Def.TextSize < lbl51MensajeMatete.textsize";
if (mostCurrent._lblv2def.getTextSize()<mostCurrent._lbl51mensajematete.getTextSize()) { 
 //BA.debugLineNum = 3950;BA.debugLine="lbl51MensajeMatete.TextSize = lblv2Def.TextSize";
mostCurrent._lbl51mensajematete.setTextSize(mostCurrent._lblv2def.getTextSize());
 };
 //BA.debugLineNum = 3953;BA.debugLine="End Sub";
return "";
}
public static String  _lbl61_click() throws Exception{
int _icual = 0;
 //BA.debugLineNum = 2485;BA.debugLine="Sub lbl61_Click";
 //BA.debugLineNum = 2486;BA.debugLine="Dim iCual As Int";
_icual = 0;
 //BA.debugLineNum = 2487;BA.debugLine="iCual = lbl61.Tag";
_icual = (int)(BA.ObjectToNumber(mostCurrent._lbl61.getTag()));
 //BA.debugLineNum = 2488;BA.debugLine="iPantallaActiva = iCual";
_vvvvvvvv4 = _icual;
 //BA.debugLineNum = 2489;BA.debugLine="Select iCual";
switch (BA.switchObjectToInt(_icual,_vvvvvvvv3.Adivino,_vvvvvvvv3.ComprarLetra,_vvvvvvvv3.SaltarPalabra,_vvvvvvvv3.Compartir,_vvvvvvvv3.Ayuda,_vvvvvvvv3.Creditos,_vvvvvvvv3.FinDeJuego,_vvvvvvvv3.Producto,_vvvvvvvv3.Premium,_vvvvvvvv3.Historia)) {
case 0: {
 //BA.debugLineNum = 2493;BA.debugLine="V2_AdivinoAnimacion(0, 0)";
_v2_adivinoanimacion((int) (0),(int) (0));
 //BA.debugLineNum = 2495;BA.debugLine="v2_MuestraNuevaPalabra(0,False,False)";
_v2_muestranuevapalabra((int) (0),anywheresoftware.b4a.keywords.Common.False,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2496;BA.debugLine="If Not (bFinDeJuego) Then";
if (anywheresoftware.b4a.keywords.Common.Not(_vvvvvvvvv3)) { 
 //BA.debugLineNum = 2497;BA.debugLine="V2_PantallaConfigura(xConfiguraPantalla.jugar,0";
_v2_pantallaconfigura(_vvvvvvvv3.Jugar,(int) (0));
 };
 break; }
case 1: {
 //BA.debugLineNum = 2501;BA.debugLine="V2_PantallaConfigura(xConfiguraPantalla.jugar, 0";
_v2_pantallaconfigura(_vvvvvvvv3.Jugar,(int) (0));
 break; }
case 2: {
 //BA.debugLineNum = 2504;BA.debugLine="V2_PantallaConfigura(xConfiguraPantalla.jugar, 0";
_v2_pantallaconfigura(_vvvvvvvv3.Jugar,(int) (0));
 break; }
case 3: {
 //BA.debugLineNum = 2506;BA.debugLine="V2_PantallaConfigura(xConfiguraPantalla.jugar,0)";
_v2_pantallaconfigura(_vvvvvvvv3.Jugar,(int) (0));
 break; }
case 4: {
 //BA.debugLineNum = 2508;BA.debugLine="V2_PantallaConfigura(xConfiguraPantalla.jugar,0)";
_v2_pantallaconfigura(_vvvvvvvv3.Jugar,(int) (0));
 break; }
case 5: {
 //BA.debugLineNum = 2510;BA.debugLine="V2_PantallaConfigura(xConfiguraPantalla.jugar,0)";
_v2_pantallaconfigura(_vvvvvvvv3.Jugar,(int) (0));
 break; }
case 6: {
 //BA.debugLineNum = 2512;BA.debugLine="If gb_EsPremium Then";
if (_gb_espremium) { 
 //BA.debugLineNum = 2513;BA.debugLine="V2_PantallaConfigura(xConfiguraPantalla.Histori";
_v2_pantallaconfigura(_vvvvvvvv3.Historia,(int) (0));
 }else {
 //BA.debugLineNum = 2518;BA.debugLine="V2_PantallaConfigura(xConfiguraPantalla.Premium";
_v2_pantallaconfigura(_vvvvvvvv3.Premium,(int) (0));
 };
 break; }
case 7: {
 //BA.debugLineNum = 2522;BA.debugLine="pnlInvisible.removeallviews";
mostCurrent._pnlinvisible.RemoveAllViews();
 //BA.debugLineNum = 2523;BA.debugLine="pnlInvisible.Visible = False";
mostCurrent._pnlinvisible.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2524;BA.debugLine="Neuronas_Mostrar";
_neuronas_mostrar();
 //BA.debugLineNum = 2525;BA.debugLine="V2_AnimaRotateImageview(imgNeuronas, 0, 720, 100";
_v2_animarotateimageview(mostCurrent._imgneuronas,(int) (0),(int) (720),(int) (1000));
 break; }
case 8: {
 //BA.debugLineNum = 2527;BA.debugLine="StartActivity(\"Main\")";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)("Main"));
 break; }
case 9: {
 //BA.debugLineNum = 2529;BA.debugLine="If bFinDeJuego Then";
if (_vvvvvvvvv3) { 
 //BA.debugLineNum = 2530;BA.debugLine="V2_PantallaConfigura(xConfiguraPantalla.FinDeJu";
_v2_pantallaconfigura(_vvvvvvvv3.FinDeJuego,(int) (0));
 }else {
 //BA.debugLineNum = 2532;BA.debugLine="V2_PantallaConfigura(xConfiguraPantalla.jugar,0";
_v2_pantallaconfigura(_vvvvvvvv3.Jugar,(int) (0));
 };
 break; }
}
;
 //BA.debugLineNum = 2537;BA.debugLine="End Sub";
return "";
}
public static String  _lblbajarletras_click() throws Exception{
 //BA.debugLineNum = 2661;BA.debugLine="Sub lblBajarLetras_Click";
 //BA.debugLineNum = 2663;BA.debugLine="BajarLetras";
_vvvvvvvvvvvvvvvvvv0();
 //BA.debugLineNum = 2664;BA.debugLine="End Sub";
return "";
}
public static String  _lblbloqueo_click() throws Exception{
 //BA.debugLineNum = 771;BA.debugLine="Sub lblBloqueo_Click";
 //BA.debugLineNum = 773;BA.debugLine="If lblBloqueo.Tag =\"S\" Then";
if ((mostCurrent._vvvvvvvvvvvvvvvvvvv4.getTag()).equals((Object)("S"))) { 
 //BA.debugLineNum = 774;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 }else {
 //BA.debugLineNum = 778;BA.debugLine="If lblBloqueo.tag=\"N\" Then";
if ((mostCurrent._vvvvvvvvvvvvvvvvvvv4.getTag()).equals((Object)("N"))) { 
 };
 };
 //BA.debugLineNum = 782;BA.debugLine="End Sub";
return "";
}
public static String  _lblcompartir_click() throws Exception{
 //BA.debugLineNum = 3015;BA.debugLine="Sub lblCompartir_Click";
 //BA.debugLineNum = 3016;BA.debugLine="imgCompartir_Click";
_imgcompartir_click();
 //BA.debugLineNum = 3017;BA.debugLine="End Sub";
return "";
}
public static String  _lblfiltro_click() throws Exception{
 //BA.debugLineNum = 4603;BA.debugLine="Sub lblFiltro_click";
 //BA.debugLineNum = 4604;BA.debugLine="gi_Historia_P_Abajo =-1";
_gi_historia_p_abajo = (int) (-1);
 //BA.debugLineNum = 4605;BA.debugLine="gi_Historia_P_Arriba = -1";
_gi_historia_p_arriba = (int) (-1);
 //BA.debugLineNum = 4607;BA.debugLine="If lblFiltro.TAG = \"TODOS\" Then";
if ((mostCurrent._lblfiltro.getTag()).equals((Object)("TODOS"))) { 
 //BA.debugLineNum = 4608;BA.debugLine="lblFiltro.Text = \"SALTADOS\"";
mostCurrent._lblfiltro.setText((Object)("SALTADOS"));
 //BA.debugLineNum = 4609;BA.debugLine="lblFiltro.Tag = \"SALTADOS\"";
mostCurrent._lblfiltro.setTag((Object)("SALTADOS"));
 }else {
 //BA.debugLineNum = 4611;BA.debugLine="lblFiltro.Text = \"TODOS\"";
mostCurrent._lblfiltro.setText((Object)("TODOS"));
 //BA.debugLineNum = 4612;BA.debugLine="lblFiltro.Tag = \"TODOS\"";
mostCurrent._lblfiltro.setTag((Object)("TODOS"));
 };
 //BA.debugLineNum = 4614;BA.debugLine="Historia_Muestra(\"IGUAL\")";
_historia_muestra("IGUAL");
 //BA.debugLineNum = 4615;BA.debugLine="End Sub";
return "";
}
public static String  _lblletras_click() throws Exception{
anywheresoftware.b4a.objects.LabelWrapper _send = null;
 //BA.debugLineNum = 876;BA.debugLine="Sub lblLetras_Click ' click  una letra (la pasa a";
 //BA.debugLineNum = 877;BA.debugLine="Dim Send As Label";
_send = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 878;BA.debugLine="Send = Sender";
_send.setObject((android.widget.TextView)(anywheresoftware.b4a.keywords.Common.Sender(mostCurrent.activityBA)));
 //BA.debugLineNum = 879;BA.debugLine="If Send.textcolor = Get_ColorLetraDisponible Then";
if (_send.getTextColor()==_get_colorletradisponible()) { 
 //BA.debugLineNum = 880;BA.debugLine="If Send.Text <>\"\" Then ' si no está vacía";
if ((_send.getText()).equals("") == false) { 
 //BA.debugLineNum = 881;BA.debugLine="If LetraPalabraDisponible Then 'si hay alguna p";
if (_vvvvvvvvvvvvvvvvvvvvvvv6()) { 
 //BA.debugLineNum = 882;BA.debugLine="EligeUnaLetra (Send, False, -1, -1)";
_vvvvvvvvvvvvvvvvvvvv6(_send,anywheresoftware.b4a.keywords.Common.False,(int) (-1),(int) (-1));
 }else {
 //BA.debugLineNum = 884;BA.debugLine="ToastMessageShow(\"UPS!\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("UPS!",anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 886;BA.debugLine="iCuentaClickLetraVacia =0";
_vvvvvvvvv4 = (int) (0);
 };
 };
 //BA.debugLineNum = 889;BA.debugLine="End Sub";
return "";
}
public static String  _lblpalabra_click() throws Exception{
anywheresoftware.b4a.objects.LabelWrapper _send = null;
 //BA.debugLineNum = 891;BA.debugLine="Sub lblPalabra_Click ' click descartar una letra (";
 //BA.debugLineNum = 892;BA.debugLine="Dim Send As Label";
_send = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 894;BA.debugLine="Send = Sender";
_send.setObject((android.widget.TextView)(anywheresoftware.b4a.keywords.Common.Sender(mostCurrent.activityBA)));
 //BA.debugLineNum = 895;BA.debugLine="If Panel61.Visible = False Then";
if (mostCurrent._panel61.getVisible()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 896;BA.debugLine="lblPalabraClick (Send)";
_vvvvvvvvvvvvvvvvvvv2(_send);
 };
 //BA.debugLineNum = 899;BA.debugLine="End Sub";
return "";
}
public static String  _lblpalabra_longclick() throws Exception{
anywheresoftware.b4a.objects.LabelWrapper _send = null;
 //BA.debugLineNum = 901;BA.debugLine="Sub lblPalabra_LongClick";
 //BA.debugLineNum = 903;BA.debugLine="Dim Send As Label";
_send = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 904;BA.debugLine="Send = Sender";
_send.setObject((android.widget.TextView)(anywheresoftware.b4a.keywords.Common.Sender(mostCurrent.activityBA)));
 //BA.debugLineNum = 906;BA.debugLine="BajarLetras";
_vvvvvvvvvvvvvvvvvv0();
 //BA.debugLineNum = 908;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvvvvvv2(anywheresoftware.b4a.objects.LabelWrapper _send) throws Exception{
 //BA.debugLineNum = 924;BA.debugLine="Sub lblPalabraClick(send As Label)";
 //BA.debugLineNum = 925;BA.debugLine="Try";
try { //BA.debugLineNum = 926;BA.debugLine="If send.Text<>\"\" Then '";
if ((_send.getText()).equals("") == false) { 
 //BA.debugLineNum = 927;BA.debugLine="If send.Tag <>\"?\" Then ' si no es una letra com";
if ((_send.getTag()).equals((Object)("?")) == false) { 
 //BA.debugLineNum = 928;BA.debugLine="DescartaUnaLetra (send)";
_vvvvvvvvvvvvvvvvvvvvv4(_send);
 //BA.debugLineNum = 929;BA.debugLine="iClickVacio = 0";
_vvvvvvvvvvvvvvvvvvvvvv5 = (int) (0);
 };
 }else {
 //BA.debugLineNum = 932;BA.debugLine="iClickVacio = iClickVacio +1";
_vvvvvvvvvvvvvvvvvvvvvv5 = (int) (_vvvvvvvvvvvvvvvvvvvvvv5+1);
 //BA.debugLineNum = 933;BA.debugLine="If iClickVacio =12  Then";
if (_vvvvvvvvvvvvvvvvvvvvvv5==12) { 
 //BA.debugLineNum = 934;BA.debugLine="Publicos.Magia (iClickVacio)";
mostCurrent._vvvvvvvvvvvvvvv7._vvv6(mostCurrent.activityBA,_vvvvvvvvvvvvvvvvvvvvvv5);
 };
 };
 } 
       catch (Exception e14) {
			processBA.setLastException(e14); //BA.debugLineNum = 938;BA.debugLine="ManejaErrorJugar(\"lblPalabraClick\", \"\")";
_vvvvvvvvvvvvvvvvvv3("lblPalabraClick","");
 };
 //BA.debugLineNum = 940;BA.debugLine="End Sub";
return "";
}
public static String  _lblpedirletra_click() throws Exception{
 //BA.debugLineNum = 2623;BA.debugLine="Sub lblPedirLetra_Click";
 //BA.debugLineNum = 2624;BA.debugLine="imgPedirLetra_click";
_imgpedirletra_click();
 //BA.debugLineNum = 2625;BA.debugLine="End Sub";
return "";
}
public static String  _lblsaltarpalabra_click() throws Exception{
 //BA.debugLineNum = 2628;BA.debugLine="Sub lblSaltarPalabra_Click";
 //BA.debugLineNum = 2629;BA.debugLine="imgSaltarPalabra_click";
_imgsaltarpalabra_click();
 //BA.debugLineNum = 2631;BA.debugLine="End Sub";
return "";
}
public static String  _lblv2def_click() throws Exception{
String _ssql = "";
 //BA.debugLineNum = 4743;BA.debugLine="Sub lblv2def_Click";
 //BA.debugLineNum = 4744;BA.debugLine="If Publicos.GetUsuario = \"JUAN.DAROCHA@GMAIL.COM\"";
if ((mostCurrent._vvvvvvvvvvvvvvv7._vvv4(mostCurrent.activityBA)).equals("JUAN.DAROCHA@GMAIL.COM")) { 
 //BA.debugLineNum = 4745;BA.debugLine="Dim ssql As String";
_ssql = "";
 //BA.debugLineNum = 4746;BA.debugLine="ssql = \"Update Avance set fin = null, adivinada=0";
_ssql = "Update Avance set fin = null, adivinada=0, Salteada =0 where idpalabra = (select max(idpalabra) from avance)";
 //BA.debugLineNum = 4747;BA.debugLine="g_sqlBaseLocalUsuario.ExecNonQuery(ssql)";
_g_sqlbaselocalusuario.ExecNonQuery(_ssql);
 };
 //BA.debugLineNum = 4750;BA.debugLine="End Sub";
return "";
}
public static String  _leadbolt_adclosed() throws Exception{
 //BA.debugLineNum = 1590;BA.debugLine="Sub Leadbolt_adClosed";
 //BA.debugLineNum = 1591;BA.debugLine="If gb_EsPremium = False Then";
if (_gb_espremium==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 1592;BA.debugLine="adBC.loadAd";
mostCurrent._vvvvvvvvvvvvvvvvvv1.loadAd();
 };
 //BA.debugLineNum = 1594;BA.debugLine="End Sub";
return "";
}
public static String  _leadbolt_adfailed() throws Exception{
 //BA.debugLineNum = 1580;BA.debugLine="Sub Leadbolt_adFailed";
 //BA.debugLineNum = 1582;BA.debugLine="End Sub";
return "";
}
public static String  _leadbolt_adloaded() throws Exception{
 //BA.debugLineNum = 1584;BA.debugLine="Sub Leadbolt_adLoaded";
 //BA.debugLineNum = 1586;BA.debugLine="End Sub";
return "";
}
public static boolean  _vvvvvvvvvvvvvvvvvvvvvvv6() throws Exception{
boolean _bret = false;
int _j = 0;
 //BA.debugLineNum = 1353;BA.debugLine="Sub LetraPalabraDisponible As Boolean";
 //BA.debugLineNum = 1354;BA.debugLine="Dim bret As Boolean = False";
_bret = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 1356;BA.debugLine="For j = 0 To lblArrayPalabra.Length-1";
{
final int step2 = 1;
final int limit2 = (int) (mostCurrent._vvvvvvvvvvvvvvvvvvv1.length-1);
for (_j = (int) (0) ; (step2 > 0 && _j <= limit2) || (step2 < 0 && _j >= limit2); _j = ((int)(0 + _j + step2)) ) {
 //BA.debugLineNum = 1358;BA.debugLine="If lblArrayPalabra(j).Text = \"\" Then";
if ((mostCurrent._vvvvvvvvvvvvvvvvvvv1[_j].getText()).equals("")) { 
 //BA.debugLineNum = 1359;BA.debugLine="bret = True";
_bret = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 1360;BA.debugLine="Exit";
if (true) break;
 };
 }
};
 //BA.debugLineNum = 1364;BA.debugLine="Return bret";
if (true) return _bret;
 //BA.debugLineNum = 1365;BA.debugLine="End Sub";
return false;
}
public static boolean  _letras_check(com.matetejuego.free.publicos._tletraselegir[] _paletraselegir,com.matetejuego.free.publicos._tletraspalabra[] _papletraspalabra) throws Exception{
com.matetejuego.free.publicos._tletraselegir[] _aletras = null;
boolean _bok = false;
int _j = 0;
int _i = 0;
 //BA.debugLineNum = 1517;BA.debugLine="Sub Letras_Check(paLetrasElegir() As tLetrasElegir";
 //BA.debugLineNum = 1518;BA.debugLine="Dim aLetras(paLetrasElegir.Length) As tLetrasElegi";
_aletras = new com.matetejuego.free.publicos._tletraselegir[_paletraselegir.length];
{
int d0 = _aletras.length;
for (int i0 = 0;i0 < d0;i0++) {
_aletras[i0] = new com.matetejuego.free.publicos._tletraselegir();
}
}
;
 //BA.debugLineNum = 1519;BA.debugLine="Dim bOk As Boolean";
_bok = false;
 //BA.debugLineNum = 1520;BA.debugLine="aLetras = paLetrasElegir";
_aletras = _paletraselegir;
 //BA.debugLineNum = 1523;BA.debugLine="For j = 0 To papLetrasPalabra.Length-1";
{
final int step4 = 1;
final int limit4 = (int) (_papletraspalabra.length-1);
for (_j = (int) (0) ; (step4 > 0 && _j <= limit4) || (step4 < 0 && _j >= limit4); _j = ((int)(0 + _j + step4)) ) {
 //BA.debugLineNum = 1524;BA.debugLine="For i = 0 To aLetras.Length-1";
{
final int step5 = 1;
final int limit5 = (int) (_aletras.length-1);
for (_i = (int) (0) ; (step5 > 0 && _i <= limit5) || (step5 < 0 && _i >= limit5); _i = ((int)(0 + _i + step5)) ) {
 //BA.debugLineNum = 1525;BA.debugLine="If papLetrasPalabra(j).letra = aLetras(i).LetraS";
if (_papletraspalabra[_j].letra==_aletras[_i].LetraSiempre) { 
 //BA.debugLineNum = 1526;BA.debugLine="aLetras(i).letrasiempre = \"\" ' borra la letra d";
_aletras[_i].LetraSiempre = BA.ObjectToChar("");
 //BA.debugLineNum = 1527;BA.debugLine="bOk = True '";
_bok = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 1528;BA.debugLine="Exit";
if (true) break;
 };
 }
};
 //BA.debugLineNum = 1532;BA.debugLine="If bOk = False Then";
if (_bok==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 1533;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 };
 }
};
 //BA.debugLineNum = 1536;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 1537;BA.debugLine="End Sub";
return false;
}
public static String  _vvvvvvvvvvvvvvvvvvvvvvv7(com.matetejuego.free.publicos._tletraspalabra[] _apletraspalabra) throws Exception{
String _ssql = "";
anywheresoftware.b4a.sql.SQL.CursorWrapper _cursorl = null;
String _sletras = "";
boolean _binventar = false;
String _sletrassiempre = "";
int _j = 0;
 //BA.debugLineNum = 1463;BA.debugLine="Sub LetrasCarga (apLetrasPalabra() As tLetrasPalab";
 //BA.debugLineNum = 1464;BA.debugLine="Dim sSql As String, CursorL As Cursor";
_ssql = "";
_cursorl = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
 //BA.debugLineNum = 1465;BA.debugLine="Dim sLetras As String=\"\", bInventar As Boolean =";
_sletras = "";
_binventar = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 1467;BA.debugLine="Dim aLetrasElegir(12) As tLetrasElegir, sLetras";
_vvvvvvvv0 = new com.matetejuego.free.publicos._tletraselegir[(int) (12)];
{
int d0 = _vvvvvvvv0.length;
for (int i0 = 0;i0 < d0;i0++) {
_vvvvvvvv0[i0] = new com.matetejuego.free.publicos._tletraselegir();
}
}
;
_sletrassiempre = "";
 //BA.debugLineNum = 1469;BA.debugLine="Try";
try { //BA.debugLineNum = 1470;BA.debugLine="sSql = \"Select Letra, Letrasiempre From LetrasPor";
_ssql = "Select Letra, Letrasiempre From LetrasPorPalabra  where idpalabra = "+BA.NumberToString(_vvvvvvvv1)+" order by posicion";
 //BA.debugLineNum = 1471;BA.debugLine="CursorL = g_sqlBaseLocalUsuario.ExecQuery(sSql)";
_cursorl.setObject((android.database.Cursor)(_g_sqlbaselocalusuario.ExecQuery(_ssql)));
 //BA.debugLineNum = 1472;BA.debugLine="If CursorL.RowCount>0  Then";
if (_cursorl.getRowCount()>0) { 
 //BA.debugLineNum = 1473;BA.debugLine="For j = 0 To CursorL.RowCount-1";
{
final int step8 = 1;
final int limit8 = (int) (_cursorl.getRowCount()-1);
for (_j = (int) (0) ; (step8 > 0 && _j <= limit8) || (step8 < 0 && _j >= limit8); _j = ((int)(0 + _j + step8)) ) {
 //BA.debugLineNum = 1474;BA.debugLine="CursorL.Position=j";
_cursorl.setPosition(_j);
 //BA.debugLineNum = 1475;BA.debugLine="sLetras = sLetras & CursorL.getstring2(0)";
_sletras = _sletras+_cursorl.GetString2((int) (0));
 //BA.debugLineNum = 1477;BA.debugLine="aLetrasElegir(j).LetraMovil = CursorL.getstring";
_vvvvvvvv0[_j].LetraMovil = BA.ObjectToChar(_cursorl.GetString2((int) (0)));
 //BA.debugLineNum = 1478;BA.debugLine="aLetrasElegir(j).LetraSiempre = CursorL.getstri";
_vvvvvvvv0[_j].LetraSiempre = BA.ObjectToChar(_cursorl.GetString2((int) (1)));
 //BA.debugLineNum = 1479;BA.debugLine="aLetrasElegir(j).LetraEnLabel = aLetrasElegir(j";
_vvvvvvvv0[_j].LetraEnLabel = _vvvvvvvv0[_j].LetraSiempre;
 }
};
 //BA.debugLineNum = 1483;BA.debugLine="bInventar = Not (Letras_Check(aLetrasElegir, apL";
_binventar = anywheresoftware.b4a.keywords.Common.Not(_letras_check(_vvvvvvvv0,_apletraspalabra));
 };
 //BA.debugLineNum = 1486;BA.debugLine="CursorL.Close";
_cursorl.Close();
 //BA.debugLineNum = 1488;BA.debugLine="If bInventar Then ' no estaban grabadas, es la pr";
if (_binventar) { 
 //BA.debugLineNum = 1489;BA.debugLine="sLetras = \"\"";
_sletras = "";
 //BA.debugLineNum = 1490;BA.debugLine="For j = 0 To apLetrasPalabra.Length-1";
{
final int step20 = 1;
final int limit20 = (int) (_apletraspalabra.length-1);
for (_j = (int) (0) ; (step20 > 0 && _j <= limit20) || (step20 < 0 && _j >= limit20); _j = ((int)(0 + _j + step20)) ) {
 //BA.debugLineNum = 1491;BA.debugLine="If apLetrasPalabra(j).comprada Then";
if (_apletraspalabra[_j].comprada) { 
 //BA.debugLineNum = 1492;BA.debugLine="sLetras = sLetras & \"?\"";
_sletras = _sletras+"?";
 }else {
 //BA.debugLineNum = 1494;BA.debugLine="sLetras=sLetras& apLetrasPalabra(j).letra";
_sletras = _sletras+BA.ObjectToString(_apletraspalabra[_j].letra);
 };
 }
};
 //BA.debugLineNum = 1499;BA.debugLine="sLetras = LetrasInventa(sLetras)";
_sletras = _vvvvvvvvvvvvvvvvvvvvvvv0(_sletras);
 //BA.debugLineNum = 1500;BA.debugLine="LetrasGraba(sLetras, igIDPalabra)";
_vvvvvvvvvvvvvvvvvvvvvvvv1(_sletras,_vvvvvvvv1);
 };
 //BA.debugLineNum = 1503;BA.debugLine="For j=0 To sLetras.Length-1";
{
final int step30 = 1;
final int limit30 = (int) (_sletras.length()-1);
for (_j = (int) (0) ; (step30 > 0 && _j <= limit30) || (step30 < 0 && _j >= limit30); _j = ((int)(0 + _j + step30)) ) {
 //BA.debugLineNum = 1504;BA.debugLine="aLetrasElegir(j).LetraMovil= sLetras.SubString2(";
_vvvvvvvv0[_j].LetraMovil = BA.ObjectToChar(_sletras.substring(_j,(int) (_j+1)));
 //BA.debugLineNum = 1505;BA.debugLine="aLetrasElegir(j).LetraSiempre = sLetras.SubStrin";
_vvvvvvvv0[_j].LetraSiempre = BA.ObjectToChar(_sletras.substring(_j,(int) (_j+1)));
 //BA.debugLineNum = 1506;BA.debugLine="aLetrasElegir(j).LetraEnLabel = sLetras.SubStrin";
_vvvvvvvv0[_j].LetraEnLabel = BA.ObjectToChar(_sletras.substring(_j,(int) (_j+1)));
 }
};
 } 
       catch (Exception e36) {
			processBA.setLastException(e36); //BA.debugLineNum = 1510;BA.debugLine="ManejaErrorJugar(\"Letras Carga\", \"Cargando letra";
_vvvvvvvvvvvvvvvvvv3("Letras Carga","Cargando letras");
 };
 //BA.debugLineNum = 1513;BA.debugLine="Return sLetras";
if (true) return _sletras;
 //BA.debugLineNum = 1514;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvvvvvvvvvvv1(String _sletras,int _idpalabra) throws Exception{
String _ssql = "";
int _j = 0;
 //BA.debugLineNum = 1539;BA.debugLine="Sub LetrasGraba(sLetras As String, idPalabra As In";
 //BA.debugLineNum = 1540;BA.debugLine="Dim sSql As String";
_ssql = "";
 //BA.debugLineNum = 1542;BA.debugLine="Try";
try { //BA.debugLineNum = 1544;BA.debugLine="sSql=\"Delete from LetrasPorPalabra where idpalabr";
_ssql = "Delete from LetrasPorPalabra where idpalabra="+BA.NumberToString(_idpalabra);
 //BA.debugLineNum = 1545;BA.debugLine="g_sqlBaseLocalUsuario.ExecNonQuery(sSql)";
_g_sqlbaselocalusuario.ExecNonQuery(_ssql);
 //BA.debugLineNum = 1547;BA.debugLine="g_sqlBaseLocalUsuario.BeginTransaction";
_g_sqlbaselocalusuario.BeginTransaction();
 //BA.debugLineNum = 1548;BA.debugLine="For j =0 To sLetras.Length-1";
{
final int step6 = 1;
final int limit6 = (int) (_sletras.length()-1);
for (_j = (int) (0) ; (step6 > 0 && _j <= limit6) || (step6 < 0 && _j >= limit6); _j = ((int)(0 + _j + step6)) ) {
 //BA.debugLineNum = 1549;BA.debugLine="sSql = \"Insert Into LetrasPorPalabra (idPalabra,";
_ssql = "Insert Into LetrasPorPalabra (idPalabra, Posicion, Letra, LetraSiempre, PosicionPalabra) values ("+BA.NumberToString(_idpalabra)+","+BA.NumberToString(_j)+",'";
 //BA.debugLineNum = 1550;BA.debugLine="sSql = sSql & sLetras.SubString2(j, j+1) & \"','\"";
_ssql = _ssql+_sletras.substring(_j,(int) (_j+1))+"','"+_sletras.substring(_j,(int) (_j+1))+"', -1)";
 //BA.debugLineNum = 1552;BA.debugLine="g_sqlBaseLocalUsuario.ExecNonQuery(sSql)";
_g_sqlbaselocalusuario.ExecNonQuery(_ssql);
 }
};
 //BA.debugLineNum = 1554;BA.debugLine="g_sqlBaseLocalUsuario.TransactionSuccessful";
_g_sqlbaselocalusuario.TransactionSuccessful();
 //BA.debugLineNum = 1555;BA.debugLine="g_sqlBaseLocalUsuario.EndTransaction";
_g_sqlbaselocalusuario.EndTransaction();
 } 
       catch (Exception e14) {
			processBA.setLastException(e14); //BA.debugLineNum = 1558;BA.debugLine="ManejaErrorJugar(\"LetrasGraba\",\"\")";
_vvvvvvvvvvvvvvvvvv3("LetrasGraba","");
 //BA.debugLineNum = 1559;BA.debugLine="g_sqlBaseLocalUsuario.EndTransaction";
_g_sqlbaselocalusuario.EndTransaction();
 };
 //BA.debugLineNum = 1563;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvvvvvvvvvv0(String _sletraspalabra) throws Exception{
String _sret = "";
int _irnd = 0;
String[] _sletras = null;
String[] _svocales = null;
String[] _sabc = null;
com.matetejuego.free.publicos._tordenar[] _iaorden = null;
com.matetejuego.free.publicos._tordenar _iaux = null;
int _i = 0;
int _j = 0;
 //BA.debugLineNum = 942;BA.debugLine="Sub LetrasInventa (sLetrasPalabra As String) As St";
 //BA.debugLineNum = 943;BA.debugLine="Dim sRet As String, iRnd As Int";
_sret = "";
_irnd = 0;
 //BA.debugLineNum = 944;BA.debugLine="Dim sLetras(iCantLetras) As String";
_sletras = new String[_vvvvvvv7];
java.util.Arrays.fill(_sletras,"");
 //BA.debugLineNum = 946;BA.debugLine="Dim sVocales() As String = Array As String (\"A\",\"";
_svocales = new String[]{"A","E","I","O","U"};
 //BA.debugLineNum = 947;BA.debugLine="Dim sABC() As String = Array As String (\"A\",\"B\",\"";
_sabc = new String[]{"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","X","Y","Z","A","E","I","O","U","N","S","T","D","M","L"};
 //BA.debugLineNum = 952;BA.debugLine="Dim iaOrden (iCantLetras) As tOrdenar, iAux  As t";
_iaorden = new com.matetejuego.free.publicos._tordenar[_vvvvvvv7];
{
int d0 = _iaorden.length;
for (int i0 = 0;i0 < d0;i0++) {
_iaorden[i0] = new com.matetejuego.free.publicos._tordenar();
}
}
;
_iaux = new com.matetejuego.free.publicos._tordenar();
 //BA.debugLineNum = 955;BA.debugLine="For i = 0 To sLetrasPalabra.Length-1";
{
final int step6 = 1;
final int limit6 = (int) (_sletraspalabra.length()-1);
for (_i = (int) (0) ; (step6 > 0 && _i <= limit6) || (step6 < 0 && _i >= limit6); _i = ((int)(0 + _i + step6)) ) {
 //BA.debugLineNum = 956;BA.debugLine="iaOrden(i).Initialize";
_iaorden[_i].Initialize();
 //BA.debugLineNum = 957;BA.debugLine="iaOrden(i).letra = sLetrasPalabra.SubString2(i,i";
_iaorden[_i].Letra = BA.ObjectToChar(_sletraspalabra.substring(_i,(int) (_i+1)));
 //BA.debugLineNum = 958;BA.debugLine="iaOrden(i).Orden = Rnd(0,97)";
_iaorden[_i].Orden = anywheresoftware.b4a.keywords.Common.Rnd((int) (0),(int) (97));
 //BA.debugLineNum = 959;BA.debugLine="iaOrden(i).Indice = i";
_iaorden[_i].Indice = _i;
 }
};
 //BA.debugLineNum = 963;BA.debugLine="If sLetrasPalabra.Length < iCantLetras Then";
if (_sletraspalabra.length()<_vvvvvvv7) { 
 //BA.debugLineNum = 966;BA.debugLine="iRnd = Rnd(0,4)";
_irnd = anywheresoftware.b4a.keywords.Common.Rnd((int) (0),(int) (4));
 //BA.debugLineNum = 967;BA.debugLine="sLetras(sLetrasPalabra.Length) = sVocales(iRnd)";
_sletras[_sletraspalabra.length()] = _svocales[_irnd];
 //BA.debugLineNum = 968;BA.debugLine="For i = sLetrasPalabra.Length To iCantLetras -1";
{
final int step15 = 1;
final int limit15 = (int) (_vvvvvvv7-1);
for (_i = _sletraspalabra.length() ; (step15 > 0 && _i <= limit15) || (step15 < 0 && _i >= limit15); _i = ((int)(0 + _i + step15)) ) {
 //BA.debugLineNum = 969;BA.debugLine="iRnd = Rnd(0,sABC.Length-1)";
_irnd = anywheresoftware.b4a.keywords.Common.Rnd((int) (0),(int) (_sabc.length-1));
 //BA.debugLineNum = 970;BA.debugLine="iaOrden(i).Indice = i";
_iaorden[_i].Indice = _i;
 //BA.debugLineNum = 971;BA.debugLine="iaOrden(i).Orden = Rnd(0,97)";
_iaorden[_i].Orden = anywheresoftware.b4a.keywords.Common.Rnd((int) (0),(int) (97));
 //BA.debugLineNum = 972;BA.debugLine="iaOrden(i).Letra=sABC(iRnd)";
_iaorden[_i].Letra = BA.ObjectToChar(_sabc[_irnd]);
 }
};
 };
 //BA.debugLineNum = 978;BA.debugLine="For i = 0 To iaOrden.Length-2";
{
final int step22 = 1;
final int limit22 = (int) (_iaorden.length-2);
for (_i = (int) (0) ; (step22 > 0 && _i <= limit22) || (step22 < 0 && _i >= limit22); _i = ((int)(0 + _i + step22)) ) {
 //BA.debugLineNum = 979;BA.debugLine="For j = i +1 To iaOrden.Length-1";
{
final int step23 = 1;
final int limit23 = (int) (_iaorden.length-1);
for (_j = (int) (_i+1) ; (step23 > 0 && _j <= limit23) || (step23 < 0 && _j >= limit23); _j = ((int)(0 + _j + step23)) ) {
 //BA.debugLineNum = 980;BA.debugLine="If iaOrden(i).Orden > iaOrden(j).Orden Then";
if (_iaorden[_i].Orden>_iaorden[_j].Orden) { 
 //BA.debugLineNum = 981;BA.debugLine="iAux.Initialize";
_iaux.Initialize();
 //BA.debugLineNum = 982;BA.debugLine="iAux.Indice = iaOrden(i).indice";
_iaux.Indice = _iaorden[_i].Indice;
 //BA.debugLineNum = 983;BA.debugLine="iAux.Letra= iaOrden(i).Letra";
_iaux.Letra = _iaorden[_i].Letra;
 //BA.debugLineNum = 984;BA.debugLine="iAux.Orden= iaOrden(i).Orden";
_iaux.Orden = _iaorden[_i].Orden;
 //BA.debugLineNum = 987;BA.debugLine="iaOrden(i).Indice = iaOrden(j).Indice";
_iaorden[_i].Indice = _iaorden[_j].Indice;
 //BA.debugLineNum = 988;BA.debugLine="iaOrden(i).Letra = iaOrden(j).Letra";
_iaorden[_i].Letra = _iaorden[_j].Letra;
 //BA.debugLineNum = 989;BA.debugLine="iaOrden(i).orden = iaOrden(j).orden";
_iaorden[_i].Orden = _iaorden[_j].Orden;
 //BA.debugLineNum = 991;BA.debugLine="iaOrden(j).Indice = iAux.Indice";
_iaorden[_j].Indice = _iaux.Indice;
 //BA.debugLineNum = 992;BA.debugLine="iaOrden(j).Letra = iAux.Letra";
_iaorden[_j].Letra = _iaux.Letra;
 //BA.debugLineNum = 993;BA.debugLine="iaOrden(j).Orden = iAux.orden";
_iaorden[_j].Orden = _iaux.Orden;
 };
 }
};
 }
};
 //BA.debugLineNum = 999;BA.debugLine="sRet = \"\"";
_sret = "";
 //BA.debugLineNum = 1000;BA.debugLine="For i = 0 To iCantLetras -1";
{
final int step39 = 1;
final int limit39 = (int) (_vvvvvvv7-1);
for (_i = (int) (0) ; (step39 > 0 && _i <= limit39) || (step39 < 0 && _i >= limit39); _i = ((int)(0 + _i + step39)) ) {
 //BA.debugLineNum = 1001;BA.debugLine="sRet = sRet & iaOrden(i).Letra";
_sret = _sret+BA.ObjectToString(_iaorden[_i].Letra);
 }
};
 //BA.debugLineNum = 1004;BA.debugLine="Return sRet";
if (true) return _sret;
 //BA.debugLineNum = 1005;BA.debugLine="End Sub";
return "";
}
public static String  _manager_billingsupported(boolean _supported,String _message) throws Exception{
 //BA.debugLineNum = 3270;BA.debugLine="Sub manager_BillingSupported (Supported As Boolean";
 //BA.debugLineNum = 3271;BA.debugLine="Log(\"Supported: \" & Supported & \", \" & Message)";
anywheresoftware.b4a.keywords.Common.Log("Supported: "+BA.ObjectToString(_supported)+", "+_message);
 //BA.debugLineNum = 3272;BA.debugLine="Log(\"Subscriptions supported: \" & manager.Subsc";
anywheresoftware.b4a.keywords.Common.Log("Subscriptions supported: "+BA.ObjectToString(_vvvvvv2.getSubscriptionsSupported()));
 //BA.debugLineNum = 3274;BA.debugLine="If Supported Then";
if (_supported) { 
 //BA.debugLineNum = 3275;BA.debugLine="manager.GetOwnedProducts";
_vvvvvv2.GetOwnedProducts(processBA);
 }else {
 };
 //BA.debugLineNum = 3280;BA.debugLine="End Sub";
return "";
}
public static String  _manager_ownedproducts(boolean _success,anywheresoftware.b4a.objects.collections.Map _purchases) throws Exception{
int _icant = 0;
anywheresoftware.b4a.inappbilling3.BillingManager3.Prchase _p = null;
 //BA.debugLineNum = 3284;BA.debugLine="Sub manager_OwnedProducts (Success As Boolean, pur";
 //BA.debugLineNum = 3285;BA.debugLine="Dim iCant As Int=0";
_icant = (int) (0);
 //BA.debugLineNum = 3289;BA.debugLine="Log(\"Cant comprados:\" & purchases.size)";
anywheresoftware.b4a.keywords.Common.Log("Cant comprados:"+BA.NumberToString(_purchases.getSize()));
 //BA.debugLineNum = 3290;BA.debugLine="If Success Then";
if (_success) { 
 //BA.debugLineNum = 3291;BA.debugLine="Log(purchases)";
anywheresoftware.b4a.keywords.Common.Log(BA.ObjectToString(_purchases));
 //BA.debugLineNum = 3292;BA.debugLine="For Each P As Purchase In purchases.Values";
final anywheresoftware.b4a.BA.IterableList group5 = _purchases.Values();
final int groupLen5 = group5.getSize();
for (int index5 = 0;index5 < groupLen5 ;index5++){
_p = (anywheresoftware.b4a.inappbilling3.BillingManager3.Prchase)(group5.Get(index5));
 //BA.debugLineNum = 3293;BA.debugLine="iCant = iCant +1";
_icant = (int) (_icant+1);
 //BA.debugLineNum = 3294;BA.debugLine="Dim aProductosComprados(iCant) As tProductosComp";
_vvvvvv3 = new com.matetejuego.free.publicos._tproductoscomprados[_icant];
{
int d0 = _vvvvvv3.length;
for (int i0 = 0;i0 < d0;i0++) {
_vvvvvv3[i0] = new com.matetejuego.free.publicos._tproductoscomprados();
}
}
;
 //BA.debugLineNum = 3295;BA.debugLine="Log(P.ProductId & \", Purchased? \" & (P.PurchaseS";
anywheresoftware.b4a.keywords.Common.Log(_p.getProductId()+", Purchased? "+BA.ObjectToString((_p.getPurchaseState()==_p.STATE_PURCHASED)));
 //BA.debugLineNum = 3297;BA.debugLine="aProductosComprados(iCant-1).ProductID = P.Produ";
_vvvvvv3[(int) (_icant-1)].ProductID = _p.getProductId();
 //BA.debugLineNum = 3298;BA.debugLine="aProductosComprados(iCant-1).State = P.PurchaseS";
_vvvvvv3[(int) (_icant-1)].State = _p.getPurchaseState();
 //BA.debugLineNum = 3299;BA.debugLine="aProductosComprados(iCant-1).Owned = P.PurchaseS";
_vvvvvv3[(int) (_icant-1)].Owned = _p.getPurchaseState()==_p.STATE_PURCHASED;
 }
;
 };
 //BA.debugLineNum = 3304;BA.debugLine="Productos_CargaPlayStore";
_productos_cargaplaystore();
 //BA.debugLineNum = 3307;BA.debugLine="If Productos_ConsumirPendientes (purchases) = Fal";
if (_productos_consumirpendientes(_purchases)==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 3309;BA.debugLine="Productos_MarcaYaComprados";
_productos_marcayacomprados();
 }else {
 //BA.debugLineNum = 3311;BA.debugLine="Neuronas_Mostrar";
_neuronas_mostrar();
 };
 //BA.debugLineNum = 3313;BA.debugLine="End Sub";
return "";
}
public static String  _manager_purchasecompleted(boolean _success,anywheresoftware.b4a.inappbilling3.BillingManager3.Prchase _product) throws Exception{
 //BA.debugLineNum = 3316;BA.debugLine="Sub manager_PurchaseCompleted (Success As Boolean,";
 //BA.debugLineNum = 3317;BA.debugLine="Try";
try { //BA.debugLineNum = 3322;BA.debugLine="If Success Then";
if (_success) { 
 //BA.debugLineNum = 3324;BA.debugLine="If Product.PurchaseState = Product.STATE_PURCHAS";
if (_product.getPurchaseState()==_product.STATE_PURCHASED) { 
 //BA.debugLineNum = 3325;BA.debugLine="Productos_CompraHacer (Product)";
_productos_comprahacer(_product);
 }else {
 //BA.debugLineNum = 3327;BA.debugLine="Msgbox(\"Su compra no fue procesada\", \"Play Stor";
anywheresoftware.b4a.keywords.Common.Msgbox("Su compra no fue procesada","Play Store",mostCurrent.activityBA);
 };
 }else {
 //BA.debugLineNum = 3331;BA.debugLine="ToastMessageShow(\"Compra no procesada\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Compra no procesada",anywheresoftware.b4a.keywords.Common.True);
 };
 } 
       catch (Exception e12) {
			processBA.setLastException(e12); //BA.debugLineNum = 3334;BA.debugLine="ToastMessageShow(\"Error en la compra!\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Error en la compra!",anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 3336;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvvvvv3(String _srutina,String _serror) throws Exception{
 //BA.debugLineNum = 3956;BA.debugLine="Sub ManejaErrorJugar(sRutina As String, sError As";
 //BA.debugLineNum = 3959;BA.debugLine="Publicos.ManejaError(sRutina, sError)";
mostCurrent._vvvvvvvvvvvvvvv7._vvv7(mostCurrent.activityBA,_srutina,_serror);
 //BA.debugLineNum = 3963;BA.debugLine="End Sub";
return "";
}
public static String  _neuronas_mostrar() throws Exception{
anywheresoftware.b4a.sql.SQL.CursorWrapper _cursor1 = null;
String _ssqlcipher = "";
String _ssql = "";
String _smonedas = "";
String _spuntos = "";
String _sjugadas = "";
 //BA.debugLineNum = 458;BA.debugLine="Sub Neuronas_Mostrar";
 //BA.debugLineNum = 460;BA.debugLine="Dim Cursor1 As Cursor, sSqlCipher As String, ssql";
_cursor1 = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
_ssqlcipher = "";
_ssql = "";
_smonedas = "";
_spuntos = "";
_sjugadas = "";
 //BA.debugLineNum = 461;BA.debugLine="Try";
try { //BA.debugLineNum = 463;BA.debugLine="ssql =\"Select monedas, puntos, idnivel From usua";
_ssql = "Select monedas, puntos, idnivel From usuario";
 //BA.debugLineNum = 464;BA.debugLine="Cursor1 = g_sqlBaseLocalUsuario.ExecQuery(ssql)";
_cursor1.setObject((android.database.Cursor)(_g_sqlbaselocalusuario.ExecQuery(_ssql)));
 //BA.debugLineNum = 466;BA.debugLine="If Cursor1.RowCount >0 Then";
if (_cursor1.getRowCount()>0) { 
 //BA.debugLineNum = 467;BA.debugLine="Cursor1.Position = 0";
_cursor1.setPosition((int) (0));
 //BA.debugLineNum = 468;BA.debugLine="g_iMonedas = Cursor1.GetInt2(0)";
_g_imonedas = _cursor1.GetInt2((int) (0));
 //BA.debugLineNum = 472;BA.debugLine="lblNeuronas.Text = g_iMonedas";
mostCurrent._lblneuronas.setText((Object)(_g_imonedas));
 //BA.debugLineNum = 474;BA.debugLine="g_iJugadas = Publicos.get_CantidadPalabrasJugad";
_g_ijugadas = mostCurrent._vvvvvvvvvvvvvvv7._get_cantidadpalabrasjugadas(mostCurrent.activityBA,(anywheresoftware.b4a.sql.SQL)(_g_sqlbaselocalusuario));
 //BA.debugLineNum = 475;BA.debugLine="g_iPalabras	 = Publicos.get_CantidadPalabras(g_";
_g_ipalabras = mostCurrent._vvvvvvvvvvvvvvv7._get_cantidadpalabras(mostCurrent.activityBA,_g_sqlbaselocaljuego);
 //BA.debugLineNum = 477;BA.debugLine="lblAvance.Text = g_iJugadas & \"/\" & g_iPalabras";
mostCurrent._lblavance.setText((Object)(BA.NumberToString(_g_ijugadas)+"/"+BA.NumberToString(_g_ipalabras)));
 };
 //BA.debugLineNum = 485;BA.debugLine="Cursor1.Close";
_cursor1.Close();
 } 
       catch (Exception e15) {
			processBA.setLastException(e15); //BA.debugLineNum = 488;BA.debugLine="ManejaErrorJugar(\"MuestraPuntos\",\"MuestraPuntos\"";
_vvvvvvvvvvvvvvvvvv3("MuestraPuntos","MuestraPuntos");
 };
 //BA.debugLineNum = 491;BA.debugLine="End Sub";
return "";
}
public static String  _palabra_cargaletras(String _spalabra,int _idpalabra) throws Exception{
String _ssql = "";
anywheresoftware.b4a.sql.SQL.CursorWrapper _cursor1 = null;
int _ipos = 0;
int _i = 0;
int _j = 0;
 //BA.debugLineNum = 1279;BA.debugLine="Sub Palabra_CargaLetras(sPalabra As String, idPala";
 //BA.debugLineNum = 1282;BA.debugLine="Dim  sSql As String, Cursor1 As Cursor";
_ssql = "";
_cursor1 = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
 //BA.debugLineNum = 1283;BA.debugLine="Dim aLetrasPalabra(sPalabra.Length) As tLetrasPal";
_vvvvvvvv7 = new com.matetejuego.free.publicos._tletraspalabra[_spalabra.length()];
{
int d0 = _vvvvvvvv7.length;
for (int i0 = 0;i0 < d0;i0++) {
_vvvvvvvv7[i0] = new com.matetejuego.free.publicos._tletraspalabra();
}
}
;
 //BA.debugLineNum = 1284;BA.debugLine="Dim iPos As Int";
_ipos = 0;
 //BA.debugLineNum = 1285;BA.debugLine="Try";
try { //BA.debugLineNum = 1287;BA.debugLine="For i = 0 To sPalabra.Length-1";
{
final int step5 = 1;
final int limit5 = (int) (_spalabra.length()-1);
for (_i = (int) (0) ; (step5 > 0 && _i <= limit5) || (step5 < 0 && _i >= limit5); _i = ((int)(0 + _i + step5)) ) {
 //BA.debugLineNum = 1288;BA.debugLine="aLetrasPalabra(i).letra = sPalabra.CharAt(i)";
_vvvvvvvv7[_i].letra = _spalabra.charAt(_i);
 //BA.debugLineNum = 1289;BA.debugLine="aLetrasPalabra(i).posicion= i+1";
_vvvvvvvv7[_i].posicion = (int) (_i+1);
 //BA.debugLineNum = 1290;BA.debugLine="aLetrasPalabra(i).comprada = False";
_vvvvvvvv7[_i].comprada = anywheresoftware.b4a.keywords.Common.False;
 }
};
 //BA.debugLineNum = 1294;BA.debugLine="sSql =\"select posicion from letrascompradas where";
_ssql = "select posicion from letrascompradas where idpalabra ="+BA.NumberToString(_idpalabra);
 //BA.debugLineNum = 1295;BA.debugLine="Cursor1 = g_sqlBaseLocalUsuario.ExecQuery(sSql)";
_cursor1.setObject((android.database.Cursor)(_g_sqlbaselocalusuario.ExecQuery(_ssql)));
 //BA.debugLineNum = 1296;BA.debugLine="For j = 0 To Cursor1.RowCount-1";
{
final int step12 = 1;
final int limit12 = (int) (_cursor1.getRowCount()-1);
for (_j = (int) (0) ; (step12 > 0 && _j <= limit12) || (step12 < 0 && _j >= limit12); _j = ((int)(0 + _j + step12)) ) {
 //BA.debugLineNum = 1297;BA.debugLine="Cursor1.Position=j";
_cursor1.setPosition(_j);
 //BA.debugLineNum = 1298;BA.debugLine="iPos = Cursor1.GetInt2(0)";
_ipos = _cursor1.GetInt2((int) (0));
 //BA.debugLineNum = 1299;BA.debugLine="aLetrasPalabra(iPos).comprada = True";
_vvvvvvvv7[_ipos].comprada = anywheresoftware.b4a.keywords.Common.True;
 }
};
 //BA.debugLineNum = 1301;BA.debugLine="Cursor1.Close";
_cursor1.Close();
 } 
       catch (Exception e19) {
			processBA.setLastException(e19); //BA.debugLineNum = 1304;BA.debugLine="ManejaErrorJugar(\"Palabras_CargaLetras\",\"Error Pa";
_vvvvvvvvvvvvvvvvvv3("Palabras_CargaLetras","Error Palabra_cargaletras");
 };
 //BA.debugLineNum = 1306;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvvvvvvvvvvv2(boolean _bprender,boolean _btransparente) throws Exception{
 //BA.debugLineNum = 393;BA.debugLine="Sub PantallaEspera(bPrender As Boolean, bTranspare";
 //BA.debugLineNum = 394;BA.debugLine="If bPrender Then";
if (_bprender) { 
 //BA.debugLineNum = 395;BA.debugLine="Publicos.ViewComoActivity(Activity, lblBloqueo)";
mostCurrent._vvvvvvvvvvvvvvv7._vvvvv1(mostCurrent.activityBA,mostCurrent._activity,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._vvvvvvvvvvvvvvvvvvv4.getObject())));
 //BA.debugLineNum = 396;BA.debugLine="lblBloqueo.BringToFront";
mostCurrent._vvvvvvvvvvvvvvvvvvv4.BringToFront();
 };
 //BA.debugLineNum = 398;BA.debugLine="If bTransparente Then";
if (_btransparente) { 
 //BA.debugLineNum = 399;BA.debugLine="lblBloqueo.Color = Colors.Transparent";
mostCurrent._vvvvvvvvvvvvvvvvvvv4.setColor(anywheresoftware.b4a.keywords.Common.Colors.Transparent);
 }else {
 //BA.debugLineNum = 401;BA.debugLine="lblBloqueo.Color = Colors.black";
mostCurrent._vvvvvvvvvvvvvvvvvvv4.setColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 };
 //BA.debugLineNum = 403;BA.debugLine="lblBloqueo.Visible = bPrender";
mostCurrent._vvvvvvvvvvvvvvvvvvv4.setVisible(_bprender);
 //BA.debugLineNum = 404;BA.debugLine="End Sub";
return "";
}
public static String  _pnlinvisible_click() throws Exception{
 //BA.debugLineNum = 3531;BA.debugLine="Sub pnlInvisible_click";
 //BA.debugLineNum = 3533;BA.debugLine="End Sub";
return "";
}
public static String  _pnlitemp_click() throws Exception{
anywheresoftware.b4a.objects.PanelWrapper _send = null;
int _ipos = 0;
 //BA.debugLineNum = 3428;BA.debugLine="Sub pnlItemP_Click";
 //BA.debugLineNum = 3429;BA.debugLine="Dim Send As Panel, iPos As Int";
_send = new anywheresoftware.b4a.objects.PanelWrapper();
_ipos = 0;
 //BA.debugLineNum = 3430;BA.debugLine="Send = Sender";
_send.setObject((android.view.ViewGroup)(anywheresoftware.b4a.keywords.Common.Sender(mostCurrent.activityBA)));
 //BA.debugLineNum = 3438;BA.debugLine="iProductoElegido = Send.tag";
_vvvvvv5 = (int)(BA.ObjectToNumber(_send.getTag()));
 //BA.debugLineNum = 3439;BA.debugLine="Producto_Procesar(aProductosPlayStore(iProductoEl";
_producto_procesar(_vvvvvv4[_vvvvvv5]);
 //BA.debugLineNum = 3440;BA.debugLine="iPantallaActiva = xConfiguraPantalla.jugar";
_vvvvvvvv4 = _vvvvvvvv3.Jugar;
 //BA.debugLineNum = 3441;BA.debugLine="End Sub";
return "";
}
public static String  _premium_muestra() throws Exception{
int[] _icolor = null;
int _icolorletra = 0;
anywheresoftware.b4a.objects.PanelWrapper _pnlrecuadro = null;
anywheresoftware.b4a.objects.PanelWrapper _pnlcontiene = null;
anywheresoftware.b4a.objects.LabelWrapper _lbltitulop = null;
anywheresoftware.b4a.objects.ImageViewWrapper _imgsuperluper = null;
anywheresoftware.b4a.objects.PanelWrapper _pnlincluye = null;
anywheresoftware.b4a.objects.LabelWrapper[] _lblincluye = null;
int _iheightlbl = 0;
int _isepara = 0;
int _j = 0;
String _spalabrasnuevas = "";
int _itextsize = 0;
anywheresoftware.b4a.objects.LabelWrapper _lblconfirmar = null;
anywheresoftware.b4a.objects.LabelWrapper _lblcancelarp = null;
int _itopprod = 0;
int _iheightprod = 0;
int _ileft = 0;
int _itop = 0;
int _iwidth = 0;
String _iheight = "";
 //BA.debugLineNum = 4048;BA.debugLine="Sub Premium_Muestra";
 //BA.debugLineNum = 4050;BA.debugLine="iPantallaActiva = xConfiguraPantalla.Premium";
_vvvvvvvv4 = _vvvvvvvv3.Premium;
 //BA.debugLineNum = 4052;BA.debugLine="Dim iColor (3) As Int, iColorLetra As Int";
_icolor = new int[(int) (3)];
;
_icolorletra = 0;
 //BA.debugLineNum = 4053;BA.debugLine="iColor(0) = Colors.RGB(247, 219, 245)";
_icolor[(int) (0)] = anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (247),(int) (219),(int) (245));
 //BA.debugLineNum = 4054;BA.debugLine="iColor(1) = Colors.RGB(242, 198, 241)";
_icolor[(int) (1)] = anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (242),(int) (198),(int) (241));
 //BA.debugLineNum = 4055;BA.debugLine="iColor(2) = Colors.RGB(238, 183, 237)";
_icolor[(int) (2)] = anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (238),(int) (183),(int) (237));
 //BA.debugLineNum = 4056;BA.debugLine="iColorLetra = Get_aTextColor(gi_CombinacionColores";
_icolorletra = _get_atextcolor(_gi_combinacioncolores,(int) (1));
 //BA.debugLineNum = 4059;BA.debugLine="pnlInvisible.color = Colors.transparent";
mostCurrent._pnlinvisible.setColor(anywheresoftware.b4a.keywords.Common.Colors.Transparent);
 //BA.debugLineNum = 4060;BA.debugLine="pnlInvisible.BringToFront";
mostCurrent._pnlinvisible.BringToFront();
 //BA.debugLineNum = 4064;BA.debugLine="Dim pnlRecuadro As Panel";
_pnlrecuadro = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 4065;BA.debugLine="pnlRecuadro.Initialize(\"\")";
_pnlrecuadro.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 4066;BA.debugLine="pnlRecuadro.Color = Colors.LightGray";
_pnlrecuadro.setColor(anywheresoftware.b4a.keywords.Common.Colors.LightGray);
 //BA.debugLineNum = 4068;BA.debugLine="pnlInvisible.AddView(pnlRecuadro, 0,0,0,0)";
mostCurrent._pnlinvisible.AddView((android.view.View)(_pnlrecuadro.getObject()),(int) (0),(int) (0),(int) (0),(int) (0));
 //BA.debugLineNum = 4069;BA.debugLine="pnlRecuadro.Height = pnlInvisible.Height";
_pnlrecuadro.setHeight(mostCurrent._pnlinvisible.getHeight());
 //BA.debugLineNum = 4070;BA.debugLine="pnlRecuadro.Width = pnlInvisible.Width";
_pnlrecuadro.setWidth(mostCurrent._pnlinvisible.getWidth());
 //BA.debugLineNum = 4071;BA.debugLine="Publicos.CentrarControlEnPanel(pnlInvisible, pnl";
mostCurrent._vvvvvvvvvvvvvvv7._vv0(mostCurrent.activityBA,mostCurrent._pnlinvisible,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(_pnlrecuadro.getObject())),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 4073;BA.debugLine="Dim pnlContiene As Panel";
_pnlcontiene = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 4074;BA.debugLine="pnlContiene.Initialize(\"\")";
_pnlcontiene.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 4076;BA.debugLine="pnlRecuadro.AddView(pnlContiene, 0,0,0,0)";
_pnlrecuadro.AddView((android.view.View)(_pnlcontiene.getObject()),(int) (0),(int) (0),(int) (0),(int) (0));
 //BA.debugLineNum = 4077;BA.debugLine="pnlContiene.Height = pnlRecuadro.Height-4dip";
_pnlcontiene.setHeight((int) (_pnlrecuadro.getHeight()-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (4))));
 //BA.debugLineNum = 4078;BA.debugLine="pnlContiene.Width = pnlRecuadro.Width-4dip";
_pnlcontiene.setWidth((int) (_pnlrecuadro.getWidth()-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (4))));
 //BA.debugLineNum = 4079;BA.debugLine="Publicos.CentrarControlEnPanel(pnlRecuadro, pnlC";
mostCurrent._vvvvvvvvvvvvvvv7._vv0(mostCurrent.activityBA,_pnlrecuadro,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(_pnlcontiene.getObject())),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 4084;BA.debugLine="Dim lblTituloP As Label";
_lbltitulop = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 4085;BA.debugLine="lblTituloP.Initialize(\"\")";
_lbltitulop.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 4086;BA.debugLine="pnlContiene.AddView(lblTituloP , 0,0,pnlContiene.";
_pnlcontiene.AddView((android.view.View)(_lbltitulop.getObject()),(int) (0),(int) (0),_pnlcontiene.getWidth(),(int) (_pnlcontiene.getHeight()*0.10));
 //BA.debugLineNum = 4087;BA.debugLine="lblTituloP.Top = 0 'imgSuperluper.Top + imgSuperl";
_lbltitulop.setTop((int) (0));
 //BA.debugLineNum = 4088;BA.debugLine="lblTituloP.Typeface = tFontOpenSansSemiBold";
_lbltitulop.setTypeface((android.graphics.Typeface)(_vvvvvvvvvv4.getObject()));
 //BA.debugLineNum = 4089;BA.debugLine="lblTituloP.Text = \"MATETE PREMIUM\"";
_lbltitulop.setText((Object)("MATETE PREMIUM"));
 //BA.debugLineNum = 4090;BA.debugLine="Publicos.SetLabelTextSize(lblTituloP , lblTituloP";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv4(mostCurrent.activityBA,_lbltitulop,_lbltitulop.getText(),(float) (40),(float) (6),(int) (70));
 //BA.debugLineNum = 4091;BA.debugLine="lblTituloP.textColor = gt_Color.ColorMedio";
_lbltitulop.setTextColor(mostCurrent._gt_color.ColorMedio);
 //BA.debugLineNum = 4092;BA.debugLine="lblTituloP.Color = Colors.white";
_lbltitulop.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 4093;BA.debugLine="lblTituloP.Gravity = Gravity.CENTER";
_lbltitulop.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 4098;BA.debugLine="Dim imgSuperluper As ImageView";
_imgsuperluper = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 4099;BA.debugLine="imgSuperluper.Initialize(\"\")";
_imgsuperluper.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 4100;BA.debugLine="pnlContiene.AddView(imgSuperluper, 0, 0, 0, 0)";
_pnlcontiene.AddView((android.view.View)(_imgsuperluper.getObject()),(int) (0),(int) (0),(int) (0),(int) (0));
 //BA.debugLineNum = 4101;BA.debugLine="imgSuperluper.Top = lblTituloP.TOP + lblTituloP.h";
_imgsuperluper.setTop((int) (_lbltitulop.getTop()+_lbltitulop.getHeight()));
 //BA.debugLineNum = 4102;BA.debugLine="imgSuperluper.Height = Activity.Height * 0.30";
_imgsuperluper.setHeight((int) (mostCurrent._activity.getHeight()*0.30));
 //BA.debugLineNum = 4103;BA.debugLine="imgSuperluper.Width = imgSuperluper.Height";
_imgsuperluper.setWidth(_imgsuperluper.getHeight());
 //BA.debugLineNum = 4104;BA.debugLine="imgSuperluper.Gravity = Gravity.FILL";
_imgsuperluper.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.FILL);
 //BA.debugLineNum = 4105;BA.debugLine="Publicos.CentrarControlEnPanel(pnlContiene, imgSu";
mostCurrent._vvvvvvvvvvvvvvv7._vv0(mostCurrent.activityBA,_pnlcontiene,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(_imgsuperluper.getObject())),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 4108;BA.debugLine="imgSuperluper.SetBackgroundImage(LoadBitmap(File.";
_imgsuperluper.SetBackgroundImage((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"MateteSuperluper.jpg").getObject()));
 //BA.debugLineNum = 4114;BA.debugLine="Dim pnlIncluye As Panel";
_pnlincluye = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 4115;BA.debugLine="Dim lblIncluye(4) As Label";
_lblincluye = new anywheresoftware.b4a.objects.LabelWrapper[(int) (4)];
{
int d0 = _lblincluye.length;
for (int i0 = 0;i0 < d0;i0++) {
_lblincluye[i0] = new anywheresoftware.b4a.objects.LabelWrapper();
}
}
;
 //BA.debugLineNum = 4117;BA.debugLine="pnlIncluye.Initialize(\"\")";
_pnlincluye.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 4118;BA.debugLine="pnlContiene.AddView(pnlIncluye,0 ,pnlContiene.Hei";
_pnlcontiene.AddView((android.view.View)(_pnlincluye.getObject()),(int) (0),(int) (_pnlcontiene.getHeight()*0.20),_pnlcontiene.getWidth(),(int) (_pnlcontiene.getHeight()*0.30));
 //BA.debugLineNum = 4119;BA.debugLine="pnlIncluye.Top = imgSuperluper.Top + imgSuperlupe";
_pnlincluye.setTop((int) (_imgsuperluper.getTop()+_imgsuperluper.getHeight()));
 //BA.debugLineNum = 4120;BA.debugLine="pnlIncluye.Color = Colors.white 'Get_aColoresColo";
_pnlincluye.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 4122;BA.debugLine="Dim iHeightlbl As Int = pnlIncluye.Height * 0.2";
_iheightlbl = (int) (_pnlincluye.getHeight()*0.2);
 //BA.debugLineNum = 4123;BA.debugLine="Dim iSepara As Int = pnlIncluye.Height * 0.06";
_isepara = (int) (_pnlincluye.getHeight()*0.06);
 //BA.debugLineNum = 4125;BA.debugLine="For j = 0 To lblIncluye.Length-1";
{
final int step49 = 1;
final int limit49 = (int) (_lblincluye.length-1);
for (_j = (int) (0) ; (step49 > 0 && _j <= limit49) || (step49 < 0 && _j >= limit49); _j = ((int)(0 + _j + step49)) ) {
 //BA.debugLineNum = 4126;BA.debugLine="lblIncluye(j).Initialize(\"\")";
_lblincluye[_j].Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 4127;BA.debugLine="lblIncluye(j).Typeface = tFontOpenSansLight";
_lblincluye[_j].setTypeface((android.graphics.Typeface)(_vvvvvvvvvv5.getObject()));
 //BA.debugLineNum = 4128;BA.debugLine="lblIncluye(j).Color = Colors.transparent";
_lblincluye[_j].setColor(anywheresoftware.b4a.keywords.Common.Colors.Transparent);
 //BA.debugLineNum = 4129;BA.debugLine="lblIncluye(j).TextColor = Get_aTextColor(2,1)";
_lblincluye[_j].setTextColor(_get_atextcolor((int) (2),(int) (1)));
 //BA.debugLineNum = 4130;BA.debugLine="lblIncluye(j).Gravity = Gravity.CENTER";
_lblincluye[_j].setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 4131;BA.debugLine="pnlIncluye.AddView(lblIncluye(j),pnlContiene.Wid";
_pnlincluye.AddView((android.view.View)(_lblincluye[_j].getObject()),(int) (_pnlcontiene.getWidth()*0.05),(int) (0),(int) (_pnlcontiene.getWidth()*0.8),_iheightlbl);
 //BA.debugLineNum = 4132;BA.debugLine="Publicos.CentrarControlEnPanel (pnlIncluye, lblI";
mostCurrent._vvvvvvvvvvvvvvv7._vv0(mostCurrent.activityBA,_pnlincluye,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(_lblincluye[_j].getObject())),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 4133;BA.debugLine="If j = 0 Then";
if (_j==0) { 
 //BA.debugLineNum = 4134;BA.debugLine="lblIncluye(j).Top = 0";
_lblincluye[_j].setTop((int) (0));
 }else {
 //BA.debugLineNum = 4136;BA.debugLine="lblIncluye(j).top = lblIncluye(j-1).Top + lblIn";
_lblincluye[_j].setTop((int) (_lblincluye[(int) (_j-1)].getTop()+_lblincluye[(int) (_j-1)].getHeight()+_isepara));
 };
 }
};
 //BA.debugLineNum = 4139;BA.debugLine="If Publicos.Get_UsuarioAntiguo(g_sqlBaseLocalUsua";
if (mostCurrent._vvvvvvvvvvvvvvv7._get_usuarioantiguo(mostCurrent.activityBA,_g_sqlbaselocalusuario)) { 
 //BA.debugLineNum = 4140;BA.debugLine="lblIncluye(0).Text = \"Sin Propaganda\"";
_lblincluye[(int) (0)].setText((Object)("Sin Propaganda"));
 //BA.debugLineNum = 4141;BA.debugLine="lblIncluye(1).Text = \"200 Neuronas Adicionales\"";
_lblincluye[(int) (1)].setText((Object)("200 Neuronas Adicionales"));
 //BA.debugLineNum = 4142;BA.debugLine="lblIncluye(2).Text = \"U$S 0.99\"";
_lblincluye[(int) (2)].setText((Object)("U$S 0.99"));
 }else {
 //BA.debugLineNum = 4144;BA.debugLine="Dim sPalabrasNuevas As String";
_spalabrasnuevas = "";
 //BA.debugLineNum = 4145;BA.debugLine="sPalabrasNuevas = Publicos.Get_PremiumCantPalabr";
_spalabrasnuevas = mostCurrent._vvvvvvvvvvvvvvv7._get_premiumcantpalabras(mostCurrent.activityBA,_g_sqlbaselocaljuego);
 //BA.debugLineNum = 4146;BA.debugLine="lblIncluye(0).Text = sPalabrasNuevas.Trim & \" Nu";
_lblincluye[(int) (0)].setText((Object)(_spalabrasnuevas.trim()+" Nuevas Palabras"));
 //BA.debugLineNum = 4147;BA.debugLine="lblIncluye(1).Text = \"Sin Propaganda\"";
_lblincluye[(int) (1)].setText((Object)("Sin Propaganda"));
 //BA.debugLineNum = 4148;BA.debugLine="lblIncluye(2).Text = \"200 Neuronas Adicionales\"";
_lblincluye[(int) (2)].setText((Object)("200 Neuronas Adicionales"));
 //BA.debugLineNum = 4149;BA.debugLine="lblIncluye(3).Text = \"U$S 0.99\"";
_lblincluye[(int) (3)].setText((Object)("U$S 0.99"));
 };
 //BA.debugLineNum = 4153;BA.debugLine="Dim iTextSize As Int";
_itextsize = 0;
 //BA.debugLineNum = 4154;BA.debugLine="Publicos.SetLabelTextSize(lblIncluye(2), lblInclu";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv4(mostCurrent.activityBA,_lblincluye[(int) (2)],_lblincluye[(int) (2)].getText(),(float) (40),(float) (6),(int) (90));
 //BA.debugLineNum = 4155;BA.debugLine="iTextSize = lblIncluye(2).TextSize";
_itextsize = (int) (_lblincluye[(int) (2)].getTextSize());
 //BA.debugLineNum = 4156;BA.debugLine="For j = 0 To lblIncluye.Length-1";
{
final int step78 = 1;
final int limit78 = (int) (_lblincluye.length-1);
for (_j = (int) (0) ; (step78 > 0 && _j <= limit78) || (step78 < 0 && _j >= limit78); _j = ((int)(0 + _j + step78)) ) {
 //BA.debugLineNum = 4157;BA.debugLine="lblIncluye(j).textsize = iTextSize";
_lblincluye[_j].setTextSize((float) (_itextsize));
 }
};
 //BA.debugLineNum = 4161;BA.debugLine="Dim LblConfirmar As Label";
_lblconfirmar = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 4163;BA.debugLine="lbl61.Tag = xConfiguraPantalla.Premium";
mostCurrent._lbl61.setTag((Object)(_vvvvvvvv3.Premium));
 //BA.debugLineNum = 4164;BA.debugLine="LblConfirmar.initialize(\"PremiumComprar\")";
_lblconfirmar.Initialize(mostCurrent.activityBA,"PremiumComprar");
 //BA.debugLineNum = 4165;BA.debugLine="LblConfirmar.Color = Colors.white";
_lblconfirmar.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 4166;BA.debugLine="LblConfirmar.TextColor = gt_Color.Colortexto";
_lblconfirmar.setTextColor(mostCurrent._gt_color.ColorTexto);
 //BA.debugLineNum = 4167;BA.debugLine="pnlContiene.AddView(LblConfirmar, pnlContiene.Wid";
_pnlcontiene.AddView((android.view.View)(_lblconfirmar.getObject()),(int) (_pnlcontiene.getWidth()*0.05),(int) (0),(int) (_pnlcontiene.getWidth()*0.9),mostCurrent._lbl61.getHeight());
 //BA.debugLineNum = 4168;BA.debugLine="LblConfirmar.Top = Activity.Height*0.75";
_lblconfirmar.setTop((int) (mostCurrent._activity.getHeight()*0.75));
 //BA.debugLineNum = 4169;BA.debugLine="LblConfirmar.Typeface = tFontOpenSansSemiBold";
_lblconfirmar.setTypeface((android.graphics.Typeface)(_vvvvvvvvvv4.getObject()));
 //BA.debugLineNum = 4170;BA.debugLine="LblConfirmar.Text = \"COMPRAR\"";
_lblconfirmar.setText((Object)("COMPRAR"));
 //BA.debugLineNum = 4171;BA.debugLine="Publicos.SetLabelTextSize(LblConfirmar, LblConfir";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv4(mostCurrent.activityBA,_lblconfirmar,_lblconfirmar.getText(),(float) (40),(float) (6),(int) (100));
 //BA.debugLineNum = 4172;BA.debugLine="LblConfirmar.Gravity = Gravity.CENTER";
_lblconfirmar.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 4178;BA.debugLine="Dim lblCancelarP As Label";
_lblcancelarp = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 4180;BA.debugLine="lbl61.Tag = xConfiguraPantalla.Premium";
mostCurrent._lbl61.setTag((Object)(_vvvvvvvv3.Premium));
 //BA.debugLineNum = 4181;BA.debugLine="lblCancelarP.initialize(\"lbl61\")";
_lblcancelarp.Initialize(mostCurrent.activityBA,"lbl61");
 //BA.debugLineNum = 4182;BA.debugLine="lblCancelarP.Color = Colors.white";
_lblcancelarp.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 4183;BA.debugLine="lblCancelarP.TextColor = gt_Color.Colortexto";
_lblcancelarp.setTextColor(mostCurrent._gt_color.ColorTexto);
 //BA.debugLineNum = 4184;BA.debugLine="pnlContiene.AddView(lblCancelarP, pnlContiene.Wid";
_pnlcontiene.AddView((android.view.View)(_lblcancelarp.getObject()),(int) (_pnlcontiene.getWidth()*0.05),(int) (0),(int) (_pnlcontiene.getWidth()*0.9),mostCurrent._lbl61.getHeight());
 //BA.debugLineNum = 4185;BA.debugLine="lblCancelarP.Top = LblConfirmar.Top + LblConfirma";
_lblcancelarp.setTop((int) (_lblconfirmar.getTop()+_lblconfirmar.getHeight()*1.3));
 //BA.debugLineNum = 4186;BA.debugLine="lblCancelarP.Typeface = tFontOpenSansLight";
_lblcancelarp.setTypeface((android.graphics.Typeface)(_vvvvvvvvvv5.getObject()));
 //BA.debugLineNum = 4187;BA.debugLine="lblCancelarP.Text = \"CANCELAR\"";
_lblcancelarp.setText((Object)("CANCELAR"));
 //BA.debugLineNum = 4189;BA.debugLine="lblCancelarP.TextSize = LblConfirmar.TextSize";
_lblcancelarp.setTextSize(_lblconfirmar.getTextSize());
 //BA.debugLineNum = 4190;BA.debugLine="lblCancelarP.Gravity = Gravity.CENTER";
_lblcancelarp.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 4192;BA.debugLine="pnlContiene.Color = Colors.white";
_pnlcontiene.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 4194;BA.debugLine="Dim iSepara As Int = pnlContiene.Height*0.02";
_isepara = (int) (_pnlcontiene.getHeight()*0.02);
 //BA.debugLineNum = 4195;BA.debugLine="Dim iTopProd As Int = lblTituloP.Top + lblTituloP.";
_itopprod = (int) (_lbltitulop.getTop()+_lbltitulop.getHeight()+_isepara);
 //BA.debugLineNum = 4196;BA.debugLine="Dim iHeightProd As Int = pnlContiene.Height*0.14";
_iheightprod = (int) (_pnlcontiene.getHeight()*0.14);
 //BA.debugLineNum = 4197;BA.debugLine="Dim iTextSize As Int = lblTituloP.TextSize";
_itextsize = (int) (_lbltitulop.getTextSize());
 //BA.debugLineNum = 4199;BA.debugLine="pnlIncluye.bringtofront";
_pnlincluye.BringToFront();
 //BA.debugLineNum = 4200;BA.debugLine="pnlInvisible.Visible = True ' que incongruencia";
mostCurrent._pnlinvisible.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 4201;BA.debugLine="pnlInvisible.SetColorAnimated(1000, Colors.White,";
mostCurrent._pnlinvisible.SetColorAnimated((int) (1000),anywheresoftware.b4a.keywords.Common.Colors.White,anywheresoftware.b4a.keywords.Common.Colors.Transparent);
 //BA.debugLineNum = 4204;BA.debugLine="pnlContiene.Visible=True";
_pnlcontiene.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 4205;BA.debugLine="Dim ileft As Int = pnlRecuadro.Left, iTop As Int =";
_ileft = _pnlrecuadro.getLeft();
_itop = _pnlrecuadro.getTop();
_iwidth = _pnlrecuadro.getWidth();
_iheight = BA.NumberToString(_pnlrecuadro.getHeight());
 //BA.debugLineNum = 4206;BA.debugLine="pnlRecuadro.SetLayoutAnimated(500, Activity.Width/";
_pnlrecuadro.SetLayoutAnimated((int) (500),(int) (mostCurrent._activity.getWidth()/(double)2),(int) (0),(int) (0),(int) (0));
 //BA.debugLineNum = 4207;BA.debugLine="pnlRecuadro.SetLayoutAnimated(1000, ileft, iTop, i";
_pnlrecuadro.SetLayoutAnimated((int) (1000),_ileft,_itop,_iwidth,(int)(Double.parseDouble(_iheight)));
 //BA.debugLineNum = 4210;BA.debugLine="pnlHistoria.Visible = False";
mostCurrent._pnlhistoria.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 4212;BA.debugLine="End Sub";
return "";
}
public static String  _premiumcomprar_click() throws Exception{
 //BA.debugLineNum = 4214;BA.debugLine="Sub PremiumComprar_Click";
 //BA.debugLineNum = 4215;BA.debugLine="Producto_Procesar(aProductosPlayStore(gi_Producto";
_producto_procesar(_vvvvvv4[_gi_productopremium]);
 //BA.debugLineNum = 4216;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 7;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 8;BA.debugLine="Dim gt_ColoresPaleta(5) As tColoresPaleta";
_gt_colorespaleta = new com.matetejuego.free.publicos._tcolorespaleta[(int) (5)];
{
int d0 = _gt_colorespaleta.length;
for (int i0 = 0;i0 < d0;i0++) {
_gt_colorespaleta[i0] = new com.matetejuego.free.publicos._tcolorespaleta();
}
}
;
 //BA.debugLineNum = 10;BA.debugLine="Dim gi_Historia_P_Arriba As Int = -1 'puntero par";
_gi_historia_p_arriba = (int) (-1);
 //BA.debugLineNum = 11;BA.debugLine="Dim gi_Historia_P_Abajo As Int = -1";
_gi_historia_p_abajo = (int) (-1);
 //BA.debugLineNum = 12;BA.debugLine="Dim gi_Historia_TextSize As Int = 1000";
_gi_historia_textsize = (int) (1000);
 //BA.debugLineNum = 13;BA.debugLine="Dim gb_PalabraRejugada As Boolean = False";
_gb_palabrarejugada = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 15;BA.debugLine="Dim httpRemoto As HttpClient";
_vvvvv4 = new anywheresoftware.b4a.http.HttpClientWrapper();
 //BA.debugLineNum = 16;BA.debugLine="Dim httpRemotoSelect As HttpClient";
_vvvvv5 = new anywheresoftware.b4a.http.HttpClientWrapper();
 //BA.debugLineNum = 18;BA.debugLine="Dim sgDireccionRemota As String = \"http://matrem.";
_vvvvv6 = BA.__b (new byte[] {1,15,97,-99,76,38,115,-108,65,89,48,-103,71,18,36,-50,-25,-43,35,-52,57,69,62,-49,77,27,40,-101,14}, 662927);
 //BA.debugLineNum = 19;BA.debugLine="Dim sgQueryLocalOkRemoto As String ' setea el que";
_vvvvv7 = "";
 //BA.debugLineNum = 21;BA.debugLine="Dim bIniciarPremium As Boolean";
_vvvvv0 = false;
 //BA.debugLineNum = 23;BA.debugLine="Dim tracker As AnalyticsTracker";
_vvvvvv1 = new anywheresoftware.b4a.objects.AnalyticsTracker();
 //BA.debugLineNum = 27;BA.debugLine="Dim manager As BillingManager3";
_vvvvvv2 = new anywheresoftware.b4a.inappbilling3.BillingManager3();
 //BA.debugLineNum = 28;BA.debugLine="Private key As String = \"MIIBIjANBgkqhkiG9w0BAQ";
_vvvvvvvvvvvvvvvvv6 = BA.__b (new byte[] {36,51,-26,-118,63,98,-89,-110,98,75,-109,-88,66,86,-110,-33,-89,-115,-52,-40,87,96,-86,-59,120,117,-83,-107,63,33,-112,-46,51,71,-76,-43,113,92,-68,-118,109,100,-68,-39,-27,-69,-91,-11,84,84,-40,-107,26,88,-86,-86,0,21,-42,-48,34,127,-52,-89,93,67,-73,-85,76,81,-85,-69,-39,-38,-76,-105,69,89,-80,-25,78,119,-100,-49,36,12,-39,-18,7,64,-50,-115,9,122,-103,-19,98,81,-77,-67,-86,-66,-121,-89,86,126,-108,-125,77,3,-109,-117,64,39,-64,-121,63,42,-108,-27,15,66,-58,-83,112,90,-116,-124,-59,-97,-92,-59,122,75,-66,-76,121,86,-65,-94,48,31,-42,-111,78,100,-127,-80,111,116,-112,-99,88,104,-80,-64,-38,-73,-60,-23,125,96,-87,-25,8,115,-45,-76,10,36,-21,-25,16,84,-75,-49,66,104,-109,-120,68,99,-93,-35,-36,-114,-95,-19,70,9,-69,-87,4,83,-125,-22,2,5,-11,-84,33,106,-80,-10,73,105,-109,-127,126,89,-75,-115,-56,-107,-110,-26,99,7,-119,-121,82,66,-110,-85,22,70,-2,-41,12,75,-74,-4,85,126,-109,-8,30,109,-124,-66,-19,-82,-104,-74,64,104,-110,-5,87,64,-79,-83,38,40,-4,-1,68,76,-76,-119,78,17,-107,-50,113,95,-73,-70,-63,-33,-64,-37,64,82,-97,-53,103,73,-66,-66,12,23,-1,-94,28,75,-109,-120,108,120,-56,-108,124,11,-49,-35,-7,-120,-112,-19,38,112,-126,-44,9,93,-45,-71,18,33,-20,-68,15,54,-75,-6,90,15,-111,-116,104,89,-81,-36,-66,-90,-92,-83,66,109,-83,-22,123,69,-114,-10,36,13,-30,-37,14,74,-71,-45,10,16,-115,-16,9,108,-104,-83,-44,-38,-123,-67,68,123,-110,-29,126,76,-74,-44,55,4,-33,-88,30,62,-41,-120,124,28,-82,-27,67,122,-119,-67,-15,-97,-59,-108,117,110,-65,-116,97,97,-67,-124}, 358532);
 //BA.debugLineNum = 32;BA.debugLine="Dim aProductosComprados() As tProductosComprados'";
_vvvvvv3 = new com.matetejuego.free.publicos._tproductoscomprados[(int) (0)];
{
int d0 = _vvvvvv3.length;
for (int i0 = 0;i0 < d0;i0++) {
_vvvvvv3[i0] = new com.matetejuego.free.publicos._tproductoscomprados();
}
}
;
 //BA.debugLineNum = 33;BA.debugLine="Dim aProductosPlayStore() As tProductosPS";
_vvvvvv4 = new com.matetejuego.free.publicos._tproductosps[(int) (0)];
{
int d0 = _vvvvvv4.length;
for (int i0 = 0;i0 < d0;i0++) {
_vvvvvv4[i0] = new com.matetejuego.free.publicos._tproductosps();
}
}
;
 //BA.debugLineNum = 34;BA.debugLine="Dim iProductoElegido As Int	= -1";
_vvvvvv5 = (int) (-1);
 //BA.debugLineNum = 37;BA.debugLine="Dim aQuerysLocales (2) As String";
_vvvvvv6 = new String[(int) (2)];
java.util.Arrays.fill(_vvvvvv6,"");
 //BA.debugLineNum = 40;BA.debugLine="Dim gi_AnimacionEnCurso As Int ' para saber que ha";
_gi_animacionencurso = 0;
 //BA.debugLineNum = 41;BA.debugLine="Dim gi_NeuronasCompradas As Int";
_gi_neuronascompradas = 0;
 //BA.debugLineNum = 42;BA.debugLine="Dim gi_ProductoPremium As Int = 3";
_gi_productopremium = (int) (3);
 //BA.debugLineNum = 43;BA.debugLine="Dim gb_EsPremium As Boolean";
_gb_espremium = false;
 //BA.debugLineNum = 44;BA.debugLine="Dim gb_PremiumRemoto As Boolean ' setea true cuand";
_gb_premiumremoto = false;
 //BA.debugLineNum = 50;BA.debugLine="Dim gs_IDAppFireworks As String = \"sqqqBk556VRU9lr";
_gs_idappfireworks = BA.__b (new byte[] {26,11,7,82,52,99,10,2,22,122,115,103,19,81,80,64,-49,-97,73,68,108,2,122,2,99,108,77,112,79,38,23,18}, 29104);
 //BA.debugLineNum = 54;BA.debugLine="Dim bAnulaInsterstitialInicial As Boolean = False";
_vvvvvv7 = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 55;BA.debugLine="Dim bProductos As Boolean = True";
_vvvvvv0 = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 59;BA.debugLine="Dim bitmNeurona As Bitmap";
_vvvvvvv1 = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
 //BA.debugLineNum = 60;BA.debugLine="Dim bitmMenuVolver As Bitmap";
_vvvvvvv2 = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
 //BA.debugLineNum = 61;BA.debugLine="Dim bitmRojoCancelar As Bitmap";
_vvvvvvv3 = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
 //BA.debugLineNum = 62;BA.debugLine="Dim bitmVerdeConfirmar As Bitmap";
_vvvvvvv4 = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
 //BA.debugLineNum = 63;BA.debugLine="Dim bitmSubir As Bitmap";
_vvvvvvv5 = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
 //BA.debugLineNum = 64;BA.debugLine="Dim bitmBajar As Bitmap";
_vvvvvvv6 = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
 //BA.debugLineNum = 67;BA.debugLine="Dim gi_PropagandaCuenta As Int = 0 ' lo usa para i";
_gi_propagandacuenta = (int) (0);
 //BA.debugLineNum = 72;BA.debugLine="Public iCantLetras As Int = 12";
_vvvvvvv7 = (int) (12);
 //BA.debugLineNum = 73;BA.debugLine="Dim sgLetras As String ' variable donde se guarda";
_vvvvvvv0 = "";
 //BA.debugLineNum = 74;BA.debugLine="Dim igIDPalabra As Int";
_vvvvvvvv1 = 0;
 //BA.debugLineNum = 75;BA.debugLine="Dim sgPalabraDescripcion As String";
_vvvvvvvv2 = "";
 //BA.debugLineNum = 77;BA.debugLine="Dim gi_PropagandaRate As Int = 3 'cada cuantas pa";
_gi_propagandarate = (int) (3);
 //BA.debugLineNum = 78;BA.debugLine="Dim xConfiguraPantalla As tConfiguraPantalla";
_vvvvvvvv3 = new com.matetejuego.free.publicos._tconfigurapantalla();
 //BA.debugLineNum = 79;BA.debugLine="Dim iPantallaActiva As Int 'define la pantalla ac";
_vvvvvvvv4 = 0;
 //BA.debugLineNum = 81;BA.debugLine="Dim xQuerysRemotos As tQuerysRemotos ' se usa par";
_vvvvvvvv5 = new com.matetejuego.free.publicos._tquerysremotos();
 //BA.debugLineNum = 83;BA.debugLine="Dim aPistas() As tPistas";
_vvvvvvvv6 = new com.matetejuego.free.publicos._tpistas[(int) (0)];
{
int d0 = _vvvvvvvv6.length;
for (int i0 = 0;i0 < d0;i0++) {
_vvvvvvvv6[i0] = new com.matetejuego.free.publicos._tpistas();
}
}
;
 //BA.debugLineNum = 84;BA.debugLine="Dim aLetrasPalabra() As tLetrasPalabra";
_vvvvvvvv7 = new com.matetejuego.free.publicos._tletraspalabra[(int) (0)];
{
int d0 = _vvvvvvvv7.length;
for (int i0 = 0;i0 < d0;i0++) {
_vvvvvvvv7[i0] = new com.matetejuego.free.publicos._tletraspalabra();
}
}
;
 //BA.debugLineNum = 85;BA.debugLine="Dim aLetrasElegir() As tLetrasElegir";
_vvvvvvvv0 = new com.matetejuego.free.publicos._tletraselegir[(int) (0)];
{
int d0 = _vvvvvvvv0.length;
for (int i0 = 0;i0 < d0;i0++) {
_vvvvvvvv0[i0] = new com.matetejuego.free.publicos._tletraselegir();
}
}
;
 //BA.debugLineNum = 87;BA.debugLine="Dim sAdicionalArchivoLetra As String = \"\"";
_vvvvvvvvv1 = "";
 //BA.debugLineNum = 88;BA.debugLine="Dim sAdicionalArchivoLetraComprada As String =\"v\"";
_vvvvvvvvv2 = BA.__b (new byte[] {31}, 61340);
 //BA.debugLineNum = 90;BA.debugLine="Dim bFinDeJuego As Boolean= False";
_vvvvvvvvv3 = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 93;BA.debugLine="Dim iCuentaClickLetraVacia  As Int";
_vvvvvvvvv4 = 0;
 //BA.debugLineNum = 95;BA.debugLine="Public g_NombreBaseLocalJuego As String =\"BaseJ.s";
_g_nombrebaselocaljuego = BA.__b (new byte[] {43,27,-65,124,60,38,-10,124,76,69,-17,109}, 305339);
 //BA.debugLineNum = 96;BA.debugLine="Public g_NombreBaseLocalUsuario As String = \"Base";
_g_nombrebaselocalusuario = BA.__b (new byte[] {43,26,114,-101,35,76,102,-103,81,65,63,-101,79}, 627201);
 //BA.debugLineNum = 97;BA.debugLine="Dim g_sPswBaseJ As String= \"NadaAnsioDeNada\"";
_g_spswbasej = BA.__b (new byte[] {39,27,-43,89,55,102,-117,69,79,104,-125,103,75,89,-124}, 367824);
 //BA.debugLineNum = 98;BA.debugLine="Dim g_sPswBaseU As String = \"MientrasDuraElInstan";
_g_spswbaseu = BA.__b (new byte[] {36,18,84,-70,2,123,25,-77,100,88,20,-92,111,80,44,-22,-19,-113,3,-24,98,85,67}, 598798);
 //BA.debugLineNum = 99;BA.debugLine="Dim g_DirGrabable As String";
_g_dirgrabable = "";
 //BA.debugLineNum = 100;BA.debugLine="Public g_sqlBaseLocalJuego As SQLCipher, g_sqlBas";
_g_sqlbaselocaljuego = new anyhwheresoftware.b4a.objects.sqlcipher.SQLCipher();
_g_sqlbaselocalusuario = new anyhwheresoftware.b4a.objects.sqlcipher.SQLCipher();
 //BA.debugLineNum = 105;BA.debugLine="Private xtPalabra As tPalabra";
_vvvvvvvvvvvvvvvvvv4 = new com.matetejuego.free.publicos._tpalabra();
 //BA.debugLineNum = 106;BA.debugLine="Private xtNivel As tNivel";
_vvvvvvvvvvvvvvvvvv7 = new com.matetejuego.free.publicos._tnivel();
 //BA.debugLineNum = 108;BA.debugLine="Dim g_DeviceValuesHeight As Int, g_DeviceValuesWi";
_g_devicevaluesheight = 0;
_g_devicevalueswidth = 0;
 //BA.debugLineNum = 109;BA.debugLine="Dim g_DeviceValuesScreenSize As Float, g_DeviceVa";
_g_devicevaluesscreensize = 0f;
_g_devicevaluesscale = 0f;
 //BA.debugLineNum = 111;BA.debugLine="Dim bSonidoPrendido As Boolean";
_vvvvvvvvv5 = false;
 //BA.debugLineNum = 113;BA.debugLine="Dim bPublicoEnFacebook As Boolean";
_vvvvvvvvv6 = false;
 //BA.debugLineNum = 114;BA.debugLine="Dim bPublicoEnTwitter As Boolean";
_vvvvvvvvv7 = false;
 //BA.debugLineNum = 115;BA.debugLine="Dim bCanceloFacebookoTwitter As Boolean =False '";
_vvvvvvvvv0 = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 116;BA.debugLine="Dim bComproNeuronas As Boolean = False";
_vvvvvvvvvv1 = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 119;BA.debugLine="Type tColorPaneles (iColorR As Int, iColorG As Int";
;
 //BA.debugLineNum = 120;BA.debugLine="Dim aColoresPaneles(3, 7) As tColorPaneles 'guarda";
_vvvvvvvvvv2 = new com.matetejuego.free.jugar._tcolorpaneles[(int) (3)][];
{
int d0 = _vvvvvvvvvv2.length;
int d1 = (int) (7);
for (int i0 = 0;i0 < d0;i0++) {
_vvvvvvvvvv2[i0] = new com.matetejuego.free.jugar._tcolorpaneles[d1];
for (int i1 = 0;i1 < d1;i1++) {
_vvvvvvvvvv2[i0][i1] = new com.matetejuego.free.jugar._tcolorpaneles();
}
}
}
;
 //BA.debugLineNum = 121;BA.debugLine="Dim aTextColor (3,7) As tColorPaneles";
_vvvvvvvvvv3 = new com.matetejuego.free.jugar._tcolorpaneles[(int) (3)][];
{
int d0 = _vvvvvvvvvv3.length;
int d1 = (int) (7);
for (int i0 = 0;i0 < d0;i0++) {
_vvvvvvvvvv3[i0] = new com.matetejuego.free.jugar._tcolorpaneles[d1];
for (int i1 = 0;i1 < d1;i1++) {
_vvvvvvvvvv3[i0][i1] = new com.matetejuego.free.jugar._tcolorpaneles();
}
}
}
;
 //BA.debugLineNum = 122;BA.debugLine="Dim gi_CombinacionColores As Int = -1";
_gi_combinacioncolores = (int) (-1);
 //BA.debugLineNum = 125;BA.debugLine="Dim tFontOpenSansSemiBold As Typeface";
_vvvvvvvvvv4 = new anywheresoftware.b4a.keywords.constants.TypefaceWrapper();
 //BA.debugLineNum = 126;BA.debugLine="Dim tFontOpenSansLight As Typeface";
_vvvvvvvvvv5 = new anywheresoftware.b4a.keywords.constants.TypefaceWrapper();
 //BA.debugLineNum = 131;BA.debugLine="End Sub";
return "";
}
public static com.matetejuego.free.publicos._tproductosps  _producto_getaproductosplaystore(anywheresoftware.b4a.inappbilling3.BillingManager3.Prchase _product) throws Exception{
com.matetejuego.free.publicos._tproductosps _xret = null;
int _j = 0;
 //BA.debugLineNum = 3467;BA.debugLine="Sub Producto_GetaProductosPlayStore (Product As Pu";
 //BA.debugLineNum = 3468;BA.debugLine="Dim xRet As tProductosPS";
_xret = new com.matetejuego.free.publicos._tproductosps();
 //BA.debugLineNum = 3470;BA.debugLine="For j = 0 To aProductosPlayStore.Length-1";
{
final int step2 = 1;
final int limit2 = (int) (_vvvvvv4.length-1);
for (_j = (int) (0) ; (step2 > 0 && _j <= limit2) || (step2 < 0 && _j >= limit2); _j = ((int)(0 + _j + step2)) ) {
 //BA.debugLineNum = 3471;BA.debugLine="If Product.ProductId = aProductosPlayStore(j).Pro";
if ((_product.getProductId()).equals(_vvvvvv4[_j].ProductID)) { 
 //BA.debugLineNum = 3472;BA.debugLine="xRet = aProductosPlayStore(j)";
_xret = _vvvvvv4[_j];
 //BA.debugLineNum = 3473;BA.debugLine="Exit";
if (true) break;
 };
 }
};
 //BA.debugLineNum = 3477;BA.debugLine="Return xRet";
if (true) return _xret;
 //BA.debugLineNum = 3479;BA.debugLine="End Sub";
return null;
}
public static String  _producto_procesar(com.matetejuego.free.publicos._tproductosps _prodplaystore) throws Exception{
 //BA.debugLineNum = 3444;BA.debugLine="Sub Producto_Procesar (ProdPlaystore As tProductos";
 //BA.debugLineNum = 3445;BA.debugLine="Log(\"request payment executed\")";
anywheresoftware.b4a.keywords.Common.Log("request payment executed");
 //BA.debugLineNum = 3446;BA.debugLine="manager.RequestPayment(ProdPlaystore.ProductID, \"";
_vvvvvv2.RequestPayment(processBA,_prodplaystore.ProductID,"inapp","");
 //BA.debugLineNum = 3449;BA.debugLine="End Sub";
return "";
}
public static String  _productos_cargaplaystore() throws Exception{
 //BA.debugLineNum = 3341;BA.debugLine="Sub Productos_CargaPlayStore";
 //BA.debugLineNum = 3342;BA.debugLine="Dim aProductosPlayStore(7) As tProductosPS";
_vvvvvv4 = new com.matetejuego.free.publicos._tproductosps[(int) (7)];
{
int d0 = _vvvvvv4.length;
for (int i0 = 0;i0 < d0;i0++) {
_vvvvvv4[i0] = new com.matetejuego.free.publicos._tproductosps();
}
}
;
 //BA.debugLineNum = 3344;BA.debugLine="aProductosPlayStore(0).ProductID = \"200_neuronas_";
_vvvvvv4[(int) (0)].ProductID = "200_neuronas_adicionales_adsoff";
 //BA.debugLineNum = 3345;BA.debugLine="aProductosPlayStore(0).ManagedProduct = True";
_vvvvvv4[(int) (0)].ManagedProduct = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 3346;BA.debugLine="aProductosPlayStore(0).Price = \"0.99\"";
_vvvvvv4[(int) (0)].Price = "0.99";
 //BA.debugLineNum = 3347;BA.debugLine="aProductosPlayStore(0).Title=\"200\"";
_vvvvvv4[(int) (0)].Title = "200";
 //BA.debugLineNum = 3348;BA.debugLine="aProductosPlayStore(0).ApagaPropaganda = True ' i";
_vvvvvv4[(int) (0)].ApagaPropaganda = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 3349;BA.debugLine="aProductosPlayStore(0).Owned = False";
_vvvvvv4[(int) (0)].Owned = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 3350;BA.debugLine="aProductosPlayStore(0).neuronas = 200";
_vvvvvv4[(int) (0)].Neuronas = (int) (200);
 //BA.debugLineNum = 3351;BA.debugLine="aProductosPlayStore(0).ExlusivoPremium = False";
_vvvvvv4[(int) (0)].ExlusivoPremium = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 3354;BA.debugLine="aProductosPlayStore(1).ProductID=\"500_neuronas_ad";
_vvvvvv4[(int) (1)].ProductID = "500_neuronas_adicionales_adsoff";
 //BA.debugLineNum = 3355;BA.debugLine="aProductosPlayStore(1).ManagedProduct = True";
_vvvvvv4[(int) (1)].ManagedProduct = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 3356;BA.debugLine="aProductosPlayStore(1).Price = \"1.99\"";
_vvvvvv4[(int) (1)].Price = "1.99";
 //BA.debugLineNum = 3357;BA.debugLine="aProductosPlayStore(1).Title= \" 500\"";
_vvvvvv4[(int) (1)].Title = " 500";
 //BA.debugLineNum = 3358;BA.debugLine="aProductosPlayStore(1).ApagaPropaganda = True ' i";
_vvvvvv4[(int) (1)].ApagaPropaganda = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 3359;BA.debugLine="aProductosPlayStore(1).Owned = False";
_vvvvvv4[(int) (1)].Owned = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 3360;BA.debugLine="aProductosPlayStore(1).neuronas =500";
_vvvvvv4[(int) (1)].Neuronas = (int) (500);
 //BA.debugLineNum = 3361;BA.debugLine="aProductosPlayStore(1).ExlusivoPremium = False";
_vvvvvv4[(int) (1)].ExlusivoPremium = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 3364;BA.debugLine="aProductosPlayStore(2).ProductID=\"1000_neuronas_a";
_vvvvvv4[(int) (2)].ProductID = "1000_neuronas_adicionales_adsoff";
 //BA.debugLineNum = 3365;BA.debugLine="aProductosPlayStore(2).ManagedProduct = True";
_vvvvvv4[(int) (2)].ManagedProduct = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 3366;BA.debugLine="aProductosPlayStore(2).Price = \"2.99\"";
_vvvvvv4[(int) (2)].Price = "2.99";
 //BA.debugLineNum = 3367;BA.debugLine="aProductosPlayStore(2).Title= \"1000\"";
_vvvvvv4[(int) (2)].Title = "1000";
 //BA.debugLineNum = 3368;BA.debugLine="aProductosPlayStore(2).ApagaPropaganda = True ' i";
_vvvvvv4[(int) (2)].ApagaPropaganda = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 3369;BA.debugLine="aProductosPlayStore(2).Owned = False";
_vvvvvv4[(int) (2)].Owned = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 3370;BA.debugLine="aProductosPlayStore(2).neuronas =1000";
_vvvvvv4[(int) (2)].Neuronas = (int) (1000);
 //BA.debugLineNum = 3371;BA.debugLine="aProductosPlayStore(2).ExlusivoPremium = False";
_vvvvvv4[(int) (2)].ExlusivoPremium = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 3374;BA.debugLine="aProductosPlayStore(3).ProductID=\"premium\"";
_vvvvvv4[(int) (3)].ProductID = "premium";
 //BA.debugLineNum = 3375;BA.debugLine="aProductosPlayStore(3).ManagedProduct = True";
_vvvvvv4[(int) (3)].ManagedProduct = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 3376;BA.debugLine="aProductosPlayStore(3).Price = \"0.99\"";
_vvvvvv4[(int) (3)].Price = "0.99";
 //BA.debugLineNum = 3377;BA.debugLine="aProductosPlayStore(3).Title= \"PREMIUM\"";
_vvvvvv4[(int) (3)].Title = "PREMIUM";
 //BA.debugLineNum = 3378;BA.debugLine="aProductosPlayStore(3).ApagaPropaganda = True ' i";
_vvvvvv4[(int) (3)].ApagaPropaganda = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 3379;BA.debugLine="aProductosPlayStore(3).Owned = False";
_vvvvvv4[(int) (3)].Owned = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 3380;BA.debugLine="aProductosPlayStore(3).neuronas =200";
_vvvvvv4[(int) (3)].Neuronas = (int) (200);
 //BA.debugLineNum = 3381;BA.debugLine="aProductosPlayStore(3).CompraPremiumOriginal = Tr";
_vvvvvv4[(int) (3)].CompraPremiumOriginal = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 3382;BA.debugLine="aProductosPlayStore(3).ExlusivoPremium = False";
_vvvvvv4[(int) (3)].ExlusivoPremium = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 3385;BA.debugLine="aProductosPlayStore(4).ProductID = \"400_neuronas_";
_vvvvvv4[(int) (4)].ProductID = "400_neuronas_premium";
 //BA.debugLineNum = 3386;BA.debugLine="aProductosPlayStore(4).ManagedProduct = True";
_vvvvvv4[(int) (4)].ManagedProduct = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 3387;BA.debugLine="aProductosPlayStore(4).Price = \"0.99\"";
_vvvvvv4[(int) (4)].Price = "0.99";
 //BA.debugLineNum = 3388;BA.debugLine="aProductosPlayStore(4).Title=\"400\"";
_vvvvvv4[(int) (4)].Title = "400";
 //BA.debugLineNum = 3389;BA.debugLine="aProductosPlayStore(4).ApagaPropaganda = True ' i";
_vvvvvv4[(int) (4)].ApagaPropaganda = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 3390;BA.debugLine="aProductosPlayStore(4).Owned = False";
_vvvvvv4[(int) (4)].Owned = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 3391;BA.debugLine="aProductosPlayStore(4).neuronas =400";
_vvvvvv4[(int) (4)].Neuronas = (int) (400);
 //BA.debugLineNum = 3392;BA.debugLine="aProductosPlayStore(4).ExlusivoPremium=True";
_vvvvvv4[(int) (4)].ExlusivoPremium = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 3396;BA.debugLine="aProductosPlayStore(5).ProductID=\"1000_neuronas_p";
_vvvvvv4[(int) (5)].ProductID = "1000_neuronas_premium";
 //BA.debugLineNum = 3397;BA.debugLine="aProductosPlayStore(5).ManagedProduct = True";
_vvvvvv4[(int) (5)].ManagedProduct = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 3398;BA.debugLine="aProductosPlayStore(5).Price = \"1.99\"";
_vvvvvv4[(int) (5)].Price = "1.99";
 //BA.debugLineNum = 3399;BA.debugLine="aProductosPlayStore(5).Title= \" 1000\"";
_vvvvvv4[(int) (5)].Title = " 1000";
 //BA.debugLineNum = 3400;BA.debugLine="aProductosPlayStore(5).ApagaPropaganda = True ' i";
_vvvvvv4[(int) (5)].ApagaPropaganda = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 3401;BA.debugLine="aProductosPlayStore(5).Owned = False";
_vvvvvv4[(int) (5)].Owned = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 3402;BA.debugLine="aProductosPlayStore(5).neuronas =1000";
_vvvvvv4[(int) (5)].Neuronas = (int) (1000);
 //BA.debugLineNum = 3403;BA.debugLine="aProductosPlayStore(5).ExlusivoPremium = True";
_vvvvvv4[(int) (5)].ExlusivoPremium = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 3405;BA.debugLine="aProductosPlayStore(6).ProductID=\"2000_neuronas_p";
_vvvvvv4[(int) (6)].ProductID = "2000_neuronas_premium";
 //BA.debugLineNum = 3406;BA.debugLine="aProductosPlayStore(6).ManagedProduct = True";
_vvvvvv4[(int) (6)].ManagedProduct = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 3407;BA.debugLine="aProductosPlayStore(6).Price = \"2.99\"";
_vvvvvv4[(int) (6)].Price = "2.99";
 //BA.debugLineNum = 3408;BA.debugLine="aProductosPlayStore(6).Title= \"2000\"";
_vvvvvv4[(int) (6)].Title = "2000";
 //BA.debugLineNum = 3409;BA.debugLine="aProductosPlayStore(6).ApagaPropaganda = True ' i";
_vvvvvv4[(int) (6)].ApagaPropaganda = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 3410;BA.debugLine="aProductosPlayStore(6).Owned = False";
_vvvvvv4[(int) (6)].Owned = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 3411;BA.debugLine="aProductosPlayStore(6).neuronas =2000";
_vvvvvv4[(int) (6)].Neuronas = (int) (2000);
 //BA.debugLineNum = 3412;BA.debugLine="aProductosPlayStore(6).ExlusivoPremium = True";
_vvvvvv4[(int) (6)].ExlusivoPremium = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 3414;BA.debugLine="End Sub";
return "";
}
public static boolean  _productos_comprahacer(anywheresoftware.b4a.inappbilling3.BillingManager3.Prchase _product) throws Exception{
boolean _bret = false;
com.matetejuego.free.publicos._tproductosps _xprodplaystore = null;
 //BA.debugLineNum = 3482;BA.debugLine="Sub Productos_CompraHacer(Product As Purchase ) As";
 //BA.debugLineNum = 3489;BA.debugLine="Dim bRet As Boolean=False, xProdPlayStore As tPro";
_bret = anywheresoftware.b4a.keywords.Common.False;
_xprodplaystore = new com.matetejuego.free.publicos._tproductosps();
 //BA.debugLineNum = 3493;BA.debugLine="xProdPlayStore = Producto_GetaProductosPlayStore(";
_xprodplaystore = _producto_getaproductosplaystore(_product);
 //BA.debugLineNum = 3497;BA.debugLine="g_sqlBaseLocalUsuario.BeginTransaction";
_g_sqlbaselocalusuario.BeginTransaction();
 //BA.debugLineNum = 3499;BA.debugLine="bRet = Publicos.Usuario_SumarNeuronas(xProdPlayS";
_bret = mostCurrent._vvvvvvvvvvvvvvv7._usuario_sumarneuronas(mostCurrent.activityBA,_xprodplaystore.Neuronas,_g_sqlbaselocalusuario);
 //BA.debugLineNum = 3501;BA.debugLine="If bRet Then";
if (_bret) { 
 //BA.debugLineNum = 3503;BA.debugLine="bRet = Publicos.Propaganda_SetApaga(g_sqlBaseLo";
_bret = mostCurrent._vvvvvvvvvvvvvvv7._propaganda_setapaga(mostCurrent.activityBA,_g_sqlbaselocalusuario);
 //BA.debugLineNum = 3504;BA.debugLine="gb_EsPremium = True";
_gb_espremium = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 3505;BA.debugLine="bIniciarPremium = False";
_vvvvv0 = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 3506;BA.debugLine="Propaganda_ApagaYa";
_propaganda_apagaya();
 //BA.debugLineNum = 3507;BA.debugLine="Publicos.Base_ActualizaPalabrasDesdeAlmacen(g_s";
mostCurrent._vvvvvvvvvvvvvvv7._base_actualizapalabrasdesdealmacen(mostCurrent.activityBA,_g_sqlbaselocaljuego,anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3509;BA.debugLine="manager.ConsumeProduct(Product)";
_vvvvvv2.ConsumeProduct(processBA,_product);
 };
 //BA.debugLineNum = 3514;BA.debugLine="If bRet Then";
if (_bret) { 
 //BA.debugLineNum = 3515;BA.debugLine="g_sqlBaseLocalUsuario.TransactionSuccessful";
_g_sqlbaselocalusuario.TransactionSuccessful();
 };
 //BA.debugLineNum = 3520;BA.debugLine="g_sqlBaseLocalUsuario.EndTransaction";
_g_sqlbaselocalusuario.EndTransaction();
 //BA.debugLineNum = 3523;BA.debugLine="gi_NeuronasCompradas = xProdPlayStore.Neuronas";
_gi_neuronascompradas = _xprodplaystore.Neuronas;
 //BA.debugLineNum = 3524;BA.debugLine="bComproNeuronas = True";
_vvvvvvvvvv1 = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 3527;BA.debugLine="Return bRet";
if (true) return _bret;
 //BA.debugLineNum = 3528;BA.debugLine="End Sub";
return false;
}
public static boolean  _productos_consumirpendientes(anywheresoftware.b4a.objects.collections.Map _purchases) throws Exception{
boolean _bret = false;
anywheresoftware.b4a.inappbilling3.BillingManager3.Prchase _p = null;
 //BA.debugLineNum = 3719;BA.debugLine="Sub Productos_ConsumirPendientes (Purchases As Map";
 //BA.debugLineNum = 3720;BA.debugLine="Dim bRet As Boolean = False";
_bret = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 3721;BA.debugLine="If Purchases.Size > 0 Then";
if (_purchases.getSize()>0) { 
 //BA.debugLineNum = 3722;BA.debugLine="If Msgbox2(\"Tiene compras de neuronas pendientes";
if (anywheresoftware.b4a.keywords.Common.Msgbox2("Tiene compras de neuronas pendientes de aplicar","Play Store","Aplicar","Dejar pendientes","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null),mostCurrent.activityBA)==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
 //BA.debugLineNum = 3724;BA.debugLine="For Each P As Purchase In Purchases.Values";
final anywheresoftware.b4a.BA.IterableList group4 = _purchases.Values();
final int groupLen4 = group4.getSize();
for (int index4 = 0;index4 < groupLen4 ;index4++){
_p = (anywheresoftware.b4a.inappbilling3.BillingManager3.Prchase)(group4.Get(index4));
 //BA.debugLineNum = 3725;BA.debugLine="If Productos_CompraHacer (P) Then";
if (_productos_comprahacer(_p)) { 
 //BA.debugLineNum = 3726;BA.debugLine="bRet = True";
_bret = anywheresoftware.b4a.keywords.Common.True;
 };
 //BA.debugLineNum = 3728;BA.debugLine="Log(\"producto pendiente procesado: \" & P.Produ";
anywheresoftware.b4a.keywords.Common.Log("producto pendiente procesado: "+_p.getProductId());
 }
;
 };
 };
 //BA.debugLineNum = 3732;BA.debugLine="Return bRet";
if (true) return _bret;
 //BA.debugLineNum = 3733;BA.debugLine="End Sub";
return false;
}
public static boolean  _productos_getproductoownedadsoff() throws Exception{
boolean _bret = false;
int _j = 0;
 //BA.debugLineNum = 3452;BA.debugLine="Sub Productos_GetProductoOwnedAdsOff As Boolean";
 //BA.debugLineNum = 3453;BA.debugLine="Dim bret As Boolean = False";
_bret = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 3455;BA.debugLine="For j = 0 To aProductosPlayStore.Length-1";
{
final int step2 = 1;
final int limit2 = (int) (_vvvvvv4.length-1);
for (_j = (int) (0) ; (step2 > 0 && _j <= limit2) || (step2 < 0 && _j >= limit2); _j = ((int)(0 + _j + step2)) ) {
 //BA.debugLineNum = 3456;BA.debugLine="If aProductosPlayStore(j).Owned And aProductosPla";
if (_vvvvvv4[_j].Owned && _vvvvvv4[_j].ApagaPropaganda) { 
 //BA.debugLineNum = 3457;BA.debugLine="bret = True";
_bret = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 3458;BA.debugLine="Exit";
if (true) break;
 };
 }
};
 //BA.debugLineNum = 3462;BA.debugLine="Return bret";
if (true) return _bret;
 //BA.debugLineNum = 3463;BA.debugLine="End Sub";
return false;
}
public static String  _productos_marcayacomprados() throws Exception{
int _j = 0;
int _i = 0;
 //BA.debugLineNum = 3417;BA.debugLine="Sub Productos_MarcaYaComprados";
 //BA.debugLineNum = 3419;BA.debugLine="For j = 0 To aProductosComprados.Length-1";
{
final int step1 = 1;
final int limit1 = (int) (_vvvvvv3.length-1);
for (_j = (int) (0) ; (step1 > 0 && _j <= limit1) || (step1 < 0 && _j >= limit1); _j = ((int)(0 + _j + step1)) ) {
 //BA.debugLineNum = 3420;BA.debugLine="For i = 0 To aProductosPlayStore.Length-1";
{
final int step2 = 1;
final int limit2 = (int) (_vvvvvv4.length-1);
for (_i = (int) (0) ; (step2 > 0 && _i <= limit2) || (step2 < 0 && _i >= limit2); _i = ((int)(0 + _i + step2)) ) {
 //BA.debugLineNum = 3421;BA.debugLine="If aProductosComprados(j).ProductID = aProducto";
if ((_vvvvvv3[_j].ProductID).equals(_vvvvvv4[_i].ProductID)) { 
 //BA.debugLineNum = 3422;BA.debugLine="aProductosPlayStore(i).Owned = True";
_vvvvvv4[_i].Owned = anywheresoftware.b4a.keywords.Common.True;
 };
 }
};
 }
};
 //BA.debugLineNum = 3426;BA.debugLine="End Sub";
return "";
}
public static String  _productos_muestra() throws Exception{
int[] _icolor = null;
int _icolorletra = 0;
int _icant = 0;
boolean _bpropagandaapagada = false;
anywheresoftware.b4a.objects.PanelWrapper _pnlrecuadro = null;
anywheresoftware.b4a.objects.PanelWrapper _pnlproductos2 = null;
anywheresoftware.b4a.objects.LabelWrapper _lbltitulop = null;
anywheresoftware.b4a.objects.LabelWrapper _lblaclarap = null;
anywheresoftware.b4a.objects.PanelWrapper _pnlaclarap = null;
anywheresoftware.b4a.objects.LabelWrapper _lblcancelarp = null;
int _isepara = 0;
int _itopprod = 0;
int _iheightprod = 0;
int _itextsize = 0;
int _j = 0;
anywheresoftware.b4a.objects.LabelWrapper _vproddisponible = null;
anywheresoftware.b4a.objects.LabelWrapper _vproddisponibleprecio = null;
anywheresoftware.b4a.objects.PanelWrapper _pnlitemp = null;
int _ileft = 0;
int _itop = 0;
int _iwidth = 0;
String _iheight = "";
 //BA.debugLineNum = 3535;BA.debugLine="Sub Productos_Muestra";
 //BA.debugLineNum = 3537;BA.debugLine="iPantallaActiva = xConfiguraPantalla.Producto";
_vvvvvvvv4 = _vvvvvvvv3.Producto;
 //BA.debugLineNum = 3539;BA.debugLine="Dim iColor (3) As Int, iColorLetra As Int";
_icolor = new int[(int) (3)];
;
_icolorletra = 0;
 //BA.debugLineNum = 3540;BA.debugLine="iColor(0) = Colors.RGB(247, 219, 245)";
_icolor[(int) (0)] = anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (247),(int) (219),(int) (245));
 //BA.debugLineNum = 3541;BA.debugLine="iColor(1) = Colors.RGB(242, 198, 241)";
_icolor[(int) (1)] = anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (242),(int) (198),(int) (241));
 //BA.debugLineNum = 3542;BA.debugLine="iColor(2) = Colors.RGB(238, 183, 237)";
_icolor[(int) (2)] = anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (238),(int) (183),(int) (237));
 //BA.debugLineNum = 3543;BA.debugLine="iColorLetra = Get_aTextColor(gi_CombinacionColores";
_icolorletra = _get_atextcolor(_gi_combinacioncolores,(int) (1));
 //BA.debugLineNum = 3546;BA.debugLine="Dim iCant As Int = 0, bPropagandaApagada As Boolea";
_icant = (int) (0);
_bpropagandaapagada = false;
 //BA.debugLineNum = 3548;BA.debugLine="bPropagandaApagada = Productos_GetProductoOwnedAds";
_bpropagandaapagada = _productos_getproductoownedadsoff();
 //BA.debugLineNum = 3551;BA.debugLine="pnlInvisible.color = Colors.transparent";
mostCurrent._pnlinvisible.setColor(anywheresoftware.b4a.keywords.Common.Colors.Transparent);
 //BA.debugLineNum = 3553;BA.debugLine="pnlInvisible.BringToFront";
mostCurrent._pnlinvisible.BringToFront();
 //BA.debugLineNum = 3557;BA.debugLine="Dim pnlRecuadro As Panel";
_pnlrecuadro = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 3558;BA.debugLine="pnlRecuadro.Initialize(\"\")";
_pnlrecuadro.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 3559;BA.debugLine="pnlRecuadro.Color = Colors.LightGray";
_pnlrecuadro.setColor(anywheresoftware.b4a.keywords.Common.Colors.LightGray);
 //BA.debugLineNum = 3562;BA.debugLine="pnlInvisible.AddView(pnlRecuadro, 0,0,0,0)";
mostCurrent._pnlinvisible.AddView((android.view.View)(_pnlrecuadro.getObject()),(int) (0),(int) (0),(int) (0),(int) (0));
 //BA.debugLineNum = 3563;BA.debugLine="pnlRecuadro.Height = pnlInvisible.Height/2 + 4dip";
_pnlrecuadro.setHeight((int) (mostCurrent._pnlinvisible.getHeight()/(double)2+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (4))));
 //BA.debugLineNum = 3564;BA.debugLine="pnlRecuadro.Width = pnlInvisible.Width *0.8 + 4di";
_pnlrecuadro.setWidth((int) (mostCurrent._pnlinvisible.getWidth()*0.8+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (4))));
 //BA.debugLineNum = 3565;BA.debugLine="Publicos.CentrarControlEnPanel(pnlInvisible, pnlR";
mostCurrent._vvvvvvvvvvvvvvv7._vv0(mostCurrent.activityBA,mostCurrent._pnlinvisible,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(_pnlrecuadro.getObject())),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3568;BA.debugLine="Dim pnlProductos2 As Panel";
_pnlproductos2 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 3569;BA.debugLine="pnlProductos2.Initialize(\"\")";
_pnlproductos2.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 3571;BA.debugLine="pnlRecuadro.AddView(pnlProductos2, 0,0,0,0)";
_pnlrecuadro.AddView((android.view.View)(_pnlproductos2.getObject()),(int) (0),(int) (0),(int) (0),(int) (0));
 //BA.debugLineNum = 3572;BA.debugLine="pnlProductos2.Height = pnlRecuadro.Height-4dip";
_pnlproductos2.setHeight((int) (_pnlrecuadro.getHeight()-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (4))));
 //BA.debugLineNum = 3573;BA.debugLine="pnlProductos2.Width = pnlRecuadro.Width-4dip";
_pnlproductos2.setWidth((int) (_pnlrecuadro.getWidth()-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (4))));
 //BA.debugLineNum = 3574;BA.debugLine="Publicos.CentrarControlEnPanel(pnlRecuadro, pnlPr";
mostCurrent._vvvvvvvvvvvvvvv7._vv0(mostCurrent.activityBA,_pnlrecuadro,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(_pnlproductos2.getObject())),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3578;BA.debugLine="Dim lblTituloP As Label";
_lbltitulop = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 3579;BA.debugLine="lblTituloP.Initialize(\"\")";
_lbltitulop.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 3580;BA.debugLine="pnlProductos2.AddView(lblTituloP , 0,0,pnlProduct";
_pnlproductos2.AddView((android.view.View)(_lbltitulop.getObject()),(int) (0),(int) (0),_pnlproductos2.getWidth(),(int) (_pnlproductos2.getHeight()*0.15));
 //BA.debugLineNum = 3581;BA.debugLine="lblTituloP.Typeface = tFontOpenSansSemiBold";
_lbltitulop.setTypeface((android.graphics.Typeface)(_vvvvvvvvvv4.getObject()));
 //BA.debugLineNum = 3582;BA.debugLine="lblTituloP.Text = \"COMPRA DE NEURONAS\"";
_lbltitulop.setText((Object)("COMPRA DE NEURONAS"));
 //BA.debugLineNum = 3583;BA.debugLine="Publicos.SetLabelTextSize(lblTituloP , lblTituloP";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv4(mostCurrent.activityBA,_lbltitulop,_lbltitulop.getText(),(float) (40),(float) (6),(int) (70));
 //BA.debugLineNum = 3584;BA.debugLine="lblTituloP.Color = gt_Color.colormedio";
_lbltitulop.setColor(mostCurrent._gt_color.ColorMedio);
 //BA.debugLineNum = 3585;BA.debugLine="lblTituloP.TextColor = Colors.white";
_lbltitulop.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 3586;BA.debugLine="lblTituloP.Gravity = Gravity.CENTER";
_lbltitulop.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 3591;BA.debugLine="Dim lblAclaraP As Label, pnlAclaraP As Panel";
_lblaclarap = new anywheresoftware.b4a.objects.LabelWrapper();
_pnlaclarap = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 3592;BA.debugLine="lblAclaraP.Initialize(\"\")";
_lblaclarap.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 3593;BA.debugLine="pnlAclaraP.Initialize(\"\")";
_pnlaclarap.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 3594;BA.debugLine="pnlProductos2.AddView(pnlAclaraP,0 ,pnlProductos2";
_pnlproductos2.AddView((android.view.View)(_pnlaclarap.getObject()),(int) (0),(int) (_pnlproductos2.getHeight()*0.64),_pnlproductos2.getWidth(),(int) (_pnlproductos2.getHeight()*0.20));
 //BA.debugLineNum = 3595;BA.debugLine="pnlAclaraP.AddView(lblAclaraP,pnlProductos2.Width";
_pnlaclarap.AddView((android.view.View)(_lblaclarap.getObject()),(int) (_pnlproductos2.getWidth()*0.05),(int) (0),(int) (_pnlproductos2.getWidth()*0.9),_pnlaclarap.getHeight());
 //BA.debugLineNum = 3597;BA.debugLine="lblAclaraP.Typeface = tFontOpenSansSemiBold";
_lblaclarap.setTypeface((android.graphics.Typeface)(_vvvvvvvvvv4.getObject()));
 //BA.debugLineNum = 3599;BA.debugLine="If Publicos.Get_EsPremium (g_sqlBaseLocalUsuario)";
if (mostCurrent._vvvvvvvvvvvvvvv7._get_espremium(mostCurrent.activityBA,_g_sqlbaselocalusuario)==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 3600;BA.debugLine="If Publicos.Get_UsuarioAntiguo(g_sqlBaseLocalUsu";
if (mostCurrent._vvvvvvvvvvvvvvv7._get_usuarioantiguo(mostCurrent.activityBA,_g_sqlbaselocalusuario)) { 
 //BA.debugLineNum = 3601;BA.debugLine="lblAclaraP.Text = \"Todas las compras incluyen a";
_lblaclarap.setText((Object)("Todas las compras incluyen apagado de propaganda "));
 }else {
 //BA.debugLineNum = 3603;BA.debugLine="lblAclaraP.Text = \"Todas las compras incluyen a";
_lblaclarap.setText((Object)("Todas las compras incluyen apagado de propaganda y 200 palabras adicionales"));
 };
 }else {
 //BA.debugLineNum = 3606;BA.debugLine="lblAclaraP.Text = \"Compras Exclusivas Versión Pr";
_lblaclarap.setText((Object)("Compras Exclusivas Versión Premium"));
 };
 //BA.debugLineNum = 3610;BA.debugLine="Publicos.SetLabelTextSize(lblAclaraP, lblAclaraP.";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv4(mostCurrent.activityBA,_lblaclarap,_lblaclarap.getText(),(float) (40),(float) (6),(int) (90));
 //BA.debugLineNum = 3611;BA.debugLine="pnlAclaraP.Color = gt_Color.colorclaro";
_pnlaclarap.setColor(mostCurrent._gt_color.ColorClaro);
 //BA.debugLineNum = 3612;BA.debugLine="lblAclaraP.Color = Colors.transparent";
_lblaclarap.setColor(anywheresoftware.b4a.keywords.Common.Colors.Transparent);
 //BA.debugLineNum = 3613;BA.debugLine="lblAclaraP.TextColor = Colors.white";
_lblaclarap.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 3614;BA.debugLine="lblAclaraP.Gravity = Gravity.CENTER";
_lblaclarap.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 3618;BA.debugLine="Dim lblCancelarP As Label";
_lblcancelarp = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 3620;BA.debugLine="lbl61.Tag = xConfiguraPantalla.Producto";
mostCurrent._lbl61.setTag((Object)(_vvvvvvvv3.Producto));
 //BA.debugLineNum = 3621;BA.debugLine="lblCancelarP.initialize(\"lbl61\")";
_lblcancelarp.Initialize(mostCurrent.activityBA,"lbl61");
 //BA.debugLineNum = 3622;BA.debugLine="lblCancelarP.Color = Colors.white";
_lblcancelarp.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 3623;BA.debugLine="lblCancelarP.TextColor = gt_Color.ColorTexto";
_lblcancelarp.setTextColor(mostCurrent._gt_color.ColorTexto);
 //BA.debugLineNum = 3624;BA.debugLine="pnlProductos2.AddView(lblCancelarP, pnlProductos2";
_pnlproductos2.AddView((android.view.View)(_lblcancelarp.getObject()),(int) (_pnlproductos2.getWidth()*0.05),(int) (0),(int) (_pnlproductos2.getWidth()*0.9),mostCurrent._lbl61.getHeight());
 //BA.debugLineNum = 3625;BA.debugLine="lblCancelarP.Top = pnlProductos2.Height-lblCancel";
_lblcancelarp.setTop((int) (_pnlproductos2.getHeight()-_lblcancelarp.getHeight()-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (3))));
 //BA.debugLineNum = 3626;BA.debugLine="lblCancelarP.Typeface = tFontOpenSansLight";
_lblcancelarp.setTypeface((android.graphics.Typeface)(_vvvvvvvvvv5.getObject()));
 //BA.debugLineNum = 3627;BA.debugLine="lblCancelarP.Text = \"CANCELAR\"";
_lblcancelarp.setText((Object)("CANCELAR"));
 //BA.debugLineNum = 3628;BA.debugLine="Publicos.SetLabelTextSize(lblCancelarP, lblCancel";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv4(mostCurrent.activityBA,_lblcancelarp,_lblcancelarp.getText(),(float) (40),(float) (6),(int) (100));
 //BA.debugLineNum = 3629;BA.debugLine="lblCancelarP.Gravity = Gravity.CENTER";
_lblcancelarp.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 3631;BA.debugLine="pnlProductos2.Color = Colors.white";
_pnlproductos2.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 3633;BA.debugLine="Dim iSepara As Int = pnlProductos2.Height*0.02";
_isepara = (int) (_pnlproductos2.getHeight()*0.02);
 //BA.debugLineNum = 3634;BA.debugLine="Dim iTopProd As Int = lblTituloP.Top + lblTituloP.";
_itopprod = (int) (_lbltitulop.getTop()+_lbltitulop.getHeight()+_isepara);
 //BA.debugLineNum = 3635;BA.debugLine="Dim iHeightProd As Int = pnlProductos2.Height*0.14";
_iheightprod = (int) (_pnlproductos2.getHeight()*0.14);
 //BA.debugLineNum = 3636;BA.debugLine="Dim iTextSize As Int = lblTituloP.TextSize";
_itextsize = (int) (_lbltitulop.getTextSize());
 //BA.debugLineNum = 3638;BA.debugLine="For j =0 To aProductosPlayStore.Length-1";
{
final int step69 = 1;
final int limit69 = (int) (_vvvvvv4.length-1);
for (_j = (int) (0) ; (step69 > 0 && _j <= limit69) || (step69 < 0 && _j >= limit69); _j = ((int)(0 + _j + step69)) ) {
 //BA.debugLineNum = 3640;BA.debugLine="If aProductosPlayStore(j).Owned = False And _";
if (_vvvvvv4[_j].Owned==anywheresoftware.b4a.keywords.Common.False && _vvvvvv4[_j].ExlusivoPremium==_gb_espremium && _vvvvvv4[_j].CompraPremiumOriginal==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 3646;BA.debugLine="iCant = iCant +1";
_icant = (int) (_icant+1);
 //BA.debugLineNum = 3647;BA.debugLine="Dim vProdDisponible As Label, vProdDisponiblePr";
_vproddisponible = new anywheresoftware.b4a.objects.LabelWrapper();
_vproddisponibleprecio = new anywheresoftware.b4a.objects.LabelWrapper();
_pnlitemp = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 3649;BA.debugLine="pnlItemP.Initialize(\"pnlItemP\")";
_pnlitemp.Initialize(mostCurrent.activityBA,"pnlItemP");
 //BA.debugLineNum = 3650;BA.debugLine="vProdDisponible.Initialize(\"vProdDisponible\")";
_vproddisponible.Initialize(mostCurrent.activityBA,"vProdDisponible");
 //BA.debugLineNum = 3651;BA.debugLine="vProdDisponiblePrecio.Initialize(\"vProdDisponib";
_vproddisponibleprecio.Initialize(mostCurrent.activityBA,"vProdDisponiblePrecio");
 //BA.debugLineNum = 3652;BA.debugLine="vProdDisponible.Text = aProductosPlayStore(j).T";
_vproddisponible.setText((Object)(_vvvvvv4[_j].Title));
 //BA.debugLineNum = 3653;BA.debugLine="vProdDisponiblePrecio.Text = aProductosPlayStor";
_vproddisponibleprecio.setText((Object)(_vvvvvv4[_j].Price));
 //BA.debugLineNum = 3654;BA.debugLine="vProdDisponible.Tag = j ' guarda la posicion de";
_vproddisponible.setTag((Object)(_j));
 //BA.debugLineNum = 3655;BA.debugLine="pnlItemP.Tag = j";
_pnlitemp.setTag((Object)(_j));
 //BA.debugLineNum = 3656;BA.debugLine="vProdDisponible.TextColor = iColorLetra 'Get_aT";
_vproddisponible.setTextColor(_icolorletra);
 //BA.debugLineNum = 3657;BA.debugLine="vProdDisponiblePrecio.Textcolor= iColorLetra'Ge";
_vproddisponibleprecio.setTextColor(_icolorletra);
 //BA.debugLineNum = 3658;BA.debugLine="vProdDisponible.Color = Colors.Transparent";
_vproddisponible.setColor(anywheresoftware.b4a.keywords.Common.Colors.Transparent);
 //BA.debugLineNum = 3659;BA.debugLine="vProdDisponiblePrecio.Color = Colors.transparen";
_vproddisponibleprecio.setColor(anywheresoftware.b4a.keywords.Common.Colors.Transparent);
 //BA.debugLineNum = 3661;BA.debugLine="pnlItemP.Color = iColor(iCant-1)";
_pnlitemp.setColor(_icolor[(int) (_icant-1)]);
 //BA.debugLineNum = 3663;BA.debugLine="vProdDisponible.Gravity = Gravity.right + Gravi";
_vproddisponible.setGravity((int) (anywheresoftware.b4a.keywords.Common.Gravity.RIGHT+anywheresoftware.b4a.keywords.Common.Gravity.CENTER_VERTICAL));
 //BA.debugLineNum = 3664;BA.debugLine="vProdDisponiblePrecio.Gravity = Gravity.RIGHT +";
_vproddisponibleprecio.setGravity((int) (anywheresoftware.b4a.keywords.Common.Gravity.RIGHT+anywheresoftware.b4a.keywords.Common.Gravity.CENTER_VERTICAL));
 //BA.debugLineNum = 3665;BA.debugLine="vProdDisponible.Typeface = tFontOpenSansLight";
_vproddisponible.setTypeface((android.graphics.Typeface)(_vvvvvvvvvv5.getObject()));
 //BA.debugLineNum = 3666;BA.debugLine="vProdDisponiblePrecio.Typeface = tFontOpenSansL";
_vproddisponibleprecio.setTypeface((android.graphics.Typeface)(_vvvvvvvvvv5.getObject()));
 //BA.debugLineNum = 3668;BA.debugLine="pnlProductos2.addview(pnlItemP, 0, iTopProd + (";
_pnlproductos2.AddView((android.view.View)(_pnlitemp.getObject()),(int) (0),(int) (_itopprod+(_icant-1)*(_iheightprod+_isepara)),_pnlproductos2.getWidth(),_iheightprod);
 //BA.debugLineNum = 3669;BA.debugLine="pnlItemP.AddView(vProdDisponible, 0, 0, pnlItem";
_pnlitemp.AddView((android.view.View)(_vproddisponible.getObject()),(int) (0),(int) (0),(int) (_pnlitemp.getWidth()*0.3),_pnlitemp.getHeight());
 //BA.debugLineNum = 3670;BA.debugLine="pnlItemP.AddView(vProdDisponiblePrecio, 0, 0, p";
_pnlitemp.AddView((android.view.View)(_vproddisponibleprecio.getObject()),(int) (0),(int) (0),(int) (_pnlitemp.getWidth()*0.95),_pnlitemp.getHeight());
 //BA.debugLineNum = 3673;BA.debugLine="If j=0 Then";
if (_j==0) { 
 //BA.debugLineNum = 3674;BA.debugLine="Publicos.SetLabelTextSize(vProdDisponible, vPr";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv4(mostCurrent.activityBA,_vproddisponible,_vproddisponible.getText(),(float) (40),(float) (6),(int) (80));
 //BA.debugLineNum = 3675;BA.debugLine="vProdDisponiblePrecio.TextSize = vProdDisponib";
_vproddisponibleprecio.setTextSize(_vproddisponible.getTextSize());
 //BA.debugLineNum = 3676;BA.debugLine="iTextSize = vProdDisponible.TextSize";
_itextsize = (int) (_vproddisponible.getTextSize());
 }else {
 //BA.debugLineNum = 3678;BA.debugLine="vProdDisponible.TextSize = iTextSize";
_vproddisponible.setTextSize((float) (_itextsize));
 //BA.debugLineNum = 3679;BA.debugLine="vProdDisponiblePrecio.Textsize = iTextSize";
_vproddisponibleprecio.setTextSize((float) (_itextsize));
 };
 //BA.debugLineNum = 3684;BA.debugLine="Dim avpnlItemP (iCant) As Panel";
mostCurrent._vvvvvvvvvvvvvvvvvvvvv0 = new anywheresoftware.b4a.objects.PanelWrapper[_icant];
{
int d0 = mostCurrent._vvvvvvvvvvvvvvvvvvvvv0.length;
for (int i0 = 0;i0 < d0;i0++) {
mostCurrent._vvvvvvvvvvvvvvvvvvvvv0[i0] = new anywheresoftware.b4a.objects.PanelWrapper();
}
}
;
 //BA.debugLineNum = 3685;BA.debugLine="avpnlItemP(iCant-1) = pnlItemP";
mostCurrent._vvvvvvvvvvvvvvvvvvvvv0[(int) (_icant-1)] = _pnlitemp;
 };
 }
};
 //BA.debugLineNum = 3690;BA.debugLine="pnlAclaraP.bringtofront";
_pnlaclarap.BringToFront();
 //BA.debugLineNum = 3691;BA.debugLine="pnlInvisible.Visible = True ' que incongruencia";
mostCurrent._pnlinvisible.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3692;BA.debugLine="pnlInvisible.SetColorAnimated(1000, Colors.White,";
mostCurrent._pnlinvisible.SetColorAnimated((int) (1000),anywheresoftware.b4a.keywords.Common.Colors.White,anywheresoftware.b4a.keywords.Common.Colors.Transparent);
 //BA.debugLineNum = 3695;BA.debugLine="pnlProductos2.Visible=True";
_pnlproductos2.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3696;BA.debugLine="Dim ileft As Int = pnlRecuadro.Left, iTop As Int =";
_ileft = _pnlrecuadro.getLeft();
_itop = _pnlrecuadro.getTop();
_iwidth = _pnlrecuadro.getWidth();
_iheight = BA.NumberToString(_pnlrecuadro.getHeight());
 //BA.debugLineNum = 3697;BA.debugLine="pnlRecuadro.SetLayoutAnimated(500, Activity.Width/";
_pnlrecuadro.SetLayoutAnimated((int) (500),(int) (mostCurrent._activity.getWidth()/(double)2),(int) (0),(int) (0),(int) (0));
 //BA.debugLineNum = 3698;BA.debugLine="pnlRecuadro.SetLayoutAnimated(1000, ileft, iTop, i";
_pnlrecuadro.SetLayoutAnimated((int) (1000),_ileft,_itop,_iwidth,(int)(Double.parseDouble(_iheight)));
 //BA.debugLineNum = 3700;BA.debugLine="pnlHistoria.Visible = False";
mostCurrent._pnlhistoria.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3702;BA.debugLine="End Sub";
return "";
}
public static String  _productos_muestracomproneuronas(int _ineuronascompradas) throws Exception{
int[] _icolor = null;
int _icolorletra = 0;
int _icant = 0;
anywheresoftware.b4a.objects.PanelWrapper _pnlrecuadro = null;
anywheresoftware.b4a.objects.PanelWrapper _pnlproductos2 = null;
anywheresoftware.b4a.objects.LabelWrapper _lbltitulop = null;
anywheresoftware.b4a.objects.ImageViewWrapper _imgneuronagira = null;
anywheresoftware.b4a.objects.LabelWrapper _lblaclarap = null;
anywheresoftware.b4a.objects.PanelWrapper _pnlaclarap = null;
int _ineuronasactuales = 0;
anywheresoftware.b4a.objects.LabelWrapper _lblcancelarp = null;
 //BA.debugLineNum = 3811;BA.debugLine="Sub Productos_MuestraComproNeuronas (iNeuronasComp";
 //BA.debugLineNum = 3812;BA.debugLine="pnlInvisible.RemoveAllViews";
mostCurrent._pnlinvisible.RemoveAllViews();
 //BA.debugLineNum = 3813;BA.debugLine="iPantallaActiva = xConfiguraPantalla.ComproNeurona";
_vvvvvvvv4 = _vvvvvvvv3.ComproNeuronas;
 //BA.debugLineNum = 3815;BA.debugLine="Dim iColor (3) As Int, iColorLetra As Int";
_icolor = new int[(int) (3)];
;
_icolorletra = 0;
 //BA.debugLineNum = 3816;BA.debugLine="iColor(0) = Colors.RGB(247, 219, 245)";
_icolor[(int) (0)] = anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (247),(int) (219),(int) (245));
 //BA.debugLineNum = 3817;BA.debugLine="iColor(1) = Colors.RGB(242, 198, 241)";
_icolor[(int) (1)] = anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (242),(int) (198),(int) (241));
 //BA.debugLineNum = 3818;BA.debugLine="iColor(2) = Colors.RGB(238, 183, 237)";
_icolor[(int) (2)] = anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (238),(int) (183),(int) (237));
 //BA.debugLineNum = 3819;BA.debugLine="iColorLetra = Get_aTextColor(gi_CombinacionColores";
_icolorletra = _get_atextcolor(_gi_combinacioncolores,(int) (1));
 //BA.debugLineNum = 3821;BA.debugLine="Dim iCant As Int = 0";
_icant = (int) (0);
 //BA.debugLineNum = 3822;BA.debugLine="pnlInvisible.color = Colors.transparent";
mostCurrent._pnlinvisible.setColor(anywheresoftware.b4a.keywords.Common.Colors.Transparent);
 //BA.debugLineNum = 3824;BA.debugLine="pnlInvisible.BringToFront";
mostCurrent._pnlinvisible.BringToFront();
 //BA.debugLineNum = 3826;BA.debugLine="Dim pnlRecuadro As Panel";
_pnlrecuadro = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 3827;BA.debugLine="pnlRecuadro.Initialize(\"\")";
_pnlrecuadro.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 3828;BA.debugLine="pnlRecuadro.Color = Colors.LightGray";
_pnlrecuadro.setColor(anywheresoftware.b4a.keywords.Common.Colors.LightGray);
 //BA.debugLineNum = 3830;BA.debugLine="pnlInvisible.AddView(pnlRecuadro, 0,0,0,0)";
mostCurrent._pnlinvisible.AddView((android.view.View)(_pnlrecuadro.getObject()),(int) (0),(int) (0),(int) (0),(int) (0));
 //BA.debugLineNum = 3831;BA.debugLine="pnlRecuadro.Height = pnlInvisible.Height/2 + 4di";
_pnlrecuadro.setHeight((int) (mostCurrent._pnlinvisible.getHeight()/(double)2+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (4))));
 //BA.debugLineNum = 3832;BA.debugLine="pnlRecuadro.Width = pnlInvisible.Width *0.8 + 4d";
_pnlrecuadro.setWidth((int) (mostCurrent._pnlinvisible.getWidth()*0.8+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (4))));
 //BA.debugLineNum = 3833;BA.debugLine="Publicos.CentrarControlEnPanel(pnlInvisible, pnl";
mostCurrent._vvvvvvvvvvvvvvv7._vv0(mostCurrent.activityBA,mostCurrent._pnlinvisible,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(_pnlrecuadro.getObject())),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3835;BA.debugLine="Dim pnlProductos2 As Panel";
_pnlproductos2 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 3836;BA.debugLine="pnlProductos2.Initialize(\"\")";
_pnlproductos2.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 3838;BA.debugLine="pnlRecuadro.AddView(pnlProductos2, 0,0,0,0)";
_pnlrecuadro.AddView((android.view.View)(_pnlproductos2.getObject()),(int) (0),(int) (0),(int) (0),(int) (0));
 //BA.debugLineNum = 3839;BA.debugLine="pnlProductos2.Height = pnlRecuadro.Height-4dip";
_pnlproductos2.setHeight((int) (_pnlrecuadro.getHeight()-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (4))));
 //BA.debugLineNum = 3840;BA.debugLine="pnlProductos2.Width = pnlRecuadro.Width-4dip";
_pnlproductos2.setWidth((int) (_pnlrecuadro.getWidth()-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (4))));
 //BA.debugLineNum = 3841;BA.debugLine="Publicos.CentrarControlEnPanel(pnlRecuadro, pnlP";
mostCurrent._vvvvvvvvvvvvvvv7._vv0(mostCurrent.activityBA,_pnlrecuadro,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(_pnlproductos2.getObject())),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3845;BA.debugLine="Dim lblTituloP As Label";
_lbltitulop = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 3846;BA.debugLine="lblTituloP.Initialize(\"\")";
_lbltitulop.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 3847;BA.debugLine="pnlProductos2.AddView(lblTituloP , 0,0,pnlProduct";
_pnlproductos2.AddView((android.view.View)(_lbltitulop.getObject()),(int) (0),(int) (0),_pnlproductos2.getWidth(),(int) (_pnlproductos2.getHeight()*0.15));
 //BA.debugLineNum = 3848;BA.debugLine="lblTituloP.Typeface = tFontOpenSansSemiBold";
_lbltitulop.setTypeface((android.graphics.Typeface)(_vvvvvvvvvv4.getObject()));
 //BA.debugLineNum = 3849;BA.debugLine="lblTituloP.Text = \"COMPRA DE \" &iNeuronasComprada";
_lbltitulop.setText((Object)("COMPRA DE "+BA.NumberToString(_ineuronascompradas)+" NEURONAS"));
 //BA.debugLineNum = 3850;BA.debugLine="Publicos.SetLabelTextSize(lblTituloP , lblTituloP";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv4(mostCurrent.activityBA,_lbltitulop,_lbltitulop.getText(),(float) (40),(float) (6),(int) (70));
 //BA.debugLineNum = 3851;BA.debugLine="lblTituloP.Color = gt_Color.ColorMedio";
_lbltitulop.setColor(mostCurrent._gt_color.ColorMedio);
 //BA.debugLineNum = 3852;BA.debugLine="lblTituloP.TextColor = Colors.white";
_lbltitulop.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 3853;BA.debugLine="lblTituloP.Gravity = Gravity.CENTER";
_lbltitulop.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 3855;BA.debugLine="Dim imgNeuronaGira As ImageView";
_imgneuronagira = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 3856;BA.debugLine="imgNeuronaGira.Initialize(\"\")";
_imgneuronagira.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 3857;BA.debugLine="pnlProductos2.AddView(imgNeuronaGira,0,0,pnlProdu";
_pnlproductos2.AddView((android.view.View)(_imgneuronagira.getObject()),(int) (0),(int) (0),(int) (_pnlproductos2.getHeight()*0.5),(int) (_pnlproductos2.getHeight()*0.5));
 //BA.debugLineNum = 3858;BA.debugLine="Publicos.CentrarControlEnPanel(pnlProductos2, img";
mostCurrent._vvvvvvvvvvvvvvv7._vv0(mostCurrent.activityBA,_pnlproductos2,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(_imgneuronagira.getObject())),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3859;BA.debugLine="imgNeuronaGira.Top = pnlProductos2.Height*0.15";
_imgneuronagira.setTop((int) (_pnlproductos2.getHeight()*0.15));
 //BA.debugLineNum = 3860;BA.debugLine="imgNeuronaGira.SetBackgroundImage(bitmNeurona)";
_imgneuronagira.SetBackgroundImage((android.graphics.Bitmap)(_vvvvvvv1.getObject()));
 //BA.debugLineNum = 3865;BA.debugLine="Dim lblAclaraP As Label, pnlAclaraP As Panel";
_lblaclarap = new anywheresoftware.b4a.objects.LabelWrapper();
_pnlaclarap = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 3866;BA.debugLine="lblAclaraP.Initialize(\"\")";
_lblaclarap.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 3867;BA.debugLine="pnlAclaraP.Initialize(\"\")";
_pnlaclarap.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 3868;BA.debugLine="pnlProductos2.AddView(pnlAclaraP,0 ,pnlProductos2";
_pnlproductos2.AddView((android.view.View)(_pnlaclarap.getObject()),(int) (0),(int) (_pnlproductos2.getHeight()*0.64),_pnlproductos2.getWidth(),(int) (_pnlproductos2.getHeight()*0.20));
 //BA.debugLineNum = 3869;BA.debugLine="pnlAclaraP.AddView(lblAclaraP,pnlProductos2.Width";
_pnlaclarap.AddView((android.view.View)(_lblaclarap.getObject()),(int) (_pnlproductos2.getWidth()*0.05),(int) (0),(int) (_pnlproductos2.getWidth()*0.9),_pnlaclarap.getHeight());
 //BA.debugLineNum = 3871;BA.debugLine="lblAclaraP.Typeface = tFontOpenSansSemiBold";
_lblaclarap.setTypeface((android.graphics.Typeface)(_vvvvvvvvvv4.getObject()));
 //BA.debugLineNum = 3872;BA.debugLine="lblAclaraP.Text = \"\"";
_lblaclarap.setText((Object)(""));
 //BA.debugLineNum = 3873;BA.debugLine="pnlAclaraP.Color = gt_Color.colormedio";
_pnlaclarap.setColor(mostCurrent._gt_color.ColorMedio);
 //BA.debugLineNum = 3874;BA.debugLine="lblAclaraP.Color = Colors.transparent";
_lblaclarap.setColor(anywheresoftware.b4a.keywords.Common.Colors.Transparent);
 //BA.debugLineNum = 3875;BA.debugLine="lblAclaraP.TextColor = Colors.white";
_lblaclarap.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 3876;BA.debugLine="lblAclaraP.Gravity = Gravity.CENTER";
_lblaclarap.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 3878;BA.debugLine="Dim iNeuronasActuales As Int = lblNeuronas.Text";
_ineuronasactuales = (int)(Double.parseDouble(mostCurrent._lblneuronas.getText()));
 //BA.debugLineNum = 3879;BA.debugLine="iNeuronasActuales = iNeuronasActuales + iNeuronas";
_ineuronasactuales = (int) (_ineuronasactuales+_ineuronascompradas);
 //BA.debugLineNum = 3880;BA.debugLine="lblAclaraP.Text =  lblNeuronas.TEXT &\"+\" & iNeuro";
_lblaclarap.setText((Object)(mostCurrent._lblneuronas.getText()+"+"+BA.NumberToString(_ineuronascompradas)+"="+BA.NumberToString(_ineuronasactuales)+"  NEURONAS"));
 //BA.debugLineNum = 3881;BA.debugLine="Publicos.SetLabelTextSize(lblAclaraP, lblAclaraP.";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv4(mostCurrent.activityBA,_lblaclarap,_lblaclarap.getText(),(float) (40),(float) (6),(int) (90));
 //BA.debugLineNum = 3883;BA.debugLine="If lblTituloP.TextSize < lblAclaraP.TextSize Then";
if (_lbltitulop.getTextSize()<_lblaclarap.getTextSize()) { 
 //BA.debugLineNum = 3884;BA.debugLine="lblAclaraP.TextSize = lblTituloP.TextSize";
_lblaclarap.setTextSize(_lbltitulop.getTextSize());
 };
 //BA.debugLineNum = 3889;BA.debugLine="Dim lblCancelarP As Label";
_lblcancelarp = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 3891;BA.debugLine="lbl61.Tag = xConfiguraPantalla.Producto";
mostCurrent._lbl61.setTag((Object)(_vvvvvvvv3.Producto));
 //BA.debugLineNum = 3892;BA.debugLine="lblCancelarP.initialize(\"lbl61\")";
_lblcancelarp.Initialize(mostCurrent.activityBA,"lbl61");
 //BA.debugLineNum = 3893;BA.debugLine="lblCancelarP.Color = gt_Color.ColorDefault";
_lblcancelarp.setColor(mostCurrent._gt_color.ColorDefault);
 //BA.debugLineNum = 3894;BA.debugLine="lblCancelarP.TextColor = gt_Color.ColorTexto";
_lblcancelarp.setTextColor(mostCurrent._gt_color.ColorTexto);
 //BA.debugLineNum = 3895;BA.debugLine="pnlProductos2.AddView(lblCancelarP, pnlProductos2";
_pnlproductos2.AddView((android.view.View)(_lblcancelarp.getObject()),(int) (_pnlproductos2.getWidth()*0.05),(int) (0),(int) (_pnlproductos2.getWidth()*0.9),mostCurrent._lbl61.getHeight());
 //BA.debugLineNum = 3896;BA.debugLine="lblCancelarP.Top = pnlProductos2.Height-lblCancel";
_lblcancelarp.setTop((int) (_pnlproductos2.getHeight()-_lblcancelarp.getHeight()-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (3))));
 //BA.debugLineNum = 3897;BA.debugLine="lblCancelarP.Typeface = tFontOpenSansLight";
_lblcancelarp.setTypeface((android.graphics.Typeface)(_vvvvvvvvvv5.getObject()));
 //BA.debugLineNum = 3898;BA.debugLine="lblCancelarP.Text = \"SEGUIR\"";
_lblcancelarp.setText((Object)("SEGUIR"));
 //BA.debugLineNum = 3899;BA.debugLine="Publicos.SetLabelTextSize(lblCancelarP, lblCancel";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv4(mostCurrent.activityBA,_lblcancelarp,_lblcancelarp.getText(),(float) (40),(float) (6),(int) (100));
 //BA.debugLineNum = 3900;BA.debugLine="lblCancelarP.Gravity = Gravity.CENTER";
_lblcancelarp.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 3901;BA.debugLine="pnlProductos2.Color = Colors.white";
_pnlproductos2.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 3905;BA.debugLine="pnlAclaraP.bringtofront";
_pnlaclarap.BringToFront();
 //BA.debugLineNum = 3906;BA.debugLine="pnlInvisible.Visible = True ' que incongruencia";
mostCurrent._pnlinvisible.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3907;BA.debugLine="pnlInvisible.SetColorAnimated(1000, Colors.White,";
mostCurrent._pnlinvisible.SetColorAnimated((int) (1000),anywheresoftware.b4a.keywords.Common.Colors.White,anywheresoftware.b4a.keywords.Common.Colors.Transparent);
 //BA.debugLineNum = 3909;BA.debugLine="pnlProductos2.Visible=True";
_pnlproductos2.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3913;BA.debugLine="Panel1.Visible = True";
mostCurrent._panel1.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3914;BA.debugLine="Panel11.Visible = False";
mostCurrent._panel11.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3915;BA.debugLine="Panel2.Visible= True";
mostCurrent._panel2.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3916;BA.debugLine="Panel3.Visible = True";
mostCurrent._panel3.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3917;BA.debugLine="Panel4.Visible = True";
mostCurrent._panel4.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3918;BA.debugLine="Panel41.Visible = False";
mostCurrent._panel41.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3919;BA.debugLine="Panel5.Visible = True";
mostCurrent._panel5.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3920;BA.debugLine="Panel51.Visible = False";
mostCurrent._panel51.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3922;BA.debugLine="imgAnimacion.Visible = False";
mostCurrent._imganimacion.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3923;BA.debugLine="lbl21.Visible = False";
mostCurrent._lbl21.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3925;BA.debugLine="Panel6.visible = True";
mostCurrent._panel6.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3926;BA.debugLine="Panel61.Visible = False";
mostCurrent._panel61.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3927;BA.debugLine="imgLoading.Visible = False";
mostCurrent._imgloading.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3929;BA.debugLine="pnlHistoria.Visible = False";
mostCurrent._pnlhistoria.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3930;BA.debugLine="V2_AnimaRotateImageview(imgNeuronaGira, 0, 1440, 1";
_v2_animarotateimageview(_imgneuronagira,(int) (0),(int) (1440),(int) (1500));
 //BA.debugLineNum = 3931;BA.debugLine="End Sub";
return "";
}
public static String  _propaganda_apagaya() throws Exception{
 //BA.debugLineNum = 3705;BA.debugLine="Sub Propaganda_ApagaYa";
 //BA.debugLineNum = 3710;BA.debugLine="adBC.destroyAd";
mostCurrent._vvvvvvvvvvvvvvvvvv1.destroyAd();
 //BA.debugLineNum = 3711;BA.debugLine="adBGris.destroyAd";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv6.destroyAd();
 //BA.debugLineNum = 3712;BA.debugLine="adInt.destroyAd";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv7.destroyAd();
 //BA.debugLineNum = 3716;BA.debugLine="End Sub";
return "";
}
public static String  _propaganda_crear() throws Exception{
 //BA.debugLineNum = 1135;BA.debugLine="Sub Propaganda_Crear";
 //BA.debugLineNum = 1136;BA.debugLine="If gb_EsPremium = False Or Publicos.Get_SoyYo = T";
if (_gb_espremium==anywheresoftware.b4a.keywords.Common.False || mostCurrent._vvvvvvvvvvvvvvv7._get_soyyo(mostCurrent.activityBA)==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 1138;BA.debugLine="adBC.Initialize(\"664913187\") ' banner bottom";
mostCurrent._vvvvvvvvvvvvvvvvvv1.Initialize(mostCurrent.activityBA,"664913187");
 //BA.debugLineNum = 1139;BA.debugLine="adInt.Initialize(\"703320557\")' interstitial";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv7.Initialize(mostCurrent.activityBA,"703320557");
 //BA.debugLineNum = 1140;BA.debugLine="adBGris.Initialize(\"463221194\") ' banner botto";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv6.Initialize(mostCurrent.activityBA,"463221194");
 //BA.debugLineNum = 1141;BA.debugLine="adInt.loadAdToCache() 'el interstital lo cach";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv7.loadAdToCache();
 //BA.debugLineNum = 1150;BA.debugLine="adDispIO.Initialize(\"Jugar\", \"5099\")";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv0.Initialize(mostCurrent.activityBA,"Jugar","5099");
 };
 //BA.debugLineNum = 1153;BA.debugLine="End Sub";
return "";
}
public static String  _propaganda_mostrar() throws Exception{
 //BA.debugLineNum = 1159;BA.debugLine="Sub Propaganda_Mostrar";
 //BA.debugLineNum = 1161;BA.debugLine="Try";
try { //BA.debugLineNum = 1163;BA.debugLine="If gb_EsPremium=False Or Publicos.Get_SoyYo = Tru";
if (_gb_espremium==anywheresoftware.b4a.keywords.Common.False || mostCurrent._vvvvvvvvvvvvvvv7._get_soyyo(mostCurrent.activityBA)==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 1165;BA.debugLine="adBGris.loadAd()";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv6.loadAd();
 //BA.debugLineNum = 1171;BA.debugLine="If bAnulaInsterstitialInicial Then";
if (_vvvvvv7) { 
 //BA.debugLineNum = 1172;BA.debugLine="bAnulaInsterstitialInicial = False";
_vvvvvv7 = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 1173;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 1179;BA.debugLine="If g_iJugadas  Mod gi_PropagandaRate =0 Then ' g";
if (_g_ijugadas%_gi_propagandarate==0) { 
 //BA.debugLineNum = 1184;BA.debugLine="Select (True)";
switch (BA.switchObjectToInt((anywheresoftware.b4a.keywords.Common.True),(_gi_propagandacuenta==0),(_gi_propagandacuenta==1))) {
case 0: {
 //BA.debugLineNum = 1186;BA.debugLine="adDispIO.showAdPlacement(\"1508\")";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv0.showAdPlacement("1508");
 break; }
case 1: {
 //BA.debugLineNum = 1189;BA.debugLine="adInt.loadAd";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv7.loadAd();
 //BA.debugLineNum = 1190;BA.debugLine="adInt.loadAdToCache";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv7.loadAdToCache();
 //BA.debugLineNum = 1191;BA.debugLine="tracker.TrackEvent(\"ADS\", \"Show\", \"Leadbolt\",0";
_vvvvvv1.TrackEvent(processBA,"ADS","Show","Leadbolt",(long) (0));
 break; }
}
;
 //BA.debugLineNum = 1214;BA.debugLine="gi_PropagandaCuenta = gi_PropagandaCuenta +1";
_gi_propagandacuenta = (int) (_gi_propagandacuenta+1);
 //BA.debugLineNum = 1215;BA.debugLine="If gi_PropagandaCuenta >= 2 Then";
if (_gi_propagandacuenta>=2) { 
 //BA.debugLineNum = 1216;BA.debugLine="gi_PropagandaCuenta = 0";
_gi_propagandacuenta = (int) (0);
 };
 };
 };
 } 
       catch (Exception e24) {
			processBA.setLastException(e24); //BA.debugLineNum = 1222;BA.debugLine="Log(\"Error en propaganda\")";
anywheresoftware.b4a.keywords.Common.Log("Error en propaganda");
 };
 //BA.debugLineNum = 1226;BA.debugLine="End Sub";
return "";
}
public static int  _propaganda_setaleatoria() throws Exception{
int _iret = 0;
 //BA.debugLineNum = 3802;BA.debugLine="Sub Propaganda_SetAleatoria As Int";
 //BA.debugLineNum = 3803;BA.debugLine="Dim iRet As Int";
_iret = 0;
 //BA.debugLineNum = 3805;BA.debugLine="iRet = Rnd(0,1)";
_iret = anywheresoftware.b4a.keywords.Common.Rnd((int) (0),(int) (1));
 //BA.debugLineNum = 3807;BA.debugLine="Log(\"Propaganda\" & iRet)";
anywheresoftware.b4a.keywords.Common.Log("Propaganda"+BA.NumberToString(_iret));
 //BA.debugLineNum = 3808;BA.debugLine="Return iRet";
if (true) return _iret;
 //BA.debugLineNum = 3809;BA.debugLine="End Sub";
return 0;
}
public static String  _remoto_ejecutarnonquery(String _squery,String _squerylocal,int _taskid) throws Exception{
anywheresoftware.b4a.http.HttpClientWrapper.HttpUriRequestWrapper _req = null;
 //BA.debugLineNum = 4259;BA.debugLine="Sub Remoto_EjecutarNonQuery(sQuery As String, sQue";
 //BA.debugLineNum = 4260;BA.debugLine="Dim req As HttpRequest";
_req = new anywheresoftware.b4a.http.HttpClientWrapper.HttpUriRequestWrapper();
 //BA.debugLineNum = 4262;BA.debugLine="If IsBackgroundTaskRunning(httpRemoto, taskId) = F";
if (anywheresoftware.b4a.keywords.Common.IsBackgroundTaskRunning(mostCurrent.activityBA,(Object)(_vvvvv4),_taskid)==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 4263;BA.debugLine="aQuerysLocales(taskId) = sQueryLocal";
_vvvvvv6[_taskid] = _squerylocal;
 //BA.debugLineNum = 4265;BA.debugLine="req.InitializePost2(sgDireccionRemota, sQuery.Get";
_req.InitializePost2(_vvvvv6,_squery.getBytes("UTF8"));
 //BA.debugLineNum = 4266;BA.debugLine="httpRemoto.Execute(req, taskId)";
_vvvvv4.Execute(processBA,_req,_taskid);
 };
 //BA.debugLineNum = 4269;BA.debugLine="End Sub";
return "";
}
public static String  _remoto_updatepremium() throws Exception{
String _ssqlremoto = "";
String _ssqllocal = "";
String _sfecha = "";
 //BA.debugLineNum = 4632;BA.debugLine="Sub Remoto_UpdatePremium";
 //BA.debugLineNum = 4634;BA.debugLine="Dim sSqlRemoto As String, sSqlLocal As String, sFe";
_ssqlremoto = "";
_ssqllocal = "";
_sfecha = "";
 //BA.debugLineNum = 4635;BA.debugLine="sFecha = DateTime.Date(DateTime.now)";
_sfecha = anywheresoftware.b4a.keywords.Common.DateTime.Date(anywheresoftware.b4a.keywords.Common.DateTime.getNow());
 //BA.debugLineNum = 4637;BA.debugLine="sSqlRemoto = \"Call Usuario_rem_iu('\" & Publicos.Ge";
_ssqlremoto = "Call Usuario_rem_iu('"+mostCurrent._vvvvvvvvvvvvvvv7._vvv4(mostCurrent.activityBA)+"', 1,'"+_sfecha+"','"+_sfecha+"',0,'')";
 //BA.debugLineNum = 4639;BA.debugLine="sSqlLocal = \"Insert Into Seteosusuario (tiposeteo,";
_ssqllocal = "Insert Into Seteosusuario (tiposeteo, fechadesde) values ('premiumremoto','"+_sfecha+"')";
 //BA.debugLineNum = 4641;BA.debugLine="Remoto_EjecutarNonQuery(sSqlRemoto, sSqlLocal, xQu";
_remoto_ejecutarnonquery(_ssqlremoto,_ssqllocal,_vvvvvvvv5.Premium);
 //BA.debugLineNum = 4643;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvvvvvv0(String _ssql,int _taskid) throws Exception{
anywheresoftware.b4a.http.HttpClientWrapper.HttpUriRequestWrapper _req = null;
 //BA.debugLineNum = 4684;BA.debugLine="Sub RemotoSelect (sSql As String, taskid As Int)";
 //BA.debugLineNum = 4685;BA.debugLine="Dim req As HttpRequest";
_req = new anywheresoftware.b4a.http.HttpClientWrapper.HttpUriRequestWrapper();
 //BA.debugLineNum = 4686;BA.debugLine="req.InitializePost2(sgDireccionRemota, sSql.GetBy";
_req.InitializePost2(_vvvvv6,_ssql.getBytes("UTF8"));
 //BA.debugLineNum = 4687;BA.debugLine="httpRemotoSelect.Execute(req, taskid) '''''''' En";
_vvvvv5.Execute(processBA,_req,_taskid);
 //BA.debugLineNum = 4688;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvvvvvvvvvv4() throws Exception{
 //BA.debugLineNum = 1114;BA.debugLine="Sub SaltarPalabraHacer";
 //BA.debugLineNum = 1116;BA.debugLine="If gb_PalabraRejugada = False Then";
if (_gb_palabrarejugada==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 1117;BA.debugLine="AdivinoGrabaNivel (False)";
_vvvvvvvvvvvvvvvvvv2(anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 1124;BA.debugLine="v2_MuestraNuevaPalabra(0,False,False)";
_v2_muestranuevapalabra((int) (0),anywheresoftware.b4a.keywords.Common.False,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1126;BA.debugLine="If bFinDeJuego = False Then";
if (_vvvvvvvvv3==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 1127;BA.debugLine="V2_AnimaRotateImageview(imgSaltarPalabra, 0, 720";
_v2_animarotateimageview(mostCurrent._imgsaltarpalabra,(int) (0),(int) (720),(int) (500));
 //BA.debugLineNum = 1128;BA.debugLine="V2_AnimaRotateImageview(imgNeuronas, 0, 720, 800";
_v2_animarotateimageview(mostCurrent._imgneuronas,(int) (0),(int) (720),(int) (800));
 };
 //BA.debugLineNum = 1133;BA.debugLine="End Sub";
return "";
}
public static boolean  _vvvvvvvvvvvvvvvvvvvvvvv5(int _imon) throws Exception{
boolean _bret = false;
 //BA.debugLineNum = 1087;BA.debugLine="Sub SaltarPalabraPuede (iMon As Int) As Boolean";
 //BA.debugLineNum = 1088;BA.debugLine="Dim bRet As Boolean=False";
_bret = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 1089;BA.debugLine="If Get_CostoSaltarPalabra <= iMon Or gb_PalabraRe";
if (_get_costosaltarpalabra()<=_imon || _gb_palabrarejugada) { 
 //BA.debugLineNum = 1090;BA.debugLine="bRet = True";
_bret = anywheresoftware.b4a.keywords.Common.True;
 };
 //BA.debugLineNum = 1093;BA.debugLine="Return bRet";
if (true) return _bret;
 //BA.debugLineNum = 1094;BA.debugLine="End Sub";
return false;
}
public static String  _vvvvvvvvvvvvvvvvvvvvvvvv3() throws Exception{
 //BA.debugLineNum = 3129;BA.debugLine="Sub ScrollViewGenera";
 //BA.debugLineNum = 3130;BA.debugLine="imgShadow.Visible = False";
mostCurrent._imgshadow.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3131;BA.debugLine="imgShadow.Left = 0";
mostCurrent._imgshadow.setLeft((int) (0));
 //BA.debugLineNum = 3132;BA.debugLine="imgShadow.Width = Activity.Width";
mostCurrent._imgshadow.setWidth(mostCurrent._activity.getWidth());
 //BA.debugLineNum = 3133;BA.debugLine="imgShadow.Height = 3dip";
mostCurrent._imgshadow.setHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (3)));
 //BA.debugLineNum = 3134;BA.debugLine="imgShadow.Top = Panel11.Top +  Panel11.Height";
mostCurrent._imgshadow.setTop((int) (mostCurrent._panel11.getTop()+mostCurrent._panel11.getHeight()));
 //BA.debugLineNum = 3136;BA.debugLine="If 	lblAyuda.IsInitialized = False Then";
if (mostCurrent._vvvvvvvvvvvvvvvvvvvvvv1.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 3137;BA.debugLine="lblAyuda.Initialize(\"\")";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv1.Initialize(mostCurrent.activityBA,"");
 };
 //BA.debugLineNum = 3139;BA.debugLine="scrViewAyuda.Panel.RemoveAllViews";
mostCurrent._scrviewayuda.getPanel().RemoveAllViews();
 //BA.debugLineNum = 3140;BA.debugLine="scrViewAyuda.Panel.AddView(lblAyuda, 0, 0, scrVie";
mostCurrent._scrviewayuda.getPanel().AddView((android.view.View)(mostCurrent._vvvvvvvvvvvvvvvvvvvvvv1.getObject()),(int) (0),(int) (0),mostCurrent._scrviewayuda.getWidth(),mostCurrent._scrviewayuda.getHeight());
 //BA.debugLineNum = 3142;BA.debugLine="scrViewAyuda.Left = 0";
mostCurrent._scrviewayuda.setLeft((int) (0));
 //BA.debugLineNum = 3143;BA.debugLine="scrViewAyuda.top = Panel11.Top + Panel11.Height +";
mostCurrent._scrviewayuda.setTop((int) (mostCurrent._panel11.getTop()+mostCurrent._panel11.getHeight()+mostCurrent._imgshadow.getHeight()));
 //BA.debugLineNum = 3144;BA.debugLine="scrViewAyuda.Width = Activity.Width";
mostCurrent._scrviewayuda.setWidth(mostCurrent._activity.getWidth());
 //BA.debugLineNum = 3145;BA.debugLine="scrViewAyuda.Height=Activity.Height - Panel11.Hei";
mostCurrent._scrviewayuda.setHeight((int) (mostCurrent._activity.getHeight()-mostCurrent._panel11.getHeight()-mostCurrent._imgshadow.getHeight()-(mostCurrent._activity.getHeight()-mostCurrent._panel61.getTop())));
 //BA.debugLineNum = 3146;BA.debugLine="scrViewAyuda.Panel.width= scrViewAyuda.Width";
mostCurrent._scrviewayuda.getPanel().setWidth(mostCurrent._scrviewayuda.getWidth());
 //BA.debugLineNum = 3148;BA.debugLine="lblAyuda.Left = scrViewAyuda.Width * 0.1";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv1.setLeft((int) (mostCurrent._scrviewayuda.getWidth()*0.1));
 //BA.debugLineNum = 3149;BA.debugLine="lblAyuda.Width = scrViewAyuda.Width * 0.8";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv1.setWidth((int) (mostCurrent._scrviewayuda.getWidth()*0.8));
 //BA.debugLineNum = 3151;BA.debugLine="lblAyuda.TextColor = Colors.White 'Get_aTextColor";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv1.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 3152;BA.debugLine="lblAyuda.Color = gt_Color.ColorDefault";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv1.setColor(mostCurrent._gt_color.ColorDefault);
 //BA.debugLineNum = 3153;BA.debugLine="scrViewAyuda.Panel.Color=gt_Color.colordefault";
mostCurrent._scrviewayuda.getPanel().setColor(mostCurrent._gt_color.ColorDefault);
 //BA.debugLineNum = 3154;BA.debugLine="scrViewAyuda.Color = gt_Color.colordefault";
mostCurrent._scrviewayuda.setColor(mostCurrent._gt_color.ColorDefault);
 //BA.debugLineNum = 3156;BA.debugLine="lblAyuda.Gravity = Gravity.CENTER_HORIZONTAL+Grav";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv1.setGravity((int) (anywheresoftware.b4a.keywords.Common.Gravity.CENTER_HORIZONTAL+anywheresoftware.b4a.keywords.Common.Gravity.TOP));
 //BA.debugLineNum = 3157;BA.debugLine="lblAyuda.Typeface = tFontOpenSansLight";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv1.setTypeface((android.graphics.Typeface)(_vvvvvvvvvv5.getObject()));
 //BA.debugLineNum = 3158;BA.debugLine="scrViewAyuda.Visible = False";
mostCurrent._scrviewayuda.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 3159;BA.debugLine="imgShadow.BringToFront";
mostCurrent._imgshadow.BringToFront();
 //BA.debugLineNum = 3160;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvvvvvvvvvvv4(String _scual) throws Exception{
 //BA.debugLineNum = 3164;BA.debugLine="Sub ScrollviewMUestra(sCual As String)";
 //BA.debugLineNum = 3165;BA.debugLine="lblAyuda.TextColor = gt_Color.ColorTexto";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv1.setTextColor(mostCurrent._gt_color.ColorTexto);
 //BA.debugLineNum = 3166;BA.debugLine="If sCual = \"A\" Then";
if ((_scual).equals("A")) { 
 //BA.debugLineNum = 3167;BA.debugLine="lblAyuda.Text = Publicos.Get_TextoAyuda";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv1.setText((Object)(mostCurrent._vvvvvvvvvvvvvvv7._get_textoayuda(mostCurrent.activityBA)));
 //BA.debugLineNum = 3168;BA.debugLine="scrViewAyuda.Panel.Height = Activity.Height*6";
mostCurrent._scrviewayuda.getPanel().setHeight((int) (mostCurrent._activity.getHeight()*6));
 }else {
 //BA.debugLineNum = 3170;BA.debugLine="lblAyuda.Text = Publicos.Get_TextoCreditos";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv1.setText((Object)(mostCurrent._vvvvvvvvvvvvvvv7._get_textocreditos(mostCurrent.activityBA)));
 //BA.debugLineNum = 3171;BA.debugLine="scrViewAyuda.Panel.Height = Activity.Height*2";
mostCurrent._scrviewayuda.getPanel().setHeight((int) (mostCurrent._activity.getHeight()*2));
 };
 //BA.debugLineNum = 3174;BA.debugLine="lblAyuda.TextSize = Get_TextSizeAux (\"MATETEJUEGO";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv1.setTextSize((float) (_get_textsizeaux("MATETEJUEGO@GMAIL.COM",mostCurrent._vvvvvvvvvvvvvvvvvvvvvv1.getWidth(),(int) (100))));
 //BA.debugLineNum = 3175;BA.debugLine="lblAyuda.Height = scrViewAyuda.Panel.Height";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv1.setHeight(mostCurrent._scrviewayuda.getPanel().getHeight());
 //BA.debugLineNum = 3177;BA.debugLine="Publicos.SetLabelTextSize(lblAyuda, lblAyuda.Text";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv4(mostCurrent.activityBA,mostCurrent._vvvvvvvvvvvvvvvvvvvvvv1,mostCurrent._vvvvvvvvvvvvvvvvvvvvvv1.getText(),(float) (40),(float) (8),(int) (100));
 //BA.debugLineNum = 3179;BA.debugLine="scrViewAyuda.ScrollPosition = 0";
mostCurrent._scrviewayuda.setScrollPosition((int) (0));
 //BA.debugLineNum = 3180;BA.debugLine="scrViewAyuda.Visible = Not (scrViewAyuda.Visible";
mostCurrent._scrviewayuda.setVisible(anywheresoftware.b4a.keywords.Common.Not(mostCurrent._scrviewayuda.getVisible()));
 //BA.debugLineNum = 3181;BA.debugLine="imgShadow.Visible = scrViewAyuda.visible";
mostCurrent._imgshadow.setVisible(mostCurrent._scrviewayuda.getVisible());
 //BA.debugLineNum = 3182;BA.debugLine="scrViewAyuda.bringtofront";
mostCurrent._scrviewayuda.BringToFront();
 //BA.debugLineNum = 3183;BA.debugLine="imgShadow.BringToFront";
mostCurrent._imgshadow.BringToFront();
 //BA.debugLineNum = 3184;BA.debugLine="End Sub";
return "";
}
public static String  _set_colores(int _inivel) throws Exception{
 //BA.debugLineNum = 4842;BA.debugLine="Sub Set_Colores(iNivel As Int)";
 //BA.debugLineNum = 4849;BA.debugLine="gi_CombinacionColores = 0";
_gi_combinacioncolores = (int) (0);
 //BA.debugLineNum = 4850;BA.debugLine="Set_ColoresVariable ( gi_CombinacionColores)";
_set_coloresvariable(_gi_combinacioncolores);
 //BA.debugLineNum = 4851;BA.debugLine="Set_ColoresPantalla";
_set_colorespantalla();
 //BA.debugLineNum = 4853;BA.debugLine="End Sub";
return "";
}
public static String  _set_colorespantalla() throws Exception{
 //BA.debugLineNum = 4856;BA.debugLine="Sub Set_ColoresPantalla";
 //BA.debugLineNum = 4857;BA.debugLine="Panel1.Color = Colors.White";
mostCurrent._panel1.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 4858;BA.debugLine="Panel11.Color = Colors.White";
mostCurrent._panel11.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 4859;BA.debugLine="Panel2.Color = Colors.White";
mostCurrent._panel2.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 4860;BA.debugLine="Panel21.Color = Colors.white";
mostCurrent._panel21.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 4861;BA.debugLine="Panel3.Color = Colors.white";
mostCurrent._panel3.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 4862;BA.debugLine="Panel4.Color = gt_Color.ColorClaro";
mostCurrent._panel4.setColor(mostCurrent._gt_color.ColorClaro);
 //BA.debugLineNum = 4863;BA.debugLine="Panel41.Color = gt_Color.ColorMedio";
mostCurrent._panel41.setColor(mostCurrent._gt_color.ColorMedio);
 //BA.debugLineNum = 4864;BA.debugLine="Panel5.Color = gt_Color.ColorOscuro";
mostCurrent._panel5.setColor(mostCurrent._gt_color.ColorOscuro);
 //BA.debugLineNum = 4865;BA.debugLine="Panel51.Color =gt_Color.ColorOscuro";
mostCurrent._panel51.setColor(mostCurrent._gt_color.ColorOscuro);
 //BA.debugLineNum = 4866;BA.debugLine="Panel6.Color = Colors.White";
mostCurrent._panel6.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 4867;BA.debugLine="Panel61.Color =Colors.White";
mostCurrent._panel61.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 4871;BA.debugLine="lblV2Nivel.TextColor = gt_Color.ColorTexto";
mostCurrent._lblv2nivel.setTextColor(mostCurrent._gt_color.ColorTexto);
 //BA.debugLineNum = 4872;BA.debugLine="lblNeuronas.textcolor = gt_Color.ColorTexto";
mostCurrent._lblneuronas.setTextColor(mostCurrent._gt_color.ColorTexto);
 //BA.debugLineNum = 4873;BA.debugLine="lblAvance.TextColor = gt_Color.ColorTexto";
mostCurrent._lblavance.setTextColor(mostCurrent._gt_color.ColorTexto);
 //BA.debugLineNum = 4876;BA.debugLine="lblv2Def.TextColor = gt_Color.ColorTexto";
mostCurrent._lblv2def.setTextColor(mostCurrent._gt_color.ColorTexto);
 //BA.debugLineNum = 4879;BA.debugLine="lblPedirLetra.TextColor = gt_Color.ColorTexto";
mostCurrent._lblpedirletra.setTextColor(mostCurrent._gt_color.ColorTexto);
 //BA.debugLineNum = 4880;BA.debugLine="lblSaltarPalabra.TextColor = gt_Color.ColorTexto";
mostCurrent._lblsaltarpalabra.setTextColor(mostCurrent._gt_color.ColorTexto);
 //BA.debugLineNum = 4881;BA.debugLine="lblPedirLetraCosto.TextColor = gt_Color.ColorText";
mostCurrent._lblpedirletracosto.setTextColor(mostCurrent._gt_color.ColorTexto);
 //BA.debugLineNum = 4882;BA.debugLine="lblSaltarPalabraCosto.TextColor = gt_Color.ColorT";
mostCurrent._lblsaltarpalabracosto.setTextColor(mostCurrent._gt_color.ColorTexto);
 //BA.debugLineNum = 4886;BA.debugLine="lblCompartir.Textcolor = gt_Color.ColorTexto";
mostCurrent._lblcompartir.setTextColor(mostCurrent._gt_color.ColorTexto);
 //BA.debugLineNum = 4887;BA.debugLine="lblBajarLetras.TextColor = gt_Color.ColorTexto";
mostCurrent._lblbajarletras.setTextColor(mostCurrent._gt_color.ColorTexto);
 //BA.debugLineNum = 4890;BA.debugLine="lbl11.Textcolor = gt_Color.ColorTexto";
mostCurrent._lbl11.setTextColor(mostCurrent._gt_color.ColorTexto);
 //BA.debugLineNum = 4893;BA.debugLine="lbl21.TextColor = gt_Color.ColorTexto";
mostCurrent._lbl21.setTextColor(mostCurrent._gt_color.ColorTexto);
 //BA.debugLineNum = 4896;BA.debugLine="lbl61.TextColor = gt_Color.ColorTexto";
mostCurrent._lbl61.setTextColor(mostCurrent._gt_color.ColorTexto);
 //BA.debugLineNum = 4898;BA.debugLine="Activity.Color = Colors.white";
mostCurrent._activity.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 4901;BA.debugLine="CargaImagenes_Volatiles";
_cargaimagenes_volatiles();
 //BA.debugLineNum = 4903;BA.debugLine="End Sub";
return "";
}
public static String  _set_coloresvariable(int _color) throws Exception{
 //BA.debugLineNum = 4771;BA.debugLine="Sub Set_ColoresVariable(Color As Int)";
 //BA.debugLineNum = 4772;BA.debugLine="gt_Color.bitmBajar = Get_ImagenColor(gt_ColoresPa";
mostCurrent._gt_color.bitmBajar = _get_imagencolor(_gt_colorespaleta[_color].ColorNombre+"-Bajar.png");
 //BA.debugLineNum = 4773;BA.debugLine="gt_Color.bitmBajarLetras = Get_ImagenColor(gt_Col";
mostCurrent._gt_color.bitmBajarLetras = _get_imagencolor(_gt_colorespaleta[_color].ColorNombre+"-Bajar-Letras.png");
 //BA.debugLineNum = 4774;BA.debugLine="gt_Color.bitmCompartir = Get_ImagenColor(gt_Color";
mostCurrent._gt_color.bitmCompartir = _get_imagencolor(_gt_colorespaleta[_color].ColorNombre+"-btn-compartir.png");
 //BA.debugLineNum = 4775;BA.debugLine="gt_Color.bitmCostos = LoadBitmapSample(File.DirAs";
mostCurrent._gt_color.bitmCostos = anywheresoftware.b4a.keywords.Common.LoadBitmapSample(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),_gt_colorespaleta[_color].ColorNombre+"-Costos.png",(int) (100),(int) (100));
 //BA.debugLineNum = 4776;BA.debugLine="gt_Color.bitmFlag = Get_ImagenColor(gt_ColoresPal";
mostCurrent._gt_color.bitmFlag = _get_imagencolor(_gt_colorespaleta[_color].ColorNombre+"-icon-flag.png");
 //BA.debugLineNum = 4777;BA.debugLine="gt_Color.bitmLetra = Get_ImagenColor(gt_ColoresPa";
mostCurrent._gt_color.bitmLetra = _get_imagencolor(_gt_colorespaleta[_color].ColorNombre+"-letra-off-borde.png");
 //BA.debugLineNum = 4778;BA.debugLine="gt_Color.bitmMenu = Get_ImagenColor(gt_ColoresPal";
mostCurrent._gt_color.bitmMenu = _get_imagencolor(_gt_colorespaleta[_color].ColorNombre+"-btn-menu.png");
 //BA.debugLineNum = 4779;BA.debugLine="gt_Color.bitmMenuFondo = Get_ImagenColor(gt_Color";
mostCurrent._gt_color.bitmMenuFondo = _get_imagencolor(_gt_colorespaleta[_color].ColorNombre+"-Menu-Fondo.png");
 //BA.debugLineNum = 4780;BA.debugLine="gt_Color.bitmMute = Get_ImagenColor(gt_ColoresPal";
mostCurrent._gt_color.bitmMute = _get_imagencolor(_gt_colorespaleta[_color].ColorNombre+"-btn-mute.png");
 //BA.debugLineNum = 4781;BA.debugLine="gt_Color.bitmPalabraComprada = Get_ImagenColor(gt";
mostCurrent._gt_color.bitmPalabraComprada = _get_imagencolor(_gt_colorespaleta[_color].ColorNombre+"-Palabra-Comprada-Borde.png");
 //BA.debugLineNum = 4782;BA.debugLine="gt_Color.bitmPalabraError = Get_ImagenColor(gt_Co";
mostCurrent._gt_color.bitmPalabraError = _get_imagencolor(_gt_colorespaleta[_color].ColorNombre+"-Error-Borde.png");
 //BA.debugLineNum = 4783;BA.debugLine="gt_Color.bitmPalabraOff = Get_ImagenColor(gt_Colo";
mostCurrent._gt_color.bitmPalabraOff = _get_imagencolor(_gt_colorespaleta[_color].ColorNombre+"-Palabra-Off-Borde.png");
 //BA.debugLineNum = 4784;BA.debugLine="gt_Color.bitmPalabraOn = Get_ImagenColor(gt_Color";
mostCurrent._gt_color.bitmPalabraOn = _get_imagencolor(_gt_colorespaleta[_color].ColorNombre+"-Palabra-On-Borde.png");
 //BA.debugLineNum = 4785;BA.debugLine="gt_Color.bitmPedir = Get_ImagenColor(gt_ColoresPa";
mostCurrent._gt_color.bitmPedir = _get_imagencolor(_gt_colorespaleta[_color].ColorNombre+"-btn-pedir.png");
 //BA.debugLineNum = 4786;BA.debugLine="gt_Color.bitmSaltarPalabra = Get_ImagenColor(gt_C";
mostCurrent._gt_color.bitmSaltarPalabra = _get_imagencolor(_gt_colorespaleta[_color].ColorNombre+"-btn-resolver.png");
 //BA.debugLineNum = 4787;BA.debugLine="gt_Color.bitmShareCancelar = Get_ImagenColor(gt_C";
mostCurrent._gt_color.bitmShareCancelar = _get_imagencolor(_gt_colorespaleta[_color].ColorNombre+"-Share-Cancelar.png");
 //BA.debugLineNum = 4788;BA.debugLine="gt_Color.bitmShareConfirmar = Get_ImagenColor(gt_";
mostCurrent._gt_color.bitmShareConfirmar = _get_imagencolor(_gt_colorespaleta[_color].ColorNombre+"-Share-Confirmar.png");
 //BA.debugLineNum = 4789;BA.debugLine="gt_Color.bitmSubir = Get_ImagenColor(gt_ColoresPa";
mostCurrent._gt_color.bitmSubir = _get_imagencolor(_gt_colorespaleta[_color].ColorNombre+"-Subir.png");
 //BA.debugLineNum = 4790;BA.debugLine="gt_Color.bitmShareFB= Get_ImagenColor(gt_ColoresP";
mostCurrent._gt_color.bitmShareFB = _get_imagencolor(_gt_colorespaleta[_color].ColorNombre+"-Share-Fb.png");
 //BA.debugLineNum = 4791;BA.debugLine="gt_Color.bitmShareTw = Get_ImagenColor(gt_Colores";
mostCurrent._gt_color.bitmShareTw = _get_imagencolor(_gt_colorespaleta[_color].ColorNombre+"-Share-Tw.png");
 //BA.debugLineNum = 4792;BA.debugLine="gt_Color.ColorClaro = gt_ColoresPaleta(Color).Col";
mostCurrent._gt_color.ColorClaro = _gt_colorespaleta[_color].ColorClaro;
 //BA.debugLineNum = 4793;BA.debugLine="gt_Color.ColorMedio = gt_ColoresPaleta(Color).Col";
mostCurrent._gt_color.ColorMedio = _gt_colorespaleta[_color].ColorIntermedio;
 //BA.debugLineNum = 4794;BA.debugLine="gt_Color.ColorOscuro = gt_ColoresPaleta(Color).Co";
mostCurrent._gt_color.ColorOscuro = _gt_colorespaleta[_color].ColorOscuro;
 //BA.debugLineNum = 4795;BA.debugLine="gt_Color.ColorTexto = gt_ColoresPaleta(Color).Col";
mostCurrent._gt_color.ColorTexto = _gt_colorespaleta[_color].ColorTexto;
 //BA.debugLineNum = 4796;BA.debugLine="gt_Color.ColorNombre = gt_ColoresPaleta(Color).Co";
mostCurrent._gt_color.ColorNombre = _gt_colorespaleta[_color].ColorNombre;
 //BA.debugLineNum = 4797;BA.debugLine="gt_Color.ColorDefault = gt_ColoresPaleta(Color).c";
mostCurrent._gt_color.ColorDefault = _gt_colorespaleta[_color].ColorDefault;
 //BA.debugLineNum = 4798;BA.debugLine="gt_Color.Paleta = Color";
mostCurrent._gt_color.Paleta = _color;
 //BA.debugLineNum = 4800;BA.debugLine="End Sub";
return "";
}
public static String  _slidemenu_click(anywheresoftware.b4a.objects.LabelWrapper _item) throws Exception{
 //BA.debugLineNum = 3091;BA.debugLine="Sub SlideMenu_Click(Item As Label)";
 //BA.debugLineNum = 3096;BA.debugLine="Select Case (True)";
switch (BA.switchObjectToInt((anywheresoftware.b4a.keywords.Common.True),(_item.getText()).equals("VOLVER"),(_item.getText()).equals("CREDITOS"),(_item.getText()).equals("AYUDA"),(_item.getText()).equals("SONIDO"),(_item.getText()).equals("SALIR"),(_item.getText()).equals("SHOP"),(_item.getText()).equals("HISTORIA"))) {
case 0: {
 break; }
case 1: {
 //BA.debugLineNum = 3101;BA.debugLine="V2_PantallaConfigura (xConfiguraPantalla.Credito";
_v2_pantallaconfigura(_vvvvvvvv3.Creditos,(int) (0));
 break; }
case 2: {
 //BA.debugLineNum = 3106;BA.debugLine="V2_PantallaConfigura (xConfiguraPantalla.Ayuda,";
_v2_pantallaconfigura(_vvvvvvvv3.Ayuda,(int) (0));
 break; }
case 3: {
 //BA.debugLineNum = 3109;BA.debugLine="SonidoActualiza";
_vvvvvvvvvvvvvvvvvvvvvvvv5();
 break; }
case 4: {
 //BA.debugLineNum = 3111;BA.debugLine="If Msgbox2(\"Quieres dejar de entrenar tus neuron";
if (anywheresoftware.b4a.keywords.Common.Msgbox2("Quieres dejar de entrenar tus neuronas?","Matete Divergente","Salir","Cancelar","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null),mostCurrent.activityBA)==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
 //BA.debugLineNum = 3112;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 };
 break; }
case 5: {
 //BA.debugLineNum = 3117;BA.debugLine="V2_PantallaConfigura(xConfiguraPantalla.producto";
_v2_pantallaconfigura(_vvvvvvvv3.Producto,(int) (0));
 break; }
case 6: {
 //BA.debugLineNum = 3124;BA.debugLine="V2_PantallaConfigura(xConfiguraPantalla.Historia";
_v2_pantallaconfigura(_vvvvvvvv3.Historia,(int) (0));
 break; }
}
;
 //BA.debugLineNum = 3127;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvvvvv6(int _isonido) throws Exception{
 //BA.debugLineNum = 1740;BA.debugLine="Sub Sonido(iSonido As Int)";
 //BA.debugLineNum = 1741;BA.debugLine="If bSonidoPrendido = False Then";
if (_vvvvvvvvv5==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 1742;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 1744;BA.debugLine="Select iSonido";
switch (BA.switchObjectToInt(_isonido,_sonido_adivino,_sonido_error,_sonido_nuevapalabra)) {
case 0: {
 //BA.debugLineNum = 1746;BA.debugLine="sound_Adivino.Play(iSound_Adivino,1,1,1,1,1)";
mostCurrent._sound_adivino.Play(_isound_adivino,(float) (1),(float) (1),(int) (1),(int) (1),(float) (1));
 break; }
case 1: {
 //BA.debugLineNum = 1748;BA.debugLine="sound_Error.Play(iSound_Error,1,1,1,1,1)";
mostCurrent._sound_error.Play(_isound_error,(float) (1),(float) (1),(int) (1),(int) (1),(float) (1));
 break; }
case 2: {
 //BA.debugLineNum = 1750;BA.debugLine="sound_NuevaPalabra.Play(iSound_NuevaPalabra,1,1,";
mostCurrent._sound_nuevapalabra.Play(_isound_nuevapalabra,(float) (1),(float) (1),(int) (1),(int) (1),(float) (1));
 break; }
}
;
 //BA.debugLineNum = 1753;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvvvvvvvvvvv5() throws Exception{
int _iset = 0;
 //BA.debugLineNum = 3019;BA.debugLine="Sub SonidoActualiza";
 //BA.debugLineNum = 3020;BA.debugLine="Dim iSet As Int";
_iset = 0;
 //BA.debugLineNum = 3022;BA.debugLine="If Publicos.Get_SeteoUsuarioEnteroDesde(g_sqlBase";
if (mostCurrent._vvvvvvvvvvvvvvv7._get_seteousuarioenterodesde(mostCurrent.activityBA,_g_sqlbaselocalusuario,"Sonido")==1) { 
 //BA.debugLineNum = 3023;BA.debugLine="sm.ItemArrayCambiaImagen(3, \"Menu-Sonido-Off.png";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv2._vvvvvvvvvvvvv7((int) (3),"Menu-Sonido-Off.png");
 //BA.debugLineNum = 3024;BA.debugLine="iSet = 0";
_iset = (int) (0);
 //BA.debugLineNum = 3025;BA.debugLine="bSonidoPrendido = False";
_vvvvvvvvv5 = anywheresoftware.b4a.keywords.Common.False;
 }else {
 //BA.debugLineNum = 3027;BA.debugLine="iSet = 1";
_iset = (int) (1);
 //BA.debugLineNum = 3028;BA.debugLine="sm.ItemArrayCambiaImagen(3, \"Menu-Sonido-On.png\"";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv2._vvvvvvvvvvvvv7((int) (3),"Menu-Sonido-On.png");
 //BA.debugLineNum = 3029;BA.debugLine="bSonidoPrendido = True";
_vvvvvvvvv5 = anywheresoftware.b4a.keywords.Common.True;
 };
 //BA.debugLineNum = 3031;BA.debugLine="Publicos.SeteoUsuarioEnteroDesde_Update(g_sqlBase";
mostCurrent._vvvvvvvvvvvvvvv7._seteousuarioenterodesde_update(mostCurrent.activityBA,_g_sqlbaselocalusuario,"Sonido",_iset);
 //BA.debugLineNum = 3035;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvvvv7() throws Exception{
 //BA.debugLineNum = 1755;BA.debugLine="Sub SonidosInicia";
 //BA.debugLineNum = 1756;BA.debugLine="sound_Adivino.Initialize(1)";
mostCurrent._sound_adivino.Initialize((int) (1));
 //BA.debugLineNum = 1757;BA.debugLine="iSound_Adivino = sound_Adivino.Load(File.DirAsset";
_isound_adivino = mostCurrent._sound_adivino.Load(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"adivino.wav");
 //BA.debugLineNum = 1758;BA.debugLine="sound_Error.Initialize(1)";
mostCurrent._sound_error.Initialize((int) (1));
 //BA.debugLineNum = 1759;BA.debugLine="iSound_Error = sound_Error.Load(File.DirAssets, \"";
_isound_error = mostCurrent._sound_error.Load(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"chan.wav");
 //BA.debugLineNum = 1760;BA.debugLine="sound_NuevaPalabra.Initialize(1)";
mostCurrent._sound_nuevapalabra.Initialize((int) (1));
 //BA.debugLineNum = 1761;BA.debugLine="iSound_NuevaPalabra = sound_NuevaPalabra.Load(Fil";
_isound_nuevapalabra = mostCurrent._sound_nuevapalabra.Load(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"nuevapalabra.wav");
 //BA.debugLineNum = 1763;BA.debugLine="If Publicos.Get_SeteoUsuarioEnteroDesde(g_sqlBase";
if (mostCurrent._vvvvvvvvvvvvvvv7._get_seteousuarioenterodesde(mostCurrent.activityBA,_g_sqlbaselocalusuario,"Sonido")==1) { 
 //BA.debugLineNum = 1764;BA.debugLine="bSonidoPrendido = True";
_vvvvvvvvv5 = anywheresoftware.b4a.keywords.Common.True;
 }else {
 //BA.debugLineNum = 1766;BA.debugLine="bSonidoPrendido = False";
_vvvvvvvvv5 = anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 1768;BA.debugLine="End Sub";
return "";
}
public static String  _v2_adivinoanimacion(int _itiempo,int _igrados) throws Exception{
 //BA.debugLineNum = 2539;BA.debugLine="Sub V2_AdivinoAnimacion (iTiempo As Int, iGrados A";
 //BA.debugLineNum = 2540;BA.debugLine="AnimPlus.InitializeRotateCenter(\"AnimPlus\", 0, iG";
mostCurrent._vvvvvvvvvvvvvvvv0.InitializeRotateCenter(mostCurrent.activityBA,"AnimPlus",(float) (0),(float) (_igrados),(android.view.View)(mostCurrent._imganimacion.getObject()));
 //BA.debugLineNum = 2541;BA.debugLine="Activity.invalidate";
mostCurrent._activity.Invalidate();
 //BA.debugLineNum = 2542;BA.debugLine="AnimPlus.Duration = iTiempo";
mostCurrent._vvvvvvvvvvvvvvvv0.setDuration((long) (_itiempo));
 //BA.debugLineNum = 2543;BA.debugLine="If AnimPlus.IsInitialized Then";
if (mostCurrent._vvvvvvvvvvvvvvvv0.IsInitialized()) { 
 //BA.debugLineNum = 2544;BA.debugLine="AnimPlus.Stop(imgAnimacion)";
mostCurrent._vvvvvvvvvvvvvvvv0.Stop((android.view.View)(mostCurrent._imganimacion.getObject()));
 //BA.debugLineNum = 2545;BA.debugLine="Activity.Invalidate";
mostCurrent._activity.Invalidate();
 //BA.debugLineNum = 2546;BA.debugLine="AnimPlus.Start(imgAnimacion)";
mostCurrent._vvvvvvvvvvvvvvvv0.Start((android.view.View)(mostCurrent._imganimacion.getObject()));
 };
 //BA.debugLineNum = 2548;BA.debugLine="Log(\"Animacion\")";
anywheresoftware.b4a.keywords.Common.Log("Animacion");
 //BA.debugLineNum = 2549;BA.debugLine="End Sub";
return "";
}
public static String  _v2_animadesplazaimageview(anywheresoftware.b4a.objects.ImageViewWrapper _imganimar,int _leftinicio,int _topinicio,int _leftfin,int _topfin,int _imilisegundos) throws Exception{
 //BA.debugLineNum = 3789;BA.debugLine="Sub V2_AnimaDesplazaImageView (imgAnimar As ImageV";
 //BA.debugLineNum = 3790;BA.debugLine="AnimPlus.InitializeTranslate(\"AnimPlus\", LeftInic";
mostCurrent._vvvvvvvvvvvvvvvv0.InitializeTranslate(mostCurrent.activityBA,"AnimPlus",(float) (_leftinicio),(float) (_topinicio),(float) (_leftfin),(float) (_topfin));
 //BA.debugLineNum = 3792;BA.debugLine="Activity.invalidate";
mostCurrent._activity.Invalidate();
 //BA.debugLineNum = 3793;BA.debugLine="AnimPlus.Duration = iMilisegundos";
mostCurrent._vvvvvvvvvvvvvvvv0.setDuration((long) (_imilisegundos));
 //BA.debugLineNum = 3794;BA.debugLine="If AnimPlus.IsInitialized Then";
if (mostCurrent._vvvvvvvvvvvvvvvv0.IsInitialized()) { 
 //BA.debugLineNum = 3795;BA.debugLine="AnimPlus.Stop(imgAnimar)";
mostCurrent._vvvvvvvvvvvvvvvv0.Stop((android.view.View)(_imganimar.getObject()));
 //BA.debugLineNum = 3796;BA.debugLine="Activity.Invalidate";
mostCurrent._activity.Invalidate();
 //BA.debugLineNum = 3797;BA.debugLine="AnimPlus.Start(imgAnimar)";
mostCurrent._vvvvvvvvvvvvvvvv0.Start((android.view.View)(_imganimar.getObject()));
 };
 //BA.debugLineNum = 3799;BA.debugLine="End Sub";
return "";
}
public static String  _v2_animaneuronas() throws Exception{
 //BA.debugLineNum = 2551;BA.debugLine="Sub V2_AnimaNeuronas";
 //BA.debugLineNum = 2552;BA.debugLine="AnimPlus.InitializeRotateCenter(\"AnimPlus\", 0, 72";
mostCurrent._vvvvvvvvvvvvvvvv0.InitializeRotateCenter(mostCurrent.activityBA,"AnimPlus",(float) (0),(float) (720),(android.view.View)(mostCurrent._imgneuronas.getObject()));
 //BA.debugLineNum = 2553;BA.debugLine="Activity.invalidate";
mostCurrent._activity.Invalidate();
 //BA.debugLineNum = 2554;BA.debugLine="AnimPlus.Duration = 500";
mostCurrent._vvvvvvvvvvvvvvvv0.setDuration((long) (500));
 //BA.debugLineNum = 2555;BA.debugLine="If AnimPlus.IsInitialized Then";
if (mostCurrent._vvvvvvvvvvvvvvvv0.IsInitialized()) { 
 //BA.debugLineNum = 2556;BA.debugLine="AnimPlus.Stop(imgNeuronas)";
mostCurrent._vvvvvvvvvvvvvvvv0.Stop((android.view.View)(mostCurrent._imgneuronas.getObject()));
 //BA.debugLineNum = 2557;BA.debugLine="Activity.Invalidate";
mostCurrent._activity.Invalidate();
 //BA.debugLineNum = 2558;BA.debugLine="AnimPlus.Start(imgNeuronas)";
mostCurrent._vvvvvvvvvvvvvvvv0.Start((android.view.View)(mostCurrent._imgneuronas.getObject()));
 };
 //BA.debugLineNum = 2561;BA.debugLine="End Sub";
return "";
}
public static String  _v2_animarotateimageview(anywheresoftware.b4a.objects.ImageViewWrapper _imganimar,int _igradoorigen,int _igradofin,int _imilisegundos) throws Exception{
 //BA.debugLineNum = 3196;BA.debugLine="Sub V2_AnimaRotateImageview (imgAnimar As ImageVie";
 //BA.debugLineNum = 3197;BA.debugLine="AnimPlus.InitializeRotateCenter(\"AnimPlus\", iGrad";
mostCurrent._vvvvvvvvvvvvvvvv0.InitializeRotateCenter(mostCurrent.activityBA,"AnimPlus",(float) (_igradoorigen),(float) (_igradofin),(android.view.View)(_imganimar.getObject()));
 //BA.debugLineNum = 3198;BA.debugLine="Activity.invalidate";
mostCurrent._activity.Invalidate();
 //BA.debugLineNum = 3199;BA.debugLine="AnimPlus.Duration = iMilisegundos";
mostCurrent._vvvvvvvvvvvvvvvv0.setDuration((long) (_imilisegundos));
 //BA.debugLineNum = 3200;BA.debugLine="If AnimPlus.IsInitialized Then";
if (mostCurrent._vvvvvvvvvvvvvvvv0.IsInitialized()) { 
 //BA.debugLineNum = 3201;BA.debugLine="AnimPlus.Stop(imgAnimar)";
mostCurrent._vvvvvvvvvvvvvvvv0.Stop((android.view.View)(_imganimar.getObject()));
 //BA.debugLineNum = 3202;BA.debugLine="Activity.Invalidate";
mostCurrent._activity.Invalidate();
 //BA.debugLineNum = 3203;BA.debugLine="AnimPlus.Start(imgAnimar)";
mostCurrent._vvvvvvvvvvvvvvvv0.Start((android.view.View)(_imganimar.getObject()));
 };
 //BA.debugLineNum = 3205;BA.debugLine="End Sub";
return "";
}
public static String  _v2_cargaimagenesestaticasenvariables() throws Exception{
 //BA.debugLineNum = 2419;BA.debugLine="Sub V2_CargaImagenesEstaticasEnVariables";
 //BA.debugLineNum = 2420;BA.debugLine="If bitmRojoCancelar.IsInitialized = False Then";
if (_vvvvvvv3.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 2421;BA.debugLine="bitmRojoCancelar = LoadBitmap(g_DirGrabable, \"ro";
_vvvvvvv3 = anywheresoftware.b4a.keywords.Common.LoadBitmap(_g_dirgrabable,"rojo-cancelar060.png");
 //BA.debugLineNum = 2422;BA.debugLine="bitmVerdeConfirmar = LoadBitmap(g_DirGrabable, \"";
_vvvvvvv4 = anywheresoftware.b4a.keywords.Common.LoadBitmap(_g_dirgrabable,"verde-confirmar060.png");
 //BA.debugLineNum = 2423;BA.debugLine="bitmNeurona = LoadBitmap(g_DirGrabable, \"Neurona";
_vvvvvvv1 = anywheresoftware.b4a.keywords.Common.LoadBitmap(_g_dirgrabable,"Neurona-Icon-Top-2.png");
 //BA.debugLineNum = 2424;BA.debugLine="bitmMenuVolver= LoadBitmap(g_DirGrabable, \"Menu-";
_vvvvvvv2 = anywheresoftware.b4a.keywords.Common.LoadBitmap(_g_dirgrabable,"Menu-Volver.png");
 };
 //BA.debugLineNum = 2447;BA.debugLine="End Sub";
return "";
}
public static String  _v2_cargaimagenesviewsestaticas() throws Exception{
 //BA.debugLineNum = 2450;BA.debugLine="Sub V2_CargaImagenesViewsEstaticas";
 //BA.debugLineNum = 2453;BA.debugLine="imgNeuronas.SetBackgroundImage(bitmNeurona)";
mostCurrent._imgneuronas.SetBackgroundImage((android.graphics.Bitmap)(_vvvvvvv1.getObject()));
 //BA.debugLineNum = 2469;BA.debugLine="End Sub";
return "";
}
public static String  _v2_copiaimagenes() throws Exception{
 //BA.debugLineNum = 2381;BA.debugLine="Sub v2_CopiaImagenes";
 //BA.debugLineNum = 2383;BA.debugLine="Publicos.Archivo_CopiaDesdeAssets(\"Violeta-Palabr";
mostCurrent._vvvvvvvvvvvvvvv7._archivo_copiadesdeassets(mostCurrent.activityBA,"Violeta-Palabra-Off-Borde.png",anywheresoftware.b4a.keywords.Common.False,_g_dirgrabable);
 //BA.debugLineNum = 2384;BA.debugLine="Publicos.Archivo_CopiaDesdeAssets(\"Violeta-Palabr";
mostCurrent._vvvvvvvvvvvvvvv7._archivo_copiadesdeassets(mostCurrent.activityBA,"Violeta-Palabra-On-Borde.png",anywheresoftware.b4a.keywords.Common.False,_g_dirgrabable);
 //BA.debugLineNum = 2397;BA.debugLine="Publicos.Archivo_CopiaDesdeAssets(\"Neurona-Icon-T";
mostCurrent._vvvvvvvvvvvvvvv7._archivo_copiadesdeassets(mostCurrent.activityBA,"Neurona-Icon-Top-2.png",anywheresoftware.b4a.keywords.Common.False,_g_dirgrabable);
 //BA.debugLineNum = 2399;BA.debugLine="Publicos.Archivo_CopiaDesdeAssets(\"Menu-Volver.pn";
mostCurrent._vvvvvvvvvvvvvvv7._archivo_copiadesdeassets(mostCurrent.activityBA,"Menu-Volver.png",anywheresoftware.b4a.keywords.Common.False,_g_dirgrabable);
 //BA.debugLineNum = 2411;BA.debugLine="Publicos.Archivo_CopiaDesdeAssets(\"rojo-cancelar0";
mostCurrent._vvvvvvvvvvvvvvv7._archivo_copiadesdeassets(mostCurrent.activityBA,"rojo-cancelar060.png",anywheresoftware.b4a.keywords.Common.False,_g_dirgrabable);
 //BA.debugLineNum = 2412;BA.debugLine="Publicos.Archivo_CopiaDesdeAssets(\"verde-confirma";
mostCurrent._vvvvvvvvvvvvvvv7._archivo_copiadesdeassets(mostCurrent.activityBA,"verde-confirmar060.png",anywheresoftware.b4a.keywords.Common.False,_g_dirgrabable);
 //BA.debugLineNum = 2413;BA.debugLine="Publicos.Archivo_CopiaDesdeAssets(\"Menu-Historia.";
mostCurrent._vvvvvvvvvvvvvvv7._archivo_copiadesdeassets(mostCurrent.activityBA,"Menu-Historia.png",anywheresoftware.b4a.keywords.Common.False,_g_dirgrabable);
 //BA.debugLineNum = 2417;BA.debugLine="End Sub";
return "";
}
public static String  _v2_dimcolorespaneles() throws Exception{
 //BA.debugLineNum = 2321;BA.debugLine="Sub V2_DimColoresPaneles";
 //BA.debugLineNum = 2323;BA.debugLine="aColoresPaneles(0,0).iColorR = 255:aColoresPa";
_vvvvvvvvvv2[(int) (0)][(int) (0)].iColorR = (int) (255);
 //BA.debugLineNum = 2323;BA.debugLine="aColoresPaneles(0,0).iColorR = 255:aColoresPa";
_vvvvvvvvvv2[(int) (0)][(int) (0)].iColorG = (int) (255);
 //BA.debugLineNum = 2323;BA.debugLine="aColoresPaneles(0,0).iColorR = 255:aColoresPa";
_vvvvvvvvvv2[(int) (0)][(int) (0)].iColorB = (int) (255);
 //BA.debugLineNum = 2324;BA.debugLine="aColoresPaneles(0,1).iColorR = 255:aColoresPa";
_vvvvvvvvvv2[(int) (0)][(int) (1)].iColorR = (int) (255);
 //BA.debugLineNum = 2324;BA.debugLine="aColoresPaneles(0,1).iColorR = 255:aColoresPa";
_vvvvvvvvvv2[(int) (0)][(int) (1)].iColorG = (int) (255);
 //BA.debugLineNum = 2324;BA.debugLine="aColoresPaneles(0,1).iColorR = 255:aColoresPa";
_vvvvvvvvvv2[(int) (0)][(int) (1)].iColorB = (int) (255);
 //BA.debugLineNum = 2325;BA.debugLine="aColoresPaneles(0,2).iColorR = 255:aColoresPa";
_vvvvvvvvvv2[(int) (0)][(int) (2)].iColorR = (int) (255);
 //BA.debugLineNum = 2325;BA.debugLine="aColoresPaneles(0,2).iColorR = 255:aColoresPa";
_vvvvvvvvvv2[(int) (0)][(int) (2)].iColorG = (int) (255);
 //BA.debugLineNum = 2325;BA.debugLine="aColoresPaneles(0,2).iColorR = 255:aColoresPa";
_vvvvvvvvvv2[(int) (0)][(int) (2)].iColorB = (int) (255);
 //BA.debugLineNum = 2326;BA.debugLine="aColoresPaneles(0,3).iColorR = 230:aColoresPa";
_vvvvvvvvvv2[(int) (0)][(int) (3)].iColorR = (int) (230);
 //BA.debugLineNum = 2326;BA.debugLine="aColoresPaneles(0,3).iColorR = 230:aColoresPa";
_vvvvvvvvvv2[(int) (0)][(int) (3)].iColorG = (int) (230);
 //BA.debugLineNum = 2326;BA.debugLine="aColoresPaneles(0,3).iColorR = 230:aColoresPa";
_vvvvvvvvvv2[(int) (0)][(int) (3)].iColorB = (int) (250);
 //BA.debugLineNum = 2327;BA.debugLine="aColoresPaneles(0,4).iColorR = 176:aColoresPa";
_vvvvvvvvvv2[(int) (0)][(int) (4)].iColorR = (int) (176);
 //BA.debugLineNum = 2327;BA.debugLine="aColoresPaneles(0,4).iColorR = 176:aColoresPa";
_vvvvvvvvvv2[(int) (0)][(int) (4)].iColorG = (int) (196);
 //BA.debugLineNum = 2327;BA.debugLine="aColoresPaneles(0,4).iColorR = 176:aColoresPa";
_vvvvvvvvvv2[(int) (0)][(int) (4)].iColorB = (int) (222);
 //BA.debugLineNum = 2328;BA.debugLine="aColoresPaneles(0,5).iColorR = 255:aColoresPa";
_vvvvvvvvvv2[(int) (0)][(int) (5)].iColorR = (int) (255);
 //BA.debugLineNum = 2328;BA.debugLine="aColoresPaneles(0,5).iColorR = 255:aColoresPa";
_vvvvvvvvvv2[(int) (0)][(int) (5)].iColorG = (int) (255);
 //BA.debugLineNum = 2328;BA.debugLine="aColoresPaneles(0,5).iColorR = 255:aColoresPa";
_vvvvvvvvvv2[(int) (0)][(int) (5)].iColorB = (int) (255);
 //BA.debugLineNum = 2330;BA.debugLine="aColoresPaneles(0,6).iColorR = 230:aColoresPanel";
_vvvvvvvvvv2[(int) (0)][(int) (6)].iColorR = (int) (230);
 //BA.debugLineNum = 2330;BA.debugLine="aColoresPaneles(0,6).iColorR = 230:aColoresPanel";
_vvvvvvvvvv2[(int) (0)][(int) (6)].iColorG = (int) (230);
 //BA.debugLineNum = 2330;BA.debugLine="aColoresPaneles(0,6).iColorR = 230:aColoresPanel";
_vvvvvvvvvv2[(int) (0)][(int) (6)].iColorB = (int) (255);
 //BA.debugLineNum = 2333;BA.debugLine="aTextColor(0,0).iColorR = 176:aTextColor(0,0)";
_vvvvvvvvvv3[(int) (0)][(int) (0)].iColorR = (int) (176);
 //BA.debugLineNum = 2333;BA.debugLine="aTextColor(0,0).iColorR = 176:aTextColor(0,0)";
_vvvvvvvvvv3[(int) (0)][(int) (0)].iColorG = (int) (196);
 //BA.debugLineNum = 2333;BA.debugLine="aTextColor(0,0).iColorR = 176:aTextColor(0,0)";
_vvvvvvvvvv3[(int) (0)][(int) (0)].iColorB = (int) (222);
 //BA.debugLineNum = 2334;BA.debugLine="aTextColor(0,1).iColorR = 176:aTextColor(0,1)";
_vvvvvvvvvv3[(int) (0)][(int) (1)].iColorR = (int) (176);
 //BA.debugLineNum = 2334;BA.debugLine="aTextColor(0,1).iColorR = 176:aTextColor(0,1)";
_vvvvvvvvvv3[(int) (0)][(int) (1)].iColorG = (int) (196);
 //BA.debugLineNum = 2334;BA.debugLine="aTextColor(0,1).iColorR = 176:aTextColor(0,1)";
_vvvvvvvvvv3[(int) (0)][(int) (1)].iColorB = (int) (222);
 //BA.debugLineNum = 2335;BA.debugLine="aTextColor(0,2).iColorR = 176:aTextColor(0,2)";
_vvvvvvvvvv3[(int) (0)][(int) (2)].iColorR = (int) (176);
 //BA.debugLineNum = 2335;BA.debugLine="aTextColor(0,2).iColorR = 176:aTextColor(0,2)";
_vvvvvvvvvv3[(int) (0)][(int) (2)].iColorG = (int) (196);
 //BA.debugLineNum = 2335;BA.debugLine="aTextColor(0,2).iColorR = 176:aTextColor(0,2)";
_vvvvvvvvvv3[(int) (0)][(int) (2)].iColorB = (int) (222);
 //BA.debugLineNum = 2336;BA.debugLine="aTextColor(0,3).iColorR = 255:aTextColor(0,3).iC";
_vvvvvvvvvv3[(int) (0)][(int) (3)].iColorR = (int) (255);
 //BA.debugLineNum = 2336;BA.debugLine="aTextColor(0,3).iColorR = 255:aTextColor(0,3).iC";
_vvvvvvvvvv3[(int) (0)][(int) (3)].iColorG = (int) (255);
 //BA.debugLineNum = 2336;BA.debugLine="aTextColor(0,3).iColorR = 255:aTextColor(0,3).iC";
_vvvvvvvvvv3[(int) (0)][(int) (3)].iColorB = (int) (255);
 //BA.debugLineNum = 2337;BA.debugLine="aTextColor(0,4).iColorR = 255:aTextColor(0,4)";
_vvvvvvvvvv3[(int) (0)][(int) (4)].iColorR = (int) (255);
 //BA.debugLineNum = 2337;BA.debugLine="aTextColor(0,4).iColorR = 255:aTextColor(0,4)";
_vvvvvvvvvv3[(int) (0)][(int) (4)].iColorG = (int) (255);
 //BA.debugLineNum = 2337;BA.debugLine="aTextColor(0,4).iColorR = 255:aTextColor(0,4)";
_vvvvvvvvvv3[(int) (0)][(int) (4)].iColorB = (int) (255);
 //BA.debugLineNum = 2338;BA.debugLine="aTextColor(0,5).iColorR = 176:aTextColor(0,5)";
_vvvvvvvvvv3[(int) (0)][(int) (5)].iColorR = (int) (176);
 //BA.debugLineNum = 2338;BA.debugLine="aTextColor(0,5).iColorR = 176:aTextColor(0,5)";
_vvvvvvvvvv3[(int) (0)][(int) (5)].iColorG = (int) (196);
 //BA.debugLineNum = 2338;BA.debugLine="aTextColor(0,5).iColorR = 176:aTextColor(0,5)";
_vvvvvvvvvv3[(int) (0)][(int) (5)].iColorB = (int) (222);
 //BA.debugLineNum = 2339;BA.debugLine="aTextColor(0,6).iColorR = 176:aTextColor(0,6).i";
_vvvvvvvvvv3[(int) (0)][(int) (6)].iColorR = (int) (176);
 //BA.debugLineNum = 2339;BA.debugLine="aTextColor(0,6).iColorR = 176:aTextColor(0,6).i";
_vvvvvvvvvv3[(int) (0)][(int) (6)].iColorG = (int) (196);
 //BA.debugLineNum = 2339;BA.debugLine="aTextColor(0,6).iColorR = 176:aTextColor(0,6).i";
_vvvvvvvvvv3[(int) (0)][(int) (6)].iColorB = (int) (222);
 //BA.debugLineNum = 2341;BA.debugLine="aColoresPaneles(1,0).iColorR = 255:aColoresPa";
_vvvvvvvvvv2[(int) (1)][(int) (0)].iColorR = (int) (255);
 //BA.debugLineNum = 2341;BA.debugLine="aColoresPaneles(1,0).iColorR = 255:aColoresPa";
_vvvvvvvvvv2[(int) (1)][(int) (0)].iColorG = (int) (255);
 //BA.debugLineNum = 2341;BA.debugLine="aColoresPaneles(1,0).iColorR = 255:aColoresPa";
_vvvvvvvvvv2[(int) (1)][(int) (0)].iColorB = (int) (255);
 //BA.debugLineNum = 2342;BA.debugLine="aColoresPaneles(1,1).iColorR = 255:aColoresPa";
_vvvvvvvvvv2[(int) (1)][(int) (1)].iColorR = (int) (255);
 //BA.debugLineNum = 2342;BA.debugLine="aColoresPaneles(1,1).iColorR = 255:aColoresPa";
_vvvvvvvvvv2[(int) (1)][(int) (1)].iColorG = (int) (255);
 //BA.debugLineNum = 2342;BA.debugLine="aColoresPaneles(1,1).iColorR = 255:aColoresPa";
_vvvvvvvvvv2[(int) (1)][(int) (1)].iColorB = (int) (255);
 //BA.debugLineNum = 2343;BA.debugLine="aColoresPaneles(1,2).iColorR = 255:aColoresPa";
_vvvvvvvvvv2[(int) (1)][(int) (2)].iColorR = (int) (255);
 //BA.debugLineNum = 2343;BA.debugLine="aColoresPaneles(1,2).iColorR = 255:aColoresPa";
_vvvvvvvvvv2[(int) (1)][(int) (2)].iColorG = (int) (255);
 //BA.debugLineNum = 2343;BA.debugLine="aColoresPaneles(1,2).iColorR = 255:aColoresPa";
_vvvvvvvvvv2[(int) (1)][(int) (2)].iColorB = (int) (255);
 //BA.debugLineNum = 2344;BA.debugLine="aColoresPaneles(1,3).iColorR = 144:aColoresPa";
_vvvvvvvvvv2[(int) (1)][(int) (3)].iColorR = (int) (144);
 //BA.debugLineNum = 2344;BA.debugLine="aColoresPaneles(1,3).iColorR = 144:aColoresPa";
_vvvvvvvvvv2[(int) (1)][(int) (3)].iColorG = (int) (238);
 //BA.debugLineNum = 2344;BA.debugLine="aColoresPaneles(1,3).iColorR = 144:aColoresPa";
_vvvvvvvvvv2[(int) (1)][(int) (3)].iColorB = (int) (144);
 //BA.debugLineNum = 2345;BA.debugLine="aColoresPaneles(1,4).iColorR = 152:aColoresPa";
_vvvvvvvvvv2[(int) (1)][(int) (4)].iColorR = (int) (152);
 //BA.debugLineNum = 2345;BA.debugLine="aColoresPaneles(1,4).iColorR = 152:aColoresPa";
_vvvvvvvvvv2[(int) (1)][(int) (4)].iColorG = (int) (251);
 //BA.debugLineNum = 2345;BA.debugLine="aColoresPaneles(1,4).iColorR = 152:aColoresPa";
_vvvvvvvvvv2[(int) (1)][(int) (4)].iColorB = (int) (152);
 //BA.debugLineNum = 2346;BA.debugLine="aColoresPaneles(1,5).iColorR = 255:aColoresPa";
_vvvvvvvvvv2[(int) (1)][(int) (5)].iColorR = (int) (255);
 //BA.debugLineNum = 2346;BA.debugLine="aColoresPaneles(1,5).iColorR = 255:aColoresPa";
_vvvvvvvvvv2[(int) (1)][(int) (5)].iColorG = (int) (255);
 //BA.debugLineNum = 2346;BA.debugLine="aColoresPaneles(1,5).iColorR = 255:aColoresPa";
_vvvvvvvvvv2[(int) (1)][(int) (5)].iColorB = (int) (255);
 //BA.debugLineNum = 2347;BA.debugLine="aColoresPaneles(1,6).iColorR = 144:aColoresPa";
_vvvvvvvvvv2[(int) (1)][(int) (6)].iColorR = (int) (144);
 //BA.debugLineNum = 2347;BA.debugLine="aColoresPaneles(1,6).iColorR = 144:aColoresPa";
_vvvvvvvvvv2[(int) (1)][(int) (6)].iColorG = (int) (238);
 //BA.debugLineNum = 2347;BA.debugLine="aColoresPaneles(1,6).iColorR = 144:aColoresPa";
_vvvvvvvvvv2[(int) (1)][(int) (6)].iColorB = (int) (130);
 //BA.debugLineNum = 2350;BA.debugLine="aTextColor(1,0).iColorR = 152:aTextColor(1,0)";
_vvvvvvvvvv3[(int) (1)][(int) (0)].iColorR = (int) (152);
 //BA.debugLineNum = 2350;BA.debugLine="aTextColor(1,0).iColorR = 152:aTextColor(1,0)";
_vvvvvvvvvv3[(int) (1)][(int) (0)].iColorG = (int) (251);
 //BA.debugLineNum = 2350;BA.debugLine="aTextColor(1,0).iColorR = 152:aTextColor(1,0)";
_vvvvvvvvvv3[(int) (1)][(int) (0)].iColorB = (int) (152);
 //BA.debugLineNum = 2351;BA.debugLine="aTextColor(1,1).iColorR = 152:aTextColor(1,1)";
_vvvvvvvvvv3[(int) (1)][(int) (1)].iColorR = (int) (152);
 //BA.debugLineNum = 2351;BA.debugLine="aTextColor(1,1).iColorR = 152:aTextColor(1,1)";
_vvvvvvvvvv3[(int) (1)][(int) (1)].iColorG = (int) (251);
 //BA.debugLineNum = 2351;BA.debugLine="aTextColor(1,1).iColorR = 152:aTextColor(1,1)";
_vvvvvvvvvv3[(int) (1)][(int) (1)].iColorB = (int) (152);
 //BA.debugLineNum = 2352;BA.debugLine="aTextColor(1,2).iColorR = 152:aTextColor(1,2)";
_vvvvvvvvvv3[(int) (1)][(int) (2)].iColorR = (int) (152);
 //BA.debugLineNum = 2352;BA.debugLine="aTextColor(1,2).iColorR = 152:aTextColor(1,2)";
_vvvvvvvvvv3[(int) (1)][(int) (2)].iColorG = (int) (251);
 //BA.debugLineNum = 2352;BA.debugLine="aTextColor(1,2).iColorR = 152:aTextColor(1,2)";
_vvvvvvvvvv3[(int) (1)][(int) (2)].iColorB = (int) (152);
 //BA.debugLineNum = 2353;BA.debugLine="aTextColor(1,3).iColorR = 255:aTextColor(1,3).iC";
_vvvvvvvvvv3[(int) (1)][(int) (3)].iColorR = (int) (255);
 //BA.debugLineNum = 2353;BA.debugLine="aTextColor(1,3).iColorR = 255:aTextColor(1,3).iC";
_vvvvvvvvvv3[(int) (1)][(int) (3)].iColorG = (int) (255);
 //BA.debugLineNum = 2353;BA.debugLine="aTextColor(1,3).iColorR = 255:aTextColor(1,3).iC";
_vvvvvvvvvv3[(int) (1)][(int) (3)].iColorB = (int) (255);
 //BA.debugLineNum = 2354;BA.debugLine="aTextColor(1,4).iColorR = 255:aTextColor(1,4)";
_vvvvvvvvvv3[(int) (1)][(int) (4)].iColorR = (int) (255);
 //BA.debugLineNum = 2354;BA.debugLine="aTextColor(1,4).iColorR = 255:aTextColor(1,4)";
_vvvvvvvvvv3[(int) (1)][(int) (4)].iColorG = (int) (255);
 //BA.debugLineNum = 2354;BA.debugLine="aTextColor(1,4).iColorR = 255:aTextColor(1,4)";
_vvvvvvvvvv3[(int) (1)][(int) (4)].iColorB = (int) (255);
 //BA.debugLineNum = 2355;BA.debugLine="aTextColor(1,5).iColorR = 152:aTextColor(1,5)";
_vvvvvvvvvv3[(int) (1)][(int) (5)].iColorR = (int) (152);
 //BA.debugLineNum = 2355;BA.debugLine="aTextColor(1,5).iColorR = 152:aTextColor(1,5)";
_vvvvvvvvvv3[(int) (1)][(int) (5)].iColorG = (int) (251);
 //BA.debugLineNum = 2355;BA.debugLine="aTextColor(1,5).iColorR = 152:aTextColor(1,5)";
_vvvvvvvvvv3[(int) (1)][(int) (5)].iColorB = (int) (152);
 //BA.debugLineNum = 2356;BA.debugLine="aTextColor(1,6).iColorR = 152:aTextColor(1,6).iC";
_vvvvvvvvvv3[(int) (1)][(int) (6)].iColorR = (int) (152);
 //BA.debugLineNum = 2356;BA.debugLine="aTextColor(1,6).iColorR = 152:aTextColor(1,6).iC";
_vvvvvvvvvv3[(int) (1)][(int) (6)].iColorG = (int) (251);
 //BA.debugLineNum = 2356;BA.debugLine="aTextColor(1,6).iColorR = 152:aTextColor(1,6).iC";
_vvvvvvvvvv3[(int) (1)][(int) (6)].iColorB = (int) (190);
 //BA.debugLineNum = 2360;BA.debugLine="aColoresPaneles(2,0).iColorR = 255:aColoresPa";
_vvvvvvvvvv2[(int) (2)][(int) (0)].iColorR = (int) (255);
 //BA.debugLineNum = 2360;BA.debugLine="aColoresPaneles(2,0).iColorR = 255:aColoresPa";
_vvvvvvvvvv2[(int) (2)][(int) (0)].iColorG = (int) (255);
 //BA.debugLineNum = 2360;BA.debugLine="aColoresPaneles(2,0).iColorR = 255:aColoresPa";
_vvvvvvvvvv2[(int) (2)][(int) (0)].iColorB = (int) (255);
 //BA.debugLineNum = 2361;BA.debugLine="aColoresPaneles(2,1).iColorR = 255:aColoresPa";
_vvvvvvvvvv2[(int) (2)][(int) (1)].iColorR = (int) (255);
 //BA.debugLineNum = 2361;BA.debugLine="aColoresPaneles(2,1).iColorR = 255:aColoresPa";
_vvvvvvvvvv2[(int) (2)][(int) (1)].iColorG = (int) (255);
 //BA.debugLineNum = 2361;BA.debugLine="aColoresPaneles(2,1).iColorR = 255:aColoresPa";
_vvvvvvvvvv2[(int) (2)][(int) (1)].iColorB = (int) (255);
 //BA.debugLineNum = 2362;BA.debugLine="aColoresPaneles(2,2).iColorR = 255:aColoresPa";
_vvvvvvvvvv2[(int) (2)][(int) (2)].iColorR = (int) (255);
 //BA.debugLineNum = 2362;BA.debugLine="aColoresPaneles(2,2).iColorR = 255:aColoresPa";
_vvvvvvvvvv2[(int) (2)][(int) (2)].iColorG = (int) (255);
 //BA.debugLineNum = 2362;BA.debugLine="aColoresPaneles(2,2).iColorR = 255:aColoresPa";
_vvvvvvvvvv2[(int) (2)][(int) (2)].iColorB = (int) (255);
 //BA.debugLineNum = 2363;BA.debugLine="aColoresPaneles(2,3).iColorR = 167:aColoresPa";
_vvvvvvvvvv2[(int) (2)][(int) (3)].iColorR = (int) (167);
 //BA.debugLineNum = 2363;BA.debugLine="aColoresPaneles(2,3).iColorR = 167:aColoresPa";
_vvvvvvvvvv2[(int) (2)][(int) (3)].iColorG = (int) (99);
 //BA.debugLineNum = 2363;BA.debugLine="aColoresPaneles(2,3).iColorR = 167:aColoresPa";
_vvvvvvvvvv2[(int) (2)][(int) (3)].iColorB = (int) (167);
 //BA.debugLineNum = 2364;BA.debugLine="aColoresPaneles(2,4).iColorR = 151:aColoresPa";
_vvvvvvvvvv2[(int) (2)][(int) (4)].iColorR = (int) (151);
 //BA.debugLineNum = 2364;BA.debugLine="aColoresPaneles(2,4).iColorR = 151:aColoresPa";
_vvvvvvvvvv2[(int) (2)][(int) (4)].iColorG = (int) (89);
 //BA.debugLineNum = 2364;BA.debugLine="aColoresPaneles(2,4).iColorR = 151:aColoresPa";
_vvvvvvvvvv2[(int) (2)][(int) (4)].iColorB = (int) (152);
 //BA.debugLineNum = 2365;BA.debugLine="aColoresPaneles(2,5).iColorR = 255:aColoresPa";
_vvvvvvvvvv2[(int) (2)][(int) (5)].iColorR = (int) (255);
 //BA.debugLineNum = 2365;BA.debugLine="aColoresPaneles(2,5).iColorR = 255:aColoresPa";
_vvvvvvvvvv2[(int) (2)][(int) (5)].iColorG = (int) (255);
 //BA.debugLineNum = 2365;BA.debugLine="aColoresPaneles(2,5).iColorR = 255:aColoresPa";
_vvvvvvvvvv2[(int) (2)][(int) (5)].iColorB = (int) (255);
 //BA.debugLineNum = 2367;BA.debugLine="aColoresPaneles(2,6).iColorR = 140:aColoresPanel";
_vvvvvvvvvv2[(int) (2)][(int) (6)].iColorR = (int) (140);
 //BA.debugLineNum = 2367;BA.debugLine="aColoresPaneles(2,6).iColorR = 140:aColoresPanel";
_vvvvvvvvvv2[(int) (2)][(int) (6)].iColorG = (int) (89);
 //BA.debugLineNum = 2367;BA.debugLine="aColoresPaneles(2,6).iColorR = 140:aColoresPanel";
_vvvvvvvvvv2[(int) (2)][(int) (6)].iColorB = (int) (200);
 //BA.debugLineNum = 2370;BA.debugLine="aTextColor(2,0).iColorR = 151:aTextColor(2,0)";
_vvvvvvvvvv3[(int) (2)][(int) (0)].iColorR = (int) (151);
 //BA.debugLineNum = 2370;BA.debugLine="aTextColor(2,0).iColorR = 151:aTextColor(2,0)";
_vvvvvvvvvv3[(int) (2)][(int) (0)].iColorG = (int) (89);
 //BA.debugLineNum = 2370;BA.debugLine="aTextColor(2,0).iColorR = 151:aTextColor(2,0)";
_vvvvvvvvvv3[(int) (2)][(int) (0)].iColorB = (int) (152);
 //BA.debugLineNum = 2371;BA.debugLine="aTextColor(2,1).iColorR = 151:aTextColor(2,1)";
_vvvvvvvvvv3[(int) (2)][(int) (1)].iColorR = (int) (151);
 //BA.debugLineNum = 2371;BA.debugLine="aTextColor(2,1).iColorR = 151:aTextColor(2,1)";
_vvvvvvvvvv3[(int) (2)][(int) (1)].iColorG = (int) (89);
 //BA.debugLineNum = 2371;BA.debugLine="aTextColor(2,1).iColorR = 151:aTextColor(2,1)";
_vvvvvvvvvv3[(int) (2)][(int) (1)].iColorB = (int) (152);
 //BA.debugLineNum = 2372;BA.debugLine="aTextColor(2,2).iColorR = 151:aTextColor(2,2)";
_vvvvvvvvvv3[(int) (2)][(int) (2)].iColorR = (int) (151);
 //BA.debugLineNum = 2372;BA.debugLine="aTextColor(2,2).iColorR = 151:aTextColor(2,2)";
_vvvvvvvvvv3[(int) (2)][(int) (2)].iColorG = (int) (89);
 //BA.debugLineNum = 2372;BA.debugLine="aTextColor(2,2).iColorR = 151:aTextColor(2,2)";
_vvvvvvvvvv3[(int) (2)][(int) (2)].iColorB = (int) (152);
 //BA.debugLineNum = 2373;BA.debugLine="aTextColor(2,3).iColorR = 255:aTextColor(2,3).iC";
_vvvvvvvvvv3[(int) (2)][(int) (3)].iColorR = (int) (255);
 //BA.debugLineNum = 2373;BA.debugLine="aTextColor(2,3).iColorR = 255:aTextColor(2,3).iC";
_vvvvvvvvvv3[(int) (2)][(int) (3)].iColorG = (int) (255);
 //BA.debugLineNum = 2373;BA.debugLine="aTextColor(2,3).iColorR = 255:aTextColor(2,3).iC";
_vvvvvvvvvv3[(int) (2)][(int) (3)].iColorB = (int) (255);
 //BA.debugLineNum = 2374;BA.debugLine="aTextColor(2,4).iColorR = 255:aTextColor(2,4)";
_vvvvvvvvvv3[(int) (2)][(int) (4)].iColorR = (int) (255);
 //BA.debugLineNum = 2374;BA.debugLine="aTextColor(2,4).iColorR = 255:aTextColor(2,4)";
_vvvvvvvvvv3[(int) (2)][(int) (4)].iColorG = (int) (255);
 //BA.debugLineNum = 2374;BA.debugLine="aTextColor(2,4).iColorR = 255:aTextColor(2,4)";
_vvvvvvvvvv3[(int) (2)][(int) (4)].iColorB = (int) (255);
 //BA.debugLineNum = 2375;BA.debugLine="aTextColor(2,5).iColorR = 151:aTextColor(2,5)";
_vvvvvvvvvv3[(int) (2)][(int) (5)].iColorR = (int) (151);
 //BA.debugLineNum = 2375;BA.debugLine="aTextColor(2,5).iColorR = 151:aTextColor(2,5)";
_vvvvvvvvvv3[(int) (2)][(int) (5)].iColorG = (int) (89);
 //BA.debugLineNum = 2375;BA.debugLine="aTextColor(2,5).iColorR = 151:aTextColor(2,5)";
_vvvvvvvvvv3[(int) (2)][(int) (5)].iColorB = (int) (152);
 //BA.debugLineNum = 2377;BA.debugLine="aTextColor(2,6).iColorR = 151:aColoresPaneles(2,";
_vvvvvvvvvv3[(int) (2)][(int) (6)].iColorR = (int) (151);
 //BA.debugLineNum = 2377;BA.debugLine="aTextColor(2,6).iColorR = 151:aColoresPaneles(2,";
_vvvvvvvvvv2[(int) (2)][(int) (6)].iColorG = (int) (89);
 //BA.debugLineNum = 2377;BA.debugLine="aTextColor(2,6).iColorR = 151:aColoresPaneles(2,";
_vvvvvvvvvv2[(int) (2)][(int) (6)].iColorB = (int) (138);
 //BA.debugLineNum = 2379;BA.debugLine="End Sub";
return "";
}
public static String  _v2_generalabelsletras(com.matetejuego.free.publicos._tletraspalabra[] _apletraspalabra) throws Exception{
int _iancho = 0;
int _ialto = 0;
int _iseparacion = 0;
String _sletraspalabra = "";
anywheresoftware.b4a.objects.collections.List _lblslist = null;
int _ilugar = 0;
anywheresoftware.b4a.objects.LabelWrapper[] _lbls = null;
int _idesplaza = 0;
int _itop1 = 0;
int _itop2 = 0;
int _itextsize = 0;
int _i = 0;
anywheresoftware.b4a.objects.LabelWrapper _ll = null;
anywheresoftware.b4a.objects.LabelWrapper _lbl = null;
 //BA.debugLineNum = 2140;BA.debugLine="Sub V2_GeneraLabelsLetras ( apLetrasPalabra() As t";
 //BA.debugLineNum = 2144;BA.debugLine="Dim iAncho As Int, iAlto As Int, iSeparacion As I";
_iancho = 0;
_ialto = 0;
_iseparacion = 0;
_sletraspalabra = "";
 //BA.debugLineNum = 2145;BA.debugLine="Dim lblsList As List, iLugar As Int, lbls(iCantLe";
_lblslist = new anywheresoftware.b4a.objects.collections.List();
_ilugar = 0;
_lbls = new anywheresoftware.b4a.objects.LabelWrapper[_vvvvvvv7];
{
int d0 = _lbls.length;
for (int i0 = 0;i0 < d0;i0++) {
_lbls[i0] = new anywheresoftware.b4a.objects.LabelWrapper();
}
}
;
_idesplaza = 0;
 //BA.debugLineNum = 2146;BA.debugLine="Dim iTop1 As Int ,  iTop2 As Int, iTextSize As In";
_itop1 = 0;
_itop2 = 0;
_itextsize = 0;
 //BA.debugLineNum = 2148;BA.debugLine="Panel5.RemoveAllViews";
mostCurrent._panel5.RemoveAllViews();
 //BA.debugLineNum = 2149;BA.debugLine="lblsList.Initialize";
_lblslist.Initialize();
 //BA.debugLineNum = 2152;BA.debugLine="sgLetras = LetrasCarga(apLetrasPalabra)";
_vvvvvvv0 = _vvvvvvvvvvvvvvvvvvvvvvv7(_apletraspalabra);
 //BA.debugLineNum = 2160;BA.debugLine="iAncho = Get_AnchoLetra";
_iancho = _get_ancholetra();
 //BA.debugLineNum = 2161;BA.debugLine="iAlto=iAncho";
_ialto = _iancho;
 //BA.debugLineNum = 2162;BA.debugLine="iSeparacion = iAncho*0.1";
_iseparacion = (int) (_iancho*0.1);
 //BA.debugLineNum = 2163;BA.debugLine="iTop1 = Panel5.Height * 0.15";
_itop1 = (int) (mostCurrent._panel5.getHeight()*0.15);
 //BA.debugLineNum = 2164;BA.debugLine="iTop2 = Panel5.Height * 0.55";
_itop2 = (int) (mostCurrent._panel5.getHeight()*0.55);
 //BA.debugLineNum = 2165;BA.debugLine="iDesplaza =  (Panel5.Width -iAncho*6 - iSeparacio";
_idesplaza = (int) ((mostCurrent._panel5.getWidth()-_iancho*6-_iseparacion*5)/(double)2);
 //BA.debugLineNum = 2167;BA.debugLine="For i = 0 To sgLetras.Length -1 ' por cada letra";
{
final int step13 = 1;
final int limit13 = (int) (_vvvvvvv0.length()-1);
for (_i = (int) (0) ; (step13 > 0 && _i <= limit13) || (step13 < 0 && _i >= limit13); _i = ((int)(0 + _i + step13)) ) {
 //BA.debugLineNum = 2168;BA.debugLine="Dim LL As Label  ' crea un label local";
_ll = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 2169;BA.debugLine="LL.Initialize(\"lblLetras\")  ' lo inicializa";
_ll.Initialize(mostCurrent.activityBA,"lblLetras");
 //BA.debugLineNum = 2172;BA.debugLine="If sgLetras.SubString2(i,i+1) = \"?\" Then";
if ((_vvvvvvv0.substring(_i,(int) (_i+1))).equals("?")) { 
 //BA.debugLineNum = 2173;BA.debugLine="LL.Textcolor = Get_ColorLetraDisponible 'Get_Col";
_ll.setTextColor(_get_colorletradisponible());
 //BA.debugLineNum = 2174;BA.debugLine="LL.Tag = \"?\"";
_ll.setTag((Object)("?"));
 }else {
 //BA.debugLineNum = 2176;BA.debugLine="LL.TextColor = Get_ColorLetraDisponible";
_ll.setTextColor(_get_colorletradisponible());
 //BA.debugLineNum = 2177;BA.debugLine="LL.Tag = i";
_ll.setTag((Object)(_i));
 //BA.debugLineNum = 2178;BA.debugLine="LL.Text = aLetrasElegir(i).LetraSiempre 'sgLe";
_ll.setText((Object)(_vvvvvvvv0[_i].LetraSiempre));
 };
 //BA.debugLineNum = 2180;BA.debugLine="LL.SetBackgroundImage(gt_Color.bitmPalabraOff)";
_ll.SetBackgroundImage((android.graphics.Bitmap)(mostCurrent._gt_color.bitmPalabraOff.getObject()));
 //BA.debugLineNum = 2183;BA.debugLine="LL.Gravity = Gravity.center";
_ll.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 2186;BA.debugLine="If i < sgLetras.Length / 2 Then";
if (_i<_vvvvvvv0.length()/(double)2) { 
 //BA.debugLineNum = 2187;BA.debugLine="iLugar = (i*(iAncho+iSeparacion))+ iDesplaza";
_ilugar = (int) ((_i*(_iancho+_iseparacion))+_idesplaza);
 //BA.debugLineNum = 2188;BA.debugLine="Panel5.AddView(LL, iLugar,iTop1,iAncho,iAlto)";
mostCurrent._panel5.AddView((android.view.View)(_ll.getObject()),_ilugar,_itop1,_iancho,_ialto);
 }else {
 //BA.debugLineNum = 2190;BA.debugLine="iLugar = ((i-sgLetras.Length/2)*(iAncho+iSepar";
_ilugar = (int) (((_i-_vvvvvvv0.length()/(double)2)*(_iancho+_iseparacion))+_idesplaza);
 //BA.debugLineNum = 2191;BA.debugLine="Panel5.AddView (LL, iLugar,iTop2,iAncho,iAlto)";
mostCurrent._panel5.AddView((android.view.View)(_ll.getObject()),_ilugar,_itop2,_iancho,_ialto);
 };
 //BA.debugLineNum = 2193;BA.debugLine="If i=0 Then";
if (_i==0) { 
 //BA.debugLineNum = 2194;BA.debugLine="Publicos.SetLabelTextSize(LL, \"A\", 30, 5, 70)";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv4(mostCurrent.activityBA,_ll,"A",(float) (30),(float) (5),(int) (70));
 //BA.debugLineNum = 2195;BA.debugLine="iTextSize = LL.TextSize";
_itextsize = (int) (_ll.getTextSize());
 }else {
 //BA.debugLineNum = 2197;BA.debugLine="LL.textsize = iTextSize";
_ll.setTextSize((float) (_itextsize));
 };
 //BA.debugLineNum = 2201;BA.debugLine="lblsList.Add(LL)";
_lblslist.Add((Object)(_ll.getObject()));
 //BA.debugLineNum = 2202;BA.debugLine="Dim lbl As Label = lblsList.Get(i)";
_lbl = new anywheresoftware.b4a.objects.LabelWrapper();
_lbl.setObject((android.widget.TextView)(_lblslist.Get(_i)));
 //BA.debugLineNum = 2203;BA.debugLine="lbls(i) = lbl";
_lbls[_i] = _lbl;
 }
};
 //BA.debugLineNum = 2207;BA.debugLine="lblArrayLetras = lbls";
mostCurrent._vvvvvvvvvvvvvvvvvvvv7 = _lbls;
 //BA.debugLineNum = 2210;BA.debugLine="End Sub";
return "";
}
public static String  _v2_generalabelspalabra(com.matetejuego.free.publicos._tletraspalabra[] _apletraspalabra) throws Exception{
anywheresoftware.b4a.objects.collections.List _lblslist = null;
int _ilargopalabra = 0;
anywheresoftware.b4a.objects.LabelWrapper[] _lbls = null;
int _iinicio = 0;
int _iancholetra = 0;
int _iseparaletra = 0;
int _itop = 0;
int _ileft = 0;
int _itextsize = 0;
int _i = 0;
anywheresoftware.b4a.objects.LabelWrapper _ll = null;
int _ialto = 0;
anywheresoftware.b4a.objects.LabelWrapper _lbl = null;
 //BA.debugLineNum = 2212;BA.debugLine="Sub v2_GeneraLabelsPalabra (apLetrasPalabra() As t";
 //BA.debugLineNum = 2214;BA.debugLine="Dim lblsList As List, iLargoPalabra As Int = xtPa";
_lblslist = new anywheresoftware.b4a.objects.collections.List();
_ilargopalabra = _vvvvvvvvvvvvvvvvvv4.Palabra.length();
 //BA.debugLineNum = 2217;BA.debugLine="Dim lbls(iLargoPalabra) As Label";
_lbls = new anywheresoftware.b4a.objects.LabelWrapper[_ilargopalabra];
{
int d0 = _lbls.length;
for (int i0 = 0;i0 < d0;i0++) {
_lbls[i0] = new anywheresoftware.b4a.objects.LabelWrapper();
}
}
;
 //BA.debugLineNum = 2219;BA.debugLine="Dim iInicio As Int";
_iinicio = 0;
 //BA.debugLineNum = 2221;BA.debugLine="BorraLabels ' borra los labels del array lblArray";
_vvvvvvvvvvvvvvvvvvv5();
 //BA.debugLineNum = 2223;BA.debugLine="lblsList.Initialize";
_lblslist.Initialize();
 //BA.debugLineNum = 2225;BA.debugLine="Dim iAnchoLetra As Int = Get_AnchoLetraPalabra(iL";
_iancholetra = _get_ancholetrapalabra(_ilargopalabra);
 //BA.debugLineNum = 2226;BA.debugLine="Dim iSeparaLetra As Int= Get_SeparaLetra";
_iseparaletra = _get_separaletra();
 //BA.debugLineNum = 2228;BA.debugLine="Dim iTop As Int  = (Panel4.Height - iAnchoLetra )";
_itop = (int) ((mostCurrent._panel4.getHeight()-_iancholetra)/(double)2);
 //BA.debugLineNum = 2229;BA.debugLine="Dim iLeft As Int, iTextSize As Int";
_ileft = 0;
_itextsize = 0;
 //BA.debugLineNum = 2231;BA.debugLine="iInicio = Get_InicioLetras(iAnchoLetra, iLargoPal";
_iinicio = _get_inicioletras(_iancholetra,_ilargopalabra);
 //BA.debugLineNum = 2232;BA.debugLine="For i = 0 To lbls.Length -1'aLetrasPalabra.Leng";
{
final int step11 = 1;
final int limit11 = (int) (_lbls.length-1);
for (_i = (int) (0) ; (step11 > 0 && _i <= limit11) || (step11 < 0 && _i >= limit11); _i = ((int)(0 + _i + step11)) ) {
 //BA.debugLineNum = 2233;BA.debugLine="Dim LL As Label";
_ll = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 2234;BA.debugLine="Dim iAlto As Int = iAnchoLetra";
_ialto = _iancholetra;
 //BA.debugLineNum = 2236;BA.debugLine="LL.Initialize(\"lblPalabra\")";
_ll.Initialize(mostCurrent.activityBA,"lblPalabra");
 //BA.debugLineNum = 2237;BA.debugLine="LL.Gravity = Gravity.CENTER";
_ll.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 2238;BA.debugLine="iLeft = (i*(iSeparaLetra+iAnchoLetra))+iInicio";
_ileft = (int) ((_i*(_iseparaletra+_iancholetra))+_iinicio);
 //BA.debugLineNum = 2239;BA.debugLine="LL.Typeface = tFontOpenSansSemiBold";
_ll.setTypeface((android.graphics.Typeface)(_vvvvvvvvvv4.getObject()));
 //BA.debugLineNum = 2243;BA.debugLine="LL.TextColor = Colors.white";
_ll.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 2244;BA.debugLine="If apLetrasPalabra(i).comprada Then";
if (_apletraspalabra[_i].comprada) { 
 //BA.debugLineNum = 2245;BA.debugLine="LL.Tag = \"?\"";
_ll.setTag((Object)("?"));
 //BA.debugLineNum = 2246;BA.debugLine="LL.SetBackgroundImage(gt_Color.bitmPalabraCompr";
_ll.SetBackgroundImage((android.graphics.Bitmap)(mostCurrent._gt_color.bitmPalabraComprada.getObject()));
 //BA.debugLineNum = 2247;BA.debugLine="LL.Text = apLetrasPalabra(i).letra";
_ll.setText((Object)(_apletraspalabra[_i].letra));
 }else {
 //BA.debugLineNum = 2249;BA.debugLine="LL.SetBackgroundImage(gt_Color.bitmPalabraOff)";
_ll.SetBackgroundImage((android.graphics.Bitmap)(mostCurrent._gt_color.bitmPalabraOff.getObject()));
 };
 //BA.debugLineNum = 2252;BA.debugLine="Panel4.AddView(LL, iLeft,iTop,iAnchoLetra,iAlto)";
mostCurrent._panel4.AddView((android.view.View)(_ll.getObject()),_ileft,_itop,_iancholetra,_ialto);
 //BA.debugLineNum = 2254;BA.debugLine="If i = 0 Then 'calcula el temaño de las letras";
if (_i==0) { 
 //BA.debugLineNum = 2255;BA.debugLine="Publicos.SetLabelTextSize(LL, \"A\", 30,5, 70)";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv4(mostCurrent.activityBA,_ll,"A",(float) (30),(float) (5),(int) (70));
 //BA.debugLineNum = 2256;BA.debugLine="iTextSize = LL.TextSize";
_itextsize = (int) (_ll.getTextSize());
 }else {
 //BA.debugLineNum = 2258;BA.debugLine="LL.textsize = iTextSize";
_ll.setTextSize((float) (_itextsize));
 };
 //BA.debugLineNum = 2262;BA.debugLine="lblsList.Add(LL)";
_lblslist.Add((Object)(_ll.getObject()));
 //BA.debugLineNum = 2263;BA.debugLine="Dim lbl As Label = lblsList.Get(i)";
_lbl = new anywheresoftware.b4a.objects.LabelWrapper();
_lbl.setObject((android.widget.TextView)(_lblslist.Get(_i)));
 //BA.debugLineNum = 2264;BA.debugLine="lbls(i) = lbl";
_lbls[_i] = _lbl;
 }
};
 //BA.debugLineNum = 2268;BA.debugLine="lblArrayPalabra = lbls";
mostCurrent._vvvvvvvvvvvvvvvvvvv1 = _lbls;
 //BA.debugLineNum = 2270;BA.debugLine="End Sub";
return "";
}
public static String  _v2_menugenera() throws Exception{
int _iancho = 0;
int _ialto = 0;
anywheresoftware.b4a.objects.ImageViewWrapper _iimagen = null;
int _itextsize = 0;
 //BA.debugLineNum = 3037;BA.debugLine="Sub V2_MenuGenera";
 //BA.debugLineNum = 3039;BA.debugLine="Try";
try { //BA.debugLineNum = 3040;BA.debugLine="Dim iAncho As Int, iAlto As Int";
_iancho = 0;
_ialto = 0;
 //BA.debugLineNum = 3042;BA.debugLine="sm.Initialize(Activity, Me, \"SlideMenu\",0, Activi";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv2._initialize(mostCurrent.activityBA,mostCurrent._activity,jugar.getObject(),"SlideMenu",(int) (0),(int) (mostCurrent._activity.getWidth()*0.45));
 //BA.debugLineNum = 3045;BA.debugLine="Dim iImagen As ImageView";
_iimagen = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 3046;BA.debugLine="iImagen.Initialize(\"\")";
_iimagen.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 3047;BA.debugLine="sm.smAddView(iImagen, 0, 0, -1, Activity.Height,";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv2._vvvvvvvvvvvvvv2((anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(_iimagen.getObject())),(int) (0),(int) (0),(int) (-1),mostCurrent._activity.getHeight(),anywheresoftware.b4a.keywords.Common.False,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 3048;BA.debugLine="iImagen.SetBackgroundImage ( gt_Color.bitmMenuFon";
_iimagen.SetBackgroundImage((android.graphics.Bitmap)(mostCurrent._gt_color.bitmMenuFondo.getObject()));
 //BA.debugLineNum = 3051;BA.debugLine="iAncho = Activity.Width * 0.45";
_iancho = (int) (mostCurrent._activity.getWidth()*0.45);
 //BA.debugLineNum = 3052;BA.debugLine="iAlto = Activity.Height * 0.07";
_ialto = (int) (mostCurrent._activity.getHeight()*0.07);
 //BA.debugLineNum = 3057;BA.debugLine="lblCalcTextSize.Width = iAncho*0.8";
mostCurrent._lblcalctextsize.setWidth((int) (_iancho*0.8));
 //BA.debugLineNum = 3058;BA.debugLine="lblCalcTextSize.Height = iAlto";
mostCurrent._lblcalctextsize.setHeight(_ialto);
 //BA.debugLineNum = 3059;BA.debugLine="Publicos.SetLabelTextSize(lblCalcTextSize, \"CREDI";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv4(mostCurrent.activityBA,mostCurrent._lblcalctextsize,"CREDITOS",(float) (40),(float) (5),(int) (100));
 //BA.debugLineNum = 3060;BA.debugLine="Dim iTextSize As Int = lblCalcTextSize.TextSize";
_itextsize = (int) (mostCurrent._lblcalctextsize.getTextSize());
 //BA.debugLineNum = 3062;BA.debugLine="sm.AddItemArray(\"VOLVER\", \"Menu-Volver.png\", 0, i";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv2._vvvvvvvvvvvvv4("VOLVER","Menu-Volver.png",(int) (0),_ialto,_itextsize);
 //BA.debugLineNum = 3063;BA.debugLine="sm.AddItemArray(\"CREDITOS\", \"Menu-Creditos.png\",";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv2._vvvvvvvvvvvvv4("CREDITOS","Menu-Creditos.png",(int) (_ialto*3),_ialto,_itextsize);
 //BA.debugLineNum = 3064;BA.debugLine="sm.AddItemArray(\"AYUDA\", \"Menu-Ayuda.png\", iAlto*";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv2._vvvvvvvvvvvvv4("AYUDA","Menu-Ayuda.png",(int) (_ialto*4),_ialto,_itextsize);
 //BA.debugLineNum = 3066;BA.debugLine="If Publicos.Get_SeteoUsuarioEnteroDesde(g_sqlBase";
if (mostCurrent._vvvvvvvvvvvvvvv7._get_seteousuarioenterodesde(mostCurrent.activityBA,_g_sqlbaselocalusuario,"Sonido")==1) { 
 //BA.debugLineNum = 3067;BA.debugLine="sm.AddItemArray(\"SONIDO\", \"Menu-Sonido-On.png\",";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv2._vvvvvvvvvvvvv4("SONIDO","Menu-Sonido-On.png",(int) (_ialto*5),_ialto,_itextsize);
 }else {
 //BA.debugLineNum = 3069;BA.debugLine="sm.AddItemArray(\"SONIDO\", \"Menu-Sonido-Off.png\",";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv2._vvvvvvvvvvvvv4("SONIDO","Menu-Sonido-Off.png",(int) (_ialto*5),_ialto,_itextsize);
 };
 //BA.debugLineNum = 3073;BA.debugLine="If bProductos Then";
if (_vvvvvv0) { 
 //BA.debugLineNum = 3074;BA.debugLine="sm.AddItemArray(\"SHOP\", \"Menu-Tienda.png\", iAlto";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv2._vvvvvvvvvvvvv4("SHOP","Menu-Tienda.png",(int) (_ialto*6),_ialto,_itextsize);
 };
 //BA.debugLineNum = 3080;BA.debugLine="sm.AddItemarray(\"HISTORIA\", \"Menu-Historia.png\",";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv2._vvvvvvvvvvvvv4("HISTORIA","Menu-Historia.png",(int) (_ialto*7),_ialto,_itextsize);
 } 
       catch (Exception e27) {
			processBA.setLastException(e27); //BA.debugLineNum = 3085;BA.debugLine="Log(\"Catch V2_MenuGenera\")";
anywheresoftware.b4a.keywords.Common.Log("Catch V2_MenuGenera");
 };
 //BA.debugLineNum = 3088;BA.debugLine="End Sub";
return "";
}
public static String  _v2_muestradefinicion() throws Exception{
 //BA.debugLineNum = 416;BA.debugLine="Sub V2_MuestraDefinicion";
 //BA.debugLineNum = 417;BA.debugLine="lblv2Def.Text = xtPalabra.descripcion";
mostCurrent._lblv2def.setText((Object)(_vvvvvvvvvvvvvvvvvv4.Descripcion));
 //BA.debugLineNum = 418;BA.debugLine="Publicos.SetLabelTextSize(lblv2Def, xtPalabra.des";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv4(mostCurrent.activityBA,mostCurrent._lblv2def,_vvvvvvvvvvvvvvvvvv4.Descripcion,(float) (60),(float) (10),(int) (100));
 //BA.debugLineNum = 420;BA.debugLine="End Sub";
return "";
}
public static String  _v2_muestranuevapalabra(int _idpalabra,boolean _saltada,boolean _brejuego) throws Exception{
 //BA.debugLineNum = 423;BA.debugLine="Sub v2_MuestraNuevaPalabra(idPalabra As Int, Salta";
 //BA.debugLineNum = 424;BA.debugLine="gb_PalabraRejugada = bRejuego";
_gb_palabrarejugada = _brejuego;
 //BA.debugLineNum = 427;BA.debugLine="If bRejuego = False And Publicos.Get_EsUltimaPala";
if (_brejuego==anywheresoftware.b4a.keywords.Common.False && mostCurrent._vvvvvvvvvvvvvvv7._get_esultimapalabra(mostCurrent.activityBA,(anywheresoftware.b4a.sql.SQL)(_g_sqlbaselocalusuario),_g_sqlbaselocaljuego)==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 428;BA.debugLine="V2_PantallaConfigura(xConfiguraPantalla.FinDeJue";
_v2_pantallaconfigura(_vvvvvvvv3.FinDeJuego,(int) (0));
 }else {
 //BA.debugLineNum = 431;BA.debugLine="Panel4.RemoveAllViews";
mostCurrent._panel4.RemoveAllViews();
 //BA.debugLineNum = 432;BA.debugLine="Panel5.RemoveAllViews";
mostCurrent._panel5.RemoveAllViews();
 //BA.debugLineNum = 438;BA.debugLine="CargarPalabra(idPalabra, bRejuego, Saltada) ' le";
_vvvvvvvvvvvvvvvvvvv7(_idpalabra,_brejuego,_saltada);
 //BA.debugLineNum = 439;BA.debugLine="Set_Colores(xtPalabra.idNivel)";
_set_colores(_vvvvvvvvvvvvvvvvvv4.idNivel);
 //BA.debugLineNum = 440;BA.debugLine="CargarNivel(xtPalabra.idNivel, xtPalabra.idPalab";
_vvvvvvvvvvvvvvvvvvv6(_vvvvvvvvvvvvvvvvvv4.idNivel,_vvvvvvvvvvvvvvvvvv4.idPalabra);
 //BA.debugLineNum = 444;BA.debugLine="V2_CargaImagenesViewsEstaticas 'carga las imagen";
_v2_cargaimagenesviewsestaticas();
 //BA.debugLineNum = 445;BA.debugLine="Costos_Muestra";
_costos_muestra();
 //BA.debugLineNum = 447;BA.debugLine="Panel4.RemoveAllViews";
mostCurrent._panel4.RemoveAllViews();
 //BA.debugLineNum = 448;BA.debugLine="v2_GeneraLabelsPalabra (aLetrasPalabra)";
_v2_generalabelspalabra(_vvvvvvvv7);
 //BA.debugLineNum = 449;BA.debugLine="V2_GeneraLabelsLetras(aLetrasPalabra)";
_v2_generalabelsletras(_vvvvvvvv7);
 //BA.debugLineNum = 452;BA.debugLine="Neuronas_Mostrar";
_neuronas_mostrar();
 //BA.debugLineNum = 453;BA.debugLine="V2_MuestraDefinicion";
_v2_muestradefinicion();
 //BA.debugLineNum = 454;BA.debugLine="Propaganda_Mostrar";
_propaganda_mostrar();
 };
 //BA.debugLineNum = 456;BA.debugLine="End Sub";
return "";
}
public static String  _v2_noadivinopalabraresalta() throws Exception{
int _i = 0;
int _j = 0;
 //BA.debugLineNum = 2563;BA.debugLine="Sub V2_NoAdivinoPalabraResalta";
 //BA.debugLineNum = 2565;BA.debugLine="Sonido(SONIDO_ERROR)";
_vvvvvvvvvvvvvvvvvv6(_sonido_error);
 //BA.debugLineNum = 2568;BA.debugLine="For i =0 To 3";
{
final int step2 = 1;
final int limit2 = (int) (3);
for (_i = (int) (0) ; (step2 > 0 && _i <= limit2) || (step2 < 0 && _i >= limit2); _i = ((int)(0 + _i + step2)) ) {
 //BA.debugLineNum = 2569;BA.debugLine="For j = 0 To lblArrayPalabra.Length-1";
{
final int step3 = 1;
final int limit3 = (int) (mostCurrent._vvvvvvvvvvvvvvvvvvv1.length-1);
for (_j = (int) (0) ; (step3 > 0 && _j <= limit3) || (step3 < 0 && _j >= limit3); _j = ((int)(0 + _j + step3)) ) {
 //BA.debugLineNum = 2570;BA.debugLine="If aLetrasPalabra(j).comprada = False Then";
if (_vvvvvvvv7[_j].comprada==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 2572;BA.debugLine="lblArrayPalabra(j).SetBackgroundImage(gt_Color";
mostCurrent._vvvvvvvvvvvvvvvvvvv1[_j].SetBackgroundImage((android.graphics.Bitmap)(mostCurrent._gt_color.bitmPalabraError.getObject()));
 //BA.debugLineNum = 2573;BA.debugLine="lblArrayPalabra(j).TextColor = Colors.white";
mostCurrent._vvvvvvvvvvvvvvvvvvv1[_j].setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 };
 }
};
 //BA.debugLineNum = 2576;BA.debugLine="Publicos.Wait(150)";
mostCurrent._vvvvvvvvvvvvvvv7._vvvvv3(mostCurrent.activityBA,(int) (150));
 //BA.debugLineNum = 2578;BA.debugLine="For j=0 To aLetrasPalabra.Length-1";
{
final int step10 = 1;
final int limit10 = (int) (_vvvvvvvv7.length-1);
for (_j = (int) (0) ; (step10 > 0 && _j <= limit10) || (step10 < 0 && _j >= limit10); _j = ((int)(0 + _j + step10)) ) {
 //BA.debugLineNum = 2579;BA.debugLine="If aLetrasPalabra(j).comprada = False Then";
if (_vvvvvvvv7[_j].comprada==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 2581;BA.debugLine="If lblArrayPalabra(j).Text = \"\" Then";
if ((mostCurrent._vvvvvvvvvvvvvvvvvvv1[_j].getText()).equals("")) { 
 //BA.debugLineNum = 2582;BA.debugLine="lblArrayPalabra(j).SetBackgroundImage(gt_Colo";
mostCurrent._vvvvvvvvvvvvvvvvvvv1[_j].SetBackgroundImage((android.graphics.Bitmap)(mostCurrent._gt_color.bitmPalabraOff.getObject()));
 //BA.debugLineNum = 2583;BA.debugLine="lblArrayPalabra(j).TextColor =Colors.transpar";
mostCurrent._vvvvvvvvvvvvvvvvvvv1[_j].setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Transparent);
 }else {
 //BA.debugLineNum = 2585;BA.debugLine="lblArrayPalabra(j).SetBackgroundImage(gt_Colo";
mostCurrent._vvvvvvvvvvvvvvvvvvv1[_j].SetBackgroundImage((android.graphics.Bitmap)(mostCurrent._gt_color.bitmPalabraOn.getObject()));
 //BA.debugLineNum = 2586;BA.debugLine="lblArrayPalabra(j).TextColor =gt_Color.ColorT";
mostCurrent._vvvvvvvvvvvvvvvvvvv1[_j].setTextColor(mostCurrent._gt_color.ColorTexto);
 };
 };
 }
};
 //BA.debugLineNum = 2591;BA.debugLine="Publicos.Wait(50)";
mostCurrent._vvvvvvvvvvvvvvv7._vvvvv3(mostCurrent.activityBA,(int) (50));
 }
};
 //BA.debugLineNum = 2595;BA.debugLine="End Sub";
return "";
}
public static String  _v2_pantallaconfigura(int _iopcion,int _ineuronas) throws Exception{
int _iganadas = 0;
int _isalteadas = 0;
String _smensaje = "";
boolean _bofrecerpremium = false;
 //BA.debugLineNum = 2695;BA.debugLine="Sub V2_PantallaConfigura(iOpcion As Int, iNeuronas";
 //BA.debugLineNum = 2697;BA.debugLine="lbl61.Tag = iOpcion";
mostCurrent._lbl61.setTag((Object)(_iopcion));
 //BA.debugLineNum = 2698;BA.debugLine="img51.Tag = iOpcion";
mostCurrent._img51.setTag((Object)(_iopcion));
 //BA.debugLineNum = 2700;BA.debugLine="iPantallaActiva = iOpcion";
_vvvvvvvv4 = _iopcion;
 //BA.debugLineNum = 2701;BA.debugLine="Select (True)";
switch (BA.switchObjectToInt((anywheresoftware.b4a.keywords.Common.True),_iopcion==_vvvvvvvv3.Compartir,_iopcion==_vvvvvvvv3.ComprarLetra,_iopcion==_vvvvvvvv3.Jugar,_iopcion==_vvvvvvvv3.SaltarPalabra,_iopcion==_vvvvvvvv3.Adivino,_iopcion==_vvvvvvvv3.Ayuda,_iopcion==_vvvvvvvv3.Creditos,_iopcion==_vvvvvvvv3.FinDeJuego,_iopcion==_vvvvvvvv3.Producto,_iopcion==_vvvvvvvv3.ComproNeuronas,_iopcion==_vvvvvvvv3.MuestraAviso,_iopcion==_vvvvvvvv3.Premium,_iopcion==_vvvvvvvv3.Historia)) {
case 0: {
 //BA.debugLineNum = 2705;BA.debugLine="lbl41.Text = \"COMPARTIR\"";
mostCurrent._lbl41.setText((Object)("COMPARTIR"));
 //BA.debugLineNum = 2706;BA.debugLine="lbl41.TextColor = gt_Color.ColorDefault";
mostCurrent._lbl41.setTextColor(mostCurrent._gt_color.ColorDefault);
 //BA.debugLineNum = 2707;BA.debugLine="img51Facebook.Visible = True";
mostCurrent._img51facebook.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2708;BA.debugLine="img51Twitter.visible = True";
mostCurrent._img51twitter.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2709;BA.debugLine="imgAnimacion.Visible = False";
mostCurrent._imganimacion.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2710;BA.debugLine="lbl51Facebook.Visible = True";
mostCurrent._lbl51facebook.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2711;BA.debugLine="lbl51Twitter.Visible = True";
mostCurrent._lbl51twitter.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2712;BA.debugLine="img51.Visible = False";
mostCurrent._img51.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2713;BA.debugLine="lbl51.Visible = False";
mostCurrent._lbl51.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2714;BA.debugLine="lbl51MensajeMatete.visible = False";
mostCurrent._lbl51mensajematete.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2715;BA.debugLine="lbl61.Text = \"CANCELAR\"";
mostCurrent._lbl61.setText((Object)("CANCELAR"));
 //BA.debugLineNum = 2716;BA.debugLine="lbl61.Visible = True";
mostCurrent._lbl61.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2718;BA.debugLine="Panel1.Visible = True";
mostCurrent._panel1.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2719;BA.debugLine="Panel41.Visible = True";
mostCurrent._panel41.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2720;BA.debugLine="Panel51.Visible = True";
mostCurrent._panel51.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2721;BA.debugLine="Panel61.Visible = True";
mostCurrent._panel61.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2722;BA.debugLine="Panel3.visible = False";
mostCurrent._panel3.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2723;BA.debugLine="Panel21.Visible = False";
mostCurrent._panel21.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2724;BA.debugLine="Panel11.Visible = False";
mostCurrent._panel11.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2726;BA.debugLine="scrViewAyuda.Visible = False";
mostCurrent._scrviewayuda.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2728;BA.debugLine="lbl61.textcolor = gt_Color.ColorTexto";
mostCurrent._lbl61.setTextColor(mostCurrent._gt_color.ColorTexto);
 //BA.debugLineNum = 2729;BA.debugLine="Panel61.Color = Colors.white";
mostCurrent._panel61.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 2730;BA.debugLine="Activity.Color = Colors.white";
mostCurrent._activity.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 2732;BA.debugLine="pnlInvisible.Visible = False";
mostCurrent._pnlinvisible.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2733;BA.debugLine="pnlHistoria.Visible = False";
mostCurrent._pnlhistoria.setVisible(anywheresoftware.b4a.keywords.Common.False);
 break; }
case 1: {
 //BA.debugLineNum = 2737;BA.debugLine="lbl41.Text = \"DESCUBRE UNA LETRA\"";
mostCurrent._lbl41.setText((Object)("DESCUBRE UNA LETRA"));
 //BA.debugLineNum = 2738;BA.debugLine="lbl41.TextColor = gt_Color.ColorDefault";
mostCurrent._lbl41.setTextColor(mostCurrent._gt_color.ColorDefault);
 //BA.debugLineNum = 2740;BA.debugLine="img51Facebook.Visible = False";
mostCurrent._img51facebook.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2741;BA.debugLine="img51Twitter.visible = False";
mostCurrent._img51twitter.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2742;BA.debugLine="img51.Visible = True";
mostCurrent._img51.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2743;BA.debugLine="img51.SetBackgroundImage(gt_Color.bitmPedir)";
mostCurrent._img51.SetBackgroundImage((android.graphics.Bitmap)(mostCurrent._gt_color.bitmPedir.getObject()));
 //BA.debugLineNum = 2745;BA.debugLine="imgAnimacion.Visible = False";
mostCurrent._imganimacion.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2746;BA.debugLine="lbl51Facebook.Visible = False";
mostCurrent._lbl51facebook.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2747;BA.debugLine="lbl51Twitter.Visible = False";
mostCurrent._lbl51twitter.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2748;BA.debugLine="lbl51.Text =\"USAR \" & xtNivel.CostoLetra & \" NEUR";
mostCurrent._lbl51.setText((Object)("USAR "+BA.NumberToString(_vvvvvvvvvvvvvvvvvv7.CostoLetra)+" NEURONA"+_vvvvvvvvvvvvvvvvvvvvvvv3(_vvvvvvvvvvvvvvvvvv7.CostoLetra>1,"S","")));
 //BA.debugLineNum = 2749;BA.debugLine="lbl51.Visible = True";
mostCurrent._lbl51.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2750;BA.debugLine="lbl51MensajeMatete.Visible = False";
mostCurrent._lbl51mensajematete.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2751;BA.debugLine="lbl61.Text = \"CANCELAR\"";
mostCurrent._lbl61.setText((Object)("CANCELAR"));
 //BA.debugLineNum = 2752;BA.debugLine="lbl61.Visible = True";
mostCurrent._lbl61.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2754;BA.debugLine="Panel41.Visible = True";
mostCurrent._panel41.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2755;BA.debugLine="Panel51.Visible = True";
mostCurrent._panel51.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2756;BA.debugLine="Panel61.Visible = True";
mostCurrent._panel61.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2757;BA.debugLine="Panel3.visible = False";
mostCurrent._panel3.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2758;BA.debugLine="Panel21.Visible = False";
mostCurrent._panel21.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2759;BA.debugLine="Panel11.Visible = False";
mostCurrent._panel11.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2761;BA.debugLine="scrViewAyuda.Visible = False";
mostCurrent._scrviewayuda.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2762;BA.debugLine="lbl61.textcolor = Get_aTextColor(gi_CombinacionCo";
mostCurrent._lbl61.setTextColor(_get_atextcolor(_gi_combinacioncolores,(int) (0)));
 //BA.debugLineNum = 2763;BA.debugLine="Panel61.Color = gt_Color.ColorDefault";
mostCurrent._panel61.setColor(mostCurrent._gt_color.ColorDefault);
 //BA.debugLineNum = 2765;BA.debugLine="Activity.Color = Colors.white";
mostCurrent._activity.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 2766;BA.debugLine="Panel61.BringToFront";
mostCurrent._panel61.BringToFront();
 //BA.debugLineNum = 2768;BA.debugLine="pnlHistoria.Visible = False";
mostCurrent._pnlhistoria.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2769;BA.debugLine="pnlInvisible.Visible = False";
mostCurrent._pnlinvisible.setVisible(anywheresoftware.b4a.keywords.Common.False);
 break; }
case 2: {
 //BA.debugLineNum = 2771;BA.debugLine="pnlInvisible.RemoveAllViews";
mostCurrent._pnlinvisible.RemoveAllViews();
 //BA.debugLineNum = 2772;BA.debugLine="pnlInvisible.Visible =False";
mostCurrent._pnlinvisible.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2773;BA.debugLine="imgShadow.Top = Panel1.Top + Panel1.height";
mostCurrent._imgshadow.setTop((int) (mostCurrent._panel1.getTop()+mostCurrent._panel1.getHeight()));
 //BA.debugLineNum = 2776;BA.debugLine="Panel1.Visible = True";
mostCurrent._panel1.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2777;BA.debugLine="Panel2.Visible = True";
mostCurrent._panel2.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2778;BA.debugLine="Panel2.Top = Panel1.Top + Panel1.Height ' lo vuel";
mostCurrent._panel2.setTop((int) (mostCurrent._panel1.getTop()+mostCurrent._panel1.getHeight()));
 //BA.debugLineNum = 2781;BA.debugLine="Panel3.Visible = True";
mostCurrent._panel3.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2782;BA.debugLine="Panel4.Visible = True";
mostCurrent._panel4.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2783;BA.debugLine="Panel5.Visible = True";
mostCurrent._panel5.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2784;BA.debugLine="Panel6.Visible = True";
mostCurrent._panel6.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2785;BA.debugLine="Panel41.Visible = False";
mostCurrent._panel41.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2786;BA.debugLine="Panel51.Visible = False";
mostCurrent._panel51.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2787;BA.debugLine="Panel61.Visible = False";
mostCurrent._panel61.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2788;BA.debugLine="Panel21.Visible = False";
mostCurrent._panel21.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2789;BA.debugLine="Panel11.Visible = False";
mostCurrent._panel11.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2791;BA.debugLine="imgLoading.Visible = False";
mostCurrent._imgloading.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2792;BA.debugLine="scrViewAyuda.Visible = False";
mostCurrent._scrviewayuda.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2794;BA.debugLine="lbl61.textcolor = Get_aTextColor(gi_CombinacionCo";
mostCurrent._lbl61.setTextColor(_get_atextcolor(_gi_combinacioncolores,(int) (0)));
 //BA.debugLineNum = 2795;BA.debugLine="Panel61.Color = gt_Color.ColorDefault";
mostCurrent._panel61.setColor(mostCurrent._gt_color.ColorDefault);
 //BA.debugLineNum = 2796;BA.debugLine="Activity.Color = Colors.white";
mostCurrent._activity.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 2798;BA.debugLine="pnlHistoria.Visible = False";
mostCurrent._pnlhistoria.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2799;BA.debugLine="pnlInvisible.Visible = False";
mostCurrent._pnlinvisible.setVisible(anywheresoftware.b4a.keywords.Common.False);
 break; }
case 3: {
 //BA.debugLineNum = 2802;BA.debugLine="lbl41.Text = \"SALTAR EL MATETE\"";
mostCurrent._lbl41.setText((Object)("SALTAR EL MATETE"));
 //BA.debugLineNum = 2803;BA.debugLine="lbl41.TextColor = gt_Color.ColorDefault";
mostCurrent._lbl41.setTextColor(mostCurrent._gt_color.ColorDefault);
 //BA.debugLineNum = 2805;BA.debugLine="img51Facebook.Visible = False";
mostCurrent._img51facebook.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2806;BA.debugLine="img51Twitter.visible = False";
mostCurrent._img51twitter.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2807;BA.debugLine="img51.Visible = True";
mostCurrent._img51.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2808;BA.debugLine="img51.SetBackgroundImage (gt_Color.bitmSaltarPala";
mostCurrent._img51.SetBackgroundImage((android.graphics.Bitmap)(mostCurrent._gt_color.bitmSaltarPalabra.getObject()));
 //BA.debugLineNum = 2809;BA.debugLine="imgAnimacion.Visible = False";
mostCurrent._imganimacion.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2810;BA.debugLine="lbl51Facebook.Visible = False";
mostCurrent._lbl51facebook.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2811;BA.debugLine="lbl51Twitter.Visible = False";
mostCurrent._lbl51twitter.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2812;BA.debugLine="lbl51.Text =\"USAR \" & xtNivel.CostoSaltar & \" NEU";
mostCurrent._lbl51.setText((Object)("USAR "+BA.NumberToString(_vvvvvvvvvvvvvvvvvv7.CostoSaltar)+" NEURONA"+_vvvvvvvvvvvvvvvvvvvvvvv3(_vvvvvvvvvvvvvvvvvv7.CostoSaltar>1,"S","")));
 //BA.debugLineNum = 2813;BA.debugLine="lbl51.Visible = True";
mostCurrent._lbl51.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2814;BA.debugLine="lbl51MensajeMatete.Visible = False";
mostCurrent._lbl51mensajematete.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2815;BA.debugLine="lbl61.Text = \"CANCELAR\"";
mostCurrent._lbl61.setText((Object)("CANCELAR"));
 //BA.debugLineNum = 2816;BA.debugLine="lbl61.Visible = True";
mostCurrent._lbl61.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2818;BA.debugLine="Panel41.Visible = True";
mostCurrent._panel41.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2819;BA.debugLine="Panel51.Visible = True";
mostCurrent._panel51.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2820;BA.debugLine="Panel61.Visible = True";
mostCurrent._panel61.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2821;BA.debugLine="Panel3.visible = False";
mostCurrent._panel3.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2822;BA.debugLine="Panel21.Visible = False";
mostCurrent._panel21.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2823;BA.debugLine="Panel11.Visible = False";
mostCurrent._panel11.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2825;BA.debugLine="scrViewAyuda.Visible = False";
mostCurrent._scrviewayuda.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2826;BA.debugLine="lbl61.textcolor = Get_aTextColor(gi_CombinacionCo";
mostCurrent._lbl61.setTextColor(_get_atextcolor(_gi_combinacioncolores,(int) (0)));
 //BA.debugLineNum = 2827;BA.debugLine="Panel61.Color = gt_Color.ColorDefault";
mostCurrent._panel61.setColor(mostCurrent._gt_color.ColorDefault);
 //BA.debugLineNum = 2828;BA.debugLine="Activity.Color = Colors.white";
mostCurrent._activity.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 2829;BA.debugLine="pnlInvisible.Visible = False";
mostCurrent._pnlinvisible.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2830;BA.debugLine="pnlHistoria.Visible = False";
mostCurrent._pnlhistoria.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2831;BA.debugLine="V2_AnimaNeuronas";
_v2_animaneuronas();
 break; }
case 4: {
 //BA.debugLineNum = 2834;BA.debugLine="If xtPalabra.bRejugada And xtPalabra.bSalteada =";
if (_vvvvvvvvvvvvvvvvvv4.bRejugada && _vvvvvvvvvvvvvvvvvv4.bSalteada==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 2835;BA.debugLine="lbl21.Text = \"\"";
mostCurrent._lbl21.setText((Object)(""));
 }else {
 //BA.debugLineNum = 2837;BA.debugLine="lbl21.Text = \"Ganaste \" & Get_MonedasPorNivel &";
mostCurrent._lbl21.setText((Object)("Ganaste "+BA.NumberToString(_get_monedaspornivel())+" neuronas"));
 };
 //BA.debugLineNum = 2839;BA.debugLine="Publicos.SetLabelTextSize(lbl21, lbl21.Text, 30,6";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv4(mostCurrent.activityBA,mostCurrent._lbl21,mostCurrent._lbl21.getText(),(float) (30),(float) (6),(int) (100));
 //BA.debugLineNum = 2840;BA.debugLine="img51Facebook.Visible = False";
mostCurrent._img51facebook.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2841;BA.debugLine="img51Twitter.visible = False";
mostCurrent._img51twitter.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2842;BA.debugLine="img51.Visible = False";
mostCurrent._img51.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2844;BA.debugLine="imgAnimacion.Visible = True";
mostCurrent._imganimacion.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2845;BA.debugLine="lbl51Facebook.Visible = False";
mostCurrent._lbl51facebook.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2846;BA.debugLine="lbl51Twitter.Visible = False";
mostCurrent._lbl51twitter.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2848;BA.debugLine="lbl51.Visible = False";
mostCurrent._lbl51.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2849;BA.debugLine="lbl51MensajeMatete.Visible = True";
mostCurrent._lbl51mensajematete.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2850;BA.debugLine="lbl61.Text = \"CONTINUAR\"";
mostCurrent._lbl61.setText((Object)("CONTINUAR"));
 //BA.debugLineNum = 2851;BA.debugLine="lbl61.Visible = True";
mostCurrent._lbl61.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2853;BA.debugLine="Panel1.Visible = True";
mostCurrent._panel1.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2854;BA.debugLine="Panel41.Visible = False";
mostCurrent._panel41.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2855;BA.debugLine="Panel51.Visible = True";
mostCurrent._panel51.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2856;BA.debugLine="Panel61.Visible = True";
mostCurrent._panel61.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2857;BA.debugLine="Panel3.visible = False";
mostCurrent._panel3.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2858;BA.debugLine="Panel21.Visible = True";
mostCurrent._panel21.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2859;BA.debugLine="Panel11.Visible = False";
mostCurrent._panel11.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2861;BA.debugLine="lblv2Def.Text = Get_MensajeAdivino (xtPalabra.idP";
mostCurrent._lblv2def.setText((Object)(_get_mensajeadivino(_vvvvvvvvvvvvvvvvvv4.idPalabra)));
 //BA.debugLineNum = 2862;BA.debugLine="Publicos.SetLabelTextSize(lblv2Def, lblv2Def.text";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv4(mostCurrent.activityBA,mostCurrent._lblv2def,mostCurrent._lblv2def.getText(),(float) (60),(float) (10),(int) (100));
 //BA.debugLineNum = 2864;BA.debugLine="If xtPalabra.Palabra.ToUpperCase <> xtPalabra.Pal";
if ((_vvvvvvvvvvvvvvvvvv4.Palabra.toUpperCase()).equals(_vvvvvvvvvvvvvvvvvv4.PalabraDiccionario.toUpperCase()) == false) { 
 //BA.debugLineNum = 2865;BA.debugLine="lbl51MensajeMatete.Text = \"(\" & xtPalabra.Palabr";
mostCurrent._lbl51mensajematete.setText((Object)("("+_vvvvvvvvvvvvvvvvvv4.PalabraDiccionario+")"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))));
 }else {
 //BA.debugLineNum = 2867;BA.debugLine="lbl51MensajeMatete.Text = \"\"";
mostCurrent._lbl51mensajematete.setText((Object)(""));
 };
 //BA.debugLineNum = 2869;BA.debugLine="lbl51MensajeMatete.Text = lbl51MensajeMatete.Text";
mostCurrent._lbl51mensajematete.setText((Object)(mostCurrent._lbl51mensajematete.getText()+_vvvvvvvvvvvvvvvvvv4.Diccionario));
 //BA.debugLineNum = 2871;BA.debugLine="Publicos.SetLabelTextSize(lbl51MensajeMatete, lbl";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv4(mostCurrent.activityBA,mostCurrent._lbl51mensajematete,mostCurrent._lbl51mensajematete.getText(),(float) (40),(float) (6),(int) (100));
 //BA.debugLineNum = 2874;BA.debugLine="If lblv2Def.TextSize < lbl51MensajeMatete.textsiz";
if (mostCurrent._lblv2def.getTextSize()<mostCurrent._lbl51mensajematete.getTextSize()) { 
 //BA.debugLineNum = 2875;BA.debugLine="lbl51MensajeMatete.TextSize = lblv2Def.TextSize";
mostCurrent._lbl51mensajematete.setTextSize(mostCurrent._lblv2def.getTextSize());
 }else {
 //BA.debugLineNum = 2877;BA.debugLine="lblv2Def.TextSize = lbl51MensajeMatete.textsize";
mostCurrent._lblv2def.setTextSize(mostCurrent._lbl51mensajematete.getTextSize());
 };
 //BA.debugLineNum = 2881;BA.debugLine="scrViewAyuda.Visible = False";
mostCurrent._scrviewayuda.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2882;BA.debugLine="lbl61.textcolor = Get_aTextColor(gi_CombinacionCo";
mostCurrent._lbl61.setTextColor(_get_atextcolor(_gi_combinacioncolores,(int) (0)));
 //BA.debugLineNum = 2883;BA.debugLine="Panel61.Color = gt_Color.ColorDefault";
mostCurrent._panel61.setColor(mostCurrent._gt_color.ColorDefault);
 //BA.debugLineNum = 2885;BA.debugLine="Activity.Color = Colors.white";
mostCurrent._activity.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 2886;BA.debugLine="pnlInvisible.Visible = False";
mostCurrent._pnlinvisible.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2888;BA.debugLine="gi_AnimacionEnCurso = xConfiguraPantalla.Adivino";
_gi_animacionencurso = _vvvvvvvv3.Adivino;
 //BA.debugLineNum = 2889;BA.debugLine="pnlHistoria.Visible = False";
mostCurrent._pnlhistoria.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2890;BA.debugLine="V2_AdivinoAnimacion(2000, 2880)";
_v2_adivinoanimacion((int) (2000),(int) (2880));
 break; }
case 5: {
 //BA.debugLineNum = 2892;BA.debugLine="ScrollviewMUestra(\"A\")";
_vvvvvvvvvvvvvvvvvvvvvvvv4("A");
 //BA.debugLineNum = 2893;BA.debugLine="lbl61.Text = \"VOLVER\"";
mostCurrent._lbl61.setText((Object)("VOLVER"));
 //BA.debugLineNum = 2894;BA.debugLine="lbl61.TextColor = gt_Color.colordefault";
mostCurrent._lbl61.setTextColor(mostCurrent._gt_color.ColorDefault);
 //BA.debugLineNum = 2895;BA.debugLine="Panel61.Color = gt_Color.ColorClaro";
mostCurrent._panel61.setColor(mostCurrent._gt_color.ColorClaro);
 //BA.debugLineNum = 2896;BA.debugLine="Panel61.Visible = True";
mostCurrent._panel61.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2898;BA.debugLine="lbl11.Text = \"AYUDA\"";
mostCurrent._lbl11.setText((Object)("AYUDA"));
 //BA.debugLineNum = 2899;BA.debugLine="Panel11.Visible = True";
mostCurrent._panel11.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2901;BA.debugLine="Panel1.Visible = False";
mostCurrent._panel1.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2902;BA.debugLine="imgShadow.Top = Panel11.Top + Panel11.Height";
mostCurrent._imgshadow.setTop((int) (mostCurrent._panel11.getTop()+mostCurrent._panel11.getHeight()));
 //BA.debugLineNum = 2904;BA.debugLine="Panel6.Visible = False";
mostCurrent._panel6.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2905;BA.debugLine="Activity.Color = gt_Color.colordefault";
mostCurrent._activity.setColor(mostCurrent._gt_color.ColorDefault);
 //BA.debugLineNum = 2907;BA.debugLine="pnlHistoria.Visible = False";
mostCurrent._pnlhistoria.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2908;BA.debugLine="pnlInvisible.Visible = False";
mostCurrent._pnlinvisible.setVisible(anywheresoftware.b4a.keywords.Common.False);
 break; }
case 6: {
 //BA.debugLineNum = 2911;BA.debugLine="ScrollviewMUestra(\"C\")";
_vvvvvvvvvvvvvvvvvvvvvvvv4("C");
 //BA.debugLineNum = 2912;BA.debugLine="lbl11.Text = \"CREDITOS\"";
mostCurrent._lbl11.setText((Object)("CREDITOS"));
 //BA.debugLineNum = 2913;BA.debugLine="lbl61.Text = \"VOLVER\"";
mostCurrent._lbl61.setText((Object)("VOLVER"));
 //BA.debugLineNum = 2914;BA.debugLine="lbl61.TextColor = gt_Color.colordefault";
mostCurrent._lbl61.setTextColor(mostCurrent._gt_color.ColorDefault);
 //BA.debugLineNum = 2915;BA.debugLine="Panel61.Color = gt_Color.ColorClaro";
mostCurrent._panel61.setColor(mostCurrent._gt_color.ColorClaro);
 //BA.debugLineNum = 2917;BA.debugLine="Panel61.Visible = True";
mostCurrent._panel61.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2918;BA.debugLine="Panel11.Visible = True";
mostCurrent._panel11.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2920;BA.debugLine="Panel1.visible = False";
mostCurrent._panel1.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2921;BA.debugLine="imgShadow.Top = Panel11.Top + Panel11.Height";
mostCurrent._imgshadow.setTop((int) (mostCurrent._panel11.getTop()+mostCurrent._panel11.getHeight()));
 //BA.debugLineNum = 2923;BA.debugLine="Panel6.Visible = False";
mostCurrent._panel6.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2924;BA.debugLine="Activity.Color = gt_Color.colordefault";
mostCurrent._activity.setColor(mostCurrent._gt_color.ColorDefault);
 //BA.debugLineNum = 2925;BA.debugLine="pnlInvisible.Visible = False";
mostCurrent._pnlinvisible.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2926;BA.debugLine="pnlHistoria.Visible = False";
mostCurrent._pnlhistoria.setVisible(anywheresoftware.b4a.keywords.Common.False);
 break; }
case 7: {
 //BA.debugLineNum = 2929;BA.debugLine="bFinDeJuego = True";
_vvvvvvvvv3 = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 2930;BA.debugLine="Dim iGanadas As Int, iSalteadas As Int";
_iganadas = 0;
_isalteadas = 0;
 //BA.debugLineNum = 2932;BA.debugLine="iGanadas=Publicos.get_CantidadPalabrasAdivinadas(";
_iganadas = mostCurrent._vvvvvvvvvvvvvvv7._get_cantidadpalabrasadivinadas(mostCurrent.activityBA,(anywheresoftware.b4a.sql.SQL)(_g_sqlbaselocalusuario));
 //BA.debugLineNum = 2933;BA.debugLine="iSalteadas = Publicos.get_CantidadPalabrasSaltead";
_isalteadas = mostCurrent._vvvvvvvvvvvvvvv7._get_cantidadpalabrassalteadas(mostCurrent.activityBA,(anywheresoftware.b4a.sql.SQL)(_g_sqlbaselocalusuario));
 //BA.debugLineNum = 2935;BA.debugLine="Dim sMensaje As String, bOfrecerPremium As Boolea";
_smensaje = "";
_bofrecerpremium = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 2938;BA.debugLine="If gb_EsPremium Or Get_QuedanPalabrasAlmacen = Fa";
if (_gb_espremium || _get_quedanpalabrasalmacen()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 2939;BA.debugLine="sMensaje = \"JUEGO COMPLETADO\" & Chr(10)";
_smensaje = "JUEGO COMPLETADO"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)));
 //BA.debugLineNum = 2940;BA.debugLine="sMensaje  = sMensaje & \"Nos has dejado sin palab";
_smensaje = _smensaje+"Nos has dejado sin palabras "+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)));
 }else {
 //BA.debugLineNum = 2943;BA.debugLine="sMensaje = \"JUEGO GRATUITO COMPLETADO\" & Chr(10)";
_smensaje = "JUEGO GRATUITO COMPLETADO"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Puedes adquirir la versión premium"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"con más palabras"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)));
 };
 //BA.debugLineNum = 2948;BA.debugLine="sMensaje = sMensaje & \" \" & iGanadas  &  \" adivin";
_smensaje = _smensaje+" "+BA.NumberToString(_iganadas)+" adivinadas"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)));
 //BA.debugLineNum = 2949;BA.debugLine="sMensaje = sMensaje & \" \" &  iSalteadas & \" salte";
_smensaje = _smensaje+" "+BA.NumberToString(_isalteadas)+" salteada"+_vvvvvvvvvvvvvvvvvvvvvvv3(_isalteadas>1,"s","");
 //BA.debugLineNum = 2951;BA.debugLine="Panel1.Visible = False";
mostCurrent._panel1.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2952;BA.debugLine="Panel11.Visible = False";
mostCurrent._panel11.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2953;BA.debugLine="Panel2.Visible= True";
mostCurrent._panel2.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2954;BA.debugLine="Panel3.Visible = False";
mostCurrent._panel3.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2955;BA.debugLine="Panel21.visible = False 'agregado";
mostCurrent._panel21.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2956;BA.debugLine="Panel4.Visible = False";
mostCurrent._panel4.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2957;BA.debugLine="Panel41.Visible = False";
mostCurrent._panel41.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2958;BA.debugLine="Panel5.Visible = False";
mostCurrent._panel5.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2959;BA.debugLine="Panel51.Visible = False";
mostCurrent._panel51.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2961;BA.debugLine="imgAnimacion.Visible = False";
mostCurrent._imganimacion.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2962;BA.debugLine="lbl21.Visible = False";
mostCurrent._lbl21.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2964;BA.debugLine="Panel6.visible = False";
mostCurrent._panel6.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2965;BA.debugLine="Panel61.Visible = True";
mostCurrent._panel61.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2966;BA.debugLine="Panel61.Color = Colors.White";
mostCurrent._panel61.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 2967;BA.debugLine="lbl61.textcolor = Get_aTextColor(gi_CombinacionCo";
mostCurrent._lbl61.setTextColor(_get_atextcolor(_gi_combinacioncolores,(int) (0)));
 //BA.debugLineNum = 2970;BA.debugLine="If gb_EsPremium Then";
if (_gb_espremium) { 
 //BA.debugLineNum = 2971;BA.debugLine="lbl61.Text = \"VER HISTORIA\"";
mostCurrent._lbl61.setText((Object)("VER HISTORIA"));
 }else {
 //BA.debugLineNum = 2973;BA.debugLine="lbl61.Text = \"COMPRAR PREMIUM\"";
mostCurrent._lbl61.setText((Object)("COMPRAR PREMIUM"));
 };
 //BA.debugLineNum = 2976;BA.debugLine="lblv2Def.Text = sMensaje";
mostCurrent._lblv2def.setText((Object)(_smensaje));
 //BA.debugLineNum = 2977;BA.debugLine="lblv2Def.TextColor = Get_aTextColor(2,1)";
mostCurrent._lblv2def.setTextColor(_get_atextcolor((int) (2),(int) (1)));
 //BA.debugLineNum = 2978;BA.debugLine="Publicos.SetLabelTextSize(lblv2Def, lblv2Def.TEXT";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv4(mostCurrent.activityBA,mostCurrent._lblv2def,mostCurrent._lblv2def.getText(),(float) (55),(float) (8),(int) (100));
 //BA.debugLineNum = 2979;BA.debugLine="imgLoading.Top = 0";
mostCurrent._imgloading.setTop((int) (0));
 //BA.debugLineNum = 2980;BA.debugLine="imgLoading.Visible = True";
mostCurrent._imgloading.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2981;BA.debugLine="imgLoading.BringToFront";
mostCurrent._imgloading.BringToFront();
 //BA.debugLineNum = 2982;BA.debugLine="Panel2.Top = imgLoading.Top + imgLoading.Height";
mostCurrent._panel2.setTop((int) (mostCurrent._imgloading.getTop()+mostCurrent._imgloading.getHeight()));
 //BA.debugLineNum = 2985;BA.debugLine="pnlInvisible.Visible = False";
mostCurrent._pnlinvisible.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2986;BA.debugLine="pnlHistoria.Visible = False";
mostCurrent._pnlhistoria.setVisible(anywheresoftware.b4a.keywords.Common.False);
 break; }
case 8: {
 //BA.debugLineNum = 2990;BA.debugLine="Productos_Muestra";
_productos_muestra();
 break; }
case 9: {
 //BA.debugLineNum = 2993;BA.debugLine="Productos_MuestraComproNeuronas ( iNeuronas)";
_productos_muestracomproneuronas(_ineuronas);
 break; }
case 10: {
 //BA.debugLineNum = 2996;BA.debugLine="Aviso_Muestra";
_aviso_muestra();
 break; }
case 11: {
 //BA.debugLineNum = 2999;BA.debugLine="Premium_Muestra";
_premium_muestra();
 break; }
case 12: {
 //BA.debugLineNum = 3004;BA.debugLine="Historia_Muestra(\"IGUAL\")";
_historia_muestra("IGUAL");
 break; }
}
;
 //BA.debugLineNum = 3009;BA.debugLine="End Sub";
return "";
}
public static String  _v2_rotapantalla() throws Exception{
anywheresoftware.b4a.objects.PanelWrapper _pnlpanelfondo = null;
anywheresoftware.b4a.objects.ImageViewWrapper _imgscreenshot = null;
 //BA.debugLineNum = 3207;BA.debugLine="Sub V2_RotaPantalla";
 //BA.debugLineNum = 3209;BA.debugLine="Dim pnlPanelFondo As Panel, imgScreenShot As Imag";
_pnlpanelfondo = new anywheresoftware.b4a.objects.PanelWrapper();
_imgscreenshot = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 3211;BA.debugLine="pnlPanelFondo.initialize (\"\")";
_pnlpanelfondo.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 3212;BA.debugLine="pnlPanelFondo.color = Colors.Black";
_pnlpanelfondo.setColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 //BA.debugLineNum = 3213;BA.debugLine="Activity.AddView(pnlPanelFondo, 0,0,Activity.Widt";
mostCurrent._activity.AddView((android.view.View)(_pnlpanelfondo.getObject()),(int) (0),(int) (0),mostCurrent._activity.getWidth(),mostCurrent._activity.getHeight());
 //BA.debugLineNum = 3215;BA.debugLine="imgScreenShot.Initialize(\"\")";
_imgscreenshot.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 3216;BA.debugLine="Activity.AddView(imgScreenShot, 0,0,Activity.Widt";
mostCurrent._activity.AddView((android.view.View)(_imgscreenshot.getObject()),(int) (0),(int) (0),mostCurrent._activity.getWidth(),mostCurrent._activity.getHeight());
 //BA.debugLineNum = 3217;BA.debugLine="imgScreenShot.SetBackgroundImage(LoadBitmap(g_Dir";
_imgscreenshot.SetBackgroundImage((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(_g_dirgrabable,"SSAnimar.png").getObject()));
 //BA.debugLineNum = 3218;BA.debugLine="imgScreenShot.Gravity = Gravity.fill";
_imgscreenshot.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.FILL);
 //BA.debugLineNum = 3220;BA.debugLine="pnlPanelFondo.bringtofront";
_pnlpanelfondo.BringToFront();
 //BA.debugLineNum = 3221;BA.debugLine="Activity.invalidate";
mostCurrent._activity.Invalidate();
 //BA.debugLineNum = 3222;BA.debugLine="DoEvents";
anywheresoftware.b4a.keywords.Common.DoEvents();
 //BA.debugLineNum = 3223;BA.debugLine="imgScreenShot.bringtofront";
_imgscreenshot.BringToFront();
 //BA.debugLineNum = 3225;BA.debugLine="V2_AnimaRotateImageview(imgScreenShot, 0, 360, 20";
_v2_animarotateimageview(_imgscreenshot,(int) (0),(int) (360),(int) (200));
 //BA.debugLineNum = 3227;BA.debugLine="pnlPanelFondo.RemoveView";
_pnlpanelfondo.RemoveView();
 //BA.debugLineNum = 3228;BA.debugLine="imgScreenShot.RemoveView";
_imgscreenshot.RemoveView();
 //BA.debugLineNum = 3229;BA.debugLine="End Sub";
return "";
}
public static String  _v2_set_colorespaneles(int _icombinacion) throws Exception{
 //BA.debugLineNum = 2273;BA.debugLine="Sub V2_Set_ColoresPaneles (iCombinacion As Int)";
 //BA.debugLineNum = 2274;BA.debugLine="Panel1.Color = gt_Color.ColorDefault";
mostCurrent._panel1.setColor(mostCurrent._gt_color.ColorDefault);
 //BA.debugLineNum = 2275;BA.debugLine="Panel11.Color = gt_Color.ColorDefault";
mostCurrent._panel11.setColor(mostCurrent._gt_color.ColorDefault);
 //BA.debugLineNum = 2276;BA.debugLine="Panel2.Color = gt_Color.ColorDefault";
mostCurrent._panel2.setColor(mostCurrent._gt_color.ColorDefault);
 //BA.debugLineNum = 2277;BA.debugLine="Panel21.Color = gt_Color.ColorDefault";
mostCurrent._panel21.setColor(mostCurrent._gt_color.ColorDefault);
 //BA.debugLineNum = 2278;BA.debugLine="Panel3.Color = gt_Color.ColorDefault";
mostCurrent._panel3.setColor(mostCurrent._gt_color.ColorDefault);
 //BA.debugLineNum = 2279;BA.debugLine="Panel4.Color = gt_Color.ColorMedio";
mostCurrent._panel4.setColor(mostCurrent._gt_color.ColorMedio);
 //BA.debugLineNum = 2280;BA.debugLine="Panel41.Color = gt_Color.ColorMedio";
mostCurrent._panel41.setColor(mostCurrent._gt_color.ColorMedio);
 //BA.debugLineNum = 2282;BA.debugLine="Panel5.Color = gt_Color.ColorOscuro";
mostCurrent._panel5.setColor(mostCurrent._gt_color.ColorOscuro);
 //BA.debugLineNum = 2283;BA.debugLine="Panel51.Color =gt_Color.ColorOscuro";
mostCurrent._panel51.setColor(mostCurrent._gt_color.ColorOscuro);
 //BA.debugLineNum = 2284;BA.debugLine="Panel6.Color = gt_Color.ColorDefault";
mostCurrent._panel6.setColor(mostCurrent._gt_color.ColorDefault);
 //BA.debugLineNum = 2285;BA.debugLine="Panel61.Color= gt_Color.ColorDefault";
mostCurrent._panel61.setColor(mostCurrent._gt_color.ColorDefault);
 //BA.debugLineNum = 2289;BA.debugLine="lblV2Nivel.TextColor = gt_Color.ColorTexto";
mostCurrent._lblv2nivel.setTextColor(mostCurrent._gt_color.ColorTexto);
 //BA.debugLineNum = 2290;BA.debugLine="lblNeuronas.textcolor = gt_Color.ColorTexto";
mostCurrent._lblneuronas.setTextColor(mostCurrent._gt_color.ColorTexto);
 //BA.debugLineNum = 2291;BA.debugLine="lblAvance.TextColor = gt_Color.ColorTexto";
mostCurrent._lblavance.setTextColor(mostCurrent._gt_color.ColorTexto);
 //BA.debugLineNum = 2294;BA.debugLine="lblv2Def.TextColor = gt_Color.ColorTexto";
mostCurrent._lblv2def.setTextColor(mostCurrent._gt_color.ColorTexto);
 //BA.debugLineNum = 2297;BA.debugLine="lblPedirLetra.TextColor = gt_Color.ColorTexto";
mostCurrent._lblpedirletra.setTextColor(mostCurrent._gt_color.ColorTexto);
 //BA.debugLineNum = 2298;BA.debugLine="lblSaltarPalabra.TextColor = gt_Color.ColorTexto";
mostCurrent._lblsaltarpalabra.setTextColor(mostCurrent._gt_color.ColorTexto);
 //BA.debugLineNum = 2299;BA.debugLine="lblPedirLetraCosto.TextColor = gt_Color.ColorText";
mostCurrent._lblpedirletracosto.setTextColor(mostCurrent._gt_color.ColorTexto);
 //BA.debugLineNum = 2300;BA.debugLine="lblSaltarPalabraCosto.TextColor = gt_Color.ColorT";
mostCurrent._lblsaltarpalabracosto.setTextColor(mostCurrent._gt_color.ColorTexto);
 //BA.debugLineNum = 2304;BA.debugLine="lblCompartir.Textcolor = gt_Color.ColorTexto";
mostCurrent._lblcompartir.setTextColor(mostCurrent._gt_color.ColorTexto);
 //BA.debugLineNum = 2305;BA.debugLine="lblBajarLetras.TextColor = gt_Color.ColorTexto";
mostCurrent._lblbajarletras.setTextColor(mostCurrent._gt_color.ColorTexto);
 //BA.debugLineNum = 2308;BA.debugLine="lbl11.Textcolor = gt_Color.ColorTexto";
mostCurrent._lbl11.setTextColor(mostCurrent._gt_color.ColorTexto);
 //BA.debugLineNum = 2311;BA.debugLine="lbl21.TextColor = gt_Color.ColorTexto";
mostCurrent._lbl21.setTextColor(mostCurrent._gt_color.ColorTexto);
 //BA.debugLineNum = 2314;BA.debugLine="lbl61.TextColor = gt_Color.ColorTexto";
mostCurrent._lbl61.setTextColor(mostCurrent._gt_color.ColorTexto);
 //BA.debugLineNum = 2316;BA.debugLine="Activity.Color = gt_Color.ColorDefault";
mostCurrent._activity.setColor(mostCurrent._gt_color.ColorDefault);
 //BA.debugLineNum = 2317;BA.debugLine="End Sub";
return "";
}
public static String  _v2_setpantalla() throws Exception{
anywheresoftware.b4a.objects.ConcreteViewWrapper[] _acompartir = null;
int _settextsizelblscompartircancelar = 0;
 //BA.debugLineNum = 1811;BA.debugLine="Sub V2_SetPantalla";
 //BA.debugLineNum = 1812;BA.debugLine="Publicos.ViewComoActivity(Activity, pnlInvisible)";
mostCurrent._vvvvvvvvvvvvvvv7._vvvvv1(mostCurrent.activityBA,mostCurrent._activity,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._pnlinvisible.getObject())));
 //BA.debugLineNum = 1813;BA.debugLine="pnlInvisible.Visible = False";
mostCurrent._pnlinvisible.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1816;BA.debugLine="Panel1.Top = 0";
mostCurrent._panel1.setTop((int) (0));
 //BA.debugLineNum = 1819;BA.debugLine="If gb_EsPremium Then";
if (_gb_espremium) { 
 //BA.debugLineNum = 1820;BA.debugLine="Panel1.Height = Activity.Height * 0.14 'Panel qu";
mostCurrent._panel1.setHeight((int) (mostCurrent._activity.getHeight()*0.14));
 //BA.debugLineNum = 1821;BA.debugLine="Panel2.Top = Panel1.Top + Panel1.Height";
mostCurrent._panel2.setTop((int) (mostCurrent._panel1.getTop()+mostCurrent._panel1.getHeight()));
 //BA.debugLineNum = 1822;BA.debugLine="Panel2.Height = Activity.Height* 0.25 ' panel qu";
mostCurrent._panel2.setHeight((int) (mostCurrent._activity.getHeight()*0.25));
 //BA.debugLineNum = 1823;BA.debugLine="Panel3.Top = Panel2.Top + Panel2.Height ' panel";
mostCurrent._panel3.setTop((int) (mostCurrent._panel2.getTop()+mostCurrent._panel2.getHeight()));
 //BA.debugLineNum = 1824;BA.debugLine="Panel3.Height = Activity.Height* 0.11";
mostCurrent._panel3.setHeight((int) (mostCurrent._activity.getHeight()*0.11));
 //BA.debugLineNum = 1825;BA.debugLine="Panel4.Top = Panel3.Top + Panel3.Height ' panel";
mostCurrent._panel4.setTop((int) (mostCurrent._panel3.getTop()+mostCurrent._panel3.getHeight()));
 //BA.debugLineNum = 1826;BA.debugLine="Panel4.Height = Activity.Height* 0.11";
mostCurrent._panel4.setHeight((int) (mostCurrent._activity.getHeight()*0.11));
 //BA.debugLineNum = 1827;BA.debugLine="Panel5.Top = Panel4.Top + Panel4.Height ' panel";
mostCurrent._panel5.setTop((int) (mostCurrent._panel4.getTop()+mostCurrent._panel4.getHeight()));
 //BA.debugLineNum = 1828;BA.debugLine="Panel5.Height = Activity.Height* 0.28";
mostCurrent._panel5.setHeight((int) (mostCurrent._activity.getHeight()*0.28));
 //BA.debugLineNum = 1829;BA.debugLine="Panel6.Top = Panel5.Top + Panel5.Height ' panel";
mostCurrent._panel6.setTop((int) (mostCurrent._panel5.getTop()+mostCurrent._panel5.getHeight()));
 //BA.debugLineNum = 1830;BA.debugLine="Panel6.Height = Activity.Height* 0.11";
mostCurrent._panel6.setHeight((int) (mostCurrent._activity.getHeight()*0.11));
 }else {
 //BA.debugLineNum = 1832;BA.debugLine="Panel1.Height = Activity.Height * 0.14 'Panel qu";
mostCurrent._panel1.setHeight((int) (mostCurrent._activity.getHeight()*0.14));
 //BA.debugLineNum = 1833;BA.debugLine="Panel2.Top = Panel1.Top + Panel1.Height";
mostCurrent._panel2.setTop((int) (mostCurrent._panel1.getTop()+mostCurrent._panel1.getHeight()));
 //BA.debugLineNum = 1834;BA.debugLine="Panel2.Height = Activity.Height* 0.21 ' panel qu";
mostCurrent._panel2.setHeight((int) (mostCurrent._activity.getHeight()*0.21));
 //BA.debugLineNum = 1835;BA.debugLine="Panel3.Top = Panel2.Top + Panel2.Height ' panel";
mostCurrent._panel3.setTop((int) (mostCurrent._panel2.getTop()+mostCurrent._panel2.getHeight()));
 //BA.debugLineNum = 1836;BA.debugLine="Panel3.Height = Activity.Height* 0.1";
mostCurrent._panel3.setHeight((int) (mostCurrent._activity.getHeight()*0.1));
 //BA.debugLineNum = 1837;BA.debugLine="Panel4.Top = Panel3.Top + Panel3.Height ' panel";
mostCurrent._panel4.setTop((int) (mostCurrent._panel3.getTop()+mostCurrent._panel3.getHeight()));
 //BA.debugLineNum = 1838;BA.debugLine="Panel4.Height = Activity.Height* 0.1";
mostCurrent._panel4.setHeight((int) (mostCurrent._activity.getHeight()*0.1));
 //BA.debugLineNum = 1839;BA.debugLine="Panel5.Top = Panel4.Top + Panel4.Height ' panel";
mostCurrent._panel5.setTop((int) (mostCurrent._panel4.getTop()+mostCurrent._panel4.getHeight()));
 //BA.debugLineNum = 1840;BA.debugLine="Panel5.Height = Activity.Height* 0.25";
mostCurrent._panel5.setHeight((int) (mostCurrent._activity.getHeight()*0.25));
 //BA.debugLineNum = 1841;BA.debugLine="Panel6.Top = Panel5.Top + Panel5.Height ' panel";
mostCurrent._panel6.setTop((int) (mostCurrent._panel5.getTop()+mostCurrent._panel5.getHeight()));
 //BA.debugLineNum = 1842;BA.debugLine="Panel6.Height = Activity.Height* 0.1";
mostCurrent._panel6.setHeight((int) (mostCurrent._activity.getHeight()*0.1));
 };
 //BA.debugLineNum = 1844;BA.debugLine="Panel1.Left = 0: Panel2.left = 0 : Panel3.left =";
mostCurrent._panel1.setLeft((int) (0));
 //BA.debugLineNum = 1844;BA.debugLine="Panel1.Left = 0: Panel2.left = 0 : Panel3.left =";
mostCurrent._panel2.setLeft((int) (0));
 //BA.debugLineNum = 1844;BA.debugLine="Panel1.Left = 0: Panel2.left = 0 : Panel3.left =";
mostCurrent._panel3.setLeft((int) (0));
 //BA.debugLineNum = 1844;BA.debugLine="Panel1.Left = 0: Panel2.left = 0 : Panel3.left =";
mostCurrent._panel4.setLeft((int) (0));
 //BA.debugLineNum = 1844;BA.debugLine="Panel1.Left = 0: Panel2.left = 0 : Panel3.left =";
mostCurrent._panel5.setLeft((int) (0));
 //BA.debugLineNum = 1844;BA.debugLine="Panel1.Left = 0: Panel2.left = 0 : Panel3.left =";
mostCurrent._panel6.setLeft((int) (0));
 //BA.debugLineNum = 1845;BA.debugLine="Panel1.width = Activity.Width: Panel2.width = Act";
mostCurrent._panel1.setWidth(mostCurrent._activity.getWidth());
 //BA.debugLineNum = 1845;BA.debugLine="Panel1.width = Activity.Width: Panel2.width = Act";
mostCurrent._panel2.setWidth(mostCurrent._activity.getWidth());
 //BA.debugLineNum = 1845;BA.debugLine="Panel1.width = Activity.Width: Panel2.width = Act";
mostCurrent._panel3.setWidth(mostCurrent._activity.getWidth());
 //BA.debugLineNum = 1846;BA.debugLine="Panel4.width = Activity.Width: Panel5.width = Act";
mostCurrent._panel4.setWidth(mostCurrent._activity.getWidth());
 //BA.debugLineNum = 1846;BA.debugLine="Panel4.width = Activity.Width: Panel5.width = Act";
mostCurrent._panel5.setWidth(mostCurrent._activity.getWidth());
 //BA.debugLineNum = 1846;BA.debugLine="Panel4.width = Activity.Width: Panel5.width = Act";
mostCurrent._panel6.setWidth(mostCurrent._activity.getWidth());
 //BA.debugLineNum = 1852;BA.debugLine="imgMenu.Height = Panel1.height*0.4";
mostCurrent._imgmenu.setHeight((int) (mostCurrent._panel1.getHeight()*0.4));
 //BA.debugLineNum = 1853;BA.debugLine="imgMenu.Width = imgMenu.Height";
mostCurrent._imgmenu.setWidth(mostCurrent._imgmenu.getHeight());
 //BA.debugLineNum = 1854;BA.debugLine="imgMenu.left = Panel1.Width * 0.05";
mostCurrent._imgmenu.setLeft((int) (mostCurrent._panel1.getWidth()*0.05));
 //BA.debugLineNum = 1855;BA.debugLine="imgMenu.Top = Panel1.Width * 0.05";
mostCurrent._imgmenu.setTop((int) (mostCurrent._panel1.getWidth()*0.05));
 //BA.debugLineNum = 1857;BA.debugLine="imgAvance.Left = imgMenu.Left";
mostCurrent._imgavance.setLeft(mostCurrent._imgmenu.getLeft());
 //BA.debugLineNum = 1858;BA.debugLine="imgAvance.Top = imgMenu.Top + imgMenu.Height + 2d";
mostCurrent._imgavance.setTop((int) (mostCurrent._imgmenu.getTop()+mostCurrent._imgmenu.getHeight()+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (2))));
 //BA.debugLineNum = 1859;BA.debugLine="imgAvance.Height = imgMenu.Height";
mostCurrent._imgavance.setHeight(mostCurrent._imgmenu.getHeight());
 //BA.debugLineNum = 1860;BA.debugLine="imgAvance.Width = imgAvance.Height /2";
mostCurrent._imgavance.setWidth((int) (mostCurrent._imgavance.getHeight()/(double)2));
 //BA.debugLineNum = 1862;BA.debugLine="lblAvance.Left = imgAvance.Left + imgAvance.Width";
mostCurrent._lblavance.setLeft((int) (mostCurrent._imgavance.getLeft()+mostCurrent._imgavance.getWidth()));
 //BA.debugLineNum = 1863;BA.debugLine="lblAvance.Width = imgMenu.Width * 3";
mostCurrent._lblavance.setWidth((int) (mostCurrent._imgmenu.getWidth()*3));
 //BA.debugLineNum = 1864;BA.debugLine="lblAvance.Height = Panel1.Height * 0.2";
mostCurrent._lblavance.setHeight((int) (mostCurrent._panel1.getHeight()*0.2));
 //BA.debugLineNum = 1865;BA.debugLine="lblAvance.Top = imgAvance.Top + lblAvance.Height";
mostCurrent._lblavance.setTop((int) (mostCurrent._imgavance.getTop()+mostCurrent._lblavance.getHeight()/(double)2));
 //BA.debugLineNum = 1866;BA.debugLine="lblAvance.Gravity = Gravity.LEFT + Gravity.CENTER";
mostCurrent._lblavance.setGravity((int) (anywheresoftware.b4a.keywords.Common.Gravity.LEFT+anywheresoftware.b4a.keywords.Common.Gravity.CENTER_VERTICAL));
 //BA.debugLineNum = 1867;BA.debugLine="Publicos.SetLabelTextSize(lblAvance, \"999/999\", 2";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv4(mostCurrent.activityBA,mostCurrent._lblavance,"999/999",(float) (20),(float) (5),(int) (100));
 //BA.debugLineNum = 1869;BA.debugLine="lblAvance.BringToFront";
mostCurrent._lblavance.BringToFront();
 //BA.debugLineNum = 1872;BA.debugLine="lblV2Nivel.Typeface = tFontOpenSansSemiBold";
mostCurrent._lblv2nivel.setTypeface((android.graphics.Typeface)(_vvvvvvvvvv4.getObject()));
 //BA.debugLineNum = 1873;BA.debugLine="lblV2Nivel.height = Panel1.Height*0.4";
mostCurrent._lblv2nivel.setHeight((int) (mostCurrent._panel1.getHeight()*0.4));
 //BA.debugLineNum = 1874;BA.debugLine="lblV2Nivel.Width = Activity.Width";
mostCurrent._lblv2nivel.setWidth(mostCurrent._activity.getWidth());
 //BA.debugLineNum = 1875;BA.debugLine="lblV2Nivel.Top = imgNeuronas.Top";
mostCurrent._lblv2nivel.setTop(mostCurrent._imgneuronas.getTop());
 //BA.debugLineNum = 1876;BA.debugLine="lblV2Nivel.Gravity = Gravity.CENTER";
mostCurrent._lblv2nivel.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 1877;BA.debugLine="lblV2Nivel.Text = \"MATETE DIVERGENTE\" & Chr(10) &";
mostCurrent._lblv2nivel.setText((Object)("MATETE DIVERGENTE"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"NIVEL MUY LARGO QUE OCUPA"));
 //BA.debugLineNum = 1878;BA.debugLine="Publicos.SetLabelTextSize(lblV2Nivel, lblV2Nivel.";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv4(mostCurrent.activityBA,mostCurrent._lblv2nivel,mostCurrent._lblv2nivel.getText(),(float) (20),(float) (5),(int) (100));
 //BA.debugLineNum = 1879;BA.debugLine="lblV2Nivel.Left = 0";
mostCurrent._lblv2nivel.setLeft((int) (0));
 //BA.debugLineNum = 1882;BA.debugLine="imgNeuronas.Height = Panel1.Height*0.4";
mostCurrent._imgneuronas.setHeight((int) (mostCurrent._panel1.getHeight()*0.4));
 //BA.debugLineNum = 1883;BA.debugLine="imgNeuronas.Width = imgNeuronas.Height";
mostCurrent._imgneuronas.setWidth(mostCurrent._imgneuronas.getHeight());
 //BA.debugLineNum = 1884;BA.debugLine="imgNeuronas.Left = Panel1.Width - imgNeuronas.Wid";
mostCurrent._imgneuronas.setLeft((int) (mostCurrent._panel1.getWidth()-mostCurrent._imgneuronas.getWidth()-mostCurrent._panel1.getWidth()*0.05));
 //BA.debugLineNum = 1885;BA.debugLine="imgNeuronas.Top = Panel1.Width * 0.05";
mostCurrent._imgneuronas.setTop((int) (mostCurrent._panel1.getWidth()*0.05));
 //BA.debugLineNum = 1886;BA.debugLine="lblNeuronas.Left = imgNeuronas.Left-imgNeuronas.W";
mostCurrent._lblneuronas.setLeft((int) (mostCurrent._imgneuronas.getLeft()-mostCurrent._imgneuronas.getWidth()*0.1));
 //BA.debugLineNum = 1887;BA.debugLine="lblNeuronas.Width = imgNeuronas.Width*1.2";
mostCurrent._lblneuronas.setWidth((int) (mostCurrent._imgneuronas.getWidth()*1.2));
 //BA.debugLineNum = 1888;BA.debugLine="lblNeuronas.Top = imgNeuronas.Top + imgNeuronas.H";
mostCurrent._lblneuronas.setTop((int) (mostCurrent._imgneuronas.getTop()+mostCurrent._imgneuronas.getHeight()+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (2))));
 //BA.debugLineNum = 1889;BA.debugLine="lblNeuronas.Height = Panel1.Height * 0.2";
mostCurrent._lblneuronas.setHeight((int) (mostCurrent._panel1.getHeight()*0.2));
 //BA.debugLineNum = 1890;BA.debugLine="lblNeuronas.Text = \"99999\"";
mostCurrent._lblneuronas.setText((Object)("99999"));
 //BA.debugLineNum = 1891;BA.debugLine="lblNeuronas.Typeface = tFontOpenSansSemiBold";
mostCurrent._lblneuronas.setTypeface((android.graphics.Typeface)(_vvvvvvvvvv4.getObject()));
 //BA.debugLineNum = 1892;BA.debugLine="lblNeuronas.textsize = lblAvance.textsize";
mostCurrent._lblneuronas.setTextSize(mostCurrent._lblavance.getTextSize());
 //BA.debugLineNum = 1894;BA.debugLine="lblNeuronas.Gravity = Gravity.CENTER_HORIZONTAL +";
mostCurrent._lblneuronas.setGravity((int) (anywheresoftware.b4a.keywords.Common.Gravity.CENTER_HORIZONTAL+anywheresoftware.b4a.keywords.Common.Gravity.TOP));
 //BA.debugLineNum = 1900;BA.debugLine="lblv2Def.Top = 0: lblv2Def.Height = Panel2.Height";
mostCurrent._lblv2def.setTop((int) (0));
 //BA.debugLineNum = 1900;BA.debugLine="lblv2Def.Top = 0: lblv2Def.Height = Panel2.Height";
mostCurrent._lblv2def.setHeight(mostCurrent._panel2.getHeight());
 //BA.debugLineNum = 1901;BA.debugLine="lblv2Def.Width = Panel2.Width*0.8";
mostCurrent._lblv2def.setWidth((int) (mostCurrent._panel2.getWidth()*0.8));
 //BA.debugLineNum = 1902;BA.debugLine="lblv2Def.Left = Panel2.Width * 0.1";
mostCurrent._lblv2def.setLeft((int) (mostCurrent._panel2.getWidth()*0.1));
 //BA.debugLineNum = 1904;BA.debugLine="lblv2Def.Typeface = tFontOpenSansLight";
mostCurrent._lblv2def.setTypeface((android.graphics.Typeface)(_vvvvvvvvvv5.getObject()));
 //BA.debugLineNum = 1906;BA.debugLine="lblv2Def.TextColor =Colors.black";
mostCurrent._lblv2def.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 //BA.debugLineNum = 1907;BA.debugLine="lblv2Def.Gravity = Gravity.CENTER";
mostCurrent._lblv2def.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 1910;BA.debugLine="imgPedirLetra.Height=Panel6.Height*0.5";
mostCurrent._imgpedirletra.setHeight((int) (mostCurrent._panel6.getHeight()*0.5));
 //BA.debugLineNum = 1911;BA.debugLine="imgPedirLetra.Width=imgPedirLetra.Height";
mostCurrent._imgpedirletra.setWidth(mostCurrent._imgpedirletra.getHeight());
 //BA.debugLineNum = 1912;BA.debugLine="imgPedirLetra.Top= (Panel3.Height - imgPedirLetra";
mostCurrent._imgpedirletra.setTop((int) ((mostCurrent._panel3.getHeight()-mostCurrent._imgpedirletra.getHeight())/(double)2));
 //BA.debugLineNum = 1913;BA.debugLine="imgPedirLetra.left = Panel3.Width * 0.05";
mostCurrent._imgpedirletra.setLeft((int) (mostCurrent._panel3.getWidth()*0.05));
 //BA.debugLineNum = 1915;BA.debugLine="lblPedirLetraCosto.Height = imgPedirLetra.Height*";
mostCurrent._lblpedirletracosto.setHeight((int) (mostCurrent._imgpedirletra.getHeight()*0.6));
 //BA.debugLineNum = 1916;BA.debugLine="lblPedirLetraCosto.Width = lblPedirLetraCosto.Hei";
mostCurrent._lblpedirletracosto.setWidth(mostCurrent._lblpedirletracosto.getHeight());
 //BA.debugLineNum = 1917;BA.debugLine="lblPedirLetraCosto.Top = imgPedirLetra.Top - imgP";
mostCurrent._lblpedirletracosto.setTop((int) (mostCurrent._imgpedirletra.getTop()-mostCurrent._imgpedirletra.getTop()*0.2));
 //BA.debugLineNum = 1918;BA.debugLine="lblPedirLetraCosto.Left = imgPedirLetra.Left - lb";
mostCurrent._lblpedirletracosto.setLeft((int) (mostCurrent._imgpedirletra.getLeft()-mostCurrent._lblpedirletracosto.getWidth()/(double)2));
 //BA.debugLineNum = 1919;BA.debugLine="lblPedirLetraCosto.TextColor = lblv2Def.textcolor";
mostCurrent._lblpedirletracosto.setTextColor(mostCurrent._lblv2def.getTextColor());
 //BA.debugLineNum = 1920;BA.debugLine="lblPedirLetraCosto.Typeface = tFontOpenSansSemiBo";
mostCurrent._lblpedirletracosto.setTypeface((android.graphics.Typeface)(_vvvvvvvvvv4.getObject()));
 //BA.debugLineNum = 1921;BA.debugLine="lblPedirLetraCosto.Gravity = Gravity.CENTER";
mostCurrent._lblpedirletracosto.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 1923;BA.debugLine="lblPedirLetra.Left = imgPedirLetra.Left + imgPedi";
mostCurrent._lblpedirletra.setLeft((int) (mostCurrent._imgpedirletra.getLeft()+mostCurrent._imgpedirletra.getWidth()+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (3))));
 //BA.debugLineNum = 1924;BA.debugLine="lblPedirLetra.Height = imgPedirLetra.Height / 1.6";
mostCurrent._lblpedirletra.setHeight((int) (mostCurrent._imgpedirletra.getHeight()/(double)1.6));
 //BA.debugLineNum = 1925;BA.debugLine="lblPedirLetra.Top = imgPedirLetra.Top + (imgPedir";
mostCurrent._lblpedirletra.setTop((int) (mostCurrent._imgpedirletra.getTop()+(mostCurrent._imgpedirletra.getHeight()/(double)2)-(mostCurrent._lblpedirletra.getHeight()/(double)2)));
 //BA.debugLineNum = 1926;BA.debugLine="lblPedirLetra.Width = Activity.Width /3.5";
mostCurrent._lblpedirletra.setWidth((int) (mostCurrent._activity.getWidth()/(double)3.5));
 //BA.debugLineNum = 1927;BA.debugLine="lblPedirLetra.Text = \"COMPRAR LETRA\"";
mostCurrent._lblpedirletra.setText((Object)("COMPRAR LETRA"));
 //BA.debugLineNum = 1928;BA.debugLine="lblPedirLetra.Typeface = tFontOpenSansSemiBold";
mostCurrent._lblpedirletra.setTypeface((android.graphics.Typeface)(_vvvvvvvvvv4.getObject()));
 //BA.debugLineNum = 1932;BA.debugLine="lblPedirLetra.Gravity = Gravity.CENTER_VERTICAL +";
mostCurrent._lblpedirletra.setGravity((int) (anywheresoftware.b4a.keywords.Common.Gravity.CENTER_VERTICAL+anywheresoftware.b4a.keywords.Common.Gravity.LEFT));
 //BA.debugLineNum = 1933;BA.debugLine="lblPedirLetra.TextColor = lblv2Def.textcolor";
mostCurrent._lblpedirletra.setTextColor(mostCurrent._lblv2def.getTextColor());
 //BA.debugLineNum = 1935;BA.debugLine="imgSaltarPalabra.Height=imgPedirLetra.Height";
mostCurrent._imgsaltarpalabra.setHeight(mostCurrent._imgpedirletra.getHeight());
 //BA.debugLineNum = 1936;BA.debugLine="imgSaltarPalabra.Width=imgSaltarPalabra.Height";
mostCurrent._imgsaltarpalabra.setWidth(mostCurrent._imgsaltarpalabra.getHeight());
 //BA.debugLineNum = 1937;BA.debugLine="imgSaltarPalabra.Top= (Panel3.Height - imgPedirLe";
mostCurrent._imgsaltarpalabra.setTop((int) ((mostCurrent._panel3.getHeight()-mostCurrent._imgpedirletra.getHeight())/(double)2));
 //BA.debugLineNum = 1938;BA.debugLine="imgSaltarPalabra.left = Panel3.Width - imgPedirLe";
mostCurrent._imgsaltarpalabra.setLeft((int) (mostCurrent._panel3.getWidth()-mostCurrent._imgpedirletra.getLeft()-mostCurrent._imgsaltarpalabra.getWidth()));
 //BA.debugLineNum = 1940;BA.debugLine="lblSaltarPalabraCosto.Height = imgSaltarPalabra.H";
mostCurrent._lblsaltarpalabracosto.setHeight((int) (mostCurrent._imgsaltarpalabra.getHeight()*0.6));
 //BA.debugLineNum = 1941;BA.debugLine="lblSaltarPalabraCosto.Width =lblSaltarPalabraCost";
mostCurrent._lblsaltarpalabracosto.setWidth(mostCurrent._lblsaltarpalabracosto.getHeight());
 //BA.debugLineNum = 1942;BA.debugLine="lblSaltarPalabraCosto.Top = imgSaltarPalabra.Top";
mostCurrent._lblsaltarpalabracosto.setTop((int) (mostCurrent._imgsaltarpalabra.getTop()-mostCurrent._imgsaltarpalabra.getTop()*0.2));
 //BA.debugLineNum = 1943;BA.debugLine="lblSaltarPalabraCosto.Left = imgSaltarPalabra.Lef";
mostCurrent._lblsaltarpalabracosto.setLeft((int) (mostCurrent._imgsaltarpalabra.getLeft()+mostCurrent._imgsaltarpalabra.getWidth()-mostCurrent._lblsaltarpalabracosto.getWidth()/(double)2));
 //BA.debugLineNum = 1944;BA.debugLine="lblSaltarPalabraCosto.TextColor = lblv2Def.textco";
mostCurrent._lblsaltarpalabracosto.setTextColor(mostCurrent._lblv2def.getTextColor());
 //BA.debugLineNum = 1945;BA.debugLine="lblSaltarPalabraCosto.Typeface = tFontOpenSansSem";
mostCurrent._lblsaltarpalabracosto.setTypeface((android.graphics.Typeface)(_vvvvvvvvvv4.getObject()));
 //BA.debugLineNum = 1946;BA.debugLine="lblSaltarPalabraCosto.Gravity = Gravity.CENTER";
mostCurrent._lblsaltarpalabracosto.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 1948;BA.debugLine="lblSaltarPalabra.Height = lblPedirLetra.Height";
mostCurrent._lblsaltarpalabra.setHeight(mostCurrent._lblpedirletra.getHeight());
 //BA.debugLineNum = 1949;BA.debugLine="lblSaltarPalabra.Width = lblPedirLetra.width";
mostCurrent._lblsaltarpalabra.setWidth(mostCurrent._lblpedirletra.getWidth());
 //BA.debugLineNum = 1950;BA.debugLine="lblSaltarPalabra.Left = imgSaltarPalabra.Left - l";
mostCurrent._lblsaltarpalabra.setLeft((int) (mostCurrent._imgsaltarpalabra.getLeft()-mostCurrent._lblsaltarpalabra.getWidth()-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (3))));
 //BA.debugLineNum = 1951;BA.debugLine="lblSaltarPalabra.Text = \"SALTAR MATETE\"";
mostCurrent._lblsaltarpalabra.setText((Object)("SALTAR MATETE"));
 //BA.debugLineNum = 1952;BA.debugLine="lblSaltarPalabra.Gravity = Gravity.CENTER_VERTICA";
mostCurrent._lblsaltarpalabra.setGravity((int) (anywheresoftware.b4a.keywords.Common.Gravity.CENTER_VERTICAL+anywheresoftware.b4a.keywords.Common.Gravity.RIGHT));
 //BA.debugLineNum = 1953;BA.debugLine="lblSaltarPalabra.Top = lblPedirLetra.Top";
mostCurrent._lblsaltarpalabra.setTop(mostCurrent._lblpedirletra.getTop());
 //BA.debugLineNum = 1954;BA.debugLine="lblSaltarPalabra.Typeface = tFontOpenSansSemiBold";
mostCurrent._lblsaltarpalabra.setTypeface((android.graphics.Typeface)(_vvvvvvvvvv4.getObject()));
 //BA.debugLineNum = 1956;BA.debugLine="lblSaltarPalabra.TextColor = lblv2Def.textcolor";
mostCurrent._lblsaltarpalabra.setTextColor(mostCurrent._lblv2def.getTextColor());
 //BA.debugLineNum = 1964;BA.debugLine="imgCompartir.height = Panel6.Height*0.5";
mostCurrent._imgcompartir.setHeight((int) (mostCurrent._panel6.getHeight()*0.5));
 //BA.debugLineNum = 1965;BA.debugLine="imgCompartir.Width = imgCompartir.height";
mostCurrent._imgcompartir.setWidth(mostCurrent._imgcompartir.getHeight());
 //BA.debugLineNum = 1966;BA.debugLine="imgCompartir.Top = (Panel6.Height -  imgCompartir";
mostCurrent._imgcompartir.setTop((int) ((mostCurrent._panel6.getHeight()-mostCurrent._imgcompartir.getHeight())/(double)2));
 //BA.debugLineNum = 1967;BA.debugLine="imgCompartir.Left = Panel6.Width * 0.05";
mostCurrent._imgcompartir.setLeft((int) (mostCurrent._panel6.getWidth()*0.05));
 //BA.debugLineNum = 1969;BA.debugLine="lblCompartir.Left = imgCompartir.Left + imgCompar";
mostCurrent._lblcompartir.setLeft((int) (mostCurrent._imgcompartir.getLeft()+mostCurrent._imgcompartir.getWidth()+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (3))));
 //BA.debugLineNum = 1970;BA.debugLine="lblCompartir.Height = lblPedirLetra.Height";
mostCurrent._lblcompartir.setHeight(mostCurrent._lblpedirletra.getHeight());
 //BA.debugLineNum = 1971;BA.debugLine="lblCompartir.Top = imgCompartir.Top + (imgCompart";
mostCurrent._lblcompartir.setTop((int) (mostCurrent._imgcompartir.getTop()+(mostCurrent._imgcompartir.getHeight()/(double)2)-(mostCurrent._lblcompartir.getHeight()/(double)2)));
 //BA.debugLineNum = 1972;BA.debugLine="lblCompartir.Width = lblPedirLetra.width";
mostCurrent._lblcompartir.setWidth(mostCurrent._lblpedirletra.getWidth());
 //BA.debugLineNum = 1973;BA.debugLine="lblCompartir.Text = \"COMPARTIR\"";
mostCurrent._lblcompartir.setText((Object)("COMPARTIR"));
 //BA.debugLineNum = 1974;BA.debugLine="lblCompartir.Typeface = tFontOpenSansSemiBold";
mostCurrent._lblcompartir.setTypeface((android.graphics.Typeface)(_vvvvvvvvvv4.getObject()));
 //BA.debugLineNum = 1976;BA.debugLine="lblCompartir.Gravity = Gravity.CENTER_VERTICAL +";
mostCurrent._lblcompartir.setGravity((int) (anywheresoftware.b4a.keywords.Common.Gravity.CENTER_VERTICAL+anywheresoftware.b4a.keywords.Common.Gravity.LEFT));
 //BA.debugLineNum = 1977;BA.debugLine="lblCompartir.TextColor = lblv2Def.textcolor";
mostCurrent._lblcompartir.setTextColor(mostCurrent._lblv2def.getTextColor());
 //BA.debugLineNum = 1979;BA.debugLine="imgBajarLetras.Height=imgCompartir.Height";
mostCurrent._imgbajarletras.setHeight(mostCurrent._imgcompartir.getHeight());
 //BA.debugLineNum = 1980;BA.debugLine="imgBajarLetras.Width=imgCompartir.Height";
mostCurrent._imgbajarletras.setWidth(mostCurrent._imgcompartir.getHeight());
 //BA.debugLineNum = 1981;BA.debugLine="imgBajarLetras.Top= (Panel3.Height - imgCompartir";
mostCurrent._imgbajarletras.setTop((int) ((mostCurrent._panel3.getHeight()-mostCurrent._imgcompartir.getHeight())/(double)2));
 //BA.debugLineNum = 1982;BA.debugLine="imgBajarLetras.left = Panel3.Width - imgCompartir";
mostCurrent._imgbajarletras.setLeft((int) (mostCurrent._panel3.getWidth()-mostCurrent._imgcompartir.getLeft()-mostCurrent._imgcompartir.getWidth()));
 //BA.debugLineNum = 1985;BA.debugLine="lblBajarLetras.Height = lblCompartir.Height";
mostCurrent._lblbajarletras.setHeight(mostCurrent._lblcompartir.getHeight());
 //BA.debugLineNum = 1986;BA.debugLine="lblBajarLetras.Width = lblPedirLetra.width";
mostCurrent._lblbajarletras.setWidth(mostCurrent._lblpedirletra.getWidth());
 //BA.debugLineNum = 1987;BA.debugLine="lblBajarLetras.Left = imgBajarLetras.Left - lblBa";
mostCurrent._lblbajarletras.setLeft((int) (mostCurrent._imgbajarletras.getLeft()-mostCurrent._lblbajarletras.getWidth()-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (3))));
 //BA.debugLineNum = 1988;BA.debugLine="lblBajarLetras.Text = \"VACIAR PALABRA\"";
mostCurrent._lblbajarletras.setText((Object)("VACIAR PALABRA"));
 //BA.debugLineNum = 1989;BA.debugLine="lblBajarLetras.Gravity = Gravity.CENTER_VERTICAL";
mostCurrent._lblbajarletras.setGravity((int) (anywheresoftware.b4a.keywords.Common.Gravity.CENTER_VERTICAL+anywheresoftware.b4a.keywords.Common.Gravity.RIGHT));
 //BA.debugLineNum = 1990;BA.debugLine="lblBajarLetras.Top = lblCompartir.Top";
mostCurrent._lblbajarletras.setTop(mostCurrent._lblcompartir.getTop());
 //BA.debugLineNum = 1991;BA.debugLine="lblBajarLetras.Typeface = tFontOpenSansSemiBold";
mostCurrent._lblbajarletras.setTypeface((android.graphics.Typeface)(_vvvvvvvvvv4.getObject()));
 //BA.debugLineNum = 1993;BA.debugLine="lblBajarLetras.TextColor = lblCompartir.textcolor";
mostCurrent._lblbajarletras.setTextColor(mostCurrent._lblcompartir.getTextColor());
 //BA.debugLineNum = 2003;BA.debugLine="Publicos.viewigual(Panel1, Panel11)";
mostCurrent._vvvvvvvvvvvvvvv7._vvvvv2(mostCurrent.activityBA,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._panel1.getObject())),(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._panel11.getObject())));
 //BA.debugLineNum = 2004;BA.debugLine="Panel11.Height = Panel6.Height";
mostCurrent._panel11.setHeight(mostCurrent._panel6.getHeight());
 //BA.debugLineNum = 2005;BA.debugLine="lbl11.Height = Panel11.Height * 0.6";
mostCurrent._lbl11.setHeight((int) (mostCurrent._panel11.getHeight()*0.6));
 //BA.debugLineNum = 2006;BA.debugLine="lbl11.Width = Panel11.Width";
mostCurrent._lbl11.setWidth(mostCurrent._panel11.getWidth());
 //BA.debugLineNum = 2007;BA.debugLine="lbl11.Typeface = tFontOpenSansSemiBold";
mostCurrent._lbl11.setTypeface((android.graphics.Typeface)(_vvvvvvvvvv4.getObject()));
 //BA.debugLineNum = 2009;BA.debugLine="Publicos.CentrarControlEnPanel(Panel11, lbl11, Tr";
mostCurrent._vvvvvvvvvvvvvvv7._vv0(mostCurrent.activityBA,mostCurrent._panel11,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._lbl11.getObject())),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2012;BA.debugLine="Publicos.ViewIgual(Panel2, Panel21)";
mostCurrent._vvvvvvvvvvvvvvv7._vvvvv2(mostCurrent.activityBA,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._panel2.getObject())),(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._panel21.getObject())));
 //BA.debugLineNum = 2014;BA.debugLine="Publicos.CentrarControlEnPanel(Panel21, lbl21, Tr";
mostCurrent._vvvvvvvvvvvvvvv7._vv0(mostCurrent.activityBA,mostCurrent._panel21,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._lbl21.getObject())),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2015;BA.debugLine="imgAnimacion.Height = Panel21.Height * 0.6";
mostCurrent._imganimacion.setHeight((int) (mostCurrent._panel21.getHeight()*0.6));
 //BA.debugLineNum = 2016;BA.debugLine="imgAnimacion.Width = imgAnimacion.Height";
mostCurrent._imganimacion.setWidth(mostCurrent._imganimacion.getHeight());
 //BA.debugLineNum = 2017;BA.debugLine="imgAnimacion.Visible = False";
mostCurrent._imganimacion.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2018;BA.debugLine="Publicos.CentrarControlEnPanel(Panel21, imgAnimac";
mostCurrent._vvvvvvvvvvvvvvv7._vv0(mostCurrent.activityBA,mostCurrent._panel21,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._imganimacion.getObject())),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2019;BA.debugLine="lbl21.Top = imgAnimacion.Top + imgAnimacion.Heigh";
mostCurrent._lbl21.setTop((int) (mostCurrent._imganimacion.getTop()+mostCurrent._imganimacion.getHeight()+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (2))));
 //BA.debugLineNum = 2020;BA.debugLine="lbl21.Height = Panel21.Height * 0.2";
mostCurrent._lbl21.setHeight((int) (mostCurrent._panel21.getHeight()*0.2));
 //BA.debugLineNum = 2021;BA.debugLine="lbl21.Width = Panel21.Width*0.8";
mostCurrent._lbl21.setWidth((int) (mostCurrent._panel21.getWidth()*0.8));
 //BA.debugLineNum = 2022;BA.debugLine="lbl21.Left = Panel21.Width * 0.1";
mostCurrent._lbl21.setLeft((int) (mostCurrent._panel21.getWidth()*0.1));
 //BA.debugLineNum = 2023;BA.debugLine="lbl21.Gravity = Gravity.CENTER";
mostCurrent._lbl21.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 2025;BA.debugLine="Publicos.ViewIgual (Panel4, Panel41)";
mostCurrent._vvvvvvvvvvvvvvv7._vvvvv2(mostCurrent.activityBA,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._panel4.getObject())),(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._panel41.getObject())));
 //BA.debugLineNum = 2026;BA.debugLine="lbl41.Height = Panel4.Height * 0.5";
mostCurrent._lbl41.setHeight((int) (mostCurrent._panel4.getHeight()*0.5));
 //BA.debugLineNum = 2027;BA.debugLine="lbl41.width = Panel4.Width*0.9";
mostCurrent._lbl41.setWidth((int) (mostCurrent._panel4.getWidth()*0.9));
 //BA.debugLineNum = 2028;BA.debugLine="Publicos.CentrarControlEnPanel (Panel4, lbl41, Tr";
mostCurrent._vvvvvvvvvvvvvvv7._vv0(mostCurrent.activityBA,mostCurrent._panel4,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._lbl41.getObject())),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2029;BA.debugLine="lbl41.Gravity = Gravity.CENTER";
mostCurrent._lbl41.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 2030;BA.debugLine="lbl41.textcolor= gt_Color.ColorMedio";
mostCurrent._lbl41.setTextColor(mostCurrent._gt_color.ColorMedio);
 //BA.debugLineNum = 2031;BA.debugLine="img51Facebook.height = Activity.Height * 0.1";
mostCurrent._img51facebook.setHeight((int) (mostCurrent._activity.getHeight()*0.1));
 //BA.debugLineNum = 2032;BA.debugLine="img51Facebook.Width = img51Facebook.Height";
mostCurrent._img51facebook.setWidth(mostCurrent._img51facebook.getHeight());
 //BA.debugLineNum = 2033;BA.debugLine="img51Twitter.height= img51Facebook.Height";
mostCurrent._img51twitter.setHeight(mostCurrent._img51facebook.getHeight());
 //BA.debugLineNum = 2034;BA.debugLine="img51Twitter.width  = img51Twitter.Height";
mostCurrent._img51twitter.setWidth(mostCurrent._img51twitter.getHeight());
 //BA.debugLineNum = 2036;BA.debugLine="Publicos.ViewIgual (Panel5, Panel51)";
mostCurrent._vvvvvvvvvvvvvvv7._vvvvv2(mostCurrent.activityBA,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._panel5.getObject())),(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._panel51.getObject())));
 //BA.debugLineNum = 2038;BA.debugLine="img51.height = Activity.Height * 0.1";
mostCurrent._img51.setHeight((int) (mostCurrent._activity.getHeight()*0.1));
 //BA.debugLineNum = 2039;BA.debugLine="img51.Width = img51.Height";
mostCurrent._img51.setWidth(mostCurrent._img51.getHeight());
 //BA.debugLineNum = 2040;BA.debugLine="Publicos.CentrarControlEnPanel(Panel51, img51, Tr";
mostCurrent._vvvvvvvvvvvvvvv7._vv0(mostCurrent.activityBA,mostCurrent._panel51,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._img51.getObject())),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2042;BA.debugLine="lbl51.Top = img51.Top + img51.Height + 1dip";
mostCurrent._lbl51.setTop((int) (mostCurrent._img51.getTop()+mostCurrent._img51.getHeight()+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (1))));
 //BA.debugLineNum = 2043;BA.debugLine="lbl51.height = img51.Height*0.3";
mostCurrent._lbl51.setHeight((int) (mostCurrent._img51.getHeight()*0.3));
 //BA.debugLineNum = 2044;BA.debugLine="lbl51.Width = Panel51.Width *0.7";
mostCurrent._lbl51.setWidth((int) (mostCurrent._panel51.getWidth()*0.7));
 //BA.debugLineNum = 2045;BA.debugLine="lbl51.TextColor = Colors.White";
mostCurrent._lbl51.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 2046;BA.debugLine="lbl51.Gravity = Gravity.CENTER";
mostCurrent._lbl51.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 2047;BA.debugLine="Publicos.CentrarControlEnPanel(Panel51, lbl51, Tr";
mostCurrent._vvvvvvvvvvvvvvv7._vv0(mostCurrent.activityBA,mostCurrent._panel51,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._lbl51.getObject())),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2049;BA.debugLine="lbl51MensajeMatete.Width = Panel51.width * 0.9";
mostCurrent._lbl51mensajematete.setWidth((int) (mostCurrent._panel51.getWidth()*0.9));
 //BA.debugLineNum = 2050;BA.debugLine="lbl51MensajeMatete.Height = Panel51.Height * 0.9";
mostCurrent._lbl51mensajematete.setHeight((int) (mostCurrent._panel51.getHeight()*0.9));
 //BA.debugLineNum = 2051;BA.debugLine="lbl51MensajeMatete.textcolor = Colors.white";
mostCurrent._lbl51mensajematete.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 2052;BA.debugLine="lbl51MensajeMatete.Typeface = tFontOpenSansLight";
mostCurrent._lbl51mensajematete.setTypeface((android.graphics.Typeface)(_vvvvvvvvvv5.getObject()));
 //BA.debugLineNum = 2053;BA.debugLine="Publicos.CentrarControlEnPanel(Panel51, lbl51Mens";
mostCurrent._vvvvvvvvvvvvvvv7._vv0(mostCurrent.activityBA,mostCurrent._panel51,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._lbl51mensajematete.getObject())),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2055;BA.debugLine="img51Facebook.height = Activity.Height * 0.1";
mostCurrent._img51facebook.setHeight((int) (mostCurrent._activity.getHeight()*0.1));
 //BA.debugLineNum = 2056;BA.debugLine="img51Facebook.Width = img51Facebook.Height";
mostCurrent._img51facebook.setWidth(mostCurrent._img51facebook.getHeight());
 //BA.debugLineNum = 2057;BA.debugLine="img51Twitter.height= img51Facebook.Height";
mostCurrent._img51twitter.setHeight(mostCurrent._img51facebook.getHeight());
 //BA.debugLineNum = 2058;BA.debugLine="img51Twitter.width  = img51Twitter.Height";
mostCurrent._img51twitter.setWidth(mostCurrent._img51twitter.getHeight());
 //BA.debugLineNum = 2059;BA.debugLine="Dim aCompartir() As View";
_acompartir = new anywheresoftware.b4a.objects.ConcreteViewWrapper[(int) (0)];
{
int d0 = _acompartir.length;
for (int i0 = 0;i0 < d0;i0++) {
_acompartir[i0] = new anywheresoftware.b4a.objects.ConcreteViewWrapper();
}
}
;
 //BA.debugLineNum = 2060;BA.debugLine="aCompartir = Array As View (  img51Facebook, img5";
_acompartir = new anywheresoftware.b4a.objects.ConcreteViewWrapper[]{(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._img51facebook.getObject())),(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._img51twitter.getObject()))};
 //BA.debugLineNum = 2061;BA.debugLine="Publicos.CentrarArrayControlesEnPanel ( Panel51,";
mostCurrent._vvvvvvvvvvvvvvv7._vv6(mostCurrent.activityBA,mostCurrent._panel51,_acompartir,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2063;BA.debugLine="lbl51Facebook.Top = img51Facebook.Top + img51Face";
mostCurrent._lbl51facebook.setTop((int) (mostCurrent._img51facebook.getTop()+mostCurrent._img51facebook.getHeight()+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (1))));
 //BA.debugLineNum = 2064;BA.debugLine="lbl51Facebook.height = img51Facebook.Height*0.3";
mostCurrent._lbl51facebook.setHeight((int) (mostCurrent._img51facebook.getHeight()*0.3));
 //BA.debugLineNum = 2065;BA.debugLine="lbl51Facebook.Width = Panel51.Width *0.3";
mostCurrent._lbl51facebook.setWidth((int) (mostCurrent._panel51.getWidth()*0.3));
 //BA.debugLineNum = 2066;BA.debugLine="lbl51Facebook.TextColor = Colors.white";
mostCurrent._lbl51facebook.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 2067;BA.debugLine="lbl51Facebook.Text = \"EN FACEBOOK\"";
mostCurrent._lbl51facebook.setText((Object)("EN FACEBOOK"));
 //BA.debugLineNum = 2069;BA.debugLine="lbl51Facebook.Left = Publicos.ViewAlinearCentro(i";
mostCurrent._lbl51facebook.setLeft(mostCurrent._vvvvvvvvvvvvvvv7._vvvv0(mostCurrent.activityBA,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._img51facebook.getObject())),(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._lbl51facebook.getObject()))));
 //BA.debugLineNum = 2071;BA.debugLine="lbl51Twitter.Top = img51Twitter.Top + img51Twitte";
mostCurrent._lbl51twitter.setTop((int) (mostCurrent._img51twitter.getTop()+mostCurrent._img51twitter.getHeight()+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (1))));
 //BA.debugLineNum = 2072;BA.debugLine="lbl51Twitter.height = img51Twitter.Height*0.3";
mostCurrent._lbl51twitter.setHeight((int) (mostCurrent._img51twitter.getHeight()*0.3));
 //BA.debugLineNum = 2073;BA.debugLine="lbl51Twitter.Width = Panel51.Width *0.3";
mostCurrent._lbl51twitter.setWidth((int) (mostCurrent._panel51.getWidth()*0.3));
 //BA.debugLineNum = 2074;BA.debugLine="lbl51Twitter.TextColor = Colors.white";
mostCurrent._lbl51twitter.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 2075;BA.debugLine="lbl51Twitter.Text = \"EN TWITTER\"";
mostCurrent._lbl51twitter.setText((Object)("EN TWITTER"));
 //BA.debugLineNum = 2077;BA.debugLine="lbl51Twitter.Left = Publicos.ViewAlinearCentro(im";
mostCurrent._lbl51twitter.setLeft(mostCurrent._vvvvvvvvvvvvvvv7._vvvv0(mostCurrent.activityBA,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._img51twitter.getObject())),(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._lbl51twitter.getObject()))));
 //BA.debugLineNum = 2081;BA.debugLine="Publicos.ViewIgual (Panel6, Panel61)";
mostCurrent._vvvvvvvvvvvvvvv7._vvvvv2(mostCurrent.activityBA,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._panel6.getObject())),(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._panel61.getObject())));
 //BA.debugLineNum = 2082;BA.debugLine="lbl61.Height = Panel6.Height * 0.6";
mostCurrent._lbl61.setHeight((int) (mostCurrent._panel6.getHeight()*0.6));
 //BA.debugLineNum = 2083;BA.debugLine="lbl61.Width = Panel6.Width";
mostCurrent._lbl61.setWidth(mostCurrent._panel6.getWidth());
 //BA.debugLineNum = 2084;BA.debugLine="Publicos.CentrarControlEnPanel(Panel6, lbl61, Tru";
mostCurrent._vvvvvvvvvvvvvvv7._vv0(mostCurrent.activityBA,mostCurrent._panel6,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._lbl61.getObject())),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 2086;BA.debugLine="lbl61.Gravity = Gravity.CENTER";
mostCurrent._lbl61.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 2087;BA.debugLine="lbl61.TextColor = lblv2Def.textcolor";
mostCurrent._lbl61.setTextColor(mostCurrent._lblv2def.getTextColor());
 //BA.debugLineNum = 2089;BA.debugLine="Panel21.Visible = False: Panel41.Visible = False:";
mostCurrent._panel21.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2089;BA.debugLine="Panel21.Visible = False: Panel41.Visible = False:";
mostCurrent._panel41.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2089;BA.debugLine="Panel21.Visible = False: Panel41.Visible = False:";
mostCurrent._panel51.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2089;BA.debugLine="Panel21.Visible = False: Panel41.Visible = False:";
mostCurrent._panel61.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2090;BA.debugLine="Panel11.visible = False";
mostCurrent._panel11.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2096;BA.debugLine="Dim setTextSizeLblsCompartirCancelar As Int";
_settextsizelblscompartircancelar = 0;
 //BA.debugLineNum = 2097;BA.debugLine="Publicos.SetLabelTextSize(lblPedirLetra , \"SALTAR";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv4(mostCurrent.activityBA,mostCurrent._lblpedirletra,"SALTAR MATETE",(float) (30),(float) (10),(int) (100));
 //BA.debugLineNum = 2098;BA.debugLine="setTextSizeLblsCompartirCancelar = lblPedirLetra.";
_settextsizelblscompartircancelar = (int) (mostCurrent._lblpedirletra.getTextSize());
 //BA.debugLineNum = 2099;BA.debugLine="lblSaltarPalabra.TextSize = setTextSizeLblsCompar";
mostCurrent._lblsaltarpalabra.setTextSize((float) (_settextsizelblscompartircancelar));
 //BA.debugLineNum = 2100;BA.debugLine="lbl51Facebook.TextSize = setTextSizeLblsCompartir";
mostCurrent._lbl51facebook.setTextSize((float) (_settextsizelblscompartircancelar));
 //BA.debugLineNum = 2101;BA.debugLine="lbl51Twitter.TextSize = setTextSizeLblsCompartirC";
mostCurrent._lbl51twitter.setTextSize((float) (_settextsizelblscompartircancelar));
 //BA.debugLineNum = 2102;BA.debugLine="lblCompartir.TextSize = setTextSizeLblsCompartirC";
mostCurrent._lblcompartir.setTextSize((float) (_settextsizelblscompartircancelar));
 //BA.debugLineNum = 2103;BA.debugLine="lbl51.TextSize = setTextSizeLblsCompartirCancelar";
mostCurrent._lbl51.setTextSize((float) (_settextsizelblscompartircancelar));
 //BA.debugLineNum = 2104;BA.debugLine="lblBajarLetras.TextSize = setTextSizeLblsComparti";
mostCurrent._lblbajarletras.setTextSize((float) (_settextsizelblscompartircancelar));
 //BA.debugLineNum = 2108;BA.debugLine="Publicos.SetLabelTextSize(lbl41 , \"COMPARTIR\", 30";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv4(mostCurrent.activityBA,mostCurrent._lbl41,"COMPARTIR",(float) (30),(float) (10),(int) (100));
 //BA.debugLineNum = 2109;BA.debugLine="setTextSizeLblsCompartirCancelar = lbl41.TextSize";
_settextsizelblscompartircancelar = (int) (mostCurrent._lbl41.getTextSize());
 //BA.debugLineNum = 2110;BA.debugLine="lbl61.TextSize = setTextSizeLblsCompartirCancelar";
mostCurrent._lbl61.setTextSize((float) (_settextsizelblscompartircancelar));
 //BA.debugLineNum = 2111;BA.debugLine="lbl11.TextSize = setTextSizeLblsCompartirCancelar";
mostCurrent._lbl11.setTextSize((float) (_settextsizelblscompartircancelar));
 //BA.debugLineNum = 2114;BA.debugLine="pnlHistoria.left = 0";
mostCurrent._pnlhistoria.setLeft((int) (0));
 //BA.debugLineNum = 2115;BA.debugLine="pnlHistoria.Top = lbl11.Top + lbl11.height";
mostCurrent._pnlhistoria.setTop((int) (mostCurrent._lbl11.getTop()+mostCurrent._lbl11.getHeight()));
 //BA.debugLineNum = 2116;BA.debugLine="pnlHistoria.width = 100%x";
mostCurrent._pnlhistoria.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 2117;BA.debugLine="pnlHistoria.Height = Activity.Height - (lbl11.Top";
mostCurrent._pnlhistoria.setHeight((int) (mostCurrent._activity.getHeight()-(mostCurrent._lbl11.getTop()+mostCurrent._lbl11.getHeight())-(mostCurrent._activity.getHeight()-mostCurrent._panel61.getTop())));
 //BA.debugLineNum = 2118;BA.debugLine="pnlHistoria.Color = Colors.white' iColor(0)";
mostCurrent._pnlhistoria.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 2120;BA.debugLine="pnlHistFiltro.Width = pnlHistoria.Width";
mostCurrent._pnlhistfiltro.setWidth(mostCurrent._pnlhistoria.getWidth());
 //BA.debugLineNum = 2121;BA.debugLine="pnlHistFiltro.Height = Activity.Height*0.1";
mostCurrent._pnlhistfiltro.setHeight((int) (mostCurrent._activity.getHeight()*0.1));
 //BA.debugLineNum = 2122;BA.debugLine="lblFiltro.Width = pnlHistFiltro.Width";
mostCurrent._lblfiltro.setWidth(mostCurrent._pnlhistfiltro.getWidth());
 //BA.debugLineNum = 2123;BA.debugLine="lblFiltro.Height = pnlHistFiltro.Height";
mostCurrent._lblfiltro.setHeight(mostCurrent._pnlhistfiltro.getHeight());
 //BA.debugLineNum = 2124;BA.debugLine="lblFiltro.Tag = \"SALTADOS\"";
mostCurrent._lblfiltro.setTag((Object)("SALTADOS"));
 //BA.debugLineNum = 2125;BA.debugLine="lblFiltro.Text = \"SALTADOS\"";
mostCurrent._lblfiltro.setText((Object)("SALTADOS"));
 //BA.debugLineNum = 2126;BA.debugLine="lblFiltro.Gravity = Gravity.CENTER";
mostCurrent._lblfiltro.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 2130;BA.debugLine="V2_PantallaConfigura(xConfiguraPantalla.Jugar,0)";
_v2_pantallaconfigura(_vvvvvvvv3.Jugar,(int) (0));
 //BA.debugLineNum = 2136;BA.debugLine="ScrollViewGenera";
_vvvvvvvvvvvvvvvvvvvvvvvv3();
 //BA.debugLineNum = 2138;BA.debugLine="End Sub";
return "";
}
}
