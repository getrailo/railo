package railo.transformer.bytecode.visitor;

import org.objectweb.asm.Label;

import railo.transformer.bytecode.BytecodeContext;

public interface LoopVisitor {
	
	public void visitContinue(BytecodeContext bc);
		
	public void visitBreak(BytecodeContext bc);

	public Label getContinueLabel();

	public Label getBreakLabel();
}
