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

	public void inc() {
		count++;
	}

	/**
	 * @return the used
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @return the scope
	 */
	public String getScope() {
		return scope;
	}

	/**
	 * @return the template
	 */
	public String getTemplate() {
		return template;
	}

	/**
	 * @return the line
	 */
	public int getLine() {
		return line;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}
