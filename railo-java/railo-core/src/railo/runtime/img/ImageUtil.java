package railo.runtime.img;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.codec.binary.Base64;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.exp.ExpressionException;

public class ImageUtil {
	
	private static String[] writerFormatNames;
	private static String[] readerFormatNames;

	/**
	 * translate a file resource to a buffered image
	 * @param res
	 * @return
	 * @throws IOException
	 */
	public static BufferedImage toBufferedImage(Resource res,String format) throws IOException {
		if(StringUtil.isEmpty(format))format=getFormat(res);
		InputStream is=null;
		try {
			is=res.getInputStream();
			return read(is,format);
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
	public static BufferedImage toBufferedImage(byte[] binary,String format) throws IOException {
		if(StringUtil.isEmpty(format))format=getFormat(binary);
		InputStream is=null;
		try {
			is=new ByteArrayInputStream(binary);
			return read(is, format);
		}
		finally {
			IOUtil.closeEL(is);
		} 
		
		
		
	}

	private static BufferedImage read(InputStream is, String format) throws IOException {
		if("psd".equalsIgnoreCase(format)) {
			PSDReader reader = new PSDReader();
			reader.read(is);
			return reader.getImage();
		}
		return ImageIO.read(is);
		
	}
	
	
	
	
	
	
	public static byte[] readBase64(String b64str) throws ExpressionException {
		if(StringUtil.isEmpty(b64str))
			throw new ExpressionException("base64 string is empty");
		
		int index = b64str.indexOf("base64,");
		if(index!=-1)b64str=b64str.substring(index + 7);
		
		return Base64.decodeBase64(b64str.getBytes());
	}
	
	public static String getFormat(Resource res) throws IOException {
		String ext=getFormatFromExtension(res,null);
		if(ext!=null) return ext;
		String mt=IOUtil.getMymeType(res, null);
		if(mt==null) return null;//throw new IOException("can't extract mimetype from ["+res+"]");
		return getFormatFromMimeType(mt);
	}

	public static String getFormat(byte[] binary) throws IOException {
		return getFormatFromMimeType(IOUtil.getMymeType(binary, ""));
	}

	public static String getFormatFromExtension(Resource res, String defaultValue) {
		String ext=ResourceUtil.getExtension(res,null);
		if("gif".equalsIgnoreCase(ext))return "gif";
		if("jpg".equalsIgnoreCase(ext))return "jpg";
		if("jpe".equalsIgnoreCase(ext))return "jpg";
		if("jpeg".equalsIgnoreCase(ext))return "jpg";
		if("png".equalsIgnoreCase(ext))return "png";
		if("tiff".equalsIgnoreCase(ext))return "tiff";
		if("bmp".equalsIgnoreCase(ext))return "bmp";
		if("bmg".equalsIgnoreCase(ext))return "bmg";
		if("wbmp".equalsIgnoreCase(ext))return "wbmp";
		if("ico".equalsIgnoreCase(ext))return "bmp";
		if("wbmg".equalsIgnoreCase(ext))return "wbmg";
		if("psd".equalsIgnoreCase(ext))return "psd";
		return defaultValue;
		
	}
	
	public static String getFormatFromMimeType(String mt) throws IOException {
		if("image/gif".equals(mt)) return "gif";
		if("image/jpeg".equals(mt)) return "jpg";
		if("image/jpg".equals(mt)) return "jpg";
		if("image/jpe".equals(mt)) return "jpg";
		if("image/pjpeg".equals(mt)) return "jpg";
		if("image/png".equals(mt)) return "png";
		if("image/x-png".equals(mt))return "png";
		if("image/tiff".equals(mt)) return "tiff";
		if("image/bmg".equals(mt)) return "bmg";
		if("image/vnd.wap.wbmp".equals(mt)) return "wbmg";
		
		if(StringUtil.isEmpty(mt))throw new IOException("can't find Format of given image");//31
		throw new IOException("can't find Format ("+mt+") of given image");
	}
	
	public static String getMimeTypeFromFormat(String mt) throws IOException {
		if("gif".equals(mt)) return "image/gif";
		if("jpeg".equals(mt)) return "image/jpg";
		if("jpg".equals(mt)) return "image/jpg";
		if("jpe".equals(mt)) return "image/jpg";
		if("png".equals(mt)) return "image/x-png";
		if("tiff".equals(mt)) return "image/tiff";
		if("bmg".equals(mt)) return "image/bmg";
		if("wbmg".equals(mt)) return "image/vnd.wap.wbmp";

		if(StringUtil.isEmpty(mt))throw new IOException("can't find Format of given image");//31
		throw new IOException("can't find Format ("+mt+") of given image");
	}

	public static void closeEL(ImageInputStream iis) {
		 try {
    		 if(iis!=null)iis.close();
    	 } 
    	 catch (Throwable t) {}
		
		
	}

	public static String[] getWriterFormatNames() {
		if(writerFormatNames==null)
			writerFormatNames=_getFormatNames(ImageIO.getWriterFormatNames());
		return writerFormatNames;
	}
	public static String[] getReaderFormatNames() {
		if(readerFormatNames==null)
			readerFormatNames=_getFormatNames(ImageIO.getReaderFormatNames());
		return readerFormatNames;
	}
	
	private static String[] _getFormatNames(String[] names) {
		Set<String> set=new HashSet<String>();
		if(names!=null)for(int i=0;i<names.length;i++){
			set.add(names[i].toLowerCase());
		}
		names= set.toArray(new String[set.size()]);
		Arrays.sort(names);
		return names;
	}
	

	
		public static BufferedImage createBufferedImage(BufferedImage image, int columns, int rows) {
	        ColorModel colormodel = image.getColorModel();
	        BufferedImage newImage;
	        if(colormodel instanceof IndexColorModel) {
	            if(colormodel.getTransparency() != 1)
	                newImage = new BufferedImage(columns, rows, 2);
	            else
	                newImage = new BufferedImage(columns, rows, 1);
	        } 
	        else {
	            newImage = new BufferedImage(colormodel, image.getRaster().createCompatibleWritableRaster(columns, rows), colormodel.isAlphaPremultiplied(), null);
	        }
	        return newImage;
	    }
	    
	    public static BufferedImage createBufferedImage(BufferedImage image) {
	        return createBufferedImage(image, image.getWidth(), image.getHeight());
	    }
}
