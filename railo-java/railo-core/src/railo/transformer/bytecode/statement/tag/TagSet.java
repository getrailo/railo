package railo.transformer.bytecode.statement.tag;

import org.objectweb.asm.Type;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.util.ASMUtil;

public final class TagSet extends TagBase  {

	/**
	 * Constructor of the class
	 * @param tag
	 */
	public TagSet(int line) {
		super(line);
	}
	public TagSet(int sl,int el) {
		super(sl,el);
	}

	/**
	 *
	 * @see railo.transformer.bytecode.statement.StatementBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter)
	 */
	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		Type rtn = getAttribute("noname").getValue().writeOut(bc, Expression.MODE_VALUE);
		// TODO sollte nicht auch long geprüft werden?
		ASMUtil.pop(bc.getAdapter(), rtn);
		//if(rtn.equals(Types.DOUBLE_VALUE))bc.getAdapter().pop2();
		//else bc.getAdapter().pop();
	}

}
