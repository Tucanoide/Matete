Type=Activity
Version=5.8
ModulesStructureVersion=1
B4A=true
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: False
#End Region

Sub Process_Globals
	'These global variables will be declared once when the application starts.
	'These variables can be accessed from all modules.

    Dim TwitterP As TwitterProvider
    TwitterP.Initialize("kRLepRJLA0NrCVkkr6yzYCIrt", "6wNH3hryKNS73ggMAvrMd1cK8tBbG4Tvh8CbNeGuc4s1zskQYv")
	
	Public sLetrasElegir As String
	Public sDefinicion As String
	Public sPalabra As String
	Public iCostoLetra As Int
	Public sLetrasPalabra As String
	Dim sMensajeJugador As String
	Dim g_DirGrabable As String
	Dim bPublicarEnEvent As Boolean = False
	Dim iIntentos As Int = 0
	Dim g_sqlBaseLocalUsuario As SQLCipher

End Sub

Sub Globals
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.

    Dim ThisActivity As SocialApiActivity

	Private txtMensajeJugador As EditText
	
	Private lblLen As Label
	Private imgIcono As ImageView
	Private imgScreenshot As ImageView
	Private imgVolver As ImageView
	Private imgCancelar As ImageView
	Private lblcancelar As Label
	Private imgMenu As ImageView
	Private lblPanel3 As Label
	Private imgPublicar As ImageView

	Private lblPublicar As Label
	Private lblTitulo As Label
	Private Panel1 As Panel
	Private Panel2 As Panel
	Private Panel3 As Panel
	Private Panel4 As Panel
	Private Panel6 As Panel

	Private TimerConectar As Timer

End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("TwitterV2")
	If FirstTime Then
			g_DirGrabable = Publicos.Get_DirectorioBase
	End If

	TimerConectar.initialize("TimerConectar",0)
End Sub

Sub Activity_Resume
	imgScreenshot.Bitmap = LoadBitmap(g_DirGrabable, "PruebaSS.png")
	
	'imgScreenshot.Bitmap = LoadBitmap(g_DirGrabable, "PruebaSS.png")

	Publicos.CargaDeviceValues
	
	V2_SetPantalla
	MensajeMatete
	DoEvents
	'txtMensajeJugador.Text = sMensajeJugador 	
	
	TwitterP.SetActivity(ThisActivity.Initialize("Twitter"))

	'usa un timer para conectar automaticamente despues de 2 segundos

	If TwitterP.Connected = False Then	
		TimerConectar.Interval = 3000
		TimerConectar.Enabled = True
	Else
		TimerConectar.Enabled = False
	End If
End Sub

Sub Activity_Pause (UserClosed As Boolean)
If UserClosed Then
	StartActivity("Jugar")
	Activity.Finish
End If	
End Sub


Sub Twitter_Event (Provider As SocialApiProvider)
'	pnlTwittear.Enabled = Provider.Connected
	'pnlCubrir.visible = Not (Provider.connected)
	'txtMensajeJugador.Enabled = Provider.Connected
	'pnlLoco.Enabled = Provider.connected
	'lblLen.Visible = Provider.Connected
	'pnlCubrir.BringToFront
	
	'If Provider.Connected Then
	'	btnPublicar.Text ="Publicar"
	'Else
	'	btnPublicar.Text = "Conectar"
	'End If
End Sub

Sub Twitter_Connected (Provider As SocialApiProvider)
    'Msgbox(Provider.Me, "!")
	TimerConectar.Enabled = False
	If bPublicarEnEvent Then
		bPublicarEnEvent = False
		TwitterPublicar(txtMensajeJugador.text)
	End If
End Sub

Sub Twitter_Disconnected (Provider As SocialApiProvider)
    Msgbox("Bye bye!", "JustDisconnected!")
End Sub

Sub Twitter_Failed (Provider As SocialApiProvider)
    If Msgbox2("Failed to actualize your details."&CRLF&CRLF&"Retry?", "Error", "Yes", "No", "", Null) = DialogResponse.POSITIVE Then
        Provider.Retry
    End If
End Sub

Sub Conectar
    TwitterP.Login(True)
End Sub

Sub BtnDisconnect_Click
    TwitterP.Logout
End Sub

Sub Publicar

	If Not (TwitterP.Connected) Then ' si no esta conectado, manda a conectar y prende la bandera para que publique cuando se conecte
		bPublicarEnEvent = True
		Conectar
	Else

		If txtMensajeJugador.Text.Length >0 Then
	 		If txtMensajeJugador.Text.Length >140 Then 
				ToastMessageShow("El mensaje es muy largo para Twitter!", True)
			Else
				If Msgbox2("¿Confirmas la publicación? Ganarás "& iCostoLetra & " Neurona" & IIF(iCostoLetra>1, "s","")  ,"Twitter", "Publicar", "Cancelar","", Null) = DialogResponse.POSITIVE Then 
					If TwitterPublicar (txtMensajeJugador.text) Then
						Publicos.Usuario_SumarNeuronas(iCostoLetra, g_sqlBaseLocalUsuario)
						Twitter_UpdateUso
						Jugar.bPublicoEnTwitter= True
						StartActivity("Jugar")		
					Else
						ToastMessageShow("No fue posible publicar en Twitter!", True)
					End If	
				End If	   
			End If

		Else 
			ToastMessageShow("Ingresa un mensaje para publicar!", True)	
		End If
	End If
End Sub


Sub MensajeMatete
Dim sMensaje As String
	

	sMensaje= sDefinicion & " #MateteDivergente"

	'sMensaje = sMensaje & Publicos.Get_Letrasparaprovider(sLetrasPalabra) 
	'sMensaje = sMensaje& Chr(10) & Publicos.provider_letras(sLetrasElegir,1)
	'sMensaje= sMensaje& Chr(10) & Publicos.provider_letras(sLetrasElegir,2)
	
	txtMensajeJugador.Text = sMensaje
	
End Sub


Sub btnvolver_click
	StartActivity("Jugar")
End Sub



Sub btnpegar_click
	txtMensajeJugador.Text = sDefinicion
End Sub


Sub btnHashtag_click
Dim sstr As String
sstr = txtMensajeJugador.text
If Not (sstr.Contains ("#MateteDivergente")) Then
	txtMensajeJugador.Text = txtMensajeJugador.Text & " #MateteDivergente"
End If
End Sub


Sub txtMensajeJugador_TextChanged (Old As String, New As String)

	lblLen.Text = New.Length
	If New.Length > 140 Then
		lblLen.TextColor = Colors.Red
	Else
		lblLen.Textcolor = Colors.white	
	End If

End Sub


Sub TwitterPublicar (sMensaje As String) As Boolean
Dim bRet As Boolean
	' por las dudas que haya caracteres invalidos los quita (enconde los reemplaza, por ejemplo los blancos los encodea y despues Twitter o FB lo decodifican)
'	Dim sEncoded As StringUtils
'	sMensaje= sEncoded.EncodeUrl(sMensaje, "UTF8")
	
    Dim Result As TwitterResult
    Result = TwitterP.Tweet(sMensaje)
	If Result = Null Then
		Msgbox("Publicación anterior pendiente.", "Twitter")
	Else
		If Result.Canceled Then Return
		' si pudo publicar, le suma 10 monedas
		If Result.Success Then
			bRet = True
		Else
			ToastMessageShow("No fue posible publicar en Twitter!", True)
		End If
	End If
Return bRet
End Sub

Sub Twitter_UpdateUso As Boolean
'solo puede publicar una vez en facebook por cada palabra, asi que graba en avance cuando ya lo hizo para que no pueda repetir
Dim sSql As String, bRet As Boolean, sFecha As String

Try
		sSql = "Update Avance Set Ind1=1 where idPalabra = (select max(idpalabra) from avance)"
		g_sqlBaseLocalUsuario.ExecNonQuery (sSql)
		bRet = True
Catch
		bRet = False
End Try
Return bRet
End Sub


Sub V2_SetPantalla
Dim iColorLetra As Int

	iColorLetra = Colors.RGB(151, 89, 152)
	Activity.Color = Colors.white
	Panel1.Color = Colors.white ' menu. titulo, icono facebook
	Panel2.color = Colors.RGB(231,234,226) 'screenshot
	Panel3.Color = Colors.RGB(177, 109, 177) 'subtitulo, "ESCRIBE UN COMENTARIO" 
	Panel4.color = Colors.white  ' text input mensaje
	Panel6.Color = Colors.white ' cancelar-publicar

'distribucion de paneles
	Panel1.Width = 100%x : 	Panel2.Width = 100%x
	Panel3.Width = 100%x : 	Panel4.Width = 100%x
	Panel6.Width = 100%x
	
	Panel1.Height = Activity.Height * 0.10: Panel1.Top = 0
	Panel2.Height = Activity.Height * 0.35: Panel2.Top = Panel1.Top + Panel1.height
	Panel3.Height = Activity.Height * 0.10: Panel3.Top = Panel2.Top + Panel2.height
	Panel4.Height = Activity.Height * 0.25: Panel4.Top = Panel3.Top + Panel3.height
	Panel6.Height = Activity.Height * 0.14: Panel6.Top = Panel4.Top + Panel4.height
	
''Panel 1
	imgMenu.Left = Panel1.Width * 0.05
	imgMenu.Height=Panel1.Height *0.5
	imgMenu.Width=imgMenu.Height
	Publicos.CentrarControlEnPanel(Panel1, imgMenu, False, True)
	
	'lblTitulo.Text = "PUBLICAR EN TWITTER"
	lblTitulo.Height = Panel1.Height*0.5
	lblTitulo.Width = 70%x
	lblTitulo.textcolor = Colors.RGB(177, 109, 177) 
	lblTitulo.Left = 0
	Publicos.CentrarControlEnPanel(Panel1, lblTitulo, True, True)
	Publicos.SetLabelTextSize(lblTitulo, lblTitulo.Text, 20,3, 80)
	
	imgIcono.Height = Panel1.Height * 0.6
	imgIcono.Width = imgIcono.Height
	imgIcono.Left = Panel1.Width - imgIcono.Width - imgMenu.Left
	Publicos.CentrarControlEnPanel(Panel1, imgIcono, False, True) 'centra vertical en el panel
	
	
'Panel2 - Muestra Screenshot - tiene el alto del 3+4+5 de jugar
	imgScreenshot.Height = Panel2.Height*0.98
	'calcula el ancho para mantener la relación de la pantalla
	imgScreenshot.Width = imgScreenshot.Height * Publicos.g_DeviceValuesWidth  / Publicos.g_DeviceValuesHeight 
	Publicos.CentrarControlEnPanel(Panel4, imgScreenshot, True, False)
	imgScreenshot.Top = Panel4.Height*0.01

'Panel 3 - Escribe un comentario
	lblPanel3.TextColor = Colors.white
	lblPanel3.Text = "ESCRIBE UN COMENTARIO"
	lblPanel3.Width = Panel3.Width
	lblPanel3.Height = Panel3.Height * 0.6
	Publicos.CentrarControlEnPanel(Panel3, lblPanel3, True, True)
	lblPanel3.TextSize = lblTitulo.textsize
	'Publicos.SetLabelTextSize(lblPanel3, lblPanel3.text, 20,6, 100)
	


'Panel 4 - edit text - tiene el alto del 2 de jugar
	
	txtMensajeJugador.Width = Panel4.Width * 0.9
	txtMensajeJugador.Height = Panel4.Height 
	txtMensajeJugador.Gravity = Gravity.TOP+Gravity.CENTER_HORIZONTAL
	txtMensajeJugador.Text = ""
	txtMensajeJugador.TextColor = iColorLetra
	txtMensajeJugador.TextSize = 20
	Publicos.CentrarControlEnPanel(Panel4, txtMensajeJugador, True, True)
	txtMensajeJugador.BringToFront
	txtMensajeJugador.SingleLine = False
	
	

'Panel6 - Publicar / Cancelar
	imgCancelar.Height = Panel6.Height * 0.70
	imgCancelar.Width = imgCancelar.Height
	imgPublicar.Height = imgCancelar.Height
	imgPublicar.Width = imgCancelar.Width
	
	lblcancelar.TextColor = iColorLetra
	lblPublicar.TextColor = iColorLetra
	lblcancelar.Width = Panel6.Width * 0.2
	lblPublicar.Width = lblcancelar.Width
	
	lblcancelar.Height = Panel6.Height * 0.2
	lblPublicar.Height = lblcancelar.Height
	
	Publicos.SetLabelTextSize(lblcancelar, "PUBLICAR", 20,6, 100)
	lblPublicar.TextSize=lblcancelar.textsize

	Dim aCentrar() As View
	aCentrar = Array As View (  imgCancelar, imgPublicar) 	
	Publicos.CentrarArrayControlesEnPanel ( Panel6, aCentrar, False)
	
	imgCancelar.top = 0: imgPublicar.Top = 0
	
	lblcancelar.Top = imgCancelar.Top + imgCancelar.Height + 1dip
	lblPublicar.Top = lblcancelar.top

	lblcancelar.Left = Publicos.ViewAlinearCentro(imgCancelar, lblcancelar)	
	lblPublicar.Left = Publicos.ViewAlinearCentro(imgPublicar, lblPublicar)


End Sub


Sub timerConectar_Tick
Log ("Intentos timer:" & iIntentos)
If iIntentos < 1 AND TwitterP.Connected = False Then
	If TwitterP.Busy = False Then
		Conectar 
		iIntentos = iIntentos + 1
	End If
End If
If iIntentos >=2 Then
	TimerConectar.Enabled = False
End If
End Sub



Sub lblCancelar_click'cancelar
	Jugar.bCanceloFacebookoTwitter = True
	StartActivity("Jugar")
	Activity.Finish
	
End Sub


Sub lblPublicar_click
If txtMensajeJugador.Text.Trim = "" Then
	Msgbox("Debes ingresar un mensaje para publicar", "Cataplún")
Else
	Publicar
End If
End Sub


Sub imgPublicar_Click
	lblPublicar_click
End Sub
Sub imgCancelar_Click
	lblCancelar_click
End Sub

Sub IIF(c As Boolean, TrueRes As String, FalseRes As String)
 If c Then Return TrueRes Else Return FalseRes
End Sub
