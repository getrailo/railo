package railo.runtime.config.ajax;

import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.config.ConfigWebFactory;

public class AjaxFactory {


	/**
	 * this method deploy all ajax functions to the railo enviroment and the helper files
	 * @param dir tag directory
	 * @param doNew redeploy even the file exist, this is set to true when a new version is started
	 */
	public static void deployFunctions(Resource dir, boolean doNew) {
		Resource f = dir.getRealResource("ajaxOnLoad.cfm");
        if(!f.exists() || doNew)ConfigWebFactory.createFileFromResourceEL("/resource/library/function/ajaxOnLoad.cfm",f);
        
	}
	
	/**
	 * this functions deploy all ajax tags to the railo enviroment and the helper files
	 * @param dir tag directory
	 * @param doNew redeploy even the file exist, this is set to true when a new version is started
	 */
	public static void deployTags(Resource dir, boolean doNew) {
		// tags
        Resource f = dir.getRealResource("AjaxImport.cfc");
        if(!f.exists() || doNew)ConfigWebFactory.createFileFromResourceEL("/resource/library/tag/AjaxImport.cfc",f);
        f = dir.getRealResource("AjaxProxy.cfc");
        if(!f.exists() || doNew)ConfigWebFactory.createFileFromResourceEL("/resource/library/tag/AjaxProxy.cfc",f);
        f = dir.getRealResource("Div.cfc");
        if(!f.exists() || doNew)ConfigWebFactory.createFileFromResourceEL("/resource/library/tag/Div.cfc",f);
        f = dir.getRealResource("Map.cfc");
        if(!f.exists() || doNew)ConfigWebFactory.createFileFromResourceEL("/resource/library/tag/Map.cfc",f);
        f = dir.getRealResource("MapItem.cfc");
        if(!f.exists() || doNew)ConfigWebFactory.createFileFromResourceEL("/resource/library/tag/MapItem.cfc",f);
        
        // helper files
        dir=dir.getRealResource("railo/core/ajax/");
        if(!dir.isDirectory())dir.mkdirs();
        f = dir.getRealResource("AjaxBase.cfc");
        if(!f.exists() || doNew)ConfigWebFactory.createFileFromResourceEL("/resource/library/tag/railo/core/ajax/AjaxBase.cfc",f);
        f = dir.getRealResource("AjaxBinder.cfc");
        if(!f.exists() || doNew)ConfigWebFactory.createFileFromResourceEL("/resource/library/tag/railo/core/ajax/AjaxBinder.cfc",f);
        f = dir.getRealResource("AjaxProxyHelper.cfc");
        if(!f.exists() || doNew)ConfigWebFactory.createFileFromResourceEL("/resource/library/tag/railo/core/ajax/AjaxProxyHelper.cfc",f);
        f = dir.getRealResource("JSLoader.cfc");
        if(!f.exists() || doNew)ConfigWebFactory.createFileFromResourceEL("/resource/library/tag/railo/core/ajax/JSLoader.cfc",f);
        f = dir.getRealResource("RailoJs.cfc");
        if(f.exists())f.delete();
        Resource jsDir = dir.getRealResource("js");
        if(!jsDir.isDirectory())jsDir.mkdirs();
        f = jsDir.getRealResource("RailoAjax.js");
        if(!f.exists() || doNew)ConfigWebFactory.createFileFromResourceEL("/resource/library/tag/railo/core/ajax/js/RailoAjax.js",f);
        f = jsDir.getRealResource("RailoMap.js");
        if(!f.exists() || doNew)ConfigWebFactory.createFileFromResourceEL("/resource/library/tag/railo/core/ajax/js/RailoMap.js",f);
        
        // delete wrong directory comes with 3.1.2.015
        Resource gDir = dir.getRealResource("google");
        if(gDir.isDirectory())ResourceUtil.removeEL(gDir, true);
        
        // create google/... again
        gDir = jsDir.getRealResource("google");
        if(!gDir.isDirectory())gDir.mkdirs();
        f = gDir.getRealResource("google-map.js");
        if(!f.exists() || doNew)ConfigWebFactory.createFileFromResourceEL("/resource/library/tag/railo/core/ajax/js/google/google-map.js",f);
        
        
        
        
        dir = dir.getRealResource("loader");
        if(!dir.isDirectory())dir.mkdirs();
        f = dir.getRealResource("loading.gif.cfm");
        if(!f.exists() || doNew)ConfigWebFactory.createFileFromResourceEL("/resource/library/tag/railo/core/ajax/loader/loading.gif.cfm",f);
	}

}
