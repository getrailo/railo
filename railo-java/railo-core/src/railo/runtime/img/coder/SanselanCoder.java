package railo.runtime.img.coder;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.lang.ExceptionUtil;

class SanselanCoder extends Coder {
	
	private String[] writerFormatNames=new String[]{"PNG","GIF","TIFF","JPEG","BMP","PNM","PGM","PBM","PPM","XMP"};
	private String[] readerFormatNames=new String[]{"PNG","GIF","TIFF","JPEG","BMP","PNM","PGM","PBM","PPM","XMP" ,"ICO","PSD"};
	
	protected SanselanCoder(){
		super();
		Sanselan.hasImageFileExtension("railo.gif");// to make sure Sanselan exist when load this class
	}
	
	/**
	 * translate a file resource to a buffered image
	 * @param res
	 * @return
	 * @throws IOException
	 */
	public final BufferedImage toBufferedImage(Resource res,String format) throws IOException {
		InputStream is=null;
		try {
			return Sanselan.getBufferedImage(is=res.getInputStream());
		} 
		catch (ImageReadException e) {
			throw ExceptionUtil.toIOException(e);
		}
		finally {
			IOUtil.closeEL(is);
		}
	}

	/**
	 * translate a binary array to a buffered image
	 * @param binary
	 * @return
	 * @throws IOException
	 */
	public final BufferedImage toBufferedImage(byte[] bytes,String format) throws IOException {
		try {
			return Sanselan.getBufferedImage(new ByteArrayInputStream(bytes));
		} 
		catch (ImageReadException e) {
			throw ExceptionUtil.toIOException(e);
		}
	}

	@Override
	public String[] getWriterFormatNames() {
		return writerFormatNames;
	}

	@Override
	public String[] getReaderFormatNames() {
		return readerFormatNames;
	}
}