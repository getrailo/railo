package railo.runtime.img.coder;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.lang.ClassException;
import railo.commons.lang.ClassUtil;
import railo.commons.lang.ExceptionUtil;

class SanselanCoder extends Coder {
	
	private String[] writerFormatNames=new String[]{"PNG","GIF","TIFF","JPEG","BMP","PNM","PGM","PBM","PPM","XMP"};
	private String[] readerFormatNames=new String[]{"PNG","GIF","TIFF","JPEG","BMP","PNM","PGM","PBM","PPM","XMP" ,"ICO","PSD"};
	private Class sanselan;
	private Method getBufferedImageIS;
	
	protected SanselanCoder() throws ClassException{
		super();
		sanselan=ClassUtil.loadClass("org.apache.sanselan.Sanselan");
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
			return getBufferedImage(is=res.getInputStream());
		} 
		catch (Throwable t) {
			throw ExceptionUtil.toIOException(t);
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
			return getBufferedImage(new ByteArrayInputStream(bytes));
		} 
		catch (Throwable t) {
			throw ExceptionUtil.toIOException(t);
		}
	}
	


	private BufferedImage getBufferedImage(InputStream is) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, ClassException {
		// getBufferedImage(java.io.InputStream is)
		if(getBufferedImageIS==null) {
			getBufferedImageIS=sanselan.getMethod("getBufferedImage", new Class[]{InputStream.class});
		}
		return (BufferedImage) getBufferedImageIS.invoke(sanselan, new Object[]{is});
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