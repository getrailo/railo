package railo.runtime.tag.util;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import railo.commons.lang.StringUtil;
import railo.runtime.db.SQL;
import railo.runtime.db.SQLCaster;
import railo.runtime.db.SQLImpl;
import railo.runtime.db.SQLItem;
import railo.runtime.db.SQLItemImpl;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.DatabaseException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.type.Array;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Struct;
import railo.runtime.type.util.KeyConstants;

public class QueryParamConverter {

	public static SQL convert(String sql, Struct params) throws PageException{
		Iterator<Entry<Key, Object>> it = params.entryIterator();
		List<NamedSQLItem> namedItems=new ArrayList<NamedSQLItem>();
		Entry<Key, Object> e;
		while(it.hasNext()){
			e = it.next();
			namedItems.add(toNamedSQLItem(e.getKey().getString(),e.getValue()));
		}
		return convert(sql, new ArrayList<SQLItem>(), namedItems);
	}
	
	public static SQL convert(String sql, Array params) throws PageException{
		Iterator<Object> it = params.valueIterator();
		List<NamedSQLItem> namedItems=new ArrayList<NamedSQLItem>();
		List<SQLItem> items=new ArrayList<SQLItem>();
		Object value;
		SQLItem item;
		while(it.hasNext()){
			value = it.next();
			item=toSQLItem(value);
			if(item instanceof NamedSQLItem)
				namedItems.add((NamedSQLItem) item);
			else
				items.add(item);
		}
		return convert(sql, items, namedItems);
	}
	
	private static SQLItem toSQLItem(Object value) throws PageException {
		if(Decision.isStruct(value)) {
			Struct sct=(Struct) value;
			// name (optional)
			String name=null;
			Object oName=sct.get(KeyConstants._name,null);
			if(oName!=null) name=Caster.toString(oName);
			
			// value (required)
			value=sct.get(KeyConstants._value);
			
			if(StringUtil.isEmpty(name))
				return fill(new SQLItemImpl(value, Types.VARCHAR),sct);
			
			return fill(new NamedSQLItem(name, value, Types.VARCHAR),sct);
		}
		return new SQLItemImpl(value);
	}

	private static NamedSQLItem toNamedSQLItem(String name, Object value) throws PageException {
		if(Decision.isStruct(value)) {
			Struct sct=(Struct) value;
			// value (required)
			value=sct.get(KeyConstants._value);
			return (NamedSQLItem) fill(new NamedSQLItem(name, value, Types.VARCHAR),sct);
		}
		return new NamedSQLItem(name, value, Types.VARCHAR);
 	}
	
	private static SQLItem fill(SQLItem item,Struct sct) throws DatabaseException, PageException {
		// type (optional)
		Object oType=sct.get(KeyConstants._cfsqltype,null);
		if(oType==null)oType=sct.get(KeyConstants._sqltype,null);
		if(oType==null)oType=sct.get(KeyConstants._type,null);
		if(oType!=null) {
			item.setType(SQLCaster.toIntType(Caster.toString(oType)));
		}
		
		// nulls (optional)
		Object oNulls=sct.get(KeyConstants._nulls,null);
		if(oNulls!=null) {
			item.setNulls(Caster.toBooleanValue(oNulls));
		}
		
		// scale (optional)
		Object oScale=sct.get(KeyConstants._scale,null);
		if(oScale!=null) {
			item.setScale(Caster.toIntValue(oNulls));
		}
		
		/* list
		if(Caster.toBooleanValue(sct.get("list",null),false)) {
			String separator=Caster.toString(sct.get("separator",null),",");
			String v = Caster.toString(item.getValue());
	    	Array arr=null;
	    	if(StringUtil.isEmpty(v)){
	    		arr=new ArrayImpl();
	    		arr.append("");
	    	}
	    	else arr=ListUtil.listToArrayRemoveEmpty(v,separator);
			
			int len=arr.size();
			StringBuilder sb=new StringBuilder();
			for(int i=1;i<=len;i++) {
			    query.setParam(item.clone(check(arr.getE(i))));
		        if(i>1)sb.append(',');
		        sb.append('?');
			}
			write(sb.toString());
		}*/
		return item;
	}

	private static SQL convert(String sql, List<SQLItem> items, List<NamedSQLItem> namedItems) throws ApplicationException{
		//if(namedParams.size()==0) return new Pair<String, List<Param>>(sql,params);
		
		StringBuilder sb=new StringBuilder();
		int sqlLen=sql.length(), initialParamSize=items.size();
		char c,del=0;
		boolean inside=false;
		int qm=0,_qm=0;
		for(int i=0;i<sqlLen;i++){
			c=sql.charAt(i);
			
			if(c=='"' || c=='\'')	{
				if(inside) {
					if(c==del) {
						inside=false;
					}
				}
				else {
					del=c;
					inside=true;
				}
			}
			else {
				if(!inside && c=='?') {
					if(++_qm>initialParamSize) 
						throw new ApplicationException("there are more question marks in the SQL than params defined");
					qm++;
				}
				else if(!inside && c==':') {
					StringBuilder name=new StringBuilder();
					char cc;
					int y=i+1;
					for(;y<sqlLen;y++){
						cc=sql.charAt(y);
						if(isSpace(cc))break;
						name.append(cc);
					}
					if(name.length()>0) {
						i=y-1;
						c='?';
						
						SQLItem p = get(name.toString(),namedItems);
						items.add(qm, p);
						qm++;
					}
					
				}
			}
			sb.append(c);
		}
		
		return new SQLImpl(sb.toString(),items.toArray(new SQLItem[items.size()]));
	}

	
	private static boolean isSpace(char c) {
		return c==' ' || c=='\t' || c=='\n' || c=='\b';
	}

	private static SQLItem get(String name, List<NamedSQLItem> items) throws ApplicationException {
		Iterator<NamedSQLItem> it = items.iterator();
		NamedSQLItem item;
		while(it.hasNext()){
			item=it.next();
			if(item.name.equalsIgnoreCase(name)) return item;
		}
		throw new ApplicationException("no param with name ["+name+"] found");
	}

	private static class NamedSQLItem extends SQLItemImpl {
		public final String name;

		public NamedSQLItem(String name, Object value, int type){
			super(value,type);
			this.name=name;
		}
		
		public String toString(){
			return "{name:"+name+";"+super.toString()+"}";
		}
	}
	
	/*
	 
	public static void main(String[] args) throws PageException {
		List<SQLItem> one=new ArrayList<SQLItem>();
		one.add(new SQLItemImpl("aaa",1));
		one.add(new SQLItemImpl("bbb",1));
		
		List<NamedSQLItem> two=new ArrayList<NamedSQLItem>();
		two.add(new NamedSQLItem("susi","sorglos",1));
		two.add(new NamedSQLItem("peter","Petrus",1));
		
		SQL sql = convert(
				"select ? as x, 'aa:a' as x from test where a=:susi and b=:peter and c=? and d=:susi",
				one,
				two);
		
		print.e(sql);

		// array with simple values
		Array arr=new ArrayImpl();
		arr.appendEL("aaa");
		arr.appendEL("bbb");
		sql = convert(
				"select * from test where a=? and b=?",
				arr);
		print.e(sql);
		
		// array with complex values
		arr=new ArrayImpl();
		Struct val1=new StructImpl();
		val1.set("value", "Susi Sorglos");
		Struct val2=new StructImpl();
		val2.set("value", "123");
		val2.set("type", "integer");
		arr.append(val1);
		arr.append(val2);
		sql = convert(
				"select * from test where a=? and b=?",
				arr);
		print.e(sql);
		
		// array with mixed values
		arr.appendEL("ccc");
		arr.appendEL("ddd");
		sql = convert(
				"select * from test where a=? and b=? and c=? and d=?",
				arr);
		print.e(sql);
		
		// array mixed with named values
		Struct val3=new StructImpl();
		val3.set("value", "456");
		val3.set("type", "integer");
		val3.set("name", "susi");
		arr.append(val3);
		sql = convert(
				"select :susi as name from test where a=? and b=? and c=? and d=?",
				arr);
		print.e(sql);
		
		
		// struct with simple values
		Struct sct=new StructImpl();
		sct.set("abc", "Sorglos");
		sql = convert(
				"select * from test where a=:abc",
				sct);
		print.e(sql);
		
		// struct with mixed values
		sct.set("peter", val1);
		sct.set("susi", val3);
		sql = convert(
				"select :peter as p, :susi as s from test where a=:abc",
				sct);
		print.e(sql);
		
		
	}*/
	
}
