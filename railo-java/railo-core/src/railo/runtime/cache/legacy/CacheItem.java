package railo.runtime.cache.legacy;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.oro.text.regex.MalformedPatternException;

import railo.commons.io.cache.Cache;
import railo.commons.io.res.Resource;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.config.ConfigImpl;
import railo.runtime.functions.cache.Util;
import railo.runtime.type.dt.TimeSpan;

 
public abstract class CacheItem {

	protected final String fileName;
	public static CacheItem getInstance(PageContext pc, String id, String key, boolean useId, Resource dir, String cacheName, TimeSpan timespan) throws IOException{
		HttpServletRequest req = pc. getHttpServletRequest();
        Cache cache = Util.getCache(pc,cacheName,ConfigImpl.CACHE_DEFAULT_TEMPLATE,null);	
		if(cache!=null) 
			return new CacheItemCache(pc, req, id, key, useId, cache,timespan);
		return new CacheItemFS(pc, req, id, key, useId, dir);
	}
	
	public CacheItem(PageContext pc, HttpServletRequest req, String id, String key, boolean useId) {
		
		//raw
		String filename=req.getServletPath();
        if(!StringUtil.isEmpty(req.getQueryString())) {
        	filename+="?"+req.getQueryString();
        	if(useId)filename+="&cfcache_id="+id;
        }
        else {
        	if(useId)filename+="?cfcache_id="+id;
        }
    	if(useId && !StringUtil.isEmpty(key)) filename=key;
    	if(!StringUtil.isEmpty(req.getContextPath())) filename=req.getContextPath()+filename;
    	fileName=filename;
    	
    	
        
		
	}
	
	
	
	public abstract boolean isValid();
	
	public abstract boolean isValid(TimeSpan timespan);
	
	public abstract void writeTo(OutputStream os, String charset) throws IOException;
	
	public abstract String getValue() throws IOException;
	
	public abstract void store(String result) throws IOException;
	
	public abstract void store(byte[] barr,boolean append) throws IOException;

	//protected abstract void _flushAll(PageContext pc, Resource dir) throws IOException;

	//protected abstract void _flush(PageContext pc, Resource dir, String expireurl) throws IOException;
	
	public static void flushAll(PageContext pc, Resource dir, String cacheName) throws IOException {
		Cache cache = Util.getCache(pc,cacheName,ConfigImpl.CACHE_DEFAULT_TEMPLATE,null);	
		if(cache!=null) CacheItemCache._flushAll(pc, cache);
		else CacheItemFS._flushAll(pc, dir);
	}

	public static void flush(PageContext pc, Resource dir, String cacheName,String expireurl) throws IOException, MalformedPatternException {
		Cache cache = Util.getCache(pc,cacheName,ConfigImpl.CACHE_DEFAULT_TEMPLATE,null);	
		if(cache!=null) CacheItemCache._flush(pc, cache,expireurl);
		else CacheItemFS._flush(pc, dir, expireurl);
	}
}