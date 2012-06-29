package railo.runtime.rest;

public interface RestSettings {
	public boolean getSkipCFCWithError();
	
	//public Resource[] getCfcLocations();

	/**
	 * return format of the request, ignored when format is defined as part of the call
	 * @return possible values are: UDF.RETURN_FORMAT_JSON, UDF.RETURN_FORMAT_WDDX, UDF.RETURN_FORMAT_SERIALIZE, UDF.RETURN_FORMAT_XML
	 */
	public int getReturnFormat();
}
