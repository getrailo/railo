package railo.transformer.bytecode.literal;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.runtime.op.Caster;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.Literal;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.util.Methods;
import railo.transformer.bytecode.util.Types;

/**
 * Literal Double Value
 */
public final class LitLong extends ExpressionBase implements Literal {
    
    private long l;

	public static Expression toExpr(long l, Position start,Position end) {
		return new LitLong(l,start,end);
	}
    
    /**
     * constructor of the class
     * @param d
     * @param line 
     */
	public LitLong(long l, Position start,Position end) {
        super(start,end);        
        this.l=l;
    }

	/**
     * @return return value as int
     */ 
    public long getLongValue() {
        return l;
    }
    
    /**
     * @return return value as Double Object
     */
    public Long getLong() {
        return new Long(l);
    }
    
    /**
     * @see railo.transformer.bytecode.Literal#getString()
     */
    public String getString() {
        return Caster.toString(l);
    }
    
    /**
     * @return return value as a Boolean Object
     */
    public Boolean getBoolean() {
        return Caster.toBoolean(l);
    }
    
    /**
     * @return return value as a boolean value
     */
    public boolean getBooleanValue() {
        return Caster.toBooleanValue(l);
    }

    /**
     * @see railo.transformer.bytecode.expression.Expression#_writeOut(org.objectweb.asm.commons.GeneratorAdapter, int)
     */
    public Type _writeOut(BytecodeContext bc, int mode) {
    	GeneratorAdapter adapter = bc.getAdapter();
        adapter.push(l);
        if(mode==MODE_REF) {
            adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_LONG_FROM_LONG_VALUE);
            return Types.LONG;
        }
        return Types.LONG_VALUE;
    }

    /**
     * @see railo.transformer.bytecode.Literal#getDouble(java.lang.Double)
     */
    public Double getDouble(Double defaultValue) {
        return getDouble();
    }

    private Double getDouble() {
		return new Double(l);
	}

	/**
     * @see railo.transformer.bytecode.Literal#getBoolean(java.lang.Boolean)
     */
    public Boolean getBoolean(Boolean defaultValue) {
        return getBoolean();
    }
}
