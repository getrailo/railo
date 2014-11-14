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
package railo.runtime.functions.s3;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Struct;

public class StoreSetMetaData extends S3Function {
	
	public static String call(PageContext pc , String url, Struct metadata) throws PageException {
		try {
			return _call(pc, url,metadata);
		} catch (Exception e) {
			throw Caster.toPageException(e);
		}
	}

	public static String _call(PageContext pc , String url, Struct metadata) {
		//S3Resource res=toS3Resource(pc,url,"StoreGetMetaData");
		
		return null;
	}
	

	
	
}
