package railo.runtime.net.s3;

import railo.commons.io.res.type.s3.S3;
import railo.commons.io.res.type.s3.S3Constants;


public class Properties {
	private String accessKeyId;
	private String secretAccessKey;
	private int defaultLocation=S3Constants.STORAGE_UNKNOW;
	/**
	 * @return the accessKeyId
	 */
	public String getAccessKeyId() {
		return accessKeyId;
	}
	/**
	 * @return the defaultLocation
	 */
	public int getDefaultLocation() {
		return defaultLocation;
	}
	/**
	 * @param defaultLocation the defaultLocation to set
	 */
	public void setDefaultLocation(String defaultLocation) {
		this.defaultLocation = S3.toIntStorage(defaultLocation,S3Constants.STORAGE_UNKNOW);
	}
	/**
	 * @param accessKeyId the accessKeyId to set
	 */
	public void setAccessKeyId(String accessKeyId) {
		this.accessKeyId = accessKeyId;
	}
	/**
	 * @return the secretAccessKey
	 */
	public String getSecretAccessKey() {
		return secretAccessKey;
	}
	/**
	 * @param secretAccessKey the secretAccessKey to set
	 */
	public void setSecretAccessKey(String secretAccessKey) {
		this.secretAccessKey = secretAccessKey;
	}
}
