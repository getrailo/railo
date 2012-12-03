package railo.transformer.bytecode.statement;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.util.ASMUtil;

public final class Continue extends StatementBaseNoFinal {

	/**
	 * Constructor of the class
	 * @param line
	 */
	public Continue(Position start,Position end) {
		super(start,end);
		setHasFlowController(true);
	}

	/**
	 * @see railo.transformer.bytecode.statement.StatementBase#_writeOut(railo.transformer.bytecode.BytecodeContext)
	 */
	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		ASMUtil.leadFlow(bc,this,FlowControl.CONTINUE);
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
