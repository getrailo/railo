package railo.runtime.listener;

import java.io.PrintStream;
import java.io.PrintWriter;

import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.config.Config;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.engine.ThreadLocalConfig;
import railo.runtime.err.ErrorPage;
import railo.runtime.exp.CatchBlock;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageExceptionImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;

public final class ModernAppListenerException extends PageException {

	private static final Collection.Key ROOT_CAUSE = KeyImpl.intern("rootCause");
	private static final Collection.Key CAUSE = KeyImpl.intern("cause");
	private PageException rootCause;
	private String eventName;

	/**
	 * Constructor of the class
	 * @param pe
	 * @param eventName 
	 */
	public ModernAppListenerException(PageException pe, String eventName) {
        super(pe.getMessage());
        setStackTrace(pe.getStackTrace());
		this.rootCause=pe;
		this.eventName=eventName;
	}

	/**
	 *
	 * @see railo.runtime.exp.IPageException#addContext(railo.runtime.PageSource, int, int)
	 */
	public void addContext(PageSource pageSource, int line, int column, StackTraceElement ste) {
		rootCause.addContext(pageSource, line, column,ste);
	}

	/**
	 *
	 * @see railo.runtime.exp.IPageException#getAdditional()
	 */
	public Struct getAdditional() {
		return rootCause.getAddional();
	}
	public Struct getAddional() {
		return rootCause.getAddional();
	}

	/**
	 * @see railo.runtime.exp.IPageException#getCatchBlock()
	 */
	public Struct getCatchBlock() {
		return getCatchBlock(ThreadLocalConfig.get());
	}
	

	public Struct getCatchBlock(PageContext pc) {
		return getCatchBlock(pc.getConfig());
	}
	
	/**
	 * @see railo.runtime.exp.IPageException#getCatchBlock(railo.runtime.PageContext)
	 */
	public CatchBlock getCatchBlock(Config config) {
		CatchBlock cb=rootCause.getCatchBlock(config);
		Collection cause = cb.duplicate(false);
		//rtn.setEL("message", getMessage());
		if(!cb.containsKey(KeyImpl.DETAIL))cb.setEL(KeyImpl.DETAIL, "Exception throwed while invoking function ["+eventName+"] from Application.cfc");
		cb.setEL(ROOT_CAUSE, cause);
		cb.setEL(CAUSE, cause);
		//cb.setEL("stacktrace", getStackTraceAsString());
		//rtn.setEL("tagcontext", new ArrayImpl());
		//rtn.setEL("type", getTypeAsString());
		cb.setEL(KeyImpl.NAME, eventName);
		return cb;
	}

	/**
	 *
	 * @see railo.runtime.exp.IPageException#getCustomTypeAsString()
	 */
	public String getCustomTypeAsString() {
		return rootCause.getCustomTypeAsString();
	}

	/**
	 *
	 * @see railo.runtime.exp.IPageException#getDetail()
	 */
	public String getDetail() {
		return rootCause.getDetail();
	}

	/**
	 *
	 * @see railo.runtime.exp.IPageException#getErrorBlock(railo.runtime.PageContext, railo.runtime.err.ErrorPage)
	 */
	public Struct getErrorBlock(PageContext pc, ErrorPage ep) {
		return rootCause.getErrorBlock(pc, ep);
	}

	/**
	 *
	 * @see railo.runtime.exp.IPageException#getErrorCode()
	 */
	public String getErrorCode() {
		return rootCause.getErrorCode();
	}

	/**
	 *
	 * @see railo.runtime.exp.IPageException#getExtendedInfo()
	 */
	public String getExtendedInfo() {
		return rootCause.getExtendedInfo();
	}

	/* *
	 *
	 * @see railo.runtime.exp.IPageException#getLine()
	 * /
	public String getLine() {
		return rootCause.getLine();
	}*/

	/**
	 *
	 * @see railo.runtime.exp.IPageException#getStackTraceAsString()
	 */
	public String getStackTraceAsString() {
		return rootCause.getStackTraceAsString();
        /*StringWriter sw=new StringWriter();
	    PrintWriter pw=new PrintWriter(sw);
        printStackTrace(pw);
        pw.flush();
        return sw.toString();*/
	}

	/**
	 *
	 * @see railo.runtime.exp.IPageException#getTracePointer()
	 */
	public int getTracePointer() {
		return rootCause.getTracePointer();
	}

	/**
	 *
	 * @see railo.runtime.exp.IPageException#getTypeAsString()
	 */
	public String getTypeAsString() {
		return rootCause.getTypeAsString();
	}

	/**
	 *
	 * @see railo.runtime.exp.IPageException#setDetail(java.lang.String)
	 */
	public void setDetail(String detail) {
		rootCause.setDetail(detail);
	}

	/**
	 *
	 * @see railo.runtime.exp.IPageException#setErrorCode(java.lang.String)
	 */
	public void setErrorCode(String errorCode) {
		rootCause.setErrorCode(errorCode);
	}

	/**
	 *
	 * @see railo.runtime.exp.IPageException#setExtendedInfo(java.lang.String)
	 */
	public void setExtendedInfo(String extendedInfo) {
		rootCause.setExtendedInfo(extendedInfo);
	}

	/**
	 *
	 * @see railo.runtime.exp.IPageException#setTracePointer(int)
	 */
	public void setTracePointer(int tracePointer) {
		rootCause.setTracePointer(tracePointer);
	}

	/**
	 *
	 * @see railo.runtime.exp.IPageException#typeEqual(java.lang.String)
	 */
	public boolean typeEqual(String type) {
		return rootCause.equals(type);
	}

	/**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return rootCause.toDumpData(pageContext,maxlevel,dp);
	}

	/**
	 * @return the eventName
	 */
	public String getEventName() {
		return eventName;
	}

	/**
	 *
	 * @see railo.runtime.exp.PageExceptionImpl#getLine(railo.runtime.PageContext)
	 */
	public String getLine(Config config) {
		return ((PageExceptionImpl)rootCause).getLine(config);
	}

	/**
	 *
	 * @see railo.runtime.exp.PageExceptionImpl#getRootCause()
	 */
	public Throwable getRootCause() {
		return rootCause.getRootCause();
	}

	/**
	 *
	 * @see railo.runtime.exp.PageExceptionImpl#getStackTrace()
	 */
	public StackTraceElement[] getStackTrace() {
		return rootCause.getStackTrace();
	}

	/**
	 *
	 * @see railo.runtime.exp.PageExceptionImpl#printStackTrace()
	 */
	public void printStackTrace() {
		rootCause.printStackTrace();
	}

	/**
	 *
	 * @see railo.runtime.exp.PageExceptionImpl#printStackTrace(java.io.PrintStream)
	 */
	public void printStackTrace(PrintStream s) {
		rootCause.printStackTrace(s);
	}

	/**
	 *
	 * @see railo.runtime.exp.PageExceptionImpl#printStackTrace(java.io.PrintWriter)
	 */
	public void printStackTrace(PrintWriter s) {
		rootCause.printStackTrace(s);
	}

	/**
	 * @see railo.runtime.exp.PageExceptionBox#getPageException()
	 */
	public PageException getPageException() {
		return rootCause;
	}

}
