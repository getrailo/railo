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
 * A filter which performs a threshold operation on an image.
 */
public class ThresholdFilter extends PointFilter  implements DynFiltering {

	private int lowerThreshold;
	private int lowerThreshold3;
	private int upperThreshold;
	private int upperThreshold3;
	private int white = 0xffffff;
	private int black = 0x000000;
	
	/**
     * Construct a ThresholdFilter.
     */
    public ThresholdFilter() {
		this(127);
	}

	/**
     * Construct a ThresholdFilter.
     * @param t the threshold value
     */
	public ThresholdFilter(int t) {
		setLowerThreshold(t);
		setUpperThreshold(t);
	}

	/**
     * Set the lower threshold value.
     * @param lowerThreshold the threshold value
     * @see #getLowerThreshold
     */
	public void setLowerThreshold(int lowerThreshold) {
		this.lowerThreshold = lowerThreshold;
		lowerThreshold3 = lowerThreshold*3;
	}
	
	/**
     * Get the lower threshold value.
     * @return the threshold value
     * @see #setLowerThreshold
     */
	public int getLowerThreshold() {
		return lowerThreshold;
	}
	
	/**
     * Set the upper threshold value.
     * @param upperThreshold the threshold value
     * @see #getUpperThreshold
     */
	public void setUpperThreshold(int upperThreshold) {
		this.upperThreshold = upperThreshold;
		upperThreshold3 = upperThreshold*3;
	}

	/**
     * Get the upper threshold value.
     * @return the threshold value
     * @see #setUpperThreshold
     */
	public int getUpperThreshold() {
		return upperThreshold;
	}

	/**
     * Set the color to be used for pixels above the upper threshold.
     * @param white the color
     * @see #getWhite
     */
	public void setWhite(int white) {
		this.white = white;
	}

	/**
     * Get the color to be used for pixels above the upper threshold.
     * @return the color
     * @see #setWhite
     */
	public int getWhite() {
		return white;
	}

	/**
     * Set the color to be used for pixels below the lower threshold.
     * @param black the color
     * @see #getBlack
     */
	public void setBlack(int black) {
		this.black = black;
	}

	/**
     * Set the color to be used for pixels below the lower threshold.
     * @return the color
     * @see #setBlack
     */
	public int getBlack() {
		return black;
	}

	public int filterRGB(int x, int y, int rgb) {
		int a = rgb & 0xff000000;
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = rgb & 0xff;
		int l = r + g + b;
		if (l < lowerThreshold3)
			return a | black;
		else if (l > upperThreshold3)
			return a | white;
		return rgb;
	}

	public String toString() {
		return "Stylize/Threshold...";
	}
	public BufferedImage filter(BufferedImage src, Struct parameters) throws PageException {BufferedImage dst=ImageUtil.createBufferedImage(src);
		Object o;
		if((o=parameters.removeEL(KeyImpl.init("White")))!=null)setWhite(ImageFilterUtil.toColorRGB(o,"White"));
		if((o=parameters.removeEL(KeyImpl.init("Black")))!=null)setBlack(ImageFilterUtil.toColorRGB(o,"Black"));
		if((o=parameters.removeEL(KeyImpl.init("LowerThreshold")))!=null)setLowerThreshold(ImageFilterUtil.toIntValue(o,"LowerThreshold"));
		if((o=parameters.removeEL(KeyImpl.init("UpperThreshold")))!=null)setUpperThreshold(ImageFilterUtil.toIntValue(o,"UpperThreshold"));
		if((o=parameters.removeEL(KeyImpl.init("Dimensions")))!=null){
			int[] dim=ImageFilterUtil.toDimensions(o,"Dimensions");
			setDimensions(dim[0],dim[1]);
		}

		// check for arguments not supported
		if(parameters.size()>0) {
			throw new FunctionException(ThreadLocalPageContext.get(), "ImageFilter", 3, "parameters", "the parameter"+(parameters.size()>1?"s":"")+" ["+CollectionUtil.getKeyList(parameters,", ")+"] "+(parameters.size()>1?"are":"is")+" not allowed, only the following parameters are supported [White, Black, LowerThreshold, UpperThreshold, Dimensions]");
		}

		return filter(src, dst);
	}
}
