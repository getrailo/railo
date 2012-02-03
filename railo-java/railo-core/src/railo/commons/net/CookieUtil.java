package railo.commons.net;

import org.apache.commons.httpclient.Cookie;

import railo.runtime.net.http.ReqRspUtil;

public class CookieUtil {

	public static Cookie toCookie(String domain, String name, String value, String charset) {
		if(!ReqRspUtil.needEncoding(name,false)) name=ReqRspUtil.encode(name, charset);
		if(!ReqRspUtil.needEncoding(value,false)) value=ReqRspUtil.encode(value, charset);
		
		return new Cookie(domain, name, value);
	}

}
