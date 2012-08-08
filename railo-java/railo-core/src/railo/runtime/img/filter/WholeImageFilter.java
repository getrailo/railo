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
import java.awt.image.ColorModel;

import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.img.ImageUtil;
import railo.runtime.type.Struct;
import railo.runtime.type.util.CollectionUtil;


/**
 * A filter which acts as a superclass for filters which need to have the whole image in memory
 * to do their stuff.
 */
public abstract class WholeImageFilter extends AbstractBufferedImageOp  implements DynFiltering {

	/**
     * The output image bounds.
     */
    protected Rectangle transformedSpace;

	/**
     * The input image bounds.
     */
	protected Rectangle originalSpace;
	
	/**
	 * Construct a WholeImageFilter.
	 */
	public WholeImageFilter() {
	}

    public BufferedImage filter( BufferedImage src, BufferedImage dst ) {
        int width = src.getWidth();
        int height = src.getHeight();
		//int type = src.getType();
		//WritableRaster srcRaster = 
		src.getRaster();

		originalSpace = new Rectangle(0, 0, width, height);
		transformedSpace = new Rectangle(0, 0, width, height);
		transformSpace(transformedSpace);

        if ( dst == null ) {
            ColorModel dstCM = src.getColorModel();
			dst = new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(transformedSpace.width, transformedSpace.height), dstCM.isAlphaPremultiplied(), null);
		}
		//WritableRaster dstRaster = 
        dst.getRaster();

		int[] inPixels = getRGB( src, 0, 0, width, height, null );
		inPixels = filterPixels( width, height, inPixels, transformedSpace );
		setRGB( dst, 0, 0, transformedSpace.width, transformedSpace.height, inPixels );

        return dst;
    }

	/**
     * Calculate output bounds for given input bounds.
     * @param rect input and output rectangle
     */
	protected void transformSpace(Rectangle rect) {
	}
	
	/**
     * Actually filter the pixels.
     * @param width the image width
     * @param height the image height
     * @param inPixels the image pixels
     * @param transformedSpace the output bounds
     * @return the output pixels
     */
	protected abstract int[] filterPixels( int width, int height, int[] inPixels, Rectangle transformedSpace );
	public BufferedImage filter(BufferedImage src, Struct parameters) throws PageException {BufferedImage dst=ImageUtil.createBufferedImage(src);
		//Object o;

		// check for arguments not supported
		if(parameters.size()>0) {
			throw new FunctionException(ThreadLocalPageContext.get(), "ImageFilter", 3, "parameters", "the parameter"+(parameters.size()>1?"s":"")+" ["+CollectionUtil.getKeyList(parameters,", ")+"] "+(parameters.size()>1?"are":"is")+" not allowed, only the following parameters are supported []");
		}

		return filter(src, dst);
	}
}

