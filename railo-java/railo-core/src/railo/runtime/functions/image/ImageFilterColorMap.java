package railo.runtime.functions.image;

import java.awt.Color;

import railo.commons.color.ColorCaster;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.img.filter.GrayscaleColormap;
import railo.runtime.img.filter.LinearColormap;
import railo.runtime.img.filter.SpectrumColormap;

public class ImageFilterColorMap {
	public static Object call(PageContext pc, String type) throws PageException {
		return call(pc, type, null, null);
	}
	public static Object call(PageContext pc, String type, String lineColor1) throws PageException {
		return call(pc, type, lineColor1, null);
		
	}
	public static Object call(PageContext pc, String type, String lineColor1,String lineColor2) throws PageException {
		type=type.toLowerCase().trim();
		
		if("grayscale".equals(type)) return new GrayscaleColormap();
		else if("spectrum".equals(type)) return new SpectrumColormap();
		else if("linear".equals(type)) {
			boolean isEmpty1=StringUtil.isEmpty(lineColor1);
			boolean isEmpty2=StringUtil.isEmpty(lineColor2);
			
			if(isEmpty1 && isEmpty2) return new LinearColormap();
			else if(!isEmpty1 && !isEmpty2) {
				Color color1 = ColorCaster.toColor(lineColor1);
				Color color2 = ColorCaster.toColor(lineColor2);
				return new LinearColormap(color1.getRGB(),color2.getRGB());
			}
			else 
				throw new FunctionException(pc, "ImageFilterColorMap", 2, "lineColor1", "when you define linecolor1 you have to define linecolor2 as well");
				
		}
		else throw new FunctionException(pc, "ImageFilterColorMap", 1, "type", "invalid type defintion, valid types are [grayscale,spectrum,linear]");
		
		
		
		
	}
}
