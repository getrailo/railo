<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testAtn" localMode="modern">

<!--- begin old test code --->
<cfset valueEquals(left="#tostring(atn(0.3))#", right="0.291456794478")>
<cfset valueEquals(left="#tostring(atn(1.3))#", right="0.915100700553")>
<cfset valueEquals(left="#tostring(atn(-100))#", right="-1.560796660108")>

<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>