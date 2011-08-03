package railo.runtime.functions.image;


import java.io.IOException;

import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.img.Image;
import railo.runtime.op.Caster;

public class ImageWrite implements Function {

	public static String call(PageContext pc, Object name) throws PageException {
		return call(pc, name, null, 0.75,true);
	}

	public static String call(PageContext pc, Object name, String destination) throws PageException {
		return call(pc, name, destination, 0.75,true);
	}
	
	public static String call(PageContext pc, Object name, String destination, double quality) throws PageException {
		return call(pc, name,destination,quality,true);
	}
	
	public static String call(PageContext pc, Object name, String destination, double quality, boolean overwrite) throws PageException {
		if(name instanceof String)
			name=pc.getVariable(Caster.toString(name));
		Image image=Image.toImage(name);
		
		if(quality<0 || quality>1)
			throw new FunctionException(pc,"ImageWrite",3,"quality","value have to be between 0 and 1");
		
		// MUST beide boolschen argumente checken
		if(destination==null) return null;
		try {
			image.writeOut(ResourceUtil.toResourceNotExisting(pc, destination), overwrite , (float)quality);
		} catch (IOException e) {
			throw Caster.toPageException(e);
		}
		return null;
	}
}
