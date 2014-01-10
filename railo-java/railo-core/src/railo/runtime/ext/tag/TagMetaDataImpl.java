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
	 * @param attrMax maximum count of attributes or -1 for infinity attributes
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
	
	@Override
	public int getAttributeMax() {
		return attrMax;
	}

	@Override
	public int getAttributeMin() {
		return attrMin;
	}

	@Override
	public int getAttributeType() {
		return attrType;
	}

	@Override
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

	@Override
	public int getBodyContent() {
		return bodyContent;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public boolean isBodyRuntimeExpressionValue() {
		return isBodyRE;
	}

	@Override
	public boolean handleException() {
		return handleException;
	}

	@Override
	public boolean hasAppendix() {
		return hasAppendix;
	}

	@Override
	public boolean hasBody() {
		return hasBody;
	}

}
