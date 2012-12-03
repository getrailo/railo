package railo.commons.lang.font;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.util.Iterator;

import railo.runtime.exp.ExpressionException;
import railo.runtime.op.Duplicator;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;

public class FontUtil {

	private static Array fonts;
	private static Graphics2D graphics;

	public synchronized static Array getAvailableFontsAsStringArray() {
		 Iterator<Object> it = getAvailableFonts(false).valueIterator();
		Array arr=new ArrayImpl();
		while(it.hasNext()) {
			arr.appendEL(((Font)it.next()).getFontName());
		}
		return arr;
	}
	private synchronized static Array getAvailableFonts(boolean duplicate) {
		if (fonts == null) {
    	    
			fonts = new ArrayImpl();
    	    	GraphicsEnvironment graphicsEvn = GraphicsEnvironment.getLocalGraphicsEnvironment();
    	    	Font[] availableFonts = graphicsEvn.getAllFonts();
    	    	for (int i = 0; i < availableFonts.length; i++) {
    				fonts.appendEL(availableFonts[i]);
    	    	}
    		
		}
		if(!duplicate) return fonts;
		return (Array) Duplicator.duplicate(fonts,false);
	}

	public static String toString(Font font) {
		if(font==null) return null;
		return font.getFontName();
	}

	public static Font getFont(String font, Font defaultValue) {
		Font f=Font.decode(font);
		if(f!=null) return f;
		// font name
		Iterator<Object> it = getAvailableFonts(false).valueIterator();
		while(it.hasNext()) {
			f=(Font) it.next();
			if(f.getFontName().equalsIgnoreCase(font)) return f;
		}
		// family
		it = getAvailableFonts(false).valueIterator();
		while(it.hasNext()) {
			f=(Font) it.next();
			if(f.getFamily().equalsIgnoreCase(font)) return f;
		}
		return defaultValue;
	}
	
	public static Font getFont(String font) throws ExpressionException {
		Font f = getFont(font,null);
		if(f!=null) return f;
		throw new ExpressionException("no font with name ["+font+"] available"
	    		,"to get available fonts call function ImageFonts()");
	}
	
	public static FontMetrics getFontMetrics(Font font) {
		if(graphics==null) {
			graphics = new BufferedImage(1, 1,BufferedImage.TYPE_INT_ARGB).createGraphics();
		}
		return graphics.getFontMetrics(font);
	}
}
