package railo.commons.net.http.httpclient3;

import railo.commons.net.http.Header;

public class HeaderWrap implements Header {

	private org.apache.commons.httpclient.Header header;

	public HeaderWrap(org.apache.commons.httpclient.Header header) {
		this.header=header;
	}

	@Override
	public String getName() {
		return header.getName();
	}

	@Override
	public String getValue() {
		return header.getValue();
	}
	
	@Override
	public String toString(){
		return header.toString();
	}
	
	@Override
	public boolean equals(Object obj){
		return header.equals(obj);
	}
}