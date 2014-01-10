<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testCreateDate" localMode="modern">

<!--- begin old test code --->
<cfset valueEquals(left="#CreateDate(2000, 12, 1)#x", right="{ts '2000-12-01 00:00:00'}x")> 
<cfset valueEquals(
	left="{d '1899-12-31'}x",
	right="#CreateODBCDate(1)#x")>
	
	
	
<cfset d = CreateDate(2007,11,30)>
<cfset d1 = d - 0>
<cfset valueEquals(left="#d1#", right="39416")>

<cfset d = CreateDate(2007,5,1)>
<cfset d1 = d - 0>
<cfset valueEquals(left="#round(d1)#", right="39203")>
<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>