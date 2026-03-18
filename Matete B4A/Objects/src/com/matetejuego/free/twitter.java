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

public class twitter extends Activity implements B4AActivity{
	public static twitter mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = false;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isFirst) {
			processBA = new BA(this.getApplicationContext(), null, null, "com.matetejuego.free", "com.matetejuego.free.twitter");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (twitter).");
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
		activityBA = new BA(this, layout, processBA, "com.matetejuego.free", "com.matetejuego.free.twitter");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.matetejuego.free.twitter", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (twitter) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (twitter) Resume **");
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
		return twitter.class;
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
        BA.LogInfo("** Activity (twitter) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
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
            BA.LogInfo("** Activity (twitter) Resume **");
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
public static com.datasteam.b4a.socialapi.TwitterProvider _vvvvvvvvvvvv1 = null;
public static String _vvvvvvvvvv6 = "";
public static String _vvvvvvvvvv7 = "";
public static String _vvvvvvvvvv0 = "";
public static int _vvvvvvvvvvv1 = 0;
public static String _vvvvvvvvvvv2 = "";
public static String _vvvvvvvvvvv7 = "";
public static String _g_dirgrabable = "";
public static boolean _vvvvvvvvvvvv2 = false;
public static int _vvvvvvvvvvv0 = 0;
public static anyhwheresoftware.b4a.objects.sqlcipher.SQLCipher _g_sqlbaselocalusuario = null;
public com.datasteam.b4a.socialapi.BaseProviderActivity _vvvvvvvvvvvvvvvvvvvvvv3 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _txtmensajejugador = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbllen = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imgicono = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imgscreenshot = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _vvvvvvvvvvvvvvvvvvvvvvvvvv1 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imgcancelar = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblcancelar = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imgmenu = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblpanel3 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imgpublicar = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblpublicar = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbltitulo = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel1 = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel2 = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel3 = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel4 = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel6 = null;
public anywheresoftware.b4a.objects.Timer _vvvvvvvvvvvvvvvvvvvvvvvv6 = null;
public com.matetejuego.free.main _vvvvvvvvvvvvvvv6 = null;
public com.matetejuego.free.publicos _vvvvvvvvvvvvvvv7 = null;
public com.matetejuego.free.jugar _vvvvvvvvvvvvvvv0 = null;
public com.matetejuego.free.facebookactivity _vvvvvvvvvvvvvvvv1 = null;
public com.matetejuego.free.svcbaseremota _vvvvvvvvvvvvvvvv3 = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 56;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 58;BA.debugLine="Activity.LoadLayout(\"TwitterV2\")";
mostCurrent._activity.LoadLayout("TwitterV2",mostCurrent.activityBA);
 //BA.debugLineNum = 59;BA.debugLine="If FirstTime Then";
if (_firsttime) { 
 //BA.debugLineNum = 60;BA.debugLine="g_DirGrabable = Publicos.Get_DirectorioBase";
_g_dirgrabable = mostCurrent._vvvvvvvvvvvvvvv7._get_directoriobase(mostCurrent.activityBA);
 };
 //BA.debugLineNum = 63;BA.debugLine="TimerConectar.initialize(\"TimerConectar\",0)";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvvvv6.Initialize(processBA,"TimerConectar",(long) (0));
 //BA.debugLineNum = 64;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 90;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 91;BA.debugLine="If UserClosed Then";
if (_userclosed) { 
 //BA.debugLineNum = 92;BA.debugLine="StartActivity(\"Jugar\")";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)("Jugar"));
 //BA.debugLineNum = 93;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 };
 //BA.debugLineNum = 95;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 66;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 67;BA.debugLine="imgScreenshot.Bitmap = LoadBitmap(g_DirGrabable,";
mostCurrent._imgscreenshot.setBitmap((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(_g_dirgrabable,"PruebaSS.png").getObject()));
 //BA.debugLineNum = 71;BA.debugLine="Publicos.CargaDeviceValues";
mostCurrent._vvvvvvvvvvvvvvv7._vv4(mostCurrent.activityBA);
 //BA.debugLineNum = 73;BA.debugLine="V2_SetPantalla";
_v2_setpantalla();
 //BA.debugLineNum = 74;BA.debugLine="MensajeMatete";
_vvvvvvvvvvvvvvvvvvvvvvvvv7();
 //BA.debugLineNum = 75;BA.debugLine="DoEvents";
anywheresoftware.b4a.keywords.Common.DoEvents();
 //BA.debugLineNum = 78;BA.debugLine="TwitterP.SetActivity(ThisActivity.Initialize(\"Twi";
_vvvvvvvvvvvv1.SetActivity(mostCurrent._vvvvvvvvvvvvvvvvvvvvvv3.Initialize(mostCurrent.activityBA,"Twitter"));
 //BA.debugLineNum = 82;BA.debugLine="If TwitterP.Connected = False Then";
if (_vvvvvvvvvvvv1.getConnected()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 83;BA.debugLine="TimerConectar.Interval = 3000";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvvvv6.setInterval((long) (3000));
 //BA.debugLineNum = 84;BA.debugLine="TimerConectar.Enabled = True";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvvvv6.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 86;BA.debugLine="TimerConectar.Enabled = False";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvvvv6.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 88;BA.debugLine="End Sub";
return "";
}
public static String  _btndisconnect_click() throws Exception{
 //BA.debugLineNum = 136;BA.debugLine="Sub BtnDisconnect_Click";
 //BA.debugLineNum = 137;BA.debugLine="TwitterP.Logout";
_vvvvvvvvvvvv1.Logout();
 //BA.debugLineNum = 138;BA.debugLine="End Sub";
return "";
}
public static String  _btnhashtag_click() throws Exception{
String _sstr = "";
 //BA.debugLineNum = 196;BA.debugLine="Sub btnHashtag_click";
 //BA.debugLineNum = 197;BA.debugLine="Dim sstr As String";
_sstr = "";
 //BA.debugLineNum = 198;BA.debugLine="sstr = txtMensajeJugador.text";
_sstr = mostCurrent._txtmensajejugador.getText();
 //BA.debugLineNum = 199;BA.debugLine="If Not (sstr.Contains (\"#MateteDivergente\")) Then";
if (anywheresoftware.b4a.keywords.Common.Not(_sstr.contains("#MateteDivergente"))) { 
 //BA.debugLineNum = 200;BA.debugLine="txtMensajeJugador.Text = txtMensajeJugador.Text &";
mostCurrent._txtmensajejugador.setText((Object)(mostCurrent._txtmensajejugador.getText()+" #MateteDivergente"));
 };
 //BA.debugLineNum = 202;BA.debugLine="End Sub";
return "";
}
public static String  _btnpegar_click() throws Exception{
 //BA.debugLineNum = 191;BA.debugLine="Sub btnpegar_click";
 //BA.debugLineNum = 192;BA.debugLine="txtMensajeJugador.Text = sDefinicion";
mostCurrent._txtmensajejugador.setText((Object)(_vvvvvvvvvv7));
 //BA.debugLineNum = 193;BA.debugLine="End Sub";
return "";
}
public static String  _btnvolver_click() throws Exception{
 //BA.debugLineNum = 185;BA.debugLine="Sub btnvolver_click";
 //BA.debugLineNum = 186;BA.debugLine="StartActivity(\"Jugar\")";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)("Jugar"));
 //BA.debugLineNum = 187;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvvvvvvvvvvvv0() throws Exception{
 //BA.debugLineNum = 132;BA.debugLine="Sub Conectar";
 //BA.debugLineNum = 133;BA.debugLine="TwitterP.Login(True)";
_vvvvvvvvvvvv1.Login(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 134;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 26;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 30;BA.debugLine="Dim ThisActivity As SocialApiActivity";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv3 = new com.datasteam.b4a.socialapi.BaseProviderActivity();
 //BA.debugLineNum = 32;BA.debugLine="Private txtMensajeJugador As EditText";
mostCurrent._txtmensajejugador = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 34;BA.debugLine="Private lblLen As Label";
mostCurrent._lbllen = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 35;BA.debugLine="Private imgIcono As ImageView";
mostCurrent._imgicono = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 36;BA.debugLine="Private imgScreenshot As ImageView";
mostCurrent._imgscreenshot = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 37;BA.debugLine="Private imgVolver As ImageView";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvvvvvv1 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 38;BA.debugLine="Private imgCancelar As ImageView";
mostCurrent._imgcancelar = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 39;BA.debugLine="Private lblcancelar As Label";
mostCurrent._lblcancelar = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 40;BA.debugLine="Private imgMenu As ImageView";
mostCurrent._imgmenu = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 41;BA.debugLine="Private lblPanel3 As Label";
mostCurrent._lblpanel3 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 42;BA.debugLine="Private imgPublicar As ImageView";
mostCurrent._imgpublicar = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 44;BA.debugLine="Private lblPublicar As Label";
mostCurrent._lblpublicar = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 45;BA.debugLine="Private lblTitulo As Label";
mostCurrent._lbltitulo = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 46;BA.debugLine="Private Panel1 As Panel";
mostCurrent._panel1 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 47;BA.debugLine="Private Panel2 As Panel";
mostCurrent._panel2 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 48;BA.debugLine="Private Panel3 As Panel";
mostCurrent._panel3 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 49;BA.debugLine="Private Panel4 As Panel";
mostCurrent._panel4 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 50;BA.debugLine="Private Panel6 As Panel";
mostCurrent._panel6 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 52;BA.debugLine="Private TimerConectar As Timer";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvvvv6 = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 54;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvvvvvvvvvv3(boolean _c,String _trueres,String _falseres) throws Exception{
 //BA.debugLineNum = 400;BA.debugLine="Sub IIF(c As Boolean, TrueRes As String, FalseRes";
 //BA.debugLineNum = 401;BA.debugLine="If c Then Return TrueRes Else Return FalseRes";
if (_c) { 
if (true) return _trueres;}
else {
if (true) return _falseres;};
 //BA.debugLineNum = 402;BA.debugLine="End Sub";
return "";
}
public static String  _imgcancelar_click() throws Exception{
 //BA.debugLineNum = 396;BA.debugLine="Sub imgCancelar_Click";
 //BA.debugLineNum = 397;BA.debugLine="lblCancelar_click";
_lblcancelar_click();
 //BA.debugLineNum = 398;BA.debugLine="End Sub";
return "";
}
public static String  _imgpublicar_click() throws Exception{
 //BA.debugLineNum = 393;BA.debugLine="Sub imgPublicar_Click";
 //BA.debugLineNum = 394;BA.debugLine="lblPublicar_click";
_lblpublicar_click();
 //BA.debugLineNum = 395;BA.debugLine="End Sub";
return "";
}
public static String  _lblcancelar_click() throws Exception{
 //BA.debugLineNum = 376;BA.debugLine="Sub lblCancelar_click'cancelar";
 //BA.debugLineNum = 377;BA.debugLine="Jugar.bCanceloFacebookoTwitter = True";
mostCurrent._vvvvvvvvvvvvvvv0._vvvvvvvvv0 = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 378;BA.debugLine="StartActivity(\"Jugar\")";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)("Jugar"));
 //BA.debugLineNum = 379;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 381;BA.debugLine="End Sub";
return "";
}
public static String  _lblpublicar_click() throws Exception{
 //BA.debugLineNum = 384;BA.debugLine="Sub lblPublicar_click";
 //BA.debugLineNum = 385;BA.debugLine="If txtMensajeJugador.Text.Trim = \"\" Then";
if ((mostCurrent._txtmensajejugador.getText().trim()).equals("")) { 
 //BA.debugLineNum = 386;BA.debugLine="Msgbox(\"Debes ingresar un mensaje para publicar\",";
anywheresoftware.b4a.keywords.Common.Msgbox("Debes ingresar un mensaje para publicar","Cataplún",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 388;BA.debugLine="Publicar";
_vvvvvvvvvvvvvvvvvvvvvvvvv6();
 };
 //BA.debugLineNum = 390;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvvvvvvvvvvvv7() throws Exception{
String _smensaje = "";
 //BA.debugLineNum = 170;BA.debugLine="Sub MensajeMatete";
 //BA.debugLineNum = 171;BA.debugLine="Dim sMensaje As String";
_smensaje = "";
 //BA.debugLineNum = 174;BA.debugLine="sMensaje= sDefinicion & \" #MateteDivergente\"";
_smensaje = _vvvvvvvvvv7+" #MateteDivergente";
 //BA.debugLineNum = 180;BA.debugLine="txtMensajeJugador.Text = sMensaje";
mostCurrent._txtmensajejugador.setText((Object)(_smensaje));
 //BA.debugLineNum = 182;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 10;BA.debugLine="Dim TwitterP As TwitterProvider";
_vvvvvvvvvvvv1 = new com.datasteam.b4a.socialapi.TwitterProvider();
 //BA.debugLineNum = 11;BA.debugLine="TwitterP.Initialize(\"kRLepRJLA0NrCVkkr6yzYCIrt";
_vvvvvvvvvvvv1.Initialize(BA.__b (new byte[] {2,41,40,1,6,91,103,60,97,29,125,7,105,106,91,95,-20,-51,78,76,79,115,109,93,77}, 463167),BA.__b (new byte[] {95,13,122,-104,69,96,15,-67,107,98,48,-10,25,90,7,-51,-33,-116,21,-49,114,0,23,-48,1,64,59,-84,57,68,103,-3,22,54,37,-19,124,94,43,-92,79,1,17,-79,-17,-102,29,-45,88,77}, 149031));
 //BA.debugLineNum = 13;BA.debugLine="Public sLetrasElegir As String";
_vvvvvvvvvv6 = "";
 //BA.debugLineNum = 14;BA.debugLine="Public sDefinicion As String";
_vvvvvvvvvv7 = "";
 //BA.debugLineNum = 15;BA.debugLine="Public sPalabra As String";
_vvvvvvvvvv0 = "";
 //BA.debugLineNum = 16;BA.debugLine="Public iCostoLetra As Int";
_vvvvvvvvvvv1 = 0;
 //BA.debugLineNum = 17;BA.debugLine="Public sLetrasPalabra As String";
_vvvvvvvvvvv2 = "";
 //BA.debugLineNum = 18;BA.debugLine="Dim sMensajeJugador As String";
_vvvvvvvvvvv7 = "";
 //BA.debugLineNum = 19;BA.debugLine="Dim g_DirGrabable As String";
_g_dirgrabable = "";
 //BA.debugLineNum = 20;BA.debugLine="Dim bPublicarEnEvent As Boolean = False";
_vvvvvvvvvvvv2 = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 21;BA.debugLine="Dim iIntentos As Int = 0";
_vvvvvvvvvvv0 = (int) (0);
 //BA.debugLineNum = 22;BA.debugLine="Dim g_sqlBaseLocalUsuario As SQLCipher";
_g_sqlbaselocalusuario = new anyhwheresoftware.b4a.objects.sqlcipher.SQLCipher();
 //BA.debugLineNum = 24;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvvvvvvvvvvvv6() throws Exception{
 //BA.debugLineNum = 140;BA.debugLine="Sub Publicar";
 //BA.debugLineNum = 142;BA.debugLine="If Not (TwitterP.Connected) Then ' si no esta con";
if (anywheresoftware.b4a.keywords.Common.Not(_vvvvvvvvvvvv1.getConnected())) { 
 //BA.debugLineNum = 143;BA.debugLine="bPublicarEnEvent = True";
_vvvvvvvvvvvv2 = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 144;BA.debugLine="Conectar";
_vvvvvvvvvvvvvvvvvvvvvvvvv0();
 }else {
 //BA.debugLineNum = 147;BA.debugLine="If txtMensajeJugador.Text.Length >0 Then";
if (mostCurrent._txtmensajejugador.getText().length()>0) { 
 //BA.debugLineNum = 148;BA.debugLine="If txtMensajeJugador.Text.Length >140 Then";
if (mostCurrent._txtmensajejugador.getText().length()>140) { 
 //BA.debugLineNum = 149;BA.debugLine="ToastMessageShow(\"El mensaje es muy largo para";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("El mensaje es muy largo para Twitter!",anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 151;BA.debugLine="If Msgbox2(\"¿Confirmas la publicación? Ganarás";
if (anywheresoftware.b4a.keywords.Common.Msgbox2("¿Confirmas la publicación? Ganarás "+BA.NumberToString(_vvvvvvvvvvv1)+" Neurona"+_vvvvvvvvvvvvvvvvvvvvvvv3(_vvvvvvvvvvv1>1,"s",""),"Twitter","Publicar","Cancelar","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null),mostCurrent.activityBA)==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
 //BA.debugLineNum = 152;BA.debugLine="If TwitterPublicar (txtMensajeJugador.text) T";
if (_vvvvvvvvvvvvvvvvvvvvvvvvvv2(mostCurrent._txtmensajejugador.getText())) { 
 //BA.debugLineNum = 153;BA.debugLine="Publicos.Usuario_SumarNeuronas(iCostoLetra,";
mostCurrent._vvvvvvvvvvvvvvv7._usuario_sumarneuronas(mostCurrent.activityBA,_vvvvvvvvvvv1,_g_sqlbaselocalusuario);
 //BA.debugLineNum = 154;BA.debugLine="Twitter_UpdateUso";
_twitter_updateuso();
 //BA.debugLineNum = 155;BA.debugLine="Jugar.bPublicoEnTwitter= True";
mostCurrent._vvvvvvvvvvvvvvv0._vvvvvvvvv7 = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 156;BA.debugLine="StartActivity(\"Jugar\")";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)("Jugar"));
 }else {
 //BA.debugLineNum = 158;BA.debugLine="ToastMessageShow(\"No fue posible publicar en";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("No fue posible publicar en Twitter!",anywheresoftware.b4a.keywords.Common.True);
 };
 };
 };
 }else {
 //BA.debugLineNum = 164;BA.debugLine="ToastMessageShow(\"Ingresa un mensaje para publi";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Ingresa un mensaje para publicar!",anywheresoftware.b4a.keywords.Common.True);
 };
 };
 //BA.debugLineNum = 167;BA.debugLine="End Sub";
return "";
}
public static String  _timerconectar_tick() throws Exception{
 //BA.debugLineNum = 361;BA.debugLine="Sub timerConectar_Tick";
 //BA.debugLineNum = 362;BA.debugLine="Log (\"Intentos timer:\" & iIntentos)";
anywheresoftware.b4a.keywords.Common.Log("Intentos timer:"+BA.NumberToString(_vvvvvvvvvvv0));
 //BA.debugLineNum = 363;BA.debugLine="If iIntentos < 1 AND TwitterP.Connected = False Th";
if (_vvvvvvvvvvv0<1 && _vvvvvvvvvvvv1.getConnected()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 364;BA.debugLine="If TwitterP.Busy = False Then";
if (_vvvvvvvvvvvv1.getBusy()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 365;BA.debugLine="Conectar";
_vvvvvvvvvvvvvvvvvvvvvvvvv0();
 //BA.debugLineNum = 366;BA.debugLine="iIntentos = iIntentos + 1";
_vvvvvvvvvvv0 = (int) (_vvvvvvvvvvv0+1);
 };
 };
 //BA.debugLineNum = 369;BA.debugLine="If iIntentos >=2 Then";
if (_vvvvvvvvvvv0>=2) { 
 //BA.debugLineNum = 370;BA.debugLine="TimerConectar.Enabled = False";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvvvv6.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 372;BA.debugLine="End Sub";
return "";
}
public static String  _twitter_connected(com.datasteam.b4a.socialapi.BaseProvider _provider) throws Exception{
 //BA.debugLineNum = 113;BA.debugLine="Sub Twitter_Connected (Provider As SocialApiProvid";
 //BA.debugLineNum = 115;BA.debugLine="TimerConectar.Enabled = False";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvvvv6.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 116;BA.debugLine="If bPublicarEnEvent Then";
if (_vvvvvvvvvvvv2) { 
 //BA.debugLineNum = 117;BA.debugLine="bPublicarEnEvent = False";
_vvvvvvvvvvvv2 = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 118;BA.debugLine="TwitterPublicar(txtMensajeJugador.text)";
_vvvvvvvvvvvvvvvvvvvvvvvvvv2(mostCurrent._txtmensajejugador.getText());
 };
 //BA.debugLineNum = 120;BA.debugLine="End Sub";
return "";
}
public static String  _twitter_disconnected(com.datasteam.b4a.socialapi.BaseProvider _provider) throws Exception{
 //BA.debugLineNum = 122;BA.debugLine="Sub Twitter_Disconnected (Provider As SocialApiPro";
 //BA.debugLineNum = 123;BA.debugLine="Msgbox(\"Bye bye!\", \"JustDisconnected!\")";
anywheresoftware.b4a.keywords.Common.Msgbox("Bye bye!","JustDisconnected!",mostCurrent.activityBA);
 //BA.debugLineNum = 124;BA.debugLine="End Sub";
return "";
}
public static String  _twitter_event(com.datasteam.b4a.socialapi.BaseProvider _provider) throws Exception{
 //BA.debugLineNum = 98;BA.debugLine="Sub Twitter_Event (Provider As SocialApiProvider)";
 //BA.debugLineNum = 111;BA.debugLine="End Sub";
return "";
}
public static String  _twitter_failed(com.datasteam.b4a.socialapi.BaseProvider _provider) throws Exception{
 //BA.debugLineNum = 126;BA.debugLine="Sub Twitter_Failed (Provider As SocialApiProvider)";
 //BA.debugLineNum = 127;BA.debugLine="If Msgbox2(\"Failed to actualize your details.\"";
if (anywheresoftware.b4a.keywords.Common.Msgbox2("Failed to actualize your details."+anywheresoftware.b4a.keywords.Common.CRLF+anywheresoftware.b4a.keywords.Common.CRLF+"Retry?","Error","Yes","No","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null),mostCurrent.activityBA)==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
 //BA.debugLineNum = 128;BA.debugLine="Provider.Retry";
_provider.Retry();
 };
 //BA.debugLineNum = 130;BA.debugLine="End Sub";
return "";
}
public static boolean  _twitter_updateuso() throws Exception{
String _ssql = "";
boolean _bret = false;
String _sfecha = "";
 //BA.debugLineNum = 239;BA.debugLine="Sub Twitter_UpdateUso As Boolean";
 //BA.debugLineNum = 241;BA.debugLine="Dim sSql As String, bRet As Boolean, sFecha As Str";
_ssql = "";
_bret = false;
_sfecha = "";
 //BA.debugLineNum = 243;BA.debugLine="Try";
try { //BA.debugLineNum = 244;BA.debugLine="sSql = \"Update Avance Set Ind1=1 where idPalabra";
_ssql = "Update Avance Set Ind1=1 where idPalabra = (select max(idpalabra) from avance)";
 //BA.debugLineNum = 245;BA.debugLine="g_sqlBaseLocalUsuario.ExecNonQuery (sSql)";
_g_sqlbaselocalusuario.ExecNonQuery(_ssql);
 //BA.debugLineNum = 246;BA.debugLine="bRet = True";
_bret = anywheresoftware.b4a.keywords.Common.True;
 } 
       catch (Exception e7) {
			processBA.setLastException(e7); //BA.debugLineNum = 248;BA.debugLine="bRet = False";
_bret = anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 250;BA.debugLine="Return bRet";
if (true) return _bret;
 //BA.debugLineNum = 251;BA.debugLine="End Sub";
return false;
}
public static boolean  _vvvvvvvvvvvvvvvvvvvvvvvvvv2(String _smensaje) throws Exception{
boolean _bret = false;
com.datasteam.b4a.socialapi.TwitterProvider.Result _result = null;
 //BA.debugLineNum = 217;BA.debugLine="Sub TwitterPublicar (sMensaje As String) As Boolea";
 //BA.debugLineNum = 218;BA.debugLine="Dim bRet As Boolean";
_bret = false;
 //BA.debugLineNum = 223;BA.debugLine="Dim Result As TwitterResult";
_result = new com.datasteam.b4a.socialapi.TwitterProvider.Result();
 //BA.debugLineNum = 224;BA.debugLine="Result = TwitterP.Tweet(sMensaje)";
_result = _vvvvvvvvvvvv1.Tweet(_smensaje);
 //BA.debugLineNum = 225;BA.debugLine="If Result = Null Then";
if (_result== null) { 
 //BA.debugLineNum = 226;BA.debugLine="Msgbox(\"Publicación anterior pendiente.\", \"Twitt";
anywheresoftware.b4a.keywords.Common.Msgbox("Publicación anterior pendiente.","Twitter",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 228;BA.debugLine="If Result.Canceled Then Return";
if (_result.Canceled) { 
if (true) return false;};
 //BA.debugLineNum = 230;BA.debugLine="If Result.Success Then";
if (_result.Success) { 
 //BA.debugLineNum = 231;BA.debugLine="bRet = True";
_bret = anywheresoftware.b4a.keywords.Common.True;
 }else {
 //BA.debugLineNum = 233;BA.debugLine="ToastMessageShow(\"No fue posible publicar en Tw";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("No fue posible publicar en Twitter!",anywheresoftware.b4a.keywords.Common.True);
 };
 };
 //BA.debugLineNum = 236;BA.debugLine="Return bRet";
if (true) return _bret;
 //BA.debugLineNum = 237;BA.debugLine="End Sub";
return false;
}
public static String  _txtmensajejugador_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 205;BA.debugLine="Sub txtMensajeJugador_TextChanged (Old As String,";
 //BA.debugLineNum = 207;BA.debugLine="lblLen.Text = New.Length";
mostCurrent._lbllen.setText((Object)(_new.length()));
 //BA.debugLineNum = 208;BA.debugLine="If New.Length > 140 Then";
if (_new.length()>140) { 
 //BA.debugLineNum = 209;BA.debugLine="lblLen.TextColor = Colors.Red";
mostCurrent._lbllen.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 }else {
 //BA.debugLineNum = 211;BA.debugLine="lblLen.Textcolor = Colors.white";
mostCurrent._lbllen.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 };
 //BA.debugLineNum = 214;BA.debugLine="End Sub";
return "";
}
public static String  _v2_setpantalla() throws Exception{
int _icolorletra = 0;
anywheresoftware.b4a.objects.ConcreteViewWrapper[] _acentrar = null;
 //BA.debugLineNum = 254;BA.debugLine="Sub V2_SetPantalla";
 //BA.debugLineNum = 255;BA.debugLine="Dim iColorLetra As Int";
_icolorletra = 0;
 //BA.debugLineNum = 257;BA.debugLine="iColorLetra = Colors.RGB(151, 89, 152)";
_icolorletra = anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (151),(int) (89),(int) (152));
 //BA.debugLineNum = 258;BA.debugLine="Activity.Color = Colors.white";
mostCurrent._activity.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 259;BA.debugLine="Panel1.Color = Colors.white ' menu. titulo, icono";
mostCurrent._panel1.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 260;BA.debugLine="Panel2.color = Colors.RGB(231,234,226) 'screensho";
mostCurrent._panel2.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (231),(int) (234),(int) (226)));
 //BA.debugLineNum = 261;BA.debugLine="Panel3.Color = Colors.RGB(177, 109, 177) 'subtitu";
mostCurrent._panel3.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (177),(int) (109),(int) (177)));
 //BA.debugLineNum = 262;BA.debugLine="Panel4.color = Colors.white  ' text input mensaje";
mostCurrent._panel4.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 263;BA.debugLine="Panel6.Color = Colors.white ' cancelar-publicar";
mostCurrent._panel6.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 266;BA.debugLine="Panel1.Width = 100%x : 	Panel2.Width = 100%x";
mostCurrent._panel1.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 266;BA.debugLine="Panel1.Width = 100%x : 	Panel2.Width = 100%x";
mostCurrent._panel2.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 267;BA.debugLine="Panel3.Width = 100%x : 	Panel4.Width = 100%x";
mostCurrent._panel3.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 267;BA.debugLine="Panel3.Width = 100%x : 	Panel4.Width = 100%x";
mostCurrent._panel4.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 268;BA.debugLine="Panel6.Width = 100%x";
mostCurrent._panel6.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 270;BA.debugLine="Panel1.Height = Activity.Height * 0.10: Panel1.To";
mostCurrent._panel1.setHeight((int) (mostCurrent._activity.getHeight()*0.10));
 //BA.debugLineNum = 270;BA.debugLine="Panel1.Height = Activity.Height * 0.10: Panel1.To";
mostCurrent._panel1.setTop((int) (0));
 //BA.debugLineNum = 271;BA.debugLine="Panel2.Height = Activity.Height * 0.35: Panel2.To";
mostCurrent._panel2.setHeight((int) (mostCurrent._activity.getHeight()*0.35));
 //BA.debugLineNum = 271;BA.debugLine="Panel2.Height = Activity.Height * 0.35: Panel2.To";
mostCurrent._panel2.setTop((int) (mostCurrent._panel1.getTop()+mostCurrent._panel1.getHeight()));
 //BA.debugLineNum = 272;BA.debugLine="Panel3.Height = Activity.Height * 0.10: Panel3.To";
mostCurrent._panel3.setHeight((int) (mostCurrent._activity.getHeight()*0.10));
 //BA.debugLineNum = 272;BA.debugLine="Panel3.Height = Activity.Height * 0.10: Panel3.To";
mostCurrent._panel3.setTop((int) (mostCurrent._panel2.getTop()+mostCurrent._panel2.getHeight()));
 //BA.debugLineNum = 273;BA.debugLine="Panel4.Height = Activity.Height * 0.25: Panel4.To";
mostCurrent._panel4.setHeight((int) (mostCurrent._activity.getHeight()*0.25));
 //BA.debugLineNum = 273;BA.debugLine="Panel4.Height = Activity.Height * 0.25: Panel4.To";
mostCurrent._panel4.setTop((int) (mostCurrent._panel3.getTop()+mostCurrent._panel3.getHeight()));
 //BA.debugLineNum = 274;BA.debugLine="Panel6.Height = Activity.Height * 0.14: Panel6.To";
mostCurrent._panel6.setHeight((int) (mostCurrent._activity.getHeight()*0.14));
 //BA.debugLineNum = 274;BA.debugLine="Panel6.Height = Activity.Height * 0.14: Panel6.To";
mostCurrent._panel6.setTop((int) (mostCurrent._panel4.getTop()+mostCurrent._panel4.getHeight()));
 //BA.debugLineNum = 277;BA.debugLine="imgMenu.Left = Panel1.Width * 0.05";
mostCurrent._imgmenu.setLeft((int) (mostCurrent._panel1.getWidth()*0.05));
 //BA.debugLineNum = 278;BA.debugLine="imgMenu.Height=Panel1.Height *0.5";
mostCurrent._imgmenu.setHeight((int) (mostCurrent._panel1.getHeight()*0.5));
 //BA.debugLineNum = 279;BA.debugLine="imgMenu.Width=imgMenu.Height";
mostCurrent._imgmenu.setWidth(mostCurrent._imgmenu.getHeight());
 //BA.debugLineNum = 280;BA.debugLine="Publicos.CentrarControlEnPanel(Panel1, imgMenu, F";
mostCurrent._vvvvvvvvvvvvvvv7._vv0(mostCurrent.activityBA,mostCurrent._panel1,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._imgmenu.getObject())),anywheresoftware.b4a.keywords.Common.False,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 283;BA.debugLine="lblTitulo.Height = Panel1.Height*0.5";
mostCurrent._lbltitulo.setHeight((int) (mostCurrent._panel1.getHeight()*0.5));
 //BA.debugLineNum = 284;BA.debugLine="lblTitulo.Width = 70%x";
mostCurrent._lbltitulo.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (70),mostCurrent.activityBA));
 //BA.debugLineNum = 285;BA.debugLine="lblTitulo.textcolor = Colors.RGB(177, 109, 177)";
mostCurrent._lbltitulo.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (177),(int) (109),(int) (177)));
 //BA.debugLineNum = 286;BA.debugLine="lblTitulo.Left = 0";
mostCurrent._lbltitulo.setLeft((int) (0));
 //BA.debugLineNum = 287;BA.debugLine="Publicos.CentrarControlEnPanel(Panel1, lblTitulo,";
mostCurrent._vvvvvvvvvvvvvvv7._vv0(mostCurrent.activityBA,mostCurrent._panel1,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._lbltitulo.getObject())),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 288;BA.debugLine="Publicos.SetLabelTextSize(lblTitulo, lblTitulo.Te";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv4(mostCurrent.activityBA,mostCurrent._lbltitulo,mostCurrent._lbltitulo.getText(),(float) (20),(float) (3),(int) (80));
 //BA.debugLineNum = 290;BA.debugLine="imgIcono.Height = Panel1.Height * 0.6";
mostCurrent._imgicono.setHeight((int) (mostCurrent._panel1.getHeight()*0.6));
 //BA.debugLineNum = 291;BA.debugLine="imgIcono.Width = imgIcono.Height";
mostCurrent._imgicono.setWidth(mostCurrent._imgicono.getHeight());
 //BA.debugLineNum = 292;BA.debugLine="imgIcono.Left = Panel1.Width - imgIcono.Width - i";
mostCurrent._imgicono.setLeft((int) (mostCurrent._panel1.getWidth()-mostCurrent._imgicono.getWidth()-mostCurrent._imgmenu.getLeft()));
 //BA.debugLineNum = 293;BA.debugLine="Publicos.CentrarControlEnPanel(Panel1, imgIcono,";
mostCurrent._vvvvvvvvvvvvvvv7._vv0(mostCurrent.activityBA,mostCurrent._panel1,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._imgicono.getObject())),anywheresoftware.b4a.keywords.Common.False,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 297;BA.debugLine="imgScreenshot.Height = Panel2.Height*0.98";
mostCurrent._imgscreenshot.setHeight((int) (mostCurrent._panel2.getHeight()*0.98));
 //BA.debugLineNum = 299;BA.debugLine="imgScreenshot.Width = imgScreenshot.Height * Publ";
mostCurrent._imgscreenshot.setWidth((int) (mostCurrent._imgscreenshot.getHeight()*mostCurrent._vvvvvvvvvvvvvvv7._g_devicevalueswidth/(double)mostCurrent._vvvvvvvvvvvvvvv7._g_devicevaluesheight));
 //BA.debugLineNum = 300;BA.debugLine="Publicos.CentrarControlEnPanel(Panel4, imgScreens";
mostCurrent._vvvvvvvvvvvvvvv7._vv0(mostCurrent.activityBA,mostCurrent._panel4,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._imgscreenshot.getObject())),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 301;BA.debugLine="imgScreenshot.Top = Panel4.Height*0.01";
mostCurrent._imgscreenshot.setTop((int) (mostCurrent._panel4.getHeight()*0.01));
 //BA.debugLineNum = 304;BA.debugLine="lblPanel3.TextColor = Colors.white";
mostCurrent._lblpanel3.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 305;BA.debugLine="lblPanel3.Text = \"ESCRIBE UN COMENTARIO\"";
mostCurrent._lblpanel3.setText((Object)("ESCRIBE UN COMENTARIO"));
 //BA.debugLineNum = 306;BA.debugLine="lblPanel3.Width = Panel3.Width";
mostCurrent._lblpanel3.setWidth(mostCurrent._panel3.getWidth());
 //BA.debugLineNum = 307;BA.debugLine="lblPanel3.Height = Panel3.Height * 0.6";
mostCurrent._lblpanel3.setHeight((int) (mostCurrent._panel3.getHeight()*0.6));
 //BA.debugLineNum = 308;BA.debugLine="Publicos.CentrarControlEnPanel(Panel3, lblPanel3,";
mostCurrent._vvvvvvvvvvvvvvv7._vv0(mostCurrent.activityBA,mostCurrent._panel3,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._lblpanel3.getObject())),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 309;BA.debugLine="lblPanel3.TextSize = lblTitulo.textsize";
mostCurrent._lblpanel3.setTextSize(mostCurrent._lbltitulo.getTextSize());
 //BA.debugLineNum = 316;BA.debugLine="txtMensajeJugador.Width = Panel4.Width * 0.9";
mostCurrent._txtmensajejugador.setWidth((int) (mostCurrent._panel4.getWidth()*0.9));
 //BA.debugLineNum = 317;BA.debugLine="txtMensajeJugador.Height = Panel4.Height";
mostCurrent._txtmensajejugador.setHeight(mostCurrent._panel4.getHeight());
 //BA.debugLineNum = 318;BA.debugLine="txtMensajeJugador.Gravity = Gravity.TOP+Gravity.C";
mostCurrent._txtmensajejugador.setGravity((int) (anywheresoftware.b4a.keywords.Common.Gravity.TOP+anywheresoftware.b4a.keywords.Common.Gravity.CENTER_HORIZONTAL));
 //BA.debugLineNum = 319;BA.debugLine="txtMensajeJugador.Text = \"\"";
mostCurrent._txtmensajejugador.setText((Object)(""));
 //BA.debugLineNum = 320;BA.debugLine="txtMensajeJugador.TextColor = iColorLetra";
mostCurrent._txtmensajejugador.setTextColor(_icolorletra);
 //BA.debugLineNum = 321;BA.debugLine="txtMensajeJugador.TextSize = 20";
mostCurrent._txtmensajejugador.setTextSize((float) (20));
 //BA.debugLineNum = 322;BA.debugLine="Publicos.CentrarControlEnPanel(Panel4, txtMensaje";
mostCurrent._vvvvvvvvvvvvvvv7._vv0(mostCurrent.activityBA,mostCurrent._panel4,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._txtmensajejugador.getObject())),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 323;BA.debugLine="txtMensajeJugador.BringToFront";
mostCurrent._txtmensajejugador.BringToFront();
 //BA.debugLineNum = 324;BA.debugLine="txtMensajeJugador.SingleLine = False";
mostCurrent._txtmensajejugador.setSingleLine(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 329;BA.debugLine="imgCancelar.Height = Panel6.Height * 0.70";
mostCurrent._imgcancelar.setHeight((int) (mostCurrent._panel6.getHeight()*0.70));
 //BA.debugLineNum = 330;BA.debugLine="imgCancelar.Width = imgCancelar.Height";
mostCurrent._imgcancelar.setWidth(mostCurrent._imgcancelar.getHeight());
 //BA.debugLineNum = 331;BA.debugLine="imgPublicar.Height = imgCancelar.Height";
mostCurrent._imgpublicar.setHeight(mostCurrent._imgcancelar.getHeight());
 //BA.debugLineNum = 332;BA.debugLine="imgPublicar.Width = imgCancelar.Width";
mostCurrent._imgpublicar.setWidth(mostCurrent._imgcancelar.getWidth());
 //BA.debugLineNum = 334;BA.debugLine="lblcancelar.TextColor = iColorLetra";
mostCurrent._lblcancelar.setTextColor(_icolorletra);
 //BA.debugLineNum = 335;BA.debugLine="lblPublicar.TextColor = iColorLetra";
mostCurrent._lblpublicar.setTextColor(_icolorletra);
 //BA.debugLineNum = 336;BA.debugLine="lblcancelar.Width = Panel6.Width * 0.2";
mostCurrent._lblcancelar.setWidth((int) (mostCurrent._panel6.getWidth()*0.2));
 //BA.debugLineNum = 337;BA.debugLine="lblPublicar.Width = lblcancelar.Width";
mostCurrent._lblpublicar.setWidth(mostCurrent._lblcancelar.getWidth());
 //BA.debugLineNum = 339;BA.debugLine="lblcancelar.Height = Panel6.Height * 0.2";
mostCurrent._lblcancelar.setHeight((int) (mostCurrent._panel6.getHeight()*0.2));
 //BA.debugLineNum = 340;BA.debugLine="lblPublicar.Height = lblcancelar.Height";
mostCurrent._lblpublicar.setHeight(mostCurrent._lblcancelar.getHeight());
 //BA.debugLineNum = 342;BA.debugLine="Publicos.SetLabelTextSize(lblcancelar, \"PUBLICAR\"";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv4(mostCurrent.activityBA,mostCurrent._lblcancelar,"PUBLICAR",(float) (20),(float) (6),(int) (100));
 //BA.debugLineNum = 343;BA.debugLine="lblPublicar.TextSize=lblcancelar.textsize";
mostCurrent._lblpublicar.setTextSize(mostCurrent._lblcancelar.getTextSize());
 //BA.debugLineNum = 345;BA.debugLine="Dim aCentrar() As View";
_acentrar = new anywheresoftware.b4a.objects.ConcreteViewWrapper[(int) (0)];
{
int d0 = _acentrar.length;
for (int i0 = 0;i0 < d0;i0++) {
_acentrar[i0] = new anywheresoftware.b4a.objects.ConcreteViewWrapper();
}
}
;
 //BA.debugLineNum = 346;BA.debugLine="aCentrar = Array As View (  imgCancelar, imgPubli";
_acentrar = new anywheresoftware.b4a.objects.ConcreteViewWrapper[]{(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._imgcancelar.getObject())),(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._imgpublicar.getObject()))};
 //BA.debugLineNum = 347;BA.debugLine="Publicos.CentrarArrayControlesEnPanel ( Panel6, a";
mostCurrent._vvvvvvvvvvvvvvv7._vv6(mostCurrent.activityBA,mostCurrent._panel6,_acentrar,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 349;BA.debugLine="imgCancelar.top = 0: imgPublicar.Top = 0";
mostCurrent._imgcancelar.setTop((int) (0));
 //BA.debugLineNum = 349;BA.debugLine="imgCancelar.top = 0: imgPublicar.Top = 0";
mostCurrent._imgpublicar.setTop((int) (0));
 //BA.debugLineNum = 351;BA.debugLine="lblcancelar.Top = imgCancelar.Top + imgCancelar.H";
mostCurrent._lblcancelar.setTop((int) (mostCurrent._imgcancelar.getTop()+mostCurrent._imgcancelar.getHeight()+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (1))));
 //BA.debugLineNum = 352;BA.debugLine="lblPublicar.Top = lblcancelar.top";
mostCurrent._lblpublicar.setTop(mostCurrent._lblcancelar.getTop());
 //BA.debugLineNum = 354;BA.debugLine="lblcancelar.Left = Publicos.ViewAlinearCentro(img";
mostCurrent._lblcancelar.setLeft(mostCurrent._vvvvvvvvvvvvvvv7._vvvv0(mostCurrent.activityBA,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._imgcancelar.getObject())),(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._lblcancelar.getObject()))));
 //BA.debugLineNum = 355;BA.debugLine="lblPublicar.Left = Publicos.ViewAlinearCentro(img";
mostCurrent._lblpublicar.setLeft(mostCurrent._vvvvvvvvvvvvvvv7._vvvv0(mostCurrent.activityBA,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._imgpublicar.getObject())),(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._lblpublicar.getObject()))));
 //BA.debugLineNum = 358;BA.debugLine="End Sub";
return "";
}
}
