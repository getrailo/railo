package railo.runtime.config;

import railo.runtime.extension.ExtensionProvider;
import railo.runtime.extension.ExtensionProviderImpl;

public class Constants {

	public static final String COMPONENT_EXTENSION = "cfc";
	public static final String TEMPLATE_EXTENSION = "cfm";
	public static final String[] ALL_EXTENSION = new String[]{COMPONENT_EXTENSION,TEMPLATE_EXTENSION};
	

	public static final String APP_CFC = "Application."+COMPONENT_EXTENSION;
	public static final String APP_CFM = "Application."+TEMPLATE_EXTENSION;
	public static final String CFAPP_NAME = "cfapplication";
	
	public static final String DEFAULT_PACKAGE = "org.railo.cfml";
	public static final String WEBSERVICE_NAMESPACE_URI="http://rpc.xml.cfml";
	
	public static final ExtensionProvider[] RAILO_EXTENSION_PROVIDERS = new ExtensionProviderImpl[]{
		new ExtensionProviderImpl("http://www.getrailo.com/ExtensionProvider.cfc",true),
		new ExtensionProviderImpl("http://www.getrailo.org/ExtensionProvider.cfc",true)
	};
	public static final String SCRIPT_TAG_NAME = "script";
	public static final String CLASS_SUFFIX = "$cf";

}
