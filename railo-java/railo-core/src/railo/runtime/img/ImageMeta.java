package railo.runtime.img;


import java.io.PrintWriter;

import railo.commons.io.res.Resource;
import railo.commons.lang.SystemOut;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.type.Struct;

public class ImageMeta {

	/**
	 */
	/**
	 * adds information about a image to the given struct
	 * @param format
	 * @param res
	 * @param info
	 */
	public static void addInfo(String format, Resource res, Struct info)  {
		try{
			ImageMetaDrew.test();
		}
		catch(Throwable t) {
			PrintWriter pw = ThreadLocalPageContext.getConfig().getErrWriter();
			SystemOut.printDate(pw, "cannot load addional pic info, library metadata-extractor.jar is missed"); 
		}
		try{
			ImageMetaDrew.addInfo(format, res, info);
		}
		catch(Throwable t) {}
	}

	

}
