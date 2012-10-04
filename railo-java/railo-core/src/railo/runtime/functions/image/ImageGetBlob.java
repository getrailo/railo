package railo.runtime.functions.image;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;

public class ImageGetBlob {

	public static Object call(PageContext pc, Object source) throws PageException {
		//if(name instanceof String) name=pc.getVariable(Caster.toString(name));
		//return Image.toImage(name).getByteArray();
		return Image.createImage(pc, source, true,true,true,null).getImageBytes(null);
	}
	
}
