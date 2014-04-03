<cfcomponent output="false">
	<cfproperty name="subi" type="string" />

	<cffunction name="init">
		<cfargument name="subi">
		<cfset variables.subi=arguments.subi>
	</cffunction>
</cfcomponent>