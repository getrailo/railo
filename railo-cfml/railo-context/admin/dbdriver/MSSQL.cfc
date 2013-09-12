<cfcomponent extends="types.Driver" output="false" implements="types.IDatasource">
	<cfset this.className="com.microsoft.jdbc.sqlserver.SQLServerDriver">
	<cfset this.dsn="jdbc:sqlserver://{host}:{port}">
		
	<cfset this.type.port=this.TYPE_FREE>
	<cfset this.value.host="localhost">
	<cfset this.value.port=1433>
	
	
	<cfset fields=array()>
	<cfset fields=array(
		field("Select Method","SelectMethod","direct,cursor",true,"A hint to the driver that determines whether the driver requests a database cursor for Select statements. Performance and behavior of the driver are affected by this property, which is defined as a hint because the driver may not always be able to satisfy the requested method.<ul>
	<li>Direct—When the driver uses the Direct method, the database server sends the complete result set in a single response to the driver when responding to a query.
	<li>Cursor—When the driver uses the Cursor method, a server-side cursor is requested. The rows are retrieved from the server in blocks when returning forward-only result sets.</ul>","select")
	
	,field("Send String Parameters as Unicode","sendStringParametersAsUnicode","true,false",true,"Set to ""false"" to specify that prepared parameters for character data are sent as ASCII instead of Unicode. 
	This parameter can improve performance for character data index lookup on non-Unicode, SQL Server 2000, or SQL Server 2005 tables. For example, ASCII row keys can be compared directly without the overhead of conversion from Unicode.","radio")
	
	
	
	)>
	
	
	
	<cfset data=struct()>
	
	<cffunction name="onBeforeUpdate" returntype="void" output="no">
		<cfset form.custom_DatabaseName=form.database>
	</cffunction>
	
	<cffunction name="getFields" returntype="array" output="no"
		hint="returns array of fields">
		<cfreturn fields>
	</cffunction>
	
	<cffunction name="getName" returntype="string" output="no"
		hint="returns display name of the driver">
		<cfreturn "MSSQL - Microsoft SQL Server (Vendor Microsoft)">
	</cffunction>
	
	<cffunction name="getDescription" returntype="string" output="no"
		hint="returns description for the driver">
		<cfreturn "Microsoft SQL Server Driver from Microsoft">
	</cffunction>

	<cffunction name="equals" returntype="boolean" output="false"
		hint="return if String class match this">
		
		<cfargument name="className"	required="true">
		<cfargument name="dsn"			required="true">
		
		<cfreturn this.className EQ arguments.className and findNoCase("sqlserver",arguments.dsn)>
	</cffunction>
	
</cfcomponent>