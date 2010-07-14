package railo.runtime.functions.image;


import javax.media.jai.BorderExtender;

import railo.commons.color.ColorCaster;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.img.Image;
import railo.runtime.op.Caster;

public class ImageAddBorder implements Function {
	public static String call(PageContext pc, Object name) throws PageException {
		return call(pc,name,1D,"black","constant");
	}
	
	public static String call(PageContext pc, Object name, double thickness) throws PageException {
		return call(pc,name,thickness,"black","constant");
	}
	
	public static String call(PageContext pc, Object name, double thickness, String color) throws PageException {
		return call(pc,name,thickness,color,"constant");
	}

	public static String call(PageContext pc, Object name, double thickness, String color, String strBorderType) throws PageException {
		if(name instanceof String)
			name=pc.getVariable(Caster.toString(name));
		strBorderType=strBorderType.trim().toLowerCase();
		int borderType=Image.BORDER_TYPE_CONSTANT;
		if("zero".equals(strBorderType))			borderType=BorderExtender.BORDER_ZERO;
		else if("constant".equals(strBorderType))	borderType=Image.BORDER_TYPE_CONSTANT;
		else if("copy".equals(strBorderType))		borderType=BorderExtender.BORDER_COPY;
		else if("reflect".equals(strBorderType))	borderType=BorderExtender.BORDER_REFLECT;
		else if("wrap".equals(strBorderType))		borderType=BorderExtender.BORDER_WRAP;
    	
		Image image=Image.toImage(name);
		image.addBorder((int)thickness,ColorCaster.toColor(color),borderType);
		
		
		return null;
	}
}
