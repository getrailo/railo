package railo.runtime.interpreter.ref.literal;

import java.util.ArrayList;
import java.util.Iterator;

import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.Ref;
import railo.runtime.interpreter.ref.RefSupport;
import railo.runtime.interpreter.ref.util.RefUtil;
import railo.runtime.op.Caster;

/**
 * Literal String
 *
 */
public final class LStringBuffer extends RefSupport implements Literal {
    
    private ArrayList refs=new ArrayList();
    private StringBuffer sb=new StringBuffer();

	//private PageContext pc;

    /**
     * constructor of the class
     * @param str 
     */
    public LStringBuffer(String str) {
        sb.append(str);
        //this.pc=pc;
    }
    /**
     * constructor of the class
     * @param str 
     */
    public LStringBuffer() {
    	//this.pc=pc;
    }
    

    /**
     * @throws PageException 
     * @see railo.runtime.interpreter.ref.Ref#getValue()
     */
    public Object getValue() throws PageException {
        if(refs.size()==0) return sb.toString();
        
        StringBuffer tmp=new StringBuffer();
        Iterator it = refs.iterator();
        while(it.hasNext()) {
            tmp.append(Caster.toString(((Ref)it.next()).getValue()));
        }
        if(sb.length()>0)tmp.append(sb);
        
        
        return tmp.toString();
    } 
    
    public void append(Ref ref) {
        if(sb.length()>0) {
            refs.add(new LString(sb.toString()));
            sb=new StringBuffer();
        }
        refs.add(ref);
    }
    
    public void append(char c) {
        sb.append(c);
    }
    
    public boolean isEmpty() {
        return sb.length()+refs.size()==0;
    }

    /**
     * @see railo.runtime.interpreter.ref.Ref#getTypeName()
     */
    public String getTypeName() {
        return "literal";
    }
    
    /**
     * @throws PageException 
     * @see railo.runtime.interpreter.ref.literal.Literal#getString()
     */
    public String getString() throws PageException {
        return (String) getValue();
    }

	/**
	 * @see railo.runtime.interpreter.ref.Ref#eeq(railo.runtime.interpreter.ref.Ref)
	 */
	public boolean eeq(Ref other) throws PageException {
		return RefUtil.eeq(this,other);
	}
}
