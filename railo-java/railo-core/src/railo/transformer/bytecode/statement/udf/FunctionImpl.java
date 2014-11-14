/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
package railo.transformer.bytecode.statement.udf;

import org.objectweb.asm.commons.GeneratorAdapter;

import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Literal;
import railo.transformer.bytecode.Page;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.var.Variable;
import railo.transformer.bytecode.util.Types;

public final class FunctionImpl extends Function {

	public FunctionImpl(Page page, Expression name, Expression returnType, Expression returnFormat, Expression output, Expression bufferOutput,
			int access, Expression displayName, Expression description,Expression hint, Expression secureJson, Expression verifyClient, Expression localMode,
			Literal cachedWithin, boolean _abstract, boolean _final,
			Body body, Position start,Position end) {
		super(page,name, returnType, returnFormat, output, bufferOutput, access, displayName,description, hint, secureJson, verifyClient,localMode,cachedWithin,_abstract,_final, body, start, end);
	}
	

	public FunctionImpl(Page page, String name, int access, String returnType, Body body,Position start,Position end) {
		super(page,name, access, returnType, body, start, end);
	}

	public final void _writeOut(BytecodeContext bc, int pageType) throws BytecodeException{
		GeneratorAdapter adapter = bc.getAdapter();
		////Page page = ASMUtil.getAncestorPage(this);
		////int index=page.addFunction(this);

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
			loadUDFProperties(bc,valueIndex,arrayIndex,false);
			adapter.invokeVirtual(Types.COMPONENT_IMPL, hasKey?REG_UDF_KEY:REG_UDF_STR);
		}
		else if(pageType==PAGE_TYPE_INTERFACE) {
			loadUDFProperties(bc,valueIndex,arrayIndex,false);
			adapter.invokeVirtual(Types.INTERFACE_IMPL, hasKey?REG_UDF_KEY:REG_UDF_STR);
		}
		else {
			adapter.newInstance(Types.UDF_IMPL);
			adapter.dup();
			loadUDFProperties(bc, valueIndex,arrayIndex,false);
			adapter.invokeConstructor(Types.UDF_IMPL, INIT_UDF_IMPL_PROP);
			
			//loadUDF(bc, index);
			adapter.invokeInterface(Types.VARIABLES, hasKey?SET_KEY:SET_STR);
			adapter.pop();
		}
	}
	
}
