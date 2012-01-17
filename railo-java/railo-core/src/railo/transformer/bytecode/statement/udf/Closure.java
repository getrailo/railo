package railo.transformer.bytecode.statement.udf;

import org.objectweb.asm.commons.GeneratorAdapter;

import railo.print;
import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Page;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.bytecode.util.Types;

public final class Closure extends Function {

	public Closure(Expression name, Expression returnType, Expression returnFormat, Expression output, Expression abstr,
			int access, Expression displayName, Expression description,Expression hint, Expression secureJson, Expression verifyClient,
			Body body, int startline, int endline) {
		super(name, returnType, returnFormat, output, abstr, access, displayName,description, hint, secureJson, verifyClient, body, startline, endline);
	}
	

	public Closure(String name, int access, String returnType, Body body,int startline, int endline) {
		super(name, access, returnType, body, startline, endline);
	}

	public final void _writeOut(BytecodeContext bc, int pageType) throws BytecodeException{
		//GeneratorAdapter adapter = bc.getAdapter();
		
		Page page = bc.getPage();
		if(page==null)page=ASMUtil.getAncestorPage(this);
		int index=page.addFunction(this);

		/*if(pageType==PAGE_TYPE_INTERFACE) {
			adapter.loadArg(0);
		}
		else if(pageType==PAGE_TYPE_COMPONENT) {
			adapter.loadArg(1);
		}
		else {
			adapter.loadArg(0);
			adapter.invokeVirtual(Types.PAGE_CONTEXT, VARIABLE_SCOPE);
		}
		*/
		createUDF(bc, index,true);
		
	}
	
}
