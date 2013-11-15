<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testAsc" localMode="modern">

<!--- begin old test code --->
<cfset valueEquals(left="#asc("a")#", right="97")>
<cfset valueEquals(left="#asc("A")#", right="65")>
<cfset valueEquals(left="#asc("	")#", right="9")>
<cfset valueEquals(left="#asc("")#", right="0")>
<cfset valueEquals(left="#asc("abc")#", right="97")>
 
<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>