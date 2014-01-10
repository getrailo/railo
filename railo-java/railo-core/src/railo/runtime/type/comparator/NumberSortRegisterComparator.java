package railo.runtime.type.comparator;

import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Operator;


/**
 * Implementation of a Comparator, compares to Softregister Objects
 */
public final class NumberSortRegisterComparator implements ExceptionComparator {
	
	private boolean isAsc;
	private PageException pageException=null;

	/**
	 * constructor of the class 
	 * @param isAsc is ascendinf or descending
	 */
	public NumberSortRegisterComparator(boolean isAsc) {
        
		this.isAsc=isAsc;
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
        /*return Operator.compare(
                ((SortRegister)oLeft).getValue(),
                ((SortRegister)oRight).getValue()
        );
        */
        return Operator.compare(
                Caster.toDoubleValue(((SortRegister)oLeft).getValue())
                ,
                Caster.toDoubleValue(((SortRegister)oRight).getValue())
        );
        
	}

}