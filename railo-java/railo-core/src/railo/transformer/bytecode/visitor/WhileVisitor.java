package railo.transformer.bytecode.visitor;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.util.ExpressionUtil;

public final class WhileVisitor implements LoopVisitor {

	private Label begin;
	private Label end;

	public void visitBeforeExpression(BytecodeContext bc) {
		begin = new Label();
		end = new Label();		
		bc.getAdapter().visitLabel(begin);
	}

	public void visitAfterExpressionBeforeBody(BytecodeContext bc) {
		bc.getAdapter().ifZCmp(Opcodes.IFEQ, end);
	}

	public void visitAfterBody(BytecodeContext bc,Position endline) {
		bc.getAdapter().visitJumpInsn(Opcodes.GOTO, begin);
		bc.getAdapter().visitLabel(end);
		ExpressionUtil.visitLine(bc, endline);
	}

	/**
	 *
	 * @see railo.transformer.bytecode.visitor.LoopVisitor#visitContinue(org.objectweb.asm.commons.GeneratorAdapter)
	 */
	public void visitContinue(BytecodeContext bc) {
		bc.getAdapter().visitJumpInsn(Opcodes.GOTO, begin);
	}
	
	/**
	 *
	 * @see railo.transformer.bytecode.visitor.LoopVisitor#visitBreak(org.objectweb.asm.commons.GeneratorAdapter)
	 */
	public void visitBreak(BytecodeContext bc) {
		bc.getAdapter().visitJumpInsn(Opcodes.GOTO, end);
	}

	/**
	 *
	 * @see railo.transformer.bytecode.visitor.LoopVisitor#getContinueLabel()
	 */
	public Label getContinueLabel() {
		return begin;
	}

	/**
	 *
	 * @see railo.transformer.bytecode.visitor.LoopVisitor#getBreakLabel()
	 */
	public Label getBreakLabel() {
		return end;
	}

}
