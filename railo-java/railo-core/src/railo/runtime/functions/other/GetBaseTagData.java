/**
 * Implements the CFML Function getbasetagdata
 */
package railo.runtime.functions.other;

import javax.servlet.jsp.tagext.Tag;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.tag.CFTag;
import railo.runtime.tag.CFTagCore;
import railo.runtime.type.Struct;

public final class GetBaseTagData implements Function {
	
	private static final long serialVersionUID = -7016207088098049143L;

	public static Struct call(PageContext pc , String tagName) throws PageException {
		return call(pc,tagName,-1);
	}
	
    public static Struct call(PageContext pc , String tagName, double minLevel) throws PageException {
    	CFTag tag=getParentCFTag(pc.getCurrentTag(), tagName, (int)minLevel);
        if(tag==null) throw new ExpressionException("can't find base tag with name ["+tagName+"]");
        return tag.getVariablesScope();
	}
    
    public synchronized static CFTag getParentCFTag(Tag tag,String trgTagName, int minLevel) {
        String pureName=trgTagName;
        int level=0;
        CFTag cfTag;
        while(tag!=null) {
        	if(tag instanceof CFTag && minLevel<=(level++)) {
            	cfTag=(CFTag)tag;
                if(cfTag instanceof CFTagCore){
            	
                	CFTagCore tc=(CFTagCore) cfTag;
                	
                	if((tc.getName()+"").equalsIgnoreCase(pureName))
                		return cfTag;
                	if(StringUtil.startsWithIgnoreCase(pureName,"cf")) {
                        pureName=pureName.substring(2);
                    }
                	if((tc.getName()+"").equalsIgnoreCase(pureName))
                		return cfTag;
                }
                else if( cfTag.getAppendix().equalsIgnoreCase(pureName)) {
                    return cfTag;
                }
                else if(StringUtil.startsWithIgnoreCase(pureName,"cf_")) {
                    pureName=pureName.substring(3);
                    if(cfTag.getAppendix().equalsIgnoreCase(pureName)) 
                        return cfTag;
                }
            }
        	tag=tag.getParent();
        }
        return null;
    }
}