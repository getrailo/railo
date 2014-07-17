package railo.runtime.type.scope;

import java.io.UnsupportedEncodingException;

import javax.servlet.ServletInputStream;

import railo.runtime.exp.PageException;
import railo.runtime.listener.ApplicationContext;

/**
 * interface fro scope form
 */
public interface Form extends Scope {

    /**
     * @return Returns the encoding.
     */
    public abstract String getEncoding();

    /**
     * @param encoding The encoding to set.
     * @throws UnsupportedEncodingException 
     * @deprecated use instead <code>setEncoding(ApplicationContext ac,String encoding)</code>
     */
    //public abstract void setEncoding(String encoding) throws UnsupportedEncodingException;

    
    /**
     * @param ac current ApplicationContext 
     * @param encoding The encoding to set.
     * @throws UnsupportedEncodingException 
     */
    public abstract void setEncoding(ApplicationContext ac,String encoding) throws UnsupportedEncodingException;


    /**
     * @return return the exception when initialised
     */
    public abstract PageException getInitException();

	public abstract void setScriptProtecting(ApplicationContext ac,boolean b);

	public FormItem getUploadResource(String key);
	
	public FormItem[] getFileItems();
	
	public ServletInputStream getInputStream();
	
	// FUTURE public void reinitialize(ApplicationContext ac);
}