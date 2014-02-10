package railo.runtime.osgi;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;

import railo.print;
import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.ResourcesImpl;
import railo.commons.lang.StringUtil;
import railo.runtime.type.util.ListUtil;



public class ManifestUtil {

	private static final int DEFAULT_MAX_LINE_SIZE=100;
	private static final Set<String> DEFAULT_MAIN_FILTER=new HashSet<String>();
	static {
		DEFAULT_MAIN_FILTER.add("Manifest-Version");
	}
	private static final Set<String> DEFAULT_INDIVIDUAL_FILTER=new HashSet<String>();
	static {
		DEFAULT_INDIVIDUAL_FILTER.add("Name");
	}

	public static String toString(Manifest manifest, int maxLineSize, Set<String> mainSectionIgnore,Set<String> individualSectionIgnore) {
		if(maxLineSize<0) maxLineSize=DEFAULT_MAX_LINE_SIZE;
		StringBuilder msb=new StringBuilder();
		Attributes main = manifest.getMainAttributes();
		
		// prepare ignores
		if(mainSectionIgnore==null)mainSectionIgnore=DEFAULT_MAIN_FILTER;
		else mainSectionIgnore.addAll(DEFAULT_MAIN_FILTER);
		if(individualSectionIgnore==null)individualSectionIgnore=DEFAULT_INDIVIDUAL_FILTER;
		else individualSectionIgnore.addAll(DEFAULT_INDIVIDUAL_FILTER);
		
		
		
		// Manifest-Version comes first
		add(msb,"Manifest-Version",main.getValue("Manifest-Version"),"1.0");
		// all other main attributes 
		printSection(msb,main,maxLineSize,mainSectionIgnore);
		
		
		// individual entries
		
		Map<String, Attributes> entries = manifest.getEntries();
		if(entries!=null && entries.size()>0) {
			Iterator<Entry<String, Attributes>> it = entries.entrySet().iterator();
			Entry<String, Attributes> e;
			StringBuilder sb;
			while(it.hasNext()){
				e = it.next();
				sb=new StringBuilder();
				printSection(sb,e.getValue(),maxLineSize,individualSectionIgnore);
				if(sb.length()>0) {
					msb.append('\n'); // new section need a empty line
					add(msb,"Name", e.getKey(), null);
					msb.append(sb);
				}
			}
		}
		
		
		
		
		
		return msb.toString();
	}

	private static void printSection(StringBuilder sb, Attributes attrs, int maxLineSize, Set<String> ignore) {
		Iterator<Entry<Object, Object>> it = attrs.entrySet().iterator();
		Entry<Object, Object> e;
		Name name;
		String value;
		while(it.hasNext()){
			e = it.next();
			name=(Name)e.getKey();
			value=(String)e.getValue();
			if(value.length()>maxLineSize) value=split(value,maxLineSize);
			if(ignore!=null && ignore.contains(name.toString())) continue;
			
			add(sb,name.toString(),value,null);
		}
	}

	private static String split(String value, int max) {
		StringBuilder sb=new StringBuilder();
		int index=0;
		while(index+max <= value.length()){
			if(sb.length()>0)sb.append("\n ");
			sb.append(value.substring(index,index+max));
			index=index+max;
		}
		if(index<value.length()) {
			if(sb.length()>0)sb.append("\n ");
			sb.append(value.substring(index,value.length()));
		}
		return sb.toString();
	}
	
	private static void add(StringBuilder sb, String name, String value, String defaultValue) {
		if(value==null) {
			if(defaultValue==null) return;
			value=defaultValue;
		}
		sb.append(name).append(": ").append(value).append('\n');
	}
	

	public static void removeFromList(Attributes attrs,String key, String valueToRemove) { 
		String val = attrs.getValue(key);
		if(StringUtil.isEmpty(val)) return;
		StringBuilder sb=new StringBuilder();
		boolean removed=false;
		
		boolean wildcard=false;
		if(valueToRemove.endsWith(".*")) {
			wildcard=true;
			valueToRemove=valueToRemove.substring(0,valueToRemove.length()-1);
		}
		
		try {
			
			Iterator<String> it = toList(val).iterator();//ListUtil.toStringArray(ListUtil.listToArrayTrim(val, ','));
			String str;
			while(it.hasNext()){
				str=it.next();
				if(wildcard?str.startsWith(valueToRemove):(str.equals(valueToRemove) || ListUtil.first(str, ";").trim().equals(valueToRemove))) {
					print.e(str);
					removed=true;
					continue;
				}
				
				if(sb.length()>0)sb.append(',');
				sb.append(str);
			}
		}
		catch (Throwable e) {}
		if(removed) attrs.putValue(key, sb.toString());
		
	}
	
	private static List<String> toList(String val) {
		List<String> list=new ArrayList<String>();
		int len=val.length();
		int inside=0;
		char c;
		int begin=0;
		for(int i=0;i<len;i++){
			c=val.charAt(i);
			if(c=='"') {
				if(inside=='"')inside=0;
				else if(inside==0)inside='"';
			}
			else if(c=='\'') {
				if(inside=='\'')inside=0;
				else if(inside==0)inside='\'';
			}
			else if(c==',' && inside==0) {
				if(begin<i)list.add(val.substring(begin,i));
				begin=i+1;
			}
		}
		if(begin<len)
			list.add(val.substring(begin));
		
		
		return list;
	}
	
	/*public static void main(String[] args) throws IOException {
		String str=new StringBuilder()
		.append("Manifest-Version: 1.0\n")
		.append("Import-Package: a.b.c,javax.servlet;version=1.2.1 , javax.servlet.http\n")
		.append("Implementation-Vendor: abc\n")
		.append(" def\n")
		.toString();

		//print.e(str);
		Manifest m = new Manifest(new ByteArrayInputStream(str.getBytes()));
		m.getMainAttributes().putValue("Created-By", "Micha");
		
		//removeFromList(m.getMainAttributes(),"Import-Package","javax.servlet.http");
		removeFromList(m.getMainAttributes(),"Import-Package","javax.*");
		
		Set<String> filter=new HashSet<String>();
		filter.add("Java-Bean");
		
		str=toString(m,150,null,filter);
		print.e(str);
		//print.e(new Manifest(new ByteArrayInputStream(str.getBytes())).getMainAttributes().getValue("Implementation-Title"));
	}*/

	
	public static void main(String[] args) throws IOException {
		Resource res = ResourcesImpl.getFileResourceProvider()
		.getResource("/Users/mic/Tmp2/mysql-connector-java-bin/META-INF/MANIFEST.MF");
		
		//String str = IOUtil.toString(res, "utf-8");
		InputStream is=null;
		try{
			Manifest m = new Manifest(is=res.getInputStream());
			ManifestUtil.removeFromList(m.getMainAttributes(),"Import-Package","javax.*"); 
			//ManifestUtil.removeFromList(m.getMainAttributes(),"Import-Package","org.osgi.*");
			
			String str = toString(m,100,null,null);
			print.e(str);
		}
		finally{
			IOUtil.closeEL(is);
		}
	}
	
	/*public static void main(String[] args) throws IOException {
		String str=new StringBuilder()
		.append("Manifest-Version: 1.0\n")
		.append("Specification-Title: Java Platform API Specification\n")
		.append("Specification-Version: 1.4\n")
		.append("Implementation-Title: Java Runtime Environment\n")
		.append("Implementation-Version: 1.4.0-rc\n")
		.append("Created-By: 1.4.0-rc (Sun Microsystems Inc.)\n")
		.append("Implementation-Vendor: abc\n")
		.append(" def\n")
		.append("Specification-Vendor: Sun Microsystems, Inc.\n")
		.append("\n")
		.append("Name: javax/swing/JScrollPane.class\n")
		.append("Java-Bean: True\n")
		.append("\n")
		.append("Name: javax/swing/JCheckBoxMenuItem.class\n")
		.append("Java-Bean: True\n")
		.append("Whatever: True\n")
		.append("\n")
		.append("Name: javax/swing/JTabbedPane.class\n")
		.append("Java-Bean: True\n")
		.append("\n")
		.append("Name: javax/swing/JMenuItem.class\n")
		.append("Java-Bean: True\n").toString();

		//print.e(str);
		Manifest m = new Manifest(new ByteArrayInputStream(str.getBytes()));
		m.getMainAttributes().putValue("Created-By", "Micha");
		
		
		
		Set<String> filter=new HashSet<String>();
		filter.add("Java-Bean");
		
		str=toString(m,10,null,filter);
		print.e(str);
		print.e(new Manifest(new ByteArrayInputStream(str.getBytes())).getMainAttributes().getValue("Implementation-Title"));
	}*/

	/*
	manifest-file:                    main-section newline *individual-section
	  main-section:                    version-info newline *main-attribute
	  version-info:                      Manifest-Version : version-number
	  version-number :               digit+{.digit+}*
	  main-attribute:                 (any legitimate main attribute) newline
	  individual-section:             Name : value newline *perentry-attribute
	  perentry-attribute:            (any legitimate perentry attribute) newline
	  newline :                            CR LF | LF | CR (not followed by LF)
	   digit:                                {0-9} 
	*/
}
