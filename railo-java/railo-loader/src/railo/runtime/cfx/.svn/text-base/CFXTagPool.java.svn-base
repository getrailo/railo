package railo.runtime.cfx;

import java.util.Map;

import com.allaire.cfx.CustomTag;

/**
 * Pool for cfx tags
 */
public interface CFXTagPool {

    /**
     * @return Returns the classes.
     */
    public abstract Map getClasses();

    /**
     * return custom tag that match the name
     * @param name
     * @return matching tag
     * @throws CFXTagException
     */
    public abstract CustomTag getCustomTag(String name) throws CFXTagException;

    /**
     * realese custom tag
     * @param ct
     */
    public abstract void releaseCustomTag(CustomTag ct);
    

}