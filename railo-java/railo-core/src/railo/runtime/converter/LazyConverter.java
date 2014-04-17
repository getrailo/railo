package railo.runtime.converter;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import railo.runtime.PageContext;
import railo.runtime.op.Caster;
import railo.runtime.text.xml.struct.XMLStruct;
import railo.runtime.type.Array;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.SimpleValue;
import railo.runtime.type.Struct;

public class LazyConverter extends ConverterSupport {

	public static String serialize(Object o)  {
		return serialize(o,new HashSet<Object>());
	}

	@Override
	public void writeOut(PageContext pc, Object source, Writer writer) throws ConverterException, IOException {
		writer.write(serialize(source));
		writer.flush();
	}
	
	
	private static String serialize(Object o,Set<Object> done)  {
		
		if(o==null) return "null";
		Object raw=toRaw(o);
		
		if(done.contains(raw)) return "parent reference";
		done.add(raw);
		try{
			 if(o instanceof Array)return serializeArray((Array)o,done);
			 if(o instanceof Struct){
				 return serializeStruct((Struct)o,done);
			 }
			 if(o instanceof SimpleValue || o instanceof Number || o instanceof Boolean)return Caster.toString(o,null);
			 return o.toString();
		}
		finally {
			done.remove(raw);
		}
	}

	public static Object toRaw(Object o) {
		if(o instanceof XMLStruct)return ((XMLStruct)o).toNode();
		return o;
	}


	private static String serializeStruct(Struct struct, Set<Object> done) {
		StringBuilder sb=new StringBuilder("{");
		Iterator<Key> it = struct.keyIterator();
		Key key;
		boolean notFirst=false;
		while(it.hasNext()) {
			if(notFirst)sb.append(", ");
			key=it.next();
			sb.append(key);
			sb.append("={");
			sb.append(serialize(struct.get(key,null),done));
			sb.append("}");
			notFirst=true;
		}
		
		return sb.append("}").toString();
	}

	private static String serializeArray(Array array, Set<Object> done) {
		StringBuilder sb=new StringBuilder("[");
		int len=array.size();
		for(int i=1;i<=len;i++){
			if(i>1)sb.append(", ");
			sb.append(serialize(array.get(i,null),done));
		}
		return sb.append("]").toString();
	}
}
