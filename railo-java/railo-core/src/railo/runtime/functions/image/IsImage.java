package railo.runtime.functions.image;

import railo.runtime.PageContext;
import railo.runtime.img.Image;

public class IsImage {

	public static boolean call(PageContext pc, Object name) {
		return Image.isImage(name);
	}
}
