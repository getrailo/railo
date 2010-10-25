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
import railo.transformer.bytecode.literal.LitDouble;
import railo.transformer.bytecode.util.Methods;
import railo.transformer.bytecode.util.Types;

/**
 * cast a Expression to a Double
 */
public final class CastDouble extends ExpressionBase implements ExprDouble {
    
    private Expression expr;
    
    private CastDouble(Expression expr) {
        super(expr.getLine());
    	this.expr=expr;
    }
    
    /**
     * Create a String expression from a Expression
     * @param expr
     * @return String expression
     * @throws TemplateException 
     */
    public static ExprDouble toExprDouble(Expression expr)  {
        if(expr instanceof ExprDouble) return (ExprDouble) expr;
        if(expr instanceof Literal) {
            Double dbl = ((Literal)expr).getDouble(null);
            if(dbl!=null) return new LitDouble(dbl.doubleValue(),expr.getLine());
        }
        return new CastDouble(expr);
    }

    /**
     * @see railo.transformer.bytecode.expression.Expression#_writeOut(org.objectweb.asm.commons.GeneratorAdapter, int)
     */
    public Type _writeOut(BytecodeContext bc, int mode) throws BytecodeException {

    	GeneratorAdapter adapter = bc.getAdapter();
        if(expr instanceof ExprBoolean) {
            expr.writeOut(bc,MODE_VALUE);
            if(mode==MODE_VALUE)adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_DOUBLE_VALUE_FROM_BOOLEAN);
            else adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_DOUBLE_FROM_BOOLEAN);
        }
        else if(expr instanceof ExprDouble) {
            expr.writeOut(bc,mode);
            //if(mode==MODE_VALUE)adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_DOUBLE_VALUE_FROM_DOUBLE);
            //if(mode==MODE_REF) adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_DOUBLE_FROM_DOUBLE);
        }
        else if(expr instanceof ExprString) {
            expr.writeOut(bc,MODE_REF);
            if(mode==MODE_VALUE)adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_DOUBLE_VALUE_FROM_STRING);
            else adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_DOUBLE_FROM_STRING);
        }
        else {
        	Type rtn = expr.writeOut(bc,mode);
        	if(mode==MODE_VALUE) {
        		if(!Types.isPrimitiveType(rtn))	{
        			adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_DOUBLE_VALUE);
        		}
        		else if(Types.DOUBLE_VALUE.equals(rtn))	{}
        		else if(Types.BOOLEAN_VALUE.equals(rtn))	{
        			adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_DOUBLE_VALUE_FROM_BOOLEAN);
        		}
        		else {
        			adapter.invokeStatic(Types.CASTER,new Method("toRef",Types.toRefType(rtn),new Type[]{rtn}));
        			adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_DOUBLE_VALUE);
        		}
        		return Types.DOUBLE_VALUE;
        	}
        	else if(Types.isPrimitiveType(rtn))	{
        		if(Types.DOUBLE_VALUE.equals(rtn))	{
        			adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_DOUBLE_FROM_DOUBLE);
        		}
        		else if(Types.BOOLEAN_VALUE.equals(rtn))	{
        			adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_DOUBLE_FROM_BOOLEAN);
        		}
        		else {
        			adapter.invokeStatic(Types.CASTER,new Method("toRef",Types.toRefType(rtn),new Type[]{rtn}));
        			adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_DOUBLE);
        		}
        		return Types.DOUBLE;
        	}
        	//else {
        	if(!Types.DOUBLE.equals(rtn)) adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_DOUBLE);
        	return Types.DOUBLE;
        	//}
        }
        

        if(mode==MODE_VALUE)return Types.DOUBLE_VALUE;
        return Types.DOUBLE;
    }

    /* *
     * @see railo.transformer.bytecode.expression.Expression#getType()
     * /
    public int getType() {
        return Types._DOUBLE;
    }*/

}




