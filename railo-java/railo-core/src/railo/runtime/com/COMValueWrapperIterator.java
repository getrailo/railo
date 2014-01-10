package railo.runtime.com;

import java.util.Iterator;

import com.jacob.com.EnumVariant;

/**
 * 
 */
public final class COMValueWrapperIterator implements Iterator {

    private EnumVariant enumVariant;
    private COMObject wrapper;

    /**
     * @param wrapper
     */
    public COMValueWrapperIterator(COMObject wrapper) {
        this.enumVariant=new EnumVariant(wrapper.getDispatch());
        this.wrapper=wrapper;
    }

    @Override
    public void remove() {
        enumVariant.safeRelease();
    }

    @Override
    public boolean hasNext() {
        return enumVariant.hasMoreElements();
    }

    @Override
    public Object next() {
        return COMUtil.toObject(wrapper,enumVariant.Next(),"",null);
    }
}