package railo.transformer.bytecode.expression;

import org.objectweb.asm.Type;

import railo.print;
import railo.runtime.exp.TemplateException;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.util.ExpressionUtil;

/**
 * A Expression (Operation, Literal aso.)
 */
public abstract class ExpressionBase implements Expression {

    private Position start;
    private Position end;

    public ExpressionBase(Position start,Position end) {
        this.start=start;
        this.end=end;
        
        if(start!=null && end==null)
        	print.ds();
    }


    /**
     * write out the stament to adapter
     * @param adapter
     * @param mode 
     * @return return Type of expression
     * @throws TemplateException
     */
    public final Type writeOut(BytecodeContext bc, int mode) throws BytecodeException {
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
