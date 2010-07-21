<cffunction name="threadJoin" output="no">
	<cfargument name="name" type="string" required="yes">
	<cfargument name="timeout" type="numeric" required="no" default="#0#">
    
    <cfthread action="join" name="#arguments.name#" timeout="#arguments.timeout#"/>
</cffunction>