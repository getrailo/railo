<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testBitAnd" localMode="modern">

<!--- begin old test code --->
<cfset valueEquals(left="#BitAnd(1, 0)#", right="0")>
<cfset valueEquals(left="#BitAnd(0, 0)#", right="0")>
<cfset valueEquals(left="#BitAnd(1, 2)#", right="0")>
<cfset valueEquals(left="#BitAnd(1, 3)#", right="1")>
<cfset valueEquals(left="#BitAnd(3, 5)#", right="1")>
<cfset valueEquals(left="#BitAnd(1, 1.0)#", right="1")>
<cfset valueEquals(left="#BitAnd(1, 1.1)#", right="1")>
<cfset valueEquals(left="#BitAnd(1, 1.9)#", right="1")>
<cfset valueEquals(left="#BitAnd(1, 0.9999999)#", right="0")>
 
<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>