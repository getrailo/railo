package railo.runtime.type.scope;

import javax.servlet.ServletInputStream;

import railo.runtime.type.scope.FormImpl.Item;

// FUTURE add to interface, move last method to form interface
public interface FormUpload {
	public Item getUploadResource(String key);
	public Item[] getFileItems();
	public ServletInputStream getInputStream();
}
