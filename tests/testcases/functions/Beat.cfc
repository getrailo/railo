<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testBeat" localMode="modern">

<!--- begin old test code --->
<cfif server.ColdFusion.ProductName EQ "railo"> 
	<cfset valueEquals(left="#beat(createDateTime(2000,1,1,12,0,0))#", right="500")>
	<cfset valueEquals(left="#beat('13:12:12')#", right="550.138")>
	<cfset valueEquals(left="#beat() GTE 0#", right="true")>
	<cfset valueEquals(left="#beat(parseDateTime('01/01/2001 12:00:00+0'))#", right="541.666")>
	<cfset valueEquals(left="#beat(parseDateTime('01/01/2001 12:00:00+1'))#", right="500")>
	<cfset valueEquals(left="#beat(parseDateTime('30/06/2001 12:00:00+1'))#", right="500")>
	<cfset valueEquals(left="#beat(parseDateTime('01/01/2001 12:00:00+2'))#", right="458.333")>
	<cfset valueEquals(left="#beat(parseDateTime('01/01/2001 12:00:00+3'))#", right="416.666")>
	<cfset valueEquals(left="#beat(parseDateTime('01/01/2001 12:00:00+4'))#", right="375")>
</cfif>

<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>