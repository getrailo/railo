package railo.transformer.bytecode.visitor;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import railo.transformer.bytecode.BytecodeContext;

public final class IfVisitor {

	private Label end;

	public void visitBeforeExpression() {


		end = new Label();
		
		
		
	}

	public void visitAfterExpressionBeforeBody(BytecodeContext bc) {
		bc.getAdapter().ifZCmp(Opcodes.IFEQ, end);
	}

	public void visitAfterBody(BytecodeContext bc) {
		bc.getAdapter().visitLabel(end);
	}

}
