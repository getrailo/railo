package railo.runtime.config.ajax;

import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.config.ConfigFactory;
import railo.runtime.config.Constants;

public class AjaxFactory {


/**
* this method deploy all ajax functions to the railo enviroment and the helper files
* @param dir tag directory
* @param doNew redeploy even the file exist, this is set to true when a new version is started
*/
public static void deployFunctions(Resource dir, boolean doNew) {
Resource f = dir.getRealResource("ajaxOnLoad."+Constants.TEMPLATE_EXTENSION);
        if(!f.exists() || doNew)ConfigFactory.createFileFromResourceEL("/resource/library/function/ajaxOnLoad."+Constants.TEMPLATE_EXTENSION,f);
        
}

/**
* this functions deploy all ajax tags to the railo enviroment and the helper files
* @param dir tag directory
* @param doNew redeploy even the file exist, this is set to true when a new version is started
*/
public static void deployTags(Resource dir, boolean doNew) {
// tags
        Resource f = dir.getRealResource("AjaxImport."+Constants.COMPONENT_EXTENSION);
        if(!f.exists() || doNew)ConfigFactory.createFileFromResourceEL("/resource/library/tag/AjaxImport."+Constants.COMPONENT_EXTENSION,f);
        f = dir.getRealResource("AjaxProxy."+Constants.COMPONENT_EXTENSION);
        if(!f.exists() || doNew)ConfigFactory.createFileFromResourceEL("/resource/library/tag/AjaxProxy."+Constants.COMPONENT_EXTENSION,f);
        f = dir.getRealResource("Div."+Constants.COMPONENT_EXTENSION);
        if(!f.exists() || doNew)ConfigFactory.createFileFromResourceEL("/resource/library/tag/Div."+Constants.COMPONENT_EXTENSION,f);
        f = dir.getRealResource("Map."+Constants.COMPONENT_EXTENSION);
        if(!f.exists() || doNew)ConfigFactory.createFileFromResourceEL("/resource/library/tag/Map."+Constants.COMPONENT_EXTENSION,f);
        f = dir.getRealResource("MapItem."+Constants.COMPONENT_EXTENSION);
        if(!f.exists() || doNew)ConfigFactory.createFileFromResourceEL("/resource/library/tag/MapItem."+Constants.COMPONENT_EXTENSION,f);
        f = dir.getRealResource("Layout."+Constants.COMPONENT_EXTENSION);
        if(!f.exists() || doNew)ConfigFactory.createFileFromResourceEL("/resource/library/tag/Layout."+Constants.COMPONENT_EXTENSION,f);
        f = dir.getRealResource("LayoutArea."+Constants.COMPONENT_EXTENSION);
        if(!f.exists() || doNew)ConfigFactory.createFileFromResourceEL("/resource/library/tag/LayoutArea."+Constants.COMPONENT_EXTENSION,f);
        f = dir.getRealResource("Window."+Constants.COMPONENT_EXTENSION);
        if(!f.exists() || doNew){
        	//String md5 = ConfigWebUtil.createMD5FromResource(f);
        	ConfigFactory.createFileFromResourceEL("/resource/library/tag/Window."+Constants.COMPONENT_EXTENSION,f);
        }
        
        
        
        
        // helper files
        dir=dir.getRealResource("railo/core/ajax/");
        if(!dir.isDirectory())dir.mkdirs();
        f = dir.getRealResource("AjaxBase."+Constants.COMPONENT_EXTENSION);
        if(!f.exists() || doNew)ConfigFactory.createFileFromResourceEL("/resource/library/tag/railo/core/ajax/AjaxBase."+Constants.COMPONENT_EXTENSION,f);
        f = dir.getRealResource("AjaxBinder."+Constants.COMPONENT_EXTENSION);
        if(!f.exists() || doNew)ConfigFactory.createFileFromResourceEL("/resource/library/tag/railo/core/ajax/AjaxBinder."+Constants.COMPONENT_EXTENSION,f);
        f = dir.getRealResource("AjaxProxyHelper."+Constants.COMPONENT_EXTENSION);
        if(!f.exists() || doNew)ConfigFactory.createFileFromResourceEL("/resource/library/tag/railo/core/ajax/AjaxProxyHelper."+Constants.COMPONENT_EXTENSION,f);
        f = dir.getRealResource("JSLoader."+Constants.COMPONENT_EXTENSION);
        if(!f.exists() || doNew)ConfigFactory.createFileFromResourceEL("/resource/library/tag/railo/core/ajax/JSLoader."+Constants.COMPONENT_EXTENSION,f);
        f = dir.getRealResource("RailoJs."+Constants.COMPONENT_EXTENSION);
        if(f.exists())f.delete();
        
        //js
        Resource jsDir = dir.getRealResource("js");
        if(!jsDir.isDirectory())jsDir.mkdirs();
        f = jsDir.getRealResource("RailoAjax.js");
        if(!f.exists() || doNew)ConfigFactory.createFileFromResourceEL("/resource/library/tag/railo/core/ajax/js/RailoAjax.js",f);
        f = jsDir.getRealResource("RailoMap.js");
        if(!f.exists() || doNew)ConfigFactory.createFileFromResourceEL("/resource/library/tag/railo/core/ajax/js/RailoMap.js",f);
        f = jsDir.getRealResource("RailoWindow.js");
        if(!f.exists() || doNew)ConfigFactory.createFileFromResourceEL("/resource/library/tag/railo/core/ajax/js/RailoWindow.js",f);
        f = jsDir.getRealResource("RailoLayout.js");
        if(!f.exists() || doNew)ConfigFactory.createFileFromResourceEL("/resource/library/tag/railo/core/ajax/js/RailoLayout.js",f);
        
        // delete wrong directory comes with 3.1.2.015
        Resource gDir = dir.getRealResource("google");
        if(gDir.isDirectory())ResourceUtil.removeEL(gDir, true);
        
        // create google/... again
        gDir = jsDir.getRealResource("google");
        if(!gDir.isDirectory())gDir.mkdirs();
        f = gDir.getRealResource("google-map.js");
        if(!f.exists() || doNew)ConfigFactory.createFileFromResourceEL("/resource/library/tag/railo/core/ajax/js/google/google-map.js",f);
        
        
        //jquery resources
        Resource jqDir = jsDir.getRealResource("jquery");
        if(!jqDir.isDirectory())jqDir.mkdirs();
        f = jqDir.getRealResource("jquery-1.4.2.js");
        if(!f.exists() || doNew)ConfigFactory.createFileFromResourceEL("/resource/library/tag/railo/core/ajax/js/jquery/jquery-1.4.2.js",f);
        f = jqDir.getRealResource("jquery-ui-1.8.2.js");
        if(!f.exists() || doNew)ConfigFactory.createFileFromResourceEL("/resource/library/tag/railo/core/ajax/js/jquery/jquery-ui-1.8.2.js",f);
        f = jqDir.getRealResource("jquery.layout.js");
        if(!f.exists() || doNew)ConfigFactory.createFileFromResourceEL("/resource/library/tag/railo/core/ajax/js/jquery/jquery.layout.js",f);
        f = jqDir.getRealResource("jquery.window.js");
        if(!f.exists() || doNew)ConfigFactory.createFileFromResourceEL("/resource/library/tag/railo/core/ajax/js/jquery/jquery.window.js",f);
  
        //css Railo Skin
        Resource cssDir = dir.getRealResource("css/jquery");
        if(!cssDir.isDirectory())cssDir.mkdirs();
        f = cssDir.getRealResource("RailoSkin.css."+Constants.TEMPLATE_EXTENSION);
        if(!f.exists() || doNew)ConfigFactory.createFileFromResourceEL("/resource/library/tag/railo/core/ajax/css/jquery/RailoSkin.css."+Constants.TEMPLATE_EXTENSION,f);
        
        //css images
        Resource imgDir = cssDir.getRealResource("images");
        if(!imgDir.isDirectory())imgDir.mkdirs();
        f = imgDir.getRealResource("ui-anim_basic_16x16.gif."+Constants.TEMPLATE_EXTENSION);
        if(!f.exists() || doNew)ConfigFactory.createFileFromResourceEL("/resource/library/tag/railo/core/ajax/css/jquery/images/ui-anim_basic_16x16.gif."+Constants.TEMPLATE_EXTENSION,f);
        f = imgDir.getRealResource("ui-bg_flat_0_aaaaaa_40x100.png."+Constants.TEMPLATE_EXTENSION);
        if(!f.exists() || doNew)ConfigFactory.createFileFromResourceEL("/resource/library/tag/railo/core/ajax/css/jquery/images/ui-bg_flat_0_aaaaaa_40x100.png."+Constants.TEMPLATE_EXTENSION,f);
        f = imgDir.getRealResource("ui-bg_flat_75_ffffff_40x100.png."+Constants.TEMPLATE_EXTENSION);
        if(!f.exists() || doNew)ConfigFactory.createFileFromResourceEL("/resource/library/tag/railo/core/ajax/css/jquery/images/ui-bg_flat_75_ffffff_40x100.png."+Constants.TEMPLATE_EXTENSION,f);
        f = imgDir.getRealResource("ui-bg_glass_55_fbf9ee_1x400.png."+Constants.TEMPLATE_EXTENSION);
        if(!f.exists() || doNew)ConfigFactory.createFileFromResourceEL("/resource/library/tag/railo/core/ajax/css/jquery/images/ui-bg_glass_55_fbf9ee_1x400.png."+Constants.TEMPLATE_EXTENSION,f);
        f = imgDir.getRealResource("ui-bg_glass_65_ffffff_1x400.png."+Constants.TEMPLATE_EXTENSION);
        if(!f.exists() || doNew)ConfigFactory.createFileFromResourceEL("/resource/library/tag/railo/core/ajax/css/jquery/images/ui-bg_glass_65_ffffff_1x400.png."+Constants.TEMPLATE_EXTENSION,f);
        f = imgDir.getRealResource("ui-bg_glass_75_dadada_1x400.png."+Constants.TEMPLATE_EXTENSION);
        if(!f.exists() || doNew)ConfigFactory.createFileFromResourceEL("/resource/library/tag/railo/core/ajax/css/jquery/images/ui-bg_glass_75_dadada_1x400.png."+Constants.TEMPLATE_EXTENSION,f);
        f = imgDir.getRealResource("ui-bg_glass_75_e6e6e6_1x400.png."+Constants.TEMPLATE_EXTENSION);
        if(!f.exists() || doNew)ConfigFactory.createFileFromResourceEL("/resource/library/tag/railo/core/ajax/css/jquery/images/ui-bg_glass_75_e6e6e6_1x400.png."+Constants.TEMPLATE_EXTENSION,f);
        f = imgDir.getRealResource("ui-bg_glass_95_fef1ec_1x400.png."+Constants.TEMPLATE_EXTENSION);
        if(!f.exists() || doNew)ConfigFactory.createFileFromResourceEL("/resource/library/tag/railo/core/ajax/css/jquery/images/ui-bg_glass_95_fef1ec_1x400.png."+Constants.TEMPLATE_EXTENSION,f);
        f = imgDir.getRealResource("ui-bg_highlight-soft_75_cccccc_1x100.png."+Constants.TEMPLATE_EXTENSION);
        if(!f.exists() || doNew)ConfigFactory.createFileFromResourceEL("/resource/library/tag/railo/core/ajax/css/jquery/images/ui-bg_highlight-soft_75_cccccc_1x100.png."+Constants.TEMPLATE_EXTENSION,f);
        f = imgDir.getRealResource("ui-icons_222222_256x240.png."+Constants.TEMPLATE_EXTENSION);
        if(!f.exists() || doNew)ConfigFactory.createFileFromResourceEL("/resource/library/tag/railo/core/ajax/css/jquery/images/ui-icons_222222_256x240.png."+Constants.TEMPLATE_EXTENSION,f);
        f = imgDir.getRealResource("ui-icons_2e83ff_256x240.png."+Constants.TEMPLATE_EXTENSION);
        if(!f.exists() || doNew)ConfigFactory.createFileFromResourceEL("/resource/library/tag/railo/core/ajax/css/jquery/images/ui-icons_2e83ff_256x240.png."+Constants.TEMPLATE_EXTENSION,f);
        f = imgDir.getRealResource("ui-icons_454545_256x240.png."+Constants.TEMPLATE_EXTENSION);
        if(!f.exists() || doNew)ConfigFactory.createFileFromResourceEL("/resource/library/tag/railo/core/ajax/css/jquery/images/ui-icons_454545_256x240.png."+Constants.TEMPLATE_EXTENSION,f);
        f = imgDir.getRealResource("ui-icons_888888_256x240.png."+Constants.TEMPLATE_EXTENSION);
        if(!f.exists() || doNew)ConfigFactory.createFileFromResourceEL("/resource/library/tag/railo/core/ajax/css/jquery/images/ui-icons_888888_256x240.png."+Constants.TEMPLATE_EXTENSION,f);
        f = imgDir.getRealResource("ui-icons_cd0a0a_256x240.png."+Constants.TEMPLATE_EXTENSION);
        if(!f.exists() || doNew)ConfigFactory.createFileFromResourceEL("/resource/library/tag/railo/core/ajax/css/jquery/images/ui-icons_cd0a0a_256x240.png."+Constants.TEMPLATE_EXTENSION,f);
       
        
        //image loader
        dir = dir.getRealResource("loader");
        if(!dir.isDirectory())dir.mkdirs();
        f = dir.getRealResource("loading.gif."+Constants.TEMPLATE_EXTENSION);
        if(!f.exists() || doNew)ConfigFactory.createFileFromResourceEL("/resource/library/tag/railo/core/ajax/loader/loading.gif."+Constants.TEMPLATE_EXTENSION,f);
}

}

