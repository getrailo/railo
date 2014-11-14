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
package railo.transformer.bytecode.cast;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.runtime.exp.TemplateException;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Literal;
import railo.transformer.bytecode.expression.ExprBoolean;
import railo.transformer.bytecode.expression.ExprDouble;
import railo.transformer.bytecode.expression.ExprString;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.literal.LitBoolean;
import railo.transformer.bytecode.util.Methods;
import railo.transformer.bytecode.util.Types;

/**
 * Cast to a Boolean
 */
public final class CastBoolean extends ExpressionBase implements ExprBoolean,Cast {
    
    /**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "(boolean)"+expr;
	}

	private Expression expr;

    /**
     * constructor of the class
     * @param expr
     */
    private CastBoolean(Expression expr) {
        super(expr.getStart(),expr.getEnd());
        this.expr=expr;
    }
    
    /**
     * Create a String expression from a Expression
     * @param expr
     * @return String expression
     * @throws TemplateException 
     */
    public static ExprBoolean toExprBoolean(Expression expr)  {
        if(expr instanceof ExprBoolean) return (ExprBoolean) expr;
        if(expr instanceof Literal) {
            Boolean bool = ((Literal)expr).getBoolean(null);
            if(bool!=null) return new LitBoolean(bool.booleanValue(),expr.getStart(),expr.getEnd());
            // TODO throw new TemplateException("can't cast value to a boolean value");
        }
        return new CastBoolean(expr);
    }

    /**
     * @see railo.transformer.bytecode.expression.Expression#writeOut(org.objectweb.asm.commons.GeneratorAdapter, int)
     */
    public Type _writeOut(BytecodeContext bc, int mode) throws BytecodeException {
    	GeneratorAdapter adapter = bc.getAdapter();
        if(expr instanceof ExprDouble) {
            expr.writeOut(bc,MODE_VALUE);
            if(mode==MODE_VALUE)adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_BOOLEAN_VALUE_FROM_DOUBLE);
            else adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_BOOLEAN_FROM_DOUBLE);
        }
        else if(expr instanceof ExprString) {
            expr.writeOut(bc,MODE_REF);
            if(mode==MODE_VALUE)adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_BOOLEAN_VALUE_FROM_STRING);
            else adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_BOOLEAN_FROM_STRING);
        }
        else {
        	Type rtn = expr.writeOut(bc,mode);
        	
        	if(mode==MODE_VALUE) {
        		if(!Types.isPrimitiveType(rtn))	{
        			adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_BOOLEAN_VALUE);
        		}
        		else if(Types.BOOLEAN_VALUE.equals(rtn))	{}
        		else if(Types.DOUBLE_VALUE.equals(rtn))	{
        			adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_BOOLEAN_VALUE_FROM_DOUBLE);
        		}
        		else {
        			adapter.invokeStatic(Types.CASTER,new Method("toRef",Types.toRefType(rtn),new Type[]{rtn}));
        			adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_BOOLEAN_VALUE);
        		}
        		//return Types.BOOLEAN_VALUE;
        	}
        	else {
        		if(Types.BOOLEAN.equals(rtn))	{}
        		else adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_BOOLEAN);
        	}
        }

        if(mode==MODE_VALUE)return Types.BOOLEAN_VALUE;
        return Types.BOOLEAN;
    }

	@Override
	public Expression getExpr() {
		return expr;
	}
}
