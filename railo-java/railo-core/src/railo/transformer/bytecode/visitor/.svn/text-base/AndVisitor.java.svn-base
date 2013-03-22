package railo.transformer.bytecode.visitor;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.transformer.bytecode.BytecodeContext;

public final class AndVisitor {
	private Label end;
	private Label l2;
	public void visitBegin() {
		end = new Label();
    	l2 = new Label();
	}
	public void visitMiddle(BytecodeContext bc) {
		bc.getAdapter().ifZCmp(Opcodes.IFEQ, end);

		
		
	}
	public void visitEnd(BytecodeContext bc) {
		GeneratorAdapter adapter = bc.getAdapter();
    	adapter.ifZCmp(Opcodes.IFEQ, end);
    	adapter.push(true);
    	
    	adapter.visitJumpInsn(Opcodes.GOTO, l2);
    	adapter.visitLabel(end);

    	adapter.push(false);
    	adapter.visitLabel(l2);
	}
}
