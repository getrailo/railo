package railo.runtime.osgi;

import java.io.File;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import railo.commons.io.res.Resource;
import railo.commons.io.res.type.file.FileResource;
import railo.commons.lang.StringUtil;
import railo.runtime.op.Caster;

public class BundleFile extends JarFile {

	private final BundleVersion version;
	private final String name;
	private final String symbolicName;
	private final String exportPackage;
	private final String importPackage;
	private final String activator;
	private final int manifestVersion;
	private final String description;
	private final String dynamicImportPackage;
	private final String classPath;
	private final String requireBundle;

	public BundleFile(Resource file) throws IOException {
		super(toFileResource(file));
		
		Manifest manifest = getManifest();
		Attributes attrs = manifest.getMainAttributes();
		
		manifestVersion = Caster.toIntValue(attrs.getValue("Bundle-ManifestVersion"),1);
		
		name = attrs.getValue("Bundle-Name");
		symbolicName = attrs.getValue("Bundle-SymbolicName");
		String tmp = attrs.getValue("Bundle-Version");
		version=StringUtil.isEmpty(tmp,true)?new BundleVersion():new BundleVersion(tmp);
		exportPackage = attrs.getValue("Export-Package");
		importPackage = attrs.getValue("Import-Package");
		dynamicImportPackage = attrs.getValue("DynamicImport-Package");
		activator = attrs.getValue("Bundle-Activator");
		description = attrs.getValue("Bundle-Description");
		classPath = attrs.getValue("Bundle-ClassPath");
		requireBundle = attrs.getValue("Require-Bundle");
		
	}

	public String getRequireBundle() {
		return requireBundle;
	}

	private static File toFileResource(Resource file) throws IOException {
		if(file instanceof FileResource) return (File)file;
		throw new IOException("only file resources (local file system) are supported");
	}
	
	public BundleVersion getVersion(){
		return version;
	}

	@Override
	public String getName() {
		return name;
	}

	public String getSymbolicName() {
		return symbolicName;
	}

	public String getExportPackage() {
		return exportPackage;
	}

	public String getImportPackage() {
		return importPackage;
	}

	public String getActivator() {
		return activator;
	}

	public int getManifestVersion() {
		return manifestVersion;
	}

	public String getDescription() {
		return description;
	}

	public String getDynamicImportPackage() {
		return dynamicImportPackage;
	}

	public String getClassPath() {
		return classPath;
	}
}
