package railo.runtime.net.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import railo.commons.io.IOUtil;
import railo.commons.net.HTTPUtil;
import railo.runtime.PageContext;
import railo.runtime.engine.ThreadLocalPageContext;

public class RequestDispatcherWrap implements RequestDispatcher {

	private String realPath;
	private HTTPServletRequestWrap req;

	public RequestDispatcherWrap(HTTPServletRequestWrap req, String realPath) {
		this.realPath=realPath;
		this.req=req;
	}

	public void forward(ServletRequest req, ServletResponse rsp)throws ServletException, IOException {
		PageContext pc = ThreadLocalPageContext.get();
		if(pc==null){
			this.req.getOriginalRequestDispatcher(realPath).forward(req, rsp);
			return;
		}
		
		
		if(pc!=null)realPath=HTTPUtil.optimizeRealPath(pc,realPath);
		
		try{
			RequestDispatcher disp = this.req.getOriginalRequestDispatcher(realPath);
			disp.forward(req,rsp);
		}
		finally{
	        if(pc!=null)ThreadLocalPageContext.register(pc);
		}
	}

	/*public void include(ServletRequest req, ServletResponse rsp)throws ServletException, IOException {
		PageContext pc = ThreadLocalPageContext.get();
		if(pc==null){
			this.req.getOriginalRequestDispatcher(realPath).include(req, rsp);
			return;
		}
		try{
			realPath=HTTPUtil.optimizeRealPath(pc,realPath);
			RequestDispatcher disp = this.req.getOriginalRequestDispatcher(realPath);
	        disp.include(req,rsp);
		}
		finally{
	        ThreadLocalPageContext.register(pc);
		}
	}*/

	public void include(ServletRequest req, ServletResponse rsp)throws ServletException, IOException {
		PageContext pc = ThreadLocalPageContext.get();
		if(pc==null){
			this.req.getOriginalRequestDispatcher(realPath).include(req, rsp);
			return;
		}
		try{
			realPath=HTTPUtil.optimizeRealPath(pc,realPath);
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			HttpServletResponseDummy drsp=new HttpServletResponseDummy(baos);
			
			RequestDispatcher disp = pc.getServletContext().getRequestDispatcher(realPath);
	        disp.include(req,drsp);
	        pc.write(IOUtil.toString(baos.toByteArray(), drsp.getCharacterEncoding()));
		}
		finally{
	        ThreadLocalPageContext.register(pc);
		}
	}
}
