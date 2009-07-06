package railo.transformer.bytecode.util;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.commons.lang.CFTypes;
import railo.runtime.op.Caster;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.expression.ExprString;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.literal.LitString;

public final class ExpressionUtil {

	private static Map last=new HashMap();

	public static void writeOutExpressionArray(BytecodeContext bc, Type arrayType, Expression[] array) throws BytecodeException {
    	GeneratorAdapter adapter = bc.getAdapter();
        adapter.push(array.length);
        adapter.newArray(arrayType);
        for (int i = 0; i < array.length; i++) {
            adapter.dup();
            adapter.push(i);
            array[i].writeOut(bc, Expression.MODE_REF);
            adapter.visitInsn(Opcodes.AASTORE);
        }
    }

    /* *
     * visit line number
     * @param adapter
     * @param line
     */
    public static synchronized void visitLine(BytecodeContext bc, int line) {
    	if(line>0 && !(""+line).equals(last.get(bc.getClassName()+":"+bc.getId()))){
    		//print.out(bc.getClassName()+"."+bc.getMethod().getName()+":"+line);
    		bc.visitLineNumber(line);
    		last.put(bc.getClassName()+":"+bc.getId(),""+line);
    		last.put(bc.getClassName(),""+line);
    	}
   }
   
    public static synchronized void lastLine(BytecodeContext bc) {
    	int line = Caster.toIntValue(last.get(bc.getClassName()),-1);
    	visitLine(bc, line);
    }

	/**
	 * write out expression without LNT
	 * @param value
	 * @param bc
	 * @param mode
	 * @throws BytecodeException
	 */
	public static void writeOutSilent(Expression value, BytecodeContext bc, int mode) throws BytecodeException {
		int line = value.getLine();
		value.setLine(-1);
		value.writeOut(bc, mode);
		value.setLine(line);
	}
	public static void writeOut(Expression value, BytecodeContext bc, int mode) throws BytecodeException {
		value.writeOut(bc, mode);
	}

	public static short toShortType(ExprString expr) {
		if(expr instanceof LitString){
			return CFTypes.toShort(((LitString)expr).getString());
		}
		return -1;
	}
}
