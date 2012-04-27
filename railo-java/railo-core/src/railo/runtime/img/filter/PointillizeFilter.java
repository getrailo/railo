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



public class PointillizeFilter extends CellularFilter  implements DynFiltering {

	private float edgeThickness = 0.4f;
	private boolean fadeEdges = false;
	private int edgeColor = 0xff000000;
	private float fuzziness = 0.1f;

	public PointillizeFilter() {
		setScale(16);
		setRandomness(0.0f);
	}
	
	public void setEdgeThickness(float edgeThickness) {
		this.edgeThickness = edgeThickness;
	}

	public float getEdgeThickness() {
		return edgeThickness;
	}

	public void setFadeEdges(boolean fadeEdges) {
		this.fadeEdges = fadeEdges;
	}

	public boolean getFadeEdges() {
		return fadeEdges;
	}

	public void setEdgeColor(int edgeColor) {
		this.edgeColor = edgeColor;
	}

	public int getEdgeColor() {
		return edgeColor;
	}

	public void setFuzziness(float fuzziness) {
		this.fuzziness = fuzziness;
	}

	public float getFuzziness() {
		return fuzziness;
	}

	public int getPixel(int x, int y, int[] inPixels, int width, int height) {
		float nx = m00*x + m01*y;
		float ny = m10*x + m11*y;
		nx /= scale;
		ny /= scale * stretch;
		nx += 1000;
		ny += 1000;	// Reduce artifacts around 0,0
		float f = evaluate(nx, ny);

		float f1 = results[0].distance;
		int srcx = ImageMath.clamp((int)((results[0].x-1000)*scale), 0, width-1);
		int srcy = ImageMath.clamp((int)((results[0].y-1000)*scale), 0, height-1);
		int v = inPixels[srcy * width + srcx];

		if (fadeEdges) {
			float f2 = results[1].distance;
			srcx = ImageMath.clamp((int)((results[1].x-1000)*scale), 0, width-1);
			srcy = ImageMath.clamp((int)((results[1].y-1000)*scale), 0, height-1);
			int v2 = inPixels[srcy * width + srcx];
			v = ImageMath.mixColors(0.5f*f1/f2, v, v2);
		} else {
			f = 1-ImageMath.smoothStep(edgeThickness, edgeThickness+fuzziness, f1);
			v = ImageMath.mixColors(f, edgeColor, v);
		}
		return v;
	}

	public String toString() {
		return "Stylize/Pointillize...";
	}
	
	public BufferedImage filter(BufferedImage src, Struct parameters) throws PageException {BufferedImage dst=ImageUtil.createBufferedImage(src);
		Object o;
		if((o=parameters.removeEL(KeyImpl.init("Fuzziness")))!=null)setFuzziness(ImageFilterUtil.toFloatValue(o,"Fuzziness"));
		if((o=parameters.removeEL(KeyImpl.init("EdgeThickness")))!=null)setEdgeThickness(ImageFilterUtil.toFloatValue(o,"EdgeThickness"));
		if((o=parameters.removeEL(KeyImpl.init("FadeEdges")))!=null)setFadeEdges(ImageFilterUtil.toBooleanValue(o,"FadeEdges"));
		if((o=parameters.removeEL(KeyImpl.init("EdgeColor")))!=null)setEdgeColor(ImageFilterUtil.toColorRGB(o,"EdgeColor"));
		if((o=parameters.removeEL(KeyImpl.init("Colormap")))!=null)setColormap(ImageFilterUtil.toColormap(o,"Colormap"));
		if((o=parameters.removeEL(KeyImpl.init("Amount")))!=null)setAmount(ImageFilterUtil.toFloatValue(o,"Amount"));
		if((o=parameters.removeEL(KeyImpl.init("Turbulence")))!=null)setTurbulence(ImageFilterUtil.toFloatValue(o,"Turbulence"));
		if((o=parameters.removeEL(KeyImpl.init("Stretch")))!=null)setStretch(ImageFilterUtil.toFloatValue(o,"Stretch"));
		if((o=parameters.removeEL(KeyImpl.init("Angle")))!=null)setAngle(ImageFilterUtil.toFloatValue(o,"Angle"));
		if((o=parameters.removeEL(KeyImpl.init("AngleCoefficient")))!=null)setAngleCoefficient(ImageFilterUtil.toFloatValue(o,"AngleCoefficient"));
		if((o=parameters.removeEL(KeyImpl.init("GradientCoefficient")))!=null)setGradientCoefficient(ImageFilterUtil.toFloatValue(o,"GradientCoefficient"));
		if((o=parameters.removeEL(KeyImpl.init("F1")))!=null)setF1(ImageFilterUtil.toFloatValue(o,"F1"));
		if((o=parameters.removeEL(KeyImpl.init("F2")))!=null)setF2(ImageFilterUtil.toFloatValue(o,"F2"));
		if((o=parameters.removeEL(KeyImpl.init("F3")))!=null)setF3(ImageFilterUtil.toFloatValue(o,"F3"));
		if((o=parameters.removeEL(KeyImpl.init("F4")))!=null)setF4(ImageFilterUtil.toFloatValue(o,"F4"));
		if((o=parameters.removeEL(KeyImpl.init("Randomness")))!=null)setRandomness(ImageFilterUtil.toFloatValue(o,"Randomness"));
		if((o=parameters.removeEL(KeyImpl.init("GridType")))!=null)setGridType(ImageFilterUtil.toString(o,"GridType"));
		if((o=parameters.removeEL(KeyImpl.init("DistancePower")))!=null)setDistancePower(ImageFilterUtil.toFloatValue(o,"DistancePower"));
		if((o=parameters.removeEL(KeyImpl.init("Scale")))!=null)setScale(ImageFilterUtil.toFloatValue(o,"Scale"));

		// check for arguments not supported
		if(parameters.size()>0) {
			throw new FunctionException(ThreadLocalPageContext.get(), "ImageFilter", 3, "parameters", "the parameter"+(parameters.size()>1?"s":"")+" ["+CollectionUtil.getKeyList(parameters,", ")+"] "+(parameters.size()>1?"are":"is")+" not allowed, only the following parameters are supported [Fuzziness, EdgeThickness, FadeEdges, EdgeColor, Colormap, Amount, Turbulence, Stretch, Angle, Coefficient, AngleCoefficient, GradientCoefficient, F1, F2, F3, F4, Randomness, GridType, DistancePower, Scale]");
		}

		return filter(src, dst);
	}
}
