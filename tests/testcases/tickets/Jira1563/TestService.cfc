<cfcomponent output="false">
	<cffunction name="returnVersion" output="false" access="remote" returntype="Version">
		<cfargument name="version" type="Version" />
		<cfreturn arguments.version />
	</cffunction>
</cfcomponent>