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


import railo.commons.lang.types.RefBoolean;
import railo.runtime.component.Property;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;
import railo.runtime.type.Objects;
import railo.runtime.type.Struct;
import railo.runtime.type.UDF;
import railo.runtime.type.UDFProperties;
/**
 * interface for a Component
 */
public interface Component extends Struct,Objects,CFObject {
    
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
    
    /**
     * returns java class to the component interface (all UDFs),
     * this class is generated dynamic when used
     * @param isNew 
     * @throws PageException
     * FUTURE deprecated
     */
    public Class getJavaAccessClass(RefBoolean isNew) throws PageException;
    // FUTURE public Class getJavaAccessClass(PageContext pc,RefBoolean isNew) throws PageException;

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
     * returns Meta Data to the Component
     * @param pc
     * @return meta data to component
     * @throws PageException
     */
    public abstract Struct getMetaData(PageContext pc) throws PageException;
    
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
	 * @return
	 */
	public Property[] getProperties(boolean onlyPeristent);// FUTURE deprecated

	public void setProperty(Property property) throws PageException;
	
	public ComponentScope getComponentScope();
	
	public boolean contains(PageContext pc,Key key);
	
	public PageSource getPageSource();
	//public Member getMember(int access,Collection.Key key, boolean dataMember,boolean superAccess);
	
	public String getBaseAbsName();
	
	public boolean isBasePeristent();
	
	public boolean equalTo(String type);
	
	public String getWSDLFile();
	

	

    public void registerUDF(String key, UDF udf);
    
    public void registerUDF(Collection.Key key, UDF udf);
    
    public void registerUDF(String key, UDFProperties props);
    
    public void registerUDF(Collection.Key key, UDFProperties props);
}