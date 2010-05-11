package railo.runtime;

import java.util.Iterator;
import java.util.Set;

import railo.runtime.component.Member;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.PageException;
import railo.runtime.type.ClonedComponent;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.UDF;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.util.ComponentUtil;
import railo.runtime.type.util.StructSupport;
import railo.runtime.type.util.StructUtil;

/**
 * 
 */
public final class ComponentScopeThis extends StructSupport implements ComponentScope {
    
    private ComponentImpl component;
    private static final int access=Component.ACCESS_PRIVATE;
	private static final Collection.Key THIS = KeyImpl.init("this");
    
    /**
     * constructor of the class
     * @param component
     */
    public ComponentScopeThis(ComponentImpl component) {
        this.component=component;
    }

    /**
     * @see railo.runtime.type.Scope#initialize(railo.runtime.PageContext)
     */
    public void initialize(PageContext pc) {
        
    }

    /**
     * @see railo.runtime.type.Scope#release()
     */
    public void release() {
        
    }

    /**
     * @see railo.runtime.type.Scope#getType()
     */
    public int getType() {
        return SCOPE_VARIABLES;
    }

    /**
     * @see railo.runtime.type.Scope#getTypeAsString()
     */
    public String getTypeAsString() {
        return "variables";
    }

    /**
     * @see railo.runtime.type.Collection#size()
     */
    public int size() {
        return component.size(access)+1;
    }
    
    /**
     * @see railo.runtime.type.Collection#keysAsString()
     */
    public String[] keysAsString() {
        Set keySet = component.keySet(access);
        keySet.add("this");
        return (String[]) keySet.toArray(new String[keySet.size()]);
    }

    public Collection.Key[] keys() {
    	return StructUtil.toCollectionKeys(keysAsString());
    }

	/**
	 * @see railo.runtime.type.Collection#remove(railo.runtime.type.Collection.Key)
	 */
	public Object remove(Collection.Key key) throws PageException {
		return component.remove(key);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#removeEL(railo.runtime.type.Collection.Key)
	 */
	public Object removeEL(Collection.Key key) {
		 return component.removeEL(key);
	}

    /**
     * @see railo.runtime.type.Collection#clear()
     */
    public void clear() {
        component.clear();
    }

	/**
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key)
	 */
	public Object get(Key key) throws PageException {
        if(key.equalsIgnoreCase(THIS)){
            return component;
        }
        return component.get(access,key);
	}

	/**
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(Collection.Key key, Object defaultValue) {
        if(key.equalsIgnoreCase(THIS)){
            return component;
        }
        return component.get(access,key,defaultValue);
	}

	/**
	 * @see railo.runtime.type.Collection#set(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(Collection.Key key, Object value) throws PageException {
		return component.set(key,value);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#setEL(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(Collection.Key key, Object value) {
		return component.setEL(key,value);
	}

    /**
     * @see railo.runtime.type.Iteratorable#keyIterator()
     */
    public Iterator keyIterator() {
        return component.iterator(access);
    }
    
	/**
	 * @see railo.runtime.type.Collection#containsKey(railo.runtime.type.Collection.Key)
	 */
	public boolean containsKey(Key key) {
		return get(key,null)!=null;
	}

    /**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return StructUtil.toDumpTable(this, "Variable Scope (of Component)", pageContext, maxlevel, dp);
		/*DumpTable table = new DumpTable("#5965e4","#9999ff","#000000");
		table.setTitle("Variable Scope (of Component)");
        
        
        Iterator it=keyIterator();
        
        while(it.hasNext()) {
            String key=Caster.toString(it.next(),"");
            table.appendRow(1,new SimpleDumpData(key.toString()),DumpUtil.toDumpData(get(key,null), pageContext,maxlevel,dp));
        }
        return table;*/
    }

    /**
     * @see railo.runtime.op.Castable#castToString()
     */
    public String castToString() throws PageException {
        return component.castToString();
    }
    
	/**
	 * @see railo.runtime.type.util.StructSupport#castToString(java.lang.String)
	 */
	public String castToString(String defaultValue) {
		return component.castToString(defaultValue);
	}

    /**
     * @see railo.runtime.op.Castable#castToBooleanValue()
     */
    public boolean castToBooleanValue() throws PageException {
        return component.castToBooleanValue();
    }
    
    /**
     * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
     */
    public Boolean castToBoolean(Boolean defaultValue) {
        return component.castToBoolean(defaultValue);
    }

    /**
     * @see railo.runtime.op.Castable#castToDoubleValue()
     */
    public double castToDoubleValue() throws PageException {
        return component.castToDoubleValue();
    }
    
    /**
     * @see railo.runtime.op.Castable#castToDoubleValue(double)
     */
    public double castToDoubleValue(double defaultValue) {
        return component.castToDoubleValue(defaultValue);
    }

    /**
     * @see railo.runtime.op.Castable#castToDateTime()
     */
    public DateTime castToDateTime() throws PageException {
        return component.castToDateTime();
    }
    
    /**
     * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
     */
    public DateTime castToDateTime(DateTime defaultValue) {
        return component.castToDateTime(defaultValue);
    }


	/**
	 * @throws PageException 
	 * @see railo.runtime.op.Castable#compare(boolean)
	 */
	public int compareTo(boolean b) throws PageException {
		return component.compareTo(b);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		return component.compareTo(dt);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		return component.compareTo(d);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		return component.compareTo(str);
	}
    
    /**
     * @see railo.runtime.type.Collection#duplicate(boolean)
     */
    public Collection duplicate(boolean deepCopy) {
    	return new ComponentScopeThis(new ClonedComponent(component,deepCopy));
    	//return new ComponentScopeThis(component.cloneComponentImpl(deepCopy));
    }

    /**
     * Returns the value of component.
     * @return value component
     */
    public ComponentPro getComponent() {
        return component;
    }

    /**
     *
     * @see railo.runtime.type.Objects#get(railo.runtime.PageContext, java.lang.String, java.lang.Object)
     */
    public Object get(PageContext pc, String key, Object defaultValue) {
        return component.get(access,key,defaultValue);
    }

	/**
	 *
	 * @see railo.runtime.type.Objects#get(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(PageContext pc, Collection.Key key, Object defaultValue) {
		return component.get(access,key,defaultValue);
	}

    /**
     * @see railo.runtime.type.Objects#get(railo.runtime.PageContext, java.lang.String)
     */
    public Object get(PageContext pc, String key) throws PageException {
    	return component.get(access,key);
    }

	/**
	 *
	 * @see railo.runtime.type.Objects#get(railo.runtime.PageContext, railo.runtime.type.Collection.Key)
	 */
	public Object get(PageContext pc, Collection.Key key) throws PageException {
		return component.get(access,key);
	}

    /**
     * @see railo.runtime.type.Objects#set(railo.runtime.PageContext, java.lang.String, java.lang.Object)
     */
    public Object set(PageContext pc, String propertyName, Object value) throws PageException {
        return component.set(propertyName,value);
    }

	/**
	 *
	 * @see railo.runtime.type.Objects#set(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(PageContext pc, Collection.Key propertyName, Object value) throws PageException {
		return component.set(propertyName,value);
	}

    /**
     *
     * @see railo.runtime.type.Objects#setEL(railo.runtime.PageContext, java.lang.String, java.lang.Object)
     */
    public Object setEL(PageContext pc, String propertyName, Object value) {
        return component.setEL(propertyName,value);
    }

	/**
	 *
	 * @see railo.runtime.type.Objects#setEL(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(PageContext pc, Collection.Key propertyName, Object value) {
		return component.setEL(propertyName,value);
	}

    /**
     * @see railo.runtime.type.Objects#call(railo.runtime.PageContext, java.lang.String, java.lang.Object[])
     */
    public Object call(PageContext pc, String key, Object[] arguments) throws PageException {
    	return call(pc, KeyImpl.init(key), arguments);
    }

	public Object call(PageContext pc, Collection.Key key, Object[] arguments) throws PageException {
    	Member m = component.getMember(access, key, false,false);
		if(m!=null) {
			if(m instanceof UDF) return ((UDF)m).call(pc, arguments, false);
	        throw ComponentUtil.notFunction(component, key, m.getValue(),access);
		}
		throw ComponentUtil.notFunction(component, key, null,access);
	}

    /**
     * @see railo.runtime.type.Objects#callWithNamedValues(railo.runtime.PageContext, java.lang.String, railo.runtime.type.Struct)
     */
    public Object callWithNamedValues(PageContext pc, String key, Struct args) throws PageException {
    	return callWithNamedValues(pc, KeyImpl.init(key), args);
    }

	public Object callWithNamedValues(PageContext pc, Collection.Key key, Struct args) throws PageException {
    	Member m = component.getMember(access, key, false,false);
		if(m!=null) {
			if(m instanceof UDF) return ((UDF)m).callWithNamedValues(pc, args, false);
	        throw ComponentUtil.notFunction(component, key, m.getValue(),access);
		}
		throw ComponentUtil.notFunction(component, key, null,access);
	}

    /**
     * @see railo.runtime.type.Objects#isInitalized()
     */
    public boolean isInitalized() {
        return component.isInitalized();
    }

	/**
	 * @see railo.runtime.ComponentScope#setComponent(railo.runtime.ComponentImpl)
	 */
	public void setComponent(ComponentImpl c) {
		this.component=c;
	}
}
