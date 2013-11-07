package railo.runtime.cache.tag;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import railo.print;
import railo.commons.digest.HashUtil;
import railo.commons.lang.KeyGenerator;
import railo.runtime.PageContext;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.cache.Util;
import railo.runtime.cache.tag.request.RequestCacheHandler;
import railo.runtime.cache.tag.timespan.TimespanCacheHandler;
import railo.runtime.cache.tag.udf.UDFArgConverter;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigImpl;
import railo.runtime.converter.LazyConverter;
import railo.runtime.db.SQL;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.type.Array;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Collection;
import railo.runtime.type.StructImpl;
import railo.runtime.type.UDF;
import railo.runtime.type.UDFImpl;
import railo.runtime.type.comparator.SortRegisterComparator;
import railo.runtime.type.util.CollectionUtil;
import railo.runtime.type.util.KeyConstants;

public class CacheHandlerFactory {

	public static final int TYPE_TIMESPAN=1;
	public static final int TYPE_REQUEST=2;

	public static final char CACHE_DEL = ';';
	public static final char CACHE_DEL2 = ':';

	public static CacheHandlerFactory query=new CacheHandlerFactory(ConfigImpl.CACHE_DEFAULT_QUERY);
	public static CacheHandlerFactory udf=new CacheHandlerFactory(ConfigImpl.CACHE_DEFAULT_FUNCTION);
	
	private final RequestCacheHandler rch=new RequestCacheHandler();
	private Map<Config,TimespanCacheHandler> tschs=new HashMap<Config, TimespanCacheHandler>();
	private int cacheDefaultType;
	
	private CacheHandlerFactory(int cacheDefaultType){
		this.cacheDefaultType=cacheDefaultType;
	}
	
	public static void release(PageContext pc){
		query.rch.clear(pc);
		udf.rch.clear(pc);
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

	public int size(PageContext pc) {
		int size=rch.size(pc);
		size+=getTimespanCacheHandler(pc.getConfig()).size(pc);
		return size;
	}


	public void clear(PageContext pc) {
		rch.clear(pc);
		getTimespanCacheHandler(pc.getConfig()).clear(pc);
	}
	
	public void clear(PageContext pc, CacheHandlerFilter filter) {
		rch.clear(pc,filter);
		getTimespanCacheHandler(pc.getConfig()).clear(pc,filter);
	}
	
	public void clean(PageContext pc) {
		rch.clean(pc);
		getTimespanCacheHandler(pc.getConfig()).clean(pc);
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
			/*for(int i=0;i<args.length;i++){
				if(!Decision.isSimpleValue(args[i])) {
					if(Decision.isStruct(args[i])) {
						_createId(sb, Caster.toStruct(args[i],null), false);
						continue;
					}
					else if(Decision.isArray(args[i])) {
						_createId(sb, Caster.toArray(args[i],null), false);
						continue;
					}
					
					throw new ApplicationException("only simple values are allowed as parameter for a function with cachedWithin");
				}
				sb.append(HashUtil.create64BitHash(args[i].toString())).append(CACHE_DEL);
				
			}*/
		}
		return HashUtil.create64BitHashAsString(sb, Character.MAX_RADIX);
	}

	private static String _createId(Object values) {
		return HashUtil.create64BitHash(UDFArgConverter.serialize(values))+"";
		/*
		//Iterator<Entry<Key, Object>> it = values.entryIterator();
		Collection.Key[] keys = CollectionUtil.keys(values);
		Arrays.sort(keys);
		Object v;
		for(int i=0;i<keys.length;i++){
			v=values.get(keys[i],null);
			if(!Decision.isSimpleValue(v)) {
				if(Decision.isStruct(v) || Decision.isArray(v)){
					sb.append(((KeyImpl)keys[i]).hash()).append(CACHE_DEL2);
					_createId(sb, Caster.toCollection(v,null), false);
					sb.append(CACHE_DEL);
					continue;
				}
				throw new ApplicationException("only simple values are allowed as parameter for a function with cachedWithin");
			}
			sb.append(((KeyImpl)keys[i]).hash()).append(CACHE_DEL2).append(HashUtil.create64BitHash(v.toString())).append(CACHE_DEL);
			
		}*/
	}
}
