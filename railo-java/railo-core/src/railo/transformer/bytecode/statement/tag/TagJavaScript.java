package railo.transformer.bytecode.statement.tag;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.visitor.IfVisitor;

public final class TagJavaScript extends TagBaseNoFinal {
	
	public TagJavaScript(Position start,Position end) {
		super(start,end);
	}

	/**
	 * @see railo.transformer.bytecode.statement.StatementBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter)
	 */
	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		IfVisitor ifv=new IfVisitor();
		ifv.visitBeforeExpression();
			bc.getAdapter().push(true);
		ifv.visitAfterExpressionBeforeBody(bc);
			getBody().writeOut(bc);
		ifv.visitAfterBody(bc);
	}
}
