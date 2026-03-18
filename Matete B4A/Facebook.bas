Type=Activity
Version=3.8
B4A=true
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: True
#End Region

Sub Process_Globals
	'These global variables will be declared once when the application starts.
	'These variables can be accessed from all modules.

End Sub

Sub Globals
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.

	Private btnPublicar As Button
	Private btnVolver As Button
	Private chkMensajeJugador As CheckBox
	Private imgIcono As ImageView
	Private lblMensajeMatete As Label
	Private lblTitulo As Label
	Private pnlTop As Panel
	Private txtMensajeJugador As EditText
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("Facebook")

End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub


