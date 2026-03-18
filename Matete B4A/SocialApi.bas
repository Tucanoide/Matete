Type=StaticCode
Version=3.8
B4A=true
@EndOfDesignText@
'Code module
'Subs in this code module will be accessible from all modules.
Sub Process_Globals
	'These global variables will be declared once when the application starts.
	'These variables can be accessed from all modules.
	
	Public TEST_REFRESH_PERMISSIONS As Int = 0
	Public TEST_ME_FULL As Int = 1
	Public TEST_ME_CUSTOM As Int = 2
	Public TEST_READ_FEED As Int = 3
	Public TEST_READ_PHOTOS As Int = 4
	Public TEST_OG_STORY As Int = 5
	Public TEST_POST As Int = 6
	Public TEST_REQ_WRITE_PERMISSIONS As Int = 7
	Public TEST_REQ_READ_PERMISSIONS As Int = 8
	Public TEST_UPLOAD_PHOTO As Int = 9
	Public TEST_UPLOAD_VIDEO As Int = 10
	Public TEST_DEAUTHORIZE As Int = 11
	
End Sub

Sub RunTest (TestId As Int)
	' https://developers.Facebook.com/docs/facebook-login/permissions/v2.0
	
	Dim Result As FacebookResult
	
	Facebook.PleaseWaitText = "Custom <please wait> text"

	Select TestId
		Case TEST_REFRESH_PERMISSIONS
			Facebook.RefreshPermissions
		Case TEST_ME_FULL
			Result = Facebook.GetMe(Null)
		Case TEST_ME_CUSTOM
			Result = Facebook.GetMe(Array As String ("email,verified"))
		Case TEST_READ_FEED
			Result = Facebook.GetFeed("")
		Case TEST_READ_PHOTOS
			Result = Facebook.GetPhotos("","")
		Case TEST_OG_STORY
			Result = Facebook.PublishOpenGraphStory("<your-object-type>", "<your-action-type>", "<your-object-uri>", False)
		Case TEST_POST
			'Result = Facebook.PublishPost("B4A Rocks!","http://basic4ppc.com","http://www.basic4ppc.com/basic4android/images/SS-2014-03-26_12.15.47.png","<name>", "<caption>", "<description>")
			Result = Facebook.PublishPost("Un juego para el lado colorido de tu cerebro","https://play.google.com/store/apps/details?id=com.matetejuego.free","","<name>", "<caption>", "<description>")
			'Facebook.PublishPost(message, Link, Picture, Name, Caption, Description)
		Case TEST_REQ_WRITE_PERMISSIONS
			Facebook.RequestPublishPermissions
			Log("RequestPublishPermissions")
		Case TEST_REQ_READ_PERMISSIONS
			Facebook.RequestReadPermissions(Array As String (Facebook.Constants.Permissions.READ_FRIENDLISTS, Facebook.Constants.Permissions.USER_ABOUT_ME, Facebook.Constants.Permissions.READ_STREAM))
		Case TEST_DEAUTHORIZE
			If Facebook.Connected AND Msgbox2("Are you sure?", "Deauthorize", "Yes", "No", "", Null) = DialogResponse.POSITIVE Then
				Facebook.Deauthorize
			End If
	End Select

	If Result = Null Then
		Msgbox("Previous request is still pending.", "Error")
	Else
		If Result.Validated Then
			If Result.Canceled Then Return

			If Result.Success Then
				Msgbox(Result.Map, "Success")
			Else
				Msgbox(Result.Message, Result.Type & ":" & Result.Code & " (" & Result.SubCode & ")")
			End If
		End If
	End If 
End Sub



