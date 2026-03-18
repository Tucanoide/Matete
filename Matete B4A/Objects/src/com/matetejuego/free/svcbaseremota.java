package com.matetejuego.free;


import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.objects.ServiceHelper;
import anywheresoftware.b4a.debug.*;

public class svcbaseremota extends android.app.Service {
	public static class svcbaseremota_BR extends android.content.BroadcastReceiver {

		@Override
		public void onReceive(android.content.Context context, android.content.Intent intent) {
			android.content.Intent in = new android.content.Intent(context, svcbaseremota.class);
			if (intent != null)
				in.putExtra("b4a_internal_intent", intent);
			context.startService(in);
		}

	}
    static svcbaseremota mostCurrent;
	public static BA processBA;
    private ServiceHelper _service;
    public static Class<?> getObject() {
		return svcbaseremota.class;
	}
	@Override
	public void onCreate() {
        mostCurrent = this;
        if (processBA == null) {
		    processBA = new BA(this, null, null, "com.matetejuego.free", "com.matetejuego.free.svcbaseremota");
            if (BA.isShellModeRuntimeCheck(processBA)) {
                processBA.raiseEvent2(null, true, "SHELL", false);
		    }
            try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            processBA.loadHtSubs(this.getClass());
            ServiceHelper.init();
        }
        _service = new ServiceHelper(this);
        processBA.service = this;
        processBA.setActivityPaused(false);
        if (BA.isShellModeRuntimeCheck(processBA)) {
			processBA.raiseEvent2(null, true, "CREATE", true, "com.matetejuego.free.svcbaseremota", processBA, _service, anywheresoftware.b4a.keywords.Common.Density);
		}
        if (!false && ServiceHelper.StarterHelper.startFromServiceCreate(processBA, true) == false) {
				
		}
		else {
            BA.LogInfo("** Service (svcbaseremota) Create **");
            processBA.raiseEvent(null, "service_create");
        }
        processBA.runHook("oncreate", this, null);
        if (false) {
			if (ServiceHelper.StarterHelper.waitForLayout != null)
				BA.handler.post(ServiceHelper.StarterHelper.waitForLayout);
		}
    }
		@Override
	public void onStart(android.content.Intent intent, int startId) {
		onStartCommand(intent, 0, 0);
    }
    @Override
    public int onStartCommand(final android.content.Intent intent, int flags, int startId) {
    	if (ServiceHelper.StarterHelper.onStartCommand(processBA))
			handleStart(intent);
		else {
			ServiceHelper.StarterHelper.waitForLayout = new Runnable() {
				public void run() {
                    BA.LogInfo("** Service (svcbaseremota) Create **");
                    processBA.raiseEvent(null, "service_create");
					handleStart(intent);
				}
			};
		}
        processBA.runHook("onstartcommand", this, new Object[] {intent, flags, startId});
		return android.app.Service.START_NOT_STICKY;
    }
    private void handleStart(android.content.Intent intent) {
    	BA.LogInfo("** Service (svcbaseremota) Start **");
    	java.lang.reflect.Method startEvent = processBA.htSubs.get("service_start");
    	if (startEvent != null) {
    		if (startEvent.getParameterTypes().length > 0) {
    			anywheresoftware.b4a.objects.IntentWrapper iw = new anywheresoftware.b4a.objects.IntentWrapper();
    			if (intent != null) {
    				if (intent.hasExtra("b4a_internal_intent"))
    					iw.setObject((android.content.Intent) intent.getParcelableExtra("b4a_internal_intent"));
    				else
    					iw.setObject(intent);
    			}
    			processBA.raiseEvent(null, "service_start", iw);
    		}
    		else {
    			processBA.raiseEvent(null, "service_start");
    		}
    	}
    }
	@Override
	public android.os.IBinder onBind(android.content.Intent intent) {
		return null;
	}
	@Override
	public void onDestroy() {
        BA.LogInfo("** Service (svcbaseremota) Destroy **");
		processBA.raiseEvent(null, "service_destroy");
        processBA.service = null;
		mostCurrent = null;
		processBA.setActivityPaused(true);
        processBA.runHook("ondestroy", this, null);
	}
public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.http.HttpClientWrapper _vvvvvvvvvvvv3 = null;
public static boolean _vvvvvvvvvvvv4 = false;
public static String _vvvvv6 = "";
public static String _vvvvvvvvvvvv5 = "";
public static String _vvvvvvvvvvvv6 = "";
public static int _vvvvvvvvvvvv7 = 0;
public static anywheresoftware.b4a.objects.NotificationWrapper _vvvvvvvvvvvv0 = null;
public static int _vvvvvvvvvvvvv1 = 0;
public com.matetejuego.free.main _vvvvvvvvvvvvvvv6 = null;
public com.matetejuego.free.publicos _vvvvvvvvvvvvvvv7 = null;
public com.matetejuego.free.jugar _vvvvvvvvvvvvvvv0 = null;
public com.matetejuego.free.facebookactivity _vvvvvvvvvvvvvvvv1 = null;
public com.matetejuego.free.twitter _vvvvvvvvvvvvvvvv2 = null;
public static String  _vvvvvvvvvvvvvvvvvvvvvvvvvv3() throws Exception{
anywheresoftware.b4a.http.HttpClientWrapper.HttpUriRequestWrapper _req = null;
String _sfecha = "";
String _query = "";
anywheresoftware.b4a.phone.Phone _p = null;
anywheresoftware.b4a.agraham.reflection.Reflection _r = null;
 //BA.debugLineNum = 47;BA.debugLine="Sub grabaLogRemoto";
 //BA.debugLineNum = 48;BA.debugLine="Dim req As HttpRequest, sFecha As String";
_req = new anywheresoftware.b4a.http.HttpClientWrapper.HttpUriRequestWrapper();
_sfecha = "";
 //BA.debugLineNum = 49;BA.debugLine="Dim Query As String";
_query = "";
 //BA.debugLineNum = 51;BA.debugLine="If False Then";
if (anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 52;BA.debugLine="DateTime.DateFormat =\"yyyy-MM-dd hh:mm:ss\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("yyyy-MM-dd hh:mm:ss");
 //BA.debugLineNum = 53;BA.debugLine="sFecha = DateTime.Date(DateTime.now)";
_sfecha = anywheresoftware.b4a.keywords.Common.DateTime.Date(anywheresoftware.b4a.keywords.Common.DateTime.getNow());
 //BA.debugLineNum = 54;BA.debugLine="Query = \"Insert Into jugadores_log(imei, mail, i";
_query = "Insert Into jugadores_log(imei, mail, ingreso, idPalabra) values ('"+_vvvvvvvvvvvv5+"','"+_vvvvvvvvvvvv6+"','"+_sfecha+"',"+BA.NumberToString(_vvvvvvvvvvvv7)+")";
 }else {
 //BA.debugLineNum = 58;BA.debugLine="Query = \"call Ingresos_i ('\" & sMail & \"',\" & sI";
_query = "call Ingresos_i ('"+_vvvvvvvvvvvv6+"',"+_vvvvvvvvvvvv5+"', 0, 0, '')";
 };
 //BA.debugLineNum = 62;BA.debugLine="req.InitializePost2(sgDireccionRemota, Query.GetB";
_req.InitializePost2(_vvvvv6,_query.getBytes("UTF8"));
 //BA.debugLineNum = 63;BA.debugLine="xHttpCli.Execute(req, 1)";
_vvvvvvvvvvvv3.Execute(processBA,_req,(int) (1));
 //BA.debugLineNum = 67;BA.debugLine="Dim p As Phone";
_p = new anywheresoftware.b4a.phone.Phone();
 //BA.debugLineNum = 68;BA.debugLine="If p.SdkVersion >= 9 Then";
if (_p.getSdkVersion()>=9) { 
 //BA.debugLineNum = 69;BA.debugLine="Dim R As Reflector";
_r = new anywheresoftware.b4a.agraham.reflection.Reflection();
 //BA.debugLineNum = 70;BA.debugLine="R.Target = R.CreateObject(\"android.os.StrictM";
_r.Target = _r.CreateObject("android.os.StrictMode$ThreadPolicy$Builder");
 //BA.debugLineNum = 71;BA.debugLine="R.Target = R.RunMethod(\"permitAll\")";
_r.Target = _r.RunMethod("permitAll");
 //BA.debugLineNum = 72;BA.debugLine="R.Target = R.RunMethod(\"build\")";
_r.Target = _r.RunMethod("build");
 //BA.debugLineNum = 73;BA.debugLine="R.RunStaticMethod(\"android.os.StrictMode\", \"s";
_r.RunStaticMethod("android.os.StrictMode","setThreadPolicy",new Object[]{_r.Target},new String[]{"android.os.StrictMode$ThreadPolicy"});
 };
 //BA.debugLineNum = 76;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 9;BA.debugLine="Dim xHttpCli As HttpClient";
_vvvvvvvvvvvv3 = new anywheresoftware.b4a.http.HttpClientWrapper();
 //BA.debugLineNum = 10;BA.debugLine="Dim bCorrer As Boolean";
_vvvvvvvvvvvv4 = false;
 //BA.debugLineNum = 13;BA.debugLine="Dim sgDireccionRemota As String = \"http://matrem.";
_vvvvv6 = BA.__b (new byte[] {1,14,-76,-18,76,39,-90,-25,65,88,-27,-22,71,19,-15,-67,-25,-44,-10,-65,57,68,-21,-68,77,26,-3,-24,14}, 284707);
 //BA.debugLineNum = 16;BA.debugLine="Dim sImei As String, sMail As String, idPalabra A";
_vvvvvvvvvvvv5 = "";
_vvvvvvvvvvvv6 = "";
_vvvvvvvvvvvv7 = 0;
 //BA.debugLineNum = 18;BA.debugLine="Dim sNotif As Notification";
_vvvvvvvvvvvv0 = new anywheresoftware.b4a.objects.NotificationWrapper();
 //BA.debugLineNum = 19;BA.debugLine="Dim iReintentos As Int=0";
_vvvvvvvvvvvvv1 = (int) (0);
 //BA.debugLineNum = 20;BA.debugLine="End Sub";
return "";
}
public static String  _recibeparametros(String _psimei,String _psmail) throws Exception{
int _iseparador = 0;
 //BA.debugLineNum = 93;BA.debugLine="Sub RecibeParametros(psImei As String, psMail As S";
 //BA.debugLineNum = 94;BA.debugLine="Dim idPalabra As Int";
_vvvvvvvvvvvv7 = 0;
 //BA.debugLineNum = 95;BA.debugLine="Dim iSeparador As Int = psImei.IndexOf (\"%\")";
_iseparador = _psimei.indexOf("%");
 //BA.debugLineNum = 96;BA.debugLine="If iSeparador>1 Then";
if (_iseparador>1) { 
 //BA.debugLineNum = 97;BA.debugLine="sImei = psImei.SubString2(0, iSeparador)";
_vvvvvvvvvvvv5 = _psimei.substring((int) (0),_iseparador);
 }else {
 //BA.debugLineNum = 99;BA.debugLine="sImei = \"\"";
_vvvvvvvvvvvv5 = "";
 };
 //BA.debugLineNum = 102;BA.debugLine="If psImei.Length>=iSeparador+1 Then";
if (_psimei.length()>=_iseparador+1) { 
 //BA.debugLineNum = 103;BA.debugLine="idPalabra = psImei.SubString(iSeparador+1)";
_vvvvvvvvvvvv7 = (int)(Double.parseDouble(_psimei.substring((int) (_iseparador+1))));
 }else {
 //BA.debugLineNum = 105;BA.debugLine="idPalabra = 0";
_vvvvvvvvvvvv7 = (int) (0);
 };
 //BA.debugLineNum = 108;BA.debugLine="sMail = psMail";
_vvvvvvvvvvvv6 = _psmail;
 //BA.debugLineNum = 110;BA.debugLine="grabaLogRemoto";
_vvvvvvvvvvvvvvvvvvvvvvvvvv3();
 //BA.debugLineNum = 111;BA.debugLine="bCorrer = True";
_vvvvvvvvvvvv4 = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 112;BA.debugLine="End Sub";
return "";
}
public static String  _service_create() throws Exception{
 //BA.debugLineNum = 21;BA.debugLine="Sub Service_Create";
 //BA.debugLineNum = 23;BA.debugLine="sNotif.Initialize";
_vvvvvvvvvvvv0.Initialize();
 //BA.debugLineNum = 24;BA.debugLine="sNotif.Icon = \"Matete.ico\"";
_vvvvvvvvvvvv0.setIcon("Matete.ico");
 //BA.debugLineNum = 25;BA.debugLine="sNotif.SetInfo(\"Matete\",\"Service Running\",Main)";
_vvvvvvvvvvvv0.SetInfo(processBA,"Matete","Service Running",(Object)(mostCurrent._vvvvvvvvvvvvvvv6.getObject()));
 //BA.debugLineNum = 26;BA.debugLine="sNotif.Sound = False";
_vvvvvvvvvvvv0.setSound(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 27;BA.debugLine="sNotif.Vibrate = False";
_vvvvvvvvvvvv0.setVibrate(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 28;BA.debugLine="sNotif.Notify(1)";
_vvvvvvvvvvvv0.Notify((int) (1));
 //BA.debugLineNum = 29;BA.debugLine="Service.StartForeground(1,sNotif)";
mostCurrent._service.StartForeground((int) (1),(android.app.Notification)(_vvvvvvvvvvvv0.getObject()));
 //BA.debugLineNum = 30;BA.debugLine="bCorrer = False";
_vvvvvvvvvvvv4 = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 31;BA.debugLine="xHttpCli.initialize(\"xHttpCli\")";
_vvvvvvvvvvvv3.Initialize("xHttpCli");
 //BA.debugLineNum = 32;BA.debugLine="End Sub";
return "";
}
public static String  _service_destroy() throws Exception{
 //BA.debugLineNum = 43;BA.debugLine="Sub Service_Destroy";
 //BA.debugLineNum = 45;BA.debugLine="End Sub";
return "";
}
public static String  _service_start(anywheresoftware.b4a.objects.IntentWrapper _startingintent) throws Exception{
 //BA.debugLineNum = 34;BA.debugLine="Sub Service_Start (StartingIntent As Intent)";
 //BA.debugLineNum = 37;BA.debugLine="If bCorrer  Then";
if (_vvvvvvvvvvvv4) { 
 //BA.debugLineNum = 38;BA.debugLine="grabaLogRemoto";
_vvvvvvvvvvvvvvvvvvvvvvvvvv3();
 };
 //BA.debugLineNum = 41;BA.debugLine="End Sub";
return "";
}
public static String  _xhttpcli_responseerror(anywheresoftware.b4a.http.HttpClientWrapper.HttpResponeWrapper _response,String _reason,int _statuscode,int _tarea) throws Exception{
String _srespuesta = "";
 //BA.debugLineNum = 85;BA.debugLine="Sub xHttpCli_ResponseError (Response As HttpRespon";
 //BA.debugLineNum = 86;BA.debugLine="Dim sRespuesta As String";
_srespuesta = "";
 //BA.debugLineNum = 87;BA.debugLine="If Response <> Null Then";
if (_response!= null) { 
 //BA.debugLineNum = 88;BA.debugLine="sRespuesta=Response.GetString(\"UTF8\")";
_srespuesta = _response.GetString("UTF8");
 //BA.debugLineNum = 89;BA.debugLine="Response.Release";
_response.Release();
 };
 //BA.debugLineNum = 91;BA.debugLine="StopService(Me)";
anywheresoftware.b4a.keywords.Common.StopService(processBA,svcbaseremota.getObject());
 //BA.debugLineNum = 92;BA.debugLine="End Sub";
return "";
}
public static String  _xhttpcli_responsesuccess(anywheresoftware.b4a.http.HttpClientWrapper.HttpResponeWrapper _response,int _tarea) throws Exception{
String _resultstring = "";
 //BA.debugLineNum = 79;BA.debugLine="Sub xHttpCli_ResponseSuccess (Response As HttpResp";
 //BA.debugLineNum = 80;BA.debugLine="Dim resultString As String";
_resultstring = "";
 //BA.debugLineNum = 81;BA.debugLine="resultString = Response.GetString(\"UTF8\")";
_resultstring = _response.GetString("UTF8");
 //BA.debugLineNum = 82;BA.debugLine="StopService(Me)";
anywheresoftware.b4a.keywords.Common.StopService(processBA,svcbaseremota.getObject());
 //BA.debugLineNum = 83;BA.debugLine="End Sub";
return "";
}
}
