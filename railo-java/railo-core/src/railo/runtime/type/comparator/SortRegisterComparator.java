package railo.runtime.type.comparator;

import java.text.Collator;
import java.util.Comparator;

import railo.commons.lang.ComparatorUtil;
import railo.runtime.PageContext;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.util.ArrayUtil;


/**
 * Implementation of a Comparator, compares to Softregister Objects
 */
public final class SortRegisterComparator implements ExceptionComparator {
	
	private boolean isAsc;
	private PageException pageException=null;
    private boolean ignoreCase;
	private final Comparator comparator;


	/**
	 * constructor of the class 
	 * @param isAsc is ascending or descending
	 * @param ignoreCase do ignore case 
	 */
	public SortRegisterComparator(PageContext pc,boolean isAsc, boolean ignoreCase, boolean localeSensitive) {
	    this.isAsc=isAsc;
	    this.ignoreCase=ignoreCase;
	    
	    comparator = ComparatorUtil.toComparator(
	    		ignoreCase?ComparatorUtil.SORT_TYPE_TEXT_NO_CASE:ComparatorUtil.SORT_TYPE_TEXT
	    		, isAsc, localeSensitive?ThreadLocalPageContext.getLocale(pc):null, null);
		
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
		return comparator.compare(strLeft, strRight);
	}

}