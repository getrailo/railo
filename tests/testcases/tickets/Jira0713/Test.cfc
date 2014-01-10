<cfcomponent accessors="true" persistent="true" >

<cfproperty name="name" />

<cffunction name="init">

	<cfset variables.name = "Brett" />
		
	<cfreturn this />

</cffunction>

</cfcomponent>