package railo.runtime.listener;

import railo.commons.lang.types.RefBoolean;
import railo.commons.lang.types.RefBooleanImpl;
import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.exp.PageException;

public final class MixedAppListener extends ModernAppListener {

	@Override
	public void onRequest(PageContext pc, PageSource requestedPage, RequestListener rl) throws PageException {
		RefBoolean isCFC=new RefBooleanImpl(false);
		
		PageSource appPS=//pc.isCFCRequest()?null:
			AppListenerUtil.getApplicationPageSource(pc, requestedPage, mode, isCFC);
		
		if(isCFC.toBooleanValue())_onRequest(pc, requestedPage,appPS,rl);
		else ClassicAppListener._onRequest(pc, requestedPage,appPS,rl);
	}
	
	@Override
	public final String getType() {
		return "mixed";
	}
}
