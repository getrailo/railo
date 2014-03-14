package railo.transformer.bytecode.statement.tag;

import railo.transformer.Factory;
import railo.transformer.Position;
import railo.transformer.TransformerException;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.statement.FlowControl;
import railo.transformer.bytecode.statement.FlowControlFinal;
import railo.transformer.bytecode.util.ASMUtil;

public final class TagBreak extends TagBase {
	
	private String label;

	public TagBreak(Factory f, Position start,Position end) {
		super(f,start,end);
		setHasFlowController(true);
	}

	/**
	 * @see railo.transformer.bytecode.statement.StatementBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter)
	 */
	public void _writeOut(BytecodeContext bc) throws TransformerException {
		
		
		ASMUtil.leadFlow(bc,this,FlowControl.BREAK,label);
	}
	
	/**
	 *
	 * @see railo.transformer.bytecode.statement.StatementBase#setParent(railo.transformer.bytecode.Statement)
	 */
	public void setParent(Statement parent) {
		super.setParent(parent);
		parent.setHasFlowController(true);
	}
	
	@Override
	public FlowControlFinal getFlowControlFinal() {
		return null;
	}

	public void setLabel(String label) {
		this.label=label;
	}
}
