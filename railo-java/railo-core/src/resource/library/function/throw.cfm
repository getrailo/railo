<cffunction name="throw" output="no">
	<cfargument name="message" type="string" required="no">
	<cfargument name="type" type="string" required="no" default="application">
	<cfargument name="detail" type="string" required="no">
	<cfargument name="errorcode" type="string" required="no">
	<cfargument name="extendedInfo" type="string" required="no">
	<cfargument name="object" type="any" required="no">
    <cfthrow attributeCollection="#arguments#">
</cffunction>