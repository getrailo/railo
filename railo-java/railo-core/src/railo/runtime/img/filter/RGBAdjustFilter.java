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



public class RGBAdjustFilter extends PointFilter  implements DynFiltering {
	
	public float rFactor, gFactor, bFactor;

	public RGBAdjustFilter() {
		this(0, 0, 0);
	}

	public RGBAdjustFilter(float r, float g, float b) {
		rFactor = 1+r;
		gFactor = 1+g;
		bFactor = 1+b;
		canFilterIndexColorModel = true;
	}

	public void setRFactor( float rFactor ) {
		this.rFactor = 1+rFactor;
	}
	
	public float getRFactor() {
		return rFactor-1;
	}
	
	public void setGFactor( float gFactor ) {
		this.gFactor = 1+gFactor;
	}
	
	public float getGFactor() {
		return gFactor-1;
	}
	
	public void setBFactor( float bFactor ) {
		this.bFactor = 1+bFactor;
	}
	
	public float getBFactor() {
		return bFactor-1;
	}

	public int[] getLUT() {
		int[] lut = new int[256];
		for ( int i = 0; i < 256; i++ ) {
			lut[i] = filterRGB( 0, 0, (i << 24) | (i << 16) | (i << 8) | i );
		}
		return lut;
	}
	
	@Override
	public int filterRGB(int x, int y, int rgb) {
		int a = rgb & 0xff000000;
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = rgb & 0xff;
		r = PixelUtils.clamp((int)(r * rFactor));
		g = PixelUtils.clamp((int)(g * gFactor));
		b = PixelUtils.clamp((int)(b * bFactor));
		return a | (r << 16) | (g << 8) | b;
	}

	@Override
	public String toString() {
		return "Colors/Adjust RGB...";
	}
	@Override
	public BufferedImage filter(BufferedImage src, Struct parameters) throws PageException {BufferedImage dst=ImageUtil.createBufferedImage(src);
		Object o;
		if((o=parameters.removeEL(KeyImpl.init("BFactor")))!=null)setBFactor(ImageFilterUtil.toFloatValue(o,"BFactor"));
		if((o=parameters.removeEL(KeyImpl.init("RFactor")))!=null)setRFactor(ImageFilterUtil.toFloatValue(o,"RFactor"));
		if((o=parameters.removeEL(KeyImpl.init("GFactor")))!=null)setGFactor(ImageFilterUtil.toFloatValue(o,"GFactor"));
		if((o=parameters.removeEL(KeyImpl.init("Dimensions")))!=null){
			int[] dim=ImageFilterUtil.toDimensions(o,"Dimensions");
			setDimensions(dim[0],dim[1]);
		}

		// check for arguments not supported
		if(parameters.size()>0) {
			throw new FunctionException(ThreadLocalPageContext.get(), "ImageFilter", 3, "parameters", "the parameter"+(parameters.size()>1?"s":"")+" ["+CollectionUtil.getKeyList(parameters,", ")+"] "+(parameters.size()>1?"are":"is")+" not allowed, only the following parameters are supported [BFactor, RFactor, GFactor, Dimensions]");
		}

		return filter(src, dst);
	}
}

