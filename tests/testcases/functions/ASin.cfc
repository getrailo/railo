<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testASin" localMode="modern">

<!--- begin old test code --->
<cfset valueEquals(left="#tostring(asin(0.3))#", right="0.304692654015")>
<cftry>
	<cfset valueEquals(left="#tostring(asin(1.3))#", right="0")>
	<cfset fail("must throw:1.3 must be within range: ( -1 : 1 )  ")>
	<cfcatch></cfcatch>
</cftry>

<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>