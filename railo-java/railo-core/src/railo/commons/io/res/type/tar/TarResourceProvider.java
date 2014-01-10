package railo.commons.io.res.type.tar;

import railo.commons.io.res.Resource;
import railo.commons.io.res.type.compress.Compress;
import railo.commons.io.res.type.compress.CompressResourceProvider;

public final class TarResourceProvider extends CompressResourceProvider {

	public TarResourceProvider() {
		scheme="tar";
	}
	
	@Override
	public Compress getCompress(Resource file) {
		return Compress.getInstance(file,Compress.FORMAT_TAR,caseSensitive);
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
		return true;
	}
}
