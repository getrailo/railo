package railo.transformer.bytecode.statement;

import org.objectweb.asm.Label;

public interface FlowControlBreak extends FlowControl {
		public Label getBreakLabel();
	}
