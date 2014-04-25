package railo.runtime.debug;

public final class DebugTimerImpl implements DebugTimer {

	private static final long serialVersionUID = -4552972253450654830L;
	
	private String label;
	private long time;
	private String template;
	
	public DebugTimerImpl(String label, long time, String template) {
		this.label = label;
		this.time = time;
		this.template = template;
	}

	/**
	 * @return the label
	 */
	@Override
	public String getLabel() {
		return label;
	}

	/**
	 * @return the template
	 */
	@Override
	public String getTemplate() {
		return template;
	}

	/**
	 * @return the time
	 */
	@Override
	public long getTime() {
		return time;
	}
}
