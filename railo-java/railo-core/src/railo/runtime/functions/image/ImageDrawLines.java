package railo.runtime.functions.image;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;

public class ImageDrawLines {

	public static String call(PageContext pc, Object name, Array xcoords, Array ycoords) throws PageException {
		return call(pc, name, xcoords, ycoords, false, false);
	}

	public static String call(PageContext pc, Object name, Array xcoords, Array ycoords, boolean isPolygon) throws PageException {
		return call(pc, name, xcoords, ycoords, isPolygon, false);
	}

	public static String call(PageContext pc, Object name, Array xcoords, Array ycoords, boolean isPolygon, boolean filled) throws PageException {
		if(name instanceof String) name=pc.getVariable(Caster.toString(name));
		Image img = Image.toImage(name);
		
		if(xcoords.size()!=ycoords.size())
			throw new ExpressionException("xcoords and ycoords has not the same size");
		img.drawLines(toIntArray(xcoords), toIntArray(ycoords), isPolygon, filled);
		return null;
	}

	private static int[] toIntArray(Array arr) throws PageException {
		int[] iarr=new int[arr.size()];
		for(int i=0;i<iarr.length;i++) {
			iarr[i]=Caster.toIntValue(arr.getE(i+1));
		}
		return iarr;
	}
	
}
