package railo.runtime.functions.query;

import java.util.Iterator;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.DatabaseException;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.type.Array;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.List;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.util.QueryUtil;

/**
 * Implements the Cold Fusion Function querynew
 */
public final class QueryNew implements Function {
	public static railo.runtime.type.Query call(PageContext pc , String columnList) throws DatabaseException {
	    return new QueryImpl(List.listToArrayTrim(columnList,","),0,"query");
	}
	public static railo.runtime.type.Query call(PageContext pc , String columnList, String columnTypeList) throws PageException {
		if(StringUtil.isEmpty(columnTypeList)) return call(pc, columnList);
		return new QueryImpl(List.listToArrayTrim(columnList,","),List.listToArrayTrim(columnTypeList,","),0,"query");
	}
	
	public static railo.runtime.type.Query call(PageContext pc , String strColumnList, String strColumnTypeList, Object data) throws PageException {
		Array columnList = List.listToArrayTrim(strColumnList,",");
		railo.runtime.type.Query qry;
		if(StringUtil.isEmpty(strColumnTypeList))
			qry= new QueryImpl(columnList,0,"query");
		else
			qry= new QueryImpl(columnList,List.listToArrayTrim(strColumnTypeList,","),0,"query");
		
		if(data==null) return qry;
		return populate(pc, qry, data);	
	}
	
	private static Query populate(PageContext pc, Query qry,Object data) throws PageException {
		if(Decision.isArray(data))
			return _populate(pc,qry,Caster.toArray(data));
		else if(Decision.isStruct(data))
			return _populate(pc,qry,Caster.toStruct(data));
		else 
			throw new FunctionException(pc, "QueryNew", 3, "data", "the date must be defined as array of structs , array of arrays or struct of arrays");
	}
	
	private static Query _populate(PageContext pc, Query qry,Struct data) throws PageException {
		Key[] keys = data.keys();
		for(int i=0;i<keys.length;i++){
			if(qry.getColumn(keys[i],null)!=null) 
				 populateColumn(qry,keys[i],Caster.toArray(data.get(keys[i])));
		}
		return qry; 
	}
	
	private static void populateColumn(Query qry, Key column, Array data) throws PageException {
		Iterator<?> it = data.valueIterator();
		int row=0;
		while(it.hasNext()){
			row++;
			if(row>qry.getRecordcount()) qry.addRow();
			qry.setAt(column, row, it.next());
		}
	}
	private static Query _populate(PageContext pc, Query qry,Array data) throws PageException {
		Iterator<?> it = data.valueIterator();
		Object o;
		while(it.hasNext()){
			o=it.next();
			qry.addRow();
			if(Decision.isStruct(o))populateRow(qry,Caster.toStruct(o));
			else if(Decision.isArray(o))populateRow(qry,Caster.toArray(o));
			else
				throw new FunctionException(pc, "QueryNew", 3, "data", "the date must be defined as array of structs , array of arrays or struct of arrays");
		}
		return qry;
	}
	
	public static void populateRow(Query qry, Struct data) throws PageException {
		Key[] columns = QueryUtil.getColumnNames(qry);
		int row=qry.getRecordcount();
		Object value;
		for(int i=0;i<columns.length;i++){
			value=data.get(columns[i],null);
			if(value!=null) qry.setAt(columns[i], row, value);
		}
		
	}
	protected static void populateRow(Query qry, Array data) throws PageException {
		Iterator<?> it = data.valueIterator();
		Key[] columns = QueryUtil.getColumnNames(qry);
		int row=qry.getRecordcount();
		int index=-1;
		while(it.hasNext()){
			index++;
			if(index>=columns.length) break;
			qry.setAt(columns[index], row, it.next());
		}
	}
}  