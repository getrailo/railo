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
	
	/**
	 * @see railo.runtime.cfx.customtag.CFXTagClass#newInstance()
	 */
	public CustomTag newInstance() throws CFXTagException {
		try {
			return _newInstance();
		} catch (Throwable e) {
			throw new CFXTagException(e);
		}
	}

	/**
	 * create and return an new CustomTag Object
	 * @throws ClassNotFoundException
	 * @see railo.runtime.cfx.customtag.CFXTagClass#newInstance()
	 */
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

    /**
     * @see railo.runtime.cfx.customtag.CFXTagClass#isReadOnly()
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * @see railo.runtime.cfx.customtag.CFXTagClass#cloneReadOnly()
     */
    public CFXTagClass cloneReadOnly() {
        return new JavaCFXTagClass(name,strClass,clazz,true);
    }
    /**
     * @see railo.runtime.cfx.customtag.CFXTagClass#getDisplayType()
     */
    public String getDisplayType() {
        return "Java";
    }
    /**
     * @see railo.runtime.cfx.customtag.CFXTagClass#getSourceName()
     */
    public String getSourceName() {
        return strClass;
    }
    
    /**
     * @see railo.runtime.cfx.customtag.CFXTagClass#isValid()
     */
    public boolean isValid() {
        try {
            return Reflector.isInstaneOf(getClazz(),CustomTag.class);
        } 
        catch (ClassNotFoundException e) {
            return false;
        }
    }
}