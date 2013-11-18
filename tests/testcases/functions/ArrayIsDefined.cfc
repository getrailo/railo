<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testArrayIsDefined">
		<cfset server.enable21=1>
		<cfset var arr = arrayNew(1)>
		<cfset arr[2] = 5>
		<cfset arr[4] = 6>
		
		<cfset valueEquals(left="#arrayIsDefined(arr,0)#", right="#false#")>
		<cfset valueEquals(left="#arrayIsDefined(arr,1)#", right="#false#")>
		<cfset valueEquals(left="#arrayIsDefined(arr,2)#", right="#true#")>
		<cfset valueEquals(left="#arrayIsDefined(arr,3)#", right="#false#")>
		<cfset valueEquals(left="#arrayIsDefined(arr,4)#", right="#true#")>
		<cfset valueEquals(left="#arrayIsDefined(arr,5)#", right="#false#")>

		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>