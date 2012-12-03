package railo.runtime;

import railo.runtime.component.Property;

public interface ComponentPro extends Component {
	public Property[] getProperties(boolean onlyPeristent, boolean includeBaseProperties, boolean preferBaseProperties, boolean inheritedMappedSuperClassOnly);
}
