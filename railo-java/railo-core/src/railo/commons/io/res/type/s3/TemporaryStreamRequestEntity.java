package railo.commons.io.res.type.s3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.httpclient.methods.RequestEntity;

import railo.loader.util.Util;


public class TemporaryStreamRequestEntity implements RequestEntity {

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
		InputStream is=null;
		try{
			Util.copy(is=ts.getInputStream(), os);
		}
		finally{
			Util.closeEL(is);
		}
		
	}

}