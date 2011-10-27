package railo.commons.net;

import org.apache.commons.httpclient.Cookie;

import railo.commons.lang.StringUtil;
import railo.runtime.net.http.ReqRspUtil;

public class CookieUtil {

	public static Cookie toCookie(String domain, String name, String value, String charset) {
		if(!StringUtil.isAscci(name)) name=ReqRspUtil.encode(name, charset);
		if(!StringUtil.isAscci(value)) value=ReqRspUtil.encode(value, charset);
		
		return new Cookie(domain, name, value);
	}

}
