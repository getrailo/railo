package railo.runtime.services;

import coldfusion.server.ServiceMetaData;

public class EmptyServiceMetaData implements ServiceMetaData {

	@Override
	public int getPropertyCount() {
		return 0;
	}

	@Override
	public String getPropertyLabel(int arg0) {
		return null;
	}

	@Override
	public String getPropertyType(int arg0) {
		return null;
	}

	@Override
	public boolean exists(String arg0) {
		return false;
	}

}
