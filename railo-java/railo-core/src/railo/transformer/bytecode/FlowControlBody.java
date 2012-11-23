package railo.transformer.bytecode;

import org.objectweb.asm.Label;

import railo.transformer.bytecode.statement.FlowControlBreak;
import railo.transformer.bytecode.statement.FlowControlContinue;

public abstract class FlowControlBody extends BodyBase implements FlowControlBreak,FlowControlContinue {

	private Label end = new Label();
	
	/**
	 *
	 * @see railo.transformer.bytecode.Body#_writeOut(org.objectweb.asm.commons.GeneratorAdapter)
	 */
	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		
		super._writeOut(bc);
		bc.getAdapter().visitLabel(end);
	}

	/**
	 *
	 * @see railo.transformer.bytecode.statement.FlowControl#getBreakLabel()
	 */
	public Label getBreakLabel() {
		return end;
	}

	/**
	 *
	 * @see railo.transformer.bytecode.statement.FlowControl#getContinueLabel()
	 */
	public Label getContinueLabel() {
		return end;
	}
}
