package railo.transformer.bytecode.visitor;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.transformer.bytecode.BytecodeContext;

public final class TryFinallyVisitor2 implements ITryFinallyVisitor {


	private Label tryBegin = new Label(); 
	private Label finallyBegin = new Label(); 
	private Label tryEnd = new Label(); 
	private Label end = new Label(); 
	private Label l4 = new Label(); 
    
	private TryCatchFinallyData data=new TryCatchFinallyData(l4);
	//private int lThrow1;

	public void visitTryBegin(BytecodeContext bc) {
		bc.getAdapter().visitLabel(tryBegin);
		bc.pushTryCatchFinallyData(data);
	}
	
	public void visitTryEndFinallyBegin(BytecodeContext bc) {
		GeneratorAdapter adapter = bc.getAdapter();

        adapter.goTo(finallyBegin); 
        adapter.visitLabel(tryEnd); 
        adapter.visitVarInsn(Opcodes.ASTORE, 4); 
        adapter.visitJumpInsn(Opcodes.JSR, l4); 
        Label l5 = new Label(); 
        adapter.visitLabel(l5);  
        adapter.visitVarInsn(Opcodes.ALOAD, 4); 
        adapter.visitInsn(Opcodes.ATHROW); 
        adapter.visitLabel(l4);  
        adapter.visitVarInsn(Opcodes.ASTORE, 3); 
        Label l6 = new Label(); 
        adapter.visitLabel(l6); 
	}
	
	
	public void visitFinallyEnd(BytecodeContext bc) {
		bc.popTryCatchFinallyData();
		GeneratorAdapter mv = bc.getAdapter();
		mv.visitVarInsn(Opcodes.RET, 3); 
        mv.visitLabel(finallyBegin); 
        mv.visitJumpInsn(Opcodes.JSR, l4); 
        mv.visitLabel(end);

        mv.visitTryCatchBlock(tryBegin, tryEnd, tryEnd, null); 
        mv.visitTryCatchBlock(finallyBegin, end, tryEnd, null); 
	}
}
