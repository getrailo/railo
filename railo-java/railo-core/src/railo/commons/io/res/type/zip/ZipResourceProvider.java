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
		return false;
	}
}
