<cfcomponent output="false">
<cffunction name="myFunction" access="remote" returntype="void" output="false" >
<cfargument name="myArray" type="MyItem[]" required="true" />
<cfreturn />
</cffunction>
<cffunction name="myFunction2" access="remote" returntype="void" output="false" >
<cfargument name="myArray" type="MyItem" required="true" />
<cfreturn />
</cffunction>
</cfcomponent>