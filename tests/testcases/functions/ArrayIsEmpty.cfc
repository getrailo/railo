<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testArrayIsEmpty">
<cfset arr=arrayNew(1)>
<cfset valueEquals(left="#arrayisEmpty(arr)#", right="true")>
<cfset ArrayAppend( arr, 1 )>
<cfset valueEquals(left="#arrayisEmpty(arr)#", right="false")>
<cfset ArrayClear( arr )>
<cfset valueEquals(left="#arrayisEmpty(arr)#", right="true")>

		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>