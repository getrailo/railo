<cfcomponent extends="types.Driver" implements="types.IDatasource">
	<cfset fields=array(
		field("Path","path","",true,"Path where the database is or should be located")
	)>
	<cfset this.type.port=this.TYPE_FREE>
	
	
	<cfset this.value.host="localhost">
	<cfset this.value.port=3050>
	
	<cfset this.dsn="jdbc:firebirdsql://{host}:{port}/{path}{database}">
	<cfset this.className="org.firebirdsql.jdbc.FBDriver">
	
	<cfset SLASH=struct(
		'/':'\',
		'\':'/'
	)>
	
	<cffunction name="onBeforeUpdate" returntype="void" output="false">
		<cfset form.custom_path=replace(
						form.custom_path,
						SLASH[server.separator.file],
						server.separator.file,
						'all')>
		<cfif right(form.custom_path,1) NEQ server.separator.file>
			<cfset form.custom_path=form.custom_path&server.separator.file>
		</cfif>
		
		
		<cfif not directoryExists(form.custom_path)>
			<cfset var parent=mid(form.custom_path,1,len(form.custom_path)-1)>
			<cfset parent=getDirectoryFromPath(parent)>
			<cfif directoryExists(parent)>
				<cfdirectory directory="#form.custom_path#" action="create" mode="777">
			<cfelse>
				<cfthrow message="directory [#form.custom_path#] doesn't exist">
			</cfif>
		</cfif>
	</cffunction>
	
	<cffunction name="getName" returntype="string"  output="no"
		hint="returns display name of the driver">
		<cfreturn "Firebird">
	</cffunction>
	
	<cffunction name="getDescription" returntype="string"  output="no"
		hint="returns description for the driver">
		<cfreturn "Firebird is a relational database offering many ANSI SQL standard features that runs on Linux, Windows, and a variety of Unix platforms. Firebird offers excellent concurrency, high performance, and powerful language support for stored procedures and triggers. It has been used in production systems, under a variety of names, since 1981.">
	</cffunction>
	
	<cffunction name="getFields" returntype="array"  output="no"
		hint="returns array of fields">
		<cfreturn fields>
	</cffunction>
	
	
</cfcomponent>