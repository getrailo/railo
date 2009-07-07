package railo.runtime.functions.image;


import java.io.IOException;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.img.Image;
import railo.runtime.op.Caster;

public class ImageReadBase64 implements Function {
	
	public static Object call(PageContext pc, String source) throws PageException {
		try {
			return new Image(source);
		} catch (IOException e) {
			throw Caster.toPageException(e);
		}
	}
}
