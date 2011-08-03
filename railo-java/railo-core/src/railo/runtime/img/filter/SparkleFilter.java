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
import java.util.Random;

import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.img.ImageUtil;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.List;
import railo.runtime.type.Struct;

public class SparkleFilter extends PointFilter  implements DynFiltering {
	
	private int rays = 50;
	private int radius = 25;
	private int amount = 50;
	private int color = 0xffffffff;
	private int randomness = 25;
	private int width, height;
	private int centreX, centreY;
	private long seed = 371;
	private float[] rayLengths;
	private Random randomNumbers = new Random();
	
	public SparkleFilter() {
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getColor() {
		return color;
	}

	public void setRandomness(int randomness) {
		this.randomness = randomness;
	}

	public int getRandomness() {
		return randomness;
	}

	/**
	 * Set the amount of sparkle.
	 * @param amount the amount
     * @min-value 0
     * @max-value 1
     * @see #getAmount
	 */
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	/**
	 * Get the amount of sparkle.
	 * @return the amount
     * @see #setAmount
	 */
	public int getAmount() {
		return amount;
	}
	
	public void setRays(int rays) {
		this.rays = rays;
	}

	public int getRays() {
		return rays;
	}

	/**
	 * Set the radius of the effect.
	 * @param radius the radius
     * @min-value 0
     * @see #getRadius
	 */
	public void setRadius(int radius) {
		this.radius = radius;
	}

	/**
	 * Get the radius of the effect.
	 * @return the radius
     * @see #setRadius
	 */
	public int getRadius() {
		return radius;
	}

	public void setDimensions(int width, int height) {
		this.width = width;
		this.height = height;
		centreX = width/2;
		centreY = height/2;
		super.setDimensions(width, height);
		randomNumbers.setSeed(seed);
		rayLengths = new float[rays];
		for (int i = 0; i < rays; i++)
			rayLengths[i] = radius + randomness / 100.0f * radius * (float)randomNumbers.nextGaussian();
	}
	
	public int filterRGB(int x, int y, int rgb) {
		float dx = x-centreX;
		float dy = y-centreY;
		float distance = dx*dx+dy*dy;
		float angle = (float)Math.atan2(dy, dx);
		float d = (angle+ImageMath.PI) / (ImageMath.TWO_PI) * rays;
		int i = (int)d;
		float f = d - i;

		if (radius != 0) {
			float length = ImageMath.lerp(f, rayLengths[i % rays], rayLengths[(i+1) % rays]);
			float g = length*length / (distance+0.0001f);
			g = (float)Math.pow(g, (100-amount) / 50.0);
			f -= 0.5f;
//			f *= amount/50.0f;
			f = 1 - f*f;
			f *= g;
		}
		f = ImageMath.clamp(f, 0, 1);
		return ImageMath.mixColors(f, rgb, color);
	}

	public String toString() {
		return "Stylize/Sparkle...";
	}
	public BufferedImage filter(BufferedImage src, Struct parameters) throws PageException {BufferedImage dst=ImageUtil.createBufferedImage(src);
		Object o;
		if((o=parameters.removeEL(KeyImpl.init("Radius")))!=null)setRadius(ImageFilterUtil.toIntValue(o,"Radius"));
		if((o=parameters.removeEL(KeyImpl.init("Amount")))!=null)setAmount(ImageFilterUtil.toIntValue(o,"Amount"));
		if((o=parameters.removeEL(KeyImpl.init("Randomness")))!=null)setRandomness(ImageFilterUtil.toIntValue(o,"Randomness"));
		if((o=parameters.removeEL(KeyImpl.init("Rays")))!=null)setRays(ImageFilterUtil.toIntValue(o,"Rays"));
		if((o=parameters.removeEL(KeyImpl.init("Color")))!=null)setColor(ImageFilterUtil.toColorRGB(o,"Color"));
		if((o=parameters.removeEL(KeyImpl.init("Dimensions")))!=null){
			int[] dim=ImageFilterUtil.toDimensions(o,"Dimensions");
			setDimensions(dim[0],dim[1]);
		}

		// check for arguments not supported
		if(parameters.size()>0) {
			throw new FunctionException(ThreadLocalPageContext.get(), "ImageFilter", 3, "parameters", "the parameter"+(parameters.size()>1?"s":"")+" ["+List.arrayToList(parameters.keysAsString(),", ")+"] "+(parameters.size()>1?"are":"is")+" not allowed, only the following parameters are supported [Radius, Amount, Randomness, Rays, Color, Dimensions]");
		}

		return filter(src, dst);
	}
}
