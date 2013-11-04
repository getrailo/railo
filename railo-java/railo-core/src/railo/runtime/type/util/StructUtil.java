package railo.runtime.type.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import railo.commons.lang.SizeOf;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.dump.DumpUtil;
import railo.runtime.dump.SimpleDumpData;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Duplicator;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.comparator.TextComparator;

/**
 * 
 */
public final class StructUtil {

    /**
     * copy data from source struct to target struct
     * @param source 
     * @param target 
     * @param overwrite overwrite data if exist in target
     */
    public static void copy(Struct source, Struct target, boolean overwrite) {
    	Iterator<Entry<Key, Object>> it = source.entryIterator();
    	Entry<Key, Object> e;
        while(it.hasNext()) {
        	e = it.next();
            if(overwrite || !target.containsKey(e.getKey())) 
                target.setEL(e.getKey(),e.getValue());
        }
    }

    public static railo.runtime.type.Collection.Key[] toCollectionKeys(String[] skeys) {
    	railo.runtime.type.Collection.Key[] keys = new railo.runtime.type.Collection.Key[skeys.length];
		for(int i=0;i<keys.length;i++) {
			keys[i]=KeyImpl.init(skeys[i]);
		}
	    return keys;
    }
    
	/**
	 * @param sct
	 * @return
	 */
	public static Struct duplicate(Struct sct,boolean deepCopy) {

		Struct rtn=new StructImpl();
		//railo.runtime.type.Collection.Key[] keys=sct.keys();
		//railo.runtime.type.Collection.Key key;
		Iterator<Entry<Key, Object>> it = sct.entryIterator();
    	Entry<Key, Object> e;
        while(it.hasNext()) {
        	e=it.next();
			rtn.setEL(e.getKey(),Duplicator.duplicate(e.getValue(),deepCopy));
		}
		return rtn;
	}

	public static void putAll(Struct struct, Map map) {
		Iterator it = map.entrySet().iterator();
		Map.Entry entry;
		while(it.hasNext()) {
			entry=(Entry) it.next();
			struct.setEL(KeyImpl.toKey(entry.getKey(),null), entry.getValue());
		}
	}

	public static Set<Entry<String, Object>> entrySet(Struct sct) {
		Iterator<Entry<Key, Object>> it = sct.entryIterator();
		Entry<Key, Object> e;
		HashSet<Entry<String, Object>> set=new HashSet<Entry<String, Object>>();
		while(it.hasNext()){
			e= it.next();
			set.add(new StructMapEntry(sct,e.getKey(),e.getValue()));
		}
		return set;
	}
	
	public static Set<String> keySet(Struct sct) {
		Iterator<Key> it = sct.keyIterator();
		Set<String> set=new HashSet<String>();
		while(it.hasNext()){
			set.add(it.next().getString());
		}
		return set;
	}

	
	public static DumpTable toDumpTable(Struct sct,String title,PageContext pageContext, int maxlevel, DumpProperties dp) {
		Key[] keys = order(sct,CollectionUtil.keys(sct));
		DumpTable table = new DumpTable("struct","#9999ff","#ccccff","#000000");// "#9999ff","#ccccff","#000000"

		int maxkeys=dp.getMaxKeys();
		if(maxkeys < sct.size()) {
			table.setComment("Entries: "+sct.size() + " (showing top " + maxkeys + ")");
		}
		else if(sct.size()>10 && dp.getMetainfo()) {
			table.setComment("Entries: "+sct.size());
		}
		if(!StringUtil.isEmpty(title))table.setTitle(title);
		maxlevel--;
		int index=0;
		for(int i=0;i<keys.length;i++) {
			if(DumpUtil.keyValid(dp,maxlevel,keys[i])){
				if(maxkeys<=index++)break;
				table.appendRow(1,
						new SimpleDumpData(keys[i].toString()),
						DumpUtil.toDumpData(sct.get(keys[i],null), 
						pageContext,maxlevel,dp));
			}
		}
		return table;
	}
	
	

	private static Key[] order(Struct sct, Key[] keys) {
		if(sct instanceof StructImpl && ((StructImpl)sct).getType()==Struct.TYPE_LINKED) return keys;
		
		TextComparator comp=new TextComparator(true,true);
		Arrays.sort(keys,comp);
		return keys;
	}

	/**
	 * create a value return value out of a struct
	 * @param sct
	 * @return
	 */
	public static java.util.Collection<?> values(Struct sct) {
		ArrayList<Object> arr = new ArrayList<Object>();
		//Key[] keys = sct.keys();
		Iterator<Object> it = sct.valueIterator();
		while(it.hasNext()) {
			arr.add(it.next());
		}
		return arr;
	}

	public static Struct copyToStruct(Map map) throws PageException {
        Struct sct = new StructImpl();
        Iterator it=map.entrySet().iterator();
        Map.Entry entry;
        while(it.hasNext()) {
            entry=(Entry) it.next();
            sct.setEL(Caster.toString(entry.getKey()),entry.getValue());
        }
        return sct;
	}

	/**
	 * return the size of given struct, size of values + keys
	 * @param sct
	 * @return
	 */
	public static long sizeOf(Struct sct) {
		Iterator<Entry<Key, Object>> it = sct.entryIterator();
		Entry<Key, Object> e;
		long size = 0;
		while(it.hasNext()) {
			e = it.next();
			size+=SizeOf.size(e.getKey());
			size+=SizeOf.size(e.getValue());
		}
		return size;
	}

	public static void setELIgnoreWhenNull(Struct sct, String key, Object value) {
		setELIgnoreWhenNull(sct, KeyImpl.init(key), value);
	}
	public static void setELIgnoreWhenNull(Struct sct, Collection.Key key, Object value) {
		if(value!=null)sct.setEL(key, value);
	}

	/**
	 * remove every entry hat has this value
	 * @param map
	 * @param obj
	 */
	public static void removeValue(Map map, Object value) {
		Iterator it = map.entrySet().iterator();
		Map.Entry entry;
		while(it.hasNext()){
			entry=(Entry) it.next();
			if(entry.getValue()==value)it.remove();
		}
	}

	
    public static Struct merge(Struct[] scts) { 
		Struct sct=new StructImpl();
		
		for(int i=scts.length-1;i>=0;i--){
			Iterator<Entry<Key, Object>> it = scts[i].entryIterator();
			Entry<Key, Object> e;
			while(it.hasNext()){
				e = it.next();
				sct.setEL(e.getKey(), e.getValue());
			}
		}
		return sct;
	}
}