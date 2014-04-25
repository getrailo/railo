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
import java.awt.image.WritableRaster;

import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.img.ImageUtil;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.util.CollectionUtil;

/**
 * An abstract superclass for point filters. The interface is the same as the old RGBImageFilter.
 */
public abstract class PointFilter extends AbstractBufferedImageOp  implements DynFiltering {

	protected boolean canFilterIndexColorModel = false;

    @Override
	public BufferedImage filter( BufferedImage src, BufferedImage dst ) {
        int width = src.getWidth();
        int height = src.getHeight();
		int type = src.getType();
		WritableRaster srcRaster = src.getRaster();

        if ( dst == null )
            dst = createCompatibleDestImage( src, null );
		WritableRaster dstRaster = dst.getRaster();

        setDimensions( width, height);

		int[] inPixels = new int[width];
        for ( int y = 0; y < height; y++ ) {
			// We try to avoid calling getRGB on images as it causes them to become unmanaged, causing horrible performance problems.
			if ( type == BufferedImage.TYPE_INT_ARGB ) {
				srcRaster.getDataElements( 0, y, width, 1, inPixels );
				for ( int x = 0; x < width; x++ )
					inPixels[x] = filterRGB( x, y, inPixels[x] );
				dstRaster.setDataElements( 0, y, width, 1, inPixels );
			} else {
				src.getRGB( 0, y, width, 1, inPixels, 0, width );
				for ( int x = 0; x < width; x++ )
					inPixels[x] = filterRGB( x, y, inPixels[x] );
				dst.setRGB( 0, y, width, 1, inPixels, 0, width );
			}
        }

        return dst;
    }

	public void setDimensions(int width, int height) {
	}

	public abstract int filterRGB(int x, int y, int rgb);
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
