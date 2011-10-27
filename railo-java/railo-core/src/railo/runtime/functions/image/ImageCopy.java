package railo.runtime.functions.image;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;
import railo.runtime.op.Caster;

public class ImageCopy {

	public static Object call(PageContext pc, Object name, double x, double y, double width, double height) throws PageException {
		if(name instanceof String) name=pc.getVariable(Caster.toString(name));
		Image img = Image.toImage(name);
		
		if (width < 0)
		    throw new FunctionException(pc,"ImageCopy",3,"width","width must contain a none negative value");
		if (height < 0)
		    throw new FunctionException(pc,"ImageCopy",4,"height","width must contain a none negative value");
		
		return img.copy((float)x, (float)y, (float)width, (float)height);
	}

	public static Object call(PageContext pc, Object name, double x, double y, double width, double height,double dx) throws PageException {
		throw new FunctionException(pc,"ImageCopy",7,"dy","when you define dx, you have also to define dy");
	}

	public static Object call(PageContext pc, Object name, double x, double y, double width, double height, double dx, double dy) throws PageException {
		if(dx==-999 && dy==-999){// -999 == default value for named argument
			return call(pc, name, x, y, width, height);
		}
		if(dx==-999){// -999 == default value for named argument
			throw new FunctionException(pc,"ImageCopy",6,"dx","when you define dy, you have also to define dx");
		}
		if(dy==-999){// -999 == default value for named argument
			throw new FunctionException(pc,"ImageCopy",7,"dy","when you define dx, you have also to define dy");
		}
		
		
		if(name instanceof String) name=pc.getVariable(Caster.toString(name));
		Image img = Image.toImage(name);
		
		if (width < 0)
		    throw new FunctionException(pc,"ImageCopy",3,"width","width must contain a none negative value");
		if (height < 0)
		    throw new FunctionException(pc,"ImageCopy",4,"height","width must contain a none negative value");
		
		return img.copy((float)x, (float)y, (float)width, (float)height, (float)dx, (float)dy);
		//return null;
	}	
}
