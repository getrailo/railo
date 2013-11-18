<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testDateConvert" localMode="modern">

<!--- begin old test code --->
<cfset valueEquals(left="{ts '2006-01-26 00:00:00'}x", right="#DateConvert("Local2utc", "{ts '2006-01-26 01:00:00'}")#x")>
<cfset valueEquals(left="{ts '2006-07-26 00:00:00'}x", right="#DateConvert("Local2utc", "{ts '2006-07-26 02:00:00'}")#x")>
<cfset valueEquals(left="{ts '2006-03-26 03:00:00'}x", right="#DateConvert("Local2utc", "{ts '2006-03-26 05:00:00'}")#x")>
<cfset valueEquals(left="{ts '2006-03-26 03:00:00'}x", right="#DateConvert("Local2utc", "{ts '2006-03-26 05:00:00'}")#x")>
<cfset valueEquals(left="{ts '2006-03-26 00:59:59'}x", right="#DateConvert("Local2utc", "{ts '2006-03-26 01:59:59'}")#x")>
<cfset valueEquals(left="{ts '2006-03-26 00:00:00'}x", right="#DateConvert("Local2utc", "{ts '2006-03-26 01:00:00'}")#x")>
<cfset valueEquals(left="{ts '2006-01-26 01:00:00'}", right="#DateConvert("Local2utc", "{ts '2006-01-26 02:00:00'}")#")>
<cfset valueEquals(left="{ts '2006-03-26 00:59:00'}", right="#DateConvert("Local2utc", "{ts '2006-03-26 01:59:00'}")#")>
<cfset valueEquals(left="{ts '2006-03-26 03:59:00'}", right="#DateConvert("utc2local", "{ts '2006-03-26 01:59:00'}")#")>
<cfset valueEquals(left="{ts '2006-03-26 05:00:00'}", right="#DateConvert("utc2local", "{ts '2006-03-26 02:00:00'}")#")>
<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>