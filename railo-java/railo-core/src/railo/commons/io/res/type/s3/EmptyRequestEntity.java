package railo.commons.io.res.type.s3;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.httpclient.methods.RequestEntity;

public class EmptyRequestEntity implements RequestEntity {

	private final String contentType;
	
	/**
	 * Constructor of the class
	 * @param contentType
	 */
	public EmptyRequestEntity(String contentType) {
		this.contentType=contentType;
	}
	
	/**
	 * Constructor of the class
	 */
	public EmptyRequestEntity() {
		this("application");
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
}