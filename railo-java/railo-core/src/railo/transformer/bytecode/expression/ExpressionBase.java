package railo.transformer.bytecode.expression;

import org.objectweb.asm.Type;

import railo.runtime.exp.TemplateException;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.util.ExpressionUtil;

/**
 * A Expression (Operation, Literal aso.)
 */
public abstract class ExpressionBase implements Expression {

    private int line;

    /**
	 * @see railo.transformer.bytecode.expression.Expression#setLine(int)
	 */
	public void setLine(int line) {
		this.line=line;
	}


	/**
     * constructor of the class
     * @param line
     */
    public ExpressionBase(int line) {
        this.line=line;
    }


    /**
     * write out the stament to adapter
     * @param adapter
     * @param mode 
     * @return return Type of expression
     * @throws TemplateException
     */
    public final Type writeOut(BytecodeContext bc, int mode) throws BytecodeException {
        ExpressionUtil.visitLine(bc, line);
    	return _writeOut(bc,mode);
    }

    /**
     * write out the stament to the adater
     * @param adapter
     * @param mode 
     * @return return Type of expression
     * @throws TemplateException
     */
    public abstract Type _writeOut(BytecodeContext bc, int mode) throws BytecodeException;


    /**
     * Returns the value of line.
     * @return value line
     */
    public int getLine() {
        return line;
    }
    
}
