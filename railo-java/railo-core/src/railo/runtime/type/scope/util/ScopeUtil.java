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
package railo.runtime.type.scope.util;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import railo.commons.net.URLDecoder;
import railo.commons.net.URLItem;
import railo.runtime.net.http.ReqRspUtil;

public class ScopeUtil {

	public static Map<String,String[]> getParameterMap(URLItem[][] itemsArr, String[] encodings) {
		String n,v;
		String[] arr;
		Map<String,String[]> parameters=new HashMap<String, String[]>();
		URLItem[] items;
		String encoding;
		for(int x=0;x<itemsArr.length;x++){
			items=itemsArr[x];
			encoding=encodings[x];
			for(int i=0;i<items.length;i++){
				n=items[i].getName();
				v=items[i].getValue();
				if(items[i].isUrlEncoded()) {
					try{
					n=URLDecoder.decode(n,encoding,true);
	            	v=URLDecoder.decode(v,encoding,true);
					}
					catch(UnsupportedEncodingException e){}
				}
				arr=parameters.get(n);
				if(arr==null)parameters.put(n, new String[]{v});
				else {
					String[] tmp = new String[arr.length+1];
					System.arraycopy(arr, 0, tmp, 0, arr.length);
					tmp[arr.length]=v;
					parameters.put(n, tmp);
				}
			}
		}
		return parameters;
	}
	

	public static String[] getParameterValues(URLItem[][] itemsArr, String[] encodings,String name) {
		String n,v;
		String encName;
		
		String[] arr=null;
		URLItem[] items;
		String encoding;
		for(int x=0;x<itemsArr.length;x++){
			items=itemsArr[x];
			encoding=encodings[x];
			if(ReqRspUtil.needEncoding(name, false)) encName=ReqRspUtil.encode(name, encoding);
			else encName=null;
			for(int i=0;i<items.length;i++){
				n=items[i].getName();
				if(!name.equals(n) && (encName==null || !encName.equals(n))) {
					continue;
				}
				v=items[i].getValue();
				if(items[i].isUrlEncoded()) {
					try{
						n=URLDecoder.decode(n,encoding,true);
						v=URLDecoder.decode(v,encoding,true);
					}
					catch(UnsupportedEncodingException e){}
				}
				if(arr==null)arr=new String[]{v};
				else {
					String[] tmp = new String[arr.length+1];
					System.arraycopy(arr, 0, tmp, 0, arr.length);
					tmp[arr.length]=v;
					arr=tmp;
				}
			}
		}
		return arr;
	}

}
