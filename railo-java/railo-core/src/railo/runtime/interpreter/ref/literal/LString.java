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
package railo.runtime.interpreter.ref.literal;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.Ref;
import railo.runtime.interpreter.ref.RefSupport;
import railo.runtime.interpreter.ref.util.RefUtil;



/**
 * Literal String
 *
 */
public final class LString extends RefSupport implements Literal {
	
	
	private String str;

    /**
	 * constructor of the class
     * @param str 
	 */
	public LString(String str) {
        this.str=str;
	}
	

	@Override
	public Object getValue(PageContext pc) {
		return str;
	}	

	@Override
    public String toString() {
		return str;
	}

	@Override
    public String getTypeName() {
		return "literal";
	}

	@Override
    public String getString(PageContext pc) {
        return toString();
    }

	@Override
    public boolean eeq(PageContext pc,Ref other) throws PageException {
		return RefUtil.eeq(pc,this,other);
	}
}
