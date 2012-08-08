package railo.runtime.type.udf;

import java.io.Serializable;

import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.dump.DumpUtil;
import railo.runtime.dump.Dumpable;
import railo.runtime.dump.SimpleDumpData;

public class UDFCacheEntry implements Serializable,Dumpable {

	public final String output;
	public final Object returnValue;
	//public final long creationdate=System.currentTimeMillis();

	
	public UDFCacheEntry(String output, Object returnValue) {
		this.output = output;
		this.returnValue = returnValue;
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

}
