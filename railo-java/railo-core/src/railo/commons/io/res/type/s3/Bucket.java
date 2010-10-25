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
	
	/**
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "name:"+name+";creation:"+creation+";ownerDisplayName:"+ownerDisplayName+";ownerIdKey:"+ownerIdKey;
	}

	/**
	 *
	 * @see railo.commons.io.res.type.s3.S3Info#getLastModified()
	 */
	public long getLastModified() {
		return getCreation().getTime();
	}

	/**
	 * @see railo.commons.io.res.type.s3.S3Info#getSize()
	 */
	public long getSize() {
		return 0;
	}

	/**
	 * @see railo.commons.io.res.type.s3.S3Info#exists()
	 */
	public boolean exists() {
		return true;
	}

	/**
	 * @see railo.commons.io.res.type.s3.S3Info#isDirectory()
	 */
	public boolean isDirectory() {
		return true;
	}

	/**
	 * @see railo.commons.io.res.type.s3.S3Info#isFile()
	 */
	public boolean isFile() {
		return false;
	}
}