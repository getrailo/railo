package railo.transformer.bytecode;

import org.objectweb.asm.Label;

import railo.transformer.bytecode.statement.FlowControlBreak;
import railo.transformer.bytecode.statement.FlowControlContinue;

public abstract class FlowControlBody extends BodyBase implements FlowControlBreak,FlowControlContinue {
	
	private Label end = new Label();
	
	@Override
	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		
		super._writeOut(bc);
		bc.getAdapter().visitLabel(end);
	}

	@Override
	public Label getBreakLabel() {
		return end;
	}

	@Override
	public Label getContinueLabel() {
		return end;
	}

	@Override
	public String getLabel() {
		return null;
	}
}
