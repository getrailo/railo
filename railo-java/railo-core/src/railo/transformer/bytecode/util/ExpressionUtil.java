package railo.transformer.bytecode.util;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.commons.lang.CFTypes;
import railo.commons.lang.StringUtil;
import railo.runtime.op.Caster;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.expression.ExprString;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.statement.StatementBase;
import railo.transformer.bytecode.statement.tag.Tag;

public final class ExpressionUtil {

	/*public static final Method END_LINE = new Method(
			"exeLogEndline",
			Types.VOID,
			new Type[]{Types.INT_VALUE});*/
	
	public static final Method START = new Method(
			"exeLogStart",
			Types.VOID,
			new Type[]{Types.INT_VALUE,Types.STRING});
	public static final Method END = new Method(
			"exeLogEnd",
			Types.VOID,
			new Type[]{Types.INT_VALUE,Types.STRING});
	

	private static Map<String,String> last=new HashMap<String,String>();

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

    /**
     * visit line number
     * @param adapter
     * @param line
     * @param silent id silent this is ignored for log
     */
    public static synchronized void visitLine(BytecodeContext bc, int line) {
    	if(line>0){
	    	if(!(""+line).equals(last.get(bc.getClassName()+":"+bc.getId()))){
	    		//writeLog(bc,line);
	    		bc.visitLineNumber(line);
	    		last.put(bc.getClassName()+":"+bc.getId(),""+line);
	    		last.put(bc.getClassName(),""+line);
	    	}
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

	public static short toShortType(ExprString expr, short defaultValue) {
		if(expr instanceof LitString){
			return CFTypes.toShort(((LitString)expr).getString(),defaultValue);
		}
		return defaultValue;
	}

	public static void callStartLog(BytecodeContext bc, Statement s, String id) {
		call_Log(bc, START, getStartLine(s),id);
	}
	public static void callEndLog(BytecodeContext bc, Statement s, String id) {
		call_Log(bc, END, getEndLine(s),id);
	}

	private static void call_Log(BytecodeContext bc, Method method, int line, String id) {
    	if(!bc.writeLog() || line<0 || (StringUtil.indexOfIgnoreCase(bc.getMethod().getName(),"call")==-1))return;
    	try{
	    	GeneratorAdapter adapter = bc.getAdapter();
	    	adapter.loadArg(0);
	        //adapter.checkCast(Types.PAGE_CONTEXT_IMPL);
	        adapter.push(line);
	        adapter.push(id);
		    adapter.invokeVirtual(Types.PAGE_CONTEXT, method);
		}
		catch(Throwable t) {
			t.printStackTrace();
		}		
	}
    
    public static int getStartLine(Statement s){
    	if(s instanceof StatementBase)return ((StatementBase)s).getStartLine();
    	if(s instanceof Tag)return ((Tag)s).getStartLine();
    	return s.getLine();
    }
    
    public static int getEndLine(Statement s){
    	if(s instanceof StatementBase)return ((StatementBase)s).getEndLine();
    	if(s instanceof Tag)return ((Tag)s).getEndLine();
    	return s.getLine();
    }

	public static boolean doLog(BytecodeContext bc) {
		return bc.writeLog() && StringUtil.indexOfIgnoreCase(bc.getMethod().getName(),"call")!=-1;
	}
}
