<cffunction name="ThreadTerminate" output="no">
	<cfargument name="name" type="string" required="yes">
    
    <cfthread action="terminate" name="#arguments.name#"/>
</cffunction>