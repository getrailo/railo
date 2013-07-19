package railo.runtime.cache.legacy;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;

import railo.commons.io.ForkWriter;
import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;

public class CacheWriter extends ForkWriter {

	private Writer out;
	private Resource cacheFile;

	public CacheWriter(Writer out, Resource cacheFile) throws IOException {
		super(out, IOUtil.getWriter(cacheFile, (Charset)null));
		this.out=out;
		this.cacheFile=cacheFile;
	}

	/**
	 * @return the cacheFile
	 */
	public Resource getCacheFile() {
		return cacheFile;
	}

	/**
	 * @param cacheFile the cacheFile to set
	 */
	public void setCacheFile(Resource cacheFile) {
		this.cacheFile = cacheFile;
	}

	/**
	 * @return the out
	 */
	public Writer getOut() {
		return out;
	}

	/**
	 * @param out the out to set
	 */
	public void setOut(Writer out) {
		this.out = out;
	}

}
