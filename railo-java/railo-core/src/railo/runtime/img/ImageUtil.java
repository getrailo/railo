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
		if("psd".equalsIgnoreCase(format)) {
			PSDReader reader = new PSDReader();
			InputStream is=null;
			try {
				reader.read(is=res.getInputStream());
				return reader.getImage();
			}
			finally {
				IOUtil.closeEL(is);
			}
		}
		if(JAIUtil.isSupportedReadFormat(format)){
			return JAIUtil.read(res);
		}
		
		BufferedImage img=null;
		InputStream is=null;
		try {
			img = ImageIO.read(is=res.getInputStream());
		}
		finally {
			IOUtil.closeEL(is);
		}
		
		if(img==null && StringUtil.isEmpty(format)) {
			return JAIUtil.read(res);
		}
		return img;
	}

	/**
	 * translate a binary array to a buffered image
	 * @param binary
	 * @return
	 * @throws IOException
	 */
	public static BufferedImage toBufferedImage(byte[] bytes,String format) throws IOException {
		if(StringUtil.isEmpty(format))format=getFormat(bytes,null);
		if("psd".equalsIgnoreCase(format)) {
			PSDReader reader = new PSDReader();
			reader.read(new ByteArrayInputStream(bytes));
			return reader.getImage();
		}
		if(JAIUtil.isSupportedReadFormat(format)){
			return JAIUtil.read(new ByteArrayInputStream(bytes),format);
		}
		BufferedImage img = ImageIO.read(new ByteArrayInputStream(bytes));
		if(img==null && StringUtil.isEmpty(format))
			return JAIUtil.read(new ByteArrayInputStream(bytes),null);
		return img;
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

	public static String getFormat(byte[] binary,String defaultValue) {
		return getFormatFromMimeType(IOUtil.getMymeType(binary, ""),defaultValue);
	}

	public static String getFormatFromExtension(Resource res, String defaultValue) {
		String ext=ResourceUtil.getExtension(res,null);
		if("gif".equalsIgnoreCase(ext))return "gif";
		if("jpg".equalsIgnoreCase(ext))return "jpg";
		if("jpe".equalsIgnoreCase(ext))return "jpg";
		if("jpeg".equalsIgnoreCase(ext))return "jpg";
		if("png".equalsIgnoreCase(ext))return "png";
		if("tiff".equalsIgnoreCase(ext))return "tiff";
		if("tif".equalsIgnoreCase(ext))return "tiff";
		if("bmp".equalsIgnoreCase(ext))return "bmp";
		if("bmg".equalsIgnoreCase(ext))return "bmg";
		if("wbmp".equalsIgnoreCase(ext))return "wbmp";
		if("ico".equalsIgnoreCase(ext))return "bmp";
		if("wbmg".equalsIgnoreCase(ext))return "wbmg";
		if("psd".equalsIgnoreCase(ext))return "psd";
		if("pnm".equalsIgnoreCase(ext))return "pnm";
		if("fpx".equalsIgnoreCase(ext))return "fpx";
		return defaultValue;	
	}
	
	public static String getFormatFromMimeType(String mt) throws IOException {
		String format = getFormatFromMimeType(mt, null);
		if(format!=null) return format;
		
		if(StringUtil.isEmpty(mt))throw new IOException("cannot find Format of given image");//31
		throw new IOException("can't find Format ("+mt+") of given image");
	}
	
	public static String getFormatFromMimeType(String mt, String defaultValue) {
		if("image/gif".equals(mt)) return "gif";
		if("image/jpeg".equals(mt)) return "jpg";
		if("image/jpg".equals(mt)) return "jpg";
		if("image/jpe".equals(mt)) return "jpg";
		if("image/pjpeg".equals(mt)) return "jpg";
		if("image/png".equals(mt)) return "png";
		if("image/x-png".equals(mt))return "png";
		if("image/tiff".equals(mt)) return "tiff";
		if("image/bmg".equals(mt)) return "bmg";
		if("image/pnm".equals(mt)) return "pnm";
		if("image/x-portable-anymap".equals(mt)) return "pnm";
		if("image/vnd.wap.wbmp".equals(mt)) return "wbmg";
		if("image/fpx".equals(mt)) return "fpx";
		if("application/vnd.fpx".equals(mt)) return "fpx";
		if("application/vnd.netfpx".equals(mt)) return "fpx";
		if("image/x-fpx".equals(mt)) return "fpx";
		if("image/vnd.fpx".equals(mt)) return "fpx";
		
		return defaultValue;
	}
	
	public static String getMimeTypeFromFormat(String mt) throws IOException {
		if("gif".equals(mt)) return "image/gif";
		if("jpeg".equals(mt)) return "image/jpg";
		if("jpg".equals(mt)) return "image/jpg";
		if("jpe".equals(mt)) return "image/jpg";
		if("png".equals(mt)) return "image/x-png";
		if("tiff".equals(mt)) return "image/tiff";
		if("bmg".equals(mt)) return "image/bmg";
		if("pnm".equals(mt)) return "image/pnm";
		if("wbmg".equals(mt)) return "image/vnd.wap.wbmp";
		if("fpx".equals(mt)) return "image/fpx";

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
		if(writerFormatNames==null)	{
			String[] iio = ImageIO.getWriterFormatNames();
			String[] jai = JAIUtil.isJAISupported()?JAIUtil.getSupportedWriteFormat():null;
			writerFormatNames=_getFormatNames(iio,jai);
		}
		return writerFormatNames;
	}
	public static String[] getReaderFormatNames() {
		if(readerFormatNames==null){
			String[] iio = ImageIO.getReaderFormatNames();
			String[] jai = JAIUtil.isJAISupported()?JAIUtil.getSupportedReadFormat():null;
			readerFormatNames=_getFormatNames(iio,jai);
		}
		return readerFormatNames;
	}
	
	private static String[] _getFormatNames(String[] names1,String[] names2) {
		Set<String> set=new HashSet<String>();
		
		if(names1!=null)for(int i=0;i<names1.length;i++){
			set.add(names1[i].toLowerCase());
		}
		if(names2!=null)for(int i=0;i<names2.length;i++){
			set.add(names2[i].toLowerCase());
		}
		
		names1= set.toArray(new String[set.size()]);
		Arrays.sort(names1);
		return names1;
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