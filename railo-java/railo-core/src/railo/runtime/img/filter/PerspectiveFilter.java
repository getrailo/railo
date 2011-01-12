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
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.List;
import railo.runtime.type.Struct;

/**
 * A filter which performs a perspective distortion on an image.
 */
public class PerspectiveFilter extends TransformFilter  implements DynFiltering {

	private float x0, y0, x1, y1, x2, y2, x3, y3;
	private float dx1, dy1, dx2, dy2, dx3, dy3;
	private float A, B, C, D, E, F, G, H, I;
	
	/**
     * Construct a PerspectiveFilter.
     */
    public PerspectiveFilter() {
		this(0, 0, 100, 0, 100, 100, 0, 100);
	}
	
	/**
     * Construct a PerspectiveFilter.
     * @param x0 the new position of the top left corner
     * @param y0 the new position of the top left corner
     * @param x1 the new position of the top right corner
     * @param y1 the new position of the top right corner
     * @param x2 the new position of the bottom right corner
     * @param y2 the new position of the bottom right corner
     * @param x3 the new position of the bottom left corner
     * @param y3 the new position of the bottom left corner
     */
	public PerspectiveFilter(float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3) {
		setCorners(x0, y0, x1, y1, x2, y2, x3, y3);
	}
	
	/**
     * Set the new positions of the image corners.
     * @param x0 the new position of the top left corner
     * @param y0 the new position of the top left corner
     * @param x1 the new position of the top right corner
     * @param y1 the new position of the top right corner
     * @param x2 the new position of the bottom right corner
     * @param y2 the new position of the bottom right corner
     * @param x3 the new position of the bottom left corner
     * @param y3 the new position of the bottom left corner
     */
	public void setCorners(float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3) {
		this.x0 = x0;
		this.y0 = y0;
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.x3 = x3;
		this.y3 = y3;
		
		dx1 = x1-x2;
		dy1 = y1-y2;
		dx2 = x3-x2;
		dy2 = y3-y2;
		dx3 = x0-x1+x2-x3;
		dy3 = y0-y1+y2-y3;
		
		float a11, a12, a13, a21, a22, a23, a31, a32;

		if (dx3 == 0 && dy3 == 0) {
			a11 = x1-x0;
			a21 = x2-x1;
			a31 = x0;
			a12 = y1-y0;
			a22 = y2-y1;
			a32 = y0;
			a13 = a23 = 0;
		} else {
			a13 = (dx3*dy2-dx2*dy3)/(dx1*dy2-dy1*dx2);
			a23 = (dx1*dy3-dy1*dx3)/(dx1*dy2-dy1*dx2);
			a11 = x1-x0+a13*x1;
			a21 = x3-x0+a23*x3;
			a31 = x0;
			a12 = y1-y0+a13*y1;
			a22 = y3-y0+a23*y3;
			a32 = y0;
		}

	    A = a22 - a32*a23;
	    B = a31*a23 - a21;
	    C = a21*a32 - a31*a22;
	    D = a32*a13 - a12;
	    E = a11 - a31*a13;
	    F = a31*a12 - a11*a32;
	    G = a12*a23 - a22*a13;
	    H = a21*a13 - a11*a23;
	    I = a11*a22 - a21*a12;
	}

	protected void transformSpace(Rectangle rect) {
		rect.x = (int)Math.min( Math.min( x0, x1 ), Math.min( x2, x3 ) );
		rect.y = (int)Math.min( Math.min( y0, y1 ), Math.min( y2, y3 ) );
		rect.width = (int)Math.max( Math.max( x0, x1 ), Math.max( x2, x3 ) ) - rect.x;
		rect.height = (int)Math.max( Math.max( y0, y1 ), Math.max( y2, y3 ) ) - rect.y;
	}

    /**
     * Get the origin of the output image. Use this for working out where to draw your new image.
     * @return the X origin.
     */
	public float getOriginX() {
		return x0 - (int)Math.min( Math.min( x0, x1 ), Math.min( x2, x3 ) );
	}

    /**
     * Get the origin of the output image. Use this for working out where to draw your new image.
     * @return the Y origin.
     */
	public float getOriginY() {
		return y0 - (int)Math.min( Math.min( y0, y1 ), Math.min( y2, y3 ) );
	}

/*
    public Point2D getPoint2D( Point2D srcPt, Point2D dstPt ) {
        if ( dstPt == null )
            dstPt = new Point2D.Double();

		dx1 = x1-x2;
		dy1 = y1-y2;
		dx2 = x3-x2;
		dy2 = y3-y2;
		dx3 = x0-x1+x2-x3;
		dy3 = y0-y1+y2-y3;
		
		float a11, a12, a13, a21, a22, a23, a31, a32;

		if (dx3 == 0 && dy3 == 0) {
			a11 = x1-x0;
			a21 = x2-x1;
			a31 = x0;
			a12 = y1-y0;
			a22 = y2-y1;
			a32 = y0;
			a13 = a23 = 0;
		} else {
			a13 = (dx3*dy2-dx2*dy3)/(dx1*dy2-dy1*dx2);
			a23 = (dx1*dy3-dy1*dx3)/(dx1*dy2-dy1*dx2);
			a11 = x1-x0+a13*x1;
			a21 = x3-x0+a23*x3;
			a31 = x0;
			a12 = y1-y0+a13*y1;
			a22 = y3-y0+a23*y3;
			a32 = y0;
		}

		float x = (float)srcPt.getX();
		float y = (float)srcPt.getY();
		float D = 1.0f/(a13*x + a23*y + 1);

        dstPt.setLocation( (a11*x + a21*y + a31)*D, (a12*x + a22*y + a32)*D );
        return dstPt;
    }
*/

	protected void transformInverse(int x, int y, float[] out) {
		out[0] = originalSpace.width * (A*x+B*y+C)/(G*x+H*y+I);
		out[1] = originalSpace.height * (D*x+E*y+F)/(G*x+H*y+I);
	}

	public String toString() {
		return "Distort/Perspective...";
	}
	public BufferedImage filter(BufferedImage src, BufferedImage dst ,Struct parameters) throws PageException {
		Object o;
		if((o=parameters.removeEL(KeyImpl.init("EdgeAction")))!=null)setEdgeAction(ImageFilterUtil.toIntValue(o,"EdgeAction"));
		if((o=parameters.removeEL(KeyImpl.init("Interpolation")))!=null)setInterpolation(ImageFilterUtil.toIntValue(o,"Interpolation"));

		// check for arguments not supported
		if(parameters.size()>0) {
			throw new FunctionException(ThreadLocalPageContext.get(), "ImageFilter", 3, "parameters", "the parameter"+(parameters.size()>1?"s":"")+" ["+List.arrayToList(parameters.keysAsString(),", ")+"] "+(parameters.size()>1?"are":"is")+" not allowed, only the following parameters are supported [Corners, EdgeAction, Interpolation]");
		}

		return filter(src, dst);
	}
}

