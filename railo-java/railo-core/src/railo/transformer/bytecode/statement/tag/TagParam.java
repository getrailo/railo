package railo.transformer.bytecode.statement.tag;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.cast.CastInt;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.util.Types;
import railo.transformer.cfml.evaluator.impl.Argument;

public final class TagParam extends TagBaseNoFinal {

	// void param(String type, String name, Object defaultValue)
	private static final Method PARAM_TYPE_NAME_DEFAULTVALUE = new Method(
			"param",
			Types.VOID,
			new Type[]{Types.STRING,Types.STRING,Types.OBJECT}
	);
	private static final Method PARAM_TYPE_NAME_DEFAULTVALUE_REGEX = new Method(
			"param",
			Types.VOID,
			new Type[]{Types.STRING,Types.STRING,Types.OBJECT,Types.STRING}
	);
	private static final Method PARAM_TYPE_NAME_DEFAULTVALUE_MIN_MAX = new Method(
			"param",
			Types.VOID,
			new Type[]{Types.STRING,Types.STRING,Types.OBJECT,Types.DOUBLE_VALUE,Types.DOUBLE_VALUE}
	);
	private static final Method PARAM_TYPE_NAME_DEFAULTVALUE_MAXLENGTH = new Method(
			"param",
			Types.VOID,
			new Type[]{Types.STRING,Types.STRING,Types.OBJECT,Types.INT_VALUE}
	);
	
	public TagParam(Position start,Position end) {
		super(start,end);
	}

	/**
	 *
	 * @see railo.transformer.bytecode.statement.tag.TagBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter)
	 */
	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		GeneratorAdapter adapter = bc.getAdapter();
		//PageContextImpl pc=null;
		//pc.param("", "", "");
		
		Argument.checkDefaultValue(this);
		
		// pc
		adapter.loadArg(0);
		
		// type
		Attribute attrType = getAttribute("type");
		if(attrType!=null) {
			attrType.getValue().writeOut(bc, Expression.MODE_REF);
		}
		else adapter.push("any");
		
		// name
		getAttribute("name").getValue().writeOut(bc, Expression.MODE_REF);
		
		// default
		Attribute attrDefault = getAttribute("default");
		if(attrDefault!=null) {
			attrDefault.getValue().writeOut(bc, Expression.MODE_REF);
		}
		else adapter.visitInsn(Opcodes.ACONST_NULL);
		
		Attribute attrMin = getAttribute("min");
		Attribute attrMax = getAttribute("max");
		Attribute attrPattern = getAttribute("pattern");
		Attribute maxLength = getAttribute("maxLength");

		if(attrMin!=null && attrMax!=null) {
			attrMin.getValue().writeOut(bc, Expression.MODE_VALUE);
			attrMax.getValue().writeOut(bc, Expression.MODE_VALUE);
			adapter.invokeVirtual(Types.PAGE_CONTEXT, PARAM_TYPE_NAME_DEFAULTVALUE_MIN_MAX);
		}
		else if(attrPattern!=null) {
			attrPattern.getValue().writeOut(bc, Expression.MODE_REF);
			adapter.invokeVirtual(Types.PAGE_CONTEXT, PARAM_TYPE_NAME_DEFAULTVALUE_REGEX);
		}
		else if(maxLength!=null) {
			CastInt.toExprInt(maxLength.getValue()).writeOut(bc, Expression.MODE_VALUE);
			adapter.invokeVirtual(Types.PAGE_CONTEXT, PARAM_TYPE_NAME_DEFAULTVALUE_MAXLENGTH);
		}
		else adapter.invokeVirtual(Types.PAGE_CONTEXT, PARAM_TYPE_NAME_DEFAULTVALUE);

	}

}
