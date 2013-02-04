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
