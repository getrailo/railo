<cfcomponent>

<cffunction name="corrupt">
  
  <!--- this will corrupted the init'd foobar variable value --->
  <cfset createObject( "component", "second" ) />

</cffunction>

</cfcomponent>