package railo.transformer.bytecode.statement.tag;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.transformer.Factory;
import railo.transformer.Position;
import railo.transformer.TransformerException;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.util.Types;
import railo.transformer.expression.ExprBoolean;
import railo.transformer.expression.Expression;

public final class TagInclude extends TagBaseNoFinal {

	private final static Method DO_INCLUDE_RUN_ONCE2 = new Method(
			"doInclude",
			Type.VOID_TYPE,
			new Type[]{Types.STRING,Types.BOOLEAN_VALUE});
	
	private final static Method DO_INCLUDE_RUN_ONCE3 = new Method(
			"doInclude",
			Type.VOID_TYPE,
			new Type[]{Types.STRING,Types.BOOLEAN_VALUE, Types.OBJECT});
	
	public TagInclude(Factory f, Position start,Position end) {
		super(f,start,end);
	}

	/**
	 * @see railo.transformer.bytecode.statement.tag.TagBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter)
	 */
	@Override
	public void _writeOut(BytecodeContext bc) throws TransformerException {
		Type type = Types.PAGE_CONTEXT;
		Method func = DO_INCLUDE_RUN_ONCE2;
		
		// cachedwithin
		Expression cachedwithin=null;
		Attribute attr = getAttribute("cachedwithin");
		if(attr!=null && attr.getValue()!=null) {
			cachedwithin = attr.getValue();
			type = Types.PAGE_CONTEXT_IMPL;
			func = DO_INCLUDE_RUN_ONCE3;
		}
		
		GeneratorAdapter adapter = bc.getAdapter();
		adapter.loadArg(0);
		if(cachedwithin!=null) adapter.checkCast(Types.PAGE_CONTEXT_IMPL);

		// template
		getAttribute("template").getValue().writeOut(bc, Expression.MODE_REF);

		// run Once
		attr = getAttribute("runonce");
		ExprBoolean expr = (attr==null)?
				bc.getFactory().FALSE():
					bc.getFactory().toExprBoolean(attr.getValue());
		expr.writeOut(bc, Expression.MODE_VALUE);

		// cachedwithin
		if(cachedwithin!=null)
			cachedwithin.writeOut(bc, Expression.MODE_REF);

		adapter.invokeVirtual(type,func);
	}
}
