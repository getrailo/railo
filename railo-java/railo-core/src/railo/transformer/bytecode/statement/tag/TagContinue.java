package railo.transformer.bytecode.statement.tag;

import org.objectweb.asm.Opcodes;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.statement.FlowControl;
import railo.transformer.bytecode.statement.FlowControlContinue;
import railo.transformer.bytecode.util.ASMUtil;

public final class TagContinue extends TagBase {

	public TagContinue(Position start, Position end) {
		super(start,end);
		setHasFlowController(true);
	}

	/**
	 *
	 * @see railo.transformer.bytecode.statement.StatementBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter)
	 */
	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		FlowControlContinue ls = ASMUtil.getAncestorContinueFCStatement(this);
		if(ls!=null)
			bc.getAdapter().visitJumpInsn(Opcodes.GOTO, ls.getContinueLabel());
		else throw new BytecodeException("continue must be inside a loop (for,while,do-while,loop ...)",getStart());
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
