package railo.runtime.err;

import railo.runtime.PageSource;

/**
 * represent a Error Page
 */
public interface ErrorPage {

	public static final short TYPE_EXCEPTION=1;
	public static final short TYPE_REQUEST=2;
	public static final short TYPE_VALIDATION=4;
	
    /**
     * sets the mailto attribute
     * @param mailto
     */
    public abstract void setMailto(String mailto);

    /**
     * sets the template attribute
     * @param template
     */
    public abstract void setTemplate(PageSource template);

    /**
     * sets the exception attribute
     * @param exception
     * @deprecated use instead <code>setException(String exception);</code>
     */
    public abstract void setTypeAsString(String exception);
    
    /**
     * sets the exception attribute
     * @param exception
     */
    public abstract void setException(String exception);

    /**
     * @return Returns the mailto.
     */
    public abstract String getMailto();

    /**
     * @return Returns the template.
     */
    public abstract PageSource getTemplate();

    /**
     * @return Returns the exception type.
     * @deprecated use instead <code>getException();</code>
     */
    public abstract String getTypeAsString();

    /**
     * @return Returns the exception type.
     */
    public abstract String getException();
    
	public void setType(short type);
	
	public short getType();

}