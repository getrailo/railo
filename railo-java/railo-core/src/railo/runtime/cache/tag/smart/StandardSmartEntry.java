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
	private String resultHash=null;
	private final long createTime;
	private final String applicationName;
	private final String cfid;
	private final Object value;
	private TemplateLine templateLine;

	public StandardSmartEntry(PageContext pc, Object value, String id, int cacheType) {
		this.id=CreateUUID.invoke();// TODO better impl
		this.typeId=CacheHandlerFactory.toStringCacheName(cacheType, null);
		this.entryHash=id;
		this.createTime=System.currentTimeMillis();
		this.applicationName=pc.getApplicationContext().getName();
		this.cfid=pc.getCFID();
		this.value=value;
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
		if(this.resultHash==null)
			this.resultHash=Long.toString(HashUtil.create64BitHash(UDFArgConverter.serialize(value)));
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

}
