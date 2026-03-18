package com.matetejuego.free.designerscripts;
import anywheresoftware.b4a.objects.TextViewWrapper;
import anywheresoftware.b4a.objects.ImageViewWrapper;
import anywheresoftware.b4a.BA;


public class LS_premium{

public static void LS_general(java.util.LinkedHashMap<String, anywheresoftware.b4a.keywords.LayoutBuilder.ViewWrapperAndAnchor> views, int width, int height, float scale) {
anywheresoftware.b4a.keywords.LayoutBuilder.setScaleRate(0.3);
//BA.debugLineNum = 2;BA.debugLine="AutoScaleAll"[premium/General script]
anywheresoftware.b4a.keywords.LayoutBuilder.scaleAll(views);
//BA.debugLineNum = 4;BA.debugLine="pnlFondo.Width = 100%x"[premium/General script]
views.get("pnlfondo").vw.setWidth((int)((100d / 100 * width)));
//BA.debugLineNum = 5;BA.debugLine="pnlFondo.Height = 100%y"[premium/General script]
views.get("pnlfondo").vw.setHeight((int)((100d / 100 * height)));
//BA.debugLineNum = 8;BA.debugLine="pnlTitulo.Top = 0"[premium/General script]
views.get("pnltitulo").vw.setTop((int)(0d));
//BA.debugLineNum = 9;BA.debugLine="pnlTitulo.Left = 0"[premium/General script]
views.get("pnltitulo").vw.setLeft((int)(0d));
//BA.debugLineNum = 10;BA.debugLine="pnlTitulo.Width = 100%x"[premium/General script]
views.get("pnltitulo").vw.setWidth((int)((100d / 100 * width)));
//BA.debugLineNum = 11;BA.debugLine="pnlTitulo.Height = 25%y"[premium/General script]
views.get("pnltitulo").vw.setHeight((int)((25d / 100 * height)));
//BA.debugLineNum = 12;BA.debugLine="lblTitulo.Width = 100%x"[premium/General script]
views.get("lbltitulo").vw.setWidth((int)((100d / 100 * width)));
//BA.debugLineNum = 13;BA.debugLine="lblTitulo.Height = 25%y"[premium/General script]
views.get("lbltitulo").vw.setHeight((int)((25d / 100 * height)));
//BA.debugLineNum = 14;BA.debugLine="lblTitulo.Text = \"MATETE SUPERLUPER\""[premium/General script]
((TextViewWrapper)views.get("lbltitulo").vw).setText("MATETE SUPERLUPER");
//BA.debugLineNum = 17;BA.debugLine="pnlCosasCopadas.top = pnlTitulo.Top + pnlTitulo.Height"[premium/General script]
views.get("pnlcosascopadas").vw.setTop((int)((views.get("pnltitulo").vw.getTop())+(views.get("pnltitulo").vw.getHeight())));
//BA.debugLineNum = 18;BA.debugLine="pnlCosasCopadas.Width = 100%x"[premium/General script]
views.get("pnlcosascopadas").vw.setWidth((int)((100d / 100 * width)));
//BA.debugLineNum = 19;BA.debugLine="pnlCosasCopadas.Height = 40%y"[premium/General script]
views.get("pnlcosascopadas").vw.setHeight((int)((40d / 100 * height)));
//BA.debugLineNum = 21;BA.debugLine="imgSinPropaganda.Left = 0"[premium/General script]
views.get("imgsinpropaganda").vw.setLeft((int)(0d));
//BA.debugLineNum = 22;BA.debugLine="imgSinPropaganda.Top = 0"[premium/General script]
views.get("imgsinpropaganda").vw.setTop((int)(0d));
//BA.debugLineNum = 23;BA.debugLine="imgSinPropaganda.Width = 20%x"[premium/General script]
views.get("imgsinpropaganda").vw.setWidth((int)((20d / 100 * width)));
//BA.debugLineNum = 24;BA.debugLine="imgSinPropaganda.Height = 20%y"[premium/General script]
views.get("imgsinpropaganda").vw.setHeight((int)((20d / 100 * height)));
//BA.debugLineNum = 25;BA.debugLine="lblSinPropaganda.Left = 20%x"[premium/General script]
views.get("lblsinpropaganda").vw.setLeft((int)((20d / 100 * width)));
//BA.debugLineNum = 26;BA.debugLine="lblSinPropaganda.Top = 0"[premium/General script]
views.get("lblsinpropaganda").vw.setTop((int)(0d));
//BA.debugLineNum = 27;BA.debugLine="lblSinPropaganda.Width = 80%x"[premium/General script]
views.get("lblsinpropaganda").vw.setWidth((int)((80d / 100 * width)));

}
}