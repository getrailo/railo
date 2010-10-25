<!--- Regional --->
<cfset stText.Regional.Server                = "You can define regional settings that will be used as a default for all web contexts here. These settings have no direct effect on the current instance. Railo lets you set your own individual locale, timezone and timeserver.">
<cfset stText.Regional.Web                   = "Railo lets you set your own individual locale, timezone and timeserver.">
<cfset stText.Regional.Locale                = "Locale">
<cfset stText.Regional.LocaleDescription    = "Define the desired time locale for Railo, this will change the default locale for the context of the web.">
<cfset stText.Regional.Other                 = " --- other --- ">
<cfset stText.Regional.TimeZone              = "Time zone">
<cfset stText.Regional.TimeZoneDescription   = "Define the desired time zone for Railo, this will also change the time for the context of the web.<br/>">
<cfset stText.Regional.ServerProp.server     = "Server Value">
<cfset stText.Regional.ServerProp.web       = "Server Administrator Value">
<cfset stText.Regional.TimeServer            = "Time server (NTP)">
<cfset stText.Regional.TimeServerDescription = "Time server that returns the current time. If set, this time will be used within Railo instead of the local server time. (Example: swisstime.ethz.ch, time.nist.gov)<br/>">
<cfset stText.Regional.TimeServerMissing     = "Please define a value for the field timezone">
<cfset stText.Regional.DefaultEncoding       = "Default encoding">
<cfset stText.Regional.DefaultEncodingDescription = "">
<cfset stText.Regional.missingEncoding= "Please enter a value for the default encoding" >

<!--- Charset --->
<cfset stText.charset.Server                = "Specify the default server character set">
<cfset stText.charset.Web                   = stText.charset.Server>

<cfset stText.charset.webCharset       = "Web charset">
<cfset stText.charset.webCharsetDescription = "Default character set for output streams, form-, url-, and cgi scope variables and reading/writing the header">
<cfset stText.charset.missingWebCharset= "please specify a web charset" >

<cfset stText.charset.templateCharset       			= "Template charset">
<cfset stText.charset.templateCharsetDescription 		= "Default character set for templates (*.cfm and *.cfc files)">
<cfset stText.charset.missingTemplateCharset	     	= "Please specify a template charset" >

<cfset stText.charset.resourceCharset       			= "Resource charset">
<cfset stText.charset.resourceCharsetDescription 	= "Default character set for reading from/writing to various resources">
<cfset stText.charset.missingResourceCharset	     	= "Please specify a resource charset" >

<!--- Components --->
<cfset stText.Components.Component                        = "Component">
<cfset stText.Components.Server                           = "Define the component settings that will be used as a default for all web contexts. ">
<cfset stText.Components.Web                              = "Defines how components will be handled by Railo.">
<cfset stText.Components.BaseComponent                    = "Base/Root Component">
<cfset stText.Components.BaseComponentDescription         = "Every component that does not explicitly extend another component (attribute ""extends"") will by default extend this component. This means that every component extends this base component in some way.">
<cfset stText.Components.BaseComponentMissing             = "Please enter a value for the base/root component">
<cfset stText.Components.ComponentDumpTemplate            = "Component ""dump"" template">
<cfset stText.Components.ComponentDumpTemplateDescription = "If you call a component directly this template will be invoked to dump the component. (Example: http://www.railo.ch/ch/railo/common/Example.cfc)">
<cfset stText.Components.ComponentDumpTemplateMissing     = "Please enter a value for the ""dump"" template">
<cfset stText.Components.DataMemberAccessType             = "Data member access type">
<cfset stText.Components.DataMemberAccessTypeDescription  = "Define the accessor for the data-members of a component. This defines how variables of the ""this"" scope of a component can be accessed from outside of the component.">
<cfset stText.Components.DMATPrivate                      = "private">
<cfset stText.Components.DMATPackage                      = "package">
<cfset stText.Components.DMATPublic                       = "public (CFML standard)">
<cfset stText.Components.DMATRemote                       = "remote">

<cfset stText.Components.triggerDataMember				="Magic functions">
<cfset stText.Components.triggerDataMemberDescription
	="If there is no accessible data member (property, element of the this scope) inside a component, Railo searches for available matching &quot;getters&quot; or &quot;setters&quot; for the requested property.
	The following example should clarify this behaviour. &quot;somevar = myComponent.properyName&quot;. If &quot;myComponent&quot; has no accessible data member named &quot;propertyName&quot;,
	Railo searches for a function member (method) named &quot;getPropertyName&quot;.">
<cfset stText.Components.useShadow="Variables scope">
<cfset stText.Components.useShadowDescription="Defines whether a component has an independent variables scope parallel to the &quot;this&quot; scope (CFML standard) or not.">

<!--- Scopes --->
<cfset stText.Scopes.Server                        = "You can define the scope settings that will be used as default for all web contexts here.">
<cfset stText.Scopes.Web                           = "Here you can define the settings for how Railo handles scopes.">
<cfset stText.Scopes.Cascading                     = "Cascading">
<cfset stText.Scopes.CascadingDescription          = "Depending on this setting Railo scans certain scopes to find a variable called from the CFML source. This will only happen, when the variable is called without a scope. (Example: ##myVar## instead of ##variables.myVar##)<br/>- strict: scans only the variables scope<br/>- small: scans the scopes variables,cgi,url,form<br/>- standard (CFML Standard): scans the scopes variables,cgi,url,form,cookie">
<cfset stText.Scopes.Strict                        = "strict">
<cfset stText.Scopes.Small                         = "small">
<cfset stText.Scopes.Standard                      = "standard (CFML Default)">
<cfset stText.Scopes.CascadeToResultSet            = "Search resultsets">
<cfset stText.Scopes.CascadeToResultSetDescription = "When a variable has no scope defined (Example: ##myVar## instead of ##variables.myVar##), Railo will also search available resultsets (CFML&nbsp;Standard) or not">
<cfset stText.Scopes.SessionType             		= "Session type">
<cfset stText.Scopes.SessionType_j2ee          		= "J2EE">
<cfset stText.Scopes.SessionType_cfml          		= "CFML">
<cfset stText.Scopes.SessionTypeDescription    		= "J2EE Sessions allow to make sessions over a cluster. when you change this setting you will lose your current session and you must make a new login">
<cfset stText.Scopes.SessionManagement             = "Session management">
<cfset stText.Scopes.SessionManagementDescription  = "By default session management can be enabled. This behaviour can be overridden by the tag cfapplication.">
<cfset stText.Scopes.ClientManagement              = "Client management">
<cfset stText.Scopes.ClientManagementDescription   = "By default client management can be enabled. This behaviour can be overridden by the tag cfapplication.">
<cfset stText.Scopes.mergeUrlForm                 = "Merge URL and form">
<cfset stText.Scopes.mergeUrlFormDescription       = "This setting defines if the scopes URL and form will be merged together or not. CFML Default is false.">

<cfset stText.Scopes.DomainCookies                 = "Domain cookies">
<cfset stText.Scopes.DomainCookiesDescription      = "Enable or disable domain cookies. This behaviour can be overridden by the tag cfapplication.">
<cfset stText.Scopes.ClientCookies                 = "Client cookies">
<cfset stText.Scopes.ClientCookiesDescription      = "Enable or disable client cookies. This behaviour can be overridden by the tag cfapplication.">
<cfset stText.Scopes.TimeoutDaysValue              = "Value days for ">
<cfset stText.Scopes.TimeoutHoursValue             = "Value hours for ">
<cfset stText.Scopes.TimeoutMinutesValue           = "Value minutes for ">
<cfset stText.Scopes.TimeoutSecondsValue           = "Value seconds for ">
<cfset stText.Scopes.TimeoutEndValue               = "timeout must have a Integer Value">
<cfset stText.Scopes.SessionTimeout                = "Session timeout">
<cfset stText.Scopes.SessionTimeoutDescription     = "Sets the amount of time Railo will keep the session scope alive. This behaviour can be overridden by the tag cfapplication.">
<cfset stText.Scopes.ApplicationTimeout            = "Application timeout">
<cfset stText.Scopes.ApplicationTimeoutDescription = "Sets the amount of time Railo will keep the application scope alive. This behaviour can be overridden by the tag cfapplication.">
<cfset stText.Scopes.ClientTimeout            = "Client timeout">
<cfset stText.Scopes.ClientTimeoutDescription = "Sets the amount of time Railo will keep the client scope alive. ">


<cfset stText.Scopes.LocalMode="Local scope mode">
<cfset stText.Scopes.LocalModeDesc="Defines how the local scope of a function is invoked when a variable with no scope definition is used.<br>
- always: the local scope is always invoked<br>
- update (CFML standard): the local scope is only invoked when the key already exists in it">
<cfset stText.Scopes.LocalModeAlways="always">
<cfset stText.Scopes.LocalModeUpdate="update (CFML standard)">

<!--- Application --->
<cfset stText.application.Server                        = "Defines application settings that represent the default values for all web contextes">
<cfset stText.application.Web                           = "Here you can define several default settings for the application context. These settings can be overridden with the tag cfapplication or the Application.cfc.">
<cfset stText.application.scriptProtect       			= "Script-protect">
<cfset stText.application.scriptProtectDescription 		= "The configuration of Script protect, secures your system from ""cross-site scripting""">
<cfset stText.application.missingScriptProtect	     	= "Please select a value for script-protect" >


<cfset stText.application.scriptProtectNone       			= "Script-protect is not active">
<cfset stText.application.scriptProtectAll       			= "Script-protect checks in all scopes for external data (cgi,cookie,form,url)">
<cfset stText.application.scriptProtectCustom       		= "You can define the scopes to be checked individually">

<cfset stText.application.RequestTimeout                = "Request timeout">
<cfset stText.application.RequestTimeoutDescription     = "Sets the amount of time Railo will wait for a request to finish before a request timeout will be raised. This means that the execution of the request will be stopped. This behaviour can be overridden by the tag cfsetting.">

<!--- Proxy --->
<!--- translate --->
<cfset stText.proxy.Server                        = "Define a global proxy, that will be used in several tags by default (cfhttp,cfftp,cfmail ...)">
<cfset stText.proxy.Web                           = stText.proxy.Server>


<cfset stText.proxy.server       			= "Server">
<cfset stText.proxy.serverDescription 		= "URL of a proxy server eg.""http://myproxyserver.org/""">

<cfset stText.proxy.port       				= "Port">
<cfset stText.proxy.PortDescription 		= "Port for the proxy server (default:80)">
<cfset stText.proxy.missingPort     		= "Please enter a value for the proxy port" >

<cfset stText.proxy.username       			= "Username">
<cfset stText.proxy.usernameDescription 	= "Username for the proxy">

<cfset stText.proxy.password       			= "Password">
<cfset stText.proxy.passwordDescription 	= "Password for the proxy">

<cfset stText.proxy.enable="Use proxy">
<cfset stText.proxy.disable="Do not use proxy">
<cfset stText.proxy.settings="Proxy settings">

<!--- // translate --->

<!--- Listener --->
<cfset stText.application.listener="Application listener">
<cfset stText.application.listenerDescription="Sets how requests are handled and which templates are invoked by default.">

<!--- Type --->
<cfset stText.application.listenerType="Type">
<cfset stText.application.listenerTypeDescription="Please select the type of the listener">

<cfset stText.application.listenerType_none="None">
<cfset stText.application.listenerTypeDescription_none="When a request is called no other initialization template will be invoked by Railo">

<cfset stText.application.listenerType_classic="Classical (CFML &lt; 7)">
<cfset stText.application.listenerTypeDescription_classic="Classic handling. Railo looks for the file ""Application.cfm"" and a coresponding file ""OnRequestEnd.cfm""">

<cfset stText.application.listenerType_modern="Modern">
<cfset stText.application.listenerTypeDescription_modern="Modern handling. Railo only looks for the file ""Application.cfc""">

<cfset stText.application.listenerType_mixed="Mixed (CFML &gt;= 7)">
<cfset stText.application.listenerTypeDescription_mixed="Mixed handling. Railo looks for a file ""Application.cfm/OnRequestEnd.cfm"" as well as for the file ""Application.cfc""">

<!--- Mode --->
<cfset stText.application.listenerMode="Mode">
<cfset stText.application.listenerModeDescription="Defines where Railo looks for the files ""Application.cfc/Application.cfm"". In case of type ""none"" this setting is meaningless.">

<cfset stText.application.listenerMode_curr="Current">
<cfset stText.application.listenerModeDescription_curr="Looks for the file ""Application.cfc/Application.cfm"" only in the current template directory .">

<cfset stText.application.listenerMode_root="Root">
<cfset stText.application.listenerModeDescription_root="Looks for the file ""Application.cfc/Application.cfm"" only in the webroot .">

<cfset stText.application.listenerMode_curr2root="Current tp root (CFML default)">
<cfset stText.application.listenerModeDescription_curr2root="Looks for the file ""Application.cfc/Application.cfm"" from the current up to the webroot directory.">

<cfset stText.err.errorTemplateDescription[500]="Template that will be invoked in case of an error. This setting can be overridden by the tag CFError.">
<cfset stText.err.errorTemplateDescription[404]="Template that will be invoked in case of a missing error. This setting can be overridden by the tag CFError.">


<cfset stText.err.errorTemplate[404]="Missing Template Error (404)">
<cfset stText.err.errorTemplate[500]="General Error Template (500)">
<cfset stText.err.descr="Please enter an individual error template.">



<cfset stText.err.errorStatusCode="Status code">
<cfset stText.err.errorStatusCodeDescription="In case of an exception should an other status code be used or would it still be 200">

<!--- Output --->
<cfset stText.setting.web="Railo output control">
<cfset stText.setting.whitespace="Whitespace management">
<cfset stText.setting.whitespaceDescription="Removes all white spaces in the output that follow a white space">

<cfset stText.setting.showVersion="Output Railo version">
<cfset stText.setting.showVersionDescription="Return the Railo version in the response header">

<cfset stText.setting.server=stText.setting.web>


<cfset stText.application.AllowURLRequestTimeout="Request timeout in URL">
<cfset stText.application.AllowURLRequestTimeoutDesc="When the URL parameter [RequestTimeout] is passed in the URL obey it (behaviour like CFML 5, 7 & 8)">

<cfset stText.setting.cacheDesc="Different Cache and Performance settings to improve overall performance">

<cfset stText.setting.inspectTemplate="Inspect Templates (CFM/CFC)">


<cfsavecontent variable="stText.setting.inspectTemplateNeverDesc">
When checked, any requested found to currently reside in the template cache will not be inspected for potential updates. For sites where templates are not updated during the life of the server, this minimizes file system overhead.
</cfsavecontent>
<cfsavecontent variable="stText.setting.inspectTemplateOnceDesc">
When checked, any requested files will be inspected only once for potential updates within a request. For sites where templates are not expected to reflect updates within the same request, this minimizes file system overhead. 
</cfsavecontent>
<cfsavecontent variable="stText.setting.inspectTemplateAlwaysDesc">
When checked, any requested files found to currently reside in the template cache will always be inspected for potential updates. For sites where templates are updated during the life of the server or within request.
</cfsavecontent>

<cfset stText.setting.inspectTemplateOnce="Once ( Good )">
<cfset stText.setting.inspectTemplateNever="Never ( Best Performance )">
<cfset stText.setting.inspectTemplateAlways="Always ( Bad )">



<cfset stText.setting.templateCache="Template Cache">
<cfset stText.setting.templateCacheClear="Clear template cache">
<cfset stText.setting.templateCacheClearCount="Clear template cache ( {count} element(s) )">
<cfset stText.setting.templateCacheClearDesc="Press the button above to clear the template cache.">

<cfset stText.setting.queryCache="Query Cache">
<cfset stText.setting.queryCacheClear="Clear query cache">
<cfset stText.setting.queryCacheClearCount="Clear query cache ( {count} element(s) )">
<cfset stText.setting.queryCacheClearDesc="Press the button above to clear the query cache.">

<cfset stText.setting.componentCache="Component path Cache">
<cfset stText.setting.componentCacheClear="Clear component path cache ( {count} element(s) )">
<cfset stText.setting.componentCacheClearDesc="Press the button above to clear the component path cache.">

<cfset stText.setting.ctCache="Custom tag path Cache">
<cfset stText.setting.ctCacheClear="Clear custom tag path cache ( {count} element(s) )">
<cfset stText.setting.ctCacheClearDesc="Press the button above to clear the custom tag path cache.">
