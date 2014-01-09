package railo.runtime.cfx;

import java.util.Map;

import railo.runtime.cfx.customtag.CFXTagClass;

import com.allaire.cfx.CustomTag;

/**
 * Pool for cfx tags
 */
public interface CFXTagPool {

    /**
     * @return Returns the classes.
     */
    public abstract Map<String,CFXTagClass> getClasses();

    /**
     * return custom tag that match the name
     * @param name
     * @return matching tag
     * @throws CFXTagException
     */
    public CustomTag getCustomTag(String name) throws CFXTagException;

    public CFXTagClass getCFXTagClass(String name) throws CFXTagException;
    
    /**
     * realese custom tag
     * @param ct
     */
    public void releaseCustomTag(CustomTag ct);
    
    public void releaseTag(Object tag);

}