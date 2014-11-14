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
 * Implements the CFML Function getprofilesections
 */
package railo.runtime.functions.other;

import java.io.IOException;

import railo.commons.io.ini.IniFile;
import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

public final class GetProfileSections implements Function {
	public static railo.runtime.type.Struct call(PageContext pc , String fileName) throws PageException {
		try {
            return IniFile.getProfileSections(ResourceUtil.toResourceExisting(pc,fileName));
        } catch (IOException e) {
            throw Caster.toPageException(e);
        }
	}
	
	 
}