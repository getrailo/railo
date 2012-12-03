package railo.commons.net.http.httpclient4.entity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.entity.AbstractHttpEntity;

public class EmptyHttpEntity extends AbstractHttpEntity implements Entity4 {

	
	
	private String strContentType;

	/**
	 * Constructor of the class
	 * @param contentType
	 */
	public EmptyHttpEntity(String contentType) {
		super();
		setContentType(contentType);
		strContentType=contentType;
	}
	
	/**
	 * @see org.apache.http.HttpEntity#getContentLength()
	 */
	public long getContentLength() {
		return 0;
	}

	/**
	 * @see org.apache.http.HttpEntity#isRepeatable()
	 */
	public boolean isRepeatable() {
		return true;
	}

	/**
	 * @see org.apache.http.HttpEntity#writeTo(java.io.OutputStream)
	 */
	public void writeTo(OutputStream os) {
		// do nothing
	}

	/**
	 * @see org.apache.http.HttpEntity#getContent()
	 */
	public InputStream getContent() throws IOException, IllegalStateException {
		return new ByteArrayInputStream(new byte[0]);
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
