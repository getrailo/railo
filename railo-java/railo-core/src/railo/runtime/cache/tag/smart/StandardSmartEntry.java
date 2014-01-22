package railo.runtime.cache.tag.smart;


import railo.print;
import railo.commons.digest.HashUtil;
import railo.commons.io.SystemUtil;
import railo.commons.io.SystemUtil.TemplateLine;
import railo.runtime.PageContext;
import railo.runtime.cache.tag.CacheHandlerFactory;
import railo.runtime.cache.tag.udf.UDFArgConverter;
import railo.runtime.functions.other.CreateUUID;

public abstract class StandardSmartEntry implements SmartEntry {

	private final String id;
	private final String typeId;
	private final String entryHash;
	private final String resultHash;
	private final long createTime;
	private final String applicationName;
	private final String cfid;
	//private final Object value;
	private TemplateLine templateLine;

	public StandardSmartEntry(PageContext pc, String entryHash, String resultHash, int cacheType) {
		this.id=CreateUUID.invoke();// TODO better impl
		this.typeId=CacheHandlerFactory.toStringCacheName(cacheType, null);
		this.entryHash=entryHash;
		this.resultHash=resultHash;
		this.createTime=System.currentTimeMillis();
		this.applicationName=pc.getApplicationContext().getName();
		this.cfid=pc.getCFID();
		//this.value=value;
		this.templateLine  = SystemUtil.getCurrentContext();
		
		
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
