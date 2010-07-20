package railo.transformer.bytecode.statement;

import org.objectweb.asm.Opcodes;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.util.ASMUtil;

public final class Continue extends StatementBase {

	/**
	 * Constructor of the class
	 * @param line
	 */
	public Continue(int line) {
		super(line);
		setHasFlowController(true);
	}

	/**
	 * @see railo.transformer.bytecode.statement.StatementBase#_writeOut(railo.transformer.bytecode.BytecodeContext)
	 */
	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		FlowControl ls = ASMUtil.getAncestorFlowControlStatement(this);
		if(ls!=null)
			bc.getAdapter().visitJumpInsn(Opcodes.GOTO, ls.getContinueLabel());
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
