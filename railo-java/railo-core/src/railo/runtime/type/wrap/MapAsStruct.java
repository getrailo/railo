package railo.runtime.type.wrap;

import java.util.Iterator;
import java.util.Map;

import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpUtil;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Duplicator;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.it.EntryIterator;
import railo.runtime.type.it.KeyIterator;
import railo.runtime.type.it.StringIterator;
import railo.runtime.type.it.ValueIterator;
import railo.runtime.type.util.StructSupport;

/**
 * 
 */
public class MapAsStruct extends StructSupport implements Struct {
    
    Map map;
	private boolean caseSensitive;

    /**
     * constructor of the class
     * @param map
     * @param caseSensitive 
     */
    private MapAsStruct(Map map, boolean caseSensitive) {
        this.map=map;
        this.caseSensitive=caseSensitive;
    }
    

    public static Struct toStruct(Map map) {
    	return toStruct(map,false);
	}

    public static Struct toStruct(Map map, boolean caseSensitive) {
    	if(map instanceof Struct) return ((Struct)map);
		return new MapAsStruct(map,caseSensitive);
	}
    
    @Override
    public int size() {
       return map.size();
    }

    @Override
    public synchronized Collection.Key[] keys() {
        int len=size();
        Collection.Key[] k=new Collection.Key[len];
        Iterator it = map.keySet().iterator();
        int count=0;
        while(it.hasNext()) {
            k[count++]=KeyImpl.init(it.next().toString());
        }
        return k;
    }
    
    public static String getCaseSensitiveKey(Map map,String key) {
    	Iterator it = map.keySet().iterator();
		String strKey;
    	while(it.hasNext()) {
    		strKey=Caster.toString(it.next(),"");
    		if(strKey.equalsIgnoreCase(key)) return strKey;
    	}
		return null;
	}

    @Override
    public synchronized Object remove(Collection.Key key) throws ExpressionException {
        Object obj= map.remove(key.getString());
        if(obj==null) {
        	if(map.containsKey(key.getString())) return null;
        	if(!caseSensitive){
        		String csKey = getCaseSensitiveKey(map,key.getString());
        		if(csKey!=null)obj= map.remove(csKey);
        		if(obj!=null)return obj;
        	}
        	throw new ExpressionException("can't remove key ["+key.getString()+"] from map, key doesn't exist");
        }
        return obj;
    }
    
    @Override
    public synchronized Object removeEL(Collection.Key key) {
    	Object obj= map.remove(key.getString());
        if(!caseSensitive && obj==null) {
        	String csKey = getCaseSensitiveKey(map,key.getString());
        	if(csKey!=null)obj= map.remove(csKey);
        }
        return obj;
    }

    @Override
    public synchronized void clear() {
        map.clear();
    }

    @Override
    public synchronized Object get(Collection.Key key) throws ExpressionException {
        Object o=map.get(key.getString());
        if(o==null) {
        	if(map.containsKey(key.getString())) return null;
        	if(!caseSensitive){
        		String csKey = getCaseSensitiveKey(map,key.getString());
        		if(csKey!=null)o= map.get(csKey);
        		if(o!=null || map.containsKey(csKey)) return o;
        	}
        	throw new ExpressionException("key "+key.getString()+" doesn't exist in "+Caster.toClassName(map));
        }
        return o;
    }

    @Override
    public synchronized Object get(Collection.Key key, Object defaultValue) {
        Object obj=map.get(key.getString());
        if(obj==null) {
        	if(map.containsKey(key.getString())) return null;
        	if(!caseSensitive){
        		String csKey = getCaseSensitiveKey(map,key.getString());
        		if(csKey!=null)obj= map.get(csKey);
        		if(obj!=null || map.containsKey(csKey)) return obj; 
        	}
            return defaultValue;
        }
        return obj;
    }

    @Override
    public synchronized Object set(Collection.Key key, Object value) throws PageException {
        return map.put(key.getString(),value);
    }

    @Override
    public synchronized Object setEL(Collection.Key key, Object value) {
        return map.put(key.getString(),value);
    }
    
    @Override
	public synchronized Iterator<Collection.Key> keyIterator() {
        return new KeyIterator(keys());
    }

	@Override
	public Iterator<String> keysAsStringIterator() {
		return new StringIterator(keys());
	}
	
	@Override
	public Iterator<Entry<Key, Object>> entryIterator() {
		return new EntryIterator(this,keys());
	}
	
	@Override
	public Iterator<Object> valueIterator() {
		return new ValueIterator(this,keys());
	}
	
	
    
    @Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
	    return DumpUtil.toDumpData(map, pageContext,maxlevel,dp); 
    }
   
    @Override
    public synchronized Collection duplicate(boolean deepCopy) {
        return new MapAsStruct(Duplicator.duplicateMap(map,deepCopy),caseSensitive);
    }

	

    @Override
    public boolean containsKey(Collection.Key key) {
    	
    	//return map.containsKey(key.getString());
    	
    	boolean contains = map.containsKey(key.getString());
    	if(contains) return true; 
    	if(!caseSensitive)return map.containsKey(getCaseSensitiveKey(map,key.getString()));
    	return false;
    }

    @Override
    public String castToString() throws ExpressionException {
        throw new ExpressionException("Can't cast Complex Object Type Struct ["+getClass().getName()+"] to String",
          "Use Built-In-Function \"serialize(Struct):String\" to create a String from Struct");
    }
	@Override
	public String castToString(String defaultValue) {
		return defaultValue;
	}


    @Override
    public boolean castToBooleanValue() throws ExpressionException {
        throw new ExpressionException("Can't cast Complex Object Type Struct ["+getClass().getName()+"] to a boolean value");
    }
    
    @Override
    public Boolean castToBoolean(Boolean defaultValue) {
        return defaultValue;
    }


    @Override
    public double castToDoubleValue() throws ExpressionException {
        throw new ExpressionException("Can't cast Complex Object Type Struct ["+getClass().getName()+"] to a number value");
    }
    
    @Override
    public double castToDoubleValue(double defaultValue) {
        return defaultValue;
    }


    @Override
    public DateTime castToDateTime() throws ExpressionException {
        throw new ExpressionException("Can't cast Complex Object Type Struct ["+getClass().getName()+"] to a Date");
    }
    
    @Override
    public DateTime castToDateTime(DateTime defaultValue) {
        return defaultValue;
    }

	@Override
	public int compareTo(boolean b) throws ExpressionException {
		throw new ExpressionException("can't compare Complex Object Type Struct ["+getClass().getName()+"] with a boolean value");
	}

	@Override
	public int compareTo(DateTime dt) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type Struct ["+getClass().getName()+"] with a DateTime Object");
	}

	@Override
	public int compareTo(double d) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type Struct ["+getClass().getName()+"] with a numeric value");
	}

	@Override
	public int compareTo(String str) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type Struct ["+getClass().getName()+"] with a String");
	}

	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	@Override
	public java.util.Collection values() {
		return map.values();
	}
}