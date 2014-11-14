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
package railo.transformer.bytecode.op;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.runtime.exp.TemplateException;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Literal;
import railo.transformer.bytecode.cast.CastBoolean;
import railo.transformer.bytecode.expression.ExprBoolean;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.literal.LitBoolean;
import railo.transformer.bytecode.util.Methods;
import railo.transformer.bytecode.util.Methods_Operator;
import railo.transformer.bytecode.util.Types;

public final class OpBool extends ExpressionBase implements ExprBoolean {

    public static final int AND=0;
    public static final int OR=1;
    public static final int XOR=2;
	public static final int EQV = 3;
	public static final int IMP = 4;
    
    private ExprBoolean left;
    private ExprBoolean right;
    private int operation;

    /**
     *
     * @see railo.transformer.bytecode.expression.ExpressionBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter, int)
     */
    public Type _writeOut(BytecodeContext bc, int mode) throws BytecodeException {
    	GeneratorAdapter adapter = bc.getAdapter();
    	
    	if(mode==MODE_REF) {
            _writeOut(bc,MODE_VALUE);
            adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_BOOLEAN_FROM_BOOLEAN);
            return Types.BOOLEAN;
        }
    	
    	
    	Label doFalse = new Label();
    	Label end = new Label();

    	if(operation==AND) {
    		left.writeOut(bc, MODE_VALUE);
        	adapter.ifZCmp(Opcodes.IFEQ, doFalse);
        	
        	right.writeOut(bc, MODE_VALUE);
        	adapter.ifZCmp(Opcodes.IFEQ, doFalse);
        	adapter.push(true);
        	
        	adapter.visitJumpInsn(Opcodes.GOTO, end);
        	adapter.visitLabel(doFalse);

        	adapter.push(false);
        	adapter.visitLabel(end);
    	}
    	if(operation==OR) {
    		left.writeOut(bc, MODE_VALUE);
        	adapter.ifZCmp(Opcodes.IFNE, doFalse);

        	right.writeOut(bc, MODE_VALUE);
        	adapter.ifZCmp(Opcodes.IFNE, doFalse);

        	adapter.push(false);
        	adapter.visitJumpInsn(Opcodes.GOTO, end);
        	adapter.visitLabel(doFalse);

        	adapter.push(true);
        	adapter.visitLabel(end);
    	}
    	else if(operation==XOR) {
    		left.writeOut(bc, MODE_VALUE);
    		right.writeOut(bc, MODE_VALUE);
    		adapter.visitInsn(Opcodes.IXOR);
    	}
    	else if(operation==EQV) {

            left.writeOut(bc,MODE_VALUE);
            right.writeOut(bc,MODE_VALUE);
            adapter.invokeStatic(Types.OPERATOR,Methods_Operator.OPERATOR_EQV_BV_BV);
    	}
    	else if(operation==IMP) {

            left.writeOut(bc,MODE_VALUE);
            right.writeOut(bc,MODE_VALUE);
            adapter.invokeStatic(Types.OPERATOR,Methods_Operator.OPERATOR_IMP_BV_BV);
    	}
    	return Types.BOOLEAN_VALUE;
    	
    }
    

    
    
    
    
    
    private OpBool(Expression left, Expression right, int operation) {
        super(left.getStart(),right.getEnd());
        this.left=CastBoolean.toExprBoolean(left);
        this.right=CastBoolean.toExprBoolean(right);  
        this.operation=operation;
    }
    
    /**
     * Create a String expression from a Expression
     * @param left 
     * @param right 
     * 
     * @return String expression
     * @throws TemplateException 
     */
    public static ExprBoolean toExprBoolean(Expression left, Expression right,int operation) {
        if(left instanceof Literal && right instanceof Literal) {
        	Boolean l=((Literal) left).getBoolean(null);
        	Boolean r=((Literal) right).getBoolean(null);
        	
        	
        	if(l!=null && r!=null) {
        		switch(operation) {
        		case AND:	return new LitBoolean(l.booleanValue()&&r.booleanValue(),left.getStart(),right.getEnd());
        		case OR:	return new LitBoolean(l.booleanValue()||r.booleanValue(),left.getStart(),right.getEnd());
        		case XOR:	return new LitBoolean(l.booleanValue()^r.booleanValue(),left.getStart(),right.getEnd());
        		}
        	}
        }
        return new OpBool(left,right,operation);
    }
    
    public String toString(){
    	return left+" "+toStringOperation()+" "+right;
    }

	private String toStringOperation() {
		if(AND==operation)	return "and";
		if(OR==operation) 	return "or";
		if(XOR==operation) 	return "xor";
		if(EQV==operation) 	return "eqv";
		if(IMP==operation) 	return "imp";
		return operation+"";
	}
}

