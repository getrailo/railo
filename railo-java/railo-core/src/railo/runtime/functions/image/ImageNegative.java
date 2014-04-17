package railo.runtime.functions.image;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;

public class ImageNegative {
	
	public static String call(PageContext pc, Object name) throws PageException {
		//if(name instanceof String) name=pc.getVariable(Caster.toString(name));
		
		Image.toImage(pc,name).invert();
		return null;
	}
}
