<cfcomponent implements="AInterface" extends="AComponent">

<cffunction name="TestPublic" access="public" returntype="void" output="false">
	<cfargument name="a" default="abc" hint="info" required="true">
</cffunction>

<cffunction name="TestPackage" access="package" returntype="void" output="false">
</cffunction>

<cffunction name="TestPrivate" access="private" returntype="void" output="false">
</cffunction>


</cfcomponent>