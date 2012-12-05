package railo.commons.io.res.util;

import java.io.File;
import java.io.FilenameFilter;

import railo.commons.io.res.Resource;
import railo.commons.io.res.type.file.FileNameResourceFilter;

public final class FileNameFilterWrapper implements FileNameResourceFilter {
	private final FilenameFilter filter;

	public FileNameFilterWrapper(FilenameFilter filter) {
		this.filter=filter;
	}
	public boolean accept(Resource dir,String name) {
		if(dir instanceof File) return accept(((File)dir),name);
		return accept(FileWrapper.toFile(dir),name);
	}

	@Override
	public boolean accept(File dir,String name) {
		return filter.accept(dir,name);
	}
}
