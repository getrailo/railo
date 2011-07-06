package railo.runtime.debug;

import railo.commons.lang.StringUtil;

public final class DebugTraceImpl implements DebugTrace {

	private static final long serialVersionUID = -3619310656845433643L;
	
	private int type;
	private String category;
	private String text;
	private String template;
	private int line;
	private String varValue;
	private long time;
	private String varName;
	private String action;

	public DebugTraceImpl(int type, String category, String text, String template, int line, String action,String varName, String varValue, long time) {
		this.type=type;
		this.category=category;
		this.text=text;
		this.template=template;
		this.line=line;
		this.varName=varName;
		this.varValue=varValue;
		this.time=(time<0)?0:time;
		this.action=StringUtil.emptyIfNull(action);
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @return the line
	 */
	public int getLine() {
		return line;
	}

	/**
	 * @return the template
	 */
	public String getTemplate() {
		return template;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @return the time
	 */
	public long getTime() {
		return time;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @return the var value
	 */
	public String getVarValue() {
		return varValue;
	}
	public String getVarName() {
		return varName;
	}
	public String getAction() {
		return action;
	}
	
}
