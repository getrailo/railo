<cfcomponent implements="types.IDriverSelector">

	
	<cffunction name="getName" returntype="string"  output="no"
		hint="returns display name of the driver">

		<cfreturn "MSSQL Database Driver (selector)">
	</cffunction>

	
	<cffunction name="getDescription" returntype="string"  output="no"
		hint="returns description for the driver">

		<cfreturn "This selector allows to choose from different types of MSSQL database drivers">
	</cffunction>
	

	<cffunction name="getOptions" returntype="array" output="false" hint="returns an array of drivers to choose from. the first item in the array is selected by default.">
	
		<cfset var result = [ "MSSQL2", "MSSQL" ]>
		
		<cfreturn result>
	</cffunction>

	
</cfcomponent>