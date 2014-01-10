package railo.runtime.component;

import java.io.Serializable;

import railo.runtime.exp.ExpressionException;
import railo.runtime.type.Duplicable;
import railo.runtime.type.util.ComponentUtil;

public abstract class MemberSupport implements Serializable,Member,Duplicable {
    private int access;
	//private Component component;
    //private Object value;
   
    /**
     * Constructor of the class
     * @param access
     * @param value
     */
    public MemberSupport(int access) {
        this.access=access;
        //this.component=component;
    }
    
	@Override
	public int getAccess() {
		return access;
	}
	
	/**
	 * @param access
	 */
	public void setAccess(int access) {
		this.access = access;
	}
	/**
	 * @param access the access to set
	 * @throws ExpressionException 
	 */
	public void setAccess(String access) throws ExpressionException {
		this.access = ComponentUtil.toIntAccess(access);
	}
	
}