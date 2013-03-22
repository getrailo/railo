package railo.runtime.exp;

import railo.commons.lang.StringUtil;
import railo.runtime.PageSource;
import railo.runtime.op.Caster;
import railo.transformer.util.CFMLString;


/**
 * Template Exception Object
 */
public class TemplateException extends PageExceptionImpl {

	/**
	 * @return the line
	 */
	public int getLine() {
		return line;
	}

	/**
	 * @return the pageSource
	 */
	public PageSource getPageSource() {
		return pageSource;
	}

	private int line;
	private PageSource pageSource;

	/**
	 * constructor of the template exception
	 * @param message Exception Message
	 */
	public TemplateException(String message) {
		super(message,"template");
	}
	
	/**
	 * constructor of the template exception
	 * @param message Exception Message
	 * @param detail Detailed Exception Message
	 */
	public TemplateException(String message, String detail) {
		super(message,"template");
		setDetail(detail);
	}

	/**
	 * Constructor of the class
	 * @param cfml
	 * @param message
	 */
	public TemplateException(PageSource ps, int line, int column,String message) {
		super(message,"template");
		//print.err(line+"+"+column);
		addContext(ps,line,column,null);
		this.line=line;
		this.pageSource=ps;
	}

	/**
	 * Constructor of the class
	 * @param cfml
	 * @param message
	 */
	public TemplateException(CFMLString cfml, String message) {
		this((PageSource)cfml.getSourceFile(),cfml.getLine(),cfml.getColumn(),message);
	}
	
	/**
	 * Constructor of the class
	 * @param cfml
	 * @param message
	 * @param detail
	 */
	public TemplateException(CFMLString cfml, String message, String detail) {
		this((PageSource)cfml.getSourceFile(),cfml.getLine(),cfml.getColumn(),message);
		setDetail(detail);
	}

	/**
	 * Constructor of the class
	 * @param cfml
	 * @param e
	 */
	public TemplateException(CFMLString cfml, Throwable e) {
		this(
				cfml,
				StringUtil.isEmpty(e.getMessage())?
						(Caster.toClassName(e)):
						e.getMessage());
		setStackTrace(e.getStackTrace());
	}
}