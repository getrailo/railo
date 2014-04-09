package railo.runtime.functions.image;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;

public class ImageTranslateDrawingAxis {

	public static String call(PageContext pc, Object name, double x, double y) throws PageException {
		//if(name instanceof String) name=pc.getVariable(Caster.toString(name));
		Image img = Image.toImage(pc,name);
		
		img.translateAxis((int)x, (int)y);
		return null;
	}
}
