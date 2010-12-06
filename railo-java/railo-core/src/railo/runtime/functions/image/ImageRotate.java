package railo.runtime.functions.image;


import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.img.Image;
import railo.runtime.op.Caster;

public class ImageRotate implements Function {
	
	public static String call(PageContext pc, Object name, String angle) throws PageException {
		return _call(pc, name,-1F,-1F,Caster.toFloatValue(angle),"nearest");
	}
	
	public static String call(PageContext pc, Object name, String angle, String strInterpolation) throws PageException {
		return _call(pc, name,-1F,-1F,Caster.toFloatValue(angle),strInterpolation);
	}
	
	public static String call(PageContext pc, Object name, String x, String y, String angle) throws PageException {
		return _call(pc, name,Caster.toFloatValue(x),Caster.toFloatValue(y),Caster.toFloatValue(angle),"nearest");
	}

	public static String call(PageContext pc, Object name, String x, String y, String angle, String strInterpolation) throws PageException {
		return _call(pc, name,Caster.toFloatValue(x),Caster.toFloatValue(y),Caster.toFloatValue(angle),strInterpolation);
	}

	private static String _call(PageContext pc, Object name, float x, float y, float angle, String strInterpolation) throws PageException {
		if(name instanceof String) name=pc.getVariable(Caster.toString(name));
		Image img = Image.toImage(name);
		strInterpolation=strInterpolation.trim().toLowerCase();
		int interpolation;
		if("nearest".equals(strInterpolation)) interpolation=railo.runtime.img.Image.INTERPOLATION_NEAREST;
		else if("bilinear".equals(strInterpolation)) interpolation=railo.runtime.img.Image.INTERPOLATION_BILINEAR;
		else if("bicubic".equals(strInterpolation)) interpolation=railo.runtime.img.Image.INTERPOLATION_BICUBIC;
		else if("none".equals(strInterpolation)) interpolation=railo.runtime.img.Image.INTERPOLATION_NONE;
		else throw new ExpressionException("invalid interpolation definition ["+strInterpolation+"]," +
				" valid values are [nearest,bilinear,bicubic]");
		
		img.rotate(x,y,angle,interpolation);
		return null;
		
	}
}
