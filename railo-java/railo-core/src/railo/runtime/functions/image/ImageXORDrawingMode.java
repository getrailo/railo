package railo.runtime.functions.image;

import railo.commons.color.ColorCaster;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;

public class ImageXORDrawingMode {

	public static String call(PageContext pc, Object name, String color) throws PageException {
		//if(name instanceof String) name=pc.getVariable(Caster.toString(name));
		Image img = Image.toImage(pc,name);
		
		img.setXorMode(ColorCaster.toColor(color));
		return null;
	}
}
