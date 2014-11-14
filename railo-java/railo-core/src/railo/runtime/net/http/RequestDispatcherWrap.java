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
package railo.runtime.net.http;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import railo.commons.net.HTTPUtil;
import railo.runtime.PageContext;
import railo.runtime.engine.ThreadLocalPageContext;

public class RequestDispatcherWrap implements RequestDispatcher {

	private String relPath;
	private HTTPServletRequestWrap req;

	public RequestDispatcherWrap(HTTPServletRequestWrap req, String relPath) {
		this.relPath=relPath;
		this.req=req;
	}

	public void forward(ServletRequest req, ServletResponse rsp)throws ServletException, IOException {
		PageContext pc = ThreadLocalPageContext.get();
		req=HTTPUtil.removeWrap(req);
		if(pc==null){
			this.req.getOriginalRequestDispatcher(relPath).forward(req, rsp);
			return;
		}
		
		
		relPath=HTTPUtil.optimizeRelPath(pc,relPath);
		
		try{
			RequestDispatcher disp = this.req.getOriginalRequestDispatcher(relPath);
			disp.forward(req,rsp);
		}
		finally{
	        ThreadLocalPageContext.register(pc);
		}
	}

	/*public void include(ServletRequest req, ServletResponse rsp)throws ServletException, IOException {
		PageContext pc = ThreadLocalPageContext.get();
		if(pc==null){
			this.req.getOriginalRequestDispatcher(relPath).include(req, rsp);
			return;
		}
		try{
			relPath=HTTPUtil.optimizeRelPath(pc,relPath);
			RequestDispatcher disp = this.req.getOriginalRequestDispatcher(relPath);
	        disp.include(req,rsp);
		}
		finally{
	        ThreadLocalPageContext.register(pc);
		}
	}*/
	
	

	public void include(ServletRequest req, ServletResponse rsp)throws ServletException, IOException {
		PageContext pc = ThreadLocalPageContext.get();
		if(pc==null){
			this.req.getOriginalRequestDispatcher(relPath).include(req, rsp);
			return;
		}
		//rsp.getWriter().flush();
		//print.out("abc:"+rsp);
		HTTPUtil.include(pc,req, rsp,relPath);
		
		/*
		relPath=HTTPUtil.optimizeRelPath(pc,relPath);
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
			
		try{
			HttpServletResponse drsp=new HttpServletResponseWrap(pc.getHttpServletResponse(),baos);
			RequestDispatcher disp = pc.getServletContext().getRequestDispatcher(relPath);
			if(disp==null)
        		throw new PageServletException(new ApplicationException("Page "+relPath+" not found"));
        	disp.include(req,drsp);
        	if(!drsp.isCommitted())drsp.flushBuffer();
	        pc.write(IOUtil.toString(baos.toByteArray(), drsp.getCharacterEncoding()));
		}
		finally{
	        ThreadLocalPageContext.register(pc);
		}*/
	}
}
