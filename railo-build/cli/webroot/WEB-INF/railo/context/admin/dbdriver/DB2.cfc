<cfcomponent extends="Driver" implements="IDriver">
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
	
	<cffunction name="getClass" returntype="string"  output="no"
		hint="return driver Java Class">
		<cfreturn this.className>
	</cffunction>
	
	<cffunction name="getDSN" returntype="string"  output="no"
		hint="return DSN">
		<cfreturn this.dsn>
	</cffunction>
	
	<cffunction name="equals" returntype="string"  output="no"
		hint="return if String class match this">
		<cfargument name="className" required="true">
		<cfargument name="dsn" required="true">
		<cfreturn this.className EQ arguments.className and this.dsn EQ arguments.dsn>
	</cffunction>
	
</cfcomponent>