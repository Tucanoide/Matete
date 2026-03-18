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

public class facebookactivity extends Activity implements B4AActivity{
	public static facebookactivity mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "com.matetejuego.free", "com.matetejuego.free.facebookactivity");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (facebookactivity).");
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
		activityBA = new BA(this, layout, processBA, "com.matetejuego.free", "com.matetejuego.free.facebookactivity");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.matetejuego.free.facebookactivity", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (facebookactivity) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (facebookactivity) Resume **");
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
		return facebookactivity.class;
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
        BA.LogInfo("** Activity (facebookactivity) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
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
            BA.LogInfo("** Activity (facebookactivity) Resume **");
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
public static String _vvvvvvvvvv6 = "";
public static String _vvvvvvvvvv7 = "";
public static String _vvvvvvvvvv0 = "";
public static int _vvvvvvvvvvv1 = 0;
public static String _vvvvvvvvvvv2 = "";
public static String _vvvvvvvvvvv3 = "";
public static boolean _vvvvvvvvvvv4 = false;
public static boolean _vvvvvvvvvvv5 = false;
public static com.datasteam.b4a.socialapi.FacebookProvider _vvvvvvvvvvv6 = null;
public static String _vvvvvvvvvvv7 = "";
public static int _vvvvvvvvvvv0 = 0;
public static String _g_dirgrabable = "";
public static anyhwheresoftware.b4a.objects.sqlcipher.SQLCipher _g_sqlbaselocalusuario = null;
public anywheresoftware.b4a.objects.EditTextWrapper _txtmensajejugador = null;
public com.datasteam.b4a.socialapi.BaseProviderActivity _vvvvvvvvvvvvvvvvvvvvvv3 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblmensajematete = null;
public anywheresoftware.b4a.objects.Timer _vvvvvvvvvvvvvvvvvvvvvvvv6 = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel1 = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel2 = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel4 = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel6 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imgscreenshot = null;
public anywheresoftware.b4a.objects.LabelWrapper _vvvvvvvvvvvvvvvvvvvvvvvvv4 = null;
public anywheresoftware.b4a.objects.LabelWrapper _vvvvvvvvvvvvvvvvvvvvvvvvv5 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbltitulo = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imgiconofacebook = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imgmenu = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel3 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblpanel3 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imgcancelar = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imgpublicar = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblcancelar = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblpublicar = null;
public com.matetejuego.free.main _vvvvvvvvvvvvvvv6 = null;
public com.matetejuego.free.publicos _vvvvvvvvvvvvvvv7 = null;
public com.matetejuego.free.jugar _vvvvvvvvvvvvvvv0 = null;
public com.matetejuego.free.twitter _vvvvvvvvvvvvvvvv2 = null;
public com.matetejuego.free.svcbaseremota _vvvvvvvvvvvvvvvv3 = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 61;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 63;BA.debugLine="Activity.LoadLayout(\"Facebook\")";
mostCurrent._activity.LoadLayout("Facebook",mostCurrent.activityBA);
 //BA.debugLineNum = 66;BA.debugLine="If FirstTime Then";
if (_firsttime) { 
 //BA.debugLineNum = 67;BA.debugLine="g_DirGrabable = Publicos.Get_DirectorioBase";
_g_dirgrabable = mostCurrent._vvvvvvvvvvvvvvv7._get_directoriobase(mostCurrent.activityBA);
 };
 //BA.debugLineNum = 70;BA.debugLine="timerConectar.initialize(\"TimerConectar\",0)";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvvvv6.Initialize(processBA,"TimerConectar",(long) (0));
 //BA.debugLineNum = 71;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 95;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 97;BA.debugLine="If UserClosed Then";
if (_userclosed) { 
 //BA.debugLineNum = 98;BA.debugLine="StartActivity(\"Jugar\")";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)("Jugar"));
 //BA.debugLineNum = 99;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 };
 //BA.debugLineNum = 101;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 73;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 75;BA.debugLine="imgScreenshot.Bitmap = LoadBitmap(g_DirGrabable,";
mostCurrent._imgscreenshot.setBitmap((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(_g_dirgrabable,"PruebaSS.png").getObject()));
 //BA.debugLineNum = 77;BA.debugLine="Publicos.CargaDeviceValues";
mostCurrent._vvvvvvvvvvvvvvv7._vv4(mostCurrent.activityBA);
 //BA.debugLineNum = 79;BA.debugLine="V2_SetPantalla";
_v2_setpantalla();
 //BA.debugLineNum = 80;BA.debugLine="DoEvents";
anywheresoftware.b4a.keywords.Common.DoEvents();
 //BA.debugLineNum = 82;BA.debugLine="Facebook.SetActivity(ThisActivity.Initialize(\"fac";
_vvvvvvvvvvv6.SetActivity(mostCurrent._vvvvvvvvvvvvvvvvvvvvvv3.Initialize(mostCurrent.activityBA,"facebook"));
 //BA.debugLineNum = 84;BA.debugLine="txtMensajeJugador.Text = sMensajeJugador";
mostCurrent._txtmensajejugador.setText((Object)(_vvvvvvvvvvv7));
 //BA.debugLineNum = 87;BA.debugLine="If Facebook.Connected = False Then";
if (_vvvvvvvvvvv6.getConnected()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 88;BA.debugLine="timerConectar.Interval = 3000";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvvvv6.setInterval((long) (3000));
 //BA.debugLineNum = 89;BA.debugLine="timerConectar.Enabled = True";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvvvv6.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 91;BA.debugLine="timerConectar.Enabled  = False";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvvvv6.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 93;BA.debugLine="End Sub";
return "";
}
public static String  _btnpegar_click() throws Exception{
 //BA.debugLineNum = 314;BA.debugLine="Sub btnpegar_click";
 //BA.debugLineNum = 316;BA.debugLine="End Sub";
return "";
}
public static String  _facebook_connected(com.datasteam.b4a.socialapi.BaseProvider _provider) throws Exception{
 //BA.debugLineNum = 220;BA.debugLine="Sub Facebook_Connected (Provider As SocialApiProvi";
 //BA.debugLineNum = 221;BA.debugLine="Try";
try { //BA.debugLineNum = 222;BA.debugLine="If bPrenderFacebook Then";
if (_vvvvvvvvvvv5) { 
 //BA.debugLineNum = 223;BA.debugLine="If Not(Facebook.HasPermission(Facebook.Constants";
if (anywheresoftware.b4a.keywords.Common.Not(_vvvvvvvvvvv6.HasPermission(_vvvvvvvvvvv6.Constants.Permissions.PUBLISH_ACTIONS))) { 
 //BA.debugLineNum = 224;BA.debugLine="Facebook.RequestPublishPermissions";
_vvvvvvvvvvv6.RequestPublishPermissions(processBA);
 };
 //BA.debugLineNum = 226;BA.debugLine="If bBotonFacebookPresionado Then";
if (_vvvvvvvvvvv4) { 
 //BA.debugLineNum = 227;BA.debugLine="bBotonFacebookPresionado = False";
_vvvvvvvvvvv4 = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 228;BA.debugLine="FacebookPublicar";
_vvvvvvvvvvvvvvvvvvvvvvvv7();
 };
 };
 } 
       catch (Exception e12) {
			processBA.setLastException(e12); //BA.debugLineNum = 232;BA.debugLine="Publicos.ManejaError(\"Conexion Facebook\", LastExc";
mostCurrent._vvvvvvvvvvvvvvv7._vvv7(mostCurrent.activityBA,"Conexion Facebook",anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage());
 };
 //BA.debugLineNum = 235;BA.debugLine="End Sub";
return "";
}
public static String  _facebook_disconnected(com.datasteam.b4a.socialapi.BaseProvider _provider) throws Exception{
 //BA.debugLineNum = 206;BA.debugLine="Sub Facebook_Disconnected (Provider As SocialApiPr";
 //BA.debugLineNum = 208;BA.debugLine="End Sub";
return "";
}
public static String  _facebook_event(com.datasteam.b4a.socialapi.BaseProvider _provider) throws Exception{
 //BA.debugLineNum = 216;BA.debugLine="Sub Facebook_Event (Provider As SocialApiProvider)";
 //BA.debugLineNum = 218;BA.debugLine="End Sub";
return "";
}
public static String  _facebook_failed(com.datasteam.b4a.socialapi.BaseProvider _provider) throws Exception{
 //BA.debugLineNum = 210;BA.debugLine="Sub Facebook_Failed (Provider As SocialApiProvider";
 //BA.debugLineNum = 214;BA.debugLine="End Sub";
return "";
}
public static boolean  _facebook_updateuso() throws Exception{
String _ssql = "";
boolean _bret = false;
String _sfecha = "";
 //BA.debugLineNum = 285;BA.debugLine="Sub Facebook_UpdateUso  As Boolean";
 //BA.debugLineNum = 287;BA.debugLine="Dim sSql As String, bRet As Boolean, sFecha As Str";
_ssql = "";
_bret = false;
_sfecha = "";
 //BA.debugLineNum = 289;BA.debugLine="Try";
try { //BA.debugLineNum = 290;BA.debugLine="sSql = \"Update Avance Set publicofacebook=1 wher";
_ssql = "Update Avance Set publicofacebook=1 where idPalabra = (select max(idpalabra) from avance)";
 //BA.debugLineNum = 291;BA.debugLine="g_sqlBaseLocalUsuario.ExecNonQuery (sSql)";
_g_sqlbaselocalusuario.ExecNonQuery(_ssql);
 //BA.debugLineNum = 292;BA.debugLine="bRet = True";
_bret = anywheresoftware.b4a.keywords.Common.True;
 } 
       catch (Exception e7) {
			processBA.setLastException(e7); //BA.debugLineNum = 294;BA.debugLine="bRet = False";
_bret = anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 296;BA.debugLine="Return bRet";
if (true) return _bret;
 //BA.debugLineNum = 297;BA.debugLine="End Sub";
return false;
}
public static boolean  _vvvvvvvvvvvvvvvvvvvvvvvv0() throws Exception{
com.datasteam.b4a.socialapi.FacebookProvider.Result _result = null;
 //BA.debugLineNum = 132;BA.debugLine="Sub FacebookConectar As Boolean";
 //BA.debugLineNum = 136;BA.debugLine="Try";
try { //BA.debugLineNum = 137;BA.debugLine="Dim Result As FacebookResult";
_result = new com.datasteam.b4a.socialapi.FacebookProvider.Result();
 //BA.debugLineNum = 138;BA.debugLine="bBotonFacebookPresionado=True";
_vvvvvvvvvvv4 = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 139;BA.debugLine="If Facebook.Connected = False Then";
if (_vvvvvvvvvvv6.getConnected()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 140;BA.debugLine="Result = Facebook.Login(Array As String (Faceboo";
_result = (com.datasteam.b4a.socialapi.FacebookProvider.Result)(_vvvvvvvvvvv6.Login(new String[]{_vvvvvvvvvvv6.Constants.Permissions.EMAIL}));
 };
 } 
       catch (Exception e8) {
			processBA.setLastException(e8); //BA.debugLineNum = 143;BA.debugLine="Publicos.ManejaError(\"Conexion Facebook\", LastExc";
mostCurrent._vvvvvvvvvvvvvvv7._vvv7(mostCurrent.activityBA,"Conexion Facebook",anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage());
 };
 //BA.debugLineNum = 145;BA.debugLine="Return Facebook.Connected";
if (true) return _vvvvvvvvvvv6.getConnected();
 //BA.debugLineNum = 146;BA.debugLine="End Sub";
return false;
}
public static boolean  _vvvvvvvvvvvvvvvvvvvvvvvvv1() throws Exception{
boolean _haspublishpermission = false;
int _i = 0;
 //BA.debugLineNum = 152;BA.debugLine="Sub FacebookGetTienePermisoPublicar As Boolean";
 //BA.debugLineNum = 154;BA.debugLine="Try";
try { //BA.debugLineNum = 155;BA.debugLine="Dim HasPublishPermission As Boolean = False";
_haspublishpermission = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 156;BA.debugLine="For I = 0 To Facebook.Permissions.Length-1";
{
final int step3 = 1;
final int limit3 = (int) (_vvvvvvvvvvv6.getPermissions().length-1);
for (_i = (int) (0) ; (step3 > 0 && _i <= limit3) || (step3 < 0 && _i >= limit3); _i = ((int)(0 + _i + step3)) ) {
 //BA.debugLineNum = 157;BA.debugLine="If Facebook.Permissions(I) = Facebook.Cons";
if ((_vvvvvvvvvvv6.getPermissions()[_i]).equals(_vvvvvvvvvvv6.Constants.Permissions.PUBLISH_ACTIONS)) { 
 //BA.debugLineNum = 158;BA.debugLine="HasPublishPermission = True";
_haspublishpermission = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 159;BA.debugLine="Exit";
if (true) break;
 };
 }
};
 } 
       catch (Exception e10) {
			processBA.setLastException(e10); //BA.debugLineNum = 163;BA.debugLine="Publicos.ManejaError(\"FacebookPermisoPublicar\", L";
mostCurrent._vvvvvvvvvvvvvvv7._vvv7(mostCurrent.activityBA,"FacebookPermisoPublicar",anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage());
 };
 //BA.debugLineNum = 168;BA.debugLine="Return HasPublishPermission";
if (true) return _haspublishpermission;
 //BA.debugLineNum = 169;BA.debugLine="End Sub";
return false;
}
public static String  _vvvvvvvvvvvvvvvvvvvvvvvvv2() throws Exception{
 //BA.debugLineNum = 148;BA.debugLine="Sub FacebookPidePermisoPublicar";
 //BA.debugLineNum = 149;BA.debugLine="Facebook.RequestPublishPermissions";
_vvvvvvvvvvv6.RequestPublishPermissions(processBA);
 //BA.debugLineNum = 150;BA.debugLine="End Sub";
return "";
}
public static boolean  _vvvvvvvvvvvvvvvvvvvvvvvvv3() throws Exception{
com.datasteam.b4a.socialapi.FacebookProvider.Result _result = null;
boolean _bret = false;
String _sfrase = "";
anywheresoftware.b4a.objects.StringUtils _sencoded = null;
 //BA.debugLineNum = 237;BA.debugLine="Sub FacebookPostear As Boolean";
 //BA.debugLineNum = 238;BA.debugLine="Dim Result As FacebookResult, bRet As Boolean = F";
_result = new com.datasteam.b4a.socialapi.FacebookProvider.Result();
_bret = anywheresoftware.b4a.keywords.Common.False;
_result = new com.datasteam.b4a.socialapi.FacebookProvider.Result();
 //BA.debugLineNum = 240;BA.debugLine="Dim Result As FacebookResult";
_result = new com.datasteam.b4a.socialapi.FacebookProvider.Result();
 //BA.debugLineNum = 241;BA.debugLine="Dim sFrase As String, sDefinicion As String";
_sfrase = "";
_vvvvvvvvvv7 = "";
 //BA.debugLineNum = 242;BA.debugLine="Facebook.RefreshPermissions";
_vvvvvvvvvvv6.RefreshPermissions();
 //BA.debugLineNum = 245;BA.debugLine="sFrase = txtMensajeJugador.Text";
_sfrase = mostCurrent._txtmensajejugador.getText();
 //BA.debugLineNum = 255;BA.debugLine="Dim sEncoded As StringUtils";
_sencoded = new anywheresoftware.b4a.objects.StringUtils();
 //BA.debugLineNum = 256;BA.debugLine="sFrase= sEncoded.EncodeUrl(sFrase, \"UTF8\")";
_sfrase = _sencoded.EncodeUrl(_sfrase,"UTF8");
 //BA.debugLineNum = 257;BA.debugLine="Result = Facebook.UploadPhoto(g_DirGrabable, \"Pru";
_result = _vvvvvvvvvvv6.UploadPhoto(_g_dirgrabable,"PruebaSS.png",_sfrase,"");
 //BA.debugLineNum = 267;BA.debugLine="If Result = Null Then";
if (_result== null) { 
 //BA.debugLineNum = 268;BA.debugLine="Msgbox(\"Publicación anterior pendiente.\", \"Faceb";
anywheresoftware.b4a.keywords.Common.Msgbox("Publicación anterior pendiente.","Facebook",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 270;BA.debugLine="If Result.Validated Then";
if (_result.Validated) { 
 //BA.debugLineNum = 271;BA.debugLine="If Result.Canceled Then Return";
if (_result.Canceled) { 
if (true) return false;};
 //BA.debugLineNum = 273;BA.debugLine="If Result.Success Then";
if (_result.Success) { 
 //BA.debugLineNum = 274;BA.debugLine="bRet = True";
_bret = anywheresoftware.b4a.keywords.Common.True;
 }else {
 //BA.debugLineNum = 276;BA.debugLine="ToastMessageShow(\"No fue posible publicar en t";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("No fue posible publicar en tu muro!",anywheresoftware.b4a.keywords.Common.True);
 };
 };
 };
 //BA.debugLineNum = 281;BA.debugLine="Return bRet";
if (true) return _bret;
 //BA.debugLineNum = 282;BA.debugLine="End Sub";
return false;
}
public static String  _vvvvvvvvvvvvvvvvvvvvvvvv7() throws Exception{
 //BA.debugLineNum = 172;BA.debugLine="Sub FacebookPublicar";
 //BA.debugLineNum = 175;BA.debugLine="If Facebook.Connected Then";
if (_vvvvvvvvvvv6.getConnected()) { 
 //BA.debugLineNum = 177;BA.debugLine="If bBotonFacebookPresionado Then ' si hizo clic";
if (_vvvvvvvvvvv4) { 
 //BA.debugLineNum = 178;BA.debugLine="bBotonFacebookPresionado = False";
_vvvvvvvvvvv4 = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 180;BA.debugLine="If FacebookGetTienePermisoPublicar = False Then";
if (_vvvvvvvvvvvvvvvvvvvvvvvvv1()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 182;BA.debugLine="FacebookPidePermisoPublicar";
_vvvvvvvvvvvvvvvvvvvvvvvvv2();
 };
 //BA.debugLineNum = 184;BA.debugLine="If FacebookGetTienePermisoPublicar Then";
if (_vvvvvvvvvvvvvvvvvvvvvvvvv1()) { 
 //BA.debugLineNum = 186;BA.debugLine="If FacebookPostear Then";
if (_vvvvvvvvvvvvvvvvvvvvvvvvv3()) { 
 //BA.debugLineNum = 187;BA.debugLine="Publicos.Usuario_SumarNeuronas(iCostoLetra, g";
mostCurrent._vvvvvvvvvvvvvvv7._usuario_sumarneuronas(mostCurrent.activityBA,_vvvvvvvvvvv1,_g_sqlbaselocalusuario);
 //BA.debugLineNum = 188;BA.debugLine="Facebook_UpdateUso";
_facebook_updateuso();
 //BA.debugLineNum = 189;BA.debugLine="Jugar.bPublicoEnFacebook = True";
mostCurrent._vvvvvvvvvvvvvvv0._vvvvvvvvv6 = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 190;BA.debugLine="StartActivity(\"Jugar\")";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)("Jugar"));
 //BA.debugLineNum = 191;BA.debugLine="Activity.finish";
mostCurrent._activity.Finish();
 }else {
 //BA.debugLineNum = 193;BA.debugLine="ToastMessageShow(\"No fue posible publicar en";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("No fue posible publicar en tu muro!",anywheresoftware.b4a.keywords.Common.True);
 };
 }else {
 //BA.debugLineNum = 196;BA.debugLine="ToastMessageShow(\"No fue posible publicar en t";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("No fue posible publicar en tu muro!",anywheresoftware.b4a.keywords.Common.True);
 };
 };
 }else {
 //BA.debugLineNum = 201;BA.debugLine="ToastMessageShow(\"No fue posible publicar en tu";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("No fue posible publicar en tu muro!",anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 204;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 31;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 35;BA.debugLine="Private txtMensajeJugador As EditText";
mostCurrent._txtmensajejugador = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 36;BA.debugLine="Dim ThisActivity As SocialApiActivity";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvv3 = new com.datasteam.b4a.socialapi.BaseProviderActivity();
 //BA.debugLineNum = 38;BA.debugLine="Private lblMensajeMatete As Label";
mostCurrent._lblmensajematete = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 40;BA.debugLine="Private timerConectar As Timer";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvvvv6 = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 43;BA.debugLine="Private Panel1 As Panel";
mostCurrent._panel1 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 44;BA.debugLine="Private Panel2 As Panel";
mostCurrent._panel2 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 45;BA.debugLine="Private Panel4 As Panel";
mostCurrent._panel4 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 46;BA.debugLine="Private Panel6 As Panel";
mostCurrent._panel6 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 47;BA.debugLine="Private imgScreenshot As ImageView";
mostCurrent._imgscreenshot = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 48;BA.debugLine="Private lblOpcion1 As Label";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvvvvv4 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 49;BA.debugLine="Private lblOpcion2 As Label";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvvvvv5 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 50;BA.debugLine="Private lblTitulo As Label";
mostCurrent._lbltitulo = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 51;BA.debugLine="Private imgIconoFacebook As ImageView";
mostCurrent._imgiconofacebook = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 52;BA.debugLine="Private imgMenu As ImageView";
mostCurrent._imgmenu = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 53;BA.debugLine="Private Panel3 As Panel";
mostCurrent._panel3 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 54;BA.debugLine="Private lblPanel3 As Label";
mostCurrent._lblpanel3 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 55;BA.debugLine="Private imgCancelar As ImageView";
mostCurrent._imgcancelar = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 56;BA.debugLine="Private imgPublicar As ImageView";
mostCurrent._imgpublicar = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 57;BA.debugLine="Private lblCancelar As Label";
mostCurrent._lblcancelar = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 58;BA.debugLine="Private lblPublicar As Label";
mostCurrent._lblpublicar = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 59;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvvvvvvvvvv3(boolean _c,String _trueres,String _falseres) throws Exception{
 //BA.debugLineNum = 439;BA.debugLine="Sub IIF(c As Boolean, TrueRes As String, FalseRes";
 //BA.debugLineNum = 440;BA.debugLine="If c Then Return TrueRes Else Return FalseRes";
if (_c) { 
if (true) return _trueres;}
else {
if (true) return _falseres;};
 //BA.debugLineNum = 441;BA.debugLine="End Sub";
return "";
}
public static String  _imgcancelar_click() throws Exception{
 //BA.debugLineNum = 434;BA.debugLine="Sub imgCancelar_Click";
 //BA.debugLineNum = 435;BA.debugLine="lblCancelar_click";
_lblcancelar_click();
 //BA.debugLineNum = 436;BA.debugLine="End Sub";
return "";
}
public static String  _imgfacebook_click() throws Exception{
 //BA.debugLineNum = 308;BA.debugLine="Sub imgFacebook_Click";
 //BA.debugLineNum = 309;BA.debugLine="lblPublicar_click";
_lblpublicar_click();
 //BA.debugLineNum = 311;BA.debugLine="End Sub";
return "";
}
public static String  _imgpublicar_click() throws Exception{
 //BA.debugLineNum = 431;BA.debugLine="Sub imgPublicar_Click";
 //BA.debugLineNum = 432;BA.debugLine="lblPublicar_click";
_lblpublicar_click();
 //BA.debugLineNum = 433;BA.debugLine="End Sub";
return "";
}
public static String  _lblcancelar_click() throws Exception{
 //BA.debugLineNum = 105;BA.debugLine="Sub lblCancelar_click'cancelar";
 //BA.debugLineNum = 106;BA.debugLine="Jugar.bCanceloFacebookoTwitter = True";
mostCurrent._vvvvvvvvvvvvvvv0._vvvvvvvvv0 = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 107;BA.debugLine="StartActivity(\"Jugar\")";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)("Jugar"));
 //BA.debugLineNum = 108;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 110;BA.debugLine="End Sub";
return "";
}
public static String  _lblpublicar_click() throws Exception{
 //BA.debugLineNum = 300;BA.debugLine="Sub lblPublicar_click";
 //BA.debugLineNum = 301;BA.debugLine="If txtMensajeJugador.Text.Trim = \"\" Then";
if ((mostCurrent._txtmensajejugador.getText().trim()).equals("")) { 
 //BA.debugLineNum = 302;BA.debugLine="Msgbox(\"Debes ingresar un mensaje para publicar\",";
anywheresoftware.b4a.keywords.Common.Msgbox("Debes ingresar un mensaje para publicar","Cataplún",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 304;BA.debugLine="Publicar";
_vvvvvvvvvvvvvvvvvvvvvvvvv6();
 };
 //BA.debugLineNum = 306;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 10;BA.debugLine="Public sLetrasElegir As String";
_vvvvvvvvvv6 = "";
 //BA.debugLineNum = 11;BA.debugLine="Public sDefinicion As String";
_vvvvvvvvvv7 = "";
 //BA.debugLineNum = 12;BA.debugLine="Public sPalabra As String";
_vvvvvvvvvv0 = "";
 //BA.debugLineNum = 13;BA.debugLine="Public iCostoLetra As Int";
_vvvvvvvvvvv1 = 0;
 //BA.debugLineNum = 14;BA.debugLine="Public sLetrasPalabra As String";
_vvvvvvvvvvv2 = "";
 //BA.debugLineNum = 16;BA.debugLine="Dim sPublicarFacebook As String = \"S\"";
_vvvvvvvvvvv3 = BA.__b (new byte[] {58}, 798266);
 //BA.debugLineNum = 17;BA.debugLine="Dim bBotonFacebookPresionado As Boolean = False";
_vvvvvvvvvvv4 = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 19;BA.debugLine="Dim bPrenderFacebook As Boolean = True";
_vvvvvvvvvvv5 = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 21;BA.debugLine="Public Facebook As FacebookProvider";
_vvvvvvvvvvv6 = new com.datasteam.b4a.socialapi.FacebookProvider();
 //BA.debugLineNum = 23;BA.debugLine="Dim sMensajeJugador As String";
_vvvvvvvvvvv7 = "";
 //BA.debugLineNum = 25;BA.debugLine="Dim iIntentos As Int = 0";
_vvvvvvvvvvv0 = (int) (0);
 //BA.debugLineNum = 26;BA.debugLine="Dim g_DirGrabable As String";
_g_dirgrabable = "";
 //BA.debugLineNum = 28;BA.debugLine="Dim g_sqlBaseLocalUsuario As SQLCipher";
_g_sqlbaselocalusuario = new anyhwheresoftware.b4a.objects.sqlcipher.SQLCipher();
 //BA.debugLineNum = 29;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvvvvvvvvvvvv6() throws Exception{
String _smonedas = "";
int _iret = 0;
 //BA.debugLineNum = 112;BA.debugLine="Sub Publicar";
 //BA.debugLineNum = 113;BA.debugLine="Dim sMonedas As String, iret As Int";
_smonedas = "";
_iret = 0;
 //BA.debugLineNum = 116;BA.debugLine="iret = Msgbox2(\"¿Confirmas la publicación? Ganar";
_iret = anywheresoftware.b4a.keywords.Common.Msgbox2("¿Confirmas la publicación? Ganarás "+BA.NumberToString(_vvvvvvvvvvv1)+" neurona"+_vvvvvvvvvvvvvvvvvvvvvvv3(_vvvvvvvvvvv1>1,"s",""),"Facebook","Confirmar","Cancelar","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null),mostCurrent.activityBA);
 //BA.debugLineNum = 117;BA.debugLine="If iret = DialogResponse.POSITIVE Then";
if (_iret==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
 //BA.debugLineNum = 118;BA.debugLine="bBotonFacebookPresionado = True";
_vvvvvvvvvvv4 = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 120;BA.debugLine="If FacebookConectar Then";
if (_vvvvvvvvvvvvvvvvvvvvvvvv0()) { 
 //BA.debugLineNum = 123;BA.debugLine="FacebookPublicar";
_vvvvvvvvvvvvvvvvvvvvvvvv7();
 //BA.debugLineNum = 124;BA.debugLine="sMensajeJugador = \"\"";
_vvvvvvvvvvv7 = "";
 }else {
 //BA.debugLineNum = 126;BA.debugLine="sMensajeJugador = txtMensajeJugador.text";
_vvvvvvvvvvv7 = mostCurrent._txtmensajejugador.getText();
 //BA.debugLineNum = 127;BA.debugLine="ToastMessageShow(\"No fue posible publicar en t";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("No fue posible publicar en tu muro!",anywheresoftware.b4a.keywords.Common.True);
 };
 };
 //BA.debugLineNum = 130;BA.debugLine="End Sub";
return "";
}
public static String  _timerconectar_tick() throws Exception{
 //BA.debugLineNum = 318;BA.debugLine="Sub timerConectar_Tick";
 //BA.debugLineNum = 319;BA.debugLine="Log (\"Intentos timer: \" & iIntentos & \" \" & Facebo";
anywheresoftware.b4a.keywords.Common.Log("Intentos timer: "+BA.NumberToString(_vvvvvvvvvvv0)+" "+BA.ObjectToString(_vvvvvvvvvvv6.getConnected()));
 //BA.debugLineNum = 320;BA.debugLine="If Facebook.Connected = False AND iIntentos = 0 Th";
if (_vvvvvvvvvvv6.getConnected()==anywheresoftware.b4a.keywords.Common.False && _vvvvvvvvvvv0==0) { 
 //BA.debugLineNum = 321;BA.debugLine="iIntentos = 1";
_vvvvvvvvvvv0 = (int) (1);
 //BA.debugLineNum = 322;BA.debugLine="FacebookConectar";
_vvvvvvvvvvvvvvvvvvvvvvvv0();
 };
 //BA.debugLineNum = 324;BA.debugLine="timerConectar.Enabled = False";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvvvv6.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 325;BA.debugLine="End Sub";
return "";
}
public static String  _v2_setpantalla() throws Exception{
int _icolorletra = 0;
anywheresoftware.b4a.objects.ConcreteViewWrapper[] _acentrar = null;
 //BA.debugLineNum = 328;BA.debugLine="Sub V2_SetPantalla";
 //BA.debugLineNum = 329;BA.debugLine="Dim iColorLetra As Int";
_icolorletra = 0;
 //BA.debugLineNum = 331;BA.debugLine="iColorLetra = Colors.RGB(151, 89, 152)";
_icolorletra = anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (151),(int) (89),(int) (152));
 //BA.debugLineNum = 332;BA.debugLine="Activity.Color = Colors.white";
mostCurrent._activity.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 333;BA.debugLine="Panel1.Color = Colors.white ' menu. titulo, icono";
mostCurrent._panel1.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 334;BA.debugLine="Panel2.color = Colors.RGB(231,234,226) 'screensho";
mostCurrent._panel2.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (231),(int) (234),(int) (226)));
 //BA.debugLineNum = 335;BA.debugLine="Panel3.Color = Colors.RGB(177, 109, 177) 'subtitu";
mostCurrent._panel3.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (177),(int) (109),(int) (177)));
 //BA.debugLineNum = 336;BA.debugLine="Panel4.color = Colors.white  ' text input mensaje";
mostCurrent._panel4.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 337;BA.debugLine="Panel6.Color = Colors.white ' cancelar-publicar";
mostCurrent._panel6.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 340;BA.debugLine="Panel1.Width = 100%x : 	Panel2.Width = 100%x";
mostCurrent._panel1.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 340;BA.debugLine="Panel1.Width = 100%x : 	Panel2.Width = 100%x";
mostCurrent._panel2.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 341;BA.debugLine="Panel3.Width = 100%x : 	Panel4.Width = 100%x";
mostCurrent._panel3.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 341;BA.debugLine="Panel3.Width = 100%x : 	Panel4.Width = 100%x";
mostCurrent._panel4.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 342;BA.debugLine="Panel6.Width = 100%x";
mostCurrent._panel6.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 344;BA.debugLine="Panel1.Height = Activity.Height * 0.10: Panel1.To";
mostCurrent._panel1.setHeight((int) (mostCurrent._activity.getHeight()*0.10));
 //BA.debugLineNum = 344;BA.debugLine="Panel1.Height = Activity.Height * 0.10: Panel1.To";
mostCurrent._panel1.setTop((int) (0));
 //BA.debugLineNum = 345;BA.debugLine="Panel2.Height = Activity.Height * 0.35: Panel2.To";
mostCurrent._panel2.setHeight((int) (mostCurrent._activity.getHeight()*0.35));
 //BA.debugLineNum = 345;BA.debugLine="Panel2.Height = Activity.Height * 0.35: Panel2.To";
mostCurrent._panel2.setTop((int) (mostCurrent._panel1.getTop()+mostCurrent._panel1.getHeight()));
 //BA.debugLineNum = 346;BA.debugLine="Panel3.Height = Activity.Height * 0.10: Panel3.To";
mostCurrent._panel3.setHeight((int) (mostCurrent._activity.getHeight()*0.10));
 //BA.debugLineNum = 346;BA.debugLine="Panel3.Height = Activity.Height * 0.10: Panel3.To";
mostCurrent._panel3.setTop((int) (mostCurrent._panel2.getTop()+mostCurrent._panel2.getHeight()));
 //BA.debugLineNum = 347;BA.debugLine="Panel4.Height = Activity.Height * 0.25: Panel4.To";
mostCurrent._panel4.setHeight((int) (mostCurrent._activity.getHeight()*0.25));
 //BA.debugLineNum = 347;BA.debugLine="Panel4.Height = Activity.Height * 0.25: Panel4.To";
mostCurrent._panel4.setTop((int) (mostCurrent._panel3.getTop()+mostCurrent._panel3.getHeight()));
 //BA.debugLineNum = 348;BA.debugLine="Panel6.Height = Activity.Height * 0.14: Panel6.To";
mostCurrent._panel6.setHeight((int) (mostCurrent._activity.getHeight()*0.14));
 //BA.debugLineNum = 348;BA.debugLine="Panel6.Height = Activity.Height * 0.14: Panel6.To";
mostCurrent._panel6.setTop((int) (mostCurrent._panel4.getTop()+mostCurrent._panel4.getHeight()));
 //BA.debugLineNum = 351;BA.debugLine="imgMenu.Left = Panel1.Width * 0.05";
mostCurrent._imgmenu.setLeft((int) (mostCurrent._panel1.getWidth()*0.05));
 //BA.debugLineNum = 352;BA.debugLine="imgMenu.Height=Panel1.Height *0.5";
mostCurrent._imgmenu.setHeight((int) (mostCurrent._panel1.getHeight()*0.5));
 //BA.debugLineNum = 353;BA.debugLine="imgMenu.Width=imgMenu.Height";
mostCurrent._imgmenu.setWidth(mostCurrent._imgmenu.getHeight());
 //BA.debugLineNum = 354;BA.debugLine="Publicos.CentrarControlEnPanel(Panel1, imgMenu, F";
mostCurrent._vvvvvvvvvvvvvvv7._vv0(mostCurrent.activityBA,mostCurrent._panel1,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._imgmenu.getObject())),anywheresoftware.b4a.keywords.Common.False,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 356;BA.debugLine="lblTitulo.Text = \"PUBLICAR EN FACEBOOK\"";
mostCurrent._lbltitulo.setText((Object)("PUBLICAR EN FACEBOOK"));
 //BA.debugLineNum = 357;BA.debugLine="lblTitulo.Height = Panel1.Height*0.5";
mostCurrent._lbltitulo.setHeight((int) (mostCurrent._panel1.getHeight()*0.5));
 //BA.debugLineNum = 358;BA.debugLine="lblTitulo.Width = 70%x";
mostCurrent._lbltitulo.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (70),mostCurrent.activityBA));
 //BA.debugLineNum = 359;BA.debugLine="lblTitulo.textcolor = Colors.RGB(177, 109, 177)";
mostCurrent._lbltitulo.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (177),(int) (109),(int) (177)));
 //BA.debugLineNum = 360;BA.debugLine="lblTitulo.Left = 0";
mostCurrent._lbltitulo.setLeft((int) (0));
 //BA.debugLineNum = 361;BA.debugLine="Publicos.CentrarControlEnPanel(Panel1, lblTitulo,";
mostCurrent._vvvvvvvvvvvvvvv7._vv0(mostCurrent.activityBA,mostCurrent._panel1,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._lbltitulo.getObject())),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 362;BA.debugLine="Publicos.SetLabelTextSize(lblTitulo, lblTitulo.Te";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv4(mostCurrent.activityBA,mostCurrent._lbltitulo,mostCurrent._lbltitulo.getText(),(float) (20),(float) (3),(int) (80));
 //BA.debugLineNum = 364;BA.debugLine="imgIconoFacebook.Height = Panel1.Height * 0.6";
mostCurrent._imgiconofacebook.setHeight((int) (mostCurrent._panel1.getHeight()*0.6));
 //BA.debugLineNum = 365;BA.debugLine="imgIconoFacebook.Width = imgIconoFacebook.Height";
mostCurrent._imgiconofacebook.setWidth(mostCurrent._imgiconofacebook.getHeight());
 //BA.debugLineNum = 366;BA.debugLine="imgIconoFacebook.Left = Panel1.Width - imgIconoFa";
mostCurrent._imgiconofacebook.setLeft((int) (mostCurrent._panel1.getWidth()-mostCurrent._imgiconofacebook.getWidth()-mostCurrent._imgmenu.getLeft()));
 //BA.debugLineNum = 367;BA.debugLine="Publicos.CentrarControlEnPanel(Panel1, imgIconoFa";
mostCurrent._vvvvvvvvvvvvvvv7._vv0(mostCurrent.activityBA,mostCurrent._panel1,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._imgiconofacebook.getObject())),anywheresoftware.b4a.keywords.Common.False,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 371;BA.debugLine="imgScreenshot.Height = Panel2.Height*0.98";
mostCurrent._imgscreenshot.setHeight((int) (mostCurrent._panel2.getHeight()*0.98));
 //BA.debugLineNum = 373;BA.debugLine="imgScreenshot.Width = imgScreenshot.Height * Publ";
mostCurrent._imgscreenshot.setWidth((int) (mostCurrent._imgscreenshot.getHeight()*mostCurrent._vvvvvvvvvvvvvvv7._g_devicevalueswidth/(double)mostCurrent._vvvvvvvvvvvvvvv7._g_devicevaluesheight));
 //BA.debugLineNum = 374;BA.debugLine="Publicos.CentrarControlEnPanel(Panel4, imgScreens";
mostCurrent._vvvvvvvvvvvvvvv7._vv0(mostCurrent.activityBA,mostCurrent._panel4,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._imgscreenshot.getObject())),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 375;BA.debugLine="imgScreenshot.Top = Panel4.Height*0.01";
mostCurrent._imgscreenshot.setTop((int) (mostCurrent._panel4.getHeight()*0.01));
 //BA.debugLineNum = 378;BA.debugLine="lblPanel3.TextColor = Colors.white";
mostCurrent._lblpanel3.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 379;BA.debugLine="lblPanel3.Text = \"ESCRIBE UN COMENTARIO\"";
mostCurrent._lblpanel3.setText((Object)("ESCRIBE UN COMENTARIO"));
 //BA.debugLineNum = 380;BA.debugLine="lblPanel3.Width = Panel3.Width";
mostCurrent._lblpanel3.setWidth(mostCurrent._panel3.getWidth());
 //BA.debugLineNum = 381;BA.debugLine="lblPanel3.Height = Panel3.Height * 0.6";
mostCurrent._lblpanel3.setHeight((int) (mostCurrent._panel3.getHeight()*0.6));
 //BA.debugLineNum = 382;BA.debugLine="Publicos.CentrarControlEnPanel(Panel3, lblPanel3,";
mostCurrent._vvvvvvvvvvvvvvv7._vv0(mostCurrent.activityBA,mostCurrent._panel3,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._lblpanel3.getObject())),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 383;BA.debugLine="lblPanel3.TextSize = lblTitulo.textsize";
mostCurrent._lblpanel3.setTextSize(mostCurrent._lbltitulo.getTextSize());
 //BA.debugLineNum = 388;BA.debugLine="txtMensajeJugador.Width = Panel4.Width * 0.9";
mostCurrent._txtmensajejugador.setWidth((int) (mostCurrent._panel4.getWidth()*0.9));
 //BA.debugLineNum = 389;BA.debugLine="txtMensajeJugador.Height = Panel4.Height";
mostCurrent._txtmensajejugador.setHeight(mostCurrent._panel4.getHeight());
 //BA.debugLineNum = 390;BA.debugLine="txtMensajeJugador.Gravity = Gravity.TOP+Gravity.l";
mostCurrent._txtmensajejugador.setGravity((int) (anywheresoftware.b4a.keywords.Common.Gravity.TOP+anywheresoftware.b4a.keywords.Common.Gravity.LEFT));
 //BA.debugLineNum = 391;BA.debugLine="txtMensajeJugador.Text = \"\"";
mostCurrent._txtmensajejugador.setText((Object)(""));
 //BA.debugLineNum = 392;BA.debugLine="txtMensajeJugador.TextColor = iColorLetra";
mostCurrent._txtmensajejugador.setTextColor(_icolorletra);
 //BA.debugLineNum = 393;BA.debugLine="txtMensajeJugador.TextSize = 20";
mostCurrent._txtmensajejugador.setTextSize((float) (20));
 //BA.debugLineNum = 394;BA.debugLine="Publicos.CentrarControlEnPanel(Panel4, txtMensaje";
mostCurrent._vvvvvvvvvvvvvvv7._vv0(mostCurrent.activityBA,mostCurrent._panel4,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._txtmensajejugador.getObject())),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 395;BA.debugLine="txtMensajeJugador.SingleLine = False";
mostCurrent._txtmensajejugador.setSingleLine(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 399;BA.debugLine="imgCancelar.Height = Panel6.Height * 0.70";
mostCurrent._imgcancelar.setHeight((int) (mostCurrent._panel6.getHeight()*0.70));
 //BA.debugLineNum = 400;BA.debugLine="imgCancelar.Width = imgCancelar.Height";
mostCurrent._imgcancelar.setWidth(mostCurrent._imgcancelar.getHeight());
 //BA.debugLineNum = 401;BA.debugLine="imgPublicar.Height = imgCancelar.Height";
mostCurrent._imgpublicar.setHeight(mostCurrent._imgcancelar.getHeight());
 //BA.debugLineNum = 402;BA.debugLine="imgPublicar.Width = imgCancelar.Width";
mostCurrent._imgpublicar.setWidth(mostCurrent._imgcancelar.getWidth());
 //BA.debugLineNum = 404;BA.debugLine="lblCancelar.TextColor = iColorLetra";
mostCurrent._lblcancelar.setTextColor(_icolorletra);
 //BA.debugLineNum = 405;BA.debugLine="lblPublicar.TextColor = iColorLetra";
mostCurrent._lblpublicar.setTextColor(_icolorletra);
 //BA.debugLineNum = 406;BA.debugLine="lblCancelar.Width = Panel6.Width * 0.2";
mostCurrent._lblcancelar.setWidth((int) (mostCurrent._panel6.getWidth()*0.2));
 //BA.debugLineNum = 407;BA.debugLine="lblPublicar.Width = lblCancelar.Width";
mostCurrent._lblpublicar.setWidth(mostCurrent._lblcancelar.getWidth());
 //BA.debugLineNum = 409;BA.debugLine="lblCancelar.Height = Panel6.Height * 0.2";
mostCurrent._lblcancelar.setHeight((int) (mostCurrent._panel6.getHeight()*0.2));
 //BA.debugLineNum = 410;BA.debugLine="lblPublicar.Height = lblCancelar.Height";
mostCurrent._lblpublicar.setHeight(mostCurrent._lblcancelar.getHeight());
 //BA.debugLineNum = 412;BA.debugLine="Publicos.SetLabelTextSize(lblCancelar, \"PUBLICAR\"";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv4(mostCurrent.activityBA,mostCurrent._lblcancelar,"PUBLICAR",(float) (20),(float) (6),(int) (100));
 //BA.debugLineNum = 413;BA.debugLine="lblPublicar.TextSize=lblCancelar.textsize";
mostCurrent._lblpublicar.setTextSize(mostCurrent._lblcancelar.getTextSize());
 //BA.debugLineNum = 415;BA.debugLine="Dim aCentrar() As View";
_acentrar = new anywheresoftware.b4a.objects.ConcreteViewWrapper[(int) (0)];
{
int d0 = _acentrar.length;
for (int i0 = 0;i0 < d0;i0++) {
_acentrar[i0] = new anywheresoftware.b4a.objects.ConcreteViewWrapper();
}
}
;
 //BA.debugLineNum = 416;BA.debugLine="aCentrar = Array As View (  imgCancelar, imgPubli";
_acentrar = new anywheresoftware.b4a.objects.ConcreteViewWrapper[]{(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._imgcancelar.getObject())),(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._imgpublicar.getObject()))};
 //BA.debugLineNum = 417;BA.debugLine="Publicos.CentrarArrayControlesEnPanel ( Panel6, a";
mostCurrent._vvvvvvvvvvvvvvv7._vv6(mostCurrent.activityBA,mostCurrent._panel6,_acentrar,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 419;BA.debugLine="imgCancelar.top = 0: imgPublicar.Top = 0";
mostCurrent._imgcancelar.setTop((int) (0));
 //BA.debugLineNum = 419;BA.debugLine="imgCancelar.top = 0: imgPublicar.Top = 0";
mostCurrent._imgpublicar.setTop((int) (0));
 //BA.debugLineNum = 421;BA.debugLine="lblCancelar.Top = imgCancelar.Top + imgCancelar.H";
mostCurrent._lblcancelar.setTop((int) (mostCurrent._imgcancelar.getTop()+mostCurrent._imgcancelar.getHeight()+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (1))));
 //BA.debugLineNum = 422;BA.debugLine="lblPublicar.Top = lblCancelar.top";
mostCurrent._lblpublicar.setTop(mostCurrent._lblcancelar.getTop());
 //BA.debugLineNum = 424;BA.debugLine="lblCancelar.Left = Publicos.ViewAlinearCentro(img";
mostCurrent._lblcancelar.setLeft(mostCurrent._vvvvvvvvvvvvvvv7._vvvv0(mostCurrent.activityBA,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._imgcancelar.getObject())),(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._lblcancelar.getObject()))));
 //BA.debugLineNum = 425;BA.debugLine="lblPublicar.Left = Publicos.ViewAlinearCentro(img";
mostCurrent._lblpublicar.setLeft(mostCurrent._vvvvvvvvvvvvvvv7._vvvv0(mostCurrent.activityBA,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._imgpublicar.getObject())),(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._lblpublicar.getObject()))));
 //BA.debugLineNum = 428;BA.debugLine="End Sub";
return "";
}
}
