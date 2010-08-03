package railo.commons.io.res.type.tgz;

import railo.commons.io.res.Resource;
import railo.commons.io.res.type.compress.Compress;
import railo.commons.io.res.type.compress.CompressResourceProvider;

public final class TGZResourceProvider extends CompressResourceProvider {

	public TGZResourceProvider() {
		scheme="tgz";
	}
	
	/**
	 * @see railo.commons.io.res.type.compress.CompressResourceProvider#getCompress(railo.commons.io.res.Resource)
	 */
	public Compress getCompress(Resource file) {
		return Compress.getInstance(file,Compress.FORMAT_TGZ,caseSensitive);
	}

	/**
	 * @see railo.commons.io.res.ResourceProvider#isAttributesSupported()
	 */
	public boolean isAttributesSupported() {
		return false;
	}

	/**
	 * @see railo.commons.io.res.ResourceProvider#isCaseSensitive()
	 */
	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	/**
	 * @see railo.commons.io.res.ResourceProvider#isModeSupported()
	 */
	public boolean isModeSupported() {
		return true;
	}
}
