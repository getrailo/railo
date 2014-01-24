package railo.runtime.cache.tag.udf;

import java.io.Serializable;

import railo.commons.digest.HashUtil;
import railo.runtime.PageContext;
import railo.runtime.cache.tag.CacheItem;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.dump.DumpUtil;
import railo.runtime.dump.Dumpable;
import railo.runtime.dump.SimpleDumpData;

public class UDFCacheItem implements CacheItem, Serializable, Dumpable {

	private static final long serialVersionUID = -3616023500492159529L;

	public final String output;
	public final Object returnValue;
	private String udfName;
	private String meta;
	private long executionTime;

	
	public UDFCacheItem(String output, Object returnValue, String udfName, String meta, long executionTime) {
		this.output = output;
		this.returnValue = returnValue;
		this.udfName = udfName;
		this.meta = meta;
		this.executionTime=executionTime;
	}

	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties properties) {
		DumpTable table = new DumpTable("#669999","#ccffff","#000000");
		table.setTitle("UDFCacheEntry");
		table.appendRow(1,new SimpleDumpData("Return Value"),DumpUtil.toDumpData(returnValue, pageContext, maxlevel, properties));
		table.appendRow(1,new SimpleDumpData("Output"),DumpUtil.toDumpData(new SimpleDumpData(output), pageContext, maxlevel, properties));
		return table;
	}
	
	public String toString(){
		return output;
	}

	@Override
	public String getHashFromValue() {
		return Long.toString(HashUtil.create64BitHash(output+":"+UDFArgConverter.serialize(returnValue)));
	}

	@Override
	public String getName() {
		return udfName;
	}

	@Override
	public int getPayload() {
		return 1;
	}

	@Override
	public String getMeta() {
		return meta;
	}

	@Override
	public long getExecutionTime() {
		return executionTime;
	}

}
