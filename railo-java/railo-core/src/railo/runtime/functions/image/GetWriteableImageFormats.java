package railo.runtime.functions.image;

import railo.runtime.PageContext;
import railo.runtime.img.ImageUtil;

public class GetWriteableImageFormats {

	public static String call(PageContext pc) {
		return GetReadableImageFormats.format(add(ImageUtil.getWriterFormatNames(),"gif"));
	}

	private static String[] add(String[] formats, String value) {
		String[] rtn=new String[formats.length+1];
		for(int i=0;i<formats.length;i++) {
			rtn[i]=formats[i];
		}
		rtn[formats.length]=value;
		return rtn;
	}
}
