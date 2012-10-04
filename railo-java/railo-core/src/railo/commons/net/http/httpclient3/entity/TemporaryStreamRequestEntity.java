package railo.commons.net.http.httpclient3.entity;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.httpclient.methods.RequestEntity;

import railo.commons.io.IOUtil;
import railo.commons.io.TemporaryStream;


public class TemporaryStreamRequestEntity implements RequestEntity, Entity3 {

	private final TemporaryStream ts;
	private final String contentType;

	public TemporaryStreamRequestEntity(TemporaryStream ts) {
		this(ts,"application");
	}
	public TemporaryStreamRequestEntity(TemporaryStream ts,String contentType) {
		this.ts=ts;
		this.contentType=contentType;
	}
	
	/**
	 * @see org.apache.commons.httpclient.methods.RequestEntity#getContentLength()
	 */
	public long getContentLength() {
		return ts.length();
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
		return false;
	}

	/**
	 * @see org.apache.commons.httpclient.methods.RequestEntity#writeRequest(java.io.OutputStream)
	 */
	public void writeRequest(OutputStream os) throws IOException {
		IOUtil.copy(ts.getInputStream(), os,true,false);
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
