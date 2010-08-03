package railo.runtime.functions.image;

import java.awt.RenderingHints;

import javax.media.jai.operator.ShearDescriptor;
import javax.media.jai.operator.ShearDir;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;
import railo.runtime.op.Caster;

public class ImageShear {
	public static String call(PageContext pc, Object name, double shear) throws PageException {
		return call(pc, name, shear, "horizontal", "nearest");
	}
	
	public static String call(PageContext pc, Object name, double shear, String direction) throws PageException {
		return call(pc, name, shear, direction,"nearest");
	}
	
	public static String call(PageContext pc, Object name, double shear, String strDirection, String strInterpolation) throws PageException {
		if(name instanceof String) name=pc.getVariable(Caster.toString(name));
		Image img = Image.toImage(name);
		
		// direction
		strDirection=strDirection.toLowerCase().trim();
		ShearDir direction;
		if("horizontal".equals(strDirection)) 			direction = ShearDescriptor.SHEAR_HORIZONTAL;
		else if("vertical".equals(strDirection)) 		direction = ShearDescriptor.SHEAR_VERTICAL;
		else throw new FunctionException(pc,"ImageShear",3,"direction","invalid direction definition ["+strDirection+"], " +
			"valid direction values are [horizontal,vertical]");

		// interpolation
		strInterpolation=strInterpolation.toLowerCase().trim();
		Object interpolation = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
		if("nearest".equals(strInterpolation)) 			interpolation = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
		else if("bilinear".equals(strInterpolation)) 	interpolation = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
		else if("bicubic".equals(strInterpolation)) 	interpolation = RenderingHints.VALUE_INTERPOLATION_BICUBIC;
		else throw new FunctionException(pc,"ImageTranslate",4,"interpolation","invalid interpolation definition ["+strInterpolation+"], " +
				"valid interpolation values are [nearest,bilinear,bicubic]");
		
		img.shear((float)shear, direction, interpolation);
		return null;
	}
}
