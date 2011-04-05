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

package railo.runtime.img.filter;import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.img.ImageUtil;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.List;
import railo.runtime.type.Struct;

/**
 * A filter which performs one round of the game of Life on an image.
 */
public class LifeFilter extends BinaryFilter  implements DynFiltering {

	public LifeFilter() {
	}

	protected int[] filterPixels( int width, int height, int[] inPixels, Rectangle transformedSpace ) {
		int index = 0;
		int[] outPixels = new int[width * height];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int r = 0, g = 0, b = 0;
				int pixel = inPixels[y*width+x];
				int a = pixel & 0xff000000;
				int neighbours = 0;

				for (int row = -1; row <= 1; row++) {
					int iy = y+row;
					int ioffset;
					if (0 <= iy && iy < height) {
						ioffset = iy*width;
						for (int col = -1; col <= 1; col++) {
							int ix = x+col;
							if (!(row == 0 && col == 0) && 0 <= ix && ix < width) {
								int rgb = inPixels[ioffset+ix];
								if (blackFunction.isBlack(rgb))
									neighbours++;
							}
						}
					}
				}
				
				if (blackFunction.isBlack(pixel))
					outPixels[index++] = (neighbours == 2 || neighbours == 3) ? pixel : 0xffffffff;
				else
					outPixels[index++] = neighbours == 3 ? 0xff000000 : pixel;
			}

		}
		return outPixels;
	}

	public String toString() {
		return "Binary/Life";
	}

	public BufferedImage filter(BufferedImage src, Struct parameters) throws PageException {BufferedImage dst=ImageUtil.createBufferedImage(src);
		Object o;
		if((o=parameters.removeEL(KeyImpl.init("Iterations")))!=null)setIterations(ImageFilterUtil.toIntValue(o,"Iterations"));
		if((o=parameters.removeEL(KeyImpl.init("Colormap")))!=null)setColormap(ImageFilterUtil.toColormap(o,"Colormap"));
		if((o=parameters.removeEL(KeyImpl.init("NewColor")))!=null)setNewColor(ImageFilterUtil.toColorRGB(o,"NewColor"));
		
		// check for arguments not supported
		if(parameters.size()>0) {
			throw new FunctionException(ThreadLocalPageContext.get(), "ImageFilter", 3, "parameters", "the parameter"+(parameters.size()>1?"s":"")+" ["+List.arrayToList(parameters.keysAsString(),", ")+"] "+(parameters.size()>1?"are":"is")+" not allowed, only the following parameters are supported [Iterations, Colormap, NewColor, BlackFunction]");
		}

		return filter(src, dst);
	}
}

