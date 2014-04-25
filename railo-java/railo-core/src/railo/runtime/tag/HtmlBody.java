package railo.runtime.tag;


import java.io.IOException;

import railo.runtime.PageContextImpl;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;

public final class HtmlBody extends HtmlHeadBodyBase {

	@Override
	public String getTagName() {
		return "htmlbody";
	}

	@Override
	public void actionAppend() throws IOException, ApplicationException {

		((PageContextImpl) pageContext).getRootOut().appendHTMLBody(text);
	}

	@Override
	public void actionWrite() throws IOException, ApplicationException {

		((PageContextImpl) pageContext).getRootOut().writeHTMLBody(text);
	}

	@Override
	public void actionReset() throws IOException {

		((PageContextImpl) pageContext).getRootOut().resetHTMLBody();
	}

	@Override
	public void actionRead() throws PageException, IOException {

		String str = ((PageContextImpl) pageContext).getRootOut().getHTMLBody();
		pageContext.setVariable(variable != null ? variable : "cfhtmlbody", str);
	}

	@Override
	public void actionFlush() throws IOException {

		((PageContextImpl) pageContext).getRootOut().flushHTMLBody();
	}

}
