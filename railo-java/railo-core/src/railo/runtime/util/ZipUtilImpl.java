package railo.runtime.util;

import java.io.IOException;

import railo.commons.io.res.Resource;

public class ZipUtilImpl implements ZipUtil {

	private static ZipUtil instance=new ZipUtilImpl();
	private ZipUtilImpl(){}
	public static ZipUtil getInstance(){
		return instance;
	}
		
	public void unzip(Resource zip, Resource dir) throws IOException {
		railo.commons.io.compress.ZipUtil.unzip(zip, dir);
	}

}
