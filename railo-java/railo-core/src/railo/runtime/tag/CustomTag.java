package railo.runtime.tag;

import railo.runtime.ext.tag.AppendixTag;
import railo.runtime.ext.tag.BodyTagTryCatchFinallyImpl;
import railo.runtime.ext.tag.DynamicAttributes;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Scope;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.scope.Caller;

public abstract class CustomTag extends BodyTagTryCatchFinallyImpl implements DynamicAttributes,AppendixTag {
	

	protected static final Collection.Key ON_ERROR = KeyImpl.getInstance("onError");
	protected static final Collection.Key ON_FINALLY = KeyImpl.getInstance("onFinally");
	protected static final Collection.Key ON_START_TAG = KeyImpl.getInstance("onStartTag");
	protected static final Collection.Key ON_END_TAG = KeyImpl.getInstance("onEndTag");
	protected static final Collection.Key INIT = KeyImpl.getInstance("init");
	protected static final Collection.Key GENERATED_CONTENT=KeyImpl.getInstance("GENERATEDCONTENT");

	protected static final Collection.Key EXECUTION_MODE=KeyImpl.getInstance("EXECUTIONMODE");      
	protected static final Collection.Key EXECUTE_BODY=KeyImpl.getInstance("EXECUTEBODY");
	protected static final Collection.Key HAS_END_TAG=KeyImpl.getInstance("HASENDTAG");
	

	protected static final Collection.Key ATTRIBUTES=KeyImpl.getInstance("ATTRIBUTES");
	protected static final Collection.Key CALLER=KeyImpl.getInstance("CALLER");
	protected static final Collection.Key THIS_TAG=KeyImpl.getInstance("THISTAG");
	

    protected StructImpl attributesScope;
    protected Caller callerScope;
	
    /**
    * @see javax.servlet.jsp.tagext.BodyTag#doInitBody()
    */
    public void doInitBody()    {}
    
    

    /**
     * @see railo.runtime.ext.tag.DynamicAttributes#setDynamicAttribute(java.lang.String, java.lang.String, java.lang.Object)
     */
    public final void setDynamicAttribute(String uri, String name, Object value) {
    	TagUtil.setDynamicAttribute(attributesScope,name,value);
    }
    
    /**
     * @return return thistag scope
     */
    public abstract Struct getThisTagScope();
    
    /**
     * @return return the caller scope
     */
    public abstract Struct getCallerScope();
    
    /**
     * @return return attributes scope
     */
    public abstract Struct getAttributesScope();

	/**
	 * @return the variables scope
	 */
	public abstract Scope getVariablesScope();

}
