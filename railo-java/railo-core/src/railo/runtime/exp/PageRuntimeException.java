package railo.runtime.exp;

import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.config.Config;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.err.ErrorPage;
import railo.runtime.type.Struct;

/**
 *
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class PageRuntimeException extends RuntimeException implements IPageException,PageExceptionBox {
	
	private PageException pe;

	
	/**
	 * constructor of the class
	 * @param pe page exception to hold
	 */
	public PageRuntimeException(PageException pe) {
		super(pe.getMessage());
		this.pe=pe;
	}
	
	/**
	 * standart excption constructor
	 * @param message message of the exception
	 */
	public PageRuntimeException(String message) {
		super(message);
		this.pe=new ApplicationException(message);
	}
	
	/**
	 * standart excption constructor
	 * @param message message of the exception
	 * @param detail detailed information to the exception
	 */
	public PageRuntimeException(String message,String detail) {
		super(message);
		this.pe=new ApplicationException(message,detail);
	}

	/**
	 * @see railo.runtime.exp.IPageException#getDetail()
	 */
	public String getDetail() {
		return pe.getDetail();
	}
	
	/**
	 * @see railo.runtime.exp.IPageException#getErrorCode()
	 */
	public String getErrorCode() {
		return pe.getErrorCode();
	}
	
	/**
	 * @see railo.runtime.exp.IPageException#getExtendedInfo()
	 */
	public String getExtendedInfo() {
		return pe.getExtendedInfo();
	}
	
	/**
	 *
	 * @see railo.runtime.exp.IPageException#getCatchBlock(railo.runtime.PageContext)
	 */
	public Struct getCatchBlock(PageContext pc) {
		return pe.getCatchBlock(pc);
	}
	
	/**
	 * @see railo.runtime.exp.IPageException#getCatchBlock()
	 */
	public Struct getCatchBlock() {
		// TLPC
		return pe.getCatchBlock(ThreadLocalPageContext.get());
	}
	
	public CatchBlock getCatchBlock(Config config) {
		return pe.getCatchBlock(config);
	}
	
	/**
	 * @see railo.runtime.exp.IPageException#getErrorBlock(PageContext pc,ErrorPage ep)
	 */
	public Struct getErrorBlock(PageContext pc,ErrorPage ep) {
		return pe.getErrorBlock(pc,ep);
	}
	/**
	 * @see railo.runtime.exp.IPageException#addContext(PageSource, int, int)
	 */
	public void addContext(PageSource template, int line, int column,StackTraceElement ste) {
		pe.addContext(template,line,column,ste);
	}
	
	/**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return pe.toDumpData(pageContext, maxlevel,dp);
	}

	/**
	 * @see railo.runtime.exp.PageExceptionBox#getPageException()
	 */
	public PageException getPageException() {
		return pe;
	}

	/**
	 * @see railo.runtime.exp.IPageException#setDetail(java.lang.String)
	 */
	public void setDetail(String detail) {
			 pe.setDetail(detail);
	}

	/**
	 * @see railo.runtime.exp.IPageException#setErrorCode(java.lang.String)
	 */
	public void setErrorCode(String errorCode) {
			 pe.setErrorCode(errorCode);		
	}

	/**
	 * @see railo.runtime.exp.IPageException#setExtendedInfo(java.lang.String)
	 */
	public void setExtendedInfo(String extendedInfo) {
			 pe.setExtendedInfo(extendedInfo);		
	}
	
	/**
	 * @see railo.runtime.exp.IPageException#typeEqual(java.lang.String)
	 */
	public boolean typeEqual(String type) {
		return 	pe.typeEqual(type);
	}
	
	/**
	 * @see railo.runtime.exp.IPageException#getTypeAsString()
	 */
	public String getTypeAsString() {
		return pe.getTypeAsString();
	}

	/**
	 * @see railo.runtime.exp.IPageException#getCustomTypeAsString()
	 */
	public String getCustomTypeAsString() {
		return pe.getCustomTypeAsString();
	}

    /* *
     * @see railo.runtime.exp.IPageException#getLine()
     * /
    public String getLine() {
        return pe.getLine();
    }*/

    /**
     * @see railo.runtime.exp.IPageException#getTracePointer()
     */
    public int getTracePointer() {
        return pe.getTracePointer();
    }

    /**
     * @see railo.runtime.exp.IPageException#setTracePointer(int)
     */
    public void setTracePointer(int tracePointer) {
        pe.setTracePointer(tracePointer);
    }

    /**
     * @see railo.runtime.exp.IPageException#getAdditional()
     */
    public Struct getAdditional() {
        return pe.getAddional();
    }
    public Struct getAddional() {
        return pe.getAddional();
    }

    public String getStackTraceAsString() {
        return pe.getStackTraceAsString();
    }
}