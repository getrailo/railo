package railo.transformer.bytecode.statement;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.runtime.type.scope.Scope;
import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.expression.ExprString;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.var.DataMember;
import railo.transformer.bytecode.expression.var.Variable;
import railo.transformer.bytecode.expression.var.VariableRef;
import railo.transformer.bytecode.expression.var.VariableString;
import railo.transformer.bytecode.literal.LitBoolean;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.statement.tag.TagTry;
import railo.transformer.bytecode.util.ExpressionUtil;
import railo.transformer.bytecode.util.Types;
import railo.transformer.bytecode.visitor.OnFinally;
import railo.transformer.bytecode.visitor.TryCatchFinallyVisitor;

/**
 * produce  try-catch-finally
 */
public final class TryCatchFinally extends StatementBase implements Opcodes,HasBodies {

	//private static LitString ANY=LitString.toExprString("any", -1);
	
	private static final Method TO_PAGE_EXCEPTION = new Method(
			"toPageException",
			Types.PAGE_EXCEPTION,
			new Type[]{Types.THROWABLE});
	
	
	
	//  public boolean typeEqual(String type);
	private static final Method TYPE_EQUAL = new Method(
			"typeEqual",
			Types.BOOLEAN_VALUE,
			new Type[]{Types.STRING});
	
	
	// Struct getCatchBlock(PageContext pc);
	private static final Method GET_CATCH_BLOCK = new Method(
			"getCatchBlock",
			Types.STRUCT,
			new Type[]{Types.PAGE_CONTEXT});


	// void isAbort(e)
	public static final Method IS_ABORT = new Method(
			"isAbort",
			Types.BOOLEAN_VALUE,
			new Type[]{Types.THROWABLE});

	

	private final static Method SET = new Method("set",Types.OBJECT,new Type[]{Types.PAGE_CONTEXT,Types.OBJECT});

	private static final Method REMOVE_EL = new Method("removeEL",Types.OBJECT,new Type[]{Types.PAGE_CONTEXT});
	
	
	
	
	private Body tryBody;
	private Body finallyBody;
	private List<Catch> catches=new ArrayList<Catch>();
	private Position finallyLine;



	private FlowControlFinal fcf;


	/**
	 * Constructor of the class
	 * @param body
	 * @param line
	 */
	public TryCatchFinally(Body body,Position start, Position end) {
		super(start,end);
		this.tryBody=body;
		body.setParent(this);
	}

	/**
	 * sets finally body
	 * @param body
	 */
	public void setFinally(Body body, Position finallyLine) {
		body.setParent(this);
		this.finallyBody=body;
		this.finallyLine=finallyLine;
	}

	/**
	 * data for a single catch block
	 */
	private class Catch {

		private ExprString type;
		private Body body;
		private VariableRef name;
		private Position line;

		public Catch(ExprString type, VariableRef name, Body body, Position line) {
			this.type=type;
			this.name=name;
			this.body=body;
			this.line=line;
		}
		
	}
	
	
	
	/**
	 *
	 * @see railo.transformer.bytecode.statement.StatementBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter)
	 */
	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		GeneratorAdapter adapter = bc.getAdapter();
		// Reference ref=null;
		final int lRef=adapter.newLocal(Types.REFERENCE);
		adapter.visitInsn(Opcodes.ACONST_NULL);
		adapter.storeLocal(lRef);
		
		// has no try body, if there is no try body, no catches are executed, only finally 
		if(!tryBody.hasStatements()) {
			if(finallyBody!=null)finallyBody.writeOut(bc);
			return;
		}
		
		
		TryCatchFinallyVisitor tcfv=new TryCatchFinallyVisitor(new OnFinally() {
			
			public void writeOut(BytecodeContext bc) throws BytecodeException {
				_writeOutFinally(bc,lRef);
			}
		},getFlowControlFinal());

		// try
		tcfv.visitTryBegin(bc);
			tryBody.writeOut(bc);
		int lThrow = tcfv.visitTryEndCatchBeging(bc);
			_writeOutCatch(bc, lRef, lThrow);
		tcfv.visitCatchEnd(bc);
		
	}
	
	
	
	
	
	
	
	
	private void _writeOutFinally(BytecodeContext bc, int lRef) throws BytecodeException {
		// ref.remove(pc);
		//Reference r=null;
		GeneratorAdapter adapter = bc.getAdapter();
		
		//if(fcf!=null && fcf.getAfterFinalGOTOLabel()!=null)ASMUtil.visitLabel(adapter,fcf.getFinalEntryLabel());
		ExpressionUtil.visitLine(bc, finallyLine);
		
		
		
		//if (reference != null)
		//    reference.removeEL(pagecontext);
		Label removeEnd=new Label();
		adapter.loadLocal(lRef);
		adapter.ifNull(removeEnd);
			adapter.loadLocal(lRef);
			adapter.loadArg(0);
			adapter.invokeInterface(Types.REFERENCE, REMOVE_EL);
			adapter.pop();
		adapter.visitLabel(removeEnd);
		
		if(finallyBody!=null)finallyBody.writeOut(bc); // finally
		/*if(fcf!=null){
			Label l = fcf.getAfterFinalGOTOLabel();
			if(l!=null)adapter.visitJumpInsn(Opcodes.GOTO, l);
		}*/
	}
	
	private void _writeOutCatch(BytecodeContext bc, int lRef,int lThrow) throws BytecodeException {
		GeneratorAdapter adapter = bc.getAdapter();
		int pe=adapter.newLocal(Types.PAGE_EXCEPTION);
		
		
		// instance of Abort
			Label abortEnd=new Label();
			adapter.loadLocal(lThrow);
			adapter.invokeStatic(Types.ABORT, TryCatchFinally.IS_ABORT);
			//adapter.instanceOf(Types.ABORT);
	        adapter.ifZCmp(Opcodes.IFEQ, abortEnd);
	        adapter.loadLocal(lThrow);
	        adapter.throwException();
	        adapter.visitLabel(abortEnd);


	        // PageExceptionImpl old=pc.getCatch();
	        int old=adapter.newLocal(Types.PAGE_EXCEPTION);
	        adapter.loadArg(0);
	        adapter.invokeVirtual(Types.PAGE_CONTEXT, TagTry.GET_CATCH);
			adapter.storeLocal(old);
	        
	        
			// cast to PageException  Caster.toPagException(t);
	        adapter.loadLocal(lThrow);
	        adapter.invokeStatic(Types.CASTER, TO_PAGE_EXCEPTION);
	        
	    // PageException pe=...
	        adapter.storeLocal(pe);

	    // catch loop
			Label endAllIf = new Label();
	        Iterator<Catch> it = catches.iterator();
	        Catch ctElse=null;
			while(it.hasNext()) {
				Catch ct=it.next();
				// store any for else
				if(ct.type!=null && ct.type instanceof LitString && ((LitString)ct.type).getString().equalsIgnoreCase("any")){
					ctElse=ct;
					continue;
				}
				
				ExpressionUtil.visitLine(bc, ct.line);
				
				// pe.typeEqual(type)
				if(ct.type==null){
					LitBoolean.TRUE.writeOut(bc, Expression.MODE_VALUE);
				}
				else {
					adapter.loadLocal(pe);
					ct.type.writeOut(bc, Expression.MODE_REF);
					adapter.invokeVirtual(Types.PAGE_EXCEPTION, TYPE_EQUAL);
				}
				
				
				

				Label endIf = new Label();
	            adapter.ifZCmp(Opcodes.IFEQ, endIf);
	            
	            catchBody(bc,adapter,ct,pe,lRef,true);
	            
	            adapter.visitJumpInsn(Opcodes.GOTO, endAllIf);
	            adapter.visitLabel(endIf);
	            

				
			}
			
			if(ctElse!=null){
				catchBody(bc,adapter,ctElse,pe,lRef,true);
			}
			else{
			// pc.setCatch(pe,true);
				adapter.loadArg(0);
		        adapter.loadLocal(pe);
		        adapter.push(false);
		        adapter.push(false);
		        adapter.invokeVirtual(Types.PAGE_CONTEXT, TagTry.SET_CATCH3);
	            
				
				adapter.loadLocal(pe);
				adapter.throwException();
			}
			adapter.visitLabel(endAllIf);
			
    		// PageExceptionImpl old=pc.setCatch(old);
            adapter.loadArg(0);
            adapter.loadLocal(old);
            adapter.invokeVirtual(Types.PAGE_CONTEXT, TagTry.SET_CATCH_PE);
			
	}

	private static void catchBody(BytecodeContext bc, GeneratorAdapter adapter, Catch ct, int pe, int lRef,boolean caugth) throws BytecodeException {
		// pc.setCatch(pe,true);
		adapter.loadArg(0);
        adapter.loadLocal(pe);
        adapter.push(caugth);
        adapter.push(false);
        adapter.invokeVirtual(Types.PAGE_CONTEXT, TagTry.SET_CATCH3);
        
        
        // ref=
        ct.name.writeOut(bc, Expression.MODE_REF);
        adapter.storeLocal(lRef);
        
		adapter.loadLocal(lRef);
		adapter.loadArg(0);
		adapter.loadLocal(pe);// (...,pe.getCatchBlock(pc))
		adapter.loadArg(0);
        adapter.invokeVirtual(Types.PAGE_EXCEPTION, GET_CATCH_BLOCK);
		adapter.invokeInterface(Types.REFERENCE, SET);
		adapter.pop();
     	
        ct.body.writeOut(bc);
	}

	/**
	 * @param type
	 * @param name
	 * @param body
	 * @param line
	 */
	public void addCatch(ExprString type, VariableRef name, Body body, Position line) {
		body.setParent(this);
		catches.add(new Catch(type,name,body,line));
	}

	/**
	 * @param type
	 * @param name
	 * @param b
	 * @param line
	 * @throws BytecodeException
	 */
	public void addCatch(Expression type, Expression name, Body b, Position line) throws BytecodeException {
		
		// type
		if(type==null || type instanceof ExprString) ;
		else if(type instanceof Variable) {
			type=VariableString.toExprString(type);
		}
		else throw new BytecodeException("type from catch statement is invalid",type.getStart());
		
		// name
		if(name instanceof LitString){
			Variable v = new Variable(Scope.SCOPE_UNDEFINED,name.getStart(),name.getEnd());
			v.addMember(new DataMember(name));
			name=new VariableRef(v);
		}
		else if(name instanceof Variable) name=new VariableRef((Variable) name);
		else throw new BytecodeException("name from catch statement is invalid",name.getStart());
		
		addCatch((ExprString)type, (VariableRef)name, b, line);
	}	
	

	/**
	 * @see railo.transformer.bytecode.statement.HasBodies#getBodies()
	 */
	public Body[] getBodies() {
		
		int len=catches.size(),count=0;
		if(tryBody!=null)len++;
		if(finallyBody!=null)len++;
		Body[] bodies=new Body[len];
		Catch c;
		Iterator<Catch> it = catches.iterator();
		while(it.hasNext()) {
			c=it.next();
			bodies[count++]=c.body;
		}
		if(tryBody!=null)bodies[count++]=tryBody;
		if(finallyBody!=null)bodies[count++]=finallyBody;
		
		return bodies;
	}

	@Override
	public FlowControlFinal getFlowControlFinal() {
		if(fcf==null) fcf=new FlowControlFinalImpl();
		return fcf;
	}
}
