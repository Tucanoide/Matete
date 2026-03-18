Type=Activity
Version=3.8
B4A=true
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: true
	#IncludeTitle: false
#End Region

Sub Process_Globals
	'These global variables will be declared once when the application starts.
	'These variables can be accessed from all modules.
	Public Facebook As FacebookProvider


End Sub

Sub Globals
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.

	Dim ThisActivity As SocialApiActivity
	Private pnlConectar As Panel
	Private imgConectar As ImageView
	Private lblConectar As Label
	Private imgIconoFondo As ImageView
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	'Activity.LoadLayout("Layout1")

	Activity.LoadLayout("v2Facebook")
	
End Sub

Sub Activity_Resume


	Facebook.SetActivity(ThisActivity.Initialize("facebook"))
	SetPantalla	

	SetPantallaSegunEstado
	
	
End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub SetPantalla

	Publicos.ViewComoActivity(Activity, pnlConectar)
	Activity.Color = Colors.white

		
	imgIconoFondo.Width = Activity.height * 0.40
	imgIconoFondo.Height = imgIconoFondo.Width
	imgIconoFondo.Top = Activity.Height * 0.20
	Publicos.CentrarControl(Activity, imgIconoFondo, True, False)

	imgConectar.Width = Activity.Width * 0.2
	imgConectar.Height= imgConectar.Width
	Publicos.CentrarControl(Activity, imgConectar, True, False)
	imgConectar.Top = imgIconoFondo.Top + imgIconoFondo.Height + 2dip
	
	lblConectar.height = imgConectar.Height /3
	lblConectar.Width = pnlConectar.Width
	lblConectar.Gravity = Gravity.center
	lblConectar.Left = 0
	lblConectar.textcolor  = Colors.blue
	lblConectar.Top = imgConectar.Top + imgConectar.Height + 2dip
	Publicos.SetLabelTextSize(lblConectar, lblConectar.Text, 25,6, 100)
End Sub


Sub SetPantallaSegunEstado
	If Facebook.Connected AND (Facebook.HasPermission(Facebook.Constants.Permissions.PUBLISH_ACTIONS)) Then
			
	
	
	Else
		
	End If
End Sub



Sub Facebook_Connected (Provider As SocialApiProvider)
Try
	If Not(Facebook.HasPermission(Facebook.Constants.Permissions.PUBLISH_ACTIONS)) Then
    	Facebook.RequestPublishPermissions
	End If
Catch
	Publicos.ManejaError("Conexion Facebook", LastException.Message)
End Try

SetPantallaSegunEstado	
	
End Sub
