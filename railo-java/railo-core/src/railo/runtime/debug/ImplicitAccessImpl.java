package railo.runtime.debug;

public class ImplicitAccessImpl implements ImplicitAccess {

	private int count=1;
	private String scope;
	private String template;
	private int line;
	private String name;

	public ImplicitAccessImpl(String scope, String name, String template, int line) {
		this.scope=scope;
		this.name=name;
		this.template=template;
		this.line=line;
	}

	@Override
	public void inc() {
		count++;
	}

	/**
	 * @return the used
	 */
	@Override
	public int getCount() {
		return count;
	}

	/**
	 * @return the scope
	 */
	@Override
	public String getScope() {
		return scope;
	}

	/**
	 * @return the template
	 */
	@Override
	public String getTemplate() {
		return template;
	}

	/**
	 * @return the line
	 */
	@Override
	public int getLine() {
		return line;
	}

	/**
	 * @return the name
	 */
	@Override
	public String getName() {
		return name;
	}
}
