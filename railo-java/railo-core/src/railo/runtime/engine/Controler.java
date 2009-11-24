package railo.runtime.engine;

import java.util.Map;

import railo.commons.io.res.Resource;
import railo.commons.io.res.filter.ExtensionResourceFilter;
import railo.commons.io.res.filter.ResourceFilter;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.SystemOut;
import railo.commons.lang.types.RefBoolean;
import railo.runtime.CFMLFactoryImpl;
import railo.runtime.Mapping;
import railo.runtime.MappingImpl;
import railo.runtime.PageSource;
import railo.runtime.PageSourcePool;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.ConfigServer;
import railo.runtime.config.ConfigWeb;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.functions.dateTime.DateAdd;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.scope.ClientFile;
import railo.runtime.type.scope.ScopeContext;
import railo.runtime.type.util.ArrayUtil;

/**
 * own thread how check the main thread and his data 
 */
public final class Controler extends Thread {

	private int interval;
	private long lastMinuteInterval=System.currentTimeMillis();
	private long lastHourInterval=System.currentTimeMillis();
	
	int maxPoolSize=500;
    private Map contextes;
    private RefBoolean run;
	//private ScheduleThread scheduleThread;
	private ConfigServer configServer;

	/**
	 * @param contextes
	 * @param interval
	 * @param run 
	 */
	public Controler(ConfigServer configServer,Map contextes,int interval, RefBoolean run) {		
        this.contextes=contextes;
		this.interval=interval;
        this.run=run;
        this.configServer=configServer;
        
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		//scheduleThread.start();
		boolean firstRun=true;
		
		CFMLFactoryImpl factories[]=null;
		while(run.toBooleanValue()) {
	        try {
				sleep(interval);
			} 
            catch (InterruptedException e) {
				e.printStackTrace();
			}
            long now = System.currentTimeMillis();
            //print.out("now:"+new Date(now));
            boolean doMinute=lastMinuteInterval+60000<now;
            if(doMinute)lastMinuteInterval=now;
            boolean doHour=(lastHourInterval+(1000*60*60))<now;
            if(doHour)lastHourInterval=now;
            
            if(doMinute) System.gc();
            // broadcar cluster scope
            factories=toFactories(factories,contextes);
            try {
				ScopeContext.getClusterScope(configServer,true).broadcast();
			} 
            catch (Throwable t) {
				t.printStackTrace();
			}
            
            
            for(int i=0;i<factories.length;i++) {
	            run(factories[i], doMinute, doHour,firstRun);
	        }
	        if(factories.length>0)
				firstRun=false;
	    }    
	}
	private CFMLFactoryImpl[] toFactories(CFMLFactoryImpl[] factories,Map contextes) {
		if(factories==null || factories.length!=contextes.size())
			factories=(CFMLFactoryImpl[]) contextes.values().toArray(new CFMLFactoryImpl[contextes.size()]);
		
		return factories;
	}

	private void run(CFMLFactoryImpl cfmlFactory, boolean doMinute, boolean doHour, boolean firstRun) {
		
		try {
				boolean isRunning=cfmlFactory.getUsedPageContextLength()>0;   
			    if(isRunning) {
					cfmlFactory.checkTimeout();
			    }
				ConfigWeb config = null;
				
				if(firstRun) {
					if(config==null) {
						config = cfmlFactory.getConfig();
						ThreadLocalConfig.register(config);
					}
					config.reloadTimeServerOffset();
					checkOldClientFile(config);
				}
				if(doMinute) {
					if(config==null) {
						config = cfmlFactory.getConfig();
						ThreadLocalConfig.register(config);
					}
					
				// clear unused DB Connections
					try{((ConfigImpl)config).getDatasourceConnectionPool().clear();}catch(Throwable t){}
				// clear all unused scopes
					try{cfmlFactory.getScopeContext().clearUnused(cfmlFactory);}catch(Throwable t){}
				// Memory usage
				// clear Query Cache
					try{cfmlFactory.getQueryCache().clearUnused();}catch(Throwable t){}
				// clear Page Pool
					try{doClearPagePools((ConfigWebImpl) config);}catch(Throwable t){}
					try{doCheckMappings(config);}catch(Throwable t){}
				}
				// every hour
				if(doHour) {
					if(config==null) {
						config = cfmlFactory.getConfig();
						ThreadLocalConfig.register(config);
					}
				
					try{config.reloadTimeServerOffset();}catch(Throwable t){}
					try{checkClientFileSize(config);}catch(Throwable t){}
					try{checkCacheFileSize(config);}catch(Throwable t){}
				}
			}
			catch(Throwable t){
				
			}
			finally{
				ThreadLocalConfig.release();
			}
	}

	private void checkClientFileSize(ConfigWeb config) {
		ExtensionResourceFilter filter = new ExtensionResourceFilter(".script",true);
		
		try {
			long date = DateAdd.invoke("d", -90, new DateTimeImpl(config)).getTime();
			ResourceUtil.deleteFileOlderThan(config.getClientScopeDir(),date,filter);
			ResourceUtil.deleteEmptyFolders(config.getClientScopeDir());
		
		} catch (Exception e) {}
		

		
		
		//checkSize(config.getClientScopeDir(),config.getClientScopeDirSize(),filter);
		
	}
	
	private void checkOldClientFile(ConfigWeb config) {
		ExtensionResourceFilter filter = new ExtensionResourceFilter(".script",false);
		
		// move old structured file in new structure
		try {
			Resource dir = config.getClientScopeDir(),trgres;
			Resource[] children = dir.listResources(filter);
			String src,trg;
			int index;
			for(int i=0;i<children.length;i++) {
				src=children[i].getName();
				index=src.indexOf('-');

				trg=ClientFile.getFolderName(src.substring(0,index), src.substring(index+1),false);
				trgres=dir.getRealResource(trg);
				if(!trgres.exists()){
					trgres.createFile(true);
					ResourceUtil.copy(children[i],trgres);
				}
					//children[i].moveTo(trgres);
				children[i].delete();
				
			}
		} catch (Throwable t) {}
		
	}
	
	private void checkCacheFileSize(ConfigWeb config) {
		checkSize(config.getCacheDir(),config.getCacheDirSize(),new ExtensionResourceFilter(".cache"));
	}
	
	private void checkSize(Resource dir,long maxSize, ResourceFilter filter) {
		if(!dir.exists()) return;
		Resource res=null;
		int count=ArrayUtil.size(dir.list(filter));
		long size=ResourceUtil.getRealSize(dir,filter);
		// TODO scheiss impl ruft immer wieder grﾚsse ab
		while(count>100000 || size>maxSize) {
			Resource[] files = dir.listResources(filter);
			for(int i=0;i<files.length;i++) {
				if(res==null || res.lastModified()>files[i].lastModified()) {
					res=files[i];
				}
			}
			if(res!=null) {
				size-=res.length();
				if(res.delete()) count--;
			}
			res=null;
		}
	}

	private void doCheckMappings(ConfigWeb config) {
        Mapping[] mappings = config.getMappings();
        for(int i=0;i<mappings.length;i++) {
            Mapping mapping = mappings[i];
            mapping.check();
        }
    }

    private void doClearPagePools(ConfigWebImpl config) {
        PageSourcePool[] pools=getPageSourcePools(config);
        int poolSize=getPageSourcePoolSize(pools);
        long start =System.currentTimeMillis();
        int startsize=poolSize;
        while(poolSize>maxPoolSize) {
            removeOldest(pools);
            poolSize--;
        }
        if(startsize>poolSize)
        	SystemOut.printDate(config.getOutWriter(),"contract pagepools from "+startsize+" to "+poolSize +" in "+(System.currentTimeMillis()-start)+" millis");
        
        
    }

    private PageSourcePool[] getPageSourcePools(ConfigWeb config) {
        Mapping[] mappings = config.getMappings();
        PageSourcePool[] pools=new PageSourcePool[mappings.length];
        int size=0;
        
        for(int i=0;i<mappings.length;i++) {
            pools[i]=((MappingImpl)mappings[i]).getPageSourcePool();
            size+=pools[i].size();
        }
        return pools;
    }
    private int getPageSourcePoolSize(PageSourcePool[] pools) {
        int size=0;
        for(int i=0;i<pools.length;i++)size+=pools[i].size();
        return size;
    }
    private void removeOldest(PageSourcePool[] pools) {
        PageSourcePool pool=null;
        Object key=null;
        PageSource ps=null;
        
        long date=-1;
        for(int i=0;i<pools.length;i++) {
        	try {
	            Object[] keys=pools[i].keys();
	            for(int y=0;y<keys.length;y++) {
	                ps = pools[i].getPageSource(keys[y],false);
	                if(date==-1 || date>ps.getLastAccessTime()) {
	                    pool=pools[i];
	                    key=keys[y];
	                    date=ps.getLastAccessTime();
	                }
	            }
        	}
        	catch(Throwable t) {
        		pools[i].clear();
        	}
        	
        }
        if(pool!=null)pool.remove(key);
    }

    /*private void doLogMemoryUsage(ConfigWeb config) {
		if(config.logMemoryUsage()&& config.getMemoryLogger()!=null)
			config.getMemoryLogger().write();
	}*/

}