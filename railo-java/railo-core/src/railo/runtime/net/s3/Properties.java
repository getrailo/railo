package railo.runtime.net.s3;

import railo.commons.io.res.type.s3.S3Constants;


public class Properties {
	private String accessKeyId;
	private String secretAccessKey;
	private String host=S3Constants.HOST;
	/**
	 * @return the accessKeyId
	 */
	public String getAccessKeyId() {
		return accessKeyId;
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
	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}
	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}
}
