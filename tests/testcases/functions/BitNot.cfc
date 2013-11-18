<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testBitNot" localMode="modern">

<!--- begin old test code --->
<cfset valueEquals(left="#BitNot(1)#", right="-2")>
<cfset valueEquals(left="#BitNot(0)#", right="-1")>
<cfset valueEquals(left="#BitNot(12)#", right="-13")>
<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>