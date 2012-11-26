package railo.transformer.bytecode.statement.tag;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.statement.FlowControl;
import railo.transformer.bytecode.statement.FlowControlFinal;
import railo.transformer.bytecode.util.ASMUtil;

public final class TagContinue extends TagBase {

	private String label;

	public TagContinue(Position start, Position end) {
		super(start,end);
		setHasFlowController(true);
	}

	@Override
	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		ASMUtil.leadFlow(bc,this,FlowControl.CONTINUE,label);
	}
	
	@Override
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
