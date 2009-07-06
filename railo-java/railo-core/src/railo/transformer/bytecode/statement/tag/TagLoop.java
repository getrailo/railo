package railo.transformer.bytecode.statement.tag;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.commons.io.IOUtil;
import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.exp.TemplateException;
import railo.runtime.type.scope.Undefined;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.cast.CastBoolean;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.statement.FlowControl;
import railo.transformer.bytecode.util.ExpressionUtil;
import railo.transformer.bytecode.util.Types;
import railo.transformer.bytecode.visitor.AndVisitor;
import railo.transformer.bytecode.visitor.DecisionDoubleVisitor;
import railo.transformer.bytecode.visitor.DecisionIntVisitor;
import railo.transformer.bytecode.visitor.DecisionObjectVisitor;
import railo.transformer.bytecode.visitor.ForContitionIntVisitor;
import railo.transformer.bytecode.visitor.ForDoubleVisitor;
import railo.transformer.bytecode.visitor.ForVisitor;
import railo.transformer.bytecode.visitor.LoopVisitor;
import railo.transformer.bytecode.visitor.TryFinallyVisitor;
import railo.transformer.bytecode.visitor.WhileVisitor;

public final class TagLoop extends TagBase implements FlowControl {


	public static final int TYPE_FILE = 0;
	public static final int TYPE_LIST = 1;
	public static final int TYPE_INDEX = 2;
	public static final int TYPE_CONDITION = 3;
	public static final int TYPE_QUERY = 4;
	public static final int TYPE_COLLECTION = 5;
	public static final int TYPE_ARRAY = 6;
	

	// VariableReference getVariableReference(PageContext pc,String var)
	private static final Method GET_VARIABLE_REFERENCE = new Method(
			"getVariableReference",
			Types.VARIABLE_REFERENCE,
			new Type[]{Types.PAGE_CONTEXT,Types.STRING});

	
	// Object set(PageContext pc, Object value)
	private static final Method SET = new Method(
			"set",
			Types.OBJECT,
			new Type[]{Types.PAGE_CONTEXT,Types.OBJECT});

	// Iterator toIterator(Object o)
	private static final Method TO_ITERATOR = new Method(
			"toIterator",
			Types.ITERATOR,
			new Type[]{Types.OBJECT});

	private static final Method NEXT = new Method(
			"next",
			Types.OBJECT,
			new Type[]{});

	private static final Method HAS_NEXT = new Method(
			"hasNext",
			Types.BOOLEAN_VALUE,
			new Type[]{});

	// File toFileExisting(PageContext pc ,String destination)
	private static final Type RESOURCE_UTIL = Type.getType(ResourceUtil.class);
	private static final Method TO_RESOURCE_EXISTING = new Method(
			"toResourceExisting",
			Types.RESOURCE,
			new Type[]{Types.PAGE_CONTEXT,Types.STRING});

	// Config getConfig()
	private static final Method GET_CONFIG = new Method(
			"getConfig",
			Types.CONFIG_WEB,
			new Type[]{});

	// SecurityManager getSecurityManager()
	private static final Method GET_SECURITY_MANAGER = new Method(
			"getSecurityManager",
			Types.SECURITY_MANAGER,
			new Type[]{});

	// void checkFileLocation(File file)
	private static final Method CHECK_FILE_LOCATION = new Method(
			"checkFileLocation",
			Types.VOID,
			new Type[]{Types.RESOURCE});

	// Reader getReader(File file, String charset)
	private static final Type IO_UTIL = Type.getType(IOUtil.class);
	private static final Method GET_BUFFERED_READER = new Method(
			"getBufferedReader",
			Types.BUFFERED_READER,
			new Type[]{Types.RESOURCE,Types.STRING});

	// void closeEL(Reader r)
	private static final Method CLOSE_EL = new Method(
			"closeEL",
			Types.VOID,
			new Type[]{Types.READER});

	// String readLine()
	private static final Method READ_LINE = new Method(
			"readLine",
			Types.STRING,
			new Type[]{});
	

	// Array listToArrayRemoveEmpty(String list, String delimeter)
	private static final Method LIST_TO_ARRAY_REMOVE_EMPTY_SS = new Method(
			"listToArrayRemoveEmpty",
			Types.ARRAY,
			new Type[]{Types.STRING,Types.STRING});
	
	// Array listToArrayRemoveEmpty(String list, char delimeter)
	private static final Method LIST_TO_ARRAY_REMOVE_EMPTY_SC = new Method(
			"listToArrayRemoveEmpty",
			Types.ARRAY,
			new Type[]{Types.STRING,Types.CHAR});

	private static final Method SIZE =  new Method(
			"size",
			Types.INT_VALUE,
			new Type[]{});


	// Object get(int key) klo
	private static final Method GETE = new Method(
			"getE",
			Types.OBJECT,
			new Type[]{Types.INT_VALUE});




	// Query getQuery(String key)
	private static final Method GET_QUERY = new Method(
			"getQuery",
			Types.QUERY,
			new Type[]{Types.STRING});

	// int getCurrentrow()
	private static final Method GET_CURRENTROW = new Method(
			"getCurrentrow",
			Types.INT_VALUE,
			new Type[]{Types.INT_VALUE});

	// double range(double number, double from)
	private static final Method RANGE = new Method(
			"range",
			Types.DOUBLE_VALUE,
			new Type[]{Types.DOUBLE_VALUE,Types.DOUBLE_VALUE});

	
	// Undefined us()
	private static final Type UNDEFINED = Type.getType(Undefined.class);
	private static final Method US = new Method(
			"us",
			UNDEFINED,
			new Type[]{});

	// void addCollection(Query coll)
	private static final Method ADD_COLLECTION = new Method(
			"addCollection",
			Types.VOID,
			new Type[]{Types.QUERY});

	// void removeCollection()
	private static final Method REMOVE_COLLECTION = new Method(
			"removeCollection",
			Types.VOID,
			new Type[]{});

	// boolean go(int index)
	private static final Method GO = new Method(
			"go",
			Types.BOOLEAN_VALUE,
			new Type[]{Types.INT_VALUE,Types.INT_VALUE});

	private static final Method GET_ID = new Method(
			"getId",
			Types.INT_VALUE,
			new Type[]{});
	private static final Method READ = new Method(
			"read",
			Types.STRING,
			new Type[]{Types.READER,Types.INT_VALUE});
	
	
	
	
	
	private int type;
	private LoopVisitor loopVisitor;

	public TagLoop(int line) {
		super(line);
	}
	public TagLoop(int sl,int el) {
		super(sl,el);
	}

	public void setType(int type) {
		this.type=type;
	}
	

	/**
	 *
	 * @see railo.transformer.bytecode.statement.tag.TagBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter)
	 */
	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		switch(type) {
		case TYPE_COLLECTION:
			writeOutTypeCollection(bc);
		break;
		case TYPE_CONDITION:
			writeOutTypeCondition(bc);
		break;
		case TYPE_FILE:
			writeOutTypeFile(bc);
		break;
		case TYPE_INDEX:
			writeOutTypeIndex(bc);
		break;
		case TYPE_LIST:
			writeOutTypeListArray(bc,false);
		break;
		case TYPE_ARRAY:
			writeOutTypeListArray(bc,true);
		break;
		case TYPE_QUERY:
			writeOutTypeQuery(bc);
		break;
		default:
			throw new BytecodeException("invalid type",getLine());
		}
	}

	/**
	 * write out collection loop
	 * @param adapter
	 * @throws TemplateException
	 */
	private void writeOutTypeCollection(BytecodeContext bc) throws BytecodeException {
		WhileVisitor whileVisitor = new WhileVisitor();
		loopVisitor=whileVisitor;
		GeneratorAdapter adapter = bc.getAdapter();
		// java.util.Iterator it=Caster.toIterator(@collection');
		int it=adapter.newLocal(Types.ITERATOR);
		getAttribute("collection").getValue().writeOut(bc,Expression.MODE_REF);
		adapter.invokeStatic(Types.CASTER,TO_ITERATOR);
		adapter.storeLocal(it);
		
		//VariableReference item=VariableInterpreter.getVariableReference(pc,item);
		int item = adapter.newLocal(Types.VARIABLE_REFERENCE);
		adapter.loadArg(0);
		
		getAttribute("item").getValue().writeOut(bc, Expression.MODE_REF);
//		if(Variable.register Key(bc, getAttribute("item").getValue()))
		
		//	adapter.invokeStatic(Types.VARIABLE_INTERPRETER, GET_VARIABLE_REFERENCE_KEY);
		//else
			adapter.invokeStatic(Types.VARIABLE_INTERPRETER, GET_VARIABLE_REFERENCE);
		adapter.storeLocal(item);
		
		// while(it.hasNext()) {
		whileVisitor.visitBeforeExpression(bc);
			adapter.loadLocal(it);
			adapter.invokeInterface(Types.ITERATOR, HAS_NEXT);
		
		whileVisitor.visitAfterExpressionBeforeBody(bc);
			// item.set(pc,it.next());
			adapter.loadLocal(item);
			adapter.loadArg(0);
				adapter.loadLocal(it);
			adapter.invokeInterface(Types.ITERATOR, NEXT);
			adapter.invokeVirtual(Types.VARIABLE_REFERENCE, SET);
			adapter.pop();
		
			getBody().writeOut(bc);
		whileVisitor.visitAfterBody(bc,getEndLine());
		
	}

	/**
	 * write out condition loop
	 * @param adapter
	 * @throws TemplateException
	 */
	private void writeOutTypeCondition(BytecodeContext bc) throws BytecodeException {
		WhileVisitor whileVisitor = new WhileVisitor();
		loopVisitor=whileVisitor;
		whileVisitor.visitBeforeExpression(bc);
			CastBoolean.toExprBoolean(getAttribute("condition").getValue()).writeOut(bc, Expression.MODE_VALUE);
		whileVisitor.visitAfterExpressionBeforeBody(bc);
			getBody().writeOut(bc);
		whileVisitor.visitAfterBody(bc,getEndLine());
		
	}
	
	/**
	 * write out file loop
	 * @param adapter
	 * @throws TemplateException
	 */
	private void writeOutTypeFile(BytecodeContext bc) throws BytecodeException {
		WhileVisitor whileVisitor = new WhileVisitor();
		loopVisitor=whileVisitor;
		GeneratorAdapter adapter = bc.getAdapter();
		
		// charset=@charset
		int charset=adapter.newLocal(Types.STRING);
		Attribute attrCharset = getAttribute("charset");
		if(attrCharset==null) adapter.visitInsn(Opcodes.ACONST_NULL);
		else attrCharset.getValue().writeOut(bc, Expression.MODE_REF);
		adapter.storeLocal(charset);
		
		// startline=@startline
		int startline=adapter.newLocal(Types.INT_VALUE);
		Attribute attrStartLine = getAttribute("startline");
		if(attrStartLine==null) attrStartLine = getAttribute("from"); // CF8
		if(attrStartLine==null) adapter.push(1);
		else {
			attrStartLine.getValue().writeOut(bc, Expression.MODE_VALUE);
			adapter.visitInsn(Opcodes.D2I);
		}
		adapter.storeLocal(startline);
		
		// endline=@endline
		int endline=adapter.newLocal(Types.INT_VALUE);
		Attribute attrEndLine = getAttribute("endline");
		if(attrEndLine==null) attrEndLine = getAttribute("to");
		if(attrEndLine==null) adapter.push(-1);
		else {
			attrEndLine.getValue().writeOut(bc, Expression.MODE_VALUE);
			adapter.visitInsn(Opcodes.D2I);
		}
		adapter.storeLocal(endline);

		
		//VariableReference index=VariableInterpreter.getVariableReference(pc,@index);
		int index = adapter.newLocal(Types.VARIABLE_REFERENCE);
		adapter.loadArg(0);
		
		getAttribute("index").getValue().writeOut(bc, Expression.MODE_REF);
		adapter.invokeStatic(Types.VARIABLE_INTERPRETER, GET_VARIABLE_REFERENCE);
		adapter.storeLocal(index);
		
		//java.io.File file=FileUtil.toResourceExisting(pc,@file);
		int resource=adapter.newLocal(Types.RESOURCE);
		adapter.loadArg(0);
		getAttribute("file").getValue().writeOut(bc, Expression.MODE_REF);
		adapter.invokeStatic(RESOURCE_UTIL, TO_RESOURCE_EXISTING);
		adapter.storeLocal(resource);
		
		// pc.getConfig().getSecurityManager().checkFileLocation(resource);
		adapter.loadArg(0);
		adapter.invokeVirtual(Types.PAGE_CONTEXT, GET_CONFIG);
		adapter.invokeInterface(Types.CONFIG_WEB, GET_SECURITY_MANAGER);
		adapter.loadLocal(resource);
		adapter.invokeInterface(Types.SECURITY_MANAGER, CHECK_FILE_LOCATION);
		
		// char[] carr=new char[characters];
		Attribute attr = getAttribute("characters");
		int carr=-1;
		if(attr!=null) {
			carr=adapter.newLocal(Types.CHAR_ARRAY);
			attr.getValue().writeOut(bc, Expression.MODE_VALUE);
			adapter.cast(Types.DOUBLE_VALUE, Types.INT_VALUE);
			adapter.newArray(Types.CHAR);
			adapter.storeLocal(carr);
		}
		
		// BufferedReader reader = IOUtil.getBufferedReader(resource,charset);
		int br=adapter.newLocal(Types.BUFFERED_READER);
		adapter.loadLocal(resource);
		adapter.loadLocal(charset);
		adapter.invokeStatic(IO_UTIL, GET_BUFFERED_READER);
		adapter.storeLocal(br);
		
		// String line;
		int line=adapter.newLocal(Types.STRING);
		
		// int count=0;  
		int count=adapter.newLocal(Types.INT_VALUE);
		adapter.push(0);
		adapter.storeLocal(count);
		

		TryFinallyVisitor tcfv=new TryFinallyVisitor();
		// try
		tcfv.visitTryBegin(bc);
			// while((line=br.readLine())!=null) { 
			//WhileVisitor wv=new WhileVisitor();
			whileVisitor.visitBeforeExpression(bc);
				DecisionObjectVisitor dv=new DecisionObjectVisitor();
				dv.visitBegin();
					if(attr!=null) {
						// IOUtil.read(bufferedreader,12)
						adapter.loadLocal(br);
						adapter.loadLocal(carr);
						adapter.arrayLength();
						adapter.invokeStatic(Types.IOUTIL, READ);
					}
					else {
						// br.readLine()
						adapter.loadLocal(br);
						adapter.invokeVirtual(Types.BUFFERED_READER, READ_LINE);
					}
					adapter.dup();
					adapter.storeLocal(line);
					
				dv.visitNEQ();
					adapter.visitInsn(Opcodes.ACONST_NULL);
				dv.visitEnd(bc);
				
			whileVisitor.visitAfterExpressionBeforeBody(bc);
				//if(++count < startLine) continue; 
				DecisionIntVisitor dv2=new DecisionIntVisitor();
				dv2.visitBegin();
					adapter.iinc(count, 1);
					adapter.loadLocal(count);
				dv2.visitLT();
					adapter.loadLocal(startline);
				dv2.visitEnd(bc);
				Label end=new Label();
				adapter.ifZCmp(Opcodes.IFEQ, end);
					whileVisitor.visitContinue(bc);
				adapter.visitLabel(end);
				
				// if(endLine!=-1 && count > endLine) break; 
				DecisionIntVisitor div=new DecisionIntVisitor();
				div.visitBegin();
					adapter.loadLocal(endline);
				div.visitNEQ();
					adapter.push(-1);
				div.visitEnd(bc);
				Label end2=new Label();
				adapter.ifZCmp(Opcodes.IFEQ, end2);
				
					DecisionIntVisitor div2 = new DecisionIntVisitor();
					div2.visitBegin();
						adapter.loadLocal(count);
					div2.visitGT();
						adapter.loadLocal(endline);
					div2.visitEnd(bc);
					Label end3=new Label();
					adapter.ifZCmp(Opcodes.IFEQ, end3);
						whileVisitor.visitBreak(bc);
					adapter.visitLabel(end3);
				adapter.visitLabel(end2);
				
				// index.set(pc,line); 
				adapter.loadLocal(index);
				adapter.loadArg(0);
				adapter.loadLocal(line);
				adapter.invokeVirtual(Types.VARIABLE_REFERENCE, SET);
				adapter.pop();
				
				getBody().writeOut(bc);
				
			whileVisitor.visitAfterBody(bc,getEndLine());
			
		
		tcfv.visitTryEndFinallyBegin(bc);
		
		
		// finally
		//tcfv.visitFinallyBegin(adapter);
			//IOUtil.closeEL(reader);
			adapter.loadLocal(br);
			adapter.invokeStatic(IO_UTIL, CLOSE_EL);
		
		tcfv.visitFinallyEnd(bc);
		
	}

	/**
	 * write out index loop
	 * @param adapter
	 * @throws TemplateException
	 */
	private void writeOutTypeIndex(BytecodeContext bc) throws BytecodeException {
		ForDoubleVisitor forContitionVisitor = new ForDoubleVisitor();
		loopVisitor=forContitionVisitor;
		GeneratorAdapter adapter = bc.getAdapter();

		// int from=(int)@from;
		int from=adapter.newLocal(Types.DOUBLE_VALUE);
		ExpressionUtil.writeOutSilent(getAttribute("from").getValue(), bc, Expression.MODE_VALUE);
		adapter.storeLocal(from);	
		
		// int to=(int)@to;
		int to=adapter.newLocal(Types.DOUBLE_VALUE);
		ExpressionUtil.writeOutSilent(getAttribute("to").getValue(), bc, Expression.MODE_VALUE);
		adapter.storeLocal(to);	
		
		// int step=(int)@step;
		int step=adapter.newLocal(Types.DOUBLE_VALUE);
		Attribute attrStep = getAttribute("step");
		if(attrStep!=null) {
			ExpressionUtil.writeOutSilent(attrStep.getValue(), bc, Expression.MODE_VALUE);
		}
		else {
			adapter.push(1D);
		}
		adapter.storeLocal(step);
		
		// boolean dirPlus=(step > 0);
		int dirPlus=adapter.newLocal(Types.BOOLEAN_VALUE);
		DecisionDoubleVisitor div=new DecisionDoubleVisitor();
		div.visitBegin();
			adapter.loadLocal(step);
		div.visitGT();
			adapter.push(0D);
		div.visitEnd(bc);
		adapter.storeLocal(dirPlus);
		
		//if(step!=0) {
		div=new DecisionDoubleVisitor();
		div.visitBegin();
			adapter.loadLocal(step);
		div.visitNEQ();
			adapter.push(0D);
		div.visitEnd(bc);
		Label ifEnd=new Label();
		adapter.ifZCmp(Opcodes.IFEQ, ifEnd);
			
			// VariableReference index>=VariableInterpreter.getVariableReference(pc,@index));
			int index = adapter.newLocal(Types.VARIABLE_REFERENCE);
			adapter.loadArg(0);
			ExpressionUtil.writeOutSilent(getAttribute("index").getValue(), bc, Expression.MODE_REF);
			adapter.invokeStatic(Types.VARIABLE_INTERPRETER, GET_VARIABLE_REFERENCE);
			adapter.storeLocal(index);
			

			// index.set(pc,from);
			adapter.loadLocal(index);
			adapter.loadArg(0);
			
			adapter.visitTypeInsn(Opcodes.NEW, "java/lang/Double");
			adapter.visitInsn(Opcodes.DUP);
			adapter.loadLocal(from);
			adapter.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Double", "<init>", "(D)V");

			adapter.invokeVirtual(Types.VARIABLE_REFERENCE, SET);
			adapter.pop();		
			
			// for
				
			int i=forContitionVisitor.visitBeforeExpression(adapter,from,step,true);
				adapter.loadLocal(dirPlus);
				Label l1 = new Label();
				adapter.visitJumpInsn(Opcodes.IFEQ, l1);
				
					div=new DecisionDoubleVisitor();
					div.visitBegin();
						adapter.visitVarInsn(Opcodes.DLOAD, i);
					div.visitLTE();
						adapter.loadLocal(to);
					div.visitEnd(bc);
					
				Label l2 = new Label();
				adapter.visitJumpInsn(Opcodes.GOTO, l2);
				adapter.visitLabel(l1);
				
					div=new DecisionDoubleVisitor();
					div.visitBegin();
						adapter.visitVarInsn(Opcodes.DLOAD, i);
					div.visitGTE();
						adapter.loadLocal(to);
					div.visitEnd(bc);
				
				adapter.visitLabel(l2);
			forContitionVisitor.visitAfterExpressionBeginBody(adapter);
				adapter.loadLocal(index);
				adapter.loadArg(0);
				
				adapter.visitTypeInsn(Opcodes.NEW, "java/lang/Double");
				adapter.visitInsn(Opcodes.DUP);
				adapter.visitVarInsn(Opcodes.DLOAD, i);
				adapter.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Double", "<init>", "(D)V");

				adapter.invokeVirtual(Types.VARIABLE_REFERENCE, SET);
				adapter.pop();
				
				getBody().writeOut(bc);
			
			forContitionVisitor.visitEndBody(bc,getEndLine());
				
			
			
			////// set i after usage
			adapter.loadLocal(index);
			adapter.loadArg(0);
			
			adapter.visitTypeInsn(Opcodes.NEW, "java/lang/Double");
			adapter.visitInsn(Opcodes.DUP);
			adapter.visitVarInsn(Opcodes.DLOAD, i);
			adapter.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Double", "<init>", "(D)V");

			adapter.invokeVirtual(Types.VARIABLE_REFERENCE, SET);
			adapter.pop();
			
		adapter.visitLabel(ifEnd);
		
	}
	
	/**
	 * write out list loop
	 * @param adapter
	 * @throws TemplateException
	 */
	private void writeOutTypeListArray(BytecodeContext bc, boolean isArray) throws BytecodeException {
		ForVisitor forVisitor = new ForVisitor();
		loopVisitor=forVisitor;
		GeneratorAdapter adapter = bc.getAdapter();

		//List.listToArrayRemoveEmpty("", 'c')
		int array = adapter.newLocal(Types.ARRAY);
		int len = adapter.newLocal(Types.INT_VALUE);
		int index = adapter.newLocal(Types.VARIABLE_REFERENCE);
		
		if(isArray) {
			getAttribute("array").getValue().writeOut(bc, Expression.MODE_REF);
		}
		else {
			// array=List.listToArrayRemoveEmpty(list, delimter)
			getAttribute("list").getValue().writeOut(bc, Expression.MODE_REF);
			if(containsAttribute("delimiters")) {
				getAttribute("delimiters").getValue().writeOut(bc, Expression.MODE_REF);
				adapter.invokeStatic(Types.LIST, LIST_TO_ARRAY_REMOVE_EMPTY_SS);
			}
			else {
				adapter.visitIntInsn(Opcodes.BIPUSH, 44);// ','
				//adapter.push(',');
				adapter.invokeStatic(Types.LIST, LIST_TO_ARRAY_REMOVE_EMPTY_SC);
			}
		}
		adapter.storeLocal(array);
		
		
		// int len=array.size();
		adapter.loadLocal(array);
		adapter.invokeInterface(Types.ARRAY, SIZE);
		adapter.storeLocal(len);

		//VariableInterpreter.getVariableReference(pc,Caster.toString(index));
		adapter.loadArg(0);
		getAttribute("index").getValue().writeOut(bc, Expression.MODE_REF);
		adapter.invokeStatic(Types.VARIABLE_INTERPRETER, GET_VARIABLE_REFERENCE);
		adapter.storeLocal(index);
		

		// for(int i=1;i<=len;i++) {		
		int i = forVisitor.visitBegin(adapter, 1, false);
			// index.set(pc, list.get(i));
			adapter.loadLocal(index);
			adapter.loadArg(0);
				adapter.loadLocal(array);
				adapter.visitVarInsn(Opcodes.ILOAD, i);
			adapter.invokeInterface(Types.ARRAY, GETE);
			adapter.invokeVirtual(Types.VARIABLE_REFERENCE, SET);
			adapter.pop();
			getBody().writeOut(bc);
		forVisitor.visitEnd(bc, len, true,getStartLine()); 
	}
	
	/**
	 * write out query loop
	 * @param adapter
	 * @throws TemplateException
	 */
	private void writeOutTypeQuery(BytecodeContext bc) throws BytecodeException {
		ForContitionIntVisitor forContitionVisitor = new ForContitionIntVisitor();// TODO replace with ForIntVisitor 
		loopVisitor=forContitionVisitor;
		GeneratorAdapter adapter = bc.getAdapter();
		// railo.runtime.type.Query query=pc.getQuery(@query);
		int query=adapter.newLocal(Types.QUERY);
		adapter.loadArg(0);
		getAttribute("query").getValue().writeOut(bc, Expression.MODE_REF);
		adapter.invokeVirtual(Types.PAGE_CONTEXT, GET_QUERY);
		adapter.dup();
		adapter.storeLocal(query);
		
		int queryImpl = adapter.newLocal(Types.QUERY_IMPL);
		//adapter.loadLocal(query);
		adapter.checkCast(Types.QUERY_IMPL);
		adapter.storeLocal(queryImpl);
		
		
		// int startAt=query.getCurrentrow();
		int startAt=adapter.newLocal(Types.INT_VALUE);
		adapter.loadLocal(queryImpl);
		adapter.loadArg(0);
		adapter.invokeVirtual(Types.PAGE_CONTEXT, GET_ID);
		adapter.invokeVirtual(Types.QUERY_IMPL, GET_CURRENTROW);
		adapter.storeLocal(startAt);
		
		
		// startrow
		int start=adapter.newLocal(Types.INT_VALUE);
		Attribute attrStartRow = getAttribute("startrow");
		if(attrStartRow!=null){
			//railo.runtime.util.NumberRange.range(@startrow,1)
			attrStartRow.getValue().writeOut(bc, Expression.MODE_VALUE);
			adapter.push(1d);
			adapter.invokeStatic(Types.NUMBER_RANGE, RANGE);
			adapter.visitInsn(Opcodes.D2I);
		}
		else {
			adapter.push(1);
		}
		adapter.storeLocal(start);
		
		// endrow
		int end=adapter.newLocal(Types.INT_VALUE);
		Attribute attrEndRow = getAttribute("endrow");
		if(attrEndRow!=null){
			attrEndRow.getValue().writeOut(bc, Expression.MODE_VALUE);
			adapter.visitInsn(Opcodes.D2I);
			adapter.storeLocal(end);
		}
		
		// pc.us().addCollection(query);
		adapter.loadArg(0);
		adapter.invokeVirtual(Types.PAGE_CONTEXT, US);
		adapter.loadLocal(query);
		adapter.invokeInterface(UNDEFINED, ADD_COLLECTION);
		
		// try
		TryFinallyVisitor tfv=new TryFinallyVisitor();
		tfv.visitTryBegin(bc);
			// For
			
			int i=forContitionVisitor.visitBegin(adapter, start, true);
				getBody().writeOut(bc);
			forContitionVisitor.visitEndBeforeContition(bc,1,false,getStartLine());
				
				// && i<=endrow
				if(attrEndRow!=null){
					AndVisitor av=new AndVisitor();
					av.visitBegin();
						adapter.loadLocal(queryImpl);
						adapter.visitVarInsn(Opcodes.ILOAD, i);

						adapter.loadArg(0);
						adapter.invokeVirtual(Types.PAGE_CONTEXT, GET_ID);
						adapter.invokeVirtual(Types.QUERY_IMPL, GO);
					av.visitMiddle(bc);
						//LitBoolean.TRUE.writeOut(bc, Expression.MODE_VALUE);
						DecisionIntVisitor dv=new DecisionIntVisitor();
						dv.visitBegin();
							adapter.visitVarInsn(Opcodes.ILOAD, i);
						dv.visitLTE();
							adapter.loadLocal(end);
						dv.visitEnd(bc);
					av.visitEnd(bc);
				}
				else {
					adapter.loadLocal(queryImpl);
					adapter.visitVarInsn(Opcodes.ILOAD, i);

					adapter.loadArg(0);
					adapter.invokeVirtual(Types.PAGE_CONTEXT, GET_ID);
					adapter.invokeVirtual(Types.QUERY_IMPL, GO);
				}
				
				
			forContitionVisitor.visitEndAfterContition(bc);

		// Finally
		tfv.visitTryEndFinallyBegin(bc);
			// pc.us().removeCollection();
			adapter.loadArg(0);
			adapter.invokeVirtual(Types.PAGE_CONTEXT, US);
			adapter.invokeInterface(UNDEFINED, REMOVE_COLLECTION);
		
			// query.go(startAt);
			adapter.loadLocal(queryImpl);
			adapter.loadLocal(startAt);
			

			adapter.loadArg(0);
			adapter.invokeVirtual(Types.PAGE_CONTEXT, GET_ID);
			
			
			adapter.invokeVirtual(Types.QUERY_IMPL, GO);
			adapter.pop();
		tfv.visitFinallyEnd(bc);
	}

	/**
	 * @see railo.transformer.bytecode.statement.FlowControl#getBreakLabel()
	 */
	public Label getBreakLabel() {
		return loopVisitor.getBreakLabel();
	}

	/**
	 * @see railo.transformer.bytecode.statement.FlowControl#getContinueLabel()
	 */
	public Label getContinueLabel() {
		return loopVisitor.getContinueLabel();
	}


}
