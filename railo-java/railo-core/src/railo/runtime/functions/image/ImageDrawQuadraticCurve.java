package railo.runtime.functions.image;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;

public class ImageDrawQuadraticCurve {
	
	public static String call(PageContext pc, Object name, 
			double x1, double y1,
			double ctrlx, double ctrly, 
			double x2, double y2) throws PageException {
		//if(name instanceof String) name=pc.getVariable(Caster.toString(name));
		Image img = Image.toImage(pc,name);
		
		img.drawQuadraticCurve(x1, y1, ctrlx, ctrly, x2, y2);
		return null;
	}
}
