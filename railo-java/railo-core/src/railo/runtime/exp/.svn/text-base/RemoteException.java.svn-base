package railo.runtime.exp;

import railo.commons.lang.StringUtil;
import railo.runtime.op.Caster;

public class RemoteException extends PageExceptionImpl {

	public RemoteException(Throwable t) {
		super(createMessage(t),"remote");
		
	}
	private static String createMessage(Throwable t) {
		StringBuffer message=new StringBuffer(t.getMessage());
		if(t instanceof IPageException) {
			IPageException pe=(IPageException) t;
			String detail=pe.getDetail();
			if(!StringUtil.isEmpty(detail))message.append("; ").append(detail);
			
		}
		message.append("; ");
		message.append(Caster.toClassName(t));
		
		
		
		return message.toString();
	}

}
