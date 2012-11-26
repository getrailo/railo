package railo.transformer.bytecode.statement;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.literal.LitBoolean;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.bytecode.util.ExpressionUtil;

public final class For extends StatementBaseNoFinal implements FlowControlBreak,FlowControlContinue,HasBody {

	private Expression init;
	private Expression condition;
	private Expression update;
	private Body body;
	
	//private static final int I=1;

	Label beforeUpdate = new Label();
	Label end = new Label();
	private String label;
	
	
	
	/**
	 * Constructor of the class
	 * @param init
	 * @param condition
	 * @param update
	 * @param body
	 * @param line
	 */
	public For(Expression init,Expression condition,Expression update,Body body,Position start, Position end, String label) {
		super(start,end);
		this.init=init;
		this.condition=condition;
		this.update=update;
		this.body=body;
		this.label=label;
		body.setParent(this);
		
	}

	@Override
	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		GeneratorAdapter adapter = bc.getAdapter();
		Label beforeInit = new Label();
		Label afterInit = new Label();
		Label afterUpdate = new Label();

		ExpressionUtil.visitLine(bc, getStart());
		adapter.visitLabel(beforeInit);
		if(init!=null) {
			init.writeOut(bc, Expression.MODE_VALUE);
			adapter.pop();
		}
		adapter.visitJumpInsn(Opcodes.GOTO, afterUpdate);
		adapter.visitLabel(afterInit);
		
		body.writeOut(bc);
		
		adapter.visitLabel(beforeUpdate);
		//ExpressionUtil.visitLine(bc, getStartLine());
		if(update!=null) {
			update.writeOut(bc, Expression.MODE_VALUE);
			ASMUtil.pop(adapter,update, Expression.MODE_VALUE); 
		}
		//ExpressionUtil.visitLine(bc, getStartLine());
		adapter.visitLabel(afterUpdate);
		
		if(condition!=null)condition.writeOut(bc, Expression.MODE_VALUE);
		else LitBoolean.TRUE.writeOut(bc, Expression.MODE_VALUE);
		adapter.visitJumpInsn(Opcodes.IFNE, afterInit);
		//ExpressionUtil.visitLine(bc, getEndLine());
		adapter.visitLabel(end);
		
	}

	@Override
	public Label getBreakLabel() {
		return end;
	}

	@Override
	public Label getContinueLabel() {
		return beforeUpdate;
	}

	@Override
	public Body getBody() {
		return body;
	}

	@Override
	public String getLabel() {
		return label;
	}
}
