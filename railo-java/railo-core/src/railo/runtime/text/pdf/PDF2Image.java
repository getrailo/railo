package railo.runtime.text.pdf;

import java.io.IOException;
import java.util.Set;

import railo.commons.io.res.Resource;
import railo.commons.lang.SystemOut;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;

public abstract class PDF2Image {
	
	private static PDF2Image instance;
	
	public static PDF2Image getInstance()	{
		if(instance==null){
			try{
				try{
					instance=new PDF2ImageICEpdf();
					SystemOut.printDate("using ICEpdf PDF2Image  Library");
				}
				catch(Throwable t){
					instance=new PDF2ImagePDFRenderer();
					SystemOut.printDate("using PDFRenderer PDF2Image  Library");
				}
			}
			catch(Throwable t){
				instance=new PDF2ImageJPedal();
				SystemOut.printDate("using JPedal PDF2Image  Library");
			}
		}
		//return new PDF2ImageJPedal();
		return instance;
	}
	

	protected static Resource createDestinationResource(Resource dir,String prefix,int page,String format, boolean overwrite) throws ExpressionException {
		Resource res = dir.getRealResource(prefix+"_page_"+page+"."+format);
		if(res.exists()) {
			if(!overwrite)throw new ExpressionException("can't overwrite existing image ["+res+"], attribute [overwrite] is false");
		}
		return res;
	}

	
	public abstract Image toImage(byte[] input,int page) throws IOException, PageException;
	public abstract void writeImages(byte[] input,Set pages,Resource outputDirectory, String prefix,String format, int scale, boolean overwrite, boolean goodQuality,boolean transparent) throws PageException, IOException;
}
