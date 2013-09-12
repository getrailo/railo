<cfcomponent>
	
	<cffunction name="onAdd" access="public" output="no" returntype="void">
    	<cfargument name="data" type="struct" required="yes">
		<cflog text="add:#serialize(data)#" type="information" file="DirectoryWatcher">
	</cffunction>
	<cffunction name="onDelete" access="public" output="no" returntype="void">
    	<cfargument name="data" type="struct" required="yes">
		<cflog text="delete:#serialize(data)#" type="information" file="DirectoryWatcher">
	</cffunction>
	<cffunction name="onChange" access="public" output="no" returntype="void">
    	<cfargument name="data" type="struct" required="yes">
		<cflog text="change:#serialize(data)#" type="information" file="DirectoryWatcher">
	</cffunction>

</cfcomponent>