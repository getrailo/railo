package railo.runtime.functions.image;

import javax.imageio.ImageIO;

import railo.runtime.PageContext;

public class GetWriteableImageFormats {

	public static String call(PageContext pc) {
		return GetReadableImageFormats.format(add(ImageIO.getWriterFormatNames(),"gif"));
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
