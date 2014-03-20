package railo.runtime.tag;


import railo.runtime.PageContextImpl;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;

import java.io.IOException;

public final class HtmlBody extends HtmlHeadBodyBase {

	public String getTagName() {
		return "htmlbody";
	}

	public void actionAppend() throws IOException, ApplicationException {

		((PageContextImpl) pageContext).getRootOut().appendHTMLBody(text);
	}

	public void actionWrite() throws IOException, ApplicationException {

		((PageContextImpl) pageContext).getRootOut().writeHTMLBody(text);
	}

	public void actionReset() throws IOException {

		((PageContextImpl) pageContext).getRootOut().resetHTMLBody();
	}

	public void actionRead() throws PageException, IOException {

		String str = ((PageContextImpl) pageContext).getRootOut().getHTMLBody();
		pageContext.setVariable(variable != null ? variable : "cfhtmlbody", str);
	}

	public void actionFlush() throws IOException {

		((PageContextImpl) pageContext).getRootOut().flushHTMLBody();
	}

}
