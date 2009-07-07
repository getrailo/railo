package railo.runtime.com;

import java.util.Iterator;

import com.jacob.com.EnumVariant;

/**
 * MUST this is a value iterator instead of a key iterator
 * 
 */
public final class COMKeyWrapperIterator implements Iterator {

    private EnumVariant enumVariant;
    private COMObject wrapper;

    /**
     * @param wrapper
     */
    public COMKeyWrapperIterator(COMObject wrapper) {
        this.enumVariant=new EnumVariant(wrapper.getDispatch());
        this.wrapper=wrapper;
    }

    /**
     * @see java.util.Iterator#remove()
     */
    public void remove() {
        enumVariant.safeRelease();
    }

    /**
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        return enumVariant.hasMoreElements();
    }

    /**
     * @see java.util.Iterator#next()
     */
    public Object next() {
        return COMUtil.toObject(wrapper,enumVariant.Next(),"",null);
    }
}