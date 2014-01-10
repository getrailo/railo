package railo.runtime.net.s3;

import railo.commons.io.res.type.s3.S3;
import railo.commons.io.res.type.s3.S3Constants;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;


public final class PropertiesImpl implements Properties {
	private String accessKeyId;
	private String secretAccessKey;
	private int defaultLocation=S3Constants.STORAGE_UNKNOW;
	private String host=S3.HOST;
	
	

	public Struct toStruct() {
		Struct sct=new StructImpl();

		sct.setEL("accessKeyId", accessKeyId);
		sct.setEL("awsSecretKey", accessKeyId);
		sct.setEL("defaultLocation", S3.toStringStorage(defaultLocation,""));
		sct.setEL("host", host);
		
		
		return sct;
	}
	
	/**
	 * @return the accessKeyId
	 */
	public String getAccessKeyId() {
		return accessKeyId;
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
	
	@Override
	public String toString(){
		return "accessKeyId:"+accessKeyId+";defaultLocation:"+defaultLocation+";host:"+host+";secretAccessKey:"+secretAccessKey;
	}
}
