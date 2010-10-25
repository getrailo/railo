<cffunction name="ThreadTerminate" output="no" returntype="void">
	<cfargument name="name" type="string" required="yes">
    
    <cfthread action="terminate" name="#arguments.name#"/>
</cffunction>