<cfcomponent extends="types.Driver" output="no" implements="types.IDatasource">
	
	<cfset fields=array(
		field("Use Unicode","useUnicode","true,false",true,"Should the driver use Unicode character encodings when handling strings? Should only be used when the driver can't determine the character set mapping, or you are trying to 'force' the driver to use a character set that MySQL either doesn't natively support (such as UTF-8)","radio"),
		field("Charset","characterEncoding","UTF-8",false,"If 'Use Unicode' is set to true, what character encoding should the driver use when dealing with strings?"),
		//field("cache ResultSetMetadata","cacheResultSetMetadata","true,false",false,"Should the driver cache ResultSetMetaData for Statements and PreparedStatements.","radio"),
		
		field("Alias Handling","useOldAliasMetadataBehavior","true,false",false,"Should the driver use the legacy behavior for ""AS"" clauses on columns and tables, 
		and only return aliases (if any) rather than the original column/table name? In 5.0.x, the default value was true.","radio"),
		//,field("Allow Multiple Queries","allowMultiQueries","true,false",false,"Allow the use of "";"" to delimit multiple queries during one statement (true/false), defaults to ""false""","radio")
		
		
		field('Allow multiple Queries','allowMultiQueries','true,false',false,'Allow the use of ";" to delimit multiple queries during one statement',"radio"),
		
		field('Zero DateTime behavior','zeroDateTimeBehavior','exception,round,convertToNull',false,'What should happen when the driver encounters DATETIME values that are composed entirely of zeroes (used by MySQL to represent invalid dates)? Valid values are "exception", "round" and "convertToNull"',"radio"),
		
		field('Auto reconnect','autoReconnect','true,false',false,'Should the driver try to re-establish stale and/or dead connections? If enabled the driver will throw an exception for a queries issued on a stale or dead connection, which belong to the current transaction, but will attempt reconnect before the next query issued on the connection in a new transaction. The use of this feature is not recommended, because it has side effects related to session state and data consistency when applications do not handle SQLExceptions properly, and is only designed to be used when you are unable to configure your application to handle SQLExceptions resulting from dead and stale connections properly. Alternatively, investigate setting the MySQL server variable "wait_timeout" to some high value rather than the default of 8 hours.',"radio"),
		
		 field('Throw error upon data truncation','jdbcCompliantTruncation','true,false',false,'If set to false then values for table fields are automatically truncated so that they fit into the field.',"radio"),
		 field('TinyInt(1) is bit','tinyInt1isBit','true,false',false,'this defines the data type returned for tinyInt(1), if set to "true" (default) tinyInt(1) is converted to a bit value otherwise as integer.',"radio"),
		 field('Legacy Datetime Code','useLegacyDatetimeCode','true,false',true,
		 	'Use code for DATE/TIME/DATETIME/TIMESTAMP handling in result sets and statements that consistently handles timezone conversions from client to server and back again, or use the legacy code for these datatypes that has been in the driver for backwards-compatibility?'
		 		,"radio",1)
		 //field('Transformed Bit Is Boolean','transformedBitIsBoolean','true,false',false,'',"radio")
		
		
		
	)>
	
	
	
	
	<cfset this.type.port=this.TYPE_FREE>
	
	<cfset this.value.host="localhost">
	<cfset this.value.port=3306>
	<cfset this.className="org.gjt.mm.mysql.Driver">
	<cfset this.dsn="jdbc:mysql://{host}:{port}/{database}">
    
    
	<cffunction name="onBeforeUpdate" returntype="void" output="no">
		<cfset custom_useUnicode=true>
	</cffunction>
    
    
	<cffunction name="getName" returntype="string" output="no"
		hint="returns display name of the driver">
		<cfreturn "MySQL">
	</cffunction>
	
	<cffunction name="getDescription" returntype="string" output="no"
		hint="returns description for the driver">
		<cfreturn "For MYSQL Databases">
	</cffunction>
	
	<cffunction name="getFields" returntype="array" output="no"
		hint="returns array of fields">
		<cfreturn fields>
	</cffunction>

	
</cfcomponent>