<cfcomponent extends="Driver" implements="IDriver">

		
	<cffunction name="getName" returntype="string"  output="no"
		hint="returns display name of the driver">

		<cfreturn "H2 Database Driver (selector)">
	</cffunction>
	
	<cffunction name="getDescription" returntype="string"  output="no"
		hint="returns description for the driver">

		<cfreturn "This selector allows to choose from different types of connections to an H2 database.">
	</cffunction>
	
	
	<cffunction name="isDriverSelector" returntype="boolean" output="false" hint="retrun true if this is a Driver selector as in the case of H2 or MSSQL databases where there is more than one option for the driver. default is false.">
	
		<cfreturn true>
	</cffunction>

	<cffunction name="getSelectorOptions">
	
		<cfset var result = [ "H2", "H2Server" ]>
		
		<cfreturn result>
	</cffunction>

	
	<cffunction name="getFields" returntype="array"  output="no"
		hint="returns array of fields">

		<cfreturn []>
	</cffunction>
	
	<cffunction name="getClass" returntype="string"  output="no"
		hint="return driver Java Class">

		<cfreturn "[selector-type]">
	</cffunction>
	
	<cffunction name="getDSN" returntype="string" hint="return DSN" output="no">

		<cfreturn "[selector-type]">
	</cffunction>
	
	<cffunction name="equals" returntype="string"  output="no"
		hint="this is a driver selector and therefore should always return false">
		<cfargument name="className" required="true">
		<cfargument name="dsn" required="true">

		<cfreturn false>
	</cffunction>
		
</cfcomponent>