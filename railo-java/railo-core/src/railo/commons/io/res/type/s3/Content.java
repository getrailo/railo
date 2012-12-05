package railo.commons.io.res.type.s3;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.xml.sax.SAXException;

public final class Content implements S3Info {
	
	
	private String key;
	private long lastModified;
	private String eTag;
	private long size;
	private String storageClass;
	private String ownerIdKey;
	private String ownerDisplayName;
	private String bucketName;
	private final S3 s3;
	private boolean truncated;
	
	/**
	 * @return the truncated
	 */
	public boolean isTruncated() {
		return truncated;
	}
	/**
	 * @return the bucketName
	 */
	public String getBucketName() {
		return bucketName;
	}
	/**
	 * @param bucketName the bucketName to set
	 */
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}
	public Content(S3 s3) {
		this.s3=s3;
	}
	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}
	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}
	/**
	 * @return the lastModified
	 */
	public long getLastModified() {
		return lastModified;
	}
	/**
	 * @param lastModified the lastModified to set
	 */
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}
	/**
	 * @return the eTag
	 */
	public String getETag() {
		return eTag;
	}
	/**
	 * @param tag the eTag to set
	 */
	public void setETag(String tag) {
		eTag = tag;
	}
	/**
	 * @return the size
	 */
	public long getSize() {
		return size;
	}
	/**
	 * @param size the size to set
	 */
	public void setSize(long size) {
		this.size = size;
	}
	/**
	 * @return the storageClass
	 */
	public String getStorageClass() {
		return storageClass;
	}
	/**
	 * @param storageClass the storageClass to set
	 */
	public void setStorageClass(String storageClass) {
		this.storageClass = storageClass;
	}
	/**
	 * @return the ownerIdKey
	 */
	public String getOwnerIdKey() {
		return ownerIdKey;
	}
	/**
	 * @param ownerIdKey the ownerIdKey to set
	 */
	public void setOwnerIdKey(String ownerIdKey) {
		this.ownerIdKey = ownerIdKey;
	}
	/**
	 * @return the ownerDisplayName
	 */
	public String getOwnerDisplayName() {
		return ownerDisplayName;
	}
	/**
	 * @param ownerDisplayName the ownerDisplayName to set
	 */
	public void setOwnerDisplayName(String ownerDisplayName) {
		this.ownerDisplayName = ownerDisplayName;
	}
	
	public String getLink(int secondsValid) throws InvalidKeyException, NoSuchAlgorithmException, IOException {
		return s3.getObjectLink(bucketName, key, secondsValid);
	}
	
	public InputStream getInputStream() throws InvalidKeyException, NoSuchAlgorithmException, IOException, SAXException {
		return s3.getInputStream(bucketName, key);
	}
	
	
	@Override
	public String toString() {
		return "eTag:"+eTag+";key:"+key+";ownerDisplayName:"+ownerDisplayName+";ownerIdKey:"+ownerIdKey+";size:"+size+";storageClass:"+storageClass+";";
	}
	
	@Override
	public boolean exists() {
		return true;
	}
	
	@Override
	public boolean isDirectory() {
		return getSize()==0 && getKey().endsWith("/");
	}
	
	@Override
	public boolean isFile() {
		return getSize()>0 || !getKey().endsWith("/");
	}
	/**
	 * @param truncated the truncated to set
	 */
	public void setTruncated(boolean truncated) {
		this.truncated = truncated;
	}
	
	
}
