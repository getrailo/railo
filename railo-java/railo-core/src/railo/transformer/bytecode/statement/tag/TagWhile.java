package railo.transformer.bytecode.statement.tag;

import org.objectweb.asm.Label;

import railo.transformer.Factory;
import railo.transformer.Position;
import railo.transformer.TransformerException;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.statement.FlowControlBreak;
import railo.transformer.bytecode.statement.FlowControlContinue;
import railo.transformer.bytecode.visitor.WhileVisitor;
import railo.transformer.expression.Expression;

public final class TagWhile extends TagBaseNoFinal implements FlowControlBreak,FlowControlContinue {

	private WhileVisitor wv;
	private String label;

	public TagWhile(Factory f, Position start,Position end) {
		super(f,start,end);
	}


	/**
	 * @see railo.transformer.bytecode.statement.StatementBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter)
	 */
	@Override
	public void _writeOut(BytecodeContext bc) throws TransformerException {
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
	@Override
	public Label getBreakLabel() {
		return wv.getBreakLabel();
	}


	/**
	 * @see railo.transformer.bytecode.statement.FlowControl#getContinueLabel()
	 */
	@Override
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
