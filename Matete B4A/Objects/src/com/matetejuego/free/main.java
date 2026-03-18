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

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "com.matetejuego.free", "com.matetejuego.free.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
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
		activityBA = new BA(this, layout, processBA, "com.matetejuego.free", "com.matetejuego.free.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.matetejuego.free.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
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
		return main.class;
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
        BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
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
            BA.LogInfo("** Activity (main) Resume **");
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
public static boolean _vvvvvvvvv3 = false;
public static boolean _vvvvvvvvvvvvv2 = false;
public static String _g_dirgrabable = "";
public static String _g_semail = "";
public static String _g_simei = "";
public static String _g_nombrebaselocaljuego = "";
public static String _g_nombrebaselocalusuario = "";
public static String _g_spswbasej = "";
public static String _g_spswbaseu = "";
public static anyhwheresoftware.b4a.objects.sqlcipher.SQLCipher _g_sqlbaselocaljuego = null;
public static anyhwheresoftware.b4a.objects.sqlcipher.SQLCipher _g_sqlbaselocalusuario = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbljugar = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _vvvvvvvvvvvvvvvvv1 = null;
public static int _vvvvvvvvvvvvvvvvv2 = 0;
public anywheresoftware.b4a.keywords.constants.TypefaceWrapper _vvvvvvvvvvvvvvvvv3 = null;
public static String _vvvvvvvvvvvvvvvvv4 = "";
public anywheresoftware.b4a.objects.ImageViewWrapper _imgiconofondo = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblmail = null;
public flm.b4a.animationplus.AnimationPlusWrapper _vvvvvvvvvvvvvvvv0 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblmatete = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblpremium = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbldivergente = null;
public com.matetejuego.free.publicos _vvvvvvvvvvvvvvv7 = null;
public com.matetejuego.free.jugar _vvvvvvvvvvvvvvv0 = null;
public com.matetejuego.free.facebookactivity _vvvvvvvvvvvvvvvv1 = null;
public com.matetejuego.free.twitter _vvvvvvvvvvvvvvvv2 = null;
public com.matetejuego.free.svcbaseremota _vvvvvvvvvvvvvvvv3 = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
vis = vis | (jugar.mostCurrent != null);
vis = vis | (facebookactivity.mostCurrent != null);
vis = vis | (twitter.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 79;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 82;BA.debugLine="Activity.Color = Colors.white";
mostCurrent._activity.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 84;BA.debugLine="Activity.LoadLayout(\"InicioV2\")";
mostCurrent._activity.LoadLayout("InicioV2",mostCurrent.activityBA);
 //BA.debugLineNum = 85;BA.debugLine="Publicos.Set_PantallaCargaSet(Activity, ImgIconoF";
mostCurrent._vvvvvvvvvvvvvvv7._set_pantallacargaset(mostCurrent.activityBA,mostCurrent._activity,mostCurrent._imgiconofondo,mostCurrent._lbljugar,anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (151),(int) (89),(int) (152)),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 86;BA.debugLine="SetPantalla";
_vvvvvvvvvvvvvvvv4();
 //BA.debugLineNum = 87;BA.debugLine="lblJugar.Text = \"CARGANDO...\"";
mostCurrent._lbljugar.setText((Object)("CARGANDO..."));
 //BA.debugLineNum = 88;BA.debugLine="DoEvents";
anywheresoftware.b4a.keywords.Common.DoEvents();
 //BA.debugLineNum = 89;BA.debugLine="lblJugar.Text = \"JUGAR\"";
mostCurrent._lbljugar.setText((Object)("JUGAR"));
 //BA.debugLineNum = 90;BA.debugLine="AnimacionInicio";
_vvvvvvvvvvvvvvvv5();
 //BA.debugLineNum = 92;BA.debugLine="InicializacionGeneral";
_vvvvvvvvvvvvvvvv6();
 //BA.debugLineNum = 94;BA.debugLine="If FirstTime Then";
if (_firsttime) { 
 //BA.debugLineNum = 95;BA.debugLine="ServicioLogRemoto";
_vvvvvvvvvvvvvvvv7();
 //BA.debugLineNum = 96;BA.debugLine="Base_CheckVersion_BaseJ";
_base_checkversion_basej();
 //BA.debugLineNum = 99;BA.debugLine="Publicos.AbreBasesParam(g_sqlBaseLocalJuego, g_N";
mostCurrent._vvvvvvvvvvvvvvv7._vv1(mostCurrent.activityBA,_g_sqlbaselocaljuego,_g_nombrebaselocaljuego,_g_spswbasej,_g_sqlbaselocalusuario,_g_nombrebaselocalusuario,_g_spswbaseu);
 //BA.debugLineNum = 102;BA.debugLine="Base_CheckVersion_BaseU";
_base_checkversion_baseu();
 //BA.debugLineNum = 104;BA.debugLine="Base_CheckPalabras";
_base_checkpalabras();
 };
 //BA.debugLineNum = 107;BA.debugLine="SetPantalla_Premium";
_setpantalla_premium();
 //BA.debugLineNum = 109;BA.debugLine="End Sub";
return "";
}
public static boolean  _activity_keypress(int _keycode) throws Exception{
 //BA.debugLineNum = 240;BA.debugLine="Sub Activity_KeyPress(KeyCode As Int) As Boolean";
 //BA.debugLineNum = 242;BA.debugLine="If KeyCode = KeyCodes.KEYCODE_BACK Then";
if (_keycode==anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_BACK) { 
 //BA.debugLineNum = 243;BA.debugLine="Activity.finish";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 244;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
 }else {
 //BA.debugLineNum = 246;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 248;BA.debugLine="End Sub";
return false;
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 119;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 123;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 113;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 114;BA.debugLine="If bFinDeJuego Then";
if (_vvvvvvvvv3) { 
 //BA.debugLineNum = 115;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 };
 //BA.debugLineNum = 117;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvvv5() throws Exception{
 //BA.debugLineNum = 336;BA.debugLine="Sub AnimacionInicio";
 //BA.debugLineNum = 340;BA.debugLine="AnimPlus.InitializeScaleCenter(\"AnimPlus\", 0, 0,";
mostCurrent._vvvvvvvvvvvvvvvv0.InitializeScaleCenter(mostCurrent.activityBA,"AnimPlus",(float) (0),(float) (0),(float) (1),(float) (1),(android.view.View)(mostCurrent._imgiconofondo.getObject()));
 //BA.debugLineNum = 341;BA.debugLine="AnimPlus.SetInterpolator(AnimPlus.INTERPOLATOR_BO";
mostCurrent._vvvvvvvvvvvvvvvv0.SetInterpolator(mostCurrent._vvvvvvvvvvvvvvvv0.INTERPOLATOR_BOUNCE);
 //BA.debugLineNum = 343;BA.debugLine="Activity.invalidate";
mostCurrent._activity.Invalidate();
 //BA.debugLineNum = 344;BA.debugLine="AnimPlus.Duration = 4000";
mostCurrent._vvvvvvvvvvvvvvvv0.setDuration((long) (4000));
 //BA.debugLineNum = 345;BA.debugLine="If AnimPlus.IsInitialized Then";
if (mostCurrent._vvvvvvvvvvvvvvvv0.IsInitialized()) { 
 //BA.debugLineNum = 346;BA.debugLine="AnimPlus.Stop(ImgIconoFondo)";
mostCurrent._vvvvvvvvvvvvvvvv0.Stop((android.view.View)(mostCurrent._imgiconofondo.getObject()));
 //BA.debugLineNum = 347;BA.debugLine="Activity.Invalidate";
mostCurrent._activity.Invalidate();
 //BA.debugLineNum = 348;BA.debugLine="AnimPlus.Start(ImgIconoFondo)";
mostCurrent._vvvvvvvvvvvvvvvv0.Start((android.view.View)(mostCurrent._imgiconofondo.getObject()));
 };
 //BA.debugLineNum = 350;BA.debugLine="End Sub";
return "";
}
public static String  _base_checkpalabras() throws Exception{
boolean _bpremium = false;
boolean _busuarioantiguo = false;
boolean _btodaslaspalabras = false;
 //BA.debugLineNum = 353;BA.debugLine="Sub Base_CheckPalabras";
 //BA.debugLineNum = 361;BA.debugLine="Dim bPremium As Boolean, bUsuarioAntiguo As Boole";
_bpremium = false;
_busuarioantiguo = false;
_btodaslaspalabras = false;
 //BA.debugLineNum = 363;BA.debugLine="bPremium = Publicos.Get_EsPremium(g_sqlBaseLocalU";
_bpremium = mostCurrent._vvvvvvvvvvvvvvv7._get_espremium(mostCurrent.activityBA,_g_sqlbaselocalusuario);
 //BA.debugLineNum = 366;BA.debugLine="bUsuarioAntiguo = Publicos.Get_UsuarioAntiguo(g_s";
_busuarioantiguo = mostCurrent._vvvvvvvvvvvvvvv7._get_usuarioantiguo(mostCurrent.activityBA,_g_sqlbaselocalusuario);
 //BA.debugLineNum = 367;BA.debugLine="bTodasLasPalabras = bPremium Or bUsuarioAntiguo";
_btodaslaspalabras = _bpremium || _busuarioantiguo;
 //BA.debugLineNum = 369;BA.debugLine="If Publicos.get_CantidadPalabrasAlmacen(g_sqlBase";
if (mostCurrent._vvvvvvvvvvvvvvv7._get_cantidadpalabrasalmacen(mostCurrent.activityBA,_g_sqlbaselocaljuego,_btodaslaspalabras)!=mostCurrent._vvvvvvvvvvvvvvv7._get_cantidadpalabras(mostCurrent.activityBA,_g_sqlbaselocaljuego)) { 
 //BA.debugLineNum = 370;BA.debugLine="Publicos.Base_ActualizaPalabrasDesdeAlmacen(g_sq";
mostCurrent._vvvvvvvvvvvvvvv7._base_actualizapalabrasdesdealmacen(mostCurrent.activityBA,_g_sqlbaselocaljuego,_bpremium,_busuarioantiguo);
 };
 //BA.debugLineNum = 373;BA.debugLine="End Sub";
return "";
}
public static boolean  _base_checkversion_basej() throws Exception{
int _iverinstalada = 0;
int _iverassets = 0;
boolean _bret = false;
 //BA.debugLineNum = 290;BA.debugLine="Sub Base_CheckVersion_BaseJ As Boolean";
 //BA.debugLineNum = 292;BA.debugLine="Dim iVerInstalada As Int, iVerAssets As Int, bRet";
_iverinstalada = 0;
_iverassets = 0;
_bret = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 293;BA.debugLine="iVerInstalada = Base_GetVersionInstalada";
_iverinstalada = _base_getversioninstalada();
 //BA.debugLineNum = 294;BA.debugLine="iVerAssets = Base_GetVersionAssets";
_iverassets = _base_getversionassets();
 //BA.debugLineNum = 296;BA.debugLine="If iVerInstalada<iVerAssets Then";
if (_iverinstalada<_iverassets) { 
 //BA.debugLineNum = 297;BA.debugLine="g_sqlBaseLocalJuego.Close ' cierra la base";
_g_sqlbaselocaljuego.Close();
 //BA.debugLineNum = 299;BA.debugLine="Publicos.Archivo_CopiaDesdeAssets(g_NombreBaseLo";
mostCurrent._vvvvvvvvvvvvvvv7._archivo_copiadesdeassets(mostCurrent.activityBA,_g_nombrebaselocaljuego,anywheresoftware.b4a.keywords.Common.True,_g_dirgrabable);
 //BA.debugLineNum = 301;BA.debugLine="Publicos.AbreBaseCipher(g_sqlBaseLocalJuego, g_D";
mostCurrent._vvvvvvvvvvvvvvv7._v7(mostCurrent.activityBA,_g_sqlbaselocaljuego,_g_dirgrabable,_g_nombrebaselocaljuego,_g_spswbasej);
 //BA.debugLineNum = 303;BA.debugLine="bRet = True";
_bret = anywheresoftware.b4a.keywords.Common.True;
 };
 //BA.debugLineNum = 305;BA.debugLine="Return bRet";
if (true) return _bret;
 //BA.debugLineNum = 306;BA.debugLine="End Sub";
return false;
}
public static String  _base_checkversion_baseu() throws Exception{
anywheresoftware.b4a.sql.SQL.CursorWrapper _crcursor = null;
String _ssql = "";
 //BA.debugLineNum = 308;BA.debugLine="Sub Base_CheckVersion_BaseU";
 //BA.debugLineNum = 312;BA.debugLine="Dim crcursor As Cursor, sSql As String";
_crcursor = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
_ssql = "";
 //BA.debugLineNum = 313;BA.debugLine="sSql = \"SELECT count(1) cuenta from sqlite_master";
_ssql = "SELECT count(1) cuenta from sqlite_master where type = 'table' and name ='SeteosUsuario'";
 //BA.debugLineNum = 314;BA.debugLine="crcursor = g_sqlBaseLocalUsuario.ExecQuery(sSql)";
_crcursor.setObject((android.database.Cursor)(_g_sqlbaselocalusuario.ExecQuery(_ssql)));
 //BA.debugLineNum = 316;BA.debugLine="If crcursor.RowCount > 0 Then";
if (_crcursor.getRowCount()>0) { 
 //BA.debugLineNum = 317;BA.debugLine="crcursor.Position=0";
_crcursor.setPosition((int) (0));
 //BA.debugLineNum = 318;BA.debugLine="If crcursor.GetInt2(0) =0 Then";
if (_crcursor.GetInt2((int) (0))==0) { 
 //BA.debugLineNum = 319;BA.debugLine="sSql = \"CREATE  TABLE main.SeteosUsuario (tipos";
_ssql = "CREATE  TABLE main.SeteosUsuario (tiposeteo VARCHAR PRIMARY KEY  Not Null , enterodesde integer, enterohasta integer, fechadesde DateTime, fechahasta DateTime, texto varchar)";
 //BA.debugLineNum = 320;BA.debugLine="g_sqlBaseLocalUsuario.ExecNonQuery(sSql)";
_g_sqlbaselocalusuario.ExecNonQuery(_ssql);
 };
 };
 //BA.debugLineNum = 323;BA.debugLine="crcursor.close";
_crcursor.Close();
 //BA.debugLineNum = 327;BA.debugLine="If Publicos.Get_SeteoUsuarioExiste(g_sqlBaseLocal";
if (mostCurrent._vvvvvvvvvvvvvvv7._get_seteousuarioexiste(mostCurrent.activityBA,_g_sqlbaselocalusuario,"Sonido")==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 328;BA.debugLine="Publicos.SeteoUsuarioEnteroDesde_Inserta(g_sqlBa";
mostCurrent._vvvvvvvvvvvvvvv7._seteousuarioenterodesde_inserta(mostCurrent.activityBA,_g_sqlbaselocalusuario,"Sonido",(int) (0));
 };
 //BA.debugLineNum = 334;BA.debugLine="End Sub";
return "";
}
public static int  _base_getversionassets() throws Exception{
String _slinea = "";
int _idesde = 0;
int _ihasta = 0;
int _iret = 0;
 //BA.debugLineNum = 262;BA.debugLine="Sub Base_GetVersionAssets As Int";
 //BA.debugLineNum = 263;BA.debugLine="Dim sLinea As String,  iDesde As Int, iHasta As I";
_slinea = "";
_idesde = 0;
_ihasta = 0;
_iret = 0;
 //BA.debugLineNum = 265;BA.debugLine="sLinea= File.ReadString(File.DirAssets, \"vr.txt\"";
_slinea = anywheresoftware.b4a.keywords.Common.File.ReadString(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"vr.txt");
 //BA.debugLineNum = 266;BA.debugLine="If sLinea.substring2(0,4) =\"vrbj\" Then";
if ((_slinea.substring((int) (0),(int) (4))).equals("vrbj")) { 
 //BA.debugLineNum = 267;BA.debugLine="iDesde = 5";
_idesde = (int) (5);
 //BA.debugLineNum = 268;BA.debugLine="iHasta = sLinea.Length";
_ihasta = _slinea.length();
 //BA.debugLineNum = 269;BA.debugLine="iRet= sLinea.SubString2 (iDesde, iHasta)";
_iret = (int)(Double.parseDouble(_slinea.substring(_idesde,_ihasta)));
 };
 //BA.debugLineNum = 271;BA.debugLine="Return iRet";
if (true) return _iret;
 //BA.debugLineNum = 272;BA.debugLine="End Sub";
return 0;
}
public static int  _base_getversioninstalada() throws Exception{
anywheresoftware.b4a.sql.SQL.CursorWrapper _crver = null;
int _iversion = 0;
 //BA.debugLineNum = 274;BA.debugLine="Sub Base_GetVersionInstalada As Int";
 //BA.debugLineNum = 275;BA.debugLine="Dim crVer As Cursor, iVersion As Int =0";
_crver = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
_iversion = (int) (0);
 //BA.debugLineNum = 276;BA.debugLine="Try";
try { //BA.debugLineNum = 277;BA.debugLine="crVer = g_sqlBaseLocalJuego.ExecQuery(\"Select Ver";
_crver.setObject((android.database.Cursor)(_g_sqlbaselocaljuego.ExecQuery("Select VersionBase from Seteos")));
 //BA.debugLineNum = 278;BA.debugLine="If crVer.RowCount>0 Then";
if (_crver.getRowCount()>0) { 
 //BA.debugLineNum = 279;BA.debugLine="crVer.Position=0";
_crver.setPosition((int) (0));
 //BA.debugLineNum = 280;BA.debugLine="iVersion = crVer.GetInt2(0)";
_iversion = _crver.GetInt2((int) (0));
 };
 //BA.debugLineNum = 282;BA.debugLine="crVer.close";
_crver.Close();
 } 
       catch (Exception e10) {
			processBA.setLastException(e10); //BA.debugLineNum = 284;BA.debugLine="Publicos.ManejaError(\"Base_GetVersionInstalada\",";
mostCurrent._vvvvvvvvvvvvvvv7._vvv7(mostCurrent.activityBA,"Base_GetVersionInstalada",anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage());
 };
 //BA.debugLineNum = 287;BA.debugLine="Return iVersion";
if (true) return _iversion;
 //BA.debugLineNum = 288;BA.debugLine="End Sub";
return 0;
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 62;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 65;BA.debugLine="Private lblJugar As Label";
mostCurrent._lbljugar = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 66;BA.debugLine="Private imgFondo As ImageView";
mostCurrent._vvvvvvvvvvvvvvvvv1 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 67;BA.debugLine="Dim iCuentaReset As Int";
_vvvvvvvvvvvvvvvvv2 = 0;
 //BA.debugLineNum = 68;BA.debugLine="Dim tFontDefault As Typeface";
mostCurrent._vvvvvvvvvvvvvvvvv3 = new anywheresoftware.b4a.keywords.constants.TypefaceWrapper();
 //BA.debugLineNum = 69;BA.debugLine="Dim sFontDefault As String= \"OpenSans-Regular.ttf";
mostCurrent._vvvvvvvvvvvvvvvvv4 = "OpenSans-Regular.ttf";
 //BA.debugLineNum = 70;BA.debugLine="Private ImgIconoFondo As ImageView";
mostCurrent._imgiconofondo = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 71;BA.debugLine="Private lblMail As Label";
mostCurrent._lblmail = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 72;BA.debugLine="Dim AnimPlus As AnimationPlus";
mostCurrent._vvvvvvvvvvvvvvvv0 = new flm.b4a.animationplus.AnimationPlusWrapper();
 //BA.debugLineNum = 73;BA.debugLine="Private lblMatete As Label";
mostCurrent._lblmatete = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 74;BA.debugLine="Private lblPremium As Label";
mostCurrent._lblpremium = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 75;BA.debugLine="Private lblDivergente As Label";
mostCurrent._lbldivergente = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 76;BA.debugLine="End Sub";
return "";
}
public static String  _imgfondo_click() throws Exception{
 //BA.debugLineNum = 134;BA.debugLine="Sub imgFondo_Click";
 //BA.debugLineNum = 136;BA.debugLine="Jugar.bIniciarPremium = True";
mostCurrent._vvvvvvvvvvvvvvv0._vvvvv0 = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 137;BA.debugLine="StartActivity(\"Jugar\")";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)("Jugar"));
 //BA.debugLineNum = 145;BA.debugLine="End Sub";
return "";
}
public static String  _imgiconofondo_click() throws Exception{
 //BA.debugLineNum = 125;BA.debugLine="Sub ImgIconoFondo_Click";
 //BA.debugLineNum = 126;BA.debugLine="lblJugar_Click";
_lbljugar_click();
 //BA.debugLineNum = 127;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvvv6() throws Exception{
boolean _bbaseusuariocopiada = false;
 //BA.debugLineNum = 147;BA.debugLine="Sub InicializacionGeneral";
 //BA.debugLineNum = 148;BA.debugLine="Dim bBaseUsuarioCopiada As Boolean = False";
_bbaseusuariocopiada = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 150;BA.debugLine="g_DirGrabable = Publicos.Get_DirectorioBase";
_g_dirgrabable = mostCurrent._vvvvvvvvvvvvvvv7._get_directoriobase(mostCurrent.activityBA);
 //BA.debugLineNum = 152;BA.debugLine="Publicos.CargaDeviceValues";
mostCurrent._vvvvvvvvvvvvvvv7._vv4(mostCurrent.activityBA);
 //BA.debugLineNum = 154;BA.debugLine="g_sIMEI=Publicos.getIMEI";
_g_simei = mostCurrent._vvvvvvvvvvvvvvv7._vvv3(mostCurrent.activityBA);
 //BA.debugLineNum = 155;BA.debugLine="g_sEMail = Publicos.GetUsuario";
_g_semail = mostCurrent._vvvvvvvvvvvvvvv7._vvv4(mostCurrent.activityBA);
 //BA.debugLineNum = 159;BA.debugLine="Publicos.Archivo_CopiaDesdeAssets(g_NombreBaseLoc";
mostCurrent._vvvvvvvvvvvvvvv7._archivo_copiadesdeassets(mostCurrent.activityBA,_g_nombrebaselocaljuego,anywheresoftware.b4a.keywords.Common.False,_g_dirgrabable);
 //BA.debugLineNum = 160;BA.debugLine="bBaseUsuarioCopiada=Publicos.Archivo_CopiaDesdeAs";
_bbaseusuariocopiada = mostCurrent._vvvvvvvvvvvvvvv7._archivo_copiadesdeassets(mostCurrent.activityBA,_g_nombrebaselocalusuario,_vvvvvvvvvvvvv2,_g_dirgrabable);
 //BA.debugLineNum = 162;BA.debugLine="Publicos.AbreBaseCipher(g_sqlBaseLocalUsuario, g_";
mostCurrent._vvvvvvvvvvvvvvv7._v7(mostCurrent.activityBA,_g_sqlbaselocalusuario,_g_dirgrabable,_g_nombrebaselocalusuario,_g_spswbaseu);
 //BA.debugLineNum = 163;BA.debugLine="Publicos.AbreBasecipher(g_sqlBaseLocalJuego, g_Di";
mostCurrent._vvvvvvvvvvvvvvv7._v7(mostCurrent.activityBA,_g_sqlbaselocaljuego,_g_dirgrabable,_g_nombrebaselocaljuego,_g_spswbasej);
 //BA.debugLineNum = 166;BA.debugLine="Base_CheckVersion_BaseJ";
_base_checkversion_basej();
 //BA.debugLineNum = 169;BA.debugLine="If bBaseUsuarioCopiada Then";
if (_bbaseusuariocopiada) { 
 //BA.debugLineNum = 171;BA.debugLine="Publicos.UsuarioGrabaImei(g_sqlBaseLocalJuego,g_";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv6(mostCurrent.activityBA,_g_sqlbaselocaljuego,_g_simei);
 };
 //BA.debugLineNum = 174;BA.debugLine="If Publicos.UsuarioExiste(g_sqlBaseLocalUsuario)";
if (mostCurrent._vvvvvvvvvvvvvvv7._vvvv5(mostCurrent.activityBA,_g_sqlbaselocalusuario)==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 176;BA.debugLine="Publicos.UsuarioGrabaImei(g_sqlBaseLocalUsuario,";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv6(mostCurrent.activityBA,_g_sqlbaselocalusuario,_g_simei);
 };
 //BA.debugLineNum = 180;BA.debugLine="End Sub";
return "";
}
public static String  _jugar_inicia(boolean _bpremium) throws Exception{
 //BA.debugLineNum = 381;BA.debugLine="Sub Jugar_Inicia (bPremium As Boolean)";
 //BA.debugLineNum = 382;BA.debugLine="iCuentaReset=0";
_vvvvvvvvvvvvvvvvv2 = (int) (0);
 //BA.debugLineNum = 383;BA.debugLine="Jugar.bIniciarPremium = bPremium";
mostCurrent._vvvvvvvvvvvvvvv0._vvvvv0 = _bpremium;
 //BA.debugLineNum = 384;BA.debugLine="StartActivity(\"Jugar\")";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)("Jugar"));
 //BA.debugLineNum = 385;BA.debugLine="Activity.finish";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 386;BA.debugLine="End Sub";
return "";
}
public static String  _lbljugar_click() throws Exception{
 //BA.debugLineNum = 129;BA.debugLine="Sub lblJugar_Click";
 //BA.debugLineNum = 130;BA.debugLine="Jugar_Inicia(False)";
_jugar_inicia(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 131;BA.debugLine="End Sub";
return "";
}
public static String  _lblpremium_click() throws Exception{
 //BA.debugLineNum = 375;BA.debugLine="Sub lblPremium_Click";
 //BA.debugLineNum = 376;BA.debugLine="If Publicos.Get_EsPremium(g_sqlBaseLocalUsuario) =";
if (mostCurrent._vvvvvvvvvvvvvvv7._get_espremium(mostCurrent.activityBA,_g_sqlbaselocalusuario)==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 377;BA.debugLine="Jugar_Inicia(True)";
_jugar_inicia(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 379;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
publicos._process_globals();
jugar._process_globals();
facebookactivity._process_globals();
twitter._process_globals();
svcbaseremota._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 37;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 42;BA.debugLine="Dim bFinDeJuego As Boolean = False";
_vvvvvvvvv3 = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 44;BA.debugLine="Dim bForzarUsuario As Boolean = False";
_vvvvvvvvvvvvv2 = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 47;BA.debugLine="Public g_DirGrabable As String";
_g_dirgrabable = "";
 //BA.debugLineNum = 50;BA.debugLine="Public g_sEMail As String, g_sIMEI As String";
_g_semail = "";
_g_simei = "";
 //BA.debugLineNum = 52;BA.debugLine="Public g_NombreBaseLocalJuego As String =\"BaseJ.s";
_g_nombrebaselocaljuego = BA.__b (new byte[] {43,27,-42,-124,60,38,-97,-124,76,69,-122,-107}, 347613);
 //BA.debugLineNum = 53;BA.debugLine="Public g_NombreBaseLocalUsuario As String = \"Base";
_g_nombrebaselocalusuario = BA.__b (new byte[] {43,27,-70,6,35,77,-82,4,81,64,-9,6,79}, 296559);
 //BA.debugLineNum = 54;BA.debugLine="Dim g_sPswBaseJ As String= \"NadaAnsioDeNada\"";
_g_spswbasej = BA.__b (new byte[] {39,25,0,61,55,100,94,33,79,106,86,3,75,91,81}, 922198);
 //BA.debugLineNum = 55;BA.debugLine="Dim g_sPswBaseU As String = \"MientrasDuraElInstan";
_g_spswbaseu = BA.__b (new byte[] {36,18,-90,111,2,123,-21,102,100,88,-26,113,111,80,-34,63,-19,-113,-15,61,98,85,-79}, 737153);
 //BA.debugLineNum = 56;BA.debugLine="Dim g_DirGrabable As String";
_g_dirgrabable = "";
 //BA.debugLineNum = 57;BA.debugLine="Public g_sqlBaseLocalJuego As SQLCipher, g_sqlBas";
_g_sqlbaselocaljuego = new anyhwheresoftware.b4a.objects.sqlcipher.SQLCipher();
_g_sqlbaselocalusuario = new anyhwheresoftware.b4a.objects.sqlcipher.SQLCipher();
 //BA.debugLineNum = 60;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvvv7() throws Exception{
int _idpalabra = 0;
 //BA.debugLineNum = 250;BA.debugLine="Sub ServicioLogRemoto";
 //BA.debugLineNum = 251;BA.debugLine="Dim idPalabra As Int";
_idpalabra = 0;
 //BA.debugLineNum = 253;BA.debugLine="Try";
try { //BA.debugLineNum = 254;BA.debugLine="idPalabra = Publicos.Get_PalabraEnJuego(g_sqlBas";
_idpalabra = mostCurrent._vvvvvvvvvvvvvvv7._get_palabraenjuego(mostCurrent.activityBA,_g_sqlbaselocalusuario);
 //BA.debugLineNum = 256;BA.debugLine="CallSubDelayed3(SvcBaseRemota,\"RecibeParametros\"";
anywheresoftware.b4a.keywords.Common.CallSubDelayed3(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvvvvvvvvvvv3.getObject()),"RecibeParametros",(Object)(_g_simei+"%"+BA.NumberToString(_idpalabra)),(Object)(_g_semail));
 } 
       catch (Exception e6) {
			processBA.setLastException(e6); //BA.debugLineNum = 258;BA.debugLine="Log(\"Error en servicio remoto\")";
anywheresoftware.b4a.keywords.Common.Log("Error en servicio remoto");
 };
 //BA.debugLineNum = 260;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvvv4() throws Exception{
 //BA.debugLineNum = 181;BA.debugLine="Sub SetPantalla";
 //BA.debugLineNum = 182;BA.debugLine="tFontDefault = Typeface.LoadFromAssets(sFontDefau";
mostCurrent._vvvvvvvvvvvvvvvvv3.setObject((android.graphics.Typeface)(anywheresoftware.b4a.keywords.Common.Typeface.LoadFromAssets(mostCurrent._vvvvvvvvvvvvvvvvv4)));
 //BA.debugLineNum = 186;BA.debugLine="lblJugar.Color = Colors.White";
mostCurrent._lbljugar.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 187;BA.debugLine="lblJugar.height = Activity.Height * 0.1";
mostCurrent._lbljugar.setHeight((int) (mostCurrent._activity.getHeight()*0.1));
 //BA.debugLineNum = 188;BA.debugLine="lblJugar.Width = Activity.Width";
mostCurrent._lbljugar.setWidth(mostCurrent._activity.getWidth());
 //BA.debugLineNum = 189;BA.debugLine="lblJugar.Top = Activity.Height*0.1";
mostCurrent._lbljugar.setTop((int) (mostCurrent._activity.getHeight()*0.1));
 //BA.debugLineNum = 190;BA.debugLine="lblJugar.Text = \"JUGAR\"";
mostCurrent._lbljugar.setText((Object)("JUGAR"));
 //BA.debugLineNum = 191;BA.debugLine="lblJugar.Gravity = Gravity.CENTER";
mostCurrent._lbljugar.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 192;BA.debugLine="Publicos.SetLabelTextSize (lblJugar,lblJugar.Text";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv4(mostCurrent.activityBA,mostCurrent._lbljugar,mostCurrent._lbljugar.getText(),(float) (30),(float) (6),(int) (100));
 //BA.debugLineNum = 194;BA.debugLine="Publicos.CentrarControl(Activity, lblJugar, True,";
mostCurrent._vvvvvvvvvvvvvvv7._vv7(mostCurrent.activityBA,mostCurrent._activity,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._lbljugar.getObject())),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 196;BA.debugLine="lblMatete.height = Activity.Height * 0.05";
mostCurrent._lblmatete.setHeight((int) (mostCurrent._activity.getHeight()*0.05));
 //BA.debugLineNum = 197;BA.debugLine="lblDivergente.Height = lblMatete.Height";
mostCurrent._lbldivergente.setHeight(mostCurrent._lblmatete.getHeight());
 //BA.debugLineNum = 198;BA.debugLine="lblPremium.Height = lblMatete.Height";
mostCurrent._lblpremium.setHeight(mostCurrent._lblmatete.getHeight());
 //BA.debugLineNum = 199;BA.debugLine="lblMatete.Width = Activity.Width";
mostCurrent._lblmatete.setWidth(mostCurrent._activity.getWidth());
 //BA.debugLineNum = 200;BA.debugLine="lblDivergente.Width = Activity.Width";
mostCurrent._lbldivergente.setWidth(mostCurrent._activity.getWidth());
 //BA.debugLineNum = 201;BA.debugLine="lblPremium.Width = Activity.Width";
mostCurrent._lblpremium.setWidth(mostCurrent._activity.getWidth());
 //BA.debugLineNum = 202;BA.debugLine="lblMatete.Left = 0";
mostCurrent._lblmatete.setLeft((int) (0));
 //BA.debugLineNum = 205;BA.debugLine="lblMatete.Text =     \"M  A  T  E  T  E \" & Chr(16";
mostCurrent._lblmatete.setText((Object)("M  A  T  E  T  E "+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (169)))));
 //BA.debugLineNum = 206;BA.debugLine="lblDivergente.Text = \"D I V E R G E N T E\"";
mostCurrent._lbldivergente.setText((Object)("D I V E R G E N T E"));
 //BA.debugLineNum = 208;BA.debugLine="lblPremium.Text = \"\"";
mostCurrent._lblpremium.setText((Object)(""));
 //BA.debugLineNum = 209;BA.debugLine="lblMatete.Gravity = Gravity.CENTER";
mostCurrent._lblmatete.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 210;BA.debugLine="lblDivergente.Gravity = Gravity.CENTER";
mostCurrent._lbldivergente.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 211;BA.debugLine="lblPremium.Gravity = Gravity.CENTER";
mostCurrent._lblpremium.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 213;BA.debugLine="lblMatete.Top = Activity.Height * 0.7";
mostCurrent._lblmatete.setTop((int) (mostCurrent._activity.getHeight()*0.7));
 //BA.debugLineNum = 214;BA.debugLine="lblDivergente.Top = lblMatete.Top + lblMatete.Hei";
mostCurrent._lbldivergente.setTop((int) (mostCurrent._lblmatete.getTop()+mostCurrent._lblmatete.getHeight()+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (1))));
 //BA.debugLineNum = 215;BA.debugLine="lblPremium.top = lblDivergente.Top + lblDivergent";
mostCurrent._lblpremium.setTop((int) (mostCurrent._lbldivergente.getTop()+mostCurrent._lbldivergente.getHeight()*2));
 //BA.debugLineNum = 217;BA.debugLine="Publicos.SetLabelTextSize(lblMatete, lblMatete.Te";
mostCurrent._vvvvvvvvvvvvvvv7._vvvv4(mostCurrent.activityBA,mostCurrent._lblmatete,mostCurrent._lblmatete.getText(),(float) (20),(float) (6),(int) (100));
 //BA.debugLineNum = 218;BA.debugLine="lblDivergente.TextSize = lblMatete.TextSize * 0.8";
mostCurrent._lbldivergente.setTextSize((float) (mostCurrent._lblmatete.getTextSize()*0.8));
 //BA.debugLineNum = 219;BA.debugLine="lblPremium.TextSize = lblMatete.TextSize";
mostCurrent._lblpremium.setTextSize(mostCurrent._lblmatete.getTextSize());
 //BA.debugLineNum = 221;BA.debugLine="lblMatete.TextColor = Colors.rgb(151,89,152)";
mostCurrent._lblmatete.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (151),(int) (89),(int) (152)));
 //BA.debugLineNum = 222;BA.debugLine="lblDivergente.TextColor = Colors.rgb(151,89,152)";
mostCurrent._lbldivergente.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (151),(int) (89),(int) (152)));
 //BA.debugLineNum = 223;BA.debugLine="lblPremium.TextColor = Colors.rgb(151,89,152)";
mostCurrent._lblpremium.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (151),(int) (89),(int) (152)));
 //BA.debugLineNum = 224;BA.debugLine="lblMail.TextColor = Colors.rgb(151,89,152)";
mostCurrent._lblmail.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (151),(int) (89),(int) (152)));
 //BA.debugLineNum = 225;BA.debugLine="lblJugar.TextColor = Colors.rgb(151,89,152)";
mostCurrent._lbljugar.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (151),(int) (89),(int) (152)));
 //BA.debugLineNum = 227;BA.debugLine="lblMail.Width = Activity.Width";
mostCurrent._lblmail.setWidth(mostCurrent._activity.getWidth());
 //BA.debugLineNum = 228;BA.debugLine="lblMail.Color = Colors.Transparent";
mostCurrent._lblmail.setColor(anywheresoftware.b4a.keywords.Common.Colors.Transparent);
 //BA.debugLineNum = 229;BA.debugLine="lblMail.Height = lblPremium.Height/2";
mostCurrent._lblmail.setHeight((int) (mostCurrent._lblpremium.getHeight()/(double)2));
 //BA.debugLineNum = 231;BA.debugLine="lblMail.Gravity = Gravity.CENTER_HORIZONTAL";
mostCurrent._lblmail.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER_HORIZONTAL);
 //BA.debugLineNum = 232;BA.debugLine="lblMail.Top = Activity.Height - lblMail.Height *1";
mostCurrent._lblmail.setTop((int) (mostCurrent._activity.getHeight()-mostCurrent._lblmail.getHeight()*1.5));
 //BA.debugLineNum = 233;BA.debugLine="lblMail.Text = \"MateteJuego@gmail.com\"";
mostCurrent._lblmail.setText((Object)("MateteJuego@gmail.com"));
 //BA.debugLineNum = 234;BA.debugLine="Publicos.CentrarControl(Activity, lblMail,True, F";
mostCurrent._vvvvvvvvvvvvvvv7._vv7(mostCurrent.activityBA,mostCurrent._activity,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._lblmail.getObject())),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 235;BA.debugLine="lblMail.Visible = False";
mostCurrent._lblmail.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 238;BA.debugLine="End Sub";
return "";
}
public static String  _setpantalla_premium() throws Exception{
 //BA.debugLineNum = 389;BA.debugLine="Sub SetPantalla_Premium";
 //BA.debugLineNum = 390;BA.debugLine="If Publicos.Get_EsPremium(g_sqlBaseLocalUsuario)";
if (mostCurrent._vvvvvvvvvvvvvvv7._get_espremium(mostCurrent.activityBA,_g_sqlbaselocalusuario)) { 
 //BA.debugLineNum = 391;BA.debugLine="lblPremium.Text = \"VERSIÓN PREMIUM\"";
mostCurrent._lblpremium.setText((Object)("VERSIÓN PREMIUM"));
 }else {
 //BA.debugLineNum = 393;BA.debugLine="lblPremium.Text = \"¿MATETE PREMIUM?\"";
mostCurrent._lblpremium.setText((Object)("¿MATETE PREMIUM?"));
 };
 //BA.debugLineNum = 395;BA.debugLine="End Sub";
return "";
}
}
