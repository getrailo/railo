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
/**
 * Implements the CFML Function setprofilestring
 */
package railo.runtime.functions.system;

import java.io.IOException;

import railo.commons.io.ini.IniFile;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

public final class SetProfileString implements Function {
	public static String call(PageContext pc , String fileName, String section, String key, String value) throws PageException {
	    try {
	    	Resource res = ResourceUtil.toResourceNotExisting(pc,fileName);
            IniFile ini = new IniFile(res);
            ini.setKeyValue(section, key,value);
            ini.save();
        } 
        catch (IOException e) {
            throw Caster.toPageException(e);
        }
        return "";
	}
}