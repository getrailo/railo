package railo.runtime.tag;

import javax.servlet.jsp.tagext.Tag;

import railo.commons.lang.StringUtil;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.TagImpl;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.Collection.Key;

/**
* Allows subtag data to be saved with the base tag. Applies only to custom tags.
*
*
*
**/
public final class Associate extends TagImpl {

	private static final Key ASSOC_ATTRS = KeyImpl.getInstance("AssocAttribs");
	private static final Key ATTRIBUTES = KeyImpl.getInstance("attributes");
	
	

	/** The name of the structure in which the base tag stores subtag data. */
	private Collection.Key datacollection=ASSOC_ATTRS;

	/** The name of the base tag. */
	private String basetag;

	/**
	* @see javax.servlet.jsp.tagext.Tag#release()
	*/
	public void release()	{
		super.release();
		datacollection=ASSOC_ATTRS;
	}

	/** set the value datacollection
	*  The name of the structure in which the base tag stores subtag data.
	* @param datacollection value to set
	**/
	public void setDatacollection(String datacollection)	{
		this.datacollection=KeyImpl.init(datacollection);
	}

	/** set the value basetag
	*  The name of the base tag.
	* @param basetag value to set
	**/
	public void setBasetag(String basetag)	{
		this.basetag=basetag;
	}


	/**
	 * @throws PageException
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	*/
	public int doStartTag() throws PageException	{

        CFTag current=getCFTag();
        CFTag parent=getParentCFTag();
        if(parent==null) throw new ApplicationException("there is no parent tag with name ["+basetag+"]");
        Struct thisTag=parent.getThis();
        
        
        
      //Struct value=Caster.toStruct(pageContext.variablesScope().get(ATTRIBUTES,null));
        Struct value=current.getAttributesScope();//Caster.toStruct(pageContext.undefinedScope().get(ATTRIBUTES,null));
        if(value==null) throw new ApplicationException("invalid context, tag is no inside a custom tag");
	    
        Object obj=thisTag.get(datacollection,null);
        
        Array array;

        if(obj==null) {
            array=new ArrayImpl(new Object[]{value});
            thisTag.set(datacollection,array);   
        }
        else if(Decision.isArray(obj) && (array=Caster.toArray(obj)).getDimension()==1) {
            array.append(value);
        }
        else {
            array=new ArrayImpl(new Object[]{obj,value});
            thisTag.set(datacollection,array);   
        }
		return SKIP_BODY; 
	}

	private CFTag getParentCFTag() {
        String pureName=basetag;
        CFTag cfTag;
        if(StringUtil.startsWithIgnoreCase(pureName,"cf_")) {
            pureName=pureName.substring(3);
        }
        
        Tag tag=this;
        int count=0;
        while((tag=tag.getParent())!=null) {
            if(tag instanceof CFTag) {
                if(count++==0)continue;
                cfTag=(CFTag)tag;
                if(cfTag.getAppendix().equalsIgnoreCase(pureName)) {
                	//print.out(cfTag.getAppendix()+"::"+pureName+"->"+count);
                    return cfTag;
                }
            }
        }
        return null;
    }
	
	private CFTag getCFTag() {
        Tag tag=this;
        while((tag=tag.getParent())!=null) {
            if(tag instanceof CFTag) {
                return (CFTag)tag;
            }
        }
        return null;
    }

    /**
	* @see javax.servlet.jsp.tagext.Tag#doEndTag()
	*/
	public int doEndTag()	{
		return EVAL_PAGE;
	}

}