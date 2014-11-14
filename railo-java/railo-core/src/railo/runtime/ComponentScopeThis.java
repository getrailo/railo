/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
package railo.runtime;

import java.util.Iterator;
import java.util.Set;

import railo.runtime.component.Member;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.UDFPlus;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.util.ComponentUtil;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.type.util.StructSupport;
import railo.runtime.type.util.StructUtil;

/**
 * 
 */
public final class ComponentScopeThis extends StructSupport implements ComponentScope {
    
    private final ComponentImpl component;
    private static final int access=Component.ACCESS_PRIVATE;
    
    /**
     * constructor of the class
     * @param component
     */
    public ComponentScopeThis(ComponentImpl component) {
        this.component=component;
    }

    @Override
    public void initialize(PageContext pc) {
        
    }

    @Override
    public void release() {}
    
    @Override
    public void release(PageContext pc) {}

    @Override
    public int getType() {
        return SCOPE_VARIABLES;
    }

    @Override
    public String getTypeAsString() {
        return "variables";
    }

    @Override
    public int size() {
        return component.size(access)+1;
    }
    
    @Override
    public Collection.Key[] keys() {
    	Set<Key> keySet = component.keySet(access);
        keySet.add(KeyConstants._this);
        Collection.Key[] arr = new Collection.Key[keySet.size()];
        Iterator<Key> it = keySet.iterator();
        
        int index=0;
        while(it.hasNext()){
        	arr[index++]=it.next();
        }
        return arr;
    }
    
    @Override
    public Object remove(Collection.Key key) throws PageException {
		return component.remove(key);
	}

    @Override
    public Object removeEL(Collection.Key key) {
		 return component.removeEL(key);
	}

    @Override
    public void clear() {
        component.clear();
    }

	@Override
	public Object get(Key key) throws PageException {
        if(key.equalsIgnoreCase(KeyConstants._THIS)){
            return component.top;
        }
        return component.get(access,key);
	}

	@Override
	public Object get(Collection.Key key, Object defaultValue) {
        if(key.equalsIgnoreCase(KeyConstants._THIS)){
            return component.top;
        }
        return component.get(access,key,defaultValue);
	}

	@Override
	public Object set(Collection.Key key, Object value) throws PageException {
		return component.set(key,value);
	}

	@Override
	public Object setEL(Collection.Key key, Object value) {
		return component.setEL(key,value);
	}

	@Override
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
	
	@Override
	public Iterator<Object> valueIterator() {
		return component.valueIterator(access);
	}
    
	@Override
	public boolean containsKey(Key key) {
		return get(key,null)!=null;
	}

    @Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return StructUtil.toDumpTable(this, "Variable Scope (of Component)", pageContext, maxlevel, dp);
    }

    @Override
    public String castToString() throws PageException {
        return component.castToString();
    }
    
	@Override
	public String castToString(String defaultValue) {
		return component.castToString(defaultValue);
	}

    @Override
    public boolean castToBooleanValue() throws PageException {
        return component.castToBooleanValue();
    }
    
    @Override
    public Boolean castToBoolean(Boolean defaultValue) {
        return component.castToBoolean(defaultValue);
    }

    @Override
    public double castToDoubleValue() throws PageException {
        return component.castToDoubleValue();
    }
    
    @Override
    public double castToDoubleValue(double defaultValue) {
        return component.castToDoubleValue(defaultValue);
    }

    @Override
    public DateTime castToDateTime() throws PageException {
        return component.castToDateTime();
    }
    
    @Override
    public DateTime castToDateTime(DateTime defaultValue) {
        return component.castToDateTime(defaultValue);
    }


	@Override
	public int compareTo(boolean b) throws PageException {
		return component.compareTo(b);
	}

	@Override
	public int compareTo(DateTime dt) throws PageException {
		return component.compareTo(dt);
	}

	@Override
	public int compareTo(double d) throws PageException {
		return component.compareTo(d);
	}

	@Override
	public int compareTo(String str) throws PageException {
		return component.compareTo(str);
	}
    
    @Override
    public Collection duplicate(boolean deepCopy) {

		StructImpl sct = new StructImpl();
		StructImpl.copy(this, sct, deepCopy);
		return sct;
    }

    /**
     * Returns the value of component.
     * @return value component
     */
    public Component getComponent() {
        return component.top;
    }

    /*public Object get(PageContext pc, String key, Object defaultValue) {
        return component.get(access,key,defaultValue);
    }*/

	@Override
	public Object get(PageContext pc, Collection.Key key, Object defaultValue) {
		return component.get(access,key,defaultValue);
	}

    /*public Object get(PageContext pc, String key) throws PageException {
    	return component.get(access,key);
    }*/

	@Override
	public Object get(PageContext pc, Collection.Key key) throws PageException {
		return component.get(access,key);
	}

    /*public Object set(PageContext pc, String propertyName, Object value) throws PageException {
        return component.set(propertyName,value);
    }*/

	@Override
	public Object set(PageContext pc, Collection.Key propertyName, Object value) throws PageException {
		return component.set(propertyName,value);
	}

    /*public Object setEL(PageContext pc, String propertyName, Object value) {
        return component.setEL(propertyName,value);
    }*/

	@Override
	public Object setEL(PageContext pc, Collection.Key propertyName, Object value) {
		return component.setEL(propertyName,value);
	}

    /*public Object call(PageContext pc, String key, Object[] arguments) throws PageException {
    	return call(pc, KeyImpl.init(key), arguments);
    }*/

	public Object call(PageContext pc, Collection.Key key, Object[] arguments) throws PageException {
    	Member m = component.getMember(access, key, false,false);
		if(m!=null) {
			if(m instanceof UDFPlus) return ((UDFPlus)m).call(pc,key, arguments, false);
	        throw ComponentUtil.notFunction(component, key, m.getValue(),access);
		}
		throw ComponentUtil.notFunction(component, key, null,access);
	}

    /*public Object callWithNamedValues(PageContext pc, String key, Struct args) throws PageException {
    	return callWithNamedValues(pc, KeyImpl.init(key), args);
    }*/

	public Object callWithNamedValues(PageContext pc, Collection.Key key, Struct args) throws PageException {
    	Member m = component.getMember(access, key, false,false);
		if(m!=null) {
			if(m instanceof UDFPlus) return ((UDFPlus)m).callWithNamedValues(pc,key, args, false);
	        throw ComponentUtil.notFunction(component, key, m.getValue(),access);
		}
		throw ComponentUtil.notFunction(component, key, null,access);
	}

    @Override
    public boolean isInitalized() {
        return component.isInitalized();
    }

	@Override
	public void setBind(boolean bind) {}

	@Override
	public boolean isBind() {
		return true;
	}
}
