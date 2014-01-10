package railo.commons.io.res.util;

import java.io.File;
import java.io.FileFilter;

import railo.commons.io.res.Resource;
import railo.commons.io.res.type.file.FileResourceFilter;

public final class FileFilterWrapper implements FileResourceFilter {

	private final FileFilter filter;

	public FileFilterWrapper(FileFilter fileFilter) {
		this.filter=fileFilter;
	}
	public boolean accept(Resource res) {
		if(res instanceof File) return accept(((File)res));
		return accept(FileWrapper.toFile(res));
	}

	@Override
	public boolean accept(File pathname) {
		return filter.accept(pathname);
	}

}
