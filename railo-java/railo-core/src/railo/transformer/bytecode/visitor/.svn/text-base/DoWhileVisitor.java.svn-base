package railo.transformer.bytecode.visitor;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.transformer.bytecode.BytecodeContext;

// TODO testen wurde noch nicht getestet

public final class DoWhileVisitor implements LoopVisitor{

	private Label begin;
	private Label end;
	private Label beforeEnd;

	public void visitBeginBody(GeneratorAdapter mv) {
		end = new Label();
		beforeEnd = new Label();
		
		begin=new Label();
		mv.visitLabel(begin);
	}
	

	public void visitEndBodyBeginExpr(GeneratorAdapter mv) {
		mv.visitLabel(beforeEnd);
	}

	public void visitEndExpr(GeneratorAdapter mv) {
		mv.ifZCmp(Opcodes.IFNE, begin);
		mv.visitLabel(end);
	}

	/**
	 * @see railo.transformer.bytecode.visitor.LoopVisitor#getBreakLabel()
	 */
	public Label getBreakLabel() {
		return end;
	}

	/**
	 * @see railo.transformer.bytecode.visitor.LoopVisitor#getContinueLabel()
	 */
	public Label getContinueLabel() {
		return beforeEnd;
	}


	/**
	 *
	 * @see railo.transformer.bytecode.visitor.LoopVisitor#visitContinue(org.objectweb.asm.commons.GeneratorAdapter)
	 */
	public void visitContinue(BytecodeContext bc) {
		bc.getAdapter().visitJumpInsn(Opcodes.GOTO, getContinueLabel());
	}
	
	/**
	 *
	 * @see railo.transformer.bytecode.visitor.LoopVisitor#visitBreak(org.objectweb.asm.commons.GeneratorAdapter)
	 */
	public void visitBreak(BytecodeContext bc) {
		bc.getAdapter().visitJumpInsn(Opcodes.GOTO, getBreakLabel());
	}

}
