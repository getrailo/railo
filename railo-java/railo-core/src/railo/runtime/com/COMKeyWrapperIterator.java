package railo.runtime.com;

import java.util.Iterator;

import railo.runtime.exp.CasterException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;

import com.jacob.com.EnumVariant;

/**
 * MUST this is a value iterator instead of a key iterator
 * 
 */
public final class COMKeyWrapperIterator implements Iterator<Collection.Key> {

    private EnumVariant enumVariant;
    private COMObject wrapper;

    /**
     * @param wrapper
     */
    public COMKeyWrapperIterator(COMObject wrapper) {
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
    public Collection.Key next() {
        try {
			return Caster.toKey(COMUtil.toObject(wrapper,enumVariant.Next(),"",null));
		} catch (CasterException e) {
			throw new PageRuntimeException(e);
		}
    }
}