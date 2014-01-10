package railo.transformer.bytecode.statement.tag;

import org.objectweb.asm.Label;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.statement.FlowControlBreak;
import railo.transformer.bytecode.statement.FlowControlContinue;
import railo.transformer.bytecode.visitor.WhileVisitor;

public final class TagWhile extends TagBaseNoFinal implements FlowControlBreak,FlowControlContinue {

	private WhileVisitor wv;
	private String label;

	public TagWhile(Position start,Position end) {
		super(start,end);
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
		wv.visitAfterBody(bc,getEnd());
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


	@Override
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label=label;
	}

}
