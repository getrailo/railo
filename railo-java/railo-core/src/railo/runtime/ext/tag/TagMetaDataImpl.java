package railo.runtime.ext.tag;

import java.util.ArrayList;
import java.util.List;

public class TagMetaDataImpl implements TagMetaData {

	private int attrMin;
	private int attrMax;
	private int attrType;
	private List attrs=new ArrayList();
	private int bodyContent;
	private String description;
	private boolean isBodyRE;
	private boolean handleException;
	private boolean hasAppendix;
	private boolean hasBody;
	


	/**
	 * Constructor of the class
	 * @param attrType TagMetaData.ATTRIBUTE_TYPE_FIX,TagMetaData.ATTRIBUTE_TYPE_DYNAMIC,TagMetaData.ATTRIBUTE_TYPE_MIXED
	 * @param attrMin minimal count of attributes needed for tag
	 * @param attrMax maximal count of attributes or -1 for infinity attributes
	 * @param bodyContent TagMetaData.BODY_CONTENT_EMPTY,TagMetaData.BODY_CONTENT_FREE,TagMetaData.BODY_CONTENT_MUST
	 * @param isBodyRE is the body of the tag parsed like inside a cfoutput
	 * @param description A description of the tag.
	 */
	public TagMetaDataImpl(int attrType, int attrMin, int attrMax, int bodyContent, boolean isBodyRE, String description,
			boolean handleException, boolean hasAppendix, boolean hasBody) {
		this.attrMax = attrMax;
		this.attrMin = attrMin;
		this.attrType = attrType;
		this.description = description;
		this.isBodyRE = isBodyRE;
		this.handleException = handleException;
		this.hasAppendix = hasAppendix;
		this.hasBody = hasBody;
	}
	
	/**
	 * @see railo.runtime.ext.tag.TagMetaData#getAttributeMax()
	 */
	public int getAttributeMax() {
		return attrMax;
	}

	/**
	 * @see railo.runtime.ext.tag.TagMetaData#getAttributeMin()
	 */
	public int getAttributeMin() {
		return attrMin;
	}

	/**
	 * @see railo.runtime.ext.tag.TagMetaData#getAttributeType()
	 */
	public int getAttributeType() {
		return attrType;
	}

	/**
	 * @see railo.runtime.ext.tag.TagMetaData#getAttributes()
	 */
	public TagMetaDataAttr[] getAttributes() {
		return (TagMetaDataAttr[]) attrs.toArray(new TagMetaDataAttr[attrs.size()]);
	}
	
	/**
	 * adds a attribute to the tag
	 * @param attr
	 */
	public void addAttribute(TagMetaDataAttr attr) {
		attrs.add(attr);
	}

	/**
	 * @see railo.runtime.ext.tag.TagMetaData#getBodyContent()
	 */
	public int getBodyContent() {
		return bodyContent;
	}

	/**
	 * @see railo.runtime.ext.tag.TagMetaData#getDescription()
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @see railo.runtime.ext.tag.TagMetaData#isBodyRuntimeExpressionValue()
	 */
	public boolean isBodyRuntimeExpressionValue() {
		return isBodyRE;
	}

	/**
	 * @see railo.runtime.ext.tag.TagMetaData#handleException()
	 */
	public boolean handleException() {
		return handleException;
	}

	/**
	 * @see railo.runtime.ext.tag.TagMetaData#hasAppendix()
	 */
	public boolean hasAppendix() {
		return hasAppendix;
	}

	/**
	 * @see railo.runtime.ext.tag.TagMetaData#hasBody()
	 */
	public boolean hasBody() {
		return hasBody;
	}

}
