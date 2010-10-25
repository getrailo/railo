package railo.runtime.dump;

import java.io.Serializable;

import railo.runtime.PageContext;


/**
 * this interface make a object printable, also to a simple object
 */
public interface Dumpable extends Serializable {
    
	/**
	 * method to print out information to a object as HTML
	 * @param pageContext
	 * @return HTML print out
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties properties);
}