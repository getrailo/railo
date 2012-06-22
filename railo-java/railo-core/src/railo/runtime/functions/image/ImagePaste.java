package railo.runtime.functions.image;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;
import railo.runtime.op.Caster;

public class ImagePaste {

	public static String call(PageContext pc, Object src1, Object src2) throws PageException {
		return call(pc, src1, src2, 0,0);
	}
	
	public static String call(PageContext pc, Object src1, Object src2, double x) throws PageException {
		return call(pc, src1, src2, x,0);
	}
	
	public static String call(PageContext pc, Object src1, Object src2, double x, double y) throws PageException {
		if(src1 instanceof String) src1=pc.getVariable(Caster.toString(src1));
		//if(src2 instanceof String) src2=pc.getVariable(Caster.toString(src2));
		
		Image.toImage(src1).paste(Image.createImage(pc, src2,true,false,true,null),(int)x,(int)y);
		return null;
	}
}
