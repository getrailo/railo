package railo.commons.io.compress;

import java.io.IOException;
import java.util.zip.ZipOutputStream;

import railo.commons.cli.Command;
import railo.commons.io.CompressUtil;
import railo.commons.io.res.Resource;

public final class ZipUtil {

	public static void unzip(Resource zip, Resource dir) throws IOException {
		if(zip.length()>0 && (dir.exists() || dir.mkdirs())) {
			if("Mac OS X".equalsIgnoreCase(System.getProperty("os.name"))) {
				try {
					//Command.execute("unzip "+zip+" -d "+dir);
					Command.execute("unzip",new String[]{"-o",zip.getAbsolutePath(),"-d",dir.getAbsolutePath()});
				} 
				catch (InterruptedException e) {}
				return;
			}
			CompressUtil.extract(CompressUtil.FORMAT_ZIP, zip, dir);
		}
	}

	public static void cloeseEL(ZipOutputStream zos) {
		if(zos==null) return;
		try {
			zos.close();
		} 
		catch (IOException e) {}
	}
}
