package railo.runtime.net.s3;

import railo.runtime.type.Struct;


public interface Properties {
	
	public Struct toStruct();
	
	/**
	 * @return the accessKeyId
	 */
	public String getAccessKeyId();
	
	/**
	 * @return the host
	 */
	public String getHost();
	/**
	 * @param host the host to set
	 */
	//public void setHost(String host);
	
	/**
	 * @return the defaultLocation
	 */
	public int getDefaultLocation();
	
	/**
	 * @param defaultLocation the defaultLocation to set
	 */
	//public void setDefaultLocation(String defaultLocation);
	
	/**
	 * @param accessKeyId the accessKeyId to set
	 */
	//public void setAccessKeyId(String accessKeyId);
	
	/**
	 * @return the secretAccessKey
	 */
	public String getSecretAccessKey();
	
	/**
	 * @param secretAccessKey the secretAccessKey to set
	 */
	//public void setSecretAccessKey(String secretAccessKey);
	
}
