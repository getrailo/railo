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
