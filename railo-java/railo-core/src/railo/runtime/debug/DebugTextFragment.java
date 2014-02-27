package railo.runtime.debug;

import railo.commons.io.SystemUtil.TemplateLine;

public class DebugTextFragment {
	public final String text;
	public final String template;
	public final int line;
	
	public DebugTextFragment(String text, String template, int line){
		this.text=text;
		this.template=template;
		this.line=line;
	}

	public DebugTextFragment(String text, TemplateLine tl) {
		this.text=text;
		this.template=tl.template;
		this.line=tl.line;
	}
}
