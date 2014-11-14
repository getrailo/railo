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
package railo.transformer.bytecode.expression;

import org.objectweb.asm.Type;

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
