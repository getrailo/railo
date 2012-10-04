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
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.util.CollectionUtil;
/**
 * A Filter to pixellate images.
 */
public class BlockFilter extends TransformFilter  implements DynFiltering {
	
	private int blockSize = 2;

	/**
	 * Set the pixel block size.
	 * @param blockSize the number of pixels along each block edge
     * @min-value 1
     * @max-value 100+
     * @see #getBlockSize
	 */
	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}

	/**
	 * Get the pixel block size.
	 * @return the number of pixels along each block edge
     * @see #setBlockSize
	 */
	public int getBlockSize() {
		return blockSize;
	}


	public BlockFilter() {
	}

	protected void transformInverse(int x, int y, float[] out) {
		out[0] = (x / blockSize) * blockSize;
		out[1] = (y / blockSize) * blockSize;
	}

	public String toString() {
		return "Stylize/Mosaic...";
	}
	public BufferedImage filter(BufferedImage src, Struct parameters) throws PageException {BufferedImage dst=null;//ImageUtil.createBufferedImage(src);
		Object o;
		if((o=parameters.removeEL(KeyImpl.init("BlockSize")))!=null)setBlockSize(ImageFilterUtil.toIntValue(o,"BlockSize"));
		if((o=parameters.removeEL(KeyImpl.init("EdgeAction")))!=null)setEdgeAction(ImageFilterUtil.toString(o,"EdgeAction"));
		if((o=parameters.removeEL(KeyImpl.init("Interpolation")))!=null)setInterpolation(ImageFilterUtil.toString(o,"Interpolation"));

		// check for arguments not supported
		if(parameters.size()>0) {
			throw new FunctionException(ThreadLocalPageContext.get(), "ImageFilter", 3, "parameters", "the parameter"+(parameters.size()>1?"s":"")+" ["+CollectionUtil.getKeyList(parameters,", ")+"] "+(parameters.size()>1?"are":"is")+" not allowed, only the following parameters are supported [BlockSize, EdgeAction, Interpolation]");
		}

		return filter(src, dst);
	}
}

