/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
package railo.transformer.bytecode.statement;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.cast.CastString;
import railo.transformer.bytecode.expression.ExprString;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.util.Types;

public final class PrintOut extends StatementBaseNoFinal {

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
    public PrintOut(Expression expr, Position start,Position end) {
        super(start,end);
        this.expr=CastString.toExprString(expr);
    }


    /**
     * @see railo.transformer.bytecode.Statement#_writeOut(org.objectweb.asm.commons.GeneratorAdapter)
     */
    public void _writeOut(BytecodeContext bc) throws BytecodeException {
    	GeneratorAdapter adapter = bc.getAdapter();
        adapter.loadArg(0);
        ExprString es=CastString.toExprString(expr);
        boolean usedExternalizer=false;
        
        /*if(es instanceof LitString) {
        	LitString ls = ((LitString)es);
        	ls.setExternalize(true);
        }*/
        
        if(!usedExternalizer)es.writeOut(bc,Expression.MODE_REF);
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
