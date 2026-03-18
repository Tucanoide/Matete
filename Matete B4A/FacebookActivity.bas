Type=Activity
Version=5.8
ModulesStructureVersion=1
B4A=true
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: false
#End Region

Sub Process_Globals
	'These global variables will be declared once when the application starts.
	'These variables can be accessed from all modules.

	Public sLetrasElegir As String
	Public sDefinicion As String
	Public sPalabra As String
	Public iCostoLetra As Int
	Public sLetrasPalabra As String

	Dim sPublicarFacebook As String = "S"
	Dim bBotonFacebookPresionado As Boolean = False

	Dim bPrenderFacebook As Boolean = True

	Public Facebook As FacebookProvider
	
	Dim sMensajeJugador As String
	
	Dim iIntentos As Int = 0
	Dim g_DirGrabable As String

	Dim g_sqlBaseLocalUsuario As SQLCipher
End Sub

Sub Globals
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.

	Private txtMensajeJugador As EditText
	Dim ThisActivity As SocialApiActivity
	
	Private lblMensajeMatete As Label

	Private timerConectar As Timer
	

	Private Panel1 As Panel
	Private Panel2 As Panel
	Private Panel4 As Panel
	Private Panel6 As Panel
	Private imgScreenshot As ImageView
	Private lblOpcion1 As Label
	Private lblOpcion2 As Label
	Private lblTitulo As Label
	Private imgIconoFacebook As ImageView
	Private imgMenu As ImageView
	Private Panel3 As Panel
	Private lblPanel3 As Label
	Private imgCancelar As ImageView
	Private imgPublicar As ImageView
	Private lblCancelar As Label
	Private lblPublicar As Label
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("Facebook")
	'Log(Facebook.HashKey)

	If FirstTime Then
		g_DirGrabable = Publicos.Get_DirectorioBase
	End If

	timerConectar.initialize("TimerConectar",0)
End Sub

Sub Activity_Resume
	'Publicos.Screenshot(Activity, g_DirGrabable, "PruebaSS.png")
	imgScreenshot.Bitmap = LoadBitmap(g_DirGrabable, "PruebaSS.png")
	
	Publicos.CargaDeviceValues
	
	V2_SetPantalla
	DoEvents
	
	Facebook.SetActivity(ThisActivity.Initialize("facebook"))
	
	txtMensajeJugador.Text = sMensajeJugador 	

	'usa un timer para conectar automaticamente despues de 2 segundos
	If Facebook.Connected = False Then	
		timerConectar.Interval = 3000
		timerConectar.Enabled = True
	Else
		timerConectar.Enabled  = False
	End If
End Sub

Sub Activity_Pause (UserClosed As Boolean)
	'Log("Facebook pause")
	If UserClosed Then
		StartActivity("Jugar")
		Activity.Finish
	End If
End Sub



Sub lblCancelar_click'cancelar
	Jugar.bCanceloFacebookoTwitter = True
	StartActivity("Jugar")
	Activity.Finish
	
End Sub

Sub Publicar
	Dim sMonedas As String, iret As Int
	
		
		iret = Msgbox2("¿Confirmas la publicación? Ganarás " & iCostoLetra & " neurona" & IIF(iCostoLetra>1, "s","")  , "Facebook", "Confirmar", "Cancelar","", Null) 
		If iret = DialogResponse.POSITIVE Then
			bBotonFacebookPresionado = True
			'og("Facebook intentando conectar")
			If FacebookConectar Then 
				'og("Facebook conectado en click")
				' si se pudo conectar, manda a publicar, sino publica cuando se dispare el evento de conexión
				FacebookPublicar
				sMensajeJugador = ""
			Else
				sMensajeJugador = txtMensajeJugador.text
				ToastMessageShow("No fue posible publicar en tu muro!", True)
			End If	
		End If
End Sub

Sub FacebookConectar As Boolean
'devuelve true si ya estaba conectado

'hace la conexion con facebook y pide permisos para publicar
Try
	Dim Result As FacebookResult
	bBotonFacebookPresionado=True
	If Facebook.Connected = False Then
		Result = Facebook.Login(Array As String (Facebook.Constants.Permissions.Email))
	 End If
Catch
	Publicos.ManejaError("Conexion Facebook", LastException.Message)
End Try
 Return Facebook.Connected
End Sub

Sub FacebookPidePermisoPublicar
	Facebook.RequestPublishPermissions	
End Sub

Sub FacebookGetTienePermisoPublicar As Boolean
'verifica si el usuario tiene permisos para publicar
Try
	Dim HasPublishPermission As Boolean = False
    For I = 0 To Facebook.Permissions.Length-1
        If Facebook.Permissions(I) = Facebook.Constants.Permissions.PUBLISH_ACTIONS Then
            HasPublishPermission = True
            Exit
        End If
    Next 
Catch
	Publicos.ManejaError("FacebookPermisoPublicar", LastException.Message)

End Try

	
Return HasPublishPermission 
End Sub


Sub FacebookPublicar
	'FacebookConectar
	'og("Facebook publicar inicio")
	If Facebook.Connected Then
		'og("Facebook publicar conectado")
	 	If bBotonFacebookPresionado Then ' si hizo click en el boton de facebook
			bBotonFacebookPresionado = False
			'og("Facebook publicar ingreso")
			If FacebookGetTienePermisoPublicar = False Then
				'og("Facebook pide permiso")
				FacebookPidePermisoPublicar
			End If 
			If FacebookGetTienePermisoPublicar Then
				'og ("Facebook tiene permisos")
				If FacebookPostear Then
					Publicos.Usuario_SumarNeuronas(iCostoLetra, g_sqlBaseLocalUsuario)
					Facebook_UpdateUso
					Jugar.bPublicoEnFacebook = True
					StartActivity("Jugar")		
					Activity.finish
				Else
					ToastMessageShow("No fue posible publicar en tu muro!", True)
				End If	
			Else
				ToastMessageShow("No fue posible publicar en tu muro!", True)
				
			End If
		End If
	Else
		ToastMessageShow("No fue posible publicar en tu muro!", True)
				
	End If
End Sub

Sub Facebook_Disconnected (Provider As SocialApiProvider)
    'Msgbox("Bye bye!", "JustDisconnected!")
End Sub

Sub Facebook_Failed (Provider As SocialApiProvider)
    'If Msgbox2("Falló la conexión con Facebook"&CRLF&CRLF&"Reintentar?", "Error", "Yes", "No", "", Null) = DialogResponse.POSITIVE Then
    '    Provider.Retry
    'End If
End Sub

Sub Facebook_Event (Provider As SocialApiProvider)
'Log("FacebookEvent" & Provider.Connected)
End Sub

Sub Facebook_Connected (Provider As SocialApiProvider)
Try
	If bPrenderFacebook Then
		If Not(Facebook.HasPermission(Facebook.Constants.Permissions.PUBLISH_ACTIONS)) Then
	    	Facebook.RequestPublishPermissions
		End If
		If bBotonFacebookPresionado Then
			bBotonFacebookPresionado = False
			FacebookPublicar
		End If
	End If
Catch
	Publicos.ManejaError("Conexion Facebook", LastException.Message)
End Try
	
End Sub

Sub FacebookPostear As Boolean
	Dim Result As FacebookResult, bRet As Boolean = False, Result As FacebookResult
	'og("posteando")
	Dim Result As FacebookResult
	Dim sFrase As String, sDefinicion As String
	Facebook.RefreshPermissions

	
	sFrase = txtMensajeJugador.Text
	'Result = Facebook.PublishPost(sFrase, _ 
	'	"https://play.google.com/store/apps/details?id=com.matetejuego.free", "", _ 
'		"", "", "")
	'Dim sFoto As String
	'sFoto = g_DirGrabable & "/PruebaSS.png"
	'Result = Facebook.PublishPost(sFrase, "", sFoto , "", "", "")
	
	
	'sFrase = "Prueba_    .renglon" & Chr(20) & " Ybucid-2"
	Dim sEncoded As StringUtils
	sFrase= sEncoded.EncodeUrl(sFrase, "UTF8")
	Result = Facebook.UploadPhoto(g_DirGrabable, "PruebaSS.png", sFrase, "")
	
	'Publicos.Archivo_CopiaDesdeAssets("Prueba.mp4", True, g_DirGrabable)
	'Result = Facebook.UploadVideo(g_DirGrabable, "Prueba.mp4", sFrase)
	
		'Facebook.PublishPost(message As String, link As String, picture As String, 
	'Name As String, 'caption As String, description As String)

	'Msgbox(sDefinicion, "simula publicacion")
	
	If Result = Null Then
		Msgbox("Publicación anterior pendiente.", "Facebook")
	Else
		If Result.Validated Then
			If Result.Canceled Then Return
		' si pudo publicar, le suma 10 monedas
			If Result.Success Then
				bRet = True
			Else
				ToastMessageShow("No fue posible publicar en tu muro!", True)
				
			End If
		End If
	End If
Return bRet	
End Sub


Sub Facebook_UpdateUso  As Boolean
'solo puede publicar una vez en facebook por cada palabra, asi que graba en avance cuando ya lo hizo para que no pueda repetir
Dim sSql As String, bRet As Boolean, sFecha As String

Try
		sSql = "Update Avance Set publicofacebook=1 where idPalabra = (select max(idpalabra) from avance)"
		g_sqlBaseLocalUsuario.ExecNonQuery (sSql)
		bRet = True
Catch
		bRet = False
End Try
Return bRet
End Sub


Sub lblPublicar_click
If txtMensajeJugador.Text.Trim = "" Then
	Msgbox("Debes ingresar un mensaje para publicar", "Cataplún")
Else
	Publicar
End If
End Sub

Sub imgFacebook_Click
lblPublicar_click

End Sub


Sub btnpegar_click
	'txtMensajeJugador.Text = Chr(10) & lblDefinicion.Text & Chr(10) & lblMensajeMatete.text
End Sub

Sub timerConectar_Tick
Log ("Intentos timer: " & iIntentos & " " & Facebook.connected)
If Facebook.Connected = False AND iIntentos = 0 Then	
	iIntentos = 1
	FacebookConectar
End If
timerConectar.Enabled = False
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
	
	lblTitulo.Text = "PUBLICAR EN FACEBOOK"
	lblTitulo.Height = Panel1.Height*0.5
	lblTitulo.Width = 70%x
	lblTitulo.textcolor = Colors.RGB(177, 109, 177) 
	lblTitulo.Left = 0
	Publicos.CentrarControlEnPanel(Panel1, lblTitulo, True, True)
	Publicos.SetLabelTextSize(lblTitulo, lblTitulo.Text, 20,3, 80)
	
	imgIconoFacebook.Height = Panel1.Height * 0.6
	imgIconoFacebook.Width = imgIconoFacebook.Height
	imgIconoFacebook.Left = Panel1.Width - imgIconoFacebook.Width - imgMenu.Left
	Publicos.CentrarControlEnPanel(Panel1, imgIconoFacebook, False, True) 'centra vertical en el panel
	
	
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
	txtMensajeJugador.Gravity = Gravity.TOP+Gravity.left
	txtMensajeJugador.Text = ""
	txtMensajeJugador.TextColor = iColorLetra
	txtMensajeJugador.TextSize = 20
	Publicos.CentrarControlEnPanel(Panel4, txtMensajeJugador, True, True)
	txtMensajeJugador.SingleLine = False


'Panel6 - Publicar / Cancelar
	imgCancelar.Height = Panel6.Height * 0.70
	imgCancelar.Width = imgCancelar.Height
	imgPublicar.Height = imgCancelar.Height
	imgPublicar.Width = imgCancelar.Width
	
	lblCancelar.TextColor = iColorLetra
	lblPublicar.TextColor = iColorLetra
	lblCancelar.Width = Panel6.Width * 0.2
	lblPublicar.Width = lblCancelar.Width
	
	lblCancelar.Height = Panel6.Height * 0.2
	lblPublicar.Height = lblCancelar.Height
	
	Publicos.SetLabelTextSize(lblCancelar, "PUBLICAR", 20,6, 100)
	lblPublicar.TextSize=lblCancelar.textsize

	Dim aCentrar() As View
	aCentrar = Array As View (  imgCancelar, imgPublicar) 	
	Publicos.CentrarArrayControlesEnPanel ( Panel6, aCentrar, False)
	
	imgCancelar.top = 0: imgPublicar.Top = 0
	
	lblCancelar.Top = imgCancelar.Top + imgCancelar.Height + 1dip
	lblPublicar.Top = lblCancelar.top

	lblCancelar.Left = Publicos.ViewAlinearCentro(imgCancelar, lblCancelar)	
	lblPublicar.Left = Publicos.ViewAlinearCentro(imgPublicar, lblPublicar)


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
