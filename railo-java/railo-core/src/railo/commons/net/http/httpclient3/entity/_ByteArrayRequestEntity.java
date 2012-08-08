package railo.commons.net.http.httpclient3.entity;

import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;



public class _ByteArrayRequestEntity extends ByteArrayRequestEntity implements Entity3 {
	public _ByteArrayRequestEntity(byte[] barr, String contentType){
		super(barr,contentType);
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
