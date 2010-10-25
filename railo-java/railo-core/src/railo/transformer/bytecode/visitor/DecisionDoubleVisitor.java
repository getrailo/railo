package railo.transformer.bytecode.visitor;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.transformer.bytecode.BytecodeContext;

public final class DecisionDoubleVisitor {
	/*
	public static final int GT=Opcodes.IF_ICMPGT;
	public static final int GTE=Opcodes.IF_ICMPGE;
	public static final int LT=Opcodes.IF_ICMPLT;
	public static final int LTE=Opcodes.IF_ICMPLE;
	public static final int EQ=Opcodes.IF_ICMPEQ;
	public static final int NEQ=Opcodes.IF_ICMPNE;
	*/
	public static final int GT=Opcodes.IFLE;
	public static final int GTE=Opcodes.IFLT;
	public static final int LT=Opcodes.IFGE;
	public static final int LTE=Opcodes.IFGT;
	public static final int EQ=Opcodes.IFNE;
	public static final int NEQ=Opcodes.IFEQ;
	
	private int operation;
	public void visitBegin() {

	}
	public void visitMiddle(int operation) {
		this.operation=operation;
	}
	public void visitGT() {
		this.operation=GT;
	}
	public void visitGTE() {
		this.operation=GTE;
	}
	public void visitLT() {
		this.operation=LT;
	}
	public void visitLTE() {
		this.operation=LTE;
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
		Label l2 = new Label();
		adapter.visitInsn(Opcodes.DCMPL);
		adapter.visitJumpInsn(operation, l1);
		adapter.visitInsn(Opcodes.ICONST_1);
		adapter.visitJumpInsn(Opcodes.GOTO, l2);
		adapter.visitLabel(l1);
		adapter.visitInsn(Opcodes.ICONST_0);
		adapter.visitLabel(l2);
	}
}
