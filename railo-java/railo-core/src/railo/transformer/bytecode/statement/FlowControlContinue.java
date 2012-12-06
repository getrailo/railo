package railo.transformer.bytecode.statement;


import org.objectweb.asm.Label;

public interface FlowControlContinue {
	public Label getContinueLabel();
}
