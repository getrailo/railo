package railo.runtime.type;

import railo.runtime.Component;

public interface ComponentMethod extends UDF {

	public Component getComponent();
	public UDF getUserDefinedFunction();
}
