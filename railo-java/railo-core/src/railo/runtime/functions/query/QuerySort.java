/**
 * Implements the Cold Fusion Function querysetcell
 */
package railo.runtime.functions.query;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.DatabaseException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.List;
import railo.runtime.type.Query;

public final class QuerySort implements Function {
	public static boolean call(PageContext pc , Query query, String columnName) throws PageException {
		return call(pc,query,columnName,null);
	}
	public static boolean call(PageContext pc , Query query, String columnNames, String directions) throws PageException {
		// column names
		String[] arrColumnNames = List.trimItems(List.listToStringArray(columnNames, ','));
		int[] dirs = new int[arrColumnNames.length];
		
		// directions
		if(!StringUtil.isEmpty(directions)) {
			String[] arrDirections = List.trimItems(List.listToStringArray(directions, ','));
			if(arrColumnNames.length!=arrDirections.length)throw new DatabaseException("column names and directions has not the same count",null,null,null);
			
			String direction;
			for(int i=0;i<dirs.length;i++){
				direction=arrDirections[i].toLowerCase();
				dirs[i]=0;
				if(direction.equals("asc"))dirs[i]=Query.ORDER_ASC;
				else if(direction.equals("desc"))dirs[i]=Query.ORDER_DESC;
				else {		
					throw new DatabaseException("argument direction of function querySort must be \"asc\" or \"desc\", now \""+direction+"\"",null,null,null);
				}
			}
		}
		else {
			for(int i=0;i<dirs.length;i++){
				dirs[i]=Query.ORDER_ASC;
			}
		}
		
		
		for(int i=arrColumnNames.length-1;i>=0;i--)
			query.sort(KeyImpl.getInstance(arrColumnNames[i]),dirs[i]);
		
		
		
		return true;		
	}
}