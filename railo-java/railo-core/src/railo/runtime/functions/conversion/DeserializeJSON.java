package railo.runtime.functions.conversion;

import java.util.Iterator;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.interpreter.JSONExpressionInterpreter;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.util.CollectionUtil;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.type.util.ListUtil;

/**
 * Decodes Binary Data that are encoded as String
 */
public final class DeserializeJSON implements Function {

	private static final Key ROWCOUNT = KeyImpl.intern("ROWCOUNT");

	public static Object call(PageContext pc, String JSONVar) throws PageException {
	    return call(pc,JSONVar,true);
	}
	public static Object call(PageContext pc, String JSONVar,boolean strictMapping) throws PageException {
		Object result = new JSONExpressionInterpreter().interpret(pc, JSONVar);
		if(!strictMapping) return toQuery(result);
		return result;
	}

	//{"COLUMNS":["AAA","BBB"],"DATA":[["a","b"],["c","d"]]}
	//{"ROWCOUNT":2,"COLUMNS":["AAA","BBB"],"DATA":{"aaa":["a","c"],"bbb":["b","d"]}}
	private static Object toQuery(Object obj) throws PageException {
		if(obj instanceof Struct) {
			Struct sct=(Struct) obj;
			Key[] keys = CollectionUtil.keys(sct);
			
			// Columns
			Key[] columns = null;
			if(contains(keys,KeyConstants._COLUMNS)) 
				columns = toColumns(sct.get(KeyConstants._COLUMNS,null));
			else if(contains(keys,KeyConstants._COLUMNLIST)) 
				columns = toColumnlist(sct.get(KeyConstants._COLUMNLIST,null));
			
			// rowcount
			int rowcount = -1;
			if(contains(keys,ROWCOUNT))
				rowcount = toRowCount(sct.get(ROWCOUNT,null));
			else if(contains(keys,KeyConstants._RECORDCOUNT))
				rowcount = toRowCount(sct.get(KeyConstants._RECORDCOUNT,null));
				
			
			if(columns!=null) {
				if(keys.length==2 && contains(keys,KeyConstants._DATA)) {
					
					Array[] data = toData(sct.get(KeyConstants._DATA,null),columns);
					if(data!=null) {
						return new QueryImpl(columns,data,"query");
					}
				}
				
				else if(keys.length==3 && rowcount!=-1 && contains(keys,KeyConstants._DATA)) {
					Array[] data = toData(sct.get(KeyConstants._DATA,null),columns,rowcount);
					if(data!=null) {
						return new QueryImpl(columns,data,"query");
					}
				}
			}
			return toQuery(sct,keys);
		}
		/*else if(obj instanceof Query) {
			return toQuery((Query) obj);
		}*/	
		else if(obj instanceof Collection) {
			Collection coll=(Collection) obj;
			return toQuery(coll, CollectionUtil.keys(coll));
		}
		
		return obj;
		
	}

	/*private static Object toQuery(Query qry) throws DatabaseException {
		int rows=qry.getRecordcount();
		String[] columns = qry.getColumns();
		Object src,trg;
		for(int row=1;row<=rows;row++) {
			for(int col=0;col<columns.length;col++) {
				trg=toQuery(src=qry.getAt(columns[col], row, null));
				if(src!=trg) qry.setAtEL(columns[col], row, trg);
			}
		}
		return qry;
	}*/
	
	private static Collection toQuery(Collection coll, Key[] keys) throws PageException {
		Object src,trg;
		for(int i=0;i<keys.length;i++) {
			trg=toQuery(src=coll.get(keys[i],null));
			if(src!=trg) coll.setEL(keys[i], trg);
		}
		return coll;
	}
	
	private static int toRowCount(Object obj) {
		return Caster.toIntValue(obj,-1);
	}

	private static Array[] toData(Object obj, Key[] columns, int rowcount) throws PageException {
		if(columns==null || rowcount==-1) return null;

		Struct sct = Caster.toStruct(obj,null,false);
		if(sct!=null && sct.size()==columns.length) {
			Array[] datas=new Array[columns.length];
			Array col;
			int colLen=-1;
			for(int i=0;i<columns.length;i++) {
				col=Caster.toArray(sct.get(columns[i],null),null);
				if(col==null || colLen!=-1 && colLen!=col.size()) return null;
				datas[i]=(Array) toQuery(col,CollectionUtil.keys(col));
				colLen=col.size();
			}
			return datas;
		}
		return null;
	}
	
	private static Array[] toData(Object obj,Key[] columns) throws PageException {
		if(columns==null) return null;
		
		Array arr = Caster.toArray(obj,null);
		if(arr!=null) {
			Array[] datas=new Array[columns.length];
			for(int i=0;i<datas.length;i++) {
				datas[i]=new ArrayImpl();
			}
			
			Array data;
			Iterator<Object> it = arr.valueIterator();
			while(it.hasNext()) {
				data=Caster.toArray(it.next(),null);
				if(data==null || data.size()!=datas.length) return null;
				for(int i=0;i<datas.length;i++) {
					datas[i].appendEL(toQuery(data.get(i+1,null)));
				}
			}
			return datas;
		}
		return null;
	}

	
	private static Key[] toColumns(Object obj) {
		Array arr = Caster.toArray(obj,null);
		if(arr!=null) {
			Key[] columns=new Key[arr.size()];
			String column;
			int index=0;
			Iterator<Object> it = arr.valueIterator();
			while(it.hasNext()) {
				column=Caster.toString(it.next(),null);
				if(StringUtil.isEmpty(column)) return null;
				columns[index++]=KeyImpl.getInstance(column);
			}
			return columns;
		}
		return null;
	}
	
	private static Key[] toColumnlist(Object obj) throws PageException {
		String list = Caster.toString(obj,null);
		if(StringUtil.isEmpty(list)) return null;
		return toColumns(ListUtil.trimItems(ListUtil.listToArrayRemoveEmpty(list, ',')));
	}
	
	
	/*private static boolean contains(Key[] haystack, Key[] needle) {
		Key h;
		outer:for(int i=0;i<haystack.length;i++) {
			h=haystack[i];
			for(int y=0;y<needle.length;y++) {
				if(h.equalsIgnoreCase(needle[y])) continue outer;
			}
			return false;
		}
		return true;
	}*/
	
	
	private static boolean contains(Key[] haystack, Key needle) {
		for(int i=0;i<haystack.length;i++) {
			if(haystack[i].equalsIgnoreCase(needle)) return true;
		}
		return false;
	}
}