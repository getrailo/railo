<cfcomponent extends="types.Driver" implements="types.IDatasource">
	<cfset fields=array()>
	
	<cfset this.type.port=this.TYPE_FREE>
	
	<cfset this.value.host="localhost">
	<cfset this.value.port=50000>
	<cfset this.className="com.ddtek.jdbc.db2.DB2Driver">
	<cfset this.dsn="jdbc:datadirect:db2://{host}:{port};DatabaseName={database}">
	<cffunction name="getName" returntype="string" output="no"
		hint="returns display name of the driver">
		<cfreturn "DB2">
	</cffunction>
	
	<cffunction name="getDescription" returntype="string"  output="no"
		hint="returns description for the driver">
		<cfreturn "For DB2 Databases">
	</cffunction>
	
	<cffunction name="getFields" returntype="array"  output="no"
		hint="returns array of fields">
		<cfreturn fields>
	</cffunction>
	
	
</cfcomponent>