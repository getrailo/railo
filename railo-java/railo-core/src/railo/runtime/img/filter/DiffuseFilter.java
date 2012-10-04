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
 * This filter diffuses an image by moving its pixels in random directions.
 */
public class DiffuseFilter extends TransformFilter  implements DynFiltering {

	private float[] sinTable, cosTable;
	private float scale = 4;
	
	public DiffuseFilter() {
		super(ConvolveFilter.CLAMP_EDGES);
	}
	
	/**
     * Specifies the scale of the texture.
     * @param scale the scale of the texture.
     * @min-value 1
     * @max-value 100+
     * @see #getScale
     */
	public void setScale(float scale) {
		this.scale = scale;
	}

	/**
     * Returns the scale of the texture.
     * @return the scale of the texture.
     * @see #setScale
     */
	public float getScale() {
		return scale;
	}

	protected void transformInverse(int x, int y, float[] out) {
		int angle = (int)(Math.random() * 255);
		float distance = (float)Math.random();
		out[0] = x + distance * sinTable[angle];
		out[1] = y + distance * cosTable[angle];
	}

    public BufferedImage filter( BufferedImage src, BufferedImage dst ) {
		sinTable = new float[256];
		cosTable = new float[256];
		for (int i = 0; i < 256; i++) {
			float angle = ImageMath.TWO_PI*i/256f;
			sinTable[i] = (float)(scale*Math.sin(angle));
			cosTable[i] = (float)(scale*Math.cos(angle));
		}
		return super.filter( src, dst );
	}

	public String toString() {
		return "Distort/Diffuse...";
	}
	public BufferedImage filter(BufferedImage src, Struct parameters) throws PageException {BufferedImage dst=null;//ImageUtil.createBufferedImage(src);
		Object o;
		if((o=parameters.removeEL(KeyImpl.init("Scale")))!=null)setScale(ImageFilterUtil.toFloatValue(o,"Scale"));
		if((o=parameters.removeEL(KeyImpl.init("EdgeAction")))!=null)setEdgeAction(ImageFilterUtil.toString(o,"EdgeAction"));
		if((o=parameters.removeEL(KeyImpl.init("Interpolation")))!=null)setInterpolation(ImageFilterUtil.toString(o,"Interpolation"));

		// check for arguments not supported
		if(parameters.size()>0) {
			throw new FunctionException(ThreadLocalPageContext.get(), "ImageFilter", 3, "parameters", "the parameter"+(parameters.size()>1?"s":"")+" ["+CollectionUtil.getKeyList(parameters,", ")+"] "+(parameters.size()>1?"are":"is")+" not allowed, only the following parameters are supported [Scale, EdgeAction, Interpolation]");
		}

		return filter(src, dst);
	}
}
