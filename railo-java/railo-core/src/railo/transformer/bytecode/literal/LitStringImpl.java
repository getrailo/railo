package railo.transformer.bytecode.literal;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.commons.lang.StringUtil;
import railo.runtime.config.ConfigImpl;
import railo.runtime.op.Caster;
import railo.transformer.Factory;
import railo.transformer.Position;
import railo.transformer.TransformerException;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.Page;
import railo.transformer.bytecode.Range;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.op.OpString;
import railo.transformer.bytecode.util.Types;
import railo.transformer.expression.ExprString;
import railo.transformer.expression.literal.LitString;

/**
 * A Literal String
 */
public class LitStringImpl extends ExpressionBase implements LitString,ExprString {
    
	public static final int MAX_SIZE = 65535;
	public static final int TYPE_ORIGINAL = 0;
	public static final int TYPE_UPPER = 1;
	public static final int TYPE_LOWER = 2;
	 
	private String str;
	private boolean fromBracket;

	/*public static ExprString toExprString(String str, Position start,Position end) {
		return new LitStringImpl(str,start,end);
	}

	public static ExprString toExprString(String str) {
		return new LitStringImpl(str,null,null);
	}

	public static LitString toLitString(String str) {
		return new LitStringImpl(str,null,null);
	}*/

    /**
     * constructor of the class
     * @param str
     * @param line 
     */
	public LitStringImpl(Factory f, String str, Position start,Position end) {
        super(f,start,end);
        this.str=str;
    }
    
	@Override
    public String getString() {
        return str;
    }

    /**
     * @see railo.transformer.expression.Expression#_writeOut(org.objectweb.asm.commons.GeneratorAdapter, int)
     */
    private static  Type _writeOut(BytecodeContext bc, int mode,String str) throws TransformerException {
        // write to a file instead to the bytecode
    	// str(0,10);
    	//print.ds(str);
    	int externalizeStringGTE=((ConfigImpl)bc.getPageSource().getMapping().getConfig()).getExternalizeStringGTE();
    	
    	if(externalizeStringGTE>-1 && str.length()>externalizeStringGTE && StringUtil.indexOfIgnoreCase(bc.getMethod().getName(),"call")!=-1) {
    		try{
	    		GeneratorAdapter ga = bc.getAdapter();
	    		Page page = bc.getPage();
	    		Range range= page.registerString(bc,str);
	    		ga.visitVarInsn(Opcodes.ALOAD, 0);
	    		ga.visitVarInsn(Opcodes.ALOAD, 1);
	    		ga.push(range.from);
	    		ga.push(range.to);
	    		ga.visitMethodInsn(Opcodes.INVOKEVIRTUAL, bc.getClassName(), "str", "(Lrailo/runtime/PageContext;II)Ljava/lang/String;");
	    		return Types.STRING;
    		}
    		catch(Throwable t){}
    	}
    	
    	
    	if(str.length()>MAX_SIZE) {
        	ExprString expr=_toExpr(bc.getFactory(),str);
        	expr.writeOut(bc, mode);
        }
        else {
        	bc.getAdapter().push(str);
        }
        return Types.STRING;
    }
    public Type _writeOut(BytecodeContext bc, int mode) throws TransformerException {
        return _writeOut(bc, mode, str);
    }
    
    public Type writeOut(BytecodeContext bc, int mode, int caseType) throws TransformerException {
    	if(TYPE_UPPER==caseType)	return _writeOut(bc, mode, str.toUpperCase());
    	if(TYPE_LOWER==caseType)	return _writeOut(bc, mode, str.toLowerCase());
        return _writeOut(bc, mode, str);
    }

    private static ExprString _toExpr(Factory factory,String str) {
    	int size=MAX_SIZE-1;
    	ExprString left = factory.createLitString(str.substring(0,size));
    	str = str.substring(size);
    	
    	ExprString right = (str.length()>size)?_toExpr(factory,str):factory.createLitString(str);

    	return OpString.toExprString(left, right, false);
	}

    @Override
    public Double getDouble(Double defaultValue) {
        return Caster.toDouble(getString(),defaultValue);
    }

    @Override
    public Boolean getBoolean(Boolean defaultValue) {
        return Caster.toBoolean(getString(),defaultValue);
    }

	@Override
	public boolean equals(Object obj) {
		if(this==obj) return true;
		if(!(obj instanceof LitString)) return false;
		
		return str.equals(((LitStringImpl)obj).getString());
	}

	@Override
	public String toString() {
		return str;
	}

	@Override
	public void upperCase() {
		str=str.toUpperCase(); 
	}
	public void lowerCase() {
		str=str.toLowerCase();
	}

	@Override
	public LitString duplicate() {
		return new LitStringImpl(getFactory(),str,getStart(),getEnd());
	}

	public void fromBracket(boolean fromBracket) {
		this.fromBracket=fromBracket;
	}
	public boolean fromBracket() {
		return fromBracket;
	}
}
