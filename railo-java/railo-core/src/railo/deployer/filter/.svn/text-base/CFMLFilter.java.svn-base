package railo.deployer.filter;

import railo.commons.io.res.Resource;
import railo.runtime.exp.PageException;
import railo.runtime.type.List;

/**
 * Die Klasse CFMLFilter implementiert das Interface Filter, 
 * die Klasse prüft bei einem übergebenen File Objekt, 
 * ob dessen Extension mit denen die dem Konstruktor mitgegeben wurden übereinstimmen.
 */
public final class CFMLFilter implements Filter {
	
	private String[] extensions;
	
	/**
	 * Konstruktor von CFMLFilter, dem Konstruktor wird ein String Array übergeben mit Extensions die geprüft werden sollen,
	 * wie z.B. {"cfml","cfm"}.
	 * @param extensions Extensions die geprüft werden sollen.
	 */
	public CFMLFilter(String[] extensions) {
		this.extensions=extensions;
		for(int i=0;i<extensions.length;i++) {
			extensions[i]=extensions[i].toLowerCase();
		}
	}
	
	public boolean isValid(Resource file) {
		String[] arr;
		try {
			arr = List.toStringArray(List.listToArray(file.getName(), '.'));
		} 
		catch (PageException e) {
			return false;
		}
		String ext=arr[arr.length-1].toLowerCase();
		for(int i=0;i<extensions.length;i++) {
			if(extensions[i].equals(ext))
				return true;
		}
		return false;
	}
}