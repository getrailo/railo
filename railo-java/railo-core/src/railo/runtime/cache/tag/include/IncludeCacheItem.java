/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
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
	private long executionTimeNS;
	private String path;
	private String name;
	private final int payload;
	
	public IncludeCacheItem(String output, PageSource ps, long executionTimeNS) {
		this.output=output;
		this.path=ps.getDisplayPath();
		this.name=ps.getFileName();
		this.executionTimeNS=executionTimeNS;
		this.payload=output==null?0:output.length();
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
	public long getPayload() {
		return payload;
	}

	@Override
	public String getMeta() {
		return path;
	}

	@Override
	public long getExecutionTime() {
		return executionTimeNS;
	}

}