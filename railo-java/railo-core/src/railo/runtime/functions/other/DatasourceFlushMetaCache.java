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
package railo.runtime.functions.other;

import java.util.Map;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.db.DataSource;
import railo.runtime.db.DataSourceSupport;


public class DatasourceFlushMetaCache {

    public synchronized static boolean call(PageContext pc) {
    	return call(pc, null);
	}

    public synchronized static boolean call(PageContext pc,String datasource) {
    	
    	DataSource[] sources = pc.getConfig().getDataSources();
    	DataSourceSupport ds;
    	boolean has=false;
    	for(int i=0;i<sources.length;i++){
    		ds=(DataSourceSupport) sources[i];
    		if(StringUtil.isEmpty(datasource) || ds.getName().equalsIgnoreCase(datasource.trim())){
    			Map cache=ds.getProcedureColumnCache();
    			if(cache!=null) cache.clear();
    			if(!StringUtil.isEmpty(datasource))return true;
    			has=true;
    		}
    	}
    	return has;
	}

}
