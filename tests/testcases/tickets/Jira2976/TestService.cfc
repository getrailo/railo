
<cfcomponent output="false">
	<cffunction name="echoVersion" output="false" access="remote" returntype="Version">
		<cfargument name="version" type="Version" />
		<cfreturn arguments.version />
	</cffunction>

	<cffunction name="returnVersion" output="false" access="remote" returntype="Version">
		<cfargument name="version" type="Version" />
		<cfset local.result = createObject("component","Version") />


		<cfset local.result.application = arguments.version.application />
		<cfset local.result.build = arguments.version.build />
		<cfset local.result.builddate = arguments.version.builddate />
		<cfset local.result.version = arguments.version.version />
		<cfreturn local.result />
	</cffunction>
</cfcomponent>