package railo.runtime.tag;


import railo.runtime.PageContextImpl;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;

import java.io.IOException;

/**
* Writes the text specified in the text attribute to the 'head' section of a generated HTML page. 
* 	 The cfhtmlhead tag can be useful for embedding CSS code, or placing other HTML tags such, as
* 	 META, LINK, TITLE, or BASE in an HTML page header.
*/
public final class HtmlHead extends HtmlHeadBodyBase {

	public String getTagName() {
		return "htmlhead";
	}

	public void actionAppend() throws IOException, ApplicationException {

		((PageContextImpl) pageContext).getRootOut().appendHTMLHead(text);
	}

	public void actionWrite() throws IOException, ApplicationException {

		((PageContextImpl) pageContext).getRootOut().writeHTMLHead(text);
	}

	public void actionReset() throws IOException {

		((PageContextImpl) pageContext).getRootOut().resetHTMLHead();
	}

	public void actionRead() throws PageException, IOException {

		String str = ((PageContextImpl) pageContext).getRootOut().getHTMLHead();
		pageContext.setVariable(variable != null ? variable : "cfhtmlhead", str);
	}

	public void actionFlush() throws IOException {

		((PageContextImpl) pageContext).getRootOut().flushHTMLHead();
	}

}