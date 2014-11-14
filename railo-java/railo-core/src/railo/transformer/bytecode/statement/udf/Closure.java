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

import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Literal;
import railo.transformer.bytecode.Page;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.expression.Expression;

public final class Closure extends Function {

	public Closure(Page page,Expression name, Expression returnType, Expression returnFormat, Expression output,Expression bufferOutput,
			int access, Expression displayName, Expression description,Expression hint, Expression secureJson, Expression verifyClient,
			Expression localMode,
			Literal cachedWithin, boolean _abstract, boolean _final,
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
