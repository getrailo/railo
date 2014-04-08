package railo.runtime.functions.image;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;

public class ImageClearRect {

	public static String call(PageContext pc, Object name, double x, double y, double width, double height) throws PageException {
		//if(name instanceof String) name=pc.getVariable(Caster.toString(name));
		Image img = Image.toImage(pc,name);
		

		if (width < 0)
		    throw new FunctionException(pc,"ImageClearRect",3,"width","width must contain a none negative value");
		if (height < 0)
		    throw new FunctionException(pc,"ImageClearRect",4,"height","width must contain a none negative value");
		
		img.clearRect((int)x, (int)y, (int)width, (int)height);
		return null;
	}
	
}
