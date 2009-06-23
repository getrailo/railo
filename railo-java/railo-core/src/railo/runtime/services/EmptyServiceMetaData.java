

package railo.runtime.services;

import coldfusion.server.ServiceMetaData;

public class EmptyServiceMetaData implements ServiceMetaData {

	public int getPropertyCount() {
		return 0;
	}

	public String getPropertyLabel(int arg0) {
		return null;
	}

	public String getPropertyType(int arg0) {
		return null;
	}

	public boolean exists(String arg0) {
		return false;
	}

}
