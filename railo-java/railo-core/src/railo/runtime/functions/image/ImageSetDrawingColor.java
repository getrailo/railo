package railo.runtime.functions.image;

import java.awt.Color;

import railo.commons.color.ColorCaster;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;
import railo.runtime.op.Caster;

public class ImageSetDrawingColor {

	public static String call(PageContext pc, Object name, String strColor) throws PageException {
		if(name instanceof String) name=pc.getVariable(Caster.toString(name));
		Image img = Image.toImage(name);
		
		Color color = ColorCaster.toColor(strColor);
		
		img.setColor(color);
		return null;
	}
}
