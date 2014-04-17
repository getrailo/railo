package railo.runtime.functions.image;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;

public class ImageDrawRect {

	public static String call(PageContext pc, Object name, double x, double y, double width,double height) throws PageException {
		return call(pc, name, x, y, width, height,false);
	}
	public static String call(PageContext pc, Object name, double x, double y, double width,double height, boolean filled) throws PageException {
		//if(name instanceof String) name=pc.getVariable(Caster.toString(name));
		Image img = Image.toImage(pc,name);
		
		if (width < 0)
		    throw new FunctionException(pc,"ImageDrawRect",3,"width","width must contain a none negative value");
		if (height < 0)
		    throw new FunctionException(pc,"ImageDrawRect",4,"height","width must contain a none negative value");
		
		img.drawRect((int)x, (int)y, (int)width, (int)height, filled);
		return null;
	}
	
}
