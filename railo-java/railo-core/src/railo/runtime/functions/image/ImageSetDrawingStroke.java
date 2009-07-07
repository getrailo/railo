package railo.runtime.functions.image;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;
import railo.runtime.op.Caster;
import railo.runtime.type.Struct;

public class ImageSetDrawingStroke {

	public static String call(PageContext pc, Object name, Struct attributeCollection) throws PageException {
		if(name instanceof String) name=pc.getVariable(Caster.toString(name));
		Image img = Image.toImage(name);
		
		
		img.setDrawingStroke(attributeCollection);
		return null;
	}
}
