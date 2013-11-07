package railo.runtime.cache.tag.udf;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import railo.print;
import railo.runtime.PageContext;
import railo.runtime.converter.ConverterException;
import railo.runtime.converter.ConverterSupport;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.text.xml.struct.XMLStruct;
import railo.runtime.type.Array;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Query;
import railo.runtime.type.SimpleValue;
import railo.runtime.type.Struct;

public class UDFArgConverter {

	public static String serialize(Object o)  {
		return serialize(o,new HashSet<Object>());
	}
	
	
	private static String serialize(Object o,Set<Object> done)  {
		
		if(o==null) return "null";
		Object raw=toRaw(o);
		
		if(done.contains(raw)) return "parent reference";
		done.add(raw);
		Collection c=null;
		try{
			if((c=Caster.toCollection(o,null))!=null) {
				done.add(c);
				return serializeCollection(c,done);
			}
			if(o instanceof SimpleValue || o instanceof Number || o instanceof Boolean)
				return Caster.toString(o,"");
			
			return o.toString();
		}
		finally {
			if(c!=null) done.remove(c);
			done.remove(raw);
		}
	}

	private static Object toRaw(Object o) {
		if(o instanceof XMLStruct)return ((XMLStruct)o).toNode();
		return o;
	}


	private static String serializeCollection(Collection coll, Set<Object> done) {
		if(coll instanceof Query) {
			Query qry=(Query) coll;
			StringBuilder sb=new StringBuilder();
			
			Iterator<Key> it = qry.keyIterator();
			Key k;
			sb.append("{");
			boolean oDoIt=false;
			int len=qry.getRecordcount();
			while(it.hasNext()) {
				k = it.next();
			    if(oDoIt)sb.append(',');
			    oDoIt=true;
			    sb.append(k.getLowerString());
	            sb.append(":[");
				boolean doIt=false;
				for(int y=1;y<=len;y++) {
				    if(doIt)sb.append(',');
				    doIt=true;
				    try {
						sb.append(serialize(qry.getAt(k,y),done));
					} catch (PageException e) {
						sb.append(serialize(e.getMessage(),done));
					}
				}
				sb.append(']');
			}
			
			sb.append('}');
			return sb.toString();
		}
		
		
		StringBuilder sb=new StringBuilder("{");
		Iterator<Entry<Key, Object>> it = coll.entryIterator();
		Entry<Key, Object> e;
		boolean notFirst=false;
		while(it.hasNext()) {
			if(notFirst)sb.append(",");
			e=it.next();
			sb.append(e.getKey().getLowerString());
			sb.append(":");
			print.e(e.getValue().getClass().getName());
			sb.append(serialize(e.getValue(),done));
			notFirst=true;
		}
		
		return sb.append("}").toString();
	}
}
