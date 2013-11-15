<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testArrayIndexExists">
		<cfif server.ColdFusion.ProductName EQ "railo">
			<cfset var a=array(1)>
			<cfset var a[3]=1>
			<cfset valueEquals(left="#ArrayIndexExists(array("a","b","c","d"),2)#", right="#true#")>
			<cfset valueEquals(left="#ArrayIndexExists(array("a","b","c","d"),5)#", right="#false#")>
			<cfset valueEquals(left="#ArrayIndexExists(a,2)#", right="#false#")>
			<cfset valueEquals(left="#ArrayIndexExists(a,3)#", right="#true#")>
		</cfif>
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>