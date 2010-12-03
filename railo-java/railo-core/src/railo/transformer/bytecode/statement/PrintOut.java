package railo.transformer.bytecode.statement;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.cast.CastString;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.util.Types;

public final class PrintOut extends StatementBase {

	// void write (String)
    private final static Method METHOD_WRITE =  new Method("write",
			Types.VOID,
			new Type[]{Types.STRING});
    // void writePSQ (Object) TODO muss param 1 wirklich objekt sein
    private final static Method METHOD_WRITE_PSQ = new Method("writePSQ",
			Types.VOID,
			new Type[]{Types.OBJECT}); 
    
    Expression expr;

	private boolean checkPSQ;

  
    
    /**
     * constructor of the class
     * @param expr
     * @param line 
     */
    public PrintOut(Expression expr, int line) {
        super(line);
        this.expr=CastString.toExprString(expr);
    }


    /**
     * @see railo.transformer.bytecode.Statement#_writeOut(org.objectweb.asm.commons.GeneratorAdapter)
     */
    public void _writeOut(BytecodeContext bc) throws BytecodeException {
    	GeneratorAdapter adapter = bc.getAdapter();
        adapter.loadArg(0);
        if(checkPSQ)
        	expr.writeOut(bc,Expression.MODE_REF);
        else
        	CastString.toExprString(expr).writeOut(bc,Expression.MODE_REF);
        adapter.invokeVirtual(Types.PAGE_CONTEXT,checkPSQ?METHOD_WRITE_PSQ:METHOD_WRITE);
    }


	/**
	 * @return the expr
	 */
	public Expression getExpr() {
		return expr;
	}

	/**
	 * @param expr the expr to set
	 */
	public void setExpr(Expression expr) {
		this.expr = expr;
	}


	/**
	 * @param preserveSingleQuote the preserveSingleQuote to set
	 */
	public void setCheckPSQ(boolean checkPSQ) {
		this.checkPSQ = checkPSQ;
	}
}
