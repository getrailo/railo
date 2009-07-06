package railo.transformer.bytecode.visitor;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.transformer.bytecode.BytecodeContext;

public final class NotVisitor {

	public static void visitNot(BytecodeContext bc) {
		GeneratorAdapter adapter = bc.getAdapter();

		Label l1=new Label();
		adapter.visitJumpInsn(Opcodes.IFEQ, l1);
		adapter.visitInsn(Opcodes.ICONST_0);
		Label l2 = new Label();
		adapter.visitJumpInsn(Opcodes.GOTO, l2);
		adapter.visitLabel(l1);
		adapter.visitInsn(Opcodes.ICONST_1);
		adapter.visitLabel(l2);
	}

}
