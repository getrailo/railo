package railo.transformer.bytecode.statement.tag;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.cast.CastBoolean;
import railo.transformer.bytecode.expression.ExprBoolean;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.literal.LitBoolean;
import railo.transformer.bytecode.util.Types;

public final class TagInclude extends TagBaseNoFinal {

	private final static Method DO_INCLUDE_RUN_ONCE = new Method(
			"doInclude",
			Type.VOID_TYPE,
			new Type[]{Types.STRING,Types.BOOLEAN_VALUE});
	
	public TagInclude(Position start,Position end) {
		super(start,end);
	}

	/**
	 * @see railo.transformer.bytecode.statement.tag.TagBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter)
	 */
	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		GeneratorAdapter adapter = bc.getAdapter();
		adapter.loadArg(0);
		
		// template
		getAttribute("template").getValue().writeOut(bc, Expression.MODE_REF);
		
		// run Once
		Attribute attr = getAttribute("runonce");
		ExprBoolean expr = (attr==null)?
				LitBoolean.FALSE:
				CastBoolean.toExprBoolean(attr.getValue());
		expr.writeOut(bc, Expression.MODE_VALUE);
		
		adapter.invokeVirtual(Types.PAGE_CONTEXT,DO_INCLUDE_RUN_ONCE);
	}
}
