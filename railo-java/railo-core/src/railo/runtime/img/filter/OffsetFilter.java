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

package railo.runtime.img.filter;
import railo.runtime.type.KeyImpl;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.PageException;
import railo.runtime.type.Struct;
import java.awt.image.BufferedImage;
import railo.runtime.type.List;
import railo.runtime.exp.FunctionException;

import railo.runtime.type.KeyImpl;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.PageException;
import railo.runtime.type.Struct;
import java.awt.image.BufferedImage;
import railo.runtime.type.List;
import railo.runtime.exp.FunctionException;

import java.awt.image.BufferedImage;

import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.List;
import railo.runtime.type.Struct;

public class OffsetFilter extends TransformFilter  implements DynFiltering {

	private int width, height;
	private int xOffset, yOffset;
	private boolean wrap;

	public OffsetFilter() {
		this(0, 0, true);
	}

	public OffsetFilter(int xOffset, int yOffset, boolean wrap) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.wrap = wrap;
		setEdgeAction( ZERO );
	}

	public void setXOffset(int xOffset) {
		this.xOffset = xOffset;
	}
	
	public int getXOffset() {
		return xOffset;
	}
	
	public void setYOffset(int yOffset) {
		this.yOffset = yOffset;
	}
	
	public int getYOffset() {
		return yOffset;
	}
	
	public void setWrap(boolean wrap) {
		this.wrap = wrap;
	}
	
	public boolean getWrap() {
		return wrap;
	}
	
	protected void transformInverse(int x, int y, float[] out) {
		if ( wrap ) {
			out[0] = (x+width-xOffset) % width;
			out[1] = (y+height-yOffset) % height;
		} else {
			out[0] = x-xOffset;
			out[1] = y-yOffset;
		}
	}

    public BufferedImage filter( BufferedImage src, BufferedImage dst ) {
		this.width = src.getWidth();
		this.height = src.getHeight();
		if ( wrap ) {
			while (xOffset < 0)
				xOffset += width;
			while (yOffset < 0)
				yOffset += height;
			xOffset %= width;
			yOffset %= height;
		}
		return super.filter( src, dst );
	}

	public String toString() {
		return "Distort/Offset...";
	}
	public BufferedImage filter(BufferedImage src, BufferedImage dst ,Struct parameters) throws PageException {
		Object o;
		if((o=parameters.removeEL(KeyImpl.init("XOffset")))!=null)setXOffset(ImageFilterUtil.toIntValue(o,"XOffset"));
		if((o=parameters.removeEL(KeyImpl.init("YOffset")))!=null)setYOffset(ImageFilterUtil.toIntValue(o,"YOffset"));
		if((o=parameters.removeEL(KeyImpl.init("Wrap")))!=null)setWrap(ImageFilterUtil.toBooleanValue(o,"Wrap"));
		if((o=parameters.removeEL(KeyImpl.init("EdgeAction")))!=null)setEdgeAction(ImageFilterUtil.toIntValue(o,"EdgeAction"));
		if((o=parameters.removeEL(KeyImpl.init("Interpolation")))!=null)setInterpolation(ImageFilterUtil.toIntValue(o,"Interpolation"));

		// check for arguments not supported
		if(parameters.size()>0) {
			throw new FunctionException(ThreadLocalPageContext.get(), "ImageFilter", 3, "parameters", "the parameter"+(parameters.size()>1?"s":"")+" ["+List.arrayToList(parameters.keysAsString(),", ")+"] "+(parameters.size()>1?"are":"is")+" not allowed, only the following parameters are supported [XOffset, YOffset, Wrap, EdgeAction, Interpolation]");
		}

		return filter(src, dst);
	}
}
