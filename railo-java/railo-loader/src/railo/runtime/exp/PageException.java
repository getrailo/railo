package railo.runtime.exp;

import javax.servlet.jsp.JspException;

import railo.runtime.dump.Dumpable;

/**
 * root Exception for railo runtime 
 */
public abstract class PageException extends JspException implements IPageException,Dumpable {
    
    /**
     * constructor of the class
     * @param message error message
     */
    public PageException(String message) {
        super(message);
    }

}