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
 * Implements the CFML Function ltrim
 */
package railo.runtime.functions.string;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;

public final class LTrim extends BIF {

	private static final long serialVersionUID = -2643049222690145727L;

	public static String call(PageContext pc , String str) {
		return StringUtil.ltrim(str,"");
	}

    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==1)
			return call(pc, Caster.toString(args[0]));

		throw new FunctionException(pc, "LTrim", 1, 1, args.length);
	}
}