package railo.runtime.listener;

import java.io.PrintStream;
import java.io.PrintWriter;

import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.config.Config;
import railo.runtime.config.Constants;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.err.ErrorPage;
import railo.runtime.exp.CatchBlock;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageExceptionImpl;
import railo.runtime.op.Duplicator;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.util.KeyConstants;

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

	@Override
	public void addContext(PageSource pageSource, int line, int column, StackTraceElement ste) {
		rootCause.addContext(pageSource, line, column,ste);
	}

	@Override
	public Struct getAdditional() {
		return rootCause.getAddional();
	}
	
	@Override
	public Struct getAddional() {
		return rootCause.getAddional();
	}

	public Struct getCatchBlock() {
		return getCatchBlock(ThreadLocalPageContext.getConfig());
	}
	
	@Override
	public Struct getCatchBlock(PageContext pc) {
		return getCatchBlock(pc.getConfig());
	}
	
	@Override
	public CatchBlock getCatchBlock(Config config) {
		CatchBlock cb=rootCause.getCatchBlock(config);
		Collection cause = (Collection) Duplicator.duplicate(cb,false);
		//rtn.setEL("message", getMessage());
		if(!cb.containsKey(KeyConstants._detail))cb.setEL(KeyConstants._detail, "Exception throwed while invoking function ["+eventName+"] from "+Constants.APP_CFC);
		cb.setEL(ROOT_CAUSE, cause);
		cb.setEL(CAUSE, cause);
		//cb.setEL("stacktrace", getStackTraceAsString());
		//rtn.setEL("tagcontext", new ArrayImpl());
		//rtn.setEL("type", getTypeAsString());
		cb.setEL(KeyConstants._name, eventName);
		return cb;
	}

	@Override
	public String getCustomTypeAsString() {
		return rootCause.getCustomTypeAsString();
	}

	@Override
	public String getDetail() {
		return rootCause.getDetail();
	}

	@Override
	public Struct getErrorBlock(PageContext pc, ErrorPage ep) {
		return rootCause.getErrorBlock(pc, ep);
	}

	@Override
	public String getErrorCode() {
		return rootCause.getErrorCode();
	}

	@Override
	public String getExtendedInfo() {
		return rootCause.getExtendedInfo();
	}

	@Override
	public String getStackTraceAsString() {
		return rootCause.getStackTraceAsString();
	}

	@Override
	public int getTracePointer() {
		return rootCause.getTracePointer();
	}

	@Override
	public String getTypeAsString() {
		return rootCause.getTypeAsString();
	}

	@Override
	public void setDetail(String detail) {
		rootCause.setDetail(detail);
	}

	@Override
	public void setErrorCode(String errorCode) {
		rootCause.setErrorCode(errorCode);
	}

	@Override
	public void setExtendedInfo(String extendedInfo) {
		rootCause.setExtendedInfo(extendedInfo);
	}

	@Override
	public void setTracePointer(int tracePointer) {
		rootCause.setTracePointer(tracePointer);
	}

	@Override
	public boolean typeEqual(String type) {
		return rootCause.equals(type);
	}

	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return rootCause.toDumpData(pageContext,maxlevel,dp);
	}

	/**
	 * @return the eventName
	 */
	public String getEventName() {
		return eventName;
	}

	public String getLine(Config config) {
		return ((PageExceptionImpl)rootCause).getLine(config);
	}

	@Override
	public Throwable getRootCause() {
		return rootCause.getRootCause();
	}

	@Override
	public StackTraceElement[] getStackTrace() {
		return rootCause.getStackTrace();
	}

	@Override
	public void printStackTrace() {
		rootCause.printStackTrace();
	}

	@Override
	public void printStackTrace(PrintStream s) {
		rootCause.printStackTrace(s);
	}

	@Override
	public void printStackTrace(PrintWriter s) {
		rootCause.printStackTrace(s);
	}

	public PageException getPageException() {
		return rootCause;
	}

}
