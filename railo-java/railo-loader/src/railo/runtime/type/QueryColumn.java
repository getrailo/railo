package railo.runtime.type;

import railo.runtime.exp.PageException;
import railo.runtime.op.Castable;
import railo.runtime.type.ref.Reference;

/**
 * represent a Single column of a query object
 */
public interface QueryColumn extends Collection,Reference,Castable {
    
    /**
     * removes the value but dont the index
     * @param row
     * @return removed Object
     * @throws PageException
     */
    public Object remove(int row)throws PageException;
    
    /**
     * remove a row from query
     * @param row
     * @return removed value
     * @throws PageException
     */
    public Object removeRow(int row)throws PageException;
    
    /**
     * removes method with int as key
     * @param row
     * @return removed Object
     */
    public Object removeEL(int row);
    
	/**
	 * get method with a int as key
	 * @param row row to get value
	 * @return row value
	 * @throws PageException
	 */
	public Object get(int row) throws PageException;

	/**
	 * return the value in this row (can be null), when row number is invalid the default value is returned
	 * 
	 * @param row row to get value
	 * @return row value
	 */
	public Object get(int row, Object defaultValue);

	/**
	 * set method with a int as key
	 * @param row row to set
	 * @param value value to set
	 * @return setted value
	 * @throws PageException
	 */
	public Object set(int row, Object value) throws PageException;

	/**
	 * adds a value to the column
	 * @param value value to add
	 */
	public void add(Object value);
	

	/**
	 * setExpressionLess method with a int as key
	 * @param row row to set
	 * @param value value to set
	 * @return setted value
	 */
	public Object setEL(int row, Object value);


	/**
	 * @param count adds count row to the column
	 */
	public void addRow(int count);

	/**
	 * @return returns the type of the Column (java.sql.Types.XYZ)
	 */
	public int getType();
	
	/**
	 * @return returns the type of the Column as String
	 */
	public String getTypeAsString();

    /**
     * cuts row to defined size
     * @param maxrows
     */
    public void cutRowsTo(int maxrows);
	
}