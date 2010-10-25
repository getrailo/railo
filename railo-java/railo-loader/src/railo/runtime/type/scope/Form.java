package railo.runtime.type.scope;

import java.io.UnsupportedEncodingException;

import org.apache.commons.fileupload.disk.DiskFileItem;

import railo.runtime.exp.PageException;
import railo.runtime.type.Scope;

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
     */
    public abstract void setEncoding(String encoding)
            throws UnsupportedEncodingException;

    
    /**
     * FUTURE replace with other return type
     * return a file upload object 
     * @param key name of the form field
     * @return apache default file item object (File Object)
     */
    public abstract DiskFileItem getFileUpload(String key);

    /**
     * @return return the exception when initialised
     */
    public abstract PageException getInitException();

	public abstract void setScriptProtecting(boolean b);

}