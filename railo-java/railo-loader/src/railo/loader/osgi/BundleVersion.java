package railo.loader.osgi;

import java.util.StringTokenizer;

public class BundleVersion {
	
	private final int major;
	private final int minor;
	private final int micro;
	private final String qualifier;
	private String str;
	
	public BundleVersion(String str){
		StringTokenizer st=new StringTokenizer(str,".");
		
		// major
		if(st.hasMoreTokens()) {
			int tmp=0;
			try{
				tmp=Integer.parseInt(st.nextToken());
			}
			catch(Throwable t){}
			major=tmp;
		}
		else major=0;
		
		// minor
		if(st.hasMoreTokens()) {
			int tmp=0;
			try{
				tmp=Integer.parseInt(st.nextToken());
			}
			catch(Throwable t){}
			minor=tmp;
		}
		else minor=0;
		
		// micro
		if(st.hasMoreTokens()) {
			int tmp=0;
			try{
				tmp=Integer.parseInt(st.nextToken());
			}
			catch(Throwable t){}
			micro=tmp;
		}
		else micro=0;
		
		// qualifier
		if(st.hasMoreTokens()) {
			qualifier=st.nextToken();
		}
		else qualifier=null;
		
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
	@Override
	public String toString() {
		
		return str;
	}
}
