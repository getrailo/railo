<cfcomponent extends="types.Driver" implements="types.IDatasource">
	<cfset fields=array(
		field("Path","path","",true,"Path where the database is or should be located (only Filesystem, virtual Resources like ""ram"" not supported)")
	)>
	<cfset this.type.host=this.TYPE_HIDDEN>
	
	<cfset this.dsn="jdbc:hsqldb:file:{path}{database}">
	<cfset this.className="org.hsqldb.jdbcDriver">
	
	<cfset SLASH=struct(
		'/':'\',
		'\':'/'
	)>
	
	<cffunction name="onBeforeUpdate" returntype="void" output="no">
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
	
	<cffunction name="getName" returntype="string" output="no"
		hint="returns display name of the driver">
		<cfreturn "HSQLDB (Hypersonic SQL DB)">
	</cffunction>
	
	<cffunction name="getDescription" returntype="string" output="no"
		hint="returns description for the driver">
		<cfreturn "Hypersonic SQL DB Driver. Here you can not only create a database connection to a existing HSQL Database, you can also create a new one.
		That means, when a Database you define not exist, it will be automaticlly created, but for that you must use username ""sa"" and password """" (empty string).
		This driver only create file based Databases, if you wanna use other types you must take the ""Other"" driver.
		">
	</cffunction>
	
	<cffunction name="getFields" returntype="array" output="no"
		hint="returns array of fields">
		<cfreturn fields>
	</cffunction>
	
	
</cfcomponent>