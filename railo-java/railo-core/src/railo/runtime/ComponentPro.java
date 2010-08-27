package railo.runtime;

import railo.runtime.component.Property;

public interface ComponentPro extends Component {
	/**
	 * @return properties of the component
	 */
	public Property[] getProperties();
	
	public void setProperty(Property property);
	
	public ComponentScope getComponentScope();
	
	public boolean contains(PageContext pc,Key key);
	
	public PageSource getPageSource();
	//public Member getMember(int access,Collection.Key key, boolean dataMember,boolean superAccess);
	
	public String getBaseAbsName();
}
