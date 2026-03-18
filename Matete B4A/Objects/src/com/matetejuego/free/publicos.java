package com.matetejuego.free;


import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.debug.*;

public class publicos {
private static publicos mostCurrent = new publicos();
public static Object getObject() {
    throw new RuntimeException("Code module does not support this method.");
}
 public anywheresoftware.b4a.keywords.Common __c = null;
public static String _v5 = "";
public static int _g_devicevaluesheight = 0;
public static int _g_devicevalueswidth = 0;
public static float _g_devicevaluesscreensize = 0f;
public static float _g_devicevaluesscale = 0f;
public static String _g_spswbasej = "";
public static String _g_spswbaseu = "";
public static anyhwheresoftware.b4a.objects.sqlcipher.SQLCipher _g_sqlbaselocaljuego = null;
public static anyhwheresoftware.b4a.objects.sqlcipher.SQLCipher _g_sqlbaselocalusuario = null;
public static String _g_nombrebaselocaljuego = "";
public static String _g_nombrebaselocalusuario = "";
public com.matetejuego.free.main _vvvvvvvvvvvvvvv6 = null;
public com.matetejuego.free.jugar _vvvvvvvvvvvvvvv0 = null;
public com.matetejuego.free.facebookactivity _vvvvvvvvvvvvvvvv1 = null;
public com.matetejuego.free.twitter _vvvvvvvvvvvvvvvv2 = null;
public com.matetejuego.free.svcbaseremota _vvvvvvvvvvvvvvvv3 = null;
public static class _tcolorespaneles{
public boolean IsInitialized;
public int iNroPanel;
public int iRgb1;
public int iRgb2;
public int iRgb3;
public void Initialize() {
IsInitialized = true;
iNroPanel = 0;
iRgb1 = 0;
iRgb2 = 0;
iRgb3 = 0;
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public static class _tproductosps{
public boolean IsInitialized;
public String ProductID;
public boolean ManagedProduct;
public String Title;
public String Description;
public String Price;
public boolean Owned;
public boolean ApagaPropaganda;
public int Neuronas;
public boolean ExlusivoPremium;
public int NeuronasOriginales;
public boolean CompraPremiumOriginal;
public void Initialize() {
IsInitialized = true;
ProductID = "";
ManagedProduct = false;
Title = "";
Description = "";
Price = "";
Owned = false;
ApagaPropaganda = false;
Neuronas = 0;
ExlusivoPremium = false;
NeuronasOriginales = 0;
CompraPremiumOriginal = false;
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public static class _tproductoscomprados{
public boolean IsInitialized;
public String ProductID;
public int State;
public boolean Owned;
public void Initialize() {
IsInitialized = true;
ProductID = "";
State = 0;
Owned = false;
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public static class _tcolorespaleta{
public boolean IsInitialized;
public String ColorNombre;
public int ColorTexto;
public int ColorClaro;
public int ColorIntermedio;
public int ColorOscuro;
public int ColorDefault;
public void Initialize() {
IsInitialized = true;
ColorNombre = "";
ColorTexto = 0;
ColorClaro = 0;
ColorIntermedio = 0;
ColorOscuro = 0;
ColorDefault = 0;
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public static class _tcolores{
public boolean IsInitialized;
public int Paleta;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper bitmBajar;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper bitmSubir;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper bitmPalabraOff;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper bitmPalabraOn;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper bitmLetra;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper bitmMenu;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper bitmCompartir;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper bitmMute;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper bitmPedir;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper bitmSaltarPalabra;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper bitmFlag;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper bitmPalabraError;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper bitmMenuFondo;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper bitmBajarLetras;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper bitmPalabraComprada;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper bitmCostos;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper bitmShareConfirmar;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper bitmShareCancelar;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper bitmShareFB;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper bitmShareTw;
public int ColorClaro;
public int ColorMedio;
public int ColorOscuro;
public int ColorTexto;
public String ColorNombre;
public int ColorDefault;
public void Initialize() {
IsInitialized = true;
Paleta = 0;
bitmBajar = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
bitmSubir = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
bitmPalabraOff = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
bitmPalabraOn = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
bitmLetra = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
bitmMenu = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
bitmCompartir = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
bitmMute = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
bitmPedir = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
bitmSaltarPalabra = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
bitmFlag = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
bitmPalabraError = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
bitmMenuFondo = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
bitmBajarLetras = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
bitmPalabraComprada = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
bitmCostos = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
bitmShareConfirmar = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
bitmShareCancelar = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
bitmShareFB = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
bitmShareTw = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
ColorClaro = 0;
ColorMedio = 0;
ColorOscuro = 0;
ColorTexto = 0;
ColorNombre = "";
ColorDefault = 0;
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public static class _tordenar{
public boolean IsInitialized;
public int Indice;
public int Orden;
public char Letra;
public void Initialize() {
IsInitialized = true;
Indice = 0;
Orden = 0;
Letra = '\0';
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public static class _tmensajeaparece{
public boolean IsInitialized;
public anywheresoftware.b4a.objects.LabelWrapper lblLetra;
public int Posicion;
public boolean Mostrada;
public String sTipoLetra;
public int Renglon;
public int PosEnPalabra;
public void Initialize() {
IsInitialized = true;
lblLetra = new anywheresoftware.b4a.objects.LabelWrapper();
Posicion = 0;
Mostrada = false;
sTipoLetra = "";
Renglon = 0;
PosEnPalabra = 0;
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public static class _tletraselegir{
public boolean IsInitialized;
public char LetraSiempre;
public char LetraMovil;
public char LetraEnLabel;
public void Initialize() {
IsInitialized = true;
LetraSiempre = '\0';
LetraMovil = '\0';
LetraEnLabel = '\0';
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public static class _thistoria{
public boolean IsInitialized;
public int idPalabra;
public int Adivinada;
public int Salteada;
public String Definicion;
public void Initialize() {
IsInitialized = true;
idPalabra = 0;
Adivinada = 0;
Salteada = 0;
Definicion = "";
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public static class _tletraspalabra{
public boolean IsInitialized;
public int idpalabra;
public int posicion;
public char letra;
public boolean comprada;
public void Initialize() {
IsInitialized = true;
idpalabra = 0;
posicion = 0;
letra = '\0';
comprada = false;
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public static class _tpalabra{
public boolean IsInitialized;
public int idPalabra;
public String Palabra;
public String Descripcion;
public String Dificultad;
public String TipoPalabra;
public int idNivel;
public String Ayuda01;
public String Ayuda02;
public String Ayuda03;
public String Diccionario;
public String PalabraDiccionario;
public boolean bRejugada;
public boolean bSalteada;
public void Initialize() {
IsInitialized = true;
idPalabra = 0;
Palabra = "";
Descripcion = "";
Dificultad = "";
TipoPalabra = "";
idNivel = 0;
Ayuda01 = "";
Ayuda02 = "";
Ayuda03 = "";
Diccionario = "";
PalabraDiccionario = "";
bRejugada = false;
bSalteada = false;
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public static class _tnivel{
public boolean IsInitialized;
public int idNivel;
public String Nombre;
public String ImagenFondo;
public int CostoSaltar;
public int CostoLetra;
public int CostoAyuda;
public String MensajeInicio;
public String MensajeFinal;
public String ImagenInicio;
public String ImagenFinal;
public int IdPrimeraPalabra;
public int IdUltimaPalabra;
public boolean bMensajeInicioMostrado;
public void Initialize() {
IsInitialized = true;
idNivel = 0;
Nombre = "";
ImagenFondo = "";
CostoSaltar = 0;
CostoLetra = 0;
CostoAyuda = 0;
MensajeInicio = "";
MensajeFinal = "";
ImagenInicio = "";
ImagenFinal = "";
IdPrimeraPalabra = 0;
IdUltimaPalabra = 0;
bMensajeInicioMostrado = false;
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public static class _tpistas{
public boolean IsInitialized;
public int pistaid;
public String pista;
public String tipopista;
public int comprada;
public int gratis;
public int valor;
public void Initialize() {
IsInitialized = true;
pistaid = 0;
pista = "";
tipopista = "";
comprada = 0;
gratis = 0;
valor = 0;
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public static class _tconfigurapantalla{
public boolean IsInitialized;
public int Compartir;
public int ComprarLetra;
public int SaltarPalabra;
public int Jugar;
public int Adivino;
public int Ayuda;
public int Creditos;
public int FinDeJuego;
public int Producto;
public int ComproNeuronas;
public int MuestraAviso;
public int Premium;
public int Historia;
public void Initialize() {
IsInitialized = true;
Compartir = 0;
ComprarLetra = 0;
SaltarPalabra = 0;
Jugar = 0;
Adivino = 0;
Ayuda = 0;
Creditos = 0;
FinDeJuego = 0;
Producto = 0;
ComproNeuronas = 0;
MuestraAviso = 0;
Premium = 0;
Historia = 0;
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public static class _tquerysremotos{
public boolean IsInitialized;
public int Premium;
public int Avance;
public void Initialize() {
IsInitialized = true;
Premium = 0;
Avance = 0;
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public static String  _v6(anywheresoftware.b4a.BA _ba,anywheresoftware.b4a.sql.SQL _base,String _sdirgrabable,String _sbasenombre) throws Exception{
 //BA.debugLineNum = 408;BA.debugLine="Public Sub AbreBase (Base As SQL, sDirGrabable As";
 //BA.debugLineNum = 409;BA.debugLine="ProgressDialogShow(\"Initializing database...\")";
anywheresoftware.b4a.keywords.Common.ProgressDialogShow(_ba,"Initializing database...");
 //BA.debugLineNum = 410;BA.debugLine="Try";
try { //BA.debugLineNum = 411;BA.debugLine="If Base.IsInitialized = False Then";
if (_base.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 412;BA.debugLine="Base.Initialize(sDirGrabable,sBaseNombre,True)";
_base.Initialize(_sdirgrabable,_sbasenombre,anywheresoftware.b4a.keywords.Common.True);
 };
 } 
       catch (Exception e7) {
			(_ba.processBA == null ? _ba : _ba.processBA).setLastException(e7); //BA.debugLineNum = 416;BA.debugLine="ManejaError(\"AbreBase\",\"error base\")";
_vvv7(_ba,"AbreBase","error base");
 };
 //BA.debugLineNum = 420;BA.debugLine="ProgressDialogHide";
anywheresoftware.b4a.keywords.Common.ProgressDialogHide();
 //BA.debugLineNum = 421;BA.debugLine="End Sub";
return "";
}
public static String  _v7(anywheresoftware.b4a.BA _ba,anyhwheresoftware.b4a.objects.sqlcipher.SQLCipher _base,String _sdirgrabable,String _sbasenombre,String _spsw) throws Exception{
 //BA.debugLineNum = 423;BA.debugLine="Public Sub AbreBaseCipher (Base As SQLCipher, sDir";
 //BA.debugLineNum = 424;BA.debugLine="ProgressDialogShow(\"Initializing database...\")";
anywheresoftware.b4a.keywords.Common.ProgressDialogShow(_ba,"Initializing database...");
 //BA.debugLineNum = 425;BA.debugLine="Try";
try { //BA.debugLineNum = 426;BA.debugLine="If Base.IsInitialized = False Then";
if (_base.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 427;BA.debugLine="Base.Initialize(sDirGrabable,sBaseNombre,True, s";
_base.Initialize(_sdirgrabable,_sbasenombre,anywheresoftware.b4a.keywords.Common.True,_spsw,"");
 };
 } 
       catch (Exception e7) {
			(_ba.processBA == null ? _ba : _ba.processBA).setLastException(e7); //BA.debugLineNum = 431;BA.debugLine="ManejaError(\"AbreBase\",\"error base\")";
_vvv7(_ba,"AbreBase","error base");
 };
 //BA.debugLineNum = 434;BA.debugLine="ProgressDialogHide";
anywheresoftware.b4a.keywords.Common.ProgressDialogHide();
 //BA.debugLineNum = 435;BA.debugLine="End Sub";
return "";
}
public static String  _v0(anywheresoftware.b4a.BA _ba) throws Exception{
String _sdirbase = "";
 //BA.debugLineNum = 920;BA.debugLine="Sub AbreBases";
 //BA.debugLineNum = 921;BA.debugLine="Dim sDirBase As String = Get_DirectorioBase";
_sdirbase = _get_directoriobase(_ba);
 //BA.debugLineNum = 923;BA.debugLine="AbreBaseCipher(g_sqlBaseLocalJuego, sDirBase, g_N";
_v7(_ba,_g_sqlbaselocaljuego,_sdirbase,_g_nombrebaselocaljuego,_g_spswbasej);
 //BA.debugLineNum = 924;BA.debugLine="AbreBaseCipher(g_sqlBaseLocalUsuario, sDirBase, g";
_v7(_ba,_g_sqlbaselocalusuario,_sdirbase,_g_nombrebaselocalusuario,_g_spswbaseu);
 //BA.debugLineNum = 926;BA.debugLine="End Sub";
return "";
}
public static String  _vv1(anywheresoftware.b4a.BA _ba,anyhwheresoftware.b4a.objects.sqlcipher.SQLCipher _g_objetobasejuego,String _g_nombrebasejuego,String _spswjuego,anyhwheresoftware.b4a.objects.sqlcipher.SQLCipher _g_objetobaseusuario,String _g_nombrebaseusuario,String _spswusuario) throws Exception{
String _sdirbase = "";
 //BA.debugLineNum = 928;BA.debugLine="Sub AbreBasesParam (g_ObjetoBaseJuego As SQLCipher";
 //BA.debugLineNum = 930;BA.debugLine="Dim sDirBase As String = Get_DirectorioBase";
_sdirbase = _get_directoriobase(_ba);
 //BA.debugLineNum = 932;BA.debugLine="AbreBaseCipher(g_ObjetoBaseJuego , sDirBase, g_No";
_v7(_ba,_g_objetobasejuego,_sdirbase,_g_nombrebasejuego,_spswjuego);
 //BA.debugLineNum = 933;BA.debugLine="AbreBaseCipher(g_objetoBaseUsuario , sDirBase, g_";
_v7(_ba,_g_objetobaseusuario,_sdirbase,_g_nombrebaseusuario,_spswusuario);
 //BA.debugLineNum = 935;BA.debugLine="End Sub";
return "";
}
public static String  _vv2(anywheresoftware.b4a.BA _ba,anywheresoftware.b4a.objects.ImageViewWrapper _imgfondo,int _iwidthactivity,int _iheightactivity,int _ianchoimagen,int _ialtoimagen) throws Exception{
double _drelacionimagen = 0;
double _drelacionpantalla = 0;
 //BA.debugLineNum = 533;BA.debugLine="Sub AcomodarFondoInicio(imgFondo As ImageView, iWi";
 //BA.debugLineNum = 535;BA.debugLine="Dim dRelacionImagen As Double, 	dRelacionPantalla";
_drelacionimagen = 0;
_drelacionpantalla = 0;
 //BA.debugLineNum = 537;BA.debugLine="dRelacionImagen = iAltoImagen/iAnchoImagen";
_drelacionimagen = _ialtoimagen/(double)_ianchoimagen;
 //BA.debugLineNum = 538;BA.debugLine="dRelacionPantalla =  iHeightActivity/iWidthActivi";
_drelacionpantalla = _iheightactivity/(double)_iwidthactivity;
 //BA.debugLineNum = 540;BA.debugLine="If dRelacionPantalla<dRelacionImagen Then";
if (_drelacionpantalla<_drelacionimagen) { 
 //BA.debugLineNum = 541;BA.debugLine="imgFondo.Width = iWidthActivity";
_imgfondo.setWidth(_iwidthactivity);
 //BA.debugLineNum = 542;BA.debugLine="imgFondo.Height = imgFondo.width * dRelacionImag";
_imgfondo.setHeight((int) (_imgfondo.getWidth()*_drelacionimagen));
 //BA.debugLineNum = 543;BA.debugLine="imgFondo.Top = (iHeightActivity-imgFondo.Height";
_imgfondo.setTop((int) ((_iheightactivity-_imgfondo.getHeight())));
 //BA.debugLineNum = 544;BA.debugLine="imgFondo.Left = 0";
_imgfondo.setLeft((int) (0));
 }else {
 //BA.debugLineNum = 547;BA.debugLine="imgFondo.Height = iHeightActivity";
_imgfondo.setHeight(_iheightactivity);
 //BA.debugLineNum = 548;BA.debugLine="imgFondo.Width = imgFondo.Height/dRelacionImagen";
_imgfondo.setWidth((int) (_imgfondo.getHeight()/(double)_drelacionimagen));
 //BA.debugLineNum = 549;BA.debugLine="imgFondo.Top = 0";
_imgfondo.setTop((int) (0));
 //BA.debugLineNum = 550;BA.debugLine="imgFondo.Left = (iWidthActivity - imgFondo.Width";
_imgfondo.setLeft((int) ((_iwidthactivity-_imgfondo.getWidth())/(double)2));
 };
 //BA.debugLineNum = 552;BA.debugLine="End Sub";
return "";
}
public static String  _activity_dimensionpantalla(anywheresoftware.b4a.BA _ba,anywheresoftware.b4a.objects.ActivityWrapper _xact) throws Exception{
 //BA.debugLineNum = 470;BA.debugLine="Public Sub Activity_DimensionPantalla (xAct As Act";
 //BA.debugLineNum = 471;BA.debugLine="xAct.Width = g_DeviceValuesWidth";
_xact.setWidth(_g_devicevalueswidth);
 //BA.debugLineNum = 472;BA.debugLine="xAct.Height = g_DeviceValuesHeight";
_xact.setHeight(_g_devicevaluesheight);
 //BA.debugLineNum = 473;BA.debugLine="End Sub";
return "";
}
public static boolean  _archivo_copiadesdeassets(anywheresoftware.b4a.BA _ba,String _sfilename,boolean _bforzar,String _sdirectorio) throws Exception{
boolean _bret = false;
 //BA.debugLineNum = 452;BA.debugLine="Public Sub Archivo_CopiaDesdeAssets (sFileName As";
 //BA.debugLineNum = 455;BA.debugLine="Dim bRet As Boolean=False";
_bret = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 457;BA.debugLine="If sDirectorio = \"\" Then";
if ((_sdirectorio).equals("")) { 
 //BA.debugLineNum = 458;BA.debugLine="sDirectorio= Get_DirectorioBase";
_sdirectorio = _get_directoriobase(_ba);
 };
 //BA.debugLineNum = 462;BA.debugLine="If File.Exists(sDirectorio, sFileName) = False O";
if (anywheresoftware.b4a.keywords.Common.File.Exists(_sdirectorio,_sfilename)==anywheresoftware.b4a.keywords.Common.False || _bforzar) { 
 //BA.debugLineNum = 463;BA.debugLine="File.Copy(File.DirAssets, sFileName,sDirectorio";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),_sfilename,_sdirectorio,_sfilename);
 //BA.debugLineNum = 464;BA.debugLine="bRet= True";
_bret = anywheresoftware.b4a.keywords.Common.True;
 };
 //BA.debugLineNum = 466;BA.debugLine="Return bRet";
if (true) return _bret;
 //BA.debugLineNum = 467;BA.debugLine="End Sub";
return false;
}
public static String  _vv3(anywheresoftware.b4a.BA _ba,int _ihasta) throws Exception{
String _ssql = "";
String _sfecha = "";
int _j = 0;
 //BA.debugLineNum = 363;BA.debugLine="Public Sub AvanzaJuego (iHasta As Int)";
 //BA.debugLineNum = 364;BA.debugLine="Dim sSql As String', sFecha As String, bRet As Bo";
_ssql = "";
 //BA.debugLineNum = 365;BA.debugLine="Dim sFecha As String";
_sfecha = "";
 //BA.debugLineNum = 366;BA.debugLine="Try";
try { //BA.debugLineNum = 367;BA.debugLine="g_sqlBaseLocalUsuario.BeginTransaction";
_g_sqlbaselocalusuario.BeginTransaction();
 //BA.debugLineNum = 370;BA.debugLine="For j = 1 To iHasta";
{
final int step5 = 1;
final int limit5 = _ihasta;
for (_j = (int) (1) ; (step5 > 0 && _j <= limit5) || (step5 < 0 && _j >= limit5); _j = ((int)(0 + _j + step5)) ) {
 //BA.debugLineNum = 371;BA.debugLine="sFecha = DateTime.Date(DateTime.now)";
_sfecha = anywheresoftware.b4a.keywords.Common.DateTime.Date(anywheresoftware.b4a.keywords.Common.DateTime.getNow());
 //BA.debugLineNum = 372;BA.debugLine="sSql = \"Insert Into Avance (idPalabra, Inicio,";
_ssql = "Insert Into Avance (idPalabra, Inicio, adivinada, salteada, fin) Values (";
 //BA.debugLineNum = 373;BA.debugLine="sSql = sSql & j & \",'\" & sFecha & \"',1,0,'\" & s";
_ssql = _ssql+BA.NumberToString(_j)+",'"+_sfecha+"',1,0,'"+_sfecha+"')";
 //BA.debugLineNum = 375;BA.debugLine="g_sqlBaseLocalUsuario.ExecNonQuery (sSql)";
_g_sqlbaselocalusuario.ExecNonQuery(_ssql);
 }
};
 //BA.debugLineNum = 377;BA.debugLine="sSql = \"Update Usuario set Monedas = 100, puntos";
_ssql = "Update Usuario set Monedas = 100, puntos = "+BA.NumberToString(_ihasta)+", idnivel = 0";
 //BA.debugLineNum = 378;BA.debugLine="g_sqlBaseLocalUsuario.ExecNonQuery (sSql)";
_g_sqlbaselocalusuario.ExecNonQuery(_ssql);
 //BA.debugLineNum = 380;BA.debugLine="g_sqlBaseLocalUsuario.TransactionSuccessful";
_g_sqlbaselocalusuario.TransactionSuccessful();
 //BA.debugLineNum = 381;BA.debugLine="g_sqlBaseLocalUsuario.EndTransaction";
_g_sqlbaselocalusuario.EndTransaction();
 } 
       catch (Exception e16) {
			(_ba.processBA == null ? _ba : _ba.processBA).setLastException(e16); //BA.debugLineNum = 386;BA.debugLine="g_sqlBaseLocalUsuario.EndTransaction";
_g_sqlbaselocalusuario.EndTransaction();
 //BA.debugLineNum = 387;BA.debugLine="ManejaError(\"ReiniciaJuego\",\"Error Actualizar Pi";
_vvv7(_ba,"ReiniciaJuego","Error Actualizar Pistas");
 };
 //BA.debugLineNum = 390;BA.debugLine="End Sub";
return "";
}
public static boolean  _base_actualizapalabrasdesdealmacen(anywheresoftware.b4a.BA _ba,anyhwheresoftware.b4a.objects.sqlcipher.SQLCipher _basejuego,boolean _bpremium,boolean _busuarioantiguo) throws Exception{
String _sselect = "";
String _sinsert = "";
String _bret = "";
String _sincluye = "";
 //BA.debugLineNum = 1295;BA.debugLine="Sub Base_ActualizaPalabrasDesdeAlmacen(BaseJuego A";
 //BA.debugLineNum = 1301;BA.debugLine="Dim sSelect As String, sInsert As String, bRet =";
_sselect = "";
_sinsert = "";
_bret = BA.ObjectToString(anywheresoftware.b4a.keywords.Common.False);
_sincluye = "";
 //BA.debugLineNum = 1303;BA.debugLine="sSelect = \"Select pa.idpalabra, pa.palabra, pa.de";
_sselect = "Select pa.idpalabra, pa.palabra, pa.descripcion, pa.dificultad, pa.tipopalabra, pa.idnivel, pa.ayuda01, "+"pa.ayuda02, pa.ayuda03, pa.Diccionario, pa.PalabraDiccionario "+"FROM palabras_almacen  As pa left join palabras As p on p.idpalabra  = pa.idpalabra "+"where p.idpalabra Is Null AND Premium In ('N'";
 //BA.debugLineNum = 1309;BA.debugLine="If bPremium  Then";
if (_bpremium) { 
 //BA.debugLineNum = 1310;BA.debugLine="sSelect = sSelect & \" , 'S', 'X', 'x', 's'\"";
_sselect = _sselect+" , 'S', 'X', 'x', 's'";
 }else {
 //BA.debugLineNum = 1313;BA.debugLine="If bUsuarioAntiguo Then";
if (_busuarioantiguo) { 
 //BA.debugLineNum = 1314;BA.debugLine="sSelect = sSelect & \" , 'X','x'\"";
_sselect = _sselect+" , 'X','x'";
 };
 };
 //BA.debugLineNum = 1317;BA.debugLine="sSelect = sSelect & \")\"";
_sselect = _sselect+")";
 //BA.debugLineNum = 1319;BA.debugLine="sInsert = \"Insert Into palabras (idpalabra, palab";
_sinsert = "Insert Into palabras (idpalabra, palabra, descripcion, dificultad, tipopalabra, idnivel, ayuda01, ayuda02, ayuda03, diccionario, palabradiccionario) ";
 //BA.debugLineNum = 1320;BA.debugLine="sInsert = sInsert & sSelect";
_sinsert = _sinsert+_sselect;
 //BA.debugLineNum = 1321;BA.debugLine="Try";
try { //BA.debugLineNum = 1322;BA.debugLine="BaseJuego.ExecNonQuery(sInsert)";
_basejuego.ExecNonQuery(_sinsert);
 //BA.debugLineNum = 1324;BA.debugLine="bRet = True";
_bret = BA.ObjectToString(anywheresoftware.b4a.keywords.Common.True);
 } 
       catch (Exception e17) {
			(_ba.processBA == null ? _ba : _ba.processBA).setLastException(e17); //BA.debugLineNum = 1326;BA.debugLine="ManejaError(\"PalabrasAlmacen\", LastException.Mes";
_vvv7(_ba,"PalabrasAlmacen",anywheresoftware.b4a.keywords.Common.LastException(_ba).getMessage());
 };
 //BA.debugLineNum = 1329;BA.debugLine="Return bRet";
if (true) return BA.ObjectToBoolean(_bret);
 //BA.debugLineNum = 1330;BA.debugLine="End Sub";
return false;
}
public static String  _vv4(anywheresoftware.b4a.BA _ba) throws Exception{
anywheresoftware.b4a.keywords.LayoutValues _devicevalues = null;
 //BA.debugLineNum = 103;BA.debugLine="Public Sub CargaDeviceValues";
 //BA.debugLineNum = 105;BA.debugLine="Dim DeviceValues As LayoutValues: DeviceValues =";
_devicevalues = new anywheresoftware.b4a.keywords.LayoutValues();
 //BA.debugLineNum = 105;BA.debugLine="Dim DeviceValues As LayoutValues: DeviceValues =";
_devicevalues = anywheresoftware.b4a.keywords.Common.GetDeviceLayoutValues(_ba);
 //BA.debugLineNum = 108;BA.debugLine="g_DeviceValuesHeight =  DeviceValues.Height";
_g_devicevaluesheight = _devicevalues.Height;
 //BA.debugLineNum = 109;BA.debugLine="g_DeviceValuesWidth =  DeviceValues.Width";
_g_devicevalueswidth = _devicevalues.Width;
 //BA.debugLineNum = 110;BA.debugLine="g_DeviceValuesScreenSize = DeviceValues.Approxima";
_g_devicevaluesscreensize = (float) (_devicevalues.getApproximateScreenSize());
 //BA.debugLineNum = 111;BA.debugLine="g_DeviceValuesScale = DeviceValues.Scale";
_g_devicevaluesscale = _devicevalues.Scale;
 //BA.debugLineNum = 113;BA.debugLine="End Sub";
return "";
}
public static String  _vv5(anywheresoftware.b4a.BA _ba,String _sletra,anywheresoftware.b4a.objects.LabelWrapper _lblletra) throws Exception{
String _sarchivo = "";
 //BA.debugLineNum = 504;BA.debugLine="Sub CargaImagenLetra (sLetra As String, lblLetra A";
 //BA.debugLineNum = 505;BA.debugLine="Dim sArchivo As String";
_sarchivo = "";
 //BA.debugLineNum = 507;BA.debugLine="If sLetra.ToUpperCase.CharAt(0) = \"Ñ\" Then";
if (_sletra.toUpperCase().charAt((int) (0))==BA.ObjectToChar("Ñ")) { 
 //BA.debugLineNum = 508;BA.debugLine="If sLetra.Length >1 Then";
if (_sletra.length()>1) { 
 //BA.debugLineNum = 509;BA.debugLine="sArchivo = \"Ni\" & sLetra.SubString2(1, sLetra.L";
_sarchivo = "Ni"+_sletra.substring((int) (1),_sletra.length())+".png";
 }else {
 //BA.debugLineNum = 511;BA.debugLine="sArchivo = \"Ni.png\"";
_sarchivo = "Ni.png";
 };
 }else {
 //BA.debugLineNum = 514;BA.debugLine="sArchivo = sLetra & \".png\"";
_sarchivo = _sletra+".png";
 };
 //BA.debugLineNum = 517;BA.debugLine="lblLetra.SetBackgroundImage (LoadBitmap(File.DirA";
_lblletra.SetBackgroundImage((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),_sarchivo).getObject()));
 //BA.debugLineNum = 519;BA.debugLine="End Sub";
return "";
}
public static String  _vv6(anywheresoftware.b4a.BA _ba,anywheresoftware.b4a.objects.PanelWrapper _pnlpanel,anywheresoftware.b4a.objects.ConcreteViewWrapper[] _imgarray,boolean _vertical) throws Exception{
int _ianchocontroles = 0;
int _iseparacion = 0;
int _ileft = 0;
int _j = 0;
 //BA.debugLineNum = 475;BA.debugLine="Public Sub CentrarArrayControlesEnPanel(pnlPanel A";
 //BA.debugLineNum = 476;BA.debugLine="Dim iAnchoControles As Int = 0, iSeparacion As In";
_ianchocontroles = (int) (0);
_iseparacion = (int) (0);
_ileft = 0;
 //BA.debugLineNum = 479;BA.debugLine="For j = 0 To imgArray.Length-1";
{
final int step2 = 1;
final int limit2 = (int) (_imgarray.length-1);
for (_j = (int) (0) ; (step2 > 0 && _j <= limit2) || (step2 < 0 && _j >= limit2); _j = ((int)(0 + _j + step2)) ) {
 //BA.debugLineNum = 480;BA.debugLine="iAnchoControles = iAnchoControles+imgArray(j).wi";
_ianchocontroles = (int) (_ianchocontroles+_imgarray[_j].getWidth());
 }
};
 //BA.debugLineNum = 484;BA.debugLine="iSeparacion = (pnlPanel.Width-iAnchoControles )/";
_iseparacion = (int) ((_pnlpanel.getWidth()-_ianchocontroles)/(double)(_imgarray.length+1));
 //BA.debugLineNum = 487;BA.debugLine="iLeft = iSeparacion";
_ileft = _iseparacion;
 //BA.debugLineNum = 488;BA.debugLine="For j = 0 To imgArray.Length-1";
{
final int step7 = 1;
final int limit7 = (int) (_imgarray.length-1);
for (_j = (int) (0) ; (step7 > 0 && _j <= limit7) || (step7 < 0 && _j >= limit7); _j = ((int)(0 + _j + step7)) ) {
 //BA.debugLineNum = 489;BA.debugLine="imgArray(j).Left = iLeft";
_imgarray[_j].setLeft(_ileft);
 //BA.debugLineNum = 490;BA.debugLine="iLeft = iLeft + imgArray(j).Width + iSeparacion";
_ileft = (int) (_ileft+_imgarray[_j].getWidth()+_iseparacion);
 }
};
 //BA.debugLineNum = 495;BA.debugLine="If vertical Then";
if (_vertical) { 
 //BA.debugLineNum = 496;BA.debugLine="For j=0 To imgArray.Length-1";
{
final int step12 = 1;
final int limit12 = (int) (_imgarray.length-1);
for (_j = (int) (0) ; (step12 > 0 && _j <= limit12) || (step12 < 0 && _j >= limit12); _j = ((int)(0 + _j + step12)) ) {
 //BA.debugLineNum = 497;BA.debugLine="imgArray(j).top = pnlPanel.Height / 2 - imgArra";
_imgarray[_j].setTop((int) (_pnlpanel.getHeight()/(double)2-_imgarray[_j].getHeight()/(double)2));
 }
};
 };
 //BA.debugLineNum = 502;BA.debugLine="End Sub";
return "";
}
public static String  _vv7(anywheresoftware.b4a.BA _ba,anywheresoftware.b4a.objects.ActivityWrapper _activity,anywheresoftware.b4a.objects.ConcreteViewWrapper _xcontrol,boolean _horizontal,boolean _vertical) throws Exception{
 //BA.debugLineNum = 143;BA.debugLine="Public Sub CentrarControl (Activity As Activity, x";
 //BA.debugLineNum = 145;BA.debugLine="If Horizontal Then";
if (_horizontal) { 
 //BA.debugLineNum = 146;BA.debugLine="xControl.Left = Activity.Width / 2 - xControl.Wi";
_xcontrol.setLeft((int) (_activity.getWidth()/(double)2-_xcontrol.getWidth()/(double)2));
 };
 //BA.debugLineNum = 148;BA.debugLine="If Vertical Then";
if (_vertical) { 
 //BA.debugLineNum = 149;BA.debugLine="xControl.top = Activity.Height / 2 - xControl.He";
_xcontrol.setTop((int) (_activity.getHeight()/(double)2-_xcontrol.getHeight()/(double)2));
 };
 //BA.debugLineNum = 152;BA.debugLine="End Sub";
return "";
}
public static String  _vv0(anywheresoftware.b4a.BA _ba,anywheresoftware.b4a.objects.PanelWrapper _xpanel,anywheresoftware.b4a.objects.ConcreteViewWrapper _xcontrol,boolean _horizontal,boolean _vertical) throws Exception{
 //BA.debugLineNum = 154;BA.debugLine="Public Sub CentrarControlEnPanel (xPanel As Panel,";
 //BA.debugLineNum = 156;BA.debugLine="If Horizontal Then";
if (_horizontal) { 
 //BA.debugLineNum = 157;BA.debugLine="xControl.Left = xPanel.Width / 2 - xControl.Widt";
_xcontrol.setLeft((int) (_xpanel.getWidth()/(double)2-_xcontrol.getWidth()/(double)2));
 };
 //BA.debugLineNum = 159;BA.debugLine="If Vertical Then";
if (_vertical) { 
 //BA.debugLineNum = 160;BA.debugLine="xControl.top = xPanel.Height / 2 - xControl.Heig";
_xcontrol.setTop((int) (_xpanel.getHeight()/(double)2-_xcontrol.getHeight()/(double)2));
 };
 //BA.debugLineNum = 165;BA.debugLine="End Sub";
return "";
}
public static int  _vvv1(anywheresoftware.b4a.BA _ba,int _idipsstandard,char _sheightwidth) throws Exception{
int _iret = 0;
 //BA.debugLineNum = 117;BA.debugLine="Public Sub ConvierteDipsStandard(iDipsStandard As";
 //BA.debugLineNum = 118;BA.debugLine="Dim iRet As Int";
_iret = 0;
 //BA.debugLineNum = 120;BA.debugLine="If sHeightWidth = \"H\" Then ' si es alto (Height)";
if (_sheightwidth==BA.ObjectToChar("H")) { 
 //BA.debugLineNum = 122;BA.debugLine="iRet = g_DeviceValuesHeight * iDipsStandard / 480";
_iret = (int) (_g_devicevaluesheight*_idipsstandard/(double)480);
 }else {
 //BA.debugLineNum = 124;BA.debugLine="If sHeightWidth = \"W\" Then";
if (_sheightwidth==BA.ObjectToChar("W")) { 
 //BA.debugLineNum = 126;BA.debugLine="iRet = g_DeviceValuesWidth* iDipsStandard / 320";
_iret = (int) (_g_devicevalueswidth*_idipsstandard/(double)320);
 }else {
 //BA.debugLineNum = 129;BA.debugLine="iRet = iDipsStandard";
_iret = _idipsstandard;
 };
 };
 //BA.debugLineNum = 133;BA.debugLine="Return iRet";
if (true) return _iret;
 //BA.debugLineNum = 135;BA.debugLine="End Sub";
return 0;
}
public static int  _vvv2(anywheresoftware.b4a.BA _ba,int _idips,String _sheightwidth) throws Exception{
double _dvalordipheight = 0;
double _dvalordipwidth = 0;
int _iret = 0;
 //BA.debugLineNum = 167;BA.debugLine="Public Sub CuadradoDipsStandard (iDips As Int, sHe";
 //BA.debugLineNum = 169;BA.debugLine="Dim dValorDipHeight As Double = g_DeviceValuesHeig";
_dvalordipheight = _g_devicevaluesheight/(double)_g_devicevalueswidth;
 //BA.debugLineNum = 170;BA.debugLine="Dim dValorDipWidth As Double = g_DeviceValuesWidth";
_dvalordipwidth = _g_devicevalueswidth/(double)_g_devicevaluesheight;
 //BA.debugLineNum = 171;BA.debugLine="Dim iRet As Int";
_iret = 0;
 //BA.debugLineNum = 174;BA.debugLine="If sHeightWidth = \"W\" Then";
if ((_sheightwidth).equals("W")) { 
 //BA.debugLineNum = 175;BA.debugLine="iRet =iDips*dValorDipHeight";
_iret = (int) (_idips*_dvalordipheight);
 }else {
 //BA.debugLineNum = 177;BA.debugLine="iRet = iDips*dValorDipWidth";
_iret = (int) (_idips*_dvalordipwidth);
 };
 //BA.debugLineNum = 181;BA.debugLine="Return iRet";
if (true) return _iret;
 //BA.debugLineNum = 182;BA.debugLine="End Sub";
return 0;
}
public static int  _facebook_getuso(anywheresoftware.b4a.BA _ba,anyhwheresoftware.b4a.objects.sqlcipher.SQLCipher _xbasesql) throws Exception{
anywheresoftware.b4a.sql.SQL.CursorWrapper _cursor1 = null;
String _ssql = "";
int _iret = 0;
 //BA.debugLineNum = 939;BA.debugLine="Sub Facebook_GetUso (xBaseSql As SQLCipher) As Int";
 //BA.debugLineNum = 940;BA.debugLine="Dim Cursor1 As Cursor, ssql As String , iRet As I";
_cursor1 = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
_ssql = "";
_iret = (int) (0);
 //BA.debugLineNum = 941;BA.debugLine="Try";
try { //BA.debugLineNum = 943;BA.debugLine="ssql =\"Select ifnull(publicofacebook,0) from ava";
_ssql = "Select ifnull(publicofacebook,0) from avance where idPalabra = (select max(idpalabra) from avance)";
 //BA.debugLineNum = 944;BA.debugLine="Cursor1 = xBaseSql.ExecQuery(ssql)";
_cursor1.setObject((android.database.Cursor)(_xbasesql.ExecQuery(_ssql)));
 //BA.debugLineNum = 945;BA.debugLine="If Cursor1.RowCount >0 Then";
if (_cursor1.getRowCount()>0) { 
 //BA.debugLineNum = 946;BA.debugLine="Cursor1.Position = 0";
_cursor1.setPosition((int) (0));
 //BA.debugLineNum = 947;BA.debugLine="iRet = Cursor1.GetInt2(0)";
_iret = _cursor1.GetInt2((int) (0));
 };
 //BA.debugLineNum = 949;BA.debugLine="Cursor1.Close";
_cursor1.Close();
 } 
       catch (Exception e11) {
			(_ba.processBA == null ? _ba : _ba.processBA).setLastException(e11); //BA.debugLineNum = 951;BA.debugLine="ManejaError(\"Facebook_GetUso\",\"\")";
_vvv7(_ba,"Facebook_GetUso","");
 };
 //BA.debugLineNum = 953;BA.debugLine="Return iRet";
if (true) return _iret;
 //BA.debugLineNum = 954;BA.debugLine="End Sub";
return 0;
}
public static int  _font_recalculatam(anywheresoftware.b4a.BA _ba,int _ifontsize) throws Exception{
int _iret = 0;
float _devscreehsize = 0f;
anywheresoftware.b4a.keywords.LayoutValues _devicevalues = null;
 //BA.debugLineNum = 786;BA.debugLine="Sub Font_RecalculaTam(iFontSize As Int) As Int";
 //BA.debugLineNum = 790;BA.debugLine="Dim iRet As Int, DevScreehSize As Float";
_iret = 0;
_devscreehsize = 0f;
 //BA.debugLineNum = 791;BA.debugLine="Dim DeviceValues As LayoutValues: DeviceValues =";
_devicevalues = new anywheresoftware.b4a.keywords.LayoutValues();
 //BA.debugLineNum = 791;BA.debugLine="Dim DeviceValues As LayoutValues: DeviceValues =";
_devicevalues = anywheresoftware.b4a.keywords.Common.GetDeviceLayoutValues(_ba);
 //BA.debugLineNum = 792;BA.debugLine="DevScreehSize = DeviceValues.ApproximateScreenSiz";
_devscreehsize = (float) (_devicevalues.getApproximateScreenSize());
 //BA.debugLineNum = 794;BA.debugLine="iRet = iFontSize/4.2* DevScreehSize";
_iret = (int) (_ifontsize/(double)4.2*_devscreehsize);
 //BA.debugLineNum = 795;BA.debugLine="Return iRet";
if (true) return _iret;
 //BA.debugLineNum = 796;BA.debugLine="End Sub";
return 0;
}
public static int  _get_cantidadpalabras(anywheresoftware.b4a.BA _ba,anyhwheresoftware.b4a.objects.sqlcipher.SQLCipher _base) throws Exception{
String _ssql = "";
anywheresoftware.b4a.sql.SQL.CursorWrapper _cursor1 = null;
int _iret = 0;
 //BA.debugLineNum = 722;BA.debugLine="Sub get_CantidadPalabras(Base As SQLCipher) As Int";
 //BA.debugLineNum = 723;BA.debugLine="Dim sSql As String, Cursor1 As Cursor, iRet As Int";
_ssql = "";
_cursor1 = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
_iret = 0;
 //BA.debugLineNum = 724;BA.debugLine="sSql = \"select count(1) x from palabras\"";
_ssql = "select count(1) x from palabras";
 //BA.debugLineNum = 725;BA.debugLine="Cursor1 = Base.ExecQuery(sSql)";
_cursor1.setObject((android.database.Cursor)(_base.ExecQuery(_ssql)));
 //BA.debugLineNum = 726;BA.debugLine="If Cursor1.RowCount>0 Then";
if (_cursor1.getRowCount()>0) { 
 //BA.debugLineNum = 727;BA.debugLine="Cursor1.Position = 0";
_cursor1.setPosition((int) (0));
 //BA.debugLineNum = 728;BA.debugLine="iRet = Cursor1.GetInt2(0)";
_iret = _cursor1.GetInt2((int) (0));
 };
 //BA.debugLineNum = 730;BA.debugLine="Cursor1.Close";
_cursor1.Close();
 //BA.debugLineNum = 731;BA.debugLine="Return iRet";
if (true) return _iret;
 //BA.debugLineNum = 732;BA.debugLine="End Sub";
return 0;
}
public static int  _get_cantidadpalabrasadivinadas(anywheresoftware.b4a.BA _ba,anywheresoftware.b4a.sql.SQL _base) throws Exception{
String _ssql = "";
anywheresoftware.b4a.sql.SQL.CursorWrapper _cursor1 = null;
int _iret = 0;
 //BA.debugLineNum = 752;BA.debugLine="Sub get_CantidadPalabrasAdivinadas(Base As SQL) As";
 //BA.debugLineNum = 753;BA.debugLine="Dim sSql As String, Cursor1 As Cursor, iRet As Int";
_ssql = "";
_cursor1 = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
_iret = 0;
 //BA.debugLineNum = 754;BA.debugLine="sSql = \"select count(1) x from avance where adiv";
_ssql = "select count(1) x from avance where adivinada = 1";
 //BA.debugLineNum = 755;BA.debugLine="Cursor1 = Base.ExecQuery(sSql)";
_cursor1.setObject((android.database.Cursor)(_base.ExecQuery(_ssql)));
 //BA.debugLineNum = 756;BA.debugLine="If Cursor1.RowCount>0 Then";
if (_cursor1.getRowCount()>0) { 
 //BA.debugLineNum = 757;BA.debugLine="Cursor1.Position = 0";
_cursor1.setPosition((int) (0));
 //BA.debugLineNum = 758;BA.debugLine="iRet = Cursor1.GetInt2(0)";
_iret = _cursor1.GetInt2((int) (0));
 };
 //BA.debugLineNum = 760;BA.debugLine="Cursor1.Close";
_cursor1.Close();
 //BA.debugLineNum = 761;BA.debugLine="Return iRet";
if (true) return _iret;
 //BA.debugLineNum = 762;BA.debugLine="End Sub";
return 0;
}
public static int  _get_cantidadpalabrasalmacen(anywheresoftware.b4a.BA _ba,anyhwheresoftware.b4a.objects.sqlcipher.SQLCipher _base,boolean _bpremium) throws Exception{
String _ssql = "";
anywheresoftware.b4a.sql.SQL.CursorWrapper _cursor1 = null;
int _iret = 0;
 //BA.debugLineNum = 734;BA.debugLine="Sub get_CantidadPalabrasAlmacen(Base As SQLCipher,";
 //BA.debugLineNum = 735;BA.debugLine="Dim sSql As String, Cursor1 As Cursor, iRet As Int";
_ssql = "";
_cursor1 = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
_iret = 0;
 //BA.debugLineNum = 736;BA.debugLine="sSql = \"select count(1) x from palabras_almacen\"";
_ssql = "select count(1) x from palabras_almacen";
 //BA.debugLineNum = 738;BA.debugLine="If bPremium = False Then";
if (_bpremium==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 739;BA.debugLine="sSql = sSql & \" where Premium = 'N'\"";
_ssql = _ssql+" where Premium = 'N'";
 };
 //BA.debugLineNum = 742;BA.debugLine="Cursor1 = Base.ExecQuery(sSql)";
_cursor1.setObject((android.database.Cursor)(_base.ExecQuery(_ssql)));
 //BA.debugLineNum = 743;BA.debugLine="If Cursor1.RowCount>0 Then";
if (_cursor1.getRowCount()>0) { 
 //BA.debugLineNum = 744;BA.debugLine="Cursor1.Position = 0";
_cursor1.setPosition((int) (0));
 //BA.debugLineNum = 745;BA.debugLine="iRet = Cursor1.GetInt2(0)";
_iret = _cursor1.GetInt2((int) (0));
 };
 //BA.debugLineNum = 747;BA.debugLine="Cursor1.Close";
_cursor1.Close();
 //BA.debugLineNum = 748;BA.debugLine="Return iRet";
if (true) return _iret;
 //BA.debugLineNum = 749;BA.debugLine="End Sub";
return 0;
}
public static int  _get_cantidadpalabrasjugadas(anywheresoftware.b4a.BA _ba,anywheresoftware.b4a.sql.SQL _base) throws Exception{
String _ssql = "";
anywheresoftware.b4a.sql.SQL.CursorWrapper _cursor1 = null;
int _iret = 0;
 //BA.debugLineNum = 775;BA.debugLine="Sub get_CantidadPalabrasJugadas(Base As SQL) As In";
 //BA.debugLineNum = 776;BA.debugLine="Dim sSql As String, Cursor1 As Cursor, iRet As Int";
_ssql = "";
_cursor1 = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
_iret = 0;
 //BA.debugLineNum = 777;BA.debugLine="sSql = \"select count(1) x from avance where Salt";
_ssql = "select count(1) x from avance where Salteada = 1 or Adivinada = 1";
 //BA.debugLineNum = 778;BA.debugLine="Cursor1 = Base.ExecQuery(sSql)";
_cursor1.setObject((android.database.Cursor)(_base.ExecQuery(_ssql)));
 //BA.debugLineNum = 779;BA.debugLine="If Cursor1.RowCount>0 Then";
if (_cursor1.getRowCount()>0) { 
 //BA.debugLineNum = 780;BA.debugLine="Cursor1.Position = 0";
_cursor1.setPosition((int) (0));
 //BA.debugLineNum = 781;BA.debugLine="iRet = Cursor1.GetInt2(0)";
_iret = _cursor1.GetInt2((int) (0));
 };
 //BA.debugLineNum = 783;BA.debugLine="Cursor1.Close";
_cursor1.Close();
 //BA.debugLineNum = 784;BA.debugLine="Return iRet";
if (true) return _iret;
 //BA.debugLineNum = 785;BA.debugLine="End Sub";
return 0;
}
public static int  _get_cantidadpalabrassalteadas(anywheresoftware.b4a.BA _ba,anywheresoftware.b4a.sql.SQL _base) throws Exception{
String _ssql = "";
anywheresoftware.b4a.sql.SQL.CursorWrapper _cursor1 = null;
int _iret = 0;
 //BA.debugLineNum = 764;BA.debugLine="Sub get_CantidadPalabrasSalteadas(Base As SQL) As";
 //BA.debugLineNum = 765;BA.debugLine="Dim sSql As String, Cursor1 As Cursor, iRet As Int";
_ssql = "";
_cursor1 = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
_iret = 0;
 //BA.debugLineNum = 766;BA.debugLine="sSql = \"select count(1) x from avance where Salt";
_ssql = "select count(1) x from avance where Salteada = 1";
 //BA.debugLineNum = 767;BA.debugLine="Cursor1 = Base.ExecQuery(sSql)";
_cursor1.setObject((android.database.Cursor)(_base.ExecQuery(_ssql)));
 //BA.debugLineNum = 768;BA.debugLine="If Cursor1.RowCount>0 Then";
if (_cursor1.getRowCount()>0) { 
 //BA.debugLineNum = 769;BA.debugLine="Cursor1.Position = 0";
_cursor1.setPosition((int) (0));
 //BA.debugLineNum = 770;BA.debugLine="iRet = Cursor1.GetInt2(0)";
_iret = _cursor1.GetInt2((int) (0));
 };
 //BA.debugLineNum = 772;BA.debugLine="Cursor1.Close";
_cursor1.Close();
 //BA.debugLineNum = 773;BA.debugLine="Return iRet";
if (true) return _iret;
 //BA.debugLineNum = 774;BA.debugLine="End Sub";
return 0;
}
public static String  _get_directoriobase(anywheresoftware.b4a.BA _ba) throws Exception{
String _sret = "";
 //BA.debugLineNum = 437;BA.debugLine="Sub Get_DirectorioBase As String";
 //BA.debugLineNum = 438;BA.debugLine="Dim sRet As String=\"\"";
_sret = "";
 //BA.debugLineNum = 439;BA.debugLine="Try";
try { //BA.debugLineNum = 441;BA.debugLine="If File.ExternalWritable Then sRet  = File.Dir";
if (anywheresoftware.b4a.keywords.Common.File.getExternalWritable()) { 
_sret = anywheresoftware.b4a.keywords.Common.File.getDirDefaultExternal();}
else {
_sret = anywheresoftware.b4a.keywords.Common.File.getDirInternal();};
 } 
       catch (Exception e5) {
			(_ba.processBA == null ? _ba : _ba.processBA).setLastException(e5); //BA.debugLineNum = 445;BA.debugLine="ManejaError(\"Get_DirectorioBase\",\"Error Get_Dire";
_vvv7(_ba,"Get_DirectorioBase","Error Get_DirectorioBase");
 };
 //BA.debugLineNum = 448;BA.debugLine="Return sRet";
if (true) return _sret;
 //BA.debugLineNum = 450;BA.debugLine="End Sub";
return "";
}
public static boolean  _get_espremium(anywheresoftware.b4a.BA _ba,anyhwheresoftware.b4a.objects.sqlcipher.SQLCipher _base) throws Exception{
String _ssql = "";
anywheresoftware.b4a.sql.SQL.CursorWrapper _cursor1 = null;
boolean _bret = false;
 //BA.debugLineNum = 1249;BA.debugLine="Sub Get_EsPremium (Base As SQLCipher) As Boolean";
 //BA.debugLineNum = 1250;BA.debugLine="Dim sSql As String, Cursor1 As Cursor, bRet As Boo";
_ssql = "";
_cursor1 = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
_bret = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 1252;BA.debugLine="Try";
try { //BA.debugLineNum = 1254;BA.debugLine="bRet = Get_Tester";
_bret = _get_tester(_ba);
 //BA.debugLineNum = 1255;BA.debugLine="If bRet = False Then";
if (_bret==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 1257;BA.debugLine="sSql = \"select count(1) from Seteosusuario where";
_ssql = "select count(1) from Seteosusuario where (tiposeteo = 'propaganda' and texto = 'apagada') or tiposeteo = 'premiumremoto'";
 //BA.debugLineNum = 1258;BA.debugLine="Cursor1 = Base.ExecQuery(sSql)";
_cursor1.setObject((android.database.Cursor)(_base.ExecQuery(_ssql)));
 //BA.debugLineNum = 1259;BA.debugLine="Cursor1.Position=0";
_cursor1.setPosition((int) (0));
 //BA.debugLineNum = 1260;BA.debugLine="bRet = (Cursor1.GetInt2(0) > 0 )";
_bret = (_cursor1.GetInt2((int) (0))>0);
 //BA.debugLineNum = 1261;BA.debugLine="Cursor1.close";
_cursor1.Close();
 };
 //BA.debugLineNum = 1264;BA.debugLine="If Get_SoyYo = True Then";
if (_get_soyyo(_ba)==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 1265;BA.debugLine="bRet = False";
_bret = anywheresoftware.b4a.keywords.Common.False;
 };
 } 
       catch (Exception e15) {
			(_ba.processBA == null ? _ba : _ba.processBA).setLastException(e15); //BA.debugLineNum = 1271;BA.debugLine="bRet = True ' si da error, apaga la propaganda";
_bret = anywheresoftware.b4a.keywords.Common.True;
 };
 //BA.debugLineNum = 1273;BA.debugLine="Return bRet";
if (true) return _bret;
 //BA.debugLineNum = 1274;BA.debugLine="End Sub";
return false;
}
public static boolean  _get_espremiumgrabadoremoto(anywheresoftware.b4a.BA _ba,anyhwheresoftware.b4a.objects.sqlcipher.SQLCipher _base) throws Exception{
String _ssql = "";
anywheresoftware.b4a.sql.SQL.CursorWrapper _cursor1 = null;
boolean _bret = false;
 //BA.debugLineNum = 1369;BA.debugLine="Sub Get_EsPremiumGrabadoRemoto (Base As SQLCipher)";
 //BA.debugLineNum = 1370;BA.debugLine="Dim sSql As String, Cursor1 As Cursor, bRet As Boo";
_ssql = "";
_cursor1 = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
_bret = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 1372;BA.debugLine="Try";
try { //BA.debugLineNum = 1374;BA.debugLine="bRet = Get_Tester";
_bret = _get_tester(_ba);
 //BA.debugLineNum = 1375;BA.debugLine="If bRet = False Then";
if (_bret==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 1377;BA.debugLine="sSql = \"select count(1) from Seteosusuario where";
_ssql = "select count(1) from Seteosusuario where tiposeteo = 'premiumremoto'";
 //BA.debugLineNum = 1378;BA.debugLine="Cursor1 = Base.ExecQuery(sSql)";
_cursor1.setObject((android.database.Cursor)(_base.ExecQuery(_ssql)));
 //BA.debugLineNum = 1379;BA.debugLine="Cursor1.Position=0";
_cursor1.setPosition((int) (0));
 //BA.debugLineNum = 1380;BA.debugLine="bRet = (Cursor1.GetInt2(0) > 0 )";
_bret = (_cursor1.GetInt2((int) (0))>0);
 //BA.debugLineNum = 1381;BA.debugLine="Cursor1.close";
_cursor1.Close();
 };
 //BA.debugLineNum = 1383;BA.debugLine="If Get_SoyYo = True Then";
if (_get_soyyo(_ba)==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 1384;BA.debugLine="bRet =False";
_bret = anywheresoftware.b4a.keywords.Common.False;
 };
 } 
       catch (Exception e15) {
			(_ba.processBA == null ? _ba : _ba.processBA).setLastException(e15); //BA.debugLineNum = 1389;BA.debugLine="bRet = True ' si da error, apaga la propaganda";
_bret = anywheresoftware.b4a.keywords.Common.True;
 };
 //BA.debugLineNum = 1391;BA.debugLine="Return bRet";
if (true) return _bret;
 //BA.debugLineNum = 1392;BA.debugLine="End Sub";
return false;
}
public static boolean  _get_esultimapalabra(anywheresoftware.b4a.BA _ba,anywheresoftware.b4a.sql.SQL _baseusuario,anyhwheresoftware.b4a.objects.sqlcipher.SQLCipher _basejuego) throws Exception{
anywheresoftware.b4a.sql.SQL.CursorWrapper _cursor1 = null;
boolean _bret = false;
String _ssql = "";
int _ipalabraavance = 0;
 //BA.debugLineNum = 295;BA.debugLine="Public Sub Get_EsUltimaPalabra (BaseUsuario As SQL";
 //BA.debugLineNum = 296;BA.debugLine="Dim Cursor1 As Cursor, bRet As Boolean = False";
_cursor1 = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
_bret = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 297;BA.debugLine="Dim sSql As String, iPalabraAvance As Int";
_ssql = "";
_ipalabraavance = 0;
 //BA.debugLineNum = 298;BA.debugLine="Try";
try { //BA.debugLineNum = 302;BA.debugLine="sSql = \"select ifnull(max(idpalabra),0) a from av";
_ssql = "select ifnull(max(idpalabra),0) a from avance where Fin is not null";
 //BA.debugLineNum = 303;BA.debugLine="Cursor1 = BaseUsuario.ExecQuery(sSql)";
_cursor1.setObject((android.database.Cursor)(_baseusuario.ExecQuery(_ssql)));
 //BA.debugLineNum = 304;BA.debugLine="If Cursor1.RowCount >0 Then";
if (_cursor1.getRowCount()>0) { 
 //BA.debugLineNum = 305;BA.debugLine="Cursor1.Position=0";
_cursor1.setPosition((int) (0));
 //BA.debugLineNum = 306;BA.debugLine="iPalabraAvance=Cursor1.GetInt2(0)";
_ipalabraavance = _cursor1.GetInt2((int) (0));
 };
 //BA.debugLineNum = 308;BA.debugLine="Cursor1.Close";
_cursor1.Close();
 //BA.debugLineNum = 311;BA.debugLine="sSql =\"Select idpalabra from palabras where idpal";
_ssql = "Select idpalabra from palabras where idpalabra>"+BA.NumberToString(_ipalabraavance);
 //BA.debugLineNum = 312;BA.debugLine="Cursor1 = BaseJuego.ExecQuery(sSql)";
_cursor1.setObject((android.database.Cursor)(_basejuego.ExecQuery(_ssql)));
 //BA.debugLineNum = 313;BA.debugLine="If Cursor1.RowCount >0 Then";
if (_cursor1.getRowCount()>0) { 
 //BA.debugLineNum = 314;BA.debugLine="bRet = False";
_bret = anywheresoftware.b4a.keywords.Common.False;
 }else {
 //BA.debugLineNum = 316;BA.debugLine="bRet = True";
_bret = anywheresoftware.b4a.keywords.Common.True;
 };
 //BA.debugLineNum = 318;BA.debugLine="Cursor1.Close";
_cursor1.Close();
 } 
       catch (Exception e20) {
			(_ba.processBA == null ? _ba : _ba.processBA).setLastException(e20); //BA.debugLineNum = 321;BA.debugLine="ManejaError(\"get_esultimapalabra\",\"Error Puntos\")";
_vvv7(_ba,"get_esultimapalabra","Error Puntos");
 };
 //BA.debugLineNum = 323;BA.debugLine="Return bRet";
if (true) return _bret;
 //BA.debugLineNum = 325;BA.debugLine="End Sub";
return false;
}
public static String  _get_fechainstalacion(anywheresoftware.b4a.BA _ba,anyhwheresoftware.b4a.objects.sqlcipher.SQLCipher _base) throws Exception{
String _sfecha = "";
String _ssql = "";
anywheresoftware.b4a.sql.SQL.CursorWrapper _cursor1 = null;
 //BA.debugLineNum = 275;BA.debugLine="Public Sub get_FechaInstalacion(Base As SQLCipher)";
 //BA.debugLineNum = 276;BA.debugLine="Dim sFecha As String = \"\"";
_sfecha = "";
 //BA.debugLineNum = 277;BA.debugLine="Dim sSql As String, Cursor1 As Cursor";
_ssql = "";
_cursor1 = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
 //BA.debugLineNum = 278;BA.debugLine="Try";
try { //BA.debugLineNum = 279;BA.debugLine="sSql = \"select fechaalta from usuario\"";
_ssql = "select fechaalta from usuario";
 //BA.debugLineNum = 280;BA.debugLine="Cursor1 = Base.ExecQuery(sSql)";
_cursor1.setObject((android.database.Cursor)(_base.ExecQuery(_ssql)));
 //BA.debugLineNum = 282;BA.debugLine="If Cursor1.RowCount>0 Then";
if (_cursor1.getRowCount()>0) { 
 //BA.debugLineNum = 283;BA.debugLine="Cursor1.Position = 0";
_cursor1.setPosition((int) (0));
 //BA.debugLineNum = 284;BA.debugLine="sFecha =Cursor1.GetString2(0)";
_sfecha = _cursor1.GetString2((int) (0));
 };
 } 
       catch (Exception e11) {
			(_ba.processBA == null ? _ba : _ba.processBA).setLastException(e11); //BA.debugLineNum = 287;BA.debugLine="ManejaError(\"UsuarioExiste\",\"Error Usuario\")";
_vvv7(_ba,"UsuarioExiste","Error Usuario");
 };
 //BA.debugLineNum = 290;BA.debugLine="Return sFecha";
if (true) return _sfecha;
 //BA.debugLineNum = 291;BA.debugLine="End Sub";
return "";
}
public static boolean  _get_haypalabrasanteriores(anywheresoftware.b4a.BA _ba,anyhwheresoftware.b4a.objects.sqlcipher.SQLCipher _basej,int _iidpalabra,String _sand) throws Exception{
int _ireg = 0;
String _ssql = "";
anywheresoftware.b4a.sql.SQL.CursorWrapper _cursor1 = null;
boolean _bret = false;
 //BA.debugLineNum = 1395;BA.debugLine="Sub Get_HayPalabrasAnteriores(BaseJ As SQLCipher,";
 //BA.debugLineNum = 1396;BA.debugLine="Dim iReg As Int";
_ireg = 0;
 //BA.debugLineNum = 1398;BA.debugLine="Dim sSql As String, Cursor1 As Cursor, bRet As Bo";
_ssql = "";
_cursor1 = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
_bret = false;
 //BA.debugLineNum = 1399;BA.debugLine="Try";
try { //BA.debugLineNum = 1400;BA.debugLine="sSql = \"Select Count(1) from avance where idpala";
_ssql = "Select Count(1) from avance where idpalabra<"+BA.NumberToString(_iidpalabra)+_sand;
 //BA.debugLineNum = 1401;BA.debugLine="Cursor1 = BaseJ.ExecQuery(sSql)";
_cursor1.setObject((android.database.Cursor)(_basej.ExecQuery(_ssql)));
 //BA.debugLineNum = 1402;BA.debugLine="If Cursor1.RowCount>0 Then";
if (_cursor1.getRowCount()>0) { 
 //BA.debugLineNum = 1403;BA.debugLine="Cursor1.Position = 0";
_cursor1.setPosition((int) (0));
 //BA.debugLineNum = 1404;BA.debugLine="bRet= (Cursor1.getint2(0)>0)";
_bret = (_cursor1.GetInt2((int) (0))>0);
 };
 //BA.debugLineNum = 1407;BA.debugLine="Cursor1.Close";
_cursor1.Close();
 } 
       catch (Exception e12) {
			(_ba.processBA == null ? _ba : _ba.processBA).setLastException(e12); //BA.debugLineNum = 1409;BA.debugLine="Log(\"Get_HayPalabrasAnteriores\")";
anywheresoftware.b4a.keywords.Common.Log("Get_HayPalabrasAnteriores");
 };
 //BA.debugLineNum = 1412;BA.debugLine="Return bRet";
if (true) return _bret;
 //BA.debugLineNum = 1413;BA.debugLine="End Sub";
return false;
}
public static boolean  _get_haypalabrasposteriores(anywheresoftware.b4a.BA _ba,anyhwheresoftware.b4a.objects.sqlcipher.SQLCipher _basej,int _iidpalabra,String _sand) throws Exception{
int _ireg = 0;
String _ssql = "";
anywheresoftware.b4a.sql.SQL.CursorWrapper _cursor1 = null;
boolean _bret = false;
 //BA.debugLineNum = 1415;BA.debugLine="Sub Get_HayPalabrasPosteriores(BaseJ As SQLCipher,";
 //BA.debugLineNum = 1416;BA.debugLine="Dim iReg As Int";
_ireg = 0;
 //BA.debugLineNum = 1418;BA.debugLine="Dim sSql As String, Cursor1 As Cursor, bRet As Bo";
_ssql = "";
_cursor1 = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
_bret = false;
 //BA.debugLineNum = 1419;BA.debugLine="Try";
try { //BA.debugLineNum = 1420;BA.debugLine="sSql = \"Select Count(1) from avance where idpala";
_ssql = "Select Count(1) from avance where idpalabra>"+BA.NumberToString(_iidpalabra)+_sand;
 //BA.debugLineNum = 1421;BA.debugLine="Cursor1 = BaseJ.ExecQuery(sSql)";
_cursor1.setObject((android.database.Cursor)(_basej.ExecQuery(_ssql)));
 //BA.debugLineNum = 1422;BA.debugLine="If Cursor1.RowCount>0 Then";
if (_cursor1.getRowCount()>0) { 
 //BA.debugLineNum = 1423;BA.debugLine="Cursor1.Position = 0";
_cursor1.setPosition((int) (0));
 //BA.debugLineNum = 1424;BA.debugLine="bRet= (Cursor1.getint2(0)>0)";
_bret = (_cursor1.GetInt2((int) (0))>0);
 };
 //BA.debugLineNum = 1427;BA.debugLine="Cursor1.Close";
_cursor1.Close();
 } 
       catch (Exception e12) {
			(_ba.processBA == null ? _ba : _ba.processBA).setLastException(e12); //BA.debugLineNum = 1429;BA.debugLine="Log(\"Get_HayPalabrasPosteriores\")";
anywheresoftware.b4a.keywords.Common.Log("Get_HayPalabrasPosteriores");
 };
 //BA.debugLineNum = 1432;BA.debugLine="Return bRet";
if (true) return _bret;
 //BA.debugLineNum = 1433;BA.debugLine="End Sub";
return false;
}
public static String  _get_letrasparaprovider(anywheresoftware.b4a.BA _ba,String _sletras) throws Exception{
String _sret = "";
String _suna = "";
int _j = 0;
 //BA.debugLineNum = 970;BA.debugLine="Sub Get_LetrasparaProvider (sLetras As String) As";
 //BA.debugLineNum = 971;BA.debugLine="Dim sRet As String = \" \", sUna As String";
_sret = " ";
_suna = "";
 //BA.debugLineNum = 972;BA.debugLine="For j = 0 To sLetras.Length-1";
{
final int step2 = 1;
final int limit2 = (int) (_sletras.length()-1);
for (_j = (int) (0) ; (step2 > 0 && _j <= limit2) || (step2 < 0 && _j >= limit2); _j = ((int)(0 + _j + step2)) ) {
 //BA.debugLineNum = 973;BA.debugLine="sUna = sLetras.SubString2(j,j+1)";
_suna = _sletras.substring(_j,(int) (_j+1));
 //BA.debugLineNum = 974;BA.debugLine="If sUna= \"_\"  Then";
if ((_suna).equals("_")) { 
 //BA.debugLineNum = 975;BA.debugLine="sRet = sRet & \"__ \"";
_sret = _sret+"__ ";
 }else {
 //BA.debugLineNum = 977;BA.debugLine="sRet  = sRet & sUna & \" \"";
_sret = _sret+_suna+" ";
 };
 }
};
 //BA.debugLineNum = 980;BA.debugLine="Return sRet";
if (true) return _sret;
 //BA.debugLineNum = 981;BA.debugLine="End Sub";
return "";
}
public static int  _get_palabraenjuego(anywheresoftware.b4a.BA _ba,anyhwheresoftware.b4a.objects.sqlcipher.SQLCipher _base) throws Exception{
String _ssql = "";
anywheresoftware.b4a.sql.SQL.CursorWrapper _cursor1 = null;
int _iret = 0;
 //BA.debugLineNum = 1277;BA.debugLine="Sub Get_PalabraEnJuego (Base As SQLCipher) As Int";
 //BA.debugLineNum = 1279;BA.debugLine="Dim sSql As String, Cursor1 As Cursor, iRet As In";
_ssql = "";
_cursor1 = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
_iret = (int) (0);
 //BA.debugLineNum = 1280;BA.debugLine="Try";
try { //BA.debugLineNum = 1281;BA.debugLine="sSql = \"Select Max(idPalabra) from avance\"";
_ssql = "Select Max(idPalabra) from avance";
 //BA.debugLineNum = 1282;BA.debugLine="Cursor1 = Base.ExecQuery(sSql)";
_cursor1.setObject((android.database.Cursor)(_base.ExecQuery(_ssql)));
 //BA.debugLineNum = 1283;BA.debugLine="If Cursor1.RowCount>0 Then";
if (_cursor1.getRowCount()>0) { 
 //BA.debugLineNum = 1284;BA.debugLine="Cursor1.Position = 0";
_cursor1.setPosition((int) (0));
 //BA.debugLineNum = 1285;BA.debugLine="iRet = Cursor1.GetInt2(0)";
_iret = _cursor1.GetInt2((int) (0));
 };
 //BA.debugLineNum = 1287;BA.debugLine="Cursor1.Close";
_cursor1.Close();
 } 
       catch (Exception e11) {
			(_ba.processBA == null ? _ba : _ba.processBA).setLastException(e11); //BA.debugLineNum = 1289;BA.debugLine="Log(\"Catch GetPalabraEnJuego\")";
anywheresoftware.b4a.keywords.Common.Log("Catch GetPalabraEnJuego");
 };
 //BA.debugLineNum = 1292;BA.debugLine="Return iRet";
if (true) return _iret;
 //BA.debugLineNum = 1293;BA.debugLine="End Sub";
return 0;
}
public static String  _get_premiumcantpalabras(anywheresoftware.b4a.BA _ba,anyhwheresoftware.b4a.objects.sqlcipher.SQLCipher _basej) throws Exception{
String _sret = "";
String _ssql = "";
anywheresoftware.b4a.sql.SQL.CursorWrapper _cursor1 = null;
int _iret = 0;
 //BA.debugLineNum = 1347;BA.debugLine="Sub Get_PremiumCantPalabras(BaseJ As SQLCipher) As";
 //BA.debugLineNum = 1348;BA.debugLine="Dim sRet As String";
_sret = "";
 //BA.debugLineNum = 1350;BA.debugLine="Dim sSql As String, Cursor1 As Cursor, iRet As In";
_ssql = "";
_cursor1 = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
_iret = (int) (0);
 //BA.debugLineNum = 1351;BA.debugLine="Try";
try { //BA.debugLineNum = 1352;BA.debugLine="sSql = \"Select Count(1) from palabras_almacen wh";
_ssql = "Select Count(1) from palabras_almacen where premium <> 'n' and premium <>'N'";
 //BA.debugLineNum = 1353;BA.debugLine="Cursor1 = BaseJ.ExecQuery(sSql)";
_cursor1.setObject((android.database.Cursor)(_basej.ExecQuery(_ssql)));
 //BA.debugLineNum = 1354;BA.debugLine="If Cursor1.RowCount>0 Then";
if (_cursor1.getRowCount()>0) { 
 //BA.debugLineNum = 1355;BA.debugLine="Cursor1.Position = 0";
_cursor1.setPosition((int) (0));
 //BA.debugLineNum = 1356;BA.debugLine="sRet = Cursor1.GetString2(0)";
_sret = _cursor1.GetString2((int) (0));
 };
 //BA.debugLineNum = 1358;BA.debugLine="Cursor1.Close";
_cursor1.Close();
 } 
       catch (Exception e12) {
			(_ba.processBA == null ? _ba : _ba.processBA).setLastException(e12); //BA.debugLineNum = 1360;BA.debugLine="Log(\"Catch Get_PremiumCantPalabras\")";
anywheresoftware.b4a.keywords.Common.Log("Catch Get_PremiumCantPalabras");
 };
 //BA.debugLineNum = 1363;BA.debugLine="Return sRet";
if (true) return _sret;
 //BA.debugLineNum = 1364;BA.debugLine="End Sub";
return "";
}
public static int  _get_seteousuarioenterodesde(anywheresoftware.b4a.BA _ba,anyhwheresoftware.b4a.objects.sqlcipher.SQLCipher _baseusuario,String _sseteo) throws Exception{
int _iret = 0;
anywheresoftware.b4a.sql.SQL.CursorWrapper _crcursor = null;
String _ssql = "";
 //BA.debugLineNum = 890;BA.debugLine="Sub Get_SeteoUsuarioEnteroDesde(BaseUsuario As SQL";
 //BA.debugLineNum = 892;BA.debugLine="Dim iRet As Int=0, crCursor As Cursor, sSql As St";
_iret = (int) (0);
_crcursor = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
_ssql = "";
 //BA.debugLineNum = 893;BA.debugLine="Try";
try { //BA.debugLineNum = 894;BA.debugLine="sSql = \"Select ifnull(enterodesde,0) c from Seteo";
_ssql = "Select ifnull(enterodesde,0) c from SeteosUsuario where tiposeteo = '"+_sseteo+"'";
 //BA.debugLineNum = 895;BA.debugLine="crCursor = BaseUsuario.ExecQuery(sSql)";
_crcursor.setObject((android.database.Cursor)(_baseusuario.ExecQuery(_ssql)));
 //BA.debugLineNum = 896;BA.debugLine="If crCursor.RowCount >0 Then";
if (_crcursor.getRowCount()>0) { 
 //BA.debugLineNum = 897;BA.debugLine="crCursor.Position = 0";
_crcursor.setPosition((int) (0));
 //BA.debugLineNum = 898;BA.debugLine="iRet  = crCursor.GetInt(\"c\")";
_iret = _crcursor.GetInt("c");
 }else {
 //BA.debugLineNum = 900;BA.debugLine="SeteoUsuarioEnteroDesde_Inserta(BaseUsuario, sSe";
_seteousuarioenterodesde_inserta(_ba,_baseusuario,_sseteo,(int) (0));
 };
 //BA.debugLineNum = 902;BA.debugLine="crCursor.close";
_crcursor.Close();
 } 
       catch (Exception e13) {
			(_ba.processBA == null ? _ba : _ba.processBA).setLastException(e13); //BA.debugLineNum = 904;BA.debugLine="ManejaError(\"Caranchos\", \"Y ahora qué pasó?\")";
_vvv7(_ba,"Caranchos","Y ahora qué pasó?");
 };
 //BA.debugLineNum = 908;BA.debugLine="Return iRet";
if (true) return _iret;
 //BA.debugLineNum = 909;BA.debugLine="End Sub";
return 0;
}
public static boolean  _get_seteousuarioexiste(anywheresoftware.b4a.BA _ba,anyhwheresoftware.b4a.objects.sqlcipher.SQLCipher _baseusuario,String _sseteo) throws Exception{
boolean _bret = false;
String _ssql = "";
anywheresoftware.b4a.sql.SQL.CursorWrapper _crcursor = null;
 //BA.debugLineNum = 837;BA.debugLine="Sub Get_SeteoUsuarioExiste(BaseUsuario As SQLCiphe";
 //BA.debugLineNum = 838;BA.debugLine="Dim bRet As Boolean, sSql As String, crCursor As";
_bret = false;
_ssql = "";
_crcursor = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
 //BA.debugLineNum = 839;BA.debugLine="Try";
try { //BA.debugLineNum = 840;BA.debugLine="sSql = \"Select count(1) c From SeteosUsuario wher";
_ssql = "Select count(1) c From SeteosUsuario where tiposeteo='"+_sseteo+"'";
 //BA.debugLineNum = 842;BA.debugLine="crCursor = BaseUsuario.ExecQuery(sSql)";
_crcursor.setObject((android.database.Cursor)(_baseusuario.ExecQuery(_ssql)));
 //BA.debugLineNum = 843;BA.debugLine="bRet = crCursor.RowCount >0";
_bret = _crcursor.getRowCount()>0;
 //BA.debugLineNum = 844;BA.debugLine="crCursor.close";
_crcursor.Close();
 } 
       catch (Exception e8) {
			(_ba.processBA == null ? _ba : _ba.processBA).setLastException(e8); //BA.debugLineNum = 847;BA.debugLine="ManejaError(\"Barbacoas\", \"apagadas\")";
_vvv7(_ba,"Barbacoas","apagadas");
 };
 //BA.debugLineNum = 850;BA.debugLine="Return bRet";
if (true) return _bret;
 //BA.debugLineNum = 851;BA.debugLine="End Sub";
return false;
}
public static boolean  _get_soyyo(anywheresoftware.b4a.BA _ba) throws Exception{
boolean _bret = false;
String _susu = "";
 //BA.debugLineNum = 828;BA.debugLine="Sub Get_SoyYo As Boolean";
 //BA.debugLineNum = 829;BA.debugLine="Dim bRet As Boolean, sUsu As String";
_bret = false;
_susu = "";
 //BA.debugLineNum = 830;BA.debugLine="sUsu = GetUsuario";
_susu = _vvv4(_ba);
 //BA.debugLineNum = 831;BA.debugLine="sUsu = sUsu.ToUpperCase";
_susu = _susu.toUpperCase();
 //BA.debugLineNum = 832;BA.debugLine="bRet = (sUsu = \"JUAN.DAROCHA@GMAIL.COM\")";
_bret = ((_susu).equals("JUAN.DAROCHA@GMAIL.COM"));
 //BA.debugLineNum = 833;BA.debugLine="Return bRet";
if (true) return _bret;
 //BA.debugLineNum = 834;BA.debugLine="End Sub";
return false;
}
public static boolean  _get_tester(anywheresoftware.b4a.BA _ba) throws Exception{
boolean _bret = false;
String _susu = "";
 //BA.debugLineNum = 798;BA.debugLine="Sub Get_Tester As Boolean";
 //BA.debugLineNum = 799;BA.debugLine="Dim bRet As Boolean, sUsu As String";
_bret = false;
_susu = "";
 //BA.debugLineNum = 800;BA.debugLine="sUsu = GetUsuario";
_susu = _vvv4(_ba);
 //BA.debugLineNum = 801;BA.debugLine="sUsu = sUsu.ToUpperCase";
_susu = _susu.toUpperCase();
 //BA.debugLineNum = 804;BA.debugLine="bRet = sUsu =\"ALEJONAVEIRAC@GMAIL.COM\"";
_bret = (_susu).equals("ALEJONAVEIRAC@GMAIL.COM");
 //BA.debugLineNum = 805;BA.debugLine="bRet = bRet Or sUsu =\"CECIPIATTINI@GMAIL.COM\"";
_bret = _bret || (_susu).equals("CECIPIATTINI@GMAIL.COM");
 //BA.debugLineNum = 806;BA.debugLine="bRet = bRet Or sUsu =\"CAROVAG@GMAIL.COM\"";
_bret = _bret || (_susu).equals("CAROVAG@GMAIL.COM");
 //BA.debugLineNum = 807;BA.debugLine="bRet = bRet Or sUsu =\"ERICAYEBRA@GMAIL.COM\"";
_bret = _bret || (_susu).equals("ERICAYEBRA@GMAIL.COM");
 //BA.debugLineNum = 808;BA.debugLine="bRet = bRet Or sUsu =\"HDVENTURINI@GMAIL.COM\"";
_bret = _bret || (_susu).equals("HDVENTURINI@GMAIL.COM");
 //BA.debugLineNum = 809;BA.debugLine="bRet = bRet Or sUsu =\"JUANMAROTTE@GMAIL.COM\"";
_bret = _bret || (_susu).equals("JUANMAROTTE@GMAIL.COM");
 //BA.debugLineNum = 810;BA.debugLine="bRet = bRet Or sUsu =\"MARCELO.MARINUCCI@GMAIL.COM";
_bret = _bret || (_susu).equals("MARCELO.MARINUCCI@GMAIL.COM");
 //BA.debugLineNum = 811;BA.debugLine="bRet = bRet Or sUsu =\"MARTIN2603@GMAIL.COM\"";
_bret = _bret || (_susu).equals("MARTIN2603@GMAIL.COM");
 //BA.debugLineNum = 812;BA.debugLine="bRet = bRet Or sUsu =\"TELLECHEA.VICTORIA@GMAIL.CO";
_bret = _bret || (_susu).equals("TELLECHEA.VICTORIA@GMAIL.COM");
 //BA.debugLineNum = 813;BA.debugLine="bRet = bRet Or sUsu = \"ANGIEVMC0426@GMAIL.COM\" 'L";
_bret = _bret || (_susu).equals("ANGIEVMC0426@GMAIL.COM");
 //BA.debugLineNum = 814;BA.debugLine="bRet = bRet Or sUsu = \"LOREHV82@GMAIL.COM\" 'adivi";
_bret = _bret || (_susu).equals("LOREHV82@GMAIL.COM");
 //BA.debugLineNum = 815;BA.debugLine="bRet = bRet Or sUsu = \"G.MATHOV@GMAIL.COM\" 'se le";
_bret = _bret || (_susu).equals("G.MATHOV@GMAIL.COM");
 //BA.debugLineNum = 816;BA.debugLine="bRet = bRet Or sUsu = \"PPRADA2010@GMAIL.COM\" 'se";
_bret = _bret || (_susu).equals("PPRADA2010@GMAIL.COM");
 //BA.debugLineNum = 817;BA.debugLine="bRet = bRet Or sUsu = \"CINTIALENCINA88@GMAIL.COM\"";
_bret = _bret || (_susu).equals("CINTIALENCINA88@GMAIL.COM");
 //BA.debugLineNum = 818;BA.debugLine="bRet = bRet Or sUsu = \"GRACHIEL63@GMAIL.COM\" 'gan";
_bret = _bret || (_susu).equals("GRACHIEL63@GMAIL.COM");
 //BA.debugLineNum = 819;BA.debugLine="bRet = bRet Or sUsu = \"HOUSE.BREITEN@GMAIL.COM\" '";
_bret = _bret || (_susu).equals("HOUSE.BREITEN@GMAIL.COM");
 //BA.debugLineNum = 820;BA.debugLine="bRet = bRet Or sUsu = \"JOGHIBAUDO@GMAIL.COM\" 'man";
_bret = _bret || (_susu).equals("JOGHIBAUDO@GMAIL.COM");
 //BA.debugLineNum = 821;BA.debugLine="bRet = bRet Or sUsu = \"TUCUMETAL@GMAIL.COM\" 'segu";
_bret = _bret || (_susu).equals("TUCUMETAL@GMAIL.COM");
 //BA.debugLineNum = 825;BA.debugLine="Return bRet";
if (true) return _bret;
 //BA.debugLineNum = 826;BA.debugLine="End Sub";
return false;
}
public static String  _get_textoayuda(anywheresoftware.b4a.BA _ba) throws Exception{
String _sret = "";
 //BA.debugLineNum = 1106;BA.debugLine="Sub Get_TextoAyuda As String";
 //BA.debugLineNum = 1107;BA.debugLine="Dim sRet As String";
_sret = "";
 //BA.debugLineNum = 1108;BA.debugLine="sRet = \"Bienvenido a\"& Chr(10) & _  		\"Matete Dive";
_sret = "Bienvenido a"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Matete Divergente"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"un juego de palabras"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"para el lado colorido de tu cerebro"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+""+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Matete te propone una definición y tú debes adivinar de que palabra se trata."+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Aquí es donde te preguntas:"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+" ¿Eso es todo?"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"¡NO!"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"En Matete Divergente las palabras se definen de una forma especial, y es por eso que para ganar necesitarás utilizar tu pensamiento lateral."+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Lo primero que debes hacer es olvidar la lógica convencional y mudarte al otro lado de tu cerebro."+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Veamos un ejemplo."+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Definición: "+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"“Coloca sobre sí mismo una montura de caballo”"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Tómate unos segundos para pensar la definición de Matete."+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Colocar una montura…..Ensillar."+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Sobre sí mismo..."+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"En el camino hasta la respuesta es posible que pases por distintos pensamientos:"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Ensillarse, "+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+" meensillo, "+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"   ensillar mismo,"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"     mismo ensilla…"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Alguno de estos pensamientos te llevará a la respuesta, que en este caso es "+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"SENCILLA"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Formada por “SE” + “ENSILLA”"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Cómo puedes ver en este ejemplo, la respuesta respeta las reglas ortográficas, sin embargo las palabras que la conforman pueden no contener exactamente las mismas letras, aunque su Sonido será similar, como sucede en este caso. "+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Veamos otro ejemplo."+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Definición: "+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"“Celebración del cacahuate”"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"  Celebración del maní,"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"    Conmemoración del maní,"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"      Fiesta del maní..."+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"¡MANIFIESTA!"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Ya te habrás dado cuenta que para descubrir las palabras debes dejar fluir tus pensamientos y las asociaciones te irán guiando hacia la respuesta"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Algunas veces puede ser la combinación del sonido de dos palabras y en otras seguir una lógica menos convencional, como en este caso:"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Definición:"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"“Baile de la vaca para cambiar de casa”"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Respuesta"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"¡MUDANZA!"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Luego de esta breve introducción, ya te encuentras listo para jugar Matete Divergente."+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Cada vez que descubras una palabra ganarás neuronas, que podrás utilizar para comprar letras o saltar matetes."+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Puedes hacernos llegar tus comentarios y sugerencias a MateteJuego@gmail.com"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"¡Que te diviertas!";
 //BA.debugLineNum = 1155;BA.debugLine="Return sRet";
if (true) return _sret;
 //BA.debugLineNum = 1156;BA.debugLine="End Sub";
return "";
}
public static String  _get_textocreditos(anywheresoftware.b4a.BA _ba) throws Exception{
String _sret = "";
 //BA.debugLineNum = 1158;BA.debugLine="Sub Get_TextoCreditos As String";
 //BA.debugLineNum = 1159;BA.debugLine="Dim sRet As String";
_sret = "";
 //BA.debugLineNum = 1161;BA.debugLine="sRet =	\"AGRADECIMIENTOS ESPECIALES\" & Chr(10) & _";
_sret = "AGRADECIMIENTOS ESPECIALES"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Juli, Ale, Vero"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Nico y Eri"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Adrian S"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+""+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"TESTERS 7X24"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Caro A, Fer B "+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Martin R, Ceci P"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Marcelo M"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Marcelo M"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Vicky T"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+""+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"TESTERS"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Alejo N"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Juan M"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Hugo V"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Dario C"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Arvid"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+""+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"DESARROLLO"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Juan Pablo da Rocha"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"DISEÑO"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Ver 1 Gabriel Mugni"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Ver2 Jose Augusto da Rocha"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"SOPORTE DESARROLLO"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"B4A Community"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"";
 //BA.debugLineNum = 1188;BA.debugLine="Return sRet";
if (true) return _sret;
 //BA.debugLineNum = 1189;BA.debugLine="End Sub";
return "";
}
public static boolean  _get_usuarioantiguo(anywheresoftware.b4a.BA _ba,anyhwheresoftware.b4a.objects.sqlcipher.SQLCipher _baseu) throws Exception{
String _sfecha = "";
boolean _bret = false;
String _sf = "";
 //BA.debugLineNum = 1332;BA.debugLine="Sub Get_UsuarioAntiguo (BaseU As SQLCipher) As Boo";
 //BA.debugLineNum = 1333;BA.debugLine="Dim sFecha As String, bRet As Boolean = False, sF";
_sfecha = "";
_bret = anywheresoftware.b4a.keywords.Common.False;
_sf = "";
 //BA.debugLineNum = 1335;BA.debugLine="sFecha = get_FechaInstalacion(BaseU)";
_sfecha = _get_fechainstalacion(_ba,_baseu);
 //BA.debugLineNum = 1336;BA.debugLine="sFecha = sFecha.SubString2(0,4) & sFecha.SubStrin";
_sfecha = _sfecha.substring((int) (0),(int) (4))+_sfecha.substring((int) (5),(int) (7));
 //BA.debugLineNum = 1338;BA.debugLine="If sFecha <\"201505\" Then";
if ((double)(Double.parseDouble(_sfecha))<(double)(Double.parseDouble("201505"))) { 
 //BA.debugLineNum = 1339;BA.debugLine="bRet = True";
_bret = anywheresoftware.b4a.keywords.Common.True;
 };
 //BA.debugLineNum = 1342;BA.debugLine="Return bRet";
if (true) return _bret;
 //BA.debugLineNum = 1343;BA.debugLine="End Sub";
return false;
}
public static String  _vvv3(anywheresoftware.b4a.BA _ba) throws Exception{
anywheresoftware.b4a.phone.Phone.PhoneId _pi = null;
String _sret = "";
 //BA.debugLineNum = 214;BA.debugLine="Public Sub getIMEI  As String";
 //BA.debugLineNum = 216;BA.debugLine="Dim pi As PhoneId, sRet As String";
_pi = new anywheresoftware.b4a.phone.Phone.PhoneId();
_sret = "";
 //BA.debugLineNum = 217;BA.debugLine="Try";
try { //BA.debugLineNum = 219;BA.debugLine="Main.g_sIMEI = pi.GetDeviceId";
mostCurrent._vvvvvvvvvvvvvvv6._g_simei = _pi.GetDeviceId();
 //BA.debugLineNum = 220;BA.debugLine="sRet = Main.g_sIMEI";
_sret = mostCurrent._vvvvvvvvvvvvvvv6._g_simei;
 } 
       catch (Exception e6) {
			(_ba.processBA == null ? _ba : _ba.processBA).setLastException(e6); //BA.debugLineNum = 222;BA.debugLine="ManejaError(\"getIMEI\",LastException.Message)";
_vvv7(_ba,"getIMEI",anywheresoftware.b4a.keywords.Common.LastException(_ba).getMessage());
 };
 //BA.debugLineNum = 225;BA.debugLine="Return sRet";
if (true) return _sret;
 //BA.debugLineNum = 226;BA.debugLine="End Sub";
return "";
}
public static String  _vvv4(anywheresoftware.b4a.BA _ba) throws Exception{
anywheresoftware.b4a.agraham.reflection.Reflection _r = null;
String _sret = "";
Object[] _accounts = null;
String _smail = "";
 //BA.debugLineNum = 190;BA.debugLine="Public Sub GetUsuario As String";
 //BA.debugLineNum = 191;BA.debugLine="Dim R As Reflector, sRet As String";
_r = new anywheresoftware.b4a.agraham.reflection.Reflection();
_sret = "";
 //BA.debugLineNum = 193;BA.debugLine="Try";
try { //BA.debugLineNum = 194;BA.debugLine="R.Target = R.RunStaticMethod(\"android.accounts.";
_r.Target = _r.RunStaticMethod("android.accounts.AccountManager","get",new Object[]{(Object)(_r.GetContext((_ba.processBA == null ? _ba : _ba.processBA)))},new String[]{"android.content.Context"});
 //BA.debugLineNum = 196;BA.debugLine="Dim accounts() As Object, sMail As String";
_accounts = new Object[(int) (0)];
{
int d0 = _accounts.length;
for (int i0 = 0;i0 < d0;i0++) {
_accounts[i0] = new Object();
}
}
;
_smail = "";
 //BA.debugLineNum = 197;BA.debugLine="accounts = R.RunMethod2(\"getAccountsByType\",\"co";
_accounts = (Object[])(_r.RunMethod2("getAccountsByType","com.google","java.lang.String"));
 //BA.debugLineNum = 199;BA.debugLine="If accounts.Length >0 Then";
if (_accounts.length>0) { 
 //BA.debugLineNum = 200;BA.debugLine="R.Target = accounts(0)";
_r.Target = _accounts[(int) (0)];
 //BA.debugLineNum = 201;BA.debugLine="Main.g_sEMail=R.GetField(\"name\")";
mostCurrent._vvvvvvvvvvvvvvv6._g_semail = BA.ObjectToString(_r.GetField("name"));
 //BA.debugLineNum = 202;BA.debugLine="Main.g_sEMail = Main.g_sEMail.ToUpperCase";
mostCurrent._vvvvvvvvvvvvvvv6._g_semail = mostCurrent._vvvvvvvvvvvvvvv6._g_semail.toUpperCase();
 };
 } 
       catch (Exception e12) {
			(_ba.processBA == null ? _ba : _ba.processBA).setLastException(e12); //BA.debugLineNum = 206;BA.debugLine="ManejaError(\"GetUsuario\",LastException.Message)";
_vvv7(_ba,"GetUsuario",anywheresoftware.b4a.keywords.Common.LastException(_ba).getMessage());
 };
 //BA.debugLineNum = 211;BA.debugLine="Return Main.g_sEMail";
if (true) return mostCurrent._vvvvvvvvvvvvvvv6._g_semail;
 //BA.debugLineNum = 212;BA.debugLine="End Sub";
return "";
}
public static Object  _vvv5(anywheresoftware.b4a.BA _ba,String _name) throws Exception{
anywheresoftware.b4a.agraham.reflection.Reflection _r = null;
int _id_drawable = 0;
 //BA.debugLineNum = 1092;BA.debugLine="Public Sub LoadDrawable(Name As String) As Object";
 //BA.debugLineNum = 1094;BA.debugLine="Dim R As Reflector";
_r = new anywheresoftware.b4a.agraham.reflection.Reflection();
 //BA.debugLineNum = 1095;BA.debugLine="R.Target = R.GetContext";
_r.Target = (Object)(_r.GetContext((_ba.processBA == null ? _ba : _ba.processBA)));
 //BA.debugLineNum = 1096;BA.debugLine="R.Target = R.RunMethod(\"getResources\")";
_r.Target = _r.RunMethod("getResources");
 //BA.debugLineNum = 1097;BA.debugLine="R.Target = R.RunMethod(\"getSystem\")";
_r.Target = _r.RunMethod("getSystem");
 //BA.debugLineNum = 1098;BA.debugLine="Dim ID_Drawable As Int";
_id_drawable = 0;
 //BA.debugLineNum = 1099;BA.debugLine="ID_Drawable = R.RunMethod4(\"getIdentifier\", Array";
_id_drawable = (int)(BA.ObjectToNumber(_r.RunMethod4("getIdentifier",new Object[]{(Object)(_name),(Object)("drawable"),(Object)("android")},new String[]{"java.lang.String","java.lang.String","java.lang.String"})));
 //BA.debugLineNum = 1101;BA.debugLine="R.Target = R.GetContext";
_r.Target = (Object)(_r.GetContext((_ba.processBA == null ? _ba : _ba.processBA)));
 //BA.debugLineNum = 1102;BA.debugLine="R.Target = R.RunMethod(\"getResources\")";
_r.Target = _r.RunMethod("getResources");
 //BA.debugLineNum = 1103;BA.debugLine="Return R.RunMethod2(\"getDrawable\", ID_Drawable, \"";
if (true) return _r.RunMethod2("getDrawable",BA.NumberToString(_id_drawable),"java.lang.int");
 //BA.debugLineNum = 1104;BA.debugLine="End Sub";
return null;
}
public static String  _vvv6(anywheresoftware.b4a.BA _ba,int _icant) throws Exception{
String _smail = "";
 //BA.debugLineNum = 393;BA.debugLine="Public Sub Magia(iCant As Int)";
 //BA.debugLineNum = 394;BA.debugLine="Dim sMail As String";
_smail = "";
 //BA.debugLineNum = 395;BA.debugLine="sMail = GetUsuario";
_smail = _vvv4(_ba);
 //BA.debugLineNum = 397;BA.debugLine="If sMail.ToUpperCase  =\"JUAN.DAROCHA@GMAIL.COM\" OR";
if ((_smail.toUpperCase()).equals("JUAN.DAROCHA@GMAIL.COM") || (_smail.toUpperCase()).equals("MARTIN2603@GMAIL.COM")) { 
 //BA.debugLineNum = 398;BA.debugLine="If Msgbox2( \"Reiniciar\",\"\", \"Si\",\"No\",\"\", Null)=";
if (anywheresoftware.b4a.keywords.Common.Msgbox2("Reiniciar","","Si","No","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null),_ba)==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
 //BA.debugLineNum = 399;BA.debugLine="ReiniciaJuego";
_vvvv1(_ba);
 //BA.debugLineNum = 400;BA.debugLine="If Msgbox2( \"Avanzar\",\"\", \"Si\",\"No\",\"\", Null)=";
if (anywheresoftware.b4a.keywords.Common.Msgbox2("Avanzar","","Si","No","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null),_ba)==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
 //BA.debugLineNum = 401;BA.debugLine="AvanzaJuego(413)";
_vv3(_ba,(int) (413));
 };
 };
 };
 //BA.debugLineNum = 405;BA.debugLine="End Sub";
return "";
}
public static boolean  _vvv7(anywheresoftware.b4a.BA _ba,String _srutina,String _serror) throws Exception{
boolean _bret = false;
 //BA.debugLineNum = 521;BA.debugLine="Sub ManejaError(sRutina As String, sError As Strin";
 //BA.debugLineNum = 523;BA.debugLine="Dim bRet As Boolean";
_bret = false;
 //BA.debugLineNum = 524;BA.debugLine="If sError =\"\" Then";
if ((_serror).equals("")) { 
 //BA.debugLineNum = 525;BA.debugLine="sError = LastException.Message";
_serror = anywheresoftware.b4a.keywords.Common.LastException(_ba).getMessage();
 };
 //BA.debugLineNum = 527;BA.debugLine="Log(LastException.Message)";
anywheresoftware.b4a.keywords.Common.Log(anywheresoftware.b4a.keywords.Common.LastException(_ba).getMessage());
 //BA.debugLineNum = 528;BA.debugLine="Msgbox(\"\",sError)";
anywheresoftware.b4a.keywords.Common.Msgbox("",_serror,_ba);
 //BA.debugLineNum = 530;BA.debugLine="bRet = True";
_bret = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 531;BA.debugLine="Return bRet";
if (true) return _bret;
 //BA.debugLineNum = 532;BA.debugLine="End Sub";
return false;
}
public static boolean  _mensajeaparece_apagartimer(anywheresoftware.b4a.BA _ba,com.matetejuego.free.publicos._tmensajeaparece[] _alabelsmensaje) throws Exception{
boolean _bret = false;
int _i = 0;
 //BA.debugLineNum = 555;BA.debugLine="Sub MensajeAparece_ApagarTimer(aLabelsMensaje() As";
 //BA.debugLineNum = 556;BA.debugLine="Dim bRet As Boolean = True";
_bret = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 557;BA.debugLine="For I = 0 To aLabelsMensaje.Length-1";
{
final int step2 = 1;
final int limit2 = (int) (_alabelsmensaje.length-1);
for (_i = (int) (0) ; (step2 > 0 && _i <= limit2) || (step2 < 0 && _i >= limit2); _i = ((int)(0 + _i + step2)) ) {
 //BA.debugLineNum = 558;BA.debugLine="If aLabelsMensaje(I).Mostrada = False Then";
if (_alabelsmensaje[_i].Mostrada==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 559;BA.debugLine="bRet = False";
_bret = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 560;BA.debugLine="Exit";
if (true) break;
 };
 }
};
 //BA.debugLineNum = 564;BA.debugLine="Return bRet";
if (true) return _bret;
 //BA.debugLineNum = 566;BA.debugLine="End Sub";
return false;
}
public static String  _mensajeaparece_cargalabels(anywheresoftware.b4a.BA _ba,anywheresoftware.b4a.objects.PanelWrapper _pnlpanel,com.matetejuego.free.publicos._tmensajeaparece[] _alabelsmensaje,String _sfrase,String _stipoletra,int _iheight,boolean _bunalinea,int _ipartes) throws Exception{
int _irenglon = 0;
int _iposenpalabra = 0;
int _j = 0;
anywheresoftware.b4a.objects.LabelWrapper _la2 = null;
 //BA.debugLineNum = 668;BA.debugLine="Sub MensajeAparece_CargaLabels (pnlPanel As Panel,";
 //BA.debugLineNum = 670;BA.debugLine="Dim iRenglon As Int";
_irenglon = 0;
 //BA.debugLineNum = 671;BA.debugLine="Dim iPosEnPalabra As Int =0";
_iposenpalabra = (int) (0);
 //BA.debugLineNum = 673;BA.debugLine="iRenglon = 1";
_irenglon = (int) (1);
 //BA.debugLineNum = 674;BA.debugLine="For j=0 To aLabelsMensaje.Length-1";
{
final int step4 = 1;
final int limit4 = (int) (_alabelsmensaje.length-1);
for (_j = (int) (0) ; (step4 > 0 && _j <= limit4) || (step4 < 0 && _j >= limit4); _j = ((int)(0 + _j + step4)) ) {
 //BA.debugLineNum = 675;BA.debugLine="aLabelsMensaje (j).Mostrada=True";
_alabelsmensaje[_j].Mostrada = anywheresoftware.b4a.keywords.Common.True;
 }
};
 //BA.debugLineNum = 677;BA.debugLine="For j = 0 To aLabelsMensaje.Length-1";
{
final int step7 = 1;
final int limit7 = (int) (_alabelsmensaje.length-1);
for (_j = (int) (0) ; (step7 > 0 && _j <= limit7) || (step7 < 0 && _j >= limit7); _j = ((int)(0 + _j + step7)) ) {
 //BA.debugLineNum = 679;BA.debugLine="If sFrase.SubString2(j,j+1) = \" \" OR sFrase.";
if ((_sfrase.substring(_j,(int) (_j+1))).equals(" ") || (_sfrase.substring(_j,(int) (_j+1))).equals("_")) { 
 //BA.debugLineNum = 681;BA.debugLine="aLabelsMensaje (j).Mostrada = True 'marca el es";
_alabelsmensaje[_j].Mostrada = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 682;BA.debugLine="If sFrase.SubString2(j,j+1) = \" \" Then";
if ((_sfrase.substring(_j,(int) (_j+1))).equals(" ")) { 
 //BA.debugLineNum = 683;BA.debugLine="iRenglon = iRenglon +1";
_irenglon = (int) (_irenglon+1);
 //BA.debugLineNum = 684;BA.debugLine="iPosEnPalabra=1";
_iposenpalabra = (int) (1);
 }else {
 //BA.debugLineNum = 686;BA.debugLine="iPosEnPalabra = iPosEnPalabra+1";
_iposenpalabra = (int) (_iposenpalabra+1);
 };
 }else {
 //BA.debugLineNum = 689;BA.debugLine="Dim la2 As Label";
_la2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 690;BA.debugLine="la2.Initialize(\"lmensaje\")";
_la2.Initialize(_ba,"lmensaje");
 //BA.debugLineNum = 691;BA.debugLine="la2.Tag = sFrase.SubString2(j,j+1)";
_la2.setTag((Object)(_sfrase.substring(_j,(int) (_j+1))));
 //BA.debugLineNum = 693;BA.debugLine="CargaImagenLetra(sFrase.SubString2(j,j+1) &sTip";
_vv5(_ba,_sfrase.substring(_j,(int) (_j+1))+_stipoletra,_la2);
 //BA.debugLineNum = 695;BA.debugLine="MensajeAparece_ConfiguraLabel(pnlPanel, la2, 10";
_mensajeaparece_configuralabel(_ba,_pnlpanel,_la2,(int) (10),_ipartes,_iheight,_irenglon,_iposenpalabra,_bunalinea);
 //BA.debugLineNum = 696;BA.debugLine="aLabelsMensaje(j).lblLetra = la2";
_alabelsmensaje[_j].lblLetra = _la2;
 //BA.debugLineNum = 698;BA.debugLine="aLabelsMensaje (j).Posicion =j";
_alabelsmensaje[_j].Posicion = _j;
 //BA.debugLineNum = 699;BA.debugLine="aLabelsMensaje (j).Mostrada = False";
_alabelsmensaje[_j].Mostrada = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 700;BA.debugLine="aLabelsMensaje (j).sTipoLetra = \"a\"";
_alabelsmensaje[_j].sTipoLetra = "a";
 //BA.debugLineNum = 701;BA.debugLine="aLabelsMensaje (j).lblLetra.Visible = False";
_alabelsmensaje[_j].lblLetra.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 702;BA.debugLine="aLabelsMensaje (j).posEnPalabra = iPosEnPalabra";
_alabelsmensaje[_j].PosEnPalabra = _iposenpalabra;
 //BA.debugLineNum = 703;BA.debugLine="aLabelsMensaje(j).sTipoLetra = sTipoLetra";
_alabelsmensaje[_j].sTipoLetra = _stipoletra;
 //BA.debugLineNum = 704;BA.debugLine="iPosEnPalabra = iPosEnPalabra +1";
_iposenpalabra = (int) (_iposenpalabra+1);
 };
 }
};
 //BA.debugLineNum = 711;BA.debugLine="End Sub";
return "";
}
public static String  _mensajeaparece_cargalabels_n(anywheresoftware.b4a.BA _ba,anywheresoftware.b4a.objects.PanelWrapper _pnlpanel,com.matetejuego.free.publicos._tmensajeaparece[] _alabelsmensaje,String _sfrase,String _stipoletra,int _iheight,boolean _bunalinea,int _ipartes,anywheresoftware.b4a.keywords.constants.TypefaceWrapper _tfont,float _p_devicevaluesscreensize) throws Exception{
int _irenglon = 0;
int _iposenpalabra = 0;
int _j = 0;
anywheresoftware.b4a.objects.LabelWrapper _la2 = null;
 //BA.debugLineNum = 618;BA.debugLine="Sub MensajeAparece_CargaLabels_N (pnlPanel As Pane";
 //BA.debugLineNum = 620;BA.debugLine="Dim iRenglon As Int";
_irenglon = 0;
 //BA.debugLineNum = 621;BA.debugLine="Dim iPosEnPalabra As Int =0";
_iposenpalabra = (int) (0);
 //BA.debugLineNum = 623;BA.debugLine="iRenglon = 1";
_irenglon = (int) (1);
 //BA.debugLineNum = 624;BA.debugLine="For j=0 To aLabelsMensaje.Length-1";
{
final int step4 = 1;
final int limit4 = (int) (_alabelsmensaje.length-1);
for (_j = (int) (0) ; (step4 > 0 && _j <= limit4) || (step4 < 0 && _j >= limit4); _j = ((int)(0 + _j + step4)) ) {
 //BA.debugLineNum = 625;BA.debugLine="aLabelsMensaje (j).Mostrada=True";
_alabelsmensaje[_j].Mostrada = anywheresoftware.b4a.keywords.Common.True;
 }
};
 //BA.debugLineNum = 627;BA.debugLine="For j = 0 To aLabelsMensaje.Length-1";
{
final int step7 = 1;
final int limit7 = (int) (_alabelsmensaje.length-1);
for (_j = (int) (0) ; (step7 > 0 && _j <= limit7) || (step7 < 0 && _j >= limit7); _j = ((int)(0 + _j + step7)) ) {
 //BA.debugLineNum = 629;BA.debugLine="If sFrase.SubString2(j,j+1) = \" \" OR sFrase.";
if ((_sfrase.substring(_j,(int) (_j+1))).equals(" ") || (_sfrase.substring(_j,(int) (_j+1))).equals("_")) { 
 //BA.debugLineNum = 631;BA.debugLine="aLabelsMensaje (j).Mostrada = True 'marca el es";
_alabelsmensaje[_j].Mostrada = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 632;BA.debugLine="If sFrase.SubString2(j,j+1) = \" \" Then";
if ((_sfrase.substring(_j,(int) (_j+1))).equals(" ")) { 
 //BA.debugLineNum = 633;BA.debugLine="iRenglon = iRenglon +1";
_irenglon = (int) (_irenglon+1);
 //BA.debugLineNum = 634;BA.debugLine="iPosEnPalabra=1";
_iposenpalabra = (int) (1);
 }else {
 //BA.debugLineNum = 636;BA.debugLine="iPosEnPalabra = iPosEnPalabra+1";
_iposenpalabra = (int) (_iposenpalabra+1);
 };
 }else {
 //BA.debugLineNum = 639;BA.debugLine="Dim la2 As Label";
_la2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 640;BA.debugLine="la2.Initialize(\"lmensaje\")";
_la2.Initialize(_ba,"lmensaje");
 //BA.debugLineNum = 641;BA.debugLine="la2.Tag = sFrase.SubString2(j,j+1)";
_la2.setTag((Object)(_sfrase.substring(_j,(int) (_j+1))));
 //BA.debugLineNum = 644;BA.debugLine="la2.Text = sFrase.SubString2(j,j+1)";
_la2.setText((Object)(_sfrase.substring(_j,(int) (_j+1))));
 //BA.debugLineNum = 646;BA.debugLine="la2.TextSize = Font_RecalculaTam(24)";
_la2.setTextSize((float) (_font_recalculatam(_ba,(int) (24))));
 //BA.debugLineNum = 647;BA.debugLine="la2.TextColor = Colors.White";
_la2.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 648;BA.debugLine="la2.Typeface = tFont";
_la2.setTypeface((android.graphics.Typeface)(_tfont.getObject()));
 //BA.debugLineNum = 650;BA.debugLine="MensajeAparece_ConfiguraLabel(pnlPanel, la2, 10";
_mensajeaparece_configuralabel(_ba,_pnlpanel,_la2,(int) (10),(int) (11),_iheight,_irenglon,_iposenpalabra,_bunalinea);
 //BA.debugLineNum = 651;BA.debugLine="aLabelsMensaje(j).lblLetra = la2";
_alabelsmensaje[_j].lblLetra = _la2;
 //BA.debugLineNum = 653;BA.debugLine="aLabelsMensaje (j).Posicion =j";
_alabelsmensaje[_j].Posicion = _j;
 //BA.debugLineNum = 654;BA.debugLine="aLabelsMensaje (j).Mostrada = False";
_alabelsmensaje[_j].Mostrada = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 655;BA.debugLine="aLabelsMensaje (j).sTipoLetra = \"a\"";
_alabelsmensaje[_j].sTipoLetra = "a";
 //BA.debugLineNum = 656;BA.debugLine="aLabelsMensaje (j).lblLetra.Visible = False";
_alabelsmensaje[_j].lblLetra.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 657;BA.debugLine="aLabelsMensaje (j).posEnPalabra = iPosEnPalabra";
_alabelsmensaje[_j].PosEnPalabra = _iposenpalabra;
 //BA.debugLineNum = 658;BA.debugLine="aLabelsMensaje(j).sTipoLetra = sTipoLetra";
_alabelsmensaje[_j].sTipoLetra = _stipoletra;
 //BA.debugLineNum = 659;BA.debugLine="iPosEnPalabra = iPosEnPalabra +1";
_iposenpalabra = (int) (_iposenpalabra+1);
 };
 }
};
 //BA.debugLineNum = 666;BA.debugLine="End Sub";
return "";
}
public static String  _mensajeaparece_configuralabel(anywheresoftware.b4a.BA _ba,anywheresoftware.b4a.objects.PanelWrapper _pnlpanel,anywheresoftware.b4a.objects.LabelWrapper _lblsetear,int _itopdesde,int _ipartes,int _aheight,int _renglon,int _posenpalabra,boolean _bunalinea) throws Exception{
int _itam = 0;
int _iespacio = 0;
int _itop = 0;
int _ileft = 0;
 //BA.debugLineNum = 569;BA.debugLine="Sub MensajeAparece_ConfiguraLabel (pnlPanel As Pan";
 //BA.debugLineNum = 570;BA.debugLine="Dim iTam As Int, iEspacio As Int, iTop As Int, iLe";
_itam = 0;
_iespacio = 0;
_itop = 0;
_ileft = 0;
 //BA.debugLineNum = 573;BA.debugLine="If bUnaLinea Then";
if (_bunalinea) { 
 //BA.debugLineNum = 574;BA.debugLine="iTam = pnlPanel.Width/iPartes";
_itam = (int) (_pnlpanel.getWidth()/(double)_ipartes);
 }else {
 //BA.debugLineNum = 577;BA.debugLine="iTam = pnlPanel.width/iPartes";
_itam = (int) (_pnlpanel.getWidth()/(double)_ipartes);
 //BA.debugLineNum = 579;BA.debugLine="iEspacio = iTam*0.1";
_iespacio = (int) (_itam*0.1);
 };
 //BA.debugLineNum = 581;BA.debugLine="If iTopDesde =-1 Then";
if (_itopdesde==-1) { 
 //BA.debugLineNum = 582;BA.debugLine="iTop = pnlPanel.Height/2 - iTam/2 - pnlPanel.heig";
_itop = (int) (_pnlpanel.getHeight()/(double)2-_itam/(double)2-_pnlpanel.getHeight()*(0.1));
 }else {
 //BA.debugLineNum = 585;BA.debugLine="If Renglon = 2123211 Then";
if (_renglon==2123211) { 
 //BA.debugLineNum = 586;BA.debugLine="iTop = iEspacio";
_itop = _iespacio;
 }else {
 //BA.debugLineNum = 588;BA.debugLine="iTop = (iTam*  (Renglon-1)) + ((iEspacio*3)* (Re";
_itop = (int) ((_itam*(_renglon-1))+((_iespacio*3)*(_renglon-1))+_itopdesde);
 };
 };
 //BA.debugLineNum = 593;BA.debugLine="iLeft  = (PosEnPalabra+1) * (iTam + iEspacio)";
_ileft = (int) ((_posenpalabra+1)*(_itam+_iespacio));
 //BA.debugLineNum = 595;BA.debugLine="pnlPanel.AddView( lblSetear, iLeft, iTop, iTam, iT";
_pnlpanel.AddView((android.view.View)(_lblsetear.getObject()),_ileft,_itop,_itam,_itam);
 //BA.debugLineNum = 597;BA.debugLine="End Sub";
return "";
}
public static int  _mensajeaparece_eligeletra(anywheresoftware.b4a.BA _ba,com.matetejuego.free.publicos._tmensajeaparece[] _alabelsmensaje) throws Exception{
int _iazar = 0;
int _j = 0;
 //BA.debugLineNum = 605;BA.debugLine="Sub MensajeAparece_EligeLetra (aLabelsMensaje() As";
 //BA.debugLineNum = 607;BA.debugLine="Dim iAzar As Int = -1";
_iazar = (int) (-1);
 //BA.debugLineNum = 608;BA.debugLine="For j=0 To aLabelsMensaje.Length -1";
{
final int step2 = 1;
final int limit2 = (int) (_alabelsmensaje.length-1);
for (_j = (int) (0) ; (step2 > 0 && _j <= limit2) || (step2 < 0 && _j >= limit2); _j = ((int)(0 + _j + step2)) ) {
 //BA.debugLineNum = 610;BA.debugLine="iAzar = Rnd(0, aLabelsMensaje.length)";
_iazar = anywheresoftware.b4a.keywords.Common.Rnd((int) (0),_alabelsmensaje.length);
 //BA.debugLineNum = 611;BA.debugLine="If aLabelsMensaje (iAzar).Mostrada = False Then";
if (_alabelsmensaje[_iazar].Mostrada==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 612;BA.debugLine="Exit";
if (true) break;
 };
 }
};
 //BA.debugLineNum = 615;BA.debugLine="Return iAzar";
if (true) return _iazar;
 //BA.debugLineNum = 616;BA.debugLine="End Sub";
return 0;
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 8;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 11;BA.debugLine="Public sNombreMonedas As String =\"Neuronas\"";
_v5 = BA.__b (new byte[] {39,30,-70,72,25,103,-25,93}, 758493);
 //BA.debugLineNum = 13;BA.debugLine="Public g_DeviceValuesHeight As Int, g_DeviceValue";
_g_devicevaluesheight = 0;
_g_devicevalueswidth = 0;
 //BA.debugLineNum = 14;BA.debugLine="Public  g_DeviceValuesScreenSize As Float, g_Devi";
_g_devicevaluesscreensize = 0f;
_g_devicevaluesscale = 0f;
 //BA.debugLineNum = 17;BA.debugLine="Dim g_sPswBaseJ  As String = \"NadaAnsioDeNada\"";
_g_spswbasej = BA.__b (new byte[] {39,26,-55,62,55,103,-105,34,79,105,-97,0,75,88,-104}, 820073);
 //BA.debugLineNum = 18;BA.debugLine="Dim g_sPswBaseU As String = \"MientrasDuraElInstan";
_g_spswbaseu = BA.__b (new byte[] {36,19,118,-63,2,122,59,-56,100,89,54,-33,111,81,14,-111,-19,-114,33,-109,98,84,97}, 192986);
 //BA.debugLineNum = 19;BA.debugLine="Public g_sqlBaseLocalJuego As SQLCipher, g_sqlBas";
_g_sqlbaselocaljuego = new anyhwheresoftware.b4a.objects.sqlcipher.SQLCipher();
_g_sqlbaselocalusuario = new anyhwheresoftware.b4a.objects.sqlcipher.SQLCipher();
 //BA.debugLineNum = 20;BA.debugLine="Public g_NombreBaseLocalJuego As String =\"BaseJ.s";
_g_nombrebaselocaljuego = BA.__b (new byte[] {43,26,111,16,60,39,38,16,76,68,63,1}, 678324);
 //BA.debugLineNum = 21;BA.debugLine="Public g_NombreBaseLocalUsuario As String = \"Base";
_g_nombrebaselocalusuario = BA.__b (new byte[] {43,27,-44,39,35,77,-64,37,81,64,-103,39,79}, 343370);
 //BA.debugLineNum = 24;BA.debugLine="Type tColoresPaneles (iNroPanel As Int, iRgb1 As";
;
 //BA.debugLineNum = 27;BA.debugLine="Type tProductosPS (ProductID As String, _ 		Manag";
;
 //BA.debugLineNum = 38;BA.debugLine="Type tProductosComprados  (ProductID As String, S";
;
 //BA.debugLineNum = 40;BA.debugLine="Type tColoresPaleta ( _  		ColorNombre As String,";
;
 //BA.debugLineNum = 49;BA.debugLine="Type tColores ( _  		Paleta As Int, _  	 	bitmBaj";
;
 //BA.debugLineNum = 80;BA.debugLine="Type tOrdenar (Indice As Int, Orden As Int, Letra";
;
 //BA.debugLineNum = 81;BA.debugLine="Type tMensajeAparece (lblLetra As Label, Posicion";
;
 //BA.debugLineNum = 82;BA.debugLine="Type tLetrasElegir (LetraSiempre As Char, LetraMo";
;
 //BA.debugLineNum = 85;BA.debugLine="Type tHistoria (idPalabra As Int, Adivinada As In";
;
 //BA.debugLineNum = 88;BA.debugLine="Type tLetrasPalabra (idpalabra As Int, posicion A";
;
 //BA.debugLineNum = 89;BA.debugLine="Type tPalabra (idPalabra As Int, Palabra As Strin";
;
 //BA.debugLineNum = 91;BA.debugLine="Type tNivel (idNivel As Int, Nombre As String, Im";
;
 //BA.debugLineNum = 93;BA.debugLine="Type tPistas (pistaid As Int, pista As String, ti";
;
 //BA.debugLineNum = 97;BA.debugLine="Type tConfiguraPantalla (Compartir As Int, Compra";
;
 //BA.debugLineNum = 99;BA.debugLine="Type tQuerysRemotos (Premium As Int, Avance As In";
;
 //BA.debugLineNum = 101;BA.debugLine="End Sub";
return "";
}
public static boolean  _propaganda_setapaga(anywheresoftware.b4a.BA _ba,anyhwheresoftware.b4a.objects.sqlcipher.SQLCipher _base) throws Exception{
String _ssql = "";
boolean _bret = false;
 //BA.debugLineNum = 1231;BA.debugLine="Sub Propaganda_SetApaga (Base As SQLCipher) As Boo";
 //BA.debugLineNum = 1232;BA.debugLine="Dim sSql As String, bRet As Boolean = False";
_ssql = "";
_bret = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 1233;BA.debugLine="Try";
try { //BA.debugLineNum = 1234;BA.debugLine="If Get_EsPremium(Base) = False Then";
if (_get_espremium(_ba,_base)==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 1235;BA.debugLine="sSql = \"Insert Into Seteosusuario (tiposeteo, t";
_ssql = "Insert Into Seteosusuario (tiposeteo, texto) values ('propaganda','apagada')";
 //BA.debugLineNum = 1236;BA.debugLine="Base.ExecNonQuery (sSql)";
_base.ExecNonQuery(_ssql);
 //BA.debugLineNum = 1237;BA.debugLine="bRet = True";
_bret = anywheresoftware.b4a.keywords.Common.True;
 }else {
 //BA.debugLineNum = 1239;BA.debugLine="bRet = True";
_bret = anywheresoftware.b4a.keywords.Common.True;
 };
 } 
       catch (Exception e11) {
			(_ba.processBA == null ? _ba : _ba.processBA).setLastException(e11); //BA.debugLineNum = 1242;BA.debugLine="ManejaError(\"Set_Propaganda\", \"No se pudo apagar";
_vvv7(_ba,"Set_Propaganda","No se pudo apagar la propaganda");
 };
 //BA.debugLineNum = 1245;BA.debugLine="Return bRet";
if (true) return _bret;
 //BA.debugLineNum = 1246;BA.debugLine="End Sub";
return false;
}
public static String  _provider_letras(anywheresoftware.b4a.BA _ba,String _sletras,int _irenglon) throws Exception{
String _sret = "";
int _iinicio = 0;
String _suna = "";
int _j = 0;
 //BA.debugLineNum = 984;BA.debugLine="Sub Provider_letras(sLetras As String, iRenglon As";
 //BA.debugLineNum = 985;BA.debugLine="Dim sRet As String = \" \", iInicio As Int, sUna As";
_sret = " ";
_iinicio = 0;
_suna = "";
 //BA.debugLineNum = 986;BA.debugLine="iInicio = 0+((iRenglon-1)*6)";
_iinicio = (int) (0+((_irenglon-1)*6));
 //BA.debugLineNum = 987;BA.debugLine="For j = iInicio To iInicio +5";
{
final int step3 = 1;
final int limit3 = (int) (_iinicio+5);
for (_j = _iinicio ; (step3 > 0 && _j <= limit3) || (step3 < 0 && _j >= limit3); _j = ((int)(0 + _j + step3)) ) {
 //BA.debugLineNum = 988;BA.debugLine="sUna = sLetras.SubString2(j,j+1)";
_suna = _sletras.substring(_j,(int) (_j+1));
 //BA.debugLineNum = 989;BA.debugLine="If sUna = \"?\" OR sUna = \" \" Then";
if ((_suna).equals("?") || (_suna).equals(" ")) { 
 //BA.debugLineNum = 990;BA.debugLine="sRet = sRet & \"  \" & \"_\"";
_sret = _sret+"  "+"_";
 }else {
 //BA.debugLineNum = 992;BA.debugLine="sRet = sRet & \"  \" & sUna";
_sret = _sret+"  "+_suna;
 };
 }
};
 //BA.debugLineNum = 995;BA.debugLine="Return sRet";
if (true) return _sret;
 //BA.debugLineNum = 996;BA.debugLine="End Sub";
return "";
}
public static String  _vvv0(anywheresoftware.b4a.BA _ba,anywheresoftware.b4a.objects.ConcreteViewWrapper _xview,int _iwidth,int _iheight) throws Exception{
 //BA.debugLineNum = 137;BA.debugLine="Public Sub RedimensionaView (xView As View, iWidth";
 //BA.debugLineNum = 138;BA.debugLine="xView.Width = ConvierteDipsStandard(iWidth, \"W\")";
_xview.setWidth(_vvv1(_ba,_iwidth,BA.ObjectToChar("W")));
 //BA.debugLineNum = 139;BA.debugLine="xView.Height=ConvierteDipsStandard(iHeight, \"H\")";
_xview.setHeight(_vvv1(_ba,_iheight,BA.ObjectToChar("H")));
 //BA.debugLineNum = 140;BA.debugLine="End Sub";
return "";
}
public static String  _vvvv1(anywheresoftware.b4a.BA _ba) throws Exception{
String _ssql = "";
 //BA.debugLineNum = 327;BA.debugLine="Public Sub ReiniciaJuego";
 //BA.debugLineNum = 328;BA.debugLine="Dim sSql As String', sFecha As String, bRet As Bo";
_ssql = "";
 //BA.debugLineNum = 330;BA.debugLine="Try";
try { //BA.debugLineNum = 331;BA.debugLine="g_sqlBaseLocalUsuario.BeginTransaction";
_g_sqlbaselocalusuario.BeginTransaction();
 //BA.debugLineNum = 334;BA.debugLine="sSql = \"Delete from avance\"";
_ssql = "Delete from avance";
 //BA.debugLineNum = 335;BA.debugLine="g_sqlBaseLocalUsuario.ExecNonQuery (sSql)";
_g_sqlbaselocalusuario.ExecNonQuery(_ssql);
 //BA.debugLineNum = 337;BA.debugLine="sSql = \"Update usuario set monedas = 150, Puntos";
_ssql = "Update usuario set monedas = 150, Puntos=0";
 //BA.debugLineNum = 338;BA.debugLine="g_sqlBaseLocalUsuario.ExecNonQuery(sSql)";
_g_sqlbaselocalusuario.ExecNonQuery(_ssql);
 //BA.debugLineNum = 342;BA.debugLine="sSql = \"delete from letrascompradas \"";
_ssql = "delete from letrascompradas ";
 //BA.debugLineNum = 343;BA.debugLine="g_sqlBaseLocalUsuario.ExecNonQuery(sSql)";
_g_sqlbaselocalusuario.ExecNonQuery(_ssql);
 //BA.debugLineNum = 345;BA.debugLine="sSql = \"delete from letrasporpalabra\"";
_ssql = "delete from letrasporpalabra";
 //BA.debugLineNum = 346;BA.debugLine="g_sqlBaseLocalUsuario.ExecNonQuery(sSql)";
_g_sqlbaselocalusuario.ExecNonQuery(_ssql);
 //BA.debugLineNum = 349;BA.debugLine="g_sqlBaseLocalUsuario.TransactionSuccessful";
_g_sqlbaselocalusuario.TransactionSuccessful();
 //BA.debugLineNum = 350;BA.debugLine="g_sqlBaseLocalUsuario.EndTransaction";
_g_sqlbaselocalusuario.EndTransaction();
 } 
       catch (Exception e15) {
			(_ba.processBA == null ? _ba : _ba.processBA).setLastException(e15); //BA.debugLineNum = 357;BA.debugLine="g_sqlBaseLocalUsuario.EndTransaction";
_g_sqlbaselocalusuario.EndTransaction();
 //BA.debugLineNum = 358;BA.debugLine="ManejaError(\"ReiniciaJuego\",\"Error Actualizar Pi";
_vvv7(_ba,"ReiniciaJuego","Error Actualizar Pistas");
 };
 //BA.debugLineNum = 361;BA.debugLine="End Sub";
return "";
}
public static String  _vvvv2(anywheresoftware.b4a.BA _ba,String _stexto,int _icant) throws Exception{
String _sret = "";
int _j = 0;
 //BA.debugLineNum = 911;BA.debugLine="Sub Replicar (sTexto As String, iCant As Int) As S";
 //BA.debugLineNum = 912;BA.debugLine="Dim sRet As String=\"\"";
_sret = "";
 //BA.debugLineNum = 913;BA.debugLine="For j = 0 To iCant";
{
final int step2 = 1;
final int limit2 = _icant;
for (_j = (int) (0) ; (step2 > 0 && _j <= limit2) || (step2 < 0 && _j >= limit2); _j = ((int)(0 + _j + step2)) ) {
 //BA.debugLineNum = 914;BA.debugLine="sRet = sRet & sTexto";
_sret = _sret+_stexto;
 }
};
 //BA.debugLineNum = 916;BA.debugLine="Return sRet";
if (true) return _sret;
 //BA.debugLineNum = 917;BA.debugLine="End Sub";
return "";
}
public static String  _vvvv3(anywheresoftware.b4a.BA _ba,anywheresoftware.b4a.objects.ActivityWrapper _xactivity,String _sdirectorio,String _sarchivo) throws Exception{
anywheresoftware.b4a.agraham.reflection.Reflection _obj1 = null;
anywheresoftware.b4a.agraham.reflection.Reflection _obj2 = null;
anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _bmp = null;
anywheresoftware.b4a.objects.drawable.CanvasWrapper _c = null;
long _now = 0L;
long _i = 0L;
String _dt = "";
Object[] _args = null;
String[] _types = null;
anywheresoftware.b4a.objects.streams.File.OutputStreamWrapper _out = null;
 //BA.debugLineNum = 1054;BA.debugLine="Sub Screenshot(xActivity As Activity , sDirectorio";
 //BA.debugLineNum = 1056;BA.debugLine="Dim Obj1, Obj2 As Reflector";
_obj1 = new anywheresoftware.b4a.agraham.reflection.Reflection();
_obj2 = new anywheresoftware.b4a.agraham.reflection.Reflection();
 //BA.debugLineNum = 1057;BA.debugLine="Dim bmp As Bitmap";
_bmp = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
 //BA.debugLineNum = 1058;BA.debugLine="Dim c As Canvas";
_c = new anywheresoftware.b4a.objects.drawable.CanvasWrapper();
 //BA.debugLineNum = 1059;BA.debugLine="Dim now, i As Long";
_now = 0L;
_i = 0L;
 //BA.debugLineNum = 1060;BA.debugLine="Dim dt As String";
_dt = "";
 //BA.debugLineNum = 1061;BA.debugLine="DateTime.DateFormat = \"yyMMddHHmmss\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("yyMMddHHmmss");
 //BA.debugLineNum = 1062;BA.debugLine="now = DateTime.now";
_now = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 1063;BA.debugLine="dt = DateTime.Date(now) ' e.g.: \"110812150355\" is";
_dt = anywheresoftware.b4a.keywords.Common.DateTime.Date(_now);
 //BA.debugLineNum = 1064;BA.debugLine="Obj1.Target = Obj1.GetActivityBA";
_obj1.Target = (Object)(_obj1.GetActivityBA((_ba.processBA == null ? _ba : _ba.processBA)));
 //BA.debugLineNum = 1065;BA.debugLine="Obj1.Target = Obj1.GetField(\"vg\")";
_obj1.Target = _obj1.GetField("vg");
 //BA.debugLineNum = 1066;BA.debugLine="bmp.InitializeMutable(xActivity.Width, xActivity.";
_bmp.InitializeMutable(_xactivity.getWidth(),_xactivity.getHeight());
 //BA.debugLineNum = 1067;BA.debugLine="c.Initialize2(bmp)";
_c.Initialize2((android.graphics.Bitmap)(_bmp.getObject()));
 //BA.debugLineNum = 1068;BA.debugLine="Dim args(1) As Object";
_args = new Object[(int) (1)];
{
int d0 = _args.length;
for (int i0 = 0;i0 < d0;i0++) {
_args[i0] = new Object();
}
}
;
 //BA.debugLineNum = 1069;BA.debugLine="Dim types(1) As String";
_types = new String[(int) (1)];
java.util.Arrays.fill(_types,"");
 //BA.debugLineNum = 1070;BA.debugLine="Obj2.Target = c";
_obj2.Target = (Object)(_c);
 //BA.debugLineNum = 1071;BA.debugLine="Obj2.Target = Obj2.GetField(\"canvas\")";
_obj2.Target = _obj2.GetField("canvas");
 //BA.debugLineNum = 1072;BA.debugLine="args(0) = Obj2.Target";
_args[(int) (0)] = _obj2.Target;
 //BA.debugLineNum = 1073;BA.debugLine="types(0) = \"android.graphics.Canvas\"";
_types[(int) (0)] = "android.graphics.Canvas";
 //BA.debugLineNum = 1074;BA.debugLine="Obj1.RunMethod4(\"draw\", args, types)";
_obj1.RunMethod4("draw",_args,_types);
 //BA.debugLineNum = 1075;BA.debugLine="Dim Out As OutputStream";
_out = new anywheresoftware.b4a.objects.streams.File.OutputStreamWrapper();
 //BA.debugLineNum = 1076;BA.debugLine="Out = File.OpenOutput(sDirectorio, sArchivo , Fal";
_out = anywheresoftware.b4a.keywords.Common.File.OpenOutput(_sdirectorio,_sarchivo,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1077;BA.debugLine="bmp.WriteToStream(Out, 100, \"PNG\")";
_bmp.WriteToStream((java.io.OutputStream)(_out.getObject()),(int) (100),BA.getEnumFromString(android.graphics.Bitmap.CompressFormat.class,"PNG"));
 //BA.debugLineNum = 1078;BA.debugLine="Out.Close";
_out.Close();
 //BA.debugLineNum = 1079;BA.debugLine="End Sub";
return "";
}
public static String  _set_pantallacargaset(anywheresoftware.b4a.BA _ba,anywheresoftware.b4a.objects.ActivityWrapper _xactivity,anywheresoftware.b4a.objects.ImageViewWrapper _imgiconofondo,anywheresoftware.b4a.objects.LabelWrapper _lblmensaje,int _icolor,boolean _biniciapremium) throws Exception{
int _ileft = 0;
int _itop = 0;
int _iwidth = 0;
int _iheight = 0;
 //BA.debugLineNum = 1191;BA.debugLine="Sub Set_PantallaCargaSet(xActivity As Activity, im";
 //BA.debugLineNum = 1193;BA.debugLine="imgIconoFondo.Width = xActivity.height * 0.40";
_imgiconofondo.setWidth((int) (_xactivity.getHeight()*0.40));
 //BA.debugLineNum = 1194;BA.debugLine="imgIconoFondo.Height = imgIconoFondo.Width";
_imgiconofondo.setHeight(_imgiconofondo.getWidth());
 //BA.debugLineNum = 1195;BA.debugLine="imgIconoFondo.Top = xActivity.Height * 0.20";
_imgiconofondo.setTop((int) (_xactivity.getHeight()*0.20));
 //BA.debugLineNum = 1196;BA.debugLine="CentrarControl(xActivity, imgIconoFondo, True, Fa";
_vv7(_ba,_xactivity,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(_imgiconofondo.getObject())),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1197;BA.debugLine="xActivity.Color = Colors.white";
_xactivity.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 1199;BA.debugLine="imgIconoFondo.bringtofront";
_imgiconofondo.BringToFront();
 //BA.debugLineNum = 1202;BA.debugLine="lblMensaje.Color = Colors.White";
_lblmensaje.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 1203;BA.debugLine="lblMensaje.height = xActivity.Height * 0.1";
_lblmensaje.setHeight((int) (_xactivity.getHeight()*0.1));
 //BA.debugLineNum = 1204;BA.debugLine="lblMensaje.Width = xActivity.Width";
_lblmensaje.setWidth(_xactivity.getWidth());
 //BA.debugLineNum = 1205;BA.debugLine="lblMensaje.Top = xActivity.Height*0.1";
_lblmensaje.setTop((int) (_xactivity.getHeight()*0.1));
 //BA.debugLineNum = 1206;BA.debugLine="lblMensaje.TextColor = iColor";
_lblmensaje.setTextColor(_icolor);
 //BA.debugLineNum = 1207;BA.debugLine="If bIniciaPremium Then";
if (_biniciapremium) { 
 //BA.debugLineNum = 1208;BA.debugLine="lblMensaje.Text = \"PREMIUMIFICANDO\" & Chr(10) &";
_lblmensaje.setText((Object)("PREMIUMIFICANDO"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"MATETE..."));
 }else {
 //BA.debugLineNum = 1210;BA.debugLine="lblMensaje.Text = \"INVENTANDO\" & Chr(10) & \"MATE";
_lblmensaje.setText((Object)("INVENTANDO"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"MATETES..."));
 };
 //BA.debugLineNum = 1215;BA.debugLine="lblMensaje.Gravity = Gravity.CENTER";
_lblmensaje.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 1216;BA.debugLine="CentrarControl(xActivity, lblMensaje, True, False";
_vv7(_ba,_xactivity,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(_lblmensaje.getObject())),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1217;BA.debugLine="SetLabelTextSize (lblMensaje,lblMensaje.Text, 30,";
_vvvv4(_ba,_lblmensaje,_lblmensaje.getText(),(float) (30),(float) (6),(int) (100));
 //BA.debugLineNum = 1219;BA.debugLine="Dim iLeft As Int = lblMensaje.Left, iTop As Int=";
_ileft = _lblmensaje.getLeft();
_itop = _lblmensaje.getTop();
_iwidth = _lblmensaje.getWidth();
_iheight = _lblmensaje.getHeight();
 //BA.debugLineNum = 1226;BA.debugLine="End Sub";
return "";
}
public static String  _seteousuarioenterodesde_inserta(anywheresoftware.b4a.BA _ba,anyhwheresoftware.b4a.objects.sqlcipher.SQLCipher _baseusuario,String _sseteo,int _ientero) throws Exception{
String _ssql = "";
 //BA.debugLineNum = 853;BA.debugLine="Sub SeteoUsuarioEnteroDesde_Inserta(BaseUsuario As";
 //BA.debugLineNum = 855;BA.debugLine="Dim ssql As String = \"Insert Into SeteosUsuario (";
_ssql = "Insert Into SeteosUsuario (TipoSeteo, enterodesde) values ('"+_sseteo+"',"+BA.NumberToString(_ientero)+")";
 //BA.debugLineNum = 856;BA.debugLine="Try";
try { //BA.debugLineNum = 857;BA.debugLine="BaseUsuario.ExecNonQuery(ssql)";
_baseusuario.ExecNonQuery(_ssql);
 } 
       catch (Exception e5) {
			(_ba.processBA == null ? _ba : _ba.processBA).setLastException(e5); //BA.debugLineNum = 859;BA.debugLine="ManejaError(\"Pero qué está pasando?\", \"Chanfle\")";
_vvv7(_ba,"Pero qué está pasando?","Chanfle");
 };
 //BA.debugLineNum = 862;BA.debugLine="End Sub";
return "";
}
public static String  _seteousuarioenterodesde_update(anywheresoftware.b4a.BA _ba,anyhwheresoftware.b4a.objects.sqlcipher.SQLCipher _baseusuario,String _sseteo,int _ivalor) throws Exception{
String _ssql = "";
 //BA.debugLineNum = 877;BA.debugLine="Sub SeteoUsuarioEnteroDesde_Update(BaseUsuario As";
 //BA.debugLineNum = 878;BA.debugLine="Dim sSql As String";
_ssql = "";
 //BA.debugLineNum = 880;BA.debugLine="Try";
try { //BA.debugLineNum = 881;BA.debugLine="sSql = \"Update SeteosUsuario Set enterodesde = \"";
_ssql = "Update SeteosUsuario Set enterodesde = "+BA.NumberToString(_ivalor)+" where tiposeteo = '"+_sseteo+"'";
 //BA.debugLineNum = 882;BA.debugLine="BaseUsuario.ExecNonQuery(sSql)";
_baseusuario.ExecNonQuery(_ssql);
 } 
       catch (Exception e6) {
			(_ba.processBA == null ? _ba : _ba.processBA).setLastException(e6); //BA.debugLineNum = 884;BA.debugLine="ManejaError(\"Caranchos\", \"Y ahora qué pasó?\")";
_vvv7(_ba,"Caranchos","Y ahora qué pasó?");
 };
 //BA.debugLineNum = 888;BA.debugLine="End Sub";
return "";
}
public static String  _seteousuariotexto_inserta(anywheresoftware.b4a.BA _ba,anyhwheresoftware.b4a.objects.sqlcipher.SQLCipher _baseusuario,String _sseteo,String _stexto) throws Exception{
String _ssql = "";
 //BA.debugLineNum = 864;BA.debugLine="Sub SeteoUsuarioTexto_Inserta(BaseUsuario As  SQLC";
 //BA.debugLineNum = 866;BA.debugLine="Dim ssql As String = \"Insert Into SeteosUsuario (";
_ssql = "Insert Into SeteosUsuario (TipoSeteo, texto) values ('"+_sseteo+"','"+_stexto+"')";
 //BA.debugLineNum = 867;BA.debugLine="Try";
try { //BA.debugLineNum = 868;BA.debugLine="BaseUsuario.ExecNonQuery(ssql)";
_baseusuario.ExecNonQuery(_ssql);
 } 
       catch (Exception e5) {
			(_ba.processBA == null ? _ba : _ba.processBA).setLastException(e5); //BA.debugLineNum = 870;BA.debugLine="ManejaError(\"Pero qué está pasando?\", \"Chanfle\")";
_vvv7(_ba,"Pero qué está pasando?","Chanfle");
 };
 //BA.debugLineNum = 873;BA.debugLine="End Sub";
return "";
}
public static String  _vvvv4(anywheresoftware.b4a.BA _ba,anywheresoftware.b4a.objects.LabelWrapper _lbl,String _txt,float _maxfontsize,float _minfontsize,int _iporcsobremaximo) throws Exception{
float _fontsize = 0f;
int _height = 0;
anywheresoftware.b4a.objects.StringUtils _stu = null;
 //BA.debugLineNum = 1029;BA.debugLine="Sub SetLabelTextSize(lbl As Label, txt As String,";
 //BA.debugLineNum = 1030;BA.debugLine="Dim FontSize = MaxFontSize As Float";
_fontsize = _maxfontsize;
 //BA.debugLineNum = 1031;BA.debugLine="Dim Height As Int";
_height = 0;
 //BA.debugLineNum = 1032;BA.debugLine="Dim stu As StringUtils";
_stu = new anywheresoftware.b4a.objects.StringUtils();
 //BA.debugLineNum = 1034;BA.debugLine="lbl.TextSize = FontSize";
_lbl.setTextSize(_fontsize);
 //BA.debugLineNum = 1035;BA.debugLine="Height = stu.MeasureMultilineTextHeight(lbl, t";
_height = _stu.MeasureMultilineTextHeight((android.widget.TextView)(_lbl.getObject()),_txt);
 //BA.debugLineNum = 1036;BA.debugLine="Do While Height > lbl.Height AND FontSize > Mi";
while (_height>_lbl.getHeight() && _fontsize>_minfontsize) {
 //BA.debugLineNum = 1037;BA.debugLine="FontSize = FontSize - 1";
_fontsize = (float) (_fontsize-1);
 //BA.debugLineNum = 1038;BA.debugLine="lbl.TextSize = FontSize";
_lbl.setTextSize(_fontsize);
 //BA.debugLineNum = 1039;BA.debugLine="Height = stu.MeasureMultilineTextHeight(lb";
_height = _stu.MeasureMultilineTextHeight((android.widget.TextView)(_lbl.getObject()),_txt);
 }
;
 //BA.debugLineNum = 1041;BA.debugLine="If iPorcSobreMaximo >0 Then";
if (_iporcsobremaximo>0) { 
 //BA.debugLineNum = 1042;BA.debugLine="lbl.TextSize = lbl.TextSize * iPorcSobreMaximo /";
_lbl.setTextSize((float) (_lbl.getTextSize()*_iporcsobremaximo/(double)100));
 };
 //BA.debugLineNum = 1044;BA.debugLine="End Sub";
return "";
}
public static boolean  _sql_ejecutar(anywheresoftware.b4a.BA _ba,anywheresoftware.b4a.sql.SQL _sqlbase,String _ssql) throws Exception{
boolean _bret = false;
 //BA.debugLineNum = 228;BA.debugLine="Public Sub SQL_Ejecutar (sqlBase As SQL, sSql As S";
 //BA.debugLineNum = 229;BA.debugLine="Dim bRet As Boolean = False";
_bret = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 231;BA.debugLine="Try";
try { //BA.debugLineNum = 232;BA.debugLine="sqlBase.ExecNonQuery (sSql)";
_sqlbase.ExecNonQuery(_ssql);
 //BA.debugLineNum = 233;BA.debugLine="bRet = True";
_bret = anywheresoftware.b4a.keywords.Common.True;
 } 
       catch (Exception e6) {
			(_ba.processBA == null ? _ba : _ba.processBA).setLastException(e6); //BA.debugLineNum = 235;BA.debugLine="bRet = False";
_bret = anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 237;BA.debugLine="Return bRet";
if (true) return _bret;
 //BA.debugLineNum = 238;BA.debugLine="End Sub";
return false;
}
public static int  _twitter_getuso(anywheresoftware.b4a.BA _ba,anyhwheresoftware.b4a.objects.sqlcipher.SQLCipher _xbasesql) throws Exception{
anywheresoftware.b4a.sql.SQL.CursorWrapper _cursor1 = null;
String _ssql = "";
int _iret = 0;
 //BA.debugLineNum = 1010;BA.debugLine="Sub Twitter_GetUso (xBaseSql As SQLCipher) As Int";
 //BA.debugLineNum = 1011;BA.debugLine="Dim Cursor1 As Cursor, ssql As String , iRet As I";
_cursor1 = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
_ssql = "";
_iret = (int) (0);
 //BA.debugLineNum = 1012;BA.debugLine="Try";
try { //BA.debugLineNum = 1014;BA.debugLine="ssql =\"Select ifnull(Ind1,0) from avance where i";
_ssql = "Select ifnull(Ind1,0) from avance where idPalabra = (select max(idpalabra) from avance)";
 //BA.debugLineNum = 1015;BA.debugLine="Cursor1 = xBaseSql.ExecQuery(ssql)";
_cursor1.setObject((android.database.Cursor)(_xbasesql.ExecQuery(_ssql)));
 //BA.debugLineNum = 1016;BA.debugLine="If Cursor1.RowCount >0 Then";
if (_cursor1.getRowCount()>0) { 
 //BA.debugLineNum = 1017;BA.debugLine="Cursor1.Position = 0";
_cursor1.setPosition((int) (0));
 //BA.debugLineNum = 1018;BA.debugLine="iRet = Cursor1.GetInt2(0)";
_iret = _cursor1.GetInt2((int) (0));
 };
 //BA.debugLineNum = 1020;BA.debugLine="Cursor1.Close";
_cursor1.Close();
 } 
       catch (Exception e11) {
			(_ba.processBA == null ? _ba : _ba.processBA).setLastException(e11); //BA.debugLineNum = 1022;BA.debugLine="ManejaError(\"Twitter_GetUso\",\"\")";
_vvv7(_ba,"Twitter_GetUso","");
 };
 //BA.debugLineNum = 1024;BA.debugLine="Return iRet";
if (true) return _iret;
 //BA.debugLineNum = 1025;BA.debugLine="End Sub";
return 0;
}
public static boolean  _usuario_sumarneuronas(anywheresoftware.b4a.BA _ba,int _ineuronassumar,anyhwheresoftware.b4a.objects.sqlcipher.SQLCipher _baseusuario) throws Exception{
String _ssql = "";
boolean _bret = false;
 //BA.debugLineNum = 957;BA.debugLine="Sub Usuario_SumarNeuronas (iNeuronasSumar As Int,";
 //BA.debugLineNum = 958;BA.debugLine="Dim sSql As String, bRet As Boolean";
_ssql = "";
_bret = false;
 //BA.debugLineNum = 959;BA.debugLine="Try";
try { //BA.debugLineNum = 961;BA.debugLine="sSql =\"Update Usuario Set Monedas = Monedas + \"";
_ssql = "Update Usuario Set Monedas = Monedas + "+BA.NumberToString(_ineuronassumar);
 //BA.debugLineNum = 962;BA.debugLine="BaseUsuario.ExecNonQuery(sSql)";
_baseusuario.ExecNonQuery(_ssql);
 //BA.debugLineNum = 963;BA.debugLine="bRet = True";
_bret = anywheresoftware.b4a.keywords.Common.True;
 } 
       catch (Exception e7) {
			(_ba.processBA == null ? _ba : _ba.processBA).setLastException(e7); //BA.debugLineNum = 965;BA.debugLine="bRet = False";
_bret = anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 967;BA.debugLine="Return bRet";
if (true) return _bret;
 //BA.debugLineNum = 968;BA.debugLine="End Sub";
return false;
}
public static boolean  _vvvv5(anywheresoftware.b4a.BA _ba,anyhwheresoftware.b4a.objects.sqlcipher.SQLCipher _base) throws Exception{
boolean _bret = false;
String _ssql = "";
anywheresoftware.b4a.sql.SQL.CursorWrapper _cursor1 = null;
 //BA.debugLineNum = 255;BA.debugLine="Public Sub UsuarioExiste(Base As SQLCipher) As Boo";
 //BA.debugLineNum = 256;BA.debugLine="Dim bRet As Boolean=False";
_bret = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 257;BA.debugLine="Dim sSql As String, Cursor1 As Cursor";
_ssql = "";
_cursor1 = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
 //BA.debugLineNum = 258;BA.debugLine="Try";
try { //BA.debugLineNum = 259;BA.debugLine="sSql = \"select ifnull(imei,'') imei, fechaalta f";
_ssql = "select ifnull(imei,'') imei, fechaalta from usuario";
 //BA.debugLineNum = 260;BA.debugLine="Cursor1 = Base.ExecQuery(sSql)";
_cursor1.setObject((android.database.Cursor)(_base.ExecQuery(_ssql)));
 //BA.debugLineNum = 262;BA.debugLine="If Cursor1.RowCount>0 Then";
if (_cursor1.getRowCount()>0) { 
 //BA.debugLineNum = 263;BA.debugLine="Cursor1.Position = 0";
_cursor1.setPosition((int) (0));
 //BA.debugLineNum = 266;BA.debugLine="bRet = True";
_bret = anywheresoftware.b4a.keywords.Common.True;
 };
 } 
       catch (Exception e11) {
			(_ba.processBA == null ? _ba : _ba.processBA).setLastException(e11); //BA.debugLineNum = 269;BA.debugLine="ManejaError(\"UsuarioExiste\",\"Error Usuario\")";
_vvv7(_ba,"UsuarioExiste","Error Usuario");
 };
 //BA.debugLineNum = 272;BA.debugLine="Return bRet";
if (true) return _bret;
 //BA.debugLineNum = 273;BA.debugLine="End Sub";
return false;
}
public static String  _vvvv6(anywheresoftware.b4a.BA _ba,anyhwheresoftware.b4a.objects.sqlcipher.SQLCipher _base,String _simei) throws Exception{
String _ssql = "";
String _sfecha = "";
 //BA.debugLineNum = 240;BA.debugLine="Public Sub UsuarioGrabaImei (Base As SQLCipher, sI";
 //BA.debugLineNum = 241;BA.debugLine="Dim sSql As String, sFecha As String";
_ssql = "";
_sfecha = "";
 //BA.debugLineNum = 242;BA.debugLine="Try";
try { //BA.debugLineNum = 243;BA.debugLine="DateTime.DateFormat =\"yyyy-MM-dd hh:mm:ss\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("yyyy-MM-dd hh:mm:ss");
 //BA.debugLineNum = 244;BA.debugLine="sFecha = DateTime.Date(DateTime.now)";
_sfecha = anywheresoftware.b4a.keywords.Common.DateTime.Date(anywheresoftware.b4a.keywords.Common.DateTime.getNow());
 //BA.debugLineNum = 245;BA.debugLine="sSql = \"Insert into usuario (imei, fechaalta, mo";
_ssql = "Insert into usuario (imei, fechaalta, monedas) values ('"+_simei+"','"+_sfecha+"', 50)";
 //BA.debugLineNum = 246;BA.debugLine="Base.ExecNonQuery (sSql)";
_base.ExecNonQuery(_ssql);
 } 
       catch (Exception e8) {
			(_ba.processBA == null ? _ba : _ba.processBA).setLastException(e8); //BA.debugLineNum = 249;BA.debugLine="ManejaError(\"UsuarioGrabaImei\", \"Error grabando";
_vvv7(_ba,"UsuarioGrabaImei","Error grabando usuario");
 };
 //BA.debugLineNum = 252;BA.debugLine="End Sub";
return "";
}
public static String  _vvvv7(anywheresoftware.b4a.BA _ba,anywheresoftware.b4a.objects.collections.List _xlist) throws Exception{
 //BA.debugLineNum = 183;BA.debugLine="Public Sub VaciarList (xList As List)";
 //BA.debugLineNum = 185;BA.debugLine="xList.clear";
_xlist.Clear();
 //BA.debugLineNum = 187;BA.debugLine="End Sub";
return "";
}
public static int  _vvvv0(anywheresoftware.b4a.BA _ba,anywheresoftware.b4a.objects.ConcreteViewWrapper _maestro,anywheresoftware.b4a.objects.ConcreteViewWrapper _hijo) throws Exception{
int _icentro = 0;
int _ileft = 0;
 //BA.debugLineNum = 1083;BA.debugLine="Sub ViewAlinearCentro(Maestro As View, Hijo As Vie";
 //BA.debugLineNum = 1084;BA.debugLine="Dim iCentro As Int, iLeft As Int";
_icentro = 0;
_ileft = 0;
 //BA.debugLineNum = 1085;BA.debugLine="iCentro = Maestro.left + Maestro.width / 2";
_icentro = (int) (_maestro.getLeft()+_maestro.getWidth()/(double)2);
 //BA.debugLineNum = 1086;BA.debugLine="iLeft = iCentro - (Hijo.Width / 2)";
_ileft = (int) (_icentro-(_hijo.getWidth()/(double)2));
 //BA.debugLineNum = 1087;BA.debugLine="Return iLeft";
if (true) return _ileft;
 //BA.debugLineNum = 1088;BA.debugLine="End Sub";
return 0;
}
public static String  _vvvvv1(anywheresoftware.b4a.BA _ba,anywheresoftware.b4a.objects.ActivityWrapper _xactivity,anywheresoftware.b4a.objects.ConcreteViewWrapper _xview) throws Exception{
 //BA.debugLineNum = 713;BA.debugLine="Sub ViewComoActivity (xActivity As Activity, xView";
 //BA.debugLineNum = 714;BA.debugLine="xView.width = xActivity.Width";
_xview.setWidth(_xactivity.getWidth());
 //BA.debugLineNum = 715;BA.debugLine="xView.Height = xActivity.Height";
_xview.setHeight(_xactivity.getHeight());
 //BA.debugLineNum = 716;BA.debugLine="xView.Top = 0";
_xview.setTop((int) (0));
 //BA.debugLineNum = 717;BA.debugLine="xView.Left = 0";
_xview.setLeft((int) (0));
 //BA.debugLineNum = 718;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvv2(anywheresoftware.b4a.BA _ba,anywheresoftware.b4a.objects.ConcreteViewWrapper _xorigen,anywheresoftware.b4a.objects.ConcreteViewWrapper _xdestino) throws Exception{
 //BA.debugLineNum = 999;BA.debugLine="Sub ViewIgual (xOrigen As View, xDestino As View)";
 //BA.debugLineNum = 1000;BA.debugLine="xDestino.Top = xOrigen.Top";
_xdestino.setTop(_xorigen.getTop());
 //BA.debugLineNum = 1001;BA.debugLine="xDestino.Left = xOrigen.Left";
_xdestino.setLeft(_xorigen.getLeft());
 //BA.debugLineNum = 1002;BA.debugLine="xDestino.Width = xOrigen.Width";
_xdestino.setWidth(_xorigen.getWidth());
 //BA.debugLineNum = 1003;BA.debugLine="xDestino.Height = xOrigen.Height";
_xdestino.setHeight(_xorigen.getHeight());
 //BA.debugLineNum = 1005;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvv3(anywheresoftware.b4a.BA _ba,int _millisekunden) throws Exception{
long _ti = 0L;
 //BA.debugLineNum = 1046;BA.debugLine="Sub Wait(MilliSekunden As Int)";
 //BA.debugLineNum = 1047;BA.debugLine="Dim Ti As Long";
_ti = 0L;
 //BA.debugLineNum = 1048;BA.debugLine="Ti = DateTime.Now + MilliSekunden";
_ti = (long) (anywheresoftware.b4a.keywords.Common.DateTime.getNow()+_millisekunden);
 //BA.debugLineNum = 1049;BA.debugLine="Do While DateTime.Now < Ti";
while (anywheresoftware.b4a.keywords.Common.DateTime.getNow()<_ti) {
 //BA.debugLineNum = 1050;BA.debugLine="DoEvents";
anywheresoftware.b4a.keywords.Common.DoEvents();
 }
;
 //BA.debugLineNum = 1052;BA.debugLine="End Sub";
return "";
}
}
