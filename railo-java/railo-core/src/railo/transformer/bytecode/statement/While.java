package railo.transformer.bytecode.statement;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Position;
import railo.transformer.expression.ExprBoolean;
import railo.transformer.expression.Expression;

public final class While extends StatementBaseNoFinal implements FlowControlBreak,FlowControlContinue,HasBody {
	
	private ExprBoolean expr;
	private Body body;
	

	private Label begin = new Label();
	private Label end = new Label();
	private String label;



	/**
	 * Constructor of the class
	 * @param expr
	 * @param body
	 * @param line
	 */
	public While(Expression expr,Body body,Position start,Position end, String label) {
		super(expr.getFactory(),start,end);
		this.expr=expr.getFactory().toExprBoolean(expr);
		this.body=body;
		body.setParent(this);
		this.label=label;
	}
	
	
	/**
	 * Constructor of the class
	 * @param b
	 * @param body
	 * @param line
	 */
	public While(boolean b, Body body,Position start,Position end, String label) {
		this(body.getFactory().createLitBoolean(b),body,start, end, label);
	}

	@Override
	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		GeneratorAdapter adapter = bc.getAdapter();
		adapter.visitLabel(begin);
		
		expr.writeOut(bc, Expression.MODE_VALUE);
		adapter.ifZCmp(Opcodes.IFEQ, end);
		
		body.writeOut(bc);
		adapter.visitJumpInsn(Opcodes.GOTO, begin);
		
		adapter.visitLabel(end);
	}

	@Override
	public Label getBreakLabel() {
		return end;
	}

	@Override
	public Label getContinueLabel() {
		return begin;
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
