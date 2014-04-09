<cfcomponent>
<cffunction name="getJson" access="remote" returntype="any" returnformat="json">
<cfset test=structnew()>
<cfset test.a=chr(228)>
<cfset test.u=chr(252)>
<cfset test.o=chr(246)>
<cfset test.s=chr(223)>
<cfreturn test>
</cffunction>
</cfcomponent>