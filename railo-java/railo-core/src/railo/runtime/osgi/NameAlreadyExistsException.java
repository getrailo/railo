package railo.runtime.osgi;

import java.io.IOException;

import railo.commons.io.res.Resource;

public class NameAlreadyExistsException extends IOException {

	private String name;
	private Resource file;
	private long size;

	public NameAlreadyExistsException(String name, Resource file, long size) { 
		super("a entry with name "+name+" is already assigned to the Zip File");
		this.name=name;
		this.file=file;
		this.size=size;
	}

	public String getName() {
		return name;
	}
	
	public Resource getFile() {
		return file;
	}
	
	public long getSize() {
		return size;
	}
}