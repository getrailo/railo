<cfcomponent>
	
	<cffunction name="invoke" access="public" output="no" returntype="void">
    	<cfargument name="data" type="struct" required="yes">
		<cflog text="#serialize(data)#" type="information" file="MailWatcher">
	</cffunction>

</cfcomponent>