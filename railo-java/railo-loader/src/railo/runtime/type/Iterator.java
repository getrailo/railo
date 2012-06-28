package railo.runtime.type;

import railo.runtime.exp.PageException;

/**
 * Interface for a simple Iterator
 */
public interface Iterator {

	/**
	* set the intern pointer of the iterator to the next position, 
	* return true if next position exist otherwise false.
	* @return	boolean
	* @throws PageException
	* @deprecated use instead <code>{@link #next(int)}</code>
	*/
	public boolean next()throws PageException;
	
	/**
	* set the intern pointer of the iterator to the next position, 
	* return true if next position exist otherwise false.
	* @return	boolean
	* @throws PageException
	*/
	public boolean next(int pid)throws PageException;

	public boolean previous(int pid);
	
	/**
	* reset ther intern pointer
	* @throws PageException
	* @deprecated use instead <code>{@link #reset(int)}</code>
	*/
	public void reset()throws PageException;
	
	/**
	*
	* reset ther intern pointer
	* @throws PageException
	*/
	public void reset(int pid)throws PageException;
	
	/**
	* return recordcount of the iterator object
	* @return	int 
	*/
	public int getRecordcount();
	
	
	/**
	* return the current position of the internal pointer
	* @return	int 
	*/
	public int getCurrentrow(int pid);

	/**
	*
	* set the internal pointer to defined position
	* @param index
	* @return	int 
	* @throws PageException
	*/
	public boolean go(int index, int pid)throws PageException;
	
	/**
	 * @return returns if iterator is empty or not
	 */
	public boolean isEmpty();
	
	//public ArrayList column(String strColumn)throws PageException;
	
	//public String[] row(int number);
}