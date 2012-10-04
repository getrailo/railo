package railo.commons.net.http.httpclient4;

import org.apache.http.Header;

public class HeaderWrap implements railo.commons.net.http.Header {
	public final Header header;

	public HeaderWrap(Header header){
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
