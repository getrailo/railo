package railo.runtime.type.scope.storage.clean;

import java.io.IOException;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.filter.AndResourceFilter;
import railo.commons.io.res.filter.DirectoryResourceFilter;
import railo.commons.io.res.filter.ExtensionResourceFilter;
import railo.commons.io.res.filter.ResourceFilter;
import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.op.Caster;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.scope.Scope;
import railo.runtime.type.scope.storage.StorageScopeEngine;
import railo.runtime.type.scope.storage.StorageScopeFile;
import railo.runtime.type.scope.storage.StorageScopeListener;

public class FileStorageScopeCleaner extends StorageScopeCleanerSupport {
	
	private static final ResourceFilter DIR_FILTER = new DirectoryResourceFilter();
	private static ExtensionResourceFilter EXT_FILTER = new ExtensionResourceFilter(".scpt",true);
	
	
	


	public FileStorageScopeCleaner(int type,StorageScopeListener listener) {
		super(type,listener,INTERVALL_DAY);
	}
	
	@Override
	public void init(StorageScopeEngine engine){
		super.init(engine);
	}

	@Override
	protected void _clean() {
		ConfigWebImpl cwi=(ConfigWebImpl) engine.getFactory().getConfig();
		Resource dir=type==Scope.SCOPE_CLIENT?cwi.getClientScopeDir():cwi.getSessionScopeDir();
		
		// for old files only the defintion from admin can be used
		long timeout=type==Scope.SCOPE_CLIENT?cwi.getClientTimeout().getMillis():cwi.getSessionTimeout().getMillis();
		long time = new DateTimeImpl(cwi).getTime()-timeout;
		
		try {
			// delete files that has expired
			AndResourceFilter andFilter = new AndResourceFilter(new ResourceFilter[]{EXT_FILTER,new ExpiresFilter(time,true)});
			String appName,cfid2,cfid;
			Resource[] apps = dir.listResources(DIR_FILTER),cfidDir,files;
			
			if(apps!=null)for(int a=0;a<apps.length;a++){
				appName=StorageScopeFile.decode(apps[a].getName());
				cfidDir=apps[a].listResources(DIR_FILTER);
				if(cfidDir!=null)for(int b=0;b<cfidDir.length;b++){
					cfid2=cfidDir[b].getName();
					files=cfidDir[b].listResources(andFilter);
					if(files!=null){
						for(int c=0;c<files.length;c++){
							cfid=files[c].getName();
							cfid=cfid2+cfid.substring(0,cfid.length()-5);
							
							if(listener!=null)listener.doEnd(engine,this, appName, cfid);
							
							//info("remove from memory "+appName+"/"+cfid);
							engine.remove(type,appName,cfid);
							
							info("remove file "+files[c]);
							files[c].delete();
						}
					}
				}
			}
			
			ResourceUtil.deleteEmptyFolders(dir);
		
		} catch (Throwable t) {error(t);}

		
		//long maxSize = type==Scope.SCOPE_CLIENT?cwi.getClientScopeDirSize():cwi.getSessionScopeDirSize();
		//checkSize(config,dir,maxSize,extfilter);
	}
	

	static class ExpiresFilter implements ResourceFilter {

		private long time;
		private boolean allowDir;

		public ExpiresFilter(long time, boolean allowDir) {
			this.allowDir=allowDir;
			this.time=time;
		}

		public boolean accept(Resource res) {

			if(res.isDirectory()) return allowDir;
			
			// load content
			String str=null;
			try {
				str = IOUtil.toString(res,"UTF-8");
			} 
			catch (IOException e) {
				return false;
			}
			
			int index=str.indexOf(':');
			if(index!=-1){
				long expires=Caster.toLongValue(str.substring(0,index),-1L);
				// check is for backward compatibility, old files have no expires date inside. they do ot expire
				if(expires!=-1) {
					if(expires<System.currentTimeMillis()){
						return true;
					}
					str=str.substring(index+1);
					return false;
				}
			}
			// old files not having a timestamp inside
			else if(res.lastModified()<=time) {
				return true;
				
			}
			return false;
		}
    	
    }


}
