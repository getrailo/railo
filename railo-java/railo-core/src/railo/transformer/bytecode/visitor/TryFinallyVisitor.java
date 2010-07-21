package railo.transformer.bytecode.visitor;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.util.Types;

public final class TryFinallyVisitor implements ITryFinallyVisitor {


	private Label tryBegin 			= new Label();
	private Label tryEndCatchBegin	= new Label();
	private Label finallyBegin 		= new Label();
	private Label finallyEnd   		= new Label();
	private Label l4 = new Label();
	private TryCatchFinallyData data;
	
	private int lThrow1;

	public void visitTryBegin(BytecodeContext bc) {
		data=new TryCatchFinallyData(l4);
		bc.pushTryCatchFinallyData(data);
		bc.getAdapter().visitLabel(tryBegin);
	}
	
	public void visitTryEndFinallyBegin(BytecodeContext bc) {
		GeneratorAdapter adapter = bc.getAdapter();

		adapter.visitJumpInsn(Opcodes.GOTO, finallyBegin);
		adapter.visitLabel(tryEndCatchBegin);
		int lThrow2 = adapter.newLocal(Types.THROWABLE);
		adapter.storeLocal(lThrow2);
		//mv.visitVarInsn(ASTORE, 3);
		
		adapter.visitJumpInsn(Opcodes.JSR, l4);
		Label l5 = new Label();
		adapter.visitLabel(l5);
		adapter.loadLocal(lThrow2);
		//mv.visitVarInsn(ALOAD, 3);
		adapter.visitInsn(Opcodes.ATHROW);
		adapter.visitLabel(l4);

		lThrow1 = adapter.newLocal(Types.OBJECT);
		adapter.storeLocal(lThrow1);
		//mv.visitVarInsn(ASTORE, 2);
		Label l6 = new Label();
		adapter.visitLabel(l6);
	}
	
	
	public void visitFinallyEnd(BytecodeContext bc) {
		GeneratorAdapter adapter = bc.getAdapter();
		bc.popTryCatchFinallyData();
		Label l7 = new Label();
		adapter.visitLabel(l7);
		adapter.ret(lThrow1);
		adapter.visitVarInsn(Opcodes.RET, 2);
		adapter.visitLabel(finallyBegin);
		adapter.visitJumpInsn(Opcodes.JSR, l4);
		adapter.visitLabel(finallyEnd);
		

		adapter.visitTryCatchBlock(tryBegin, tryEndCatchBegin, tryEndCatchBegin, null);
	}
}
