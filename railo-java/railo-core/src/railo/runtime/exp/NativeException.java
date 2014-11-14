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
package railo.runtime.exp;

import railo.runtime.Info;
import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.op.Caster;
import railo.runtime.reflection.Reflector;
import railo.runtime.type.Collection;
import railo.runtime.type.util.KeyConstants;


/**
 * Box a Native Exception, Native = !PageException
 */
public class NativeException extends PageExceptionImpl {

	private static final long serialVersionUID = 6221156691846424801L;
	
	private Throwable t;

    /**
	 * Standart constructor for native Exception class
	 * @param t Throwable
	 */
	public NativeException(Throwable t) {
        super(t,t.getClass().getName());
        this.t=t;
        StackTraceElement[] st = t.getStackTrace();
        if(hasRailoRuntime(st))setStackTrace(st);
        else {
        	StackTraceElement[] cst = Thread.currentThread().getStackTrace();
        	if(hasRailoRuntime(cst)){
        		StackTraceElement[] mst=new StackTraceElement[st.length+cst.length-1];
        		System.arraycopy(st, 0, mst, 0, st.length);
        		System.arraycopy(cst, 1, mst, st.length, cst.length-1);
        		
        		setStackTrace(mst);
        	}
        	else setStackTrace(st);
        }
        setAdditional(KeyConstants._Cause, t.getClass().getName());
	}

	private boolean hasRailoRuntime(StackTraceElement[] st) {
		if(st!=null)for(int i=0;i<st.length;i++){
			if(st[i].getClassName().indexOf("railo.runtime")!=-1) return true;
		}
		return false;
	}

	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
	    DumpData data = super.toDumpData(pageContext, maxlevel,dp);
	    if(data instanceof DumpTable)
        ((DumpTable)data).setTitle("Railo ["+Info.getVersionAsString()+"] - Error ("+Caster.toClassName(t)+")");
        
        return data;
    }

    @Override
    public boolean typeEqual(String type) {
    	if(super.typeEqual(type))return true;
        return Reflector.isInstaneOfIgnoreCase(t.getClass(),type);
    }

	@Override
	public void setAdditional(Collection.Key key, Object value) {
		super.setAdditional(key, value);
	}
}