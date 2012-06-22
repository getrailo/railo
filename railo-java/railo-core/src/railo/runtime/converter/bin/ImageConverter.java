package railo.runtime.converter.bin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import railo.runtime.PageContext;
import railo.runtime.converter.BinaryConverter;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;

public abstract class ImageConverter implements BinaryConverter {
	byte[] _serialize(PageContext pc,Object source, String format) throws PageException, IOException {
		Image img = Image.createImage(pc, source, false, true,true,format);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		img.writeOut(baos, format, 1, true);
		return baos.toByteArray();
	}
}
