<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testDatePart" localMode="modern">

<!--- begin old test code --->
<cfset d1=CreateDateTime(2001, 12, 1, 4, 10, 1)> 
<cfset valueEquals(left="#datePart("m",d1)#", right="12")>

<cfset valueEquals(
	left="1899" ,
	right="#DatePart("yyyy", 1)#")>
    
<cfset valueEquals(left="7", right="#DatePart("w", d1)#")>
<cfset valueEquals(left="48", right="#DatePart("ww", d1)#")>
<cfset valueEquals(left="4", right="#DatePart("q", d1)#")>
<cfset valueEquals(left="12", right="#DatePart("m", d1)#")>
<cfset valueEquals(left="335", right="#DatePart("y", d1)#")>
<cfset valueEquals(left="1", right="#DatePart("d", d1)#")>
<cfset valueEquals(left="4", right="#DatePart("h", d1)#")>
<cfset valueEquals(left="10", right="#DatePart("n", d1)#")>
<cfset valueEquals(left="1", right="#DatePart("s", d1)#")>
<cfset valueEquals(left="0", right="#DatePart("l", d1)#")>
<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>