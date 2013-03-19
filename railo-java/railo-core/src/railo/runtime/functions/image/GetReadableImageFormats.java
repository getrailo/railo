package railo.runtime.functions.image;

import java.util.Arrays;
import java.util.HashSet;

import railo.runtime.PageContext;
import railo.runtime.img.ImageUtil;
import railo.runtime.type.util.ListUtil;

public class GetReadableImageFormats {

	public static String call(PageContext pc) {
		return format(ImageUtil.getReaderFormatNames());
		
	}

	public static String format(String[] formats) {
		HashSet set=new HashSet();
		for(int i=0;i<formats.length;i++) {
			set.add(formats[i].toUpperCase());
		}
		formats=(String[]) set.toArray(new String[set.size()]);
		Arrays.sort(formats);
		return ListUtil.arrayToList(formats, ",").toUpperCase();
	}
}
