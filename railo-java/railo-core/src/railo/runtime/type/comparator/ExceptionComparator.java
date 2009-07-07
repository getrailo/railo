
package railo.runtime.type.comparator;

import java.util.Comparator;

import railo.runtime.exp.PageException;

/**
 * Comparator that store Exception
 */
public interface ExceptionComparator extends Comparator {
    /**
	 * @return Returns the expressionException.
	 */
	public PageException getPageException();
}