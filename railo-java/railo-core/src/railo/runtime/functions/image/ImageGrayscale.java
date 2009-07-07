package railo.runtime.functions.image;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;
import railo.runtime.op.Caster;

public class ImageGrayscale {
	
	public static String call(PageContext pc, Object name) throws PageException {
		if(name instanceof String) name=pc.getVariable(Caster.toString(name));
		
		Image.toImage(name).grayscale();
		return null;
	}
}
