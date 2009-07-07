package railo.runtime.functions.image;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;
import railo.runtime.op.Caster;

public class ImageSharpen {

	public static String call(PageContext pc, Object name) throws PageException {
		return call(pc, name,1.0);
	}
	public static String call(PageContext pc, Object name, double gain) throws PageException {
		if(name instanceof String) name=pc.getVariable(Caster.toString(name));
		Image img = Image.toImage(name);
		
		if (gain < -1.0 || gain > 2.0)
		    throw new FunctionException(pc,"ImageSharpen",2,"gain","value must be between 1 and 2");
		img.sharpen((float)gain);
		return null;
	}
}
