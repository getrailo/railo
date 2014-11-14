/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
package railo.runtime.ext.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import railo.loader.engine.CFMLEngineFactory;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.util.Excepton;

/**
 * Implementation of the Tag
 */
public abstract class TagSupport implements Tag {

    /**
     * Field <code>pageContext</code>
     */
    protected PageContext pageContext;
    
    private Tag parent;
       
    /**
     * sets a Railo PageContext
     * @param pageContext
     */
    public void setPageContext(PageContext pageContext) {
        this.pageContext=pageContext;
    }
    /**
     * @see javax.servlet.jsp.tagext.Tag#setPageContext(javax.servlet.jsp.PageContext)
     */
    public void setPageContext(javax.servlet.jsp.PageContext pageContext) {
        this.pageContext=(PageContext) pageContext;
    }

    /**
     * @see javax.servlet.jsp.tagext.Tag#setParent(javax.servlet.jsp.tagext.Tag)
     */
    public void setParent(Tag parent) {
        this.parent=parent;
    }

    /**
     * @see javax.servlet.jsp.tagext.Tag#getParent()
     */
    public Tag getParent() {
        return parent;
    }

    /**
     * @see javax.servlet.jsp.tagext.Tag#doStartTag()
     */
    public int doStartTag() throws JspException {
        return SKIP_BODY;
    }

    /**
     * @see javax.servlet.jsp.tagext.Tag#doEndTag()
     */
    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }

    /**
     * @see javax.servlet.jsp.tagext.Tag#release()
     */
    public void release() {
        pageContext=null;
        parent=null;
    }    
    
    /**
     * check if value is not empty
     * @param tagName 
     * @param actionName 
     * @param attributeName 
     * @param attribute 
     * @throws PageException
     */
    public void required(String tagName, String actionName, String attributeName, Object attribute) throws PageException {
        if(attribute==null) {
            Excepton util = CFMLEngineFactory.getInstance().getExceptionUtil();
            throw util.createApplicationException("Attribute ["+attributeName+"] for tag ["+tagName+"] is required if attribute action has the value ["+actionName+"]");
        }
    }
    
    
    
}