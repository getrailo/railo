package railo.runtime.functions.other;

import javax.servlet.http.Cookie;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class URLSessionFormat implements Function {
    
    public static String call(PageContext pc, String strUrl) {
        boolean hasCFID=false;
        Cookie[] cookies = pc. getHttpServletRequest().getCookies();
        
        if(!pc.getApplicationContext().isSetClientCookies() || cookies==null) {
            int indexQ=strUrl.indexOf('?');
            int indexA=strUrl.indexOf('&');
            int len=strUrl.length();
            if(indexQ==len-1 || indexA==len-1)strUrl+=pc.getURLToken();
            else if(indexQ!=-1)strUrl+="&"+pc.getURLToken();
            else strUrl+="?"+pc.getURLToken();
        }
        
        return strUrl;
    }
    

}