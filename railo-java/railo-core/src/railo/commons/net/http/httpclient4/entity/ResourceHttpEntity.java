package railo.commons.net.http.httpclient4.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.entity.AbstractHttpEntity;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;

/**
 * A RequestEntity that represents a Resource.
 */
public class ResourceHttpEntity extends AbstractHttpEntity implements Entity4 {

    final Resource res;
	private String strContentType;
    
    public ResourceHttpEntity(final Resource res, final String contentType) {
    	super();
        this.res = res;
        setContentType(contentType);
        strContentType = contentType;
    }
   
    /**
     * @see org.apache.http.HttpEntity#getContentLength()
     */
    public long getContentLength() {
        return this.res.length();
    }

    /**
     * @see org.apache.http.HttpEntity#isRepeatable()
     */
    public boolean isRepeatable() {
        return true;
    }
    
    /**
     * @see org.apache.http.HttpEntity#getContent()
     */
    public InputStream getContent() throws IOException {
    	return res.getInputStream();
    }

    /**
     * @see org.apache.http.HttpEntity#writeTo(java.io.OutputStream)
     */
    public void writeTo(final OutputStream out) throws IOException {
       IOUtil.copy(res.getInputStream(), out,true,false);
    }

	/**
	 * @see org.apache.http.HttpEntity#isStreaming()
	 */
	public boolean isStreaming() {
		return false;
	}

	@Override
	public long contentLength() {
		return getContentLength();
	}

	@Override
	public String contentType() {
		return strContentType;
	}
}