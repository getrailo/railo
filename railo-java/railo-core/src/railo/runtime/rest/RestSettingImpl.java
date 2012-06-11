package railo.runtime.rest;

public class RestSettingImpl implements RestSetting {

	private final boolean skipCFCWithError;
	private int returnFormat;

	public RestSettingImpl(boolean skipCFCWithError, int returnFormat) {
		this.skipCFCWithError=skipCFCWithError;
		this.returnFormat=returnFormat;
	}

	@Override
	public boolean getSkipCFCWithError() {
		return skipCFCWithError;
	}

	@Override
	public int getReturnFormat() {
		return returnFormat;
	}

}
