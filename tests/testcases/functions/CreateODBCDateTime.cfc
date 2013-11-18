<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testCreateODBCDateTime" localMode="modern">

<!--- begin old test code --->
<cfset fixDate=CreateDateTime(2001, 11, 1, 4, 10, 4)> 
<cfset valueEquals(left="#CreateODBCDateTime(fixDate)#", right="{ts '2001-11-01 04:10:04'}")>
<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>