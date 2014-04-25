package railo.transformer.bytecode.statement.tag;

import railo.transformer.Factory;
import railo.transformer.Position;
import railo.transformer.TransformerException;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.visitor.IfVisitor;

public final class TagScript extends TagBaseNoFinal {
	
	public TagScript(Factory f, Position start,Position end) {
		super(f,start,end);
	}

	/**
	 * @see railo.transformer.bytecode.statement.StatementBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter)
	 */
	@Override
	public void _writeOut(BytecodeContext bc) throws TransformerException {
		IfVisitor ifv=new IfVisitor();
		ifv.visitBeforeExpression();
			bc.getAdapter().push(true);
		ifv.visitAfterExpressionBeforeBody(bc);
			getBody().writeOut(bc);
		ifv.visitAfterBody(bc);
	}
}
