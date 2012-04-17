package railo.runtime.functions.image;


import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.img.Image;
import railo.runtime.op.Caster;

public class ImageResize implements Function {

	public static String call(PageContext pc, Object name,String width, String height) throws PageException {
		return call(pc, name, width, height, "highestQuality", 1.0);
	}

	public static String call(PageContext pc, Object name,String width, String height,String interpolation) throws PageException {
		return call(pc, name, width, height, interpolation, 1.0);
	}
	
	
	public static String call(PageContext pc, Object name,String width, String height,String interpolation, double blurFactor) throws PageException {
		// image
		if(name instanceof String)
			name=pc.getVariable(Caster.toString(name));
		Image image=Image.toImage(name);
		
		interpolation = interpolation.toLowerCase().trim();
		
		
		if (blurFactor <= 0.0 || blurFactor > 10.0) 
			throw new FunctionException(pc,"ImageResize",5,"blurFactor","argument blurFactor must be between 0 and 10");
			
		
		// MUST interpolation/blur
		//if(!"highestquality".equals(interpolation) || blurFactor!=1.0)throw new ExpressionException("argument interpolation and blurFactor are not supported for function ImageResize");
		
		image.resize(width,height,interpolation,blurFactor);
		return null;
	}
	
	/*private static int toDimension(String label, String dimension) throws PageException {
		if(StringUtil.isEmpty(dimension)) return -1;
		dimension=dimension.trim();
		// int value
		int i=Caster.toIntValue(dimension,-1);
		if(i>-1) return i;
		throw new ExpressionException("attribute ["+label+"] value has an invalid value ["+dimension+"]"); 
	}*/
}
