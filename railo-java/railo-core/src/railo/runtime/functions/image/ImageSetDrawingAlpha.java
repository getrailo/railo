package railo.runtime.functions.image;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;
import railo.runtime.op.Caster;

public class ImageSetDrawingAlpha {

	public static String call(PageContext pc, Object name, double alpha) throws PageException {
		if(name instanceof String) name=pc.getVariable(Caster.toString(name));
		Image img = Image.toImage(name);
		
		if(alpha<0 || alpha>1)
			throw new FunctionException(pc,"ImageSetDrawingAlpha",2,"alpha","alpha must be a value between 0 and 1");
			
		
		img.setAlpha((float)alpha);
		return null;
	}
}
