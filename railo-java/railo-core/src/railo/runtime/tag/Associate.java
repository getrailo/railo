package railo.runtime.tag;

import javax.servlet.jsp.tagext.Tag;

import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.TagImpl;
import railo.runtime.functions.other.GetBaseTagData;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;

/**
* Allows subtag data to be saved with the base tag. Applies only to custom tags.
*
*
*
**/
public final class Associate extends TagImpl {

	private static final Key ASSOC_ATTRS = KeyImpl.intern("AssocAttribs");
	

	/** The name of the structure in which the base tag stores subtag data. */
	private Collection.Key datacollection=ASSOC_ATTRS;

	/** The name of the base tag. */
	private String basetag;

	@Override
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


	@Override
	public int doStartTag() throws PageException	{
		
		// current
        CFTag current=getCFTag();
        Struct value;
        if(current==null || (value=current.getAttributesScope())==null) 
        	throw new ApplicationException("invalid context, tag is no inside a custom tag");
        
        // parent
        CFTag parent=GetBaseTagData.getParentCFTag(current.getParent(), basetag, -1);
        if(parent==null) throw new ApplicationException("there is no parent tag with name ["+basetag+"]");
        
        Struct thisTag=parent.getThis();
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

	/*private static CFTag getParentCFTag(Tag srcTag,String trgTagName) {
        String pureName=trgTagName;
        CFTag cfTag;
        if(StringUtil.startsWithIgnoreCase(pureName,"cf_")) {
            pureName=pureName.substring(3);
        }
        if(StringUtil.startsWithIgnoreCase(pureName,"cf")) {
            pureName=pureName.substring(2);
        }
        int count=0;
        while((srcTag=srcTag.getParent())!=null) {
        	if(srcTag instanceof CFTag) {
                if(count++==0)continue;
                cfTag=(CFTag)srcTag;
                if(cfTag instanceof CFTagCore){
                	CFTagCore tc=(CFTagCore) cfTag;
                	if(tc.getName().equalsIgnoreCase(pureName))
                		return cfTag;
                	continue;
                }
                if(cfTag.getAppendix().equalsIgnoreCase(pureName)) {
                    return cfTag;
                }
            }
        }
        return null;
    }*/
	
	private CFTag getCFTag() {
        Tag tag=this;
        while((tag=tag.getParent())!=null) {
            if(tag instanceof CFTag) {
                return (CFTag)tag;
            }
        }
        return null;
    }

    @Override
	public int doEndTag()	{
		return EVAL_PAGE;
	}

}