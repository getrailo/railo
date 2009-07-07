package railo.runtime.util;

import java.io.IOException;

import railo.commons.io.res.Resource;

public interface ZipUtil {
	public void unzip(Resource zip, Resource dir) throws IOException;
}
