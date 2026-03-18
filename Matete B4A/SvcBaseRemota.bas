Type=Service
Version=5.8
ModulesStructureVersion=1
B4A=true
@EndOfDesignText@
#Region  Service Attributes 
	#StartAtBoot: False
	'#StartCommandReturnValue: android.app.Service.START_STICKY
#End Region

Sub Process_Globals
	'These global variables will be declared once when the application starts.
	'These variables can be accessed from all modules.
	Dim xHttpCli As HttpClient
	Dim bCorrer As Boolean
	
	'Dim sgDireccionRemota As String = "http://matrem.esy.es/paises.php"
	Dim sgDireccionRemota As String = "http://matrem.esy.es/ukit.php"
	
	
	Dim sImei As String, sMail As String, idPalabra As Int
	
	Dim sNotif As Notification
	Dim iReintentos As Int=0
End Sub
Sub Service_Create
	'tipo de ejecucion
	sNotif.Initialize
	sNotif.Icon = "Matete.ico"
	sNotif.SetInfo("Matete","Service Running",Main)
	sNotif.Sound = False
	sNotif.Vibrate = False
	sNotif.Notify(1)
	Service.StartForeground(1,sNotif)
	bCorrer = False
	xHttpCli.initialize("xHttpCli")
End Sub

Sub Service_Start (StartingIntent As Intent)

'CallSub(Main, "IconoLogRemInicia"&bCorrer)
If bCorrer  Then
	grabaLogRemoto
End If

End Sub

Sub Service_Destroy

End Sub

Sub grabaLogRemoto
	Dim req As HttpRequest, sFecha As String
	Dim Query As String
	'Query="INSERT INTO paises (ID, nombre, poblacion) VALUES ('x1','prueba2',0)"
	If False Then
		DateTime.DateFormat ="yyyy-MM-dd hh:mm:ss"
		sFecha = DateTime.Date(DateTime.now)
		Query = "Insert Into jugadores_log(imei, mail, ingreso, idPalabra) values ('" & sImei & "','" & sMail & "','" & _ 
			sFecha &"'," & idPalabra & ")"
	Else 
		'Query = "call Ingresos_i ('mail', 'imei', 99,99, '')"
		Query = "call Ingresos_i ('" & sMail & "'," & sImei & "', 0, 0, '')"
	End If


	req.InitializePost2(sgDireccionRemota, Query.GetBytes("UTF8"))
	xHttpCli.Execute(req, 1)

' Evita el error android.os.NetworkOnMainThreadException
' Activar librerías Phone y Reflection
	Dim p As Phone
	If p.SdkVersion >= 9 Then
		Dim R As Reflector
   		R.Target = R.CreateObject("android.os.StrictMode$ThreadPolicy$Builder")
   		R.Target = R.RunMethod("permitAll")
  	 	R.Target = R.RunMethod("build")
   		R.RunStaticMethod("android.os.StrictMode", "setThreadPolicy", _
      	Array As Object(R.Target), Array As String("android.os.StrictMode$ThreadPolicy"))
	End If
End Sub


Sub xHttpCli_ResponseSuccess (Response As HttpResponse, tarea As Int)
	Dim resultString As String
	resultString = Response.GetString("UTF8")
	StopService(Me)
End Sub

Sub xHttpCli_ResponseError (Response As HttpResponse, Reason As String, StatusCode As Int, tarea As Int)
	Dim sRespuesta As String
	If Response <> Null Then
		sRespuesta=Response.GetString("UTF8")
		Response.Release
	End If
	StopService(Me)
End Sub 
Sub RecibeParametros(psImei As String, psMail As String)
Dim idPalabra As Int
Dim iSeparador As Int = psImei.IndexOf ("%")
If iSeparador>1 Then
	sImei = psImei.SubString2(0, iSeparador)
Else
	sImei = ""
End If

If psImei.Length>=iSeparador+1 Then
	idPalabra = psImei.SubString(iSeparador+1)
Else
	idPalabra = 0
End If
'sImei = psImei
sMail = psMail

grabaLogRemoto
bCorrer = True
End Sub
