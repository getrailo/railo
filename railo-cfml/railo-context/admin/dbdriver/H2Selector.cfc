<cfcomponent implements="types.IDriverSelector">

		
	<cffunction name="getName" returntype="string"  output="no"
		hint="returns display name of the driver">

		<cfreturn "H2 Database Driver (selector)">
	</cffunction>
	
	
	<cffunction name="getDescription" returntype="string"  output="no"
		hint="returns description for the driver">

		<cfreturn "This selector allows to choose from different types of connections to an H2 database.">
	</cffunction>
	

	<cffunction name="getOptions" returntype="array" output="false">
	
		<cfset var result = [ "H2", "H2Server" ]>
		
		<cfreturn result>
	</cffunction>

		
</cfcomponent>