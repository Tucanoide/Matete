Type=Activity
Version=5.8
ModulesStructureVersion=1
B4A=true
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: true
	#IncludeTitle: false
#End Region
'25/8 se crean funciones para calculo de costos letras y saltar palabra. Las letras cuestan 5 de la primera a la 9na palabra.

Sub Process_Globals
	Dim gt_ColoresPaleta(5) As tColoresPaleta

	Dim gi_Historia_P_Arriba As Int = -1 'puntero para la panntalla de historia de matetes
	Dim gi_Historia_P_Abajo As Int = -1
	Dim gi_Historia_TextSize As Int = 1000
	Dim gb_PalabraRejugada As Boolean = False

	Dim httpRemoto As HttpClient
	Dim httpRemotoSelect As HttpClient
	
	Dim sgDireccionRemota As String = "http://matrem.esy.es/ukit.php"	
	Dim sgQueryLocalOkRemoto As String ' setea el query local que debe ejecutar si el remoto funciono ok
	
	Dim bIniciarPremium As Boolean

	Dim tracker As AnalyticsTracker
	

''INAPP PRODU|CTOS
   Dim manager As BillingManager3
   Private key As String = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApRHoUo0M+jS7lrqSZy30dkQbwnUxD5BFVuW/yMgRCxomhL6A8TdoKyBn1YsvNAbKm3oM/UmOB1c98d9/RalWIrWOjrS7KqLtYeyY8lglOXhDrUKXDM8skQFd1G1btTCtnZHXpSdAhVZEIgLwG2Sq5azwnbR/YlOapAuHEfKNUzd7p+nOexi6q2HTcGN0dPnz7EumvIlgXWd3wpMkIZQ79WCUy7jLSdWiM23QPkrHUnMhemPjjCuTLT0MV64Egrlw0AmW0i1olQD/q8Hmh4fEDlVD+OI7CVE2JwwkHjEXvLFD38k92SfnI5slWWu+IvMIPpi+q2/DM2SgjRxnjx1EmQIDAQAB"

							
'array donde guarda los productos adquiridos por el usuario
	Dim aProductosComprados() As tProductosComprados' guarda los productoID de los productos comprados por el usuario
	Dim aProductosPlayStore() As tProductosPS	
	Dim iProductoElegido As Int	= -1

'array de querys locales (que se ejecutan con el exito de los querys remotos)
	Dim aQuerysLocales (2) As String


Dim gi_AnimacionEnCurso As Int ' para saber que hacer cuando termina una animación
Dim gi_NeuronasCompradas As Int
Dim gi_ProductoPremium As Int = 3
Dim gb_EsPremium As Boolean
Dim gb_PremiumRemoto As Boolean ' setea true cuando consulta remoto y le da que si.

''FIN INAPP PRODUCTS

''PUBLISHER ID ADSENSE.GOOGLE

Dim gs_IDAppFireworks As String = "sqqqBk556VRU9lr3Qel5z3LjZXvM1Vfj"
'adbuddiz
'Dim gs_AdBuddizId As String = "95ce0c18-693f-4fa9-b30f-14624f4116c0"

Dim bAnulaInsterstitialInicial As Boolean = False
Dim bProductos As Boolean = True

''IMAGENES PRECARGADAS

Dim bitmNeurona As Bitmap
Dim bitmMenuVolver As Bitmap
Dim bitmRojoCancelar As Bitmap
Dim bitmVerdeConfirmar As Bitmap
Dim bitmSubir As Bitmap
Dim bitmBajar As Bitmap


Dim gi_PropagandaCuenta As Int = 0 ' lo usa para intercalar propagandas
'Dim gb_propagandaEspera As Boolean ' usa para mantener apagada la propaganda cuando recien arranca el juego

	'These global variables will be declared once when the application starts.
	'These variables can be accessed from all modules.
	Public iCantLetras As Int = 12
	Dim sgLetras As String ' variable donde se guardan las letras que se pueden elegir
	Dim igIDPalabra As Int
	Dim sgPalabraDescripcion As String
	
	Dim gi_PropagandaRate As Int = 3 'cada cuantas palabras muestra una propaganda interstitial )
	Dim xConfiguraPantalla As tConfiguraPantalla 
	Dim iPantallaActiva As Int 'define la pantalla activa, para saber lo que tiene que hacer al presionar back
	
	Dim xQuerysRemotos As tQuerysRemotos ' se usa para las tareas de los querys remotos (tipos de querys que se disparan)
	
	Dim aPistas() As tPistas
	Dim aLetrasPalabra() As tLetrasPalabra
	Dim aLetrasElegir() As tLetrasElegir
	
	Dim sAdicionalArchivoLetra As String = ""
	Dim sAdicionalArchivoLetraComprada As String ="v"

	Dim bFinDeJuego As Boolean= False
	

	Dim iCuentaClickLetraVacia  As Int
	
	Public g_NombreBaseLocalJuego As String ="BaseJ.sqlite"
	Public g_NombreBaseLocalUsuario As String = "BaseUE.sqlite"
	Dim g_sPswBaseJ As String= "NadaAnsioDeNada"
	Dim g_sPswBaseU As String = "MientrasDuraElInstante2"	
	Dim g_DirGrabable As String
	Public g_sqlBaseLocalJuego As SQLCipher, g_sqlBaseLocalUsuario As SQLCipher
	
	

'Palabra y Nivel usando type
	Private xtPalabra As tPalabra
	Private xtNivel As tNivel
	
	Dim g_DeviceValuesHeight As Int, g_DeviceValuesWidth As Int
	Dim g_DeviceValuesScreenSize As Float, g_DeviceValuesScale As Float
	
	Dim bSonidoPrendido As Boolean

	Dim bPublicoEnFacebook As Boolean 
	Dim bPublicoEnTwitter As Boolean 
	Dim bCanceloFacebookoTwitter As Boolean =False ' la prende cuando presiona el boton cancelar en publicar facebook o publicar twitter
	Dim bComproNeuronas As Boolean = False
''V2 V2  V2
'colores de los paneles x colores * y paneles 3 combinaciones x 6 paneles
Type tColorPaneles (iColorR As Int, iColorG As Int, iColorB As Int)
Dim aColoresPaneles(3, 7) As tColorPaneles 'guarda la combinación RGB del color del fondo del panel
Dim aTextColor (3,7) As tColorPaneles  
Dim gi_CombinacionColores As Int = -1
  
'''FONTS 
Dim tFontOpenSansSemiBold As Typeface
Dim tFontOpenSansLight As Typeface




End Sub

Sub Globals
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.



	Dim scvMain As ScrollView
	Dim gt_Color As tColores
	

'INAPP PRODUCTS
	'Private pnlProductos As Panel
	Dim avpnlItemP () As Panel
'FIN INAPP PRODUCTS
	
	
	Dim lblAyuda As Label
	Dim sm As SlideMenu
	'sonido
	Dim SONIDO_ADIVINO As Int= 1, SONIDO_ERROR As Int= 2, SONIDO_NUEVAPALABRA As Int=3
	Dim sound_Adivino As SoundPool, iSound_Adivino As Int
	Dim sound_Error As SoundPool, iSound_Error As Int
	Dim sound_NuevaPalabra As SoundPool, iSound_NuevaPalabra As Int

	'FACEBOOK
	Dim ThisActivity As SocialApiActivity
	Private lblFacebook As Label

'	XXXDim tFontDefinicion As Typeface, tFontMonedas As Typeface
	'XXX'Dim sFontDefinicion As String = "PackardAntique.ttf"' "Batik.ttf"

	Dim iClickVacio As Int

'ARRAYS
	Dim lblArrayLetras() As Label  'array donde guarda los labels de las letras para elegir
	Dim lblArrayPalabra() As Label ' array donde guarda los labels de las letras de la palabra a elegir

'aviso gano
	Private lblBloqueo As Label

'PROPAGANDAS y trackint
    'LEADBOLT prueba 14/7/14
	Dim adBC As LeadBoltB4A ' 664913187 
	Dim adBGris As LeadBoltB4A ' 463221194
	Dim adInt As LeadBoltB4A '703320557 'Dim adWall As LeadBoltB4A '797043925 se quita en octubre 2014 porque no da nada de plata en comparación con el interstitial
	

'AIRPUSH
	'Dim adAirPush As AirpushBanner
	'Dim adAirPushAppAd As AirpushInAppAd
'adMob
	'Dim AdView1 As AdView
' adbuddiz, appfireworks y appnext - se sacan en versión 58 para reemplazarlas por display.io
	'Dim appfw As AppFireworksB4A
	''APPNEXT
	'Dim appNext As AppNextLib
	'ADBuddiz
	'Dim adBuddiz As BAadbuddiz ' 95ce0c18-693f-4fa9-b30f-14624f4116c0 
	
	Dim adDispIO As ioDisplay
	
''XXXXXXXXXXXXXXXXX	
	Dim g_iMonedas As Int, g_iPuntos As Int, g_iJugadas As Int
	Dim g_iPalabras As Int
	
	Private lblAviso As Label
	Private imgAvance As ImageView
	Private imgMenu As ImageView
	Private imgNeuronas As ImageView
	Private lblAvance As Label
	Private lblNeuronas As Label
	
	Private lblV2Nivel As Label
	Private Panel1 As Panel
	Public Panel2 As Panel
	Private Panel3 As Panel
	Private Panel4 As Panel
	Private Panel5 As Panel
	Private Panel6 As Panel

''nuevas views
	Private imgPedirLetra As ImageView
	Private imgSaltarPalabra As ImageView
	Private imgCompartir As ImageView
	Private lblPedirLetra As Label
	Private lblSaltarPalabra As Label

	Private lblv2Def As Label
	Private Panel51 As Panel
	Private Panel41 As Panel
	Private Panel61 As Panel
	Private lbl41 As Label
	Private lbl51 As Label
	Private lbl61 As Label
	Private Panel21 As Panel
	Private lbl21 As Label
	Private lblCompartir As Label

'animacion
	Dim AnimPlus As AnimationPlus

	Private imgAnimacion As ImageView
	Private img51 As ImageView
	Private img51Facebook As ImageView
	Private img51Twitter As ImageView
	
	Private lblMenuTitulo As Label
	Private lbl51Facebook As Label
	Private lbl51Twitter As Label
	Private imgBajarLetras As ImageView
	Private lblBajarLetras As Label
	Private lbl51MensajeMatete As Label

	'label trucho para calcular tamaños de letras
	Dim lblCalcTextSize As Label

	Dim scrViewAyuda As ScrollView

	Private imgShadow As ImageView
	Private Panel11 As Panel
	Private lbl11 As Label

	Private imgLoading As ImageView
	Private pnlLoading As Panel
	Private lblLoading As Label
	Private pnlInvisible As Panel
	Private lblPedirLetraCosto As Label
	Private lblSaltarPalabraCosto As Label
	Private pnlHistoria As Panel
	Private pnlHistFiltro As Panel
	Private lblFiltro As Label
End Sub

Sub Activity_Create(FirstTime As Boolean)

	Activity.LoadLayout("Loading")
	Publicos.Set_PantallaCargaSet(Activity, imgLoading, lblLoading, Colors.rgb(151,89,152), bIniciarPremium )
	DoEvents
	Activity.LoadLayout("JugarV2")

	If FirstTime Then
		'gb_propagandaEspera = True

		tracker.Initialize


		gi_PropagandaCuenta = Rnd(0,1)
	
		'conexion http usada para conectar con la base remota
		httpRemoto.Initialize("httpRemoto")
		httpRemotoSelect.Initialize("httpRemotoSelect")
	
	
		g_DirGrabable = Publicos.Get_DirectorioBase
		v2_CopiaImagenes ' Copia las imágenes desde assets
		V2_CargaImagenesEstaticasEnVariables
		
		'V2_DimColoresPaneles ' genera los array de los colores

		Create_Variables ' genera variables de tipo esas que son grosas, que se divierten como locas.
				
		Publicos.AbreBasesParam(g_sqlBaseLocalJuego, g_NombreBaseLocalJuego, g_sPswBaseJ, _ 
								g_sqlBaseLocalUsuario, g_NombreBaseLocalUsuario, g_sPswBaseU)
						
		CheckPremium						
	
	
		'INAPP PRODUCTS
      	manager.Initialize("manager", key)
     	manager.DebugLogging = True

		gi_PropagandaCuenta = Propaganda_SetAleatoria
	Else
		manager.GetOwnedProducts
	End If
	
	' se apaga fireworks porque empezaron a mostrar propaganda en lugar de loguear eventos. Inentendible
	'FireWorks_Inicio
	Propaganda_Crear
	Set_ColoresVariable (0)
	V2_MenuGenera
	
	
	'apaga las variables de retorno de Facebook/Twitter si pasa por le create, quiere decir que destruyó el activity Jugar, necesita regenerar toda la pantalla
	bCanceloFacebookoTwitter= False
	bPublicoEnFacebook = False
	bPublicoEnTwitter = False
	
	'apaga loading
	lblLoading.RemoveView
	imgLoading.visible = False
	
End Sub

Sub Activity_Resume
	Dim bNoRefrescar As Boolean = bCanceloFacebookoTwitter Or bPublicoEnFacebook Or bPublicoEnTwitter  Or bComproNeuronas

	bAnulaInsterstitialInicial = True

	' si viene desde facebook o twitter y cancelo o si viene del eshop
	If bNoRefrescar Then
		' setea los controles sobre la pantalla	
		V2_PantallaConfigura(xConfiguraPantalla.Jugar,0 )
		If bComproNeuronas Then
			V2_PantallaConfigura (xConfiguraPantalla.ComproNeuronas, gi_NeuronasCompradas)
			bComproNeuronas=False
			'no refresca puntos a proposito, lo hace al salir de la pantalla de informe de compra
		Else	
			'refresca puntos
			Neuronas_Mostrar
		End If
	Else
		Log("reume else norefrescar")
		V2_SetPantalla 
	End If

	
	
	SonidosInicia

	If bFinDeJuego Or Publicos.get_EsUltimaPalabra(g_sqlBaseLocalUsuario, g_sqlBaseLocalJuego) = True Then
		imgLoading.Visible = True
		'Publicos.Set_PantallaCargaSet(Activity, imgLoading, lblLoading, Colors.rgb(151,89,152))
		V2_PantallaConfigura(xConfiguraPantalla.FinDeJuego, 5)
	Else
		'
		If bNoRefrescar Then
			bCanceloFacebookoTwitter=False
		Else
			InicioMatete
		End If
	End If
	If gb_EsPremium = False Then
		adBC.loadAd()
	End If	

	If bPublicoEnFacebook Then
		bPublicoEnFacebook = False
		ToastMessageShow("Publicación exitosa en tu muro!", True)
		'appfw.eventWithValue("Facebook", xtPalabra.idPalabra)

	End If	
	If bPublicoEnTwitter Then
		bPublicoEnTwitter = False
		ToastMessageShow("Publicación exitosa en Twitter!", True)
		'appfw.eventWithValue("Twitter", xtPalabra.idPalabra)

	End If	

	If bIniciarPremium Then
		V2_PantallaConfigura(xConfiguraPantalla.Premium, 0)
	End If
	
	
	'
	'FireWorks_Resume

End Sub


Sub PantallaEspera(bPrender As Boolean, bTransparente As Boolean)
	If bPrender Then
		Publicos.ViewComoActivity(Activity, lblBloqueo)
		lblBloqueo.BringToFront
	End If	
	If bTransparente Then
		lblBloqueo.Color = Colors.Transparent
	Else
		lblBloqueo.Color = Colors.black
	End If
	lblBloqueo.Visible = bPrender
End Sub

Sub Activity_Pause (UserClosed As Boolean)
'Fireworks_Pause
End Sub

Sub InicioMatete ' inicio del Activity

	v2_MuestraNuevaPalabra(0,False,False) 'muestra en pantalla la definición de la palabra leida, acomoda el tamaño de la letra
	
End Sub

Sub V2_MuestraDefinicion
	lblv2Def.Text = xtPalabra.descripcion
	Publicos.SetLabelTextSize(lblv2Def, xtPalabra.descripcion, 60, 10, 100)
	
End Sub

'recibe como parámetro el id de la palabra a mostrar y en bRejuego true en caso de que sea una palabra que se vuelve a jugar (salteada o adivinada, pero se juega repetida. En este caso, si ya la habia adivinado, no graba avance)
Sub v2_MuestraNuevaPalabra(idPalabra As Int, Saltada As Boolean, bRejuego As Boolean)
gb_PalabraRejugada = bRejuego


	If bRejuego = False And Publicos.Get_EsUltimaPalabra(g_sqlBaseLocalUsuario, g_sqlBaseLocalJuego) = True Then
		V2_PantallaConfigura(xConfiguraPantalla.FinDeJuego,0)
		
	Else
		Panel4.RemoveAllViews
		Panel5.RemoveAllViews
		'Dim iPaleta As Int =  xtPalabra.idPalabra Mod 3
	
		
		

		CargarPalabra(idPalabra, bRejuego, Saltada) ' lee la palabra de la base y la guarda en variable xtPalabra
		Set_Colores(xtPalabra.idNivel)
		CargarNivel(xtPalabra.idNivel, xtPalabra.idPalabra) ' carga datos especificos del nivel, costos, descripcion, etc
		

		'V2_Set_ColoresPaneles (gi_CombinacionColores) 'setea los colores de los paneles
		V2_CargaImagenesViewsEstaticas 'carga las imagenes que no cambian con los colores de la pantalla, que son siempre las mismas
		Costos_Muestra
		
		Panel4.RemoveAllViews 
		v2_GeneraLabelsPalabra (aLetrasPalabra)
		V2_GeneraLabelsLetras(aLetrasPalabra)
		
		
		Neuronas_Mostrar
		V2_MuestraDefinicion		
		Propaganda_Mostrar
	End If
End Sub

Sub Neuronas_Mostrar

	Dim Cursor1 As Cursor, sSqlCipher As String, ssql As String, sMonedas As String, sPuntos As String, sJugadas As String
	Try
		'busca puntos y monedas
		ssql ="Select monedas, puntos, idnivel From usuario"
		Cursor1 = g_sqlBaseLocalUsuario.ExecQuery(ssql)

		If Cursor1.RowCount >0 Then
			Cursor1.Position = 0
			g_iMonedas = Cursor1.GetInt2(0)
			
			
			'lblMonedas.Text ='" "&Publicos.sNombreMonedas & ": "  & iMonedas
			lblNeuronas.Text = g_iMonedas
			
			g_iJugadas = Publicos.get_CantidadPalabrasJugadas(g_sqlBaseLocalUsuario)
			g_iPalabras	 = Publicos.get_CantidadPalabras(g_sqlBaseLocalJuego)

			lblAvance.Text = g_iJugadas & "/" & g_iPalabras	
			
			'barra de avance
			'lblAvanceHecho.Width = (g_iJugadas / g_iPalabras)*Activity.Width 
			'lblAvancePendiente.Left = lblAvanceHecho.Left + lblAvanceHecho.width
			'lblAvancePendiente.Width = Activity.Width - lblAvanceHecho.width 
		
		End If	
	    Cursor1.Close
	
	Catch 
		ManejaErrorJugar("MuestraPuntos","MuestraPuntos")
	End Try

End Sub

Sub CargarPalabra(pIDPalabra As Int, bRejuego As Boolean, bSalteada As Boolean) As Boolean
'devuelve true si encontro una palabra, false si ya no hay mas, por lo que termino el juego
	'sgLetras = "0123456789AB"
	Dim iIDPalabra As Int, sSql As String
	Dim Cursor1 As Cursor, sFin As String, sFecha As String
	Dim iInsertarAvance As Int = 1
	Dim bRet As Boolean = False, iProximaPalabra As Int
	Try
	
		Sonido(SONIDO_NUEVAPALABRA)
		
		If bRejuego Then	
			iIDPalabra = pIDPalabra
			iInsertarAvance = 0
		Else	
			'busca el avance del usuario si no es rejuego
			sSql ="select idPalabra, ifnull(fin,'') fin from avance where idPalabra = (select max(idPalabra) from avance)"
			Cursor1 = g_sqlBaseLocalUsuario.ExecQuery(sSql)

			If Cursor1.RowCount >0 Then
				bRet = True
				
				Cursor1.Position = 0
				sFin = ""& Cursor1.getstring2(1)'fin
				iIDPalabra= Cursor1.GetInt2(0)'idpalabra
				
				If sFin = ""  Then ' si es una palabra que todavia no adivino, le resta uno para que el proximo query la tome como siguiente
					iIDPalabra = iIDPalabra-1 ' 	
		 			iInsertarAvance = 0
				End If
			Else
				
				iIDPalabra = 0
			End If
		    Cursor1.Close
		End If		
		'si es una palabra que nunca h
		If iInsertarAvance = 1 Then
			sFecha = DateTime.Date(DateTime.now)
			'sSql = "Insert Into Avance (idPalabra, Inicio, adivinada, salteada) Select idPalabra,"
			'sSql = sSql & "'" & sFecha & "', 0,0 from palabras where idpalabra = ("
			'sSql = sSql & "select min(idpalabra) from palabras where idpalabra > "& iIDPalabra & ")"
			
			'trae proxima palabra
			sSql = "select min(idpalabra) from palabras where idpalabra > "& iIDPalabra 
			Cursor1 = g_sqlBaseLocalJuego.ExecQuery(sSql)
			If Cursor1.rowcount>0 Then
				Cursor1.Position = 0
				iProximaPalabra= Cursor1.GetInt2(0)
			End If
			Cursor1.close
			
			sSql = "Insert Into Avance (idPalabra, Inicio, adivinada, salteada) Values ("
			sSql = sSql & iProximaPalabra & ",'" & sFecha & "',0,0)"
			
			g_sqlBaseLocalUsuario.ExecNonQuery(sSql)
		End If
	
		If bRejuego = False Then
			' trae la palabra siguiente
			sSql = "select idPalabra, Palabra, Descripcion, Dificultad, IdNivel, TipoPalabra, Diccionario, PalabraDiccionario from palabras where idpalabra ="
			'sSql = "select idPalabra, Palabra, Descripcion from palabras where idpalabra ="
			sSql = sSql & "(Select Min(idpalabra) from palabras where idpalabra>" & iIDPalabra & ")"
		Else 'trae la palabra que quiere rejugar
			sSql = "select idPalabra, Palabra, Descripcion, Dificultad, IdNivel, TipoPalabra, Diccionario, PalabraDiccionario from palabras where idpalabra =" & pIDPalabra
		End If
		Cursor1 = g_sqlBaseLocalJuego.ExecQuery(sSql)
		
		If Cursor1.RowCount>0 Then
			
			Cursor1.Position =0
			igIDPalabra= Cursor1.GetInt2(0)
			sgPalabraDescripcion = Cursor1.GetString2(2)
			
			xtPalabra.idPalabra = Cursor1.GetInt2(0)
			xtPalabra.Palabra= Cursor1.Getstring2(1): xtPalabra.Palabra = xtPalabra.Palabra.ToUpperCase
			sgLetras = xtPalabra.Palabra

			xtPalabra.Descripcion = Cursor1.Getstring2(2)
			xtPalabra.Dificultad = Cursor1.Getstring2(3)
			xtPalabra.idNivel = Cursor1.GetInt2(4)
			xtPalabra.TipoPalabra= Cursor1.Getstring2(5)
			xtPalabra.Diccionario = Cursor1.GetString2(6)
			xtPalabra.PalabraDiccionario = Cursor1.GetString2(7)
			xtPalabra.bRejugada = bRejuego
			xtPalabra.bSalteada = bSalteada
	
		Else
			ManejaErrorJugar("Cursor Vacio", "Cursor")
		End If
		Cursor1.close
	
		
		Palabra_CargaLetras (xtPalabra.Palabra, igIDPalabra)
		
	Catch	
		ManejaErrorJugar("CargarPalabra","Cursor Vacio")
	End Try
	
Return bRet		
End Sub

Sub CargarNivel (idNivel As Int, idPalabra As Int) As Boolean
'devuelve true si encontro el nivel. Lo carga en una variable de tipo usuario
	Dim sSql As String, bRet As Boolean
	Dim Cursor1 As Cursor
	Try

		sSql ="select idNivel, Nombre, ifnull(imagenfondo,''), ifnull(costosaltar,15), ifnull(costoletra,1), ifnull(costoayuda,1), ifnull(mensajeinicio,''), ifnull(mensajefinal,''),"  
		sSql= sSql & "ifnull(imageninicio,''), ifnull(imagenfinal,'') from Niveles where idnivel = " & idNivel
		Cursor1 = g_sqlBaseLocalJuego.ExecQuery(sSql)

		If Cursor1.RowCount >0 Then
			bRet = True
			Cursor1.Position = 0
			
			xtNivel.idNivel = Cursor1.getint2(0)
			xtNivel.Nombre = Cursor1.GetString2(1)
			xtNivel.ImagenFondo= Cursor1.GetString2(2)
			xtNivel.CostoSaltar = Cursor1.getint2(3)
			xtNivel.CostoLetra = Cursor1.getint2(4)
			xtNivel.CostoAyuda = Cursor1.getint2(5)
			xtNivel.MensajeInicio= Cursor1.GetString2(6)
			xtNivel.MensajeFinal= Cursor1.GetString2(7)
			xtNivel.IdPrimeraPalabra = Get_NivelPrimeraPalabra(xtNivel.idNivel)
			xtNivel.IdUltimaPalabra = Get_NivelUltimaPalabra(xtNivel.idNivel)

		End If
		Cursor1.close
	Catch	
		ManejaErrorJugar("CargarNivel",LastException.Message)
	End Try

	If Publicos.Get_EsPremium(g_sqlBaseLocalUsuario) Then
		lblV2Nivel.Text = "MATETE DIVERGENTE PREMIUM" & Chr(10) & "NIVEL " & xtNivel.Nombre.ToUpperCase
	Else
		lblV2Nivel.Text = "MATETE DIVERGENTE" & Chr(10) & "NIVEL " & xtNivel.Nombre.ToUpperCase
	End If
	
	
		' si esta jugando la primera palabra del nivel, muestra el cartel de inicio
	If xtNivel.MensajeInicio.Trim <>"" Then
		If xtNivel.IdPrimeraPalabra = idPalabra And xtNivel.bMensajeInicioMostrado = False Then
			V2_PantallaConfigura (xConfiguraPantalla.MuestraAviso, 0)

			xtNivel.bMensajeInicioMostrado = True
		End If
	End If
Return bRet		
End Sub

Sub EligeUnaLetra (lLabel As Label, bComprada As Boolean, iPosicionPalabra As Int, iPosLetras As Int)
' cuando hace click en una letra de la lista para completar la palabra.	
'iPosicionPalabra = -1 si hizo click en una letra, y la posicion en la palabra si compro una letra
Dim sLetra As String, sAux As String
'busca la primera posicion vacia en la lista de labels a  menos que pase por parametro una posicion forzada en iPosicionPalabra

Try

	
	If iPosicionPalabra = -1 Then ' si va a la primera posicion libre porque eligio una letra
		'busca la primera posicion libre	
		For i = 0 To lblArrayPalabra.Length-1
			If lblArrayPalabra(i).Text = "" Then
				iPosicionPalabra = i 
				Exit
			End If
		Next
	End If
	' ahora en iposicionpalabra tiene la posicion de la palabra donde va la letra elegida (comprada o clickeada)

	lblArrayPalabra(iPosicionPalabra).Text = lLabel.Text
	sLetra = lLabel.Text
	If bComprada Then
		'sLetra = sLetra & "v"
		aLetrasPalabra(iPosicionPalabra).comprada = True
		lblArrayPalabra(iPosicionPalabra).Tag="?"	

		For i = 0 To sgLetras.Length-1
			If i<> iPosLetras Then
				sAux = sAux & sgLetras.SubString2(i,i+1)
			Else
				sAux = sAux & "?"
			End If
		Next 
		sgLetras = sAux	
		lblArrayPalabra(iPosicionPalabra).SetBackgroundImage(gt_Color.bitmPalabraComprada)	
	Else
		lblArrayPalabra(iPosicionPalabra).TextColor = gt_Color.colortexto
		lblArrayPalabra(iPosicionPalabra).Tag = lLabel.Tag ' pone en el tag de la palabra el tag de la letra elegida (indice)
		lblArrayPalabra(iPosicionPalabra).SetBackgroundImage(gt_Color.bitmPalabraOn)	
	
	End If
	
	' vacia la posicion de las letras que hizo click

	lLabel.textcolor = Get_ColorLetraVacia	
	' completa la letra de la palabra
	
	lblArrayPalabra(iPosicionPalabra).text = sLetra
	'Publicos.SetLabelTextSize(lblArrayPalabra(iPosicionPalabra), sLetra, 30,5, 70) 
		
	'si se completaron todas las letras de la palabra	
	If ComparaPalabraCuentaLetras = xtPalabra.Palabra.Length Then
		ComparaPalabraHacer
	End If

Catch

	ManejaErrorJugar("EligeUnaLetra","Error Letra: "&sLetra)


End Try

End Sub

Sub ComparaPalabra As Boolean
Dim sPalabra As String = ""
	
	For Each v As Label In lblArrayPalabra
		If v.Text ="" Then
			Exit
		End If
		sPalabra = sPalabra & v.Text
	Next 
Return sPalabra = xtPalabra.palabra 	
End Sub

'cuenta la cantidad de letras ya elegidas en la palabra
'hace un shortcut si alguna no está completa para no contar sin sentido. 
Sub ComparaPalabraCuentaLetras As Int
	Dim iCant As Int = 0
	For Each v As Label In lblArrayPalabra
		If v.Text ="" Then
			Exit
		End If
		iCant = iCant +1
	Next 
Return iCant
End Sub

Sub ComparaPalabraHacer
	' si acerto la palabra
	If ComparaPalabra Then
		AdivinoPalabra
	Else
		If ComparaPalabraCuentaLetras = xtPalabra.Palabra.Length Then
			V2_NoAdivinoPalabraResalta
		End If
	End If
End Sub

Sub AdivinoPalabra 
Dim iTop As Int

	
	Sonido(SONIDO_ADIVINO)
	
	AdivinoGrabaNivel(True)

	V2_PantallaConfigura(xConfiguraPantalla.Adivino,0)
	'continua en el click de pnladivino
	

	'appfw.eventWithValue("PalabraAdivinada", xtPalabra.idPalabra)
	
	
End Sub

Sub bloquearpantalla (bBloquear As Boolean, sTipoBloqueo As String, bTransparente As Boolean)
lblBloqueo.BringToFront
lblBloqueo.Visible = bBloquear
lblBloqueo.Tag = sTipoBloqueo 
If bTransparente Then
	lblBloqueo.Color = Colors.Transparent
End If
End Sub

Sub lblBloqueo_Click 

If lblBloqueo.Tag ="S" Then
	Activity.Finish
	'appfw.eventWithValue("close", xtPalabra.idPalabra)
	'appfw.closeSession(True)
Else
	If lblBloqueo.tag="N" Then
		'imgseguir_Click
	End If
End If
End Sub

Sub AdivinoGrabaNivel_Avance(bAdivino As Boolean) As Boolean
Dim sSql As String, bRet As Boolean, sFecha As String

Try
	' si es una palabra que rejuega y ya habia adivinado, no graba nada
		If xtPalabra.bRejugada = False Or xtPalabra.bSalteada Then
			If bAdivino Then
				sSql = "Update Avance Set adivinada=1, Salteada =0"
			Else 
				sSql = "Update Avance Set salteada =1, adivinada = 0 "
			End If
			sFecha = DateTime.Date(DateTime.now)
			sSql = sSql & ", Fin ='" & sFecha & "' where idPalabra = " & xtPalabra.idpalabra ' (select max(idpalabra) from avance)"
			g_sqlBaseLocalUsuario.ExecNonQuery (sSql)
			
			BaseRemota_GrabarAvance (xtPalabra.idPalabra)
		End If

		bRet = True
		
Catch
		bRet = False
End Try

Return bRet
End Sub

Sub AdivinoGrabaNivel_Usuario (bAdivino As Boolean) As Boolean
Dim sSql As String, bRet As Boolean, iMonxNiv As Int

Try
		If xtPalabra.bRejugada = False Or xtPalabra.bSalteada Then

			'' si adivino le suma puntos, sino descuenta el salto
			If bAdivino Then
				'graba puntos y monedas en usuario
				iMonxNiv = Get_MonedasPorNivel
				sSql ="Update Usuario Set Puntos = ifnull(Puntos,1)+1,  Monedas = ifnull(monedas,0) + " & iMonxNiv
			Else
				sSql ="Update Usuario Set Monedas = monedas - " & Get_CostoSaltarPalabra
			End If
			
			g_sqlBaseLocalUsuario.ExecNonQuery(sSql)
		End If
		bRet = True
Catch
	bRet = False
End Try
Return bRet
End Sub

Sub AdivinoGrabaNivel (bAdivino As Boolean)

	Try
	
		AdivinoGrabaNivel_Avance (bAdivino)
		
		
		AdivinoGrabaNivel_Usuario(bAdivino) 
	
	Catch	
		ManejaErrorJugar("AdivinoGrabaNivel","Error GrabaNivel")
		
	End Try

End Sub

Sub DescartaUnaLetra (lLabel As Label) ' recibe como parametro el label que hizo click
' cuando hace click en una letra de la palabra.	
	'busca el label que era dueño de la letra, por el tag.
	' el tag de la letra de la palabra tiene guardado el id del tag del label de las letras
	If lLabel.Text <>"" And lLabel.Tag <>"C" Then ' si el label clickeado tiene una letra cargada, acciona, sino no hace nada
		For Each v As Label In lblArrayLetras
			If v.Tag = lLabel.tag Then ' si es el label que contenia esa letra
			    v.Text = lLabel.Text
				'Publicos.SetLabelTextSize(V, lLabel.Text, 30, 5, 70) 
		
				lLabel.text = ""
				'pone sin letra en la palabra Publicos.CargaImagenLetra("SinLetra", lLabel)
				lLabel.Text = ""
				' pone letra en label letras Publicos.CargaImagenLetra(V.Text&sAdicionalArchivoLetra, V)
				v.TextColor = Get_ColorLetraDisponible
				lLabel.SetBackgroundImage(gt_Color.bitmPalabraOff)	
				Exit
			
			End If
				
		Next 
		
	End If
End Sub

Sub lblLetras_Click ' click  una letra (la pasa a la palabra)
    Dim Send As Label
    Send = Sender
	If Send.textcolor = Get_ColorLetraDisponible Then ' si la letra esta disponible, (sino, hizo click sobre una letra que ya fue usada y esta en la palabra
		If Send.Text <>"" Then ' si no está vacía
			If LetraPalabraDisponible Then 'si hay alguna posicion de la palabra libre
				EligeUnaLetra (Send, False, -1, -1) 	   
			Else
				ToastMessageShow("UPS!", False)
			End If	
			iCuentaClickLetraVacia =0
		End If
	End If
End Sub

Sub lblPalabra_Click ' click descartar una letra (sobre la palabra)
    Dim Send As Label

    Send = Sender
	If Panel61.Visible = False Then
		lblPalabraClick (Send)
	End If
	
End Sub

Sub lblPalabra_LongClick
'devuelve todas las letras de la palabra a sus posiciones originales desde la que cliqueo hasta la ultima
Dim Send As Label
Send = Sender

BajarLetras

End Sub

Sub BajarLetras

For j=0 To lblArrayPalabra.Length-1
	lblPalabraClick (lblArrayPalabra(j))
Next

'appfw.eventWithValue("VaciarPalabra", xtPalabra.idPalabra)

End Sub

Sub imgBajarLetras_Click
	BajarLetras
End Sub

Sub lblPalabraClick(send As Label)
Try	
		If send.Text<>"" Then ' 
			If send.Tag <>"?" Then ' si no es una letra comprada
				DescartaUnaLetra (send) 	   
				iClickVacio = 0
			End If
		Else
			iClickVacio = iClickVacio +1
			If iClickVacio =12  Then
				Publicos.Magia (iClickVacio)
			End If
		End If
Catch
	ManejaErrorJugar("lblPalabraClick", "")
End Try
End Sub

Sub LetrasInventa (sLetrasPalabra As String) As String
	Dim sRet As String, iRnd As Int
	Dim sLetras(iCantLetras) As String
	'Dim data() As Int = Array As Int (1,2,2,2,2,2,3,1,2,0,0,2,2,0,3)
	Dim sVocales() As String = Array As String ("A","E","I","O","U")
	Dim sABC() As String = Array As String ("A","B","C","D","E","F","G","H","I","J","K","L", _
				"M","N","O","P","Q","R","S","T","U","V","X","Y","Z", _
				"A","E","I","O","U","N","S","T","D","M","L")
	

	Dim iaOrden (iCantLetras) As tOrdenar, iAux  As tOrdenar

	'desordena las letras de la palabra
	For i = 0 To sLetrasPalabra.Length-1
		iaOrden(i).Initialize
		iaOrden(i).letra = sLetrasPalabra.SubString2(i,i+1)
		iaOrden(i).Orden = Rnd(0,97)
		iaOrden(i).Indice = i
	Next

	' si faltan letras para completar el total
	If sLetrasPalabra.Length < iCantLetras Then
		'agrega letras al azar
		'una vocal primero
		iRnd = Rnd(0,4)
		sLetras(sLetrasPalabra.Length) = sVocales(iRnd)
		For i = sLetrasPalabra.Length To iCantLetras -1
			iRnd = Rnd(0,sABC.Length-1)
			iaOrden(i).Indice = i
			iaOrden(i).Orden = Rnd(0,97)
			iaOrden(i).Letra=sABC(iRnd)
		Next
	End If


	'ordena el vector por el numero random
	For i = 0 To iaOrden.Length-2
		For j = i +1 To iaOrden.Length-1
			If iaOrden(i).Orden > iaOrden(j).Orden Then
				iAux.Initialize
				iAux.Indice = iaOrden(i).indice
				iAux.Letra= iaOrden(i).Letra
				iAux.Orden= iaOrden(i).Orden
					
				
				iaOrden(i).Indice = iaOrden(j).Indice
				iaOrden(i).Letra = iaOrden(j).Letra
				iaOrden(i).orden = iaOrden(j).orden
				
				iaOrden(j).Indice = iAux.Indice
				iaOrden(j).Letra = iAux.Letra
				iaOrden(j).Orden = iAux.orden
			End If
		Next
	Next
	
	'vuelve las letras a sRetw
	sRet = ""
	For i = 0 To iCantLetras -1
		sRet = sRet & iaOrden(i).Letra
	Next
	
Return sRet
End Sub

Sub BorraLabels 
		
     For Each L As Label In lblArrayPalabra
	 	L.RemoveView
	 Next

End Sub

Sub activity_KeyPress(KeyCode As Int) As Boolean

If KeyCode = KeyCodes.KEYCODE_BACK Then
	'Activity.finish
	' si estaba jugando y ya terminó el juego, vuelve a la pantalla de historia
	If iPantallaActiva = xConfiguraPantalla.Jugar And bFinDeJuego Then
		V2_PantallaConfigura(xConfiguraPantalla.Historia,0)
	Else
		If iPantallaActiva = xConfiguraPantalla.jugar Or iPantallaActiva = xConfiguraPantalla.Premium Or iPantallaActiva=xConfiguraPantalla.FinDeJuego Then
			StartActivity("Main")
			Activity.finish
			'appfw.eventWithValue("close", xtPalabra.idPalabra)
			'appfw.closeSession(True)
		Else
			If iPantallaActiva = xConfiguraPantalla.Adivino Then
				lbl61_Click
			Else
				V2_PantallaConfigura(xConfiguraPantalla.Jugar, 0)
			End If
		End If		
	End If
	Return True
Else
	Return False
End If

End Sub

Sub GetCantidadPistasPendientes() As Int
Dim sSql As String, iRet As Int=0, xCursor As Cursor

Try
	sSql ="Select count(1) cta from pistas where palabraid = " & igIDPalabra & " and comprada=0 and gratis =0"
	xCursor = g_sqlBaseLocalJuego.ExecQuery(sSql)
	
	If xCursor.RowCount >0 Then
		xCursor.Position=0
		iRet = xCursor.GetInt2(0)
	End If
	xCursor.Close
Catch
	ManejaErrorJugar ("GetCantidadPistasPendientes","Error en Pistas")
End Try
Return iRet
End Sub
'actualiza la base dde datos y el vector con la compra de la pista

Sub imgSaltarPalabra_click
	Dim bRet As String, sTit As String ="¿Confirma saltar el matete?"
	
	
	If SaltarPalabraPuede(g_iMonedas) Then

		'saca foto a la pantalla, para poder animarla
	
		V2_PantallaConfigura(xConfiguraPantalla.SaltarPalabra, 0)
		'bRet = Msgbox2(sTit,"Costo: "&Get_CostoSaltarPalabra& " monedas" , "Saltar", "Cancelar","", Null) = DialogResponse.POSITIVE
		'If bRet Then
			'SaltarPalabraHace
		'End If
	Else
		Dim sMsg As String, iRet As Int
		sMsg = "No tienes " & Publicos.sNombreMonedas & " suficientes" & Chr(10) & "Te faltan " & (Get_CostoSaltarPalabra - g_iMonedas) _
			& Chr(10) & "Puedes adquirir neuronas en el Shop"
		
		iRet = Msgbox2(sMsg, "MATETE", "Ir al Shop", "Salir", "",Null)
		If iRet = DialogResponse.POSITIVE Then
			V2_PantallaConfigura(xConfiguraPantalla.producto, 0)
		End If
	End If
End Sub

Sub SaltarPalabraPuede (iMon As Int) As Boolean
	Dim bRet As Boolean=False
	If Get_CostoSaltarPalabra <= iMon Or gb_PalabraRejugada Then
		bRet = True
	End If

Return bRet
End Sub

Sub Get_CostoComprarLetras
'hice una funcion porque voy a utilizar distintos costos segun la palabra en la que este, las primeras mas baratas
Dim iRet As Int
iRet = xtNivel.CostoLetra
Return iRet
End Sub

Sub Get_CostoSaltarPalabra As Int
'hice una funcion porque voy a utilizar distintos costos segun la palabra en la que este, las primeras mas baratas
Dim iret As Int
iret = xtNivel.CostoSaltar
Return iret 
End Sub

Sub Get_CostoAyuda As Int
Return xtNivel.CostoAyuda
End Sub

Sub SaltarPalabraHacer
	
	If gb_PalabraRejugada = False Then	
		AdivinoGrabaNivel (False)
	End If
	
	'Publicos.Screenshot(Activity, g_DirGrabable, "SSAnimar.png")
	'Anima_DesplazaScreenshot

	
	v2_MuestraNuevaPalabra(0,False,False)

	If bFinDeJuego = False Then
		V2_AnimaRotateImageview(imgSaltarPalabra, 0, 720, 500)
		V2_AnimaRotateImageview(imgNeuronas, 0, 720, 800)
	End If
	'appfw.eventWithValue("SaltarPalabra", xtPalabra.idPalabra)
	
	
End Sub

Sub Propaganda_Crear
	If gb_EsPremium = False Or Publicos.Get_SoyYo = True Then
		'LEADBOLT
		  adBC.Initialize("664913187") ' banner bottom
		  adInt.Initialize("703320557")' interstitial 
		  adBGris.Initialize("463221194") ' banner bottom alternativo (gris)
 		  adInt.loadAdToCache() 'el interstital lo cachea para tenerlo listo cuando lo tenga que mostrar
		' APPNEXT
		  'appNext.Initialize("appNextAd")
	      'appNext.setAppID("84a1f3de-e6b1-420e-8d43-897e3bff952b")
	 	  'appNext.cacheAd
		'adbuddiz
		'adBuddiz.Initialize("AdBuddizDelegate", "95ce0c18-693f-4fa9-b30f-14624f4116c0", False)
		
		'DisplayIO
		adDispIO.Initialize("Jugar", "5099")
				  	 
	End If
End Sub 

	


'muestra los interstitial
Sub Propaganda_Mostrar

Try

	If gb_EsPremium=False Or Publicos.Get_SoyYo = True Then 
		'BANNER BOTTOM
		adBGris.loadAd()
		'appNext.addMoreGamesLeft("84a1f3de-e6b1-420e-8d43-897e3bff952b")

		'interstitial
		' si recien pasó por el activity_resume, no muestra interstitial
		
		If bAnulaInsterstitialInicial Then
			bAnulaInsterstitialInicial = False
			Return
		End If	
		' si da el rate de propaganda
		'gi_PropagandaCuenta = 2
		'hasta que no jugó una palabra no muestra propaganda
		'gb_propagandaEspera = Not (g_iJugadas>1)
		If g_iJugadas  Mod gi_PropagandaRate =0 Then ' gb_propagandaEspera = False Then
		
		
			' intercala de 0 a 2
			'0 leadbolt - 1 apnext - 2 adbudizz
			Select (True)
			Case (gi_PropagandaCuenta = 0) ' displayIO	
				adDispIO.showAdPlacement("1508")
				
			Case (gi_PropagandaCuenta = 1 ) 'leadbolt
				adInt.loadAd
				adInt.loadAdToCache
				tracker.TrackEvent("ADS", "Show", "Leadbolt",0)
				'appfw.event("Leadbolt")
			
			
			'Case (gi_PropagandaCuenta = 1) 'appnext
				'appNext.showBubble	
				'appNext.cacheAd
				'tracker.TrackEvent("ADS", "Show", "AppNext",0)
			'Case (gi_PropagandaCuenta=2) 'adbuddiz
				'If adBuddiz.isReadyToShowAd Then
				'	adBuddiz.showAd
				'	tracker.TrackEvent("ADS", "Show", "AdBuddiz",0)
				'Else 'si no puede mostrar adbuddiz muestra leadbolt
				'	adBuddiz.cacheAds
				'	adInt.loadAd
				'	adInt.loadAdToCache
				'	tracker.TrackEvent("ADS", "Show", "Leadbolt",0)
				'End If
				'Case (gi_PropagandaCuenta = 3) 'FIREWORKS
				'	FireWorks_Inicio
				'	tracker.TrackEvent("ADS", "Show", "Fireworks",0)
			End Select	

			gi_PropagandaCuenta = gi_PropagandaCuenta +1
			If gi_PropagandaCuenta >= 2 Then 
				gi_PropagandaCuenta = 0
			End If
	
		End If
	End If
Catch
	Log("Error en propaganda")
	
End Try

End Sub

Sub imgPedirLetra_click
	
If ComprarLetraPuede Then	
	'saca foto a la pantalla, para poder animarla
	
	'Publicos.Screenshot(Activity, g_DirGrabable, "SSAnimar.png")

	'If ComprarLetraQuiere Then
'		ComprarLetraHacer	
	'End If
	'appfw.eventWithValue("ComprarLetra", xtPalabra.idPalabra)

	V2_PantallaConfigura(xConfiguraPantalla.ComprarLetra, 0)
	
Else
	Dim sMsg As String, iRet As Int
	sMsg = "No tienes " & Publicos.sNombreMonedas & " suficientes" & Chr(10) & "Te faltan " & (Get_CostoComprarLetras - g_iMonedas) _ 
		& Chr(10) & "Puedes adquirir neuronas en el Shop"
	iRet = Msgbox2(sMsg, "MATETE", "Ir al Shop", "Salir", "",Null)
	If iRet = DialogResponse.POSITIVE Then
		V2_PantallaConfigura(xConfiguraPantalla.producto, 0)
	End If


End If


End Sub

Sub ComprarLetraPuede As Boolean 
	Dim bret As Boolean
	bret = Get_CostoComprarLetras <= g_iMonedas

Return bret
End Sub

Sub ComprarLetraQuiere () As Boolean 
	Dim bRet As Boolean = False, sTit As String, iCostoMonedas As Int, sDescMonedas As String
	iCostoMonedas = Get_CostoComprarLetras
	If iCostoMonedas = 1 Then
		sDescMonedas ="moneda"
	Else
		sDescMonedas = "monedas"
	End If

	sTit = "¿Confirma la compra de una letra?"
	bRet = Msgbox2(sTit,"Costo: "&iCostoMonedas &" "& sDescMonedas , "Comprar", "Cancelar","", Null) = DialogResponse.POSITIVE

	Return bRet
End Sub

Sub Palabra_CargaLetras(sPalabra As String, idPalabra As Int)
'carga las letras de la palabra, marcando las compradas
	
	Dim  sSql As String, Cursor1 As Cursor
	Dim aLetrasPalabra(sPalabra.Length) As tLetrasPalabra
	Dim iPos As Int
Try

	For i = 0 To sPalabra.Length-1
		aLetrasPalabra(i).letra = sPalabra.CharAt(i)
		aLetrasPalabra(i).posicion= i+1
		aLetrasPalabra(i).comprada = False
	Next
	
	'marca las compradas
	sSql ="select posicion from letrascompradas where idpalabra ="&idPalabra
	Cursor1 = g_sqlBaseLocalUsuario.ExecQuery(sSql)
	For j = 0 To Cursor1.RowCount-1
		Cursor1.Position=j
		iPos = Cursor1.GetInt2(0)
		aLetrasPalabra(iPos).comprada = True
	Next
	Cursor1.Close

Catch
	ManejaErrorJugar("Palabras_CargaLetras","Error Palabra_cargaletras")
End Try	
End Sub

Sub Get_LetrasPosicion(sLetra As String) As Int
'devuelve la posicion en que esta una letra buscandola entre las disponibles
Dim iRet As Int = -1
For j= 0 To aLetrasElegir.Length-1
	If aLetrasElegir(j).LetraMovil = aLetrasElegir(j).LetraSiempre Then
		' si es la letra buscada
		If lblArrayLetras(j).Text= sLetra Then
			iRet = j
			Exit
		End If
	End If	
Next
Return iRet
End Sub

Sub ComprarLetraGrabar(iPos As Int, iPosLetras As Int) As Boolean
	Dim sSql As String, bRet As Boolean=False

Try	
	
	g_sqlBaseLocalUsuario.BeginTransaction
	sSql = "Insert Into LetrasCompradas (idpalabra, posicion) Values ("&igIDPalabra & "," & iPos &")"
	g_sqlBaseLocalUsuario.ExecNonQuery(sSql)
	
	sSql ="Update Usuario Set Monedas = monedas -"& Get_CostoComprarLetras & "
	g_sqlBaseLocalUsuario.ExecNonQuery(sSql)
	
	sSql ="Update LetrasPorPalabra set letra ='?', PosicionPalabra = " & iPos & " where idpalabra="&igIDPalabra & " and Posicion=" & iPosLetras
	g_sqlBaseLocalUsuario.ExecNonQuery(sSql)
	

	g_sqlBaseLocalUsuario.TransactionSuccessful
	g_sqlBaseLocalUsuario.EndTransaction
	bRet = True

Catch
	ManejaErrorJugar("ComprarLetrasGrabar","Error ComprarLetrasGrabar")
	g_sqlBaseLocalUsuario.EndTransaction
End Try
Return bRet
End Sub

Sub FinDeJuego
End Sub

Sub LetraPalabraDisponible As Boolean
Dim bret As Boolean = False

For j = 0 To lblArrayPalabra.Length-1
	'og(lblArrayPalabra(j).Text)
	If lblArrayPalabra(j).Text = "" Then
		bret = True
		Exit
	End If
Next

Return bret
End Sub

Sub Get_MensajeAdivino (idPalabra As Int) As String
' elige un mensaje al azar del array de mensajes
Dim  sRet As String, iPos As Int
Dim aMensajes () As String 
aMensajes = Array As String  ("Correcto. " &Chr(10) &"Tu divergencia sigue aumentando", _
			"Acertaste. "&Chr(10)& "Tus engranajes se van aflojando", _
			"Bien. "&Chr(10)& "Ya estás con un pié fuera de la caja" , _
			"Superluper. "&Chr(10)& "Vas quitando herrumbre a tus neuronas", _
			"La pucha."&Chr(10)& "Eres una barbaridad", _
			"Tremendo."&Chr(10)& "Aunque ésta era fácil, pero la próxima te saca una cana", _
			"Revolución."&Chr(10)& "Tus neuronas están como locas" , _
			"Eres suertudo"&Chr(10)& "o tu pensamiento lateral está muy desarrollado", _ 
			"Tu mate te está funcionando muy bien", _ 
			"Tienes el cerebro frito con aceite de lubricar motores", _  
			"Patapufete."&Chr(10)& "Si sigues adivinando se nos van a acabar las palabras", _   
			"Sambomba."&Chr(10)& "Se ha generado un nuevo circuito en tu cerebro", _ 
			"Caramba que vas muy bien. Apostaría que ya puedas descubrir la traducción combinada de TheySmokeDos", _  
			"Sorprendente."&Chr(10)& "Tienes una gran capacidad de abandonar la lógica. Tu pareja tiene razón", _ 
			"Te luciste."&Chr(10)& "Estás cerca de graduarte en pensamiento divergente", _ 
			"Caracoles."&Chr(10)& "Se nota que has practicado en el viaje", _ 
			"Cáspitas."&Chr(10)& "Tu cociente intelectual debería llamarse multiplicador intelectual", _   
			"Impactante."&Chr(10)& "Debes tener un procesador de cuatro núcleos", _ 
			"Chanfle."&Chr(10)& "Ya te mereces tu propia estatua del pensador", _   
			"Excelente."&Chr(10)& "Te ganaste un premio Eric de aplausos y campanadas"&Chr(10)& "Clap ton Clap ton", _  
			"Yuuujuuu."&Chr(10)& "Eres el primero en acertar esta palabra a esta hora en este lugar."&Chr(10)& "Y con esas medias!", _ 
			"En breve tu cabeza quedará chica para la expansión que está sufriendo tu cerebro", _ 
			"Boing boing."&Chr(10)& "Tus neuronas están dando saltos increíbles", _  
			"Cáspitas."&Chr(10)& "Eso fue muy rápido. Espero que estés jugando limpio. Sino ve a asearte", _   
			"Soberbio."&Chr(10)& "El pitecantropus estaría impresionado con tu evolución", _ 
			"Lativa te queda chica."&Chr(10)& "Tu performance es superlativa", _ 
			"Fantástico."&Chr(10)& "Ya se debe estar gestando tu propio club de fans", _ 
			"Tu capacidad de resolución se está volviendo infinita", _ 
			"Fenómeno."&Chr(10)& "Evidentemente tienes un ecosistema cerebral hiper desarrollado", _ 
			"Impresionante lo que has logrado este último tiempo", _ 
			"Recórcholis."&Chr(10)& "En cualquier momento empiezas a inventar tus propios matetes", _ 
			"Piedra libre."&Chr(10)& "El orden ayuda a encontrar y el caos a descubrir."&Chr(10)& "Si, si, te ha ayudado el caos", _ 
			"Te mereces un anterior a lo que me pertenece."&Chr(10)& "Claro Claro: Un premio", _ 
			"Patapufete."&Chr(10)& "Tu tienes habilidad para salir de la caja pero por favor cierra la tapa para que no se escapen otros", _ 
			"Caramba."&Chr(10)& "Tus habilidades matetísticas dejan paticonfuso a cualquiera", _ 
			"Tus neuronas están decididas a abandonar el blanco y negro", _ 
			"Matete está provocando una migración masiva de neuronas. La lógica convencional ya no tiene espacio en tu cerebro", _ 
			"Zás!"& Chr(10) &"Matete está reconfigurando tus neuronas", _ 
			"Upalalá"& Chr(10) &"La parte colorida de tu cerebro parece un arcoiris", _ 
			"Santos Matetes Batman, este jugador podría ayudarnos a derrotar al Acertijo", _ 
			"Carambolas" & Chr(10)& "Resuelves muy bien los Matetes, podrías dedicarte a...." & Chr(10) & "Lo siento, es una habilidad completamente inútil", _
			"Con tanto Matete ¿te parece que empiezas a sentir el movimiento de tus neuronas?" & Chr(10) & "Es extraño. Posiblemente tengas piojos", _
			"El ejercicio mental te rejuvenece. Cada matete resuelto te hace ver 39 segundos más joven.", _ 
			"Deberían nominarte para el premio intergaláctico de Matetes. Si existiera, claro.", _ 
			"Santos tragos barman!" & Chr(10) & "A algunas personas Matete les hace crecer tanto las neuronas que le empujan el pelo hacia afuera y las confunden con el tío cosa.", _ 
			"Resolviste hábilmente este Matete, pero tengo que advertirte que el próximo produce insomnio.", _ 
			"Esferas de Caram!" & Chr(10) & "Logras sorprenderme con tus habilidades." & Chr(10) & " Bah, no es cierto, pero te quería levantar el ánimo.", _ 
			"A la little ball." & Chr(10) & "Hubiera jurado que esta la salteabas, me monja-encendiste." , _
			"Si pudiste resolver este Matete, el problemita que te está dando vueltas en la cabeza es pan comido.", _ 
			"Otro Matete resuelto es otro pequeño puente entre tus hemisferios cerebrales.", _ 
			"A la pipetuá! Deberían inventar un superheroe en tu nombre, algo así como Supermantete.", _ 
			"A esta altura del juego es posible que te descubras analizando tooodas las palabras buscando Matetes. El diagnóstico es Matetitis.", _ 
			"Acertaste y te ganaste un consejo: No pidas peras al olmo. Son mucho más ricas al borgoña.", _ 
			"Uii Uii."& Chr(10) &"Estás haciendo rechinar la estantería de tu pensamiento lógico.", _ 
			"Cataplún! " & Chr(10) & "Lógica lógica derrumbada. Bienvenida lógica ilógica.")
			
			
Try
	'iRnd = Rnd(0, aMensajes.Length-1)
	iPos= idPalabra Mod aMensajes.Length
	sRet = aMensajes(iPos)
Catch
	sRet = "Correcto. "&Chr(10)& "Tu divergencia sigue aumentando"
End Try
Return sRet

End Sub

Sub Get_MonedasPorNivel As Int
'Dim sSql As String, Cursor2 As Cursor
Dim iRet As Int =10

Try

'	sSql = "select ifnull(monedaspornivel,10) from seteos"
'	Cursor2=BaseJuego.ExecQuery(sSql)
'	If Cursor2.RowCount >0 Then
'		Cursor2.Position=0
'		iRet = Cursor2.GetInt2(0)
''	End If
'	Cursor2.close

iRet = xtPalabra.palabra.Length

Catch
	ManejaErrorJugar ("Get_MonedasxNivel", "Error Seteos")

End Try
Return iRet

End Sub

Sub LetrasCarga (apLetrasPalabra() As tLetrasPalabra) As String
	Dim sSql As String, CursorL As Cursor
	Dim sLetras As String="", bInventar As Boolean = True
	'utiliza este array para tener siempre guardadas las 12 letras elegidas al azar
  	Dim aLetrasElegir(12) As tLetrasElegir, sLetrasSiempre As String
	
Try
	sSql = "Select Letra, Letrasiempre From LetrasPorPalabra  where idpalabra = " & igIDPalabra & " order by posicion"
	CursorL = g_sqlBaseLocalUsuario.ExecQuery(sSql)
	If CursorL.RowCount>0  Then
		For j = 0 To CursorL.RowCount-1
			CursorL.Position=j
			sLetras = sLetras & CursorL.getstring2(0)	
			
			aLetrasElegir(j).LetraMovil = CursorL.getstring2(0)
			aLetrasElegir(j).LetraSiempre = CursorL.getstring2(1)
			aLetrasElegir(j).LetraEnLabel = aLetrasElegir(j).LetraSiempre
		Next 
		'verifica si las letras grabadas estan ok con la palabra, sino fuerza que las vuelva a inventar
		'es una porqueria, pero algunas veces falla y deja grabadas letras que no cierran con la palabra que esta jugando y no pude encontrar el bug
		bInventar = Not (Letras_Check(aLetrasElegir, apLetrasPalabra))
		
	End If
	CursorL.Close

	If bInventar Then ' no estaban grabadas, es la primera vez que carga las letras, genera al azar y graba
		sLetras = ""
		For j = 0 To apLetrasPalabra.Length-1
			If apLetrasPalabra(j).comprada Then 
				sLetras = sLetras & "?"
			Else 	
				sLetras=sLetras& apLetrasPalabra(j).letra
			End If
			
			'inventa las letras que faltan y desordena la palabra
		Next 
		sLetras = LetrasInventa(sLetras)
		LetrasGraba(sLetras, igIDPalabra)
	End If
	
	For j=0 To sLetras.Length-1
		aLetrasElegir(j).LetraMovil= sLetras.SubString2(j,j+1)
		aLetrasElegir(j).LetraSiempre = sLetras.SubString2(j,j+1)
		aLetrasElegir(j).LetraEnLabel = sLetras.SubString2(j,j+1)
	Next 
	
Catch
		ManejaErrorJugar("Letras Carga", "Cargando letras")
		
End Try
Return sLetras
End Sub
'Letras_Check verifica que las letras del array aletraselegir esten ok con las letras de la palabra (algunas veces falla)
'devuelve true si estan ok, false si falta alguna letra de la palabra, es decir si con las letras de aletraselegir no se puede formar la palabra
Sub Letras_Check(paLetrasElegir() As tLetrasElegir, papLetrasPalabra() As tLetrasPalabra) As Boolean
Dim aLetras(paLetrasElegir.Length) As tLetrasElegir 
Dim bOk As Boolean
aLetras = paLetrasElegir

'por cada letra de la palabra, busca si esta en apletraspalabra que es aletras
For j = 0 To papLetrasPalabra.Length-1
	For i = 0 To aLetras.Length-1
		If papLetrasPalabra(j).letra = aLetras(i).LetraSiempre Then
			aLetras(i).letrasiempre = "" ' borra la letra de las disponibles
			bOk = True ' 
			Exit 
		End If
		' si no encontró una letra, devuelve falso
	Next
	If bOk = False Then
		Return False
	End If
Next 
Return True
End Sub

Sub LetrasGraba(sLetras As String, idPalabra As Int)
Dim sSql As String

Try
	'borra las palabas x si habia
	sSql="Delete from LetrasPorPalabra where idpalabra=" & idPalabra
	g_sqlBaseLocalUsuario.ExecNonQuery(sSql)
	
	g_sqlBaseLocalUsuario.BeginTransaction
	For j =0 To sLetras.Length-1
		sSql = "Insert Into LetrasPorPalabra (idPalabra, Posicion, Letra, LetraSiempre, PosicionPalabra) values (" & idPalabra & "," & j & ",'"
		sSql = sSql & sLetras.SubString2(j, j+1) & "','" & sLetras.SubString2(j, j+1) & "', -1)"
		
		g_sqlBaseLocalUsuario.ExecNonQuery(sSql)
	Next 
	g_sqlBaseLocalUsuario.TransactionSuccessful
	g_sqlBaseLocalUsuario.EndTransaction

Catch
	ManejaErrorJugar("LetrasGraba","")
	g_sqlBaseLocalUsuario.EndTransaction
End Try


End Sub

Sub Get_PosicionEnPalabra(lblPalabra As Label) As Int
Dim iRet As Int=-1
' busca la posicion por tag
	For j = 0 To lblArrayLetras.Length-1
		If lblArrayLetras(j).Tag = lblPalabra.tag Then
			If lblArrayLetras(j).Text<>"" Then
				iRet = j
			End If
			Exit
		End If
	Next 
Return iRet
End Sub

'Sub Leadbolt_adCompleted
Sub Leadbolt_adFailed
	
End Sub

Sub Leadbolt_adLoaded
	
End Sub
'Sub Leadbolt_AdCached 
'sub Leadbolt_AdClicked

Sub Leadbolt_adClosed
	If gb_EsPremium = False Then
		adBC.loadAd
	End If
End Sub

Sub Get_ComprarLetraAzar As Int
'elige una letra al azar para comprar, devuelve la posicion en el array de letraspalabra
Dim iAux As Int =0, iLetraComprada As Int, iPos As Int, ailetrasnocompradas(aLetrasPalabra.Length) As Int
	'guarda en un array todas las letras que no estan compradas
	For j = 0 To aLetrasPalabra.Length-1
		If aLetrasPalabra(j).comprada = False Then
			iAux = iAux +1
			ailetrasnocompradas(iAux-1) = j
		End If	
	Next
	iPos = Rnd(0,iAux)
	iLetraComprada = ailetrasnocompradas(iPos)
Return iLetraComprada
End Sub

Sub ComprarLetraHacer 
'compra de una letra. 
	Dim  iPosLetras As Int,  iLetraEnPalabra As Int
	Dim iComprar As Int
	Dim iPosActualLetraComprarEnPalabra As Int, sLetraAComprar As String



'elige al azar la letra a comprar
	iComprar =Get_ComprarLetraAzar 
	'si la palabra tiene la posicion elegida ocupada, la desocupa
	If lblArrayPalabra(iComprar).Text <>"" Then
		lblPalabraClick (lblArrayPalabra(iComprar)) 
	End If

'busco entre las letras disponibles (no pasadas a la palabra) 
	sLetraAComprar = aLetrasPalabra(iComprar).letra
	iPosLetras = Get_LetrasPosicion(sLetraAComprar)
'si no la encontro, es porque esta elegida en la palabra, la tengo que ubicar y devolver a su posicion para poder encontrarla
	If iPosLetras = -1 Then 
		iPosActualLetraComprarEnPalabra = get_LetraEnPalabra(sLetraAComprar)
		lblPalabraClick (lblArrayPalabra(iPosActualLetraComprarEnPalabra)) ' la devuelve a lblletras desde lblpalabra
	'ahora que fue devuelta, la vuelve a buscar
		iPosLetras = Get_LetrasPosicion(sLetraAComprar)
		
	End If	
	
	If ComprarLetraGrabar (iComprar, iPosLetras) Then
		Neuronas_Mostrar
		EligeUnaLetra (lblArrayLetras(iPosLetras), True, iComprar, iPosLetras)
	End If	


	'saca una foto de la pantalla, la pone en primer plano y la rota
	Activity.Invalidate
	DoEvents
	'Publicos.Screenshot(Activity, g_DirGrabable, "SSAnimar.png")
	'V2_RotaPantalla 
	V2_AnimaRotateImageview(imgPedirLetra, 0, 720, 500)
	V2_AnimaRotateImageview(imgNeuronas, 0, 720, 800)



End Sub

Sub get_LetraEnPalabra (sLetra As String) As Int
Dim iRet As Int = -1
'devuelve la primera posicion donde encuentra sLetra en los label de la palabra (que no este comprada)
For j =0 To aLetrasPalabra.Length-1
	If aLetrasPalabra(j).comprada = False Then
		If lblArrayPalabra(j).Text = sLetra Then
			iRet = j
			Exit
		End If
	End If
Next
Return iRet
End Sub

Sub Get_AnchoLetra As Int
	Dim iRet As Int

	'iRet = Panel5.Height * 0.23
	'If iRet > Panel5.Width /11.5 Then
	iRet = Panel5.Width /9
	'End If	

	Return iRet
End Sub
Sub Get_AnchoLetraPalabra(iLargo As Int) As Int
	Dim iRet As Int

	'iRet = Panel5.Height * 0.23
	'If iRet > Panel5.Width /11.5 Then
	If iLargo <10 Then
		iRet = Activity.Width /11
	Else
		iRet = Activity.Width /12.2
	End If

	Return iRet
End Sub

Sub Get_SeparaLetra As Int
	Dim iRet As Int
	iRet = 2dip
	Return iRet
End Sub

Sub Get_InicioLetras (iAncho As Int, ipCantLetras As Int) As Int
Dim iRet As Int
 iRet = Activity.Width / 2 - (Get_SeparaLetra+iAncho) * (ipCantLetras/2)
Return iRet
End Sub

Sub Get_NivelPrimeraPalabra(idNivel As Int) As Int
'devuelve el idPalabra de la primera palabra del nivel idNivel 
Dim sSql As String, crCursor1 As Cursor, iRet As Int=-1
Try
	sSql ="Select ifnull(min(idPalabra),-1) idPalabra from Palabras where idNivel = "& idNivel
	crCursor1 = g_sqlBaseLocalJuego.ExecQuery(sSql)
	If crCursor1.RowCount >0 Then
		crCursor1.Position = 0
		iRet = 	crCursor1.GetInt2(0)
	End If
	crCursor1.close
Catch
	ManejaErrorJugar("Get_NivelPrimeraLetra", LastException.Message)
End Try
Return iRet
End Sub

Sub Get_NivelUltimaPalabra(idNivel As Int) As Int
'devuelve el idPalabra de la primera palabra del nivel idNivel 
Dim sSql As String, crCursor1 As Cursor, iRet As Int=-1
Try
	sSql ="Select ifnull(max(idPalabra),-1) idPalabra from Palabras where idNivel = "& idNivel
	crCursor1 = g_sqlBaseLocalJuego.ExecQuery(sSql)
	If crCursor1.RowCount >0 Then
		crCursor1.Position = 0
		iRet = 	crCursor1.GetInt2(0)
	End If
	crCursor1.close
Catch
	ManejaErrorJugar("Get_NivelPrimeraLetra", LastException.Message)
End Try
Return iRet
End Sub

Sub Sonido(iSonido As Int)
If bSonidoPrendido = False Then
	Return
End If
Select iSonido
	Case SONIDO_ADIVINO
		sound_Adivino.Play(iSound_Adivino,1,1,1,1,1)		
	Case SONIDO_ERROR
		sound_Error.Play(iSound_Error,1,1,1,1,1)
	Case SONIDO_NUEVAPALABRA
		sound_NuevaPalabra.Play(iSound_NuevaPalabra,1,1,1,1,1)
End Select

End Sub

Sub SonidosInicia
	sound_Adivino.Initialize(1)
	iSound_Adivino = sound_Adivino.Load(File.DirAssets,"adivino.wav")
	sound_Error.Initialize(1)
	iSound_Error = sound_Error.Load(File.DirAssets, "chan.wav")
	sound_NuevaPalabra.Initialize(1)
	iSound_NuevaPalabra = sound_NuevaPalabra.Load(File.DirAssets, "nuevapalabra.wav")

	If Publicos.Get_SeteoUsuarioEnteroDesde(g_sqlBaseLocalUsuario, "Sonido") = 1  Then
		bSonidoPrendido = True
	Else 
		bSonidoPrendido = False
	End If
End Sub

Sub Get_LetrasPalabraFb As String
'arma un string con la palabra como la ve el jugador, rellenando solo las letras que tiene compradas
Dim sRet As String
For j = 0 To aLetrasPalabra.Length -1
	If aLetrasPalabra(j).comprada Then
		sRet = sRet & aLetrasPalabra(j).letra
	Else
		sRet = sRet & "_"
	End If
Next 
Return sRet	
End Sub

Sub Get_LetrasElegirFb As String
Dim sRet As String = ""
For j = 0 To sgLetras.Length-1
	If sgLetras.SubString2(j,j+1) = "?" Then
		sRet = sRet & " "
	Else
		sRet = sRet & sgLetras.SubString2(j,j+1)
	End If
Next
Return sRet
End Sub

Sub img51Twitter_Click
Dim bRet As Int
	If Publicos.twitter_getuso(g_sqlBaseLocalUsuario) = 0 Then
		StartActivity("Twitter")
		Twitter.sDefinicion = xtPalabra.Descripcion
		Twitter.sLetrasElegir = Get_LetrasElegirFb
		Twitter.sPalabra = xtPalabra.Palabra
		Twitter.iCostoLetra = xtNivel.CostoLetra
		Twitter.sLetrasPalabra = Get_LetrasPalabraFb
		Twitter.g_sqlBaseLocalUsuario = g_sqlBaseLocalUsuario
	Else
		ToastMessageShow("Ya publicaste esta palabra en Twitter!", True)	
	End If

End Sub

Sub V2_SetPantalla
	Publicos.ViewComoActivity(Activity, pnlInvisible)
	pnlInvisible.Visible = False
	
'paneles
	Panel1.Top = 0

	' si es premium aprovecha toda la pantalla
	If gb_EsPremium Then 
		Panel1.Height = Activity.Height * 0.14 'Panel que tiene la info de estado, boton de menu, etc
		Panel2.Top = Panel1.Top + Panel1.Height
		Panel2.Height = Activity.Height* 0.25 ' panel que muestra la definición
		Panel3.Top = Panel2.Top + Panel2.Height ' panel que muestra botones comprar letra y saltar palabra
		Panel3.Height = Activity.Height* 0.11
		Panel4.Top = Panel3.Top + Panel3.Height ' panel que muestra palabra (espacios para completarla)
		Panel4.Height = Activity.Height* 0.11
		Panel5.Top = Panel4.Top + Panel4.Height ' panel que muestra las letras que se pueden elegir
		Panel5.Height = Activity.Height* 0.28
		Panel6.Top = Panel5.Top + Panel5.Height ' panel que muestra boton compartir / cancelar/confirmar
		Panel6.Height = Activity.Height* 0.11
	Else
		Panel1.Height = Activity.Height * 0.14 'Panel que tiene la info de estado, boton de menu, etc
		Panel2.Top = Panel1.Top + Panel1.Height
		Panel2.Height = Activity.Height* 0.21 ' panel que muestra la definición
		Panel3.Top = Panel2.Top + Panel2.Height ' panel que muestra botones comprar letra y saltar palabra
		Panel3.Height = Activity.Height* 0.1
		Panel4.Top = Panel3.Top + Panel3.Height ' panel que muestra palabra (espacios para completarla)
		Panel4.Height = Activity.Height* 0.1
		Panel5.Top = Panel4.Top + Panel4.Height ' panel que muestra las letras que se pueden elegir
		Panel5.Height = Activity.Height* 0.25
		Panel6.Top = Panel5.Top + Panel5.Height ' panel que muestra boton compartir / cancelar/confirmar
		Panel6.Height = Activity.Height* 0.1
	End If
	Panel1.Left = 0: Panel2.left = 0 : Panel3.left = 0 : Panel4.Left=0 : Panel5.Left = 0: Panel6.Left = 0
	Panel1.width = Activity.Width: Panel2.width = Activity.Width: Panel3.width = Activity.Width
	Panel4.width = Activity.Width: Panel5.width = Activity.Width: Panel6.width = Activity.Width

	

''PANEL1
	'IZQUIERDA
	imgMenu.Height = Panel1.height*0.4
	imgMenu.Width = imgMenu.Height
	imgMenu.left = Panel1.Width * 0.05
	imgMenu.Top = Panel1.Width * 0.05

	imgAvance.Left = imgMenu.Left
	imgAvance.Top = imgMenu.Top + imgMenu.Height + 2dip
	imgAvance.Height = imgMenu.Height
	imgAvance.Width = imgAvance.Height /2

	lblAvance.Left = imgAvance.Left + imgAvance.Width 
	lblAvance.Width = imgMenu.Width * 3
	lblAvance.Height = Panel1.Height * 0.2
	lblAvance.Top = imgAvance.Top + lblAvance.Height / 2
	lblAvance.Gravity = Gravity.LEFT + Gravity.CENTER_VERTICAL
	Publicos.SetLabelTextSize(lblAvance, "999/999", 20,5, 100)
	
	lblAvance.BringToFront

	'CENTRO
	lblV2Nivel.Typeface = tFontOpenSansSemiBold
	lblV2Nivel.height = Panel1.Height*0.4
	lblV2Nivel.Width = Activity.Width
	lblV2Nivel.Top = imgNeuronas.Top 
	lblV2Nivel.Gravity = Gravity.CENTER
	lblV2Nivel.Text = "MATETE DIVERGENTE" & Chr(10) & "NIVEL MUY LARGO QUE OCUPA"
	Publicos.SetLabelTextSize(lblV2Nivel, lblV2Nivel.TEXT, 20,5, 100)
	lblV2Nivel.Left = 0 

	'DERECHA
	imgNeuronas.Height = Panel1.Height*0.4
	imgNeuronas.Width = imgNeuronas.Height
	imgNeuronas.Left = Panel1.Width - imgNeuronas.Width - Panel1.width * 0.05
	imgNeuronas.Top = Panel1.Width * 0.05
	lblNeuronas.Left = imgNeuronas.Left-imgNeuronas.Width*0.1
	lblNeuronas.Width = imgNeuronas.Width*1.2
	lblNeuronas.Top = imgNeuronas.Top + imgNeuronas.Height + 2dip
	lblNeuronas.Height = Panel1.Height * 0.2
	lblNeuronas.Text = "99999"
	lblNeuronas.Typeface = tFontOpenSansSemiBold
	lblNeuronas.textsize = lblAvance.textsize
	'Publicos.SetLabelTextSize(lblNeuronas, "9999", 30,5, 100)
	lblNeuronas.Gravity = Gravity.CENTER_HORIZONTAL + Gravity.TOP



'panel2
	
	lblv2Def.Top = 0: lblv2Def.Height = Panel2.Height 
	lblv2Def.Width = Panel2.Width*0.8
	lblv2Def.Left = Panel2.Width * 0.1
	'lblv2Def.Color = Colors.gray
	lblv2Def.Typeface = tFontOpenSansLight
	'lblV2Def.mlbl.TextSize = 28
	lblv2Def.TextColor =Colors.black
	lblv2Def.Gravity = Gravity.CENTER

'panel3 
	imgPedirLetra.Height=Panel6.Height*0.5
	imgPedirLetra.Width=imgPedirLetra.Height
	imgPedirLetra.Top= (Panel3.Height - imgPedirLetra.Height)/2
	imgPedirLetra.left = Panel3.Width * 0.05

	lblPedirLetraCosto.Height = imgPedirLetra.Height*0.6
	lblPedirLetraCosto.Width = lblPedirLetraCosto.Height
	lblPedirLetraCosto.Top = imgPedirLetra.Top - imgPedirLetra.Top*0.2
	lblPedirLetraCosto.Left = imgPedirLetra.Left - lblPedirLetraCosto.Width / 2
	lblPedirLetraCosto.TextColor = lblv2Def.textcolor
	lblPedirLetraCosto.Typeface = tFontOpenSansSemiBold
	lblPedirLetraCosto.Gravity = Gravity.CENTER
	
	lblPedirLetra.Left = imgPedirLetra.Left + imgPedirLetra.Width + 3dip
	lblPedirLetra.Height = imgPedirLetra.Height / 1.6
	lblPedirLetra.Top = imgPedirLetra.Top + (imgPedirLetra.Height/2) - (lblPedirLetra.Height / 2)
	lblPedirLetra.Width = Activity.Width /3.5
	lblPedirLetra.Text = "COMPRAR LETRA"
	lblPedirLetra.Typeface = tFontOpenSansSemiBold
	'Publicos.SetLabelTextSize(lblPedirLetra, lblPedirLetra.text, 30,5, 100)


	lblPedirLetra.Gravity = Gravity.CENTER_VERTICAL + Gravity.LEFT
	lblPedirLetra.TextColor = lblv2Def.textcolor

	imgSaltarPalabra.Height=imgPedirLetra.Height
	imgSaltarPalabra.Width=imgSaltarPalabra.Height
	imgSaltarPalabra.Top= (Panel3.Height - imgPedirLetra.Height)/2
	imgSaltarPalabra.left = Panel3.Width - imgPedirLetra.left - imgSaltarPalabra.Width
	
	lblSaltarPalabraCosto.Height = imgSaltarPalabra.Height * 0.6
	lblSaltarPalabraCosto.Width =lblSaltarPalabraCosto.Height
	lblSaltarPalabraCosto.Top = imgSaltarPalabra.Top - imgSaltarPalabra.Top*0.2
	lblSaltarPalabraCosto.Left = imgSaltarPalabra.Left + imgSaltarPalabra.Width-lblSaltarPalabraCosto.Width/2
	lblSaltarPalabraCosto.TextColor = lblv2Def.textcolor
	lblSaltarPalabraCosto.Typeface = tFontOpenSansSemiBold
	lblSaltarPalabraCosto.Gravity = Gravity.CENTER
		
	lblSaltarPalabra.Height = lblPedirLetra.Height
	lblSaltarPalabra.Width = lblPedirLetra.width
	lblSaltarPalabra.Left = imgSaltarPalabra.Left - lblSaltarPalabra.Width - 3DIP
	lblSaltarPalabra.Text = "SALTAR MATETE"
	lblSaltarPalabra.Gravity = Gravity.CENTER_VERTICAL + Gravity.RIGHT
	lblSaltarPalabra.Top = lblPedirLetra.Top
	lblSaltarPalabra.Typeface = tFontOpenSansSemiBold
	'Publicos.SetLabelTextSize(lblSaltarPalabra, lblSaltarPalabra.text, 30,5, 100)
	lblSaltarPalabra.TextColor = lblv2Def.textcolor
	

''panel 4 palabras 

'Panel5 letras )

'panel6
	imgCompartir.height = Panel6.Height*0.5
	imgCompartir.Width = imgCompartir.height
	imgCompartir.Top = (Panel6.Height -  imgCompartir.height)/2
	imgCompartir.Left = Panel6.Width * 0.05

	lblCompartir.Left = imgCompartir.Left + imgCompartir.Width + 3dip
	lblCompartir.Height = lblPedirLetra.Height
	lblCompartir.Top = imgCompartir.Top + (imgCompartir.Height/2) - (lblCompartir.Height / 2)
	lblCompartir.Width = lblPedirLetra.width
	lblCompartir.Text = "COMPARTIR"
	lblCompartir.Typeface = tFontOpenSansSemiBold
	'Publicos.SetLabelTextSize(lblCompartir, lblCompartir.text, 30,5, 100)
	lblCompartir.Gravity = Gravity.CENTER_VERTICAL + Gravity.LEFT
	lblCompartir.TextColor = lblv2Def.textcolor

	imgBajarLetras.Height=imgCompartir.Height
	imgBajarLetras.Width=imgCompartir.Height
	imgBajarLetras.Top= (Panel3.Height - imgCompartir.Height)/2
	imgBajarLetras.left = Panel3.Width - imgCompartir.left - imgCompartir.Width
	
	
	lblBajarLetras.Height = lblCompartir.Height
	lblBajarLetras.Width = lblPedirLetra.width
	lblBajarLetras.Left = imgBajarLetras.Left - lblBajarLetras.Width - 3DIP
	lblBajarLetras.Text = "VACIAR PALABRA"
	lblBajarLetras.Gravity = Gravity.CENTER_VERTICAL + Gravity.RIGHT
	lblBajarLetras.Top = lblCompartir.Top
	lblBajarLetras.Typeface = tFontOpenSansSemiBold
	'Publicos.SetLabelTextSize(lblSaltarPalabra, lblSaltarPalabra.text, 30,5, 100)
	lblBajarLetras.TextColor = lblCompartir.textcolor

'	btnSonido.Visible = False
	'btnSonido.height = Panel6.Height*0.5
	''btnSonido.Width = btnSonido.Height
	'btnSonido.Top = (Panel6.Height -  imgCompartir.height)/2
	'btnSonido.Left = Panel6.Width - imgCompartir.Left - btnSonido.width

' Setea Paneles duplicados (11, 21,41,51,61)

	Publicos.viewigual(Panel1, Panel11)
	Panel11.Height = Panel6.Height
	lbl11.Height = Panel11.Height * 0.6
	lbl11.Width = Panel11.Width
	lbl11.Typeface = tFontOpenSansSemiBold

	Publicos.CentrarControlEnPanel(Panel11, lbl11, True, True)


	Publicos.ViewIgual(Panel2, Panel21) 
	
	Publicos.CentrarControlEnPanel(Panel21, lbl21, True, True)
	imgAnimacion.Height = Panel21.Height * 0.6
	imgAnimacion.Width = imgAnimacion.Height
	imgAnimacion.Visible = False
	Publicos.CentrarControlEnPanel(Panel21, imgAnimacion, True, True)
	lbl21.Top = imgAnimacion.Top + imgAnimacion.Height + 2dip
	lbl21.Height = Panel21.Height * 0.2
	lbl21.Width = Panel21.Width*0.8
	lbl21.Left = Panel21.Width * 0.1
	lbl21.Gravity = Gravity.CENTER
	
	Publicos.ViewIgual (Panel4, Panel41) 
	lbl41.Height = Panel4.Height * 0.5
	lbl41.width = Panel4.Width*0.9
	Publicos.CentrarControlEnPanel (Panel4, lbl41, True, True)
	lbl41.Gravity = Gravity.CENTER
	lbl41.textcolor= gt_Color.ColorMedio
	img51Facebook.height = Activity.Height * 0.1
	img51Facebook.Width = img51Facebook.Height
	img51Twitter.height= img51Facebook.Height
	img51Twitter.width  = img51Twitter.Height

	Publicos.ViewIgual (Panel5, Panel51)

	img51.height = Activity.Height * 0.1
	img51.Width = img51.Height
	Publicos.CentrarControlEnPanel(Panel51, img51, True, True)

	lbl51.Top = img51.Top + img51.Height + 1dip
	lbl51.height = img51.Height*0.3
	lbl51.Width = Panel51.Width *0.7
	lbl51.TextColor = Colors.White
	lbl51.Gravity = Gravity.CENTER
	Publicos.CentrarControlEnPanel(Panel51, lbl51, True, False)

	lbl51MensajeMatete.Width = Panel51.width * 0.9
	lbl51MensajeMatete.Height = Panel51.Height * 0.9
	lbl51MensajeMatete.textcolor = Colors.white
	lbl51MensajeMatete.Typeface = tFontOpenSansLight
	Publicos.CentrarControlEnPanel(Panel51, lbl51MensajeMatete, True, True)

	img51Facebook.height = Activity.Height * 0.1
	img51Facebook.Width = img51Facebook.Height
	img51Twitter.height= img51Facebook.Height
	img51Twitter.width  = img51Twitter.Height
	Dim aCompartir() As View
	aCompartir = Array As View (  img51Facebook, img51Twitter) 	
	Publicos.CentrarArrayControlesEnPanel ( Panel51, aCompartir, True)
	
	lbl51Facebook.Top = img51Facebook.Top + img51Facebook.Height + 1dip
	lbl51Facebook.height = img51Facebook.Height*0.3
	lbl51Facebook.Width = Panel51.Width *0.3
	lbl51Facebook.TextColor = Colors.white
	lbl51Facebook.Text = "EN FACEBOOK"
	'Publicos.SetLabelTextSize(lbl51Facebook, lbl51Facebook.Text, 20,5, 100)
	lbl51Facebook.Left = Publicos.ViewAlinearCentro(img51Facebook, lbl51Facebook)	

	lbl51Twitter.Top = img51Twitter.Top + img51Twitter.Height + 1dip
	lbl51Twitter.height = img51Twitter.Height*0.3
	lbl51Twitter.Width = Panel51.Width *0.3
	lbl51Twitter.TextColor = Colors.white
	lbl51Twitter.Text = "EN TWITTER"
	'Publicos.SetLabelTextSize(lbl51Twitter, lbl51Twitter.Text, 20,5, 100)
	lbl51Twitter.Left = Publicos.ViewAlinearCentro(img51Twitter, lbl51Twitter)	



	Publicos.ViewIgual (Panel6, Panel61)
	lbl61.Height = Panel6.Height * 0.6
	lbl61.Width = Panel6.Width
	Publicos.CentrarControlEnPanel(Panel6, lbl61, True, True)
	'Publicos.SetLabelTextSize ( lbl61, "CONTINUAR >", 40,6, 80)
	lbl61.Gravity = Gravity.CENTER
	lbl61.TextColor = lblv2Def.textcolor

	Panel21.Visible = False: Panel41.Visible = False: Panel51.Visible = False: Panel61.Visible = False
	Panel11.visible = False
	'lbl21.textcolor = Colors.Black
	'lbl41.Textcolor = Colors.Black
	
'setea variable para tamaño de texto labels que son parecidos
	'TAMAÑO CHICO, LOS LABLES QUE ACOMPAÑAN IMAGENES
	Dim setTextSizeLblsCompartirCancelar As Int
	Publicos.SetLabelTextSize(lblPedirLetra , "SALTAR MATETE", 30,10,100)
	setTextSizeLblsCompartirCancelar = lblPedirLetra.TextSize
	lblSaltarPalabra.TextSize = setTextSizeLblsCompartirCancelar
	lbl51Facebook.TextSize = setTextSizeLblsCompartirCancelar
	lbl51Twitter.TextSize = setTextSizeLblsCompartirCancelar
	lblCompartir.TextSize = setTextSizeLblsCompartirCancelar
	lbl51.TextSize = setTextSizeLblsCompartirCancelar
	lblBajarLetras.TextSize = setTextSizeLblsCompartirCancelar

	
	'TAMAÑO MEDIO, LABELS COMO CANCELAR Y COMPARTIR
	Publicos.SetLabelTextSize(lbl41 , "COMPARTIR", 30,10,100)
	setTextSizeLblsCompartirCancelar = lbl41.TextSize
	lbl61.TextSize = setTextSizeLblsCompartirCancelar 
	lbl11.TextSize = setTextSizeLblsCompartirCancelar 

''panel de historia de matetes jugados
	pnlHistoria.left = 0 
	pnlHistoria.Top = lbl11.Top + lbl11.height
	pnlHistoria.width = 100%x 
	pnlHistoria.Height = Activity.Height - (lbl11.Top + lbl11.Height) - (Activity.Height -Panel61.top)
	pnlHistoria.Color = Colors.white' iColor(0)

	pnlHistFiltro.Width = pnlHistoria.Width
	pnlHistFiltro.Height = Activity.Height*0.1
	lblFiltro.Width = pnlHistFiltro.Width 
	lblFiltro.Height = pnlHistFiltro.Height
	lblFiltro.Tag = "SALTADOS"
	lblFiltro.Text = "SALTADOS"
	lblFiltro.Gravity = Gravity.CENTER
	
	
	'configura la pantalla para Jugar
	V2_PantallaConfigura(xConfiguraPantalla.Jugar,0)




	
	ScrollViewGenera 

End Sub

Sub V2_GeneraLabelsLetras ( apLetrasPalabra() As tLetrasPalabra)
'' genera un array de labels para contener las letras de la palabra

	'total 60+5+2
	Dim iAncho As Int, iAlto As Int, iSeparacion As Int,  sLetrasPalabra As String=""
	Dim lblsList As List, iLugar As Int, lbls(iCantLetras) As Label, iDesplaza As Int
	Dim iTop1 As Int ,  iTop2 As Int, iTextSize As Int
	
	Panel5.RemoveAllViews
	lblsList.Initialize
 	
	'Carga las 12 letras de la base, si no estan grabadas inventa las que faltan.
	sgLetras = LetrasCarga(apLetrasPalabra)

	'la distribucion a lo alto del panel es la siguiente
	'letras 30%+30%
	'arriba 15%
	'abajo 15%
	'separacion 10%
	
	iAncho = Get_AnchoLetra
	iAlto=iAncho 
	iSeparacion = iAncho*0.1 
	iTop1 = Panel5.Height * 0.15
	iTop2 = Panel5.Height * 0.55
	iDesplaza =  (Panel5.Width -iAncho*6 - iSeparacion*5)/2

	For i = 0 To sgLetras.Length -1 ' por cada letra
      Dim LL As Label  ' crea un label local
      LL.Initialize("lblLetras")  ' lo inicializa con el mismo nombre (vector)

	  'si es una letra comprada
   	  If sgLetras.SubString2(i,i+1) = "?" Then
		LL.Textcolor = Get_ColorLetraDisponible 'Get_ColorLetraVacia
		LL.Tag = "?"
	  Else	
	  	LL.TextColor = Get_ColorLetraDisponible
		LL.Tag = i	 	
	    LL.Text = aLetrasElegir(i).LetraSiempre 'sgLetras.SubString2(i,i+1)
	  End If	
	  LL.SetBackgroundImage(gt_Color.bitmPalabraOff)
	  'LL.Text = sgLetras.SubString2(i,i+1)
	 
	  LL.Gravity = Gravity.center
	  
	  'primera línea
	  If i < sgLetras.Length / 2 Then
		iLugar = (i*(iAncho+iSeparacion))+ iDesplaza
		Panel5.AddView(LL, iLugar,iTop1,iAncho,iAlto)
	  Else
	  	iLugar = ((i-sgLetras.Length/2)*(iAncho+iSeparacion))+ iDesplaza
	  	Panel5.AddView (LL, iLugar,iTop2,iAncho,iAlto)
	  End If	  
	  If i=0 Then
	  	 Publicos.SetLabelTextSize(LL, "A", 30, 5, 70) 
		 iTextSize = LL.TextSize
	  Else
	  	LL.textsize = iTextSize
	  End If

	  ''' agrego al array
	  lblsList.Add(LL)
	  Dim lbl As Label = lblsList.Get(i)
	  lbls(i) = lbl
		    
   Next 	

   lblArrayLetras = lbls
   
   
End Sub

Sub v2_GeneraLabelsPalabra (apLetrasPalabra() As tLetrasPalabra)
'' genera un array de labels para contener las letas de la palabra
	Dim lblsList As List, iLargoPalabra As Int = xtPalabra.Palabra.Length
  '	Dim lbls(apLetrasPalabra.Length) As Label
	
	Dim lbls(iLargoPalabra) As Label
	
	Dim iInicio As Int

	BorraLabels ' borra los labels del array lblArrayPalabra
	
	lblsList.Initialize

	Dim iAnchoLetra As Int = Get_AnchoLetraPalabra(iLargoPalabra)
	Dim iSeparaLetra As Int= Get_SeparaLetra
	
	Dim iTop As Int  = (Panel4.Height - iAnchoLetra )/2
	Dim iLeft As Int, iTextSize As Int 

	iInicio = Get_InicioLetras(iAnchoLetra, iLargoPalabra) ' calcula el inicio de acuerdo a la cantidad de letras
   For i = 0 To lbls.Length -1'aLetrasPalabra.Length -1
		Dim LL As Label
		Dim iAlto As Int = iAnchoLetra
		'creacion y seteos label
		LL.Initialize("lblPalabra")
		LL.Gravity = Gravity.CENTER
		iLeft = (i*(iSeparaLetra+iAnchoLetra))+iInicio
		LL.Typeface = tFontOpenSansSemiBold

		'LL.Text = xtPalabra.Palabra.SubString2(i, i+1)
		'info de estado de la letra (comprada, etc)
		LL.TextColor = Colors.white
		If apLetrasPalabra(i).comprada Then
			LL.Tag = "?" 
			LL.SetBackgroundImage(gt_Color.bitmPalabraComprada)
			LL.Text = apLetrasPalabra(i).letra
		Else
			LL.SetBackgroundImage(gt_Color.bitmPalabraOff)
		End If
		'agrega al panel		
		Panel4.AddView(LL, iLeft,iTop,iAnchoLetra,iAlto)
	
		If i = 0 Then 'calcula el temaño de las letras
		   	Publicos.SetLabelTextSize(LL, "A", 30,5, 70) 
			iTextSize = LL.TextSize
		Else
			LL.textsize = iTextSize
		End If	
		
		' agrego al array
		lblsList.Add(LL)
		Dim lbl As Label = lblsList.Get(i)
		lbls(i) = lbl
   Next 	
   
   ' pasa el array a la variable publica que tiene los labels de la palabra
	lblArrayPalabra = lbls
   
End Sub

'setea los colores de los paneles en la combinación iCombinacion
Sub V2_Set_ColoresPaneles (iCombinacion As Int)
	Panel1.Color = gt_Color.ColorDefault
	Panel11.Color = gt_Color.ColorDefault
	Panel2.Color = gt_Color.ColorDefault
	Panel21.Color = gt_Color.ColorDefault
	Panel3.Color = gt_Color.ColorDefault
	Panel4.Color = gt_Color.ColorMedio
	Panel41.Color = gt_Color.ColorMedio

	Panel5.Color = gt_Color.ColorOscuro
	Panel51.Color =gt_Color.ColorOscuro
	Panel6.Color = gt_Color.ColorDefault
	Panel61.Color= gt_Color.ColorDefault
	
	
	'otras views	
	lblV2Nivel.TextColor = gt_Color.ColorTexto
	lblNeuronas.textcolor = gt_Color.ColorTexto
	lblAvance.TextColor = gt_Color.ColorTexto
	
	'panel2
	lblv2Def.TextColor = gt_Color.ColorTexto
	
	'panel3
	lblPedirLetra.TextColor = gt_Color.ColorTexto
	lblSaltarPalabra.TextColor = gt_Color.ColorTexto
	lblPedirLetraCosto.TextColor = gt_Color.ColorTexto
	lblSaltarPalabraCosto.TextColor = gt_Color.ColorTexto
	
	
	'panel6
	lblCompartir.Textcolor = gt_Color.ColorTexto
	lblBajarLetras.TextColor = gt_Color.ColorTexto
	
	'Panel11
	lbl11.Textcolor = gt_Color.ColorTexto
	
	'Panel21
	lbl21.TextColor = gt_Color.ColorTexto
	
	'panel61
	lbl61.TextColor = gt_Color.ColorTexto
	
	Activity.Color = gt_Color.ColorDefault
End Sub


'carga el array con los colores de los paneles de fondo de la pantalla
Sub V2_DimColoresPaneles
 'grupo de colores 0 (azules)
     aColoresPaneles(0,0).iColorR = 255:aColoresPaneles(0,0).iColorG = 255: aColoresPaneles(0,0).iColorB = 255  
     aColoresPaneles(0,1).iColorR = 255:aColoresPaneles(0,1).iColorG = 255: aColoresPaneles(0,1).iColorB = 255  
     aColoresPaneles(0,2).iColorR = 255:aColoresPaneles(0,2).iColorG = 255: aColoresPaneles(0,2).iColorB = 255  
     aColoresPaneles(0,3).iColorR = 230:aColoresPaneles(0,3).iColorG = 230: aColoresPaneles(0,3).iColorB = 250  
     aColoresPaneles(0,4).iColorR = 176:aColoresPaneles(0,4).iColorG = 196: aColoresPaneles(0,4).iColorB = 222  
     aColoresPaneles(0,5).iColorR = 255:aColoresPaneles(0,5).iColorG = 255: aColoresPaneles(0,5).iColorB = 255  
     'color claro
	 aColoresPaneles(0,6).iColorR = 230:aColoresPaneles(0,6).iColorG = 230: aColoresPaneles(0,6).iColorB = 255
	 
	 'textcolor
     aTextColor(0,0).iColorR = 176:aTextColor(0,0).iColorG = 196: aTextColor(0,0).iColorB = 222  
     aTextColor(0,1).iColorR = 176:aTextColor(0,1).iColorG = 196: aTextColor(0,1).iColorB = 222  
     aTextColor(0,2).iColorR = 176:aTextColor(0,2).iColorG = 196: aTextColor(0,2).iColorB = 222  
	 aTextColor(0,3).iColorR = 255:aTextColor(0,3).iColorG = 255: aTextColor(0,3).iColorB = 255  
     aTextColor(0,4).iColorR = 255:aTextColor(0,4).iColorG = 255: aTextColor(0,4).iColorB = 255  
     aTextColor(0,5).iColorR = 176:aTextColor(0,5).iColorG = 196: aTextColor(0,5).iColorB = 222  
 	 aTextColor(0,6).iColorR = 176:aTextColor(0,6).iColorG = 196: aTextColor(0,6).iColorB = 222
'grupo de colores 1 (verdes)
     aColoresPaneles(1,0).iColorR = 255:aColoresPaneles(1,0).iColorG = 255: aColoresPaneles(1,0).iColorB = 255  
     aColoresPaneles(1,1).iColorR = 255:aColoresPaneles(1,1).iColorG = 255: aColoresPaneles(1,1).iColorB = 255  
     aColoresPaneles(1,2).iColorR = 255:aColoresPaneles(1,2).iColorG = 255: aColoresPaneles(1,2).iColorB = 255  
     aColoresPaneles(1,3).iColorR = 144:aColoresPaneles(1,3).iColorG = 238: aColoresPaneles(1,3).iColorB = 144  
     aColoresPaneles(1,4).iColorR = 152:aColoresPaneles(1,4).iColorG = 251: aColoresPaneles(1,4).iColorB = 152  
     aColoresPaneles(1,5).iColorR = 255:aColoresPaneles(1,5).iColorG = 255: aColoresPaneles(1,5).iColorB = 255  
     aColoresPaneles(1,6).iColorR = 144:aColoresPaneles(1,6).iColorG = 238: aColoresPaneles(1,6).iColorB = 130
 	 
	'texto (los de fondo blanco, 0,1,2,5 tiene color de letra 
     aTextColor(1,0).iColorR = 152:aTextColor(1,0).iColorG = 251: aTextColor(1,0).iColorB = 152  
     aTextColor(1,1).iColorR = 152:aTextColor(1,1).iColorG = 251: aTextColor(1,1).iColorB = 152  
     aTextColor(1,2).iColorR = 152:aTextColor(1,2).iColorG = 251: aTextColor(1,2).iColorB = 152  
	 aTextColor(1,3).iColorR = 255:aTextColor(1,3).iColorG = 255: aTextColor(1,3).iColorB = 255  
     aTextColor(1,4).iColorR = 255:aTextColor(1,4).iColorG = 255: aTextColor(1,4).iColorB = 255  
     aTextColor(1,5).iColorR = 152:aTextColor(1,5).iColorG = 251: aTextColor(1,5).iColorB = 152  
	 aTextColor(1,6).iColorR = 152:aTextColor(1,6).iColorG = 251: aTextColor(1,6).iColorB = 190  
  'grupo de colores 2 (violetas)
  	' TONO 2 9E5D9E - RGB(167,99,167)
	' TONO 3 975998 ' rgb(151,89,152)
     aColoresPaneles(2,0).iColorR = 255:aColoresPaneles(2,0).iColorG = 255: aColoresPaneles(2,0).iColorB = 255  
     aColoresPaneles(2,1).iColorR = 255:aColoresPaneles(2,1).iColorG = 255: aColoresPaneles(2,1).iColorB = 255  
     aColoresPaneles(2,2).iColorR = 255:aColoresPaneles(2,2).iColorG = 255: aColoresPaneles(2,2).iColorB = 255  
     aColoresPaneles(2,3).iColorR = 167:aColoresPaneles(2,3).iColorG =  99: aColoresPaneles(2,3).iColorB = 167  
     aColoresPaneles(2,4).iColorR = 151:aColoresPaneles(2,4).iColorG =  89: aColoresPaneles(2,4).iColorB = 152  
     aColoresPaneles(2,5).iColorR = 255:aColoresPaneles(2,5).iColorG = 255: aColoresPaneles(2,5).iColorB = 255  
	' se agrega un color bien claro
	 aColoresPaneles(2,6).iColorR = 140:aColoresPaneles(2,6).iColorG =  89: aColoresPaneles(2,6).iColorB = 200

	'texto
     aTextColor(2,0).iColorR = 151:aTextColor(2,0).iColorG = 89: aTextColor(2,0).iColorB = 152  
     aTextColor(2,1).iColorR = 151:aTextColor(2,1).iColorG = 89: aTextColor(2,1).iColorB = 152  
     aTextColor(2,2).iColorR = 151:aTextColor(2,2).iColorG = 89: aTextColor(2,2).iColorB = 152  
	 aTextColor(2,3).iColorR = 255:aTextColor(2,3).iColorG = 255: aTextColor(2,3).iColorB = 255  
     aTextColor(2,4).iColorR = 255:aTextColor(2,4).iColorG = 255: aTextColor(2,4).iColorB = 255  
     aTextColor(2,5).iColorR = 151:aTextColor(2,5).iColorG = 89: aTextColor(2,5).iColorB = 152  
	'text para el color bien claro
	 aTextColor(2,6).iColorR = 151:aColoresPaneles(2,6).iColorG =  89: aColoresPaneles(2,6).iColorB = 138  

End Sub

Sub v2_CopiaImagenes
'' PALABRA
	Publicos.Archivo_CopiaDesdeAssets("Violeta-Palabra-Off-Borde.png", False, g_DirGrabable)
	Publicos.Archivo_CopiaDesdeAssets("Violeta-Palabra-On-Borde.png", False, g_DirGrabable)

'' LETRAS A ELEGIR	
	'Publicos.Archivo_CopiaDesdeAssets("violeta-letra-off-borde.png", False, g_DirGrabable)

'' ICONOS
	'Publicos.Archivo_CopiaDesdeAssets("violeta-btn-menu.png", False, g_DirGrabable)
	'Publicos.Archivo_CopiaDesdeAssets("twitter.png", False, g_DirGrabable)
	'Publicos.Archivo_CopiaDesdeAssets("violeta-btn-compartir.png", False, g_DirGrabable)
	'Publicos.Archivo_CopiaDesdeAssets("violeta-btn-mute.png", False, g_DirGrabable)
	'Publicos.Archivo_CopiaDesdeAssets("violeta-btn-pedir.png", False, g_DirGrabable)
	'Publicos.Archivo_CopiaDesdeAssets("violeta-btn-resolver.png", False, g_DirGrabable)
	'Publicos.Archivo_CopiaDesdeAssets("violeta-icon-flag.png", False, g_DirGrabable)
	Publicos.Archivo_CopiaDesdeAssets("Neurona-Icon-Top-2.png", False, g_DirGrabable)
	'Publicos.Archivo_CopiaDesdeAssets("Violeta-Error-Borde.png", False, g_DirGrabable)
	Publicos.Archivo_CopiaDesdeAssets("Menu-Volver.png", False, g_DirGrabable)
	'Publicos.Archivo_CopiaDesdeAssets("Menu-Fondo-Violeta.png", False, g_DirGrabable)


	'Publicos.Archivo_CopiaDesdeAssets("Violeta-Palabra-Comprada-Borde.png", False, g_DirGrabable)
	'Publicos.Archivo_CopiaDesdeAssets("Violeta-Bajar-Letras.png", False, g_DirGrabable)

	'Publicos.Archivo_CopiaDesdeAssets("Violeta-Costos.png", False, g_DirGrabable)

	'Publicos.Archivo_CopiaDesdeAssets("Violeta-Share-Confirmar.png", False, g_DirGrabable)
	'Publicos.Archivo_CopiaDesdeAssets("Violeta-Share-Cancelar.png", False, g_DirGrabable)

	Publicos.Archivo_CopiaDesdeAssets("rojo-cancelar060.png", False, g_DirGrabable)
	Publicos.Archivo_CopiaDesdeAssets("verde-confirmar060.png", False, g_DirGrabable)
	Publicos.Archivo_CopiaDesdeAssets("Menu-Historia.png", False, g_DirGrabable)
	'Publicos.Archivo_CopiaDesdeAssets("Violeta-Subir.png", False, g_DirGrabable)
	'Publicos.Archivo_CopiaDesdeAssets("Violeta-Bajar.png", False, g_DirGrabable)
	
End Sub

Sub V2_CargaImagenesEstaticasEnVariables
	If bitmRojoCancelar.IsInitialized = False Then
		bitmRojoCancelar = LoadBitmap(g_DirGrabable, "rojo-cancelar060.png")
		bitmVerdeConfirmar = LoadBitmap(g_DirGrabable, "verde-confirmar060.png")
		bitmNeurona = LoadBitmap(g_DirGrabable, "Neurona-Icon-Top-2.png")
		bitmMenuVolver= LoadBitmap(g_DirGrabable, "Menu-Volver.png")

		'bitmPalabraError = LoadBitmap(g_DirGrabable, "Violeta-Error-Borde.png")
		'bitmMenuFondoVioleta= LoadBitmap(g_DirGrabable, "Menu-Fondo-Violeta.png")
		'bitmBajarLetras = LoadBitmap(g_DirGrabable, "Violeta-Bajar-Letras.png")
		'bitmPalabraComprada= LoadBitmap(g_DirGrabable, "Violeta-Palabra-Comprada-Borde.png")
		'bitmCostos = LoadBitmap(g_DirGrabable, "Violeta-Costos.png")	
		'bitmShareConfirmar = LoadBitmap(g_DirGrabable, "Violeta-Share-Confirmar.png")	
		'bitmShareCancelar  = LoadBitmap(g_DirGrabable, "Violeta-Share-Cancelar.png")	
	 	'bitmBajar = LoadBitmap(g_DirGrabable, "Violeta-Bajar.png")
	 	'bitmSubir = LoadBitmap(g_DirGrabable, "Violeta-Subir.png")
		'bitmPalabraOff = LoadBitmap(g_DirGrabable,"Violeta-Palabra-Off-Borde.png")
		'bitmPalabraOn= LoadBitmap(g_DirGrabable, "Violeta-Palabra-On-Borde.png")
		'bitmLetra= LoadBitmap(g_DirGrabable, "violeta-letra-off-borde.png")
		'bitmMenu= LoadBitmap(g_DirGrabable, "violeta-btn-menu.png")
		'bitmCompartir= LoadBitmap(g_DirGrabable, "violeta-btn-compartir.png")
		'bitmMute= LoadBitmap(g_DirGrabable, "violeta-btn-mute.png")
		'bitmPedir= LoadBitmap(g_DirGrabable, "violeta-btn-pedir.png")
		'bitmSaltarPalabra= LoadBitmap(g_DirGrabable, "violeta-btn-resolver.png")
		'bitmFlag= LoadBitmap(g_DirGrabable, "violeta-icon-flag.png")
	
	
	End If
End Sub

'carga las imágenes en los views de única vez, las que no cambian con el cambio de color de la pantalla
Sub V2_CargaImagenesViewsEstaticas
'panel1
	'imgMenu.SetBackgroundImage(bitmMenu)
	imgNeuronas.SetBackgroundImage(bitmNeurona)	
	'imgAvance.SetBackgroundImage(bitmFlag)
'panel3
	'imgPedirLetra.SetBackgroundImage(bitmPedir)
	'imgSaltarPalabra.SetBackgroundImage(bitmSaltarPalabra)
	
	'lblPedirLetraCosto.setbackgroundimage(LoadBitmapSample(g_DirGrabable, "Violeta-Costos.png", 100, 100))
	'lblSaltarPalabraCosto.SetBackgroundImage(LoadBitmapSample(g_DirGrabable, "Violeta-Costos.png", 100, 100))
	
'panel6
	'imgCompartir.SetBackgroundImage(bitmCompartir)
	'imgBajarLetras.SetBackgroundImage(bitmBajarLetras)
	'btnSonido.SetBackgroundImage(bitmMute)
	


End Sub


Sub Get_ColorLetraDisponible As Int
Dim iRet As Int
iRet = gt_Color.colordefault

Return iRet
End Sub

Sub Get_ColorLetraVacia As Int
Dim iRet As Int
iRet = Colors.Transparent
Return iRet
End Sub

Sub lbl61_Click
	Dim iCual As Int
	iCual = lbl61.Tag
	iPantallaActiva = iCual
	Select iCual
	Case xConfiguraPantalla.Adivino
		'AnimPlus_AnimationEnd
		'si canceló fuerza una nueva animacion porque sino queda pendiente un animplus_animationend y no muestra la proxima animacion
		V2_AdivinoAnimacion(0, 0)
		'apaga paneles adivino
		v2_MuestraNuevaPalabra(0,False,False)
		If Not (bFinDeJuego) Then
			V2_PantallaConfigura(xConfiguraPantalla.jugar,0)
		End If
	Case xConfiguraPantalla.ComprarLetra
	'	ComprarLetraHacer
		V2_PantallaConfigura(xConfiguraPantalla.jugar, 0)
	Case xConfiguraPantalla.SaltarPalabra
	'	SaltarPalabraHace
		V2_PantallaConfigura(xConfiguraPantalla.jugar, 0)
	Case xConfiguraPantalla.Compartir
		V2_PantallaConfigura(xConfiguraPantalla.jugar,0)
	Case xConfiguraPantalla.Ayuda
		V2_PantallaConfigura(xConfiguraPantalla.jugar,0)
	Case xConfiguraPantalla.Creditos
		V2_PantallaConfigura(xConfiguraPantalla.jugar,0)
	Case xConfiguraPantalla.FinDeJuego	
		If gb_EsPremium Then
			V2_PantallaConfigura(xConfiguraPantalla.Historia,0)
			'Activity.Finish
			'appfw.eventWithValue("close", xtPalabra.idPalabra)
			'appfw.closeSession(True)
		Else
			V2_PantallaConfigura(xConfiguraPantalla.Premium, 0)
		End If
	
	Case xConfiguraPantalla.Producto
		pnlInvisible.removeallviews
		pnlInvisible.Visible = False
		Neuronas_Mostrar
		V2_AnimaRotateImageview(imgNeuronas, 0, 720, 1000)
	Case xConfiguraPantalla.Premium
		StartActivity("Main")
	Case xConfiguraPantalla.Historia
		If bFinDeJuego Then
			V2_PantallaConfigura(xConfiguraPantalla.FinDeJuego,0)
		Else
			V2_PantallaConfigura(xConfiguraPantalla.jugar,0)
		End If
	
	End Select
	
End Sub

Sub V2_AdivinoAnimacion (iTiempo As Int, iGrados As Int)
	AnimPlus.InitializeRotateCenter("AnimPlus", 0, iGrados, imgAnimacion)
	Activity.invalidate
	AnimPlus.Duration = iTiempo
	If AnimPlus.IsInitialized Then
		AnimPlus.Stop(imgAnimacion)
		Activity.Invalidate
		AnimPlus.Start(imgAnimacion)
	End If
	Log("Animacion")
End Sub

Sub V2_AnimaNeuronas
	AnimPlus.InitializeRotateCenter("AnimPlus", 0, 720, imgNeuronas)
	Activity.invalidate
	AnimPlus.Duration = 500
	If AnimPlus.IsInitialized Then
		AnimPlus.Stop(imgNeuronas)
		Activity.Invalidate
		AnimPlus.Start(imgNeuronas)
	End If

End Sub

Sub V2_NoAdivinoPalabraResalta

Sonido(SONIDO_ERROR)


	For i =0 To 3
		For j = 0 To lblArrayPalabra.Length-1
			If aLetrasPalabra(j).comprada = False Then
				'lblArrayPalabra(j).color = Colors.Red
				lblArrayPalabra(j).SetBackgroundImage(gt_Color.bitmPalabraError)
				lblArrayPalabra(j).TextColor = Colors.white
			End If
		Next
		Publicos.Wait(150)
		'restaura original
		For j=0 To aLetrasPalabra.Length-1
			If aLetrasPalabra(j).comprada = False Then
				'lblArrayPalabra(j).Color = Get_ColorText (3)
				If lblArrayPalabra(j).Text = "" Then
					lblArrayPalabra(j).SetBackgroundImage(gt_Color.bitmPalabraOff)
					lblArrayPalabra(j).TextColor =Colors.transparent
				Else ' si tiene alguna letra, la pone de color blanco
					lblArrayPalabra(j).SetBackgroundImage(gt_Color.bitmPalabraOn)
					lblArrayPalabra(j).TextColor =gt_Color.ColorTexto
				End If
			
			End If
		Next 
		Publicos.Wait(50)
	Next


End Sub

Sub Img51_Click
	Dim iCual As Int
	iCual = img51.tag

	
	Select iCual
	Case xConfiguraPantalla.ComprarLetra
		ComprarLetraHacer
	Case xConfiguraPantalla.SaltarPalabra
		SaltarPalabraHacer
	End Select
	' si al comprar una letra completó la palabra, no refresca la pantalla, espera el click en continuar
	If Not (ComparaPalabra ) And Not (bFinDeJuego) Then
		V2_PantallaConfigura(xConfiguraPantalla.jugar, 0)
	End If
End Sub

Sub imgCompartir_Click
'hace un screenshot de la pantalla
	Publicos.Screenshot(Activity, g_DirGrabable, "PruebaSS.png")

	V2_PantallaConfigura (xConfiguraPantalla.Compartir, 0)
	 

End Sub

Sub lblPedirLetra_Click
	imgPedirLetra_click	
End Sub


Sub lblSaltarPalabra_Click
	imgSaltarPalabra_click
	
End Sub

Sub img51Facebook_Click
	
	If Publicos.Facebook_GetUso (g_sqlBaseLocalUsuario) = 0 Then
		StartActivity("FacebookActivity")
		FacebookActivity.sDefinicion = xtPalabra.Descripcion
		FacebookActivity.sLetrasElegir = Get_LetrasElegirFb
		FacebookActivity.sPalabra = xtPalabra.Palabra
		FacebookActivity.iCostoLetra = xtNivel.CostoLetra
		FacebookActivity.sLetrasPalabra = Get_LetrasPalabraFb
		FacebookActivity.g_sqlBaseLocalUsuario = g_sqlBaseLocalUsuario
	Else
		ToastMessageShow("Ya publicaste esta palabra en Facebook!", True)	
	End If

End Sub

Sub imgMenu_Click
	If Panel61.Visible = False Then

		'Menu.ShowMenu
		If sm.isVisible Then
			sm.hide
		Else
			sm.Show
		End If
	End If
End Sub

Sub lblBajarLetras_Click
	'Animacion_SumarNeuronas (100)
	BajarLetras
End Sub

Sub CreateTypeQuerysRemotos(pAvance As Int, pPremium As Int) As tQuerysRemotos
 Dim m As tQuerysRemotos
 m.Avance = pAvance
 m.Premium = pPremium
Return m
End Sub

Sub CreateTypeConfiguraPantalla(pCompartir As Int, pComprarLetra As Int, pSaltarPalabra As Int, pJugar As Int, pAdivino As Int, pAyuda As Int, pCreditos As Int, pFinDeJuego As Int, pProducto As Int, pComproNeuronas As Int, pMuestraAviso As Int, pPremium As Int, pHistoria As Int) As tConfiguraPantalla 
'	Type tConfiguraPantalla (Compartir As Int, ComprarLetra As Int, SaltarPalabra As Int, Jugar As Int)'
'	Dim xConfiguraPantalla As tConfiguraPantalla 
 
 Dim m As tConfiguraPantalla
 m.Initialize
 m.Compartir = pCompartir
 m.ComprarLetra = pComprarLetra
 m.SaltarPalabra= pSaltarPalabra
 m.Jugar = pJugar
 m.Adivino = pAdivino
 m.Ayuda = pAyuda
 m.Creditos = pCreditos
 m.FinDeJuego = pFinDeJuego
 m.Producto = pProducto
 m.ComproNeuronas = pComproNeuronas
 m.MuestraAviso = pMuestraAviso
 m.Premium = pPremium
 m.Historia = pHistoria
 Return m
End Sub

Sub V2_PantallaConfigura(iOpcion As Int, iNeuronas As Int )

lbl61.Tag = iOpcion
img51.Tag = iOpcion 

iPantallaActiva = iOpcion
Select (True)
Case iOpcion = xConfiguraPantalla.Compartir
	
	' los que prende
	lbl41.Text = "COMPARTIR"
	lbl41.TextColor = gt_Color.ColorDefault
	img51Facebook.Visible = True
	img51Twitter.visible = True
	imgAnimacion.Visible = False
	lbl51Facebook.Visible = True
	lbl51Twitter.Visible = True
	img51.Visible = False
	lbl51.Visible = False
		lbl51MensajeMatete.visible = False
	lbl61.Text = "CANCELAR"
	lbl61.Visible = True

	Panel1.Visible = True
	Panel41.Visible = True
	Panel51.Visible = True
	Panel61.Visible = True
	Panel3.visible = False
	Panel21.Visible = False
	Panel11.Visible = False

	scrViewAyuda.Visible = False

	lbl61.textcolor = gt_Color.ColorTexto
	Panel61.Color = Colors.white
	Activity.Color = Colors.white

	pnlInvisible.Visible = False
	pnlHistoria.Visible = False

Case iOpcion = xConfiguraPantalla.ComprarLetra
	' los que prende
	lbl41.Text = "DESCUBRE UNA LETRA"
	lbl41.TextColor = gt_Color.ColorDefault

	img51Facebook.Visible = False
	img51Twitter.visible = False
	img51.Visible = True
	img51.SetBackgroundImage(gt_Color.bitmPedir)	

	imgAnimacion.Visible = False
	lbl51Facebook.Visible = False
	lbl51Twitter.Visible = False
	lbl51.Text ="USAR " & xtNivel.CostoLetra & " NEURONA" & IIF(xtNivel.CostoLetra>1, "S", "")
	lbl51.Visible = True
	lbl51MensajeMatete.Visible = False	
	lbl61.Text = "CANCELAR"
	lbl61.Visible = True
	
	Panel41.Visible = True
	Panel51.Visible = True
	Panel61.Visible = True
	Panel3.visible = False
	Panel21.Visible = False
	Panel11.Visible = False
	
	scrViewAyuda.Visible = False	
	lbl61.textcolor = Get_aTextColor(gi_CombinacionColores, 0)
	Panel61.Color = gt_Color.ColorDefault
	'lbl61.Color = Colors.transparent
	Activity.Color = Colors.white
	Panel61.BringToFront
	
	pnlHistoria.Visible = False	
	pnlInvisible.Visible = False
Case iOpcion = xConfiguraPantalla.Jugar
	pnlInvisible.RemoveAllViews
	pnlInvisible.Visible =False
	imgShadow.Top = Panel1.Top + Panel1.height

	'
	Panel1.Visible = True
	Panel2.Visible = True
	Panel2.Top = Panel1.Top + Panel1.Height ' lo vuelve a setear por si paso por la pantalla de fin de juego
	
	
	Panel3.Visible = True
	Panel4.Visible = True
	Panel5.Visible = True
	Panel6.Visible = True
	Panel41.Visible = False
	Panel51.Visible = False
	Panel61.Visible = False
	Panel21.Visible = False
	Panel11.Visible = False
	
	imgLoading.Visible = False
	scrViewAyuda.Visible = False

	lbl61.textcolor = Get_aTextColor(gi_CombinacionColores, 0)
	Panel61.Color = gt_Color.ColorDefault
	Activity.Color = Colors.white
	'Panel6.BringToFront
	pnlHistoria.Visible = False
	pnlInvisible.Visible = False
Case iOpcion = xConfiguraPantalla.SaltarPalabra
	' los que prende
	lbl41.Text = "SALTAR EL MATETE"
	lbl41.TextColor = gt_Color.ColorDefault

	img51Facebook.Visible = False
	img51Twitter.visible = False
	img51.Visible = True
	img51.SetBackgroundImage (gt_Color.bitmSaltarPalabra)
	imgAnimacion.Visible = False
	lbl51Facebook.Visible = False
	lbl51Twitter.Visible = False
	lbl51.Text ="USAR " & xtNivel.CostoSaltar & " NEURONA" & IIF(xtNivel.CostoSaltar>1, "S", "")
	lbl51.Visible = True
	lbl51MensajeMatete.Visible = False	
	lbl61.Text = "CANCELAR"
	lbl61.Visible = True

	Panel41.Visible = True
	Panel51.Visible = True
	Panel61.Visible = True
	Panel3.visible = False
	Panel21.Visible = False
	Panel11.Visible = False
	
	scrViewAyuda.Visible = False
	lbl61.textcolor = Get_aTextColor(gi_CombinacionColores, 0)
	Panel61.Color = gt_Color.ColorDefault
	Activity.Color = Colors.white
	pnlInvisible.Visible = False
	pnlHistoria.Visible = False
	V2_AnimaNeuronas
Case iOpcion = xConfiguraPantalla.Adivino
	
	If xtPalabra.bRejugada And xtPalabra.bSalteada = False Then
		lbl21.Text = ""	
	Else	
		lbl21.Text = "Ganaste " & Get_MonedasPorNivel & " neuronas"
	End If
	Publicos.SetLabelTextSize(lbl21, lbl21.Text, 30,6, 100)
	img51Facebook.Visible = False
	img51Twitter.visible = False
	img51.Visible = False

	imgAnimacion.Visible = True
	lbl51Facebook.Visible = False
	lbl51Twitter.Visible = False

	lbl51.Visible = False
	lbl51MensajeMatete.Visible = True
	lbl61.Text = "CONTINUAR"
	lbl61.Visible = True

	Panel1.Visible = True
	Panel41.Visible = False
	Panel51.Visible = True
	Panel61.Visible = True
	Panel3.visible = False
	Panel21.Visible = True
	Panel11.Visible = False
	
	lblv2Def.Text = Get_MensajeAdivino (xtPalabra.idPalabra) 'xtPalabra.Diccionario	
	Publicos.SetLabelTextSize(lblv2Def, lblv2Def.text, 60,10, 100)
	
	If xtPalabra.Palabra.ToUpperCase <> xtPalabra.PalabraDiccionario.ToUpperCase Then
		lbl51MensajeMatete.Text = "(" & xtPalabra.PalabraDiccionario& ")" & Chr(10)
	Else
		lbl51MensajeMatete.Text = "" 
	End If
	lbl51MensajeMatete.Text = lbl51MensajeMatete.Text & xtPalabra.diccionario

	Publicos.SetLabelTextSize(lbl51MensajeMatete, lbl51MensajeMatete.Text, 40,6, 100)
	
	'pone los dos mensajes del mismo tamaño de letra.
	If lblv2Def.TextSize < lbl51MensajeMatete.textsize Then
		lbl51MensajeMatete.TextSize = lblv2Def.TextSize
	Else
		lblv2Def.TextSize = lbl51MensajeMatete.textsize
	End If	
	
	
	scrViewAyuda.Visible = False
	lbl61.textcolor = Get_aTextColor(gi_CombinacionColores, 0)
	Panel61.Color = gt_Color.ColorDefault

	Activity.Color = Colors.white
	pnlInvisible.Visible = False
	
	gi_AnimacionEnCurso = xConfiguraPantalla.Adivino
	pnlHistoria.Visible = False
	V2_AdivinoAnimacion(2000, 2880)
Case iOpcion =  xConfiguraPantalla.Ayuda
	ScrollviewMUestra("A")
	lbl61.Text = "VOLVER"
	lbl61.TextColor = gt_Color.colordefault
	Panel61.Color = gt_Color.ColorClaro
	Panel61.Visible = True

	lbl11.Text = "AYUDA"
	Panel11.Visible = True
		
	Panel1.Visible = False	
	imgShadow.Top = Panel11.Top + Panel11.Height
	
	Panel6.Visible = False
	Activity.Color = gt_Color.colordefault
	
	pnlHistoria.Visible = False
	pnlInvisible.Visible = False
	
Case iOpcion = xConfiguraPantalla.creditos
	ScrollviewMUestra("C")
	lbl11.Text = "CREDITOS"
	lbl61.Text = "VOLVER"
	lbl61.TextColor = gt_Color.colordefault
	Panel61.Color = gt_Color.ColorClaro

	Panel61.Visible = True
	Panel11.Visible = True
	
	Panel1.visible = False
	imgShadow.Top = Panel11.Top + Panel11.Height

	Panel6.Visible = False
	Activity.Color = gt_Color.colordefault
	pnlInvisible.Visible = False
	pnlHistoria.Visible = False

Case iOpcion = xConfiguraPantalla.FinDeJuego
	bFinDeJuego = True
	Dim iGanadas As Int, iSalteadas As Int
	'iPalabras = Publicos.get_CantidadPalabras(sqlMate)
	iGanadas=Publicos.get_CantidadPalabrasAdivinadas(g_sqlBaseLocalUsuario)
	iSalteadas = Publicos.get_CantidadPalabrasSalteadas(g_sqlBaseLocalUsuario)
	
	Dim sMensaje As String, bOfrecerPremium As Boolean = False

	
	If gb_EsPremium Or Get_QuedanPalabrasAlmacen = False Then 
		sMensaje = "JUEGO COMPLETADO" & Chr(10)
		sMensaje  = sMensaje & "Nos has dejado sin palabras " & Chr(10)
		
	Else 
		sMensaje = "JUEGO GRATUITO COMPLETADO" & Chr(10) & _ 
				   "Puedes adquirir la versión premium" & Chr(10) & "con más palabras"	& Chr(10)
	End If
	
	'sMensaje = sMensaje & " " & iPalabras & " palabras"
	sMensaje = sMensaje & " " & iGanadas  &  " adivinadas" & Chr(10)
	sMensaje = sMensaje & " " &  iSalteadas & " salteada" & IIF(iSalteadas>1, "s", "")

	Panel1.Visible = False
	Panel11.Visible = False
	Panel2.Visible= True
	Panel3.Visible = False
	Panel21.visible = False 'agregado
	Panel4.Visible = False
	Panel41.Visible = False
	Panel5.Visible = False
	Panel51.Visible = False
	
	imgAnimacion.Visible = False
	lbl21.Visible = False
	
	Panel6.visible = False
	Panel61.Visible = True
	Panel61.Color = Colors.White
	lbl61.textcolor = Get_aTextColor(gi_CombinacionColores, 0)

	
	If gb_EsPremium Then
		lbl61.Text = "VER HISTORIA"
	Else
		lbl61.Text = "COMPRAR PREMIUM"
	End If
	
	lblv2Def.Text = sMensaje
	lblv2Def.TextColor = Get_aTextColor(2,1)
	Publicos.SetLabelTextSize(lblv2Def, lblv2Def.TEXT, 55,8,100)
	imgLoading.Top = 0
	imgLoading.Visible = True
	imgLoading.BringToFront
	Panel2.Top = imgLoading.Top + imgLoading.Height
	

	pnlInvisible.Visible = False
	pnlHistoria.Visible = False

Case iOpcion = xConfiguraPantalla.producto
	
	Productos_Muestra

Case iOpcion = xConfiguraPantalla.ComproNeuronas
	Productos_MuestraComproNeuronas ( iNeuronas)

Case iOpcion = xConfiguraPantalla.MuestraAviso
	Aviso_Muestra

Case iOpcion = xConfiguraPantalla.Premium
	Premium_Muestra

Case iOpcion = xConfiguraPantalla.Historia
'lista los matetes resueltos
	
	Historia_Muestra("IGUAL")


End Select

End Sub

Sub IIF(C As Boolean, TrueRes As String, FalseRes As String)
 If C Then Return TrueRes Else Return FalseRes
End Sub

Sub lblCompartir_Click
imgCompartir_Click
End Sub

Sub SonidoActualiza
	Dim iSet As Int
	
	If Publicos.Get_SeteoUsuarioEnteroDesde(g_sqlBaseLocalUsuario, "Sonido") = 1 Then
		sm.ItemArrayCambiaImagen(3, "Menu-Sonido-Off.png")
		iSet = 0
		bSonidoPrendido = False	
	Else
		iSet = 1
		sm.ItemArrayCambiaImagen(3, "Menu-Sonido-On.png")
		bSonidoPrendido = True
	End If
	Publicos.SeteoUsuarioEnteroDesde_Update(g_sqlBaseLocalUsuario, "Sonido", iSet)



End Sub

Sub V2_MenuGenera

Try
	Dim iAncho As Int, iAlto As Int
'crea el menu
	sm.Initialize(Activity, Me, "SlideMenu",0, Activity.Width * 0.45)

'agrega una imagen para el fondo
	Dim iImagen As ImageView
	iImagen.Initialize("")
	sm.smAddView(iImagen, 0, 0, -1, Activity.Height, False, True)
	iImagen.SetBackgroundImage ( gt_Color.bitmMenuFondo)

'ayuda, creditos, volver, salir, tienda, sonido
	iAncho = Activity.Width * 0.45
	iAlto = Activity.Height * 0.07
	
''AGREGAR SETTEXTSIZE AUTOMATICO
	'USA EL label trucho para calcular el tamaño
	
	lblCalcTextSize.Width = iAncho*0.8
	lblCalcTextSize.Height = iAlto 
	Publicos.SetLabelTextSize(lblCalcTextSize, "CREDITOS", 40, 5, 100)
	Dim iTextSize As Int = lblCalcTextSize.TextSize

	sm.AddItemArray("VOLVER", "Menu-Volver.png", 0, iAlto, iTextSize )
	sm.AddItemArray("CREDITOS", "Menu-Creditos.png", iAlto*3, iAlto, iTextSize)
	sm.AddItemArray("AYUDA", "Menu-Ayuda.png", iAlto*4, iAlto, iTextSize)

	If Publicos.Get_SeteoUsuarioEnteroDesde(g_sqlBaseLocalUsuario, "Sonido") = 1 Then
		sm.AddItemArray("SONIDO", "Menu-Sonido-On.png", iAlto*5, iAlto, iTextSize)
	Else
		sm.AddItemArray("SONIDO", "Menu-Sonido-Off.png", iAlto*5, iAlto, iTextSize)
	End If

	' si están habilitados los InAppProducts, agrega el item en el menu
	If bProductos Then 
		sm.AddItemArray("SHOP", "Menu-Tienda.png", iAlto*6, iAlto, iTextSize)
	End If 
	
	'si es premium muesta en el menú la opción de ver la lista de matetes jugados
	
'	If gb_EsPremium Then
		sm.AddItemarray("HISTORIA", "Menu-Historia.png", iAlto*7, iAlto, iTextSize)
'	End If


Catch
	Log("Catch V2_MenuGenera")
End Try

End Sub

'Event sub which is called when an item in the slidemenu is clicked
Sub SlideMenu_Click(Item As Label)
	'ToastMessageShow("Item clicked: " & Item, False)
	'Msgbox("Item:2" & Item ,"slidemenu_click")
	
	
	Select Case (True)
	Case Item.Text = "VOLVER" 
		
	Case Item.Text = "CREDITOS"
		'ScrollviewMUestra("C")
		V2_PantallaConfigura (xConfiguraPantalla.Creditos, 0)
		'appfw.eventWithValue("Creditos", xtPalabra.idPalabra)

	Case Item.Text = "AYUDA"
		'ScrollviewMUestra("A")	
		V2_PantallaConfigura (xConfiguraPantalla.Ayuda, 0)
		'appfw.eventWithValue("Ayuda", xtPalabra.idPalabra)
	Case Item.Text = "SONIDO"
		SonidoActualiza
	Case Item.Text = "SALIR"
		If Msgbox2("Quieres dejar de entrenar tus neuronas?", "Matete Divergente", "Salir", "Cancelar", "", Null) = DialogResponse.POSITIVE Then
			Activity.Finish
			'appfw.eventWithValue("close", xtPalabra.idPalabra)
			'appfw.closeSession(True)
		End If
	Case Item.Text = "SHOP"
		V2_PantallaConfigura(xConfiguraPantalla.producto, 0)
	Case Item.Text = "HISTORIA"
		'DoEvents
		'ToastMessageShow("CARGANDO HISTORIA...", True)
		'DoEvents
		'Activity.Invalidate
		
		V2_PantallaConfigura(xConfiguraPantalla.Historia, 0)
	End Select

End Sub

Sub ScrollViewGenera
	imgShadow.Visible = False
	imgShadow.Left = 0
	imgShadow.Width = Activity.Width
	imgShadow.Height = 3dip
	imgShadow.Top = Panel11.Top +  Panel11.Height
	
	If 	lblAyuda.IsInitialized = False Then
			lblAyuda.Initialize("")
	End If
	scrViewAyuda.Panel.RemoveAllViews
	scrViewAyuda.Panel.AddView(lblAyuda, 0, 0, scrViewAyuda.width, scrViewAyuda.Height)
	
	scrViewAyuda.Left = 0
	scrViewAyuda.top = Panel11.Top + Panel11.Height + imgShadow.Height
	scrViewAyuda.Width = Activity.Width
	scrViewAyuda.Height=Activity.Height - Panel11.Height - imgShadow.Height - (Activity.height - Panel61.Top)
	scrViewAyuda.Panel.width= scrViewAyuda.Width

	lblAyuda.Left = scrViewAyuda.Width * 0.1
	lblAyuda.Width = scrViewAyuda.Width * 0.8

	lblAyuda.TextColor = Colors.White 'Get_aTextColor(2, 2)
	lblAyuda.Color = gt_Color.ColorDefault
	scrViewAyuda.Panel.Color=gt_Color.colordefault
	scrViewAyuda.Color = gt_Color.colordefault
	
	lblAyuda.Gravity = Gravity.CENTER_HORIZONTAL+Gravity.TOP
	lblAyuda.Typeface = tFontOpenSansLight
	scrViewAyuda.Visible = False
	imgShadow.BringToFront
End Sub

' "A" muestra agradecimientos
' "C" muestra los créditos
Sub ScrollviewMUestra(sCual As String)
	lblAyuda.TextColor = gt_Color.ColorTexto
	If sCual = "A" Then
		lblAyuda.Text = Publicos.Get_TextoAyuda
		scrViewAyuda.Panel.Height = Activity.Height*6
	Else
		lblAyuda.Text = Publicos.Get_TextoCreditos
		scrViewAyuda.Panel.Height = Activity.Height*2
	End If
	'calcula el tamaño de letra que quiere poner (tiene que entrar el mail en una sola linea
	lblAyuda.TextSize = Get_TextSizeAux ("MATETEJUEGO@GMAIL.COM",lblAyuda.Width,100)
	lblAyuda.Height = scrViewAyuda.Panel.Height

	Publicos.SetLabelTextSize(lblAyuda, lblAyuda.Text, 40,8, 100)

	scrViewAyuda.ScrollPosition = 0
	scrViewAyuda.Visible = Not (scrViewAyuda.Visible )
	imgShadow.Visible = scrViewAyuda.visible
	scrViewAyuda.bringtofront
	imgShadow.BringToFront
End Sub

'calcula el tamaño de letra usando un label auxiliar
' devuelve el tamaño de la letra
Sub Get_TextSizeAux (sMensaje As String, iW As Int, iH As Int) As Int
	lblCalcTextSize.width = iW
	lblCalcTextSize.Height = iH
	Publicos.SetLabelTextSize(lblCalcTextSize, sMensaje, 40,5, 100)

Return lblCalcTextSize.textsize
End Sub

Sub V2_AnimaRotateImageview (imgAnimar As ImageView, iGradoOrigen As Int, iGradoFin As Int, iMilisegundos As Int)
	AnimPlus.InitializeRotateCenter("AnimPlus", iGradoOrigen, iGradoFin, imgAnimar)
	Activity.invalidate
	AnimPlus.Duration = iMilisegundos
	If AnimPlus.IsInitialized Then
		AnimPlus.Stop(imgAnimar)
		Activity.Invalidate
		AnimPlus.Start(imgAnimar)
	End If
End Sub

Sub V2_RotaPantalla

	Dim pnlPanelFondo As Panel, imgScreenShot As ImageView

	pnlPanelFondo.initialize ("")
	pnlPanelFondo.color = Colors.Black
	Activity.AddView(pnlPanelFondo, 0,0,Activity.Width, Activity.Height)
	
	imgScreenShot.Initialize("")
	Activity.AddView(imgScreenShot, 0,0,Activity.Width, Activity.Height)
	imgScreenShot.SetBackgroundImage(LoadBitmap(g_DirGrabable, "SSAnimar.png"))
	imgScreenShot.Gravity = Gravity.fill

	pnlPanelFondo.bringtofront
	Activity.invalidate
	DoEvents	
	imgScreenShot.bringtofront

	V2_AnimaRotateImageview(imgScreenShot, 0, 360, 200)
	
	pnlPanelFondo.RemoveView
	imgScreenShot.RemoveView
End Sub

Sub Anima_DesplazaImagen (imgAnimar As ImageView, iFromDx As Int, IfromDy As Int, iToDX As Int, iToDY As Int, iMilisegundos As Int, iInterPolator As Int)
	AnimPlus.SetInterpolator(iInterPolator)
	AnimPlus.InitializeTranslate("AnimPlus", iFromDx, IfromDy, iToDX, iToDY)
	AnimPlus.PersistAfter = False

	Activity.invalidate
	AnimPlus.Duration = iMilisegundos
	If AnimPlus.IsInitialized Then
		AnimPlus.Stop(imgAnimar)
		Activity.Invalidate
		AnimPlus.Start(imgAnimar)
	End If
	'imgAnimar.Visible = False
End Sub

Sub Anima_DesplazaScreenshot
	Dim imgScreenShot As ImageView

	'pnlPanelFondo.initialize ("")
	'pnlPanelFondo.color = Colors.Black
	'Activity.AddView(pnlPanelFondo, 0,0,Activity.Width, Activity.Height)
		
	imgScreenShot.Initialize("")
	imgScreenShot.Gravity = Gravity.FILL
	Activity.AddView(imgScreenShot, 0,0,Activity.Width, Activity.Height)
	imgScreenShot.SetBackgroundImage(LoadBitmap(g_DirGrabable, "SSAnimar.png"))

	'pnlPanelFondo.bringtofront
	Activity.invalidate
	DoEvents	
	imgScreenShot.bringtofront

	Anima_DesplazaImagen (imgScreenShot, 0,0, Activity.Width, 0, 500, AnimPlus.INTERPOLATOR_LINEAR)

	'pnlPanelFondo.RemoveView
	imgScreenShot.removeview
End Sub

'PRODUCTOS - Se dispara al inicializar el manager de productos
Sub manager_BillingSupported (Supported As Boolean, Message As String)
   Log("Supported: " & Supported & ", " & Message)
   Log("Subscriptions supported: " & manager.SubscriptionsSupported)

	If Supported Then
		manager.GetOwnedProducts
	Else
		' no maneja compra de productos
	End If

End Sub

'se dispara al llamar a mangaer.GetOwnedProducts
'Si hay productos comprados no consumidos, los consume 
Sub manager_OwnedProducts (Success As Boolean, purchases As Map)
	Dim iCant As Int=0
	
	'pnlProductos.RemoveAllViews
	'sgbox("Productos:" & purchases.Size , "manager_ownedproducts")
   Log("Cant comprados:" & purchases.size)
   If Success Then
      Log(purchases)
      For Each P As Purchase In purchases.Values
		iCant = iCant +1
		Dim aProductosComprados(iCant) As tProductosComprados 		
		Log(P.ProductId & ", Purchased? " & (P.PurchaseState = P.STATE_PURCHASED))
      	'Productos_AgregaItem(p, iCant)
		aProductosComprados(iCant-1).ProductID = P.ProductId
		aProductosComprados(iCant-1).State = P.PurchaseState
		aProductosComprados(iCant-1).Owned = P.PurchaseState = P.STATE_PURCHASED	
	  Next
	End If
	
	'carga los productos disponibles
	Productos_CargaPlayStore

	'consume los productos que estaban comprados pero no consumidos
	If Productos_ConsumirPendientes (purchases) = False Then
		'marca los productos ya comprados
		Productos_MarcaYaComprados
	Else
		Neuronas_Mostrar
	End If
End Sub

' se ejecuta al finaliza la compra del producto
Sub manager_PurchaseCompleted (Success As Boolean, Product As Purchase)
Try
	
	'Msgbox("Purchase Completed:" & Product.PurchaseState, ""& Success)
	
	' si algo falla, hace rollback de la transaccion 
	If Success Then 
		' si fue exitosa y se compró
		If Product.PurchaseState = Product.STATE_PURCHASED Then
			Productos_CompraHacer (Product)
		Else
			Msgbox("Su compra no fue procesada", "Play Store")
		End If	
	Else
			'Msgbox("Su compra no ha podido ser procesada", "Play Store")
			ToastMessageShow("Compra no procesada", True)
	End If
Catch
	ToastMessageShow("Error en la compra!", True)
End Try
End Sub

'carga manualmente(no se puede automatico) los productos disponibles en el play store
' Abril 2015 se agrega un producto nuevo que es el de compra premium que solo se puede comprar 1 vez

Sub Productos_CargaPlayStore
Dim aProductosPlayStore(7) As tProductosPS
' 200 neuronas sin propaganda
	aProductosPlayStore(0).ProductID = "200_neuronas_adicionales_adsoff"
	aProductosPlayStore(0).ManagedProduct = True
	aProductosPlayStore(0).Price = "0.99" 
	aProductosPlayStore(0).Title="200"
	aProductosPlayStore(0).ApagaPropaganda = True ' indica que este producto incluye el apagado de propaganda
	aProductosPlayStore(0).Owned = False
	aProductosPlayStore(0).neuronas = 200
	aProductosPlayStore(0).ExlusivoPremium = False

'compra de monedas y apagado de propaganda
	aProductosPlayStore(1).ProductID="500_neuronas_adicionales_adsoff"
	aProductosPlayStore(1).ManagedProduct = True
	aProductosPlayStore(1).Price = "1.99"
	aProductosPlayStore(1).Title= " 500"
	aProductosPlayStore(1).ApagaPropaganda = True ' indica si incluye el apagado de propaganda
	aProductosPlayStore(1).Owned = False
	aProductosPlayStore(1).neuronas =500
	aProductosPlayStore(1).ExlusivoPremium = False

'solo compra de monedas
	aProductosPlayStore(2).ProductID="1000_neuronas_adicionales_adsoff"
	aProductosPlayStore(2).ManagedProduct = True
	aProductosPlayStore(2).Price = "2.99"
	aProductosPlayStore(2).Title= "1000"
	aProductosPlayStore(2).ApagaPropaganda = True ' indica que este producto, no incluye el apagado de propaganda
	aProductosPlayStore(2).Owned = False
	aProductosPlayStore(2).neuronas =1000
	aProductosPlayStore(2).ExlusivoPremium = False

'Compra Premium
	aProductosPlayStore(3).ProductID="premium"
	aProductosPlayStore(3).ManagedProduct = True
	aProductosPlayStore(3).Price = "0.99"
	aProductosPlayStore(3).Title= "PREMIUM"
	aProductosPlayStore(3).ApagaPropaganda = True ' indica que este producto, no incluye el apagado de propaganda
	aProductosPlayStore(3).Owned = False
	aProductosPlayStore(3).neuronas =200
	aProductosPlayStore(3).CompraPremiumOriginal = True
	aProductosPlayStore(3).ExlusivoPremium = False
	
' 400 neuronas exclusivo premium
	aProductosPlayStore(4).ProductID = "400_neuronas_premium"
	aProductosPlayStore(4).ManagedProduct = True
	aProductosPlayStore(4).Price = "0.99" 
	aProductosPlayStore(4).Title="400"
	aProductosPlayStore(4).ApagaPropaganda = True ' indica que este producto incluye el apagado de propaganda
	aProductosPlayStore(4).Owned = False
	aProductosPlayStore(4).neuronas =400
	aProductosPlayStore(4).ExlusivoPremium=True
	

'compra de monedas y apagado de propaganda
	aProductosPlayStore(5).ProductID="1000_neuronas_premium"
	aProductosPlayStore(5).ManagedProduct = True
	aProductosPlayStore(5).Price = "1.99"
	aProductosPlayStore(5).Title= " 1000"
	aProductosPlayStore(5).ApagaPropaganda = True ' indica si incluye el apagado de propaganda
	aProductosPlayStore(5).Owned = False
	aProductosPlayStore(5).neuronas =1000
	aProductosPlayStore(5).ExlusivoPremium = True
'solo compra de monedas
	aProductosPlayStore(6).ProductID="2000_neuronas_premium"
	aProductosPlayStore(6).ManagedProduct = True
	aProductosPlayStore(6).Price = "2.99"
	aProductosPlayStore(6).Title= "2000"
	aProductosPlayStore(6).ApagaPropaganda = True ' indica que este producto, no incluye el apagado de propaganda
	aProductosPlayStore(6).Owned = False
	aProductosPlayStore(6).neuronas =2000
	aProductosPlayStore(6).ExlusivoPremium = True

End Sub

' marca en los productos del play store, los que ya fueron comprados (y son managed products) que no pueden ser comprados otra vez.
Sub Productos_MarcaYaComprados
	'recorre los productos comprados y por cada uno, busca en los disponibles en playstore para marcarlos (solo marca los managed para no mostrarlos como disponibles para comprar)
	For j = 0 To aProductosComprados.Length-1
		For i = 0 To aProductosPlayStore.Length-1
			If aProductosComprados(j).ProductID = aProductosPlayStore(i).ProductID Then
				aProductosPlayStore(i).Owned = True
			End If
		Next 
	Next
End Sub

Sub pnlItemP_Click
 Dim Send As Panel, iPos As Int
 Send = Sender

 'Send.Color = Get_aColoresColor(gi_CombinacionColores, 3)
 'Send.TextColor = Get_aTextColor(gi_CombinacionColores, 3)
 
	' guarda la posicion del array aProductosPlayStore seleccionada que está almacendada en el tag del radiobutton clickeado

		
	iProductoElegido = Send.tag
	Producto_Procesar(aProductosPlayStore(iProductoElegido))
	iPantallaActiva = xConfiguraPantalla.jugar
End Sub

'realiza la compra de un producto
Sub Producto_Procesar (ProdPlaystore As tProductosPS)
	Log("request payment executed")
	manager.RequestPayment(ProdPlaystore.ProductID, "inapp", "")
	
	
End Sub

'Verifica si hay algun producto comprado que inclya apagado de propaganda y devuelve true
Sub Productos_GetProductoOwnedAdsOff As Boolean
Dim bret As Boolean = False

For j = 0 To aProductosPlayStore.Length-1
	If aProductosPlayStore(j).Owned And aProductosPlayStore(j).ApagaPropaganda Then
		bret = True
		Exit
	End If
Next

Return bret
End Sub


' recibe el producto comprado en el play store y devuelve el objeto del array de productos playstore aProductosPlayStore
Sub Producto_GetaProductosPlayStore (Product As Purchase) As tProductosPS
Dim xRet As tProductosPS

For j = 0 To aProductosPlayStore.Length-1
	If Product.ProductId = aProductosPlayStore(j).ProductID Then
		xRet = aProductosPlayStore(j)
		Exit
	End If
Next 

Return xRet

End Sub

'ejecuta los pasos de la compra
Sub Productos_CompraHacer(Product As Purchase ) As Boolean
	'que hace:
	' abre una transacción de la base usuario
	' agrega las neuronas en la base
	' si apaga propaganda
		' actualiza seteos con apagado de propaganda
	
	Dim bRet As Boolean=False, xProdPlayStore As tProductosPS
	'busca el producto comprado en el array del playstore y lo guarda en xProdPlayStore
	
		
	xProdPlayStore = Producto_GetaProductosPlayStore(Product)
	

	'abre transaccion base de datos
	g_sqlBaseLocalUsuario.BeginTransaction
		' suma las monedas compradas al usuario
		bRet = Publicos.Usuario_SumarNeuronas(xProdPlayStore.Neuronas, g_sqlBaseLocalUsuario) 
		'Animacion_SumarNeuronas(xProdPlayStore.Neuronas)
		If bRet Then
			' CUALQUIER COMPRA APAGA LA PROPAGANDA, SE QUITA EL IF
			bRet = Publicos.Propaganda_SetApaga(g_sqlBaseLocalUsuario)
			gb_EsPremium = True
			bIniciarPremium = False
			Propaganda_ApagaYa
			Publicos.Base_ActualizaPalabrasDesdeAlmacen(g_sqlBaseLocalJuego, True, True)
			'If False Then
			manager.ConsumeProduct(Product)
			'appfw.eventWithValue("ComprarNeuronas", xtPalabra.idPalabra)

			'End If
		End If
	If bRet Then
		g_sqlBaseLocalUsuario.TransactionSuccessful
		'V2_PantallaConfigura (xConfiguraPantalla.ComproNeuronas, xProdPlayStore.neuronas)
		
	End If
	
	g_sqlBaseLocalUsuario.EndTransaction
		'devolver el producto
	
	gi_NeuronasCompradas = xProdPlayStore.Neuronas
	bComproNeuronas = True
	'appfw.eventWithValue("ComprarNeuronas", lblNeuronas.Text )
	
	Return bRet
End Sub

'este panel hace que no pase nada cuando hace click sobre el panel invisible, si no esta este evento, ejecuta el evento de cualquier view que este abajo del panel
Sub pnlInvisible_click
'NO BORRAR ESTE EVENTO
End Sub

Sub Productos_Muestra

iPantallaActiva = xConfiguraPantalla.Producto

Dim iColor (3) As Int, iColorLetra As Int
iColor(0) = Colors.RGB(247, 219, 245)
iColor(1) = Colors.RGB(242, 198, 241)
iColor(2) = Colors.RGB(238, 183, 237)
iColorLetra = Get_aTextColor(gi_CombinacionColores, 1)


Dim iCant As Int = 0, bPropagandaApagada As Boolean
'verifica si ya tiene comprado algun producto que apague la propaganda
bPropagandaApagada = Productos_GetProductoOwnedAdsOff 
'Propaganda_getApagada 
' x cada productos del play store
pnlInvisible.color = Colors.transparent
'pnlInvisible.RemoveAllViews
pnlInvisible.BringToFront


	'panel para recuadro
	Dim pnlRecuadro As Panel
	pnlRecuadro.Initialize("")
	pnlRecuadro.Color = Colors.LightGray

	
	pnlInvisible.AddView(pnlRecuadro, 0,0,0,0)
	pnlRecuadro.Height = pnlInvisible.Height/2 + 4dip
	pnlRecuadro.Width = pnlInvisible.Width *0.8 + 4dip
	Publicos.CentrarControlEnPanel(pnlInvisible, pnlRecuadro, True, True)
		
'pnlproductos (está creado
	Dim pnlProductos2 As Panel
	pnlProductos2.Initialize("")
	
	pnlRecuadro.AddView(pnlProductos2, 0,0,0,0)
	pnlProductos2.Height = pnlRecuadro.Height-4dip
	pnlProductos2.Width = pnlRecuadro.Width-4dip
	Publicos.CentrarControlEnPanel(pnlRecuadro, pnlProductos2, True, True)
	
	
' lblTituloP
	Dim lblTituloP As Label	
	lblTituloP.Initialize("")
	pnlProductos2.AddView(lblTituloP , 0,0,pnlProductos2.Width, pnlProductos2.Height*0.15)
	lblTituloP.Typeface = tFontOpenSansSemiBold
	lblTituloP.Text = "COMPRA DE NEURONAS"
	Publicos.SetLabelTextSize(lblTituloP , lblTituloP.Text, 40,6,70)
	lblTituloP.Color = gt_Color.colormedio
	lblTituloP.TextColor = Colors.white
	lblTituloP.Gravity = Gravity.CENTER


''lblAclaraP

	Dim lblAclaraP As Label, pnlAclaraP As Panel
	lblAclaraP.Initialize("")
	pnlAclaraP.Initialize("")
	pnlProductos2.AddView(pnlAclaraP,0 ,pnlProductos2.Height*0.64,pnlProductos2.Width, pnlProductos2.Height*0.20)
	pnlAclaraP.AddView(lblAclaraP,pnlProductos2.Width * 0.05,0,pnlProductos2.Width*0.9, pnlAclaraP.Height)
	'lblAclaraP.top = pnlProductos2.Height * 0.66
	lblAclaraP.Typeface = tFontOpenSansSemiBold
	
	If Publicos.Get_EsPremium (g_sqlBaseLocalUsuario) = False Then 
		If Publicos.Get_UsuarioAntiguo(g_sqlBaseLocalUsuario) Then ' si es usuario antiguo, ya tiene todas las palabras!
			lblAclaraP.Text = "Todas las compras incluyen apagado de propaganda "
		Else
			lblAclaraP.Text = "Todas las compras incluyen apagado de propaganda y 200 palabras adicionales"
		End If
	Else
		lblAclaraP.Text = "Compras Exclusivas Versión Premium"
	End If
	
	
	Publicos.SetLabelTextSize(lblAclaraP, lblAclaraP.Text, 40,6,90)
	pnlAclaraP.Color = gt_Color.colorclaro
	lblAclaraP.Color = Colors.transparent
	lblAclaraP.TextColor = Colors.white
	lblAclaraP.Gravity = Gravity.CENTER
	
	
'lblcancelarP
	Dim lblCancelarP As Label
	'SETEA EL TAG CON VUELVE DE PRODUCTO PARA QUE APAGUE EL PANEL DE PRODUCTOS 
	lbl61.Tag = xConfiguraPantalla.Producto
	lblCancelarP.initialize("lbl61")
	lblCancelarP.Color = Colors.white
	lblCancelarP.TextColor = gt_Color.ColorTexto
	pnlProductos2.AddView(lblCancelarP, pnlProductos2.Width *0.05,0, pnlProductos2.Width*0.9, lbl61.Height)
	lblCancelarP.Top = pnlProductos2.Height-lblCancelarP.Height-3dip	
	lblCancelarP.Typeface = tFontOpenSansLight
	lblCancelarP.Text = "CANCELAR"
	Publicos.SetLabelTextSize(lblCancelarP, lblCancelarP.Text, 40,6, 100)
	lblCancelarP.Gravity = Gravity.CENTER

	pnlProductos2.Color = Colors.white
	
Dim iSepara As Int = pnlProductos2.Height*0.02
Dim iTopProd As Int = lblTituloP.Top + lblTituloP.Height  + iSepara
Dim iHeightProd As Int = pnlProductos2.Height*0.14
Dim iTextSize As Int = lblTituloP.TextSize

For j =0 To aProductosPlayStore.Length-1
	' si es un producto que no está comprado (no puede comprar dos veces un producto managed)
	If aProductosPlayStore(j).Owned = False And _ 
			aProductosPlayStore(j).ExlusivoPremium = gb_EsPremium And _ 
			aProductosPlayStore(j).CompraPremiumOriginal = False	Then
		' si es un producto que apaga propaganda, y ya compro alguno que apaga propaganda, no lo muestra
		'If bPropagandaApagada = False OR aProductosPlayStore(j).ApagaPropaganda = False Then
		' se quita el if, porque decidi hacer que todas las compras apaguen la propaganda
			iCant = iCant +1
			Dim vProdDisponible As Label, vProdDisponiblePrecio As Label, pnlItemP As Panel
			
			pnlItemP.Initialize("pnlItemP") 
			vProdDisponible.Initialize("vProdDisponible")  
			vProdDisponiblePrecio.Initialize("vProdDisponiblePrecio")
			vProdDisponible.Text = aProductosPlayStore(j).Title  
			vProdDisponiblePrecio.Text = aProductosPlayStore(j).price
			vProdDisponible.Tag = j ' guarda la posicion del array en el tag
			pnlItemP.Tag = j
			vProdDisponible.TextColor = iColorLetra 'Get_aTextColor(gi_CombinacionColores, 3)
			vProdDisponiblePrecio.Textcolor= iColorLetra'Get_aTextColor(gi_CombinacionColores, 3)
			vProdDisponible.Color = Colors.Transparent
			vProdDisponiblePrecio.Color = Colors.transparent

			pnlItemP.Color = iColor(iCant-1)
			
			vProdDisponible.Gravity = Gravity.right + Gravity.CENTER_VERTICAL
			vProdDisponiblePrecio.Gravity = Gravity.RIGHT + Gravity.CENTER_VERTICAL
			vProdDisponible.Typeface = tFontOpenSansLight
			vProdDisponiblePrecio.Typeface = tFontOpenSansLight
			
			pnlProductos2.addview(pnlItemP, 0, iTopProd + (iCant-1)*(iHeightProd+iSepara), pnlProductos2.Width, iHeightProd)
			pnlItemP.AddView(vProdDisponible, 0, 0, pnlItemP.width*0.3, pnlItemP.Height)
			pnlItemP.AddView(vProdDisponiblePrecio, 0, 0, pnlItemP.Width*0.95 , pnlItemP.Height)
			
			'setea tamaño de la letra
			If j=0 Then
				Publicos.SetLabelTextSize(vProdDisponible, vProdDisponible.Text, 40,6,80)
				vProdDisponiblePrecio.TextSize = vProdDisponible.TextSize
				iTextSize = vProdDisponible.TextSize
			Else
				vProdDisponible.TextSize = iTextSize
				vProdDisponiblePrecio.Textsize = iTextSize
			End If
				
		'agrega la view al array de views, esto si que es original
		'la cisterna se quedó sin agua justo cuando la necesitaba. Ni con una bomba puedo hacer que me suba agua al tanque.	
			Dim avpnlItemP (iCant) As Panel
			avpnlItemP(iCant-1) = pnlItemP
		
		'End If
	End If
Next
pnlAclaraP.bringtofront
pnlInvisible.Visible = True ' que incongruencia
pnlInvisible.SetColorAnimated(1000, Colors.White, Colors.transparent)


pnlProductos2.Visible=True
Dim ileft As Int = pnlRecuadro.Left, iTop As Int = pnlRecuadro.Top, iWidth As Int = pnlRecuadro.Width, iHeight = pnlRecuadro.Height
pnlRecuadro.SetLayoutAnimated(500, Activity.Width/2,0,0,0)
pnlRecuadro.SetLayoutAnimated(1000, ileft, iTop, iWidth, iHeight)

pnlHistoria.Visible = False

End Sub


Sub Propaganda_ApagaYa
'adBC.pauseAd
'adBGris.pauseAd
'adInt.pauseAd

adBC.destroyAd
adBGris.destroyAd
adInt.destroyAd
' appNext 


End Sub

'consume los productos 
Sub Productos_ConsumirPendientes (Purchases As Map) As Boolean
    Dim bRet As Boolean = False
	If Purchases.Size > 0 Then
		If Msgbox2("Tiene compras de neuronas pendientes de aplicar", "Play Store", "Aplicar", "Dejar pendientes", "", Null) = DialogResponse.POSITIVE Then

			For Each P As Purchase In Purchases.Values
				If Productos_CompraHacer (P) Then
					bRet = True
				End If
				Log("producto pendiente procesado: " & P.ProductId)
			Next
		End If	
	End If
Return bRet
End Sub

Sub Costos_Muestra
	lblSaltarPalabraCosto.text =xtNivel.costosaltar
	lblPedirLetraCosto.text = xtNivel.CostoLetra

	Publicos.SetLabelTextSize(lblSaltarPalabraCosto, lblSaltarPalabraCosto.Text, 30,6, 95)
	lblPedirLetraCosto.TextSize = lblSaltarPalabraCosto.textsize
End Sub

Sub Animacion_SumarNeuronas(iNeuronas As Int)
	
	
	Dim imgNeuronas As ImageView
	imgNeuronas.Initialize("")
	pnlInvisible.RemoveAllViews
	pnlInvisible.Color = Colors.Transparent
	pnlInvisible.AddView(imgNeuronas, 0,0,Activity.Width / 3, Activity.Width/3)
	Publicos.CentrarControlEnPanel(pnlInvisible, imgNeuronas, True, True)
	imgNeuronas.SetBackgroundImage(bitmNeurona)

	Dim lblNeuronas As Label
	lblNeuronas.Initialize("")
	pnlInvisible.AddView(lblNeuronas, 0,imgNeuronas.Top+imgNeuronas.Height*1.1, Activity.Width/2, imgNeuronas.height/3)
	lblNeuronas.Text = "SUMAS " & iNeuronas & " NEURONAS" 
	Publicos.SetLabelTextSize(lblNeuronas, lblNeuronas.Text, 40, 6, 100)
	lblNeuronas.color = Colors.White
	lblNeuronas.TextColor = lblv2Def.textcolor
	Publicos.CentrarControlEnPanel(pnlInvisible, lblNeuronas, True, False)
	
	pnlInvisible.Visible=True
	gi_AnimacionEnCurso = 1
	V2_AnimaRotateImageview(imgNeuronas, 0, 720, 3000)
	
	

End Sub

Sub AnimPlus_AnimationEnd

	Select (True)
	Case gi_AnimacionEnCurso = xConfiguraPantalla.ComproNeuronas 

		Panel21.Visible = False
		pnlInvisible.Visible = False
		gi_AnimacionEnCurso=-1
		'anima la neurona chiquita
		V2_AnimaRotateImageview	(imgNeuronas, 0, 2880, 2000)	

	Case gi_AnimacionEnCurso = xConfiguraPantalla.Adivino
		'V2_PantallaConfigura (xConfiguraPantalla.adivino, -2)
		Panel21.Visible = False
		
	End Select
End Sub

Sub V2_AnimaDesplazaImageView (imgAnimar As ImageView, LeftInicio As Int, TopInicio As Int, LeftFin As Int, TopFin As Int, iMilisegundos As Int)
	AnimPlus.InitializeTranslate("AnimPlus", LeftInicio, TopInicio, LeftFin, TopFin)
	
	Activity.invalidate
	AnimPlus.Duration = iMilisegundos
	If AnimPlus.IsInitialized Then
		AnimPlus.Stop(imgAnimar)
		Activity.Invalidate
		AnimPlus.Start(imgAnimar)
	End If
End Sub

' como hay 3 interstitial diferentes, arma al azar con cual de los tres empieza, para que no sea siempre leadbolt
Sub Propaganda_SetAleatoria As Int
Dim iRet As Int

iRet = Rnd(0,1)

Log("Propaganda" & iRet)
Return iRet
End Sub

Sub Productos_MuestraComproNeuronas (iNeuronasCompradas As Int)
pnlInvisible.RemoveAllViews
iPantallaActiva = xConfiguraPantalla.ComproNeuronas

Dim iColor (3) As Int, iColorLetra As Int
iColor(0) = Colors.RGB(247, 219, 245)
iColor(1) = Colors.RGB(242, 198, 241)
iColor(2) = Colors.RGB(238, 183, 237)
iColorLetra = Get_aTextColor(gi_CombinacionColores, 1)

Dim iCant As Int = 0
pnlInvisible.color = Colors.transparent
'pnlInvisible.RemoveAllViews
pnlInvisible.BringToFront
	'panel para recuadro
	Dim pnlRecuadro As Panel
	pnlRecuadro.Initialize("")
	pnlRecuadro.Color = Colors.LightGray

	pnlInvisible.AddView(pnlRecuadro, 0,0,0,0)
		pnlRecuadro.Height = pnlInvisible.Height/2 + 4dip
		pnlRecuadro.Width = pnlInvisible.Width *0.8 + 4dip
		Publicos.CentrarControlEnPanel(pnlInvisible, pnlRecuadro, True, True)
		
	Dim pnlProductos2 As Panel
	pnlProductos2.Initialize("")
	
	pnlRecuadro.AddView(pnlProductos2, 0,0,0,0)
		pnlProductos2.Height = pnlRecuadro.Height-4dip
		pnlProductos2.Width = pnlRecuadro.Width-4dip
		Publicos.CentrarControlEnPanel(pnlRecuadro, pnlProductos2, True, True)
	
	
' lblTituloP
	Dim lblTituloP As Label	
	lblTituloP.Initialize("")
	pnlProductos2.AddView(lblTituloP , 0,0,pnlProductos2.Width, pnlProductos2.Height*0.15)
	lblTituloP.Typeface = tFontOpenSansSemiBold
	lblTituloP.Text = "COMPRA DE " &iNeuronasCompradas & " NEURONAS"
	Publicos.SetLabelTextSize(lblTituloP , lblTituloP.Text, 40,6,70)
	lblTituloP.Color = gt_Color.ColorMedio
	lblTituloP.TextColor = Colors.white
	lblTituloP.Gravity = Gravity.CENTER
'' imagen giratoria
	Dim imgNeuronaGira As ImageView
	imgNeuronaGira.Initialize("")
	pnlProductos2.AddView(imgNeuronaGira,0,0,pnlProductos2.Height*0.5,pnlProductos2.Height*0.5)
	Publicos.CentrarControlEnPanel(pnlProductos2, imgNeuronaGira, True, False)
	imgNeuronaGira.Top = pnlProductos2.Height*0.15
	imgNeuronaGira.SetBackgroundImage(bitmNeurona)
	

''lblAclaraP

	Dim lblAclaraP As Label, pnlAclaraP As Panel
	lblAclaraP.Initialize("")
	pnlAclaraP.Initialize("")
	pnlProductos2.AddView(pnlAclaraP,0 ,pnlProductos2.Height*0.64,pnlProductos2.Width, pnlProductos2.Height*0.20)
	pnlAclaraP.AddView(lblAclaraP,pnlProductos2.Width * 0.05,0,pnlProductos2.Width*0.9, pnlAclaraP.Height)
	'lblAclaraP.top = pnlProductos2.Height * 0.66
	lblAclaraP.Typeface = tFontOpenSansSemiBold
	lblAclaraP.Text = ""
	pnlAclaraP.Color = gt_Color.colormedio
	lblAclaraP.Color = Colors.transparent
	lblAclaraP.TextColor = Colors.white
	lblAclaraP.Gravity = Gravity.CENTER
	
	Dim iNeuronasActuales As Int = lblNeuronas.Text
	iNeuronasActuales = iNeuronasActuales + iNeuronasCompradas
	lblAclaraP.Text =  lblNeuronas.TEXT &"+" & iNeuronasCompradas & "=" &iNeuronasActuales& "  NEURONAS"
	Publicos.SetLabelTextSize(lblAclaraP, lblAclaraP.Text, 40,6,90)

	If lblTituloP.TextSize < lblAclaraP.TextSize Then
		lblAclaraP.TextSize = lblTituloP.TextSize 
	End If

	
'lblcancelarP
	Dim lblCancelarP As Label
	'SETEA EL TAG CON VUELVE DE PRODUCTO PARA QUE APAGUE EL PANEL DE PRODUCTOS 
	lbl61.Tag = xConfiguraPantalla.Producto
	lblCancelarP.initialize("lbl61")
	lblCancelarP.Color = gt_Color.ColorDefault
	lblCancelarP.TextColor = gt_Color.ColorTexto
	pnlProductos2.AddView(lblCancelarP, pnlProductos2.Width *0.05,0, pnlProductos2.Width*0.9, lbl61.Height)
	lblCancelarP.Top = pnlProductos2.Height-lblCancelarP.Height-3dip	
	lblCancelarP.Typeface = tFontOpenSansLight
	lblCancelarP.Text = "SEGUIR"
	Publicos.SetLabelTextSize(lblCancelarP, lblCancelarP.Text, 40,6, 100)
	lblCancelarP.Gravity = Gravity.CENTER
	pnlProductos2.Color = Colors.white

	
	
pnlAclaraP.bringtofront
pnlInvisible.Visible = True ' que incongruencia
pnlInvisible.SetColorAnimated(1000, Colors.White, Colors.transparent)

pnlProductos2.Visible=True
'Dim ileft As Int = pnlRecuadro.Left, iTop As Int = pnlRecuadro.Top, iWidth As Int = pnlRecuadro.Width, iHeight = pnlRecuadro.Height
'pnlRecuadro.SetLayoutAnimated(500, Activity.Width/2,0,0,0)
'pnlRecuadro.SetLayoutAnimated(1000, ileft, iTop, iWidth, iHeight)
	Panel1.Visible = True
	Panel11.Visible = False
	Panel2.Visible= True
	Panel3.Visible = True
	Panel4.Visible = True
	Panel41.Visible = False
	Panel5.Visible = True
	Panel51.Visible = False
	
	imgAnimacion.Visible = False
	lbl21.Visible = False
	
	Panel6.visible = True
	Panel61.Visible = False
	imgLoading.Visible = False

pnlHistoria.Visible = False
V2_AnimaRotateImageview(imgNeuronaGira, 0, 1440, 1500)
End Sub

Sub lbl51MensajeMatete_Click

Select (True)
Case lbl51MensajeMatete.Text.Contains (xtPalabra.Diccionario)
	lbl51MensajeMatete.Text = xtPalabra.Descripcion
	Publicos.SetLabelTextSize(lbl51MensajeMatete, lbl51MensajeMatete.Text, 40,6, 100)
Case lbl51MensajeMatete.Text = xtPalabra.Descripcion 
	If xtPalabra.Palabra.ToUpperCase <> xtPalabra.PalabraDiccionario.ToUpperCase Then
		lbl51MensajeMatete.Text = "(" & xtPalabra.PalabraDiccionario& ")" & Chr(10)
	Else
		lbl51MensajeMatete.Text = "" 
	End If
	lbl51MensajeMatete.Text = lbl51MensajeMatete.Text & xtPalabra.diccionario
	Publicos.SetLabelTextSize(lbl51MensajeMatete, lbl51MensajeMatete.Text, 40,6, 100)
End Select

If lblv2Def.TextSize < lbl51MensajeMatete.textsize Then
	lbl51MensajeMatete.TextSize = lblv2Def.TextSize
End If	

End Sub


Sub ManejaErrorJugar(sRutina As String, sError As String)

'appfw.eventWithValue("Error", sRutina)
Publicos.ManejaError(sRutina, sError)

'appfw.eventWithValue("Error", sRutina)

End Sub

Sub Aviso_Muestra
bAnulaInsterstitialInicial=True

Dim iColorLetra As Int
iColorLetra = Get_aTextColor(gi_CombinacionColores, 1)

iPantallaActiva = xConfiguraPantalla.MuestraAviso

pnlInvisible.color = Colors.transparent
'pnlInvisible.RemoveAllViews
pnlInvisible.BringToFront

	'panel para recuadro
	Dim pnlRecuadro As Panel
	pnlRecuadro.Initialize("")
	pnlRecuadro.Color = Colors.LightGray

	pnlInvisible.AddView(pnlRecuadro, 0,0,0,0)
		pnlRecuadro.Height = pnlInvisible.Height*0.8 +4dip
		pnlRecuadro.Width = pnlInvisible.Width *0.8 + 4dip
		Publicos.CentrarControlEnPanel(pnlInvisible, pnlRecuadro, True, True)
		
'pnlproductos (está creado
	Dim pnlProductos2 As Panel
	pnlProductos2.Initialize("")
	
	pnlRecuadro.AddView(pnlProductos2, 0,0,0,0)
		pnlProductos2.Height = pnlRecuadro.Height-4dip
		pnlProductos2.Width = pnlRecuadro.Width-4dip
		Publicos.CentrarControlEnPanel(pnlRecuadro, pnlProductos2, True, True)
	
	
' lblTituloP
	Dim lblTituloP As Label	
	lblTituloP.Initialize("")
	pnlProductos2.AddView(lblTituloP , 0,0,pnlProductos2.Width, pnlProductos2.Height*0.15)
	lblTituloP.Typeface = tFontOpenSansSemiBold
	lblTituloP.Text = "MATETE DICE"
	Publicos.SetLabelTextSize(lblTituloP , lblTituloP.Text, 40,6,70)
	lblTituloP.Color = gt_Color.ColorMedio
	lblTituloP.TextColor = Colors.white
	lblTituloP.Gravity = Gravity.CENTER
	
'lblcancelarP
	Dim lblCancelarP As Label
	'SETEA EL TAG CON VUELVE DE PRODUCTO PARA QUE APAGUE EL PANEL DE PRODUCTOS 
	lbl61.Tag = xConfiguraPantalla.Producto
	lblCancelarP.initialize("lbl61")
	lblCancelarP.Color = Colors.white
	lblCancelarP.TextColor = gt_Color.ColorMedio
	pnlProductos2.AddView(lblCancelarP, pnlProductos2.Width *0.05,0, pnlProductos2.Width*0.9, lbl61.Height)
	lblCancelarP.Top = pnlProductos2.Height-lblCancelarP.Height-3dip	
	lblCancelarP.Typeface = tFontOpenSansLight
	lblCancelarP.Text = "CONTINUAR"
	Publicos.SetLabelTextSize(lblCancelarP, lblCancelarP.Text, 40,6, 100)
	lblCancelarP.Gravity = Gravity.CENTER

	pnlProductos2.Color = Colors.white

	Dim lblMensaje As Label
	lblMensaje.Initialize("")
	lblMensaje.Typeface = tFontOpenSansLight
	lblMensaje.TextColor = iColorLetra 
	lblMensaje.Gravity = Gravity.CENTER
	pnlProductos2.addview(lblMensaje , 0, pnlProductos2.Height*0.15, pnlProductos2.Width*0.85, pnlProductos2.Height * 0.7)
	Publicos.CentrarControlEnPanel(pnlProductos2,lblMensaje, True, False)
	lblMensaje.Text = xtNivel.MensajeInicio
	Publicos.SetLabelTextSize(lblMensaje, lblMensaje.Text, 30,6, 100)

pnlInvisible.Visible = True ' que incongruencia
pnlInvisible.SetColorAnimated(1000, Colors.White, Colors.transparent)


pnlProductos2.Visible=True
Dim ileft As Int = pnlRecuadro.Left, iTop As Int = pnlRecuadro.Top, iWidth As Int = pnlRecuadro.Width, iHeight = pnlRecuadro.Height
pnlRecuadro.SetLayoutAnimated(500, Activity.Width/2,0,0,0)
pnlRecuadro.SetLayoutAnimated(1000, ileft, iTop, iWidth, iHeight)

pnlHistoria.Visible = False

End Sub


Sub Premium_Muestra

iPantallaActiva = xConfiguraPantalla.Premium

Dim iColor (3) As Int, iColorLetra As Int
iColor(0) = Colors.RGB(247, 219, 245)
iColor(1) = Colors.RGB(242, 198, 241)
iColor(2) = Colors.RGB(238, 183, 237)
iColorLetra = Get_aTextColor(gi_CombinacionColores, 1)


pnlInvisible.color = Colors.transparent
pnlInvisible.BringToFront


	'panel para recuadro
	Dim pnlRecuadro As Panel
	pnlRecuadro.Initialize("")
	pnlRecuadro.Color = Colors.LightGray
	
	pnlInvisible.AddView(pnlRecuadro, 0,0,0,0)
		pnlRecuadro.Height = pnlInvisible.Height
		pnlRecuadro.Width = pnlInvisible.Width 
		Publicos.CentrarControlEnPanel(pnlInvisible, pnlRecuadro, True, True)
		
	Dim pnlContiene As Panel
	pnlContiene.Initialize("")
	
	pnlRecuadro.AddView(pnlContiene, 0,0,0,0)
		pnlContiene.Height = pnlRecuadro.Height-4dip
		pnlContiene.Width = pnlRecuadro.Width-4dip
		Publicos.CentrarControlEnPanel(pnlRecuadro, pnlContiene, True, True)

	
' lblTituloP 
'10%
	Dim lblTituloP As Label	
	lblTituloP.Initialize("")
	pnlContiene.AddView(lblTituloP , 0,0,pnlContiene.Width, pnlContiene.Height*0.10)
	lblTituloP.Top = 0 'imgSuperluper.Top + imgSuperluper.Height
	lblTituloP.Typeface = tFontOpenSansSemiBold
	lblTituloP.Text = "MATETE PREMIUM"
	Publicos.SetLabelTextSize(lblTituloP , lblTituloP.Text, 40,6,70)
	lblTituloP.textColor = gt_Color.ColorMedio
	lblTituloP.Color = Colors.white
	lblTituloP.Gravity = Gravity.CENTER

'imagen superluper
'30%

	Dim imgSuperluper As ImageView
	imgSuperluper.Initialize("")
	pnlContiene.AddView(imgSuperluper, 0, 0, 0, 0)
	imgSuperluper.Top = lblTituloP.TOP + lblTituloP.height
	imgSuperluper.Height = Activity.Height * 0.30
	imgSuperluper.Width = imgSuperluper.Height
	imgSuperluper.Gravity = Gravity.FILL
	Publicos.CentrarControlEnPanel(pnlContiene, imgSuperluper, True, False)
	
	'imgSuperluper.SetBackgroundImage= (LoadBitmap(File.DirAssets, "MateteSuperluper.jpg"))
	imgSuperluper.SetBackgroundImage(LoadBitmap(File.DirAssets, "MateteSuperluper.jpg"))
	


''lblCosasCopadasQueIncluye
'30%
	Dim pnlIncluye As Panel
	Dim lblIncluye(4) As Label
	
	pnlIncluye.Initialize("")
	pnlContiene.AddView(pnlIncluye,0 ,pnlContiene.Height*0.20,pnlContiene.Width, pnlContiene.Height*0.30)
	pnlIncluye.Top = imgSuperluper.Top + imgSuperluper.height
	pnlIncluye.Color = Colors.white 'Get_aColoresColor(gi_CombinacionColores, 3)
	
	Dim iHeightlbl As Int = pnlIncluye.Height * 0.2
	Dim iSepara As Int = pnlIncluye.Height * 0.06			

	For j = 0 To lblIncluye.Length-1
		lblIncluye(j).Initialize("")
		lblIncluye(j).Typeface = tFontOpenSansLight
		lblIncluye(j).Color = Colors.transparent
		lblIncluye(j).TextColor = Get_aTextColor(2,1)
		lblIncluye(j).Gravity = Gravity.CENTER
		pnlIncluye.AddView(lblIncluye(j),pnlContiene.Width * 0.05,0,pnlContiene.Width*0.8, iHeightlbl)
		Publicos.CentrarControlEnPanel (pnlIncluye, lblIncluye(j), True, False)
		If j = 0 Then
			lblIncluye(j).Top = 0
		Else
			lblIncluye(j).top = lblIncluye(j-1).Top + lblIncluye(j-1).Height + iSepara 'la primera vez está en 0 iSEpara
		End If			
	Next
	If Publicos.Get_UsuarioAntiguo(g_sqlBaseLocalUsuario) Then
		lblIncluye(0).Text = "Sin Propaganda"
		lblIncluye(1).Text = "200 Neuronas Adicionales"
		lblIncluye(2).Text = "U$S 0.99"
	Else
		Dim sPalabrasNuevas As String
		sPalabrasNuevas = Publicos.Get_PremiumCantPalabras(g_sqlBaseLocalJuego)
		lblIncluye(0).Text = sPalabrasNuevas.Trim & " Nuevas Palabras"
		lblIncluye(1).Text = "Sin Propaganda"
		lblIncluye(2).Text = "200 Neuronas Adicionales"
		lblIncluye(3).Text = "U$S 0.99"
	End If


	Dim iTextSize As Int
	Publicos.SetLabelTextSize(lblIncluye(2), lblIncluye(2).Text, 40,6,90)
	iTextSize = lblIncluye(2).TextSize
	For j = 0 To lblIncluye.Length-1
		lblIncluye(j).textsize = iTextSize
	Next 
	
'lblconfirmar	
	Dim LblConfirmar As Label
	'SETEA EL TAG CON VUELVE DE PRODUCTO PARA QUE APAGUE EL PANEL DE PRODUCTOS 
	lbl61.Tag = xConfiguraPantalla.Premium
	LblConfirmar.initialize("PremiumComprar")
	LblConfirmar.Color = Colors.white
	LblConfirmar.TextColor = gt_Color.Colortexto
	pnlContiene.AddView(LblConfirmar, pnlContiene.Width *0.05,0, pnlContiene.Width*0.9, lbl61.Height)
	LblConfirmar.Top = Activity.Height*0.75
	LblConfirmar.Typeface = tFontOpenSansSemiBold
	LblConfirmar.Text = "COMPRAR"
	Publicos.SetLabelTextSize(LblConfirmar, LblConfirmar.Text, 40,6, 100)
	LblConfirmar.Gravity = Gravity.CENTER

	
	
	
'lblcancelarP
	Dim lblCancelarP As Label
	'SETEA EL TAG CON VUELVE DE PRODUCTO PARA QUE APAGUE EL PANEL DE PRODUCTOS 
	lbl61.Tag = xConfiguraPantalla.Premium
	lblCancelarP.initialize("lbl61")
	lblCancelarP.Color = Colors.white
	lblCancelarP.TextColor = gt_Color.Colortexto
	pnlContiene.AddView(lblCancelarP, pnlContiene.Width *0.05,0, pnlContiene.Width*0.9, lbl61.Height)
	lblCancelarP.Top = LblConfirmar.Top + LblConfirmar.Height *1.3
	lblCancelarP.Typeface = tFontOpenSansLight
	lblCancelarP.Text = "CANCELAR"
	'Publicos.SetLabelTextSize(lblCancelarP, lblCancelarP.Text, 40,6, 100)
	lblCancelarP.TextSize = LblConfirmar.TextSize
	lblCancelarP.Gravity = Gravity.CENTER

	pnlContiene.Color = Colors.white
	
Dim iSepara As Int = pnlContiene.Height*0.02
Dim iTopProd As Int = lblTituloP.Top + lblTituloP.Height  + iSepara
Dim iHeightProd As Int = pnlContiene.Height*0.14
Dim iTextSize As Int = lblTituloP.TextSize

pnlIncluye.bringtofront
pnlInvisible.Visible = True ' que incongruencia
pnlInvisible.SetColorAnimated(1000, Colors.White, Colors.transparent)


pnlContiene.Visible=True
Dim ileft As Int = pnlRecuadro.Left, iTop As Int = pnlRecuadro.Top, iWidth As Int = pnlRecuadro.Width, iHeight = pnlRecuadro.Height
pnlRecuadro.SetLayoutAnimated(500, Activity.Width/2,0,0,0)
pnlRecuadro.SetLayoutAnimated(1000, ileft, iTop, iWidth, iHeight)


pnlHistoria.Visible = False

End Sub

Sub PremiumComprar_Click
	Producto_Procesar(aProductosPlayStore(gi_ProductoPremium))
End Sub


Sub Get_QuedanPalabrasAlmacen As Boolean
Dim bRet As Boolean
	If Publicos.get_CantidadPalabrasAlmacen(g_sqlBaseLocalJuego, True) > Publicos.get_CantidadPalabras(g_sqlBaseLocalJuego) Then
		bRet = True
	End If
Return bRet
End Sub

Sub BaseRemota_GrabarAvance(idPalabra As Int)
	Dim Cursor1 As Cursor, Ssql As String, sInsert As String, sQueryLocal As String
	Try
		'busca puntos y monedas
		Ssql ="Select inicio, fin, adivinada, salteada from avance where idpalabra = " & idPalabra
		Cursor1 = g_sqlBaseLocalUsuario.ExecQuery(Ssql)

		'busca el avance local y prepara el insert para la base remota
		If Cursor1.RowCount >0 Then
			Cursor1.Position = 0
		
			sInsert = "call Avance_IU ('" & Publicos.GetUsuario & "','" & Publicos.getIMEI & "'," & idPalabra & _ 
					",'" & Cursor1.GetString2(0) & "','" & Cursor1.GetString2(1)  & _ 				
					"'," & Cursor1.GetInt2(2) & "," & Cursor1.GetInt2(3) & ",'')"
		
		
			sQueryLocal = "Update Avance set registroenviado = 1 where idPalabra = " & idPalabra
			Remoto_EjecutarNonQuery (sInsert, sQueryLocal, xQuerysRemotos.Avance)
		End If	
	    Cursor1.Close
	
	Catch 
		ManejaErrorJugar("MuestraPuntos","MuestraPuntos")
	End Try





End Sub


Sub Remoto_EjecutarNonQuery(sQuery As String, sQueryLocal As String, taskId As Int)
Dim req As HttpRequest
' si no está ejecutando otro query
If IsBackgroundTaskRunning(httpRemoto, taskId) = False Then 
	aQuerysLocales(taskId) = sQueryLocal
	
	req.InitializePost2(sgDireccionRemota, sQuery.GetBytes("UTF8"))
	httpRemoto.Execute(req, taskId)
End If

End Sub


Sub httpRemoto_ResponseSuccess (Response As HttpResponse, tarea As Int)
Dim resultString As String
resultString = Response.GetString("UTF8")

'Msgbox("Los datos han sido almacenados", "Éxito de operación")
If aQuerysLocales(tarea) <>"" Then
	Try
		g_sqlBaseLocalUsuario.ExecNonQuery(aQuerysLocales(tarea))	
		aQuerysLocales(tarea) = ""
	Catch
		Log("Error query local ok remoto")
	End Try

End If
End Sub

Sub httpRemoto_ResponseError (Response As HttpResponse, Reason As String, StatusCode As Int, tarea As Int)
Log("Error: " & Reason & ", StatusCode: " & StatusCode)
If Response <> Null Then
Log(Response.GetString("UTF8"))
Response.Release
End If
End Sub 

Sub Historia_Muestra(sDireccion As String)

	iPantallaActiva = xConfiguraPantalla.Historia

	Dim iColor (3) As Int, iColorLetra As Int
	iColor(0) = Colors.RGB(247, 219, 245)
	iColor(1) = Colors.RGB(242, 198, 241)
	iColor(2) = Colors.RGB(238, 183, 237)
	iColorLetra = Get_aTextColor(gi_CombinacionColores, 1)

	pnlHistoria.Visible = True
	pnlHistoria.BringToFront
	imgLoading.Visible = False
	'lblFiltro.Tag = "SALTADOS" 'FUERZO SALTADOS QUE SE LLENA MAS RAPIDO, PARA QUE NO TENGA QUE ESPERAR SI ESTA MUY AVANZADO
	Historia_llenaScroll(pnlHistoria, lblFiltro.tag = "SALTADOS", sDireccion )
	
	lbl11.Text = "MATETES JUGADOS"
	lbl11.TextColor= iColorLetra
	lbl61.Text = "VOLVER"

	Panel61.Color =  gt_Color.Colordefault
	Panel61.Visible = True
	Panel61.BringToFront
	Panel11.Visible = True
	lbl61.TextColor = gt_Color.ColorTexto
		
	imgShadow.Top = Panel11.Top + Panel11.Height

	lblFiltro.TextColor = Get_aTextColor(gi_CombinacionColores,1)
	'lbl61.Color = Get_aColoresColor(gi_CombinacionColores, 4)
	Activity.Color = Colors.White 'Get_aColoresColor(gi_CombinacionColores, 4)
	pnlInvisible.Visible = False

End Sub

' sdireccion "ARRIBA" o "ABAJO" dependiendo de si apretó el boton de arriba o abajo. Si repite lo mismo, "igual"
Sub Historia_llenaScroll(pnlScroll As Panel, bSaltadas As Boolean, sDireccion As String)
	Dim Bitmap1 As Bitmap, iCantPalabras As Int, Panel0 As Panel
	Dim PanelTop, PanelHeight, Label2Top, ProgressBarTop, ProgressBarWidth As Int, PanelWidth As Int
	Dim labelHeight As Int, labelWidth As Int
	Dim Progress As Int, ProgressBar1Top As Int, ProgressBarHeight As Int
	Dim sAnd As String, iPanelScrollHeight As Int = Activity.Height - (lbl11.Top + lbl11.Height) - (Activity.Height -Panel61.top)
	Dim iCantRegistrosMuestra As Int = 20 'cantidad de registros que se muestran en el scroll

	'gi_Historia_PunteroAbajo puntero (idpalabra) mas grande que mostró. 
	If scvMain.IsInitialized Then
		scvMain.RemoveView
	End If
	scvMain.Initialize(500)
	
	'pnlScroll.RemoveAllViews
	scvMain.Panel.RemoveAllViews
	
	'pone el scroll abajo del panel que tiene los filtros de la historia
	pnlScroll.AddView(scvMain,0,pnlHistFiltro.Height,pnlScroll.width,iPanelScrollHeight)
	scvMain.Panel.Width = pnlScroll.Width
	scvMain.Panel.Height = iPanelScrollHeight
	scvMain.Color = Colors.white
	
	
	Panel0=scvMain.Panel
	Panel0.Color=Colors.white
	pnlHistoria.Color = Colors.white

	PanelTop=0
	PanelWidth = scvMain.Width
	PanelHeight = iPanelScrollHeight*0.2
	labelWidth = PanelWidth * 0.75
	labelHeight = PanelHeight * 0.7
	ProgressBar1Top= PanelHeight*0.8
	ProgressBarWidth=labelWidth
	ProgressBarHeight = PanelHeight* 0.1

	'crea filtros
	lblFiltro.TextSize = lbl11.TextSize -2


	'trae las primeras iCantRegistrosMuestra +1 (trae uno de mas, porque si tiene uno mas, tiene que habilitar el scroll hacia abajo.
	Dim sSql As String, Cursor1 As Cursor, crDef As Cursor
	If bSaltadas Then 
		Select sDireccion
		Case "ABAJO"
			sSql ="Select idPalabra, adivinada, salteada from avance where fin is not null and salteada = 1 and idPalabra>" & gi_Historia_P_Abajo & " order by idPalabra limit " & (iCantRegistrosMuestra +1)
		Case "ARRIBA"
			sSql ="Select idPalabra, adivinada, salteada from avance where fin is not null and salteada = 1 and idPalabra<" & gi_Historia_P_Arriba & " order by idPalabra DESC limit " & (iCantRegistrosMuestra +1)
		Case Else 'no se mueve, vuelve a la pantalla para mostrar lo mismo
			sSql ="Select idPalabra, adivinada, salteada from avance where fin is not null and salteada = 1 and idPalabra>=" & gi_Historia_P_Arriba & " order by idPalabra limit " & (iCantRegistrosMuestra +1)
		End Select
	Else
		Select sDireccion
		Case "ABAJO"
			sSql ="Select idPalabra, adivinada, salteada from avance where fin is not null and idPalabra>" & gi_Historia_P_Abajo & " order by idPalabra limit " & (iCantRegistrosMuestra+1)
		Case "ARRIBA"
			sSql ="Select idPalabra, adivinada, salteada from avance where fin is not null and idPalabra<" & gi_Historia_P_Arriba & " order by idPalabra DESC limit " & (iCantRegistrosMuestra+1)
		Case Else 'no se mueve, muestra las mismas paralabras, por ejemplo si volvió con un back space
			sSql ="Select idPalabra, adivinada, salteada from avance where fin is not null and idPalabra>=" & gi_Historia_P_Arriba & " order by idPalabra limit " & (iCantRegistrosMuestra+1)
		End Select
	End If
	
	
	Cursor1 = g_sqlBaseLocalUsuario.ExecQuery(sSql)

	Dim labelSize As Label , i As Int, iLimiteSuperior As Int , iLimiteInferior As Int
	labelSize.Initialize("")
	pnlScroll.AddView(labelSize,0,0, labelWidth, labelHeight)

	
	If Cursor1.RowCount >0 Then
		'iCantPalabras = Publicos.get_CantidadPalabras(g_sqlBaseLocalJuego)
		
		'hace este loop para calcular el tamaño de la letra para todos los items iguales.

		
		'define limites. Si apretó para arriba, arma la info al reves, porque esta ordenada al reves en el query
		If Cursor1.RowCount -1 < iCantRegistrosMuestra Then
			iLimiteSuperior = Cursor1.RowCount -1
		Else
			iLimiteSuperior = iCantRegistrosMuestra -1
		End If 
		If sDireccion = "ABAJO" Or sDireccion = "IGUAL" Then
			iLimiteInferior = 0
			Dim xHistoria(iLimiteSuperior+1) As tHistoria ' en este array guarda todas las palabras

		Else
			iLimiteInferior = iLimiteSuperior
			iLimiteSuperior = 0
			Dim xHistoria(iLimiteInferior+1) As tHistoria ' en este array guarda todas las palabras
		End If
		
		Dim icursor As Int=0
		i=iLimiteInferior
		'For i = iLimiteInferior To iLimiteSuperior  ' por cada palabra 
		Do While icursor <= iLimiteInferior+iLimiteSuperior
			Cursor1.Position = icursor
			icursor = icursor+1
			
			
			' por cada palabra adivinada busca la definicion en la base del juego
			crDef = g_sqlBaseLocalJuego.ExecQuery("Select descripcion from palabras where idpalabra= "&Cursor1.GetInt2(0))
			If crDef.RowCount > 0 Then
				crDef.Position=0
				xHistoria(i).idPalabra = Cursor1.GetInt2(0)
				xHistoria(i).Adivinada = Cursor1.GetInt2(1)
				xHistoria(i).Salteada = Cursor1.GetInt2(2)
				xHistoria(i).Definicion = crDef.GetString2(0)
				labelSize.Text = xHistoria(i).Definicion.Trim
				Publicos.SetLabelTextSize(labelSize, labelSize.Text, 18, 4, 100)
				If labelSize.TextSize < gi_Historia_TextSize Then
					gi_Historia_TextSize = labelSize.TextSize
				End If
			End If	
			crDef.Close
			If iLimiteSuperior>iLimiteInferior Then 'ordenado de menor a mayor
				i=i+1
			Else 'de mayor a menor
				i=i-1
			End If

		
		Loop	
		labelSize.RemoveView 		
		
		If iLimiteSuperior>iLimiteInferior Then
			gi_Historia_P_Abajo = xHistoria(iLimiteSuperior).idPalabra
			gi_Historia_P_Arriba = xHistoria(iLimiteInferior).idPalabra
		Else
			gi_Historia_P_Abajo = xHistoria(iLimiteInferior).idPalabra
			gi_Historia_P_Arriba = xHistoria(iLimiteSuperior).idPalabra
		End If
	
	
		Panel0.RemoveAllViews
		' si tiene que agregar el que va para arriba
		If bSaltadas Then
			lblFiltro.Text = "SALTADOS" ' & i& "]"
			sAnd = " AND salteada = 1"
		Else	
			lblFiltro.Text = "TODOS" ' & i & "]"
		End If
	'si hay palabras anteriores agrega un item para cargar las anteriores.
		If Publicos.Get_HayPalabrasAnteriores(g_sqlBaseLocalUsuario, xHistoria(0).idPalabra, sAnd)  Then
			Dim xPanelItem As Panel, ImageView1 As ImageView
			Historia_llenaScroll_Item (xPanelItem, gi_Historia_TextSize, xHistoria(0), labelWidth, labelHeight, ImageView1, "ANTERIORES", _ 
						Panel0, PanelTop, PanelWidth, PanelHeight, pnlScroll, "HistoriaClickArriba", iPanelScrollHeight)
			PanelTop=PanelTop+PanelHeight+1dip			
		End If
		
		For i = 0 To xHistoria.Length-1			
			If xHistoria(i).Definicion <> "" Then		
			
				Dim PanelItem As Panel, ImageView1 As ImageView
				''AGREGA UN ITEM
				Historia_llenaScroll_Item (PanelItem, gi_Historia_TextSize, xHistoria(i), labelWidth, labelHeight, ImageView1, "", _ 
						Panel0, PanelTop, PanelWidth, PanelHeight, pnlScroll, "HistoriaClick", iPanelScrollHeight)
				
				PanelTop=PanelTop+PanelHeight+1dip
			
			End If
			
		Next
		'si hay mas palabras para abajo
		If xHistoria.Length >= iCantRegistrosMuestra Then 'AND  Publicos.Get_HayPalabrasPosteriores(g_sqlBaseLocalUsuario, xHistoria(xHistoria.Length-1).idPalabra, sAnd) Then
		
			Dim PanelItem As Panel, ImageView1 As ImageView
			Historia_llenaScroll_Item (PanelItem, gi_Historia_TextSize, xHistoria(0), labelWidth, labelHeight, ImageView1, "SIGUIENTES", _ 
						Panel0, PanelTop, PanelWidth, PanelHeight, pnlScroll, "HistoriaClickAbajo", iPanelScrollHeight)
		End If 		
	Else	
		'si no hay items, carga uno fantasma
			Dim xh As tHistoria, PanelItem As Panel, ImageView1 As ImageView
			Historia_llenaScroll_Item (PanelItem, gi_Historia_TextSize, xh, labelWidth, labelHeight, ImageView1, "NO HAY MATETES", _ 
						Panel0, PanelTop, PanelWidth, PanelHeight, pnlScroll, "SinHistoriaClick", iPanelScrollHeight)
	
	End If
	Cursor1.close
	If Panel0.Height < PanelTop Then
		Panel0.Height=PanelTop+Panel0.Height*0.2 + Panel61.height
	End If
	Panel0.Height = Panel0.Height
	pnlScroll.Height = Panel0.Height
	
End Sub
Sub Historia_llenaScroll_Item(PanelItem As Panel, iTextSizeMenor As Int, xHistoria As tHistoria, _ 
	labelwidth As Int, labelheight As Int, ImageView1 As ImageView, sDescPisa As String, Panel0 As Panel, _ 
		paneltop As Int, panelwidth As Int, panelheight As Int, pnlscroll As Panel, sEvento As String, iPanelScrollHeight As Int)

		PanelItem.Initialize(sEvento)
		Panel0.AddView(PanelItem,0,paneltop,panelwidth,panelheight)
		PanelItem.Color=Colors.white

		If sDescPisa <> "" Then ' si es el anteriores o siguientes
			PanelItem.Height= iPanelScrollHeight/5
		Else
			PanelItem.Height= iPanelScrollHeight/5
		End If
		
		' si fue saltada pone el id de la palabra en negativo
		If xHistoria.Salteada = 1 Then
			PanelItem.Tag = xHistoria.idpalabra*-1
		Else
			PanelItem.Tag = xHistoria.idpalabra
		End If

		'linea separacion
		Dim separa As Panel 
		separa.Initialize("")
		PanelItem.AddView(separa,0,0, PanelItem.Width, 1dip)
		separa.Color = Colors.DarkGray
		
		
		Dim Label1 As Label
		Label1.Initialize("")
		PanelItem.AddView(Label1,PanelItem.width*0.05,PanelItem.Height*0.1, labelwidth, PanelItem.Height)
		Publicos.CentrarControlEnPanel(PanelItem, Label1, False, True)
		
		Label1.Color=Colors.transparent
		Label1.textsize = iTextSizeMenor
		Label1.Gravity = Gravity.CENTER
		
		ImageView1.Initialize("")
		PanelItem.AddView(ImageView1, PanelItem.Width*0.85, PanelItem.Height*0.1, PanelItem.width*0.1, PanelItem.width*0.1)
		Publicos.CentrarControlEnPanel(PanelItem, ImageView1, False, True)
	
		ImageView1.Gravity = Gravity.FILL

		If sDescPisa = "NO HAY MATETES" Then
			Label1.Text = sDescPisa
			Publicos.CentrarControlEnPanel(PanelItem,Label1,True,True)
			Publicos.SetLabelTextSize (Label1, Label1.Text, lbl61.textsize,6, 100)

		Else
			If sDescPisa <>"" Then ' si manda una descripcion, no es una palabra, es uno de los botones de abajo o arriba
				Publicos.CentrarControlEnPanel(PanelItem,Label1,True,True)
				'Label1.Text = sDescPisa
				
				Publicos.SetLabelTextSize (Label1, Label1.Text, lbl61.textsize,6, 100)
				If sDescPisa = "ANTERIORES" Then
					ImageView1.Bitmap = gt_Color.bitmSubir		
				Else
					ImageView1.Bitmap = gt_Color.bitmBajar
				End If
				Publicos.CentrarControlEnPanel(PanelItem, ImageView1, True, True)
			Else
				Label1.Tag=xHistoria.idPalabra
				If Publicos.GetUsuario ="JUAN.DAROCHA@GMAIL.COM" Then
					Label1.Text= xHistoria.idPalabra & " " & xHistoria.Definicion
				Else
					Label1.Text= xHistoria.Definicion
				End If
				If xHistoria.Adivinada = 1 Then
					ImageView1.Bitmap=bitmVerdeConfirmar
				Else
					ImageView1.Bitmap=bitmRojoCancelar
				End If
			End If
		End If		
	
		'If xHistoria(i).Adivinada = 1 Then
		Label1.TextColor = Get_aTextColor(gi_CombinacionColores, 2)
		'Else
		'	Label1.TextColor = Colors.red
		'End If
		



End Sub
Sub lblFiltro_click
	gi_Historia_P_Abajo =-1
	gi_Historia_P_Arriba = -1
	
	If lblFiltro.TAG = "TODOS" Then
		lblFiltro.Text = "SALTADOS"
		lblFiltro.Tag = "SALTADOS"
	Else
		lblFiltro.Text = "TODOS"
		lblFiltro.Tag = "TODOS"
	End If
	Historia_Muestra("IGUAL")
End Sub

Sub HistoriaClick_Click
Dim Send As Panel
 Send = Sender'
Dim idPalabra As Int = Send.tag
Dim iSaltada As Boolean = False
If idPalabra <0 Then
	iSaltada = True
End If

V2_PantallaConfigura(xConfiguraPantalla.Jugar,0)
v2_MuestraNuevaPalabra (Abs(idPalabra), iSaltada, True)


End Sub

Sub Remoto_UpdatePremium
'Usuario_rem_iu(In pMail varchar(50), In pPremium Int, In  palta DateTime, In  ppremiumdesde DateTime, In ppremiumregalado  bool, In hash integer)
Dim sSqlRemoto As String, sSqlLocal As String, sFecha As String
sFecha = DateTime.Date(DateTime.now)

sSqlRemoto = "Call Usuario_rem_iu('" & Publicos.GetUsuario & "', 1,'" & sFecha & "','" & sFecha & "',0,'')" 
'graba en la base local que fue exitoso el query remoto
sSqlLocal = "Insert Into Seteosusuario (tiposeteo, fechadesde) values ('premiumremoto','" & sFecha & "')"

Remoto_EjecutarNonQuery(sSqlRemoto, sSqlLocal, xQuerysRemotos.Premium)

End Sub


Sub Create_Variables
	xConfiguraPantalla= CreateTypeConfiguraPantalla(0,1,2,3, 4, 5, 6, 7, 8 , 9, 10, 11, 12)
	xQuerysRemotos = CreateTypeQuerysRemotos(0,1)
	tFontOpenSansSemiBold= Typeface.LoadFromAssets("OpenSans-Semibold.ttf")
	tFontOpenSansLight= Typeface.LoadFromAssets("OpenSans-Light.ttf")
	Inicializa_ColoresPaleta
End Sub

Sub CheckPremium
'verifica si el tipo o la tipa es premium 
'El premium está asociado al usuario (gmail), por lo tanto, al hacerse premium en un celular, se hace premium en todos.
'para hacerlo, el dato de premium se graba en la base remota (y cuando se graba en la base remota, se graba tambien en la local un registron en seteosusuario para registrar que ya está grabado en la base remota)
'con todo esto:
' Publicos.Get_EsPremiumGrabadoRemoto devuelve true si es premium y está grabado en la base remota.
' Publicos.Get_EsPremium devuelve true si es premium esté o no grabado en la base remota
' si es premium pero no está en la base remota, lo manda a grabar a la base remota, la pucha.	

'setea variable global para informar que es premium
gb_EsPremium = (Publicos.Get_EsPremium(g_sqlBaseLocalUsuario ))
'si es premium, pero no está grabado remoto, manda a grabar
If 	gb_EsPremium  Then
	If Publicos.Get_EsPremiumGrabadoRemoto(g_sqlBaseLocalUsuario) = False Then
		Remoto_UpdatePremium
	End If
Else ' si no es premium local, consulta remoto. Si lo encuentra, actualiza base local
	
	Dim sSql As String = "CALL Usuario_rem_s ('" & Publicos.GetUsuario & "', '')"
	RemotoSelect (sSql, xQuerysRemotos.Premium)

End If

If Publicos.Get_SoyYo = True Then
	gb_EsPremium = False
End If


End Sub

Sub RemotoSelect (sSql As String, taskid As Int)
	Dim req As HttpRequest
	req.InitializePost2(sgDireccionRemota, sSql.GetBytes("UTF8"))
	httpRemotoSelect.Execute(req, taskid) '''''''' En ResponseSuccess recibe los datos
End Sub

Sub httpRemotoSelect_ResponseError (Response As HttpResponse, Reason As String, StatusCode As Int, tarea As Int)
Log("Error: " & Reason & ", StatusCode: " & StatusCode)
If Response <> Null Then
Log(Response.GetString("UTF8"))
Response.Release
End If
ProgressDialogHide
End Sub



' Esta es la Respuesta de la orden hc.Execute
Sub httpRemotoSelect_ResponseSuccess (Response As HttpResponse, tarea As Int)
Dim res As String
res = Response.GetString("UTF8")
'Log("Respuesta del servidor: " & res)
Dim parser As JSONParser
parser.Initialize(res)

Select tarea


	' ************* PREMIUM *****************
	Case xQuerysRemotos.premium
	Try
		Dim Registros As List, iPremium As Int
		Registros.Initialize
		Registros = parser.NextArray
		'solo debe devolver 1 registro de premium
		If Registros.Size >0 Then
			Dim m As Map
			m = Registros.Get(0)
			iPremium= m.Get("Premium")      ' ES
			gb_PremiumRemoto = iPremium >0
			gb_EsPremium = gb_PremiumRemoto And Publicos.Get_SoyYo = False
			If gb_PremiumRemoto And Publicos.get_soyyo = False Then
				'actualiza base local
				'Publicos.SeteoUsuarioTexto_Inserta (g_sqlBaseLocalUsuario, "premium", "grabadoremoto")
				Dim sFecha As String
				DateTime.DateFormat ="yyyy-MM-dd hh:mm:ss"
				sFecha = DateTime.Date(DateTime.now)
				g_sqlBaseLocalUsuario.ExecNonQuery("Insert Into Seteosusuario (TipoSeteo, texto, fechadesde) values ('premiumremoto','desderemoto', '" & sFecha & "')")
				Propaganda_ApagaYa
			End If

		End If
	Catch
		Publicos.ManejaError("httpRemotoSelect_Response", "Matete Divergente R")
	End Try
End Select
Response.Release
End Sub

Sub lblv2def_Click
If Publicos.GetUsuario = "JUAN.DAROCHA@GMAIL.COM" Then
	Dim ssql As String
	ssql = "Update Avance set fin = null, adivinada=0, Salteada =0 where idpalabra = (select max(idpalabra) from avance)"
	g_sqlBaseLocalUsuario.ExecNonQuery(ssql)
End If

End Sub

Sub HistoriaClickAbajo_Click
	Historia_Muestra ("ABAJO")

End Sub


Sub HistoriaClickArriba_Click
	Historia_Muestra("ARRIBA")

End Sub

Sub Get_ImagenColor(sImagen As String) As Bitmap
	'Return LoadBitmap(g_DirGrabable, sImagen)
	Dim bitRet As Bitmap
	bitRet = LoadBitmap(File.DirAssets, sImagen)
	Return bitRet
End Sub


Sub Set_ColoresVariable(Color As Int)
	gt_Color.bitmBajar = Get_ImagenColor(gt_ColoresPaleta(Color).ColorNombre & "-Bajar.png")
	gt_Color.bitmBajarLetras = Get_ImagenColor(gt_ColoresPaleta(Color).ColorNombre & "-Bajar-Letras.png")
	gt_Color.bitmCompartir = Get_ImagenColor(gt_ColoresPaleta(Color).ColorNombre & "-btn-compartir.png")
	gt_Color.bitmCostos = LoadBitmapSample(File.DirAssets, gt_ColoresPaleta(Color).ColorNombre & "-Costos.png", 100, 100)
	gt_Color.bitmFlag = Get_ImagenColor(gt_ColoresPaleta(Color).ColorNombre & "-icon-flag.png")
	gt_Color.bitmLetra = Get_ImagenColor(gt_ColoresPaleta(Color).ColorNombre & "-letra-off-borde.png")
	gt_Color.bitmMenu = Get_ImagenColor(gt_ColoresPaleta(Color).ColorNombre & "-btn-menu.png")
	gt_Color.bitmMenuFondo = Get_ImagenColor(gt_ColoresPaleta(Color).ColorNombre & "-Menu-Fondo.png")
	gt_Color.bitmMute = Get_ImagenColor(gt_ColoresPaleta(Color).ColorNombre & "-btn-mute.png")
	gt_Color.bitmPalabraComprada = Get_ImagenColor(gt_ColoresPaleta(Color).ColorNombre & "-Palabra-Comprada-Borde.png")
	gt_Color.bitmPalabraError = Get_ImagenColor(gt_ColoresPaleta(Color).ColorNombre & "-Error-Borde.png")
	gt_Color.bitmPalabraOff = Get_ImagenColor(gt_ColoresPaleta(Color).ColorNombre & "-Palabra-Off-Borde.png")
	gt_Color.bitmPalabraOn = Get_ImagenColor(gt_ColoresPaleta(Color).ColorNombre	 & "-Palabra-On-Borde.png")
	gt_Color.bitmPedir = Get_ImagenColor(gt_ColoresPaleta(Color).ColorNombre	& "-btn-pedir.png")
	gt_Color.bitmSaltarPalabra = Get_ImagenColor(gt_ColoresPaleta(Color).ColorNombre & "-btn-resolver.png")
	gt_Color.bitmShareCancelar = Get_ImagenColor(gt_ColoresPaleta(Color).ColorNombre	 & "-Share-Cancelar.png")
	gt_Color.bitmShareConfirmar = Get_ImagenColor(gt_ColoresPaleta(Color).ColorNombre & "-Share-Confirmar.png")	
	gt_Color.bitmSubir = Get_ImagenColor(gt_ColoresPaleta(Color).ColorNombre & "-Subir.png"	)
	gt_Color.bitmShareFB= Get_ImagenColor(gt_ColoresPaleta(Color).ColorNombre & "-Share-Fb.png"	)
	gt_Color.bitmShareTw = Get_ImagenColor(gt_ColoresPaleta(Color).ColorNombre & "-Share-Tw.png")
	gt_Color.ColorClaro = gt_ColoresPaleta(Color).ColorClaro
	gt_Color.ColorMedio = gt_ColoresPaleta(Color).ColorIntermedio
	gt_Color.ColorOscuro = gt_ColoresPaleta(Color).ColorOscuro
	gt_Color.ColorTexto = gt_ColoresPaleta(Color).ColorTexto
	gt_Color.ColorNombre = gt_ColoresPaleta(Color).ColorNombre
	gt_Color.ColorDefault = gt_ColoresPaleta(Color).colorDefault
	gt_Color.Paleta = Color 
	
End Sub

Sub Inicializa_ColoresPaleta
	gt_ColoresPaleta(0).ColorNombre= "Violeta"
	gt_ColoresPaleta(0).ColorClaro = Colors.RGB(190, 124, 190)
	gt_ColoresPaleta(0).ColorIntermedio =Colors.RGB(167, 99, 167)
	gt_ColoresPaleta(0).ColorOscuro= Colors.RGB(151, 89, 152)
	gt_ColoresPaleta(0).ColorTexto = Colors.RGB(151, 89, 152)
	gt_ColoresPaleta(0).colorDefault = Colors.white
	
	

	gt_ColoresPaleta(1).ColorNombre= "Rojo"
	gt_ColoresPaleta(1).ColorClaro = Colors.RGB(242, 72, 77)
	gt_ColoresPaleta(1).ColorIntermedio  = Colors.RGB(241, 46, 51)
	gt_ColoresPaleta(1).ColorOscuro= Colors.RGB(223, 17, 28)
	gt_ColoresPaleta(1).ColorTexto = Colors.RGB(223, 17, 28)
	gt_ColoresPaleta(1).colorDefault = Colors.white
	
	gt_ColoresPaleta(2).ColorNombre= "Azul"
	gt_ColoresPaleta(2).ColorClaro = Colors.RGB(119, 126, 219)
	gt_ColoresPaleta(2).ColorIntermedio  = Colors.RGB(87, 97, 210)
	gt_ColoresPaleta(2).ColorOscuro= Colors.RGB(63, 72, 204)
	gt_ColoresPaleta(2).ColorTexto = Colors.RGB(63, 72, 204)
	gt_ColoresPaleta(2).colorDefault = Colors.white
	
	gt_ColoresPaleta(3).ColorNombre = "Verde"
	gt_ColoresPaleta(3).ColorClaro = Colors.RGB(80, 222, 122)
	gt_ColoresPaleta(3).ColorIntermedio  = Colors.RGB(34, 177, 76)
	gt_ColoresPaleta(3).ColorOscuro= Colors.RGB(29, 150, 65)
	gt_ColoresPaleta(3).ColorTexto = Colors.RGB(29, 150, 65)
	gt_ColoresPaleta(3).colorDefault = Colors.white

	gt_ColoresPaleta(4).ColorNombre = "Gris"
	gt_ColoresPaleta(4).ColorClaro = Colors.RGB(181, 181, 181)
	gt_ColoresPaleta(4).ColorIntermedio  = Colors.RGB(129, 129, 129)
	gt_ColoresPaleta(4).ColorOscuro= Colors.RGB(105, 105, 105)
	gt_ColoresPaleta(4).ColorTexto = Colors.RGB(105, 105, 105)
	gt_ColoresPaleta(4).colorDefault = Colors.white
	
End Sub

Sub Set_Colores(iNivel As Int)
	'gi_CombinacionColores= xtPalabra.idpalabra Mod 5
	' XXX fuerza 0 para probar con violeta
	
	'carga la variable gt_Color con lo que necesita para este color

	'If gi_CombinacionColores <> gt_Color.Paleta Then
		gi_CombinacionColores = 0 
		Set_ColoresVariable ( gi_CombinacionColores)
		Set_ColoresPantalla 
	'End If
End Sub

'setea la pantalla con los colores que corresponden segun el color elegido
Sub Set_ColoresPantalla
	Panel1.Color = Colors.White
	Panel11.Color = Colors.White
	Panel2.Color = Colors.White
	Panel21.Color = Colors.white
	Panel3.Color = Colors.white
	Panel4.Color = gt_Color.ColorClaro
	Panel41.Color = gt_Color.ColorMedio
	Panel5.Color = gt_Color.ColorOscuro
	Panel51.Color =gt_Color.ColorOscuro
	Panel6.Color = Colors.White
	Panel61.Color =Colors.White
	
	
	'otras views	
	lblV2Nivel.TextColor = gt_Color.ColorTexto
	lblNeuronas.textcolor = gt_Color.ColorTexto
	lblAvance.TextColor = gt_Color.ColorTexto
	
	'panel2
	lblv2Def.TextColor = gt_Color.ColorTexto
	
	'panel3
	lblPedirLetra.TextColor = gt_Color.ColorTexto
	lblSaltarPalabra.TextColor = gt_Color.ColorTexto
	lblPedirLetraCosto.TextColor = gt_Color.ColorTexto
	lblSaltarPalabraCosto.TextColor = gt_Color.ColorTexto
	
	
	'panel6
	lblCompartir.Textcolor = gt_Color.ColorTexto
	lblBajarLetras.TextColor = gt_Color.ColorTexto
	
	'Panel11
	lbl11.Textcolor = gt_Color.ColorTexto
	
	'Panel21
	lbl21.TextColor = gt_Color.ColorTexto
	
	'panel61
	lbl61.TextColor = gt_Color.ColorTexto
	
	Activity.Color = Colors.white
	

	CargaImagenes_Volatiles

End Sub
'se hace la funcion para compatibilidad con el manejo viejo de colores
Sub Get_aTextColor (a As Int, b As Int) As Int
Dim iRet As Int
iRet = gt_Color.ColorTexto
Return iRet
End Sub

Sub CargaImagenes_Volatiles
	 'panel1
	imgMenu.SetBackgroundImage(gt_Color.bitmMenu)
	imgAvance.SetBackgroundImage(gt_Color.bitmFlag)
'panel3
	imgPedirLetra.SetBackgroundImage(gt_Color.bitmPedir)
	imgSaltarPalabra.SetBackgroundImage(gt_Color.bitmSaltarPalabra)
	
	'lblPedirLetraCosto.setbackgroundimage(LoadBitmapSample(g_DirGrabable, "Violeta-Costos.png", 100, 100))
	'lblSaltarPalabraCosto.SetBackgroundImage(LoadBitmapSample(g_DirGrabable, "Violeta-Costos.png", 100, 100))
	lblPedirLetraCosto.setbackgroundimage(gt_Color.bitmCostos)
	lblSaltarPalabraCosto.SetBackgroundImage(gt_Color.bitmCostos)
	
'panel6
	imgCompartir.SetBackgroundImage(gt_Color.bitmCompartir)
	imgBajarLetras.SetBackgroundImage(gt_Color.bitmBajarLetras)
	
End Sub


Sub Jugar_onAdShown(placementId As String)
	Log("ioDisplay.Shown")
'Catch ad impression
End Sub

Sub Jugar_onNoAds(placementId As String)
	Log("ioDisplay.NoAds")
'Do something, when no ads
End Sub
