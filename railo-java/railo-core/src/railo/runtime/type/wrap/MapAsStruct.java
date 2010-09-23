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
import railo.runtime.type.util.StructSupport;

/**
 * 
 */
public  class MapAsStruct extends StructSupport implements Struct {
    
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
    

    public static Struct toStruct(Map map, boolean caseSensitive) {
    	if(map instanceof Struct) return ((Struct)map);
		return new MapAsStruct(map,caseSensitive);
	}
    
    /**
     * @see railo.runtime.type.Collection#size()
     */
    public int size() {
       return map.size();
    }


    /**
     * @see railo.runtime.type.Collection#keysAsString()
     */
    public synchronized String[] keysAsString() {
        int len=size();
        String[] k=new String[len];
        Iterator it = map.keySet().iterator();
        int count=0;
        while(it.hasNext()) {
            k[count++]=it.next().toString();
        }
        return k;
    }
    /**
     * @see railo.runtime.type.Collection#keys()
     */
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

    /**
     * @see railo.runtime.type.Collection#remove(java.lang.String)
     */
    public synchronized Object remove(Collection.Key key) throws ExpressionException {
        Object obj= map.remove(key.getString());
        if(obj==null) {
        	if(!caseSensitive){
        		String csKey = getCaseSensitiveKey(map,key.getString());
        		if(csKey!=null)obj= map.remove(csKey);
        		if(obj!=null)return obj;
        	}
        	throw new ExpressionException("can't remove key ["+key.getString()+"] from map, key doesn't exists");
        }
        return obj;
    }
    
    /**
     * @see railo.runtime.type.Collection#removeEL(java.lang.String)
     */
    public synchronized Object removeEL(Collection.Key key) {
    	Object obj= map.remove(key.getString());
        if(!caseSensitive && obj==null) {
        	String csKey = getCaseSensitiveKey(map,key.getString());
        	if(csKey!=null)obj= map.remove(csKey);
        }
        return obj;
    }

    /**
     * @see railo.runtime.type.Collection#clear()
     */
    public synchronized void clear() {
        map.clear();
    }

    /**
     * @see railo.runtime.type.Collection#get(java.lang.String)
     */
    public synchronized Object get(Collection.Key key) throws ExpressionException {
        Object o=map.get(key.getString());
        if(o==null) {
        	if(!caseSensitive){
        		String csKey = getCaseSensitiveKey(map,key.getString());
        		if(csKey!=null)o= map.get(csKey);
        		if(o!=null) return o;
        	}
        	throw new ExpressionException("key "+key.getString()+" doesn't exists in "+Caster.toClassName(map));
        }
        return o;
    }

    /**
     * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key, java.lang.Object)
     */
    public synchronized Object get(Collection.Key key, Object defaultValue) {
        Object obj=map.get(key.getString());
        if(obj==null) {
        	if(!caseSensitive){
        		String csKey = getCaseSensitiveKey(map,key.getString());
        		if(csKey!=null)obj= map.get(csKey);
        		if(obj!=null) return obj; 
        	}
            return defaultValue;
        }
        return obj;
    }

    /**
     * @see railo.runtime.type.Collection#set(java.lang.String, java.lang.Object)
     */
    public synchronized Object set(Collection.Key key, Object value) throws PageException {
        return map.put(key.getString(),value);
    }

    /**
     * @see railo.runtime.type.Collection#setEL(java.lang.String, java.lang.Object)
     */
    public synchronized Object setEL(Collection.Key key, Object value) {
        return map.put(key.getString(),value);
    }
    
    
    /**
     * @see railo.runtime.type.Collection#keyIterator()
     */
    public synchronized Iterator keyIterator() {
        return map.keySet().iterator();//new ArrayIterator(map.keySet().toArray());
    }
    
    /**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
	    return DumpUtil.toDumpData(map, pageContext,maxlevel,dp); 
    }
   
    /**
     * @see railo.runtime.type.Collection#duplicate(boolean)
     */
    public synchronized Collection duplicate(boolean deepCopy) {
        return new MapAsStruct(Duplicator.duplicateMap(map,deepCopy),caseSensitive);
    }

	

    /**
     * @see railo.runtime.type.Collection#containsKey(java.lang.String)
     */
    public boolean containsKey(Collection.Key key) {
    	
    	//return map.containsKey(key.getString());
    	
    	boolean contains = map.containsKey(key.getString());
    	if(contains) return true; 
    	if(!caseSensitive)return map.containsKey(getCaseSensitiveKey(map,key.getString()));
    	return false;
    }

    /**
     * @see railo.runtime.op.Castable#castToString()
     */
    public String castToString() throws ExpressionException {
        throw new ExpressionException("Can't cast Complex Object Type Struct ["+getClass().getName()+"] to String",
          "Use Build-In-Function \"serialize(Struct):String\" to create a String from Struct");
    }
	/**
	 * @see railo.runtime.type.util.StructSupport#castToString(java.lang.String)
	 */
	public String castToString(String defaultValue) {
		return defaultValue;
	}


    /**
     * @see railo.runtime.op.Castable#castToBooleanValue()
     */
    public boolean castToBooleanValue() throws ExpressionException {
        throw new ExpressionException("Can't cast Complex Object Type Struct ["+getClass().getName()+"] to a boolean value");
    }
    
    /**
     * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
     */
    public Boolean castToBoolean(Boolean defaultValue) {
        return defaultValue;
    }


    /**
     * @see railo.runtime.op.Castable#castToDoubleValue()
     */
    public double castToDoubleValue() throws ExpressionException {
        throw new ExpressionException("Can't cast Complex Object Type Struct ["+getClass().getName()+"] to a number value");
    }
    
    /**
     * @see railo.runtime.op.Castable#castToDoubleValue(double)
     */
    public double castToDoubleValue(double defaultValue) {
        return defaultValue;
    }


    /**
     * @see railo.runtime.op.Castable#castToDateTime()
     */
    public DateTime castToDateTime() throws ExpressionException {
        throw new ExpressionException("Can't cast Complex Object Type Struct ["+getClass().getName()+"] to a Date");
    }
    
    /**
     * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
     */
    public DateTime castToDateTime(DateTime defaultValue) {
        return defaultValue;
    }

	/**
	 * @see railo.runtime.op.Castable#compare(boolean)
	 */
	public int compareTo(boolean b) throws ExpressionException {
		throw new ExpressionException("can't compare Complex Object Type Struct ["+getClass().getName()+"] with a boolean value");
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type Struct ["+getClass().getName()+"] with a DateTime Object");
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type Struct ["+getClass().getName()+"] with a numeric value");
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type Struct ["+getClass().getName()+"] with a String");
	}

	/**
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	/**
	 * @see java.util.Map#values()
	 */
	public java.util.Collection values() {
		return map.values();
	}
}






