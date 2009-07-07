package railo.runtime.functions.image;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;
import railo.runtime.op.Caster;

public class ImageDrawCubicCurve {
	
	public static String call(PageContext pc, Object name, 
			double x1, double y1,
			double ctrlx1, double ctrly1, 
			double ctrlx2, double ctrly2,
			double x2, double y2) throws PageException {
		if(name instanceof String) name=pc.getVariable(Caster.toString(name));
		Image img = Image.toImage(name);
		
		img.drawCubicCurve(ctrlx1, ctrly1, ctrlx2, ctrly2, x1, y1, x2, y2);
		return null;
	}
}
