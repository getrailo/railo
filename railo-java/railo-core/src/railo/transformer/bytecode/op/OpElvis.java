package railo.transformer.bytecode.op;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.runtime.op.Elvis;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Literal;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.expression.var.DataMember;
import railo.transformer.bytecode.expression.var.Member;
import railo.transformer.bytecode.expression.var.Variable;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.bytecode.util.ExpressionUtil;
import railo.transformer.bytecode.util.Types;
import railo.transformer.bytecode.visitor.ArrayVisitor;

public final class OpElvis extends ExpressionBase {

	

    private static final Type ELVIS=Type.getType(Elvis.class);
    public static final Method INVOKE_STR = new Method(
    		"operate",
    		Types.BOOLEAN_VALUE,
    		new Type[]{Types.PAGE_CONTEXT,Types.DOUBLE_VALUE,Types.STRING_ARRAY});
	
    public static final Method INVOKE_KEY = new Method(
    		"operate",
    		Types.BOOLEAN_VALUE,
    		new Type[]{Types.PAGE_CONTEXT,Types.DOUBLE_VALUE,Types.COLLECTION_KEY_ARRAY});
	
	private Variable left;
    private Expression right;

    /**
     *
     * @see railo.transformer.bytecode.expression.ExpressionBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter, int)
     */
    public Type _writeOut(BytecodeContext bc, int mode) throws BytecodeException {
    	if(ASMUtil.hasOnlyDataMembers(left))return _writeOutPureDataMember(bc, mode);
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	Label notNull = new Label();
    	Label end = new Label();
    	
    	GeneratorAdapter ga = bc.getAdapter();
    	
    	int l = ga.newLocal(Types.OBJECT);
    	ExpressionUtil.visitLine(bc, left.getStart());
    	left.writeOut(bc, MODE_REF);
    	ExpressionUtil.visitLine(bc, left.getEnd());
    	ga.dup();
    	ga.storeLocal(l);
    	
    	ga.visitJumpInsn(Opcodes.IFNONNULL, notNull);
    	ExpressionUtil.visitLine(bc, right.getStart());
    	right.writeOut(bc, MODE_REF);
    	ExpressionUtil.visitLine(bc, right.getEnd());
    	ga.visitJumpInsn(Opcodes.GOTO, end);
    	ga.visitLabel(notNull);
    	ga.loadLocal(l);
    	ga.visitLabel(end);
    	
    	return Types.OBJECT;
    }
    
    
    public Type _writeOutPureDataMember(BytecodeContext bc, int mode) throws BytecodeException {
    	// TODO use function isNull for this
    	GeneratorAdapter adapter = bc.getAdapter();
    	
    	Label yes = new Label();
    	Label end = new Label();
    	
    	List<Member> members = left.getMembers();
    	
    	
    	
    	// to array
    	Iterator<Member> it = members.iterator();
    	List<DataMember> list=new ArrayList<DataMember>();
    	while(it.hasNext()){
    		list.add((DataMember) it.next());
    	}
    	DataMember[] arr = list.toArray(new DataMember[members.size()]);
    	
    	ExpressionUtil.visitLine(bc, left.getStart());
    	
    // public static boolean call(PageContext pc , double scope,String[] varNames)
    	// pc
    	adapter.loadArg(0);
    	// scope
		adapter.push((double)left.getScope());
		//varNames
		
		// all literal string?
		boolean allLiteral=true;
		for(int i=0;i<arr.length;i++){
			if(!(arr[i].getName() instanceof Literal)) allLiteral=false;
		}
		
		ArrayVisitor av=new ArrayVisitor();
		if(!allLiteral) {
			// String Array
	        av.visitBegin(adapter,Types.STRING,arr.length);
	        for(int i=0;i<arr.length;i++){
				av.visitBeginItem(adapter, i);
					arr[i].getName().writeOut(bc, MODE_REF); 
				av.visitEndItem(adapter);
	        }
		}
		else {
			// Collection.Key Array
	        av.visitBegin(adapter,Types.COLLECTION_KEY,arr.length);
	        for(int i=0;i<arr.length;i++){
				av.visitBeginItem(adapter, i);
					Variable.registerKey(bc, arr[i].getName());
				av.visitEndItem(adapter);
	        }
		}
        av.visitEnd();
		
        
        // allowNull
        //adapter.push(false);
		
        
        
		//ASMConstants.NULL(adapter);
		
        // call IsDefined.invoke
    	adapter.invokeStatic(ELVIS, allLiteral?INVOKE_KEY:INVOKE_STR);
		ExpressionUtil.visitLine(bc, left.getEnd());
    	
    	
    	adapter.visitJumpInsn(Opcodes.IFEQ, yes);
    	
    	// left
    	ExpressionUtil.visitLine(bc, left.getStart());
    	left.writeOut(bc, MODE_REF);
    	ExpressionUtil.visitLine(bc, left.getEnd());
    	adapter.visitJumpInsn(Opcodes.GOTO, end);
    	
    	// right
    	ExpressionUtil.visitLine(bc, right.getStart());
    	adapter.visitLabel(yes);
    	right.writeOut(bc, MODE_REF);
    	ExpressionUtil.visitLine(bc, right.getEnd());
    	adapter.visitLabel(end);
    	
    	return Types.OBJECT;
    	
    }
    

    
    
    
    
    
    private OpElvis(Variable left, Expression right) {
        super(left.getStart(),right.getEnd());
        this.left=left;
        this.right=right;  
    }
    

    public static Expression toExpr(Variable left, Expression right) {
        return new OpElvis(left,right);
    }
}

