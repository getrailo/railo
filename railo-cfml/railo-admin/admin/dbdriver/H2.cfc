<cfcomponent extends="Driver" implements="IDriver">
	<cfset fields=array(
		field("Path","path","",true,"Path where the database is or should be located (only Filesystem, virtual Resources like ""ram"" not supported)"),
		field("Protocol", "protocol", "File,Mem,SSL,TCP,ZIP", true, "Use File, Mem, or ZIP for embedded mode, and TCP or SSL for server mode.", "radio"),
		field("Additional Paramters", "additional", "", false, "Any additional connection string parameters. Separate multiple values with a semi-colon, e.g. MODE=MSSQLServer;IGNORECASE=TRUE")
	)>
	
	<cfset this.dsn="jdbc:h2:{protocol}{host}{path}{database}{additional}">
	<cfset this.className="org.h2.Driver">

	<cfset SLASH=struct(
		'/':'\',
		'\':'/'
	)>
	
	
	<cffunction name="onBeforeUpdate" returntype="void" output="no">
		
		<cfif not directoryExists(form.custom_path)>
			<cfset var parent=mid(form.custom_path,1,len(form.custom_path)-1)>
			<cfset parent=getDirectoryFromPath(parent)>
			<cfif directoryExists(parent)>
				<cfdirectory directory="#form.custom_path#" action="create" mode="777">
			<cfelse>
				<cfthrow message="directory [#form.custom_path#] doesn't exist">
			</cfif>
		</cfif>
		
		<cfset form.custom_protocol = lcase( form.custom_protocol )>
		<cfif ( form.custom_protocol == "tcp" ) || ( form.custom_protocol == "ssl" )>
		
			<cfset form.custom_protocol &= "://">
			
			<cfif right( form.host, 1 ) != "/">

				<cfset form.host &= "/">
			</cfif>
		<cfelse>
		
			<cfset form.custom_protocol &= ":">
			<cfset form.host = "">
		</cfif>
		
		<cfif len( form.custom_additional ) && left( form.custom_additional, 1 ) != ';'>
		
			<cfset form.custom_additional = ';' & form.custom_additional>
		</cfif>
	</cffunction>
	
	<cffunction name="getName" returntype="string"  output="no"
		hint="returns display name of the driver">
		<cfreturn "H2 Database Engine">
	</cffunction>
	
	<cffunction name="getDescription" returntype="string"  output="no"
		hint="returns description for the driver">
		<cfreturn "Here you can not only create a database connection to a existing H2 Database, you can also create a new one.
		That means, when a Database you define not exist, it will be automaticlly created, but for that you must use username ""sa"" and password """" (empty string).">
	</cffunction>
	
	<cffunction name="getFields" returntype="array"  output="no"
		hint="returns array of fields">
		<cfreturn fields>
	</cffunction>
	
	<cffunction name="getClass" returntype="string"  output="no"
		hint="return driver Java Class">
		<cfreturn this.className>
	</cffunction>
	
	<cffunction name="getDSN" returntype="string" hint="return DSN" output="no">
		<cfreturn this.dsn>
	</cffunction>
	
	<cffunction name="equals" returntype="string"  output="no"
		hint="return if String class match this">
		<cfargument name="className" required="true">
		<cfargument name="dsn" required="true">
		<cfreturn this.className EQ arguments.className and this.dsn EQ arguments.dsn>
	</cffunction>
	
</cfcomponent>