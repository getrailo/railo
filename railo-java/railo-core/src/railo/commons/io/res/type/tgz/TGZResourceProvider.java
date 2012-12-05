package railo.commons.io.res.type.tgz;

import railo.commons.io.res.Resource;
import railo.commons.io.res.type.compress.Compress;
import railo.commons.io.res.type.compress.CompressResourceProvider;

public final class TGZResourceProvider extends CompressResourceProvider {

	public TGZResourceProvider() {
		scheme="tgz";
	}
	
	@Override
	public Compress getCompress(Resource file) {
		return Compress.getInstance(file,Compress.FORMAT_TGZ,caseSensitive);
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
