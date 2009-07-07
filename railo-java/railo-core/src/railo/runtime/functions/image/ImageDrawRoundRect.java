package railo.runtime.functions.image;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;
import railo.runtime.op.Caster;

public class ImageDrawRoundRect {

	public static String call(PageContext pc, Object name, double x, double y, double width,double height,
			double arcWidth, double arcHeight) throws PageException {
		return call(pc, name, x, y, width, height,arcWidth,arcHeight,false);
	}
	public static String call(PageContext pc, Object name, double x, double y, double width,double height, 
			double arcWidth, double arcHeight, boolean filled) throws PageException {
		if(name instanceof String) name=pc.getVariable(Caster.toString(name));
		Image img = Image.toImage(name);
		
		if (width < 0)
		    throw new FunctionException(pc,"ImageDrawRoundRect",3,"width","width must contain a none negative value");
		if (height < 0)
		    throw new FunctionException(pc,"ImageDrawRoundRect",4,"height","width must contain a none negative value");
		if (arcWidth < 0)
		    throw new FunctionException(pc,"ImageDrawRoundRect",5,"arcWidth","arcWidth must contain a none negative value");
		if (arcHeight < 0)
		    throw new FunctionException(pc,"ImageDrawRoundRect",6,"arcHeight","arcHeight must contain a none negative value");
		
		img.drawRoundRect((int)x, (int)y, (int)width, (int)height,(int)arcWidth,(int)arcHeight, filled);
		return null;
	}
	
}
