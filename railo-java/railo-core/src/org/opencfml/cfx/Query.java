package org.opencfml.cfx;

import java.sql.ResultSet;




/**
 * Alternative Implementation of Jeremy Allaire's Query Interface
 */
public interface Query extends ResultSet {

	/**
	 * @return adds a row to resultset
	 */
	public int addRow();

	/**
	 * returns index of a columnName
	 * @param coulmnName column name to get index for
	 * @return index of a columnName
	 */
	public int getColumnIndex(String coulmnName);

	/**
	 * @return All column Names of resultset as string
	 * FUTURE change to getColumnNamesAsString();
	 */
	public String[] getColumns();
	
	// FUTURE public String[] getColumnNamesAsString();
	// FUTURE public Collection.Key[] getColumnNames();


	/**
	 * returns one field of a Query as String
	 * @param row
	 * @param col
	 * @return data from query object
	 * @throws IndexOutOfBoundsException
	 */
	public String getData(int row, int col) throws IndexOutOfBoundsException;

	/**
	 * @return returns name of the query
	 */
	public String getName();

	/**
	 * @return returns row count
	 */
	public int getRowCount();

	/**
	 * sets value at a defined position in Query
	 * @param row
	 * @param col
	 * @param value
	 * @throws IndexOutOfBoundsException
	 */
	public void setData(int row, int col, String value)
			throws IndexOutOfBoundsException ;

}