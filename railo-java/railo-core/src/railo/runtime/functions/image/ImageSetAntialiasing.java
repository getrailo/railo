package railo.runtime.functions.image;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;
import railo.runtime.op.Caster;

public class ImageSetAntialiasing {

	public static String call(PageContext pc, Object name) throws PageException {
		return call(pc, name,"on");
	}
	public static String call(PageContext pc, Object name, String strAntialias) throws PageException {
		if(name instanceof String) name=pc.getVariable(Caster.toString(name));
		Image img = Image.toImage(name);
		
		strAntialias=strAntialias.trim().toLowerCase();
		boolean antialias;
		if("on".equals(strAntialias))antialias=true;
		else if("off".equals(strAntialias))antialias=false;
		else antialias=Caster.toBooleanValue(strAntialias);
		
		img.setAntiAliasing(antialias);
		return null;
	}
}
