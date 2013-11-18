<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testBitMaskSet" localMode="modern">

<!--- begin old test code --->
<cfset valueEquals(left="#BitMaskSet(255, 255, 4, 4)#", right="255")>
<cfset valueEquals(left="#BitMaskSet(255, 0, 4, 4)#", right="15")>
<cfset valueEquals(left="#BitMaskSet(0, 15, 4, 4)#", right="240")>
<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>