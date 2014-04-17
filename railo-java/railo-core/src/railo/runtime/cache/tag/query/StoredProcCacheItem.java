package railo.runtime.cache.tag.query;

import java.io.Serializable;

import railo.commons.digest.HashUtil;
import railo.runtime.cache.tag.CacheItem;
import railo.runtime.cache.tag.udf.UDFArgConverter;
import railo.runtime.type.Struct;

public class StoredProcCacheItem implements CacheItem, Serializable {

	private static final long serialVersionUID = 7327671003736543783L;


	private Struct sct;


	private String procedure;


	private long executionTime;


	public StoredProcCacheItem(Struct sct, String procedure, long executionTime) {
		this.sct=sct;
		this.procedure=procedure;
		this.executionTime=executionTime;
	}

	@Override
	public String getHashFromValue() {
		return Long.toString(HashUtil.create64BitHash(UDFArgConverter.serialize(sct)));
	}
	
	@Override
	public String getName() {
		return procedure;
	}

	@Override
	public long getPayload() {
		return sct.size();
	}
	
	@Override
	public String getMeta() {
		return "";
	}
	
	@Override
	public long getExecutionTime() {
		return executionTime;
	}

	public Struct getStruct() {
		return sct;
	}

}
