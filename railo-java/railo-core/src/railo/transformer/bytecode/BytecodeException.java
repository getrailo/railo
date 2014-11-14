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
package railo.transformer.bytecode;

import railo.commons.lang.ClassException;
import railo.runtime.exp.TemplateException;

public final class BytecodeException extends TemplateException {

	private Position pos;

	public BytecodeException(String message, Position pos) {
		super(message);
		this.pos=pos;
	}

	public BytecodeException(ClassException cause, Position start) {
		this(cause.getMessage(),start);
		initCause(cause);
	}

	public Position getPosition() {
		return pos;
	}

}
