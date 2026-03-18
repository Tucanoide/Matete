Type=Activity
Version=4
ModulesStructureVersion=1
B4A=true
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: true
	#IncludeTitle: False
#End Region

' Play store maneja dos tipos de productos:
'los Managed products - Tienen la particularidad de que no pueden comprarse otra vez hasta no ser consumidos
'Los unmanaged products se pueden comprar todas las veces que se quiera, sin necesidad de consumirse.
' los unmanaged products no estan soportados por la librearía, asi que manejo todos con managed
'Yo manejo asi:
'         los productos que incluyen quitar la propaganda nunca los consumo (compra de neuronas + ads off o solo ads off)
'		  los productos que NO incluyen quitar la propaganda son managed y los consumo con la compra (compra de neuronas exclusivamente)
' cuando compra algun producto que anula la propaganda, genera un registro en 
'la tabla seteos_usuario TipoSeteo = "ADS", Entero_Desde = 1, FechaDesde = date, Text = Mail del usuario (para asegurar que es el mismo usuario y no se copiaron bases a otro lado)
Sub Process_Globals
	'These global variables will be declared once when the application starts.
	'These variables can be accessed from all modules.
   Dim manager As BillingManager3
   Private key As String = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApRHoUo0M+jS7lrqSZy30dkQbwnUxD5BFVuW/yMgRCxomhL6A8TdoKyBn1YsvNAbKm3oM/UmOB1c98d9/RalWIrWOjrS7KqLtYeyY8lglOXhDrUKXDM8skQFd1G1btTCtnZHXpSdAhVZEIgLwG2Sq5azwnbR/YlOapAuHEfKNUzd7p+nOexi6q2HTcGN0dPnz7EumvIlgXWd3wpMkIZQ79WCUy7jLSdWiM23QPkrHUnMhemPjjCuTLT0MV64Egrlw0AmW0i1olQD/q8Hmh4fEDlVD+OI7CVE2JwwkHjEXvLFD38k92SfnI5slWWu+IvMIPpi+q2/DM2SgjRxnjx1EmQIDAQAB"

	Public g_NombreBaseLocalJuego As String ="BaseJ.sqlite"
	Public g_NombreBaseLocalUsuario As String = "BaseUE.sqlite"
	Dim g_sPswBaseJ As String= "NadaAnsioDeNada"
	Dim g_sPswBaseU As String = "MientrasDuraElInstante2"	
	Dim g_DirGrabable As String
	Public g_sqlBaseLocalJuego As SQLCipher, g_sqlBaseLocalUsuario As SQLCipher

							
							
'array donde guarda todos los productos disponibles en Play Store (se carga manualmente en Sub Productos_Disponibles
	Dim aProductosPlaystore() As tProductosPS
	
		

	Dim tFontOpenSansSemiBold As Typeface

End Sub

Sub Globals
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.


	Private lblCancelar As Label
End Sub


Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
   
   Activity.LoadLayout("Productos")
   
   If FirstTime Then
      manager.Initialize("manager", key)
      manager.DebugLogging = True

	  Publicos.AbreBasesParam(g_sqlBaseLocalJuego, g_NombreBaseLocalJuego, g_sPswBaseJ, _ 
								g_sqlBaseLocalUsuario, g_NombreBaseLocalUsuario, g_sPswBaseU)

   
   	  tFontOpenSansSemiBold= Typeface.LoadFromAssets("OpenSans-Semibold.ttf")
   Else
	manager.GetOwnedProducts
  	
   End If
	
	   
End Sub

Sub Activity_Resume
	SetPantalla

	' carga productos
	Log("Resume")
If False Then
	manager.GetOwnedProducts
	Productos_CargaPlayStore
	Productos_MarcaYaComprados
End If	
End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub


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
	'marca los productos ya comprados
	Productos_MarcaYaComprados

	Productos_MuestraOpciones
End Sub








'carga manualmente(no se puede automatico) los productos disponibles en el play store
Sub Productos_CargaPlayStore
Dim aProductosPlaystore(3) As tProductosPS
' 200 neuronas
	'aProductosPlayStore(0).ProductID = "200_neuronas_adicionales"
	'aProductosPlayStore(0).ManagedProduct = True
	'aProductosPlayStore(0).Price = "0.99" 
	'aProductosPlayStore(0).Title="200 NEURONAS"
	'aProductosPlayStore(0).ApagaPropaganda = True ' indica que este producto incluye el apagado de propaganda
	'aProductosPlayStore(0).Owned = False
	'aProductosPlayStore(0).Neuronas= 200
' 200 neuronas sin propaganda
	aProductosPlaystore(0).ProductID = "200_neuronas_adicionales_adosff"
	aProductosPlaystore(0).ManagedProduct = True
	aProductosPlaystore(0).Price = "0.99" 
	aProductosPlaystore(0).Title="200 NEURONAS"
	aProductosPlaystore(0).ApagaPropaganda = True ' indica que este producto incluye el apagado de propaganda
	aProductosPlaystore(0).Owned = False
	aProductosPlaystore(0).neuronas = 200

' producto Managed 500 neuronas
	'aProductosPlayStore(2).ProductID = "500_neuronas_adicionales"
	'aProductosPlayStore(2).ManagedProduct = True
	'aProductosPlayStore(2).Price = "1.99" 
	'aProductosPlayStore(2).Title="500 NEURONAS"
	'aProductosPlayStore(2).ApagaPropaganda = False ' indica que este producto incluye el apagado de propaganda
	'aProductosPlayStore(2).Owned = False
	'aProductosPlayStore(2).neuronas = 500
'compra de monedas y apagado de propaganda
	aProductosPlaystore(1).ProductID="500_neuronas_adicionales_adsoff"
	aProductosPlaystore(1).ManagedProduct = True
	aProductosPlaystore(1).Price = "1.99"
	aProductosPlaystore(1).Title= "500 NEURONAS"
	aProductosPlaystore(1).ApagaPropaganda = True ' indica si incluye el apagado de propaganda
	aProductosPlaystore(1).Owned = False
	aProductosPlaystore(1).neuronas =500
'producto Managed 1000 monedas 
	'aProductosPlayStore(4).ProductID = "1000_neuronas_adicionales"
	'aProductosPlayStore(4).ManagedProduct = True
	'aProductosPlayStore(4).Price = "2.99"
	'aProductosPlayStore(4).Title="1000 NEURONAS"
	'aProductosPlayStore(4).ApagaPropaganda = False ' indica que este producto incluye el apagado de propaganda
	'aProductosPlayStore(4).Owned = False
	'aProductosPlayStore(4).neuronas =1000
'solo compra de monedas
	aProductosPlaystore(2).ProductID="1000_neuronas_adicionales_adsoff"
	aProductosPlaystore(2).ManagedProduct = True
	aProductosPlaystore(2).Price = "2.99"
	aProductosPlaystore(2).Title= "1000 NEURONAS"
	aProductosPlaystore(2).ApagaPropaganda = True ' indica que este producto, no incluye el apagado de propaganda
	aProductosPlaystore(2).Owned = False
	aProductosPlaystore(2).neuronas =1000
End Sub

' marca en los productos del play store, los que ya fueron comprados (y son managed products) que no pueden ser comprados otra vez.
Sub Productos_MarcaYaComprados
	'recorre los productos comprados y por cada uno, busca en los disponibles en playstore para marcarlos (solo marca los managed para no mostrarlos como disponibles para comprar)
	For j = 0 To aProductosComprados.Length-1
		For i = 0 To aProductosPlaystore.Length-1
			If aProductosComprados(j).ProductID = aProductosPlaystore(i).ProductID Then
				aProductosPlaystore(i).Owned = True
			End If
		Next 
	Next
End Sub


'genera las views de los productos disponibles 
Sub Productos_MuestraOpciones
Dim iCant As Int = 0, bPropagandaApagada As Boolean
'verifica si ya tiene comprado algun producto que apague la propaganda
bPropagandaApagada = Productos_GetProductoOwnedAdsOff 
'Propaganda_getApagada 
' x cada productos del play store
pnlProductos.RemoveAllViews
For j =0 To aProductosPlaystore.Length-1
	' si es un producto que no está comprado (no puede comprar dos veces un producto managed)
	If aProductosPlaystore(j).Owned = False Then
		' si es un producto que apaga propaganda, y ya compro alguno que apaga propaganda, no lo muestra
		'If bPropagandaApagada = False OR aProductosPlayStore(j).ApagaPropaganda = False Then
		' se quita el if, porque decidi hacer que todas las compras apaguen la propaganda
			iCant = iCant +1
			Dim vProdDisponible As Label, vProdDisponiblePrecio As Label
			vProdDisponible.Initialize("vProdDisponible")  
			vProdDisponiblePrecio.Initialize("vProdDisponiblePrecio")
			vProdDisponible.Text = aProductosPlaystore(j).Title  
			vProdDisponiblePrecio.Text = aProductosPlaystore(j).price
			vProdDisponible.Tag = j ' guarda la posicion del array en el tag
			vProdDisponible.TextColor = Colors.Black
			vProdDisponiblePrecio.Textcolor=Colors.black
			vProdDisponible.Color = Colors.Transparent
			vProdDisponiblePrecio.Color = Colors.Transparent
			vProdDisponible.Gravity = Gravity.LEFT
			vProdDisponiblePrecio.Gravity = Gravity.RIGHT
			
			pnlProductos.AddView(vProdDisponible, 0, (iCant-1)  * 52dip, pnlProductos.Width , 50dip)
			pnlProductos.AddView(vProdDisponiblePrecio, 0, (iCant-1)  * 52dip, pnlProductos.Width , 50dip)
				
		'agrega la view al array de views, esto si que es original
		'la cisterna se quedó sin agua justo cuando la necesitaba. Ni con una bomba puedo hacer que me suba agua al tanque.	
			Dim avProdDisponibles (iCant) As Label
			avProdDisponibles(iCant-1) = vProdDisponible
		'End If
	End If
Next
End Sub

'cuando presional el boton de ok
Sub lblcancelar_click
	StartActivity("Jugar")
	Activity.finish
End Sub



Sub SetPantalla


End Sub




