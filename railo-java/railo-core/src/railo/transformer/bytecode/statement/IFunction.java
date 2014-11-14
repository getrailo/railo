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
package railo.transformer.bytecode.statement;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;

public interface IFunction {

	public static final int PAGE_TYPE_REGULAR=0;
	public static final int PAGE_TYPE_COMPONENT=1;
	public static final int PAGE_TYPE_INTERFACE=2;
	

	public static final int ARRAY_INDEX=0;
	public static final int VALUE_INDEX=1;
	
	public void writeOut(BytecodeContext bc, int type)
			throws BytecodeException;

}