package railo.transformer.bytecode.statement.tag;

import org.objectweb.asm.Label;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.statement.FlowControl;
import railo.transformer.bytecode.visitor.WhileVisitor;

public final class TagWhile extends TagBase implements FlowControl {

	private WhileVisitor wv;

	/**
	 * Constructor of the class
	 * @param tag
	 */
	public TagWhile(int line) {
		super(line);
	}
	public TagWhile(int sl,int el) {
		super(sl,el);
	}


	/**
	 * @see railo.transformer.bytecode.statement.StatementBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter)
	 */
	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		wv = new WhileVisitor();
		wv.visitBeforeExpression(bc);
			getAttribute("condition").getValue().writeOut(bc, Expression.MODE_VALUE);
		wv.visitAfterExpressionBeforeBody(bc);
			getBody().writeOut(bc);
		wv.visitAfterBody(bc,getEndLine());
	}


	/**
	 * @see railo.transformer.bytecode.statement.FlowControl#getBreakLabel()
	 */
	public Label getBreakLabel() {
		return wv.getBreakLabel();
	}


	/**
	 * @see railo.transformer.bytecode.statement.FlowControl#getContinueLabel()
	 */
	public Label getContinueLabel() {
		return wv.getContinueLabel();
	}

}
