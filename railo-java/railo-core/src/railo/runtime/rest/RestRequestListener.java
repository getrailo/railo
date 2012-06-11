package railo.runtime.rest;

import javax.servlet.http.HttpServletRequest;

import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.exp.PageException;
import railo.runtime.listener.RequestListener;
import railo.runtime.type.Struct;

public class RestRequestListener implements RequestListener {

	private Mapping mapping;
	private String path;
	private int format;
	private Struct matrix;
	private Result defaultValue;
	private Result result;

	public RestRequestListener(Mapping mapping,String path,int format,Struct matrix, Result defaultValue) {
		this.mapping=mapping;
		this.path=path;
		this.format=format;
		this.matrix=matrix;
		this.defaultValue=defaultValue;
	}

	@Override
	public PageSource execute(PageContext pc, PageSource requestedPage) throws PageException {
		result = mapping.getResult(pc, path, format, matrix, defaultValue);
		HttpServletRequest req = pc.getHttpServletRequest();
		req.setAttribute("client", "railo-rest-1-0");
		req.setAttribute("rest-path", path);
		req.setAttribute("rest-result", result); 
		
		if(result==null) {
			RestUtil.setStatus(pc,404,"no rest service for ["+path+"] found in mapping ["+mapping.getVirtual()+"]");
			return null;
		}
		
		return result.getSource().getPageSource();
	}

	/**
	 * @return the result
	 */
	public Result getResult() {
		return result;
	}
}
