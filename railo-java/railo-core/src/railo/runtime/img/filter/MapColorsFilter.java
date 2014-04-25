/*
*

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package railo.runtime.img.filter;import java.awt.image.BufferedImage;

import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.img.ImageUtil;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.util.CollectionUtil;



/**
 * A filter which replaces one color by another in an image. This is frankly, not often useful, but has its occasional
 * uses when dealing with GIF transparency and the like.
 */
public class MapColorsFilter extends PointFilter  implements DynFiltering {

	private int oldColor;
	private int newColor;
	
	/**
     * Construct a MapColorsFilter.
     */
    public MapColorsFilter() {
		this( 0xffffffff, 0xff000000 );
	}
	
	/**
     * Construct a MapColorsFilter.
     * @param oldColor the color to replace
     * @param newColor the color to replace it with
     */
	public MapColorsFilter(int oldColor, int newColor) {
		canFilterIndexColorModel = true;
		this.oldColor = oldColor;
		this.newColor = newColor;
	}

	@Override
	public int filterRGB(int x, int y, int rgb) {
		if (rgb == oldColor)
			return newColor;
		return rgb;
	}
	@Override
	public BufferedImage filter(BufferedImage src, Struct parameters) throws PageException {BufferedImage dst=ImageUtil.createBufferedImage(src);
		Object o;
		if((o=parameters.removeEL(KeyImpl.init("Dimensions")))!=null){
			int[] dim=ImageFilterUtil.toDimensions(o,"Dimensions");
			setDimensions(dim[0],dim[1]);
		}

		// check for arguments not supported
		if(parameters.size()>0) {
			throw new FunctionException(ThreadLocalPageContext.get(), "ImageFilter", 3, "parameters", "the parameter"+(parameters.size()>1?"s":"")+" ["+CollectionUtil.getKeyList(parameters,", ")+"] "+(parameters.size()>1?"are":"is")+" not allowed, only the following parameters are supported [Dimensions]");
		}

		return filter(src, dst);
	}
}

