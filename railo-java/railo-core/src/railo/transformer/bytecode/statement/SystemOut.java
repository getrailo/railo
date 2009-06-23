

package railo.transformer.bytecode.statement;

import java.io.PrintStream;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.util.Types;

public final class SystemOut extends StatementBase {
    

    // void println (Object)
    private final static Method METHOD_PRINTLN = new Method("println",
			Types.VOID,
			new Type[]{Types.OBJECT}); 
    
    Expression expr;
    
    /**
     * constructor of the class
     * @param expr
     * @param line 
     */
    public SystemOut(Expression expr, int line) {
        super(line);
        this.expr=expr;
    }
 
    /**
     * @see railo.transformer.bytecode.statement.StatementBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter)
     */
    public void _writeOut(BytecodeContext bc) throws BytecodeException {
    	GeneratorAdapter adapter = bc.getAdapter();
        adapter.getStatic(Type.getType(System.class),"out",Type.getType(PrintStream.class));
        expr.writeOut(bc,Expression.MODE_REF);
        adapter.invokeVirtual(Type.getType(PrintStream.class),METHOD_PRINTLN);
    }
}
