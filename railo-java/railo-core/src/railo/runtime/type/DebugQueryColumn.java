package railo.runtime.type;

import java.util.Iterator;
import java.util.List;

import railo.runtime.exp.DatabaseException;

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
	

    public DebugQueryColumn(Object[] data, Key key, QueryImpl query,
			QueryColumnUtil queryColumnUtil, int size, int type,
			boolean typeChecked) {
		this.data=data;
		this.key=key;
		this.query=query;
		this.queryColumnUtil=queryColumnUtil;
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


	/**
     * @see railo.runtime.type.QueryColumn#get(int)
     */
    public Object get(int row){
    	used=true;
    	return super.get(row);
    }

    /**
     * touch the given line on the column at given row
     * @param row
     * @return new row or existing
     * @throws DatabaseException
     */
    public Object touch(int row) throws DatabaseException{
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

	/**
	 * @see railo.runtime.type.QueryColumn#get(int, java.lang.Object)
	 */
	public Object get(int row, Object defaultValue) {
		used=true;
    	return super.get(row,defaultValue);
	}
	
    
	public synchronized QueryColumnPro cloneColumn(QueryImpl query, boolean deepCopy) {
        DebugQueryColumn clone=new DebugQueryColumn();
        populate(this, clone, deepCopy);
        return clone;
    }

	/**
	 * @see railo.runtime.type.Iteratorable#valueIterator()
	 */
	public Iterator valueIterator() {
		used=true;
		return super.valueIterator();
	}
	/**
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	public int indexOf(Object o) {
		used=true;
		return super.indexOf(o);
	}

	/**
	 * @see java.util.List#lastIndexOf(java.lang.Object)
	 */
	public int lastIndexOf(Object o) {
		used=true;
		return super.lastIndexOf(o);
	}

	/**
	 * @see java.util.List#subList(int, int)
	 */
	public List<Object> subList(int fromIndex, int toIndex) {
		used=true;
		return super.subList(fromIndex, toIndex);
	}

	/**
	 * @see java.util.List#toArray()
	 */
	public Object[] toArray() {
		used=true;
		return super.toArray();
	}

	/**
	 * @see java.util.List#toArray(T[])
	 */
	public  Object[] toArray(Object[] trg) {
		used=true;
		return super.toArray(trg);
	}
	
	public QueryColumnPro toDebugColumn() {
		return this;
	}
}