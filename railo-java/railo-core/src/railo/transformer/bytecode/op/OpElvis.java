package railo.transformer.bytecode.op;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.print;
import railo.runtime.PageContext;
import railo.runtime.functions.decision.IsDefined;
import railo.runtime.interpreter.VariableInterpreter;
import railo.runtime.type.scope.Scope;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.cast.CastBoolean;
import railo.transformer.bytecode.expression.ExprBoolean;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.expression.var.DataMember;
import railo.transformer.bytecode.expression.var.Member;
import railo.transformer.bytecode.expression.var.Variable;
import railo.transformer.bytecode.literal.Identifier;
import railo.transformer.bytecode.literal.LitBoolean;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.util.ASMConstants;
import railo.transformer.bytecode.util.ExpressionUtil;
import railo.transformer.bytecode.util.Types;
import railo.transformer.bytecode.visitor.ArrayVisitor;

public final class OpElvis extends ExpressionBase {

	

    private static final Type IS_DEFINED=Type.getType(IsDefined.class);
	public static final Method CALL = new Method(
    		"call",
    		Types.BOOLEAN_VALUE,
    		new Type[]{Types.PAGE_CONTEXT,Types.DOUBLE_VALUE,Types.STRING_ARRAY,Types.BOOLEAN_VALUE});
	
	private Variable left;
    private Expression right;

    /**
     *
     * @see railo.transformer.bytecode.expression.ExpressionBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter, int)
     */
    public Type _writeOut(BytecodeContext bc, int mode) throws BytecodeException {
    	GeneratorAdapter adapter = bc.getAdapter();
    	
    	Label yes = new Label();
    	Label end = new Label();
    	
    	List<Member> members = left.getMembers();
    	Iterator<Member> it = members.iterator();
    	
    	// to array
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
		
		ArrayVisitor av=new ArrayVisitor();
        av.visitBegin(adapter,Types.STRING,arr.length);
        for(int i=0;i<arr.length;i++){
			av.visitBeginItem(adapter, i);
				arr[i].getName().writeOut(bc, MODE_REF); 
			av.visitEndItem(adapter);
        }
        
        // allowNull
        adapter.push(false);
		
        av.visitEnd();
        
		//ASMConstants.NULL(adapter);
		
        // call IsDefined.call
    	adapter.invokeStatic(IS_DEFINED, CALL);
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

