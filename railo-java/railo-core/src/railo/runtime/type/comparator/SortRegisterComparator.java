package railo.runtime.type.comparator;

import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;


/**
 * Implementation of a Comparator, compares to Softregister Objects
 */
public final class SortRegisterComparator implements ExceptionComparator {
	
	private boolean isAsc;
	private PageException pageException=null;
    private boolean ignoreCase;


	/**
	 * constructor of the class 
	 * @param isAsc is ascending or descending
	 * @param ignoreCase do ignore case 
	 */
	public SortRegisterComparator(boolean isAsc, boolean ignoreCase) {
	    this.isAsc=isAsc;
	    this.ignoreCase=ignoreCase;
		
	}
	
	/**
	 * @return Returns the expressionException.
	 */
	public PageException getPageException() {
		return pageException;
	}
	
	@Override
	public int compare(Object oLeft, Object oRight) {
		try {
			if(pageException!=null) return 0;
			else if(isAsc) return compareObjects(oLeft, oRight);
			else return compareObjects(oRight, oLeft);
		} catch (PageException e) {
			pageException=e;
			return 0;
		}
	}
	
	private int compareObjects(Object oLeft, Object oRight) throws PageException {
		String strLeft=Caster.toString(((SortRegister)oLeft).getValue());
		String strRight=Caster.toString(((SortRegister)oRight).getValue());
		if(ignoreCase) return strLeft.compareToIgnoreCase(strRight);
		return strLeft.compareTo(strRight);
	}

}