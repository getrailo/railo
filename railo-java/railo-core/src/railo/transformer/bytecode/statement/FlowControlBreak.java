package railo.transformer.bytecode.statement;

import org.objectweb.asm.Label;

public interface FlowControlBreak {
		public Label getBreakLabel();
	}
