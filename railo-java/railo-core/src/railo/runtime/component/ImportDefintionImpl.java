package railo.runtime.component;

import railo.commons.lang.StringUtil;

public class ImportDefintionImpl implements ImportDefintion {

	private String pack;
	private String name;
	private boolean wildcard;
	private String packAsPath;

	public ImportDefintionImpl(String pack, String name) {
		this.pack=pack;
		this.name=name;
		this.wildcard=name.equals("*");
		
	}

	public static ImportDefintion getInstance(String fullname,ImportDefintion defaultValue) {
		int index=fullname.lastIndexOf('.');
		if(index==-1) return defaultValue;
		String p=fullname.substring(0,index).trim();
		String n=fullname.substring(index+1,fullname.length()).trim();
		if(StringUtil.isEmpty(p) || StringUtil.isEmpty(n))
			return defaultValue;
		
		return new ImportDefintionImpl(p,n);
	}

	/**
	 * @return the wildcard
	 */
	public boolean isWildcard() {
		return wildcard;
	}

	/**
	 * @return the pack
	 */
	public String getPackage() {
		return pack;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	public String getPackageAsPath() {
		if(packAsPath==null) {
			packAsPath=pack.replace('.','/')+"/";
		}
		return packAsPath;
	}
	
	public String toString(){
		return pack+"."+name;
	}
	
}
