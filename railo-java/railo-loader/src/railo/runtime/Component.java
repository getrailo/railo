package railo.runtime;


import java.util.Iterator;
import java.util.Set;

import railo.commons.lang.types.RefBoolean;
import railo.runtime.component.Member;
import railo.runtime.component.Property;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;
import railo.runtime.type.Objects;
import railo.runtime.type.Struct;
import railo.runtime.type.UDF;
import railo.runtime.type.UDFProperties;
/**
 * interface for a Component
 */
public interface Component extends Struct,Objects,CIObject {
    
    /**
     * Constant for Access Mode Remote
     */
    public static final int ACCESS_REMOTE = 0;

    /**
     * Constant for Access Mode Public
     */
    public static final int ACCESS_PUBLIC = 1;

    /**
     * Constant for Access Mode Package
     */
    public static final int ACCESS_PACKAGE = 2;

    /**
     * Constant for Access Mode Private
     */
    public static final int ACCESS_PRIVATE = 3;
    
    /**
     * Field <code>ACCESS_COUNT</code>
     */
    public static final int ACCESS_COUNT=4;

	public static final int MODIFIER_NONE = 0;
	public static final int MODIFIER_FINAL = 1;
	public static final int MODIFIER_ABSTRACT = 2;
    
    /**
     * returns java class to the component interface (all UDFs),
     * this class is generated dynamic when used
     * @param isNew 
     * @throws PageException
     * @deprecated use instead <code>getJavaAccessClass(PageContext pc,RefBoolean isNew,boolean writeLog, boolean takeTop, boolean create, boolean supressWSbeforeArg)</code>
     */
    public Class getJavaAccessClass(RefBoolean isNew) throws PageException;
    
    public Class getJavaAccessClass(PageContext pc,RefBoolean isNew,boolean writeLog, boolean takeTop, boolean create, boolean supressWSbeforeArg) throws PageException;
        
    
    
    /**
     * @return Returns the display name.
     */
    public abstract String getDisplayName();

    /**
     * @return Returns the Extends.
     */
    public abstract String getExtends();

    /**
     * @return Returns the Hint.
     */
    public abstract String getHint();

    /**
     * @return Returns the Name.
     */
    public abstract String getName();

    /**
     * @return Returns the Name.
     */
    public abstract String getCallName();

    /**
     * @return Returns the Name.
     */
    public abstract String getAbsName();

    /**
     * @return Returns the output.
     */
    public abstract boolean getOutput();

    /**
     * check if Component is instance of this type
     * @param type type to compare as String
     * @return is instance of this type
     */
    public abstract boolean instanceOf(String type);

    /**
     * check if value is a valid access modifier constant
     * @param access
     * @return is valid access
     */
    public abstract boolean isValidAccess(int access);
    
    /**
     * is a persistent component (orm)
     * @return
     */
    public boolean isPersistent();
    
    
    /**
     * has accessors set
     * @return
     */
    public boolean isAccessors();
	
    

    /**
     * returns Meta Data to the Component
     * @param pc
     * @return meta data to component
     * @throws PageException
     */
    public Struct getMetaData(PageContext pc) throws PageException;

    public Object getMetaStructItem(Collection.Key name);
    
    
    /**
     * call a method of the component with no named arguments
     * @param pc PageContext
     * @param key name of the method
     * @param args Arguments for the method
     * @return return result of the method
     * @throws PageException
     */
    public abstract Object call(PageContext pc, String key, Object[] args) throws PageException;
    
    /**
     * call a method of the component with named arguments
     * @param pc PageContext
     * @param key name of the method
     * @param args Named Arguments for the method
     * @return return result of the method
     * @throws PageException
     */
    public abstract Object callWithNamedValues(PageContext pc, String key, Struct args) throws PageException;
    
    
	/**
	 * return all properties from component
	 * @param onlyPeristent if true return only columns where attribute persistent is not set to false
	 * @deprecated use instead <code>getProperties(boolean onlyPeristent, boolean includeBaseProperties, boolean preferBaseProperties, boolean inheritedMappedSuperClassOnly)</code>
	 */
	public Property[] getProperties(boolean onlyPeristent);
	
	/**
	 * return all properties from component
	 * @param onlyPeristent if true return only columns where attribute persistent is not set to false
	 * @return
	 */
	public Property[] getProperties(boolean onlyPeristent, boolean includeBaseProperties, boolean preferBaseProperties, boolean inheritedMappedSuperClassOnly);
	

	public void setProperty(Property property) throws PageException;
	
	public ComponentScope getComponentScope();
	
	public boolean contains(PageContext pc,Key key);
	
	public PageSource getPageSource();
	//public Member getMember(int access,Collection.Key key, boolean dataMember,boolean superAccess);
	
	public String getBaseAbsName();
	
	public boolean isBasePeristent();
	
	public boolean equalTo(String type);
	
	public String getWSDLFile();
	
	public void setEntity(boolean entity);
	public boolean isEntity();
	public Component getBaseComponent();
	
	

    public void registerUDF(String key, UDF udf);
    
    public void registerUDF(Collection.Key key, UDF udf);
    
    public void registerUDF(String key, UDFProperties props);
    
    public void registerUDF(Collection.Key key, UDFProperties props);
    
    

	// access
    Set<Key> keySet(int access);
    Object call(PageContext pc, int access, Collection.Key name, Object[] args) throws PageException;
	Object callWithNamedValues(PageContext pc, int access, Collection.Key name, Struct args) throws PageException;
	int size(int access);
	Collection.Key[] keys(int access);

	Iterator<Collection.Key> keyIterator(int access);
	Iterator<String> keysAsStringIterator(int access);
	

	Iterator<Entry<Key, Object>> entryIterator(int access);
	Iterator<Object> valueIterator(int access);
	
	Object get(int access, Collection.Key key) throws PageException;
	Object get(int access, Collection.Key key, Object defaultValue);
	DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp, int access);
	boolean contains(int access,Key name);
	Member getMember(int access,Collection.Key key, boolean dataMember,boolean superAccess);
}