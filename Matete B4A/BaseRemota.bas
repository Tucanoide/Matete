Type=Service
Version=4.3
ModulesStructureVersion=1
B4A=true
@EndOfDesignText@
#Region  Service Attributes 
	#StartAtBoot: False
#End Region

Sub Process_Globals
	'These global variables will be declared once when the application starts.
	'These variables can be accessed from all modules.
	
	Dim xHttpCli As HttpClient
	Dim bCorrer As Boolean
	Dim sgDireccionRemota As String = "http://matrem.esy.es/ukit.php"
	Dim sNotif As Notification
	Dim iReintentos As Int=0
	
	Dim sQueryRemoto As String
	Dim sQueryLocalOk As String
	Dim dbBaseLocal As SQLCipher
	
	
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
If bCorrer  Then
	Graba
End If
End Sub

Sub Service_Destroy

End Sub

Sub Graba
	Dim req As HttpRequest, sFecha As String

	req.InitializePost2(sgDireccionRemota, sQueryRemoto.GetBytes("UTF8"))
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

	'GrabaLocalOk(sQueryLocalOk, dbBaseLocal)

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

'el servicio ejecuta un query remoto, si es exitoso, ejecuta el query psQueryEjecutaOk en la base pBase
Sub RecibeParametros(psQueryEjecutarRemoto As String, psQueryEjecutarOk As String)

sQueryRemoto = psQueryEjecutarRemoto
sQueryLocalOk = psQueryEjecutarOk
'dbBaseLocal = pBase
Graba
bCorrer = True
End Sub


