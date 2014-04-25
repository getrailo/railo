
package railo.transformer.library.tag;

import java.util.Map;

import railo.transformer.cfml.ExprTransformer;

/**
 * 
 */
public final class ImportTagLib extends TagLib {
    
    private String taglib;
    private String prefix;
    
    public ImportTagLib(String taglib,String prefix) {
    	super(false);
        this.taglib=taglib;
        this.prefix=prefix;
    }
    

    /**
     * @see railo.transformer.library.tag.TagLib#getAppendixTag(java.lang.String)
     */
    @Override
	public TagLibTag getAppendixTag(String name) {
        return super.getAppendixTag(name);
    }
    /**
     * @see railo.transformer.library.tag.TagLib#getELClass()
     */
    @Override
	public String getELClass() {
        return super.getELClass();
    }
    /**
     * @see railo.transformer.library.tag.TagLib#getExprTransfomer()
     */
    @Override
	public ExprTransformer getExprTransfomer() throws TagLibException {
        return super.getExprTransfomer();
    }
    /**
     * @see railo.transformer.library.tag.TagLib#getNameSpace()
     */
    @Override
	public String getNameSpace() {
        return super.getNameSpace();
    }
    /**
     * @see railo.transformer.library.tag.TagLib#getNameSpaceAndSeparator()
     */
    @Override
	public String getNameSpaceAndSeparator() {
        return super.getNameSpaceAndSeparator();
    }
    /**
     * @see railo.transformer.library.tag.TagLib#getNameSpaceSeparator()
     */
    @Override
	public String getNameSpaceSeparator() {
        return super.getNameSpaceSeparator();
    }
    /**
     * @see railo.transformer.library.tag.TagLib#getTag(java.lang.String)
     */
    @Override
	public TagLibTag getTag(String name) {
        return super.getTag(name);
    }
    /**
     * @see railo.transformer.library.tag.TagLib#getTags()
     */
    @Override
	public Map getTags() {
        return super.getTags();
    }
    /**
     * @see railo.transformer.library.tag.TagLib#setELClass(java.lang.String)
     */
    @Override
	protected void setELClass(String eLClass) {
        super.setELClass(eLClass);
    }
    /**
     * @see railo.transformer.library.tag.TagLib#setNameSpace(java.lang.String)
     */
    @Override
	public void setNameSpace(String nameSpace) {
        super.setNameSpace(nameSpace);
    }
    /**
     * @see railo.transformer.library.tag.TagLib#setNameSpaceSeperator(java.lang.String)
     */
    @Override
	public void setNameSpaceSeperator(String nameSpaceSeperator) {
        super.setNameSpaceSeperator(nameSpaceSeperator);
    }
    /**
     * @see railo.transformer.library.tag.TagLib#setTag(railo.transformer.library.tag.TagLibTag)
     */
    @Override
	public void setTag(TagLibTag tag) {
        super.setTag(tag);
    }
}