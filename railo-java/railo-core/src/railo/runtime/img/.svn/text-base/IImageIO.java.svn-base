package railo.runtime.img;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;


/**
 * Example of how to read JPEG images as Rasters.
 */
public class IImageIO {
	

    public static BufferedImage read(InputStream is) throws IOException  {
    	try {
            return ImageIO.read(is);
        } 
        catch(IIOException e) {
        	return _read(is);
        }
    }

	
    private static BufferedImage _read(InputStream is) throws IOException {
        // Find a JPEG reader which supports reading Rasters.
        Iterator readers = ImageIO.getImageReadersByFormatName("JPEG");
        ImageReader reader = null;
        while(readers.hasNext()) {
            reader = (ImageReader)readers.next();
            if(reader.canReadRaster()) {
                break;
            }
        }

        // Set the input.
        ImageInputStream input =ImageIO.createImageInputStream(is);
        reader.setInput(input);

        
        // Try reading a Raster (no color conversion).
        Raster raster = reader.readRaster(0, null);

        // Arbitrarily select a BufferedImage type.
        int imageType;
        switch(raster.getNumBands()) {
        case 1:
            imageType = BufferedImage.TYPE_BYTE_GRAY;
            break;
        case 3:
            imageType = BufferedImage.TYPE_3BYTE_BGR;
            break;
        case 4:
            imageType = BufferedImage.TYPE_4BYTE_ABGR;
            break;
        default:
            throw new UnsupportedOperationException();
        }

        // Create a BufferedImage.
        BufferedImage image = new BufferedImage(raster.getWidth(),
                                  raster.getHeight(),
                                  imageType);

        // Set the image data.
        image.getRaster().setRect(raster);
        return image;
    }

}