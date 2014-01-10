<cfcomponent>

<cfset Variables.Foobar = "default" />
<cffunction name="init">

  <cfset Variables.Foobar = "inited" />

</cffunction>

<cffunction name="check">

  <cfreturn Variables.Foobar />

</cffunction>

</cfcomponent>