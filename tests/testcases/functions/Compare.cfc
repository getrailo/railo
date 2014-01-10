<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testCompare" localMode="modern">

<!--- begin old test code --->
<cfset valueEquals(left="#compare("0","0")#", right="0")>
<cfset valueEquals(left="#compare("0",0)#", right="0")>
<cfset valueEquals(left="#compare("","")#", right="0")>
<cfset valueEquals(left="#compare("a","aa")#", right="-1")>
<cfset valueEquals(left="#compare("a","A")#", right="1")>
<cfset valueEquals(left="#compare("a","0")#", right="1")>
<cfset valueEquals(left="#compare("aaaa","aaaaa")#", right="-1")>
<cfset valueEquals(left="#compare("1","1.0")#", right="-1")>
<cfset valueEquals(left="#compare("2","11")#", right="1")>
<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>