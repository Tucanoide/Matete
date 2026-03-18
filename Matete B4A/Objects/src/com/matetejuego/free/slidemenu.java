package com.matetejuego.free;


import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.B4AClass;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.debug.*;

public class slidemenu extends B4AClass.ImplB4AClass implements BA.SubDelegator{
    private static java.util.HashMap<String, java.lang.reflect.Method> htSubs;
    private void innerInitialize(BA _ba) throws Exception {
        if (ba == null) {
            ba = new BA(_ba, this, htSubs, "com.matetejuego.free.slidemenu");
            if (htSubs == null) {
                ba.loadHtSubs(this.getClass());
                htSubs = ba.htSubs;
            }
            
        }
        if (BA.isShellModeRuntimeCheck(ba)) 
			   this.getClass().getMethod("_class_globals", com.matetejuego.free.slidemenu.class).invoke(this, new Object[] {null});
        else
            ba.raiseEvent2(null, true, "class_globals", false);
    }

 public anywheresoftware.b4a.keywords.Common __c = null;
public anywheresoftware.b4a.objects.PanelWrapper _vvvvvvvvvvvvvv3 = null;
public anywheresoftware.b4a.objects.PanelWrapper _vvvvvvvvvvvvvv4 = null;
public Object _vvvvvvvvvvvvvv5 = null;
public String _vvvvvvvvvvvvvv6 = "";
public anywheresoftware.b4a.objects.ListViewWrapper _vvvvvvvvvvvvvv7 = null;
public anywheresoftware.b4a.objects.AnimationWrapper _vvvvvvvvvvvvvv0 = null;
public anywheresoftware.b4a.objects.AnimationWrapper _vvvvvvvvvvvvvvv1 = null;
public anywheresoftware.b4a.keywords.constants.TypefaceWrapper _vvvvvvvvvvvvvvv2 = null;
public anywheresoftware.b4a.objects.LabelWrapper[] _vvvvvvvvvvvvvvv3 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper[] _vvvvvvvvvvvvvvv4 = null;
public int _vvvvvvvvvvvvvvv5 = 0;
public com.matetejuego.free.main _vvvvvvvvvvvvvvv6 = null;
public com.matetejuego.free.publicos _vvvvvvvvvvvvvvv7 = null;
public com.matetejuego.free.jugar _vvvvvvvvvvvvvvv0 = null;
public com.matetejuego.free.facebookactivity _vvvvvvvvvvvvvvvv1 = null;
public com.matetejuego.free.twitter _vvvvvvvvvvvvvvvv2 = null;
public com.matetejuego.free.svcbaseremota _vvvvvvvvvvvvvvvv3 = null;
public static class _actionitem{
public boolean IsInitialized;
public String Text;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper Image;
public Object Value;
public void Initialize() {
IsInitialized = true;
Text = "";
Image = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
Value = new Object();
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public String  _vvvvvvvvvvvvv3(String _text,anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _image,Object _returnvalue) throws Exception{
com.matetejuego.free.slidemenu._actionitem _item = null;
 //BA.debugLineNum = 65;BA.debugLine="Public Sub AddItem(Text As String, Image As Bitmap";
 //BA.debugLineNum = 66;BA.debugLine="Dim item As ActionItem";
_item = new com.matetejuego.free.slidemenu._actionitem();
 //BA.debugLineNum = 67;BA.debugLine="item.Initialize";
_item.Initialize();
 //BA.debugLineNum = 68;BA.debugLine="item.Text = Text";
_item.Text = _text;
 //BA.debugLineNum = 69;BA.debugLine="item.Image = Image";
_item.Image = _image;
 //BA.debugLineNum = 70;BA.debugLine="item.Value = ReturnValue";
_item.Value = _returnvalue;
 //BA.debugLineNum = 73;BA.debugLine="If Not(Image.IsInitialized) Then";
if (__c.Not(_image.IsInitialized())) { 
 //BA.debugLineNum = 74;BA.debugLine="mListView.AddTwoLinesAndBitmap2(Text, \"\", Null,";
_vvvvvvvvvvvvvv7.AddTwoLinesAndBitmap2(_text,"",(android.graphics.Bitmap)(__c.Null),_returnvalue);
 }else {
 //BA.debugLineNum = 76;BA.debugLine="mListView.AddTwoLinesAndBitmap2(Text, \"\", Image,";
_vvvvvvvvvvvvvv7.AddTwoLinesAndBitmap2(_text,"",(android.graphics.Bitmap)(_image.getObject()),_returnvalue);
 };
 //BA.debugLineNum = 78;BA.debugLine="mListView.BringToFront";
_vvvvvvvvvvvvvv7.BringToFront();
 //BA.debugLineNum = 79;BA.debugLine="mListView.Color = Colors.Transparent";
_vvvvvvvvvvvvvv7.setColor(__c.Colors.Transparent);
 //BA.debugLineNum = 80;BA.debugLine="End Sub";
return "";
}
public String  _vvvvvvvvvvvvv4(String _stexto,String _simagen,int _itop,int _iheight,int _itextsize) throws Exception{
int _iwidth = 0;
int _ileftimagen = 0;
int _ianchoimagen = 0;
anywheresoftware.b4a.objects.LabelWrapper _lbllabel = null;
anywheresoftware.b4a.objects.ImageViewWrapper _imgimagen = null;
 //BA.debugLineNum = 155;BA.debugLine="Public Sub AddItemArray(sTexto As String, sImagen";
 //BA.debugLineNum = 157;BA.debugLine="Dim iWidth As Int, iLeftImagen As Int, ianchoImag";
_iwidth = 0;
_ileftimagen = 0;
_ianchoimagen = 0;
 //BA.debugLineNum = 158;BA.debugLine="Dim lblLabel As Label, imgImagen As ImageView";
_lbllabel = new anywheresoftware.b4a.objects.LabelWrapper();
_imgimagen = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 161;BA.debugLine="lblLabel .Initialize(\"LabelC\"): imgImagen.Initial";
_lbllabel.Initialize(ba,"LabelC");
 //BA.debugLineNum = 161;BA.debugLine="lblLabel .Initialize(\"LabelC\"): imgImagen.Initial";
_imgimagen.Initialize(ba,"");
 //BA.debugLineNum = 162;BA.debugLine="lblLabel .Text = sTexto";
_lbllabel.setText((Object)(_stexto));
 //BA.debugLineNum = 163;BA.debugLine="lblLabel .Typeface = tFontDef";
_lbllabel.setTypeface((android.graphics.Typeface)(_vvvvvvvvvvvvvvv2.getObject()));
 //BA.debugLineNum = 164;BA.debugLine="lblLabel.Color = Colors.Transparent";
_lbllabel.setColor(__c.Colors.Transparent);
 //BA.debugLineNum = 166;BA.debugLine="iWidth = mSlidePanel.Width";
_iwidth = _vvvvvvvvvvvvvv3.getWidth();
 //BA.debugLineNum = 167;BA.debugLine="ianchoImagen = iheight * 0.6";
_ianchoimagen = (int) (_iheight*0.6);
 //BA.debugLineNum = 168;BA.debugLine="iLeftImagen = mSlidePanel.Width - ianchoImagen";
_ileftimagen = (int) (_vvvvvvvvvvvvvv3.getWidth()-_ianchoimagen);
 //BA.debugLineNum = 171;BA.debugLine="mSlidePanel.AddView (lblLabel , 2, itop, iWidth,";
_vvvvvvvvvvvvvv3.AddView((android.view.View)(_lbllabel.getObject()),(int) (2),_itop,_iwidth,_iheight);
 //BA.debugLineNum = 172;BA.debugLine="mSlidePanel.AddView(imgImagen , mSlidePanel.Width";
_vvvvvvvvvvvvvv3.AddView((android.view.View)(_imgimagen.getObject()),(int) (_vvvvvvvvvvvvvv3.getWidth()-_ianchoimagen),_itop,_ianchoimagen,_ianchoimagen);
 //BA.debugLineNum = 173;BA.debugLine="imgImagen.Top = itop + (lblLabel .Height-imgImage";
_imgimagen.setTop((int) (_itop+(_lbllabel.getHeight()-_imgimagen.getHeight())/(double)2));
 //BA.debugLineNum = 174;BA.debugLine="lblLabel .Gravity = Gravity.CENTER_VERTICAL + Gra";
_lbllabel.setGravity((int) (__c.Gravity.CENTER_VERTICAL+__c.Gravity.LEFT));
 //BA.debugLineNum = 175;BA.debugLine="imgImagen.Gravity = Gravity.CENTER_VERTICAL";
_imgimagen.setGravity(__c.Gravity.CENTER_VERTICAL);
 //BA.debugLineNum = 176;BA.debugLine="imgImagen.BringToFront";
_imgimagen.BringToFront();
 //BA.debugLineNum = 179;BA.debugLine="If sImagen <>\"\" Then";
if ((_simagen).equals("") == false) { 
 //BA.debugLineNum = 180;BA.debugLine="imgImagen.SetBackgroundImage(LoadBitmapSample(Fi";
_imgimagen.SetBackgroundImage((android.graphics.Bitmap)(__c.LoadBitmapSample(__c.File.getDirAssets(),_simagen,(int) (120),(int) (120)).getObject()));
 //BA.debugLineNum = 181;BA.debugLine="imgImagen.Gravity = Gravity.FILL";
_imgimagen.setGravity(__c.Gravity.FILL);
 };
 //BA.debugLineNum = 183;BA.debugLine="imgImagen.BringToFront";
_imgimagen.BringToFront();
 //BA.debugLineNum = 186;BA.debugLine="iCantLabels = iCantLabels + 1";
_vvvvvvvvvvvvvvv5 = (int) (_vvvvvvvvvvvvvvv5+1);
 //BA.debugLineNum = 187;BA.debugLine="Dim aLabels (iCantLabels) As Label";
_vvvvvvvvvvvvvvv3 = new anywheresoftware.b4a.objects.LabelWrapper[_vvvvvvvvvvvvvvv5];
{
int d0 = _vvvvvvvvvvvvvvv3.length;
for (int i0 = 0;i0 < d0;i0++) {
_vvvvvvvvvvvvvvv3[i0] = new anywheresoftware.b4a.objects.LabelWrapper();
}
}
;
 //BA.debugLineNum = 189;BA.debugLine="aLabels(iCantLabels-1) = lblLabel";
_vvvvvvvvvvvvvvv3[(int) (_vvvvvvvvvvvvvvv5-1)] = _lbllabel;
 //BA.debugLineNum = 190;BA.debugLine="aImagen (iCantLabels-1) = imgImagen";
_vvvvvvvvvvvvvvv4[(int) (_vvvvvvvvvvvvvvv5-1)] = _imgimagen;
 //BA.debugLineNum = 193;BA.debugLine="End Sub";
return "";
}
public String  _class_globals() throws Exception{
 //BA.debugLineNum = 7;BA.debugLine="Private Sub Class_Globals";
 //BA.debugLineNum = 8;BA.debugLine="Type ActionItem (Text As String, Image As Bitmap,";
;
 //BA.debugLineNum = 10;BA.debugLine="Private mSlidePanel As Panel";
_vvvvvvvvvvvvvv3 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 11;BA.debugLine="Private mBackPanel As Panel";
_vvvvvvvvvvvvvv4 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 13;BA.debugLine="Private mModule As Object";
_vvvvvvvvvvvvvv5 = new Object();
 //BA.debugLineNum = 14;BA.debugLine="Private mEventName As String";
_vvvvvvvvvvvvvv6 = "";
 //BA.debugLineNum = 16;BA.debugLine="Private mListView As ListView";
_vvvvvvvvvvvvvv7 = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 18;BA.debugLine="Private mInAnimation As Animation";
_vvvvvvvvvvvvvv0 = new anywheresoftware.b4a.objects.AnimationWrapper();
 //BA.debugLineNum = 19;BA.debugLine="Private mOutAnimation As Animation";
_vvvvvvvvvvvvvvv1 = new anywheresoftware.b4a.objects.AnimationWrapper();
 //BA.debugLineNum = 21;BA.debugLine="Dim tFontDef As Typeface";
_vvvvvvvvvvvvvvv2 = new anywheresoftware.b4a.keywords.constants.TypefaceWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Dim aLabels () As Label";
_vvvvvvvvvvvvvvv3 = new anywheresoftware.b4a.objects.LabelWrapper[(int) (0)];
{
int d0 = _vvvvvvvvvvvvvvv3.length;
for (int i0 = 0;i0 < d0;i0++) {
_vvvvvvvvvvvvvvv3[i0] = new anywheresoftware.b4a.objects.LabelWrapper();
}
}
;
 //BA.debugLineNum = 24;BA.debugLine="Private aImagen (6) As ImageView";
_vvvvvvvvvvvvvvv4 = new anywheresoftware.b4a.objects.ImageViewWrapper[(int) (6)];
{
int d0 = _vvvvvvvvvvvvvvv4.length;
for (int i0 = 0;i0 < d0;i0++) {
_vvvvvvvvvvvvvvv4[i0] = new anywheresoftware.b4a.objects.ImageViewWrapper();
}
}
;
 //BA.debugLineNum = 25;BA.debugLine="Dim iCantLabels As Int = 0";
_vvvvvvvvvvvvvvv5 = (int) (0);
 //BA.debugLineNum = 26;BA.debugLine="End Sub";
return "";
}
public String  _vvvvvvvvvvvvv5() throws Exception{
 //BA.debugLineNum = 96;BA.debugLine="Public Sub Hide";
 //BA.debugLineNum = 97;BA.debugLine="If isVisible = False Then Return";
if (_vvvvvvvvvvvvv6()==__c.False) { 
if (true) return "";};
 //BA.debugLineNum = 99;BA.debugLine="mBackPanel.Left = -mBackPanel.Width";
_vvvvvvvvvvvvvv4.setLeft((int) (-_vvvvvvvvvvvvvv4.getWidth()));
 //BA.debugLineNum = 100;BA.debugLine="mSlidePanel.Left = -mSlidePanel.Width";
_vvvvvvvvvvvvvv3.setLeft((int) (-_vvvvvvvvvvvvvv3.getWidth()));
 //BA.debugLineNum = 101;BA.debugLine="mOutAnimation.Start(mSlidePanel)";
_vvvvvvvvvvvvvvv1.Start((android.view.View)(_vvvvvvvvvvvvvv3.getObject()));
 //BA.debugLineNum = 102;BA.debugLine="End Sub";
return "";
}
public String  _initialize(anywheresoftware.b4a.BA _ba,anywheresoftware.b4a.objects.ActivityWrapper _activity,Object _module,String _eventname,int _top,int _width) throws Exception{
innerInitialize(_ba);
 //BA.debugLineNum = 34;BA.debugLine="Sub Initialize(Activity As Activity, Module As Obj";
 //BA.debugLineNum = 36;BA.debugLine="tFontDef = Typeface.LoadFromAssets(\"OpenSans-Ligh";
_vvvvvvvvvvvvvvv2.setObject((android.graphics.Typeface)(__c.Typeface.LoadFromAssets("OpenSans-Light.ttf")));
 //BA.debugLineNum = 39;BA.debugLine="mModule = Module";
_vvvvvvvvvvvvvv5 = _module;
 //BA.debugLineNum = 40;BA.debugLine="mEventName = EventName";
_vvvvvvvvvvvvvv6 = _eventname;
 //BA.debugLineNum = 42;BA.debugLine="mSlidePanel.Initialize(\"mSlidePanel\")";
_vvvvvvvvvvvvvv3.Initialize(ba,"mSlidePanel");
 //BA.debugLineNum = 45;BA.debugLine="mInAnimation.InitializeTranslate(\"\", -Width, 0, 0";
_vvvvvvvvvvvvvv0.InitializeTranslate(ba,"",(float) (-_width),(float) (0),(float) (0),(float) (0));
 //BA.debugLineNum = 46;BA.debugLine="mInAnimation.Duration = 300";
_vvvvvvvvvvvvvv0.setDuration((long) (300));
 //BA.debugLineNum = 47;BA.debugLine="mOutAnimation.InitializeTranslate(\"Out\", Width, 0";
_vvvvvvvvvvvvvvv1.InitializeTranslate(ba,"Out",(float) (_width),(float) (0),(float) (0),(float) (0));
 //BA.debugLineNum = 48;BA.debugLine="mOutAnimation.Duration = 300";
_vvvvvvvvvvvvvvv1.setDuration((long) (300));
 //BA.debugLineNum = 50;BA.debugLine="Activity.AddView(mSlidePanel, 0, Top, Width, 100%";
_activity.AddView((android.view.View)(_vvvvvvvvvvvvvv3.getObject()),(int) (0),_top,_width,(int) (__c.PerYToCurrent((float) (100),ba)-_top));
 //BA.debugLineNum = 54;BA.debugLine="mBackPanel.Initialize(\"mBackPanel\")";
_vvvvvvvvvvvvvv4.Initialize(ba,"mBackPanel");
 //BA.debugLineNum = 55;BA.debugLine="mBackPanel.Color = Colors.Transparent";
_vvvvvvvvvvvvvv4.setColor(__c.Colors.Transparent);
 //BA.debugLineNum = 56;BA.debugLine="Activity.AddView(mBackPanel, -100%x, 0, 100%x, 10";
_activity.AddView((android.view.View)(_vvvvvvvvvvvvvv4.getObject()),(int) (-__c.PerXToCurrent((float) (100),ba)),(int) (0),__c.PerXToCurrent((float) (100),ba),__c.PerYToCurrent((float) (100),ba));
 //BA.debugLineNum = 58;BA.debugLine="mSlidePanel.Visible = False";
_vvvvvvvvvvvvvv3.setVisible(__c.False);
 //BA.debugLineNum = 59;BA.debugLine="End Sub";
return "";
}
public boolean  _vvvvvvvvvvvvv6() throws Exception{
 //BA.debugLineNum = 116;BA.debugLine="Public Sub isVisible As Boolean";
 //BA.debugLineNum = 117;BA.debugLine="Return mSlidePanel.Visible";
if (true) return _vvvvvvvvvvvvvv3.getVisible();
 //BA.debugLineNum = 118;BA.debugLine="End Sub";
return false;
}
public String  _vvvvvvvvvvvvv7(int _iposicionarray,String _simagen) throws Exception{
 //BA.debugLineNum = 215;BA.debugLine="Public Sub ItemArrayCambiaImagen(iPosicionArray As";
 //BA.debugLineNum = 217;BA.debugLine="If sImagen <>\"\" Then";
if ((_simagen).equals("") == false) { 
 //BA.debugLineNum = 218;BA.debugLine="aImagen(iPosicionArray).SetBackgroundImage(LoadB";
_vvvvvvvvvvvvvvv4[_iposicionarray].SetBackgroundImage((android.graphics.Bitmap)(__c.LoadBitmapSample(__c.File.getDirAssets(),_simagen,(int) (120),(int) (120)).getObject()));
 };
 //BA.debugLineNum = 221;BA.debugLine="End Sub";
return "";
}
public String  _labelc_click() throws Exception{
anywheresoftware.b4a.objects.LabelWrapper _send = null;
String _subname = "";
 //BA.debugLineNum = 195;BA.debugLine="Sub LabelC_Click";
 //BA.debugLineNum = 196;BA.debugLine="Dim Send As Label";
_send = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 197;BA.debugLine="Send = Sender'";
_send.setObject((android.widget.TextView)(__c.Sender(ba)));
 //BA.debugLineNum = 201;BA.debugLine="Send.Color = Colors.BLACK";
_send.setColor(__c.Colors.Black);
 //BA.debugLineNum = 202;BA.debugLine="DoEvents";
__c.DoEvents();
 //BA.debugLineNum = 203;BA.debugLine="Dim subname As String";
_subname = "";
 //BA.debugLineNum = 204;BA.debugLine="Hide	'oculta el menu";
_vvvvvvvvvvvvv5();
 //BA.debugLineNum = 205;BA.debugLine="subname = mEventName & \"_Click\"";
_subname = _vvvvvvvvvvvvvv6+"_Click";
 //BA.debugLineNum = 206;BA.debugLine="If SubExists(mModule, subname) Then";
if (__c.SubExists(ba,_vvvvvvvvvvvvvv5,_subname)) { 
 //BA.debugLineNum = 207;BA.debugLine="CallSub2(mModule, subname, Send)";
__c.CallSubNew2(ba,_vvvvvvvvvvvvvv5,_subname,(Object)(_send));
 };
 //BA.debugLineNum = 211;BA.debugLine="Send.Color = Colors.Transparent";
_send.setColor(__c.Colors.Transparent);
 //BA.debugLineNum = 213;BA.debugLine="End Sub";
return "";
}
public String  _mbackpanel_touch(int _action,float _x,float _y) throws Exception{
 //BA.debugLineNum = 108;BA.debugLine="Private Sub mBackPanel_Touch (Action As Int, X As";
 //BA.debugLineNum = 109;BA.debugLine="If Action = 1 Then";
if (_action==1) { 
 //BA.debugLineNum = 110;BA.debugLine="Hide";
_vvvvvvvvvvvvv5();
 };
 //BA.debugLineNum = 112;BA.debugLine="End Sub";
return "";
}
public String  _out_animationend() throws Exception{
 //BA.debugLineNum = 104;BA.debugLine="Private Sub Out_AnimationEnd";
 //BA.debugLineNum = 105;BA.debugLine="mSlidePanel.Visible = False";
_vvvvvvvvvvvvvv3.setVisible(__c.False);
 //BA.debugLineNum = 106;BA.debugLine="End Sub";
return "";
}
public String  _vvvvvvvvvvvvv0(anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _image) throws Exception{
anywheresoftware.b4a.objects.ImageViewWrapper _imagen = null;
 //BA.debugLineNum = 121;BA.debugLine="Public Sub SetBackground(Image As Bitmap)";
 //BA.debugLineNum = 122;BA.debugLine="Dim imagen As ImageView";
_imagen = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 123;BA.debugLine="imagen.Initialize(\"Imagen\")";
_imagen.Initialize(ba,"Imagen");
 //BA.debugLineNum = 124;BA.debugLine="mSlidePanel.AddView(imagen, 0,0,mSlidePanel.Width,";
_vvvvvvvvvvvvvv3.AddView((android.view.View)(_imagen.getObject()),(int) (0),(int) (0),_vvvvvvvvvvvvvv3.getWidth(),_vvvvvvvvvvvvvv3.getHeight());
 //BA.debugLineNum = 125;BA.debugLine="imagen.SetBackgroundImage (Image)";
_imagen.SetBackgroundImage((android.graphics.Bitmap)(_image.getObject()));
 //BA.debugLineNum = 128;BA.debugLine="End Sub";
return "";
}
public String  _vvvvvvvvvvvvvv1() throws Exception{
 //BA.debugLineNum = 83;BA.debugLine="Public Sub Show";
 //BA.debugLineNum = 84;BA.debugLine="If isVisible = True Then Return";
if (_vvvvvvvvvvvvv6()==__c.True) { 
if (true) return "";};
 //BA.debugLineNum = 86;BA.debugLine="mBackPanel.BringToFront";
_vvvvvvvvvvvvvv4.BringToFront();
 //BA.debugLineNum = 87;BA.debugLine="mSlidePanel.BringToFront";
_vvvvvvvvvvvvvv3.BringToFront();
 //BA.debugLineNum = 88;BA.debugLine="mBackPanel.Left = 0";
_vvvvvvvvvvvvvv4.setLeft((int) (0));
 //BA.debugLineNum = 89;BA.debugLine="mSlidePanel.Left = 0";
_vvvvvvvvvvvvvv3.setLeft((int) (0));
 //BA.debugLineNum = 91;BA.debugLine="mSlidePanel.Visible = True";
_vvvvvvvvvvvvvv3.setVisible(__c.True);
 //BA.debugLineNum = 92;BA.debugLine="mInAnimation.Start(mSlidePanel)";
_vvvvvvvvvvvvvv0.Start((android.view.View)(_vvvvvvvvvvvvvv3.getObject()));
 //BA.debugLineNum = 93;BA.debugLine="End Sub";
return "";
}
public String  _vvvvvvvvvvvvvv2(anywheresoftware.b4a.objects.ConcreteViewWrapper _xview,int _ileft,int _itop,int _iwidth,int _iheight,boolean _bfront,boolean _bback) throws Exception{
 //BA.debugLineNum = 134;BA.debugLine="Sub smAddView(xView As View, iLeft As Int, iTop As";
 //BA.debugLineNum = 135;BA.debugLine="If iwidth = -1 Then";
if (_iwidth==-1) { 
 //BA.debugLineNum = 136;BA.debugLine="iwidth = mSlidePanel.Width";
_iwidth = _vvvvvvvvvvvvvv3.getWidth();
 };
 //BA.debugLineNum = 138;BA.debugLine="If iheight = -1 Then";
if (_iheight==-1) { 
 //BA.debugLineNum = 139;BA.debugLine="iheight = mSlidePanel.width";
_iheight = _vvvvvvvvvvvvvv3.getWidth();
 };
 //BA.debugLineNum = 143;BA.debugLine="mSlidePanel.AddView(xView, iLeft, iTop, iwidth, i";
_vvvvvvvvvvvvvv3.AddView((android.view.View)(_xview.getObject()),_ileft,_itop,_iwidth,_iheight);
 //BA.debugLineNum = 144;BA.debugLine="If bFront Then";
if (_bfront) { 
 //BA.debugLineNum = 145;BA.debugLine="xView.BringToFront";
_xview.BringToFront();
 };
 //BA.debugLineNum = 147;BA.debugLine="If bBack Then";
if (_bback) { 
 //BA.debugLineNum = 148;BA.debugLine="xView.SendToBack";
_xview.SendToBack();
 };
 //BA.debugLineNum = 151;BA.debugLine="End Sub";
return "";
}
public Object callSub(String sub, Object sender, Object[] args) throws Exception {
BA.senderHolder.set(sender);
return BA.SubDelegator.SubNotFound;
}
}
