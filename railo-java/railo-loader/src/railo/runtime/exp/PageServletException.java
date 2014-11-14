/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
package railo.runtime.exp;


import javax.servlet.ServletException;

import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.config.Config;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.err.ErrorPage;
import railo.runtime.type.Struct;


/**
 * by definition a JSP Tag can only throw JSPExceptions, 
 * for that case the PageException is a Subclass of the JSPExceptions, but when a PageException, 
 * is escaleted to a parent page, this goes over the include method of the PageContext Object, but this can only throw ServletException.
 * For that this class can Box a JSPException (PageException) in a ServletException (PageServletException)
 */
public final class PageServletException extends ServletException implements IPageException,PageExceptionBox {
	private PageException pe;

		
	/**
	 * constructor of the class
	 * @param pe page exception to hold
	 */
	public PageServletException(PageException pe) {
		super(pe.getMessage());
		this.pe=pe;
	}

	/**
	 * @see railo.runtime.exp.PageExceptionBox#getPageException()
	 */
	public PageException getPageException() {
		return pe;
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
		return pe.getCatchBlock(pc.getConfig());
	}

	/**
	 *
	 * @see railo.runtime.exp.IPageException#getCatchBlock(railo.runtime.PageContext)
	 */
	public CatchBlock getCatchBlock(Config config) {
		return pe.getCatchBlock(config);
	}

	/**
	 * @see railo.runtime.exp.IPageException#getErrorBlock(PageContext pc,ErrorPage ep)
	 */
	public Struct getErrorBlock(PageContext pc,ErrorPage ep) {
		return pe.getErrorBlock(pc, ep);
	}

	/**
	 * @see railo.runtime.exp.IPageException#addContext(railo.runtime.PageSource, int, int, java.lang.StackTraceElement)
	 */
	public void addContext(PageSource template, int line, int column, StackTraceElement ste) {
		pe.addContext(template,line,column,ste);
	}

	/**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int, railo.runtime.dump.DumpProperties)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return pe.toDumpData(pageContext, maxlevel,dp);
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
	 * @see railo.runtime.exp.IPageException#getTypeAsString()
	 */
	public String getTypeAsString() {
		return pe.getTypeAsString();
	}

	/**
	 * @see railo.runtime.exp.IPageException#typeEqual(java.lang.String)
	 */
	public boolean typeEqual(String type) {
		return pe.typeEqual(type);
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

    /**
     * @see railo.runtime.exp.IPageException#getStackTraceAsString()
     */
    public String getStackTraceAsString() {
        return pe.getStackTraceAsString();
    }
}