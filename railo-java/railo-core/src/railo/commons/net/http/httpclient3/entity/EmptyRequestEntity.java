package railo.commons.net.http.httpclient3.entity;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.httpclient.methods.RequestEntity;


public class EmptyRequestEntity implements RequestEntity,Entity3 {

	private final String contentType;
	
	/**
	 * Constructor of the class
	 * @param contentType
	 */
	public EmptyRequestEntity(String contentType) {
		this.contentType=contentType;
	}
	
	/**
	 * @see org.apache.commons.httpclient.methods.RequestEntity#getContentLength()
	 */
	public long getContentLength() {
		return 0;
	}

	/**
	 * @see org.apache.commons.httpclient.methods.RequestEntity#getContentType()
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @see org.apache.commons.httpclient.methods.RequestEntity#isRepeatable()
	 */
	public boolean isRepeatable() {
		return true;
	}

	public void writeRequest(OutputStream os) throws IOException {
		// do nothing
	}
	
	@Override
	public long contentLength() {
		return getContentLength();
	}

	@Override
	public String contentType() {
		return getContentType();
	}
}
