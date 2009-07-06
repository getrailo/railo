package railo.transformer.library.tag;

import java.util.Map;

import railo.commons.collections.HashTable;


/**
 * extends the normal tag library, because Custom Tags has no restrictions by a TLD this Taglib accept everything
 */
public final class CustomTagLib extends TagLib {
    
    private String textTagLib;
    

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
        tlt.setTagClass("railo.runtime.tag.CFImportTag");
        tlt.setHandleExceptions(true);
        tlt.setBodyContent("free");
        tlt.setParseBody(false);
        tlt.setDescription("Creates a ColdFusion Custom Tag");
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
        return null;
    }
    /**
     * @see railo.transformer.library.tag.TagLib#getTags()
     */
    public Map getTags() {
        return new HashTable();
    }

    /**
     * @see railo.transformer.library.tag.TagLib#setTag(railo.transformer.library.tag.TagLibTag)
     */
    public void setTag(TagLibTag tag) {}
    

}