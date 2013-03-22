package railo.runtime.functions.image;

import java.io.IOException;

import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;
import railo.runtime.op.Caster;

public class ImageWriteBase64 {
	
	public static String call(PageContext pc, Object name, String destination, String format) throws PageException {
		return call(pc, name, destination, format,false);
	}
	
	public static String call(PageContext pc, Object name, String destination, String format, boolean inHTMLFormat) throws PageException {
		if(name instanceof String)
			name=pc.getVariable(Caster.toString(name));
		Image image=Image.toImage(name);
		try {
			return image.writeBase64(ResourceUtil.toResourceNotExisting(pc, destination), format, inHTMLFormat);
		} catch (IOException e) {
			throw Caster.toPageException(e);
		}
		
	}
}
