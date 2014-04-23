package railo.runtime.type.comparator;

import java.util.TimeZone;

import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Operator;
import railo.runtime.op.date.DateCaster;


/**
 * Implementation of a Comparator, compares to Softregister Objects
 */
public final class NumberSortRegisterComparator implements ExceptionComparator {
	
	private boolean isAsc;
	private TimeZone tz;
	private PageException pageException=null;

	/**
	 * constructor of the class 
	 * @param isAsc is ascendinf or descending
	 */
	public NumberSortRegisterComparator(boolean isAsc,TimeZone tz) {
        
		this.isAsc=isAsc;
		this.tz = tz;
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
		Object left = ((SortRegister)oLeft).getValue();
		Object right = ((SortRegister)oRight).getValue();
		// elevate a raw java.util.date object to railo date time.
		
        return Operator.compare(
                Caster.toDoubleValue(_fixIfDate(left))
                ,
                Caster.toDoubleValue(_fixIfDate(right))
        );
        
	}
	
	private Object _fixIfDate(Object o) throws PageException {
		if(o instanceof java.util.GregorianCalendar || o instanceof java.util.Date) {
			return DateCaster.toDateAdvanced(o, tz);
		}
		return o;
	}

}