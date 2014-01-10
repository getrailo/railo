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
	
	@Override
	public long getContentLength() {
		return 0;
	}

	@Override
	public boolean isRepeatable() {
		return true;
	}

	@Override
	public void writeTo(OutputStream os) {
		// do nothing
	}

	@Override
	public InputStream getContent() throws IOException, IllegalStateException {
		return new ByteArrayInputStream(new byte[0]);
	}

	@Override
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
