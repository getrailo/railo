<cffunction name="writeLog" output="no">
	<cfargument name="text" type="string" required="yes">
	<cfargument name="type" type="string" required="no" default="Information">
	<cfargument name="application" type="boolean" required="no">
	<cfargument name="file" type="string" required="no">
	<cfargument name="log" type="string" required="no">
    <cflog attributeCollection="#arguments#">
</cffunction>