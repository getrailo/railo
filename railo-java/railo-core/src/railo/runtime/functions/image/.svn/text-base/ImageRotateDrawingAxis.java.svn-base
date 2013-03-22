package railo.runtime.functions.image;


import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.img.Image;
import railo.runtime.op.Caster;

public class ImageRotateDrawingAxis implements Function {

	public static String call(PageContext pc, Object name, double angle) throws PageException {
		if(name instanceof String) name=pc.getVariable(Caster.toString(name));
		Image img = Image.toImage(name);
		
		img.rotateAxis(angle);
		return null;
		
	}

	public static String call(PageContext pc, Object name, double angle, double x) throws PageException {
		return call(pc, name, angle, x, 0);
	}

	public static String call(PageContext pc, Object name, double angle, double x, double y) throws PageException {
		if(name instanceof String) name=pc.getVariable(Caster.toString(name));
		Image img = Image.toImage(name);
		
		img.rotateAxis(angle, x, y);
		return null;
		
	}
}
