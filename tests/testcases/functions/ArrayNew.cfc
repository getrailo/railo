<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testArrayNew" localMode="modern">

<!--- begin old test code --->
<cfset arr=arrayNew(1)>
<cfset arr=arrayNew(2)>
<cfset arr=arrayNew(3)>

<cftry>
	<cfset arr=arrayNew(4)>
	<cfset fail("must throw:Array dimension 4 must be between 1 and 3. ")>
	<cfcatch></cfcatch>
</cftry>
<cftry>
	<cfset arr=arrayNew(0)>
	<cfset fail("must throw:Array dimension 4 must be between 1 and 3. ")>
	<cfcatch></cfcatch>
</cftry>

<cfset arr=arrayNew(2)>
<cfset x=arr[1]>

<!--- end old test code --->
	
		
		<!--- <cfset assertEquals("","")> --->
	</cffunction>
	
	<cffunction access="private" name="valueEquals">
		<cfargument name="left">
		<cfargument name="right">
		<cfset assertEquals(arguments.right,arguments.left)>
	</cffunction>
</cfcomponent>