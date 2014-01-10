package railo.runtime.type.comparator;

import java.util.Comparator;

import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.op.Caster;


/**
 * comparator implementation to compare textes
 */
public final class TextComparator implements Comparator {
	
	private boolean isAsc;
	private boolean ignoreCase;

	/**
	 * constructor of the class
	 * @param isAsc ascending or desending
	 * @param ignoreCase ignore case or not
	 */
	public TextComparator(boolean isAsc, boolean ignoreCase) {
		this.isAsc=isAsc;
		this.ignoreCase=ignoreCase;
	}
	
	@Override
	public int compare(Object oLeft, Object oRight) {
		try {
			if(isAsc) return compareObjects(oLeft, oRight);
			return compareObjects(oRight, oLeft);
		} 
		catch (PageException e) {
			throw new PageRuntimeException(new ExpressionException("can only sort arrays with simple values",e.getMessage()));
		}
	}
	
	private int compareObjects(Object oLeft, Object oRight) throws PageException {
		if(ignoreCase)return Caster.toString(oLeft).compareToIgnoreCase(Caster.toString(oRight));
		return Caster.toString(oLeft).compareTo(Caster.toString(oRight));
	}

}