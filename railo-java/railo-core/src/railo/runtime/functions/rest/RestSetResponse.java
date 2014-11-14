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
package railo.runtime.functions.rest;

import javax.servlet.http.HttpServletRequest;

import railo.runtime.PageContext;
import railo.runtime.exp.ApplicationException;
import railo.runtime.rest.Result;
import railo.runtime.type.Struct;

public class RestSetResponse {
	public static String call(PageContext pc , Struct rsp) throws ApplicationException {
		HttpServletRequest req = pc.getHttpServletRequest();
		
		Result result = (Result) req.getAttribute("rest-result");
		if(result==null)throw new ApplicationException("not inside a REST Request");
		
		result.setCustomResponse(rsp);
		
		
		return null;
	}
}
