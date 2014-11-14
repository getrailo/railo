/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
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