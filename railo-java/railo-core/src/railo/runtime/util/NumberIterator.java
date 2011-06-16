package railo.runtime.util;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Operator;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;

/**
 * A Number Iterator Implementation to iterate from to
 */
public final class NumberIterator {
	
	private int _from;
	private int _to;
	private int _current;
	
	//private static int count=0;
	
	/**
	 * constructor of the number iterator
	 * @param from iterate from
	 * @param to iterate to
	 */
	private NumberIterator(int from, int to) {
	    //railo.print.ln(count++);
        init(from,to);
	}
	private NumberIterator init(int from, int to) {
	    this._from=from;
		this._current=from;
		this._to=to; 
		return this;
	}
		
	/**
	 * @return returns if there is a next value
	 */
	public boolean hasNext() {
		return _current<_to;
	}
	
	/**
	 * @return increment and return new value
	 */
	public int next() {
	    return ++_current;
	}
	
	/**
	 * @return returns if there is a previous value
	 */
	public boolean hasPrevious() {
		return _current>_from;
	}
	
	/**
	 * @return decrement and return new value
	 */
	public int previous() {
		return --_current;
	}
	
	/**
	 * @return returns smallest possible value
	 */
	public int from() {
		return _from;
	}
	
	/**
	 * @return returns greatest value
	 */
	public int to() {
		return _to;
	}
	
	/**
	 * @return set value to first and return
	 */
	public int first() {
		return _current=_from;
	}
	
	/**
	 * @return set value to last and return thid value
	 */
	public int last() {
		return _current=_to;
	}
	/**
	 * @return returns current value
	 */
	public int current() {
		return _current;
	}
	
	/**
	 * sets the current position
	 * @param current
	 */
	public void setCurrent(int current) {
		_current=current;
	}

	/**
	 * @return is after last
	 */
	public boolean isAfterLast() {
		return _current>_to;
	}	
	/**
	 * @return is pointer on a valid position
	 */
	public boolean isValid() {
		return _current>=_from && _current<=_to;
	}	
	
	
	
	
	private static NumberIterator[] iterators=new NumberIterator[]{
		    new NumberIterator(1,1),
		    new NumberIterator(1,1),
		    new NumberIterator(1,1),
		    new NumberIterator(1,1),
		    new NumberIterator(1,1),
		    new NumberIterator(1,1),
		    new NumberIterator(1,1),
		    new NumberIterator(1,1),
		    new NumberIterator(1,1),
		    new NumberIterator(1,1)
	};
	private static int pointer=0;
	
	/**
	 * load a iterator
	 * @param from
	 * @param to iterate to
	 * @return NumberIterator
	 */
	private static NumberIterator _load(int from, int to) {
	    if(pointer>=iterators.length) return new NumberIterator(from,to);
	    return iterators[pointer++].init(from,to);
	}
	
	/**
	 * create a Number Iterator with value from and to
	 * @param from
	 * @param to
	 * @return NumberIterator
	 */
	public static synchronized NumberIterator load(double from, double to) {
		return _load((int)from,(int)to);
	}
	
	/**
	 * create a Number Iterator with value from and to
	 * @param from
	 * @param to
	 * @param max
	 * @return NumberIterator
	 */
	public static synchronized NumberIterator load(double from, double to, double max) {
	    return _load((int)from,(int)((from+max-1<to)?from+max-1:to));
	}
	
	/**
	 * @param ni
	 * @param query
	 * @param groupName
	 * @param caseSensitive
	 * @return number iterator for group
	 * @throws PageException
	 */
	public static synchronized NumberIterator load(PageContext pc, NumberIterator ni, Query query, String groupName, boolean caseSensitive) throws PageException {
		int startIndex=query.getCurrentrow(pc.getId()); 
		
        Object startValue=query.get(KeyImpl.init(groupName)); 
        while(ni.hasNext()) { 
            if(!Operator.equals(startValue,query.getAt(KeyImpl.init(groupName),ni.next()),caseSensitive)) { 
                        ni.previous();
                        return _load(startIndex,ni.current());
            } 
        } 
        return _load(startIndex,ni.current());
    } 
	
	/**
	 * @param ni Iterator to release
	 */
	public static synchronized void release(NumberIterator ni) {
	    if(pointer>0) {
	        iterators[--pointer]=ni;
	    }
	}
	
	
	
	
	
	
	
}