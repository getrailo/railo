package railo.runtime.functions.image;


import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.img.Image;
import railo.runtime.type.Struct;

public class ImageInfo implements Function {
	
	public static Struct call(PageContext pc, Object source) throws PageException {
		return Image.createImage(pc, source, true, false,true,null).info();
		
		//if(name instanceof String) name=pc.getVariable(Caster.toString(name));
		//return Image.toImage(name).info();
	}
}
