package railo.runtime.functions.image;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;
import railo.runtime.op.Caster;

public class ImageScaleToFit {
	
	public static String call(PageContext pc, Object name,String fitWidth, String fitHeight) throws PageException {
		return call(pc, name, fitWidth, fitHeight, "highestQuality",1.0);
	}
	
	public static String call(PageContext pc, Object name,String fitWidth, String fitHeight, String interpolation) throws PageException {
		return call(pc, name, fitWidth, fitHeight, interpolation,1.0);
	}
	
	public static String call(PageContext pc, Object name, String fitWidth, String fitHeight, String strInterpolation, double blurFactor) throws PageException {
		if(name instanceof String) name=pc.getVariable(Caster.toString(name));
		Image img = Image.toImage(name);
		img.scaleToFit(fitWidth, fitHeight, strInterpolation, blurFactor);
		return null;
	}
}
