Type=Activity
Version=3.8
B4A=true
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: false
	#IncludeTitle: False
#End Region

Sub Process_Globals
	'These global variables will be declared once when the application starts.
	'These variables can be accessed from all modules.

End Sub

Sub Globals
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.

	Private btnAnterior As Button
	Private btnSiguiente As Button
	Private btnVolver As Button
	Private imgExplica As ImageView
	Dim iPosicionFondo As Int

End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("MateteAyuda")
	SetInicio
		
End Sub

Sub SetInicio
	Dim iTop As Int, iLetra As Int 
	iTop = Activity.Height - btnAnterior.Height
		
	
	iLetra = Publicos.Font_RecalculaTam(20)
	
	imgExplica.Top = 0
	imgExplica.Left = 0
	imgExplica.Width = Activity.Width
	imgExplica.Height = Activity.Height
	

	
	btnAnterior.Color = Colors.Transparent
	btnAnterior.Top = iTop
	btnAnterior.Width = Activity.Width/3.2
	btnAnterior.TextSize = iLetra
	btnAnterior.Left = 0
	
	btnSiguiente.color	= Colors.transparent
	btnSiguiente.Top = iTop
	btnSiguiente.Width = Activity.Width/3.2
	btnSiguiente.TextSize = iLetra
	btnSiguiente.Left = Activity.Width - btnSiguiente.width
	
	btnVolver.Text ="Volver"
	btnVolver.Color = Colors.Transparent
	btnVolver.Top = iTop
	btnVolver.Width = Activity.Width/3.2
	Publicos.CentrarControl(Activity, btnVolver, True, False)
	btnVolver.TextSize = iLetra
	
	iPosicionFondo=0
	CambiaAyuda(1)
End Sub


Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub btnVolver_Click 
	

	Activity.Finish
End Sub

Sub Activity_KeyPress(KeyCode As Int) As Boolean

If KeyCode = KeyCodes.KEYCODE_BACK Then

	Activity.Finish
	Return True
	
Else
	Return False
End If

End Sub
Sub btnSiguiente_click
	CambiaAyuda(1)
	
End Sub
Sub btnAnterior_click
	CambiaAyuda(-1)
End Sub

Sub CambiaAyuda(iSuma As Int)
	Dim sArchivoFondo As String
	
	iPosicionFondo = iPosicionFondo+iSuma
	btnSiguiente.Enabled = iPosicionFondo <9
	btnAnterior.Enabled = iPosicionFondo >1
	sArchivoFondo = "Exp0" & iPosicionFondo & ".jpg"
	Publicos.Archivo_CopiaDesdeAssets(sArchivoFondo, False)
	imgExplica.Bitmap = LoadBitmap(Main.g_DirGrabable,sArchivoFondo)
End Sub