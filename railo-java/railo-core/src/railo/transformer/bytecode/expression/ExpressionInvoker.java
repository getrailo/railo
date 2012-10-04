package railo.transformer.bytecode.expression;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.expression.var.DataMember;
import railo.transformer.bytecode.expression.var.Member;
import railo.transformer.bytecode.expression.var.UDF;
import railo.transformer.bytecode.util.ExpressionUtil;
import railo.transformer.bytecode.util.Types;

public final class ExpressionInvoker extends ExpressionBase implements Invoker {

    // Object getCollection (Object,String)
    private final static Method GET_COLLECTION = new Method("getCollection",
			Types.OBJECT,
			new Type[]{Types.OBJECT,Types.STRING}
			);
    
    // Object get (Object,String)
    private final static Method GET = new Method("get",
			Types.OBJECT,
			new Type[]{Types.OBJECT,Types.STRING}
			);

    // Object getFunction (Object,String,Object[])
    private final static Method GET_FUNCTION = new Method("getFunction",
			Types.OBJECT,
			new Type[]{Types.OBJECT,Types.STRING,Types.OBJECT_ARRAY}
			);
    
    // Object getFunctionWithNamedValues (Object,String,Object[])
    private final static Method GET_FUNCTION_WITH_NAMED_ARGS = new Method("getFunctionWithNamedValues",
			Types.OBJECT,
			new Type[]{Types.OBJECT,Types.STRING,Types.OBJECT_ARRAY}
			);
	
    
	private Expression expr;
	private List members=new ArrayList();

	public ExpressionInvoker(Expression expr) {
		super(expr.getStart(),expr.getEnd());
		this.expr=expr;
	}

	public Type _writeOut(BytecodeContext bc, int mode)	throws BytecodeException {

    	GeneratorAdapter adapter = bc.getAdapter();
    	
		Type rtn=Types.OBJECT;
		int count=members.size();
		
		for(int i=0;i<count;i++) {
    		adapter.loadArg(0);
		}
    	
		expr.writeOut(bc, Expression.MODE_REF);
		
		for(int i=0;i<count;i++) {
			Member member=((Member)members.get(i));
    		
			// Data Member
			if(member instanceof DataMember)	{
				((DataMember)member).getName().writeOut(bc, MODE_REF);
				adapter.invokeVirtual(Types.PAGE_CONTEXT,((i+1)==count)?GET:GET_COLLECTION);
				rtn=Types.OBJECT;
			}
			
			// UDF
			else if(member instanceof UDF) {
				UDF udf=(UDF) member;
				
				udf.getName().writeOut(bc, MODE_REF);
				ExpressionUtil.writeOutExpressionArray(bc, Types.OBJECT, udf.getArguments());
				
				adapter.invokeVirtual(Types.PAGE_CONTEXT,udf.hasNamedArgs()?GET_FUNCTION_WITH_NAMED_ARGS:GET_FUNCTION);
				rtn=Types.OBJECT;
				
			}
		}
		
		return rtn;
	}

	/**
	 *
	 * @see railo.transformer.bytecode.expression.Invoker#addMember(railo.transformer.bytecode.expression.var.Member)
	 */
	public void addMember(Member member) {
		members.add(member);
	}

	/**
	 *
	 * @see railo.transformer.bytecode.expression.Invoker#getMembers()
	 */
	public List getMembers() {
		return members;
	}

}
