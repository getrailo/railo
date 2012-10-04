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
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

import railo.runtime.exp.PageException;
import railo.runtime.img.ImageUtil;
import railo.runtime.op.Caster;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;


/**
 * Scales an image using the area-averaging algorithm, which can't be done with AffineTransformOp.
 */
public class ScaleFilter extends AbstractBufferedImageOp  implements DynFiltering {

	private int width;
	private int height;

    /**
     * Construct a ScaleFilter.
     */
	public ScaleFilter() {
		this(32, 32);
	}

    /**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
     * Construct a ScaleFilter.
     * @param width the width to scale to
     * @param height the height to scale to
     */
	public ScaleFilter( int width, int height ) {
		this.width = width;
		this.height = height;
	}

    public BufferedImage filter( BufferedImage src, BufferedImage dst ) {
        int w = src.getWidth();
        int h = src.getHeight();

		if ( dst == null ) {
			ColorModel dstCM = src.getColorModel();
			dst = new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(w, h), dstCM.isAlphaPremultiplied(), null);
		}

		//Image scaleImage = 
		src.getScaledInstance( w, h, Image.SCALE_AREA_AVERAGING );
		Graphics2D g = dst.createGraphics();
		g.drawImage( src, 0, 0, width, height, null );
		g.dispose();

        return dst;
    }

	public String toString() {
		return "Distort/Scale";
	}

	public BufferedImage filter(BufferedImage src, Struct parameters) throws PageException {BufferedImage dst=ImageUtil.createBufferedImage(src);
		int width=Caster.toIntValue(parameters.get(KeyImpl.init("Width")));
		int height=Caster.toIntValue(parameters.get(KeyImpl.init("Height")));
		setHeight(height);
		setWidth(width);
		
		dst=ImageUtil.createBufferedImage(dst,width,height);
		
		
		return filter(src, dst);
	}
}
