package railo.loader.osgi.factory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import railo.loader.util.Util;

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

	private String name;
	private String symbolicName;
	private String description;
	private String version;
	private String activator;

	private List<File> jars=new ArrayList<File>(); 

	/**
	 * 
	 * @param symbolicName this entry specifies a unique identifier for a bundle, based on the reverse domain name convention (used also by the java packages).
	 * @param name Defines a human-readable name for this bundle, Simply assigns a short name to the bundle. 
	 * @param description A description of the bundle's functionality. 
	 * @param version Designates a version number to the bundle.
	 * @param activator Indicates the class name to be invoked once a bundle is activated.
	 * @param name 
	 * @param name 
	 * @param name 
	 * @throws BundleBuilderFactoryException 
	 */
	public BundleBuilderFactory(String name, String symbolicName, String description, String version, String activator) throws BundleBuilderFactoryException{
		if(Util.isEmpty(symbolicName)) throw new BundleBuilderFactoryException("symbolic name is reqired");
		this.name=name;
		this.symbolicName=symbolicName;
		this.description=description;
		this.version=version;
		this.activator=activator;
		
	}
	
	private String buildManifestSource(List<String> jarsUsed){
		StringBuilder sb=new StringBuilder();
		if(!Util.isEmpty(name))sb.append("Bundle-Name: ").append(name).append('\n');
		sb.append("Bundle-SymbolicName: ").append(symbolicName).append('\n');
		if(!Util.isEmpty(description))sb.append("Bundle-Description: ").append(description).append('\n');
		sb.append("Bundle-ManifestVersion: ").append(MANIFEST_VERSION).append('\n');
		if(!Util.isEmpty(version))sb.append("Bundle-Version: ").append(version).append('\n');
		if(!Util.isEmpty(activator))sb.append("Bundle-Activator: ").append(activator).append('\n');
		sb.append("DynamicImport-Package: ").append("railo.*,railo.loader.engine.*").append('\n');
		
		// jars bundled
		/*sb.append("jars-bundled: ");
		Iterator<String> it = jarsUsed.iterator();
		boolean first=true;
		while(it.hasNext()){
			if(!first) {
				sb.append("\n ;");
			}
			sb.append(it.next());
			first=false;
		}
		
		sb.append('\n');*/
		
		return sb.toString();// NL at the end is needed, so no trim
	}

	public void addJar(File jar){
		jars.add(jar);
	}
	
	public void build(File target) throws IOException {
		OutputStream os = new FileOutputStream(target);
		try{
			build(os);
		}
		finally {
			//Util.closeEL(os);
		}
	}
	
	public void build(OutputStream os) throws IOException {
		Charset charset = Charset.forName("UTF-8");
		ZipOutputStream zos=new MyZipOutputStream(os,charset);
		try{
		
			
			// jars
			List<String> jarsUsed=new ArrayList<String>();
			{
				File jar;
				Iterator<File> it = jars.iterator();
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
	        	Util.closeEL(is);
	            zos.closeEntry();
	        }
		
		
		
		}
		finally {
			Util.closeEL(zos);
		}
	}
	

	private void handleEntry(ZipOutputStream target, File file, EntryListener listener) throws IOException {
		ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
		try{
			ZipEntry entry;
			while((entry=zis.getNextEntry())!=null){
				listener.handleEntry(file,zis,entry);
				zis.closeEntry();
			}
		}
		finally {
			Util.closeEL(zis);
		}
	}

	class JarEntryListener implements EntryListener {

		private ZipOutputStream zos;

		public JarEntryListener(ZipOutputStream zos) { 
			this.zos=zos;
		}

		@Override
		public void handleEntry(File zipFile,ZipInputStream source,ZipEntry entry) throws IOException {
			
			// manifest
			if("META-INF/MANIFEST.MF".equalsIgnoreCase(entry.getName())) {
				ByteArrayOutputStream baos=new ByteArrayOutputStream();
				copy(source,baos);
				log(zipFile+" -> META-INF/MANIFEST.MF");
				log(new String(baos.toByteArray()));
				return;
			}
			
			// ignore the following stuff
			if(entry.getName().endsWith(".DS_Store")||
					entry.getName().startsWith("__MACOSX")) {
				return;
			}
			
			
			ZipEntry ze=new ZipEntry(entry.getName());
			ze.setComment(entry.getComment());
			ze.setTime(entry.getTime());
			
    		try {
    			zos.putNextEntry(ze);
    		}
    		catch(NameAlreadyExistsException naee){
    			if(entry.isDirectory()) {
    				return;
    			}
    			log("duplette:"+ze.getName());
        		
    			/*if(entry.getName().startsWith("META_INF/")){
    				
    				equalsIgnoreCase("META-INF/LICENSE")) return;
    			}*/
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

		public void handleEntry(File zipFile, ZipInputStream source, ZipEntry entry) throws IOException;

	}
	
	public class MyZipOutputStream extends ZipOutputStream {
		
		private Set<String> names=new HashSet<String>();
		
		public MyZipOutputStream(OutputStream out,Charset charset) {
			super(out);
		}

		@Override
		public void putNextEntry(ZipEntry e) throws IOException {
			if(names.contains(e.getName()))
				throw new NameAlreadyExistsException(e.getName());
			names.add(e.getName());
			super.putNextEntry(e);
		}
		
	}

	private final static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[0xffff];
        int len;
        while((len = in.read(buffer)) !=-1)
          out.write(buffer, 0, len);
    }
	private void log(String str) {
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
