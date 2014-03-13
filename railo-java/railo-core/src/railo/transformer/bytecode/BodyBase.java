package railo.transformer.bytecode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.transformer.Factory;
import railo.transformer.bytecode.statement.PrintOut;
import railo.transformer.bytecode.statement.StatementBaseNoFinal;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.bytecode.util.ExpressionUtil;
import railo.transformer.bytecode.util.Types;
import railo.transformer.expression.Expression;
import railo.transformer.expression.literal.LitString;

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
	public BodyBase(Factory f) {
    	super(f,null,null);
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

	@Override
	public void addPrintOut(Factory f,String str, Position start,Position end) {
		if(concatPrintouts(str)) return;
		
		last=new PrintOut(f.createLitString(str,start,end),start,end);
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
					po.setExpr(lit.getFactory().createLitString(lit.getString().concat(str),lit.getStart(),lit.getEnd()));
					return true;
				}
			}
		}
		return false;
	}

	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		writeOut(bc,this);
    }
	
	
	

	public static void writeOut(final BytecodeContext bc, Body body) throws BytecodeException {
		writeOut(bc,body.getStatements());
	}
	

	
	public static void writeOut(final BytecodeContext bc, List<Statement> statements) throws BytecodeException {
		GeneratorAdapter adapter = bc.getAdapter();
        boolean isOutsideMethod;
        GeneratorAdapter a=null;
		Method m;
		BytecodeContext _bc=bc;
		Iterator<Statement> it = statements.iterator();
		boolean split = bc.getPage().getSplitIfNecessary();
        
		
		//int lastLine=-1;
		while(it.hasNext()) {
			isOutsideMethod=bc.getMethod().getReturnType().equals(Types.VOID);
	    	Statement s = it.next();
	    	if(split && _bc.incCount()>MAX_STATEMENTS && bc.doSubFunctions() && 
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
    			
    			_bc=new BytecodeContext(bc.getStaticConstructor(),bc.getConstructor(),bc.getKeys(),bc,a,m);
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
        	ExpressionUtil.writeOut(s, _bc);
        }	
        if(a!=null){
        	a.returnValue();
			a.endMethod();
        } 
    }
	
	
	public static void writeOutNew(final BytecodeContext bc, List<Statement> statements) throws BytecodeException {
		
		if(statements==null || statements.size()==0) return;
		
		Statement s;
		Iterator<Statement> it = statements.iterator();
		boolean isVoidMethod=bc.getMethod().getReturnType().equals(Types.VOID);
        boolean split = bc.getPage().getSplitIfNecessary();
        
        
        
		// split
        if(split && isVoidMethod && statements.size()>1 && bc.doSubFunctions()) {
        	int collectionSize=statements.size()/10;
    		if(collectionSize<1) collectionSize=1;
    		List<Statement> _statements=new ArrayList<Statement>();
    		while(it.hasNext()){
				s=it.next();
				
				if(s.hasFlowController()) {
		    		// add existing statements to sub method
					if(_statements.size()>0) {
						addToSubMethod(bc,_statements.toArray(new Statement[_statements.size()]));
						_statements.clear();
					}
					ExpressionUtil.writeOut(s, bc);
				}
				else {
					_statements.add(s);
					if(_statements.size()>=collectionSize) {
						if(_statements.size()<=10 && ASMUtil.count(_statements,true)<=20) {
							Iterator<Statement> _it = _statements.iterator();
							while(_it.hasNext())
								ExpressionUtil.writeOut(_it.next(), bc);
						}
						else 
							addToSubMethod(bc,_statements.toArray(new Statement[_statements.size()]));
						_statements.clear();
					}
				}
			}
    		
    		if(_statements.size()>0)
    			addToSubMethod(bc,_statements.toArray(new Statement[_statements.size()]));
		}
		// no split
		else {
			while(it.hasNext()){
				ExpressionUtil.writeOut(it.next(), bc);
			}
		}
    }
    
	private static void addToSubMethod(BytecodeContext bc, Statement... statements) throws BytecodeException {
		if(statements==null || statements.length==0) return; 
		
		GeneratorAdapter adapter = bc.getAdapter();
		String method= ASMUtil.createOverfowMethod(bc.getMethod().getName(),bc.getPage().getMethodCount());
		
		for(int i=0;i<statements.length;i++){
			if(statements[i].getStart()!=null) {
				ExpressionUtil.visitLine(bc, statements[i].getStart());
				break;
			}
		}
		
		//ExpressionUtil.lastLine(bc);
		Method m = new Method(method,Types.VOID,new Type[]{Types.PAGE_CONTEXT});
		GeneratorAdapter a = new GeneratorAdapter(Opcodes.ACC_PRIVATE+Opcodes.ACC_FINAL , m, null, new Type[]{Types.THROWABLE}, bc.getClassWriter());
		
		BytecodeContext _bc = new BytecodeContext(bc.getStaticConstructor(),bc.getConstructor(),bc.getKeys(),bc,a,m);
		if(bc.getRoot()!=null)_bc.setRoot(bc.getRoot());
		else _bc.setRoot(bc);
		
		adapter.visitVarInsn(Opcodes.ALOAD, 0);
    	adapter.visitVarInsn(Opcodes.ALOAD, 1);
    	adapter.visitMethodInsn(Opcodes.INVOKEVIRTUAL, bc.getClassName(), method, "(Lrailo/runtime/PageContext;)V");
		
    	for(int i=0;i<statements.length;i++){
    		ExpressionUtil.writeOut(statements[i], _bc);
		}
    	
		a.returnValue();
		a.endMethod();
	}


	public static synchronized String id() {
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
