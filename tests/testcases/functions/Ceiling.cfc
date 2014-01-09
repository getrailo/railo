<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testCeiling" localMode="modern">

<!--- begin old test code --->
<cfset valueEquals(left="#ceiling(3.4)#", right="4")>
<cfset valueEquals(left="#ceiling(3)#", right="3")>
<cfset valueEquals(left="#ceiling(3.8)#", right="4")>
<cfset valueEquals(left="#ceiling(3.4)#", right="4")>
<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>