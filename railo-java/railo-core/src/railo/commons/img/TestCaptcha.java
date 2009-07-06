package railo.commons.img;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class TestCaptcha {
	
	public static void main(String[] args) throws IOException {
		Captcha captcha=new Captcha();
		
		// generate captcha image
		BufferedImage image = captcha.generate(
				Captcha.randomString(10),	// Text
				450,					// width
				70, 					// height
				new String[]{"arial","courier new"},	// fonts 
				true, 					// use anti alias
				Color.BLACK, 			// font color
				45, 					// font size
				Captcha.DIFFICULTY_HIGH	// difficulty
			);

		// write out captcha image as a png file
		FileOutputStream fos = new FileOutputStream(new File("/Users/mic/temp/captcha.png"));
		Captcha.writeOut(image, fos, "png");

		// write out captcha image as a jpg file
		fos = new FileOutputStream(new File("/Users/mic/temp/captcha.jpg"));
		Captcha.writeOut(image, fos, "jpg");
		
	}
}
