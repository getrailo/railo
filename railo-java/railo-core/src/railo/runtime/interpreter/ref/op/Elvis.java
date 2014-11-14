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
import railo.runtime.interpreter.ref.RefSupport;
import railo.runtime.interpreter.ref.literal.LFunctionValue;
import railo.runtime.interpreter.ref.var.Variable;

public class Elvis  extends RefSupport implements Ref{

	private Ref left;
	private Ref right;

	public Elvis(Ref left, Ref right) {
		this.left=left;
		this.right=right; 
	}


    @Override
	public Object getValue(PageContext pc) throws PageException {
    	if(left instanceof Variable) {
    		Variable var = (Variable)left;
    		String[] arr = LFunctionValue.toStringArray(pc, var);
        	return railo.runtime.op.Elvis.operate(pc, arr)?left.getValue(pc):right.getValue(pc);
    	}
    	
    	Object val = left.getValue(pc);
    	if(val!=null) return val;
    	return right.getValue(pc);
    	
    }

    @Override
    public String getTypeName() {
        return "operation";
    }
}
