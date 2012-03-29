package railo.commons.net.http.httpclient4.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.entity.AbstractHttpEntity;

import railo.commons.io.IOUtil;
import railo.commons.io.TemporaryStream;


public class TemporaryStreamHttpEntity extends AbstractHttpEntity implements Entity4 {

	private final TemporaryStream ts;
	private String ct;

	public TemporaryStreamHttpEntity(TemporaryStream ts,String contentType) {
		this.ts=ts;
		setContentType(contentType);
		this.ct=contentType;
	}
	
	/**
	 * @see org.apache.http.HttpEntity#getContentLength()
	 */
	public long getContentLength() {
		return ts.length();
	}

	/**
	 * @see org.apache.http.HttpEntity#isRepeatable()
	 */
	public boolean isRepeatable() {
		return false;
	}

	/**
	 * @see org.apache.http.HttpEntity#writeTo(java.io.OutputStream)
	 */
	public void writeTo(OutputStream os) throws IOException {
		IOUtil.copy(ts.getInputStream(), os,true,false);
	}

	/**
	 * @see org.apache.http.HttpEntity#getContent()
	 */
	public InputStream getContent() throws IOException, IllegalStateException {
		return ts.getInputStream();
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
		return ct;
	}
}