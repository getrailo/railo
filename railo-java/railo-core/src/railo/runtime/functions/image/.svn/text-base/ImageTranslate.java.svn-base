package railo.runtime.functions.image;

import java.awt.RenderingHints;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;
import railo.runtime.op.Caster;

public class ImageTranslate {

	public static String call(PageContext pc, Object name, double xTrans, double yTrans) throws PageException {
		return call(pc, name, xTrans, yTrans,"nearest");
	}
	
	public static String call(PageContext pc, Object name, double xTrans, double yTrans, String strInterpolation) throws PageException {
		if(name instanceof String) name=pc.getVariable(Caster.toString(name));
		Image img = Image.toImage(name);
		
		strInterpolation=strInterpolation.toLowerCase().trim();
		Object interpolation = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
		
		if("nearest".equals(strInterpolation)) 			interpolation = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
		else if("bilinear".equals(strInterpolation)) 	interpolation = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
		else if("bicubic".equals(strInterpolation)) 	interpolation = RenderingHints.VALUE_INTERPOLATION_BICUBIC;
		
		else throw new FunctionException(pc,"ImageTranslate",4,"interpolation","invalid interpolation definition ["+strInterpolation+"], " +
				"valid interpolation values are [nearest,bilinear,bicubic]");
		
		img.translate((int)xTrans, (int)yTrans,interpolation);
		return null;
	}
}
