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
package railo.runtime.functions.file;

import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;

public class FileGetMimeType {
	public static String call(PageContext pc, Object oSrc) throws PageException {
		return call(pc, oSrc, true);
	}
	
	
	public static String call(PageContext pc, Object oSrc, boolean checkHeader) throws PageException {
		Resource src = Caster.toResource(pc,oSrc,false);
		pc.getConfig().getSecurityManager().checkFileLocation(src);
		
		// check type
        int checkingType=checkHeader?ResourceUtil.MIMETYPE_CHECK_HEADER:ResourceUtil.MIMETYPE_CHECK_EXTENSION;
        
        String mimeType = ResourceUtil.getMimeType(src, checkingType, null);
        if(StringUtil.isEmpty(mimeType,true)) return "application/octet-stream";
        return mimeType;
	}
}
