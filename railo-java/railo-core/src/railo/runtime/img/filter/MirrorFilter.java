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

package railo.runtime.img.filter;import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;

import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.img.ImageUtil;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.List;
import railo.runtime.type.Struct;

public class MirrorFilter extends AbstractBufferedImageOp  implements DynFiltering {
    private float opacity = 1.0f;
	private float centreY = 0.5f;
    private float distance;
    private float angle;
    private float rotation;
    private float gap;

    public MirrorFilter() {
	}
	
	/**
     * Specifies the angle of the mirror.
     * @param angle the angle of the mirror.
     * @angle
     * @see #getAngle
     */
	public void setAngle( float angle ) {
		this.angle = angle;
	}

	/**
     * Returns the angle of the mirror.
     * @return the angle of the mirror.
     * @see #setAngle
     */
	public float getAngle() {
		return angle;
	}
	
	public void setDistance( float distance ) {
		this.distance = distance;
	}

	public float getDistance() {
		return distance;
	}
	
	public void setRotation( float rotation ) {
		this.rotation = rotation;
	}

	public float getRotation() {
		return rotation;
	}
	
	public void setGap( float gap ) {
		this.gap = gap;
	}

	public float getGap() {
		return gap;
	}
	
	/**
     * Set the opacity of the reflection.
     * @param opacity the opacity.
     * @see #getOpacity
     */
	public void setOpacity( float opacity ) {
		this.opacity = opacity;
	}

	/**
     * Get the opacity of the reflection.
     * @return the opacity.
     * @see #setOpacity
     */
	public float getOpacity() {
		return opacity;
	}
	
	public void setCentreY( float centreY ) {
		this.centreY = centreY;
	}

	public float getCentreY() {
		return centreY;
	}
	
    public BufferedImage filter( BufferedImage src, BufferedImage dst ) {
        if ( dst == null )
            dst = createCompatibleDestImage( src, null );
        BufferedImage tsrc = src;
		Shape clip;
		int width = src.getWidth();
		int height = src.getHeight();
		int h = (int)(centreY * height);
		int d = (int)(gap * height);

		Graphics2D g = dst.createGraphics();
		clip = g.getClip();
		g.clipRect( 0, 0, width, h );
		g.drawRenderedImage( src, null );
		g.setClip( clip );
		g.clipRect( 0, h+d, width, height-h-d );
		g.translate( 0, 2*h+d );
		g.scale( 1, -1 );
		g.drawRenderedImage( src, null );
		g.setPaint( new GradientPaint( 0, 0, new Color( 1.0f, 0.0f, 0.0f, 0.0f ), 0, h, new Color( 0.0f, 1.0f, 0.0f, opacity ) ) );
		g.setComposite( AlphaComposite.getInstance( AlphaComposite.DST_IN ) );
		g.fillRect( 0, 0, width, h );
		g.setClip( clip );
		g.dispose();
        
        return dst;
    }
    
	public String toString() {
		return "Effects/Mirror...";
	}
	public BufferedImage filter(BufferedImage src, Struct parameters) throws PageException {BufferedImage dst=ImageUtil.createBufferedImage(src);
		Object o;
		if((o=parameters.removeEL(KeyImpl.init("Angle")))!=null)setAngle(ImageFilterUtil.toFloatValue(o,"Angle"));
		if((o=parameters.removeEL(KeyImpl.init("CentreY")))!=null)setCentreY(ImageFilterUtil.toFloatValue(o,"CentreY"));
		if((o=parameters.removeEL(KeyImpl.init("Distance")))!=null)setDistance(ImageFilterUtil.toFloatValue(o,"Distance"));
		if((o=parameters.removeEL(KeyImpl.init("Rotation")))!=null)setRotation(ImageFilterUtil.toFloatValue(o,"Rotation"));
		if((o=parameters.removeEL(KeyImpl.init("Gap")))!=null)setGap(ImageFilterUtil.toFloatValue(o,"Gap"));
		if((o=parameters.removeEL(KeyImpl.init("Opacity")))!=null)setOpacity(ImageFilterUtil.toFloatValue(o,"Opacity"));

		// check for arguments not supported
		if(parameters.size()>0) {
			throw new FunctionException(ThreadLocalPageContext.get(), "ImageFilter", 3, "parameters", "the parameter"+(parameters.size()>1?"s":"")+" ["+List.arrayToList(parameters.keysAsString(),", ")+"] "+(parameters.size()>1?"are":"is")+" not allowed, only the following parameters are supported [Angle, CentreY, Distance, Rotation, Gap, Opacity]");
		}

		return filter(src, dst);
	}
}
