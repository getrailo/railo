package railo.runtime.functions.image;

import railo.runtime.PageContext;
import railo.runtime.img.filter.CurvesFilter;

public class ImageFilterCurves {
	public static Object call(PageContext pc) {
		return new CurvesFilter.Curve();
	}
}
