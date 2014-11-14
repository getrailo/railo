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

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.statement.FlowControlFinal;
import railo.transformer.bytecode.statement.FlowControlFinalImpl;
import railo.transformer.bytecode.util.Types;
import railo.transformer.bytecode.visitor.NotVisitor;
import railo.transformer.bytecode.visitor.OnFinally;
import railo.transformer.bytecode.visitor.TryFinallyVisitor;

public final class TagSilent extends TagBase {

	// boolean setSilent()
	private static final Method SET_SILENT = new Method(
			"setSilent",
			Types.BOOLEAN_VALUE,
			new Type[]{}
	);

	// boolean unsetSilent();
	private static final Method UNSET_SILENT = new Method(
			"unsetSilent",
			Types.BOOLEAN_VALUE,
			new Type[]{}
	);

	private FlowControlFinalImpl fcf;
	
	public TagSilent(Position start,Position end) {
		super(start,end);
	}

	/**
	 *
	 * @see railo.transformer.bytecode.statement.tag.TagBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter)
	 */
	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		final GeneratorAdapter adapter = bc.getAdapter();
		
		final int silentMode=adapter.newLocal(Types.BOOLEAN_VALUE);
		
		// boolean silentMode= pc.setSilent();
		adapter.loadArg(0);
		adapter.invokeVirtual(Types.PAGE_CONTEXT, SET_SILENT);
		adapter.storeLocal(silentMode);
		
		// call must be 
		TryFinallyVisitor tfv=new TryFinallyVisitor(new OnFinally() {
			public void _writeOut(BytecodeContext bc) {
				//if(fcf!=null && fcf.getAfterFinalGOTOLabel()!=null)ASMUtil.visitLabel(adapter,fcf.getFinalEntryLabel());
				// if(!silentMode)pc.unsetSilent();
				Label _if=new Label();
				adapter.loadLocal(silentMode);
				NotVisitor.visitNot(bc);
				adapter.ifZCmp(Opcodes.IFEQ, _if);
					adapter.loadArg(0);
					adapter.invokeVirtual(Types.PAGE_CONTEXT, UNSET_SILENT);
					adapter.pop();
				
				adapter.visitLabel(_if);
				/*if(fcf!=null) {
					Label l = fcf.getAfterFinalGOTOLabel();
					if(l!=null)adapter.visitJumpInsn(Opcodes.GOTO, l);
				}*/
			}
		},getFlowControlFinal());
		tfv.visitTryBegin(bc);
			getBody().writeOut(bc);
		tfv.visitTryEnd(bc);

	}

	@Override
	public FlowControlFinal getFlowControlFinal() {
		if(fcf==null)fcf = new FlowControlFinalImpl();
		return fcf;
	}

}
