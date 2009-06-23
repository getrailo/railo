<cfset stText.Security.Title                       = "Default access rights">
<cfset stText.Security.TitleDescription            = "Here you can define the access rights that will be used as a default for all web contexts. If allowed, you can overwrite some of the settings in the corresponding ""web administrator"".">
<cfset stText.Security.WebAdministrator            = "Web administrator">
<cfset stText.Security.WebAdministratorDescription = "Here you can define the access rights for the settings that can be overridden in the ""web administrator"".">
<cfset stText.Security.Settings                    = "Settings (regional, component, scope)">
<cfset stText.Security.SettingsDescription         = "The settings (regional,component and scope) can be changed in the ""web administrator""">
<cfset stText.Security.Mail                        = "Mail">
<cfset stText.Security.MailDescription             = "The mail settings can be changed in the ""web administrator""">
<cfset stText.Security.Datasource                  = "Datasource">
<cfset stText.Security.DatasourceDescription       = "Defines how many datasources can be added in the ""web administrator"".">
<cfset stText.Security.DatasourceTextes			   =struct('-1':"unlimited")>

<cfset stText.Security.Mapping                     = "Mapping">
<cfset stText.Security.MappingDescription          = "Allows adding, removing and updating of mappings in the ""web administrator"".">


<cfset stText.Security.Remote                     = "Remote">
<cfset stText.Security.RemoteDescription          = "It allows the settings in the administrator to be synchronized with other Railo contexts">
<cfset stText.Security.CustomTag                   = "Custom Tag">
<cfset stText.Security.CustomTagDescription        = "The custom tag settings can be changed in the ""web administrator""">
<cfset stText.Security.CFX                         = "CFX">
<cfset stText.Security.CFXDescription              = "The settings for the cfx tags can be changed. The globally defined CFX tags defined in the ""server administrator"" can be used as well.">
<cfset stText.Security.Debugging                   = "Debugging">
<cfset stText.Security.DebuggingDescription        = "The debugging settings can be changed in the ""web administrator""">
<cfset stText.Security.Search                      = "Search">
<cfset stText.Security.SearchDescription           = "The search settings can be changed in the ""web administrator""">
<cfset stText.Security.ScheduledTask               = "Scheduled task">
<cfset stText.Security.ScheduledTaskDescription    = "The scheduled task settings can be changed in the ""web administrator""">

<cfset stText.Security.CFMLEnvironment             = "CFML Enviroment">
<cfset stText.Security.CFMLEnvironmentDescription  = "Settings that have an effect on how Railo code interacts with the host enviroment">
<cfset stText.Security.File                        = "File access">
<cfset stText.Security.FileDescription             = "Defines how Railo can interact with the local filesystem in a web context.<br/>- none: allows no access to the filesystem at all<br/>- local: allows only access to the filesystem within the webroot<br/>- all: allows full file access on the hosts filesystem<br/>">
<cfset stText.Security.FileAll                     = "all">
<cfset stText.Security.FileLocal                   = "local">
<cfset stText.Security.FileNone                    = "none">
<cfset stText.Security.JavaAccess                  = "Direct Java access">
<cfset stText.Security.JavaAccessDescription       = "Allows access to Java methods and properties from the Railo code (Example: stringValue.substring(2,5)). Allowing access to Java methods and properties might be a potential security risk">
<cfset stText.Security.Functions                   = "Tags &amp; Functions">
<cfset stText.Security.FunctionsDescription        = "Tags and Functions that might be a potential risk to the hosts system">
<cfset stText.Security.TagExecute                  = "Tag CFExecute">
<cfset stText.Security.TagExecuteDescription       = "This tag is used to execute a process on the local hosts system">
<cfset stText.Security.TagImport                   = "Tag CFImport">
<cfset stText.Security.TagImportDescription        = "This tag can be used to import JSP and Railo tag libraries">
<cfset stText.Security.TagObject                   = "Tag CFObject / <br/>function CreateObject">
<cfset stText.Security.TagObjectDescription        = "With the tag CFObject and the function CreateObject you can load Java objects. If disabled, you only can create objects of type ""component""">
<cfset stText.Security.TagRegistry                 = "Tag CFRegistry">
<cfset stText.Security.TagRegistryDescription      = "With the tag CFRegistry you have full access to the registry of the local hosts system">
<cfset stText.Security.CFXTags                     = "CFX tags">
<cfset stText.Security.CFXTagsDescription          = "With CFX tags one can load Java classes which might have full access to the local hosts system. This might be a potential security risk to the hosts system">


<cfset stText.Security.desc="Define the access rights for the different web contextes (webapps). Under the ""general"" tab you define default rights for all web contexts that do not have a speficic definition in the ""individual"" tab.">
<cfset stText.Security.tabGeneral="General">
<cfset stText.Security.tabSpecial="Individual">
<cfset stText.Security.generalDesc="Define the default security settings for web contexts.">
<cfset stText.Security.specialDesc="Define the security settings for a specific web context (webapps).">
<cfset stText.Security.specListTitle="specific web context">
<cfset stText.Security.specListText="Security settings for a specific web context. You can edit or delete these contexts.">
<cfset stText.Security.specListHost="Host name">
<cfset stText.Security.specListPath="Path">
<cfset stText.Security.specListWebContext="Web context">
<cfset stText.Security.specListNewTitle="create new web context">


<cfset stText.Security.general="General Access">
<cfset stText.Security.generalDesc="Define the General Access for administrator and tag cfadmin">


<cfset stText.Security.accessWrite="Access Write">
<cfset stText.Security.accessWriteDesc="define the access for writing data">

<cfset stText.Security.accessRead="Access Read">
<cfset stText.Security.accessReadDesc="define the access for reading data">
