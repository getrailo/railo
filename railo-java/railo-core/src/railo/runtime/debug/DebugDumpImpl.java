package railo.runtime.debug;

public class DebugDumpImpl implements DebugDump {

	private final String template;
	private final int line;
	private final String output;

	public DebugDumpImpl(String template, int line, String output) { 
		this.template=template;
		this.line=line;
		this.output=output;
	}

	@Override
	public int getLine() {
		return line;
	}

	@Override
	public String getTemplate() {
		return template;
	}

	@Override
	public String getOutput() {
		return output;
	}

}
