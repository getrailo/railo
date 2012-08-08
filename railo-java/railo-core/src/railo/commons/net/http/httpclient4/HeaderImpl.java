package railo.commons.net.http.httpclient4;

import org.apache.http.message.BasicHeader;

public class HeaderImpl extends BasicHeader implements railo.commons.net.http.Header {
	
	public HeaderImpl(String name,String value){
		super(name,value);
	}
}
