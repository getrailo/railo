<cfcomponent output="false">
	<cfproperty name="itemkey" type="string" />

	<cffunction name="init">
		<cfargument name="itemkey">
		<cfset variables.itemkey=arguments.itemkey>
	</cffunction>
</cfcomponent>