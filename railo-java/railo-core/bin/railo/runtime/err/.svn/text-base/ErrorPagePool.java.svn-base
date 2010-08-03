package railo.runtime.err;

import java.util.ArrayList;

import railo.runtime.exp.PageException;

/**
 * Handle Page Errors
 */
public final class ErrorPagePool {
	
	private ArrayList pages=new ArrayList();
	private boolean hasChanged=false;
	
	/**
	 * sets the page error
	 * @param errorPage
	 */
	public void setErrorPage(ErrorPage errorPage) {
		//railo.print.ln("set:"+errorPage.getTypeAsString());
		pages.add(errorPage);
		//pages.put(Caster.toLowerCase(errorPage.getTypeAsString()),errorPage);
		hasChanged=true;
	}
	
	/**
	 * returns the error page
	 * @param pe Page Exception
	 * @return
	 */
	public ErrorPage getErrorPage(PageException pe) {
		for(int i=pages.size()-1;i>=0;i--) {
			ErrorPage ep=(ErrorPage) pages.get(i);
			if(pe.typeEqual(ep.getTypeAsString())) return ep;
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
		pages.remove(getErrorPage(pe));
		hasChanged=true;
	}
	
}