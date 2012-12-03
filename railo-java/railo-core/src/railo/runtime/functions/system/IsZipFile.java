package railo.runtime.functions.system;

import java.io.InputStream;
import java.util.zip.ZipInputStream;

import railo.commons.io.IOUtil;
import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.PageContext;

public class IsZipFile {

	public static boolean call(PageContext pc, String path) {
		InputStream is=null;
		boolean hasEntries=false;
		try {
			//ZipEntry ze;
			ZipInputStream zis = new ZipInputStream(is=ResourceUtil.toResourceExisting(pc, path).getInputStream());
			while ((zis.getNextEntry()) != null ) {
	        	zis.closeEntry();
	        	hasEntries=true;
	        }
		} catch (Exception e) {
			return false;
		}
		finally {
			IOUtil.closeEL(is);
		}
		return hasEntries;
	}
}
