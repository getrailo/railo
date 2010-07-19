package railo.loader.util;


import java.io.File;
import java.io.FileFilter;

/**
 * Filter für die <code>listFiles</code> Methode des FIle Objekt, 
 * zum filtern von FIles mit einer bestimmten Extension.
 */
public final class ExtensionFilter implements FileFilter {
	
	private final String[] extensions;
	private final boolean allowDir;
	private final boolean ignoreCase;
    //private int extLen;
	

	/**
	 * Konstruktor des Filters
	 * @param extension Endung die geprüft werden soll.
	 */
	public ExtensionFilter(String extension) {
		this(new String[]{extension},false,true);
	}

	/**
	 * Konstruktor des Filters
	 * @param extension Endung die geprüft werden soll.
	 */
	public ExtensionFilter(String extension, boolean allowDir) {
		this(new String[]{extension},allowDir,true);
	}
	
	public ExtensionFilter(String[] extensions) {
		this(extensions,false,true);
	}
	
	public ExtensionFilter(String[] extensions, boolean allowDir) {
		this(extensions,allowDir,true);
	}

	
	public ExtensionFilter(String[] extensions, boolean allowDir, boolean ignoreCase) {
		for(int i=0;i<extensions.length;i++) {
			if(!extensions[i].startsWith("."))
	            extensions[i]="."+extensions[i];
			if(ignoreCase)extensions[i]=extensions[i].toLowerCase();
		}
		this.extensions=extensions;
    	this.allowDir=allowDir;
    	this.ignoreCase=ignoreCase;
	}

	/**
	 * @see java.io.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File res) {
		if(res.isDirectory()) return allowDir;
		if(res.exists()) {
			String name=ignoreCase?res.getName().toLowerCase():res.getName();
			for(int i=0;i<extensions.length;i++) {
				if(name.endsWith(extensions[i]))
					return true;
			}
		}
		return false;
	}
	
    /**
     * @return Returns the extension.
     */
    public String[] getExtensions() {
        return extensions;
    }
}