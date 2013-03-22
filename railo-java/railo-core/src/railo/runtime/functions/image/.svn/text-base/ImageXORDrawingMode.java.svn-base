package railo.runtime.functions.image;

import railo.commons.color.ColorCaster;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;
import railo.runtime.op.Caster;

public class ImageXORDrawingMode {

	public static String call(PageContext pc, Object name, String color) throws PageException {
		if(name instanceof String) name=pc.getVariable(Caster.toString(name));
		Image img = Image.toImage(name);
		
		img.setXorMode(ColorCaster.toColor(color));
		return null;
	}
}
