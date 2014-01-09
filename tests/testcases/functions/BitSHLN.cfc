<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testBitSHLN" localMode="modern">

<!--- begin old test code --->
<cfset valueEquals(left="#BitSHLN(1,1)#", right="2")>
<cfset valueEquals(left="#BitSHLN(1,30)#", right="1073741824")>
<cfset valueEquals(left="#BitSHLN(1,31)#", right="-2147483648")>
<cfset valueEquals(left="#BitSHLN(2,31)#", right="0")>
<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>