<cffunction name="trace" output="no">
	<cfargument name="var" type="string" required="no">
	<cfargument name="text" type="string" required="no">
	<cfargument name="type" type="string" required="no" default="Information">
	<cfargument name="category" type="string" required="no">
	<cfargument name="inline" type="boolean" required="no" default="#false#">
	<cfargument name="abort" type="boolean" required="no" default="#false#">
    <cftrace attributeCollection="#arguments#">
</cffunction>