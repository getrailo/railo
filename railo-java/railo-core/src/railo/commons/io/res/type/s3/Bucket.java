package railo.commons.io.res.type.s3;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.xml.sax.SAXException;

import railo.runtime.type.dt.DateTime;

public final class Bucket implements S3Info {

	private final S3 s3;
	private String name;
	private DateTime creation;
	private String ownerIdKey;
	private String ownerDisplayName;
	
	public Bucket(S3 s3) {
		this.s3 = s3;
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

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the creation
	 */
	public DateTime getCreation() {
		return creation;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param creation the creation to set
	 */
	public void setCreation(DateTime creation) {
		this.creation = creation;
	}
	
	public Content[] listContent(String prefix,String marker,int maxKeys) throws InvalidKeyException, MalformedURLException, NoSuchAlgorithmException, IOException, SAXException {
		return s3.listContents(name, prefix, marker, maxKeys);
	}
	
	@Override
	public String toString() {
		return "name:"+name+";creation:"+creation+";ownerDisplayName:"+ownerDisplayName+";ownerIdKey:"+ownerIdKey;
	}

	@Override
	public long getLastModified() {
		return getCreation().getTime();
	}

	@Override
	public long getSize() {
		return 0;
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public boolean isDirectory() {
		return true;
	}

	@Override
	public boolean isFile() {
		return false;
	}
}