package railo.runtime.osgi;

import java.io.ByteArrayInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import railo.print;
import railo.commons.io.CharsetUtil;
import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.loader.util.Util;
import railo.runtime.PageContext;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;

public class BundleBuilderFactory {
		
	//Indicates the OSGi specification to use for reading this bundle.
	private static final int MANIFEST_VERSION=2;

	private static final Set<String> INDIVIDUAL_FILTER = new HashSet<String>();
	private static final Set<String> MAIN_FILTER = new HashSet<String>();
	static {
		MAIN_FILTER.add("SHA1-Digest-Manifest");
		MAIN_FILTER.add("MD5-Digest-Manifest");
		//MAIN_FILTER.add("Sealed");
		
		INDIVIDUAL_FILTER.add("SHA1-Digest");
		INDIVIDUAL_FILTER.add("MD5-Digest");
		//INDIVIDUAL_FILTER.add("Sealed");
	}

	private final String name;
	private final String symbolicName;
	private String description;
	private BundleVersion bundleVersion;
	private Manifest manifest;
	
	public BundleVersion getBundleVersion() {
		return bundleVersion;
	}

	public void setBundleVersion(String version) {
		if(StringUtil.isEmpty(version,true))return ;
		this.bundleVersion=new BundleVersion(version);
	}
	private String activator;

	private List<Resource> jars=new ArrayList<Resource>();

	private List<String> exportPackage;
	private List<String> importPackage; 
	private List<String> dynImportPackage; 
	private List<String> classPath; 

	/**
	 * 
	 * @param symbolicName this entry specifies a unique identifier for a bundle, based on the reverse domain name convention (used also by the java packages).
	 * @param name Defines a human-readable name for this bundle, Simply assigns a short name to the bundle. 
	 * @param description A description of the bundle's functionality. 
	 * @param version Designates a version number to the bundle.
	 * @param activator Indicates the class name to be invoked once a bundle is activated.
	 * @param name 
	 * @throws BundleBuilderFactoryException 
	 */
	public BundleBuilderFactory(String name, String symbolicName) throws ApplicationException {
		if(StringUtil.isEmpty(symbolicName)) {
			if(StringUtil.isEmpty(name))
				throw new ApplicationException("symbolic name is reqired");
			symbolicName=toSymbolicName(name);
		}
		this.name=name;
		this.symbolicName=symbolicName;
		
	}

	private String toSymbolicName(String name) {
		name=name.replace(' ', '.');
		name=name.replace('_', '.');
		name=name.replace('-', '.');
		return name;
	}
	
	public List<String> getExportPackage() {
		return exportPackage;
	}

	public void addExportPackage(String strExportPackage) {
		if(StringUtil.isEmpty(strExportPackage)) return;
		if(exportPackage==null)exportPackage=new ArrayList<String>();
		addPackages(exportPackage,strExportPackage);
		
	}
	
	private static void addPackages(List<String> packages, String str) {
		StringTokenizer st=new StringTokenizer(str,",");
		while(st.hasMoreTokens()){
			packages.add(st.nextToken().trim());
		}
	}

	public List<String> getImportPackage() {
		return importPackage;
	}

	public List<String> getDynamicImportPackage() {
		return dynImportPackage;
	}

	public void addImportPackage(String strImportPackage) {
		if(StringUtil.isEmpty(strImportPackage)) return;
		if(importPackage==null)importPackage=new ArrayList<String>();
		addPackages(importPackage,strImportPackage);
	}
	
	public void addDynamicImportPackage(String strDynImportPackage) {
		if(StringUtil.isEmpty(strDynImportPackage)) return;
		if(dynImportPackage==null)dynImportPackage=new ArrayList<String>();
		addPackages(dynImportPackage,strDynImportPackage);
	}

	public List<String> getClassPath() {
		return classPath;
	}

	public void addClassPath(String str) {
		if(classPath==null)classPath=new ArrayList<String>();
		addPackages(classPath,str);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getActivator() {
		return activator;
	}

	public void setActivator(String activator) {
		this.activator = activator;
	}

	private void extendManifest(Manifest mf){
		Attributes attrs = mf.getMainAttributes();
		attrs.putValue("Bundle-ManifestVersion", ""+MANIFEST_VERSION);
		if(!StringUtil.isEmpty(name)) attrs.putValue("Bundle-Name",name);
		attrs.putValue("Bundle-SymbolicName",symbolicName);	
		if(!StringUtil.isEmpty(description))attrs.putValue("Bundle-Description",description);
		if(bundleVersion!=null) attrs.putValue("Bundle-Version",bundleVersion.toString());
		
		if(!StringUtil.isEmpty(activator)) {
			attrs.putValue("Bundle-Activator",activator);
			addImportPackage("org.osgi.framework");
		}
		String str = attrs.getValue("Export-Package");
		if(Util.isEmpty(str,true)) addList(attrs,"Export-Package",exportPackage);
		
		str = attrs.getValue("Import-Package");
		if(Util.isEmpty(str,true)) addList(attrs,"Import-Package",importPackage);
		
		str = attrs.getValue("DynamicImport-Package");
		if(Util.isEmpty(str,true)) addList(attrs,"DynamicImport-Package",dynImportPackage);
		
		str = attrs.getValue("Bundle-ClassPath");
		if(Util.isEmpty(str,true)) addList(attrs,"Bundle-ClassPath",classPath);
	}

	private void addList(Attributes attrs,String name,List<String> values) {
		if(values==null || values.size()==0) return;
		
		StringBuilder sb=new StringBuilder();
		Iterator<String> it = values.iterator();
		boolean first=true;
		while(it.hasNext()) {
			if(!first) {
				sb.append(',');
			}
			sb.append(it.next());
			first=false;
		}
		attrs.putValue(name, sb.toString());
	}

	public void addJar(Resource jar) throws ApplicationException{
		if(!jar.isFile())
			throw new ApplicationException("["+jar+"] is not a file");
		log("add "+jar+" to the bundle");
		jars.add(jar);
	}

	public void addJars(PageContext pc,String jars) throws PageException{
		StringTokenizer st=new StringTokenizer(jars,",");
		while(st.hasMoreTokens()){
			addJar(ResourceUtil.toResourceExisting(pc,st.nextToken().trim()));
		}
	}
	
	
	
	public void build(Resource target) throws IOException {
		OutputStream os = target.getOutputStream();
		try{
			build(os);
		}
		finally {
			IOUtil.closeEL(os);
		}
	}
	
	public void build(OutputStream os) throws IOException {
		ZipOutputStream zos=new MyZipOutputStream(os,CharsetUtil.UTF8);
		try{
			
			// jars
			List<String> jarsUsed=new ArrayList<String>();
			{
				Resource jar;
				Iterator<Resource> it = jars.iterator();
				while(it.hasNext()){
					jar=it.next();
					//log("jar:"+jar.getName());
					jarsUsed.add(jar.getName());
					handleEntry(zos,jar, new JarEntryListener(zos));
				}
			}
			
			// manifest
			if(manifest==null)manifest=new Manifest();
			extendManifest(manifest);
			
			String mf = ManifestUtil.toString(manifest,128,MAIN_FILTER,INDIVIDUAL_FILTER);
			//print.e("+++++++++++++++++++++++++++++++++++");
			//print.e(mf);
			InputStream is=new ByteArrayInputStream(mf.getBytes(CharsetUtil.UTF8));
			ZipEntry ze=new ZipEntry("META-INF/MANIFEST.MF");
			zos.putNextEntry(ze);
	        try {
	            copy(is,zos);
	        } 
	        finally {
	        	IOUtil.closeEL(is);
	            zos.closeEntry();
	        }
		
		
		
		}
		finally {
			IOUtil.closeEL(zos);
		}
	}
	

	private void handleEntry(ZipOutputStream target, Resource file, EntryListener listener) throws IOException {
		ZipInputStream zis = new ZipInputStream(file.getInputStream());
		try{
			ZipEntry entry;
			while((entry=zis.getNextEntry())!=null){
				listener.handleEntry(file,zis,entry);
				zis.closeEntry();
			}
		}
		finally {
			IOUtil.closeEL(zis);
		}
	}

	class JarEntryListener implements EntryListener {

		private ZipOutputStream zos;

		public JarEntryListener(ZipOutputStream zos) { 
			this.zos=zos;
		}

		
		@Override
		public void handleEntry(Resource zipFile,ZipInputStream source,ZipEntry entry) throws IOException {
			// security
			if("META-INF/IDRSIG.DSA".equalsIgnoreCase(entry.getName()) 
					|| "META-INF/IDRSIG.SF".equalsIgnoreCase(entry.getName())
					|| "META-INF/INDEX.LIST".equalsIgnoreCase(entry.getName())) {
				print.e(zipFile+"->"+entry.getName());
				return;
			}
			
			// manifest
			if("META-INF/MANIFEST.MF".equalsIgnoreCase(entry.getName())) {
				manifest = new Manifest(source);
				Attributes attrs = manifest.getMainAttributes();
				
				// they are in bootdelegation
				ManifestUtil.removeFromList(attrs,"Import-Package","javax.*"); 
				ManifestUtil.removeFromList(attrs,"Import-Package","org.osgi.*");
				
				
				//manifest.getEntries().clear();
				
				
				/*while((line=br.readLine())!=null){
					if(StringUtil.isEmpty(line,true)) continue;
					if(line.startsWith("Bundle-Name:") || line.startsWith("Bundle-ManifestVersion:")) {}
					else if(line.startsWith("Bundle-SymbolicName:")) { 
						if(!line.trim().equals("Bundle-SymbolicName: "+symbolicName))
							throw new  IOException("jar Manifest already has a [Bundle-SymbolicName] line with a different value ["+line+"], your name is ["+symbolicName+"]");
					}
					else if(line.startsWith("Bundle-Version:")) { 
						if(!line.trim().equals("Bundle-Version: "+bundleVersion)) {
							BundleVersion bv = new BundleVersion(StringUtil.replace(line, "Bundle-Version:", "",true));
							if(!bv.equals(bundleVersion))
								throw new  IOException("jar Manifest already has a [Bundle-Version] line with a different value ["+line+"], your name is ["+bundleVersion+"]");
						}
					}
					else {
						if(StringUtil.startsWithIgnoreCase(line, "SHA1-Digest:")) {
							print.e("zip:"+zipFile);
							System.exit(0);
						}
						
						mani.append(line).append('\n');
						
					}
				}*/
				
				
				return;
			}
			
			// ignore the following stuff
			if(entry.getName().endsWith(".DS_Store")||
					entry.getName().startsWith("__MACOSX")) {
				return;
			}
			
			
			MyZipEntry ze=new MyZipEntry(entry.getName());
			ze.setComment(entry.getComment());
			ze.setTime(entry.getTime());
			ze.setFile(zipFile);
			
    		try {
    			zos.putNextEntry(ze);
    		}
    		catch(NameAlreadyExistsException naee){
    			if(entry.isDirectory()) {
    				return;
    			}
    			log("--------------------------------");
    			log(ze.getName());
    			log("before:"+naee.getFile());
    			log("curren:"+zipFile);
    			log("size:"+naee.getSize()+"=="+entry.getSize());
        		return; // TODO throw naee;
    		}
            try {
                copy(source,zos);
            } 
            finally {
                zos.closeEntry();
            }
		}
	}
	
	
	public interface EntryListener {

		public void handleEntry(Resource zipFile, ZipInputStream source, ZipEntry entry) throws IOException;

	}
	
	public class MyZipOutputStream extends ZipOutputStream {
		
		private Map<String,Resource> names=new HashMap<String,Resource>();
		
		public MyZipOutputStream(OutputStream out,Charset charset) {
			super(out);
		}
		
		
		@Override
		public void putNextEntry(ZipEntry e) throws IOException {
			Resource file = names.get(e.getName());
			if(names.containsKey(e.getName()))
				throw new NameAlreadyExistsException(e.getName(),file,e.getSize());
			
			if(e instanceof MyZipEntry)names.put(e.getName(),((MyZipEntry)e).getFile());
			super.putNextEntry(e);
		}
		
	}
	
	public class MyZipEntry extends ZipEntry {

		private Resource file;
		
		public MyZipEntry(String name) {
			super(name);
		}
		public void setFile(Resource file) {
			this.file=file;
		}
		public MyZipEntry(ZipEntry e) {
			super(e);
		}
		
		public Resource getFile(){
			return file;
		}
	}

	private final static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[0xffff];
        int len;
        while((len = in.read(buffer)) !=-1)
          out.write(buffer, 0, len);
    }
	public void log(String str) {
		System.out.println(str);
	}
}
