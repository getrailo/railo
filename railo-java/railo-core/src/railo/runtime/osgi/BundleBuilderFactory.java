package railo.runtime.osgi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;

public class BundleBuilderFactory {
	
	/*
TODO

Export-Package: org.wikipedia.helloworld;version="1.0.0"
Import-Package: org.osgi.framework;version="1.3.0"

Export-Package: Expresses which Java packages contained in a bundle will be made available to the outside world.
Import-Package: Indicates which Java packages will be required from the outside world to fulfill the dependencies needed in a bundle.
	 * */
	
	//Indicates the OSGi specification to use for reading this bundle.
	private static final int MANIFEST_VERSION=2;

	private final String name;
	private final String symbolicName;
	private String description;
	private BundleVersion bundleVersion;
	
	public BundleVersion getBundleVersion() {
		return bundleVersion;
	}

	public void setBundleVersion(String version) {
		if(StringUtil.isEmpty(version,true))return ;
		this.bundleVersion=new BundleVersion(version);
		
		System.out.println(version+"->"+bundleVersion.toString());
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

	private String buildManifestSource(List<String> jarsUsed){
		StringBuilder sb=new StringBuilder();
			sb.append("Bundle-ManifestVersion: ").append(MANIFEST_VERSION).append('\n');
		if(!StringUtil.isEmpty(name))
			sb.append("Bundle-Name: ").append(name).append('\n');
			sb.append("Bundle-SymbolicName: ").append(symbolicName).append('\n');
		if(!StringUtil.isEmpty(description))
			sb.append("Bundle-Description: ").append(description).append('\n');
		if(bundleVersion!=null)
			sb.append("Bundle-Version: ").append(bundleVersion.toString()).append('\n');
		
		if(!StringUtil.isEmpty(activator)) {
			addImportPackage("org.osgi.framework");
			sb.append("Bundle-Activator: ").append(activator).append('\n');
		}
		addPackage(sb,"Export-Package",exportPackage);
		addPackage(sb,"Import-Package",importPackage);
		addPackage(sb,"DynamicImport-Package",dynImportPackage);
		addPackage(sb,"Bundle-ClassPath",classPath);
		
		log(sb.toString());
		return sb.toString();// NL at the end is needed, so no trim
	}

	private void addPackage(StringBuilder sb,String label, List<String> pack) {
		if(pack!=null && pack.size()>0) {
			sb.append(label).append(": ");
			Iterator<String> it = pack.iterator();
			boolean first=true;
			while(it.hasNext()) {
				if(!first) {
					sb.append(',');
				}
				sb.append(it.next());
				first=false;
			}
			sb.append('\n');
		}
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
		Charset charset = Charset.forName("UTF-8");
		ZipOutputStream zos=new MyZipOutputStream(os,charset);
		try{
		
			
			// jars
			List<String> jarsUsed=new ArrayList<String>();
			{
				Resource jar;
				Iterator<Resource> it = jars.iterator();
				while(it.hasNext()){
					jar=it.next();
					log("jar:"+jar.getName());
					jarsUsed.add(jar.getName());
					handleEntry(zos,jar, new JarEntryListener(zos));
				}
			}
			
			// manifest
			String mani = buildManifestSource(jarsUsed);
			InputStream is=new ByteArrayInputStream(mani.getBytes(charset));
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
			System.out.println("- "+entry.getName());
			// manifest
			if("META-INF/MANIFEST.MF".equalsIgnoreCase(entry.getName())) {
				ByteArrayOutputStream baos=new ByteArrayOutputStream();
				copy(source,baos);
				//log(zipFile+" -> META-INF/MANIFEST.MF");
				//log(new String(baos.toByteArray()));
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
	

	/*public static void test(String[] jars, String trg) throws Exception {
		File target=new File(trg);
		BundleBuilderFactory bf = new BundleBuilderFactory("Test", "test", "", "1.0.0", null);
		for(int i=0;i<jars.length;i++){
			bf.addJar(caster.toResource(jars[i]));
		}
		bf.build(target);
		
		
	}*/
}
