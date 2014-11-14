/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
package railo.runtime.functions.other;

import javax.servlet.http.Cookie;

import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.ext.function.Function;
import railo.runtime.net.http.ReqRspUtil;

public final class URLSessionFormat implements Function {

	private static final long serialVersionUID = 1486918425114400713L;

	public static String call(PageContext pc, String strUrl) {
        Cookie[] cookies = ReqRspUtil.getCookies(pc.getHttpServletRequest(),((PageContextImpl)pc).getWebCharset());
        
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