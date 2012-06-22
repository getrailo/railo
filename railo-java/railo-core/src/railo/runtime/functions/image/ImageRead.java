package railo.runtime.functions.image;


import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.img.Image;

public class ImageRead implements Function {
	
	public static Object call(PageContext pc, Object source) throws PageException {
		return Image.createImage(pc, source, true, true,true,null);
	}
}
