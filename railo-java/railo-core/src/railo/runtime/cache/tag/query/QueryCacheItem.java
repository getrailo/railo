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
import java.util.Date;

import railo.commons.digest.HashUtil;
import railo.runtime.PageContext;
import railo.runtime.cache.tag.CacheItem;
import railo.runtime.cache.tag.udf.UDFArgConverter;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.Dumpable;
import railo.runtime.type.Query;

public class QueryCacheItem implements CacheItem, Dumpable, Serializable {

	private static final long serialVersionUID = 7327671003736543783L;

	public final Query query;
	private final long creationDate;

	public QueryCacheItem(Query query){
		this.query=query;
		this.creationDate=System.currentTimeMillis();
	}

	@Override
	public String getHashFromValue() {
		return Long.toString(HashUtil.create64BitHash(UDFArgConverter.serialize(query)));
	}
	
	@Override
	public String getName() {
		return query.getName();
	}

	public Query getQuery() {
		return query;
	}

	@Override
	public long getPayload() {
		return query.getRecordcount();
	}
	
	@Override
	public String getMeta() {
		return query.getSql().getSQLString();
	}
	
	@Override
	public long getExecutionTime() {
		return query.getExecutionTime();
	}

	public boolean isCachedAfter(Date cacheAfter) {
    	if(cacheAfter==null) return true;
    	if(creationDate>=cacheAfter.getTime()){
        	return true;
        }
        return false;
    }

	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties properties) {
		return query.toDumpData(pageContext, maxlevel, properties);
	}

}
