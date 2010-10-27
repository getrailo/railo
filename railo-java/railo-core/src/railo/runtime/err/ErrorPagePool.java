package railo.runtime.err;

import java.util.ArrayList;

import railo.runtime.exp.PageException;

/**
 * Handle Page Errors
 */
public final class ErrorPagePool {

	private ArrayList<ErrorPage> pages=new ArrayList<ErrorPage>();
	private boolean hasChanged=false;
	
	/**
	 * sets the page error
	 * @param errorPage
	 */
	public void setErrorPage(ErrorPage errorPage) {
		pages.add(errorPage);
		hasChanged=true;
	}
	
	/**
	 * returns the error page
	 * @param pe Page Exception
	 * @return
	 */
	public ErrorPage getErrorPage(PageException pe, short type) {
		for(int i=pages.size()-1;i>=0;i--) {
			ErrorPageImpl ep=(ErrorPageImpl) pages.get(i);
			if(ep.getType()==type) {
				if(type==ErrorPageImpl.TYPE_EXCEPTION){
					if(pe.typeEqual(ep.getTypeAsString()))return ep;
				}
				else return ep;
				
			}
		}
		return null;
	}
	
	
	
	/**
	 * clear the error page pool
	 */
	public void clear() {
		if(hasChanged) {
			pages.clear();
		}
		hasChanged=false;
	}

	/**
	 * remove this error page 
	 * @param type
	 */
	public void removeErrorPage(PageException pe) {
		// exception
		ErrorPage ep = getErrorPage(pe,ErrorPageImpl.TYPE_EXCEPTION);
		if(ep!=null){
			pages.remove(ep);
			hasChanged=true;
		}
		// request
		ep = getErrorPage(pe,ErrorPageImpl.TYPE_REQUEST);
		if(ep!=null){
			pages.remove(ep);
			hasChanged=true;
		}
		// validation
		ep = getErrorPage(pe,ErrorPageImpl.TYPE_VALIDATION);
		if(ep!=null){
			pages.remove(ep);
			hasChanged=true;
		}
		
	}
	
}