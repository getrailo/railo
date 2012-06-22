package railo.runtime.functions.image;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;
import railo.runtime.op.Caster;

public class ImageDrawImage {
	
	public static String call(PageContext pc, Object name, Object image,double x, double y) throws PageException {
		if(name instanceof String) name=pc.getVariable(Caster.toString(name));
		Image img = Image.toImage(name);
		
		
		img.drawImage(Image.createImage(pc, image, true, false,true,null), (int)x, (int)y);
		return null;
	}
	
}
