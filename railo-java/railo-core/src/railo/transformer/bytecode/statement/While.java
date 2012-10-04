package railo.transformer.bytecode.statement;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.cast.CastBoolean;
import railo.transformer.bytecode.expression.ExprBoolean;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.literal.LitBoolean;

public final class While extends StatementBase implements FlowControl,HasBody {
	
	private ExprBoolean expr;
	private Body body;
	

	private Label begin = new Label();
	private Label end = new Label();



	/**
	 * Constructor of the class
	 * @param expr
	 * @param body
	 * @param line
	 */
	public While(Expression expr,Body body,Position start,Position end) {
		super(start,end);
		this.expr=CastBoolean.toExprBoolean(expr);
		this.body=body;
		body.setParent(this);
	}
	
	
	/**
	 * Constructor of the class
	 * @param b
	 * @param body
	 * @param line
	 */
	public While(boolean b, Body body,Position start,Position end) {
		this(LitBoolean.toExprBoolean(b),body,start, end);
	}


	/**
	 *
	 * @see railo.transformer.bytecode.statement.StatementBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter)
	 */
	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		GeneratorAdapter adapter = bc.getAdapter();
		adapter.visitLabel(begin);
		
		expr.writeOut(bc, Expression.MODE_VALUE);
		adapter.ifZCmp(Opcodes.IFEQ, end);
		
		body.writeOut(bc);
		adapter.visitJumpInsn(Opcodes.GOTO, begin);
		
		adapter.visitLabel(end);
	}


	/**
	 *
	 * @see railo.transformer.bytecode.statement.FlowControl#getBreakLabel()
	 */
	public Label getBreakLabel() {
		return end;
	}


	/**
	 *
	 * @see railo.transformer.bytecode.statement.FlowControl#getContinueLabel()
	 */
	public Label getContinueLabel() {
		return begin;
	}

	/**
	 * @see railo.transformer.bytecode.statement.HasBody#getBody()
	 */
	public Body getBody() {
		return body;
	}
}
