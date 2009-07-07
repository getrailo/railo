package org.apache.taglibs.datetime;

import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public final class CurrentTimeTag extends TagSupport {

	public CurrentTimeTag() {
	}

	public final int doEndTag() throws JspException {
		Date date = new Date();
		try {
			super.pageContext.getOut().write("" + date.getTime());
		}
		catch(Exception e) {
			throw new JspException("IO Error: " + e.getMessage());
		}
		return 6;
	}
}