package railo.runtime.text.pdf;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Set;

import org.icepdf.core.pobjects.Catalog;
import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.PDimension;
import org.icepdf.core.pobjects.PRectangle;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.util.GraphicsRenderingHints;

import railo.commons.io.res.Resource;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;
import railo.runtime.op.Caster;

public class PDF2ImageICEpdf extends PDF2Image {
	
	public PDF2ImageICEpdf(){
		Document.class.getName();// this is needed, that the class throws a error when the PDFRenderer.jar is not in the enviroment
	}

	@Override
	public Image toImage(byte[] input, int pageNumber) throws PageException {
		return toImage(input, pageNumber, 100,false);
	}
	
	public Image toImage(byte[] input, int pageNumber, int scale, boolean transparent) throws PageException {
		Document document = toDocument(input);
        BufferedImage bi=toBufferedImage(document,pageNumber,scale/100f,transparent);
		document.dispose();
		return new Image(bi);
	}

	@Override
	public void writeImages(byte[] input, Set pages, Resource outputDirectory,
			String prefix, String format, int scale, boolean overwrite,
			boolean goodQuality, boolean transparent) throws PageException,
			IOException {
		if(scale<1) 
			throw new ExpressionException("invalid scale definition ["+Caster.toString(scale)+"], value should be in range from 1 to n");
        
		Document document = toDocument(input);
        try{	
			Resource res;
			int count = document.getNumberOfPages();
			for(int page=1;page<=count;page++) {
				if(pages!=null && !pages.contains(Integer.valueOf(page)))continue;
				res=createDestinationResource(outputDirectory,prefix,page,format,overwrite);
				//res=outputDirectory.getRealResource(prefix+"_page_"+page+"."+format);
				writeImage(document,page,res,format,scale,overwrite,goodQuality, transparent);
			}
		}
		finally{
			
		}
        document.dispose();
		
	}
	
	private static void writeImage(Document document, int page, Resource destination,String format, int scale,
			boolean overwrite, boolean goodQuality, boolean transparent) throws PageException, IOException {
		
		BufferedImage bi=toBufferedImage(document,page,scale/100f,transparent);
		Image img = new Image(bi);
		img.writeOut(destination,format, overwrite, goodQuality?1f:0.5f);
	}
	


	private Document toDocument(byte[] input) throws PageException {
		Document document = new Document();
        try {
			document.setByteArray(input, 0, input.length, null);
		} catch (Throwable t) {
			throw Caster.toPageException(t);
		}
		return document;
	}

	private static BufferedImage toBufferedImage(Document document, int pageNumber,float scale,boolean transparent) {
		System.getProperties().put("org.icepdf.core.screen.background", "VALUE_DRAW_NO_BACKGROUND");
		
		Catalog cat = document.getCatalog();
		Page page = cat.getPageTree().getPage(pageNumber-1, document);
        PDimension sz = page.getSize(Page.BOUNDARY_CROPBOX, 0f, scale);

        int pageWidth = (int) sz.getWidth();
        int pageHeight = (int) sz.getHeight();

        BufferedImage image = new BufferedImage(pageWidth,
                pageHeight,
                transparent?BufferedImage.TYPE_INT_ARGB:BufferedImage.TYPE_INT_RGB);
        Graphics g = image.createGraphics();
        if (!transparent) {
        	PRectangle pageBoundary = page.getPageBoundary(Page.BOUNDARY_CROPBOX);
        	float x = 0 - pageBoundary.x;
            float y = 0 - (pageBoundary.y - pageBoundary.height);

            g.setColor(Color.WHITE);
            g.fillRect((int) (0 - x),
                    (int) (0 - y),
                    (int) pageBoundary.width,
                    (int) pageBoundary.height);
        }
        
        page.paint(g, GraphicsRenderingHints.SCREEN,
        		Page.BOUNDARY_CROPBOX, 0f, scale);
        
        
        
        g.dispose();
        cat.getPageTree().releasePage(page, document);

        return image;

	}

	
	
	
}
