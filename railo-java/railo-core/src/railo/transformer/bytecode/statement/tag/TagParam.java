package railo.transformer.bytecode.statement.tag;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.transformer.Factory;
import railo.transformer.Position;
import railo.transformer.TransformerException;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.util.Types;
import railo.transformer.cfml.evaluator.impl.Argument;
import railo.transformer.expression.Expression;

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
	
	public TagParam(Factory f, Position start,Position end) {
		super(f,start,end);
	}

	@Override
	public void _writeOut(BytecodeContext bc) throws TransformerException {
		GeneratorAdapter adapter = bc.getAdapter();
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

		if(attrMin!=null || attrMax!=null) {
			// min
			if(attrMin!=null)attrMin.getValue().writeOut(bc, Expression.MODE_VALUE);
			else {
				adapter.visitLdcInsn(new Double("NaN"));
			}
			// max
			if(attrMax!=null)attrMax.getValue().writeOut(bc, Expression.MODE_VALUE);
			else {
				adapter.visitLdcInsn(new Double("NaN"));
			}
			adapter.invokeVirtual(Types.PAGE_CONTEXT, PARAM_TYPE_NAME_DEFAULTVALUE_MIN_MAX);
		}
		else if(attrPattern!=null) {
			attrPattern.getValue().writeOut(bc, Expression.MODE_REF);
			adapter.invokeVirtual(Types.PAGE_CONTEXT, PARAM_TYPE_NAME_DEFAULTVALUE_REGEX);
		}
		else if(maxLength!=null) {
			bc.getFactory().toExprInt(maxLength.getValue()).writeOut(bc, Expression.MODE_VALUE);
			adapter.invokeVirtual(Types.PAGE_CONTEXT, PARAM_TYPE_NAME_DEFAULTVALUE_MAXLENGTH);
		}
		else adapter.invokeVirtual(Types.PAGE_CONTEXT, PARAM_TYPE_NAME_DEFAULTVALUE);

	}

}
