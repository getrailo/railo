package railo.transformer.bytecode.statement.tag;

import org.objectweb.asm.Type;

import railo.transformer.Factory;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.expression.Expression;

public final class TagSet extends TagBaseNoFinal  {

	public TagSet(Factory f, Position start,Position end) {
		super(f,start,end);
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
