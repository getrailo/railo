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
import railo.transformer.expression.ExprFloat;
import railo.transformer.expression.literal.LitFloat;

/**
 * Literal Double Value
 */
public final class LitFloatImpl extends ExpressionBase implements LitFloat,ExprFloat {
    
    private float f;

    /**
     * constructor of the class
     * @param d
     * @param line 
     */
	public LitFloatImpl(Factory fac,float f, Position start,Position end) {
        super(fac,start,end);
        this.f=f;
    }

	@Override
    public float getFloatValue() {
        return f;
    }
    
    @Override
    public Float getFloat() {
        return new Float(f);
    }
    
    @Override
    public String getString() {
        return Caster.toString(f);
    }
    
    /**
     * @return return value as a Boolean Object
     */
    public Boolean getBoolean() {
        return Caster.toBoolean(f);
    }
    
    /**
     * @return return value as a boolean value
     */
    public boolean getBooleanValue() {
        return Caster.toBooleanValue(f);
    }

    @Override
    public Type _writeOut(BytecodeContext bc, int mode) {
    	GeneratorAdapter adapter = bc.getAdapter();
        adapter.push(f);
        if(mode==MODE_REF) {
            adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_FLOAT_FROM_FLOAT);
            return Types.FLOAT;
        }
        return Types.FLOAT_VALUE;
    }

    @Override
    public Double getDouble(Double defaultValue) {
        return new Double(getFloatValue());
    }

    @Override
    public Boolean getBoolean(Boolean defaultValue) {
        return getBoolean();
    }
}
