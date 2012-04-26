package railo.runtime;

import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import railo.commons.lang.types.RefBoolean;
import railo.runtime.component.Member;
import railo.runtime.component.Property;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Objects;
import railo.runtime.type.Struct;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.cfc.ComponentAccess;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.it.EntryIterator;
import railo.runtime.type.it.StringIterator;
import railo.runtime.type.util.ComponentUtil;
import railo.runtime.type.util.StructSupport;

public final class ComponentWrap extends StructSupport implements Component, Objects {
   
    private int access;
    private ComponentAccess component;
    //private ComponentImpl ci;

    /**
     * constructor of the class
     * @param access
     * @param component
     * @throws ExpressionException 
     */
    public ComponentWrap(int access, ComponentAccess component) {
    	this.access=access;
        this.component=component;
    }
    
    public static ComponentWrap  toComponentWrap(int access, Component component) throws ExpressionException {
    	return new ComponentWrap(access, ComponentUtil.toComponentAccess(component));
    }
    

    public Page getPage(){
    	return component.getPage();
    }

    /**
     * @see railo.runtime.Component#getPageSource()
     */
    public PageSource getPageSource(){
    	return component.getPageSource();
    }
    
    /**
     * @see railo.runtime.Component#keySet()
     */
    public Set keySet() {
        return component.keySet(access);
    }

    /**
     * @see railo.runtime.Component#getDisplayName()
     */
    public String getDisplayName() {
        return component.getDisplayName();
    }

    /**
     * @see railo.runtime.Component#getExtends()
     */
    public String getExtends() {
        return component.getExtends();
    }

    /**
     * @see railo.runtime.Component#getHint()
     */
    public String getHint() {
        return component.getHint();
    }

    /**
     * @see railo.runtime.Component#getName()
     */
    public String getName() {
        return component.getName();
    }

    /**
     * @see railo.runtime.Component#getCallName()
     */
    public String getCallName() {
        return component.getCallName();
    }

    /**
     * @see railo.runtime.Component#getAbsName()
     */
    public String getAbsName() {
        return component.getAbsName();
    }
    
    /**
     * @see railo.runtime.Component#getBaseAbsName()
     */
    public String getBaseAbsName() {
        return component.getBaseAbsName();
    }
    
    public boolean isBasePeristent() {
		return component.isPersistent();
	}

    /* *
     * @see railo.runtime.Component#getBase()
     * /
    public Component getBase() {
        return component.getBaseComponent();
    }*/
    
    /* *
     * @see railo.runtime.Component#getBaseComponent()
     * /
    public Component getBaseComponent() {
        return component.getBaseComponent();
    }*/

    /**
     * @see railo.runtime.Component#getOutput()
     */
    public boolean getOutput() {
        return component.getOutput();
    }

    /**
     * @see railo.runtime.Component#instanceOf(java.lang.String)
     */
    public boolean instanceOf(String type) {
        return component.instanceOf(type);
    }

    /**
     * @see railo.runtime.Component#isValidAccess(int)
     */
    public boolean isValidAccess(int access) {
        return component.isValidAccess(access);
    }

    /**
     * @see railo.runtime.Component#getMetaData(railo.runtime.PageContext)
     */
    public Struct getMetaData(PageContext pc) throws PageException {
        return component.getMetaData(pc);
    }

    /**
     * @see railo.runtime.Component#call(railo.runtime.PageContext, java.lang.String, java.lang.Object[])
     */
    public Object call(PageContext pc, String key, Object[] args) throws PageException {
        return call(pc, KeyImpl.init(key), args);
    }

	/**
	 * @see railo.runtime.type.Objects#call(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object[])
	 */
	public Object call(PageContext pc, Collection.Key key, Object[] args) throws PageException {
		return component.call(pc,access,key,args);
	}

    /**
     * @see railo.runtime.Component#callWithNamedValues(railo.runtime.PageContext, java.lang.String, railo.runtime.type.Struct)
     */
    public Object callWithNamedValues(PageContext pc, String key, Struct args)throws PageException {
        return callWithNamedValues(pc,KeyImpl.init(key),args);
    }

	/**
	 * @see railo.runtime.type.Objects#callWithNamedValues(railo.runtime.PageContext, railo.runtime.type.Collection.Key, railo.runtime.type.Struct)
	 */
	public Object callWithNamedValues(PageContext pc, Collection.Key key, Struct args) throws PageException {
		return component.callWithNamedValues(pc,access,key,args);
	}

    /**
     * @see railo.runtime.type.Collection#size()
     */
    public int size() {
        return component.size(access);
    }

    /**
     * @see railo.runtime.type.Collection#keys()
     */
    public Collection.Key[] keys() {
        return component.keys(access);
    }

	/**
	 *
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
	 *
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key)
	 */
	public Object get(Collection.Key key) throws PageException {
		return component.get(access,key);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(Collection.Key key, Object defaultValue) {
		 return component.get(access,key, defaultValue);
	}

	/**
	 *
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
    public Iterator<Collection.Key> keyIterator() {
        return component.keyIterator(access);
    }
    
	@Override
	public Iterator<String> keysAsStringIterator() {
    	return component.keysAsStringIterator(access);
    }
	
	@Override
	public Iterator<Entry<Key, Object>> entryIterator() {
		return component.entryIterator(access);
	}
    
	/**
	 * @see railo.runtime.type.Collection#containsKey(railo.runtime.type.Collection.Key)
	 */
	public boolean containsKey(Collection.Key key) {
		return component.get(access,key,null)!=null;
	}

    /**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
	    return component.toDumpData(pageContext,maxlevel,dp,access);
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
	 * @see railo.runtime.op.Castable#compareTo(String)
	 */
	public int compareTo(String str) throws PageException {
		return component.compareTo(str);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		return component.compareTo(d);
	}

    /**
     *
     * @see railo.runtime.type.ContextCollection#get(railo.runtime.PageContext, java.lang.String, java.lang.Object)
     */
    public Object get(PageContext pc, String key, Object defaultValue) {
        return get(pc,KeyImpl.init(key),defaultValue);
    }

	/**
	 *
	 * @see railo.runtime.type.Objects#get(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(PageContext pc, Collection.Key key, Object defaultValue) {
		return component.get(access,key,defaultValue);
	}

    /**
     * @see railo.runtime.type.ContextCollection#get(railo.runtime.PageContext, java.lang.String)
     */
    public Object get(PageContext pc, String key) throws PageException {
        return get(pc,KeyImpl.init(key));
    }

	/**
	 * @see railo.runtime.type.Objects#get(railo.runtime.PageContext, railo.runtime.type.Collection.Key)
	 */
	public Object get(PageContext pc, Collection.Key key) throws PageException {
		return component.get(access,key);
	}
    
    /**
     * @see railo.runtime.type.Collection#duplicate(boolean)
     */
    public Collection duplicate(boolean deepCopy) {
    	return new ComponentWrap(access,(ComponentAccess) component.duplicate(deepCopy));
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
	 * @see railo.runtime.type.Objects#setEL(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(PageContext pc, Key propertyName, Object value) {
		return component.setEL(propertyName,value);
	}

    /**
     * @see railo.runtime.type.Objects#isInitalized()
     */
    public boolean isInitalized() {
        return component.isInitalized();
    }

    /**
     *
     * @see railo.runtime.Component#getAccess()
     */
    public int getAccess() {
        return access;
    }

	/**
	 *
	 * @see railo.runtime.Component#getJavaAccessClass(railo.commons.lang.types.RefBoolean)
	 */
	public Class getJavaAccessClass(RefBoolean isNew) throws PageException {
		return component.getJavaAccessClass(isNew);
	}

	public String getWSDLFile() {
		return component.getWSDLFile();
	}

	/**
	 * @see railo.runtime.Component#getProperties()
	 */
	public Property[] getProperties(boolean onlyPeristent) {
		return component.getProperties(onlyPeristent);
	}
	
	/**
	 * @see railo.runtime.Component#getComponentScope()
	 */
	public ComponentScope getComponentScope(){
		return component.getComponentScope();
	}

	public ComponentAccess getComponentAccess() {
		return component;
	}

	/**
	 * @see railo.runtime.Component#contains(railo.runtime.PageContext, railo.runtime.type.Collection.Key)
	 */
	public boolean contains(PageContext pc, Key key) {
		return component.contains(access,key);
	}

	/**
	 * @see railo.runtime.Component#getMember(int, railo.runtime.type.Collection.Key, boolean, boolean)
	 */
	public Member getMember(int access, Key key, boolean dataMember,boolean superAccess) {
		return component.getMember(access, key, dataMember, superAccess);
	}

	/**
	 * @see railo.runtime.Component#setProperty(railo.runtime.component.Property)
	 */
	public void setProperty(Property property) throws PageException {
		component.setProperty(property);
	}

	public boolean equalTo(String type) {
		return component.equalTo(type);
	}
}
