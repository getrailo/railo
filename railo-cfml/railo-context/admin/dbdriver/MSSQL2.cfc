<cfcomponent extends="types.Driver" output="false" implements="types.IDatasource">
	<cfset this.className="net.sourceforge.jtds.jdbc.Driver">
	<cfset this.dsn="jdbc:jtds:sqlserver://{host}:{port}/{database}">
		
	<cfset this.type.port=this.TYPE_FREE>
	<cfset this.value.host="localhost">
	<cfset this.value.port=1433>
	
	
	<cfset fields=array(
		field("Charset","charset","",false,"(default - the character set the server was installed with) Very important setting, determines the byte value to character mapping for CHAR/VARCHAR/TEXT values. Applies for characters from the extended set (codes 128-255). For NCHAR/NVARCHAR/NTEXT values doesn't have any effect since these are stored using Unicode.")
	)>
	<cfset data=struct()>
	
    
	<cffunction name="onBeforeUpdate" returntype="void" output="no">
		<cfif StructKeyExists(form,'custom_charset') and len(form.custom_charset) EQ 0>
        	<cfset StructDelete(form,'custom_charset',false)>
        </cfif>
	</cffunction>
    
	<cffunction name="getFields" returntype="array" output="no"
		hint="returns array of fields">
		<cfreturn fields>
	</cffunction>
	
	<cffunction name="getName" returntype="string" output="no"
		hint="returns display name of the driver">
		<cfreturn "MSSQL - Microsoft SQL Server (Vendor jTDS)">
	</cffunction>
	
	<cffunction name="getDescription" returntype="string" output="no"
		hint="returns description for the driver">
		<cfreturn "Open Source Microsoft SQL Server Driver from jTDS.  see http://jtds.sourceforge.net/ for details.">
	</cffunction>

	<cffunction name="equals" returntype="boolean" output="false"
		hint="return if String class match this">
		
		<cfargument name="className"	required="true">
		<cfargument name="dsn"			required="true">
		
		<cfreturn this.className EQ arguments.className and findNoCase("sqlserver",arguments.dsn)>
	</cffunction>
	
</cfcomponent>