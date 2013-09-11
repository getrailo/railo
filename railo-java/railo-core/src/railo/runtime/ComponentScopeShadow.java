package railo.runtime;

import java.util.Iterator;

import railo.commons.collection.MapFactory;
import railo.commons.collection.MapPro;
import railo.runtime.component.Member;
import railo.runtime.config.NullSupportHelper;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Duplicator;
import railo.runtime.type.Collection;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.UDF;
import railo.runtime.type.UDFPlus;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.it.EntryIterator;
import railo.runtime.type.it.KeyIterator;
import railo.runtime.type.it.StringIterator;
import railo.runtime.type.it.ValueIterator;
import railo.runtime.type.util.ComponentUtil;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.type.util.StructSupport;
import railo.runtime.type.util.StructUtil;

public class ComponentScopeShadow extends StructSupport implements ComponentScope {

	private static final long serialVersionUID = 4930100230796574243L;

	private final ComponentImpl component;
	private static final int access=Component.ACCESS_PRIVATE;
	private final MapPro<Key,Object> shadow;


	/**
	 * Constructor of the class
	 * @param component
	 * @param shadow
	 */
	public ComponentScopeShadow(ComponentImpl component, MapPro shadow) {
        this.component=component;
        this.shadow=shadow;
        
	}
	
	/**
	 * Constructor of the class
	 * @param component
	 * @param shadow
	 */
	public ComponentScopeShadow(ComponentImpl component, ComponentScopeShadow scope,boolean cloneShadow) {
        this.component=component;
        this.shadow=cloneShadow?(MapPro)Duplicator.duplicateMap(scope.shadow,MapFactory.getConcurrentMap(), false):scope.shadow;
	}


	@Override
	public Component getComponent() {
		return component.top;
	}

    @Override
    public int getType() {
        return SCOPE_VARIABLES;
    }

    @Override
    public String getTypeAsString() {
        return "variables";
    }

	@Override
	public void initialize(PageContext pc) {}

	@Override
	public boolean isInitalized() {
        return component.isInitalized();
	}


    @Override
    public void release() {}
    
    @Override
    public void release(PageContext pc) {}


	@Override
	public void clear() {
		shadow.clear();
	}

	@Override
	public boolean containsKey(Collection.Key key) {
		return get(key,null)!=null;
	}

	@Override
	public Object get(Key key) throws PageException {
		Object o = get(key,NullSupportHelper.NULL());
		if(o!=NullSupportHelper.NULL()) return o;
        throw new ExpressionException("Component ["+component.getCallName()+"] has no accessible Member with name ["+key+"]");
	}
	
	@Override
	public Object get(Key key, Object defaultValue) {
		if(key.equalsIgnoreCase(KeyConstants._SUPER)) {
			return SuperComponent.superInstance((ComponentImpl)ComponentUtil.getActiveComponent(ThreadLocalPageContext.get(),component)._base());
		}
		if(key.equalsIgnoreCase(KeyConstants._THIS)) return component.top;
		
		if(NullSupportHelper.full())return shadow.g(key,defaultValue); 
		
		Object o=shadow.get(key);
		if(o!=null) return o;
		return defaultValue;
		
	}

	@Override
	public Iterator<Collection.Key> keyIterator() {
		return new KeyIterator(keys());
	}
    
	@Override
	public Iterator<String> keysAsStringIterator() {
    	return new StringIterator(keys());
    }

	@Override
	public Iterator<Entry<Key, Object>> entryIterator() {
		return new EntryIterator(this, keys());
	}

	@Override
	public Iterator<Object> valueIterator() {
		return new ValueIterator(this,keys());
	}

	@Override
	public Collection.Key[] keys() {
		Collection.Key[] keys=new Collection.Key[shadow.size()+1];
		Iterator<Key> it = shadow.keySet().iterator();
		int index=0;
		while(it.hasNext()) {
			keys[index++]=it.next();
		}
		keys[index]=KeyConstants._THIS;
		return keys;
	}

	@Override
	public Object remove(Collection.Key key) throws PageException {
		if(key.equalsIgnoreCase(KeyConstants._this) || key.equalsIgnoreCase(KeyConstants._super))
			throw new ExpressionException("key ["+key.getString()+"] is part of the component and can't be removed");
		if(NullSupportHelper.full())return shadow.r(key);
		
		Object o=shadow.remove(key);
		if(o!=null) return o;
		throw new ExpressionException("can't remove key ["+key.getString()+"] from struct, key doesn't exist");
	}



	public Object removeEL(Key key) {
		if(key.equalsIgnoreCase(KeyConstants._this) || key.equalsIgnoreCase(KeyConstants._super))return null;
		return shadow.remove(key);
	}

	@Override
	public Object set(Collection.Key key, Object value) {
		if(key.equalsIgnoreCase(KeyConstants._this) || key.equalsIgnoreCase(KeyConstants._super)) return value;
		
		if(!component.afterConstructor && value instanceof UDF) {
			component.addConstructorUDF(key,(UDF)value);
		}
		shadow.put(key, value);
		return value;
	}

	@Override
	public Object setEL(Collection.Key key, Object value) {
		return set(key, value);
	}

	@Override
	public int size() {
		return keys().length;
	}

	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return StructUtil.toDumpTable(this, "Variable Scope (of Component)", pageContext, maxlevel, dp);
	}

	@Override
	public boolean castToBooleanValue() throws PageException {
        throw new ExpressionException("Can't cast Complex Object Type to a boolean value");
	}
    
    @Override
    public Boolean castToBoolean(Boolean defaultValue) {
        return defaultValue;
    }

	@Override
	public DateTime castToDateTime() throws PageException {
        throw new ExpressionException("Can't cast Complex Object Type to a Date Object");
	}
    
    @Override
    public DateTime castToDateTime(DateTime defaultValue) {
        return defaultValue;
    }

	@Override
	public double castToDoubleValue() throws PageException {
        throw new ExpressionException("Can't cast Complex Object Type to a numeric value");
	}
    
    @Override
    public double castToDoubleValue(double defaultValue) {
        return defaultValue;
    }

	@Override
	public String castToString() throws PageException {
        throw new ExpressionException("Can't cast Complex Object Type to a String");
	}
	
	@Override
	public String castToString(String defaultValue) {
		return defaultValue;
	}

	@Override
	public int compareTo(boolean b) throws PageException {
		throw new ExpressionException("can't compare Complex Object with a boolean value");
	}

	@Override
	public int compareTo(DateTime dt) throws PageException {
		throw new ExpressionException("can't compare Complex Object with a DateTime Object");
	}

	@Override
	public int compareTo(double d) throws PageException {
		throw new ExpressionException("can't compare Complex Object with a numeric value");
	}

	@Override
	public int compareTo(String str) throws PageException {
		throw new ExpressionException("can't compare Complex Object with a String");
	}

	/*public Object call(PageContext pc, String key, Object[] arguments) throws PageException {
		return call(pc, KeyImpl.init(key), arguments);
	}*/

	public Object call(PageContext pc, Collection.Key key, Object[] arguments) throws PageException {
		// first check variables
		Object o=shadow.get(key);
		if(o instanceof UDFPlus) {
			return ((UDFPlus)o).call(pc,key, arguments, false);
		}
		
		// then check in component
		Member m = component.getMember(access, key, false,false);
		if(m!=null) {
			if(m instanceof UDFPlus) return ((UDFPlus)m).call(pc,key, arguments, false);
		}
		throw ComponentUtil.notFunction(component, key, m!=null?m.getValue():null,access);
	}

	/*public Object callWithNamedValues(PageContext pc, String key,Struct args) throws PageException {
		return callWithNamedValues(pc, KeyImpl.init(key), args);
	}*/

	public Object callWithNamedValues(PageContext pc, Key key, Struct args) throws PageException {
		// first check variables
		Object o=shadow.get(key);
		if(o instanceof UDFPlus) {
			return ((UDFPlus)o).callWithNamedValues(pc,key, args, false);
		}
		
		Member m = component.getMember(access, key, false,false);
		if(m!=null) {
			if(m instanceof UDFPlus) return ((UDFPlus)m).callWithNamedValues(pc,key, args, false);
	        throw ComponentUtil.notFunction(component, key, m.getValue(),access);
		}
		throw ComponentUtil.notFunction(component, key, null,access);
	}
    
	@Override
	public Collection duplicate(boolean deepCopy) {
		StructImpl sct = new StructImpl();
		StructImpl.copy(this, sct, deepCopy);
		return sct;
//		 MUST muss deepCopy checken
        //return new ComponentScopeShadow(component,shadow);//new ComponentScopeThis(component.cloneComponentImpl());
    }
	

	/*public Object get(PageContext pc, String key, Object defaultValue) {
		return get(key, defaultValue);
	}*/

	@Override
	public Object get(PageContext pc, Key key, Object defaultValue) {
		return get(key, defaultValue);
	}

	@Override
	public Object set(PageContext pc, Collection.Key propertyName, Object value) throws PageException {
		return set(propertyName, value);
	}

	/*public Object setEL(PageContext pc, String propertyName, Object value) {
		return setEL(propertyName, value);
	}*/

	@Override
	public Object setEL(PageContext pc, Collection.Key propertyName, Object value) {
		return set(propertyName, value);
	}

	/*public Object get(PageContext pc, String key) throws PageException {
		return get(key);
	}*/

	@Override
	public Object get(PageContext pc, Collection.Key key) throws PageException {
		return get(key);
	}

	public MapPro<Key,Object> getShadow() {
		return shadow;
	}

	@Override
	public void setBind(boolean bind) {}

	@Override
	public boolean isBind() {
		return true;
	}
}
