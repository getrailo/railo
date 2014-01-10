<cfcomponent hint="hintAComponent">

<cffunction name="AComponentPublic" access="public" returntype="void" output="false" hint="hintAComponentPublic">
	<cfargument name="a" default="abc" hint="info" required="true" type="string">
</cffunction>

</cfcomponent>
<!----

<cffunction name="AComponentPublic2" access="public" returntype="void" output="false" hint="hintAComponentPublic">
	<cfargument name="a" default="abc" hint="info" required="true" type="string">
</cffunction>
---->