package railo.runtime;

import java.util.Iterator;
import java.util.Map;

import railo.commons.collections.HashTableNotSync;
import railo.runtime.component.Member;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.UDF;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.it.KeyIterator;
import railo.runtime.type.util.ComponentUtil;
import railo.runtime.type.util.StructSupport;
import railo.runtime.type.util.StructUtil;

public class ComponentScopeShadow extends StructSupport implements ComponentScope {

	private ComponentImpl component;
    private static final int access=Component.ACCESS_PRIVATE;
    private Map shadow=newMap();

	public static Map newMap() {
		return new HashTableNotSync();//asx
	}

	/**
	 * Constructor of the class
	 * @param component
	 * @param shadow
	 */
	public ComponentScopeShadow(ComponentImpl component, Map shadow) {
        this.component=component;
        this.shadow=shadow;
        
	}
	
	/**
	 * Constructor of the class
	 * @param component
	 * @param shadow
	 */
	public ComponentScopeShadow(ComponentImpl component, ComponentScopeShadow scope) {
        this.component=component;
        this.shadow=scope.shadow;
	}


	/**
	 * @see railo.runtime.ComponentScope#getComponent()
	 */
	public ComponentPro getComponent() {
		return component;
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
	 * @see railo.runtime.type.Scope#initialize(railo.runtime.PageContext)
	 */
	public void initialize(PageContext pc) {}

	/**
	 * @see railo.runtime.type.Scope#isInitalized()
	 */
	public boolean isInitalized() {
        return component.isInitalized();
	}

	/**
	 * @see railo.runtime.type.Scope#release()
	 */
	public void release() {}

	/**
	 * @see railo.runtime.type.Collection#clear()
	 */
	public void clear() {
		shadow.clear();
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#containsKey(railo.runtime.type.Collection.Key)
	 */
	public boolean containsKey(Collection.Key key) {
		return get(key,null)!=null;
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key)
	 */
	public Object get(Key key) throws PageException {
		Object o = get(key,null);
		if(o!=null) return o;
        throw new ExpressionException("Component ["+component.getCallName()+"] has no acessible Member with name ["+key+"]");
	}

	private ComponentImpl getUDFComponent(PageContext pc) {
		return ComponentUtil.getActiveComponent(pc, component);
	}
	
	/**
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(Key key, Object defaultValue) {
		//print.out("key:"+key);
    	
		if(key.equalsIgnoreCase(ComponentImpl.KEY_SUPER)) {
			return SuperComponent.superInstance(getUDFComponent(ThreadLocalPageContext.get()).base);
		}
		if(key.equalsIgnoreCase(ComponentImpl.KEY_THIS)) return component;
		
		Object o=shadow.get(key);
		if(o!=null) return o;
		return defaultValue;
	}

	/**
	 * @see railo.runtime.type.Collection#keyIterator()
	 */
	public Iterator keyIterator() {
		return new KeyIterator(keys());
	}
	
	/**
	 * @see railo.runtime.type.Collection#keysAsString()
	 */
	public String[] keysAsString() {
		String[] keys=new String[shadow.size()+1];
		Iterator it = shadow.keySet().iterator();
		int index=0;
		while(it.hasNext()) {
			keys[index++]=((Collection.Key)it.next()).getString();
		}
		keys[index]=ComponentImpl.KEY_THIS.getString();
		return keys;
	}

	/**
	 * @see railo.runtime.type.Collection#keys()
	 */
	public Collection.Key[] keys() {
		Collection.Key[] keys=new Collection.Key[shadow.size()+1];
		Iterator it = shadow.keySet().iterator();
		int index=0;
		while(it.hasNext()) {
			keys[index++]=(Collection.Key)it.next();
		}
		keys[index]=ComponentImpl.KEY_THIS;
		return keys;
	}

	/**
	 * @see railo.runtime.type.Collection#remove(railo.runtime.type.Collection.Key)
	 */
	public Object remove(Collection.Key key) throws PageException {
		if(key.equalsIgnoreCase(ComponentImpl.KEY_THIS) || key.equalsIgnoreCase(ComponentImpl.KEY_SUPER))
			throw new ExpressionException("key ["+key.getString()+"] is part from component and can't be removed");
		
		Object o=shadow.remove(key);
		if(o!=null) return o;
		throw new ExpressionException("can't remove key ["+key.getString()+"] from struct, key doesn't exists ");
	}



	public Object removeEL(Key key) {
		if(key.equalsIgnoreCase(ComponentImpl.KEY_THIS) || key.equalsIgnoreCase(ComponentImpl.KEY_SUPER))return null;
		return shadow.remove(key);
	}

	/**
	 * @see railo.runtime.type.Collection#set(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(Collection.Key key, Object value) {
		if(key.equalsIgnoreCase(ComponentImpl.KEY_THIS) || key.equalsIgnoreCase(ComponentImpl.KEY_SUPER)) return value;
		
		if(!component.afterConstructor && value instanceof UDF) {
			component.addConstructorUDF(key,value);
		}
		return shadow.put(key, value);
		
		//return setEL(key, value);
	}

	/**
	 * @see railo.runtime.type.Collection#setEL(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(Collection.Key key, Object value) {
		return set(key, value);
	}

	/**
	 * @see railo.runtime.type.Collection#size()
	 */
	public int size() {
		return keysAsString().length;
	}

	/**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return StructUtil.toDumpTable(this, "Variable Scope (of Component)", pageContext, maxlevel, dp);
		/*DumpTable table = new DumpTable("#5965e4","#9999ff","#000000");
		table.setTitle("Variable Scope (of Component)");
		String[] keys = keysAsString();
		String key;
		maxlevel--;
		for(int i=0;i<keys.length;i++) {
			key=keys[i];
			if(DumpUtil.keyValid(dp,maxlevel, key))
				table.appendRow(1,new SimpleDumpData(key),DumpUtil.toDumpData(get(key,null), pageContext,maxlevel,dp));
		}
		return table;*/
	}

	/**
	 *
	 * @see railo.runtime.op.Castable#castToBooleanValue()
	 */
	public boolean castToBooleanValue() throws PageException {
        throw new ExpressionException("Can't cast Complex Object Type to a boolean value");
	}
    
    /**
     * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
     */
    public Boolean castToBoolean(Boolean defaultValue) {
        return defaultValue;
    }

	/**
	 * @see railo.runtime.op.Castable#castToDateTime()
	 */
	public DateTime castToDateTime() throws PageException {
        throw new ExpressionException("Can't cast Complex Object Type to a Date Object");
	}
    
    /**
     * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
     */
    public DateTime castToDateTime(DateTime defaultValue) {
        return defaultValue;
    }

	/**
	 * @see railo.runtime.op.Castable#castToDoubleValue()
	 */
	public double castToDoubleValue() throws PageException {
        throw new ExpressionException("Can't cast Complex Object Type to a numeric value");
	}
    
    /**
     * @see railo.runtime.op.Castable#castToDoubleValue(double)
     */
    public double castToDoubleValue(double defaultValue) {
        return defaultValue;
    }

	/**
	 * @see railo.runtime.op.Castable#castToString()
	 */
	public String castToString() throws PageException {
        throw new ExpressionException("Can't cast Complex Object Type to a String");
	}
	
	/**
	 * @see railo.runtime.type.util.StructSupport#castToString(java.lang.String)
	 */
	public String castToString(String defaultValue) {
		return defaultValue;
	}

	/**
	 * @see railo.runtime.op.Castable#compare(boolean)
	 */
	public int compareTo(boolean b) throws PageException {
		throw new ExpressionException("can't compare Complex Object with a boolean value");
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		throw new ExpressionException("can't compare Complex Object with a DateTime Object");
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		throw new ExpressionException("can't compare Complex Object with a numeric value");
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		throw new ExpressionException("can't compare Complex Object with a String");
	}

	/**
	 * @see railo.runtime.type.Objects#call(railo.runtime.PageContext, java.lang.String, java.lang.Object[])
	 */
	public Object call(PageContext pc, String key, Object[] arguments) throws PageException {
		return call(pc, KeyImpl.init(key), arguments);
	}

	public Object call(PageContext pc, Collection.Key key, Object[] arguments) throws PageException {
		
		// then check in component
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
	public Object callWithNamedValues(PageContext pc, String key,Struct args) throws PageException {
		return callWithNamedValues(pc, KeyImpl.init(key), args);
	}

	public Object callWithNamedValues(PageContext pc, Key key, Struct args) throws PageException {
		Member m = component.getMember(access, key, false,false);
		if(m!=null) {
			if(m instanceof UDF) return ((UDF)m).callWithNamedValues(pc, args, false);
	        throw ComponentUtil.notFunction(component, key, m.getValue(),access);
		}
		throw ComponentUtil.notFunction(component, key, null,access);
	}
    
	/**
	 * @see railo.runtime.type.Collection#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy) {
//		 MUST muss deepCopy checken
        return new ComponentScopeShadow(component,shadow);//new ComponentScopeThis(component.cloneComponentImpl());
    }

	/**
	 * @see railo.runtime.type.Objects#get(railo.runtime.PageContext, java.lang.String, java.lang.Object)
	 */
	public Object get(PageContext pc, String key, Object defaultValue) {
		return get(key, defaultValue);
	}

	/**
	 *
	 * @see railo.runtime.type.Objects#get(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(PageContext pc, Key key, Object defaultValue) {
		return get(key, defaultValue);
	}

	/**
	 * @see railo.runtime.type.Objects#set(railo.runtime.PageContext, java.lang.String, java.lang.Object)
	 */
	public Object set(PageContext pc, String propertyName, Object value) throws PageException {
		return set(KeyImpl.init(propertyName), value);
	}

	/**
	 *
	 * @see railo.runtime.type.Objects#set(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(PageContext pc, Collection.Key propertyName, Object value) throws PageException {
		return set(propertyName, value);
	}

	/**
	 * @see railo.runtime.type.Objects#setEL(railo.runtime.PageContext, java.lang.String, java.lang.Object)
	 */
	public Object setEL(PageContext pc, String propertyName, Object value) {
		return setEL(propertyName, value);
	}

	/**
	 * @see railo.runtime.type.Objects#setEL(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(PageContext pc, Collection.Key propertyName, Object value) {
		return set(propertyName, value);
	}

	/**
	 * @see railo.runtime.type.Objects#get(railo.runtime.PageContext, java.lang.String)
	 */
	public Object get(PageContext pc, String key) throws PageException {
		return get(key);
	}

	/**
	 * @see railo.runtime.type.Objects#get(railo.runtime.PageContext, railo.runtime.type.Collection.Key)
	 */
	public Object get(PageContext pc, Collection.Key key) throws PageException {
		return get(key);
	}

	public Map getShadow() {
		return shadow;
	}

	/**
	 * @see railo.runtime.ComponentScope#setComponent(railo.runtime.ComponentImpl)
	 */
	public void setComponent(ComponentImpl c) {
		this.component=c;
	}
}
