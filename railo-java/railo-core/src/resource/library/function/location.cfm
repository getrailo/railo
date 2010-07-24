<cffunction name="location" output="no">
	<cfargument name="url" type="string" required="yes">
	<cfargument name="addToken" type="boolean" required="no" default="#true#">
	<cfargument name="statusCode" type="numeric" required="no" default="#302#">
	<cflocation attributeCollection="#arguments#">
</cffunction>