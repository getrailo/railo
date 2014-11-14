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
package railo.transformer.bytecode.statement.tag;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.statement.FlowControlFinal;
import railo.transformer.bytecode.statement.FlowControlFinalImpl;

public class TagOther extends TagBase {

	private FlowControlFinalImpl fcf;
	
	public TagOther(Position start, Position end) {
		super(start, end);
	}
	
	@Override
	public FlowControlFinal getFlowControlFinal(){
		if(fcf==null && getTagLibTag().handleException())
			fcf=new FlowControlFinalImpl();
		return fcf;
	}
	
	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		_writeOut(bc,true,getFlowControlFinal());
	}

}
