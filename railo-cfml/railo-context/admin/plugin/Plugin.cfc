<cfcomponent hint="Plugin">
	<cffunction name="load" output="no" returntype="any"
		hint="load persitent data from admin">
		<cfset var data=struct()>
		<cftry>
			<cfadmin 
				action="storageGet"
				type="#request.adminType#"
				password="#session["password"&request.adminType]#"
				key="#url.plugin#"
				returnVariable="data">
			<cfcatch>
				<cfset data="">
			</cfcatch>
		</cftry>
		<cfreturn data>
	</cffunction>
	
	<cffunction name="save" returntype="void"
		hint="save persitent data from admin">
		<cfargument name="data" type="any">
		<cfadmin 
			action="storageSet"
			type="#request.adminType#"
			password="#session["password"&request.adminType]#"
			key="#url.plugin#"
			value="#data#">
	</cffunction>
	
	<cffunction name="init"
		hint="this function will be called to initalize">
		<cfargument name="lang" type="struct">
		<cfargument name="app" type="struct">
	</cffunction>
	
	<cffunction name="overview" output="no"
		hint="this is the main display action">
		<cfargument name="lang" type="struct">
		<cfargument name="app" type="struct">
		<cfargument name="req" type="struct">
	</cffunction>
	
	
<cffunction name="action">
	<cfargument name="action" type="string" required="yes">
	<cfargument name="qs" type="string" required="no" default="">
	
	
	<cfreturn request.self&"?action="&url.action&"&plugin="&url.plugin&"&pluginAction="&arguments.action&"&"&arguments.qs>
</cffunction>
	
	<cffunction name="_action">
		<cfargument name="action" type="string">
		<cfargument name="lang" type="struct">
		<cfargument name="app" type="struct">
		<cfargument name="req" type="struct">
		<cfset var lang=lang>
		<cfset var app=app>
		<cfset var req=req>
		<cfreturn this[arguments.action](arguments.lang,arguments.app,arguments.req)>
	</cffunction>
	
	<cffunction name="_display">
		<cfargument name="template" type="string">
		<cfargument name="lang" type="struct">
		<cfargument name="app" type="struct">
		<cfargument name="req" type="struct">
		<cfinclude template="#arguments.template#">
	</cffunction>
	
</cfcomponent>