package railo.transformer.bytecode.visitor;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.transformer.bytecode.BytecodeContext;

public final class DecisionObjectVisitor {

	public static final int EQ=Opcodes.IF_ACMPEQ;
	public static final int NEQ=Opcodes.IF_ACMPNE;
	
	private int operation;
	public void visitBegin() {

	}
	public void visitMiddle(int operation) {
		this.operation=operation;
	}
	public void visitEQ() {
		this.operation=EQ;
	}
	public void visitNEQ() {
		this.operation=NEQ;
	}
	public void visitEnd(BytecodeContext bc) {
		GeneratorAdapter adapter = bc.getAdapter();

		Label l1 = new Label();
		adapter.visitJumpInsn(operation, l1);
		//mv.visitJumpInsn(IF_ICMPGT, l1);
		adapter.visitInsn(Opcodes.ICONST_0);
		Label l2 = new Label();
		adapter.visitJumpInsn(Opcodes.GOTO, l2);
		adapter.visitLabel(l1);
		adapter.visitInsn(Opcodes.ICONST_1);
		adapter.visitLabel(l2);

	}
}
