package railo.transformer.bytecode.statement.udf;

import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Page;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.expression.Expression;

public final class Closure extends Function {

	public Closure(Page page,Expression name, Expression returnType, Expression returnFormat, Expression output,Expression bufferOutput,
			int access, Expression displayName, Expression description,Expression hint, Expression secureJson, Expression verifyClient,
			Expression localMode,
			long cachedWithin, boolean _abstract, boolean _final,
			Body body, Position start,Position end) {
		super(page,name, returnType, returnFormat, output,bufferOutput, access, displayName,description, hint, secureJson, verifyClient,
				localMode,cachedWithin,_abstract,_final,body, start, end);
		
	}
	

	public Closure(Page page, String name, int access, String returnType, Body body,Position start,Position end) {
		super(page,name, access, returnType, body, start, end);
	}

	public final void _writeOut(BytecodeContext bc, int pageType) throws BytecodeException{
		//GeneratorAdapter adapter = bc.getAdapter();
		
		////Page page = bc.getPage();
		////if(page==null)page=ASMUtil.getAncestorPage(this);
		//int index=page.addFunction(this);

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
		createUDF(bc, valueIndex,true);
		
		
	}
	
}
