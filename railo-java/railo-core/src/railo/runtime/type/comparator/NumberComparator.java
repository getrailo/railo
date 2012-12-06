package railo.runtime.type.comparator;

import java.util.Comparator;

import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.op.Caster;


/**
 * comparator implementation, compare to numbers
 */
public final class NumberComparator implements Comparator {

	private boolean isAsc;

	/**
	 * constructor of the class 
	 * @param isAsc is ascendinf or descending
	 */
	public NumberComparator(boolean isAsc) {
		this.isAsc=isAsc;
	}
	
	@Override
	public int compare(Object oLeft, Object oRight) {
		try {
			if(isAsc) return compareObjects(oLeft, oRight);
			return compareObjects(oRight, oLeft);
		} catch (PageException e) {
			throw new PageRuntimeException(new ExpressionException("can only sort arrays with simple values",e.getMessage()));
		}
	}
	
	private int compareObjects(Object oLeft, Object oRight) throws PageException {
		double left=Caster.toDoubleValue(oLeft);
		double right=Caster.toDoubleValue(oRight);
		if(left < right)return -1;
		return left > right ? 1 : 0;
		
	}
}