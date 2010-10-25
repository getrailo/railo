package railo.transformer.bytecode.statement.tag;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.util.Types;

public final class TagReThrow extends TagBase {

	/**
	 * Constructor of the class
	 * @param tag
	 */
	public TagReThrow(int line) {
		super(line);
	}
	public TagReThrow(int sl,int el) {
		super(sl,el);
	}

	// void throwCatch()
	private static final Method THROW_CATCH = new Method("throwCatch",Type.VOID_TYPE,new Type[]{});

	/**
	 *
	 * @see railo.transformer.bytecode.statement.StatementBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter)
	 */
	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		bc.getAdapter().loadArg(0);
		bc.getAdapter().invokeVirtual(Types.PAGE_CONTEXT, THROW_CATCH);
	}

}
