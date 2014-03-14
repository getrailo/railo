<cfcomponent extends="types.Driver" implements="types.IDatasource">
	<cfset fields=array(
		field("Class","class","",true,"The class that implement the Driver"),
		field("Connection String","dsn","",true,"The Datasource Connection String")
	)>
	
	<cfset this.className="">
	<cfset this.type.port=this.TYPE_HIDDEN>
	<cfset this.type.host=this.TYPE_HIDDEN>
	<cfset this.type.database=this.TYPE_HIDDEN>
	<cfset this.data.classname="">
	<cfset this.data.dsn="ddd">
	
	<cffunction name="onBeforeUpdate" returntype="void" output="no">
		<cfset this.class=form.custom_class>
		<cfset StructDelete(form,'custom_class')>
		<cfset this.dsn=form.custom_dsn>
		<cfset StructDelete(form,'custom_dsn')>
	</cffunction>

	<cffunction name="init" returntype="void" output="no">
		<cfargument name="data" required="yes" type="struct">
		<cfif not structKeyExists(data,"classname")>
			<cfset data.classname="">
		</cfif>
		<cfif not structKeyExists(data,"dsn")>
			<cfset data.dsn="">
		</cfif>
		<cfset fields=array(
			field("Class","class",data.classname,true,"The class that implement the Driver"),
			field("Connection String","dsn",data.dsn,true,"The Datasource Connection String")
		)>
		
	</cffunction>
		
	<cffunction name="getName" returntype="string" output="no"
		hint="returns display name of the driver">
		<cfreturn "Other - JDBC Driver">
	</cffunction>
	
	<cffunction name="getDescription" returntype="string" output="no"
		hint="returns description for the driver">
		<cfreturn "Connect with a user supplied JDBC Driver available on the system.">
	</cffunction>
	
	<cffunction name="getFields" returntype="array" output="no"
		hint="returns array of fields">
		<cfreturn fields>
	</cffunction>
	
	
	<cffunction name="equals" returntype="boolean" output="false"
		hint="always returns false">
		
		<cfargument name="className"	required="true">
		<cfargument name="dsn"			required="true">
		
		<cfreturn false>
	</cffunction>
	
</cfcomponent>