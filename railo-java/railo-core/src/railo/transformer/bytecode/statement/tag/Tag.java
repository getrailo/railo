package railo.transformer.bytecode.statement.tag;

import java.util.Map;

import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.statement.HasBody;
import railo.transformer.library.tag.TagLibTag;

public interface Tag extends Statement,HasBody {

	/**
	 * appendix of the tag
	 * @return appendix
	 */
	public abstract String getAppendix();

	/**
	 * return all Attributes as a map 
	 * @return attributes
	 */
	public abstract Map getAttributes();

	/**
	 * returns the fullname of the tag
	 * @return fullname
	 */
	public abstract String getFullname();

	/**
	 * return the TagLibTag to this tag
	 * @return taglibtag
	 */
	public abstract TagLibTag getTagLibTag();

	/**
	 * sets the appendix of the tag
	 * @param appendix
	 */
	public abstract void setAppendix(String appendix);

	/**
	 * sets the fullanem of the tag
	 * @param fullname
	 */
	public abstract void setFullname(String fullname);

	/**
	 * sets the tagLibTag of this tag
	 * @param tagLibTag
	 */
	public abstract void setTagLibTag(TagLibTag tagLibTag);

	/**
	 * adds a attribute to the tag
	 * @param attribute
	 */
	public abstract void addAttribute(Attribute attribute);

	/**
	 * check if tag has a tag with given name
	 * @param name
	 * @return contains attribute
	 */
	public abstract boolean containsAttribute(String name);

	/**
	 * returns the body of the tag
	 * @return body of the tag
	 */
	public Body getBody();

	/**
	 * sets the body of the tag
	 * @param body
	 */
	public abstract void setBody(Body body);

	/**
	 * returns a specified attribute from the tag
	 * @param name
	 * @return
	 */
	public abstract Attribute getAttribute(String name);

	/**
	 * returns a specified attribute from the tag
	 * @param name
	 * @return
	 */
	public abstract Attribute removeAttribute(String name);

	public abstract void addMissingAttribute(String name, String type);
	
	public abstract Map getMissingAttributes();


	public abstract void setScriptBase(boolean scriptBase);
	public abstract boolean isScriptBase();

	//public abstract void setHint(String hint);
	public abstract void addMetaData(Attribute metadata);
	//public abstract String getHint();
	public abstract Map<String, Attribute> getMetaData();
}