package railo.transformer.bytecode.statement;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.transformer.Position;
import railo.transformer.TransformerException;
import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.expression.ExprBoolean;
import railo.transformer.expression.Expression;

public final class DoWhile extends StatementBaseNoFinal implements FlowControlBreak,FlowControlContinue,HasBody {

	private ExprBoolean expr;
	private Body body;

	private Label begin = new Label();
	private Label beforeEnd = new Label();
	private Label end = new Label();
	private String label;
	
	
	/**
	 * Constructor of the class
	 * @param expr
	 * @param body
	 * @param line
	 */
	public DoWhile(Expression expr,Body body,Position start, Position end, String label) {
		super(expr.getFactory(),start,end);
		this.expr=expr.getFactory().toExprBoolean(expr);
		this.body=body;
		body.setParent(this);
		this.label=label;
	}
	
	@Override
	public void _writeOut(BytecodeContext bc) throws TransformerException {
		GeneratorAdapter adapter = bc.getAdapter();
		adapter.visitLabel(begin);
		body.writeOut(bc);
		
		adapter.visitLabel(beforeEnd);
		
		
		expr.writeOut(bc, Expression.MODE_VALUE);
		adapter.ifZCmp(Opcodes.IFNE, begin);
		
		adapter.visitLabel(end);
		
	}

	@Override
	public Label getBreakLabel() {
		return end;
	}

	@Override
	public Label getContinueLabel() {
		return beforeEnd;
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
