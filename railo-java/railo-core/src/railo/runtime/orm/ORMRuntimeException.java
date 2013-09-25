package railo.runtime.orm;

import org.hibernate.HibernateException;

import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.config.Config;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.err.ErrorPage;
import railo.runtime.exp.CatchBlock;
import railo.runtime.exp.IPageException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageExceptionBox;
import railo.runtime.orm.hibernate.HibernateORMEngine;
import railo.runtime.type.Struct;

public class ORMRuntimeException extends HibernateException  implements IPageException,PageExceptionBox  {

	private static final long serialVersionUID = -7745292875775743390L;
	
	private PageException pe;

	
	/**
	 * constructor of the class
	 * @param pe page exception to hold
	 */
	public ORMRuntimeException(PageException pe) {
		super(pe.getMessage());
		this.pe=pe;
	}
	
	
	/**
	 * standart excption constructor
	 * @param message message of the exception
	 * @param detail detailed information to the exception
	 */
	public ORMRuntimeException(String message,String detail) {
		super(message);
		this.pe=new ORMException(null,null,message,detail);
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
	public Struct getCatchBlock(PageContext pc) {
		return getCatchBlock(pc.getConfig());
	}
	
	public Struct getCatchBlock() {
		// TLPC
		return pe.getCatchBlock(ThreadLocalPageContext.getConfig());
	}
	

	public CatchBlock getCatchBlock(Config config) {
		return pe.getCatchBlock(config);
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
