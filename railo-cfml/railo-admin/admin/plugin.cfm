<!--- 
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 --->
<cfparam name="application.plugin" default="#struct()#">
<cfparam name="application.pluginLanguage.de" default="#struct()#">
<cfparam name="application.pluginLanguage.en" default="#struct()#">
<cfparam name="url.pluginAction" default="overview">
<cfif not structKeyExists(url,"plugin")>
	<cflocation url="#request.self#" addtoken="no">
</cfif>

<!--- load plugin --->
<cfif not structKeyExists(application.plugin,url.plugin)>
	<cfset application.plugin[url.plugin].application=struct()>
</cfif>
<cfif not structKeyExists(application.plugin[url.plugin],'component') or session.alwaysNew>
	<cfset application.plugin[url.plugin].component=createObject('component','railo_plugin_directory.'&url.plugin&'.Action')>
	<cfset application.plugin[url.plugin].component.init(
		application.pluginLanguage[session.railo_admin_lang][url.plugin],
		application.plugin[url.plugin].application)>
</cfif>
<cfset plugin=application.plugin[url.plugin]>

<cfset plugin.language=application.pluginLanguage[session.railo_admin_lang][url.plugin]>

<cfoutput><cfif not request.disableFrame and structKeyExists(plugin.language,'text') and len(trim(plugin.language.text))>#plugin.language.text#<br /><br /></cfif></cfoutput>

<!--- create scopes --->
<cfset req=duplicate(url)>
<cfset _form=duplicate(form)>
<cfif structKeyExists(_form,'fieldnames')>
	<cfset structDelete(_form,'fieldnames')>
</cfif>
<cfloop collection="#_form#" item="key">
	<cfset req[key]=_form[key]>
</cfloop>
<cfset app=plugin.application>
<cfset lang=plugin.language>

<!---cfset plugin.component._action(plugin:plugin,lang:lang,app:app,req:req)--->

<!--- first call the action if exists --->
<cfset hasAction=structKeyExists(plugin.component,url.pluginAction)>

<cfif hasAction>
	<cfset rtnAction= plugin.component._action(url.pluginAction,lang,app,req)>
    
	<!--- cfset rtnAction= plugin.component[url.pluginAction](lang,app,req)--->
</cfif>
<cfif not isDefined('rtnAction')>
	<cfset rtnAction=url.pluginAction>
</cfif>

<!--- redirect --->
<cfif findNoCase('redirect:',rtnAction) EQ 1>
	<cflocation url="#plugin.component.action(mid(rtnAction,10,len(rtnAction)))#" addtoken="no">
</cfif>

<!--- then call display --->
<cfset dspFile="/railo_plugin_directory/#url.plugin#/#rtnAction#.cfm">

<cfset hasDisplay=fileExists(expandPath(dspFile))>
<cfif rtnAction NEQ "_none" and hasDisplay>
	<cftry>
		<cfset rtnAction= plugin.component._display(dspFile,lang,app,req)>
		<cfcatch>
			<cfset printError(cfcatch,!findNoCase("trial",cfcatch.message))>
		</cfcatch>
	</cftry>
</cfif>

<cfif not hasAction and not hasDisplay>
<cfset printError(struct(message:"there is no action [#url.pluginAction#] or diplay handler [#expandPath(dspFile)#] defined for "&url.plugin,detail:''))>
</cfif>
