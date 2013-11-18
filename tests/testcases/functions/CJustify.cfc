<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testCJustify" localMode="modern">

<!--- begin old test code --->
<cfset valueEquals(left="#Cjustify("abc",1)#", right="abc")>
<cfset valueEquals(left="#Cjustify("abc",4)#", right="abc ")>
<cfset valueEquals(left="#Cjustify("abc",5)#", right=" abc ")>
<cfset valueEquals(left="#Cjustify("abc",6)#", right=" abc  ")>
<cftry>
	<cfset valueEquals(left="#Cjustify("abc",0)#", right="abc")>
	<cfset fail("must throw:Cjustify('abc',0)")>
	<cfcatch></cfcatch>
</cftry>
<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>