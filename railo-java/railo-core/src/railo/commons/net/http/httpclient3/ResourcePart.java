package railo.commons.net.http.httpclient3;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.util.EncodingUtil;

import railo.commons.io.res.Resource;
import railo.commons.lang.StringUtil;

public class ResourcePart extends FilePart {
	protected static final String FILE_NAME = "; filename=";

    /** Attachment's file name as a byte array */
    private static final byte[] FILE_NAME_BYTES = EncodingUtil.getAsciiBytes(FILE_NAME);
    
	private Resource resource;

	private String headerCharset;

	/*public ResourcePart(String name, ResourcePartSource partSource, String contentType, String charset) {
		super(name, partSource, contentType, charset==null?"":charset);
		this.resource=partSource.getResource();
	}*/
	
	public ResourcePart(String name, ResourcePartSource partSource, String contentType, String headerCharset) {
		super(name, partSource, contentType, "");
		this.resource=partSource.getResource();
		this.headerCharset=headerCharset;
	}

	/**
	 * @return the resource
	 */
	public Resource getResource() {
		return resource;
	}

	@Override
	public String getCharSet() {
		String cs = super.getCharSet();
		if(StringUtil.isEmpty(cs)) return null;
		return cs;
	}
	

    @Override
	protected void sendDispositionHeader(OutputStream out)  throws IOException {
		sendDispositionHeader(getName(),getSource().getFileName(),headerCharset,out);
	}
	
	
    public static void sendDispositionHeader(String name,String filename, String headerCharset, OutputStream out)  throws IOException {
    	out.write(CONTENT_DISPOSITION_BYTES);
        out.write(QUOTE_BYTES);
        if(StringUtil.isAscii(name))
        	out.write(EncodingUtil.getAsciiBytes(name));
        else
        	out.write(name.getBytes(headerCharset));
        out.write(QUOTE_BYTES);

        if (filename != null) {
        	out.write(FILE_NAME_BYTES);
            out.write(QUOTE_BYTES);
            if(StringUtil.isAscii(filename))
            	out.write(EncodingUtil.getAsciiBytes(filename));
            else
            	out.write(filename.getBytes(headerCharset));
            out.write(QUOTE_BYTES);
        }
    }

	

}
