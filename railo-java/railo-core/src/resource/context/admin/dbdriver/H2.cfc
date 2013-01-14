<cfcomponent extends="types.Driver" implements="types.IDatasource">
	<cfset fields=array(
		field("path","path","",true,"Path where the database is or should be located (only Filesystem, virtual Resources like ""ram"" not supported)"),
		field("mode","mode","MySQL,PostgreSQL,HSQLDB",true,"All database engines behave a little bit different. For certain features, this database can emulate the behavior of specific databases. Not all features or differences of those databases are implemented. Currently, this feature is mainly used for randomized comparative testing","radio")
	)>
	<cfset this.type.host=this.TYPE_HIDDEN>
	
	<cfset this.dsn="jdbc:h2:{path}{database};MODE={mode}">
	<cfset this.className="org.h2.Driver">

	<cfset SLASH=struct(
		'/':'\',
		'\':'/'
	)>
	
	
	<cffunction name="onBeforeUpdate" returntype="void" output="no">
		<!--- add the right file delimiter --->
		<cfset form.custom_path=replace(
						form.custom_path,
						SLASH[server.separator.file],
						server.separator.file,
						'all')>
		<cfif right(form.custom_path,1) NEQ server.separator.file>
			<cfset form.custom_path=form.custom_path&server.separator.file>
		</cfif>
		
		<!--- make sure relative path and path with placeholder are working --->
		<cfif not directoryExists(form.custom_path)>
			<cfset local._custom_path=expandPath(form.custom_path)>
			<cfif directoryExists(local._custom_path)>
				<cfset form.custom_path=local._custom_path>
			</cfif>
		</cfif>
		
		<!--- if parent exist, create it --->
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
		<cfreturn "H2 Database Engine in Embedded Mode">
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
	
</cfcomponent>