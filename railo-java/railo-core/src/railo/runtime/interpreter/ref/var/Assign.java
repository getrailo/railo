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
package railo.runtime.interpreter.ref.var;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.interpreter.InterpreterException;
import railo.runtime.interpreter.ref.Ref;
import railo.runtime.interpreter.ref.RefSupport;
import railo.runtime.interpreter.ref.Set;

public final class Assign extends RefSupport implements Ref {
	
	private Ref value;
	private Set coll;


    public Assign(Ref coll, Ref value) throws ExpressionException {
        if(!(coll instanceof Set))
        	throw new InterpreterException("invalid assignment left-hand side ("+coll.getTypeName()+")");
        this.coll=(Set) coll;
        this.value=value;
    }

    public Assign(Set coll, Ref value) {
        this.coll=coll;
        this.value=value;
    }
	
    @Override
    public Object getValue(PageContext pc) throws PageException {
        return coll.setValue(pc,value.getValue(pc));
	}

    @Override
    public String getTypeName() {
		return "operation";
	}
}
