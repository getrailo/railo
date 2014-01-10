package railo.transformer.bytecode.statement;

import org.objectweb.asm.Label;

public interface FlowControlRetry extends FlowControl {
	public Label getRetryLabel();
}
