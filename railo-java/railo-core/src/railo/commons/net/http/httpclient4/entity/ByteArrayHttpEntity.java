package railo.commons.net.http.httpclient4.entity;

import org.apache.http.Header;
import org.apache.http.entity.ByteArrayEntity;

import railo.commons.lang.StringUtil;

public class ByteArrayHttpEntity extends ByteArrayEntity implements Entity4 {
 
	
	private String strContentType;
	private int contentLength;

	public ByteArrayHttpEntity(byte[] barr, String contentType) {
		super(barr);
		contentLength=barr==null?0:barr.length;
		
		if(StringUtil.isEmpty(contentType,true)) {
			Header h = getContentType();
			if(h!=null) strContentType=h.getValue();
		}
		else this.strContentType=contentType;
	}

	@Override
	public long contentLength() {
		return contentLength;
	}

	@Override
	public String contentType() {
		return strContentType;
	}

}
