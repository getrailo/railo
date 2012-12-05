package railo.commons.io.res.type.zip;

import railo.commons.io.res.Resource;
import railo.commons.io.res.type.compress.Compress;
import railo.commons.io.res.type.compress.CompressResourceProvider;

public final class ZipResourceProvider extends CompressResourceProvider {
	
	public ZipResourceProvider() {
		scheme="zip";
	}
	
	public Compress getCompress(Resource file) {
		return Compress.getInstance(file,Compress.FORMAT_ZIP,caseSensitive);
	}

	@Override
	public boolean isAttributesSupported() {
		return false;
	}

	@Override
	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	@Override
	public boolean isModeSupported() {
		return false;
	}
}
