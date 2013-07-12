package railo.transformer.library.tag;

import java.io.File;
import java.util.Map;

import railo.commons.collection.MapFactory;
import railo.runtime.tag.CFImportTag;


/**
 * extends the normal tag library, because Custom Tags has no restrictions by a TLD this Taglib accept everything
 */
public final class CustomTagLib extends TagLib {
    
    private String textTagLib;
	private TagLib[] taglibs;
    

    /**
     * constructor of the class
     * @param textTagLib
     * @param nameSpace the namespace definition
     * @param nameSpaceSeperator the seperator beetween namespace and name of the tag
     */
    public CustomTagLib(String textTagLib, String nameSpace,String nameSpaceSeperator) {
    	super(false);
        this.textTagLib = textTagLib;
        setNameSpace(nameSpace);
        setNameSpaceSeperator(nameSpaceSeperator);

    }

    /**
     * @see railo.transformer.library.tag.TagLib#getAppendixTag(java.lang.String)
     */
    public TagLibTag getAppendixTag(String name) {

        TagLibTag tlt = new TagLibTag(this);
        tlt.setName("");
        tlt.setAppendix(true);
        tlt.setTagClass(CFImportTag.class.getName());
        tlt.setHandleExceptions(true);
        tlt.setBodyContent("free");
        tlt.setParseBody(false);
        tlt.setDescription("Creates a CFML Custom Tag");
        tlt.setAttributeType(TagLibTag.ATTRIBUTE_TYPE_DYNAMIC);

        TagLibTagAttr tlta=new TagLibTagAttr(tlt);
        tlta.setName("__custom_tag_path");
        tlta.setRequired(true);
        tlta.setRtexpr(true);
        tlta.setType("string");
        tlta.setHidden(true);
        tlta.setDefaultValue(textTagLib);
        
        tlt.setAttribute(tlta);
        setTag(tlt);
        
        return tlt;
    }

    /**
     * @see railo.transformer.library.tag.TagLib#getTag(java.lang.String)
     */
    public TagLibTag getTag(String name) {
    	if(taglibs!=null){
    		TagLibTag tag=null;
    		for(int i=0;i<taglibs.length;i++){
    			if((tag=taglibs[i].getTag(name))!=null) return tag;
    		}
    	}
        return null;
    }
    /**
     * @see railo.transformer.library.tag.TagLib#getTags()
     */
    public Map getTags() {
        return MapFactory.<String,String>getConcurrentMap();
    }

    /**
     * @see railo.transformer.library.tag.TagLib#setTag(railo.transformer.library.tag.TagLibTag)
     */
    public void setTag(TagLibTag tag) {}

	public void append(TagLib other) {
		if(other instanceof CustomTagLib)
			textTagLib+=File.pathSeparatorChar+((CustomTagLib)other).textTagLib;
		else{
			if(taglibs==null){
				taglibs=new TagLib[]{other};
			}
			else {
				TagLib[] tmp = new TagLib[taglibs.length+1];
				for(int i=0;i<taglibs.length;i++){
					tmp[i]=taglibs[i];
				}
				tmp[taglibs.length]=other;
			}
			
		}
	}
    

}