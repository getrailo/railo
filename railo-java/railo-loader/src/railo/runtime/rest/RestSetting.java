package railo.runtime.rest;

import railo.commons.io.res.Resource;

public interface RestSetting {
	public boolean skipCFCWithError();
	
	public Resource[] getCfcLocations();
}
