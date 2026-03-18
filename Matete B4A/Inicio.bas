Type=Activity
Version=3.5
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: False
#End Region

Sub Process_Globals
	'These global variables will be declared once when the application starts.
	'These variables can be accessed from all modules.
	
End Sub

Sub Globals
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.

	Private btnJugar As Button
	Private btnComprarMonedas As Button
	Private imgFondo As ImageView
	Private imgFacebook As ImageView
	Private btnAyuda As Button

	Private imgLogo As ImageView
	
	Dim iColorBlueRed() As Int = Array As Int (Colors.Blue, Colors.Red)
	Dim iColorCyanRed() As Int = Array As Int (Colors.Cyan, Colors.Red)
	Dim iColorRedBlue() As Int = Array As Int (Colors.red, Colors.Blue)
	Dim iColorRedCyan() As Int = Array As Int (Colors.red, Colors.cyan)
	Dim iColorBlackWhite() As Int = Array As Int (Colors.white, Colors.DarkGray, Colors.gray)
	Dim iColoresBotones() As Int = Array As Int (Colors.black, Colors.Gray)
	Dim iCuentaReset As Int
	
	Private pnlFondo As Panel
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("MateteInicio")
	SetInicio
	
	LayoutPantalla
	
	
	 
	

End Sub

Sub LayoutPantalla




End Sub



Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub SetInicio

	Dim grdFondo As GradientDrawable
	
	
	
	imgFondo.top=Publicos.ConvierteDipsStandard(5,"H")
	imgFondo.Left = 0
	imgFondo.Height = Activity.Height
	imgFondo.width = Activity.Width
	imgFondo.SendToBack
	
	'pnlFondo.Top = 0
	'pnlFondo.Left = 0
	'pnlFondo.Width = Activity.Width
	'pnlFondo.Height= Activity.height
	
	'botones
	Publicos.RedimensionaView(btnJugar,270,60)
	btnJugar.top = Publicos.ConvierteDipsStandard(40,"H")
	'btnJugar.Color = Colors.Transparent
	grdFondo.Initialize("TOP_BOTTOM",iColoresBotones) 
  	grdFondo.CornerRadius = 20
	btnJugar.Background = grdFondo

	btnJugar.TextSize = 34
	Publicos.CentrarControl (Activity,btnJugar,True,False)
	

	btnAyuda.Height = btnJugar.Height
	btnAyuda.Width = btnJugar.width
	btnAyuda.Top = Publicos.ConvierteDipsStandard(110,"H")
	btnAyuda.TextSize = 32
	grdFondo.Initialize("TOP_BOTTOM",iColoresBotones) 
  	grdFondo.CornerRadius = 20
	btnAyuda.Background = grdFondo
	btnAyuda.Text = "Ayuda"
	Publicos.CentrarControl(Activity,btnAyuda,True,False)

	
	'imgLogo.Top = 10dip
	'imgLogo.Width = Activity.width/7
	'imgLogo.Height= imgLogo.Width/4
	'imgLogo.Left=0
	'Publicos.CentrarControl(Activity, imgLogo, True, False)
	
	
End Sub

Sub btnJugar_Click
	iCuentaReset=0
	StartActivity("Jugar")
	
	


End Sub


Sub btnAyuda_Click
iCuentaReset=0
StartActivity("Ayuda")

End Sub

Sub imgFondo_Click
iCuentaReset = iCuentaReset +1
If iCuentaReset =7 Then
	Dim Base As SQL,sDirGrabable As String
    If File.ExternalWritable Then sDirGrabable = File.DirDefaultExternal Else sDirGrabable = File.DirInternal

	
	Publicos.AbreBase(Base, sDirGrabable, "matete01.sqlite")
	Publicos.Magia(iCuentaReset,Base)
	Base.Close

End If
End Sub
