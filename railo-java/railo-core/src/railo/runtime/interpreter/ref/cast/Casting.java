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
package railo.runtime.interpreter.ref.cast;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.Ref;
import railo.runtime.interpreter.ref.RefSupport;
import railo.runtime.interpreter.ref.var.Variable;
import railo.runtime.op.Caster;

/**
 * cast
 */
public final class Casting extends RefSupport implements Ref {
    
    private final short type;
    private final String strType;
    private Ref ref;
    private Object val;
    
    /**
     * constructor of the class
     * @param pc 
     * @param strType 
     * @param type
     * @param ref
     */
    public Casting(String strType,short type, Ref ref) {
    	this.type=type;
        this.strType=strType;
        this.ref=ref;
    }
    public Casting(String strType,short type, Object val) {
    	this.type=type;
        this.strType=strType;
        this.val=val;
    }
    
    @Override
    public Object getValue(PageContext pc) throws PageException {
    	// if ref == null, it is val based Casting
    	if(ref==null) return Caster.castTo(pc,type,strType,val);
    	if(ref instanceof Variable && "queryColumn".equalsIgnoreCase(strType)) {
    		Variable var=(Variable) ref;
    		return Caster.castTo(pc,type,strType,var.getCollection(pc));
    	}
    	return Caster.castTo(pc,type,strType,ref.getValue(pc));
    }
    
    public Ref getRef() {
        return ref;
    }
    
    public String getStringType() {
        return strType;
    }
    
    public short getType() {
        return type;
    }

    public String getTypeName() {
        return "operation";
    }
    

    public String toString() {
        return strType+":"+ref+":"+val;
    }
}
