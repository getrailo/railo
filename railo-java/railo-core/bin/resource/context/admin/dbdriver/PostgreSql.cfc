<cfcomponent extends="Driver" implements="IDriver">
	<cfset this.dsn="jdbc:postgresql://{host}:{port}/{database}">
	<cfset this.className="org.postgresql.Driver">
	
	<cfset this.type.port=this.TYPE_FREE>
	<cfset this.value.host="localhost">
	<cfset this.value.port=5432>
	<cfset fields=array()>
	
	<cffunction name="getName" returntype="string" output="no"
		hint="returns display name of the driver">
		<cfreturn "PostgreSQL">
	</cffunction>
	
	<cffunction name="getDescription" returntype="string" output="no"
		hint="returns description for the driver">
		<cfreturn "PostgreSQL JDBC Driver">
	</cffunction>
	
	<cffunction name="getFields" returntype="array" output="no"
		hint="returns array of fields">
		<cfreturn fields>
	</cffunction>
	
	<cffunction name="getClass" returntype="string" output="no"
		hint="return driver Java Class">
		<cfreturn this.className>
	</cffunction>
	
	<cffunction name="getDSN" returntype="string" output="no"
		hint="return DSN">
		<cfreturn this.dsn>
	</cffunction>
	
	<cffunction name="getUsername" returntype="string" output="no"
		hint="return Username">
		<cfreturn data.username>
	</cffunction>
	
	<cffunction name="getPassword" returntype="string" output="no"
		hint="return Password">
		<cfreturn data.password>
	</cffunction>
	
	<cffunction name="equals" returntype="string" output="no"
		hint="return if String class match this">
		<cfargument name="className" required="true">
		<cfargument name="dsn" required="true">
		<cfreturn this.className EQ arguments.className and this.dsn EQ arguments.dsn>
	</cffunction>
	
</cfcomponent>