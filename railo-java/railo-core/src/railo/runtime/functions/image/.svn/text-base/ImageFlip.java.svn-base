package railo.runtime.functions.image;

import javax.media.jai.operator.TransposeDescriptor;
import javax.media.jai.operator.TransposeType;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;
import railo.runtime.op.Caster;

public class ImageFlip {
	public static String call(PageContext pc, Object name) throws PageException {
		return call(pc,name,"vertical");
	}
	public static String call(PageContext pc, Object name, String strTranspose) throws PageException {
		if(name instanceof String) name=pc.getVariable(Caster.toString(name));
		Image img = Image.toImage(name);
		
		strTranspose=strTranspose.toLowerCase().trim();
		TransposeType transpose = TransposeDescriptor.FLIP_VERTICAL;
		if("vertical".equals(strTranspose)) transpose=TransposeDescriptor.FLIP_VERTICAL;
		else if("horizontal".equals(strTranspose)) transpose=TransposeDescriptor.FLIP_HORIZONTAL;
		else if("diagonal".equals(strTranspose)) transpose=TransposeDescriptor.FLIP_DIAGONAL;
		else if("antidiagonal".equals(strTranspose)) transpose=TransposeDescriptor.FLIP_ANTIDIAGONAL;
		else if("anti diagonal".equals(strTranspose)) transpose=TransposeDescriptor.FLIP_ANTIDIAGONAL;
		else if("anti-diagonal".equals(strTranspose)) transpose=TransposeDescriptor.FLIP_ANTIDIAGONAL;
		else if("anti_diagonal".equals(strTranspose)) transpose=TransposeDescriptor.FLIP_ANTIDIAGONAL;
		else if("90".equals(strTranspose)) transpose=TransposeDescriptor.ROTATE_90;
		else if("180".equals(strTranspose)) transpose=TransposeDescriptor.ROTATE_180;
		else if("270".equals(strTranspose)) transpose=TransposeDescriptor.ROTATE_270;
		else throw new FunctionException(pc,"ImageFlip",2,"transpose","invalid transpose definition ["+strTranspose+"], " +
				"valid transpose values are [vertical,horizontal,diagonal,90,180,270]");
		
		img.flip(transpose);
		return null;
	}
	
}
