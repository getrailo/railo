package railo.runtime.net.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import railo.commons.lang.Pair;
import railo.commons.lang.StringUtil;
import railo.runtime.op.Caster;

public final class ReqRspUtil {

	public static String get(Pair[] items, String name) {
		for(int i=0;i<items.length;i++) {
			if(items[i].getName().equalsIgnoreCase(name)) 
				return Caster.toString(items[i].getValue(),null);
		}
		return null;
	}
	
	public static Pair[] add(Pair[] items, String name, Object value) {
		Pair[] tmp = new Pair[items.length+1];
		for(int i=0;i<items.length;i++) {
			tmp[i]=items[i];
		}
		tmp[items.length]=new Pair(name,value);
		return tmp;
	}
	
	public static Pair[] set(Pair[] items, String name, Object value) {
		for(int i=0;i<items.length;i++) {
			if(items[i].getName().equalsIgnoreCase(name)) {
				items[i]=new Pair(name,value);
				return items;
			}
		}
		 return add(items, name, value);
	}

	/**
	 * return path to itself
	 * @param req
	 */
	public static String self(HttpServletRequest req) {
		StringBuffer sb=new StringBuffer(req.getServletPath());
		String qs=req.getQueryString();
		if(!StringUtil.isEmpty(qs))sb.append('?').append(qs);
		return sb.toString();
	}

	public static void setContentLength(HttpServletResponse rsp, int length) {
		rsp.setContentLength(length);
	}
	public static void setContentLength(HttpServletResponse rsp, long length) {
		if(length <= Integer.MAX_VALUE){
			setContentLength(rsp,(int)length);
		}
		else{
			rsp.addHeader("Content-Length", Caster.toString(length));
		}
	}
}
