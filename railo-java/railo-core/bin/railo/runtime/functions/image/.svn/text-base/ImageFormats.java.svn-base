package railo.runtime.functions.image;

import java.util.HashSet;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

public class ImageFormats {

	public static Struct call(PageContext pc) throws PageException {
		StructImpl sct=new StructImpl();
		sct.set("decoder", toArray(Image.getReaderFormatNames()));
		sct.set("encoder", toArray(Image.getWriterFormatNames()));
		
		return sct;
	}

	private static Object toArray(String[] arr) {
		HashSet set=new HashSet();
		for(int i=0;i<arr.length;i++) {
			set.add(arr[i].toUpperCase());
		}
		
		return set.toArray(new String[set.size()]);
	}
}
