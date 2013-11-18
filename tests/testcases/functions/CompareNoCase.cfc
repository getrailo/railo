<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testCompareNoCase" localMode="modern">

<!--- begin old test code --->
<cfset valueEquals(left="#compareNoCase("a","A")#", right="0")>
<cfset valueEquals(left="#compareNoCase("a","0")#", right="1")>
<cfset valueEquals(left="#compareNoCase("aaaa","AAAAA")#", right="-1")>
<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>