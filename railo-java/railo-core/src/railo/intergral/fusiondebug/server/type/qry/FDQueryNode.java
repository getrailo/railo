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
package railo.intergral.fusiondebug.server.type.qry;

import railo.intergral.fusiondebug.server.type.FDNodeValueSupport;
import railo.intergral.fusiondebug.server.util.FDCaster;
import railo.runtime.type.Query;

import com.intergral.fusiondebug.server.FDLanguageException;
import com.intergral.fusiondebug.server.FDMutabilityException;
import com.intergral.fusiondebug.server.IFDStackFrame;

public class FDQueryNode extends FDNodeValueSupport {

	private Query qry;
	private int row;
	private String column;

	public FDQueryNode(IFDStackFrame frame, Query qry, int row, String column) {
		super(frame);
		this.qry=qry;
		this.row=row;
		this.column=column;
	}

	public String getName() {
		return column;
	}

	@Override
	protected Object getRawValue() {
		return qry.getAt(column, row,null);
	}

	public boolean isMutable() {
		return true;
	}

	public void set(String value) throws FDMutabilityException,FDLanguageException {
		qry.setAtEL(column,row, FDCaster.unserialize(value));
	}
}
