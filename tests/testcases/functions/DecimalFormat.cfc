<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testDecimalFormat" localMode="modern">

<!--- begin old test code --->
<cfset valueEquals(left="x#toString(DecimalFormat (123))#", right="x123.00")> 
<cfset valueEquals(left="x#toString(DecimalFormat (123.00000000002))#", right="x123.00")> 
<cfset valueEquals(left="x#toString(DecimalFormat (123456789.00))#", right="x123,456,789.00")> 
<cfset valueEquals(left="x#toString(DecimalFormat (123456.00))#", right="x123,456.00")> 

<cfset valueEquals(left="x#toString(DecimalFormat (-123))#", right="x-123.00")> 
<cfset valueEquals(left="x#toString(DecimalFormat (-1234))#", right="x-1,234.00")> 
<cfset valueEquals(left="x#toString(DecimalFormat (-123.00000000002))#", right="x-123.00")> 
<cfset valueEquals(left="x#toString(DecimalFormat (-123456789.00))#", right="x-123,456,789.00")> 
<cfset valueEquals(left="x#toString(DecimalFormat (-123456.00))#", right="x-123,456.00")>
<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>