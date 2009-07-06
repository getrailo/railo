package railo.transformer.bytecode.statement.tag;

import org.objectweb.asm.Opcodes;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.statement.FlowControl;
import railo.transformer.bytecode.util.ASMUtil;

public final class TagContinue extends TagBase {

	/**
	 * Constructor of the class
	 * @param tag
	 */
	public TagContinue(int sl) {
		this(sl,-1);
	}
	public TagContinue(int sl,int el) {
		super(sl,el);
		setHasFlowController(true);
	}

	/**
	 *
	 * @see railo.transformer.bytecode.statement.StatementBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter)
	 */
	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		FlowControl ls = ASMUtil.getAncestorFlowControlStatement(this);
		if(ls!=null)
			bc.getAdapter().visitJumpInsn(Opcodes.GOTO, ls.getContinueLabel());
		else throw new BytecodeException("continue must be inside a loop (for,while,do-while,loop ...)",getLine());
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
