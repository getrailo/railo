package railo.runtime;

import railo.runtime.component.Property;
import railo.runtime.exp.PageException;
// FUTURE add to interface
public interface ComponentPro extends Component {
	/**
	 * @return properties of the component
	 */
	//public Property[] getProperties();
	
	/**
	 * return all properties from component
	 * @param onlyPeristent if true return only columns where attribute persistent is not set to false
	 * @return
	 */
	public Property[] getProperties(boolean onlyPeristent);
	public Property[] getProperties(boolean onlyPeristent, boolean includeBaseProperties);
	
	public void setProperty(Property property) throws PageException;
	
	public ComponentScope getComponentScope();
	
	public boolean contains(PageContext pc,Key key);
	
	public PageSource getPageSource();
	//public Member getMember(int access,Collection.Key key, boolean dataMember,boolean superAccess);
	
	public String getBaseAbsName();
	
	public boolean isBasePeristent();
	
	public boolean equalTo(String type);
	
	public String getWSDLFile();
}
