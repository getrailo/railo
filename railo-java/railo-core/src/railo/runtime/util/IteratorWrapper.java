package railo.runtime.util;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * 
 */
public final class IteratorWrapper implements Iterator {
    
    private Enumeration e;

    /**
     * constructor of the class
     * @param enum
     */
    public IteratorWrapper(Enumeration e) {
        this.e=e;
    }

    /**
     * @see java.util.Iterator#remove()
     */
    @Override
	public void remove() {
    }

    /**
     * @see java.util.Iterator#hasNext()
     */
    @Override
	public boolean hasNext() {
        return e.hasMoreElements();
    }

    /**
     * @see java.util.Iterator#next()
     */
    @Override
	public Object next() {
        return e.nextElement();
    }
}