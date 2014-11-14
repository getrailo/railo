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

	private final static Method DO_INCLUDE_RUN_ONCE2 = new Method(
			"doInclude",
			Type.VOID_TYPE,
			new Type[]{Types.STRING,Types.BOOLEAN_VALUE});
	
	private final static Method DO_INCLUDE_RUN_ONCE3 = new Method(
			"doInclude",
			Type.VOID_TYPE,
			new Type[]{Types.STRING,Types.BOOLEAN_VALUE, Types.OBJECT});
	
	public TagInclude(Position start,Position end) {
		super(start,end);
	}

	/**
	 * @see railo.transformer.bytecode.statement.tag.TagBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter)
	 */
	public void _writeOut(BytecodeContext bc) throws BytecodeException {
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
				LitBoolean.FALSE:
				CastBoolean.toExprBoolean(attr.getValue());
		expr.writeOut(bc, Expression.MODE_VALUE);

		// cachedwithin
		if(cachedwithin!=null)
			cachedwithin.writeOut(bc, Expression.MODE_REF);

		adapter.invokeVirtual(type,func);
	}
}
