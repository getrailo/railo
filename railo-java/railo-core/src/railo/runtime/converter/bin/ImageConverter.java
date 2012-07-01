package railo.runtime.converter.bin;

import java.io.IOException;
import java.io.OutputStream;

import railo.runtime.PageContext;
import railo.runtime.converter.BinaryConverter;
import railo.runtime.converter.ConverterException;
import railo.runtime.converter.ConverterSupport;
import railo.runtime.img.Image;

public class ImageConverter implements BinaryConverter {
	
	private final String format;

	public ImageConverter(String format){
		this.format=format;
	}
	
	@Override
	public void writeOut(PageContext pc,Object source, OutputStream os) throws ConverterException, IOException {
		try {
			Image img = Image.createImage(pc, source, false, true,true,format);
			img.writeOut(os, format, 1, false);
		} 
		catch (IOException ioe) {
			throw ioe;
		}
		catch (Exception e) {
			throw ConverterSupport.toConverterException(e);
		}
		
	}
	
	
}
