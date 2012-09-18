<cfcomponent extends="types.Driver" implements="types.IDatasource">
	
	<cfset this.type.port=this.TYPE_FREE>
	
	
	<cfset this.className="net.sourceforge.jtds.jdbc.Driver">
	<cfset this.value.port=7100>
	<cfset this.value.host="localhost">
	<cfset this.dsn="jdbc:jtds:sybase://{host}:{port}/{database}">
		
	
	<cfset fields=array(
		field("Charset","charset","UTF-8",false,"(default - the character set the server was installed with) Very important setting, determines the byte value to character mapping for CHAR/VARCHAR/TEXT values. Applies for characters from the extended set (codes 128-255). For NCHAR/NVARCHAR/NTEXT values doesn't have any effect since these are stored using Unicode.")
	)>
	<cfset data=struct()>
	
	
	<cffunction name="getName" returntype="string" output="no"
		hint="returns display name of the driver">
		<cfreturn "Sybase">
	</cffunction>
	
	<cffunction name="getDescription" returntype="string" output="no"
		hint="returns description for the driver">
		<cfreturn "Sybase Driver">
	</cffunction>
	
	<cffunction name="getFields" returntype="array" output="no"
		hint="returns array of fields">
		<cfreturn fields>
	</cffunction>
	
	<cffunction name="setFormData" returntype="void" output="no"
		hint="returns the form data">
		<cfset data=duplicate(form)>
	</cffunction>

	
	<cffunction name="equals" returntype="boolean" output="false"
		hint="return if String class match this">
		
		<cfargument name="className"	required="true">
		<cfargument name="dsn"			required="true">
		
		<cfreturn this.className EQ arguments.className and findNoCase("sybase",arguments.dsn)>
	</cffunction>
	
</cfcomponent>