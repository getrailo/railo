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
import java.awt.image.BufferedImageOp;

import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.type.List;
import railo.runtime.type.Struct;

/**
 * A BufferedImageOp which iterates another BufferedImageOp.
 */
public class IteratedFilter extends AbstractBufferedImageOp  implements DynFiltering {
	private BufferedImageOp filter;
	private int iterations;
	
    /**
     * Construct an IteratedFilter.
     * @param filter the filetr to iterate
     * @param iterations the number of iterations
     */
	public IteratedFilter( BufferedImageOp filter, int iterations ) {
		this.filter = filter;
		this.iterations = iterations;
	}
	
	public BufferedImage filter( BufferedImage src, BufferedImage dst ) {
		BufferedImage image = src;

		for ( int i = 0; i < iterations; i++ )
			image = filter.filter( image, dst );
		
		return image;
	}
	public BufferedImage filter(BufferedImage src, BufferedImage dst ,Struct parameters) throws PageException {
		Object o;

		// check for arguments not supported
		if(parameters.size()>0) {
			throw new FunctionException(ThreadLocalPageContext.get(), "ImageFilter", 3, "parameters", "the parameter"+(parameters.size()>1?"s":"")+" ["+List.arrayToList(parameters.keysAsString(),", ")+"] "+(parameters.size()>1?"are":"is")+" not allowed, only the following parameters are supported []");
		}

		return filter(src, dst);
	}
}
