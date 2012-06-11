package railo.runtime.listener;

import java.io.IOException;

import railo.runtime.CFMLFactory;
import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.config.Constants;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;

public final class ClassicAppListener extends AppListenerSupport {

	private int mode=MODE_CURRENT2ROOT;
	private String type;

	@Override
	public void onRequest(PageContext pc,PageSource requestedPage, RequestListener rl) throws PageException {
		
		PageSource application=//pc.isCFCRequest()?null:
			AppListenerUtil.getApplicationPageSource(pc,requestedPage,Constants.APP_CFM,mode);
		
		_onRequest(pc, requestedPage, application,rl);
	}
	
	static void _onRequest(PageContext pc,PageSource requestedPage,PageSource application, RequestListener rl) throws PageException {
		
		// on requestStart
		if(application!=null)pc.doInclude(application);
		
		if(rl!=null) {
			requestedPage=rl.execute(pc, requestedPage);
			if(requestedPage==null) return;
		}
		
		// request
		pc.doInclude(requestedPage);
		
		// on Request End
		if(application!=null){
			PageSource onReqEnd = application.getRealPage("OnRequestEnd.cfm");
	        if(onReqEnd.exists())pc.doInclude(onReqEnd);
		}
	}

	@Override
	public boolean onApplicationStart(PageContext pc) throws PageException {
		// do nothing
		return true;
	}

	@Override
	public void onSessionStart(PageContext pc) throws PageException {
		// do nothing
	}

	@Override
	public void onApplicationEnd(CFMLFactory factory, String applicationName) throws PageException {
		// do nothing	
	}

	@Override
	public void onSessionEnd(CFMLFactory cfmlFactory, String applicationName, String cfid) throws PageException {
		// do nothing
	}

	@Override
	public void onDebug(PageContext pc) throws PageException {
		try {
			pc.getDebugger().writeOut(pc);
		} 
		catch (IOException e) {
			throw Caster.toPageException(e);
		}
	}

	@Override
	public void onError(PageContext pc,PageException pe) {
		pc.handlePageException(pe);
	}

	@Override
	public void setMode(int mode) {
		this.mode=mode;
	}

	@Override
	public int getMode() {
		return mode;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}
}