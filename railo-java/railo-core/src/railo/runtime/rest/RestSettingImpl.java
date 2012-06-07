package railo.runtime.rest;

import railo.commons.io.res.Resource;

public class RestSettingImpl implements RestSetting {

	private final Resource[] cfcLocations;
	private final boolean skipCFCWithError;

	public RestSettingImpl(Resource[] cfcLocations, boolean skipCFCWithError) {
		this.cfcLocations=cfcLocations;
		this.skipCFCWithError=skipCFCWithError;
	}

	@Override
	public boolean skipCFCWithError() {
		return skipCFCWithError;
	}

	@Override
	public Resource[] getCfcLocations() {
		return cfcLocations;
	}

}
