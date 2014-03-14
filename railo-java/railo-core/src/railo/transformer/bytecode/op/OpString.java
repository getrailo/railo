package railo.transformer.bytecode.op;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

import railo.transformer.TransformerException;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.util.Types;
import railo.transformer.expression.ExprString;
import railo.transformer.expression.Expression;
import railo.transformer.expression.literal.Literal;

public final class OpString extends ExpressionBase implements ExprString {
    
    private ExprString right;
    private ExprString left;

    // String concat (String)
    private final static Method METHOD_CONCAT = new Method("concat",
			Types.STRING,
			new Type[]{Types.STRING});
	private static final int MAX_SIZE = 65535;
    
    private OpString(Expression left, Expression right) {
        super(left.getFactory(),left.getStart(),right.getEnd());
        this.left=left.getFactory().toExprString(left);
        this.right=left.getFactory().toExprString(right);
    }

    public static ExprString toExprString(Expression left, Expression right, boolean concatStatic) {
        if(concatStatic && left instanceof Literal && right instanceof Literal) {
            String l = ((Literal)left).getString();
        	String r = ((Literal)right).getString();
        	if((l.length()+r.length())<=MAX_SIZE)return left.getFactory().createLitString(l.concat(r),left.getStart(),right.getEnd());
        }
        return new OpString(left,right);
    }

    @Override
    public Type _writeOut(BytecodeContext bc, int mode) throws TransformerException {
        left.writeOut(bc,MODE_REF);
        right.writeOut(bc,MODE_REF);
        bc.getAdapter().invokeVirtual(Types.STRING,METHOD_CONCAT);
        return Types.STRING;
    }
}
