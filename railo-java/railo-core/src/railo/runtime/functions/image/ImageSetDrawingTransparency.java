package railo.runtime.functions.image;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;
import railo.runtime.op.Caster;

public class ImageSetDrawingTransparency {

	public static String call(PageContext pc, Object name) throws PageException {
		return call(pc, name,1.0);
	}
	public static String call(PageContext pc, Object name, double percent) throws PageException {
		if(name instanceof String) name=pc.getVariable(Caster.toString(name));
		Image img = Image.toImage(name);
		
		if (percent < 0.0 || percent > 100.0)
		    throw new FunctionException(pc,"ImageSetDrawingTransparency",2,"percent","value must be between 0 and 100");
		img.setTranparency((float)percent);
		return null;
	}
}
