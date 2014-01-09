<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testBitSHRN" localMode="modern">

<!--- begin old test code --->
<cfset valueEquals(left="#BitSHRN(1,1)#", right="0")>
<cfset valueEquals(left="#BitSHRN(1,30)#", right="0")>
<cfset valueEquals(left="#BitSHRN(1,31)#", right="0")>
<cfset valueEquals(left="#BitSHRN(2,31)#", right="0")>
<cfset valueEquals(left="#BitSHRN(128,2)#", right="32")>
<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>