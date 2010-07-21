package railo.runtime.type.comparator;

import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;


/**
 * comparator implementation to compare textes
 */
public final class TextComparator implements ExceptionComparator {
	
	private boolean isAsc;
	private boolean ignoreCase;
	private PageException pageException=null;

	/**
	 * constructor of the class
	 * @param isAsc ascending or desending
	 * @param ignoreCase ignore case or not
	 */
	public TextComparator(boolean isAsc, boolean ignoreCase) {
		this.isAsc=isAsc;
		this.ignoreCase=ignoreCase;
	}
	
	/**
	 * @return Returns the expressionException.
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
		if(ignoreCase)return Caster.toString(oLeft).compareToIgnoreCase(Caster.toString(oRight));
		return Caster.toString(oLeft).compareTo(Caster.toString(oRight));
	}

}