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
package railo.runtime.functions.xml;

import org.xml.sax.InputSource;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.text.xml.XMLUtil;
import railo.runtime.type.Struct;

/**
 * 
 */
public final class XmlValidate implements Function {

	public static Struct call(PageContext pc, String strXml) throws PageException {
		return call(pc,strXml,null);
	}
	public static Struct call(PageContext pc, String strXml, String strValidator) throws PageException {
		strXml=strXml.trim();
		try {
			InputSource xml = XMLUtil.toInputSource(pc,strXml);
			InputSource validator = StringUtil.isEmpty(strValidator)?null:XMLUtil.toInputSource(pc,strValidator);
			return XMLUtil.validate(xml, validator,strValidator);
		} 
		catch (Exception e) {
			throw Caster.toPageException(e);
		}
		
	}
}