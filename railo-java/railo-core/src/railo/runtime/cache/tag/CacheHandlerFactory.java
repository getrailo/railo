package railo.runtime.cache.tag;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import railo.commons.digest.HashUtil;
import railo.commons.lang.KeyGenerator;
import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.cache.tag.request.RequestCacheHandler;
import railo.runtime.cache.tag.timespan.TimespanCacheHandler;
import railo.runtime.cache.tag.udf.UDFArgConverter;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.ConfigWebUtil;
import railo.runtime.db.SQL;
import railo.runtime.exp.PageException;
import railo.runtime.functions.cache.Util;
import railo.runtime.op.Caster;
import railo.runtime.type.Struct;
import railo.runtime.type.UDF;
import railo.runtime.type.util.KeyConstants;

public class CacheHandlerFactory { 

	public static final int TYPE_TIMESPAN=1;
	public static final int TYPE_REQUEST=2;
	
	public static final char CACHE_DEL = ';';
	public static final char CACHE_DEL2 = ':';

	
	final RequestCacheHandler rch;
	private Map<Config,TimespanCacheHandler> tschs=new HashMap<Config, TimespanCacheHandler>();
	private int cacheDefaultType;
	
	protected CacheHandlerFactory(int cacheDefaultType) {
		this.cacheDefaultType=cacheDefaultType;
		rch=new RequestCacheHandler(cacheDefaultType);
	}
	
	/**
	 * based on the cachedWithin Object we  choose the right Cachehandler and return it
	 * @return 
	 */
	public CacheHandler getInstance(Config config,Object cachedWithin){
		if(Caster.toTimespan(cachedWithin,null)!=null) {
			return getTimespanCacheHandler(config);
		}
		String str=Caster.toString(cachedWithin,"").trim();
		if("request".equalsIgnoreCase(str)) return rch;
		
		return null;
	}
	
	public CacheHandler getInstance(Config config,int type){
		if(TYPE_TIMESPAN==type)return getTimespanCacheHandler(config);
		if(TYPE_REQUEST==type) return rch;
		return null;
	}
	
	private CacheHandler getTimespanCacheHandler(Config config) {
		TimespanCacheHandler tsch = tschs.get(config);
		if(tsch==null) {
			tschs.put(config, tsch=new TimespanCacheHandler(cacheDefaultType, null));
		}
		return tsch;
	}

	public int size(PageContext pc) throws PageException {
		int size=rch.size(pc);
		size+=getTimespanCacheHandler(pc.getConfig()).size(pc);
		return size;
	}


	public void clear(PageContext pc) throws PageException {
		rch.clear(pc);
		getTimespanCacheHandler(pc.getConfig()).clear(pc);
	}
	
	public void clear(PageContext pc, CacheHandlerFilter filter) throws PageException {
		rch.clear(pc,filter);
		getTimespanCacheHandler(pc.getConfig()).clear(pc,filter);
	}
	
	public void clean(PageContext pc) throws PageException {
		rch.clean(pc);
		getTimespanCacheHandler(pc.getConfig()).clean(pc);
	}

	public static String createId(PageSource[] sources) throws PageException{
		String str;
		if(sources.length==1) {
			str= sources[0].getDisplayPath();
		}
		else {
			StringBuilder sb=new StringBuilder();
			for(int i=0;i<sources.length;i++){
				if(i>0)sb.append(";");
				sb.append(sources[i].getDisplayPath());
			}
			str=sb.toString();
		}
		try {
			return Util.key(KeyGenerator.createKey(str));
		}
		catch (IOException e) {
			throw Caster.toPageException(e);
		}
	}

	public static String createId(SQL sql, String datasource, String username,String password) throws PageException{
		try {
			return Util.key(KeyGenerator.createKey(sql.toHashString()+datasource+username+password));
		}
		catch (IOException e) {
			throw Caster.toPageException(e);
		}
	}

	public static String createId(UDF udf, Object[] args, Struct values) {
		StringBuilder sb=new StringBuilder()
			.append(HashUtil.create64BitHash(udf.getPageSource().getDisplayPath()))
			.append(CACHE_DEL)
			.append(HashUtil.create64BitHash(udf.getFunctionName()))
			.append(CACHE_DEL);
		
		
		
		if(values!=null) {
			// argumentCollection
			Struct sct;
			if(values.size()==1 && (sct=Caster.toStruct(values.get(KeyConstants._argumentCollection,null),null))!=null) {
				sb.append(_createId(sct));
			}
			else sb.append(_createId(values));
		}
		else if(args!=null){
			sb.append(_createId(args));
		}
		return HashUtil.create64BitHashAsString(sb, Character.MAX_RADIX);
	}

	private static String _createId(Object values) {
		return HashUtil.create64BitHash(UDFArgConverter.serialize(values))+"";
	}
	

	public static String toStringType(int type, String defaultValue) {
		switch(type){
		case TYPE_REQUEST: 	return "request";
		case TYPE_TIMESPAN:	return "timespan";
		}
		return defaultValue;
	}
	
	public static String toStringCacheName(int type, String defaultValue) {
		switch(type){
		case ConfigImpl.CACHE_DEFAULT_FUNCTION:	return "function";
		case ConfigImpl.CACHE_DEFAULT_INCLUDE: 	return "include";
		case ConfigImpl.CACHE_DEFAULT_OBJECT: 	return "object";
		case ConfigImpl.CACHE_DEFAULT_QUERY: 	return "query";
		case ConfigImpl.CACHE_DEFAULT_RESOURCE: 	return "resource";
		case ConfigImpl.CACHE_DEFAULT_TEMPLATE: 	return "template";
		}
		return defaultValue;
	}

	public static CacheItem toCacheItem(Object value, CacheItem defaultValue) {
		if(value instanceof CacheItem) return (CacheItem) value;
		return defaultValue;
	}
}
