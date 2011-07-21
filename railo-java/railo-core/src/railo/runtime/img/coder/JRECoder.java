package railo.runtime.img.coder;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.lang.StringUtil;
import railo.runtime.img.ImageUtil;
import railo.runtime.img.JAIUtil;
import railo.runtime.img.PSDReader;

class JRECoder extends Coder {
	

	private String[] writerFormatNames;
	private String[] readerFormatNames;
	
	protected JRECoder(){
		super();
	}
	
	/**
	 * translate a file resource to a buffered image
	 * @param res
	 * @return
	 * @throws IOException
	 */
	public final BufferedImage toBufferedImage(Resource res,String format) throws IOException {
		if(StringUtil.isEmpty(format))format=ImageUtil.getFormat(res);
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
	public final BufferedImage toBufferedImage(byte[] bytes,String format) throws IOException {
		if(StringUtil.isEmpty(format))format=ImageUtil.getFormat(bytes,null);
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
	
	public final String[] getWriterFormatNames() {
		if(writerFormatNames==null)	{
			String[] iio = ImageIO.getWriterFormatNames();
			String[] jai = JAIUtil.isJAISupported()?JAIUtil.getSupportedWriteFormat():null;
			writerFormatNames=mixTogetherOrdered(iio,jai);
		}
		return writerFormatNames;
	}
	public final String[] getReaderFormatNames() {
		if(readerFormatNames==null){
			String[] iio = ImageIO.getReaderFormatNames();
			String[] jai = JAIUtil.isJAISupported()?JAIUtil.getSupportedReadFormat():null;
			readerFormatNames=mixTogetherOrdered(iio,jai);
		}
		return readerFormatNames;
	}
	
	public static final String[] mixTogetherOrdered(String[] names1,String[] names2) {
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
}