package railo.runtime.functions.query;

import java.util.Iterator;
import java.util.Map.Entry;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.DatabaseException;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.util.ListUtil;
import railo.runtime.type.util.QueryUtil;

/**
 * Implements the CFML Function querynew
 */
public final class QueryNew extends BIF {

	private static final long serialVersionUID = -4313766961671090938L;
	
	public static railo.runtime.type.Query call(PageContext pc , String columnList) throws DatabaseException {
	    return new QueryImpl(ListUtil.listToArrayTrim(columnList,","),0,"query");
	}
	public static railo.runtime.type.Query call(PageContext pc , String columnList, String columnTypeList) throws PageException {
		if(StringUtil.isEmpty(columnTypeList)) return call(pc, columnList);
		return new QueryImpl(ListUtil.listToArrayTrim(columnList,","),ListUtil.listToArrayTrim(columnTypeList,","),0,"query");
	}
	
	public static railo.runtime.type.Query call(PageContext pc , String strColumnList, String strColumnTypeList, Object data) throws PageException {
		Array columnList = ListUtil.listToArrayTrim(strColumnList,",");
		railo.runtime.type.Query qry;
		if(StringUtil.isEmpty(strColumnTypeList))
			qry= new QueryImpl(columnList,0,"query");
		else
			qry= new QueryImpl(columnList,ListUtil.listToArrayTrim(strColumnTypeList,","),0,"query");
		
		if(data==null) return qry;
		return populate(pc, qry, data);	
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==1)return call(pc,Caster.toString(args[0]));
		if(args.length==2)return call(pc,Caster.toString(args[0]),Caster.toString(args[1]));
		return call(pc,Caster.toString(args[0]),Caster.toString(args[1]),args[2]);
	}
	
	public static Query populate(PageContext pc, Query qry,Object data) throws PageException {
		if(Decision.isArray(data))
			return _populate(pc,qry,Caster.toArray(data));
		else if(Decision.isStruct(data))
			return _populate(pc,qry,Caster.toStruct(data));
		else 
			throw new FunctionException(pc, "QueryNew", 3, "data", "the date must be defined as array of structs , array of arrays or struct of arrays");
	}
	
	private static Query _populate(PageContext pc, Query qry,Struct data) throws PageException {
		Iterator<Entry<Key, Object>> it = data.entryIterator();
		Entry<Key, Object> e;
		Object v;
		Array arr;
		int rows = qry.getRecordcount();
		while(it.hasNext()){
			e = it.next();
			if(qry.getColumn(e.getKey(),null)!=null) {
				v=e.getValue();
				arr = Caster.toArray(v,null);
				if(arr==null) arr=new ArrayImpl(new Object[]{v});
				populateColumn(qry,e.getKey(),arr,rows);
			}
		}
		return qry; 
	}
	
	private static void populateColumn(Query qry, Key column, Array data,int rows) throws PageException {
		Iterator<?> it = data.valueIterator();
		int row=rows;
		while(it.hasNext()){
			row++;
			if(row>qry.getRecordcount()) qry.addRow();
			qry.setAt(column, row, it.next());
		}
	}
	private static Query _populate(PageContext pc, Query qry,Array data) throws PageException {
		/*
		 * 3 types of structures are supported
		 * array - ["Urs","Weber"]
		 * array of struct - [{firstname="Urs",lastname="Weber"},{firstname="Peter",lastname="Mueller"}]
		 * array of array - [["Urs","Weber"],["Peter","Mueller"]]
		 */
		
		// check if the array only contains simple values or mixed
		Iterator<?> it = data.valueIterator();
		Object o;
		boolean hasSimpleValues=false;
		while(it.hasNext()){
			o=it.next();
			if(!Decision.isStruct(o) && !Decision.isArray(o)) hasSimpleValues=true;
		}
		
		
		if(hasSimpleValues) {
			qry.addRow();
			populateRow(qry, data);
		}
		else {
			it = data.valueIterator();
			while(it.hasNext()){
				o=it.next();
				qry.addRow();
				if(Decision.isStruct(o))populateRow(qry,Caster.toStruct(o));
				else if(Decision.isArray(o))populateRow(qry,Caster.toArray(o));
				else {
					populateRow(qry,new ArrayImpl(new Object[]{o}));
				}
			}
		}
		return qry;
	}
	
	private static void populateRow(Query qry, Struct data) throws PageException {
		Key[] columns = QueryUtil.getColumnNames(qry);
		int row=qry.getRecordcount();
		Object value;
		for(int i=0;i<columns.length;i++){
			value=data.get(columns[i],null);
			if(value!=null) qry.setAt(columns[i], row, value);
		}
		
	}
	private static void populateRow(Query qry, Array data) throws PageException {
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