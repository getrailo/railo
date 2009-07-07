package railo.runtime.functions.image;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;
import railo.runtime.op.Caster;

public class ImageCrop {

	public static String call(PageContext pc, Object name, double x, double y, double width, double height) throws PageException {
		if(name instanceof String) name=pc.getVariable(Caster.toString(name));
		Image img = Image.toImage(name);
		
		if (width < 0)
		    throw new FunctionException(pc,"ImageCrop",3,"width","width must contain a none negative value");
		if (height < 0)
		    throw new FunctionException(pc,"ImageCrop",4,"height","width must contain a none negative value");
		
		img.crop((float)x, (float)y, (float)width, (float)height);
		return null;
	}
	
}
