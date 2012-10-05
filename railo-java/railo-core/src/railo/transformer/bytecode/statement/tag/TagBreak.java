package railo.transformer.bytecode.statement.tag;

import org.objectweb.asm.Opcodes;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.statement.FlowControl;
import railo.transformer.bytecode.statement.FlowControlBreak;
import railo.transformer.bytecode.util.ASMUtil;

public final class TagBreak extends TagBase {
	
	public TagBreak(Position start,Position end) {
		super(start,end);
		setHasFlowController(true);
	}

	/**
	 * @see railo.transformer.bytecode.statement.StatementBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter)
	 */
	public void _writeOut(BytecodeContext bc) throws BytecodeException {

		FlowControlBreak fc = ASMUtil.getAncestorBreakFCStatement(this);
		
		if(fc!=null)
			bc.getAdapter().visitJumpInsn(Opcodes.GOTO, fc.getBreakLabel());
		else throw new BytecodeException("break must be inside a loop (for,while,do-while,<cfloop>,<cfwhile> ...)",getStart());
	}
	
	/**
	 *
	 * @see railo.transformer.bytecode.statement.StatementBase#setParent(railo.transformer.bytecode.Statement)
	 */
	public void setParent(Statement parent) {
		super.setParent(parent);
		parent.setHasFlowController(true);
	}

}
