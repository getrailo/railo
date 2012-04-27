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

package railo.runtime.img.filter;import java.awt.Color;
import java.awt.image.BufferedImage;

import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.img.ImageUtil;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.util.CollectionUtil;

public class HSBAdjustFilter extends PointFilter  implements DynFiltering {
	
	public float hFactor, sFactor, bFactor;
	private float[] hsb = new float[3];
	
	public HSBAdjustFilter() {
		this(0, 0, 0);
	}

	public HSBAdjustFilter(float r, float g, float b) {
		hFactor = r;
		sFactor = g;
		bFactor = b;
		canFilterIndexColorModel = true;
	}

	public void setHFactor( float hFactor ) {
		this.hFactor = hFactor;
	}
	
	public float getHFactor() {
		return hFactor;
	}
	
	public void setSFactor( float sFactor ) {
		this.sFactor = sFactor;
	}
	
	public float getSFactor() {
		return sFactor;
	}
	
	public void setBFactor( float bFactor ) {
		this.bFactor = bFactor;
	}
	
	public float getBFactor() {
		return bFactor;
	}
	
	public int filterRGB(int x, int y, int rgb) {
		int a = rgb & 0xff000000;
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = rgb & 0xff;
		Color.RGBtoHSB(r, g, b, hsb);
		hsb[0] += hFactor;
		while (hsb[0] < 0)
			hsb[0] += Math.PI*2;
		hsb[1] += sFactor;
		if (hsb[1] < 0)
			hsb[1] = 0;
		else if (hsb[1] > 1.0)
			hsb[1] = 1.0f;
		hsb[2] += bFactor;
		if (hsb[2] < 0)
			hsb[2] = 0;
		else if (hsb[2] > 1.0)
			hsb[2] = 1.0f;
		rgb = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
		return a | (rgb & 0xffffff);
	}

	public String toString() {
		return "Colors/Adjust HSB...";
	}
	public BufferedImage filter(BufferedImage src, Struct parameters) throws PageException {BufferedImage dst=ImageUtil.createBufferedImage(src);
		Object o;
		if((o=parameters.removeEL(KeyImpl.init("HFactor")))!=null)setHFactor(ImageFilterUtil.toFloatValue(o,"HFactor"));
		if((o=parameters.removeEL(KeyImpl.init("SFactor")))!=null)setSFactor(ImageFilterUtil.toFloatValue(o,"SFactor"));
		if((o=parameters.removeEL(KeyImpl.init("BFactor")))!=null)setBFactor(ImageFilterUtil.toFloatValue(o,"BFactor"));
		if((o=parameters.removeEL(KeyImpl.init("Dimensions")))!=null){
			int[] dim=ImageFilterUtil.toDimensions(o,"Dimensions");
			setDimensions(dim[0],dim[1]);
		}

		// check for arguments not supported
		if(parameters.size()>0) {
			throw new FunctionException(ThreadLocalPageContext.get(), "ImageFilter", 3, "parameters", "the parameter"+(parameters.size()>1?"s":"")+" ["+CollectionUtil.getKeyList(parameters,", ")+"] "+(parameters.size()>1?"are":"is")+" not allowed, only the following parameters are supported [HFactor, SFactor, BFactor, Dimensions]");
		}

		return filter(src, dst);
	}
}

