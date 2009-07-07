package railo.runtime.functions.image;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;
import railo.runtime.op.Caster;

public class ImageBlur {

	public static String call(PageContext pc, Object name) throws PageException {
		return call(pc,name,3d);
	}
	
	public static String call(PageContext pc, Object name, double blurFactor) throws PageException {
		if(name instanceof String) name=pc.getVariable(Caster.toString(name));
		Image img = Image.toImage(name);
		if(blurFactor<3 || blurFactor>10)
			throw new FunctionException(pc,"ImageBlur",2,"blurFactor","invalid value ["+Caster.toString(blurFactor)+"], value have to be between 3 and 10");
		img.blur((int)blurFactor);
		return null;
	}
}
