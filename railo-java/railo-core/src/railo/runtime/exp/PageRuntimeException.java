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

	@Override
	public String getDetail() {
		return pe.getDetail();
	}
	
	@Override
	public String getErrorCode() {
		return pe.getErrorCode();
	}
	
	@Override
	public String getExtendedInfo() {
		return pe.getExtendedInfo();
	}
	
	@Override
	public CatchBlock getCatchBlock(Config config) {
		return pe.getCatchBlock(config);
	}
	
	@Override
	public Struct getCatchBlock(PageContext pc) {
		return pe.getCatchBlock(pc.getConfig());
	}
	
	public Struct getCatchBlock() {
		// TLPC
		return pe.getCatchBlock(ThreadLocalPageContext.getConfig());
	}
	
	@Override
	public Struct getErrorBlock(PageContext pc,ErrorPage ep) {
		return pe.getErrorBlock(pc,ep);
	}
	@Override
	public void addContext(PageSource template, int line, int column,StackTraceElement ste) {
		pe.addContext(template,line,column,ste);
	}
	
	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return pe.toDumpData(pageContext, maxlevel,dp);
	}

	@Override
	public PageException getPageException() {
		return pe;
	}

	@Override
	public void setDetail(String detail) {
			 pe.setDetail(detail);
	}

	@Override
	public void setErrorCode(String errorCode) {
			 pe.setErrorCode(errorCode);		
	}

	@Override
	public void setExtendedInfo(String extendedInfo) {
			 pe.setExtendedInfo(extendedInfo);		
	}
	
	@Override
	public boolean typeEqual(String type) {
		return 	pe.typeEqual(type);
	}
	
	@Override
	public String getTypeAsString() {
		return pe.getTypeAsString();
	}

	@Override
	public String getCustomTypeAsString() {
		return pe.getCustomTypeAsString();
	}

    @Override
    public int getTracePointer() {
        return pe.getTracePointer();
    }

    @Override
    public void setTracePointer(int tracePointer) {
        pe.setTracePointer(tracePointer);
    }

    @Override
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