package railo.runtime.interpreter.ref.literal;

import java.util.ArrayList;
import java.util.Iterator;

import railo.runtime.PageContext;
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

    /**
     * constructor of the class
     * @param str 
     */
    public LStringBuffer(String str) {
        sb.append(str);
    }
    /**
     * constructor of the class
     * @param str 
     */
    public LStringBuffer() {
    }
    

    @Override
	public Object getValue(PageContext pc) throws PageException {
        if(refs.size()==0) return sb.toString();
        
        StringBuffer tmp=new StringBuffer();
        Iterator it = refs.iterator();
        while(it.hasNext()) {
            tmp.append(Caster.toString(((Ref)it.next()).getValue(pc)));
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
    public void append(String str) {
        sb.append(str);
    }
    
    public boolean isEmpty() {
        return sb.length()+refs.size()==0;
    }

    @Override
    public String getTypeName() {
        return "literal";
    }
    
    @Override
    public String getString(PageContext pc) throws PageException {
        return (String) getValue(pc);
    }

    @Override
    public boolean eeq(PageContext pc,Ref other) throws PageException {
		return RefUtil.eeq(pc,this,other);
	}
}
