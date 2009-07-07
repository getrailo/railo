/**
 * Implements the Cold Fusion Function getbasetaglist
 */
package railo.runtime.functions.other;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.jsp.tagext.Tag;

import railo.runtime.PageContext;
import railo.runtime.config.ConfigImpl;
import railo.runtime.ext.function.Function;
import railo.runtime.ext.tag.AppendixTag;
import railo.runtime.tag.CFImportTag;
import railo.runtime.tag.CFTag;
import railo.runtime.tag.Module;
import railo.runtime.type.List;
import railo.transformer.library.tag.TagLib;
import railo.transformer.library.tag.TagLibTag;

public final class GetBaseTagList implements Function {
    public static String call(PageContext pc) {
        return call(pc,",");
    }
    public static String call(PageContext pc, String delimeter) {
        Tag tag=pc.getCurrentTag();
        StringBuffer sb=new StringBuffer();
        while(tag!=null) {
          
            if(sb.length()>0)sb.append(delimeter);
            sb.append(getName(pc,tag));
            tag=tag.getParent();
        }
        return sb.toString();
    }
    private static String getName(PageContext pc, Tag tag) {
                Class clazz = tag.getClass();
                if(clazz==CFImportTag.class)clazz=CFTag.class;
                String className=clazz.getName();
                TagLib[] tlds = ((ConfigImpl)pc.getConfig()).getTLDs();
                TagLibTag tlt;
                
                
                
                for(int i=0;i<tlds.length;i++) {
                    String ns = tlds[i].getNameSpaceAndSeparator();
                    
                    
                    Map tags = tlds[i].getTags();
                    Iterator it = tags.keySet().iterator();
                    while(it.hasNext()){
                        tlt=(TagLibTag) tags.get(it.next());
                        if(tlt.getTagClassName().equals(className)) {
                            if(tag instanceof AppendixTag) {
                                AppendixTag atag=(AppendixTag)tag;
                                if(atag.getAppendix()!=null && !(tag instanceof Module)) {
                                    return tlt.getFullName().toUpperCase()+atag.getAppendix().toUpperCase();
                                }
                            }
                            return tlt.getFullName().toUpperCase();
                        }
                    }
                }
                return List.last(className,".").toUpperCase();
        
    }
}