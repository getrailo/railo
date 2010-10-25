package railo.transformer.bytecode.literal;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.runtime.op.Caster;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.Literal;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.util.Methods;
import railo.transformer.bytecode.util.Types;

/**
 * Literal Double Value
 */
public final class LitInteger extends ExpressionBase implements Literal {
    
    private int i;

	public static Expression toExpr(int i, int line) {
		return new LitInteger(i,line);
	}
    
    /**
     * constructor of the class
     * @param d
     * @param line 
     */
	public LitInteger(int i, int line) {
        super(line);        
        this.i=i;
    }

	/**
     * @return return value as int
     */ 
    public int geIntValue() {
        return i;
    }
    
    /**
     * @return return value as Double Object
     */
    public Integer getInteger() {
        return new Integer(i);
    }
    
    /**
     * @see railo.transformer.bytecode.Literal#getString()
     */
    public String getString() {
        return Caster.toString(i);
    }
    
    /**
     * @return return value as a Boolean Object
     */
    public Boolean getBoolean() {
        return Caster.toBoolean(i);
    }
    
    /**
     * @return return value as a boolean value
     */
    public boolean getBooleanValue() {
        return Caster.toBooleanValue(i);
    }

    /**
     * @see railo.transformer.bytecode.expression.Expression#_writeOut(org.objectweb.asm.commons.GeneratorAdapter, int)
     */
    public Type _writeOut(BytecodeContext bc, int mode) {
    	GeneratorAdapter adapter = bc.getAdapter();
        adapter.push(i);
        if(mode==MODE_REF) {
            adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_INTEGER_FROM_INT);
            return Types.INTEGER;
        }
        return Types.INT_VALUE;
    }

    /**
     * @see railo.transformer.bytecode.Literal#getDouble(java.lang.Double)
     */
    public Double getDouble(Double defaultValue) {
        return getDouble();
    }

    private Double getDouble() {
		return new Double(i);
	}

	/**
     * @see railo.transformer.bytecode.Literal#getBoolean(java.lang.Boolean)
     */
    public Boolean getBoolean(Boolean defaultValue) {
        return getBoolean();
    }
}
