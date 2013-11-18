<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<cffunction name="beforeTests">
		<cfapplication action="update" clientmanagement="true">
	</cffunction>
	
	
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testDeleteClientVariable" localMode="modern">

<!--- begin old test code --->
<cflock timeout="1000" throwontimeout="yes" type="exclusive" scope="request">
		<cfset client.susi=1>
<cfset valueEquals(left="#DeleteClientVariable('susi')#", right="#true#")>
<cfset valueEquals(left="#DeleteClientVariable('susi')#", right="#false#")>
</cflock>
<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>