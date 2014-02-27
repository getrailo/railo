<cfcomponent output="false">
	<cfproperty name="name" type="string" />

	<cffunction name="init">
		<cfargument name="name">
		<cfset variables.name=arguments.name>
	</cffunction>
</cfcomponent>