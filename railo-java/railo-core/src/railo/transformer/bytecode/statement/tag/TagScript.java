package railo.transformer.bytecode.statement.tag;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.visitor.IfVisitor;

public final class TagScript extends TagBase {
	
	/**
	 * Constructor of the class
	 * @param tag
	 */
	public TagScript(int line) {
		super(line);
	}
	public TagScript(int sl,int el) {
		super(sl,el);
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
