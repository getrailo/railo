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
package railo.intergral.fusiondebug.server.type;

import java.util.List;

import railo.intergral.fusiondebug.server.type.coll.FDUDF;
import railo.intergral.fusiondebug.server.util.FDCaster;
import railo.runtime.type.UDF;

import com.intergral.fusiondebug.server.IFDStackFrame;

public abstract class FDNodeValueSupport extends FDValueSupport {
	
	private IFDStackFrame frame;

	public FDNodeValueSupport(IFDStackFrame frame){
		this.frame=frame;
	}
	
	public List getChildren() {
		return getChildren(frame,getName(),getRawValue());
	}
	

	/*public IFDValue getValue() {
		Object value = getRawValue();
		if(isSimpleValue(value))
			return getFDNodeVariableSupport();
		return FDCaster.toFDVariable(getName(), value).getValue();
	}*/

	@Override
	public String toString() {
		Object raw = getRawValue();
		if(raw instanceof UDF)return FDUDF.toString((UDF)raw);
		return FDCaster.serialize(raw);
	}
	
	@Override
	public boolean hasChildren() {
		return hasChildren(getRawValue());
	}

	protected abstract Object getRawValue();
	//protected abstract FDNodeValueSupport getFDNodeVariableSupport();
}
