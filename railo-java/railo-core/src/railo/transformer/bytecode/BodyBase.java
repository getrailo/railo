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
import railo.transformer.bytecode.statement.StatementBase;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.bytecode.util.ExpressionUtil;
import railo.transformer.bytecode.util.Types;

/**
 * Base Body implementation
 */
public class BodyBase extends StatementBase implements Body {

	private LinkedList statements=new LinkedList();
    private Statement last=null;
	//private int count=-1;
    private final static int MAX_STATEMENTS=206;
	
	/**
	 * Constructor of the class
	 */
	public BodyBase() {
    	super(-1,-1);
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

    /*public static void main(String[] args) {
		List list =new LinkedList();
		list.add("aaa");
		list.add("bb");
		list.add("cc");
		list.add(0,"xxx");
		print.out(list);
	}*/
    
	/**
	 *
	 * @see railo.transformer.bytecode.Body#getStatements()
	 */
	public List getStatements() {
		return statements;
	}

	/**
	 *
	 * @see railo.transformer.bytecode.Body#moveStatmentsTo(railo.transformer.bytecode.Body)
	 */
	public void moveStatmentsTo(Body trg) {
		Iterator it = statements.iterator();
		while(it.hasNext()) {
			Statement stat=(Statement) it.next();
			stat.setParent(trg);
			trg.getStatements().add(stat);
		}
		statements.clear();
	}

	public void addPrintOut(String str, int line) {
		if(concatPrintouts(str)) return;
		
		last=new PrintOut(new LitString(str,line),line);
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
					po.setExpr(LitString.toExprString(lit.getString().concat(str),lit.getLine()));
					return true;
				}
			}
		}
		return false;
	}

	public void _writeOut(BytecodeContext bc) throws BytecodeException {
        writeOut(bc.getStaticConstructor(),bc.getConstructor(),bc.getKeys(),statements, bc);
		/*Iterator it = statements.iterator();
        //adapter.visitLabel(new Label());
        while(it.hasNext()) {
            ((Statement)it.next()).writeOut(bc);
        }
        //adapter.visitLabel(new Label());*/
    }
	

	/*public static void writeOut(BytecodeContext statConstr,BytecodeContext constr,List keys,List statements,BytecodeContext bc) throws BytecodeException {
		GeneratorAdapter adapter = bc.getAdapter();
        boolean isOutsideMethod;
        GeneratorAdapter a=null;
		Method m;
		BytecodeContext _bc=bc;
		Iterator it = statements.iterator();
		boolean first=true;
		while(it.hasNext()) {
			isOutsideMethod=bc.getMethod().getReturnType().equals(Types.VOID);
	    	Statement s = ((Statement)it.next());
	    	if(first || (_bc.incCount()>MAX_STATEMENTS && bc.doSubFunctions() && 
					(isOutsideMethod || !s.hasFlowController()))) {
        		if(a!=null){
        			a.returnValue();
    				a.endMethod();
	        	}
        		ExpressionUtil.visitLine(bc, s.getLine());
        		String method= "_"+ASMUtil.getId();
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
        	s.writeOut(_bc);
        	first=false;
        }
        if(a!=null){
        	a.returnValue();
			a.endMethod();
        } 
    }*/
	
	
	public static void writeOut(BytecodeContext statConstr,BytecodeContext constr,List keys,List statements,BytecodeContext bc) throws BytecodeException {
		GeneratorAdapter adapter = bc.getAdapter();
        boolean isOutsideMethod;
        GeneratorAdapter a=null;
		Method m;
		BytecodeContext _bc=bc;
		Iterator it = statements.iterator();
		while(it.hasNext()) {
			isOutsideMethod=bc.getMethod().getReturnType().equals(Types.VOID);
	    	Statement s = ((Statement)it.next());
	    	if(_bc.incCount()>MAX_STATEMENTS && bc.doSubFunctions() && 
					(isOutsideMethod || !s.hasFlowController())) {
        		if(a!=null){
        			a.returnValue();
    				a.endMethod();
	        	}
        		//ExpressionUtil.visitLine(bc, s.getLine());
        		String method= ASMUtil.createOverfowMethod();
        		//print.out("call:"+method);
        		ExpressionUtil.lastLine(bc);
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
        	s.writeOut(_bc);
        }
        if(a!=null){
        	a.returnValue();
			a.endMethod();
        } 
    }

	/*public static void writeOut2(List keys,List statements,BytecodeContext bc) throws BytecodeException {
		GeneratorAdapter adapter = bc.getAdapter();
        	
        GeneratorAdapter a=null;
		Method m;
		BytecodeContext _bc=bc;
		Iterator it = statements.iterator();
		while(it.hasNext()) {
			//print.out(_bc.getClassName()+":"+_bc.getCount()+":"+bc);
	    	if(bc.doSubFunctions() && _bc.incCount()>MAX_STATEMENTS && bc.getMethod().getReturnType().equals(Types.VOID)) {
        		if(a!=null){
        			a.returnValue();
    				a.endMethod();
	        	}
        		String method= "_"+ASMUtil.getId();
        		m= new Method(method,Types.VOID,new Type[]{Types.PAGE_CONTEXT});
    			a = new GeneratorAdapter(Opcodes.ACC_PUBLIC+Opcodes.ACC_FINAL , m, null, new Type[]{Types.THROWABLE}, bc.getClassWriter());
    			_bc=new BytecodeContext(keys,bc,a,m);
    			adapter.visitVarInsn(Opcodes.ALOAD, 0);
	        	adapter.visitVarInsn(Opcodes.ALOAD, 1);
	        	//print.out(m);
	        	adapter.visitMethodInsn(Opcodes.INVOKEVIRTUAL, bc.getClassName(), method, "(Lrailo/runtime/PageContext;)V");
        	}
	    	Statement s = ((Statement)it.next());
	    	print.out(bc.getMethod().getName()+"::"+s.getClass().getName()+":"+s.hasFlowController());
        	s.writeOut(_bc);
        }
        if(a!=null){
        	a.returnValue();
			a.endMethod();
        }
		  
    }*/
	
	/*public static void writeOut(List keys,List statements,BytecodeContext bc) throws BytecodeException {
		
        print.out(bc.getClassName()+":"+bc.getCount());
        GeneratorAdapter adapter = bc.getAdapter();
        if(bc.getCount()>MAX_STATEMENTS) {
			GeneratorAdapter a=null;
			Method m;
			BytecodeContext _bc=null;
			
			Iterator it = statements.iterator();
			//int xcount=MAX_STATEMENTS;
			int mcount=0;
			Statement s;
	        while(it.hasNext()) {
	        	s=((Statement)it.next());
	        	bc.setCount(bc.getCount()+1);
	        	if(bc.getCount()>MAX_STATEMENTS) {
	        		if(a!=null){
	        			a.returnValue();
	    				a.endMethod();
		        	}
	        		bc.setCount(0);
	        		//count=0;
	        		mcount++;
	        		String method= "_"+mcount+"_"+((int)(Math.random()*1000));
	        		m= new Method(method,Types.VOID,new Type[]{Types.PAGE_CONTEXT});
	    			a = new GeneratorAdapter(Opcodes.ACC_PUBLIC+Opcodes.ACC_FINAL , m, null, new Type[]{Types.THROWABLE}, bc.getClassWriter());
	    			_bc=new BytecodeContext(keys,bc,a);
	    			adapter.visitVarInsn(Opcodes.ALOAD, 0);
		        	adapter.visitVarInsn(Opcodes.ALOAD, 1);
		        	adapter.visitMethodInsn(Opcodes.INVOKEVIRTUAL, bc.getClassName(), method, "(Lrailo/runtime/PageContext;)V");
	        	}
	        	s.writeOut(_bc);
	        }
	        if(a!=null){
	        	a.returnValue();
				a.endMethod();
	        }
		}
		else {
			Iterator it = statements.iterator();
	        while(it.hasNext()) {
	        	bc.setCount(bc.getCount()+1);
	        	((Statement)it.next()).writeOut(bc);
	        }
		}
        
    }*/


	/**
	 *
	 * @see railo.transformer.bytecode.Body#isEmpty()
	 */
	public boolean isEmpty() {
		return statements.isEmpty();
	}
}
