package railo.runtime.type;

import railo.runtime.Component;
import railo.runtime.ComponentImpl;
import railo.runtime.exp.ApplicationException;

/**
 * 
 */
public final class ClonedComponent extends ComponentImpl {
    
    //private Component cloneMaster;
 
    /**
     * @param c component that is cloned
     * @throws ApplicationException 
     */
    public ClonedComponent(Component c, boolean deepCopy) {
    	super(c,deepCopy);
    }	
}