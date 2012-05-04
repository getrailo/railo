package railo.transformer.bytecode.statement.tag;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.runtime.exp.TemplateException;
import railo.runtime.type.scope.Undefined;
import railo.runtime.util.NumberIterator;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.util.Types;
import railo.transformer.bytecode.visitor.DecisionIntVisitor;
import railo.transformer.bytecode.visitor.NotVisitor;
import railo.transformer.bytecode.visitor.OnFinally;
import railo.transformer.bytecode.visitor.ParseBodyVisitor;
import railo.transformer.bytecode.visitor.TryFinallyVisitor;
import railo.transformer.bytecode.visitor.WhileVisitor;

public final class TagOutput extends TagBase {


	public static final int TYPE_QUERY = 0;
	public static final int TYPE_GROUP = 1;
	public static final int TYPE_INNER_GROUP = 2;
	public static final int TYPE_INNER_QUERY = 3;
	public static final int TYPE_NORMAL= 4;
	
	// int getCurrentrow()
	/*public static final Method GET_CURRENTROW_1 = new Method(
			"getCurrentrow",
			Types.INT_VALUE,
			new Type[]{Types.INT_VALUE});*/

	// Undefined us()
	public static final Type UNDEFINED = Type.getType(Undefined.class);
	public static final Method US = new Method(
			"us",
			UNDEFINED,
			new Type[]{});

	// void addCollection(Query coll)
	public static final Method ADD_COLLECTION = new Method(
			"addCollection",
			Types.VOID,
			new Type[]{Types.QUERY});

	// void removeCollection()
	public static final Method REMOVE_COLLECTION = new Method(
			"removeCollection",
			Types.VOID,
			new Type[]{});

	
	// int getRecordcount()
	public static final Method GET_RECORDCOUNT = new Method(
			"getRecordcount",
			Types.INT_VALUE,
			new Type[]{});

	// double range(double number, double from)
	public static final Method RANGE = new Method(
			"range",
			Types.DOUBLE_VALUE,
			new Type[]{Types.DOUBLE_VALUE,Types.DOUBLE_VALUE});

	public static final Type NUMBER_ITERATOR = Type.getType(NumberIterator.class);

	// NumberIterator load(double from, double to, double max) 
	public static final Method LOAD_3 = new Method(
			"load",
			NUMBER_ITERATOR,
			new Type[]{Types.DOUBLE_VALUE,Types.DOUBLE_VALUE,Types.DOUBLE_VALUE});


	// NumberIterator load(double from, double to, double max) 
	public static final Method LOAD_2 = new Method(
			"load",
			NUMBER_ITERATOR,
			new Type[]{Types.DOUBLE_VALUE,Types.DOUBLE_VALUE});
	

	// NumberIterator load(NumberIterator ni, Query query, String groupName, boolean caseSensitive)
	public static final Method LOAD_5 = new Method(
			"load",
			NUMBER_ITERATOR,
			new Type[]{Types.PAGE_CONTEXT, NUMBER_ITERATOR,Types.QUERY,Types.STRING,Types.BOOLEAN_VALUE});

	// boolean isValid()
	public static final Method IS_VALID = new Method(
			"isValid",
			Types.BOOLEAN_VALUE,
			new Type[]{});

	// int current()
	public static final Method CURRENT = new Method(
			"current",
			Types.INT_VALUE,
			new Type[]{});

	// void release(NumberIterator ni)
	public static final Method REALEASE = new Method(
			"release",
			Types.VOID,
			new Type[]{NUMBER_ITERATOR});

	// void setCurrent(int current)
	public static final Method SET_CURRENT = new Method(
			"setCurrent",
			Types.VOID,
			new Type[]{Types.INT_VALUE});
	

	// void reset()
	public static final Method RESET = new Method(
			"reset",
			Types.VOID,
			new Type[]{Types.INT_VALUE});

	// int first()
	public static final Method FIRST = new Method(
			"first",
			Types.INT_VALUE,
			new Type[]{});
	private static final Method GET_ID = new Method(
			"getId",
			Types.INT_VALUE,
			new Type[]{});


	private int type;
	
	private int numberIterator=-1;
	private int query=-1;
	//private int queryImpl=-1;
	private int group=-1;
	private int pid;

	public TagOutput(Position start,Position end) {
		super(start,end);
	}


	public static TagOutput getParentTagOutputQuery(Statement stat) throws BytecodeException {
		Statement parent=stat.getParent();
		if(parent==null) throw new BytecodeException("there is no parent output with query",null);
		else if(parent instanceof TagOutput) {
			if(((TagOutput)parent).hasQuery())
				return ((TagOutput)parent);
		}
		return getParentTagOutputQuery(parent);
	}

	public void setType(int type) {
		this.type=type;
	}


	/**
	 *
	 * @see railo.transformer.bytecode.statement.tag.TagBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter)
	 */
	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		boolean old;
		switch(type) {
		case TYPE_GROUP:
			old = bc.changeDoSubFunctions(false);
			writeOutTypeGroup(bc);
			bc.changeDoSubFunctions(old);
		break;
		case TYPE_INNER_GROUP:
			old = bc.changeDoSubFunctions(false);
			writeOutTypeInnerGroup(bc);
			bc.changeDoSubFunctions(old);
		break;
		case TYPE_INNER_QUERY:
			old = bc.changeDoSubFunctions(false);
			writeOutTypeInnerQuery(bc);
			bc.changeDoSubFunctions(old);
		break;
		case TYPE_NORMAL:
			writeOutTypeNormal(bc);
		break;
		case TYPE_QUERY:
			old = bc.changeDoSubFunctions(false);
			writeOutTypeQuery(bc);
			bc.changeDoSubFunctions(old);
		break;
		
		default:
			throw new BytecodeException("invalid type",getStart());
		}
	}


	private void writeOutTypeGroup(BytecodeContext bc) throws BytecodeException {
		GeneratorAdapter adapter = bc.getAdapter();
		ParseBodyVisitor pbv=new ParseBodyVisitor();
		pbv.visitBegin(bc);
		
		// Group
		Attribute attrGroup = getAttribute("group");
		group=adapter.newLocal(Types.STRING);
		attrGroup.getValue().writeOut(bc, Expression.MODE_REF);
		adapter.storeLocal(group);
		
		// Group Case Sensitve
		Attribute attrGroupCS = getAttribute("groupcasesensitive");
		int groupCaseSensitive=adapter.newLocal(Types.BOOLEAN_VALUE);
		if(attrGroupCS!=null)	attrGroupCS.getValue().writeOut(bc, Expression.MODE_VALUE);
		else 					adapter.push(true);
		adapter.storeLocal(groupCaseSensitive);
		
		TagOutput parent = TagOutput.getParentTagOutputQuery(this);
		numberIterator = parent.getNumberIterator();
		query = parent.getQuery();
		//queryImpl = parent.getQueryImpl();
		
		// current
		int current=adapter.newLocal(Types.INT_VALUE);
		adapter.loadLocal(numberIterator);
		adapter.invokeVirtual(TagOutput.NUMBER_ITERATOR, TagOutput.CURRENT);
		adapter.storeLocal(current);
		
		
		// current
		int icurrent=adapter.newLocal(Types.INT_VALUE);
		WhileVisitor wv = new WhileVisitor();
		wv.visitBeforeExpression(bc);
			
			//while(ni.isValid()) {
			adapter.loadLocal(numberIterator);
			adapter.invokeVirtual(TagOutput.NUMBER_ITERATOR, TagOutput.IS_VALID);
			
		wv.visitAfterExpressionBeforeBody(bc);
		
			// if(!query.go(ni.current()))break; 
			adapter.loadLocal(query);
			adapter.loadLocal(numberIterator);
			adapter.invokeVirtual(TagOutput.NUMBER_ITERATOR, TagOutput.CURRENT);
			
			adapter.loadArg(0);
			adapter.invokeVirtual(Types.PAGE_CONTEXT, GET_ID);
			adapter.invokeInterface(Types.QUERY, TagLoop.GO);
			
			/* OLD
			adapter.invokeInterface(Types.QUERY, TagLoop.GO_1);
			*/
			NotVisitor.visitNot(bc);
			Label _if=new Label();
			adapter.ifZCmp(Opcodes.IFEQ, _if);
				wv.visitBreak(bc);
			adapter.visitLabel(_if);
		
			// NumberIterator oldNi=numberIterator;
			int oldNi=adapter.newLocal(TagOutput.NUMBER_ITERATOR);
			
			adapter.loadLocal(numberIterator);
			adapter.storeLocal(oldNi);
			
			// numberIterator=NumberIterator.load(ni,query,group,grp_case);
			adapter.loadArg(0);
			adapter.loadLocal(numberIterator);
			adapter.loadLocal(query);
			adapter.loadLocal(group);
			adapter.loadLocal(groupCaseSensitive);
			adapter.invokeStatic(TagOutput.NUMBER_ITERATOR, TagOutput.LOAD_5);
			adapter.storeLocal(numberIterator);
			
			// current=oldNi.current();
			adapter.loadLocal(oldNi);
			adapter.invokeVirtual(TagOutput.NUMBER_ITERATOR, TagOutput.CURRENT);
			adapter.storeLocal(icurrent);
			
			getBody().writeOut(bc);
			
			//tmp(adapter,current);
			
			
			// NumberIterator.release(ni);
			adapter.loadLocal(numberIterator);
			adapter.invokeStatic(TagOutput.NUMBER_ITERATOR, TagOutput.REALEASE);
			
			// numberIterator=oldNi;
			adapter.loadLocal(oldNi);
			adapter.storeLocal(numberIterator);
		
			// ni.setCurrent(current+1);
			adapter.loadLocal(numberIterator);
			adapter.loadLocal(icurrent);
			adapter.push(1);
			adapter.visitInsn(Opcodes.IADD);
			adapter.invokeVirtual(TagOutput.NUMBER_ITERATOR, TagOutput.SET_CURRENT);
	
		wv.visitAfterBody(bc,getEnd());
	

		//query.go(ni.current(),pc.getId())
		resetCurrentrow(adapter,current);
		
		// ni.first();
		adapter.loadLocal(numberIterator);
		adapter.invokeVirtual(TagOutput.NUMBER_ITERATOR, TagOutput.FIRST);
		adapter.pop();
		

		pbv.visitEnd(bc);
	}


	private void resetCurrentrow(GeneratorAdapter adapter, int current) {
		//query.go(ni.current(),pc.getId())
		adapter.loadLocal(query);
		adapter.loadLocal(current);
		adapter.loadArg(0);
		adapter.invokeVirtual(Types.PAGE_CONTEXT, GET_ID);
		adapter.invokeInterface(Types.QUERY, TagLoop.GO);
		
		/* OLD
		adapter.invokeInterface(Types.QUERY, TagLoop.GO_1);
		*/
		adapter.pop();
		
	}
	private void writeOutTypeInnerGroup(BytecodeContext bc) throws BytecodeException {
		GeneratorAdapter adapter = bc.getAdapter();

		TagOutput parent = TagOutput.getParentTagOutputQuery(this);
		numberIterator = parent.getNumberIterator();
		query = parent.getQuery();
		//queryImpl = parent.getQueryImpl();
		
		int current=adapter.newLocal(Types.INT_VALUE);
		adapter.loadLocal(numberIterator);
		adapter.invokeVirtual(TagOutput.NUMBER_ITERATOR, TagOutput.CURRENT);
		adapter.storeLocal(current);
		
		
		// inner current
		int icurrent=adapter.newLocal(Types.INT_VALUE);
		WhileVisitor wv = new WhileVisitor();
		wv.visitBeforeExpression(bc);
			
			//while(ni.isValid()) {
			adapter.loadLocal(numberIterator);
			adapter.invokeVirtual(TagOutput.NUMBER_ITERATOR, TagOutput.IS_VALID);
			
		wv.visitAfterExpressionBeforeBody(bc);
		
			// if(!query.go(ni.current()))break; 
			
			adapter.loadLocal(query);
			adapter.loadLocal(numberIterator);
			adapter.invokeVirtual(TagOutput.NUMBER_ITERATOR, TagOutput.CURRENT);
			
			adapter.loadArg(0);
			adapter.invokeVirtual(Types.PAGE_CONTEXT, GET_ID);
			adapter.invokeInterface(Types.QUERY, TagLoop.GO);
			
			/*OLD
			adapter.invokeInterface(Types.QUERY, TagLoop.GO_1);
			*/
			NotVisitor.visitNot(bc);
			Label _if=new Label();
			adapter.ifZCmp(Opcodes.IFEQ, _if);
				wv.visitBreak(bc);
			adapter.visitLabel(_if);
		
			// current=ni.current();
			adapter.loadLocal(numberIterator);
			adapter.invokeVirtual(TagOutput.NUMBER_ITERATOR, TagOutput.CURRENT);
			adapter.storeLocal(icurrent);
			
			getBody().writeOut(bc);
			
			// ni.setCurrent(current+1);
			adapter.loadLocal(numberIterator);
			adapter.loadLocal(icurrent);
			adapter.push(1);
			adapter.visitInsn(Opcodes.IADD);
			adapter.invokeVirtual(TagOutput.NUMBER_ITERATOR, TagOutput.SET_CURRENT);
	
		wv.visitAfterBody(bc,getEnd());
	
		resetCurrentrow(adapter,current);
		
		
		// ni.first();
		adapter.loadLocal(numberIterator);
		adapter.invokeVirtual(TagOutput.NUMBER_ITERATOR, TagOutput.FIRST);
		adapter.pop();
	}


	private void writeOutTypeInnerQuery(BytecodeContext bc) throws BytecodeException {
		GeneratorAdapter adapter = bc.getAdapter();
		//if(tr ue)return ;
		TagOutput parent = TagOutput.getParentTagOutputQuery(this);
		numberIterator = parent.getNumberIterator();
		query = parent.getQuery();
		pid=parent.getPID();
		//queryImpl = parent.getQueryImpl();
		
		//int currentOuter=ni.current();
		int currentOuter=adapter.newLocal(Types.INT_VALUE);
		adapter.loadLocal(numberIterator);
		adapter.invokeVirtual(TagOutput.NUMBER_ITERATOR, TagOutput.CURRENT);
		adapter.storeLocal(currentOuter);
		
		// current
		int current=adapter.newLocal(Types.INT_VALUE);
		
		WhileVisitor wv = new WhileVisitor();
		wv.visitBeforeExpression(bc);
			
			//while(ni.isValid()) {
			adapter.loadLocal(numberIterator);
			adapter.invokeVirtual(TagOutput.NUMBER_ITERATOR, TagOutput.IS_VALID);
			
		wv.visitAfterExpressionBeforeBody(bc);
		
			// if(!query.go(ni.current()))break; 
			adapter.loadLocal(query);
			adapter.loadLocal(numberIterator);
			adapter.invokeVirtual(TagOutput.NUMBER_ITERATOR, TagOutput.CURRENT);
			
			adapter.loadLocal(pid);
			//adapter.loadArg(0);
			//adapter.invokeVirtual(Types.PAGE_CONTEXT, GET_ID);
			adapter.invokeInterface(Types.QUERY, TagLoop.GO);
			
			/* OLD
			adapter.invokeInterface(Types.QUERY, TagLoop.GO_1);
			*/
			NotVisitor.visitNot(bc);
			Label _if=new Label();
			adapter.ifZCmp(Opcodes.IFEQ, _if);
				wv.visitBreak(bc);
			adapter.visitLabel(_if);
		
			// current=ni.current();
			adapter.loadLocal(numberIterator);
			adapter.invokeVirtual(TagOutput.NUMBER_ITERATOR, TagOutput.CURRENT);
			adapter.storeLocal(current);
			
			getBody().writeOut(bc);
			
			// ni.setCurrent(current+1);
			adapter.loadLocal(numberIterator);
			adapter.loadLocal(current);
			adapter.push(1);
			adapter.visitInsn(Opcodes.IADD);
			adapter.invokeVirtual(TagOutput.NUMBER_ITERATOR, TagOutput.SET_CURRENT);
	
		wv.visitAfterBody(bc,getEnd());
	
		
		// ni.setCurrent(currentOuter);
		adapter.loadLocal(numberIterator);
		adapter.loadLocal(currentOuter);
		adapter.invokeVirtual(TagOutput.NUMBER_ITERATOR, TagOutput.SET_CURRENT);
		
		adapter.loadLocal(query);
		adapter.loadLocal(currentOuter);
		
		adapter.loadLocal(pid);
		//adapter.loadArg(0);
		//adapter.invokeVirtual(Types.PAGE_CONTEXT, GET_ID);
		adapter.invokeInterface(Types.QUERY, TagLoop.GO);
		adapter.pop();
		//adapter.pop();
	}


	private int getPID() {
		return pid;
	}
	/**
	 * write out normal query
	 * @param adapter
	 * @throws TemplateException
	 */
	private void writeOutTypeNormal(BytecodeContext bc) throws BytecodeException {
		ParseBodyVisitor pbv=new ParseBodyVisitor();
		pbv.visitBegin(bc);
			getBody().writeOut(bc);
		pbv.visitEnd(bc);
	}


	private void writeOutTypeQuery(BytecodeContext bc) throws BytecodeException {
		final GeneratorAdapter adapter = bc.getAdapter();

		numberIterator = adapter.newLocal(TagOutput.NUMBER_ITERATOR);
		ParseBodyVisitor pbv=new ParseBodyVisitor();
		pbv.visitBegin(bc);
			
		
		// Query query=pc.getQuery(@query);
		query =adapter.newLocal(Types.QUERY);
		adapter.loadArg(0);
		Expression val = getAttribute("query").getValue();
		val.writeOut(bc, Expression.MODE_REF);
		if(val instanceof LitString)
			adapter.invokeVirtual(Types.PAGE_CONTEXT, TagLoop.GET_QUERY_STRING);
		else
			adapter.invokeVirtual(Types.PAGE_CONTEXT, TagLoop.GET_QUERY_OBJ);
		
		adapter.storeLocal(query);
		
		
		pid = adapter.newLocal(Types.INT_VALUE);
		adapter.loadArg(0);
		adapter.invokeVirtual(Types.PAGE_CONTEXT, TagLoop.GET_ID);
		adapter.storeLocal(pid);
		

		
		// int startAt=query.getCurrentrow();
		final int startAt=adapter.newLocal(Types.INT_VALUE);
		adapter.loadLocal(query);
		
		adapter.loadLocal(pid);
		//adapter.loadArg(0);
		//adapter.invokeVirtual(Types.PAGE_CONTEXT, TagLoop.GET_ID);
		adapter.invokeInterface(Types.QUERY, TagLoop.GET_CURRENTROW_1);
		
		/* OLD
		adapter.invokeInterface(Types.QUERY, TagLoop.GET_CURRENTROW_0);
		*/
		adapter.storeLocal(startAt);
		
		
		
		// if(query.getRecordcount()>0) {
		DecisionIntVisitor div=new DecisionIntVisitor();
		div.visitBegin();
			adapter.loadLocal(query);
			adapter.invokeInterface(Types.QUERY, TagOutput.GET_RECORDCOUNT);
		div.visitGT();
			adapter.push(0);
		div.visitEnd(bc);
		Label ifRecCount=new Label();
		adapter.ifZCmp(Opcodes.IFEQ, ifRecCount);
			
			// startrow
			int from = adapter.newLocal(Types.DOUBLE_VALUE);
			Attribute attrStartRow = getAttribute("startrow");
			if(attrStartRow!=null){
				// NumberRange.range(@startrow,1)
				attrStartRow.getValue().writeOut(bc, Expression.MODE_VALUE);
				adapter.push(1d);
				adapter.invokeStatic(Types.NUMBER_RANGE, TagOutput.RANGE);
				//adapter.visitInsn(Opcodes.D2I);
			}
			else {
				adapter.push(1d);
			}
			adapter.storeLocal(from);
			
			// numberIterator
			
			Attribute attrMaxRow = getAttribute("maxrows");
			
			adapter.loadLocal(from);
			adapter.loadLocal(query);
			adapter.invokeInterface(Types.QUERY, TagOutput.GET_RECORDCOUNT);
			adapter.visitInsn(Opcodes.I2D);
			if(attrMaxRow!=null) {
				attrMaxRow.getValue().writeOut(bc, Expression.MODE_VALUE);
				adapter.invokeStatic(TagOutput.NUMBER_ITERATOR, TagOutput.LOAD_3);
			}
			else {
				adapter.invokeStatic(TagOutput.NUMBER_ITERATOR, TagOutput.LOAD_2);
			}
			adapter.storeLocal(numberIterator);
			
			// Group
			Attribute attrGroup = getAttribute("group");
			Attribute attrGroupCS = getAttribute("groupcasesensitive");
			group=adapter.newLocal(Types.STRING);
			final int groupCaseSensitive=adapter.newLocal(Types.BOOLEAN_VALUE);
			if(attrGroup!=null)	{
				attrGroup.getValue().writeOut(bc, Expression.MODE_REF);
				adapter.storeLocal(group);
				
				if(attrGroupCS!=null)	attrGroupCS.getValue().writeOut(bc, Expression.MODE_VALUE);
				else 					adapter.push(true);
				adapter.storeLocal(groupCaseSensitive);
			}
			
			// pc.us().addCollection(query);
			adapter.loadArg(0);
			adapter.invokeVirtual(Types.PAGE_CONTEXT, TagOutput.US);
			adapter.loadLocal(query);
			adapter.invokeInterface(TagOutput.UNDEFINED, TagOutput.ADD_COLLECTION);
			
			// current
			final int current=adapter.newLocal(Types.INT_VALUE);
			
			// Try
			TryFinallyVisitor tfv=new TryFinallyVisitor(new OnFinally() {
				public void writeOut(BytecodeContext bc) {
					// query.reset();
					
					// query.go(startAt);
					adapter.loadLocal(query);
					adapter.loadLocal(startAt);
					
					adapter.loadLocal(pid);
					//adapter.loadArg(0);
					//adapter.invokeVirtual(Types.PAGE_CONTEXT, TagLoop.GET_ID);
					adapter.invokeInterface(Types.QUERY, TagLoop.GO);
					adapter.pop();
					
					
					
					
					// pc.us().removeCollection();
					adapter.loadArg(0);
					adapter.invokeVirtual(Types.PAGE_CONTEXT, TagOutput.US);
					adapter.invokeInterface(TagOutput.UNDEFINED, TagOutput.REMOVE_COLLECTION);
					
			    	// NumberIterator.release(ni);
					adapter.loadLocal(numberIterator);
					adapter.invokeStatic(TagOutput.NUMBER_ITERATOR, TagOutput.REALEASE);
				}
			});
			tfv.visitTryBegin(bc);
				WhileVisitor wv = new WhileVisitor();
				wv.visitBeforeExpression(bc);
					
					//while(ni.isValid()) {
					adapter.loadLocal(numberIterator);
					adapter.invokeVirtual(TagOutput.NUMBER_ITERATOR, TagOutput.IS_VALID);
					
				wv.visitAfterExpressionBeforeBody(bc);
				
					// if(!query.go(ni.current()))break; 
					adapter.loadLocal(query);
					adapter.loadLocal(numberIterator);
					adapter.invokeVirtual(TagOutput.NUMBER_ITERATOR, TagOutput.CURRENT);
					
					adapter.loadLocal(pid);
					//adapter.loadArg(0);
					//adapter.invokeVirtual(Types.PAGE_CONTEXT, GET_ID);
					adapter.invokeInterface(Types.QUERY, TagLoop.GO);
					
					/* OLD
					adapter.invokeInterface(Types.QUERY, TagLoop.GO_1);
					*/
					NotVisitor.visitNot(bc);
					Label _if=new Label();
					adapter.ifZCmp(Opcodes.IFEQ, _if);
						wv.visitBreak(bc);
					adapter.visitLabel(_if);
					
					if(attrGroup!=null) {
						// NumberIterator oldNi=numberIterator;
						int oldNi=adapter.newLocal(TagOutput.NUMBER_ITERATOR);
						adapter.loadLocal(numberIterator);
						adapter.storeLocal(oldNi);
						
						// numberIterator=NumberIterator.load(ni,query,group,grp_case);
						adapter.loadArg(0);
						adapter.loadLocal(numberIterator);
						adapter.loadLocal(query);
						adapter.loadLocal(group);
						adapter.loadLocal(groupCaseSensitive);
						adapter.invokeStatic(TagOutput.NUMBER_ITERATOR, TagOutput.LOAD_5);
						adapter.storeLocal(numberIterator);
						
						// current=oldNi.current();
						adapter.loadLocal(oldNi);
						adapter.invokeVirtual(TagOutput.NUMBER_ITERATOR, TagOutput.CURRENT);
						adapter.storeLocal(current);
						
						getBody().writeOut(bc);
						
						//tmp(adapter,current);
						
						// NumberIterator.release(ni);
						adapter.loadLocal(numberIterator);
						adapter.invokeStatic(TagOutput.NUMBER_ITERATOR, TagOutput.REALEASE);
						
						// numberIterator=oldNi;
						adapter.loadLocal(oldNi);
						adapter.storeLocal(numberIterator);
					}
					else {
						// current=ni.current();
						adapter.loadLocal(numberIterator);
						adapter.invokeVirtual(TagOutput.NUMBER_ITERATOR, TagOutput.CURRENT);
						adapter.storeLocal(current);
						
						getBody().writeOut(bc);
					}

					// ni.setCurrent(current+1);
					adapter.loadLocal(numberIterator);
					adapter.loadLocal(current);
					adapter.push(1);
					adapter.visitInsn(Opcodes.IADD);
					adapter.invokeVirtual(TagOutput.NUMBER_ITERATOR, TagOutput.SET_CURRENT);
			
				wv.visitAfterBody(bc,getEnd());
			
				tfv.visitTryEnd(bc);

		adapter.visitLabel(ifRecCount);
		

		pbv.visitEnd(bc);
	}
	
	/**
	 * returns numberiterator of output
	 * @return numberiterator
	 */
	public int getNumberIterator()	{
		return numberIterator;
	}

	/**
	 * returns query of output
	 * @return query
	 */
	public int getQuery()	{
		return query;
	}
	
	/*public int getQueryImpl()	{
		return queryImpl;
	}*/
	
	/**
	 * returns query of output
	 * @return query
	 */
	public int getGroup()	{
		return group;
	}

	/**
	 * returns if output has numberiterator
	 * @return has numberiterator
	 */
	public boolean hasNumberIterator()	{
		return numberIterator!=-1;
	}

	/**
	 * returns if output has query
	 * @return has query
	 */
	public boolean hasQuery()	{
		return getAttribute("query")!=null;
	}

	/**
	 * returns if output has query
	 * @return has query
	 */
	public boolean hasGroup()	{
		return getAttribute("group")!=null;
	}


}
