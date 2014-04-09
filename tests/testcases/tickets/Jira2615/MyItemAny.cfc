<cfcomponent output="false">
	<cfproperty name="itemkey" type="string" />
	<cfproperty name="subi" type="MyItemAnySub" />

	<cffunction name="init">
		<cfargument name="itemkey">
		<cfset variables.itemkey=arguments.itemkey>
		<cfset variables.subi=new MyItemAnySub(arguments.itemkey)>
	</cffunction>
</cfcomponent>