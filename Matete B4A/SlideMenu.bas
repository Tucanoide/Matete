Type=Class
Version=5.8
ModulesStructureVersion=1
B4A=true
@EndOfDesignText@
'SlideMenu Class module
'
'Author:  Markus Stipp
'Version: 1.0

'Class module
Private Sub Class_Globals
	Type ActionItem (Text As String, Image As Bitmap, Value As Object)
	
	Private mSlidePanel As Panel
	Private mBackPanel As Panel
	
	Private mModule As Object
	Private mEventName As String
	
	Private mListView As ListView

	Private mInAnimation As Animation
	Private mOutAnimation As Animation
	
	Dim tFontDef As Typeface
	
	Dim aLabels () As Label
	Private aImagen (6) As ImageView
	Dim iCantLabels As Int = 0
End Sub

'Initializes the SlideMenu object
' Activity - Pass a reference to the Activity here where the Slidemenu should be added to.
' Module - The calling Module. Pass the "Me" reference here
' EventNAme - EventName for the Click event
' Top - Top position of the Menu.
' Width - Width of the menu
Sub Initialize(Activity As Activity, Module As Object, EventName As String, Top As Int, Width As Int)

	tFontDef = Typeface.LoadFromAssets("OpenSans-Light.ttf")

	
	mModule = Module
	mEventName = EventName

	mSlidePanel.Initialize("mSlidePanel")


	mInAnimation.InitializeTranslate("", -Width, 0, 0, 0)
	mInAnimation.Duration = 300
	mOutAnimation.InitializeTranslate("Out", Width, 0, 0, 0)
	mOutAnimation.Duration = 300
	
	Activity.AddView(mSlidePanel, 0, Top, Width, 100%y - Top)
	'mSlidePanel.SetBackgroundImage(LoadBitmap(File.DirAssets, "Menu-Fondo-Violeta.png"))
	
	
	mBackPanel.Initialize("mBackPanel")
	mBackPanel.Color = Colors.Transparent
	Activity.AddView(mBackPanel, -100%x, 0, 100%x, 100%y)

	mSlidePanel.Visible = False
End Sub

'Adds an item to the SlideMenu
' Text - Text to show in menu
' Image - Image to show
' ReturnValue - The value that will be returned in the Click event
Public Sub AddItem(Text As String, Image As Bitmap, ReturnValue As Object)
	Dim item As ActionItem
	item.Initialize
	item.Text = Text
	item.Image = Image
	item.Value = ReturnValue

	
	If Not(Image.IsInitialized) Then
		mListView.AddTwoLinesAndBitmap2(Text, "", Null, ReturnValue)
	Else
		mListView.AddTwoLinesAndBitmap2(Text, "", Image, ReturnValue)
	End If
	mListView.BringToFront
	mListView.Color = Colors.Transparent
End Sub

'Show the SlideMenu
Public Sub Show
	If isVisible = True Then Return
	
	mBackPanel.BringToFront
	mSlidePanel.BringToFront
	mBackPanel.Left = 0
	mSlidePanel.Left = 0
	
	mSlidePanel.Visible = True
	mInAnimation.Start(mSlidePanel)
End Sub

'Hide the SlideMenu
Public Sub Hide
	If isVisible = False Then Return
	
	mBackPanel.Left = -mBackPanel.Width
    mSlidePanel.Left = -mSlidePanel.Width
	mOutAnimation.Start(mSlidePanel)
End Sub

Private Sub Out_AnimationEnd
	mSlidePanel.Visible = False
End Sub

Private Sub mBackPanel_Touch (Action As Int, X As Float, Y As Float)
	If Action = 1 Then
		Hide
	End If
End Sub


'Check if the menu is currently visible
Public Sub isVisible As Boolean
	Return mSlidePanel.Visible
End Sub


Public Sub SetBackground(Image As Bitmap)
Dim imagen As ImageView
imagen.Initialize("Imagen")
mSlidePanel.AddView(imagen, 0,0,mSlidePanel.Width, mSlidePanel.Height)
imagen.SetBackgroundImage (Image)
'mSlidePanel.SetBackgroundImage(Image) 

End Sub





Sub smAddView(xView As View, iLeft As Int, iTop As Int, iwidth As Int, iheight As Int, bFront As Boolean, bBack As Boolean)
	If iwidth = -1 Then 
		iwidth = mSlidePanel.Width
	End If
	If iheight = -1 Then
		iheight = mSlidePanel.width
	End If
	
	
	mSlidePanel.AddView(xView, iLeft, iTop, iwidth, iheight)
	If bFront Then
		xView.BringToFront
	End If
	If bBack Then
		xView.SendToBack
	End If

End Sub


'agrega un item completo, conformado por un panel que contiene un label y un ícono
Public Sub AddItemArray(sTexto As String, sImagen As String, itop As Int, iheight As Int, iTextSize As Int)

	Dim iWidth As Int, iLeftImagen As Int, ianchoImagen As Int
	Dim lblLabel As Label, imgImagen As ImageView

	'Inicializa views
	lblLabel .Initialize("LabelC"): imgImagen.Initialize("")
	lblLabel .Text = sTexto
	lblLabel .Typeface = tFontDef 
	lblLabel.Color = Colors.Transparent

	iWidth = mSlidePanel.Width 
	ianchoImagen = iheight * 0.6
	iLeftImagen = mSlidePanel.Width - ianchoImagen

	'AGREGA EL LABEL Y LA IMAGEN
	mSlidePanel.AddView (lblLabel , 2, itop, iWidth, iheight)
	mSlidePanel.AddView(imgImagen , mSlidePanel.Width-ianchoImagen, itop , ianchoImagen, ianchoImagen)
	imgImagen.Top = itop + (lblLabel .Height-imgImagen.Height)/2
	lblLabel .Gravity = Gravity.CENTER_VERTICAL + Gravity.LEFT
	imgImagen.Gravity = Gravity.CENTER_VERTICAL
	imgImagen.BringToFront


	If sImagen <>"" Then
		imgImagen.SetBackgroundImage(LoadBitmapSample(File.DirAssets, sImagen, 120,120))
		imgImagen.Gravity = Gravity.FILL
	End If
	imgImagen.BringToFront

	' guarda el label en un array y las imagenes en otro
	iCantLabels = iCantLabels + 1
	Dim aLabels (iCantLabels) As Label
	'Dim aImagen(iCantLabels) As ImageView
	aLabels(iCantLabels-1) = lblLabel 
	aImagen (iCantLabels-1) = imgImagen
	

End Sub

Sub LabelC_Click
 Dim Send As Label
 Send = Sender'

' Msgbox("Click arraylabel", "joya")	

	Send.Color = Colors.BLACK
	DoEvents
	Dim subname As String
	Hide	'oculta el menu
	subname = mEventName & "_Click"
	If SubExists(mModule, subname) Then
		CallSub2(mModule, subname, Send)
	End If

	
	Send.Color = Colors.Transparent
	
End Sub

Public Sub ItemArrayCambiaImagen(iPosicionArray As Int, sImagen As String)

	If sImagen <>"" Then
		aImagen(iPosicionArray).SetBackgroundImage(LoadBitmapSample(File.DirAssets, sImagen, 120,120))
	End If

End Sub
