package railo.runtime.cache.tag.include;

import java.io.Serializable;

import railo.commons.digest.HashUtil;
import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.cache.tag.CacheItem;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.dump.DumpUtil;
import railo.runtime.dump.Dumpable;
import railo.runtime.dump.SimpleDumpData;

public class IncludeCacheItem implements CacheItem, Serializable, Dumpable {

	private static final long serialVersionUID = -3616023500492159529L;

	public final String output;
	private long executionTime;
	private String path;
	private String name;
	
	public IncludeCacheItem(String output, PageSource ps, long executionTime) {
		this.output=output;
		this.path=ps.getDisplayPath();
		this.name=ps.getFileName();
		this.executionTime=executionTime;
	}

	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties properties) {
		DumpTable table = new DumpTable("#669999","#ccffff","#000000");
		table.setTitle("IncludeCacheEntry");
		table.appendRow(1,new SimpleDumpData("Output"),DumpUtil.toDumpData(new SimpleDumpData(output), pageContext, maxlevel, properties));
		if(path!=null)table.appendRow(1,new SimpleDumpData("Path"),DumpUtil.toDumpData(new SimpleDumpData(path), pageContext, maxlevel, properties));
		return table;
	}
	
	public String toString(){
		return output;
	}

	@Override
	public String getHashFromValue() {
		return Long.toString(HashUtil.create64BitHash(output));
	}

	public String getOutput() {
		return output;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getPayload() {
		return 1;
	}

	@Override
	public String getMeta() {
		// TODO Auto-generated method stub
		return path;
	}

	@Override
	public long getExecutionTime() {
		return executionTime;
	}

}