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
	
	@Override
	public long getContentLength() {
		return ts.length();
	}

	@Override
	public boolean isRepeatable() {
		return false;
	}

	@Override
	public void writeTo(OutputStream os) throws IOException {
		IOUtil.copy(ts.getInputStream(), os,true,false);
	}

	@Override
	public InputStream getContent() throws IOException, IllegalStateException {
		return ts.getInputStream();
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
		return ct;
	}
}