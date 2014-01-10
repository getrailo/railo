package railo.transformer.bytecode;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.statement.PrintOut;
import railo.transformer.bytecode.statement.StatementBaseNoFinal;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.bytecode.util.ExpressionUtil;
import railo.transformer.bytecode.util.Types;

/**
 * Base Body implementation
 */
public class BodyBase extends StatementBaseNoFinal implements Body {

	private static long counter=0;
	private LinkedList<Statement> statements=new LinkedList<Statement>();
    private Statement last=null;
	//private int count=-1;
    private final static int MAX_STATEMENTS=206;
	
	/**
	 * Constructor of the class
	 */
	public BodyBase() {
    	super(null,null);
	}

    
    /**
	 *
	 * @see railo.transformer.bytecode.Body#addStatement(railo.transformer.bytecode.Statement)
	 */
    public void addStatement(Statement statement) {
        
        if(statement instanceof PrintOut) {
        	Expression expr = ((PrintOut)statement).getExpr();
        	if(expr instanceof LitString && concatPrintouts(((LitString)expr).getString())) return;
        }
        statement.setParent(this);
        this.statements.add(statement);
        last=statement;
    }
    
    public void addFirst(Statement statement) {
        statement.setParent(this);
        this.statements.add(0,statement);
    }
    
    public void remove(Statement statement) {
        statement.setParent(null);
        this.statements.remove(statement);
    }
    
	/**
	 *
	 * @see railo.transformer.bytecode.Body#getStatements()
	 */
	public List<Statement> getStatements() {
		return statements;
	}
	
	public boolean hasStatements() {
		return !statements.isEmpty();
	}

	/**
	 *
	 * @see railo.transformer.bytecode.Body#moveStatmentsTo(railo.transformer.bytecode.Body)
	 */
	public void moveStatmentsTo(Body trg) {
		Iterator<Statement> it = statements.iterator();
		while(it.hasNext()) {
			Statement stat=it.next();
			stat.setParent(trg);
			trg.getStatements().add(stat);
		}
		statements.clear();
	}

	public void addPrintOut(String str, Position start,Position end) {
		if(concatPrintouts(str)) return;
		
		last=new PrintOut(new LitString(str,start,end),start,end);
        last.setParent(this);
        this.statements.add(last);
	}
	
	private boolean concatPrintouts(String str) {
		if(last instanceof PrintOut)  {
			PrintOut po=(PrintOut) last;
			Expression expr = po.getExpr();
			if(expr instanceof LitString) {
				LitString lit=(LitString)expr;
				if(lit.getString().length()<1024) {
					po.setExpr(LitString.toExprString(lit.getString().concat(str),lit.getStart(),lit.getEnd()));
					return true;
				}
			}
		}
		return false;
	}

	public void _writeOut(BytecodeContext bc) throws BytecodeException {
        writeOut(bc.getStaticConstructor(),bc.getConstructor(),bc.getKeys(),statements, bc);
    }

	public static void writeOut2(BytecodeContext statConstr,BytecodeContext constr,List<LitString> keys,List<Statement> statements,BytecodeContext bc) throws BytecodeException {
		Iterator<Statement> it = statements.iterator();
		while(it.hasNext()) {
			Statement s = it.next();
			s.writeOut(bc);
        }	
    }
	
	public static void writeOut(BytecodeContext statConstr,BytecodeContext constr,List<LitString> keys,List<Statement> statements,final BytecodeContext bc) throws BytecodeException {
		GeneratorAdapter adapter = bc.getAdapter();
        boolean isOutsideMethod;
        GeneratorAdapter a=null;
		Method m;
		BytecodeContext _bc=bc;
		Iterator<Statement> it = statements.iterator();
		//int lastLine=-1;
		while(it.hasNext()) {
			isOutsideMethod=bc.getMethod().getReturnType().equals(Types.VOID);
	    	Statement s = it.next();
	    	if(_bc.incCount()>MAX_STATEMENTS && bc.doSubFunctions() && 
					(isOutsideMethod || !s.hasFlowController()) && s.getStart()!=null) {
        		if(a!=null){
        			a.returnValue();
    				a.endMethod();
	        	}
        		//ExpressionUtil.visitLine(bc, s.getLine());
        		String method= ASMUtil.createOverfowMethod(bc.getMethod().getName(),bc.getPage().getMethodCount());
        		ExpressionUtil.visitLine(bc, s.getStart());
        		//ExpressionUtil.lastLine(bc);
        		m= new Method(method,Types.VOID,new Type[]{Types.PAGE_CONTEXT});
    			a = new GeneratorAdapter(Opcodes.ACC_PRIVATE+Opcodes.ACC_FINAL , m, null, new Type[]{Types.THROWABLE}, bc.getClassWriter());
    			
    			_bc=new BytecodeContext(statConstr,constr,keys,bc,a,m);
    			if(bc.getRoot()!=null)_bc.setRoot(bc.getRoot());
    			else _bc.setRoot(bc);
    			
    			adapter.visitVarInsn(Opcodes.ALOAD, 0);
	        	adapter.visitVarInsn(Opcodes.ALOAD, 1);
	        	adapter.visitMethodInsn(Opcodes.INVOKEVIRTUAL, bc.getClassName(), method, "(Lrailo/runtime/PageContext;)V");
        	}
        	if(_bc!=bc && s.hasFlowController()) {
				if(a!=null){
        			a.returnValue();
    				a.endMethod();
	        	}
				_bc=bc;
				a=null;
			}
        	        	
        	if(ExpressionUtil.doLog(bc)) {
        		String id=id();
        		ExpressionUtil.callStartLog(bc, s,id);
            	s.writeOut(_bc);
            	ExpressionUtil.callEndLog(bc, s,id);
        	}
        	else s.writeOut(_bc);
        }	
        if(a!=null){
        	a.returnValue();
			a.endMethod();
        } 
    }
    
	private static synchronized String id() {
		counter++;
		if(counter<0) counter=1;
		return Long.toString(counter, Character.MAX_RADIX);
	}

	/**
	 *
	 * @see railo.transformer.bytecode.Body#isEmpty()
	 */
	public boolean isEmpty() {
		return statements.isEmpty();
	}
}
