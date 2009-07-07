package railo.runtime.functions.image;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;
import railo.runtime.op.Caster;

public class ImageShearDrawingAxis {
	
	public static String call(PageContext pc, Object name, double shx, double shy) throws PageException {
		if(name instanceof String) name=pc.getVariable(Caster.toString(name));
		Image img = Image.toImage(name);
		
		
		img.shearAxis(shx, shy);
		return null;
	}
}
