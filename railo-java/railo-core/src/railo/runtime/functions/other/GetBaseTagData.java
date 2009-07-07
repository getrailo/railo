/**
 * Implements the Cold Fusion Function getbasetagdata
 */
package railo.runtime.functions.other;

import javax.servlet.jsp.tagext.Tag;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.tag.CFTag;
import railo.runtime.type.Struct;

public final class GetBaseTagData implements Function {
	
    public static Struct call(PageContext pc , String tagName) throws PageException {
		return call(pc,tagName,-1);
	}
	
    public static Struct call(PageContext pc , String tagName, double minLevel) throws PageException {
        CFTag tag=getCFTag(pc, tagName, minLevel);
        if(tag==null) throw new ExpressionException("can't find base tag with name ["+tagName+"]");
        return tag.getVariablesScope();
	}
    

    private synchronized static CFTag getCFTag(PageContext pc , String tagName, double minLevel) {
        
        String pureName=tagName;
        int level=0;
        CFTag cfTag;
        if(StringUtil.startsWithIgnoreCase(pureName,"cf_")) {
            pureName=pureName.substring(3);
        }
        Tag tag=pc.getCurrentTag();
        //print.ln("tag:"+tag);
        while(tag!=null) {
            if(tag instanceof CFTag && minLevel<++level) {
                cfTag=(CFTag)tag;
                if(cfTag.getAppendix().equalsIgnoreCase(pureName)) {
                    return cfTag;
                }
            }
            tag=tag.getParent();
        }
        return null;
    }
}