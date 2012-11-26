package railo.transformer.bytecode.cast;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.runtime.exp.TemplateException;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Literal;
import railo.transformer.bytecode.expression.ExprInt;
import railo.transformer.bytecode.expression.ExprString;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.literal.LitInteger;
import railo.transformer.bytecode.util.Methods;
import railo.transformer.bytecode.util.Types;

/**
 * cast a Expression to a Double
 */
public final class CastInt extends ExpressionBase implements ExprInt,Cast {
    
    private Expression expr;
    
    private CastInt(Expression expr) {
        super(expr.getStart(),expr.getEnd());
    	this.expr=expr;
    }
    
    /**
     * Create a String expression from a Expression
     * @param expr
     * @return String expression
     * @throws TemplateException 
     */
    public static ExprInt toExprInt(Expression expr)  {
    	if(expr instanceof ExprInt) return (ExprInt) expr;
        if(expr instanceof Literal) {
            Double dbl = ((Literal)expr).getDouble(null);
            if(dbl!=null) return new LitInteger((int)dbl.doubleValue(),expr.getStart(),expr.getEnd());
        }
        return new CastInt(expr);
    }

    /**
     * @see railo.transformer.bytecode.expression.Expression#_writeOut(org.objectweb.asm.commons.GeneratorAdapter, int)
     */
    public Type _writeOut(BytecodeContext bc, int mode) throws BytecodeException {
    	GeneratorAdapter adapter = bc.getAdapter();

        if(expr instanceof ExprString) {
            expr.writeOut(bc,MODE_REF);
            if(mode==MODE_VALUE)adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_INT_VALUE_FROM_STRING);
            else adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_INTEGER_FROM_STRING);
        }
        else {
        	Type rtn = expr.writeOut(bc,mode);
        	if(mode==MODE_VALUE) {
        		if(!Types.isPrimitiveType(rtn))	{
        			adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_INT_VALUE);
        		}
        		else if(Types.BOOLEAN_VALUE.equals(rtn))	{
        			adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_INT_VALUE_FROM_BOOLEAN);
        		}
        		else if(Types.SHORT_VALUE.equals(rtn))	{
        			// No Cast needed
        		}
        		else if(Types.FLOAT_VALUE.equals(rtn))	{
        			adapter.cast(Types.FLOAT_VALUE, Types.INT_VALUE);
        		}
        		else if(Types.LONG_VALUE.equals(rtn))	{
        			adapter.cast(Types.LONG_VALUE, Types.INT_VALUE);
        		}
        		else if(Types.DOUBLE_VALUE.equals(rtn))	{
        			adapter.cast(Types.DOUBLE_VALUE, Types.INT_VALUE);
        		}
        		else if(Types.INT_VALUE.equals(rtn))	{
        			// No Cast needed
        		}
        		else {
        			adapter.invokeStatic(Types.CASTER,new Method("toRef",Types.toRefType(rtn),new Type[]{rtn}));
        			adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_INT_VALUE);
        		}
        		return Types.INT_VALUE;
        		
        		
        	}
        	else if(Types.isPrimitiveType(rtn))	{
        		if(Types.DOUBLE_VALUE.equals(rtn))	{
        			adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_INTEGER_FROM_DOUBLE);
        		}
        		else if(Types.BOOLEAN_VALUE.equals(rtn))	{
        			adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_INTEGER_FROM_BOOLEAN);
        		}
        		else {
        			adapter.invokeStatic(Types.CASTER,new Method("toRef",Types.toRefType(rtn),new Type[]{rtn}));
        			adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_INTEGER);
        		}
        		return Types.INTEGER;
        	}
        	
        	if(!Types.INTEGER.equals(rtn)) adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_INTEGER);
        	return Types.INTEGER;
        }
        

        if(mode==MODE_VALUE)return Types.INT_VALUE;
        return Types.INTEGER;
    }

	@Override
	public Expression getExpr() {
		return expr;
	}
}




