package railo.transformer.bytecode.statement.udf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.commons.lang.CFTypes;
import railo.commons.lang.StringUtil;
import railo.runtime.Component;
import railo.runtime.exp.TemplateException;
import railo.runtime.type.FunctionArgument;
import railo.runtime.type.FunctionArgumentImpl;
import railo.runtime.type.FunctionArgumentLight;
import railo.runtime.type.util.ComponentUtil;
import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Page;
import railo.transformer.bytecode.cast.CastBoolean;
import railo.transformer.bytecode.cast.CastString;
import railo.transformer.bytecode.expression.ExprBoolean;
import railo.transformer.bytecode.expression.ExprString;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.var.Variable;
import railo.transformer.bytecode.literal.LitBoolean;
import railo.transformer.bytecode.literal.LitInteger;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.statement.tag.Attribute;
import railo.transformer.bytecode.util.ASMConstants;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.bytecode.util.ExpressionUtil;
import railo.transformer.bytecode.util.Types;

public final class Function extends AbstrFunction {

	public Function(Expression name, Expression returnType, Expression returnFormat, Expression output, Expression abstr,
			int access, Expression displayName, Expression description,Expression hint, Expression secureJson, Expression verifyClient,
			Body body, int startline, int endline) {
		super(name, returnType, returnFormat, output, abstr, access, displayName,description, hint, secureJson, verifyClient, body, startline, endline);
	}
	

	public Function(String name, int access, String returnType, Body body,int startline, int endline) {
		super(name, access, returnType, body, startline, endline);
	}

	public final void _writeOut(BytecodeContext bc, int pageType) throws BytecodeException{
		GeneratorAdapter adapter = bc.getAdapter();
		Page page = ASMUtil.getAncestorPage(this);
		int index=page.addFunction(this);

		// c.set(<name>,udf);
		if(pageType==PAGE_TYPE_INTERFACE) {
			adapter.loadArg(0);
		}
		else if(pageType==PAGE_TYPE_COMPONENT) {
			adapter.loadArg(1);
		}
		// pc.variablesScope().set(<name>,udf);
		else {
			adapter.loadArg(0);
			adapter.invokeVirtual(Types.PAGE_CONTEXT, VARIABLE_SCOPE);
		}
		
		
		boolean hasKey = Variable.registerKey(bc,name,true);
		if(pageType==PAGE_TYPE_COMPONENT) {
			loadUDF(bc,index,true);
			adapter.invokeVirtual(Types.COMPONENT_IMPL, hasKey?REG_UDF_KEY:REG_UDF_STR);
		}
		else if(pageType==PAGE_TYPE_INTERFACE) {
			loadUDF(bc,index,true);
			adapter.invokeVirtual(Types.INTERFACE_IMPL, hasKey?REG_UDF_KEY:REG_UDF_STR);
		}
		else {
			loadUDF(bc, index);
			adapter.invokeInterface(Types.VARIABLES, hasKey?SET_KEY:SET_STR);
			adapter.pop();
		}
	}
	
}
