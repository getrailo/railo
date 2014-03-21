<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testCreateODBCDate" localMode="modern">

<!--- begin old test code --->
<cfset fixDate=CreateDateTime(2001, 11, 1, 4, 10, 4)> 

<cfset assertEquals("{d '2001-11-01'}",CreateODBCDate(fixDate))>
<cfset assertEquals("{d '2001-11-01'}",toString(CreateODBCDate(fixDate)))>
<cfset assertEquals(0,hour(CreateODBCDate(fixDate)))>
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
</cfcomponent>