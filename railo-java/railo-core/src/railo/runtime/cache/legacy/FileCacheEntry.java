package railo.runtime.cache.legacy;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.runtime.type.dt.TimeSpan;

public class FileCacheEntry implements CacheEntry {

	private static final String ENC = "utf-8";
	private Resource res;
	//private Resource directory;
	//private String name,raw;
	
	
	
	private boolean isOK(TimeSpan timeSpan) {
		return res.exists() && (res.lastModified()+timeSpan.getMillis()>=System.currentTimeMillis());
	}
	public String readEntry(TimeSpan timeSpan,String defaultValue) throws IOException {
		if(isOK(timeSpan))
			return IOUtil.toString(res,ENC);
		return defaultValue;
	}

	public void writeEntry(String entry,boolean append) throws IOException {
		IOUtil.copy(new ByteArrayInputStream(entry.getBytes(ENC)), res.getOutputStream(append),true,true);
	}

}
