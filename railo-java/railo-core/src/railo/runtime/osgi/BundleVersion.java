package railo.runtime.osgi;

import railo.commons.lang.StringUtil;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.util.ListUtil;

public class BundleVersion {
	
	private final int major;
	private final int minor;
	private final int micro;
	private final String qualifier;
	private String str;
	
	public BundleVersion(String str) {
		String[] arr;
		try {
			arr = ListUtil.toStringArrayTrim(ListUtil.listToArray(str.trim(), '.'));
		}
		catch (PageException e) {
			arr=new String[0]; // should not happen
		}
		
		if(arr.length==0) {
			major=0;
			minor=0;
			micro=0;
			qualifier=null;
		}
		else if(arr.length==1) {
			major=Caster.toIntValue(arr[0],0);
			minor=0;
			micro=0;
			qualifier=null;
		}
		else if(arr.length==2) {
			major=Caster.toIntValue(arr[0],0);
			minor=Caster.toIntValue(arr[1],0);
			micro=0;
			qualifier=null;
		}
		else if(arr.length==3) {
			major=Caster.toIntValue(arr[0],0);
			minor=Caster.toIntValue(arr[1],0);
			micro=Caster.toIntValue(arr[2],0);
			qualifier=null;
		}
		else {
			major=Caster.toIntValue(arr[0],0);
			minor=Caster.toIntValue(arr[1],0);
			micro=Caster.toIntValue(arr[2],0);
			qualifier=arr[3];
		}
		
		StringBuilder sb = new StringBuilder()
		.append(major)
		.append('.')
		.append(minor)
		.append('.')
		.append(micro);
		
		if(qualifier!=null)
			sb.append('.').append(qualifier);
		this.str=sb.toString();
		
		
	}
	public BundleVersion() {
		major=0;
		minor=0;
		micro=0;
		qualifier=null;
	}
	@Override
	public String toString() {
		return str;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof BundleVersion)) return false;
		BundleVersion other=(BundleVersion) obj;
		if(major!=other.major || minor!=other.minor || micro!=other.micro) return false;
		if(qualifier==null) {
			return StringUtil.isEmpty(other.qualifier);
		}
		return qualifier.equals(other.qualifier);
	}
}
