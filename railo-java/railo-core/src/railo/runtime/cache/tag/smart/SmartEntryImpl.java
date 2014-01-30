package railo.runtime.cache.tag.smart;

import railo.commons.digest.HashUtil;
import railo.commons.io.SystemUtil;
import railo.commons.io.SystemUtil.TemplateLine;
import railo.runtime.PageContext;
import railo.runtime.cache.tag.CacheHandlerFactory;
import railo.runtime.cache.tag.CacheItem;
import railo.runtime.cache.tag.udf.UDFArgConverter;
import railo.runtime.functions.other.CreateUUID;

public final class SmartEntryImpl implements SmartEntry {

	private final String id;
	private final String typeId;
	private final String entryHash;
	private final String resultHash;
	private final long createTime;
	private final String applicationName;
	private final String cfid;
	//private final Object value;
	private TemplateLine templateLine;
	
	private String name;
	private long payLoad;
	private String meta;
	private long executionTime;

	public SmartEntryImpl(PageContext pc, CacheItem item, String entryHash, int cacheType) { 
		
		this.name=item.getName();
		this.payLoad=item.getPayload();
		this.meta=item.getMeta();
		this.executionTime=item.getExecutionTime();
	
		this.id=CreateUUID.invoke();// TODO better impl
		this.typeId=CacheHandlerFactory.toStringCacheName(cacheType, null);
		this.entryHash=entryHash;
		this.resultHash=item.getHashFromValue();
		this.createTime=System.currentTimeMillis();
		this.applicationName=pc.getApplicationContext().getName();
		this.cfid=pc.getCFID();
		//this.value=value;
		this.templateLine  = SystemUtil.getCurrentContext();
		
		
	}

	@Override
	public long getExecutionTime() {
		return executionTime;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public long getPayLoad() {
		return payLoad;
	}

	@Override
	public String getMeta() {
		return meta;
	}

	public final String getId() {
		return id;
	}

	public String getTypeId() {
		return typeId;
	}

	public final String getEntryHash() {
		return entryHash;
	}

	public String getResultHash() {
		return resultHash;
	}

	public long getCreateTime() {
		return createTime;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public String getCfid() {
		return cfid;
	}
	
	public String getTemplate(){
		return templateLine.template;
	}
	
	public int getLine(){
		return templateLine.line;
	}

	public static String toResultHash(Object value) {
		return Long.toString(HashUtil.create64BitHash(UDFArgConverter.serialize(value)));
	}
}
