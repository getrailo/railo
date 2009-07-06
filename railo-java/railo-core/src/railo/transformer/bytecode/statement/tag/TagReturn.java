package railo.transformer.bytecode.statement.tag;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.statement.Return;

public final class TagReturn extends TagBase {

	public TagReturn(int line) {
		this(line,-1);
	}
	public TagReturn(int sl,int el) {
		super(sl,el);
		setHasFlowController(true);
	}

	/**
	 *
	 * @see railo.transformer.bytecode.statement.tag.TagBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter)
	 */
	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		Attribute attr = getAttribute("expr");
		Expression expr=null;
		if(attr!=null)expr=attr.getValue();
		new Return(expr,-1).writeOut(bc);
		
		/*if(attr!=null)attr.getValue().writeOut(bc, Expression.MODE_REF);
		else ASMConstants.NULL(adapter);
		adapter.returnValue();
		*/
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
