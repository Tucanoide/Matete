Type=StaticCode
Version=5.8
ModulesStructureVersion=1
B4A=true
@EndOfDesignText@
' Casos
' Probar Usuario nuevo no premium, usuario nuevo y hacerlo premium, usuario viejo no premium, usuario viejo hacerlo premium
'
'
'
'
'
Sub Process_Globals
	'These global variables will be declared once when the application starts.
	'These variables can be accessed from all modules.
	Public sNombreMonedas As String ="Neuronas"
	
	Public g_DeviceValuesHeight As Int, g_DeviceValuesWidth As Int
	Public  g_DeviceValuesScreenSize As Float, g_DeviceValuesScale As Float
	
	'MODIFICAR TAMBIEN LAS MISMAS VARIABLES EN Jugar activity
	Dim g_sPswBaseJ  As String = "NadaAnsioDeNada"
	Dim g_sPswBaseU As String = "MientrasDuraElInstante2"
	Public g_sqlBaseLocalJuego As SQLCipher, g_sqlBaseLocalUsuario As SQLCipher
	Public g_NombreBaseLocalJuego As String ="BaseJ.sqlite"
	Public g_NombreBaseLocalUsuario As String = "BaseUE.sqlite"

	'Type t
	Type tColoresPaneles (iNroPanel As Int, iRgb1 As Int, iRgb2 As Int, iRgb3 As Int) 
	
'' INAPP PRODUCTS - TIPOS DE DATOS
	Type tProductosPS (ProductID As String, _
		ManagedProduct As Boolean, _
		Title As String, _ 
		Description As String, _ 
		Price As String, _ 
		Owned As Boolean, _ 'indica si el usuario ya compro este producto. Lo complta automaticamente con un query a play store
		ApagaPropaganda As Boolean, _
		Neuronas As Int, _ ' indica si el producto incluye el apagado de propaganda de Matete.
		ExlusivoPremium As Boolean, _ 'indica si el producto es exclusivo de jugadores premium (para poder tener neuronas con descuento
		NeuronasOriginales As Int, _   ' para poder mostrar que el premium incluye el doble de neuronas
		CompraPremiumOriginal As Boolean ) 'indica que es el producto premium (el unico que se muestra la primera vez)
	Type tProductosComprados  (ProductID As String, State As Int, Owned As Boolean)

	Type tColoresPaleta ( _ 
		ColorNombre As String, _  
		ColorTexto As Int, _  
		ColorClaro As Int, _ 
		ColorIntermedio As Int, _ 
		ColorOscuro As Int, _ 
		ColorDefault As Int) 'color fondo, blanco x ahora


	Type tColores ( _ 
		Paleta As Int, _ 
	 	bitmBajar As Bitmap, _  
	 	bitmSubir As Bitmap, _ 
		bitmPalabraOff As Bitmap, _ 
		bitmPalabraOn  As Bitmap, _ 
		bitmLetra  As Bitmap, _ 
		bitmMenu  As Bitmap, _ 
		bitmCompartir As Bitmap, _ 
		bitmMute As Bitmap, _ 
		bitmPedir As Bitmap, _ 
		bitmSaltarPalabra As Bitmap, _ 
		bitmFlag  As Bitmap, _ 
		bitmPalabraError  As Bitmap, _ 
		bitmMenuFondo As Bitmap, _ 
		bitmBajarLetras  As Bitmap, _ 
		bitmPalabraComprada As Bitmap, _ 
		bitmCostos  As Bitmap, _ 
		bitmShareConfirmar  As Bitmap, _ 
		bitmShareCancelar   As Bitmap, _ 
		bitmShareFB As Bitmap, _ 
		bitmShareTw As Bitmap, _ 
		ColorClaro As Int, _ 
		ColorMedio As Int, _ 
		ColorOscuro As Int, _ 
		ColorTexto As Int, _ 
		ColorNombre As String, _ 
		ColorDefault As Int)

	'FIN INAPP PRODUCTS
	'TIPOS
	Type tOrdenar (Indice As Int, Orden As Int, Letra As Char ) 
	Type tMensajeAparece (lblLetra As Label, Posicion As Int, Mostrada As Boolean, sTipoLetra As String, Renglon As Int, PosEnPalabra As Int) 
	Type tLetrasElegir (LetraSiempre As Char, LetraMovil As Char, LetraEnLabel As Char)	

	'para traer palabras para la historia
	Type tHistoria (idPalabra As Int, Adivinada As Int, Salteada As Int, Definicion As String)
	

	Type tLetrasPalabra (idpalabra As Int, posicion As Int, letra As Char, comprada As Boolean)
	Type tPalabra (idPalabra As Int, Palabra As String, Descripcion As String, Dificultad As String, TipoPalabra As String, idNivel As Int, Ayuda01 As String, Ayuda02 As String, Ayuda03 As String, Diccionario As String, PalabraDiccionario As String, bRejugada As Boolean, bSalteada As Boolean)
	
	Type tNivel (idNivel As Int, Nombre As String, ImagenFondo As String, CostoSaltar As Int, CostoLetra As Int, CostoAyuda As Int, _   
			MensajeInicio As String, MensajeFinal As String, ImagenInicio As String, ImagenFinal As String, IdPrimeraPalabra As Int, IdUltimaPalabra As Int, bMensajeInicioMostrado As Boolean)
	Type tPistas (pistaid As Int, pista As String, tipopista As String, comprada As Int, gratis As Int, valor As Int)


'constantes utilizadas para configurar la pantalla cuando hace click en algún botón
	Type tConfiguraPantalla (Compartir As Int, ComprarLetra As Int, SaltarPalabra As Int, Jugar As Int, Adivino As Int, Ayuda As Int, Creditos As Int, FinDeJuego As Int, Producto As Int, ComproNeuronas As Int, MuestraAviso As Int, Premium As Int, Historia As Int)

	Type tQuerysRemotos (Premium As Int, Avance As Int)

End Sub

Public Sub CargaDeviceValues

	Dim DeviceValues As LayoutValues: DeviceValues = GetDeviceLayoutValues
'	Dim DeviceDensity As Float: DeviceDensity = Density
	
	g_DeviceValuesHeight =  DeviceValues.Height
	g_DeviceValuesWidth =  DeviceValues.Width
	g_DeviceValuesScreenSize = DeviceValues.ApproximateScreenSize
	g_DeviceValuesScale = DeviceValues.Scale

End Sub



Public Sub ConvierteDipsStandard(iDipsStandard As Int, sHeightWidth As Char) As Int
Dim iRet As Int

If sHeightWidth = "H" Then ' si es alto (Height)
	'standard 480 scale 1
	iRet = g_DeviceValuesHeight * iDipsStandard / 480 
Else 
	If sHeightWidth = "W" Then
		'standard 320 scale 1
		iRet = g_DeviceValuesWidth* iDipsStandard / 320
			
	Else ' ambos
		iRet = iDipsStandard
	End If
End If
	
Return iRet 

End Sub

Public Sub RedimensionaView (xView As View, iWidth As Int, iHeight As Int)
	xView.Width = ConvierteDipsStandard(iWidth, "W")
	xView.Height=ConvierteDipsStandard(iHeight, "H")
End Sub


Public Sub CentrarControl (Activity As Activity, xControl As View, Horizontal As Boolean, Vertical As Boolean)
	
	If Horizontal Then
		xControl.Left = Activity.Width / 2 - xControl.Width /2
	End If
	If Vertical Then
		xControl.top = Activity.Height / 2 - xControl.Height /2
	End If	
	
End Sub

Public Sub CentrarControlEnPanel (xPanel As Panel, xControl As View, Horizontal As Boolean, Vertical As Boolean)
	
	If Horizontal Then
		xControl.Left = xPanel.Width / 2 - xControl.Width /2
	End If
	If Vertical Then
		xControl.top = xPanel.Height / 2 - xControl.Height /2
	End If	
	
	
	
End Sub

Public Sub CuadradoDipsStandard (iDips As Int, sHeightWidth As String)As Int
' devuelve los dips equivalentes para que se forme un cuadrado dependiendo de la configuracion del telefono
Dim dValorDipHeight As Double = g_DeviceValuesHeight/g_DeviceValuesWidth
Dim dValorDipWidth As Double = g_DeviceValuesWidth/g_DeviceValuesHeight
Dim iRet As Int


If sHeightWidth = "W" Then
	iRet =iDips*dValorDipHeight
Else
	iRet = iDips*dValorDipWidth
End If


Return iRet
End Sub
Public Sub VaciarList (xList As List)

	xList.clear
 
End Sub


Public Sub GetUsuario As String
Dim R As Reflector, sRet As String
   
Try   
   R.Target = R.RunStaticMethod("android.accounts.AccountManager", "get", _
   Array As Object(R.GetContext), Array As String("android.content.Context"))
   Dim accounts() As Object, sMail As String
   accounts = R.RunMethod2("getAccountsByType","com.google", "java.lang.String")
   'For i = 0 To accounts.Length - 1
   	If accounts.Length >0 Then
      R.Target = accounts(0)
	  Main.g_sEMail=R.GetField("name")
	  Main.g_sEMail = Main.g_sEMail.ToUpperCase
   	End If
   'Next
Catch
	ManejaError("GetUsuario",LastException.Message)
End Try

'Main.g_sEMail = "CAMILITA1894@GMAIL.COM"

Return Main.g_sEMail   
End Sub

Public Sub getIMEI  As String

Dim pi As PhoneId, sRet As String
Try

 Main.g_sIMEI = pi.GetDeviceId
 sRet = Main.g_sIMEI
Catch
	ManejaError("getIMEI",LastException.Message)
 End Try 
 
Return sRet
End Sub

Public Sub SQL_Ejecutar (sqlBase As SQL, sSql As String) As Boolean
	Dim bRet As Boolean = False
	
	Try
		sqlBase.ExecNonQuery (sSql)
		bRet = True
	Catch
		bRet = False
	End Try
Return bRet
End Sub

Public Sub UsuarioGrabaImei (Base As SQLCipher, sIMEI As String)
Dim sSql As String, sFecha As String
	Try		
		DateTime.DateFormat ="yyyy-MM-dd hh:mm:ss"
		sFecha = DateTime.Date(DateTime.now)
		sSql = "Insert into usuario (imei, fechaalta, monedas) values ('" & sIMEI & "','" & sFecha &"', 50)"
		Base.ExecNonQuery (sSql)
	
	Catch
		ManejaError("UsuarioGrabaImei", "Error grabando usuario")
	End Try

End Sub


Public Sub UsuarioExiste(Base As SQLCipher) As Boolean
Dim bRet As Boolean=False
Dim sSql As String, Cursor1 As Cursor
Try
		sSql = "select ifnull(imei,'') imei, fechaalta from usuario"
		Cursor1 = Base.ExecQuery(sSql)
	
		If Cursor1.RowCount>0 Then
			Cursor1.Position = 0
			

			bRet = True
		End If
Catch
	ManejaError("UsuarioExiste","Error Usuario")
End Try

Return bRet
End Sub

Public Sub get_FechaInstalacion(Base As SQLCipher) As String
Dim sFecha As String = ""
Dim sSql As String, Cursor1 As Cursor
Try
		sSql = "select fechaalta from usuario"
		Cursor1 = Base.ExecQuery(sSql)
	
		If Cursor1.RowCount>0 Then
			Cursor1.Position = 0
			sFecha =Cursor1.GetString2(0)
		End If
Catch
	ManejaError("UsuarioExiste","Error Usuario")
End Try

Return sFecha
End Sub



Public Sub Get_EsUltimaPalabra (BaseUsuario As SQL, BaseJuego As SQLCipher) As Boolean
	Dim Cursor1 As Cursor, bRet As Boolean = False
	Dim sSql As String, iPalabraAvance As Int
Try
	'busca si quedan palabras

'trae la ultima palabra ganada por el usuario
	sSql = "select ifnull(max(idpalabra),0) a from avance where Fin is not null"
	Cursor1 = BaseUsuario.ExecQuery(sSql)
	If Cursor1.RowCount >0 Then
		Cursor1.Position=0
		iPalabraAvance=Cursor1.GetInt2(0)
	End If
	Cursor1.Close

'busca si hay alguna palabra con id mayor. si no devuelve registros es la ultima
	sSql ="Select idpalabra from palabras where idpalabra>" & iPalabraAvance
	Cursor1 = BaseJuego.ExecQuery(sSql)		
	If Cursor1.RowCount >0 Then
		bRet = False
	Else
		bRet = True
	End If	
    Cursor1.Close
	
Catch 
	ManejaError("get_esultimapalabra","Error Puntos")
End Try
Return bRet

End Sub

Public Sub ReiniciaJuego 
	Dim sSql As String', sFecha As String, bRet As Boolean=False
	
	Try
		g_sqlBaseLocalUsuario.BeginTransaction
		'actualiza 
		'acutliazar fecha de compra
		sSql = "Delete from avance"
		g_sqlBaseLocalUsuario.ExecNonQuery (sSql)

		sSql = "Update usuario set monedas = 150, Puntos=0"
		g_sqlBaseLocalUsuario.ExecNonQuery(sSql)


	
		sSql = "delete from letrascompradas "
		g_sqlBaseLocalUsuario.ExecNonQuery(sSql)
			
		sSql = "delete from letrasporpalabra"
		g_sqlBaseLocalUsuario.ExecNonQuery(sSql)
			
				
		g_sqlBaseLocalUsuario.TransactionSuccessful
		g_sqlBaseLocalUsuario.EndTransaction
		
		
		

	Catch
	
		g_sqlBaseLocalUsuario.EndTransaction
		ManejaError("ReiniciaJuego","Error Actualizar Pistas")
	End Try

End Sub

Public Sub AvanzaJuego (iHasta As Int)
	Dim sSql As String', sFecha As String, bRet As Boolean=False
	Dim sFecha As String
	Try
		g_sqlBaseLocalUsuario.BeginTransaction
		'actualiza 
		'acutliazar fecha de compra
		For j = 1 To iHasta
			sFecha = DateTime.Date(DateTime.now)
			sSql = "Insert Into Avance (idPalabra, Inicio, adivinada, salteada, fin) Values ("
			sSql = sSql & j & ",'" & sFecha & "',1,0,'" & sFecha & "')" 

			g_sqlBaseLocalUsuario.ExecNonQuery (sSql)
		Next
		sSql = "Update Usuario set Monedas = 100, puntos = " & iHasta & ", idnivel = 0"
		g_sqlBaseLocalUsuario.ExecNonQuery (sSql)		
				
		g_sqlBaseLocalUsuario.TransactionSuccessful
		g_sqlBaseLocalUsuario.EndTransaction


	Catch
	
		g_sqlBaseLocalUsuario.EndTransaction
		ManejaError("ReiniciaJuego","Error Actualizar Pistas")
	End Try

End Sub

'programacion oculta con clave de clicks
Public Sub Magia(iCant As Int)
Dim sMail As String
sMail = GetUsuario

If sMail.ToUpperCase  ="JUAN.DAROCHA@GMAIL.COM" OR sMail.ToUpperCase  ="MARTIN2603@GMAIL.COM" Then
		If Msgbox2( "Reiniciar","", "Si","No","", Null)= DialogResponse.POSITIVE Then
			ReiniciaJuego 
			If Msgbox2( "Avanzar","", "Si","No","", Null)= DialogResponse.POSITIVE Then
				AvanzaJuego(413)
			End If
		End If
End If 
End Sub


Public Sub AbreBase (Base As SQL, sDirGrabable As String, sBaseNombre As String)
ProgressDialogShow("Initializing database...")
Try
	If Base.IsInitialized = False Then
		Base.Initialize(sDirGrabable,sBaseNombre,True)
	End If

Catch
	ManejaError("AbreBase","error base")

End Try

ProgressDialogHide
End Sub

Public Sub AbreBaseCipher (Base As SQLCipher, sDirGrabable As String, sBaseNombre As String, sPsw As String)
ProgressDialogShow("Initializing database...")
Try
	If Base.IsInitialized = False Then
		Base.Initialize(sDirGrabable,sBaseNombre,True, sPsw, "")
	End If

Catch
	ManejaError("AbreBase","error base")
End Try

ProgressDialogHide
End Sub

Sub Get_DirectorioBase As String
	Dim sRet As String=""
Try
	' inicia variable directorio grabable   
    If File.ExternalWritable Then sRet  = File.DirDefaultExternal Else sRet = File.DirInternal


Catch
		ManejaError("Get_DirectorioBase","Error Get_DirectorioBase")
End Try

Return sRet

End Sub

Public Sub Archivo_CopiaDesdeAssets (sFileName As String, bForzar As Boolean, sDirectorio As String) As Boolean
'copia un archivo desde assets si no existe o si se le pide que lo haga obligatorio (bforzar=true)
'devuelve true si se copio el archivo
  	Dim bRet As Boolean=False
	
	If sDirectorio = "" Then
		sDirectorio= Get_DirectorioBase
		'Msgbox("Directorio Base Vacio, recupera","")
	End If
	
 	If File.Exists(sDirectorio, sFileName) = False OR bForzar Then
	 	File.Copy(File.DirAssets, sFileName,sDirectorio, sFileName)
   		bRet= True
	End If
	Return bRet
End Sub


Public Sub Activity_DimensionPantalla (xAct As Activity)
	xAct.Width = g_DeviceValuesWidth
	xAct.Height = g_DeviceValuesHeight
End Sub

Public Sub CentrarArrayControlesEnPanel(pnlPanel As Panel, imgArray() As View, vertical As Boolean)
	Dim iAnchoControles As Int = 0, iSeparacion As Int = 0, iLeft As Int

	'suma todos los anchos de los controles para ver el espacio que ocupan en total
	For j = 0 To imgArray.Length-1
		iAnchoControles = iAnchoControles+imgArray(j).width
	Next
	
	' calcula las separaciones (cantidad de controles +1) 
	iSeparacion = (pnlPanel.Width-iAnchoControles )/ (imgArray.Length+1)
	
	'ubica los controles
	iLeft = iSeparacion
	For j = 0 To imgArray.Length-1
		imgArray(j).Left = iLeft 
		iLeft = iLeft + imgArray(j).Width + iSeparacion
	Next
	
	'vertical	
	
	If vertical Then
		For j=0 To imgArray.Length-1
			imgArray(j).top = pnlPanel.Height / 2 - imgArray(j).Height /2
		Next 
	End If	

	
End Sub

Sub CargaImagenLetra (sLetra As String, lblLetra As Label)
	Dim sArchivo As String
	
	If sLetra.ToUpperCase.CharAt(0) = "Ñ" Then
		If sLetra.Length >1 Then
			sArchivo = "Ni" & sLetra.SubString2(1, sLetra.Length) & ".png"
		Else	
			sArchivo = "Ni.png"
		End If
	Else
		sArchivo = sLetra & ".png"
	End If
	'Archivo_CopiaDesdeAssets(sArchivo)
	lblLetra.SetBackgroundImage (LoadBitmap(File.DirAssets, sArchivo))
	
End Sub

Sub ManejaError(sRutina As String, sError As String) As Boolean
'devuelve true si el que llama tiene que abortar el programa
Dim bRet As Boolean
	If sError ="" Then 
		sError = LastException.Message
	End If
	Log(LastException.Message)
	Msgbox("",sError)

bRet = True
Return bRet
End Sub
Sub AcomodarFondoInicio(imgFondo As ImageView, iWidthActivity As Int, iHeightActivity As Int, iAnchoImagen As Int, iAltoImagen As Int)
'acomoda el fondo de inicio para que no se deforme y siempre se vea la cabeza con los engranajes.
Dim dRelacionImagen As Double, 	dRelacionPantalla As Double

	dRelacionImagen = iAltoImagen/iAnchoImagen
	dRelacionPantalla =  iHeightActivity/iWidthActivity 
	'si la relacion de la pantalla es menor que la relacion de la imagen (la pantalla es mas ancha en relacion al alto)
	If dRelacionPantalla<dRelacionImagen Then
		imgFondo.Width = iWidthActivity
		imgFondo.Height = imgFondo.width * dRelacionImagen
		imgFondo.Top = (iHeightActivity-imgFondo.Height )
		imgFondo.Left = 0
		
	Else	'la pantalla es mas alta que la imagen en relacion
		imgFondo.Height = iHeightActivity
		imgFondo.Width = imgFondo.Height/dRelacionImagen
		imgFondo.Top = 0
		imgFondo.Left = (iWidthActivity - imgFondo.Width )/2
	End If	
End Sub


Sub MensajeAparece_ApagarTimer(aLabelsMensaje() As tMensajeAparece)  As Boolean
	Dim bRet As Boolean = True
	For I = 0 To aLabelsMensaje.Length-1
		If aLabelsMensaje(I).Mostrada = False Then
			bRet = False
			Exit
		End If                     
	Next 

	Return bRet

End Sub
 
'configura el Label (top, left, Height, Width) de las letras de mensaje 
Sub MensajeAparece_ConfiguraLabel (pnlPanel As Panel, lblSetear As Label, iTopDesde As Int, iPartes As Int, aHeight As Int, Renglon As Int, PosEnPalabra As Int, bUnaLinea As Boolean)
Dim iTam As Int, iEspacio As Int, iTop As Int, iLeft As Int

'si es solo una linea, utiliza todo el ancho
If bUnaLinea Then
	iTam = pnlPanel.Width/iPartes

Else
	iTam = pnlPanel.width/iPartes
	
	iEspacio = iTam*0.1
End If
If iTopDesde =-1 Then
	iTop = pnlPanel.Height/2 - iTam/2 - pnlPanel.height* (0.1)
	
Else
	If Renglon = 2123211 Then
		iTop = iEspacio
	Else 
		iTop = (iTam*  (Renglon-1)) + ((iEspacio*3)* (Renglon-1)) + iTopDesde
	End If

End If

iLeft  = (PosEnPalabra+1) * (iTam + iEspacio)

pnlPanel.AddView( lblSetear, iLeft, iTop, iTam, iTam)

End Sub



'Sub MensajeAparece_CargaLabels (aLabelsMensaje() As tMensajeAparece, sFrase As String, sTipoLetra As String)



Sub MensajeAparece_EligeLetra (aLabelsMensaje() As tMensajeAparece) As Int

Dim iAzar As Int = -1
For j=0 To aLabelsMensaje.Length -1 
	' elige al azar una letra hasta que encuentra una no mostrada
	iAzar = Rnd(0, aLabelsMensaje.length)
	If aLabelsMensaje (iAzar).Mostrada = False Then
		Exit
	End  If
Next
Return iAzar
End Sub

Sub MensajeAparece_CargaLabels_N (pnlPanel As Panel, aLabelsMensaje() As tMensajeAparece, sFrase As String, sTipoLetra As String, iHeight As Int, bUnaLinea As Boolean, iPartes As Int, tFont As Typeface, p_DeviceValuesScreenSize As Float)

	Dim iRenglon As Int
	Dim iPosEnPalabra As Int =0
	
	iRenglon = 1
	For j=0 To aLabelsMensaje.Length-1
		aLabelsMensaje (j).Mostrada=True
	Next 
	For j = 0 To aLabelsMensaje.Length-1
	   	'si es un espacio, salta un renglón
	     If sFrase.SubString2(j,j+1) = " " OR sFrase.SubString2(j,j+1) = "_" Then
			
			aLabelsMensaje (j).Mostrada = True 'marca el espacio como mostrado
			If sFrase.SubString2(j,j+1) = " " Then
				iRenglon = iRenglon +1
				iPosEnPalabra=1
			Else
				iPosEnPalabra = iPosEnPalabra+1
			End If
		Else
			Dim la2 As Label 
			la2.Initialize("lmensaje")
			la2.Tag = sFrase.SubString2(j,j+1)
			'cargar la letra
			'CargaImagenLetra("Sinletra", la2)
			la2.Text = sFrase.SubString2(j,j+1) 
			
			la2.TextSize = Font_RecalculaTam(24)
			la2.TextColor = Colors.White
			la2.Typeface = tFont
			
			MensajeAparece_ConfiguraLabel(pnlPanel, la2, 10, 11, iHeight, iRenglon, iPosEnPalabra, bUnaLinea)
			aLabelsMensaje(j).lblLetra = la2

			aLabelsMensaje (j).Posicion =j
       	 	aLabelsMensaje (j).Mostrada = False
			aLabelsMensaje (j).sTipoLetra = "a"
	   		aLabelsMensaje (j).lblLetra.Visible = False
			aLabelsMensaje (j).posEnPalabra = iPosEnPalabra
			aLabelsMensaje(j).sTipoLetra = sTipoLetra
			iPosEnPalabra = iPosEnPalabra +1
			'MensajeAparece_ConfiguraLabel (aLabelsMensaje(), j, 10, 16) 'configura el tamaño y la posición de cada Label
	
			
		End If
	Next 

End Sub

Sub MensajeAparece_CargaLabels (pnlPanel As Panel, aLabelsMensaje() As tMensajeAparece, sFrase As String, sTipoLetra As String, iHeight As Int, bUnaLinea As Boolean, iPartes As Int)

	Dim iRenglon As Int
	Dim iPosEnPalabra As Int =0
	
	iRenglon = 1
	For j=0 To aLabelsMensaje.Length-1
		aLabelsMensaje (j).Mostrada=True
	Next 
	For j = 0 To aLabelsMensaje.Length-1
	   	'si es un , salta un renglón
	     If sFrase.SubString2(j,j+1) = " " OR sFrase.SubString2(j,j+1) = "_" Then
			
			aLabelsMensaje (j).Mostrada = True 'marca el espacio como mostrado
			If sFrase.SubString2(j,j+1) = " " Then
				iRenglon = iRenglon +1
				iPosEnPalabra=1
			Else
				iPosEnPalabra = iPosEnPalabra+1
			End If
		Else
			Dim la2 As Label 
			la2.Initialize("lmensaje")
			la2.Tag = sFrase.SubString2(j,j+1)
			'cargar la letra
			CargaImagenLetra(sFrase.SubString2(j,j+1) &sTipoLetra, la2)
		
			MensajeAparece_ConfiguraLabel(pnlPanel, la2, 10, iPartes, iHeight, iRenglon, iPosEnPalabra, bUnaLinea)
			aLabelsMensaje(j).lblLetra = la2

			aLabelsMensaje (j).Posicion =j
       	 	aLabelsMensaje (j).Mostrada = False
			aLabelsMensaje (j).sTipoLetra = "a"
	   		aLabelsMensaje (j).lblLetra.Visible = False
			aLabelsMensaje (j).posEnPalabra = iPosEnPalabra
			aLabelsMensaje(j).sTipoLetra = sTipoLetra
			iPosEnPalabra = iPosEnPalabra +1
			'MensajeAparece_ConfiguraLabel (aLabelsMensaje(), j, 10, 16) 'configura el tamaño y la posición de cada Label
	
			
		End If
	Next 

End Sub

Sub ViewComoActivity (xActivity As Activity, xView As View)
	xView.width = xActivity.Width
	xView.Height = xActivity.Height
	xView.Top = 0
	xView.Left = 0
End Sub



Sub get_CantidadPalabras(Base As SQLCipher) As Int
Dim sSql As String, Cursor1 As Cursor, iRet As Int
		sSql = "select count(1) x from palabras"
		Cursor1 = Base.ExecQuery(sSql)
		If Cursor1.RowCount>0 Then
			Cursor1.Position = 0
			iRet = Cursor1.GetInt2(0)
		End If
		Cursor1.Close
Return iRet	
End Sub
'devuelve la cantidad de palabras del almacen de palabras (con o sin las premium, de acuerdo a si la compro el usuario)
Sub get_CantidadPalabrasAlmacen(Base As SQLCipher, bPremium As Boolean) As Int
Dim sSql As String, Cursor1 As Cursor, iRet As Int
		sSql = "select count(1) x from palabras_almacen"
		' si no es premium, cuenta solo las palabras que no son premium
		If bPremium = False Then
			sSql = sSql & " where Premium = 'N'"
		End If
		
		Cursor1 = Base.ExecQuery(sSql)
		If Cursor1.RowCount>0 Then
			Cursor1.Position = 0
			iRet = Cursor1.GetInt2(0)
		End If
		Cursor1.Close
Return iRet	
End Sub


Sub get_CantidadPalabrasAdivinadas(Base As SQL) As Int
Dim sSql As String, Cursor1 As Cursor, iRet As Int
		sSql = "select count(1) x from avance where adivinada = 1"
		Cursor1 = Base.ExecQuery(sSql)
		If Cursor1.RowCount>0 Then
			Cursor1.Position = 0
			iRet = Cursor1.GetInt2(0)
		End If
		Cursor1.Close
Return iRet	
End Sub

Sub get_CantidadPalabrasSalteadas(Base As SQL) As Int
Dim sSql As String, Cursor1 As Cursor, iRet As Int
		sSql = "select count(1) x from avance where Salteada = 1"
		Cursor1 = Base.ExecQuery(sSql)
		If Cursor1.RowCount>0 Then
			Cursor1.Position = 0
			iRet = Cursor1.GetInt2(0)
		End If
		Cursor1.Close
Return iRet	
End Sub
Sub get_CantidadPalabrasJugadas(Base As SQL) As Int
Dim sSql As String, Cursor1 As Cursor, iRet As Int
		sSql = "select count(1) x from avance where Salteada = 1 or Adivinada = 1"
		Cursor1 = Base.ExecQuery(sSql)
		If Cursor1.RowCount>0 Then
			Cursor1.Position = 0
			iRet = Cursor1.GetInt2(0)
		End If
		Cursor1.Close
Return iRet	
End Sub
Sub Font_RecalculaTam(iFontSize As Int) As Int
'recalcula el tamanio del font en base al tamano de la pantalla.
' el que recibe es en base a un tamano de pantalla de 4.7
'se baja a 4.2 porque en algunos aparatos chiquitos funciona mal, quedan grandes las letras.
Dim iRet As Int, DevScreehSize As Float
	Dim DeviceValues As LayoutValues: DeviceValues = GetDeviceLayoutValues
	DevScreehSize = DeviceValues.ApproximateScreenSize

iRet = iFontSize/4.2* DevScreehSize
Return iRet
End Sub

Sub Get_Tester As Boolean
	Dim bRet As Boolean, sUsu As String
	sUsu = GetUsuario
	sUsu = sUsu.ToUpperCase

	'bRet = (sUsu = "JUAN.DAROCHA@GMAIL.COM")
	bRet = sUsu ="ALEJONAVEIRAC@GMAIL.COM"
	bRet = bRet Or sUsu ="CECIPIATTINI@GMAIL.COM"
	bRet = bRet Or sUsu ="CAROVAG@GMAIL.COM"
	bRet = bRet Or sUsu ="ERICAYEBRA@GMAIL.COM"
	bRet = bRet Or sUsu ="HDVENTURINI@GMAIL.COM"
	bRet = bRet Or sUsu ="JUANMAROTTE@GMAIL.COM"
	bRet = bRet Or sUsu ="MARCELO.MARINUCCI@GMAIL.COM"
	bRet = bRet Or sUsu ="MARTIN2603@GMAIL.COM"
	bRet = bRet Or sUsu ="TELLECHEA.VICTORIA@GMAIL.COM"
	bRet = bRet Or sUsu = "ANGIEVMC0426@GMAIL.COM" 'LA QUE LE DETECTABA LEADBOLT COMO VIRUS (VER MAIL EN MATETEJUEGO 29/3/15)
	bRet = bRet Or sUsu = "LOREHV82@GMAIL.COM" 'adivinó un matete en facebook en abril 2015
	bRet = bRet Or sUsu = "G.MATHOV@GMAIL.COM" 'se le borraba la aplicacion x app2sd abril 2015
	bRet = bRet Or sUsu = "PPRADA2010@GMAIL.COM" 'se le borraba la aplicacion x app2sd abril 2015
	bRet = bRet Or sUsu = "CINTIALENCINA88@GMAIL.COM" 'ganó matete facebook abril 2015
	bRet = bRet Or sUsu = "GRACHIEL63@GMAIL.COM" 'ganó matete facebook abril 2015
	bRet = bRet Or sUsu = "HOUSE.BREITEN@GMAIL.COM" 'mando un matete
	bRet = bRet Or sUsu = "JOGHIBAUDO@GMAIL.COM" 'mando un matete
	bRet = bRet Or sUsu = "TUCUMETAL@GMAIL.COM" 'seguidor 100 en twitter
	
	
	
Return bRet
End Sub

Sub Get_SoyYo As Boolean
	Dim bRet As Boolean, sUsu As String
	sUsu = GetUsuario
	sUsu = sUsu.ToUpperCase
	bRet = (sUsu = "JUAN.DAROCHA@GMAIL.COM")
Return bRet
End Sub


Sub Get_SeteoUsuarioExiste(BaseUsuario As SQLCipher , sSeteo As String) As Boolean
	Dim bRet As Boolean, sSql As String, crCursor As Cursor
Try
	sSql = "Select count(1) c From SeteosUsuario where tiposeteo='" & sSeteo & "'"
	
	crCursor = BaseUsuario.ExecQuery(sSql)
	bRet = crCursor.RowCount >0 
	crCursor.close
	
Catch
	ManejaError("Barbacoas", "apagadas")
End Try

Return bRet
End Sub

Sub SeteoUsuarioEnteroDesde_Inserta(BaseUsuario As  SQLCipher, sSeteo As String, iEntero As Int)
'inserta un seteo 	
	Dim ssql As String = "Insert Into SeteosUsuario (TipoSeteo, enterodesde) values ('" & sSeteo & "'," & iEntero & ")"
Try
	BaseUsuario.ExecNonQuery(ssql)
Catch
	ManejaError("Pero qué está pasando?", "Chanfle") 
End Try

End Sub

Sub SeteoUsuarioTexto_Inserta(BaseUsuario As  SQLCipher, sSeteo As String, sTexto As String)
'inserta un seteo 	
	Dim ssql As String = "Insert Into SeteosUsuario (TipoSeteo, texto) values ('" & sSeteo & "','" & sTexto & "')"
Try
	BaseUsuario.ExecNonQuery(ssql)
Catch
	ManejaError("Pero qué está pasando?", "Chanfle") 
End Try

End Sub



Sub SeteoUsuarioEnteroDesde_Update(BaseUsuario As SQLCipher, sSeteo As String, iValor As Int)
	Dim sSql As String

	Try
		sSql = "Update SeteosUsuario Set enterodesde = " & iValor & " where tiposeteo = '" & sSeteo & "'"
		BaseUsuario.ExecNonQuery(sSql)
	Catch
		ManejaError("Caranchos", "Y ahora qué pasó?")
	End Try
	

End Sub

Sub Get_SeteoUsuarioEnteroDesde(BaseUsuario As SQLCipher, sSeteo As String) As Int
	
	Dim iRet As Int=0, crCursor As Cursor, sSql As String
Try
	sSql = "Select ifnull(enterodesde,0) c from SeteosUsuario where tiposeteo = '" & sSeteo & "'"	
	crCursor = BaseUsuario.ExecQuery(sSql)
	If crCursor.RowCount >0 Then
		crCursor.Position = 0
		iRet  = crCursor.GetInt("c")
	Else
		SeteoUsuarioEnteroDesde_Inserta(BaseUsuario, sSeteo, 0)
	End If
	crCursor.close
Catch
	ManejaError("Caranchos", "Y ahora qué pasó?")
End Try
	
	
Return iRet
End Sub

Sub Replicar (sTexto As String, iCant As Int) As String
Dim sRet As String=""
For j = 0 To iCant
	sRet = sRet & sTexto
Next 
Return sRet
End Sub


Sub AbreBases
	Dim sDirBase As String = Get_DirectorioBase

	AbreBaseCipher(g_sqlBaseLocalJuego, sDirBase, g_NombreBaseLocalJuego, g_sPswBaseJ)
	AbreBaseCipher(g_sqlBaseLocalUsuario, sDirBase, g_NombreBaseLocalUsuario, g_sPswBaseU)

End Sub

Sub AbreBasesParam (g_ObjetoBaseJuego As SQLCipher, g_NombreBaseJuego As String, sPswJuego As String, _ 
					g_objetoBaseUsuario As SQLCipher, g_NombreBaseUsuario As String, sPswUsuario As String)
	Dim sDirBase As String = Get_DirectorioBase

	AbreBaseCipher(g_ObjetoBaseJuego , sDirBase, g_NombreBaseJuego, sPswJuego)
	AbreBaseCipher(g_objetoBaseUsuario , sDirBase, g_NombreBaseUsuario, sPswUsuario)

End Sub



Sub Facebook_GetUso (xBaseSql As SQLCipher) As Int
	Dim Cursor1 As Cursor, ssql As String , iRet As Int = 0
	Try
		'busca puntos y monedas
		ssql ="Select ifnull(publicofacebook,0) from avance where idPalabra = (select max(idpalabra) from avance)"
		Cursor1 = xBaseSql.ExecQuery(ssql)
		If Cursor1.RowCount >0 Then
			Cursor1.Position = 0
			iRet = Cursor1.GetInt2(0)
		End If	
	    Cursor1.Close
	Catch 
		ManejaError("Facebook_GetUso","")
	End Try
Return iRet
End Sub


Sub Usuario_SumarNeuronas (iNeuronasSumar As Int, BaseUsuario As SQLCipher)  As Boolean
Dim sSql As String, bRet As Boolean
Try
		'' si adivino le suma puntos, sino descuenta el salto
		sSql ="Update Usuario Set Monedas = Monedas + " & iNeuronasSumar
		BaseUsuario.ExecNonQuery(sSql)
		bRet = True
Catch
	bRet = False
End Try
Return bRet
End Sub

Sub Get_LetrasparaProvider (sLetras As String) As String
Dim sRet As String = " ", sUna As String
For j = 0 To sLetras.Length-1
	sUna = sLetras.SubString2(j,j+1) 
	If sUna= "_"  Then
		sRet = sRet & "__ "
	Else
		sRet  = sRet & sUna & " " 
	End If	
Next
Return sRet
End Sub


Sub Provider_letras(sLetras As String, iRenglon As Int) As String
Dim sRet As String = " ", iInicio As Int, sUna As String
iInicio = 0+((iRenglon-1)*6)
For j = iInicio To iInicio +5
	sUna = sLetras.SubString2(j,j+1)
	If sUna = "?" OR sUna = " " Then
		sRet = sRet & "  " & "_" 
	Else	
		sRet = sRet & "  " & sUna
	End If
Next 
Return sRet
End Sub


Sub ViewIgual (xOrigen As View, xDestino As View)
xDestino.Top = xOrigen.Top
xDestino.Left = xOrigen.Left
xDestino.Width = xOrigen.Width
xDestino.Height = xOrigen.Height

End Sub




Sub Twitter_GetUso (xBaseSql As SQLCipher) As Int
	Dim Cursor1 As Cursor, ssql As String , iRet As Int = 0
	Try
		'busca si ya publicó en twitter (se guarda en el campo Ind1
		ssql ="Select ifnull(Ind1,0) from avance where idPalabra = (select max(idpalabra) from avance)"
		Cursor1 = xBaseSql.ExecQuery(ssql)
		If Cursor1.RowCount >0 Then
			Cursor1.Position = 0
			iRet = Cursor1.GetInt2(0)
		End If	
	    Cursor1.Close
	Catch 
		ManejaError("Twitter_GetUso","")
	End Try
Return iRet
End Sub


'iPorSobreMaximo permite achicar la letra del maximo calculado por la función
Sub SetLabelTextSize(lbl As Label, txt As String, MaxFontSize As Float, MinFontSize As Float, iPorcSobreMaximo As Int)
    Dim FontSize = MaxFontSize As Float
    Dim Height As Int
    Dim stu As StringUtils
    
    lbl.TextSize = FontSize
    Height = stu.MeasureMultilineTextHeight(lbl, txt)
    Do While Height > lbl.Height AND FontSize > MinFontSize 
        FontSize = FontSize - 1
        lbl.TextSize = FontSize
        Height = stu.MeasureMultilineTextHeight(lbl, txt)
    Loop
	If iPorcSobreMaximo >0 Then
		lbl.TextSize = lbl.TextSize * iPorcSobreMaximo / 100
	End If
End Sub

Sub Wait(MilliSekunden As Int)
   Dim Ti As Long
   Ti = DateTime.Now + MilliSekunden
   Do While DateTime.Now < Ti
      DoEvents
   Loop
End Sub

Sub Screenshot(xActivity As Activity , sDirectorio, sArchivo As String)
	' Take a screenshot.
	Dim Obj1, Obj2 As Reflector
	Dim bmp As Bitmap
	Dim c As Canvas
	Dim now, i As Long
	Dim dt As String
	DateTime.DateFormat = "yyMMddHHmmss"
	now = DateTime.now
	dt = DateTime.Date(now) ' e.g.: "110812150355" is Aug.12, 2011, 3:03:55 p.m.
	Obj1.Target = Obj1.GetActivityBA
	Obj1.Target = Obj1.GetField("vg")
	bmp.InitializeMutable(xActivity.Width, xActivity.Height)
	c.Initialize2(bmp)
	Dim args(1) As Object
	Dim types(1) As String
	Obj2.Target = c
	Obj2.Target = Obj2.GetField("canvas")
	args(0) = Obj2.Target
	types(0) = "android.graphics.Canvas"
	Obj1.RunMethod4("draw", args, types)
	Dim Out As OutputStream
	Out = File.OpenOutput(sDirectorio, sArchivo , False)
	bmp.WriteToStream(Out, 100, "PNG")
	Out.Close
End Sub


'calcula el left para que el control Hijo este centrado con Maestro (calculo del left que corresponde)
Sub ViewAlinearCentro(Maestro As View, Hijo As View) As Int
Dim iCentro As Int, iLeft As Int
iCentro = Maestro.left + Maestro.width / 2
iLeft = iCentro - (Hijo.Width / 2)
Return iLeft
End Sub



Public Sub LoadDrawable(Name As String) As Object
	'Gets a drawable from the Android system resources
	Dim R As Reflector
	R.Target = R.GetContext
	R.Target = R.RunMethod("getResources")
	R.Target = R.RunMethod("getSystem")
	Dim ID_Drawable As Int
	ID_Drawable = R.RunMethod4("getIdentifier", Array As Object(Name, "drawable", "android"), _
	                                            Array As String("java.lang.String", "java.lang.String", "java.lang.String"))
	R.Target = R.GetContext
	R.Target = R.RunMethod("getResources")
	Return R.RunMethod2("getDrawable", ID_Drawable, "java.lang.int")
End Sub

Sub Get_TextoAyuda As String
Dim sRet As String
sRet = "Bienvenido a"& Chr(10) & _ 
		"Matete Divergente" & Chr(10) & _ 
		"un juego de palabras" & Chr(10) & _ 
		"para el lado colorido de tu cerebro" & Chr(10) & _ 	
		"" & Chr(10) & _ 
		"Matete te propone una definición y tú debes adivinar de que palabra se trata." & Chr(10) & _ 
		Chr(10) & _ 
		"Aquí es donde te preguntas:" & Chr(10) & " ¿Eso es todo?" & Chr(10) &  "¡NO!" & Chr(10) & _ 
		Chr(10) & _
		"En Matete Divergente las palabras se definen de una forma especial, y es por eso que para ganar necesitarás utilizar tu pensamiento lateral." & Chr(10) & _ 
		Chr(10) & _ 
		"Lo primero que debes hacer es olvidar la lógica convencional y mudarte al otro lado de tu cerebro." & Chr(10) & _ 
		"Veamos un ejemplo." & Chr(10) & _ 
		"Definición: "& Chr(10) & _ 
		"“Coloca sobre sí mismo una montura de caballo”" & Chr(10) & _ 
		Chr(10)& "Tómate unos segundos para pensar la definición de Matete." & Chr(10) & _ 
		"Colocar una montura…..Ensillar." & Chr(10) & _ 
		"Sobre sí mismo..." & Chr(10) & _ 
		Chr(10) & _ 
		"En el camino hasta la respuesta es posible que pases por distintos pensamientos:" & Chr(10) & _ 
		"Ensillarse, " & Chr(10) & _ 
		" meensillo, " & Chr(10) & _ 
		"   ensillar mismo,"& Chr(10) & _ 
		"     mismo ensilla…" & Chr(10) & _ 
		"Alguno de estos pensamientos te llevará a la respuesta, que en este caso es " & Chr(10) &  "SENCILLA" & Chr(10) & _ 
		"Formada por “SE” + “ENSILLA”" & Chr(10) & _ 
		"Cómo puedes ver en este ejemplo, la respuesta respeta las reglas ortográficas, sin embargo las palabras que la conforman pueden no contener exactamente las mismas letras, aunque su Sonido será similar, como sucede en este caso. " & Chr(10) & _ 
		Chr(10) & _ 
		"Veamos otro ejemplo." & Chr(10) & _ 
		"Definición: "& Chr(10) & _ 
		"“Celebración del cacahuate”" & Chr(10) & _ 
		"  Celebración del maní," & Chr(10) & _ 
		"    Conmemoración del maní,"& Chr(10) & _ 
		"      Fiesta del maní..." & Chr(10) & _ 
		"¡MANIFIESTA!" & Chr(10) & _ 
		Chr(10) & _ 
		"Ya te habrás dado cuenta que para descubrir las palabras debes dejar fluir tus pensamientos y las asociaciones te irán guiando hacia la respuesta" & Chr(10) & _ 
		"Algunas veces puede ser la combinación del sonido de dos palabras y en otras seguir una lógica menos convencional, como en este caso:" & Chr(10) & _ 
		"Definición:" & Chr(10) & _ 
		"“Baile de la vaca para cambiar de casa”" & Chr(10) & _ 
		"Respuesta" & Chr(10) & _ 
		"¡MUDANZA!" & Chr(10) & _
		Chr(10) & _ 
		"Luego de esta breve introducción, ya te encuentras listo para jugar Matete Divergente." & Chr(10) & _ 
		"Cada vez que descubras una palabra ganarás neuronas, que podrás utilizar para comprar letras o saltar matetes." & Chr(10) & _ 
		"Puedes hacernos llegar tus comentarios y sugerencias a MateteJuego@gmail.com" & Chr(10) & _ 
		"¡Que te diviertas!" 
Return sRet
End Sub

Sub Get_TextoCreditos As String
Dim sRet As String

sRet =	"AGRADECIMIENTOS ESPECIALES" & Chr(10) & _
		"Juli, Ale, Vero"& Chr(10) & _
		"Nico y Eri"& Chr(10) & _
		"Adrian S"& Chr(10) & _
		""& Chr(10) & _
		"TESTERS 7X24"& Chr(10) & _
		"Caro A, Fer B "& Chr(10) & _
		"Martin R, Ceci P"& Chr(10) & _
		"Marcelo M" & Chr(10) & _
		"Marcelo M"& Chr(10) & _
		"Vicky T"& Chr(10) & _
		""& Chr(10) & _
		"TESTERS"& Chr(10) & _
		"Alejo N"& Chr(10) & _
		"Juan M"& Chr(10) & _
		"Hugo V"& Chr(10) & _
		"Dario C"& Chr(10) & _
		"Arvid"& Chr(10) & _
		""& Chr(10) & _
		"DESARROLLO"& Chr(10) & _
		"Juan Pablo da Rocha"& Chr(10) & _
		"DISEÑO"& Chr(10) & _
		"Ver 1 Gabriel Mugni"& Chr(10) & _
		"Ver2 Jose Augusto da Rocha"& Chr(10) & _
		"SOPORTE DESARROLLO"& Chr(10) & _
		"B4A Community"& Chr(10) & _
		""
Return sRet
End Sub

Sub Set_PantallaCargaSet(xActivity As Activity, imgIconoFondo As ImageView, lblMensaje As Label, iColor As Int, bIniciaPremium As Boolean)

	imgIconoFondo.Width = xActivity.height * 0.40
	imgIconoFondo.Height = imgIconoFondo.Width
	imgIconoFondo.Top = xActivity.Height * 0.20
	CentrarControl(xActivity, imgIconoFondo, True, False)
	xActivity.Color = Colors.white
	
	imgIconoFondo.bringtofront


	lblMensaje.Color = Colors.White
	lblMensaje.height = xActivity.Height * 0.1
	lblMensaje.Width = xActivity.Width
	lblMensaje.Top = xActivity.Height*0.1
	lblMensaje.TextColor = iColor
	If bIniciaPremium Then
		lblMensaje.Text = "PREMIUMIFICANDO" & Chr(10) & "MATETE..."
	Else
		lblMensaje.Text = "INVENTANDO" & Chr(10) & "MATETES..."
	End If
	
	
	'SetLabelTextSize(lblMensaje, lblMensaje.Text, 30,8,80)
	lblMensaje.Gravity = Gravity.CENTER
	CentrarControl(xActivity, lblMensaje, True, False)
	SetLabelTextSize (lblMensaje,lblMensaje.Text, 30,6,100)
	
	Dim iLeft As Int = lblMensaje.Left, iTop As Int= lblMensaje.Top, iWidth As Int = lblMensaje.Width , iHeight As Int = lblMensaje.Height

	'lblMensaje.SetLayout(0,0,0,0)
	'DoEvents
	'lblMensaje.SetLayoutanimated(1500, iLeft, iTop, iWidth, iHeight)
	'DoEvents

End Sub


'graba en la base que compró algun producto de los que apagan propaganda

Sub Propaganda_SetApaga (Base As SQLCipher) As Boolean
Dim sSql As String, bRet As Boolean = False
	Try		
		If Get_EsPremium(Base) = False Then
			sSql = "Insert Into Seteosusuario (tiposeteo, texto) values ('propaganda','apagada')"  
			Base.ExecNonQuery (sSql)
			bRet = True
		Else
			bRet = True
		End If
	Catch
		ManejaError("Set_Propaganda", "No se pudo apagar la propaganda")
	End Try

Return bRet
End Sub

'devuelve true si es premium (si hizo alguna compra en el playstore)
Sub Get_EsPremium (Base As SQLCipher) As Boolean
Dim sSql As String, Cursor1 As Cursor, bRet As Boolean = True

Try
	' si es tester, lo hace premium (yo no estoy, para poder probar con y sin premium)
	bRet = Get_Tester
	If bRet = False Then 
		' si hizo una compra en este dispositivo o si se hizo premium y está grabado en la base remota.
		sSql = "select count(1) from Seteosusuario where (tiposeteo = 'propaganda' and texto = 'apagada') or tiposeteo = 'premiumremoto'"
		Cursor1 = Base.ExecQuery(sSql)
		Cursor1.Position=0
		bRet = (Cursor1.GetInt2(0) > 0 )
		Cursor1.close
	End If
	'si soy yo, apago el premium
	If Get_SoyYo = True Then	
		bRet = False	
	End If
		
		
Catch
	'ManejaError("Propaganda", "No se pudo apagar la propaganda")
	bRet = True ' si da error, apaga la propaganda
End Try
Return bRet
End Sub


Sub Get_PalabraEnJuego (Base As SQLCipher) As Int
	
	Dim sSql As String, Cursor1 As Cursor, iRet As Int = 0
	Try	
		sSql = "Select Max(idPalabra) from avance"
		Cursor1 = Base.ExecQuery(sSql)
		If Cursor1.RowCount>0 Then
			Cursor1.Position = 0
			iRet = Cursor1.GetInt2(0)
		End If
		Cursor1.Close
	Catch
		Log("Catch GetPalabraEnJuego")
	End Try
		
Return iRet
End Sub

Sub Base_ActualizaPalabrasDesdeAlmacen(BaseJuego As SQLCipher, bPremium As Boolean, bUsuarioAntiguo As Boolean) As Boolean
' en la tabla palabras_almacen, el campo Premium define si cada palabra es premium o no.
' tiene 3 estados posibles
' N es una palabra comun, se copia directamente a Palabras para todos los usuarios
' S es una palabra premium. Se copia a los que compraron la aplicación o a los que son usuarios antiguos (ver funcion Get_UsuarioAntiguo...)
' X es una palabra comun para los usuarios antiguos, pero una palabra premium para los usuarios nuevos (ver funcion Get_UsuarioAntiguo...)
	Dim sSelect As String, sInsert As String, bRet = False, sIncluye As String
	
	sSelect = "Select pa.idpalabra, pa.palabra, pa.descripcion, pa.dificultad, pa.tipopalabra, pa.idnivel, pa.ayuda01, " & _ 
		"pa.ayuda02, pa.ayuda03, pa.Diccionario, pa.PalabraDiccionario " & _
		"FROM palabras_almacen  As pa left join palabras As p on p.idpalabra  = pa.idpalabra " & _ 
		"where p.idpalabra Is Null AND Premium In ('N'"

	' si es premium, incluye las premium S y las opcionales X	
	If bPremium  Then
		sSelect = sSelect & " , 'S', 'X', 'x', 's'"
	Else  ' si no es premium
		'si es un usuario antiguo, incluye
		If bUsuarioAntiguo Then
			sSelect = sSelect & " , 'X','x'"
		End If
	End If	
	sSelect = sSelect & ")"
	
	sInsert = "Insert Into palabras (idpalabra, palabra, descripcion, dificultad, tipopalabra, idnivel, ayuda01, ayuda02, ayuda03, diccionario, palabradiccionario) "
	sInsert = sInsert & sSelect
	Try
		BaseJuego.ExecNonQuery(sInsert)
		'Log(sInsert)
		bRet = True
	Catch
		ManejaError("PalabrasAlmacen", LastException.Message)
	End Try

Return bRet	
End Sub

Sub Get_UsuarioAntiguo (BaseU As SQLCipher) As Boolean
Dim sFecha As String, bRet As Boolean = False, sF As String

	sFecha = get_FechaInstalacion(BaseU)
	sFecha = sFecha.SubString2(0,4) & sFecha.SubString2(5,7)
	'si la instalacion es anterior a mayo de 2015, le regala las palabras premium
	If sFecha <"201505" Then
		bRet = True	
	End If

Return bRet
End Sub



Sub Get_PremiumCantPalabras(BaseJ As SQLCipher) As String
Dim sRet As String

	Dim sSql As String, Cursor1 As Cursor, iRet As Int = 0
	Try	
		sSql = "Select Count(1) from palabras_almacen where premium <> 'n' and premium <>'N'"
		Cursor1 = BaseJ.ExecQuery(sSql)
		If Cursor1.RowCount>0 Then
			Cursor1.Position = 0
			sRet = Cursor1.GetString2(0)
		End If
		Cursor1.Close
	Catch
		Log("Catch Get_PremiumCantPalabras")
	End Try
	
Return sRet	
End Sub



'devuelve true si es premium y el dato está grabado en la base remota (cuando graba en remota, genera un registro en seteosusuario.
Sub Get_EsPremiumGrabadoRemoto (Base As SQLCipher) As Boolean
Dim sSql As String, Cursor1 As Cursor, bRet As Boolean = True

Try
	' si es tester, lo hace premium (yo no estoy, para poder probar con y sin premium)
	bRet = Get_Tester
	If bRet = False Then 
		' si hizo una compra en este dispositivo o si se hizo premium y está grabado en la base remota.
		sSql = "select count(1) from Seteosusuario where tiposeteo = 'premiumremoto'"
		Cursor1 = Base.ExecQuery(sSql)
		Cursor1.Position=0
		bRet = (Cursor1.GetInt2(0) > 0 )
		Cursor1.close
	End If	
	If Get_SoyYo = True Then
		bRet =False 
	End If
	
Catch
	'ManejaError("Propaganda", "No se pudo apagar la propaganda")
	bRet = True ' si da error, apaga la propaganda
End Try
Return bRet
End Sub


Sub Get_HayPalabrasAnteriores(BaseJ As SQLCipher, iIdPalabra As Int, sAnd As String) As Boolean
Dim iReg As Int

	Dim sSql As String, Cursor1 As Cursor, bRet As Boolean
	Try	
		sSql = "Select Count(1) from avance where idpalabra<" & iIdPalabra & sAnd 
		Cursor1 = BaseJ.ExecQuery(sSql)
		If Cursor1.RowCount>0 Then
			Cursor1.Position = 0
			bRet= (Cursor1.getint2(0)>0)
			
		End If
		Cursor1.Close
	Catch
		Log("Get_HayPalabrasAnteriores")
	End Try
	
Return bRet
End Sub

Sub Get_HayPalabrasPosteriores(BaseJ As SQLCipher, iIdPalabra As Int, sAnd As String) As Boolean
Dim iReg As Int

	Dim sSql As String, Cursor1 As Cursor, bRet As Boolean
	Try	
		sSql = "Select Count(1) from avance where idpalabra>" & iIdPalabra & sAnd 
		Cursor1 = BaseJ.ExecQuery(sSql)
		If Cursor1.RowCount>0 Then
			Cursor1.Position = 0
			bRet= (Cursor1.getint2(0)>0)
			
		End If
		Cursor1.Close
	Catch
		Log("Get_HayPalabrasPosteriores")
	End Try
	
Return bRet
End Sub
