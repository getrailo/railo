<cffunction name="writeDump" output="yes">
	<cfargument name="var" type="object" required="yes">
	<cfargument name="expand" type="boolean" required="no">
	<cfargument name="format" type="string" required="no">
	<cfargument name="hide" type="string" required="no">
	<cfargument name="keys" type="numeric" required="no">
	<cfargument name="label" type="string" required="no">
	<cfargument name="metainfo" type="boolean" required="no">
	<cfargument name="output" type="string" required="no">
	<cfargument name="show" type="string" required="no">
	<cfargument name="showUDFs" type="boolean" required="no">
	<cfargument name="top" type="numeric" required="no">
	<cfargument name="abort" type="boolean" required="no">
    
    <cfdump attributeCollection="#arguments#">
</cffunction>