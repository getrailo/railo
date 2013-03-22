package railo.runtime.type.comparator;

import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;


/**
 * comparator implementation, compare to numbers
 */
public final class NumberComparator implements ExceptionComparator {

	private boolean isAsc;
	private PageException pageException=null;

	/**
	 * constructor of the class 
	 * @param isAsc is ascendinf or descending
	 */
	public NumberComparator(boolean isAsc) {
		this.isAsc=isAsc;
	}
	
	/**
	 * @return Returns the pageException.
	 */
	public PageException getPageException() {
		return pageException;
	}
	
	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
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
		double left=Caster.toDoubleValue(oLeft);
		double right=Caster.toDoubleValue(oRight);
		if(left < right)return -1;
		return left > right ? 1 : 0;
		
	}
}