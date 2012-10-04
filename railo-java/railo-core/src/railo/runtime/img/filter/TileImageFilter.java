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

package railo.runtime.img.filter;import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.img.ImageUtil;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.util.CollectionUtil;

/**
 * A filter which tiles an image into a lerger one.
 */
public class TileImageFilter extends AbstractBufferedImageOp  implements DynFiltering {

	private int width;
	private int height;
	private int tileWidth;
	private int tileHeight;

	/**
     * Construct a TileImageFilter.
     */
    public TileImageFilter() {
		this(32, 32);
	}

	/**
     * Construct a TileImageFilter.
     * @param width the output image width
     * @param height the output image height
     */
	public TileImageFilter(int width, int height) {
		this.width = width;
		this.height = height;
	}

	/**
     * Set the output image width.
     * @param width the width
     * @see #getWidth
     */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
     * Get the output image width.
     * @return the width
     * @see #setWidth
     */
	public int getWidth() {
		return width;
	}

	/**
     * Set the output image height.
     * @param height the height
     * @see #getHeight
     */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
     * Get the output image height.
     * @return the height
     * @see #setHeight
     */
	public int getHeight() {
		return height;
	}

    public BufferedImage filter( BufferedImage src, BufferedImage dst ) {
        int tileWidth = src.getWidth();
        int tileHeight = src.getHeight();

        if ( dst == null ) {
            ColorModel dstCM = src.getColorModel();
			dst = new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(width, height), dstCM.isAlphaPremultiplied(), null);
		}

		Graphics2D g = dst.createGraphics();
		for ( int y = 0; y < height; y += tileHeight) {
			for ( int x = 0; x < width; x += tileWidth ) {
				g.drawImage( src, null, x, y );
			}
		}
		g.dispose();

        return dst;
    }

	public String toString() {
		return "Tile";
	}
	public BufferedImage filter(BufferedImage src, Struct parameters) throws PageException {BufferedImage dst=ImageUtil.createBufferedImage(src);
		Object o;
		if((o=parameters.removeEL(KeyImpl.init("Width")))!=null)setWidth(ImageFilterUtil.toIntValue(o,"Width"));
		if((o=parameters.removeEL(KeyImpl.init("Height")))!=null)setHeight(ImageFilterUtil.toIntValue(o,"Height"));

		// check for arguments not supported
		if(parameters.size()>0) {
			throw new FunctionException(ThreadLocalPageContext.get(), "ImageFilter", 3, "parameters", "the parameter"+(parameters.size()>1?"s":"")+" ["+CollectionUtil.getKeyList(parameters,", ")+"] "+(parameters.size()>1?"are":"is")+" not allowed, only the following parameters are supported [Width, Height]");
		}

		return filter(src, dst);
	}
}
