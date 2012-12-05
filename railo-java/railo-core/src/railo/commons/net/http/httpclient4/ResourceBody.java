package railo.commons.net.http.httpclient4;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.content.AbstractContentBody;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.lang.StringUtil;

public class ResourceBody extends AbstractContentBody {

    public static final String DEFAULT_MIMETYPE = "application/octet-stream";
    
	private String fileName = null;
	private Resource res;
	private String charset;
    
	public ResourceBody(Resource res, String mimetype, String fileName, String charset) throws FileNotFoundException{
		super(StringUtil.isEmpty(mimetype,true)?DEFAULT_MIMETYPE:mimetype);
		this.res=res;
        if (!res.isFile()) {
            throw new FileNotFoundException("File is not a normal file.");
        }
        if (!res.isReadable()) {
            throw new FileNotFoundException("File is not readable.");
        }
        this.fileName = StringUtil.isEmpty(fileName,true)?res.getName():fileName;  
        this.charset = charset;       
        
	}
	
	public String getFilename() {
    	return (fileName == null) ? "noname" : fileName;
    }

	@Override
	public void writeTo(OutputStream os) throws IOException {
		IOUtil.copy(res, os, false);
	}

	@Override
	public String getCharset() {
		return charset;
	}

	@Override
	public long getContentLength() {
		if (this.res != null) {
            return this.res.length();
        } 
        return 0;
	}

	@Override
	public String getTransferEncoding() {
		return MIME.ENC_BINARY;
	}

    /**
	 * @return the res
	 */
	public Resource getResource() {
		return res;
	}
}
