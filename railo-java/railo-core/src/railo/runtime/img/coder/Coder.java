package railo.runtime.img.coder;

import java.awt.image.BufferedImage;
import java.io.IOException;

import railo.commons.io.res.Resource;
import railo.commons.lang.SystemOut;
public abstract class Coder {
	
	private static Coder instance;
	
	protected Coder(){}
	
	public static Coder getInstance(){
		
		if(instance==null){
			instance = new JRECoder();
			
			// try to load Sanselan, does not load when lib not exist
			try{
				SanselanCoder sanselan = new SanselanCoder();
				instance=new DoubleCoder(sanselan, instance);
				SystemOut.printDate("use Sanselan and JRE Image Coder ");
			}
			catch(Throwable t){
				SystemOut.printDate("use JRE Image Coder ");
			}
		} 
		return instance;
	}
	

	/**
	 * translate a file resource to a buffered image
	 * @param res
	 * @return
	 * @throws IOException
	 */
	public abstract BufferedImage toBufferedImage(Resource res,String format) throws IOException;

	/**
	 * translate a binary array to a buffered image
	 * @param binary
	 * @return
	 * @throws IOException
	 */
	public abstract BufferedImage toBufferedImage(byte[] bytes,String format) throws IOException;

	public abstract String[] getWriterFormatNames();
	
	public abstract String[] getReaderFormatNames();
	
}
