<cfcomponent extends="Driver" implements="IDriver">
	
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
	
	<cffunction name="getClass" returntype="string"output="no" 
		hint="return driver Java Class">
		<cfreturn this.className>
	</cffunction>
	
	<cffunction name="getDSN" returntype="string" output="no"
		hint="return DSN">
		<cfreturn this.dsn>
	</cffunction>
	
	<cffunction name="equals" returntype="string" output="no"
		hint="return if String class match this">
		<cfargument name="className" required="true">
		<cfargument name="dsn" required="true">
		<cfreturn this.className EQ arguments.className and this.dsn EQ arguments.dsn>
	</cffunction>
	
</cfcomponent>