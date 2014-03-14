package railo.transformer.bytecode.statement.tag;

import railo.transformer.Factory;
import railo.transformer.Position;
import railo.transformer.TransformerException;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.statement.Return;
import railo.transformer.expression.Expression;

public final class TagReturn extends TagBaseNoFinal {

	public TagReturn(Factory f, Position start,Position end) {
		super(f, start,end);
		setHasFlowController(true);
	}

	/**
	 *
	 * @see railo.transformer.bytecode.statement.tag.TagBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter)
	 */
	public void _writeOut(BytecodeContext bc) throws TransformerException {
		Attribute attr = getAttribute("expr");
		Expression expr=null;
		if(attr!=null)expr=attr.getValue();
		new Return(expr,expr.getStart(),expr.getEnd()).writeOut(bc);
	}

	/**
	 *
	 * @see railo.transformer.bytecode.statement.StatementBase#setParent(railo.transformer.bytecode.Statement)
	 */
	public void setParent(Statement parent) {
		super.setParent(parent);
		parent.setHasFlowController(true);
	}

}
