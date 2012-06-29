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
import java.awt.image.BufferedImageOp;

import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.img.ImageUtil;
import railo.runtime.type.Struct;
import railo.runtime.type.util.CollectionUtil;

/**
 * A BufferedImageOp which combines two other BufferedImageOps, one after the other.
 */
public class CompoundFilter extends AbstractBufferedImageOp  implements DynFiltering {
	private BufferedImageOp filter1;
	private BufferedImageOp filter2;
	
	/**
     * Construct a CompoundFilter.
     * @param filter1 the first filter
     * @param filter2 the second filter
     */
    public CompoundFilter( BufferedImageOp filter1, BufferedImageOp filter2 ) {
		this.filter1 = filter1;
		this.filter2 = filter2;
	}
	
	public BufferedImage filter( BufferedImage src, BufferedImage dst ) {
		BufferedImage image = filter1.filter( src, dst );
		image = filter2.filter( image, dst );
		return image;
	}
	public BufferedImage filter(BufferedImage src, Struct parameters) throws PageException {BufferedImage dst=ImageUtil.createBufferedImage(src);
		//Object o;

		// check for arguments not supported
		if(parameters.size()>0) {
			throw new FunctionException(ThreadLocalPageContext.get(), "ImageFilter", 3, "parameters", "the parameter"+(parameters.size()>1?"s":"")+" ["+CollectionUtil.getKeyList(parameters,", ")+"] "+(parameters.size()>1?"are":"is")+" not allowed, only the following parameters are supported []");
		}

		return filter(src, dst);
	}
}
