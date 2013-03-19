package railo.deployer.filter;

import railo.commons.io.res.Resource;
import railo.runtime.exp.PageException;
import railo.runtime.type.util.ListUtil;

/**
 * Die Klasse CFMLFilter implementiert das Interface Filter, 
 * die Klasse prueft bei einem uebergebenen File Objekt, 
 * ob dessen Extension mit denen die dem Konstruktor mitgegeben wurden uebereinstimmen.
 */
public final class CFMLFilter implements Filter {
	
	private String[] extensions;
	
	/**
	 * Konstruktor von CFMLFilter, dem Konstruktor wird ein String Array uebergeben mit Extensions die geprueft werden sollen,
	 * wie z.B. {"cfml","cfm"}.
	 * @param extensions Extensions die geprueft werden sollen.
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
			arr = ListUtil.toStringArray(ListUtil.listToArray(file.getName(), '.'));
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