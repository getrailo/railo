package railo.commons.net.http.httpclient3;

import railo.commons.net.http.Header;

public class HeaderImpl extends org.apache.commons.httpclient.Header implements Header {

	public HeaderImpl(String name,String value) {
		super(name,value);
	}
}
