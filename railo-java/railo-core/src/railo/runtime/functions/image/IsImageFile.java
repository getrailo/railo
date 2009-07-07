package railo.runtime.functions.image;

import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.PageContext;
import railo.runtime.img.Image;

public class IsImageFile {

	public static boolean call(PageContext pc, String path) {
		try {
			new Image(ResourceUtil.toResourceExisting(pc, path));
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
