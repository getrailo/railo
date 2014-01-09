package railo.runtime.debug;

import railo.commons.lang.StringUtil;

public final class DebugTraceImpl implements DebugTrace {

	private static final long serialVersionUID = -3619310656845433643L;

	// FUTURE move the following types to interface
    public static final int TYPE_INFO=0;
    public static final int TYPE_DEBUG=1;
    public static final int TYPE_WARN=2;
    public static final int TYPE_ERROR=3;
    public static final int TYPE_FATAL=4;
    public static final int TYPE_TRACE=5;

	
	
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
	
	public static int toType(String type, int defaultValue) {
        if(type==null) return defaultValue;
        type=type.toLowerCase().trim();
        if(type.startsWith("info")) return TYPE_INFO;
        if(type.startsWith("debug")) return TYPE_DEBUG;
        if(type.startsWith("warn")) return TYPE_WARN;
        if(type.startsWith("error")) return TYPE_ERROR;
        if(type.startsWith("fatal")) return TYPE_FATAL;
        if(type.startsWith("trace")) return TYPE_TRACE;
        
        return defaultValue;
    } 
	
	public static String toType(int type, String defaultValue) {
        switch(type) {
        case TYPE_INFO:    return "INFO"; 
        case TYPE_DEBUG:   return "DEBUG"; 
        case TYPE_WARN:    return "WARN"; 
        case TYPE_ERROR:   return "ERROR"; 
        case TYPE_FATAL:   return "FATAL"; 
        case TYPE_TRACE:   return "TRACE"; 
        default:                return defaultValue;
        }
    }
	
}
