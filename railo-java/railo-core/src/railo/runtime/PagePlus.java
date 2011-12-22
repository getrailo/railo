package railo.runtime;

import railo.runtime.component.ImportDefintion;
import railo.runtime.type.Struct;


// FUTURE move all this to page and delete this class
public abstract class PagePlus extends Page {
	
	private static final ImportDefintion[] ZERO=new ImportDefintion[0];
	
	
	/**
	 * @see railo.runtime.Page#call(railo.runtime.PageContext)
	 */
	@Override
	public void call(PageContext pc) throws Throwable {
		// TODO Auto-generated method stub
		
	}



	/**
	 * @see railo.runtime.Page#getPageSource()
	 */
	@Override
	public PageSource getPageSource() {
		//print.ds("getPS");
		return super.getPageSource();
	}



	public ImportDefintion[] getImportDefintions() {
		
		return ZERO;//new ImportDefintion[]{ImportDefintion.getInstance("jm.test.components.*",null),ImportDefintion.getInstance("jm.test.*",null)};
	}
	
	public Struct metaData;
}
