package railo.transformer.bytecode.literal;

import org.objectweb.asm.Type;

import railo.runtime.op.Caster;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Literal;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.expression.ExprString;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.op.OpString;
import railo.transformer.bytecode.util.Types;

/**
 * A Literal String
 */
public class LitString extends ExpressionBase implements Literal,ExprString {
    
	public static final int MAX_SIZE = 65535;
	public static final int TYPE_ORIGINAL = 0;
	public static final int TYPE_UPPER = 1;
	public static final int TYPE_LOWER = 2;
	public static final LitString EMPTY = new LitString("",null,null);
	private String str;
	private boolean fromBracket;


	public static ExprString toExprString(String str, Position start,Position end) {
		return new LitString(str,start,end);
	}

	public static ExprString toExprString(String str) {
		return new LitString(str,null,null);
	}

	public static LitString toLitString(String str) {
		return new LitString(str,null,null);
	}

    /**
     * constructor of the class
     * @param str
     * @param line 
     */
	public LitString(String str, Position start,Position end) {
        super(start,end);
        this.str=str;
    }
    
    /**
     * @see railo.transformer.bytecode.Literal#getString()
     */
    public String getString() {
        return str;
    }

    /**
     * @see railo.transformer.bytecode.expression.Expression#_writeOut(org.objectweb.asm.commons.GeneratorAdapter, int)
     */
    private static  Type _writeOut(BytecodeContext bc, int mode,String str) throws BytecodeException {
        if(str.length()>MAX_SIZE) {
        	ExprString expr=_toExpr(str);
        	expr.writeOut(bc, mode);
        }
        else {
        	bc.getAdapter().push(str);
        }
        return Types.STRING;
    }
    public Type _writeOut(BytecodeContext bc, int mode) throws BytecodeException {
        return _writeOut(bc, mode, str);
    }
    
    public Type writeOut(BytecodeContext bc, int mode, int caseType) throws BytecodeException {
    	if(TYPE_UPPER==caseType)	return _writeOut(bc, mode, str.toUpperCase());
    	if(TYPE_LOWER==caseType)	return _writeOut(bc, mode, str.toLowerCase());
        return _writeOut(bc, mode, str);
    }

    private static ExprString _toExpr(String str) {
    	int size=MAX_SIZE-1;
    	ExprString left = LitString.toExprString(str.substring(0,size));
    	str = str.substring(size);
    	
    	ExprString right = (str.length()>size)?_toExpr(str):toExprString(str);

    	return OpString.toExprString(left, right, false);
	}


    /**
     * @see railo.transformer.bytecode.Literal#getDouble(java.lang.Double)
     */
    public Double getDouble(Double defaultValue) {
        return Caster.toDouble(getString(),defaultValue);
    }

    /**
     * @see railo.transformer.bytecode.Literal#getBoolean(java.lang.Boolean)
     */
    public Boolean getBoolean(Boolean defaultValue) {
        return Caster.toBoolean(getString(),defaultValue);
    }
    

    /**
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(this==obj) return true;
		if(!(obj instanceof LitString)) return false;
		return str.equals(((LitString)obj).str);
	}

	/**
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return str;
	}

	public void upperCase() {
		str=str.toUpperCase(); 
	}
	public void lowerCase() {
		str=str.toLowerCase();
	}

	public LitString duplicate() {
		return new LitString(str,getStart(),getEnd());
	}

	public void fromBracket(boolean fromBracket) {
		this.fromBracket=fromBracket;
	}
	public boolean fromBracket() {
		return fromBracket;
	}


    /* *
     * @see railo.transformer.bytecode.expression.Expression#getType()
     * /
    public int getType() {
        return Types._STRING;
    }*/
}
