package railo.runtime.functions.image;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;
import railo.runtime.op.Caster;

public class ImageDrawArc {

	public static String call(PageContext pc, Object name, double x, double y, double width, double height
			, double startAngle, double arcAngle) throws PageException {
		return call(pc, name, x, y, width, height, startAngle, arcAngle, false);
	}
	public static String call(PageContext pc, Object name, double x, double y, double width, double height
			, double startAngle, double arcAngle , boolean filled) throws PageException {
		if(name instanceof String) name=pc.getVariable(Caster.toString(name));
		Image img = Image.toImage(name);
		
		if (width < 0)
		    throw new FunctionException(pc,"ImageDrawArc",3,"width","width must contain a none negative value");
		if (height < 0)
		    throw new FunctionException(pc,"ImageDrawArc",4,"height","width must contain a none negative value");
		
		img.drawArc((int)x, (int)y, (int)width, (int)height, (int)startAngle, (int)arcAngle, filled);
		return null;
	}
	
}
