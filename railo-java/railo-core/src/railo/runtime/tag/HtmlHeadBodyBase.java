package railo.runtime.tag;

import railo.commons.lang.StringUtil;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.BodyTagTryCatchFinallyImpl;
import railo.runtime.op.Caster;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/**
 * base class for both cfhtmlhead and cfhtmlbody
 */
public abstract class HtmlHeadBodyBase extends BodyTagTryCatchFinallyImpl {

	private final static String REQUEST_ATTRIBUTE_PREFIX = "REQUEST_ATTRIBUTE_IDMAP_";

	/**
	 * The text to add to the 'head' area of an HTML page. Everything inside the quotation marks is placed in the 'head' section
	 */
	protected String text = null;
	protected String variable = null;

	private String action = null;
	private String id = null;

	@Override
	public void release() {
		super.release();
		text = null;
		variable = null;
		action = null;
		id = null;
	}

	public abstract String getTagName();
	public abstract void actionAppend() throws IOException, ApplicationException;
	public abstract void actionFlush() throws IOException;
	public abstract void actionRead() throws IOException, PageException;
	public abstract void actionReset() throws IOException;
	public abstract void actionWrite() throws IOException, ApplicationException;

	/**
	 * @param variable the variable to set
	 */
	public void setVariable(String variable) {
		this.variable = variable;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		if (!StringUtil.isEmpty(action, true))
			this.action = action.trim().toLowerCase();
	}


	/**
	 * set the value text
	 * The text to add to the 'head' area of an HTML page. Everything inside the quotation marks is
	 * placed in the 'head' section
	 *
	 * @param text value to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public int doStartTag() throws PageException {

		return EVAL_BODY_BUFFERED;
	}


	@Override
	public int doEndTag() throws PageException {

		if (bodyContent == null)
			processTag();

		return SKIP_BODY;
	}

	public int doAfterBody() throws PageException {

		if (bodyContent != null) {

			text = bodyContent.getString();
			processTag();
		}

		return SKIP_BODY;
	}

	protected void processTag() throws PageException {

		try {

			if (StringUtil.isEmpty(action, true) || action.equals("append")) {

				required(getTagName(), "text", text);
				if (isValid())
					actionAppend();
			}
			else if (action.equals("reset")) {

				resetIdMap();
				actionReset();
			}
			else if (action.equals("write")) {

				required(getTagName(), "text", text);
				resetIdMap();
				if (isValid())          // call isValid() to register the id if set
					actionWrite();
			}
			else if (action.equals("read")) actionRead();
			else if (action.equals("flush")) actionFlush();
			else
				throw new ApplicationException("invalid value [" + action + "] for attribute action", "values for attribute action are:append,read,reset,write");
		} catch (IOException e) {
			throw Caster.toPageException(e);
		}
	}

	/**
	 *
	 * @return - true if the id was not set or was set and was not used yet in the request. if it was not set -- register it for future calls of the tag
	 */
	protected boolean isValid() {

		if (StringUtil.isEmpty(id))
			return true;

		Map m = getIdMap();

		boolean result = !m.containsKey(id);

		if (!result)
			m.put(id, Boolean.TRUE);

		return result;
	}

	protected Map getIdMap() {

		String reqAttr = REQUEST_ATTRIBUTE_PREFIX + getTagName();

		Map result = (Map)pageContext.getRequest().getAttribute(reqAttr);

		if (result == null) {

			result = new TreeMap(String.CASE_INSENSITIVE_ORDER);
			pageContext.getRequest().setAttribute(reqAttr, result);
		}

		return result;
	}

	protected void resetIdMap() {

		String reqAttr = REQUEST_ATTRIBUTE_PREFIX + getTagName();

		pageContext.getRequest().setAttribute(reqAttr, null);
	}
}
