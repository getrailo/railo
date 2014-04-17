<cfcomponent output="false">
	<cfproperty name="ItemKey" type="string" />

	<cffunction name="init">
		<cfargument name="itemkey">
		<cfset variables.itemkey=arguments.itemkey>
	</cffunction>
</cfcomponent>