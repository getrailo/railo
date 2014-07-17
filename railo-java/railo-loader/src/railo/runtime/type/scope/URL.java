package railo.runtime.type.scope;

import java.io.UnsupportedEncodingException;

import railo.runtime.listener.ApplicationContext;


/**
 * inteface for the url scope
 */
public interface URL extends Scope {

    /**
     * @return Returns the encoding.
     */
    public abstract String getEncoding();

    /**
     * @param ac current ApplicationContext 
     * @param encoding The encoding to set.
     * @throws UnsupportedEncodingException 
     */
    public abstract void setEncoding(ApplicationContext ac,String encoding) throws UnsupportedEncodingException;

	public abstract void setScriptProtecting(ApplicationContext ac,boolean b);

	// FUTURE public abstract void reinitialize(ApplicationContext ac);
	

}