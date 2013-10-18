<cfcomponent>

<cffunction name="onMissingMethod" hint="method to handle missing methods" access="public" returntype="Any" output="true">
    <cfargument name="missingMethodName" type="string" required="true">
    <cfargument name="missingMethodArguments" type="struct" required="false" default="#StructNew()#">
    <cfinvoke method="test" argumentcollection="#arguments.missingMethodArguments#" returnvariable="res1"/>
    <cfinvoke method="test" argumentcollection="#arguments.missingMethodArguments#" b="37"  returnvariable="res2"/>
    #trim(res1)##trim(res2)#
</cffunction>

<cffunction name="test" hint="method to handle missing methods" access="public" returntype="Any" output="false">
	<cfargument name="a" type="Any" required="true" />
	<cfargument name="b" type="Any" required="true" />
    <cfset var rtn="">
    <cfset var keys=structKeyArray(arguments)>
    <cfset ArraySort(keys,'textnocase')>
    
    <cfloop array="#keys#" index="key">
    	<cfset rtn=rtn&key&":"&arguments[key]&";">
    </cfloop>
    
    
    <cfreturn rtn>
    
</cffunction>




</cfcomponent> 