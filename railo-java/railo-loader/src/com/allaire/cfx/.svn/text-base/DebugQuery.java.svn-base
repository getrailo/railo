package com.allaire.cfx;

import railo.loader.engine.CFMLEngineFactory;
import railo.runtime.cfx.QueryWrap;

/**
 * Implementation of the DebugQuery
 */
public final class DebugQuery extends QueryWrap {

	/**
	 * Constructor of the DebugQuery
	 * @param name
	 * @param columns
	 * @param data
	 * @throws IllegalArgumentException
	 */
	public DebugQuery(String name, String[] columns, String[][] data) throws IllegalArgumentException  {
		super(toQuery(name, columns, data),name);
	}
	
	/**
	 * Constructor of the DebugQuery
	 * @param name
	 * @param columns
	 * @throws IllegalArgumentException
	 */
	public DebugQuery(String name, String[] columns) throws IllegalArgumentException {
		super(toQuery(name, columns,0),name);
	}

	private static railo.runtime.type.Query toQuery(String name, String[] columns, String[][] data) {
        
        railo.runtime.type.Query query=toQuery(name, columns,data.length);
		
		for(int row=0;row<data.length;row++) {
			int len=data[row].length>columns.length?columns.length:data[row].length;
			for(int col=0;col<len;col++) {
				try {
					query.setAt(columns[col],row+1,data[row][col]);
				} catch (Exception e) {}
			}
		}
		return query;
	}
	private static railo.runtime.type.Query toQuery(String name, String[] columns, int rows) {
        return CFMLEngineFactory.getInstance().getCreationUtil().createQuery(columns,rows,name);
	}
}