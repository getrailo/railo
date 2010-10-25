package railo.transformer.bytecode.statement.tag;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.util.Types;

public final class TagInclude extends TagBase {


	private final static Method DO_INCLUDE = new Method("doInclude",Type.VOID_TYPE,new Type[]{Types.STRING});
	

	/**
	 * Constructor of the class
	 * @param line
	 */
	public TagInclude(int line) {
		super(line);
	}
	public TagInclude(int sl,int el) {
		super(sl,el);
	}

	/**
	 * @see railo.transformer.bytecode.statement.tag.TagBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter)
	 */
	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		GeneratorAdapter adapter = bc.getAdapter();
		adapter.loadArg(0);
		getAttribute("template").getValue().writeOut(bc, Expression.MODE_REF);
		adapter.invokeVirtual(Types.PAGE_CONTEXT,DO_INCLUDE);
	}
}
