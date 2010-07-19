package railo.transformer.bytecode.visitor;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import railo.transformer.bytecode.BytecodeContext;

public final class ConditionVisitor {

	private Label end;
	private Label endIf;

	public void visitBefore() {
		end = new Label();
	}
	
	public void visitWhenBeforeExpr() {}

	public void visitWhenAfterExprBeforeBody(BytecodeContext bc){
        	endIf = new Label();
            bc.getAdapter().ifZCmp(Opcodes.IFEQ, endIf);
	}
	
	public void visitWhenAfterBody(BytecodeContext bc)	{
        bc.getAdapter().visitJumpInsn(Opcodes.GOTO, end);
        bc.getAdapter().visitLabel(endIf);
	}

	public void visitOtherviseBeforeBody(){}
	
	public void visitOtherviseAfterBody()	{}

	public void visitAfter(BytecodeContext bc) {
		bc.getAdapter().visitLabel(end);
	}
	
}
