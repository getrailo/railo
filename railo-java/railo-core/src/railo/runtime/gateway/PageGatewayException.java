package railo.runtime.gateway;

import railo.runtime.exp.PageException;
import railo.runtime.exp.PageExceptionBox;

public class PageGatewayException extends GatewayException implements PageExceptionBox {
	
	private PageException pe;

	public PageGatewayException(PageException pe){
		super(pe.getMessage());
		this.pe=pe;
		
	}

	public PageException getPageException() {
		return pe;
	}
	
}
