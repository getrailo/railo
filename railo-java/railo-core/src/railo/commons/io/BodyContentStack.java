package railo.commons.io;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;

import railo.runtime.writer.BodyContentImpl;
import railo.runtime.writer.CFMLWriter;
import railo.runtime.writer.DevNullBodyContent;

/**
 * Stack for the Body Content Objects
 */
public final class BodyContentStack {

    private CFMLWriter base;
    
    private final DevNullBodyContent nirvana=new DevNullBodyContent();
    private Entry current;
    private final Entry root;
    
    
    /**
     * Default Constructor
     */
    public BodyContentStack() {
        current=new Entry(null,null);
        root=current;
    }

    /**
     * initialize the BodyContentStack
     * @param rsp
     */
    public void init(CFMLWriter writer) {
    	this.base=writer;
    }

    /**
     * release the BodyContentStack
     */
    public void release() {
        this.base=null;
        current=root;
        current.body=null;
        current.after=null;
        current.before=null;
    }
    

    /**
     * push a new BodyContent to Stack
     * @return new BodyContent
     */
    public BodyContent push() {
        if(current.after==null) {
            current.after=new Entry(current,new BodyContentImpl(current.body==null?(JspWriter)base:current.body));
        }
        else {
            current.after.doDevNull=false;
            current.after.body.init(current.body==null?(JspWriter)base:current.body);
        }
        current=current.after;
        return current.body;
    }
    
    /**
     * pop a BodyContent from Stack
     * @return BodyContent poped
     */
    public JspWriter pop() {
        if(current.before!=null) current=current.before;
        return getWriter();
    }
    
    /**
     * set if actuell BodyContent is DevNull or not
     * @param doDevNull
     */
    public void setDevNull(boolean doDevNull) {
        current.doDevNull=doDevNull;
    }
    
    /**
     * @return returns actuell writer
     */
    public JspWriter getWriter() {
        if(!current.doDevNull) {
            if(current.body!=null) return current.body;
            return base;
        }
        return nirvana;
    }
    
    
    class Entry {
        private Entry before;
        private Entry after;
        private boolean doDevNull=false;
        private BodyContentImpl body;
        private Entry(Entry before, BodyContentImpl body) {
            this.before=before;
            this.body=body;
        }
        
    }

    /**
     * @return returns DevNull Object
     */
    public boolean getDevNull() {
        return current.doDevNull;
    }

    /**
     * @return returns DevNull Object
     */
    public DevNullBodyContent getDevNullBodyContent() {
        return nirvana;
    }

    /**
     * @return Returns the base.
     */
    public CFMLWriter getBase() {
        return base;
    }

}