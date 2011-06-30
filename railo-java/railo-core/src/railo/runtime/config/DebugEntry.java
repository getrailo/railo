package railo.runtime.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import railo.commons.digest.MD5;
import railo.runtime.type.List;
import railo.runtime.type.Struct;

public class DebugEntry {

	private final String id;
	private final String type;
	private final String ipRange;
	private final String label;
	private final Struct custom;
	private final boolean readOnly;
	private final String path;
	private final String fullname;

	public DebugEntry(String id, String type, String ipRange, String label, String path, String fullname, Struct custom) {
		this(id,type,ipRange,label,path,fullname,custom,false);
	}
	
	private DebugEntry(String id, String type, String ipRange, String label, String path, String fullname, Struct custom, boolean readOnly) {
		this.id=id;
		this.type=type;
		this.ipRange=ipRange;
		this.label=label;
		this.custom=custom;
		this.readOnly=readOnly;
		this.path=path;
		this.fullname=fullname;
	}
	
	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @return the fullname
	 */
	public String getFullname() {
		return fullname;
	}

	/**
	 * @return the readOnly
	 */
	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the ipRange
	 */
	public String getIpRange() {
		return ipRange;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return the custom
	 */
	public Struct getCustom() {
		return (Struct) custom.duplicate(false);
	}

	public DebugEntry duplicate(boolean readOnly) {
		DebugEntry de = new DebugEntry(id, type, ipRange, label, path,fullname,custom,readOnly);
		return de;
	}
	
	public static String organizeIPRange(String ipRange) {
		String[] arr = List.trim(List.trimItems(List.listToStringArray(ipRange, ',')));
		Set<String> set=new HashSet<String>();
		for(int i=0;i<arr.length;i++){
			set.add(arr[i]);
		}
		arr=set.toArray(new String[set.size()]);
		Arrays.sort(arr);
		return List.arrayToList(arr, ",");
	}
	

	public static String ipRangeToId(String ipRange) {
		ipRange=organizeIPRange(ipRange);
		try {
			return MD5.getDigestAsString(ipRange);
		} catch (IOException e) {
			return ipRange;
		}
	}
	
	/*public static void main(String[] args) {
		print.o(ipRangeToId("*,127.0.0.1"));
		print.o(ipRangeToId(",,,*,127.0.0.1"));
		print.o(ipRangeToId(",,,*,127.0.0.1,*"));
	}*/
	

}
