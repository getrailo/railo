package railo.runtime.functions.struct;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

public class StructKeyTranslate extends BIF {
	
	private static final long serialVersionUID = -7978129950865681102L;

	public static double call(PageContext pc ,Struct sct) throws PageException {
		return call(pc, sct,false,false);
    }
	public static double call(PageContext pc ,Struct sct,boolean deepTranslation) throws PageException {
		return call(pc, sct,deepTranslation,false);
    }
	public static double call(PageContext pc ,Struct sct,boolean deepTranslation,boolean leaveOriginalKey) throws PageException {
		return translate(sct, deepTranslation,leaveOriginalKey);
    }
	
	public static int translate(Collection coll,boolean deep,boolean leaveOrg) throws PageException {
		Iterator<Entry<Key, Object>> it = coll.entryIterator();
		Entry<Key, Object> e;
		boolean isStruct=coll instanceof Struct;
		String key;
		int index;
		int count=0;
		while(it.hasNext()){
			e = it.next();
			key=e.getKey().getString();
			if(deep)count+=translate(e.getValue(),leaveOrg);
			if(isStruct && (index=key.indexOf('.'))!=-1){
				count++;
				translate(index,e.getKey(),key,coll,leaveOrg);
			}
		}
		return count;
    }

	private static int translate(Object value,boolean leaveOrg) throws PageException {
		if(value instanceof Collection)
			return translate((Collection)value, true,leaveOrg);
		if(value instanceof List)
			return translate((List<?>)value, leaveOrg);
		if(value instanceof Map)
			return translate((Map<?,?>)value, leaveOrg);
		if(Decision.isArray(value))
			return translate(Caster.toNativeArray(value), leaveOrg);
		return 0;
	}

	private static int translate(List<?> list,boolean leaveOrg) throws PageException {
		Iterator<?> it = list.iterator();
		int count=0;
		while(it.hasNext()){
			count+=translate(it.next(),leaveOrg);
		}
		return count;
	}

	private static int translate(Map<?,?> map,boolean leaveOrg) throws PageException {
		Iterator<?> it = map.entrySet().iterator();
		int count=0;
		while(it.hasNext()){
			count+=translate(((Map.Entry<?,?>)it.next()).getValue(),leaveOrg);
		}
		return count;
	}

	private static int translate(Object[] arr,boolean leaveOrg) throws PageException {
		int count=0;
		for(int i=0;i<arr.length;i++){
			count+=translate(arr[i],leaveOrg);
		}
		return count;
	}

	private static void translate(int index, Key key, String strKey, Collection coll,boolean leaveOrg) throws PageException {
		String left;
		Object value=leaveOrg?coll.get(key):coll.remove(key);
		do{
			left=strKey.substring(0,index);
			strKey=strKey.substring(index+1);
			coll=touch(coll,KeyImpl.init(left));
			
		}
		while((index=strKey.indexOf('.'))!=-1);
		coll.set(KeyImpl.init(strKey), value);
	}

	private static Collection touch(Collection coll, Key key) throws PageException {
		Object obj = coll.get(key,null);
		if(obj instanceof Collection) return (Collection) obj;
		if(Decision.isCastableToStruct(obj))
			return Caster.toStruct(obj);
		coll.set(key, coll=new StructImpl());
		return coll;
		
	}
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==3) return call(pc,Caster.toStruct(args[0]),Caster.toBooleanValue(args[1]),Caster.toBooleanValue(args[2]));
		if(args.length==2) return call(pc,Caster.toStruct(args[0]),Caster.toBooleanValue(args[1]));
		return call(pc,Caster.toStruct(args[0]));
	}
	
}
