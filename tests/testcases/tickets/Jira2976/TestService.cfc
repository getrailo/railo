
<cfcomponent output="false">
	<cffunction name="echoVersion" output="false" access="remote" returntype="Version">
		<cfargument name="version" type="Version" />
		<cfreturn arguments.version />
	</cffunction>

	<cffunction name="returnVersion" output="false" access="remote" returntype="Version">
		<cfargument name="version" type="Version" />
		<cfset local.result = createObject("component","Version") />
		<cfset local.version.application = arguments.version.application />
		<cfset local.version.build = arguments.version.build />
		<cfset local.version.builddate = arguments.version.builddate />
		<cfset local.version.version = arguments.version.version />
		<cfreturn local.version />
	</cffunction>
</cfcomponent>