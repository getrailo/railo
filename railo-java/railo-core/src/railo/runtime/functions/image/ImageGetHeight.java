package railo.runtime.functions.image;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;

public class ImageGetHeight {

	@Deprecated
	public static double call(PageContext pc, Object name) throws PageException {
		return Image.toImage(pc,name).getHeight();
	}
	
	public static double call(PageContext pc, Image img) throws PageException {
		return img.getHeight();
	}
	
}
