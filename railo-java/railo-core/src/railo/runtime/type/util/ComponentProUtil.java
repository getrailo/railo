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
package railo.runtime.type.util;

import railo.runtime.Component;
import railo.runtime.ComponentPro;
import railo.runtime.component.Member;
import railo.runtime.component.Property;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;

public class ComponentProUtil {
	
	public static Property[] getProperties(Component c,boolean onlyPeristent, boolean includeBaseProperties, boolean preferBaseProperties, boolean inheritedMappedSuperClassOnly) {
		ComponentPro cp = toComponentPro(c);
		if(cp!=null) return cp.getProperties(onlyPeristent, includeBaseProperties,preferBaseProperties,inheritedMappedSuperClassOnly);
		
		// reflection
		try{
			java.lang.reflect.Method getProperties = c.getClass().getMethod("getProperties", new Class[]{
				boolean.class,boolean.class,boolean.class,boolean.class});
			return (Property[]) getProperties.invoke(c, new Object[]{onlyPeristent,includeBaseProperties,preferBaseProperties,inheritedMappedSuperClassOnly});
		}
		catch(Throwable t){
			throw new RuntimeException(t);
		}
	}

	public static Component getBaseComponent(Component c) { 
		ComponentPro cp = toComponentPro(c);
		if(cp!=null) return cp.getBaseComponent();
		
		// reflection
		try{
			java.lang.reflect.Method getBaseComponent = c.getClass().getMethod("getBaseComponent", new Class[]{});
			return (Component) getBaseComponent.invoke(c, new Object[]{});
		}
		catch(Throwable t){
			throw new RuntimeException(t);
		}
	}

	public static boolean isPersistent(Component c) {
		ComponentPro cp = toComponentPro(c);
		if(cp!=null) return cp.isPersistent();
		
		// reflection
		try{
			java.lang.reflect.Method isPersistent = c.getClass().getMethod("isPersistent", new Class[]{});
			return Caster.toBooleanValue(isPersistent.invoke(c, new Object[]{}));
		}
		catch(Throwable t){
			throw new RuntimeException(t);
		}
	}
	
	public static Key[] keys(Component c, int access) {
		ComponentPro cp = toComponentPro(c);
		if(cp!=null) return cp.keys(access);
		
		// reflection
		try{
			java.lang.reflect.Method keys = c.getClass().getMethod("keys", new Class[]{int.class});
			return (Key[]) keys.invoke(c, new Object[]{access});
		}
		catch(Throwable t){
			throw new RuntimeException(t);
		}
	}

	public static Member getMember(Component c, int access,Collection.Key key, boolean dataMember,boolean superAccess) {
		ComponentPro cp = toComponentPro(c);
		if(cp!=null) return cp.getMember(access, key, dataMember, superAccess);
		
		// reflection
		try{
			java.lang.reflect.Method getMember = c.getClass().getMethod("getMember", new Class[]{int.class,Collection.Key.class, boolean.class,boolean.class});
			return (Member)getMember.invoke(c, new Object[]{access,key,dataMember,superAccess});
		}
		catch(Throwable t){
			throw new RuntimeException(t);
		}
	}
	
	public static Object get(Component c, int access, Key key, Object defaultValue) {
		ComponentPro cp = toComponentPro(c);
		if(cp!=null) return cp.get(access, key, defaultValue);
		
		// reflection
		try{
			java.lang.reflect.Method getMember = c.getClass().getMethod("get", new Class[]{int.class, Key.class, Object.class});
			return getMember.invoke(c, new Object[]{access,key,defaultValue});
		}
		catch(Throwable t){
			throw new RuntimeException(t);
		}
	}
	
	public static ComponentPro toComponentPro(Component cfc) {
		if(cfc instanceof ComponentPro)return (ComponentPro) cfc;
		
		// check for method getComponent
		try{
			java.lang.reflect.Method getComponent = cfc.getClass().getMethod("getComponent", new Class[]{});
			return toComponentPro((Component) getComponent.invoke(cfc, new Object[]{}));
		}
		catch(Throwable t){
			throw new RuntimeException(t);
		}
	}

}
