package railo.runtime.tag;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContextImpl;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.BodyTagTryCatchFinallyImpl;
import railo.runtime.op.Caster;

/**
* Writes the text specified in the text attribute to the 'head' section of a generated HTML page. 
* 	 The cfhtmlhead tag can be useful for embedding CSS code, or placing other HTML tags such, as
* 	 META, LINK, TITLE, or BASE in an HTML page header.
*/
public final class HtmlHead extends BodyTagTryCatchFinallyImpl {

	private final static String REQUEST_ATTRIBUTE = "ATTRIBUTE_CFHTMLHEADBODY_ID_MAP";

	/**
	 * The text to add to the 'head' area of an HTML page. Everything inside the quotation marks is placed in the 'head' section
	 */
	private String text = "";
	private String variable = "cfhtmlhead";
	private String action = null;
	private String id = null;

	@Override
	public void release() {
		super.release();
		text = "";
		variable = "cfhtmlhead";
		action = null;
		id = null;
	}

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

	public void actionAppend() throws IOException, ApplicationException {

		required("htmlhead", "text", text);

		if (isValid())
			((PageContextImpl) pageContext).getRootOut().appendHTMLHead(text);
	}

	public void actionWrite() throws IOException, ApplicationException {

		required("htmlhead", "text", text);

		resetIdMap();
		if (isValid())          // call isValid() to register the id if set
			((PageContextImpl) pageContext).getRootOut().writeHTMLHead(text);
	}

	public void actionReset() throws IOException {

		resetIdMap();
		((PageContextImpl) pageContext).getRootOut().resetHTMLHead();
	}

	public void actionRead() throws PageException, IOException {
		String str = ((PageContextImpl) pageContext).getRootOut().getHTMLHead();
		pageContext.setVariable(variable, str);
	}

	public void actionFlush() throws IOException {

		((PageContextImpl) pageContext).getRootOut().flushHTMLHead();
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

	void processTag() throws PageException {

		try {
			if (StringUtil.isEmpty(action, true) || action.equals("append")) actionAppend();
			else if (action.equals("reset")) actionReset();
			else if (action.equals("write")) actionWrite();
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
	boolean isValid() {

		if (StringUtil.isEmpty(id))
			return true;

		Map m = getIdMap();

		boolean result = m.containsKey(id);

		if (!result)
			m.put(id, Boolean.TRUE);

		return result;
	}

	Map getIdMap() {

		Map result = (Map)pageContext.getRequest().getAttribute(REQUEST_ATTRIBUTE);

		if (result == null) {

			result = new TreeMap(String.CASE_INSENSITIVE_ORDER);
			pageContext.getRequest().setAttribute(REQUEST_ATTRIBUTE, result);
		}

		return result;
	}

	void resetIdMap() {

		pageContext.getRequest().setAttribute(REQUEST_ATTRIBUTE, null);
	}
}