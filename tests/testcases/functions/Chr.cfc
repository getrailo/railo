<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testChr" localMode="modern">

<!--- begin old test code --->
<cfset valueEquals(left="#chr(0)#", right="")>
<cfset valueEquals(left="#chr(38)#", right="&")>
<cfset valueEquals(left="#chr(9)#", right="	")>
<cftry>
	<cfset valueEquals(left="#chr(-1)#", right="")>
	<cfset fail("must throw:chr(-1)")>
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