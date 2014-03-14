package railo.transformer.bytecode.literal;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.runtime.op.Caster;
import railo.transformer.Factory;
import railo.transformer.Position;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.util.Methods;
import railo.transformer.bytecode.util.Types;
import railo.transformer.expression.literal.LitLong;

/**
 * Literal Double Value
 */
public final class LitLongImpl extends ExpressionBase implements LitLong {

    private long l;

    /**
     * constructor of the class
     * @param d
     * @param line 
     */
	public LitLongImpl(Factory f,long l, Position start,Position end) {
        super(f,start,end);
        this.l=l;
    }

	@Override
    public long getLongValue() {
        return l;
    }
    
	@Override
    public Long getLong() {
        return new Long(l);
    }
    
	@Override
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
     * @see railo.transformer.expression.Expression#_writeOut(org.objectweb.asm.commons.GeneratorAdapter, int)
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

    @Override
    public Double getDouble(Double defaultValue) {
        return getDouble();
    }

    private Double getDouble() {
		return new Double(l);
	}

    @Override
    public Boolean getBoolean(Boolean defaultValue) {
        return getBoolean();
    }
}
