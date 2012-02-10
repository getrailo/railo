package railo.runtime.cache.legacy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.oro.text.regex.MalformedPatternException;

import railo.commons.io.IOUtil;
import railo.commons.io.cache.Cache;
import railo.commons.lang.ExceptionUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.cache.util.CacheKeyFilterAll;
import railo.runtime.cache.util.WildCardFilter;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.dt.TimeSpan;

public class CacheItemCache extends CacheItem {

	private Cache cache;
	private TimeSpan timespan;
	private String lcFileName;

	public CacheItemCache(PageContext pc, HttpServletRequest req, String id, String key, boolean useId, Cache cache, TimeSpan timespan)  {
		super(pc, req, id, key, useId);
		this.cache=cache;
		this.timespan=timespan;
		lcFileName=fileName;
	}

	@Override
	public boolean isValid() {
		try {
			return cache.getValue(lcFileName)!=null;
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public boolean isValid(TimeSpan timespan) {
		return isValid();
	}

	@Override
	public void writeTo(OutputStream os,String charset) throws IOException {
		byte[] barr = getValue().getBytes(StringUtil.isEmpty(charset,true)?"UTF-8":charset);
		IOUtil.copy(new ByteArrayInputStream(barr),os,true,false);
	}

	public String getValue() throws IOException {
		try {
			return Caster.toString(cache.getValue(lcFileName));
		} catch (PageException e) {
			throw ExceptionUtil.toIOException(e);
		}
	}

	@Override
	public void store(String value) throws IOException {
		cache.put(lcFileName, value, null,valueOf(timespan));
		
	}

	@Override
	public void store(byte[] barr, boolean append) throws IOException {
		String value=(append)?getValue():"";
		value+=IOUtil.toString(barr, "UTF-8");
		store(value);
	}

	public static void _flushAll(PageContext pc, Cache cache) throws IOException {
		cache.remove(CacheKeyFilterAll.getInstance());
	}

	public static void _flush(PageContext pc, Cache cache, String expireurl) throws MalformedPatternException, IOException {
		cache.remove(new WildCardFilter(expireurl,true));
	}
	
	private static Long valueOf(TimeSpan timeSpan) {
		if(timeSpan==null) return null;
		return Long.valueOf(timeSpan.getMillis());
	}

}
