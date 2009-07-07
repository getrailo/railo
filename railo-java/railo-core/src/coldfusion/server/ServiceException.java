package coldfusion.server;

import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.err.ErrorPage;
import railo.runtime.exp.ApplicationException;
import railo.runtime.type.Struct;

public class ServiceException extends ApplicationException {

	public ServiceException(String message) {
		super(message);
	}

	public String getDetail() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getErrorCode() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getExtendedInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLine() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getTracePointer() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setTracePointer(int arg0) {
		// TODO Auto-generated method stub

	}

	public String getTypeAsString() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getCustomTypeAsString() {
		// TODO Auto-generated method stub
		return null;
	}

	public Struct getCatchBlock() {
		// TODO Auto-generated method stub
		return null;
	}

	public Struct getErrorBlock(PageContext arg0, ErrorPage arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public void addContext(PageSource arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	public boolean typeEqual(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public void setDetail(String arg0) {
		// TODO Auto-generated method stub

	}

	public void setErrorCode(String arg0) {
		// TODO Auto-generated method stub

	}

	public void setExtendedInfo(String arg0) {
		// TODO Auto-generated method stub

	}

	public Struct getAddional() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getStackTraceAsString() {
		// TODO Auto-generated method stub
		return null;
	}

	public String toHTML(PageContext arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
