package railo.runtime.functions.image;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.img.filter.CurvesFilter;

public class ImageFilterCurves {
	public static Object call(PageContext pc) throws PageException {
		return new CurvesFilter.Curve();
	}
}
