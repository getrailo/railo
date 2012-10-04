package railo.transformer.bytecode.visitor;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.util.Types;

public class TryFinallyVisitor implements Opcodes {

	private Label beforeTry;
	private Label afterTry;
	private Label beforeFinally;
	private Label afterFinally;
	private int lThrow;
	private OnFinally onFinally;



	public TryFinallyVisitor(OnFinally onFinally) {
		this.onFinally=onFinally;
	}

	public void visitTryBegin(BytecodeContext bc) {
		GeneratorAdapter ga = bc.getAdapter();
		bc.pushOnFinally(onFinally);
		beforeTry = new Label();
		afterTry = new Label();
		beforeFinally = new Label();
		afterFinally = new Label();
		
		ga.visitLabel(beforeTry);
	}

	public void visitTryEnd(BytecodeContext bc) throws BytecodeException {
		GeneratorAdapter ga = bc.getAdapter();
		bc.popOnFinally();
		ga.visitJumpInsn(GOTO, beforeFinally);
		
		ga.visitLabel(afterTry);
		lThrow = ga.newLocal(Types.THROWABLE);
		ga.storeLocal(lThrow);
		onFinally.writeOut(bc);
	
		ga.loadLocal(lThrow);
		ga.visitInsn(ATHROW);
		
		bc.getAdapter().visitLabel(beforeFinally);
		
		onFinally.writeOut(bc);
		
		bc.getAdapter().visitLabel(afterFinally);
		ga.visitTryCatchBlock(beforeTry, afterTry, afterTry, null);
	}
}
