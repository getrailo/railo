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
package railo.runtime.cfx.customtag;

import railo.runtime.cfx.CFXTagException;
import railo.runtime.reflection.Reflector;

import com.allaire.cfx.CustomTag;

/**
 * 
 */
public final class JavaCFXTagClass implements CFXTagClass {
	
	private String name;
	private String strClass;
	private Class clazz;
	private boolean readOnly=false;

    
	public JavaCFXTagClass(String name, String strClass) {
		name=name.toLowerCase();
		if(name.startsWith("cfx_"))name=name.substring(4);
		this.name=name;
		this.strClass=strClass;
	}
	private JavaCFXTagClass(String name, String strClass, Class clazz,boolean readOnly) {
		
		this.name=name;
		this.strClass=strClass;
		this.clazz=clazz;
		this.readOnly=readOnly;
	}
	
	@Override
	public CustomTag newInstance() throws CFXTagException {
		try {
			return _newInstance();
		} catch (Throwable e) {
			throw new CFXTagException(e);
		}
	}

	public CustomTag _newInstance() throws ClassNotFoundException, InstantiationException, IllegalAccessException  {
		
		Object o=getClazz().newInstance();
		return (CustomTag)o;
	}
    /**
     * @return Returns the clazz.
     * @throws ClassNotFoundException 
     */
    public Class<CustomTag> getClazz() throws ClassNotFoundException {
        if(clazz==null) {
            clazz=this.getClass().getClassLoader().loadClass(strClass);
		}
        return clazz;
    }
    
    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }
    /**
     * @return Returns the strClass.
     */
    public String getStrClass() {
        return strClass;
    }

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    @Override
    public CFXTagClass cloneReadOnly() {
        return new JavaCFXTagClass(name,strClass,clazz,true);
    }
    @Override
    public String getDisplayType() {
        return "Java";
    }
    @Override
    public String getSourceName() {
        return strClass;
    }
    
    @Override
    public boolean isValid() {
        try {
            return Reflector.isInstaneOf(getClazz(),CustomTag.class);
        } 
        catch (ClassNotFoundException e) {
            return false;
        }
    }
}