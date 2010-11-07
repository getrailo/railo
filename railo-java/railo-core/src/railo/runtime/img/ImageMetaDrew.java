package railo.runtime.img;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.runtime.exp.PageException;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.tiff.TiffMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;

public class ImageMetaDrew {

	/**
	 * adds information about a image to the given struct
	 * @param info
	 * @throws PageException 
	 * @throws IOException 
	 * @throws MetadataException 
	 * @throws JpegProcessingException 
	 */
	public static void addInfo(String format, Resource res, Struct info)  {
		if("jpg".equalsIgnoreCase(format))jpg(res, info);
		else if("tiff".equalsIgnoreCase(format))tiff(res, info);
		
	}

	private static void jpg(Resource res,Struct info) {
		InputStream is=null;
		try {
			is = res.getInputStream();
			fill(info,JpegMetadataReader.readMetadata(is));
		}
		catch(Throwable t) {
			//throw Caster.toPageException(t);
		}
		finally {
			IOUtil.closeEL(is);
		}
	}
	
	private static void tiff(Resource res,Struct info) {
		InputStream is=null;
		try {
			is = res.getInputStream();
			fill(info,TiffMetadataReader.readMetadata(is));
		}
		catch(Throwable t) {
			//throw Caster.toPageException(t);
		}
		finally {
			IOUtil.closeEL(is);
		}
	}

	private static void fill(Struct info,Metadata metadata) throws MetadataException {
		Iterator<Directory> directories = metadata.getDirectoryIterator();
		while (directories.hasNext()) {
		    Directory directory = directories.next();
		    Struct sct=new StructImpl();
		    info.setEL(KeyImpl.init(directory.getName()), sct);
		    
		    Iterator<Tag> tags = directory.getTagIterator();
		    while (tags.hasNext()) {
		        Tag tag = tags.next();
		        sct.setEL(KeyImpl.init(tag.getTagName()), tag.getDescription());
		    }
		}
	}

	public static void test() {
		// to not delete, this methd is called to test if the jar exists
		
	}

	

}
