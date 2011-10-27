package railo.runtime.functions.image;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.img.filter.WarpGrid;
import railo.runtime.op.Caster;

public class ImageFilterWarpGrid {
	
	public static Object call(PageContext pc, double rows, double cols, double width, double height) throws PageException {
		return new WarpGrid(Caster.toIntValue(rows), Caster.toIntValue(cols), Caster.toIntValue(width), Caster.toIntValue(height));
	}
}
