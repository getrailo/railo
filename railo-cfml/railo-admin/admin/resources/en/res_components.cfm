<!--- Components --->
<cfset stText.Components.Component                        = "Component">
<cfset stText.Components.Server                           = "Define the component settings that will be used as a <strong>default</strong> for all webs.">
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