package railo.runtime.type;

import java.util.Iterator;
import java.util.List;

import railo.runtime.exp.DatabaseException;
import railo.runtime.exp.DeprecatedException;

/**
 * implementation of the query column
 */
public final class DebugQueryColumn extends QueryColumnImpl implements QueryColumnPro,Sizeable,Objects {
      
	
	private boolean used;


	/**
	 * @return the used
	 */
	public boolean isUsed() {
		return used;
	}
	

    public DebugQueryColumn(Object[] data, Key key, QueryImpl query,int size, int type,
			boolean typeChecked) {
		this.data=data;
		this.key=key;
		this.query=query;
		this.size=size;
		this.type=type;
		this.typeChecked=typeChecked;
	}
	
	
	/**
	 * Constructor of the class
	 * for internal usage only
	 */
	public DebugQueryColumn() {
		super();
	}


	@Override
    public Object get(int row) throws DeprecatedException{
    	used=true;
    	return super.get(row);
    }

    /**
     * touch the given line on the column at given row
     * @param row
     * @return new row or existing
     * @throws DatabaseException
     */
    public Object touch(int row) {
    	used=true;
    	return super.touch(row);
    }
    
    /**
     * touch the given line on the column at given row
     * @param row
     * @return new row or existing
     * @throws DatabaseException
     */
    public Object touchEL(int row) {
    	used=true;
    	return super.touchEL(row);
    }

	@Override
	public Object get(int row, Object defaultValue) {
		used=true;
    	return super.get(row,defaultValue);
	}
	
    
	public synchronized QueryColumnPro cloneColumn(QueryImpl query, boolean deepCopy) {
        DebugQueryColumn clone=new DebugQueryColumn();
        populate(this, clone, deepCopy);
        return clone;
    }

	@Override
	public Iterator<Object> valueIterator() {
		used=true;
		return super.valueIterator();
	}
	@Override
	public int indexOf(Object o) {
		used=true;
		return super.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		used=true;
		return super.lastIndexOf(o);
	}

	@Override
	public List<Object> subList(int fromIndex, int toIndex) {
		used=true;
		return super.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {
		used=true;
		return super.toArray();
	}

	@Override
	public  Object[] toArray(Object[] trg) {
		used=true;
		return super.toArray(trg);
	}
	
	public QueryColumnPro toDebugColumn() {
		return this;
	}
}