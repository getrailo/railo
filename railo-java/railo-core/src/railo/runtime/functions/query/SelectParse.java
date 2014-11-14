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
 * Implements the CFML Function isquery
 */
package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.sql.SQLParserException;
import railo.runtime.sql.SelectParser;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

public final class SelectParse implements Function {
	public static Struct call(PageContext pc , String sql) throws PageException {
		
		try {
			//Selects selects = 
			new SelectParser().parse(sql);
			Struct sct=new StructImpl();
			
			
			
			return sct;
		} 
		catch (SQLParserException e) {
			throw Caster.toPageException(e);
		}
	}
}