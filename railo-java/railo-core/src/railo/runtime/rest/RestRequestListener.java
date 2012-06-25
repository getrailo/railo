package railo.runtime.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import railo.commons.lang.mimetype.MimeType;
import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.exp.PageException;
import railo.runtime.listener.RequestListener;
import railo.runtime.type.Struct;

public class RestRequestListener implements RequestListener {

	private final Mapping mapping;
	private final String path;
	private final int format;
	private final Struct matrix;
	private final Result defaultValue;
	private Result result;
	private final List<MimeType> accept;
	private final MimeType contentType;
	private final boolean hasFormatExtension;

	public RestRequestListener(Mapping mapping,String path,Struct matrix,int format,boolean hasFormatExtension,List<MimeType> accept,MimeType contentType, Result defaultValue) {
		this.mapping=mapping;
		this.path=path;
		this.format=format;
		this.hasFormatExtension=hasFormatExtension;
		this.matrix=matrix;
		this.defaultValue=defaultValue;
		this.accept=accept;
		this.contentType=contentType;
	}

	@Override
	public PageSource execute(PageContext pc, PageSource requestedPage) throws PageException {
		result = mapping.getResult(pc, path, matrix,format,hasFormatExtension,accept,contentType, defaultValue);
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
