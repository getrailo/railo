<cfcomponent extends="types.Driver" implements="types.IDatasource">
	
	<cfset fields=array()>
	<cfset this.type.host=this.TYPE_HIDDEN>
	<cfset this.className="sun.jdbc.odbc.JdbcOdbcDriver">
	<cfset this.dsn="jdbc:odbc:{database}">
	
	<cffunction name="getName" returntype="string" output="no"
		hint="returns display name of the driver">
		<cfreturn "JDBC-ODBC Bridge (for Access,MSSQL)">
	</cffunction>
	
	<cffunction name="getDescription" returntype="string" output="no"
		hint="returns description for the driver">
		<cfreturn "JDBC-ODBC Bridge Driver to access a ODBC Connection on windows">
	</cffunction>
	
	<cffunction name="getFields" returntype="array" output="no"
		hint="returns array of fields">
		<cfreturn fields>
	</cffunction>
	
	
</cfcomponent>