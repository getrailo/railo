<!--- Components --->
<cfset stText.Components.Component                        = "Component">
<cfset stText.Components.Server                           = "Define the component settings that will be used as a <strong>default</strong> for all webs.">
<cfset stText.Components.Web                              = "Defines how components will be handled by Railo.">

<cfset stText.Components.BaseComponent                    = "Base/Root Component">
<cfset stText.Components.BaseComponentDescription         = "Every component that does not explicitly extend another component (attribute ""extends"") will by default extend this component. This means that every component extends this base component in some way.">
<cfset stText.Components.BaseComponentMissing             = "Please enter a value for the base/root component">

<cfset stText.Components.AutoImport                    = "Auto Import">
<cfset stText.Components.AutoImportDescription         = "the following package defintion is imported into every template.">
<cfset stText.Components.AutoImportMissing             = "Please enter a value for the auto import field">

<cfset stText.Components.ComponentDumpTemplate            = "Component ""dump"" template">
<cfset stText.Components.ComponentDumpTemplateDescription = "If you call a component directly this template will be invoked to dump the component. (Example: http://www.railo.ch/ch/railo/common/Example.cfc)">
<cfset stText.Components.ComponentDumpTemplateMissing     = "Please enter a value for the ""dump"" template">
<cfset stText.Components.DataMemberAccessType             = "Data member access type">
<cfset stText.Components.DataMemberAccessTypeDescription  = "Define the accessor for the data-members of a component. This defines how variables of the ""this"" scope of a component can be accessed from outside of the component.">
<cfset stText.Components.DMATPrivate                      = "private">
<cfset stText.Components.DMATPackage                      = "package">
<cfset stText.Components.DMATPublic                       = "public (CFML standard)">
<cfset stText.Components.DMATRemote                       = "remote">


<cfset stText.Components.Physical = "Resource">
<cfset stText.Components.Archive  = "Archive">
<cfset stText.Components.Primary  = "Primary">
<cfset stText.Components.Trusted  = "Trusted">
<cfset stText.Components.PhysicalMissing = "Please enter a value for the resource (row ">
<cfset stText.Components.ArchiveMissing  = "Please enter a value for the archive name (row ">
<cfset stText.Components.componentMappings="Addional Resources">
<cfset stText.Components.componentMappingsDesc="Addional Resources that Railo checks for Components.">


<cfset stText.Components.componentLocalSearch="Search local">
<cfset stText.Components.componentLocalSearchDesc="Search relative to the caller directory for the component">
<cfset stText.Components.componentMappingSearch="Search mappings">
<cfset stText.Components.componentMappingSearchDesc="Search the component in the mappings defined, this include the implicit mapping ""/"", that points on the webroot">

<cfset stText.Components.componentPathCache="Cache">
<cfset stText.Components.componentPathCacheDesc="component path is cached and not resolved again">