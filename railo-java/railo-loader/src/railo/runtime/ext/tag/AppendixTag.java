package railo.runtime.ext.tag;

import javax.servlet.jsp.tagext.Tag;

/**
 * for tag with appendix like cfx or cusomtags
 */
public interface AppendixTag extends Tag {
    /**
     * @return returns the appendix of the tag
     */
    public String getAppendix();

    /**
     * sets the appendix of the tag
     * @param appendix
     */
    public void setAppendix(String appendix);
}