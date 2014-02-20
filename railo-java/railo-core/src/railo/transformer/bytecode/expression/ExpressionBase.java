package railo.transformer.bytecode.expression;

import org.objectweb.asm.Type;

import railo.runtime.exp.TemplateException;
import railo.transformer.Context;
import railo.transformer.Factory;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.util.ExpressionUtil;
import railo.transformer.expression.Expression;

/**
 * A Expression (Operation, Literal aso.)
 */
public abstract class ExpressionBase implements Expression {

    private Position start;
    private Position end;
	private Factory factory;

    public ExpressionBase(Factory factory,Position start,Position end) {
        this.start=start;
        this.end=end;
        this.factory=factory;
    }

    @Override
    public final Type writeOut(Context c, int mode) throws BytecodeException {
    	BytecodeContext bc=(BytecodeContext) c;
    	ExpressionUtil.visitLine(bc, start);
    	Type type = _writeOut(bc,mode);
        ExpressionUtil.visitLine(bc, end);
        return type;
    }

    /**
     * write out the stament to the adater
     * @param adapter
     * @param mode 
     * @return return Type of expression
     * @throws TemplateException
     */
    public abstract Type _writeOut(BytecodeContext bc, int mode) throws BytecodeException;

    @Override
    public Factory getFactory() {
        return factory;
    }
    
	@Override
    public Position getStart() {
        return start;
    }
    
    @Override
    public Position getEnd() {
        return end;
    }
   
    @Override
    public void setStart(Position start) {
        this.start= start;
    }
    @Override
    public void setEnd(Position end) {
        this.end= end;
    }
    
    
}
