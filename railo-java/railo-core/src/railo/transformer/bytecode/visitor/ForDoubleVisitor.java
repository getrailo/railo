package railo.transformer.bytecode.visitor;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.util.ExpressionUtil;
import railo.transformer.bytecode.util.Types;

public final class ForDoubleVisitor implements Opcodes, LoopVisitor {

	public Label beforeInit=new Label();
	public Label beforeExpr=new Label(),afterExpr=new Label();
	public Label beforeBody=new Label(),afterBody=new Label();
	public Label beforeUpdate=new Label(),afterUpdate=new Label();
	public int i;
	
	public int visitBeforeExpression(GeneratorAdapter adapter, int start, int step, boolean isLocal) {
		// init
		adapter.visitLabel(beforeInit);
		forInit(adapter, start, isLocal);
		adapter.goTo(beforeExpr);
		
		// update
		adapter.visitLabel(beforeUpdate);
		forUpdate(adapter, step, isLocal);
		
		// expression
		adapter.visitLabel(beforeExpr);
		return i;
	}

	public void visitAfterExpressionBeginBody(GeneratorAdapter adapter) {
		adapter.ifZCmp(Opcodes.IFEQ, afterBody);
	}
	
	public void visitEndBody(BytecodeContext bc,Position line) {
		bc.getAdapter().goTo(beforeUpdate);
		ExpressionUtil.visitLine(bc, line);
		bc.getAdapter().visitLabel(afterBody);
		//adapter.visitLocalVariable("i", "I", null, beforeInit, afterBody, i);
	}

	
	public void forInit(GeneratorAdapter adapter, int start, boolean isLocal) {
		i=adapter.newLocal(Types.DOUBLE_VALUE); 
		if(isLocal)adapter.loadLocal(start,Types.DOUBLE_VALUE);
		else adapter.push((double)start);
		adapter.visitVarInsn(DSTORE, i);
	}
	
	public void forUpdate(GeneratorAdapter adapter, int step, boolean isLocal) {
		if(isLocal) {
			adapter.visitVarInsn(DLOAD, i);
			adapter.loadLocal(step);
			adapter.visitInsn(DADD);
			adapter.visitVarInsn(DSTORE, i);
		}
		else adapter.visitIincInsn(i, step);
	}

	/**
	 *
	 * @see railo.transformer.bytecode.visitor.LoopVisitor#visitContinue(org.objectweb.asm.commons.GeneratorAdapter)
	 */
	public void visitContinue(BytecodeContext bc) {
		bc.getAdapter().visitJumpInsn(Opcodes.GOTO, beforeUpdate);
	}
	
	/**
	 *
	 * @see railo.transformer.bytecode.visitor.LoopVisitor#visitBreak(org.objectweb.asm.commons.GeneratorAdapter)
	 */
	public void visitBreak(BytecodeContext bc) {
		bc.getAdapter().visitJumpInsn(Opcodes.GOTO, afterBody);
	}

	/**
	 *
	 * @see railo.transformer.bytecode.visitor.LoopVisitor#getContinueLabel()
	 */
	public Label getContinueLabel() {
		return beforeUpdate;
	}

	/**
	 *
	 * @see railo.transformer.bytecode.visitor.LoopVisitor#getBreakLabel()
	 */
	public Label getBreakLabel() {
		return afterBody;
	}
	
}
