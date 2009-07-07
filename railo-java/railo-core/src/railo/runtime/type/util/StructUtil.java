package railo.runtime.type.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.dump.DumpUtil;
import railo.runtime.dump.SimpleDumpData;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Duplicator;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.Collection.Key;

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
    	railo.runtime.type.Collection.Key[] skeys = source.keys();
        for(int i=0;i<skeys.length;i++) {
            if(overwrite || !target.containsKey(skeys[i])) 
                target.setEL(skeys[i],source.get(skeys[i],null));
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
		railo.runtime.type.Collection.Key[] keys=sct.keys();
		railo.runtime.type.Collection.Key key;
		for(int i=0;i<keys.length;i++) {
			key=keys[i];
			rtn.setEL(key,Duplicator.duplicate(sct.get(key,null),deepCopy));
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

	public static Set entrySet(Struct sct) {
		HashSet set=new HashSet();
        Collection.Key[] keys = sct.keys();
        for(int i=0;i<keys.length;i++) {
            set.add(new StructMapEntry(sct,keys[i],sct.get(keys[i], null)));
        }
        return set;
	}
	

	public static Set keySet(Struct sct) {
		Collection.Key[] arr=sct.keys();
		Set set=new HashSet();
		
		for(int i=0;i<arr.length;i++){
			set.add(arr[i].getString());
		}
		return set;
	}

	
	public static DumpData toDumpData(Struct sct,String title,PageContext pageContext, int maxlevel, DumpProperties props) {
		Key[] keys = sct.keys();
		//"#5965e4","#9999ff","#000000"
		
	    DumpTable table = new DumpTable("#5965e4","#9999ff","#000000");//new DumpTable("#669900","#99cc00","#263300");
		if(!StringUtil.isEmpty(title))table.setTitle(title);
		maxlevel--;
		int maxkeys=props.getMaxKeys();
		int index=0;
		for(int i=0;i<keys.length;i++) {
			if(DumpUtil.keyValid(props,maxlevel,keys[i])){
				if(maxkeys<=index++)break;
				table.appendRow(1,
						new SimpleDumpData(keys[i].toString()),
						DumpUtil.toDumpData(sct.get(keys[i],null), 
						pageContext,maxlevel,props));
			}
		}
		return table;
	}

	/**
	 * create a value return value out of a struct
	 * @param sct
	 * @return
	 */
	public static java.util.Collection values(Struct sct) {
		ArrayList arr = new ArrayList();
		Key[] keys = sct.keys();
		for(int i=0;i<keys.length;i++) {
			arr.add(sct.get(keys[i],null));
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
}