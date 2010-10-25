package railo.runtime.err;

import railo.runtime.PageSource;

/**
 * represent a Error Page
 */
public interface ErrorPage {

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
     */
    public abstract void setTypeAsString(String exception);

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
     */
    public abstract String getTypeAsString();

}