package railo.runtime.img;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;

import railo.commons.img.AbstractCaptcha;
import railo.commons.img.CaptchaException;
import railo.commons.lang.font.FontUtil;
import railo.runtime.exp.ExpressionException;
import railo.runtime.img.filter.MarbleFilter;

public class MarpleCaptcha extends AbstractCaptcha {

	public static final int DIFFICULTY_LOW=0;
	public static final int DIFFICULTY_MEDIUM=1;
	public static final int DIFFICULTY_HIGH=2;

	public BufferedImage generate(String text,int width, int height, String[] fonts, boolean useAntiAlias, Color fontColor,int fontSize, int difficulty) throws CaptchaException {
		MarbleFilter mf = new MarbleFilter();
		try {
			mf.setEdgeAction("WRAP");
		} catch (ExpressionException e1) {}
		mf.setAmount(0.1F);
		BufferedImage src=super.generate(text, width, height, fonts, useAntiAlias, fontColor, fontSize, difficulty);
		
		if(difficulty==DIFFICULTY_LOW) mf.setTurbulence(0.0f);
		else if(difficulty==DIFFICULTY_MEDIUM) mf.setTurbulence(0.10f);
		else mf.setTurbulence(0.2f);
		
		try {
			mf.setInterpolation("NEAREST_NEIGHBOUR");
		} catch (ExpressionException e) {}
		
		BufferedImage dst = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		mf.filter(src, dst);
		return dst;
	}
	
	public Font getFont(String font, Font defaultValue) {
		return FontUtil.getFont(font,defaultValue);
	}
}
