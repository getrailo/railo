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
package railo.runtime.interpreter.ref.op;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.Ref;

/**
 * Minus operation
 */
public final class BigMod extends Big {

	/**
	 * constructor of the class
	 * @param left
	 * @param right
	 */
	public BigMod(Ref left, Ref right) {
		super(left,right);
	}

	@Override
	public Object getValue(PageContext pc) throws PageException {
		return getLeft(pc).remainder(getRight(pc)).toString();
	}
}
