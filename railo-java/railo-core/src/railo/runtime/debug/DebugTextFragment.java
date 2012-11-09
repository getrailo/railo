package railo.runtime.debug;

public class DebugTextFragment {
	public final String text;
	public final String template;
	public final int line;
	
	public DebugTextFragment(String text, String template, int line){
		this.text=text;
		this.template=template;
		this.line=line;
	}
}
