package railo.transformer.bytecode.visitor;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.util.Types;


public final class TryCatchFinallyVisitor {


	private Label beginTry;
	private Label endTry;
	
	private Label endTryCatch;
	private TryCatchFinallyData data;
	
	private Label beginGoToFinally;
	private Label endGoToFinally;
	private int lThrow;
	private Label l13= new Label();;
	//private boolean isFirst=true;
	
	public void visitTryBegin(BytecodeContext bc) {
		beginTry = new Label();
		endTry = new Label();
		endTryCatch = new Label();
		beginGoToFinally = new Label();
		endGoToFinally = new Label();
		data=new TryCatchFinallyData(l13);
		bc.pushTryCatchFinallyData(data);
		bc.getAdapter().visitLabel(beginTry);
	}

	public void visitTryEnd(BytecodeContext bc) {
		//bc.popTryCatchFinallyData();
		bc.getAdapter().visitJumpInsn(Opcodes.GOTO, beginGoToFinally);
		bc.getAdapter().visitLabel(endTry);
	}

	public int visitCatchBegin(BytecodeContext bc, Type type) {
		//doReturnCatch(bc);
		GeneratorAdapter adapter = bc.getAdapter();
		
		Label beginCatch=new Label();
		Label beginCatchBody=new Label();
			
		adapter.visitTryCatchBlock(beginTry, endTry, beginCatch,type.getInternalName());
		adapter.visitLabel(beginCatch);
		int lThrow = adapter.newLocal(type);
		adapter.storeLocal(lThrow);
		adapter.visitLabel(beginCatchBody);
			
		//bc.pushTryCatchFinallyData(data);
		return lThrow;
	}


	public void visitCatchEnd(BytecodeContext bc) {
		//bc.popTryCatchFinallyData();
		bc.getAdapter().visitLabel(new Label());
		bc.getAdapter().visitJumpInsn(Opcodes.GOTO, beginGoToFinally);
	}

	public int visitFinallyBegin(BytecodeContext bc) {
		GeneratorAdapter adapter = bc.getAdapter();
		adapter.visitLabel(endTryCatch);
		int lThrow2 = adapter.newLocal(Types.THROWABLE);
		adapter.storeLocal(lThrow2);
		adapter.visitJumpInsn(Opcodes.JSR, l13);
		Label l14 = new Label();
		adapter.visitLabel(l14);
		adapter.loadLocal(lThrow2);
		adapter.visitInsn(Opcodes.ATHROW);
		adapter.visitLabel(l13);
		
		lThrow = adapter.newLocal(Types.OBJECT);
		adapter.storeLocal(lThrow);
		Label l15 = new Label(); 
		adapter.visitLabel(l15);
		return lThrow;
	}

	public void visitFinallyEnd(BytecodeContext bc) {
		bc.popTryCatchFinallyData();
		GeneratorAdapter adapter = bc.getAdapter();
		Label l16 = new Label();
		adapter.visitLabel(l16);
		adapter.ret(lThrow);
		adapter.visitVarInsn(Opcodes.RET, 2);
		adapter.visitLabel(beginGoToFinally);
		adapter.visitJumpInsn(Opcodes.JSR, l13);
		adapter.visitLabel(endGoToFinally);
		adapter.visitTryCatchBlock(beginTry, endTryCatch, endTryCatch, null);
	}

}
