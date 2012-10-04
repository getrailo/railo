package railo.transformer.bytecode.visitor;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.util.ExpressionUtil;
import railo.transformer.bytecode.util.Types;

public final class ForVisitor implements Opcodes,LoopVisitor {

	private Label l0= new Label();
	private Label l1= new Label();
	private Label l2= new Label();
	private Label l3= new Label();
	private int i;
	private Label lend= new Label();
	private Label lbegin= new Label();
	
	public int visitBegin(GeneratorAdapter adapter, int start, boolean isLocal) {
		adapter.visitLabel(l0);
		
		forInit(adapter, start, isLocal);
		
		
		adapter.visitLabel(l1);
		adapter.visitJumpInsn(GOTO, l2);
		adapter.visitLabel(l3);
		
		return i;
	}
	public void visitEnd(BytecodeContext bc, int end, boolean isLocal,Position startline) {
		GeneratorAdapter adapter=bc.getAdapter();
		
		adapter.visitLabel(lbegin);
		forUpdate(adapter);
		
		ExpressionUtil.visitLine(bc, startline);
		adapter.visitLabel(l2);
		adapter.visitVarInsn(ILOAD, i);
		
		if(isLocal)adapter.loadLocal(end);
		else adapter.push(end);
		adapter.visitJumpInsn(IF_ICMPLE, l3);
		
		adapter.visitLabel(lend);

		//adapter.visitLocalVariable("i", "I", null, l1, lend, i);

	}
	
	
	private void forUpdate(GeneratorAdapter adapter) {
		adapter.visitIincInsn(i, 1);
	}
	private void forInit(GeneratorAdapter adapter, int start, boolean isLocal) {
		i=adapter.newLocal(Types.INT_VALUE); 
		if(isLocal)adapter.loadLocal(start);
		else adapter.push(start);
		adapter.visitVarInsn(ISTORE, i);
	}
	
	/**
	 *
	 * @see railo.transformer.bytecode.visitor.LoopVisitor#visitContinue(org.objectweb.asm.commons.GeneratorAdapter)
	 */
	public void visitContinue(BytecodeContext bc) {
		bc.getAdapter().visitJumpInsn(Opcodes.GOTO, lbegin);
	}
	
	/**
	 *
	 * @see railo.transformer.bytecode.visitor.LoopVisitor#visitBreak(org.objectweb.asm.commons.GeneratorAdapter)
	 */
	public void visitBreak(BytecodeContext bc) {
		bc.getAdapter().visitJumpInsn(Opcodes.GOTO, lend);
	}

	/**
	 *
	 * @see railo.transformer.bytecode.visitor.LoopVisitor#getContinueLabel()
	 */
	public Label getContinueLabel() {
		return lbegin;
	}

	/**
	 *
	 * @see railo.transformer.bytecode.visitor.LoopVisitor#getBreakLabel()
	 */
	public Label getBreakLabel() {
		return lend;
	}
}
