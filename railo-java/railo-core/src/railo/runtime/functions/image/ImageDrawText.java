package railo.runtime.functions.image;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;
import railo.runtime.op.Caster;
import railo.runtime.type.Struct;

public class ImageDrawText {

	public static String call(PageContext pc, Object name, String str,double x, double y) throws PageException {
		return call(pc, name, str,x, y, null);
	}
	public static String call(PageContext pc, Object name, String str,double x, double y, Struct ac) throws PageException {
		if(name instanceof String) name=pc.getVariable(Caster.toString(name));
		Image img = Image.toImage(name);
		
		img.drawString(str, (int)x, (int)y, ac);
		return null;
	}
	
}
