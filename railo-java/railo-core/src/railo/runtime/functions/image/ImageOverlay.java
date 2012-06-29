package railo.runtime.functions.image;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;
import railo.runtime.op.Caster;

public class ImageOverlay {
	
	public static String call(PageContext pc, Object src1, Object src2) throws PageException {
		if(src1 instanceof String) src1=pc.getVariable(Caster.toString(src1));
		//if(src2 instanceof String) src2=pc.getVariable(Caster.toString(src2));
		
		Image.toImage(src1).overlay(Image.createImage(pc, src2,true,false,true,null));
		return null;
	}
}
