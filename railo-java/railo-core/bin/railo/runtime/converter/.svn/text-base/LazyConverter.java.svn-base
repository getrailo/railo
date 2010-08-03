package railo.runtime.converter;

import java.util.Iterator;

import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.Collection;
import railo.runtime.type.SimpleValue;
import railo.runtime.type.Struct;

public class LazyConverter {
	 public static String serialize(Object o)  {
		 if(o==null) return "null";
		 if(o instanceof Array)return serializeArray((Array)o);
		 if(o instanceof Struct)return serializeStruct((Struct)o);
		 if(o instanceof SimpleValue || o instanceof Number || o instanceof Boolean)return Caster.toString(o,null);
		 return o.toString();
	 }

	private static String serializeStruct(Struct struct) {
		StringBuffer sb=new StringBuffer("{");
		Iterator it = struct.keyIterator();
		Object key;
		boolean notFirst=false;
		while(it.hasNext()) {
			if(notFirst)sb.append(", ");
			key=it.next();
			sb.append(key);
			sb.append("={");
			if(key instanceof String)
				sb.append(serialize(struct.get((String)key,null)));
			else
				sb.append(serialize(struct.get((Collection.Key)key,null)));
			sb.append("}");
			notFirst=true;
		}
		
		return sb.append("}").toString();
	}

	private static String serializeArray(Array array) {
		StringBuffer sb=new StringBuffer("[");
		int len=array.size();
		for(int i=1;i<=len;i++){
			if(i>1)sb.append(", ");
			sb.append(serialize(array.get(i,null)));
		}
		return sb.append("]").toString();
	}
}
